---
authors:
- Alexis Seigneurin
tags:
- Apache Kafka
date: 2018-08-21T00:00:00.000Z
title: "Kafka Streams - Processing late events"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/08/kafka-logo.png
---

I wrote quite a few [tutorials about Kafka](https://blog.ippon.tech/tag/apache-kafka/), so now is the time to look at more advanced problems. In this post, we will see how to perform _windowed aggregations_ and we are going to deal with _late events_.

In the tutorials, we were processing _messages_, but we will now start dealing with **events**. Events are things that happened at a particular **time**.

# Time

Before processing our events, we need to understand the semantics of our timestamps:
- **event time**: this indicates when the event _happened_ at the source
- **processing time**: this is when the event is _processed_ by our application.

The processing time will happen _after_ the event time for 2 reasons:
- it can take time for the event to reach the Kafka broker, either because the events have been cached in the client before being sent (think of IOT devices that may not be connected to a network all the time), or because of network latency
- our processing engine may not process the events as soon as they reach the broker.

In any case, the first thing is to make sure our events have a timestamp. Luckily enough, since Kafka 0.10, messages stored in Kafka are associated with a timestamp. This timestamp can either be assigned by the producer, or assigned by the broker if none was provided by the producer. In this example, we will make sure to assign timestamps at the source, i.e. in the producer code.

# A basic use case

Suppose we have events arriving in our system, and we want to calculate a basic metric: how many events happened per unit of time. This could look like this, where `tN` represents the Nth time unit, and `*` represents a single event:

```text
|    t1    |    t2    |    t3    |    ...
|* *  * *  |**   *    | * *** * *|    ...
```

We would produce the following aggregations:
- t1: 4 events
- t2: 3 events
- t3: 6 events

Now, suppose one of the events that happened during `t1` actually took a little bit of time to reach our system, and only came at `t2`:

```text
|    t1    |    t2    |    t3    |    ...
|* *  * -  |**   *    | * *** * *|    ...
        \-----> *(t1)
```

If we use the _processing time_ as a reference, we would generate the following results:
- t1: **3** events
- t2: **4** events
- t3: 6 events

Now, you may have noticed I wrote we want to calculate "how many events **happened** per unit of time", so these results would be incorrect.

The event that arrived at `t2` is called a **late event** and we basically have 2 options:
- we can _discard_ the event: the aggregated result for `t1` would be incorrect but the result for `t2` would remain correct
- or we can try to be smart and correlate the late event with the right aggregation window.

The latter can be tricky: when you are performing a _streaming aggregation_, you need to decide _when_ you will produce aggregates, and whether you can update these aggregates. For instance, we could say:
- As soon as `t1` is over, produce a result (result for t1 = 3), then produce another result when we receive a late event (_updated_ result for t1 = 4).
- Another option is to wait a little after the end of a window before producing a result, e.g. we could wait the equivalent of another window of time, and therefore produce the result for t1 at the end of t2.

Frameworks offer different options (the model offered by Apache Beam being probably one of the most advanced), and we will see how Kafka Streams behaves in this case.

# Our simple - flawed - data generator

To test our system, we need a data generator that will send events at a fixed rate, and that will sometimes generate late events. Our generator will generate data with the following pattern:
- 1 message every second
- at the 58th second of every minute, the message will be delayed by 4 second.

On the consumer side, we will perform aggregations per windows of 10 seconds. Our goal will therefore to make sure that the late event get counted in the correct window.

The code is pretty simple. We first need a timer that fires every second. We make sure this timer fires 0.2 second after every second, so as to allow the timer to drift slightly without messages being sent at 0.995 second, for instance, which would invalidate the results:

```kotlin
val now = System.currentTimeMillis()
val delay = 1200 - Math.floorMod(now, 1000)
val timer = Timer()
timer.schedule(object : TimerTask() {
    override fun run() {
        // sending logic
    }
}, delay, 1000L)
```

(The code is [in Kotlin](/my-journey-with-kotlin-part-2-introduction-to-kotlin/), by the way.)

When the timer fires, we just need to send an event every time unless at the 58th second, and we need to send a late event at the 2nd second (with a late timestamp):

```kotlin
val ts = System.currentTimeMillis()
val second = Math.floorMod(ts / 1000, 60)

if (second != 58L) {
    sendMessage("$second", ts, "on time")
}
if (second == 2L) {
    // send the late record
    sendMessage("58", ts - 4000, "late")
}
```

In the `sendMessage` function, just make sure to assign the timestamp of the message by using the appropriate constructor:

```kotlin
val window = (ts / 10000) * 10000
val value = "window=$window - n=$id - $info"
val futureResult = producer.send(ProducerRecord("events", null, ts, "$window", value))
logger.debug("Sent a record: $value")
futureResult.get()
```

Let's now create a topic for these events:

```bash
$ kafka-topics --zookeeper localhost:2181 --create --topic events --replication-factor 1 --partitions 4
```

Now, if we run the producer, we can see that event #58 does not arrive between #57 and #59, but instead arrives shortly after #2:

```text
15:37:55.304 ... Sent a record: window=1535398670000 - n=55 - on time
15:37:56.304 ... Sent a record: window=1535398670000 - n=56 - on time
15:37:57.305 ... Sent a record: window=1535398670000 - n=57 - on time
    <-- missing event
15:37:59.308 ... Sent a record: window=1535398670000 - n=59 - on time
15:38:00.311 ... Sent a record: window=1535398680000 - n=0 - on time
15:38:01.313 ... Sent a record: window=1535398680000 - n=1 - on time
15:38:02.317 ... Sent a record: window=1535398680000 - n=2 - on time
15:38:02.319 ... Sent a record: window=1535398670000 - n=58 - late   <-- late event
15:38:03.318 ... Sent a record: window=1535398680000 - n=3 - on time
15:38:04.318 ... Sent a record: window=1535398680000 - n=4 - on time
```

# Streaming aggregation with Kafka Streams

Let's build a Kafka Streams application to perform a streaming aggregation: we want to count how many events happened per window of 10 seconds. The first step is to create a KStream from our topic:

```kotlin
val streamsBuilder = StreamsBuilder()

val eventStream: KStream<String, String> = streamsBuilder
        .stream("events", Consumed.with(Serdes.String(), Serdes.String()))
```

We then need to aggregate the number of events per window of 10 seconds. Notice that the `groupBy` function is the entry point to aggregate events of a `KStream`, and we are providing a dummy key as we want to aggregate all the events together:

```kotlin
val aggregates: KTable<Windowed<String>, Long> = eventStream
        .groupBy({ k, v -> "dummy" }, Serialized.with(Serdes.String(), Serdes.String()))
        .windowedBy(TimeWindows.of(TimeUnit.SECONDS.toMillis(10)))
        .count(Materialized.with(Serdes.String(), Serdes.Long()))
```

Notice the type of the resulting KTable: `KTable<Windowed<String>, Long>`. The table would therefore look like:

```text
key                 | value
--------------------|-------
1535398670000:dummy |    10
1535398680000:dummy |     7
```

The key is a compound value, with the window and the (dummy) aggregation key.

We then need to output the KTable to another topic. To do so, we need to convert the KTable to a KStream:

```kotlin
aggregates
        .toStream()
        .map { ws, i -> KeyValue("${ws.window().start()}", "$i") }
        .to("aggs", Produced.with(Serdes.String(), Serdes.String()))
```

Finally, we can build the topology and start the application:

```kotlin
val topology = streamsBuilder.build()
val props = Properties()
...
val streams = KafkaStreams(topology, props)
streams.start()
```

By default, Kafka Streams will output results every 30 seconds, and the output will look like this:

```
1535401900000	10
1535401910000	10
1535401920000	10
1535401930000	2
...   <-- 30 second gap
1535401930000	10
1535401940000	10
1535401950000	10
1535401960000	2
...
```

The interesting thing is that all the results that were updated since the last output are printed. In this case, the current aggregate for window 1535401930000 was 2, and an _udpated_ result was printed later on, with a value of 10.

Keep in mind this is a _streaming aggregation_ and, because the stream is _unbounded_, it is difficult to know _when_ the results are final. That is, you shouldn't think an invalid result was printed. Instead, you should think that the result that was printed was valid _at the time_ of the output, and that this result might change later on.

One way to get Kafka Streams to output results more frequently is to change the commit frequency, e.g.:

```kotlin
props["commit.interval.ms"] = 0
```

We would now get an update every time an event is received:

```
1535402380000	1
1535402380000	2
1535402380000	3
1535402380000	4
1535402380000	5
1535402380000	6
1535402380000	7
1535402380000	8
1535402380000	9
1535402380000	10
1535402390000	1   <---- beginning of a new window
1535402390000	2
1535402390000	3
1535402390000	4
1535402390000	5
1535402390000	6
1535402390000	7
1535402390000	8
1535402390000	9
1535402400000	1   <---- beginning of a new window
1535402400000	2
1535402400000	3
1535402390000	10  <----- late event
1535402400000	4
1535402400000	5
1535402400000	6
1535402400000	7
1535402400000	8
1535402400000	9
1535402400000	10
```

Here, it is very easy to spot the impact of the late event:
- the aggregate result for window 1535402390000 stops at 9, and results start arriving for window 1535402400000
- then, the late event arrives, thus producing an update for window 1535402390000.

The good thing is that the window during which the late event arrived (window 1535402400000) does not include the late event. Kafka Streams rightly applied the event time semantics to perform the aggregation!

# Conclusion

Processing a stream of events is much more complex than processing a fixed set of records. Events can arrive late, out-of-order, and it is virtually impossible to know when all the data has arrived. The capabilities of the processing framework will therefore make a big difference in how you can process the data. You have to think of _when_ you want the results to be emitted, what to do when data arrives late, etc.

Although Kafka Streams did a good job at handling late events, we saw we had to change to the commit interval to get more frequent results. We could also have changed the size of the cache, as indicated in [Record caches in the DSL](https://kafka.apache.org/0110/documentation/streams/developer-guide#streams_developer-guide_memory-management_record-cache). However, changing the commit interval or cache settings can have negative side effects. Apache Beam offers a much more advanced model based on [triggers](https://beam.apache.org/documentation/programming-guide/#triggers).

We did not explore in this post how to discard late events. The process is called _watermarking_ and this is controlled in Kafka Streams through the retention period of the aggregation windows. Again, Apache Beam offers a more advanced model: [Watermarks and late data](https://beam.apache.org/documentation/programming-guide/#watermarks-and-late-data).

I do hope than, one day, Kafka Streams will implement the Apache Beam model. This would allow Kafka Streams to offer a great processing model as well as offering a simple deployment model. The best of both worlds.

The code used in this post can be found [here](https://github.com/aseigneurin/kafka-tutorials/event-processing). Feel free to ask questions in the comments section below!

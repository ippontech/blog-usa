---
authors:
- Alexis Seigneurin
tags:
- Apache Kafka
date: 2017-08-24T13:57:14.000Z
title: "Why Kafka Streams didn't work for us? - Part 3"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/08/apache-kafka-3.png
---

This is the third and final post in this series of posts in which I explain why, for our application, we had to transition from Kafka Streams to an implementation using plain Kafka Consumers. In this post, I will explain how we re-implemented the application.

Make sure you read the [first](/why-kafka-streams-didnt-work-for-us-part-1/) and [second](/why-kafka-streams-didnt-work-for-us-part-2/) posts before reading this.

# Design

In the second post, we saw that the primary concern with the Kafka Streams implementation was that, because we had to count the messages by their `valid` or `invalid` property, we were losing the partitioning of the messages, thus negatively affecting the performance. To solve this problem, we are going to get rid of the repartitioning operation and, instead, we are going to directly count the messages **per partition**. That is, if we re-use our initial example:

```text
          t0          t1          t2
     ----------- ----------- -----------
tx-0 |v|v|v|i|v| |v|i|       |i|v|v|
     ----------- ----------- -----------
tx-1 |v|i|v|v|   |v|v|v|     |i|i|v|v|
     ----------- ----------- -----------
```

We will now include the partition number as the key of our output:

```json
$ kafka-console-consumer --topic metrics --property print.key=true ...
{"topic":"tx","partition":0,"window":1501273548000,"status":"valid"}      4
{"topic":"tx","partition":0,"window":1501273548000,"status":"invalid"}    1
{"topic":"tx","partition":1,"window":1501273548000,"status":"valid"}      3
{"topic":"tx","partition":1,"window":1501273548000,"status":"invalid"}    1
{"topic":"tx","partition":0,"window":1501273549000,"status":"valid"}      1
{"topic":"tx","partition":0,"window":1501273549000,"status":"invalid"}    1
{"topic":"tx","partition":1,"window":1501273549000,"status":"valid"}      3
{"topic":"tx","partition":0,"window":1501273550000,"status":"valid"}      2
{"topic":"tx","partition":0,"window":1501273550000,"status":"invalid"}    1
{"topic":"tx","partition":1,"window":1501273550000,"status":"valid"}      2
{"topic":"tx","partition":1,"window":1501273550000,"status":"invalid"}    2
```

Furthermore, our application will **not** perform the final aggregation. We will store these values directly into InfluxDB, with the partition number as a _tag_:

```text
> select valid, invalid from tx
name: tx
time                partition valid invalid
----                --------- ----- -------
1501273548000000000 0         4     1
1501273548000000000 1         3     1
1501273549000000000 0         1     1
...
```

And we will leverage InfluxDB's aggregation functionality to display aggregated values in Grafana:

```text
> select sum(valid) as valid, sum(invalid) as invalid from "topic-health" where time>=1501273548000000000 and time<=1501273550000000000
name: topic-health
time                valid invalid
----                ----- -------
1501273548000000000 7     2
1501273549000000000 4     1
1501273550000000000 4     3
```

Now, we got rid of the `repartition` topic(s) but we still need a `changelog` if we want our application to be fault tolerant. Well, the change log is nothing else than the _metrics_ topic with an additional field to store the offset that correspond to the last message we counted. Let's merge these topics and change the format of the value:

```json
$ kafka-console-consumer --topic changelog --property print.key=true ...
{"topic":"tx","partition":0,"window":1501273548000,"status":"valid"}      {"value":4,"offset":5}
{"topic":"tx","partition":0,"window":1501273548000,"status":"invalid"}    {"value":1,"offset":4}
{"topic":"tx","partition":1,"window":1501273548000,"status":"valid"}      {"value":3,"offset":4}
...
```

This topic is a _changelog_ so we can make it a _compacted_ topic, thus allowing Kafka to reclaim some space if we update the same key multiple times.

Finally, another complaint we had about Kafka Streams was that it required too many _internal_ topics, especially because we were not sharing them between instances of the application. In this case, we can share them, meaning we will have a single _changelog_ topic for all the instances of our application.

# Implementation

To implement this, we didn't reuse Kafka Streams at all, although we reused some of the core ideas of the library:

- **Multi-threaded implementation with one internal _state store_ per thread**. A thread is responsible for one or more partitions of the source topic. No data is shared between threads, meaning we have no concurrency issue (no synchronization code).
- **The _state store_ is backed by a _changelog_ topic** to which we write at regular intervals (every second) and from which we can read from when the application restarts or when a rebalancing occurs (see below).
- **Handling of late arrival data** by updating the values of the corresponding time windows.
- **Expiration of old data** after a given time (1 day) to limit the size of the state store.

Let's go through the main parts of the code. The main components are:

- `StateStore`: an in-memory store of the active counts.
- `ChangelogWriter` and `ChangelogReader`: to write the _state store_ to the _changelog_ topic, and read it back.
- `DataCheckerThread`: the main consumer loop.
- `ConsumerRebalanceListener`: the event listener to take actions when a partition rebalancing happens.

## The _state store_

The _state store_ is an object with 2 fields:

- `values`: a map to hold the current state of the _changelog_ (only for the partitions the thread is responsible for).
- `updatedKeys`: the keys that have been updated since the last write to the _changelog_ topic. This list is reset after a successful write.

```scala
class StateStore(config: Config) extends StrictLogging {

  private val values = mutable.Map[StateKey, StateValue]()
  private val updatedKeys = mutable.Set[StateKey]()
  ...
```

The key and the value are _case classes_:

```scala
case class StateKey(topic: String, partition: Int, window: Long, status: String)
case class StateValue(offset: Long, value: Long)
```

## The _changelog_ reader and writer

We have a `ChangelogWriter` class to write the content of the _state store_ using a regular `KafkaProducer`:

```scala
class ChangelogWriter(config: Config) extends StrictLogging {

  private val producer: KafkaProducer[StateKey, StateValue] =

  def write(key: StateKey, value: StateValue): Future[RecordMetadata] = ...
```

Unsurprisingly, the `ChangelogReader` does the opposite, using a `KafkaConsumer` (we create a new consumer each time, since reading the _changelog_ is not a regular operation):

```scala
class ChangelogReader(config: Config) extends StrictLogging {

  def read(topicPartitions: immutable.Set[TopicPartition]): immutable.Map[StateKey, StateValue] = {
    val consumer = new KafkaConsumer[StateKey, StateValue](...)
    ...
```

Notice that, when reading the _changelog_, we are only reading the _changelog_ items that correspond to the partitions that are assigned to the current thread. We are also filtering out items that are too old (> 1 day).

One "difficulty" when reading the _changelog_ is that we need to read it _till the end_, but, unlike a file, a topic doesn't really have an end. To fix this issue, we retrieve the _end offsets_ before starting the reading loop, and we read from the beginning until the _read offsets_ are as high as the _end offsets_.

## Consumer thread

We then have the main reading loop, running in a thread:

```scala
class DataCheckerThread(config: Config) extends Runnable with StrictLogging {

  private val consumer: new KafkaConsumer[Array[Byte], Array[Byte]] = ...
  private val rebalanceListener: ConsumerRebalanceListener = ...

  override def run(): Unit = {
    consumer.subscribe(inputTopics, rebalanceListener)
    while (true) {
      for (record <- records) {
        stateStore.count(record)
      }
      // every second:
      changelogWriter.write(...)
      // every 5 minutes:
      stateStore.cleanup()
  }
}
```

This loop simply reads from the source topic using another `KafkaConsumer`, writing the changelog and cleaning up the state store at regular intervals (respectively 1 second and 5 minutes).

## Rebalance listener

Notice here that we register an instance of a [ConsumerRebalanceListener](https://kafka.apache.org/0100/javadoc/org/apache/kafka/clients/consumer/ConsumerRebalanceListener.html):

```java
public interface ConsumerRebalanceListener {
    void onPartitionsRevoked(Collection<TopicPartition> partitions);
    void onPartitionsAssigned(Collection<TopicPartition> partitions);
}
```

This callback plays a key role in making sure our application is fault tolerant. It will be called every time a rebalancing operation occurs, i.e. every time the partitions of the topic are reassigned between clients. This happens when a consumer joins or leaves the _consumer group_.

In our case, we take the following actions:

- When `onPartitionsRevoked()` is called, we purge the _state store_.
- When `onPartitionsAssigned()` is called, we use the `ChangelogReader` to reinitialize the _state store_, and we reset the read state of the main consumer by calling the `seek()` method. This makes sure that we pickup from where we left off.

# Conclusions

This new implementation works remarkably well. First and foremost, calculations are accurate even when we start new instances or kill running instances of the application. When we start a new instance, the new instance gets assigned some partitions, it reads the _changelog_ that has been produced by other instances, and resumes the work on these partitions. When an instance stops, other instances read the part of the _changelog_ it has produce to pickup its work.

On the side of performance, we had to use 16 threads to process 10000 messages per second with the Kafka Streams implementation. With this new implementation, 2 threads are enough to achieve the same performance, meaning we now have lots of room to scale!

Finally, our application now only requires a single topic for the _changelog_ and the output. The path to production is much easier.

To sum up, my advice to Confluent to improve Kafka Streams would be:

1. Make aggreations in 2 steps: first by partition, then across partitions.
2. Don't force a repartitioning when this can be avoided.

When this is done, I can reconsider using Kafka Streams for this application.

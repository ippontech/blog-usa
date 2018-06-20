---
authors:
- Alexis Seigneurin
tags:
- Apache Kafka
date: 2017-08-23T19:03:41.000Z
title: "Why Kafka Streams didn't work for us? - Part 2"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/08/apache-kafka-2.png
---

This is the second post in this series of posts in which I explain why, for our application, we had to transition from Kafka Streams to an implementation using plain Kafka Consumers. In this post, I will explain how we made the first implementation using Kafka Streams and what problems we encountered.

Make sure you read [the first post](/why-kafka-streams-didnt-work-for-us-part-1/) before reading this.

# About Kafka Streams

[Kafka Streams](https://kafka.apache.org/documentation/streams/) is a library that allows you to process data from Kafka. It is built on top of the Java Kafka client, and offers the ability to process messages independently from each other, or by making aggregations. Kafka Streams offers a DSL as well as a lower-level API, and it allows to make fault-tolerant calculations.

I have been using Kafka Streams since its early days - beginning of 2016 - and I even talked about it at [a Kafka Meetup](https://www.meetup.com/Apache-Kafka-DC/events/236376949/) as well as at the [Kafka Summit New York](https://kafka-summit.org/sessions/microservices-kafka-introduction-kafka-streams-real-life-example/). Even though we had problems with Kafka Streams on this use case, I still encourage you to give it a try if you are building data pipelines on top of Kafka.

# Implementation with Kafka Streams

In the previous post, we saw that our goal is to read messages from a topic, determine if the messages are valid or invalid, and output counters for each time window of 1 second.

The implementation with Kafka Streams is very simple:

```scala
messages
  .map((_, message) => message match {
    case _: GoodMessage => ("valid", 1L)
    case _: BadMessage => ("invalid", 1L)
  })
  .groupByKey
  .count(TimeWindows.of(1000), "metrics-agg-store")
  .toStream
  .map((k, v) => (MetricKey(inputTopic, k.window.start, k.key), v))
  .to(metricsTopic)
```

Note: our application is written in Scala but, to this day, Kafka Streams doesn't come with a real Scala API. I wrote a thin wrapper aroung the Java API and I open-sourced [on Github](https://github.com/aseigneurin/kafka-streams-scala).

## Aggregations

What is important to know is that Kafka Streams can only make aggregations **by key** (a count is an aggregation). Therefore, if we want to count messages depending on whether they are `valid` or `invalid`, we have to tranform our messages to make this information appear in the key of the messages (value `1L` is just a placeholder here):

```scala
  .map((_, message) => message match {
    case _: GoodMessage => ("valid", 1L)
    case _: BadMessage => ("invalid", 1L)
  })
```

Under the hood, Kafka Streams will create a `repartition` topic that will hold our tranformed messages. This is how the data _looks like_ in this topic:

```text
valid    1
valid    1
invalid  1
valid    1
invalid  1
```

Once this repartition is done, we can make the aggregation:

```scala
  .groupByKey
  .count(TimeWindows.of(1000), "metrics-agg-store")
```

Kafka Streams creates a _state store_ to perform the aggregation (here called `metrics-agg-store`), and this state store is backed by a _changelog_ (effictively another internal topic) to make it fault-tolerant. The _changelog_ topic basically keeps track of the updates made to the state store, and it is read from if the application has to recover from an interruption. In this topic, the key is a compound key made of the aggregation key (`valid` / `invalid`) and of the time window, and the value is the running count as well as an offset of the message in the input topic.

## Writing the result

With Kafka Streams, the result of an aggregation is a `KTable`. To be able to output this to a topic, we first need to convert the `KTable` to a `KStream`:

```scala
  .toStream
```

Thanks to this transformation, any change to the `KTable` will be a new message. We're effectively turning this `KTable` into a _changelog_.

We then do a final transformation of our key and value and output this to a topic:

```scala
  .map((k, v) => (MetricKey(inputTopic, k.window.start, k.key), v))
  .to(metricsTopic)
```

This would look like:

```json
$ kafka-console-consumer --topic metrics --property print.key=true ...
{"topic":"tx","window":1501273548000,"status":"valid"}      7
{"topic":"tx","window":1501273548000,"status":"invalid"}    2
{"topic":"tx","window":1501273549000,"status":"valid"}      4
...
{"topic":"tx","window":1501273549000,"status":"valid"}      5
```

Notice here that we used the key of the message to make sure that all the updates made to the same counter are written to the same partition of the metrics topic. This is something you should do every time the topic is effectively a changelog.

By doing this, we ensure that the updates of the same key can be read in the same order as they were written.

# The problems

The application performs the calculation that we expect and it is fault-tolerant. So far, so good. A few problems appeared when looking at scaling this application.

## Handling large volumes of messages

The biggest problem for us was how Kafka Streams forces you to repartition the messages by key. The distribution of our keys is really skewed: we have a lot more `valid` messages than `invalid` ones. This means that, while we read from multiple partitions in the source topic, most messages end up in the same partition of the `repartition` topic.

For instance, if we have 4 partitions in input with the following messages:

```text
     -----------
tx-0 |v|v|v|i|v|
     -----------
tx-1 |v|i|v|v|
     -----------
tx-2 |v|v|v|
     -----------
tx-3 |v|v|i|v|v|
     -----------
```

The `repartition` topic will also have 4 partitions and it might end up looking like this:

```text
              -----------------------------
repartition-0
              -----------------------------
repartition-1 |v|v|v|v|v|v|v|v|v|v|v|v|v|v|
              -----------------------------
repartition-2 |i|i|i|
              -----------------------------
repartition-3
              -----------------------------
```

Or, even worse, if the hashing function puts the `valid` and `invalid` keys in the same partition:

```text
              -----------------------------------
repartition-0
              -----------------------------------
repartition-1 |v|v|v|i|v|v|v|i|v|v|v|i|v|v|v|v|v|
              -----------------------------------
repartition-2
              -----------------------------------
repartition-3
              -----------------------------------
```

This puts a lot of pressure on the topic holding the valid messages, as well as on the thread that is going to read them, and this effectively limits the ability for the application to handle large volumes of messages.

## Threads

Another factor that is limiting the performance of the application is the number of threads that you allocate to Kafka Streams. In our tests, our source topic had 8 partitions, and we had configured our application to use 8 threads, thinking this would make a good 1-to-1 mapping between partitions and threads.

It turned out that the application could handle 2000 messages per second, but, above this threshold, it would start lagging behind. It would definitely not handle the 10000 messages per second that we had in mind.

The reason for this is that the application also has to read from the `repartition` topic. This topic is automatically created with the same number of partitions as the source topic, meaning our application was now reading from 16 partitions with 8 threads, thus creating some kind of contention.

We solved this problem by increasing the number of threads to 16, but without being very satisfied of the solution. Do we really need 16 threads to process 10000 messages per second... ?

## Number of topics

As we have seen above, this implementation requires 2 _internal_ topics: the `repartition` and the `changelog` topics. In our case, we wanted to deploy one instance of the application per topic to monitor, meaning 2 internal topics per source topic. In other terms, if we want to monitor 100 topics, our cluster actually needs holds 300 topics (plus 1 topic for the metrics). This requires a lot of administration, especially on a production cluster (ACLs...).

Now, if you consider the number of partitions, assuming each input topic has 8 partitions, this makes a total of 2400 partitions. This makes a high number of partitions. Confluent actually recommends not to exceed 2000 partitions per node in your cluster, so you have to be careful not to overload your cluster.

# To be followed...

This is all for now. In [the next post](/why-kafka-streams-didnt-work-for-us-part-3/), we will see how we re-implemented this application _without_ Kafka Streams!

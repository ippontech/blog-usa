---
authors:
- Alexis Seigneurin
tags:
- Apache Kafka
date: 2018-08-14T00:00:00.000Z
title: "Kafka tutorial #9 - Latency measures"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/08/kafka-logo.png
---

In [the previous posts](https://blog.ippon.tech/tag/apache-kafka/), we went through how to consume data from Kafka with the low-level Java client, with Kafka Streams, and with Spark Structured Streaming. In this post, we will run a quick experiment to see what latency each library/framework can achieve.

**Please note this is a simple test, not very scientific, and I am doing my best to make an unbiased comparison!**

# How to measure the latency

Since Kafka 0.10, Kafka messages contain a timestamp. This timestamp can be assigned by the producer, or is assigned by the broker if none is provided. By comparing timestamps in the output topic with timestamps in the input topic, we can measure processing latency.

So far, we built 3 consumers that consume records from a Kafka topic and produce output records in another topic:
- we started by using the Java Kafka client [in part 2](https://blog.ippon.tech/kafka-tutorial-2-simple-consumer-in-kotlin/)
- we then used Kafka Streams [in part 6](https://blog.ippon.tech/kafka-tutorial-6-kafka-streams-in-kotlin/)
- we finally used Spark Structured Streaming [in part 8](https://blog.ippon.tech/kafka-tutorial-8-spark-structured-streaming/).

In these 3 examples, we were consuming person objects from the input topic `persons`:

```json
{"firstName":"Quentin","lastName":"Corkery","birthDate":"1984-10-26T03:52:14.449+0000"}
{"firstName":"Neil","lastName":"Macejkovic","birthDate":"1971-08-06T18:03:11.533+0000"}
...
```

The output was key-value pairs in the `ages` topic.

```text
Quentin Corkery	33
Lysanne Beer	20
...
```

By reading from both topics, we can match output records with input records and calculate the latency.

Codewise, we use a map to store the timestamps found in the input topic. The key is `<first name><space><last name>` to match what we will read in the output topic. The value is the timestamp found in the input topic:

```kotlin
private val inputTimestampMap = mutableMapOf<String, Long>()
```

We then have a function to read from the input topic and populate the map:

```kotlin
private fun readFromInputTopic() {
    val records = inputConsumer.poll(Duration.ofSeconds(1))
    records.iterator().forEach {
        val person = it.value()
        val key = "${person.firstName} ${person.lastName}"
        val timestamp = it.timestamp()
        inputTimestampMap.put(key, timestamp)
    }
}
```

We also have a function to read from the output topic, perform a lookup in the map, and print the calculated latency to the console:

```kotlin
private fun readFromOutputTopic() {
    val records = outputConsumer.poll(Duration.ofSeconds(1))
    records.iterator().forEach {
        val key = it.key()
        val timestamp = inputTimestampMap[key]
        if (timestamp == null) {
            println("Key '$key' not found in timestamp map")
        } else {
            val latency = it.timestamp() - timestamp
            println("Latency: $latency")
        }
    }
}
```

We finally have the main loop to poll records from the input and output topics:

```kotlin
fun start() {
    inputConsumer.subscribe(listOf(inputTopic))
    outputConsumer.subscribe(listOf(outputTopic))

    while (true) {
        readFromInputTopic()
        readFromOutputTopic()
    }
}
```

Notice that, because we are reading alternatively in one topic then the other, we may read some records in the output topics before we read the corresponding records in the input topic. In this case, we will not find a match in the lookup table and a message will be printed in the console. Again, this code is very simple and simply aims at making a basic measure of the latency, not at being perfect.

Finally, in the results below, we are launching a producer that sends records at a rate of 1000 messages per second. This is to measure the system under load, not when it is idle. The results shown are 10 measures after the system has stabilized. (Don't leave the app running forever, though, because the _map_ grows indefinitely!)

# Results with the Java client

Let's start by measuring the latency of the processor we built with the Java Kafka client [in part 2](https://blog.ippon.tech/kafka-tutorial-2-simple-consumer-in-kotlin/).

```text
Latency: 1
Latency: 1
Latency: 1
Latency: 0
Latency: 1
Latency: 1
Latency: 1
Latency: 1
Latency: 1
Latency: 2
```

Latency is quite low: about 1 millisecond. This is quite good, especially since we didn't tune the consumer/producer that we are using (we used the default values for the buffer sizes, timeouts, etc.), and this gives us a baseline for what we can expect from other libraries and frameworks.

# Results with Kafka Streams

Let's now measure the performance of the Kafka Streams application we built [in part 6](https://blog.ippon.tech/kafka-tutorial-6-kafka-streams-in-kotlin/):

```text
Latency: 0
Latency: 0
Latency: 0
Latency: 0
Latency: 0
Latency: 0
Key 'Molly Dietrich' not found in timestamp map
Key 'Enola Kiehn' not found in timestamp map
Latency: 0
Latency: 0
```

Latency is almost always less than one millisecond, and we also have missed lookups, meaning we're not reading fast enough from the input topic.

Kafka Streams is faster than a simple application built with the Java client. The default settings of the consumer and producer are probably tuned to provide better latency.

# Results with Spark Structured Streaming

Now, let's look at how the Spark Structured Streaming application we created [in part 8](https://blog.ippon.tech/kafka-tutorial-8-spark-structured-streaming/) performs:

```text
Latency: 223
Latency: 219
Latency: 215
Latency: 207
Latency: 202
Latency: 191
Latency: 188
Latency: 184
Latency: 448
Latency: 375
```

Latency is much higher here, and it oscillates heavily: it sometimes goes down to 60-70 milliseconds, but also goes up to 0.5 second. This is multiple orders of magnitude higher than what the Java client and Kafka Streams provide. I was saying [in the previous post](https://blog.ippon.tech/kafka-tutorial-8-spark-structured-streaming/) that Spark is throughput-oriented, not latency-oriented, and this is a confirmation.

Such latency can be a problem on some projects, in particular when the data pipeline is made of multiple microservices that are chained together, ending up in the total latency of the pipeline being higher than one second.

# Spark with Continuous processing

Databricks - the company behind Spark - realized that Spark was not competitive for low-latency use cases, so they decided to address this problem by introducing a new processing mode: **continuous processing**.

Continuous processing is an experimental feature of Spark 2.3 and offers low latency processing of messages, provided no aggregation function is used. You can read more about Continuous processing [in this blog post by Databricks](https://databricks.com/blog/2018/03/20/low-latency-continuous-processing-mode-in-structured-streaming-in-apache-spark-2-3-0.html) and [in the documentation](http://spark.apache.org/docs/latest/structured-streaming-programming-guide.html#continuous-processing).

Continuous processing can be enabled very easily by setting a _trigger_ on the _query_:

```scala
val kafkaOutput = resDf.writeStream
  .format("kafka")
  ...
  .trigger(Trigger.Continuous("1 second"))
  .start()
```

Here, we are requiring the query to be processed _continuously_ with a checkpoint interval of 1 second.

Let's see the results:

```text
Latency: 2
Latency: 1
Latency: 1
Latency: 1
Latency: 2
Latency: 3
Latency: 6
Latency: 2
Latency: 1
Latency: 3
```

Latency is much closer to what the Java client and Kafka Streans have to offer, which is a major improvement over Spark's normal execution mode. This comes at the cost of not being able to perform aggregations, and by a lower processing guarantee: at-least-once, versus exactly-once with Spark's microbatches, but keep in mind this is only the _processing_ guarantee, not the _delivery_ guarantee, so this may not be a big problem in its own.

# Conclusions

Depending on your use case, low-latency can be a critical requirement for a processing technology. Kafka's Java client and Kafka Streams provide millisecond latency out-of-the-box, which make them great to build data pipelines with multiple microservices than consume from Kafka and produce to other Kafka topics.

Spark is a different animal. Many developers who use Spark for batch processing find that Spark Structured Streaming can be a natural fit for processing streaming data. This post shows that Spark may not be adequate for low-latency processing unless you enable the experimental continuous processing mode. If you do enable this mode, you lose the ability to perform aggregations, which is probably the most valuable feature of Spark, hence quite a downside.

Make sure you keep all of this in mind when you choose a technology to process messages from Kafka!

The code of these tutorials can be found [here](https://github.com/aseigneurin/kafka-tutorials).

Feel free to ask questions in the comments section below!

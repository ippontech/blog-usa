---
authors:
- Alexis Seigneurin
tags:
- Apache Kafka
- Kotlin
date: 2018-08-08T13:05:09.000Z
title: "Kafka tutorial #6 - Kafka Streams in Kotlin"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/08/kafka-logo.png
---

This is the sixth post in this series where we go through the basics of using Kafka. In this post, instead of using the Java client (producer and consumer API), we are going to use Kafka Streams, a powerful library to process streaming data.

# A few words about Kafka Streams

Kafka Streams is a library that allows you to process data from and to Kafka. It provides a DSL with similar functions as to what we can find in Spark: `map()`, `flatMap()`, `filter()`, `groupBy()`, `join()`, etc. Stateful transformations are available, as well as powerful windowing functions, including support for late arrival data, etc.

Since Kafka Streams is a library, a Kafka Streams applications can be deployed by just executing the Jar of your application. There is no server in which to deploy your application, meaning you can use any tool you like to run your Kafka Streams applications: Docker, Kubernetes, bare-metal, etc.

Kafka Streams allows you to scale out by running multiple threads in the same JVM, and/or by starting multiple instances of your application (multiple JVMs).

Although Kafka Streams is part of the Apache Kafka project, I highly recommend reading [the documentation provided by Confluent](https://docs.confluent.io/current/streams/index.html). You can also watch the talk I gave at Kafka Summit last year: [Microservices with Kafka: An Introduction to Kafka Streams with a Real-Life Example](https://www.confluent.io/kafka-summit-nyc17/microservices-with-kafka-an-introduction-to-kafka-streams-with-a-real-life-example/inv/).

# Setting up our project

We just need one dependency for Kafka Streams. The Kafka client is a transitive dependency, so you don't need to add that one explicitly:

```gradle
dependencies {
    ...
    compile 'org.apache.kafka:kafka-streams:2.0.0'
```

We will keep the dependencies for Jackson and its Kotlin module.

We said we could just run a Kafka Streams application as a jar, so let's modify our build to generate a "fat jar":

```gradle
jar {
    from {
        configurations.compile.collect { it.isDirectory() ? it : zipTree(it) }
    }
}
```

# The consumer code

To write a Kafka Streams application, the first thing to do is to get a `StreamsBuilder`. This is what will allow us to build a _topology_:

```kotlin
val streamsBuilder = StreamsBuilder()
```

We can use this builder to consume data from a topic. Here, we want to use a `KStream` (a representation of a stream of records), not a `KTable` (a changelog stream), so we use the `stream()` method.

```kotlin
val personJsonStream: KStream<String, String> = streamsBuilder
        .stream<String, String>(personsTopic, Consumed.with(Serdes.String(), Serdes.String()))
```

It is a good practice to specify explicitly how to deserialize the messages at this level (`Consumed.with(...)`), rather than through application-wide properties, because a Kafka Streams application may read from multiple sources of data with different formats.

Because we are reading JSON data as strings, we need to deserialize the value of our messages into `Person` objects:

```kotlin
val personStream: KStream<String, Person> = personJsonStream.mapValues { v ->
    jsonMapper.readValue(v, Person::class.java)
}
```

Notice that the `mapValues()` method has 2 implementations with different lambdas, so we are explicitly specifying that we are using the lambda with a single argument (`v -> ...`).

We can now process the `Person` objects to calculate the age of the persons:

```kotlin
val resStream: KStream<String, String> = personStream.map { _, p ->
    val birthDateLocal = p.birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val age = Period.between(birthDateLocal, LocalDate.now()).getYears()
    KeyValue("${p.firstName} ${p.lastName}", "$age")
}
```

In this case, `map()` only has one implementation with a lambda that takes 2 parameters. We don't need the key (it is null) so `_` is a way to indicate we know the parameter is here, but we don't need a variable for this value.

Now comes the time to write the result back to the output topic. Again, it is a good practice to be explicit with the serializers to use:

```kotlin
resStream.to(agesTopic, Produced.with(Serdes.String(), Serdes.String()))
```

Out topology is built, so let's get a reference to it:

```kotlin
val topology = streamsBuilder.build()
```

We can now start the Kafka Streams engine, passing it the topology and a couple of properties:

```kotlin
val props = Properties()
props["bootstrap.servers"] = brokers
props["application.id"] = "kafka-tutorial"
val streams = KafkaStreams(topology, props)
streams.start()
```

Notice that, unlike how we used the Java client, we don't need to specify serializers or deserializers here. Instead, we have defined these explicitly when reading from or writing to topics. If we need to use additional topics with different formats, there will be no ambiguity as to what serde to use.

# Testing the code

We can reuse the code of [part 1](/kafka-tutorial-1-simple-producer-in-kotlin/) to produce data, so let's go ahead and run this producer, and run the console consumer to visualize the data:

```shell
$ kafka-console-consumer --bootstrap-server localhost:9092 --topic persons
{"firstName":"Patrick","lastName":"Rempel","birthDate":"1988-03-15T17:58:16.310+0000"}
{"firstName":"Charlotte","lastName":"Windler","birthDate":"1978-11-28T03:16:24.950+0000"}
...
```

Now, we can either run the Kafka Streams application straight from the IDE, or build it and run it from the command line:

```shell
$ gradle build
...
$ java -cp build/libs/kafka-streams.jar com.ippontech.kafkastreams.StreamsProcessorKt
...
```

Now, if you start the console consumer on the output topic, you should see similar results as to what we had seen in [part 2](/kafka-tutorial-2-simple-consumer-in-kotlin/):

```
$ kafka-console-consumer --bootstrap-server localhost:9092 --topic ages
Patrick Rempel	30
Charlotte Windler	39
...
```

Success!

# Scaling out

We said that Kafka Streams can easily scale out by running multiple instances of the same application. Let's verify this by running a second instance in another window (I am shortening the logs for better readability):

```shell
$ java -cp build/libs/kafka-streams.jar com.ippontech.kafkastreams.StreamsProcessorKt
...
18/08/03 18:43:56.039 INFO ... Setting newly assigned partitions [persons-2, persons-3]
```

This new instance was just assigned 2 partitions of our topic (2 and 3), out of a total of 4 partitions.

Let's look at the log of the first instance:

```shell
...
18/08/03 18:43:56.014 INFO ... Revoking previously assigned partitions [persons-0, persons-1, persons-2, persons-3]
18/08/03 18:43:56.014 INFO ... State transition from RUNNING to PARTITIONS_REVOKED
18/08/03 18:43:56.014 INFO ... State transition from RUNNING to REBALANCING
18/08/03 18:43:56.035 INFO ... Setting newly assigned partitions [persons-0, persons-1]
...
```

The first instance was processing the 4 partitions. It entered a state where the partitions got reassigned to consumers, ending up in this instance being assigned 2 partitions (0 and 1).

We just found a way to easily scale out! This works because our consumers are part of the same consumer group, which is controlled here through the `application.id` property.

We could go further and assign multiple threads to each instance of our application by defining the `num.stream.threads` property. Each thread would be independent, with its own consumer and producer. This makes it easy to consume the resources of our servers.

# Conclusion

I can't say enough how Kafka Streams is a great library. It is my tool of choice when building data pipelines with Kafka. Kafka Streams removes a lot of the work that you would have to do with the plain Java client, while being a lot simpler to deploy and manage than a Spark or Flink application.

There is a lot more to know about Kafka Streams, so let me know in the comments section below if there is something specific you would like me to expose in a further post.

The code of these tutorials can be found [here](https://github.com/aseigneurin/kafka-tutorials).

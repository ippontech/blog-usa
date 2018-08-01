---
authors:
- Alexis Seigneurin
tags:
- Apache Kafka
- Kotlin
date: 2018-08-01T00:00:00.000Z
title: "Kafka tutorial #1 - Simple Kafka producer in Kotlin"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/08/kafka-logo.png
---

Kafka 2.0 [just got released](http://kafka.apache.org/downloads#2.0.0), so it is a good time to review the basics of _using_ Kafka. I am going to focus on producing, consuming and processing messages or events. I don't plan on covering the basic properties of Kafka (partitioning, replication, offset management, etc.) - these are well covered [in the documentation of Kafka](https://kafka.apache.org/documentation/) - although feel free to let me know what you would like to see in further posts by adding comments in the section below this post.

Most of the code shown in these tutorials will be written in Kotlin. Kotlin is a better Java, and if you haven't started using it, now is the time to read [my introduction to this language](https://blog.ippon.tech/my-journey-with-kotlin-part-1-how-i-came-to-dislike-java/). These posts will be a good opportunity to see some nice features of Kotlin.

# What we are going to build in this first tutorial

We are going to start by using the Java client library, in particular its [Producer API](https://kafka.apache.org/20/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html) (later down the road, we will see how to use Kafka Streams and Spark Streaming).

We need a source of data, so to make it simple, we will produce mock data. Each message will be a _person_ with a first name, last name, and birth date. We will use a _producer_ to send these messages in JSON format to a topic:

```json
{"firstName":"Quentin","lastName":"Corkery","birthDate":"1984-10-26T03:52:14.449+0000"}
{"firstName":"Neil","lastName":"Macejkovic","birthDate":"1971-08-06T18:03:11.533+0000"}
...
```

That will be all for now. We will see how to consume and process the messages in the next post.

# Setting up our project

Let's start by setting up our build script with Gradle. As we are using Kotlin, our `build.gradle` file needs the Kotlin plugin:

```gradle
buildscript {
    dependencies { classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.2.51" }
}
apply plugin: "java"
apply plugin: "kotlin"
```

Let's then add a few dependencies, starting with Kotlin's standard library:

```gradle
compile "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.2.51"
```

We are going to use [Java Faker](https://github.com/DiUS/java-faker) to generate mock data:

```gradle
compile "com.github.javafaker:javafaker:0.15"
```

We will use Jackson for JSON (de-)serialization, but let's not forget to add the Kotlin module to (de-)serialize Kotlin data classes:

```gradle
compile 'com.fasterxml.jackson.core:jackson-databind:2.9.6'
compile 'com.fasterxml.jackson.module:jackson-module-kotlin:2.9.6'
```

And of course we need the Kafka client:

```gradle
compile 'org.apache.kafka:kafka-clients:2.0.0'
```

We're all set. Let's open this project in IntelliJ and start coding.

# The producer

Let's start by creating a `KafkaProducer` that we will use to send messages to Kafka:

```kotlin
private fun createProducer(brokers: String): Producer<String, String> {
    val props = Properties()
    props["bootstrap.servers"] = brokers
    props["key.serializer"] = StringSerializer::class.java
    props["value.serializer"] = StringSerializer::class.java
    return KafkaProducer<String, String>(props)
}
```

Notice how `Properties` behaves like a `Map`, by allowing us to set values using `=` rather than by calling a method. Sweet.

We are using a `StringSerializer` both for the key and the value:
- we will not use the key (we will leave it `null`) but a key serializer is mandatory
- we will serialize the values to JSON by hand using Jackson (we will see how to write our own serializer in a future post).

Now we want to generate some data. Let's create a _model_ to hold our data:

```kotlin
data class Person(
        val firstName: String,
        val lastName: String,
        val birthDate: Date
)
```

This is a `data class`: pretty much like a POJO in Java. In this case, the fields are immutable (`val` instead of `var`) and they all only allow non-null values.

We can now generate data:

```kotlin
val faker = Faker()
val fakePerson = Person(
        firstName = faker.name().firstName(),
        lastName = faker.name().lastName(),
        birthDate = faker.date().birthday()
)
```

Kotlin doesn't force you to assign the fields by name, but this makes the code more readable, especially when the list of fields grows.

Now we need to serialize our data class to JSON. Let's create a JSON mapper first:

```kotlin
val jsonMapper = ObjectMapper().apply {
    registerKotlinModule()
    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    setDateFormat(StdDateFormat())
}
```

Here, we initialize the `ObjectMapper`, enable the Kotlin module, and set the serialization format for dates.

See how Kotlin allows us to create an object, called some methods on it, and return that instance? Well, there's more than just the `apply` method we use here, and this handy methods are well described in [this post](https://ask.ericlin.info/post/2017/06/subtle-differences-between-kotlins-with-apply-let-also-and-run/).

Now we can use the mapper to serialize our object:

```kotlin
val fakePersonJson = jsonMapper.writeValueAsString(fakePerson)
```

And we can finally send this JSON object to Kafka:

```kotlin
val futureResult = producer.send(ProducerRecord(personsTopic, fakePersonJson))
futureResult.get()
```

Notice that we send a record without a key (we only specified the value), so the key will be `null`. We also called `get()` on the result to wait for the write acknowledgment: without that, messages could be sent to Kafka but lost without us knowing about the failure.

# Testing the code

I will assume you have downloaded [Kafka 2.0](https://kafka.apache.org/downloads#2.0.0) or [Confluent Platform 5.0](https://www.confluent.io/download/) and you want to run a single broker on your laptop. Let's start ZooKeeper:

```shell
$ bin/zookeeper-server-start etc/kafka/zookeeper.properties
...
[2018-08-01 09:57:11,823] INFO binding to port 0.0.0.0/0.0.0.0:2181 (org.apache.zookeeper.server.NIOServerCnxnFactory)
```

ZooKeeper is now running on port 2181. Now, start Kafka:

```shell
$ bin/kafka-server-start etc/kafka/server.properties
...
[2018-08-01 09:57:32,511] INFO Kafka version : 2.0.0-cp1 (org.apache.kafka.common.utils.AppInfoParser)
```

Our broker is running on port 9092. Let's create a topic for our data:

```shell
$ kafka-topics --zookeeper localhost:2181 --create --topic persons --replication-factor 1 --partitions 4
```

Now we can start the application in our IDE, and see data coming in:

```shell
$ kafka-console-consumer --bootstrap-server localhost:9092 --topic persons
{"firstName":"Meredith","lastName":"Willms","birthDate":"1980-08-21T10:13:27.533+0000"}
{"firstName":"Anastacio","lastName":"Beier","birthDate":"1966-03-29T05:00:48.993+0000"}
{"firstName":"Eudora","lastName":"Ritchie","birthDate":"1994-02-18T08:29:49.276+0000"}
...
```

Success!

# Conclusion

We have seen how to use Kafka's Java client to send messages to Kafka. We did this using Kotlin without problem, and actually benefitted from a couple of nice features of the language.

One thing to keep in mind, when producing data, is what **write guarantee** you want to achieve. Are you ready to lose data in the case of a network or broker failure? There is usually a trade-off to be made between your availability to produce, the latency when producing, and the guarantee that your messages will be safely written. In the example above, we only have one broker, the producer has a default value of `acks=1`, and we are waiting for the broker's acknowledgment (call to `future.get()`). This means that we have a guarantee that a message will be persisted (although not flushed to disk) before we can produce another message: we will not loose messages but our latency is higher than in a "fire and forget" case.

The code of these tutorials can be found [here](https://github.com/aseigneurin/kafka-tutorials).

Feel free to ask questions in the comments section below!

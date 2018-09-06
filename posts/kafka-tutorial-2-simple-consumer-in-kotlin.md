---
authors:
- Alexis Seigneurin
tags:
- Apache Kafka
- Kotlin
date: 2018-08-06T12:48:46.000Z
title: "Kafka tutorial #2 - Simple Kafka consumer in Kotlin"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/08/kafka-logo.png
---

This is the second post in this series where we go through the basics of using Kafka. We saw [in the previous post](/kafka-tutorial-1-simple-producer-in-kotlin/) how to produce messages. We will see here how to consume the messages we have produced, how to process them and how to send the results to another topic.

# What we are going to build in this tutorial

So far, we have produced JSON data in a topic called `persons`:

```json
{"firstName":"Quentin","lastName":"Corkery","birthDate":"1984-10-26T03:52:14.449+0000"}
{"firstName":"Lysanne","lastName":"Beer","birthDate":"1997-10-22T04:09:35.696+0000"}
{"firstName":"Neil","lastName":"Macejkovic","birthDate":"1971-08-06T18:03:11.533+0000"}
...
```

This time, we will use the [Consumer API](https://kafka.apache.org/20/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html) to fetch these messages. We will calculate the age of the persons, and write the results to another topic called `ages`:

```text
Quentin Corkery	33
Lysanne Beer	20
Neil Macejkovic	46
...
```

# The consumer

We can create a consumer in a very similar way to how we created a producer in the previous post:

```kotlin
private fun createConsumer(brokers: String): Consumer<String, String> {
    val props = Properties()
    props["bootstrap.servers"] = brokers
    props["group.id"] = "person-processor"
    props["key.deserializer"] = StringDeserializer::class.java
    props["value.deserializer"] = StringDeserializer::class.java
    return KafkaConsumer<String, String>(props)
}
```

This time, we need to provide _deserializers_ rather than _serializers_. We will not use the key deserializer but, just as for the key serializer of the producer, this is a mandatory parameter. On the other hand, we will need the value deserializer to return a JSON string from our data, and we will deserialize the JSON object with Jackson.

We also need to provide a _group ID_: this is to identify the **consumer group** that our consumer will join. If multiple consumers are started in parallel - either through different processes or through different threads - each consumer will be assigned a subset of the _partitions_ of the topic. E.g. since our topic was created with 4 partitions, we could create up to 4 consumers so as to consume data in parallel.

Once our consumer is created, we can _subscribe_ to the source topic:

```kotlin
consumer.subscribe(listOf(personsTopic))
```

This has the effect of requesting dynamic assignment of the partitions to our consumer, and to effectively _join_ the consumer group.

We can now write an infinite loop to consume records:

```kotlin
while (true) {
    val records = consumer.poll(Duration.ofSeconds(1))
    ...
}
```

The duration passed in parameter to the `poll()` method is a timeout: the consumer will wait at most 1 second before returning. The moment the broker will return records to the client also depends on the value of `fetch.min.bytes`, which defaults to 1, and which defines the minimum amount of data the broker should wait to be available for the client. Another configuration property is `fetch.max.bytes` (default = 52428800 bytes), which defines how much data can be returned at once.

In our case, the broker:
- will return all the records that are available without exceeding the capacity of the buffer (50 MB)
- will return as soon as 1 byte of data is available
- while waiting at most 1 second.

This also means that, if no records are available, the broker will return an empty list of records.

Now, we can iterate over the records:

```kotlin
records.iterator().forEach {
    val personJson = it.value()
    ...
}
```

I usually don't like the Java syntax of an `Iterator`, but Kotlin provides a nice way to process all the elements that are returned. `it` implicitly refers to a record in the scope of the lambda expression.

Let's deserialize the JSON string to extract a `Person` object:

```kotlin
val person = jsonMapper.readValue(personJson, Person::class.java)
```

If you forget to register the Kotlin module for Jackson, you may see the following error:

```text
Exception in thread "main" com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance of `com.ippontech.kafkatutorials.simpleclient.Person` (no Creators, like default construct, exist): cannot deserialize from Object value (no delegate- or property-based Creator)
```

The important part is `no Creators, like default construct, exist`. Kotlin data classes have specific constructors to assign the fields when creating an object, so there is no default constructor like in a Java POJO. Make sure to call `registerKotlinModule()` on your `ObjectMapper` to allow Jackson to work with data classes.

Now, we can do some processing and calculate the age of the persons:

```kotlin
val birthDateLocal = person.birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
val age = Period.between(birthDateLocal, LocalDate.now()).getYears()
```

And we can finally write the results to another topic:

```kotlin
val future = producer.send(ProducerRecord(agesTopic, "${person.firstName} ${person.lastName}", "$age"))
future.get()
```

Here, the records have a key (first name and last name) and a value (the calculated age). Both values are written as plain strings.

I am using a separate Kafka producer for this, but we could reuse the producer created in the first part of this tutorial.

# Testing the code

I am going to assume the producer (first part of the tutorial) is running and producing data to the `persons` topic. Let's create a new topic for our output:

```shell
$ kafka-topics --zookeeper localhost:2181 --create --topic ages --replication-factor 1 --partitions 4
```

We can start a consumer:

```shell
$ kafka-console-consumer --bootstrap-server localhost:9092 --topic ages --property print.key=true
```

Since our messages have a key, we want to print that key. This is what the `--property print.key=true` option does.

If we run the consumer in our IDE, we can now see the results in the console consumer:

```text
Quentin Corkery	33
Lysanne Beer	20
Neil Macejkovic	46
...
```

Success! We created our first **Kafka micro-service**: an application that takes some data in input from a Kafka topic, does some processing, and writes the result to another Kafka topic. This is the first step to create a **data pipeline**.

# Conclusion

We have seen how to use Kafka's Java API to consume messages. Again, Kotlin interoperates smoothly with Java and makes the code nicer. Just don't forget to configure Jackson correctly to be able to deserialize JSON data into your data classes.

When we were producing data, the main things to think about were the delivery guarantee and the partitioning. When consuming data, there is a lot more to think about:
- How many consumers should I run in parallel (threads / processes)?
- How much data should I consume at once (memory usage)?
- How much time am I ready to wait to receive messages (latency vs throughput)?
- When should I mark a message as being processed (committing offsets)?
- ...

We didn't see that last part, but the consumer automatically commits the offsets for you during the next call to `poll()` if `enable.auto.commit` is set to `true`, which is the default. The whole batch of records will therefore be committed: if your application crashes after processing a few messages but not all of the records of a batch, they will not be committed and will be processed again by another consumer. This is called _at least once processing_. And there is a lot more to understand about offset management, but this is outside the scope of this post!

The code of this tutorial can be found [here](https://github.com/aseigneurin/kafka-tutorial-simple-client).

Feel free to ask questions in the comments section below!

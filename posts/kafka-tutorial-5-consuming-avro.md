---
authors:
- Alexis Seigneurin
tags:
- Apache Kafka
- Kotlin
date: 2018-08-03T00:00:00.000Z
title: "Kafka tutorial #5 - Consuming Avro data"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/08/kafka-logo.png
---

This is the fifth post in this series where we go through the basics of using Kafka. We saw [in the previous post](/kafka-tutorial-4-avro-and-schema-registry/) how to produce messages in Avro format and how to use the Schema Registry. We will see here how to consume the messages we produced.

# What we are going to build in this tutorial

We have produced messages in Avro format and we have been able to display them in JSON format using the Kafka Avro console consumer:

```shell
$ kafka-avro-console-consumer --bootstrap-server localhost:9092 --topic persons-avro
{"firstName":"Stephania","lastName":"Okuneva","birthDate":582023554621}
{"firstName":"Aleen","lastName":"Terry","birthDate":159202477258}
...
```

We are now going to take the code from [part 2](/kafka-tutorial-2-simple-consumer-in-kotlin/) and adapt it to read Avro data.

# The consumer

Let's start by changing the code that creates the consumer:

```kotlin
private fun createConsumer(brokers: String, schemaRegistryUrl: String): Consumer<String, GenericRecord> {
    val props = Properties()
    props["bootstrap.servers"] = brokers
    props["group.id"] = "person-processor"
    props["key.deserializer"] = StringDeserializer::class.java
    props["value.deserializer"] = KafkaAvroDeserializer::class.java
    props["schema.registry.url"] = schemaRegistryUrl
    return KafkaConsumer<String, GenericRecord>(props)
}
```

The changes are similar to the ones made on the other side, for the producer:
1. We are replacing the value deserializer with a `KafkaAvroDeserializer`.
2. We are defining the URL of the Schema Registry for the deserializer to fetch schemas (messages will only contain the ID of the schemas, not the schemas themselves).
3. We are changing the generic type of the value of the consumer to return `GenericRecord` objects.

Now, let's subscribe to the new topic:

```kotlin
consumer.subscribe(listOf(personsAvroTopic))
```

We can now consume messages of type `GenericRecord`:

```kotlin
records.iterator().forEach {
    val personAvro: GenericRecord = it.value()
    ...
```

Let's "rehydrate" our model instead of manipulating generic records:

```kotlin
val person = Person(
        firstName = personAvro["firstName"].toString(),
        lastName = personAvro["lastName"].toString(),
        birthDate = Date(personAvro["birthDate"] as Long)
)
```

As we said in the previous post, this code is not typesafe: types are checked at runtime, so you need to be careful with that. The main gotcha is that strings are not of type `java.lang.String` but of type `org.apache.avro.util.Utf8`. Here, we are avoiding a cast by directly calling `toString()` on the objects.

And the rest of the code remains the same. You can refer to [part 2](/kafka-tutorial-2-simple-consumer-in-kotlin/) to see the output.

# Conclusion

This concludes this part of the tutorial where, instead of sending data in JSON format, we use Avro as a serialization format. The main benefit of Avro is that the data conforms to a schema. Schemas are stored in the Schema Registry so that anyone has the ability to read the data in the future, even if the code of the producers or of the consumers are no longer available.

Avro also guarantees backward or forward compatibility of your messages, provided you follow some basic rules (e.g. when adding a field, make its value optional).

I encourage you to use Avro and the Schema Registry for all your data in Kafka, rather than just plain text or JSON messages. This is a safe choice to ensure the evolutivity of your platform.

The code of these tutorials can be found [here](https://github.com/aseigneurin/kafka-tutorials).

Feel free to ask questions in the comments section below!

---
authors:
- Alexis Seigneurin
tags:
- Apache Kafka
- Kotlin
date: 2018-08-01T00:00:00.000Z
title: "Kafka tutorial #7 - Kafka Streams SerDes and Avro"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/08/kafka-logo.png
---

This is the seventh post in this series where we go through the basics of using Kafka. We saw [in the previous post](/kafka-tutorial-6-kafka-streams-in-kotlin/) how to build a simple Kafka Streams application. We will see here how to use a custom SerDe (Serializer / Deserializer) and how to use Avro and the Schema Registry.

# The concept of SerDe

In [Kafka tutorial #3 - JSON SerDes](/kafka-tutorial-3-json-serdes/), I introduced the name _SerDe_ but we had 2 separate classes for the serializer and the deserializer. Kafka Streams keeps the serializer and the deserializer together, and uses the `org.apache.kafka.common.serialization.Serde` interface for that. Here is the Java code of this interface:

```java
public interface Serde<T> extends Closeable {

    void configure(Map<String, ?> configs, boolean isKey);

    void close();

    Serializer<T> serializer();

    Deserializer<T> deserializer();
}
```

We will see how to use this interface.

# Custom deserializer

The goal here is to avoid having to deserialize JSON strings into `Person` objects by hand in our Kafka Streams topology, as we did in [part 6](/kafka-tutorial-6-kafka-streams-in-kotlin/):

```kotlin
val personJsonStream: KStream<String, String> = streamsBuilder
        .stream<String, String>(personsTopic, Consumed.with(Serdes.String(), Serdes.String()))

val personStream: KStream<String, Person> = personJsonStream.mapValues { v ->
    jsonMapper.readValue(v, Person::class.java)
}
```

This is where we want to use an implementation of `Serde<Person>`. To write one, we first need implementations of `Serializer<Person>` and `Deserializer<Person>`. We already wrote these classes in [part 3](/kafka-tutorial-3-json-serdes/). We can therefore simply write the SerDe as follows:

```kotlin
class PersonSerde : Serde<Person> {
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}
    override fun close() {}
    override fun deserializer(): Deserializer<Person> = PersonDeserializer()
    override fun serializer(): Serializer<Person> = PersonSerializer()
}

class PersonSerializer : Serializer<Person> {
    ...
}

class PersonDeserializer : Deserializer<Person> {
    ...
}
```

We can now use this SerDe to build a KStream that directly deserializes the values of the messages as `Person` objects:

```kotlin
val personStream: KStream<String, Person> = streamsBuilder
        .stream(personsTopic, Consumed.with(Serdes.String(), PersonSerde()))
```

Another option, instead of creating our own `PersonSerde` class, would have been to use `Serdes.serdeFrom()` to dynamically wrap our serializer and deserializer into a `Serde`:

```kotlin
val personSerde = Serdes.serdeFrom(PersonSerializer(), PersonDeserializer())
```

The rest of the code remains the same as in [part 6](/kafka-tutorial-6-kafka-streams-in-kotlin/)!

# Avro and the Schema Registry

Now, let's assume we have produced our messages in Avro format, as we did in [part 4](/kafka-tutorial-4-avro-and-schema-registry/). In [part 5](/kafka-tutorial-5-consuming-avro/), we had been able to consume this data by configuring the URL to the Schema Registry and by using a `KafkaAvroDeserializer`. Here, we need to use an instance of a `Serde`, so let's add a dependency to get one:

```gradle
repositories {
    mavenCentral()
    maven { url 'https://packages.confluent.io/maven/' }
}

dependencies {
    ...
    compile 'io.confluent:kafka-streams-avro-serde:5.0.0'
}
```

This dependency contains `GenericAvroSerde` and `SpecificAvroSerde`, two implementations of `Serde` that allow you to work with Avro records. We will use the former, and we need to configure it with the URL of the Schema Registry:

```kotlin
val avroSerde = GenericAvroSerde().apply {
    configure(mapOf(Pair("schema.registry.url", "http://localhost:8081")), false)
}
```

We can now create a KStream with this Serde, to get a KStream that contains `GenericRecord` objects:

```kotlin
val personAvroStream: KStream<String, GenericRecord> = streamsBuilder
        .stream(personsAvroTopic, Consumed.with(Serdes.String(), avroSerde))
```

We can finally "rehydrate" our model objects:

```kotlin
val personStream: KStream<String, Person> = personAvroStream.mapValues { personAvro ->
    val person = Person(
            firstName = personAvro["firstName"].toString(),
            lastName = personAvro["lastName"].toString(),
            birthDate = Date(personAvro["birthDate"] as Long)
    )
    logger.debug("Person: $person")
    person
}
```

And, again, the rest of the code remains the same as in [part 6](/kafka-tutorial-6-kafka-streams-in-kotlin/)!

We could make our code cleaner by creating our own Serde that would include the "rehydration" code, so that we would directly deserialize Avro objects into `Person` objects. To do so, we would have to extend the `GenericAvroDeserializer`. We will leave this exercise to the reader!

# Conclusion

We have seen how we can improve our Kafka Streams application to deserialize data in JSON or Avro format. The serialization part - when writing to a topic - would be very similar since we are using SerDes that are capable both of deserializing and serializing data.

Notice that if you are working in Scala, [the Kafka Streams Circe library](https://github.com/joan38/kafka-streams-circe) offers SerDes that handle JSON data through the Circe library (equivalent of Jackson in the Scala world).

The code of these tutorials can be found [here](https://github.com/aseigneurin/kafka-tutorials).

Feel free to ask questions in the comments section below!

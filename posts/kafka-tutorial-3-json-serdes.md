---
authors:
- Alexis Seigneurin
tags:
- Apache Kafka
- Kotlin
date: 2018-08-01T00:00:00.000Z
title: "Kafka tutorial #3 - JSON SerDes"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/08/kafka-logo.png
---

This is the third post in this series where we go through the basics of using Kafka. We saw in the [previous posts](https://blog.ippon.tech/tag/apache-kafka/) how to produce and consume JSON messages using the plain Java client and Jackson. We will see here how to create our own serializers and deserializers.

# What we are going to build in this tutorial

In the previous posts, we had created a Kotlin data class for our data model:

```kotlin
data class Person(
        val firstName: String,
        val lastName: String,
        val birthDate: Date
)
```

We were then using a Jackson `ObjectMapper` to convert data between `Person` objects and JSON strings:

```json
{"firstName":"Quentin","lastName":"Corkery","birthDate":"1984-10-26T03:52:14.449+0000"}
...
```

We had seen that we were using a `StringSerializer` in the producer, and a `StringDeserializer` in the consumer. We will now see how to build our own _SerDe_ (Serializer/Deserializer) to abstract the serialization/deserialization process away from the main code of the application.

# The serializer

To build a serializer, the first thing to do is to create a class that implements the `org.apache.kafka.common.serialization.Serializer` interface. This is a generic type so that you can indicate what type is going to be converted into an array of bytes:

```kotlin
class PersonSerializer : Serializer<Person> {
    override fun serialize(topic: String, data: Person?): ByteArray? {
        if (data == null) return null
        return jsonMapper.writeValueAsBytes(data)
    }

    override fun close() {}
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}
}
```

Notice that you might have to "help" the Kotlin compiler a little to let it know whether the data types are nullable or not (e.g. `Person` is non-nullable, `Person?` is nullable). In this case, I made the `data` parameter as well as the return value nullable so as to account for null values, just in case.

We can then replace the `StringSerializer` with our own serializer when creating the producer, and change the generic type of our producer:

```kotlin
private fun createProducer(brokers: String): Producer<String, Person> {
    ...
    props["value.serializer"] = PersonSerializer::class.java
    return KafkaProducer<String, Person>(props)
}
```

We can now send `Person` objects in our records without having the convert them to `String` by hand:

```kotlin
val fakePerson = Person(...)
val futureResult = producer.send(ProducerRecord(personsTopic, fakePerson))
```

# The deserializer

In a similar fashion, we can build a deserializer by creating a class that implements the `org.apache.kafka.common.serialization.Deserializer` interface:

```kotlin
class PersonDeserializer : Deserializer<Person> {
    override fun deserialize(topic: String, data: ByteArray?): Person? {
        if (data == null) return null
        return jsonMapper.readValue(data, Person::class.java)
    }

    override fun close() {}
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}
}
```

We then update the code that creates the consumer:

```kotlin
private fun createConsumer(brokers: String): Consumer<String, Person> {
    ...
    props["value.deserializer"] = PersonDeserializer::class.java
    return KafkaConsumer<String, Person>(props)
}
```

Finally, the value of our records contain `Person` objects rather than `String`s:

```kotlin
records.iterator().forEach {
    val person: Person = it.value()
    ...
}
```

# Conclusion

We have seen how to create our own SerDe to abstract away the serialization code from the main logic of our application. That was simple, but you now know how a Kafka SerDe works in case you need to use an existing one or build your own.

As Avro is a common serialization type for Kafka, we will see how to use Avro in the next post.

The code of these tutorials can be found [here](https://github.com/aseigneurin/kafka-tutorials).

Feel free to ask questions in the comments section below!

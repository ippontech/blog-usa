---
authors:
- Alexis Seigneurin
tags:
- Apache Kafka
- Kotlin
date: 2018-08-01T00:00:00.000Z
title: "Kafka tutorial #4 - Avro and the Schema Registry"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/08/kafka-logo.png
---

This is the fourth post in this series where we go through the basics of using Kafka. We saw in the [previous posts](https://blog.ippon.tech/tag/apache-kafka/) how to produce and consume data in JSON format. We will now see how to serialize our data with Avro.

# Avro and the Schema Registry

[Apache Avro](https://avro.apache.org/) is a binary serialization format. It relies on schemas (defined in JSON format) that define what fields are present and their type. Nested fields are supported as well as arrays.

Avro supports schema evolutivity: you can have multiple versions of your schema, by adding or removing fields. A little care needs to be taken to indicate fields as optional to ensure backward or forward compatibility.

Since Avro converts data into arrays of bytes, and that Kafka messages also contain binary data, we can ship Avro messages with Kafka. The real question is: _where to store the schema?_

The [Schema Registry](https://docs.confluent.io/current/schema-registry/docs/index.html) is the answer to this problem: it is a server that runs in your infrastructure (close to your Kafka brokers) and that stores your schemas (including all their versions). When you send Avro messages to Kafka, the messages contain an identifier of a schema stored in the Schema Registry.

A library allows you to serialize and deserialize Avro messages, and to interact transparently with the Schema Registry:
- When sending a message, the serializer will make sure the schema is registered, get its ID, or register a new version of the schema for you (this can be disabled by setting `auto.register.schemas` to `false`).
- When reading a message, the deserializer will find the ID of the schema in the message, and fetch the schema from the Schema Registry to deserialize the Avro data.

Both the Schema Registry and the library are under the Confluent umbrella: open source but not part of the Apache project. This means you will want to use the Confluent distribution to use the Schema Registry, not the Apache distribution.

# Defining the Avro schema

Let's start by defining an Avro schema. As a reminder, our model looks like this:

```kotlin
data class Person(
        val firstName: String,
        val lastName: String,
        val birthDate: Date
)
```

The corresponding Avro schema would be ([documentation is available on the project's site](http://avro.apache.org/docs/current/spec.html)):

```json
{
  "type": "record",
  "name": "Person",
  "namespace": "com.ippontech.kafkatutorials",
  "fields": [
    {
      "name": "firstName",
      "type": "string"
    },
    {
      "name": "lastName",
      "type": "string"
    },
    {
      "name": "birthDate",
      "type": "long"
    }
  ]
}
```

Let's save this under `src/main/resources/persons.avsc` (`avsc` = AVro SChema).

One thing to note is that I decided to serialize the date as a `long`. Avro doesn't have a dedicated date type, so you have to choose between a long and a string (an ISO-8601 string is usually better but I wanted to show how to use different data types in this example).

# Starting the Schema Registry and registering the schema

We have our schema. Now we need to register it in the Schema Registry.

Make sure you have downloaded the Confluent Platform, then start the Schema Registry:

```shell
$ bin/schema-registry-start etc/schema-registry/schema-registry.properties
...
[2018-08-02 11:24:15,570] INFO Started NetworkTrafficServerConnector@2dd80673{HTTP/1.1,[http/1.1]}{0.0.0.0:8081} (org.eclipse.jetty.server.AbstractConnector:289)
```

The Schema Registry is running on port 8081. It offers a REST API with which you can interact with Curl, for instance. Registering a schema is not very easy, though, because you have to embed the JSON schema into another JSON object, meaning you have to do some escaping... Instead, I have a [small Python scripts](https://gist.github.com/aseigneurin/5730c07b4136a84acb5aeec42310312c) to register a schema:

```shell
$ python src/main/resources/register_schema.py http://localhost:8081 persons-avro src/main/resources/person.avsc
Schema Registry URL: http://localhost:8081
Topic: persons-avro
Schema file: src/main/resources/person.avsc

Success
```

You have to provide the URL of the Schema Registry (starting with `http://`, not just a hostname and port), the topic for which the schema should be registered, and the path to the schema.

The equivalent Curl command would have been:

```shell
$ curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
  --data '{ "schema": "{ \"type\": \"record\", \"name\": \"Person\", \"namespace\": \"com.ippontech.kafkatutorials\", \"fields\": [ { \"name\": \"firstName\", \"type\": \"string\" }, { \"name\": \"lastName\", \"type\": \"string\" }, { \"name\": \"birthDate\", \"type\": \"long\" } ]}" }' \
  http://localhost:8081/subjects/persons-avro-value/versions

{"id":2}
```

Notice that we are registering the schema under a "subject" named `persons-avro-value`. The Schema Registry actually doesn't know anything about our Kafka topics (we have not even created the `persons-avro` topic yet), and it is a convention (used by the serializers) to register schemas under a name that follows the `<topic>-(key|value)` format. In this case, since the schema is for the value of the messages, the suffix is `-value`, but this means we could also use Avro for the keys of our messages.

We can check that our schema has been registered:

```shell
$ curl http://localhost:8081/subjects/persons-avro-value/versions/
[1]

$ curl http://localhost:8081/subjects/persons-avro-value/versions/1
{"subject":"persons-avro-value","version":1,"id":2,"schema":"{\"type\":\"record\",\"name\":\"Person\",\"namespace\":\"com.ippontech.kafkatutorials\",\"fields\":[{\"name\":\"firstName\",\"type\":\"string\"},{\"name\":\"lastName\",\"type\":\"string\"},{\"name\":\"birthDate\",\"type\":\"long\"}]}"}
```

# Producing Avro records

Now, we want to change our producer code to send Avro data. The first thing to know is that there are two flavors of Avro records:
- _specific records_: from the Avro schema, you generate Java classes using an Avro command - I don't like this approach too much though
- _generic records_: you use a data structure that is pretty much like a map/dictionary, meaning you get/set the fields by their names and have to know their type. Although this is not type-safe, this offers a lot more flexibility and is much easier to work with when your schema changes over time - this is the approach we are going to use here.

Before we can start coding, we need to add the library that adds Avro support to the Kafka client - this library is stored in Confluent's Maven repository:

```gradle
repositories {
    mavenCentral()
    maven { url 'https://packages.confluent.io/maven/' }
}
dependencies {
    ...
    compile 'io.confluent:kafka-avro-serializer:5.0.0'
}
```

Let's go ahead and modify our producer:

```kotlin
private fun createProducer(brokers: String, schemaRegistryUrl: String): Producer<String, GenericRecord> {
    val props = Properties()
    props["bootstrap.servers"] = brokers
    props["key.serializer"] = StringSerializer::class.java
    props["value.serializer"] = KafkaAvroSerializer::class.java
    props["schema.registry.url"] = schemaRegistryUrl
    return KafkaProducer<String, GenericRecord>(props)
}
```

Three things to note:
1. We switched to using the `KafkaAvroSerializer` for the value only, and kept the key serializer as is.
2. We added the URL of the Schema Registry under the `schema.registry.url` property.
3. We changed the type of the producer to accept objects of type `GenericRecord` for the value.

Now, because we are going to use generic records, we need to load the schema. We could get it from the Schema Registry, but this is not very safe because you don't know what version of the schema has been registered. Instead, **it is a good practice to store the schema alongside the code**. That way, your code always produces the right type of data, even if someone else changes the schema registered in the Schema Registry.

```kotlin
val schema = Schema.Parser().parse(File("src/main/resources/person.avsc"))
```

We can now create `GenericRecord` objects using a `GenericRecordBuilder`:

```kotlin
val avroPerson = GenericRecordBuilder(schema).apply {
    set("firstName", fakePerson.firstName)
    set("lastName", fakePerson.lastName)
    set("birthDate", fakePerson.birthDate.time)
}.build()
```

As in the first post, we used Kotlin's `apply` method to avoid repeating code.

We can now send the record:

```kotlin
val futureResult = producer.send(ProducerRecord(personsAvroTopic, avroPerson))
```

# Testing the code

So far, we still haven't created a new topic for our messages. Let's go ahead and create one:

```shell
$ kafka-topics --zookeeper localhost:2181 --create --topic persons-avro --replication-factor 1 --partitions 4
```

Notice that we're just creating a normal topic. Nothing here indicates the format of the messages.

Now, start the code in your IDE and launch a console consumer:

```shell
$ kafka-console-consumer --bootstrap-server localhost:9092 --topic persons-avro
TrystanCummerata��

Esteban
Smith�����&
```

This is not really pretty. Data is in binary format - we can read the strings but not the rest.

A better option is to use `kafka-avro-console-consumer` instead, which deserializes Avro records and prints them as JSON objects:

```shell
$ kafka-avro-console-consumer --bootstrap-server localhost:9092 --topic persons-avro
{"firstName":"Stephania","lastName":"Okuneva","birthDate":582023554621}
{"firstName":"Aleen","lastName":"Terry","birthDate":159202477258}
...
```

Sweet!

# Conclusion

We have seen how to produce Kafka messages in Avro format. This is fairly simple to do with the help of the Schema Registry and of the associated library.

One of the common mistakes is for a producer to fetch the schema from the Schema Registry prior to producing data. As I wrote above, I'd rather know exactly what schema I am producing data with, so keeping a copy of the schema alongside the code gives you that guarantee.

Now, notice that the Kafka Avro serializer will by default register your schema against the Schema Registry if it doesn't already exist, or if your schema differs from an already registered version. While this can be convenient in development, **I suggest disabling this functionality in production (`auto.register.schemas` property)**.

We will see in the next post how to consume the Avro messages we have produced!

The code of these tutorials can be found [here](https://github.com/aseigneurin/kafka-tutorials).

Feel free to ask questions in the comments section below!

---
authors:
- Alexis Seigneurin
tags:
- Apache Kafka
- Scala
date: 2018-08-15T14:51:35.000Z
title: "Kafka tutorial #8 - Spark Structured Streaming"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/08/kafka-logo.png
---

This is the post number 8 in this series where we go through the basics of using Kafka. So far, we have been using the Java client for Kafka, and Kafka Streams. This time, we are going to use [Spark Structured Streaming](http://spark.apache.org/docs/latest/structured-streaming-programming-guide.html) (the counterpart of Spark Streaming that provides a Dataframe API).

Although we used Kotlin in the previous posts, we are going to code in Scala this time. This is because Spark's Java API is more complicated to use than the Scala API.

# What we are going to build in this first tutorial

We are going to reuse the example from [part 1](/kafka-tutorial-1-simple-producer-in-kotlin/) and [part 2](/kafka-tutorial-2-simple-consumer-in-kotlin/) of this tutorial. In part 1, we created a producer than sends data in JSON format to a topic:

```json
{"firstName":"Quentin","lastName":"Corkery","birthDate":"1984-10-26T03:52:14.449+0000"}
{"firstName":"Neil","lastName":"Macejkovic","birthDate":"1971-08-06T18:03:11.533+0000"}
...
```

We are going to build the consumer that processes the data to calculate the age of the persons, as we did in part 2:

```text
Quentin Corkery	33
Lysanne Beer	20
...
```

# Creating the project

We are going to create a separate project using SBT (Scala Build Tool). Let's create the `build.sbt` file:

```scala
name := "kafka-tutorials"
organization := "com.ippontech"
version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.12"

val sparkVersion = "2.2.2"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-library" % scalaVersion.value,

  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion
)
```

We added dependencies for Spark SQL - necessary for Spark Structured Streaming - and for the Kafka connector. A few notes about the versions we used:
- All the dependencies are for Scala 2.11. Support for Scala 2.12 was recently added but not yet released.
- We are using Spark 2.2.2, not the latest version - 2.3.1 - because I found [an issue](https://issues.apache.org/jira/browse/SPARK-25106) in that version with the Kafka client being created again for every micro-batch.
- The Kafka connector is for Kafka 0.10, which is literally 3 versions behind (0.11, 1.0, 2.0). This is a bit worrying but will hopefully work just fine...

We can now add a `log4j.properties` file under the `src/main/resources` directory to configure the logging, and we can start coding.

# Structure of the code

Let's create a class called `StreamsProcessor` with a constructor to initialize the `brokers` field, and add a `process()` method where we will do all the work:

```scala
class StreamsProcessor(brokers: String) {
  def process(): Unit = {
    ...
  }
}
```

We can create the entry point of our application by writing a `main` function. In Scala, a `static` method needs to be in an `object`, not in a `class`, so let's create one:

```scala
object StreamsProcessor {
  def main(args: Array[String]): Unit = {
    new StreamsProcessor("localhost:9092").process()
  }
}
```

We can now initialize Spark in the `process()` method:

```scala
val spark = SparkSession.builder()
  .appName("kafka-tutorials")
  .master("local[*]")
  .getOrCreate()

import spark.implicits._
```

We are going to work locally, running the application straight from our IDE, so we are setting the master to `local[*]`, meaning we are creating as many threads as there are cores on the machine.

We also imported Spark's implicit conversions to make it easier to work with Dataframes, in particular for column selectors (`$"<column name>"`).

# Reading from Kafka

Thanks to the Kafka connector that we added as a dependency, Spark Structured Streaming can read a stream from Kafka:

```scala
val inputDf = spark.readStream
  .format("kafka")
  .option("kafka.bootstrap.servers", brokers)
  .option("subscribe", "persons")
  .load()
```

Let's see the structure of the Dataframe by calling `.printSchema()` on it:

```text
root
 |-- key: binary (nullable = true)
 |-- value: binary (nullable = true)
 |-- topic: string (nullable = true)
 |-- partition: integer (nullable = true)
 |-- offset: long (nullable = true)
 |-- timestamp: timestamp (nullable = true)
 |-- timestampType: integer (nullable = true)
```

Each message will have columns for:
- the key and the value, **as binary data** - deserializers cannot be configured, you have to use Dataframe operations to deserialize the data
- the topic, partition and offset of the message
- the timestamp of the message, and a type for the timestamp (always `0`).

Now, if we wanted to visualize the data, we could not just call `.show()` on the Dataframe. Instead, we have to write a _streaming query_ that outputs the content of the Dataframe to the console:

```scala
val consoleOutput = inputDf.writeStream
  .outputMode("append")
  .format("console")
  .start()
consoleOutput.awaitTermination()
```

And the output would look like:

```text
-------------------------------------------
Batch: 8
-------------------------------------------
+----+--------------------+-------+---------+------+--------------------+-------------+
| key|               value|  topic|partition|offset|           timestamp|timestampType|
+----+--------------------+-------+---------+------+--------------------+-------------+
|null|[7B 22 66 69 72 7...|persons|        0| 25582|2018-08-14 12:02:...|            0|
+----+--------------------+-------+---------+------+--------------------+-------------+

-------------------------------------------
Batch: 9
-------------------------------------------
+----+--------------------+-------+---------+------+--------------------+-------------+
| key|               value|  topic|partition|offset|           timestamp|timestampType|
+----+--------------------+-------+---------+------+--------------------+-------------+
|null|[7B 22 66 69 72 7...|persons|        0| 25583|2018-08-14 12:02:...|            0|
+----+--------------------+-------+---------+------+--------------------+-------------+
...
```

We can see that the value of the messages is indeed binary data.

# Converting the data

Spark sees the data in binary format, but we know it is actually JSON data. Let's first convert the messages to strings:

```scala
val personJsonDf = inputDf.selectExpr("CAST(value AS STRING)")
```

The output now looks like:

```text
+--------------------+
|               value|
+--------------------+
|{"firstName":"Rey...|
+--------------------+
```

We can now deserialize the JSON. The easiest is to use Spark's `from_json()` function from the `org.apache.spark.sql.functions` object. We need to provide the structure (list of fields) of the JSON data so that the Dataframe can reflect this structure:

```scala
val struct = new StructType()
  .add("firstName", DataTypes.StringType)
  .add("lastName", DataTypes.StringType)
  .add("birthDate", DataTypes.StringType)

val personNestedDf = personJsonDf.select(from_json($"value", struct).as("person"))
```

At this point, the Dataframe contains nested columns, as indicated by the schema:

```text
root
 |-- person: struct (nullable = true)
 |    |-- firstName: string (nullable = true)
 |    |-- lastName: string (nullable = true)
 |    |-- birthDate: string (nullable = true)
 ```

Let's _flatten_ this Dataframe:

```scala
val personFlattenedDf = personNestedDf.selectExpr("person.firstName", "person.lastName", "person.birthDate")
```

```text
root
 |-- firstName: string (nullable = true)
 |-- lastName: string (nullable = true)
 |-- birthDate: string (nullable = true)
```

As you may have noticed, we read the birth date as a string. This is because, even though the `from_json()` function relies on Jackson, there is no way to specify the format of the date to read at that time (we used an ISO-8601 format).

Instead, we are going to use Spark's `to_timestamp()` function (there is also a `to_date()` function if you want to read a date with a time indication). Notice that, in this case, we are overriding the `birthDate` column by creating a new column with the same name:

```scala
val personDf = personFlattenedDf.withColumn("birthDate", to_timestamp($"birthDate", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
```

# Processing the data

We can now calculate the age of the persons. Spark doesn't have a built-in function to calculate the number of years between two dates, so we are going to create a User Defined Function (UDF).

We start by creating a regular Scala function (or lambda, in this case) taking a `java.sql.Timestamp` in input (this is how timestamps are represented in a Spark Datateframe), and returning an `Int`:

```scala
val ageFunc: java.sql.Timestamp => Int = birthDate => {
  val birthDateLocal = birthDate.toLocalDateTime().toLocalDate()
  val age = Period.between(birthDateLocal, LocalDate.now()).getYears()
  age
}
```

We can now wrap this function in a Spark UDF:

```scala
val ageUdf: UserDefinedFunction = udf(ageFunc, DataTypes.IntegerType)
```

We can finally apply the UDF to the `birthDate` column to create a new column called `age`:

```scala
val processedDf = personDf.withColumn("age", ageUdf.apply($"birthDate"))
```

# Preparing the output

Now that we have processed the data to calculate the age of the persons, we need to get ready to output the data to another Kafka topic.

The Kafka connector supports writing to Kafka. The requirement is for the Dataframe to have columns named `key` and `value`, both either of type string or binary.

In our case, we want the key to be in the form `<first name><space><last name>`. We can use the `concat()` function as well as the `lit()` function for the space. For the value, we will need to convert the `age` column to a string.

We then use `.select()` on the Dataframe to compute these two columns and assign them names thanks to the `as()` method (`alias()` would do the same).

```scala
val resDf = processedDf.select(
  concat($"firstName", lit(" "), $"lastName").as("key"),
  processedDf.col("age").cast(DataTypes.StringType).as("value"))
```

Our Dataframe is ready, in the format that is expected by the Kafka connector, so we can instruct Spark to output the stream of data to Kafka:

```scala
val kafkaOutput = resDf.writeStream
  .format("kafka")
  .option("kafka.bootstrap.servers", brokers)
  .option("topic", "ages")
  .option("checkpointLocation", "/Users/aseigneurin/dev/kafka-tutorials/spark/checkpoints")
  .start()

kafkaOutput.awaitTermination()
```

Notice that we have to specify a location for checkpoints. This is required for Kafka's High Availability feature, although this does not guarantee that the messages will be written exactly once: Spark only provides at-least-once delivery guarantee.

# Testing the code

To test this code, let's first start the producer we created in [part 1](/kafka-tutorial-1-simple-producer-in-kotlin/) and run a console consumer to visualize the data:

```json
{"firstName":"Corene","lastName":"Glover","birthDate":"1999-12-26T17:07:18.279+0000"}
{"firstName":"Forest","lastName":"Spencer","birthDate":"1979-04-29T08:07:12.123+0000"}
...
```

Now, let's run the Spark application straight from our IDE and run a console consumer on the `ages` topic:

```text
Corene Glover	18
Forest Spencer	39
...
```

It all looks good!

Once thing to know, if you left a console output in your code, is that you need to start all the queries before you call `awaitTermination()` on any of them, e.g.:

```scala
val consoleOutput = processedDf.writeStream
  ...
  .start()
val kafkaOutput = resDf.writeStream
  ...
  .start()

kafkaOutput.awaitTermination()
consoleOutput.awaitTermination()
```

# Conclusion

If you already use Spark to process data in batch with Spark SQL, Spark Structured Streaming is appealing. It offers the same Dataframes API as its batch counterpart.

My personal opinion is more contrasted, though:
- Spark (Structured) Streaming is oriented towards throughput, not latency, and this might be a big problem for processing streams of data with low latency.
- Support for Kafka in Spark has never been great - especially as regards to offset management - and the fact that the connector still relies on Kafka 0.10 is a concern.
- The deployment model - and the impact it has on how you upgrade applications - is complex, especially in comparison with what Kafka Streams has to offer.

It is up to you to make your own opinion but, at the time of this writing, Kafka Streams is my preferred option for building a streaming application.

The code of these tutorials can be found [here](https://github.com/aseigneurin/kafka-tutorials).

Feel free to ask questions in the comments section below!

---
authors:
- Raphael Brugier
tags:
- Big Data
- Apache Spark
- MongoDB
date: 2017-05-03T10:20:00.000Z
title: "MongoDB and Apache Spark - Getting started tutorial"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/04/MongoDB-and-Apache-Spark-Blog-2.png
---

MongoDB and Apache Spark are two popular Big Data technologies.

In my [previous post](http://blog.ippon.tech/introduction-to-the-mongodb-connector-for-apache-spark/), I listed the capabilities of the [MongoDB connector for Spark](https://docs.mongodb.com/spark-connector/v2.0/). In this tutorial, I will show you how to configure Spark to connect to MongoDB, load data, and write queries.

To demonstrate how to use Spark with MongoDB, I will use the zip codes from MongoDB tutorial on [the aggregation pipeline documentation using a zip code data set](https://docs.mongodb.com/v3.2/tutorial/aggregation-zip-code-data-set/). I have prepared a Maven project and a Docker Compose file to get you started quickly.

## Prerequisites
- Install [Docker and Docker Compose](https://docs.docker.com/engine/installation/)
- Install Maven
- Download the project [from Github](https://github.com/raphaelbrugier/spark-mongo-example)

From the project root, launch the MongoDB server with docker-compose:
`docker-compose -f docker/docker-compose.yml up -d`

Import the data in the MongoDB database running in the container:
`docker exec -it mongo_container sh /scripts/import-data.sh`

Check that the data has been loaded in MongoDB by connecting to the container and running a count:
`docker exec mongo_container mongo --eval "db.zips.count()"`

This should return:
```bash
MongoDB shell version: 3.2.11
connecting to: test
29353
```

The zips collection is a collection of _Document_ with the following model:
```json
{
  "_id": "10280",
  "city": "NEW YORK",
  "state": "NY",
  "pop": 5574,
  "loc": [
    -74.016323,
    40.710537
  ]
}
```

Import the Maven project in your favorite IDE. Create a new file _Main.scala_ to copy the examples or run the `MongoSparkMain` for the solution.

## Read data from MongoDB to Spark
In this example, we will see how to configure the connector and read from a MongoDB collection to a DataFrame.

First, you need to create a minimal SparkContext, and then to configure the `ReadConfig` instance used by the connector with the MongoDB URL, the name of the database and the collection to load:

```scala
import com.mongodb.spark._
import com.mongodb.spark.config.ReadConfig
import com.mongodb.spark.sql._
import com.typesafe.scalalogging.slf4j.LazyLogging
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{max, min}
import org.bson.Document

object Main extends App with LazyLogging {
    val spark = SparkSession.builder()
    .appName("mongozips")
    .master("local[*]")
    .getOrCreate()

  // Read the data from MongoDB to a DataFrame
  val readConfig = ReadConfig(Map("uri" -> "mongodb://127.0.0.1/", "database" -> "test", "collection" -> "zips")) // 1)
  val zipDf = spark.read.mongo(readConfig) // 2)
  zipDf.printSchema() // 3)
  zipDf.show()
}
```

1. Set the MongoDB URL, database, and collection to read.
2. The connector provides a method to convert a MongoRDD to a DataFrame. The DataFrame’s schema is automatically inferred by the connector by [sampling](https://docs.mongodb.com/v3.2/reference/operator/aggregation/sample/) the collection. Alternatively, you can explicitly pass a schema definition.
3. Print the schema inferred by the connector.

Results:
```text
root
 |-- _id: string (nullable = true)
 |-- city: string (nullable = true)
 |-- loc: array (nullable = true)
 |    |-- element: double (containsNull = true)
 |-- pop: integer (nullable = true)
 |-- state: string (nullable = true)

+-----+-----------+--------------------+-----+-----+
|  _id|       city|                 loc|  pop|state|
+-----+-----------+--------------------+-----+-----+
|01001|     AGAWAM|[-72.622739, 42.0...|15338|   MA|
|01002|    CUSHMAN|[-72.51565, 42.37...|36963|   MA|
|01005|      BARRE|[-72.108354, 42.4...| 4546|   MA|
|01007|BELCHERTOWN|[-72.410953, 42.2...|10579|   MA|
|01008|  BLANDFORD|[-72.936114, 42.1...| 1240|   MA|
+-----+-----------+--------------------+-----+-----+
only showing top 5 rows
```

The connector has correctly inferred the schema based on the documents sampling. Both the column names and types have been identified accurately.

### Inferring inner-documents

Interestingly, the `loc` array from the MongoDB document has been translated to a Spark’s Array type.

But what if the document contains inner documents? The connector does not flatten the inner document but translates them as a Spark’s StructType, a key-value type.

Take for example this MongoDB document:

```json
{
  name: "Joe Bookreader",
  country: {
    isocode: "USA",
    name: "United States"
  },
  addresses: [
    {
      street: "123 Fake Street",
      city: "Faketon",
      state: "MA",
      zip: "12345"
    }
  ]
}
```

The document has two inner documents. The first one is the country and the second one is an address contained in a list.

After loading the collections, the schema inferred by the connector shows a StructType for both the `country` and the `address` in the array of addresses.

```scala
val personDf = spark.read.mongo(ReadConfig(Map("uri" -> "mongodb://127.0.0.1/", "database" -> "test", "collection" -> "person")))
personDf.printSchema()
```

Results:
```text
root
 |-- _id: struct (nullable = true)
 |    |-- oid: string (nullable = true)
 |-- addresses: array (nullable = true)
 |    |-- element: struct (containsNull = true)
 |    |    |-- street: string (nullable = true)
 |    |    |-- city: string (nullable = true)
 |    |    |-- state: string (nullable = true)
 |    |    |-- zip: string (nullable = true)
 |-- country: struct (nullable = true)
 |    |-- isocode: string (nullable = true)
 |    |-- name: string (nullable = true)
 |-- name: string (nullable = true)
```

The values in the StructType types can be accessed by their column names:

```scala
personDf.select($"_id", $"addresses"(0)("street"), $"country"("name"))
```

You can find the list of the mappings between the MongoDB types and the DataFrame’s types in the [connector's documentation](https://docs.mongodb.com/spark-connector/v2.0/scala/datasets-and-sql/#datatypes).

## Use the Spark API to query the data
After loading the collection in a DataFrame, we can now use the Spark API to query and transform the data.

As an example, we write a query to find the states with a population greater or equal to 10 million. The example also shows how the Spark API can easily map to the original MongoDB query.

The MongoDB query is:
```scala
db.zipcodes.aggregate( [
   { $group: { _id: "$state", totalPop: { $sum: "$pop" } } },
   { $match: { totalPop: { $gte: 10*1000*1000 } } }
] )
```

And now the Spark query:
```scala
println( "States with Populations above 10 Million" )
import zipDf.sqlContext.implicits._ // 1)
zipDf.groupBy("state")
    .sum("pop")
    .withColumnRenamed("sum(pop)", "count") // 2)
    .filter($"count" > 10000000)
    .show()
```

Result:

```text
States with Populations above 10 Millions:
+-----+--------+
|state|   count|
+-----+--------+
|   TX|16984601|
|   NY|17990402|
|   OH|10846517|
|   IL|11427576|
|   CA|29754890|
|   PA|11881643|
|   FL|12686644|
+-----+--------+
```

The DataFrame API is pretty straight forward for this simple query.

1. Use the import to have implicit conversions from `String` to `Column` with the `$`.
2. Rename the result of the sum column for readability.

## Spark SQL
Spark and the DataFrame abstraction also enables to write plain Spark SQL queries with a familiar SQL syntax.

For example, let’s rewrite the previous query with SQL:

```scala
// SparkSQL:
  zipDf.createOrReplaceTempView("zips") // 1)
  zipDf.sqlContext.sql( // 2)
    """SELECT state, sum(pop) AS count
      FROM zips
      GROUP BY state
      HAVING sum(pop) > 10000000"""
  )
  .show()
```
1. Register the DataFrame as a Spark SQL table.
2. Execute the Spark SQL query.

## Predicates pushdown
_“Predicates pushdown”_ is an optimization from the connector and the Catalyst optimizer to automatically “push down” predicates to the data nodes. The goal is to maximize the amount of data filtered out on the data storage side before loading it into Spark’s node memory.

There are two kinds of predicates automatically pushed down by the connector to MongoDB:

- the `select` clause (projections) as a [`$project`](https://docs.mongodb.com/manual/reference/operator/aggregation/project/)
- the `filter` clause content (`where`) as one or more [`$match`](https://docs.mongodb.com/manual/reference/operator/aggregation/match/)

Both are sent by the connector as an [aggregation pipeline](https://github.com/mongodb/mongo-spark/blob/master/src/main/scala/com/mongodb/spark/sql/MongoRelationHelper.scala#L30).

To verify if the predicates are sent, we use the Spark’s _explain_ method to examine the query plan produced by Spark for a simple query with a filter:

```scala
zipDf
    .filter($"pop" > 0)
    .select("state")
    .explain(true)
```

Output:

```text
== Parsed Logical Plan ==
[...]

== Analyzed Logical Plan ==
[...]

== Optimized Logical Plan ==
[...]

== Physical Plan ==
*Project [state#4]
+- *Filter (isnotnull(pop#3) && (pop#3 > 0))
   +- *Scan MongoRelation(MongoRDD[0] at RDD at MongoRDD.scala:52,Some(StructType(StructField(_id,StringType,true), StructField(city,StringType,true), StructField(loc,ArrayType(DoubleType,true),true), StructField(pop,IntegerType,true), StructField(state,StringType,true)))) [state#4,pop#3] PushedFilters: [IsNotNull(pop), GreaterThan(pop,0)], ReadSchema: struct<state:string>
```

We can see in the physical plan generated by the Catalyst optimizer, the name of the fields to project (`state` and `pop`), and the filters to push (pop not null and pop greater than 0).

To confirm what is actually executed on the MongoDB nodes, we need to increase MongoDB’s log level and examine the `system.profile` collection.

Enable the logging on MongoDB, run the Spark query again, and find the trace of the query in the `system.profile` collection:

```bash
$mongo
MongoDB shell version: 3.2.11
connecting to: test

> db.setProfilingLevel(2)
> db.system.profile.find().pretty()
```

The result is:
```json
{
	"op" : "command",
	"ns" : "test.zips",
	"command" : {
		"aggregate" : "zips",
		"pipeline" : [
			{
				"$match" : {
					"_id" : {
						"$gte" : { "$minKey" : 1 },
						"$lt" : { "$maxKey" : 1 }
					}
				}
			},
			{
				"$match" : {
					"pop" : {
						"$exists" : true,
						"$ne" : null,
						"$gt" : 0
					}
				}
			},
			{
				"$project" : {
					"state" : 1,
					"pop" : 1,
					"_id" : 0
				}
			}
		],
		"cursor" : {

		},
		"allowDiskUse" : true
	},
	[...]
}
```
The result shows the `$project` and `$match` clauses executed by MongoDB and, as expected, they match the Spark’s physical plan.

## Conclusion
In this article, I have shown how to connect to a MongoDB database with Apache Spark to load and query the data. The connector provides a set of utility methods to easily load data from MongoDB to a DataFrame.

I have also presented how a MongoDB _Document_ is mapped to a Spark’s DataFrame. Because of the hierarchical nature of a _Document_, only the first level of attributes is mapped to columns. Inner documents become nested columns.

Finally, I have described how the connector minimizes the data loaded in Spark by taking advantage of the predicates pushdown optimization, an essential feature of every connector. The connector does a good job of sending the predicates automatically, but it is helpful to know how to confirm if and how the predicates are applied on the MongoDB side.

In conclusion, the connector is fully functional to take benefit from using Spark on top of MongoDB.

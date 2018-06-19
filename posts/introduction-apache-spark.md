---
authors:
- Alexis Seigneurin
categories:
- 
date: 2014-11-11T09:34:15.000Z
title: "Introduction to Apache Spark"
id: 5a267e57dd54250018d6b5d5
image: 
---

[Spark](http://spark.apache.org/) is a tool intended to process large volumes of data in a distributed fashion (cluster computing). Programming in Spark is simpler than in Hadoop, and Spark speeds up execution time by a factor of up to 100.
 Spark has a lot of momentum (almost as much as Docker) and is replacing Hadoop at an unbelievable pace. Hadoop usage, when used strictly for Map Reduce use cases, is decreasing fast.
 This article is the first in a series designed to discover Spark, its programming model and its ecosystem. The code presented is written in Java.

## Background

Spark was born in 2009 in the laboratory [AMPLab](https://amplab.cs.berkeley.edu/) the University of Berkeley on the assumption that:

- on one hand, RAM is becoming less expensive and therefore the servers have more RAM available
- on the other hand, many data sets called “Big Data” data have a size of about 10 GB, so they can be held in memory

The project integrated the Apache Incubator in June 2013 and became a “Top-Level Project” in [February 2014](https://blogs.apache.org/foundation/entry/the_apache_software_foundation_announces50).

Spark version 1.0.0 was released in [May 2014](http://spark.apache.org/news/spark-1-0-0-released.html) and the project is now at version 1.1.0. It is undergoing rapid changes. The ecosystem now includes Spark and several tools:

- Spark for “batch” processing
- Spark Streaming for continuous processing of data stream
- MLlib for “machine learning”
- GraphX ​​for graph calculations (still in alpha)
- Spark SQL, an implementation of SQL-like query data

Moreover, Spark integrates seamlessly with the Hadoop ecosystem (HDFS particular), and integrations with Cassandra and ElasticSearch are planned.
 Finally, the framework is written in Scala and offers a Java binding that is easy to use. Java 8 is recommended, in order to benefit from lambda expressions that make code easier to write and more legible.

## Fundamentals

The basic element is we will manipulate is the RDD: *Resilient Distributed Dataset*.

An RDD is a collection abstraction on which operations are performed in a distributed manner while being tolerant of hardware failures. The processing is written and seems to run in our JVM but it will be cut to run on multiple nodes. If a node fails, the sub-processing is automatically restarted on another node by the framework, without impacting the results.

The elements manipulated by the RDD (classes `JavaRDD`, `JavaPairRDD`…) can be simple classes (String, Integer…), your own custom classes, or, more frequently, tuples (`Tuple2` class). In the latter case, the operations provided by the API will manipulate the collection as a key-value map.

The API exposed by the RDD can perform transformations on the data:

- `map()` can turn one element into another element
- `mapToPair()` converts a key element to a tuple value
- `filter()` allows you to filter elements by keeping only those that match an expression
- `flatMap()` can cut an element into several others
- `reduce()` and `reduceByKey()` allows the aggregation of elements together
- etc.

These transformations are “lazy”: they will not run until a final operation is carried out at the end of the chain. Some of the final operations are:

- `count()` to count the elements
- `collect()` to retrieve the items in a collection in the Java JVM executor (dangerous in a cluster)
- `saveAsTextFile()` to save the result into text files (see below)
- etc.

Finally, the API can temporarily store an intermediate result with the methods `cache()` (memory storage) or `persist()` (storage in memory or on disk, depending on a parameter).

## First Steps

For a first code example, we use the data of [Open Data of the Town Hall of Paris](http://opendata.paris.fr/), in this case the list of [alignment trees](http://opendata.paris.fr/explore/dataset/arbresalignementparis2010/?tab=metas) in the town of Paris .
 The CSV file can be downloaded [here](http://opendata.paris.fr/explore/dataset/arbresalignementparis2010/download/?format=csv). It contains 103,589 records. Here is an excerpt:

```language-none
geom_x_y;geom;genre_lati;genre_fran;variete;arbre_rema;circonfere;hauteur_m;date_mesur;lib_type_e;lib_etat_c;x;y
48.8560632291, 2.34626065083;"{""type"": ""Point"", ""coordinates"": [2.346260650825456, 48.856063229114774]}";Platanus;Platane;;0;90.0;0.0;0001-01-01T00:00:00+00:00;Trottoir;grille fonte/acier ajourée;600715.8125;128400.648438
48.8574478618, 2.3458179812;"{""type"": ""Point"", ""coordinates"": [2.345817981197062, 48.85744786180517]}";Platanus;Platane;;0;0.0;0.0;0001-01-01T00:00:00+00:00;Trottoir;Terre sable;600683.3125;128554.617188
48.8329651219, 2.31476577319;"{""type"": ""Point"", ""coordinates"": [2.314765773191272, 48.8329651219174]}";Prunus;Cerisier à fleur;hisakura-new-red;0;0.0;0.0;0001-01-01T00:00:00+00:00;Trottoir;Stabilisé;598404.0;125832.320313
```

This file has the following characteristics:

- line header
- one record per line
- the fields of a record are separated by a semicolon.

We’ll just count the records for which the height of the tree is populated and is greater than zero.
 You must first create a “Spark context”. Since we code this in Java, the class we use is `JavaSparkContext` and we pass it a configuration object containing:

- an application name (useful when the application is deployed in cluster)
- the reference to a Spark cluster to use, the “local” occurrence to perform processing within the current JVM.

```language-java
SparkConf conf = new SparkConf()
.setAppName(“arbres-alignement”)
.setMaster(“local”);
JavaSparkContext sc = new JavaSparkContext(conf);
```

We can then write the following code and get the result:

```language-java
sc.textFile(“arbresalignementparis2010.csv”)
  .filter(line -> !line.startsWith(“geom”))
  .map(line -> line.split(“;”))
  .map(fields -> Float.parseFloat(fields[7]))
  .filter(height -> height > 0)
  .count();
System.out.println(count);
```

Let’s walk through the code:

- We start by asking Spark to read the CSV file. Spark can natively read a text file and cut it into lines.The method used is `textFile()` and the type returned is `JavaRDD<String>` (a Strings RDD).

```language-java
sc.textFile("arbresalignementparis2010.csv")
```

- We filter directly the first line (the header line). This filtering is performed by content rather than by line number. Indeed, RDD elements are not ordered since a file can be read in fragments, especially when it is a long read on a cluster file. The method used is `filter()` and it does not change the return type which remains `JavaRDD<String>`.

```language-java
  .filter(line -> !line.startsWith("geom"))
```

- The lines can then be divided into fields. We use a lambda expression that can be read as follows: for each element we call `line`, return the result of the expression `line.split(“;”)`. The `map()` operation is used and the type returned becomes `JavaRDD<String[]>`.  `.map(line -> line.split(";"))`

- The field containing the height of the tree can then be parsed. We keep this value: the other fields are not preserved. The `map()` operation is used again and the return type is `JavaRDD<Float>`.

```language-java
  .map(fields -> Float.parseFloat(fields[7]))
```

- We then filter the elements to keep only the heights greater than zero. The transaction `filter()` is used again. The type remains `JavaRDD<Float>`.

```language-java
  .filter(height -> height > 0)
```

- Finally, we have the elements of the RDD.
 The final `count()` operation is used `long` is returned.

```language-java
  .count()
```

Here is an excerpt of the console output:

```language-none
...
14/10/29 17:09:54 INFO FileInputFormat: Total input paths to process : 1 14/10/29 17:09:54 INFO SparkContext: Starting job: count at FirstSteps.java:26 14/10/29 17:09:54 INFO DAGScheduler: Got job 0 (count at FirstSteps.java:26) with 1 output partitions (allowLocal=false)
...
14/10/29 17:09:54 INFO TaskSetManager: Starting task 0.0 in stage 0.0 (TID 0, localhost, PROCESS_LOCAL, 1242 bytes) 14/10/29 17:09:54 INFO Executor: Running task 0.0 in stage 0.0 (TID 0) 14/10/29 17:09:54 INFO HadoopRDD: Input split: file:/Users/aseigneurin/dev/spark-samples/arbresalignementparis2010.csv:0+25008725
...
14/10/29 17:09:55 INFO SparkContext: Job finished: count at FirstSteps.java:26, took 0.475835815 s 6131
```

Spark processed the file locally, within the JVM.

The file was read in a single block. Indeed, the file size is 25 MB, and by default Spark cuts files into blocks of 32MB.

The result (6131) is obtained in less than half a second. This execution time is not really impressive in this case, but we will see the power of the framework when we handle larger files.

## Conclusion

The code written with Spark has the advantage of being compact and easy to read. We will see in the next few posts that it is possible to handle very large volumes of data, even for operations manipulating the entire dataset, and without having to change the code.

You can find the complete example code on [GitHub](https://github.com/aseigneurin/spark-sandbox).

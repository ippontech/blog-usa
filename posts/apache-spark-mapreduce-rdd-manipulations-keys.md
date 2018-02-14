---
authors:
- Alexis Seigneurin
categories:
- 
date: 2014-12-30T15:26:45.000Z
title: "Apache Spark: MapReduce and RDD manipulations with keys"
image: 
---

In a [previous article](http://www.ipponusa.com/intro-mapreduce-spark/ "Intro to MapReduce operations with Spark"), we saw that Apache Spark allows us to perform aggregations on every value of an RDD. We will now perform aggregations and other manipulations with keys.

### **Approach**

A key-based reduction operation aggregates values for each RDD key. This type of operation can only be performed on an RDD of type `JavaPairRDD`, which is an RDD in which elements are key-value tuples. Note that, unlike with Java Map objects, there is no unique constraint on keys. Multiple tuples with the same key may exist in the RDD.

The reduction operation will be performed on the values of the same key until there is only one value per key. The resulting RDD will then be a true key-value Map, with unique keys.

Let’s take the following RDD as example (key, value):

```language-none
(A, 3)
(A, 5)
(B, 2)
(A, 4)
(B, 7)
```

After application of a reduction by keys, summing values, we obtain:

```language-none
(A, 12)
(B, 9)
```

The RDD contains as many tuples as there were distinct keys in the original RDD.

### **Sample data**

We are going to use Wikipedia traffic metrics as a dataset. These files are public and free. One file is produced every hour. Each file is about 300 MB, once uncompressed.

Here is an example file extract:

```language-none
fr Louvoy%C3%A8rent 1 7766
fr Louvoyer 2 24276
fr Louvre-Lens 1 39497
fr Louvres 1 36541
fr Louxor 2 33183
```

Each line represents a record with 4 space-delimited fields:

- the name of the Wikipedia project: country code, sometimes followed by a suffix indicating if the site is Wikipedia, Wikibooks, Wiktionary, etc.
- the URL-encoded page title
- the number of requests received
- the size of the page in bytes

### **Let’s practice**

From the Wikipedia statistics, we can calculate the number of visits per project. Unlike with a global aggregation, we want to obtain a key-value list: project code – total number of visits.

Let’s first read a file by lines and split each record:

```language-java
sc.textFile(“data/wikipedia-pagecounts/pagecounts-20141101-000000″)
  .map(line -> line.split(” “))
```

We obtain a `JavaRDD<String[]>`.

We can transform this `JavaRDD` into a `JavaPairRDD` through the `mapToPair()` operation. And then we have to return objects of type `Tuple2`:

```language-java
  .mapToPair(s -> new Tuple2<String , Long>(s[0], Long.parseLong(s[2])))
```

The JavaPairRDD provides transformations allowing us to natively work on this key-value collection: `reduceByKey()`, `sortByKey()`, as well as functions that can combine two `JavaPairRDD`s: `join()`, `intersect()`, etc.

For example, we will use the `reduceByKey()` function with a sum operation. Returned values will belong to the same key, without us having knowledge of the key:

```language-java
  .reduceByKey((x, y) -> x + y)
```

At last, we can output all the tuples to the console. The tuple key is represented by field `_1`, while the value is represented by field `_2`.

```language-java
  .foreach(t -> System.out.println(t._1 + ” -> ” + t._2))
```

The entire code is:

```language-java
SparkConf conf = new SparkConf().setAppName(“wikipedia-mapreduce-by-key”)
.setMaster(“local”);
JavaSparkContext sc = new JavaSparkContext(conf);
sc.textFile(“data/wikipedia-pagecounts/pagecounts-20141101-000000″)
  .map(line -> line.split(” “))
  .mapToPair(s -> new Tuple2<String , Long>(s[0], Long.parseLong(s[2])))
  .reduceByKey((x, y) -> x + y)
  .foreach(t -> System.out.println(t._1 + ” -> ” + t._2));
```

When executing this, we get a result similar to:

```language-none
got.mw -> 14
mo.d -> 1
eo.q -> 38
fr -> 602583
ja.n -> 167
mus -> 21
xal -> 214
```

The value 3269849 for Wikipedia France is then the sum of visits marked as “fr” in the file.

### **Sorting results by key**

We notice that results are not sorted. Indeed, for performance reasons, Apache Spark does not guarantee order in the RDD: tuples are processed independently.

We can sort tuples by key with the sortByKey() method which takes a boolean as input to reverse the sort:

```language-java
  .sortByKey()
```

The result is:

```language-none
AR -> 195
De -> 115
EN -> 4
En -> 10
En.d -> 8
FR -> 1
It -> 2
SQ.mw -> 11
Simple -> 1
aa -> 27
aa.b -> 6
aa.d -> 1
aa.mw -> 11
…
```

The sort is case-sensitive. To perform a case-insensitive sort, we can use a comparator. Unfortunately, we cannot use a comparator coming from Comparator.comparing() (new in Java 8), because the returned comparator is not serializable.

```language-java
// produces an exception:
// Task not serializable: java.io.NotSerializableException
.sortByKey(Comparator.comparing(String::toLowerCase))
```

We then have to use a comparator that implements the Serializable interface:

```language-java
class LowerCaseStringComparator implements Comparator, Serializable {
@Override
public int compare(String s1, String s2) {
return s1.toLowerCase().compareTo(s2.toLowerCase());
}
}
```

We then use the comparator this way:

`Comparator c = new LowerCaseStringComparator(); … .sortByKey(c)`

We obtain what we wanted:
…
ang.q -> 15
ang.s -> 9
AR -> 195
ar -> 108324
ar.b -> 293
…

### **Sorting results by value**

The JavaPairRDD class has a sortByKey() method, but there is no sortByValue() method. To sort by value, we then have to reverse our tuples so that values become keys.

Since a JavaPairRDD does not impose unique keys, we can have redundant values.

We reverse tuples with mapToPair():

```language-none
.mapToPair(t -> new Tuple2<Long , String>(t._2, t._1))
```

We can then sort the RDD by descending order (highest values first) and save the 10 first elements with the method take():

```language-none
.sortByKey(false).take(10)
```

Note that take() returns a Java collection (java.util.List), not an RDD. The forEach() method we use is then the one from the collections API, not the RDD’s forEach().

```language-none
.forEach(t -> System.out.println(t._2 + ” -> ” + t._1));
```

The sorting code is:

```language-java
.mapToPair(t -> new Tuple2<Long , String>(t._2, t._1))
.sortByKey(false) .take(10)
.forEach(t -> System.out.println(t._2 + ” -> ” + t._1));
```

We then obtain the top 10 of the most visited Wikipedia sites in that hour:

```language-none
meta.m -> 15393394
meta.mw -> 12390990
en -> 7209054
en.mw -> 4405366
es -> 1210216
de -> 692501
ja.mw -> 674700
es.mw -> 666607
ru -> 664970
ja -> 637371
```

### **Processing multiple files**

It is possible to process multiple files thanks to wildcard support in the textFile() method parameter.
We can tell Apache Spark to use 16 threads through setMaster(“local[16]”).

Thus, the top 10 calculation for November 1st (24 files) becomes:

```language-java
SparkConf conf = new SparkConf()
.setAppName(“wikipedia-mapreduce-by-key”)
.setMaster(“local[16]”);
JavaSparkContext sc = new JavaSparkContext(conf);
sc.textFile(“data/wikipedia-pagecounts/pagecounts-20141101-*”)
.map(line -> line.split(” “))
.mapToPair(s -> new Tuple2<String , Long>(s[0], Long.parseLong(s[2])))
.reduceByKey((x, y) -> x + y)
.mapToPair(t -> new Tuple2<Long , String>(t._2, t._1))
.sortByKey(false) .take(10)
.forEach(t -> System.out.println(t._2 + ” -> ” + t._1));
```

We get this result:

```language-none
meta.m -> 430508482
meta.mw -> 313310187
en -> 171157735
en.mw -> 103491223
ru -> 29955421
ja -> 22201812
ja.mw -> 22186094
es -> 21424067
de -> 21098513
fr -> 17967662
```

On a Macbook pro (SSD, Core i7 2GHz), this processing takes 40 seconds for 9 GB of data. Not bad!

**Conclusion**

We had a good overview of the Apache Spark API and what it offers. The manipulation of key-value tuples is one of the most frequently used parts of the framework.
In our following posts, we will deploy a Spark cluster and study the streaming API.

This code is available on [Github](https://github.com/aseigneurin/spark-sandbox).

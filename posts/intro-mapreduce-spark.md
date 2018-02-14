---
authors:
- Alexis Seigneurin
categories:
- 
date: 2014-11-22T13:57:20.000Z
title: "Intro to MapReduce operations with Spark"
image: 
---

In the previous post, we used the Map operation which allows us to transform values with a transformation function. We will now explore the Reduce operation which produces aggregates. Thus, we will work in MapReduce just like we do with Hadoop.


## Theory

With Spark, just like Hadoop, a Reduce operation is an operation that aggregates values two by two, by using as many steps as necessary to process all the elements of the collection. This is what allows the framework to perform aggregations in parallel, on multiple nodes when needed. The framework will choose two elements and pass them to a function we will define. The function must return the new element that will replace the two first. This means that the output type must be identical to the input type: values must be homogeneous, so that the operation can be repeated until all elements are processed. With Spark there are two reduction operations:

- **reduce()** works on elements, whatever their type, and returns a unique value.
- **reduceByKey()** works on values associated to the same key. This operation is only possible on RDDs of type JavaPairRDD (a list of key-value tuples), and it produces a result which is a JavaPairRDD too, but in which each key appears only once (equivalent to a key-value Map).

We are going to study the reduce() operation in this blog post. The reduceByKey() operation will be addressed in an upcoming article.


## Value aggregation in MapReduce

In our first code example, we built an RDD containing the heights of trees in Paris. We used the count() operation to count trees with a certain height.

```language-java
long count = sc.textFile(“arbresalignementparis2010.csv”)
.filter(line -> !line.startsWith(“geom”))
.map(line -> line.split(“;”))
.map(fields -> Float.parseFloat(fields[3]))
.filter(height -> height > 0) .count(); 
```

Instead of just counting non-null heights, we can use the reduce() operation to calculate the total height of trees. To do that, we need a function that will takes two heights as input and returns their sum. This function has two parameters of type Float and returns a Float:

This function can be written as a lambda expression:

```language-java
(x, y) -> x + y
```

Thus, we can write:

```language-java
float totalHeight = sc.textFile(“arbresalignementparis2010.csv”)
…
.reduce((x, y) -> x + y); 
```

The framework will call our reduce function until every value is processed.


## Counting in Map Reduce

The count() operation we previously saw can also be written as a reduction operation. Since the expected result is an amount of trees (int or Integer), we first have to convert input elements (Float type) to have consistent data. We will then proceed in two steps:

- a **map()** operation to convert each element to the value 1
- a **reduce()** operation to add up the 1s

```language-java
long count = sc.textFile(“data/arbresalignementparis2010.csv”)
…
.map(item -> 1)
.reduce((x, y) -> x + y);
```

Note that the lambda expression used in the reduce() method is identical to the one used we used previously, but the equivalent function manipulates Integer objects.

## Calculating the average tree height

We can calculate the average tree height in our file by doing:

- an aggregation operation (sum) of heights
- a count of elements
- a division of the first value by the second

Since the aggregation and the counting are based on the same file and the first operations are identical, we can reuse the same RDD. We will then use the cache() operation which allows us to cache the RDD in memory. The calculations will thus be executed only once and the intermediary result will be directly used for both operations of aggregation and counting.

```language-java
JavaRDD<float> rdd = sc.textFile(“data/arbresalignementparis2010.csv”)
.filter(line -> !line.startsWith(“geom”))
.map(line -> line.split(“;”))
.map(fields -> Float.parseFloat(fields[3]))
.filter(height -> height > 0)
.cache();
float totalHeight = rdd.reduce((x, y) -> x + y);
long count = rdd.count();
System.out.println(“Count: ” + count);
System.out.println(“Total height: ” + totalHeight);
System.out.println(“Average height ” + totalHeight / count);
```

We get the following result:

```language-none
Count: 32112
Total height: 359206.0
Average height 11.186036
```

## Conclusion

Spark performs aggregations on all values through a Reduce operation that processes elements two by two, in order to distribute the processing. In the next post, we will see how to perform aggregation operations by key. You can find the code example on [Github](https://github.com/aseigneurin/spark-sandbox).

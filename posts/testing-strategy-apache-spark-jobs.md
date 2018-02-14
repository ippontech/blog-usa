---
authors:
- Raphael Brugier
categories:
- Apache Spark
- Scala
- tdd
- test
date: 2016-03-11T09:28:21.000Z
title: "Testing strategy for Apache Spark jobs - Part 1 of 2"
image: 
---

Like any other application, Apache Spark jobs deserve good testing practices and coverage.

Indeed, the costs of running jobs with production data makes unit testing a must-do to have a fast feedback loop and discover the errors earlier.

But because of its distributed nature and the RDD abstraction on top of the data, Spark requires special care for testing.

In this post, we’ll explore how to design your code for testing, how to setup a simple unit-test for your job logic and how the [spark-testing-base](https://github.com/holdenk/spark-testing-base) library can help.


## Design for testing

We have already covered in a [previous post](http://www.ipponusa.com/blog/introduction-apache-spark/) what are Spark jobs, but from a higher point of view, any job can be described as an “immutable” transformation of distributed data.

In particular, any Spark job can be refactored to a composition of functions taking data as input, the so-called RDD, and returning data, hence a RDD again.

Extracting the logic of the job into functions will make it possible to reuse the functions across different jobs and to isolate the behavior to test it in a deterministic environment.

To separate the logic from the scheduling and configuration of the job, you will also want to isolate the logic to a separated object.

Let’s apply this pattern to the well-known word count example.

```language-scala
package com.ipponusa
import org.apache.spark.{SparkConf, SparkContext}

object Main {

  val sparkConf = new SparkConf()
    .setMaster("local[*]")
    .setAppName("spark-testing-example")
  val sc = new SparkContext(sparkConf)

  def main(args: Array[String]) {
    val countByWordRdd =  sc.textFile("src/main/resources/intro.txt")
      .flatMap(l => l.split("\\W+"))
      .map(word => (word, 1))
      .reduceByKey(_ + _)

    countByWordRdd.foreach(println)
  }
}
```

Main.scala

Extracting a method is a [classic refactoring pattern](http://refactoring.com/catalog/extractMethod.html). Therefore, this can be easily done with a few keystrokes within your favorite IDE:

1. Extract the input data in a separated variable to separate it from the logic.
2. Extract the logic in a count method (select + refactor -> extract -> method)
3. Move the method to a new object, WordCounter

```language-scala
package com.ipponusa
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Main {

  val sparkConf = new SparkConf()
    .setMaster("local[*]")
    .setAppName("spark-testing-example")
  val sc = new SparkContext(sparkConf)

  def main(args: Array[String]) {
    val input: RDD[String] = sc.textFile("src/main/resources/intro.txt")
    val countByWordRdd: RDD[(String, Int)] = WordCounter.count(input)

    countByWordRdd.foreach(println)
  }
}
```
Main.scala
```language-scala
package com.ipponusa
import org.apache.spark.rdd.RDD

object WordCounter {
  def count(lines: RDD[String]): RDD[(String, Int)] = {
    val wordsCount = lines.flatMap(l => l.split("\\W+"))
      .map(word => (word, 1))
      .reduceByKey(_ + _)
    wordsCount
  }
}
```
WordCounter.scala 


## Basic test

Now that we have extracted the logic, we can write a test assuming an input data and asserting the result of the function to an expected data. We will use [ScalaTest](http://www.scalatest.org/) as a testing framework.

The tricky part when writing tests for Spark is the RDD abstraction. Your first idea would probably be to mock the input and the expected. But then, you will not be able to execute the Spark behavior on the RDD passed to the function.

Instead, we have to start a `SparkContext` to build the input and expected RDDs and run the transformation in a real Spark environment. Indeed, creating a `SparkContext` for unit testing is the [recommended approach](http://spark.apache.org/docs/latest/programming-guide.html#unit-testing).

Because starting a `SparkContext` is time-consuming, you will save a lot of time starting the context only once before all the tests. Also, even if it possible with some tweaking, it is not recommended to have more than one `SparkContext` living in the JVM. So make sure you stop the context after running all the tests and to disable the parallel execution.

Starting and stopping the `SparkContext` can easily be done with the `BeforeAndAfter` trait.

```language-scala
package com.ipponusa
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class WordCounterTest extends FlatSpec with Matchers with BeforeAndAfter {

  var sc:SparkContext = _

  before {
    val sparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName("test-wordcount")
    sc = new SparkContext(sparkConf)
  }

  after {
    sc.stop()
  }

  behavior of "Words counter"

  it should "count words in a text" in {
    val text =
      """Hello Spark
        |Hello world
      """.stripMargin
    val lines: RDD[String] = sc.parallelize(List(text))
    val wordCounts: RDD[(String, Int)] = WordCounter.count(lines)

    wordCounts.collect() should contain allOf (("Hello", 2), ("Spark", 1), ("world", 1))
  }
}
```
WordCounterTest.scala 


## Spark-testing-base library

Setting up the `before` and `after` methods for all your test cases can become tedious if you have many tests. An alternative could be to hold the Context in a Singleton Object and start it once for all the tests, or to inherits a common trait to implement this behavior.

Also, the previous example works fine when working with a local cluster where all the data can fit in memory. But if you are testing with a lot of data, a large sample of your production data for example, calling the `collect()` method to gather all the data locally to compare with an expected output is no longer an option.

Fortunately, the [spark-testing-base library](https://github.com/holdenk/spark-testing-base) provides traits and methods to prepare your tests and run distributed comparisons.

Let’s import the library and rewrite the test:

Pom.xml extract:
```language-xml
<dependencies>
   <dependency>
      <groupId>org.apache.spark</groupId>
      <artifactId>spark-core_${scala.dep.version}</artifactId>
      <version>${spark.version}</version>
   </dependency>
   <!--spark-testing has a dependency to spark-sql, spark-hive, spark-mllib -->
   <dependency>
      <groupId>org.apache.spark</groupId>
      <artifactId>spark-sql_${scala.dep.version}</artifactId>
      <version>${spark.version}</version>
   </dependency>
   <dependency>
      <groupId>org.apache.spark</groupId>
      <artifactId>spark-hive_${scala.dep.version}</artifactId>
      <version>${spark.version}</version>
   </dependency>
   <dependency>
      <groupId>org.apache.spark</groupId>
      <artifactId>spark-mllib_${scala.dep.version}</artifactId>
      <version>${spark.version}</version>
   </dependency>
   <dependency>
      <groupId>org.scalatest</groupId>
      <artifactId>scalatest_${scala.dep.version}</artifactId>
      <version>2.2.6</version>
      <scope>test</scope>
   </dependency>
   <dependency>
      <groupId>com.holdenkarau</groupId>
      <artifactId>spark-testing-base_${scala.dep.version}</artifactId>
      <version>${spark.version}_0.3.2-SNAPSHOT</version>
      <scope>test</scope>
   </dependency>
</dependencies>
```

```language-scala
package com.ipponusa
import com.holdenkarau.spark.testing.{RDDComparisons, RDDGenerator, SharedSparkContext}
import org.apache.spark.rdd.RDD
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.scalatest.prop.Checkers
import org.scalatest.{FlatSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class WordCounterWithSparkTestingTest extends FlatSpec with Matchers with SharedSparkContext{

  behavior of "Words counter"

  it should "count words in a text" in {
    val text =
      """Hello Spark
        |Hello world
      """.stripMargin

    val inputRdd: RDD[String] = sc.parallelize(List(text))
    val expectedRdd: RDD[(String, Int)] = sc.parallelize(List(("Hello", 2), ("Spark", 1), ("world", 1)))

    val resRdd: RDD[(String, Int)] = WordCounter.count(inputRdd)
    assert(None === RDDComparisons.compare(resRdd, expectedRdd))
  }
}
```
WordCounterWithSparkTestingTest.scala

The test class now extends the `SharedSparkContext` trait instead of `BeforeAndAfter`. This trait will automatically take care of starting and stopping a `SparkContext` for you.

The method RDDComparisons.compare(…) is more interesting.

Instead of locally collecting the data to be compared, the comparison will be run as RDD operations on Spark nodes. Even if this may involve a lot of shuffling operations, the data is still distributed and thus can fit in memory.

Of course, in the same manner, the input and expected data would not be loaded locally but most probably from external distributed storage.

Like HDFS for example:
```language-scala
> val inputRdd = sc.textFile("hdfs://127.0.0.1:9000/data/test/bigInput.txt") val expectedRdd = sc.textFile("hdfs://127.0.0.1:9000/data/test/bigExpected.txt")
```

The spark-testing-base library also provides methods for property-based testing via an integration of the [ScalaCheck](https://www.scalacheck.org/) library.

```language-scala
class WordCounterWithSparkTestingTest extends FlatSpec with Matchers with SharedSparkContext with Checkers {

  behavior of "Words counter"
  
  it should "have stable count, with generated RDDs" in {
     val stableProperty =
       forAll(RDDGenerator.genRDD[String](sc)(Arbitrary.arbitrary[String])) {
         rdd => None === RDDComparisons.compare(WordCounter.count(rdd), WordCounter.count(rdd))
       }
     check(stableProperty)
   }
}
```
WordCounterWithSparkTestingTest.scala

Here, `RddGenerator.genRDD[String]` will generate RDDs on top of random Strings.

We declare the property to have the same count result when executing twice the method.

We then test the property with the ScalaCheck method.

While not very relevant for the wordcount example, it allows to test your job logic against randomly generated data as input and therefore test the reliability of your code.


## Conclusion

In this article, we have seen how it is possible to refactor and test a Spark job. Testing your jobs will allow faster feedback when implementing them and you can even practice TDD.

The next step would be to run the tests not only on a local cluster, but on a “production-like” cluster with more data on your continuous integration server. Simply override the `setMaster()` value when configuring the `SparkContext` to redirect to your test cluster.

Finally, I definitely recommend you watch Holden Karau’s session on testing Spark recorded at the last Spark Summit ([video](https://www.youtube.com/watch?v=rOQEiTXNS0g), [slides](http://www.slideshare.net/SparkSummit/beyond-parallelize-and-collect-by-holden-karau)).  
 You can find the code for these examples on [Github](https://github.com/raphaelbrugier/spark-testing-example).

The second part of this series can be found [here](http://blog.ippon.tech/testing-strategy-for-spark-streaming/)

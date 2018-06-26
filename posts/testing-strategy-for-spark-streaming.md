---
authors:
- Raphael Brugier
tags:
- Apache Spark
- streaming
- test
- Big Data
date: 2016-03-30T11:53:21.000Z
title: "Testing strategy for Spark Streaming - Part 2 of 2"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/01/spark-logo.png
---

In [a previous post](http://blog.ippon.tech/testing-strategy-apache-spark-jobs/), we’ve seen why it’s important to test your Spark jobs and how you could easily unit test the job’s logic, first by designing your code to be testable and then by writing unit tests.

In this post, we will look at applying the same pattern to another important part of the Spark engine: Spark Streaming.

## Spark Streaming example

Spark Streaming is an extension of Spark to implement streaming processing on top of the batch engine. The base idea is to accumulate a flow of events in micro-batches and then process them separately. As for a signal, the stream is *discretized *and thus named *DStream* in the Spark API.

For this example, we will simply generate a stream of characters in input, and have each character capitalized in the output stream. To spice up the thing, the output will be a sliding window.

We will configure the batch duration to 1 second, the window duration to 3 seconds and the slide duration to 2 seconds.

This is better explained with the following diagram:

[![sparkCapitalizedWindowedStream](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/03/sparkCapitalizedWindowedStream.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/03/sparkCapitalizedWindowedStream.png)

And the code implementing this process:

```language-scala
package com.ipponusa

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, StreamingContext}
import scala.collection.mutable

object MainStreaming {

  val sparkConf = new SparkConf()
    .setMaster("local[*]")
    .setAppName("spark-streaming-testing-example")

  val ssc = new StreamingContext(sparkConf, Seconds(1))

  def main(args: Array[String]) {

    val rddQueue = new mutable.Queue[RDD[Char]]()

    ssc.queueStream(rddQueue)
      .map(_.toUpper)
      .window(windowDuration = Seconds(3), slideDuration = Seconds(2))
      .print()

    ssc.start()

    for (c <- 'a' to 'z') {
      rddQueue += ssc.sparkContext.parallelize(List(c))
    }

    ssc.awaitTermination()
  }
}
```

MainStreaming.scala

Here, we simulate a stream of characters by continuously adding them as RDDs of one character in a mutable Queue.

We create a QueueStream to wrap the Queue as an InputDStream. Because the QueueInputDStream is mainly designed for testing, each RDD is consumed one by one by default. We have configured the StreamingContext to have batches of 1 second, hence each character will be consumed by the stream logic every 1 second.

When you run this code, the Spark engine will print the last 3 capitalized letters every 2 seconds.

## Testing stream operations

As in the previous article, the pattern to make the code testable consists in extracting the logic in a separate function that takes a DStream in parameter and that returns the resulting DStream.

```language-scala
package com.ipponusa
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.dstream.DStream

object StreamOperations {

  def capitalizeWindowed(input: DStream[Char]): DStream[Char] = {
    input.map(_.toUpper)
          .window(windowDuration = Seconds(3), slideDuration = Seconds(2))
  }
}
```

StreamOperations.scala

The main problem with testing Streams is their time-based nature. To compare the output stream to an expected other set of data, you will have to do the assertion at the instant where the engine consumes the input. Adding `Thread.Sleep(...)` would be an inaccurate solution and will have the major drawback to dramatically slow down your tests.

The appropriate solution to control the time is to use the “Virtual Clock” pattern, where you replace the system’s clock with your own implementation. During the execution of the test, you can then change the value returned by the clock and therefore control the timing.

Internally, Spark uses this pattern with a [Clock](https://github.com/apache/spark/blob/master/core/src/main/scala/org/apache/spark/util/Clock.scala#L23) interface and a default implementation returning the system time. It allows to replace this implementation by your own implementation by setting the `spark.streaming.clock”` param when configuring the `SparkContext`.

This is how Spark internal unit tests work, by replacing the [SystemClock](https://github.com/apache/spark/blob/master/core/src/main/scala/org/apache/spark/util/Clock.scala#L31) implementation with [ManualClock](https://github.com/apache/spark/blob/master/core/src/main/scala/org/apache/spark/util/ManualClock.scala#L27).

Unfortunately, the Clock interface has a package protected visibility limited to the `org.apache.spark package`. But we can workaround this by placing our own implementation extending the interface in this same package.

```language-scala
package org.apache.spark
import java.util.Date
import org.apache.spark.streaming.Duration

class FixedClock(var currentTime: Long) extends org.apache.spark.util.Clock {

  def this() = this(0L)

  def setCurrentTime(time: Date): Unit = synchronized {
    currentTime = time.getTime
    notifyAll()
  }

  def addTime(duration: Duration): Unit = synchronized {
    currentTime += duration.toMillis
    notifyAll()
  }

  override def getTimeMillis(): Long = synchronized {
    currentTime
  }

  override def waitTillTime(targetTime: Long): Long = synchronized {
    while (currentTime < targetTime) {
      wait(10)
    }
    getTimeMillis()
  }
}
```
FixedClock.scala

And then add an utility method to workaround the private accessibility of the `Clock` in the `StreamingContext`:

```language-scala
package org.apache.spark.streaming
import org.apache.spark.FixedClock

object Clock {
  def getFixedClock(ssc: StreamingContext): FixedClock = {
    ssc.scheduler.clock.asInstanceOf[FixedClock]
  }
}
```
Clock.scala

The test can now be written:

```language-scala
package com.ipponusa
import java.util.concurrent.TimeUnit
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Clock, Seconds, StreamingContext}
import org.apache.spark.{FixedClock, SparkConf, SparkContext}
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Millis, Span}
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.Duration

class StreamingTest extends FlatSpec with Matchers with BeforeAndAfter with Eventually {

  var sc:SparkContext = _
  var ssc: StreamingContext = _
  var fixedClock: FixedClock = _

  override implicit val patienceConfig = PatienceConfig(timeout = scaled(Span(1500, Millis)))

  before {
    val sparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName("test-streaming")
      .set("spark.streaming.clock", "org.apache.spark.FixedClock")

    ssc = new StreamingContext(sparkConf, Seconds(1))
    sc = ssc.sparkContext
    fixedClock = Clock.getFixedClock(ssc)
  }

  after {
    ssc.stop(stopSparkContext = true, stopGracefully = false)
  }

  behavior of "stream transformation"

  it should "apply transformation" in {
    val inputData: mutable.Queue[RDD[Char]] = mutable.Queue()
    var outputCollector = ListBuffer.empty[Array[Char]]

    val inputStream = ssc.queueStream(inputData)
    val outputStream = StreamOperations.capitalizeWindowed(inputStream)

    outputStream.foreachRDD(rdd=> {outputCollector += rdd.collect()})

    ssc.start()

    inputData += sc.makeRDD(List('a'))
    wait1sec() // T = 1s

    inputData += sc.makeRDD(List('b'))
    wait1sec() // T = 2s

    assertOutput(outputCollector, List('A','B'))

    inputData += sc.makeRDD(List('c'))
    wait1sec() // T = 3s

    inputData += sc.makeRDD(List('d'))
    wait1sec() // T = 4s
    assertOutput(outputCollector, List('B', 'C', 'D'))

    // wait until next slide
    wait1sec() // T = 5s
    wait1sec() // T = 6s
    assertOutput(outputCollector, List('D'))
  }

  def assertOutput(result: Iterable[Array[Char]], expected: List[Char]) =
    eventually {
      result.last.toSet should equal(expected.toSet)
    }

  def wait1sec(): Unit = {
    fixedClock.addTime(Seconds(1))
  }
}
```

StreamingTest.scala

Here again, we start a StreamingContext before running the test and we’ll stop it after. We also replace the clock with our own implementation so that we can control the time. Like in the `Main` example, we use a `QueueInputDStream` to simulate the input of data. We also use a `ListBuffer` to stack the resulting value of the `OuputDStream` collected with the *forEachRDD(…)* method.

One extra tricky thing…

Because the processing of the DStream occurs in a separated thread, when we change the time to the exact instant of the sliding window trigger, it still requires a few extra ms to do the actual computation. Thus, as suggested in [this post](http://mkuthan.github.io/blog/2015/03/01/spark-unit-testing/) about testing Spark, we protect the assertion with an *eventually* block from ScalaTest’s Eventually trait. The *patienceConfig* set the timeout to retry the assertion multiple times before a time-out.

We now have a fast and predictable test to ensure the streaming operations’ correctness!

## Spark-testing-base library

The [Spark-testing-base](https://github.com/holdenk/spark-testing-base) library have some built-in trait and methods to help you test the streaming logic.

When using the library, the `FixedClock` workaround is also already implemented.

Let’s rewrite the window test with the StreamingSuiteBase trait:

```language-scala
package com.ipponusa
import com.holdenkarau.spark.testing.StreamingSuiteBase

class StreamingWithSparkTestingTest extends StreamingSuiteBase {

  test("capitalize by window") {
    val input = List(List('a'), List('b'), List('c'), List('d'), List('e'))

    val slide1 = List('A', 'B')
    val slide2 = List('B', 'C', 'D')
    val expected = List(slide1, slide2)

    testOperation(input, StreamOperations.capitalizeWindowed, expected)
  }
}
```
StreamingWithSparkTestingTest.scala

The `StreamingSuiteBase` trait will take care of all the setup we had manually prepared in the previous test: starting and stopping the `StreamingContext`, replacing the `Clock` in the engine, and incrementing the time when each element is consumed.

Unlike the `RDDComparisons.compare(...)` method shown in the previous article, where the comparison was distributed, the elements are collected locally from the OutputDStream. Be sure not to produce too much data in your tests or you can rapidly run out of memory.

## Conclusion

While requiring more setup and care than testing batch jobs, it is also possible to test Spark Streaming jobs. Controlling the time is the key to having a predictable output to compare results to expected ones.

The Spark-testing-base library can be very helpful, especially to avoid the workarounds.

You can find the code of these examples on [Github](https://github.com/raphaelbrugier/spark-testing-example).

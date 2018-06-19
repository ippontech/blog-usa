---
authors:
- Alexis Seigneurin
categories:
- Big Data
date: 2016-09-12T08:54:54.000Z
title: "Spark - Calling Scala code from PySpark"
id: 5a267e57dd54250018d6b616
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/01/spark-logo-1.png
---

In [a previous post](https://blog.ippon.tech/spark-kafka-achieving-zero-data-loss/), I demonstrated how to consume a Kafka topic using Spark in a resilient manner. The resiliency code was written in Scala. Now, I want to leverage that Scala code to connect Spark to Kafka in a PySpark application. We will see how we can call Scala code from Python code and what are the restrictions.

## Basic method call through Py4J

PySpark relies on [Py4J](https://www.py4j.org/) to execute Python code that can call objects that reside in the JVM. To do that, Py4J uses a *gateway* between the JVM and the Python interpreter, and PySpark sets it up for you.

Let’s see how we can make a basic method call. We first create a minimal Scala object with a single method:

```language-scala
package com.ippontech

object Hello {
  def hello = println("hello")
}
```

We need to package this class in a JAR. I’m reusing the [spark-kafka-source project](https://github.com/ippontech/spark-kafka-source) from the previous post but any Maven/SBT/… project should work.

```language-bash
$ mvn package
...
Building jar: .../target/spark-kafka-source-0.2.0-SNAPSHOT.jar
```

We can now launch the PySpark console and add the JAR to the classpath:

```language-bash
$ pyspark --driver-class-path .../target/spark-kafka-source-0.2.0-SNAPSHOT.jar
>>>
```

From there, JVM objects are accessible through the `_jvm` field of the SparkContext object (`sc._jvm`):

```language-scala
>>> sc._jvm.com.ippontech.Hello.hello()
hello
```

Good, we can call Scala code from Python. That’s a good start.

Notice that we can also assign an instance of a JVM object to a Python variable, then make the method call on that variable:

```language-scala
>>> h = sc._jvm.com.ippontech.Hello
>>> h.hello()
hello
```

## Real-life method call

Here is the method we want to call:

```language-scala
object KafkaSource extends LazyLogging {

  def kafkaStream[K: ClassTag, V: ClassTag, KD <: Decoder[K] : ClassTag, VD <: Decoder[V] : ClassTag]
    (ssc: StreamingContext, brokers: String, offsetsStore: OffsetsStore, topic: String): InputDStream[(K, V)]
{
    ...
```

There are a few challenges here:

- This method has generic parameters. At that point, I’m not sure we can pass them from Python. We will need to create a helper method that doesn’t have generic arguments.
- The `StreamingContext` parameter and the `InputDStream` return value are objects from the Spark framework. Notice that **PySpark works with Python wrappers around the Java version of Spark objects, not around the Scala version of Spark objects**. We will have to wrap/unwrap objects accordingly.
- The `brokers` and `topic` parameters are strings. Python strings and Java strings are inter-changeable.
- The `OffsetsStore` parameter is an object from our code base. We will have to create it on the Python side and simply pass the reference as a parameter.

Let’s create a helper class and helper method to lift the constraints described above:

```language-scala
object KafkaSourcePythonHelper {

  def kafkaStream(jssc: JavaStreamingContext, brokers: String, offsetsStore: OffsetsStore,
                  topic: String): JavaDStream[(String, String)] = {
    val dstream = KafkaSource.kafkaStream[String, String, StringDecoder, StringDecoder](jssc.ssc, brokers, offsetsStore, topic)
    val jdstream = new JavaDStream(dstream)
    jdstream
  }
}
```

Here are the things to notice:

- We have removed the generic parameters from the helper method’s signature and hard-coded them when calling the actual method.
- We expect a `JavaStreamingContext` instance and unwrap it by calling `jssc.ssc` to get the `StreamingContext` instance.
- We wrap the `DStream` into a `JavaDStream` that will be returned to the Python caller.

We can now compile this code. Notice that, since our code uses dependent libraries, we need these dependencies to be included in the JAR. I’m using a profile and the [Maven Shade plugin](https://maven.apache.org/plugins/maven-shade-plugin/) to do so:

```language-bash
$ mvn package -Puber-jar
```

We can now restart PySpark and test our code. We start by creating a `StreamingContext`:

```language-scala
>>> from pyspark.streaming import StreamingContext
>>> ssc = StreamingContext(sparkContext=sc, batchDuration=int(1))
```

Before calling the `kafkaStream` method, we need to create an instance of an `OffsetStore`:

```language-scala
>>> zkStore = sc._jvm.com.ippontech.kafka.stores.ZooKeeperOffsetsStore("localhost:2181", "/my_topic/offsets")
```

Notice that the `zkStore` instance is just a pointer to an object that resides in the JVM:

```language-scala
>>> zkStore
JavaObject id=o21
```

Now is time to call the `kafkaStream` method:

```language-scala
>>> jstream = sc._jvm.com.ippontech.kafka.KafkaSourcePythonHelper.kafkaStream(ssc._jssc, "localhost:9092", zkStore, "my_topic")
```

Notice we unwrapped the Python `StreamingContext` to get the `JavaStreamingContext` by calling `ssc._jssc`.

The last thing we need to do is to wrap the `JavaDStream` into a Python `DStream`. We have to provide a deserializer for the messages:

```language-scala
>>> from pyspark.serializers import PairDeserializer, NoOpSerializer
>>> from pyspark.streaming import DStream
>>> ser = PairDeserializer(NoOpSerializer(), NoOpSerializer())
>>> stream = DStream(jstream, ssc, ser)
```

We can now define our transformations and actions on the DStream and start the application:

```language-scala
>>> stream.pprint()
>>> ssc.start()
>>> -------------------------------------------
Time: 2016-09-01 15:21:38
-------------------------------------------

-------------------------------------------
Time: 2016-09-01 15:21:39
-------------------------------------------
...
```

Now, if you send some data into Kafka, you should see it in PySpark:

```language-bash
$ echo "foo bar" | kafka-console-producer.sh --broker-list localhost:9092 --topic my_topic
```

```language-bash
-------------------------------------------
Time: 2016-09-01 15:26:18
-------------------------------------------
(None, 'foo bar')
```

## Conclusion

We have seen it is fairly easy to call Scala code from Python. In a context where Data Scientists write Python code but Software Engineers prefer to write Java/Scala code, that’s a good thing because we can share the responsibilities within the team. That makes me happy because I’m not a big fan of shipping code written by Data Scientists to production, and that also means we will have less code duplication between languages.

In the context of Spark, the key thing to remember is to appropriately wrap/unwrap your objects. Spark has 3 versions of each class (Scala, Java, Python) and the Python classes are wrappers over the Java classes. This means:

- When passing a Spark object as a parameter to as Scala method: - On the Python side, unwrap the Python object to get the Java object (e.g. unwrap a Python `StreamingContext` into a Java`JavaStreamingContext`).
- On the Scala side, unwrap the Java object to get the Scala object (e.g. unwrap a Java `JavaStreamingContext` into a Scala`StreamingContext`).
- When returning a Spark object from the Scala method: - On the Scala side, wrap the object into the corresponding Java object (e.g. wrap a Scala `DStream` into a Java `JavaDStream`).
- On the Python side, wrap the Java object into the correspondong Python object (e.g. wrap a Java `JavaDStream` into a Python`DStream`).

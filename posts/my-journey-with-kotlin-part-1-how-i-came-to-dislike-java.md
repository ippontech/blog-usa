---
authors:
- Alexis Seigneurin
categories:
- Kotlin
- Language
date: 2018-02-13T01:42:15.000Z
title: "My journey with Kotlin - Part 1: How I came to dislike Java"
id: 5a78841b369b1f00224be143
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/02/kotlin-2.svg
---

This post is a 2-part introduction to Kotlin, a programming language for the JVM. If you are a Java developer, you should read this and, like I did a few months ago, consider the option of learning Kotlin.

**Part 1 (this post) explains the problems of Java, as a language. If you want to skip this, you can read [part 2](/my-journey-with-kotlin-part-2-introduction-to-kotlin/) to immediately start reading what Kotlin looks like.**

# How I came to dislike Java

I've been writing code for about 20 years, exploring many languages, with a huge preference for statically typed languages. In the past 3 years, Scala has been my primary programming language and, although I enjoy its power, I have to admit that not all the teams are ready to use such a complex language.

It happens that, in October, I started working on a project which codebase was written in Java. I very quickly got tired of Java's verbosity, and I tweeted this:

<blockquote class="twitter-tweet" data-partner="tweetdeck"><p lang="en" dir="ltr">For Christmas, on behalf of Java developers, I would like:<br>- pojos without stupid getters/setters<br>- string interpolation<br>- inferred types (val/var)<br>- map() directly on collections (no stupid call to stream())<br>- and so much more</p>&mdash; Alexis Seigneurin 🚲 ⛵️ 🗽 (@ASeigneurin) <a href="https://twitter.com/ASeigneurin/status/931568662292652033?ref_src=twsrc%5Etfw">November 17, 2017</a></blockquote>
<script async src="https://platform.twitter.com/widgets.js" charset="utf-8"></script>

There was a bit of troll in the responses I received, as you can imagine. Some people said I was describing Scala, which is kind of true, but I did not want to use Scala as my team would not have been able to make this step. Also, if we had been slowly migrating from Java to Scala, the not-so-great interoperability between the two languages would have been a problem.

Some other people suggested Kotlin, and I thought it was time to explore this language...

# What hurts me in Java

Before we dive into Kotlin, let's have a look at what irritates me when I code in Java.

## POJOs

POJOs - or [JavaBeans, as per Wikipedia's definition](https://en.wikipedia.org/wiki/Plain_old_Java_object#JavaBeans) - are these classes that are only meant to hold _data_. We use them a lot: for domain objects, for Hibernate entities, for DTOs (Data Transfer Objects), etc.

The convention is to use private fields and public getters/setters, so that you could - at least in theory - intercept the changes of values. In practice, it is really rare to have custom getters/setters, and you end up with boilerplate code. Yes, your IDE will generate these methods for you, but still.

The naming of the getters/setters is also a problem to me. They need to follow a convention, but they are sometimes not respected. If you use Kafka, you may have noticed that this convention is not followed ([here is an example](https://kafka.apache.org/0110/javadoc/org/apache/kafka/common/TopicPartitionInfo.html)).

Also, getting and setting values are made through method calls. This means your code looks complex, when you could just use `=` to set a value. Because of this, step-by-step debugging is also more complex than necessary (although your IDE might come to the rescue).

POJOs are also not immutable by default. Immutability is not a very common concept for Java programmers, and it's a shame because it can save you from lots of potential bugs.

Finally, you have to manually declare your `equals()` and `hashCode()` methods. Spring or Apache Commons Lang can help you with that, but you still have to write code, when the language could have sensible defaults for you.

So, when I started working on this project, POJOs represented 590 lines of code out of a total of 2059 lines (that was a very small project at the time). That's about 30% of the codebase with not much value.

## No type inference (or so few)

Java doesn't have type inference. Well, it does, but I am really not impressed by [the diamond operator](http://www.onlinetutorialspoint.com/java/type-inference-java-7.html).

No type inference means duplicated code, as shows this simple piece of code:

```java
NewTopic newTopic = new NewTopic(topicName,
        topicParameters.getNumPartitions(),
        topicParameters.getReplicationFactor());
```

With type inference, we could use a `val` keyword to declare the `newTopic` variable, and we wouldn't have to repeat the type (`NewTopic`).

(Also, note the 2 method calls for the getters, and the useless semicolon.)

## The Streams API is verbose

Java 8 introduced the [Streams API](https://www.tutorialspoint.com/java8/java8_streams.htm). It was about time because other languages - such as C# - had provided APIs to process your collections for a very long time.

There are reasons why this API was defined this way, but having to call `stream()` and to use collectors is painful when you just want to apply a `filter()` or a `map()` to your collection. On the example below, anything outside the filter is boilerplate code:

```java
List<AclBinding> userAclBindings = aclBindings.stream()
        .filter(aclBinding -> aclBinding.entry().principal().equals(filterUser))
        .collect(Collectors.toList());
```

## No operator overloading

In the code example above, did you notice we had to use `equals()` to compare objects? This is one of the very first things you get to learn when you start programming in Java: use `equals()` (object equality) not `==` (reference equality).

Seriously, 90% of your checks will use `equals()`, not `==`... How about using `==` for object equality and `===` for reference equality?

## No string interpolation

When you want to create Strings by concatenating variables and other pieces of Strings, you have to use `+` (and lots of `"`), or use complex objects to build the string, e.g.:

```java
MessageFormat.format("{0}://{1}{2}:{3}", securityProtocol, instanceName, domain, port).toString()
```

```java
new RuntimeException("Tag [" + tagKey + "] not found")
```

Also, multi-line strings are not supported at all.

## One class per file

Java forces you to define each class in a separate file (apart from [nested classes](https://docs.oracle.com/javase/tutorial/java/javaOO/nested.html), but these do not fit every use case).

This makes it easy to find classes, but this is not convenient if you have a few small classes that are tightly related.

For instance, we have a `ClusterDto.java` file that has a reference to the `NodeDto` class:

```java
public class ClusterDto {
    private String clusterId;
    private NodeDto controller;
    private List<NodeDto> nodes;
    ...
```

The `NodeDto` class needs to be declared in its own `NodeDto.java` file:

```java
public class NodeDto {
    private int id;
    private String host;
    private int port;
    private String rack;
    ...
```

## Declaring a “function”

Java introduced [lambda expressions](https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html) to replace anonymous classes. While this is a great addition when defining a function, the syntax to declare the signature of a function is very verbose: you have to declare a "functional interface", i.e. an interface with a single abstract method (the `@FunctionalInterface` annotation is optional).

```java
@FunctionalInterface
public interface KafkaAdminFunction<T> {
    T apply(AdminClient client) throws InterruptedException, ExecutionException;
}
```

We could certainly do better than that.

## Nulls

Java has null references when a reference doesn't have a value. This is better than running into segmentation faults, but this still leads to `NullPointerException`s, and Tony Hoare, who invented null references, now regrets his [billion dollar mistake](https://www.infoq.com/presentations/Null-References-The-Billion-Dollar-Mistake-Tony-Hoare).

The language itself should have a way to tell whether a variable or a parameter could be null, and the [Option type introduced in Java 8](http://www.oracle.com/technetwork/articles/java/java8-optional-2175753.html) is certainly painful to use.

# What are our options?

We see that Java has many flaws, many of which have been solved in other - more recent - languages. So, what are our options to fix all of these problems?

Could it be Java 9? Well, the most important feature of the new version of Java is its modularity (a.k.a. _project Jigsaw_). There's also a new GC and support for HTTP 2. On the side of the syntax, the changes are extremely specific and don't solve any of the problems described above (try-with-resource with a pre-defined variable, diamond operator on anonymous inner classes, interface with private methods).

Maybe Java 10? This upcoming version will introduce value types and reified generics but, again, nothing to solve the painpoints listed above.

What about Scala? Scala is certainly extremely powerful and provides answers to the verbosity of Java, but it can be overwhelming, especially in a team like mine, where some Ops also contribute to the codebase. Also, the Scala-Java interop isn’t the best (this is getting better with Scala 2.12) and it would require a stop-the-world project migration.

Should we chose a non-JVM language instead? This would require a language change as well as an ecosystem change, which is too disruptive.

So how about another JVM language? Clojure goes too far in the functional world. JRuby, JPython, and other JVM adaptations of non-JVM languages, are too clunky. Groovy is a dynamic language, which is too risky outside of a very small codebase. **Kotlin? YES!**

**Let's see [in part 2](/my-journey-with-kotlin-part-2-introduction-to-kotlin/) what Kotlin looks like.**

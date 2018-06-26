---
authors:
- Laurent Mathieu
tags:
- API
- Java
- MapReduce
date: 2014-04-24T13:30:56.000Z
title: "Java 8's Stream API: a new way to manage collections"
image: 
---

By Luc Clément – [@luclement](http://www.twitter.com/luclement)

Until now, processing collections or tables in Java in a MapReduce manner was done mostly with the Iterator pattern. Java 8 introduces the Stream API to simplify this type of processing with a new Object: Stream

A stream is built from a data source (e.g. a collection, a table or I/O sources), and it has a few specific properties:

- A stream does not store data, unlike a collection. It only transfers from a source to a series of operations.
- A stream does not modify the data of the source from which it was built. If it needs to modify the data for another use, it will build another stream from the initial stream. This property is critical to maintain coherence when parallelizing operations.
- In order to improve performance, operations data is loaded lazily. For instance, if we look for a certain pattern in a stream of strings, we will load only the necessary elements for the search, and the rest will not be loaded.
- A stream is not constrained to be finite, unlike collections. It is however important to make sure operations time out, for instance by using methods such as limit(n) or findFirst().
- Finally, a stream is not reusable. Once it has been processed, if we want to reuse the data from the source, we will have to build a new stream from the same source.

Two types of operation can be performed on a stream: intermediate and terminal.

Intermediate operations (e.g. Stream.map or Stream.filter) are done lazily and return a new stream, which creates multiple streams called stream pipelines. As long as no terminal operation is called on a stream pipelines, intermediate operations are not executed.

When a terminal operation is called (e.g. Stream.reduce or Stream.collect), all the streams created by intermediate operations are traversed, operations are applied to data, and the terminal operation is performed. Then every stream is consumed and destroyed, and cannot be used any longer.

==Example of Collection processing==
```language-java
List<String> strings = Arrays.asList("giraffe", "cat", "camel", "fish", "whale");
strings.stream()
       // filtrage
       .filter(x -> x.contains("ca"))
       // mapping : reformating of character strings
       .map(x -> x.substring(0, 1).toUpperCase() + x.substring(1))
       // alphabetical sort
       .sorted()
       // Outputs:
       // camel
       // cat
       .forEach( System.out::println );
```

# Stream creation

There are different ways to create a stream. The simplest way is to call the method stream() or parallelStream() on a collection. Another way is to call methods that were added to existing classes.

For instance, String’s method chars() returns an IntStream with the characters in the String, and BufferedReader’s lines() method creates a stream of characters from the lines of the open file. Interestingly, Random’s method ints() returns a stream of pseudo-random numbers.</>

The API also includes static methods in the Stream class. For instance, the following code “Stream.iterate(1, x -> x*2)” returns an infinite stream of powers of 2. The first argument contains the initial value of the stream, and the second the function to go from the element n to the element n+1.

# Parallelization

One of the main advantages of the new API is the ability to easily parallelize processing. Indeed, any stream can be parallelized by calling its parallel() method inherited from the BaseStream interface. Any stream can become sequential with an invocation of the sequential() method. It is also possible to build a parallel stream directly on a collection by calling the collection’s parallelStream()’s method.

These methods conveniently abstract the work distribution, but they should not be used lightly: in some cases, parallelization can decrease performance (as shown further down with stateful operations).

# Intermediate operations

Intermediate operations can be stateful or stateless. Stateless operations are applied to the elements of a stream independently – without taking into account other elements.

==Collects allows us to easily store the result in a list==
```language-java
List<Order> myOrders = … ;
List myClients = myOrders.stream()
                                 .map( c -> c.getClient() )
                                 .collect( Collectors.toList() );
```

Stateful operations generally require knowledge of the full stream to get a result (e.g. Stream.distinct or Stream.sorted). Therefore, parallelizing a stateful process may decrease performance.

==We deduplicate the list of clients with the intermediate operation distinct()==
```language-java
List<Order> myOrders = … ;
List myClients = myOrders.stream()
                                 .map( c -> c.getClient() )
                                 .distinct()
                                 .collect( Collectors.toList() );
```

# Terminal operations

There are two types of reduction in the Stream API: simple reductions and mutable reductions.

Simple reductions are, for instance: Stream.sum (sum of elements), Stream.max (maximum) or Stream.count (count of elements). It is defined this way:

```language-java
U reduce(U identity,
BiFunction<U, ? super T, U> accumulator,
BinaryOperator<U> combiner);
```

The identity element is the initial element for the reduction (and the returned element if the stream is empty). The accumulator creates a new partial result from the partial result and a new element, and the combiner creates a new partial result from the two partial results. Note two things:

- First, the identity needs to be an identity in the mathematical sense for the combiner function: combiner.apply (u, identity) must be equal to u for every u.
- The combiner function must be associative. It is necessary to avoid getting random results when parallelizing the work.Thus, the sum() method can be rewritten using the reduce() method:

==We can use the reduce() method to rewrite the sum() method==
```language-java
List<Order> myOrders = …;
int revenue = myOrders.stream()
                      .reduce( 0,
                               (result, order) -> result + order.getPrice(),
                               (resultA, resultB) -> resultA + resultB );
```

Mutable reductions generalize this concept by accumulating the elements of a stream in a container. This container can be a Collection, a StringBuilder, or even a simple integer (in which case we would have a simple reduction).

```language-java
<R> R collect(Supplier<R> supplier,
BiConsumer<R, ? super T> accumulator,
BiConsumer<R, R> combiner);
```

This syntax is similar to the reduce() method syntax. This time, however, we have to initialise a container (Supplier), then define the accumulator method that will add an element to a container, and finally define the combiner method that will create a new container from the two temporary containers.

In order to simplify our code, the Stream API also provides another class, Collectors, which encapsulates the three arguments necessary to a reduction for certain classical operations (e.g. fetching data in a list, map or set, concatenating characters). For instance, we can modify our previous code and get the same result:

```language-java
List<Order> myOrders = …;
int revenue = myOrders.stream()
                      .collect( Collectors.summingInt( Order::getPrice ) );
```

# Conclusion

This new JDK 8 API is going to fundamentally change the way we process Collections by providing an alternative to the cumbersome Iterator pattern. The new API benefits from the new lambda syntax to reduce code size while improving performance. Moreover, the Collectors class provides many patterns that will often replace the Iterator pattern.

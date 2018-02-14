---
authors:
- Alexis Seigneurin
categories:
- Kotlin
- Language
date: 2018-02-13T01:42:58.000Z
title: "My journey with Kotlin - Part 2: Introduction to Kotlin"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/02/kotlin-2-1.svg
---

This is the second post of a 2-part introduction to Kotlin, a programming language for the JVM. If you are a Java developer, you should read this and, like I did a few months ago, consider the option of learning Kotlin.

If you haven't read it, [part 1](/my-journey-with-kotlin-part-1-how-i-came-to-dislike-java/) will tell you the reasons why you would want to go away from Java.

# Kotlin

Kotlin is a JVM language initiated and developed by JetBrains, the company behind IntelliJ IDEA. The primary goals of Kotlin are:

* to make the code more concise and more readable than Java code
* to make the code safer than pure Java code (i.e. avoid nulls).

Kotlin is a statically typed language and it feels modern. It is also mature: great support is provided in build tools (Maven, Gradle), Spring 5 will provide extended support for Kotlin (to write more idiomatic code), and, last but not least, [Android made Kotlin one of its official programming languages](https://blog.jetbrains.com/kotlin/2017/05/kotlin-on-android-now-official/).

Kotlin was made for 100% interoperability with Java. You can call Kotlin code from Java code, or call Java code from Kotlin code, without any awkward syntax (unllike sometimes Scala). This also means you can migrate one class at a time. If you use IntelliJ IDEA (and you should), the IDE can convert a file, a package, or a whole codebase automatically. The converted code usually works fine, and you can then adjust it to make it more Kotlin-idiomatic.

# Basic syntax

## Defining a class and methods

Here is how to define a class with a constructor and 2 methods:

```kotlin
class KafkaDescribeClient(val adminClientFactory: KafkaAdminClientFactory) {
    fun describeCluster(region: String, cluster: String): ClusterDto {
        …
    }
    fun buildUri(securityProtocol: String, instanceName: String, port: String) = "..."
}
```

* `class` is used to define a class
* `fun` is used to define a method
* `(val adminClientFactory: KafkaAdminClientFactory)` defines a constructor with one parameter, and a field to store this value
* Parameters and variables are declared with the `<name>: <type>` syntax. In a similar fashion, the return type of a function is placed after the list of arguments (`ClusterDto` in the example).
* When a function is a single statement, you can omit the brackets, the return statement, and optionally the return type. Just use `=` between the declaration and the definition of the function.

## Defining an immutable variable

You can declare a variable without explicitly defining its type (the type inference mechanism will _infer_ the type, in this case `String`):

```kotlin
val region = "us-east-1"
```

Or you can explicitly declare the type (again, with `<name>: <type>` syntax):

```kotlin
val cluster: String = "engine"
```

When creating an object, the `new` keyword is omitted:

```kotlin
val adminClient = KafkaDescribeClient(adminClientFactory)
```

In the 3 cases above, the variables cannot be reassigned. These variables are _immutable_.

## Defining a mutable variable

You could define _mutable_ variables with the `var` keyword (but you don’t really need that):

```kotlin
var something = 12
something = 42
```

## Static methods

Kotlin doesn't have a `static` keyword. Instead, you define functions outside of a class:

```kotlin
package com.seigneurin.mapper

fun nodeToDto(node: Node): NodeDto {
    return NodeDto(node.id(), node.host(), node.port(), node.rack())
}
```

You can then import the function and call it directly, without a class prefix:

```kotlin
import com.seigneurin.mapper.nodeToDto
...
nodeToDto(controller)
```

## Reference equality

`===` and `!==` can be used to test reference equality.

`==` calls the `equals()` method under the hood, making it much simpler to compare objects.

From the documentation:

> Note that the `==` operator in Kotlin code is translated into a call to [equals] when objects on both sides of the operator are not null.

Meaning `==` is safe, and `o == null` or `null == o` do not raise `NullPointerException`s.

# Strings

For string comparison, use ‘==’

```kotlin
if (port == "9092") {
```

Kotlin also has built-in string interpolation using the `$variable` syntax (or `${...}` for more complex expressions):

```kotlin
private fun buildUri(securityProtocol: String, instanceName: String, port: String) =
        "$securityProtocol://$instanceName$domain:$port"
```

Multi-line strings can be created with `"""` (and there is also an option to trim the margin):

```kotlin
        System.err.println("""
========== Data checking ==========
Purpose: checks data quality of messages in a topic.
- Kafka brokers      = ${config.kafkaBrokers}
- Kafka group ID     = ${config.applicationId}
...
      """)
```

# Data classes

Kotlin has a special type of classes to replace POJOs: _data classes_.

You can declare such classes with the `data class` keywords and, most of the times, your class won't need a body:

```kotlin
data class AclParameters(
        val operation: AclOperation,
        val user: String,
        val hostname: String?,
        val groupId: String?)
```

The `equals()`, `hashCode()` and `toString()` methods will be automatically generated by the compiler.

Again, to create an instance of a class, Kotlin doesn't have a `new` keyword:

```kotlin
val aclParams = AclParameters(AclOperation.READ, "u1", "h1", “g1")
```

Notice that it is a good practice to make your classes immutable by using `val` for your fields. Instead of modifying an instance, you would then make a copy and change some fields:

```kotlin
val aclParamsCopy = aclParams.copy(groupId = groupName)
```

If you use Jackson to map JSON values to object, [Jackson has a module](https://github.com/FasterXML/jackson-module-kotlin) for this.

Data classes can also be _destructured_ to get the values of the individual fields of the class:

```kotlin
val (operation, user, hostname, groupId) = aclParams
```

Or you could partially destructure the class if you don't need all the values:

```kotlin
val (operation, _, hostname, _) = aclParams
```

# Expressions

In Kotlin, complex expression can be evaluated and the result be assigned to a variable:

```kotlin
val res = if (operation == CREATE) {
    createAcl(aclParameters)
} else if (operation == ALTER) {
    alterAcl(aclParameters)
} else if (operation == DELETE) {
    deleteAcl(aclParameters)
} else {
    throw RuntimeException("Operation not supported: ${operation}")
}
```

This is a good example of how you can use _immutable_ variables instead of _mutable_ ones.

# Streams

If you have read [part 1 of this post](/my-journey-with-kotlin-part-1-how-i-came-to-dislike-java/), you probably emember this piece of Java code:

```java
List<AclBinding> userAclBindings = aclBindings.stream()
        .filter(aclBinding -> aclBinding.entry().principal().equals(filterUser))
        .collect(Collectors.toList());
```

You can achieve the same in Kotlin in a more readable way:

```kotlin
val userAclBindings = aclBindings.filter { it.entry().principal() == filterUser }
```

A few things to note:

* The type of the returned value (`userAclBindings`) is inferred by the compiler.
* `filter()` is directly called on the collection (no call to `stream()`).
* The result _is_ a collection (no need to use a collector).
* `it` automatically refers to the argument of the lambda expression.

Here is another example of Java code:

```java
private List<String> buildKafkaUris(List<Instance> instances, String securityProtocol, String port) {
    return instances.stream()
            .map(i -> getTagOrFail(i, TAG_KEY_NAME))
            .map(name -> buildUri(securityProtocol, name, port))
            .collect(Collectors.toList());
}
```

The same in Kotlin:

```kotlin
private fun buildKafkaUris(instances: List<Instance>, securityProtocol: String, port: String): List<String> {
    return instances
            .map { getTag(it, TAG_KEY_NAME) }
            .map { buildUri(securityProtocol, it, port) }
}
```

(We also have null-safe code, here - see below.)

# Null safety

Kotlin tries to eliminate `NullPointerException`s through a simple syntax and extra checks provided by the compiler.

Variables that allow `null` values have their types suffixed with `?`:

```kotlin
var s: String? = null
```

If you try to use the value of a nullable variable without testing for `null`s, the compiler will raise an error:

```kotlin
return s.length
```

```text
Error:(37, 17) Kotlin: Only safe (?.) or non-null asserted (!!.) calls are allowed on a nullable receiver of type String?
```

A simple `if` statement will solve the error:

```kotlin
if (s !=null) {
    return s.length
}
```

Notice that, once you have tested a value for nulls, its type automatically becomes a non-nullable type (`String` instead of `String?`) in the rest of the function.

The compiler will not allow you to return a potentially null value if the return type was not declared as nullable:

```kotlin
private fun getTag(instance: Instance, tagKey: String): String {
    val tag = instance.tags
            ...
            .firstOrNull()
    return tag // this can be null
}
```

```text
Error:(40, 16) Kotlin: Type mismatch: inferred type is String? but String was expected
```

The following is safe:

```kotlin
private fun getTag(instance: Instance, tagKey: String): String {
    val tag = instance.tags
            ...
            .firstOrNull()
    if (tag == null) {
        throw  RuntimeException("Tag [${tagKey}] not found")
    }
    return tag // this cannot be null
}
```

(Or just call first() in this case.)

## Elvis operator

Kotlin has an _Elvis operator_ to get a value or, if it is `null`, a default value:

```kotlin
val hostname = aclParameters.hostname ?: “*”
```

## Safe calls

If a value is nullable, you can make a _safe call_, i.e. call the method/field is the object is defined, or return `null` otherwise:

```kotlin
val length: Int? = s?.length
```

Because `null` can be returned, the type of the returned value is a nullable type.

# Functions

Kotlin has first class support for functions:

```kotlin
protected fun <R> withAdminClient(func: (AdminClient) -> R): R {
    val adminClient = adminClientFactory.createAdminClient()
    return adminClient.use { func(adminClient) }
}
```

Here, `func` is a variable of type `(AdminClient) -> R`, i.e. a function that takes one argument of type `AdminClient` and returns a value of type `R`.

Then, the function is called directly by passing parameters: `func(adminClient)`.

# Type aliases

Aliases can be defined on any type:

```kotlin
typealias Clusters = List<Cluster>
```

This can be handy when you want to avoid repeating complex types, e.g.:

```kotlin
typealias KakfaAdminFunc<R> = (AdminClient) -> R

protected fun <R> withAdminClient(func: KakfaAdminFunc<R>): R {
    ...
```

Notice that type aliases are local to your compilation unit.

# Extension functions

Kotlin also allows you to extend existing classes with new methods. These extension function are resolved statically / locally and make it as if you were calling a real method of a class.

You define the function by prefixing the name of the function with the name of the class to extend:

```kotlin
fun AmazonEC2.findInstances(request: DescribeInstancesRequest): ArrayList<Instance> {
    ...
    return allInstances
}
```

You then call the function like any other method:

```kotlin
val ec2: AmazonEC2 = ...
ec2.findInstances(request)
```

# Conclusion

This post gives you an overview of the syntax of Kotlin. Make sure to read [the reference of the language](https://kotlinlang.org/docs/reference/) to get more details.

So far, I have had a very positive experience with Kotlin:

* The language is easy to use and intuitive.
* It has greater expressivity than Java.
* It is simpler than Scala.

I really encourage you to try it out. And if you need help convincing your team to migrate from Java to Kotlin, I will be happy to assist!

---
authors:
- Davis Mohar
tags:
- Java
- Kotlin
- Spring Boot
date: 2015-04-23T12:21:50.000Z
title: "Kotlin: Like Java, But Better (Part 1)"
image: #TODO
---

Kotlin has quickly risen to popularity as an alternative language to Java. In 
this blog series, we will first get a Kotlin crash-course examining some of the
design between Kotlin and Java, and then add some Kotlin code to an existing
Java Spring Boot microservice in part two.

# Kotlin 101
<!-- TODO ADD IMAGE HERE. Maybe school blackboard with 'kotlin 101' on it or something-->
Kotlin was introduced by JetBrains in 2011 as a new language for the JVM. It
was designed to be fully interoperable with Java, and in several cases, has 
replaced it. In 2019, Google announced that Kotlin was now the offical 
language for Android development. Tools such as Spring Boot and 
[JHipster](https://www.jhipster.tech/) allow you to start from scratch with
Kotlin, but you can also seamlessly add Kotlin to an existing Java codebase!

# What Kotlin Does Differently
## Variable Declaration
There are two keywords to declare a variable: `var` and `val`. 

`var` is used 
to declare a variable that behaves just as you would expect a normal Java 
variable to act. The type of the variable is declared after the variable name,
and declaring the type is optional if the variable is instantiated. 
```
var num = 12
var name: String
name = "Jane Doe"
```

`val` is used to declare variables as read only, similar to declaring a 
variable final in Java. Attempting to change the reference of a val will result
in a compilation error.
```
val num = 12 
num = 13 //compilation error
```

You also may notice the lack of semicolons. Fear not- I haven't made a mistake!
Semicolons are optional in Kotlin, and you shouldn't ever need them. The 
compiler will infer your statement endings.

## Null Safety
One of the key design decisions that the Kotlin team made was to enforce 
[null safety](https://kotlinlang.org/docs/null-safety.html) at compile-time.
All variables must explicitly be declared as either nullable or non-nullable. 
```
var foo: Int? // nullable Int variable
var bar: Int // non-nullable Int

bar = Null // compilation error
```
As a result of this feature, NullPointerExceptions will never be thrown in 
pure Kotlin code unless you explicitly throw one. They won't be missed! Kotlin
also provides some additonal syntax to help working with these new null 
constraints easier. 

Inline null-checks can be used to access methods or fields of an object that
may or may not be null. If the object is null, then expression is evaluated as
null. If the object is not null, then the expression is evaluated as normal.
```
var name: String? = null
var length: Int? = name?.length()
// length is null

name = "Jane Doe"
length = name?.length()
// length is eight
```

My favorite null safety feature is the Elvis operator `?:`. This operator has 
earned such a phenominal name because if you tilt your head sideways, it kind 
of looks like an emoticon of Elvis with his rock-star hair! It is the 
null-coalescing operator availible in Kotlin, and makes working with nullable
variables even easier. The Elvis operator allows for you to return an 
alternative value if a statement is null.
```
var name: String? = null
var length: Int // length is a non-nullable Int
length = name?.length() ?: 0 
// length is zero

name = "Jane Doe"
length = name?.length() ?: 0
// length is eight
```

## Data Classes
Kotlin provides the 
[data class](https://kotlinlang.org/docs/data-classes.html) syntax to make 
declaring POJOs (Plain Old Java Objects) a one line affair.
```
data class Person(var firstName: String, var lastName: String, var age: Int)
```
The data class syntax creates a new class with a default constructor, getters 
and setters, and commonly used methods such as equals(), toString(), 
hashCode(), and copy(). All of these defaults can be overridden if neccessary,
but are completely fine for most use cases.

## Class Extensions
One of the coolest Kotlin features is the ability to extend existing classes by
defining new methods for that class outside of the class definition. These 
[extension functions](https://kotlinlang.org/docs/extensions.html) are 
extremely useful when working with classes that are imported from a library or 
if you want to install some new logic onto a built-in class.

```
// MutableList is Kotlin's built-in version of ArrayList
fun MutableList<Int>.swap(index1: Int, index2: Int) {
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}

val list = mutableListOf(1, 2, 3)
list.swap(0, 2)
```

## Coroutines

Kotlin has introduced a robust 
[coroutine](https://kotlinlang.org/docs/coroutines-overview.html) system to 
support asynchronous programming. Coroutines are basically lightweight virtual 
threads that all run on the main thread.  

```
fun main() {
    GlobalScope.launch { // launch a new coroutine in background and continue
        delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
        println("World!") // print after delay
    }
    println("Hello,") // main thread continues while coroutine is delayed
    Thread.sleep(2000L) // block main thread for 2 seconds to keep JVM alive
}
```

An in-depth look at all of what coroutines have to offer would probably turn
this blog post into a short book, but they are one of the primary featuress 
that allow you to utilize more modern programming principles in your Kotlin 
code.

# How to Get Started
If you're ready to get started with Kotlin, there are tons of great resources 
to learn Kotlin from a Java background.
- [Kotlin Koans]()
- b
- c
- d


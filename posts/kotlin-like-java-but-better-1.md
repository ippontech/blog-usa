---
authors:
- Davis Mohar
tags:
- Java
- Kotlin
- Spring Boot
date: 2015-04-23T12:21:50.000Z
title: "Kotlin: Like Java, But Better"
image: #TODO
---

Kotlin has quickly risen to populatiry as an alternative language to Java. In 
this blog, we will get a Kotlin crash-course examining some of the design 
between Kotlin and Java, and then add some Kotlin code to an existing Java
Spring Boot microservice.

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
name = "Davis Mohar"
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
One of the key design decisions that the Kotlin team made was to enforce null
safety at compile-time. All variables must explicitly be declared as either
nullable or non-nullable. 
```
var foo: Int? // nullable Int variable
var bar: Int // non-nullable Int

bar = Null // compilation error
```
As a result of this feature, NullPointerExceptions will never be thrown in 
pure Kotlin code unless you explicitly throw one. They won't be missed! Kotlin
also provides some additonal syntax to help working with these new null 
constraints easier. 

## Data Classes

## Class Extensions

## Coroutines

# What Makes Kotlin Better?
<!-- TODO dont like this section name-->





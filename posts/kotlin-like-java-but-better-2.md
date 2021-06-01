---
authors:
- Davis Mohar
tags:
- Java
- Kotlin
- Spring Boot
date: 2015-05-17T12:21:50.000Z
title: "Kotlin: Like Java, But Better (Part 2)"
image: https://github.com/davismohar/blog-usa/blob/master/images/2021/05/kotlin-like-java-but-better-1.png
---

If you haven't already read part one of this blog series, then check out [Kotlin: Like Java, But Better Part 1](https://blog.ippon.tech/kotlin-like-java-but-better-part-1/)!

# Introduction
One of Kotlin's greatest strengths is the full iteroperability with Java. This enables developers to write Kotlin code in existing Java projects, which makes it much easier to get started with Kotlin. In this blog post, we will put this interoperability to the test by converting an existing Java Spring Boot microservice into Kotlin!

If you're following along at home, you can clone the [source code from github](https://github.com/davismohar/kotlin-spring-boot-demo). The 'main' branch has the starting Java code, and the 'kotlin' branch contains the final app that has been converted to Kotlin. 

# Our Microservice
First, lets take a tour of the Spring application we will be working with! This is a very basic app that has a few REST endpoints that allow us to manipulate a list of people. The endpoints are:
- `/api/ping`
  - Basic health check endpoint. Can be used to verify the application is up.
- `/api/people`
  - A `GET` request to this endpoint will return the current list of people.
- `/api/people/add`
  - Make a `POST` request to this endpoint with a Person object in the body to add that Person to the list.
  - A valid Person JSON Object looks like: `{"firstName":"Jane", "lastName": "Doe", "Age": 31}`

Let's get the app running and test these endpoints out! 

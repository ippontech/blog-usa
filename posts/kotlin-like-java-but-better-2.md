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

It is highly recommend to use [IntelliJ IDEA](https://www.jetbrains.com/idea/) as your IDE when working with Kotlin files. 

# Our Microservice
First, lets take a tour of the Spring application we will be working with! This is a very basic app that has a few REST endpoints that allow us to manipulate a list of people. The endpoints are:
- `/api/ping`
  - Basic health check endpoint. Can be used to verify the application is up.
- `/api/people`
  - A `GET` request to this endpoint will return the current list of people.
- `/api/people/add`
  - Make a `POST` request to this endpoint with a Person object in the body to add that Person to the list.
  - A valid Person JSON Object looks like: `{"firstName":"Jane", "lastName": "Doe", "Age": 31}`

Start up the app and test these endpoints out! First, we can hit the `/ping` endpoint to double check that our service is running and accepting connections. We expect a simple `pong` response from this request.
```
curl --request GET --url http://localhost:8080/api/ping
```

We can then add a new person to the service using the `/people/add` endpoint. Feel free to edit this command to add several people! We expect the first and last name of the person we added to be returned. For the following request, we expect a response with a body of `Jane Doe`.
```
curl --request POST \
  --url http://localhost:8080/api/people/add \
  --header 'Content-Type: application/json' \
  --data '{
	"firstName": "Jane",
	"lastName": "Doe",
	"age": 32
}'
```

Finally, we can request the list of people with the `/people` endpoint.
```
curl --request GET \
  --url http://localhost:8080/api/people
```
We expect a response that looks something like this (assuming you've added two People so far):
```
[
  {
    "firstName": "Jane",
    "lastName": "Doe",
    "age": 32
  },
  {
    "firstName": "John",
    "lastName": "Doe",
    "age": 34
  }
]
```
As we continue working with the service, you may want to use a tool like [Postman](https://www.postman.com/) or [Insomnia](https://insomnia.rest/) instead of manually editing the curl commands.

# Diving Into the Code
Now that we have explored the general behavior of the application, let's take a look at the `Person.java`, `PersonService.java`, and `PersonController.java` classes and convert them to Kotlin classes.

There is also a test suite included with this repo that we will be using to ensure that the behavior and functionality of the application does not change as we convert our application.

## Person.Java
```Kotlin
package com.example.kotlinspringbootdemo.model;

public class Person {
    private String firstName;
    private String lastName;
    private int age;

    public Person(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public int getAge() {
        return this.age;
    }

    public void setFirstName(String name) {
        this.firstName = name;
    }

    public void setLastName(String name) {
        this.lastName = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String toString() {
        return this.firstName + " " + this.lastName;
    }

}
```
This class represents the Person model that our service works with. This is a great use case for Kotlin's [Data Classes](https://kotlinlang.org/docs/data-classes.html), as there are lots of boilerplate getter and setter functions. 


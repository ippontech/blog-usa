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

If you haven't already read part one of this blog series, then check out '[Kotlin: Like Java, But Better Part 1](https://blog.ippon.tech/kotlin-like-java-but-better-part-1/)' to learn about Kotlin's background and features!

# Introduction
One of Kotlin's greatest strengths is the full interoperability with Java. This enables developers to write Kotlin code in existing Java projects, which makes it much easier to get started with Kotlin. In this blog post, we will put this interoperability to the test by converting an existing Java Spring Boot microservice into Kotlin.

If you're following along at home, you can clone the [source code from github](https://github.com/davismohar/kotlin-spring-boot-demo). The 'main' branch has the starting Java code, and the 'kotlin' branch contains the final app that has been converted to Kotlin. 

It is highly recommend to use [IntelliJ IDEA](https://www.jetbrains.com/idea/) as your IDE when working with Kotlin files. 

# Our Microservice
First, lets take a tour of the Spring application we will be working with. This is a basic app that has a few REST endpoints that allow us to manipulate a list of people. The endpoints are:
- `/api/ping`
  - Basic health check endpoint. Can be used to verify the application is up.
- `/api/people/add`
  - Make a `POST` request to this endpoint with a Person object in the body to add that Person to the list.
  - A valid Person JSON Object looks like: `{"firstName":"Jane", "lastName": "Doe", "Age": 31}`
- `/api/people`
  - A `GET` request to this endpoint will return the current list of people.

### /api/ping
Start up the app and test these endpoints out. First, we can hit the `/ping` endpoint to double check that our service is running and accepting connections. We expect a simple `pong` response from this request.
```
curl --request GET --url http://localhost:8080/api/ping
```

### /api/people/add
We can then add a new person to the service using the `/people/add` endpoint. Feel free to edit this command to add several people. We expect the first and last name of the person we added to be returned.
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
The expected response looks like:
```
Jane Doe
```

### /api/people
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
## The `Person` Class
### Person.java
```Java
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
This class represents the Person model that our service works with. This is a fairly basic Plain Old Java Object (POJO) that includes several boilerplate getter and setter functions. As we are rewriting this class in Kotlin, this is a great opportunity to utilize [Data Classes](https://kotlinlang.org/docs/data-classes.html), which already have default getter and setter functions, as well as defaults for other common functions such as `toString()`. We can delete the old Person.Java file, and create a new Person.kt Kotlin file that looks like this:
### Person.kt
```Kotlin
/**
 * Class constructor declaration, which declares the properties for the class, 
 * as well as getters and setters for these properties.
 **/
data class Person(
    var firstName: String,
    var lastName: String,
    var age: Int
) {
    /**
     * Override of the default toString() function to match the functionality
     * of the Java class. Notice the use of string templates to dynamically 
     * insert the variables into the string
     **/
    override fun toString(): String {
        return "$firstName $lastName"
    }
}
```
We can verify that our application functionality hasn't changed by running our test suite with a `mvn test`. Once we're sure that our code is still working as expected, we can move on to converting our next class. 

## The `PersonService` Class
### PersonService.java
```Java
@Service
public class PersonService {
    private ArrayList<Person> people;

    public JavaPersonService() {
        people = new ArrayList<>();
    }

    public ArrayList<Person> getPeople() {
        return this.people;
    }

    public Person addPerson(Person person) {
        this.people.add(person);
        return person;
    }
}
```
The PersonService handles the logic for maintaining our Person list. There's nothing too complicated going on with this class, but take note that it has the Spring `@Service` annotation. We will want to make sure to include that in our Kotlin class, as well as double check that it functions the same way once we have made our conversion.

As we're writing the Kotlin code, we can initialize our `people` variable with the declaration so that there is no need to write an `init` block to replace the constructor. We also don't need to include the `getPeople()` function, as that is automatically provided for us. The only function that we need to rewrite is the `addPerson()` function. This function in our Kotlin code is nearly identical, save for minor syntactic differences. Notice we have the same `@Service` annotation above the class as well. Spring requires no additional information or configuration to support the Java/Kotlin interoperability.

### PersonService.kt
```Kotlin
@Service
class PersonService {
    // Directly initialize the 'people' ArrayList
    val people = ArrayList<Person>()
 
    fun addPerson(person: Person): Person {
        people.add(person)
        return person
    }
}
```

Once again, we can run a `mvn test` to verify that our functionality hasn't changed. Let's move on to our third and final class to convert.

## The `PersonController` class
### PersonController.java
```java
@Controller
@RequestMapping("/api")
public class PersonController {

    PersonService personService = new PersonService();

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("pong", HttpStatus.OK);
    }

    @GetMapping("/people")
    @ResponseBody
    public  ResponseEntity<ArrayList<Person>> getPeople() {
        return new ResponseEntity<>(personService.getPeople(), HttpStatus.OK);
    }

    @PostMapping("/people/add")
    public ResponseEntity<String> addPerson(
            @RequestBody Person person
    ) {
        try {
            return new ResponseEntity<>(personService.addPerson(person).toString(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Not a valid person object", HttpStatus.BAD_REQUEST);
        }
    }
}
```
Our controller class is fairly basic, without very much we can change or improve as we convert to Kotlin. Notice that in our Kotlin Controller the `person` argument in the `addPerson()` function is not nullable. This is intentional, as Spring will automatically respond with a `400 Bad Request` if a Person object is not passed in the body of the request, so we can always assume that the body request exists. We have also made no changes to any of the Spring annotations, further showcasing the interoperability support that is offered by Spring.

### PersonController.kt
```kotlin
@Controller
@RequestMapping("/api")
class PersonController {
    val personService = PersonService()

    @GetMapping("/ping")
    fun ping(): ResponseEntity<String> { return ResponseEntity("pong", HttpStatus.OK) }

    @GetMapping("/people")
    @ResponseBody
    fun getPeople(): ResponseEntity<ArrayList<Person>> {
        return ResponseEntity(personService.people, HttpStatus.OK)
    }

    @PostMapping("/people/add")
    fun addPerson(
        @RequestBody person: Person
    ): ResponseEntity<String> {
        return ResponseEntity(personService.addPerson(person).toString(), HttpStatus.OK)
    }
}
```
We can run our tests one last time to ensure that our app still works as expected, and then we can sit back and admire our work!

# Wrap up
By converting our app from Java to Kotlin, we have been able to see some of the features that Kotlin offers, such as: Data Classes, Java interoperability, and Spring compatibility. We were also able to make the code in our app easier to read by reducing boilerplate code while maintaining a similar structure.

Want to learn more about Kotlin? Check out these resources:
- [Getting Started With Kotlin](https://kotlinlang.org/docs/getting-started.html)
- [Kotlin Koans](https://play.kotlinlang.org/koans/overview) - A set of exercises to introduce someone familiar with Java to Kotlin syntax and patterns  
- [Spring Boot Kotlin Tutorial](https://spring.io/guides/tutorials/spring-boot-kotlin/) - Get started creating a Spring Boot project completely in Kotlin.

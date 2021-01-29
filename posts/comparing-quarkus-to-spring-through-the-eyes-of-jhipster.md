---
authors:
- Dennis Sharpe
tags:
- Quarkus
- Spring
- JHipster
- Java
date: 2021-01-29T15:52:00.000Z
title: "Comparing Quarkus to Spring Through the Eyes of JHipster"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/01/quarkus-main.png
---
# Approach
According to the website, ![Quarkus](https://quarkus.io/) is “A Kubernetes Native Java stack tailored for OpenJDK HotSpot and GraalVM, crafted from the best of breed Java libraries and standards.” I wanted to explore the differences between a typical Spring Boot/Data application and a Quarkus application from a code perspective. (as opposed to performance) 


# Generate an Application
Since JHipster generates an application that is more real than Hello World, I fired up the Quarkus JHipster blueprint. I would like to thank this ![post](https://medium.com/quarkify/generate-quarkus-project-with-jhipster-bff4edecb815) for providing step-by-step instructions on how to install the Quarkus blueprint and generate an application.


# What’s different?
## Basic Entities and Repositories
Since the entities are still using JPA, they do not look all that much different from the Spring version. They do extend from `PanacheEntityBase` which is a Quarkus-specific class that provides your typical `findById`, `find`, `findAll`, `list` and similar methods. This is markedly different from the Spring Data pattern where the Entity classes do not typically extend another class and the Repository interface provides these types of methods. The Repository pattern can still be used with Quarkus but it uses a class that implements `PanacheRepository` instead of an interface.

### Spring Data
```java
@Entity
@Table(name = "country")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Country implements Serializable {
```
### Quarkus
```java
@Entity
@Table(name = "country")
@Cacheable
@RegisterForReflection
public class Country extends PanacheEntityBase implements Serializable 
```

## Queries
### Spring Data
Spring Data provides “syntactic sugar” for a variety of queries. Queries can be built simply by adding Repository methods with the correct naming scheme.
```java
List<User> findAllByActivatedIsFalseAndCreatedDateBefore(Instant dateTime)
```
And for more complex queries, the @Query annotation can be used.
```java
@Query(value = "select distinct ticket from Ticket ticket left join fetch ticket.labels")
List<Ticket> findAllWithEagerRelationships();
```
### Quarkus
Quarkus does not provide the method name query syntax. For simple add-ons to a typical list query, (i.e. an ORDER BY clause) the `PanacheEntityBase` methods provide String inputs.
```java
List<Person> persons = Person.list("order by name,birth");
```
More complex queries can be passed in this SQL-like syntax to the find and list methods.

## Pagination
### Spring Data
With Spring Data, implement the `PagingAndSortingRepository` and a paginated `findAll` method is provided for the simple case.
```java
Page<T> findAll(Pageable pageable);
```
###Quarkus
In Quarkus, the finder methods return a `PanacheQuery` object that can be operated on to provide pagination.
```java
// create a query for all living persons
PanacheQuery<Person> livingPersons = Person.find("status", Status.Alive);

// make it use pages of 25 entries at a time
livingPersons.page(Page.ofSize(25));

// get the first page
List<Person> firstPage = livingPersons.list();

// get the second page
List<Person> secondPage = livingPersons.nextPage().list();
```

## REST Resources (Controllers)
The REST resources are very similar between the two frameworks. The differences are basically syntactical and use about the same amount of code.

This is an example class declaration:
### Spring Data
```java
@RestController
@RequestMapping("/api")
public class AccountResource
```
### Quarkus
```java
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class AccountResource
```

The method declarations are similar as well:
### Spring Data
```java
@PostMapping("/register")
@ResponseStatus(HttpStatus.CREATED)
public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM)
```
### Quarkus
```java
@POST
@Path("/register")
@PermitAll
public CompletionStage<Response> registerAccount(@Valid ManagedUserVM managedUserVM)
```

# Conclusion
It is clear that Spring is the more mature platform and has had many more years to provide some of the niceties that developers have grown used to having. More code is definitely required to use Quarkus at this point. There are plenty of resources online that talk about performance gains from using Quarkus so that very well could drive the decision to try it out. Based on this introductory analysis, the learning curve does not look too steep for Quarkus. I think the next step could be to investigate how Quarkus handles security and other details.

# Postscript
My understanding is that much of the performance gains provided by Quarkus are directly tied to using the ![GraalVM](https://www.graalvm.org/). Fairly recently, Spring has introduced ![Spring Native for GraalVM](https://github.com/spring-projects-experimental/spring-native/releases/tag/0.8.3) that may end up making the performance gains less of a factor for choosing Quarkus.


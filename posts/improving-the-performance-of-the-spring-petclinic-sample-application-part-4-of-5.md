---
authors:
- Julien Dubois
categories:
- JPA
- Performance
- Spring
- Spring-Petclinic
date: 2013-03-14T11:00:37.000Z
title: "Improving the performance of the Spring-Petclinic sample application (part 4 of 5)"
id: 5a267e57dd54250018d6b5b9
image: 
---

This is part 4 of our 5-part series on improving the performance of the Spring-petclinic application. Here are [part 1](https://test-ippon.ghost.io/improving-the-performance-of-the-spring-petclinic-sample-application-part-1-of-5/), [part 2](https://test-ippon.ghost.io/improving-the-performance-of-the-spring-petclinic-sample-application-part-2-of-5/) and [part 3](https://test-ippon.ghost.io/improving-the-performance-of-the-spring-petclinic-sample-application-part-3-of-5/).

## Testing the other persistence mechanisms: is JDBC really faster?

The good thing with the Spring-petclinic application is that we can easily switch between different persistence solutions: JDBC, JPA and Spring Data JPA.

## First tests: JDBC wins!

We are first doing a very simple test, by switching the Spring profile in the web.xml file and comparing the results.

Here are the results:

- JDBC: 959 req/sec
- JPA: 902 req/sec
- Spring Data JPA: 797 req/sec

One of the reasons of the difference between the JPA and the Spring Data JPA tests is that there is a cache on the [JpaVetRepositoryImpl](https://github.com/jdubois/spring-petclinic/blob/197888fef0ad5066006f817c801c99f57e44103d/src/main/java/org/springframework/samples/petclinic/repository/jpa/JpaVetRepositoryImpl.java#L44). and not on the [SpringDataVetRepository](https://github.com/jdubois/spring-petclinic/blob/197888fef0ad5066006f817c801c99f57e44103d/src/main/java/org/springframework/samples/petclinic/repository/springdatajpa/SpringDataVetRepository.java#L28). So this is rather a non-issue: both repositories have roughly the same performance, and we will generally recommend using Spring Data over JPA as there is a lot less code to write. However, for the rest of the tests we will use JPA as this cache gives us better performance.

## Using Lazy loading and Open Session In View

The previous tests might have given some people the impression that JPA is not a good technology for performance tuning an application.
 This is just plain wrong: JPA is an excellent solution, as long as you use it correctly.

For instance, the biggest problem here is that we have a collection of visits which is eagerly fetched:

[[Source code]](https://github.com/jdubois/spring-petclinic/blob/197888fef0ad5066006f817c801c99f57e44103d/src/main/java/org/springframework/samples/petclinic/model/Pet.java#L51)

Let’s discuss this problem:

- As this is a sample application, demonstrating that we can switch persistence solutions easily, we are fetching all data in the repository layer. So this decision is understandable.
- However, that means that each time we show the “owners” page, we fetch all the visits, for no reason.

In order to solve this issue, we have decided to go back to normal and use lazy-loading (which is the default in Hibernate/JPA). As this will cause lazy loading exceptions in the visits page, we have to find a solution, and there are two of them:

- Create a specific business method that can return the visits eagerly when needed. This is the most performant solution, but it requires recoding a lot of things.
- Use the “Open Session In View” pattern

You will find a lot of people on the Internet telling that the “Open Session In View” is a bad pattern: indeed, it will make your database transaction live a lot longer than needed. Instead of just using a transaction at the service layer, you will now use it as long as your whole JSP view is not completely generated.
 For example, if you configure the Open Session In View filter on your whole application (on the “/*” pattern), and run our stress test again, you will have some transactions errors.

However, we only need it on the owners page, so we are going to configure it on this page only:

[[Source code]](https://github.com/jdubois/spring-petclinic/blob/681026758d2f80082c4597ec0393bff07c95be65/src/main/webapp/WEB-INF/web.xml#L78)

I have personally used this pattern on a lot of production applications, and it really eases development, for a small performance cost. So unless you have some very big performance needs, you can use it without worrying too much.

The resulting code is rather simple to write:

[[Source code]](https://github.com/jdubois/spring-petclinic/commit/681026758d2f80082c4597ec0393bff07c95be65)

And **here are the results: 1066 req/sec** ! So using JPA with the Open Session In View is not such a bad solution after all!

## Conclusion of part 4

Switching to JPA for our persistence layer has given us a performance boost, as soon as we have tuned it, of course! Besides, we have room for improvement: more tuning can probably be done, and the Open Session In View is not a good performance pattern (but it is an excellent productivity pattern!).

But most importantly, switching to JPA (or, even better, Spring Data JPA) has given us an excellent developer productivity and code quality boost.

We will finish this series with part 5, where we will add more cache to the application, to make it even more performant.

[edit]

You can find the other episodes of this series here : [part 1](http://blog.ippon.fr/?p=7496), [part 2](http://blog.ippon.fr/?p=7500), [part 3](http://blog.ippon.fr/?p=7512), and [part 5](http://blog.ippon.fr/?p=7527).

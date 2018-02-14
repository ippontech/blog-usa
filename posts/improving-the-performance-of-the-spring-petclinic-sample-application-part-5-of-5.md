---
authors:
- Julien Dubois
categories:
- Cache
- Performance
- Spring
- Spring-Petclinic
date: 2013-03-15T11:00:40.000Z
title: "Improving the performance of the Spring-Petclinic sample application (part 5 of 5)"
image: 
---

This is part 5 of our 5-part series on improving the performance of the Spring-petclinic application. Here are [part 1](https://test-ippon.ghost.io/improving-the-performance-of-the-spring-petclinic-sample-application-part-1-of-5/), [part 2](https://test-ippon.ghost.io/improving-the-performance-of-the-spring-petclinic-sample-application-part-2-of-5/), [part 3](https://test-ippon.ghost.io/improving-the-performance-of-the-spring-petclinic-sample-application-part-3-of-5/) and [part 4](https://test-ippon.ghost.io/improving-the-performance-of-the-spring-petclinic-sample-application-part-4-of-5/).


## Adding more cache

To increase application performance, one of the classic solutions is to add more cache. We already have a cache configured in the application, it is on the [JpaVetRepositoryImpl](https://github.com/jdubois/spring-petclinic/blob/681026758d2f80082c4597ec0393bff07c95be65/src/main/java/org/springframework/samples/petclinic/repository/jpa/JpaVetRepositoryImpl.java#L44).

We are going to generalize this cache on 2 different parts of the application:

- Using the [Spring Cache abstraction](http://static.springsource.org/spring/docs/3.2.x/spring-framework-reference/html/cache.html), like on the [JpaVetRepositoryImpl](https://github.com/jdubois/spring-petclinic/blob/681026758d2f80082c4597ec0393bff07c95be65/src/main/java/org/springframework/samples/petclinic/repository/jpa/JpaVetRepositoryImpl.java#L44), to cache frequently used business methods. The [JpaOwnerRepositoryImpl.findByLastName()](https://github.com/jdubois/spring-petclinic/blob/05a60b774011c41d522293ec3b2f4ed89b680ec5/src/main/java/org/springframework/samples/petclinic/repository/jpa/JpaOwnerRepositoryImpl.java#L46) method is a perfect candidate for this: doing a “like” clause is very slow on a database, and people’s last names do not change frequently. So we decided to cache this result for 60 seconds.
- Using the Hibernate second level cache: we have cached most entities and collections of the application, and re-coded parts of the application to use this cache. As a result, using Hibernate’s “*showSql=true*” parameter, we can see that the application does not execute any unnecessary SQL request (as long as there is no write, requests are only executed once).

You can see those changes in the following commit:

[[Source code]](https://github.com/jdubois/spring-petclinic/commit/05a60b774011c41d522293ec3b2f4ed89b680ec5)

**Our final result goes up to 1225 req/sec**. That’s only a 15% performance boost, but please note that we have a very small database, and that it is running locally. On a real-world system, the improvement should be better, especially for the JpaOwnerRepositoryImpl.findByLastName() method.


## Extreme testing

We decided to do an extreme testing session, in order to see if we really stabilized the application (using Tomcat’s NIO connector and going stateless, specifically).

So we ran our test again on the Macbook, still with 500 threads, but this time we let it run until we had **100 000 user sessions**.

Our results are extremely good:

- No HTTP error at all
- Memory stays at the same level, no matter how many users are using it
- The application runs smoothly, and stabilizes at **1565 req/sec** !!

This is, of course, an excellent result.


## Conclusion and final thoughts

During those five days, we have gone through the classical steps we use at Ippon Technologies when auditing a project:

- Creating a “real” test case
- Removing data in the HTTP Sessions
- Removing the JVM locks
- Tuning the persistence layer
- Adding a cache

For each of these steps, JMeter and YourKit were our best tools to stress test the application and monitor how it responded to the test.

Of course, more work could have been done on JVM tuning, and on database tuning (but changing the database schema is outside the scope of this article).

At the beginning of the tests, we had to increase our heap memory size to 1 Gb, and could only serve 548 req/sec, with some HTTP errors. After completing our audit, we are now back to 128 M, and can serve 1225 req/sec with no error at all. We expect those results to be even better on a real server, with many cores and threads, and where removing JVM locks will have a more significant impact.

We also had a great improvement in the application stability, having 0 errors and 1565 req/sec with 100 000 users on our “extreme tests”. The application is now ready to handle a lot of users without any trouble.

Last but not least, we have switched the persistence layer from JDBC to JPA: a quick look at the code shows how much clearer and smaller the JPA code is. And the Spring Data JPA code is even clearer and smaller. It’s great to see that quality code can also be more performant than low-level, hard-to-code classes.

[edit]

You can find the other episodes of this series here : [part 1](http://blog.ippon.fr/?p=7496), [part 2](http://blog.ippon.fr/?p=7500), [part 3](http://blog.ippon.fr/?p=7512) and [part 4](http://blog.ippon.fr/?p=7520).

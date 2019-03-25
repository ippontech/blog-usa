---
authors:
- Julien Dubois
tags:
- Performance
- Spring
date: 2013-03-11T11:00:54.000Z
title: "Improving the performance of the Spring-Petclinic sample application (part 1 of 5)"
image: 
---

This post is a performance audit of the [“spring-petclinic” sample application](https://github.com/SpringSource/spring-petclinic) provided by VMWare/SpringSource as a demonstration of the latest Spring features and configurations.

We are going to do a step-by-step audit of the application, and show:

- Tools & techniques we usually work with when doing a performance audit
- Common pitfalls and how to avoid them

For doing this audit, we will use our own fork of the project, which is located at: [https://github.com/jdubois/spring-petclinic](https://github.com/jdubois/spring-petclinic)

As this tuning is a bit long, we have split it into 5 parts, which we will post every day this week:

- Monday: setting up the tests
- Tuesday: going stateless and Tomcat tuning
- Wednesday: removing the JVM locks
- Thursday: should we stay on JDBC, or is JPA better?
- Friday: adding a cache, and final words

Many thanks to:

- [Pierre Templier](https://twitter.com/ptemplier) and [Christophe Parageaud](http://blog.ippon.fr/author/Christophe_PARAGEAUD/), from Ippon Technologies, for proof-reading this analysis.
- [Michaël Isvy](https://twitter.com/michaelisvy) from SpringSource/VMWare, who is the main developer of the Spring-petclinic application, for helping me and allowing me to torture his application.
- [Thibault Duchateau](https://twitter.com/tduchateau) and [Romain Lespinasse](https://twitter.com/rlespinasse) from the Dandelion Project, for correcting their library at an unbelievable speed.
- [James Ward](https://twitter.com/_JamesWard), the author of WebJars, for best practices in using his library.

## Creating a JMeter test

First of all, we have configured the application to be in “production” mode:

- We defined the logging level to error [[Source code]](https://github.com/jdubois/spring-petclinic/commit/2c9a37179d7abe3aa057fee200966a2412e4c2e8)
- We have configured the application to use MySQL (as in this project HSQLDB runs in the same JVM as the application, and thus will distort profiling) [[Source code]](https://github.com/jdubois/spring-petclinic/commit/58ce7d6d794c69696d23d62616221a4708d13455)

We’ve created a JMeter test, which is available [here](https://github.com/jdubois/spring-petclinic/commit/69e55e406db37a386ff8348a5a84343801169f85).

We have run this test a first time, with the “POST new visit” step activated, in order to create 10 visits per pet. This means running this test with 10 threads, doing 13 loops each (as there are 13 pets). The “POST new visit” step was then disabled: the application does not paginate visits, so doing inserts will just break it down really quickly.

As visits are the main objects that are being created in the application, they should have been paginated, or there should be at least a limit on the dataset that is returned. On the other hand, it would be really strange that a user has a pet that goes 1000 times to the vet, so this would not be a realistic test case.

That’s why we have decided to limit this test to 10 visits per pet: this is important, as this will have an impact in the future tests.

We have have then run the test normally (using “*mvn clean tomcat7:run*“), with a 2011 Macbook Pro, with 500 concurrent users each doing the test 10 times.

It is also important to mention that:

- Everything is on the same computer: Tomcat, MySQL, JMeter… This is not a “production” architecture but it’s easier to diagnose/debug it that way
- We use Tomcat 7, with Java 1.6 and 128M of RAM
- We have been careful in doing a “*mvn clean*” in the process, in order to remove older HTTP session states
- We do a “warm up” of the application before launching the tests, so the JIT compiler has the time to optimize the application

We have also run those tests on several other computers, with the same results (proportionally, some having more CPU power than the others).

The first results are as follow: the application quickly goes up to **285 requests per second (req/sec)**, then slows down and throws “**java.lang.OutOfMemoryError: Java heap space**” errors after nearly 700 users. We then have lots of HTTP errors, and the application gets so slow it is not usable anymore.

Of course, we can give more memory to the application, so that it can handle our 5000 users. With 1 Gigabytes of heap, the application can handle the load, with a result of **548 req/sec and 0,2% of HTTP errors**.

## Conclusion of part 1

The application cannot handle a lot of load: with 128 MB of RAM, the application starts to **fail after 600 simultaneous users**, which is a rather small number. Of course, we can give it more RAM to push that limit: we will be able to have more users, but as the GC will have more work, it is likely that the application does not perform very well.

We will see tomorrow, in the next episode of this series, how we started to solve that problem.

[edit]

You can find the other episodes of this series here : [part 2](http://blog.ippon.fr/?p=7500), [part 3](http://blog.ippon.fr/?p=7512), [part 4](http://blog.ippon.fr/?p=7520) and [part 5](http://blog.ippon.fr/?p=7527).

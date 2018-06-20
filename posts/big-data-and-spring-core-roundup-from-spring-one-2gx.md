---
authors:
- Romain Lheritier
tags:
- Big Data
date: 2015-09-24T09:20:22.000Z
title: "Big Data and Spring Core roundup from Spring One 2GX"
image: 
---

By Dennis Sharpe and Romain Lheritier.

<span style="font-weight: 400;">Spring One 2GX 2015 took place in Washington DC from September 14th through September 17th.</span>

<span style="font-weight: 400;">This post is a brief summary of the conference. We will point out some of the talks we saw, so that you can decide to explore them further through their online slides or videos. The videos will be uploaded by Pivotal and available for free in the coming months. </span>

Overall, the main buzzword was ‘[Cloud Native](http://pivotal.io/platform/migrating-to-cloud-native-application-architectures-ebook)’. It is the concept that you should design applications to run anywhere from the get-go. Going from the developer desktop to the cloud should be seamless. Many of the talks, whether about Digital or Big Data development, focused on distributed architectures that can scale flexibly across servers.

# <span style="font-weight: 400;">Big Data</span>

[<span style="font-weight: 400;">Spring XD</span>](http://projects.spring.io/spring-xd/)<span style="font-weight: 400;"> was the subject of many sessions in the Big Data track. Spring XD simplifies data ingestion, analytics and batch processing (workflows), and data export. Its power is in its connectors. It has numerous connectors for databases and external systems such as Hadoop, Spark, GemFire, etc. It is easy to configure orchestration and pipelines without any code. It allows the distribution of the workload among containers. Pivotal also just introduced [Spring Cloud Data Flow](http://blog.pivotal.io/pivotal-cloud-foundry/products/introducing-spring-cloud-data-flow), a ‘cloud native’ evolution of Spring XD.</span>

**IoT realized – the Connected Car (**[**outline**](https://2015.event.springone2gx.com/schedule/sessions/iot_realized_the_connected_car_v2.html)**)**

<span style="font-weight: 400;">Michael Minella presented a POC with a live car driving in Washington DC. All the code for this is available on GitHub at: </span>[<span style="font-weight: 400;">https://github.com/pivotal/IoT-ConnectedCar</span>](https://github.com/pivotal/IoT-ConnectedCar)<span style="font-weight: 400;"> . The car used an ODB2 connector to transmit car data. The data was ingested and processed with Spring XD. The goal is to predict destinations in real-time to in turn provide a more accurate MPG estimate than usual. Their app uses Hadoop and Spark, so it is definitely a good project to learn these skills. One challenge of IoT is the inconsistency of data sources. Cars implement ODB2 differently. It is then critical to have an easy way to cleanse the ingested data, and Spring XD can do this easily without coding.</span>

**Scaling Java Applications for NLP on MPP through PL/Java (**[slides](http://www.slideshare.net/SrivatsanRamanujam))

<span style="font-weight: 400;">Srivatsan Ramajunam described the need for Natural Language Processing (NLP) for sentiment analysis. This technology can be used for churn prediction, for instance. In a telecom company, you can analyze interactions with a customer to see if they are likely to cancel their service. If someone complains about dropped calls, they are more likely to cancel than someone who asks questions about their service package. </span>

<span style="font-weight: 400;">Then Srivatsan described the architecture he POCed to predict commodity futures based on Twitter sentiment analysis. He used Part-of-Speech tagging to extract the context necessary to analyze sentiment. Without context, words can have different meanings (e.g. “cool”). </span>

**Supercharging Operations Analysis: using Spring XD to support analytics** ([outline](https://2015.event.springone2gx.com/schedule/sessions/supercharging_operations_and_analytics_using_spring_xd_to_support_analytics_and_cep.html))

<span style="font-weight: 400;">Log & Event analysis was the subject of another notable talk, by Capital One engineers (Joseph Paulchell and Jason Baumgartner). </span><span style="font-weight: 400;">The business goal is to detect customer dissatisfiers and extract intelligence from online interactions. For instance, they managed to fix weird login issues based on patterns analyzed through their system. Their challenge was to provide data faster to their analysts and deal with the explosion of metrics and recordings. And they have numerous apps to consider. Some of them logged too much, some not enough. </span>

<span style="font-weight: 400;">Their talk showed the evolution of their architecture. It went from saving audit events to a database through JDBC to using non-blocking JMS queues easily called from multiple apps through an @Audit annotation. The annotation solution did not scale up because SpEL expressions became complicated and the amount of data just became unmanageable (over 450GB a day). Replicating to Splunk helped some, but Splunk as a 90 day limit, so they had to dump the data somewhere else (Hadoop data lake). They looked for an open-source solution aligned to their internal skills (Java, Spring) and their constraints. They then decided to use Spring XD to manage data ingestion, transformation and workflow. </span>

# <span style="font-weight: 400;">Spring Core</span>

<span style="font-weight: 400;">Several of the sessions focused on either the core Spring Framework or on supporting technologies built around Spring.</span>

**Modern Java Component Design with Spring Framework 4.2**

<span style="font-weight: 400;">Jurgen Holler and Stephane Nicoll presented all the new features in [Spring 4.2](https://spring.io/blog/2015/07/31/spring-framework-4-2-goes-ga). They also spoke a little about Spring 5. The important note about Spring 5 is that it will require Java 8. Up until this point all Spring versions have been backwards compatible to Java 6. This will allow Spring 5 to take advantage of all the features of Java 8 and better integrate the framework with the new features.</span>

<span style="font-weight: 400;">One of the main features of Spring 4.2 is the ability to aggregate and customize annotations. The Spring framework uses this technique frequently to prevent the copying/pasting of some sets of annotations that are frequently used together. The framework also allows easy customization by allowing overriding of just specific methods contained in an annotation. The framework is very flexible in the ways that these methods are overridden.</span>

<span style="font-weight: 400;">Annotation-driven event listeners were also added to the framework. This is especially useful in messaging applications and reduces the required code significantly.</span>

**Get the Most out of Testing with Spring 4.2**

<span style="font-weight: 400;">Sam Brannen and Nicolas Frankel went over unit testing in general with Spring and a few new features in Spring 4.2 that make testing easier. The two speakers had a fun debate over TestNG and JUnit for unit testing. Surveying the audience, 99% of the attendees used JUnit. The speakers raised some points about differentiating between unit tests and integration tests. They had several tips about testing in general. The naming scheme recommended for tests were *Test.java and *IT.java for unit tests and integration tests, respectfully. Since integration tests generally take longer to run, they recommended running all unit tests first on the build job so no integration tests were executed if a unit test failed. They also recommended tying a hotkey in your IDE to execute the current unit test and the previous unit test.</span>

<span style="font-weight: 400;">One of the improvements in Spring 4.2 is the addition of JUnit </span><span style="font-weight: 400;">TestRule</span><span style="font-weight: 400;">s</span><span style="font-weight: 400;">that are independent of any </span><span style="font-weight: 400;">org.junit.runner.Runner</span><span style="font-weight: 400;"> so it is no longer required to use the </span><span style="font-weight: 400;">SpringJUnit4ClassRunner</span><span style="font-weight: 400;">. That means third-party runners like the Mockito JUnit Runner can be used.</span>

<span style="font-weight: 400;">Also, HtmlUnit support has been enhanced so that a servlet container is no longer required to test HTML pages. Now tests on the view itself are faster and do not require end-to-end integration testing.</span>

<span style="font-weight: 400;">Lastly, inlined SQL statements can be added to tests with the </span><span style="font-weight: 400;">@Sql</span><span style="font-weight: 400;"> annotation.</span>

**Testing your Spring Boot Application with Selenium**

<span style="font-weight: 400;">Mathilde Lemee discussed several projects and tools she used with Selenium for end-to-end integration testing. She recommended disregarding “coverage” when it comes to end-to-end tests and just focusing on core flows through the application. She was a big fan of the [Geb](http://www.slideshare.net/nareshak/better-selenium-tests-with-geb-selenium-conf-2014) (Groovy-based) toolkit for writing Selenium tests. She admitted the Java API for Selenium was serviceable.</span>

<span style="font-weight: 400;">Her main suggestion was to use something called the Page Object Pattern when writing Selenium tests. The idea behind the pattern is to isolate the mechanism for identifying objects on a page (i.e. select boxes, buttons, etc.) from the code performing the tests. These page objects can be shared in multiple tests and if something changes on a page, it only needs to be changed in one place. A page object should not have any assertions.</span>

<span style="font-weight: 400;">She also briefly touched on the use of Selenium Grid versus Jenkins for running Selenium tests. She recommended Jenkins and suggested parallelizing tests if they were taking too long to run. When asked about testing on multiple browsers, she suggested having a Windows node in addition to any linux nodes to allow for Jenkins to kick off Internet Explorer tests.</span>

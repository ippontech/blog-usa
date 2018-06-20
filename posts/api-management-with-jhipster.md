---
authors:
- Julien Dubois
tags:
- JHipster
- api
date: 2017-02-27T19:29:00.000Z
title: "API management with JHipster"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/04/API-Management-with-JHipster-Blog--2-.png
---

# Introduction

API management is a hot topic today with many company acquisitions, like Layer 7 (bought by CA), Apigee (bought by Google), Mashery (bought by Tibco), 3Scale (bought by Red Hat) or Vordel (bought by French company Axway). In the Open Source world, we also have several popular products like [Tyk](https://tyk.io/), [Kong]( https://getkong.org/) or [JHipster](https://jhipster.github.io/), all of which we encounter quite often at Ippon Technologies when working with our various clients.

Those products are particularly helpful when working with microservice architectures, which are also all the rage today. The more services you have, the more you will benefit from an API management solution that provides:

- **A gateway**: it acts as an API front-end and handles the requests, while enforcing security and throttling policies
- **A developer portal**: publishing API endpoints in an easy-to-use portal will help your company developers, but also third-party developers, and ensure your APIs are successful
- **A monitoring and reporting tool**: so you know which API is widely used, and if you provide the correct quality of service
- **An API lifecycle solution**: it will handle new services being published, as well as upgrades of existing services

As the lead developer of JHipster and the one who coded a big part of the API management solution in JHipster, I wanted to write this blog post to highlight what JHipster provides, as this is not clear to many people.

# What is JHipster doing in the API management world?

As a complete development stack, we started developing some API management code in JHipster in 2015, as we needed this for several client projects.

Our customers were selecting some “enterprise” solutions in production, but nothing was available for developers. The issue is that it was nearly impossible for developers to code efficiently, as they had to deploy their microservices to a “developer” portal, and then code their front-end applications on that portal. We found it much easier to provide our own solution, packaged as a Docker image, that could be used instantly on the developers’ machines.

The surprise was that we quickly found out that the Netflix OSS stack, on top of which JHipster is built, is much more powerful than most “enterprise” stacks we were working on.

In the end, there are two ways to use JHipster for API management:

- Only for development, as it’s much easier (and cheaper!) to use than a full-blown, proprietary enterprise solution
- For development and production, as it provides a powerful (and free!) alternative to enterprise solutions

# Is JHipster a popular API management solution?

Indeed, JHipster is never listed as an API management solution, and that’s why this blog post needed to be written.

Despite this lack of advertisement, many people have found out that JHipster is a great API management solution:

- The JHipster Registry, which is at the heart of our microservices architecture, has an official Docker image which was pulled 50,000 times on Docker Hub
- Our statistics are widely inaccurate, but we estimate that 25,000 to 50,000 gateways have been generated, which looks logical compared to the JHipster Registry numbers

Those numbers, as well as contacts we have from companies using JHipster, show that there is a strong interest in the API management solution provided by JHipster, even if it does not have the media coverage that other solutions provide.

And of course, JHipster will never be present in the Gartner magic quadrant, as we don’t pay to get listed somewhere. :-)

# What does JHipster provide?

## A gateway based on Netflix Zuul

The JHipster Gateway is based on [Netflix Zuul]( https://github.com/Netflix/zuul), which is highly performant and provides automatic load balancing, failover, and circuit breaking.

This gateway is a bit different from classical solutions, as we encounter many companies running several JHipster gateways. It is quite common to have a “public gateway,” a “back-office gateway” (with internal APIs), and maybe even a “mobile gateway” (with specific APIs for mobile devices).

This gateway is totally stateless, so it can scale easily if you need failover and load balancing at the gateway level (and in that case, you will need a hardware load-balancer in front of it).

It also provides security to the whole architecture, based on JWT (JSON Web Tokens) and Spring Security. As Spring Security provides APIs for nearly every authentication and authorization system available today, it is quite easy to connect the gateway to your enterprise LDAP or OAuth2 server. If needed, JHipster also provides as a BETA option, a “JHipster UAA” server, which is an OAuth2 server based on [CloudFoundry UAA](https://github.com/cloudfoundry/uaa).

For HTTP request throttling, JHipster will use a Cassandra database and use its distributed counter feature to provide throttling per user, IP address, and/or API. The provided code can of course be modified to suit your company’s specific needs. We have had the opportunity to audit several “enterprise grade” competitors, and each of them used the exact same approach (and usually have their Cassandra nodes wide open to the public, with either no password or a “cassandra/cassandra” combo, allowing us to do that audit!).

## Full, executable documentation using Swagger

Each JHipster application exposes its API using Swagger UI, and the JHipster gateway aggregates all those APIs.

It is thus very easy to select the service you want to use, browse its complete documentation, and even execute it with the Swagger UI or use a command-line tool.

This makes it much easier for front-end developers to use those APIs and is what we commonly use when we develop Angular 2 applications on top of a JHipster architecture.

## Monitoring and reporting

All JHipster applications have the option to send their data to a monitoring server, and it is fully documented at https://jhipster.github.io/monitoring/. Several options are available here, but the most common one is to use the JHipster Console, which is based on the ELK stack, and which provides full monitoring, reporting and alerting to a JHipster microservices architecture.

## Automatic services publishing and rolling upgrades

Each service published in a JHipster microservices architecture is registered in the “JHipster Registry.” This registry allows gateways and microservices to scale horizontally, as they will automatically find newer instances of the same service.

All competing solutions we have seen ask to have a specific configuration or API call to register a new service. They also needed the same kind of operation to de-register a service. Thanks to the JHipster Registry, this all works automatically and out-of-the-box with JHipster.

Of course, if automatic publishing is not what you want, and if you need a more customized approach, this can be configured using Spring Boot application properties on your gateway. And as the JHipster Registry also serves as a Spring Boot configuration server, it can push that specific configuration automatically to the gateway, so this can still be done very easily.

# Which technologies does the JHipster Gateway use?

Like all good software, JHipster stands on the shoulder of giants.

JHipster mixes, in fact, 3 main technologies:

- On the server-side, JHipster is based on Spring Boot and Spring Cloud, which in turn use the Netflix OSS stack that does the heavy work: HTTP request routing, load balancing, circuit breaking, failover…
- On the client-side, a GUI made with Angular 2
- To package everything and deploy it easily (in development and in production), Docker and Docker Compose

Aside from this core technology stack, JHipster also:

- generates your business APIs,
- configures Swagger UI for documentation,
- adds ELK for monitoring,
- creates its own Zuul filters for request throttling and QoS, and
- secures everything with its own Spring Security JWT implementation.

# Benefits of using JHipster as an API Gateway

The biggest benefit of using JHipster is that it’s completely free and Open Source, even for advanced features like scaling or monitoring:

- “Enterprise” solutions are totally locked, so you have no way of knowing how they work internally or how to modify them according to your specific needs.
- “Open Source” solutions like Tik or Kong have enterprise editions, so if you want advanced features you end up in the same world as the “enterprise” solutions.

Being Open Source also means it’s very easy to install, as we don’t ask you to have licenses, keys, etc. Our new trainees at Ippon Technologies are usually up to speed with a full microservices architecture in less than one day, often without any prior knowledge of Spring or Docker!

Performance of the JHipster gateway is very good; our internal tests have shown an excellent throughput, with 50,000 simultaneous users on commodity hardware. This number could be higher with some specific OS and JVM tuning, but as the gateway can scale horizontally, that would be our favorite option for handling more users. This shouldn’t come as a surprise, as Zuul is the solution used at Netflix to handle their traffic! And concerning JHipster itself, we have reports of one company handling 350,000 users without any problem.

Apart from the technical arguments, of course the cost of JHipster is a huge difference, as you don’t have to pay anything! We only ask you to give us a star on GitHub, and write a nice bug report when you find an issue, but that’s all optional. :-)

This also means you will not have to pay in order to scale your gateway(s), and you won’t be locked in if your business grows suddenly.

# Cons of using JHipster as an API Gateway

Last but not least, being Open Source means being transparent, and that has always been the spirit of JHipster.

There are indeed several issues when using JHipster:

- As it is mostly a developer tool, it doesn’t provide a fancy GUI for everything. If you want to have a specific rate limiting policy, you will need to code it. We believe that code should be pretty simple to develop, and that this will give much more speed and power than a configured solution, but some people might find it less user-friendly.
- JHipster is tied to Java. While you can use the gateway in front of a non-Java microservice, and there are also ways to use .NET and NodeJS applications with the JHipster Registry, you will lose a lot of JHipster’s benefits and end up with something similar to what competing solutions provide.
- WebSockets support doesn’t work completely. While the gateway can provide WebSockets support, it cannot serve as a proxy to a microservice that uses WebSockets. This is a limitation of Zuul 1, which will be corrected in the future when Zuul 2 is released. Please also note that JHipster supports Kafka, and that we have many clients who use it to pass messages between the gateway and the microservices, and that this might be a technically superior solution.

# Conclusion

We have seen that JHipster provides a widely-used, complete API management solution that can either be used just for development, or both for development and production.

As an Open Source solution, it is fully configurable and can be customized to your exact needs. Of course, we are always open for improvement. With 300 contributors, we have always welcomed new people and new ideas, so if something doesn’t fit your needs or if there is something you would like to improve, don’t hesitate to participate in the project: https://github.com/jhipster/generator-jhipster.

And if you choose to use JHipster for building your next API platform, don’t forget to join the 140 companies which have registered at https://jhipster.github.io/companies-using-jhipster/ and send us a nice message! We’ll be happy to hear your story!

---
authors:
- John Zaccone
categories:
date: 2015-04-21T12:57:50.000Z
title: "Microservices and Modularity"
id: 5a267e57dd54250018d6b5e4
image: 
---

There is a lot of “buzz” surrounding microservices, and it seems that everyone has a slightly different definition of the term. I prefer to think of microservices as a group of small autonomous services that work together. Services evolve, operate, and are deployed independently of each other, but a group of services work together to solve a larger problem.

In this article, I address one big reason why organizations should start thinking about microservices seriously: Modularity.

Modularity is important when designing large software systems. It allows for the evolution of different components, while minimizing impact to other parts of the system. When the impact of change is low, software developers can make progress towards solving business problems, rather than spending time fixing regression. Without careful consideration for modularity, dependencies of a growing software application will become unmanageable.

[![spagetti code](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/04/spagetti-code.jpg)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/04/spagetti-code.jpg)

We aim for modularity by using abstractions and employing the concepts of cohesion and coupling. Software that needs to change at the same time, should be placed within the same module to define solid cohesion. Software components that can change independently from each other, should be connected with higher granularity interfaces (abstractions) to support loose coupling between components. Implementations that support an interface can be changed or even replaced without affecting software components that rely on that abstraction. The interface itself should be carefully designed to implement [“just enough”](http://martinfowler.com/articles/consumerDrivenContracts.html) validation to allow it to evolve without breaking consumers. The goal is to reduce the frequency of introducing breaking changes into the interface, and thus the need to change all of its dependent software components.

With a monolithic application, maintaining these levels of abstraction is difficult. Boundaries between modules need to be explicitly defined by architects. Multiple developer teams need to work together and communicate to maintain these interfaces. Looming project deadlines pressure teams to take shortcuts, and with the entire application code base at their disposal, it is too easy to create “back doors” around interfaces.

A common “back door” is sharing databases across multiple modules. At the database level, there is no abstraction, no layers to protect dependent software from change. This form of coupling is especially dangerous, as it is an easy mistake to make as a developer, and the implications are devastating. One change to a schema could break all components that depend on that database.

[![services_database_bad](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/04/services_database_bad1.jpg)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/04/services_database_bad1.jpg)

## Introduce microservices architecture

In microservices architecture, services (modules) are isolated from each other across the network, and service interfaces need to be “published” to allow communication between components. The result is firm boundaries between components that are well defined by high granularity interfaces. If implemented correctly, microservices make backdoors difficult because interactions with the underlying software implementation and the database are prohibited. All communication is done through the interface.

[![Microservices protect implementation details with interfaces](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/04/services_databases_good.jpg)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/04/services_databases_good.jpg)

Network separation does not guarantee modularity. Rather, modularity is defined by the nature of the [dependencies between components.](http://www.infoq.com/articles/dependency-oriented-thinking-1) This idea is commonly missed, especially when it comes to the tools brought on with traditional SOA. SOAP-based web services introduce interface definitions through WSDLs. WSDLs are strongly typed, rigid, and are often used to expose business objects directly. Without a layer of abstraction above those objects, WSDLs are subject to frequent changes.

[![Brittle WSDLs](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/04/Brittle-WSDLs.jpg)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/04/Brittle-WSDLs.jpg)

It is possible to use SOAP-based web services, but the interface needs to be designed with the abstraction and decoupling to promote modularity of the system. On the other hand, the architectural guidelines defined by [REST](http://en.wikipedia.org/wiki/Representational_state_transfer) are a great resource to designing interfaces, because its definition focuses on the decoupling of client and server components.

In the end, good modularity (and poor modularity), can result from both a microservices and monolithic architecture. However, the constraints placed by a microservices architecture encourage good practices among developers. The constraints force developers to respect the firm boundaries between software components and enables them to think about the modularity in a system. Interface design increases in value, and guidelines such as REST are implemented to achieve maximum decoupling and better modularity. Ultimately, good modularity means lower impact of change, and lower impact of change means we can solve business problems faster with less bugs.

There are other reasons why organizations should consider microservices architecture. There are also a number of challenges that arise that prove microservices architecture is “not a free lunch”. I’ll cover these in a future article as well as tips on how an organization can go about migrating from a monolithic to a microservices architecture.

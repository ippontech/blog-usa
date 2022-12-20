---
authors:
- Cory Walker
tags:
- Entando
- Monolithic Architecture
- Microservices
- Micro Frontends
- Vertical Teams
date: 
title: "Advancing from microservices to including Micro Frontends and how this leads to Vertical Teams"
image: 
--- 

# Advancing from microservices to including micro frontends and how this leads to vertical teams 

The journey from a monolithic architecture to a more scalable one is more important than ever for larger companies. They need the flexibility to scale and keep up with market demands in this unpredictable, evolving market. As customers' needs change and grow, enterprise applications need to be designed around a loosely-coupled infrastructure. We will explore how microservices solve many issues arising from monolith architectures. Then, we will dive further into how micro frontends take the concept of microservices to the next level. Finally, we will touch on how an application composition platform called Entando opens up possibilities surrounding micro frontends and how they promote vertical-type teams.

## The Journey From Monolithic Type Architecture to Microservices

### What is a Monolithic Architecture?

A [monolithic architecture](https://www.atlassian.com/microservices/microservices-architecture/microservices-vs-monolith) is where all application components are part of a single unit. All application components are developed, scaled, and deployed as one unit. Usually, one main technology stack or programming language is used for the entire application. This type of setup could be ideal for smaller applications, but the monolithic approach can entail many drawbacks for large applications.

![Monolith Architecture Diagram](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/12/monolith.png) 
- ***4 components bundled within 1 application***

### Some Monolithic Architecture Disadvantages

Teams working in this environment risk affecting the work of other teams in the application. Since developers may have different levels of experience, backgrounds, and coding styles, this could increase the chances of bugs or inefficient code within the application. The development process of this type of application has less flexibility, more tangled parts, and a tightly-coupled type of infrastructure. The application components are developed, scaled, and deployed as one unit; therefore, every change made would require the deployment of the whole application. Imagine having to deploy the entire application multiple times a day every time there is a need to make changes to a single component. The release process of an application will be much longer, potentially causing downtime and reducing productivity. This is also the case with scaling some small functionality within a single component of an application.  

Once upon a time, the monolithic approach to development may have been the standard way of composing applications. However, as mentioned above, companies needed a more efficient way to develop, scale, and deploy their applications.

## Microservices

### What are Microservices?

[Microservices](https://aws.amazon.com/microservices) are small individual components completely independent of other components of the application. Essentially, they are loosely-coupled, self-contained services that makeup one whole extensive application. Following the [Single-Responsibility Principle](https://en.wikipedia.org/wiki/Single-responsibility_principle) of [SOLID](https://en.wikipedia.org/wiki/SOLID), microservices are mainly made up of one service, one process, one job, or one function. In essence, each microservice should have one single domain. Therefore, changes to a microservice prevent affecting other components of the application due to its natural encapsulation. Unlike the monolithic architecture, microservices are developed, scaled, and deployed separately. However, they are still part of the application and can communicate with one another via APIs, Message Brokers, or Service Mesh.    
 
Microservices also eliminate the issue of deploying the entire application for a single component or feature. Microservices allow the deployment of a single component with new changes, increasing the release process timeline. Microservices also enable teams to work independently on a single component without affecting other teams working on different application sections. This workflow gives developers more flexibility to code in the language or framework of their choice. 

Examples of microservices may include a shopping cart or an account component of an e-commerce website. As seen in the image below, four components could be microservices.

![Microservices Architecture Diagram](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/12/microservices.png)
- ***4 independently standalone components***

Microservices have improved overall development and solved many issues or limitations surrounding monolith practices. Thus, we can benefit from microservices and enhance the development process even more. Until recent years, microservices have been mainly utilized in the backend. The next logical step is to extend this monolithic-to-microservices migration process to the frontend.

## Micro FrontEnds (MFE)

### What are Micro FrontEnds?

A [micro frontend](https://micro-frontends.org) has all the same development, scaling, testing, and deployment features we explored earlier with microservices. The significant difference is that a micro frontend is a tiny, independent piece of user experience or functionality within the user interface. It is typically divided by steps in a customer journey but can also be even more granular down to the components within a page. This clear division makes the team's code base more manageable by isolating possible failures into a single component. Teams can also take advantage of reusability with micro frontend components. Examples of micro frontend components include a webpage header or footer used throughout multiple pages.

![Micro frontend header footer](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/12/mfe-header-footer.png)

In their production applications, many large enterprise organizations already use micro frontends such as Ikea, Spotify, and Zolando. Micro frontends provide the ability to take a design and deliver it in a modular way. As with microservices, micro frontends allow teams to update iteratively and release independently without risk. MFE's practices enable teams to exercise scalability easily by allowing more developers to work on the same project. Teams can also leverage framework diversity along with a customer focus environment where developers and non-technical managers can concentrate on one feature at a time.


![Micro frontend](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/12/Microfrontend.png)

Micro frontends practices take the concept of microservices to the frontend of an application. It seems like a natural progression that microservices have migrated into the frontend arena utilizing all of their benefits instead of monolithic approaches in large applications. Types of frameworks or tools that use micro frontends are [Open Components](https://opencomponents.github.io), [Bit](https://bit.dev), [Luigi](https://luigi-project.io/), and [Entando](https://www.entando.com).
  
![Entando](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/12/e-logo.png)
- ***image resource [Entando Website](https://www.entando.com)***

### What is Entando?

Entando can be used as a tool which has the capability to really bring [micro frontends](https://developer.entando.com/next/tutorials/create/mfe) to life by providing the ability to automatically create microservices and micro frontends that can be installed into an application. Entando is an open source application composition platform (ACP) designed for [Kubernetes](https://kubernetes.io). It's a set of technologies that enable teams to build, deploy, run, and manage applications. Entando utilizes the independent component concept, along with business capabilities, which are the building blocks of the platform. Components can be micro frontends, microservices, page templates, content, data widgets, UX, or UI elements. Moreover, Entando elimenates the need to code the same feature multiple times as they can be reused in other pages or applications. The business capabilities consists of software tools that enable developers, project managers, and business associates to collaborate through a 4 step process; create, curate, compose, and consume. ***See image below*** 

![Entando Business Capabilities](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/12/4-step-process.png)
- ***image resource [Entando Website](https://developer.entando.com/v7.1/docs)***

Powered by Kubernetes, Entando provides customers with a fast and convenient user experience, simplifying deployments by abstracting the complexity of Kubernetes, thus accelerating app development.

### Integrating MFE into existing legacy applications

Modernizing legacy applications with a micro frontend approach and microservices can allow you to update small pieces of a legacy system without a big-bang process. Upgrades or changes can be made gradually, piece by piece. Carve out only important vital pieces of functionality rather than rewriting your entire infrastructure from scratch all at once.

### Leveraging Vertical Teams

In the agile development world, leveraging microservices and micro frontends, development teams can work on a single piece of functionality at a time from start to finish. Traditionally development teams were somewhat horizontally constructed, where one team may work on the database, while another team works on the user interface, and so forth. This lead to challenges around communication breakdown between teams and even delays in production or productivity due to one team potentially waiting for another team to finish before deploying a new feature. For instance, the database team may not have the knowledge of the what the front end team does, however, they will need to communicate with each other if the front end part of the application needs to retrieve data from the database. 

Entando's business capabilities architecture allows teams to be more vertical in the sense that each team is part of every step of the process. If one team is responsible for one feature component from start to finish, there is no waiting for another team to finish before developing, testing, or deploying that component. There's also a knowledge transfer aspect of teams working vertically. Developers, project owners, and business associates benefit from reduced communication barriers and deploy features faster. Project owners are able to leverage managing [vertical teams](https://marksamuel.com/2010/02/09/are-you-creating-horizontal-or-vertical-teams) as well, due to the efficiency of the vertical team workflow. Project owners and business associates benefit from vertical team members being unified and which the responsibility is focused on one feature at a time. 

Microservices and micro frontends promote the health of vertical teams by carving out small pieces of functionality that layer the backend with the frontend. See the visualization below:

![Vertical Teams](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/12/vertical-team.png)

Communication barriers are reduced drastically when it comes to the vertical team environment. Examining the image above, one vertical team is part of a feature development life cycle process from start to finish. This eliminates the need of communication between a divided frontend and backend team, or a backend and database team for example. Instead there is one team where every team member plays a part in the entire development process. In this type of team environment, knowledge is shared throughout the team where everyone has some experience and knowledge of each area of the application, whether it be the database, backend or the user interface. This style of team development promotes unity keeping every team member on the same page without confusion or misunderstandings. 

## Wrap Up

Are you still trying to expand your enterprise to a more loosely-coupled platform? Make sure to extend your production environment passed just microservices, and all the components of your application are scalable to evolve with customer needs. To add or discover more about micro frontends, check out: https://martinfowler.com/articles/micro-frontends.html. You can also learn about Entando at https://entando.com if you are interested in using a platform that utilizes micro frontends as one of its fundamental development elements. Stay caught up as technology changes and peruse the other [blogs](https://blog.ippon.tech) and [articles](https://medium.com/ippon-technologies-usa) Ippon has to offer.




 

 

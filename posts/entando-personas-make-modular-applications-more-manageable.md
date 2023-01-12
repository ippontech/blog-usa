---
authors:
- Ford Hatfield
tags: 
- Entando
- Micro-frontends
- Microservices
date: 2023-01-12T9:15:00:00.000Z
title: "Entando Personas Make Modular Applications More Manageable"
image: ![Multiple people working on a web application](https://github.com/fuhsaz/blog-usa/blob/master/images/2023/01/entndo_personas_blog_image.jpg)
---

Over the past few years, it has become apparent that businesses need to adapt quickly to changing conditions to stay competitive. Pushing a new feature's time-to-market due to another application part's unpreparedness is unacceptable. Building with a modular architecture in mind can give development teams more independence and potentially improve overall throughput. Unfortunately, modularizing applications can quickly become very complicated and difficult to manage.

## Enter Entando
Entando is an open-source Application Composition Platform (ACP) that enables companies to build better apps more quickly by facilitating modular application design. Developers and content creators can use the Entando ACP to design, build, and assemble applications from microservices, micro-frontends, and web content. Creating this infrastructure within a Kubernetes environment allows for scalability, availability, and resiliency.

There will always be a diverse set of skills on any given team. Some have UI/UX training. Others see the broader business goals and objectives. It is helpful to have a framework to help everyone get to where they need to be to do the most good. For this, Entando talks about Personas, which provide some guidance and structure to where someone's role may be while working on a composable application.

## What are Personas in Entando?
Entando defines the Four C's of Application Composition as Create, Curate, Compose, and Consume. Each has a "Persona", or a role that one or more people can fill to focus their strengths towards where they can best contribute. Creators may be more comfortable working in a high-code/traditional-build environment, whereas Composers use a low-code interface to assemble the application. Curators manage and catalog components and identify what may still be needed, and Consumers populate the application with up-to-date and approved web content. 

A single person or team may fit and act as more than one Persona at different times. Still, larger teams may wish to organize people by their technical or business specialties to parallelize the development process better. 

## Creators
Front-end and back-end developers likely find a Creator role similar to their experience. Creators are responsible for creating and packaging business capabilities, such as a micro-frontend and its associated microservice or a logo widget that can be reused in several of a company's applications, which can then be pushed to a central hub. A Creator can be a single developer, but there is room for design and business people in this role too! An entire development team consisting of design, development, testing, and business people can keep internally-created components consistent and aligned with business goals. 

Creators can quickly use the Entando Component Generator to generate the base code for new microservices and micro-frontends. The generator leverages [JHipster](https://www.jhipster.tech/) with [JHipster Blueprints](https://www.jhipster.tech/modules/extending-and-customizing/) and aims to accelerate development and standardize technology by allowing developers to quickly spin up new components with a consistent tech stack.

There are a few things that Creators can keep in mind to allow the Application Composition process to flow as smoothly as possible:
1. Loosely couple any components and fully map dependencies so that future developers can clearly understand any potential side effects when making changes.
2. Document the custom web components used to inject micro-frontends into your parent application. It can be helpful to keep track of all attributes, custom events, and CSS properties these components may require to funciton.
3. Take advantage of automation wherever possible. CI/CD pipelines save time, of course, but the Entando CLI has numerous features, such as boilerplate code generation and environment profiles, that can speed of development. 

## Curators
Created components can be pushed to a central hub, hosted by Entando or privately by a company. Here they can be accessed by all different arms of a company to keep branding, styling, and accessibility decisions consistent. Curators are responsible for managing this hub. They can set up categorization, log component inventory, and correct any gaps they find.

Curators also ensure that components are ready for the Composers on the team to use. They test for bugs and validate any business requirements. 

Good communication skills are essential for Curators because they bridge the gap between Creators and Composers. They moderate content but should avoid being a bottleneck. Taking an attitude of, "let's figure out what is wrong" will likely lead to better results than rejecting components and leaving it up to the other team to fix the issues and bring the piece back for re-review.

## Composers
Composers work in the low-code environment of the Entando App Builder to combine the different business capabilities provided by the Creators and made available by the Curators to create an application that fits the business' needs. 

A benefit of building your application in Entando is the Entando Hub, which contains numerous existing components that may fit your business case with little to no configuration required. The best code is often code your team does not need to write or maintain.

Entando uses Role-Based Access Controls (RBAC), which restrict individual pages or directories  to people with the knowledge and business need to access them. Favoring components with this built-in functionality, or building components around RBAC from the beginning, will allow you to make sure that all users have the right level of access to all parts of your application without much extra work.

## Consumers
Consumers are the final step in the Application Composition process. They add and update web content within the page templates provided by the Composers, then set up analytics to track how that content performs. 

Consumers are not necessarily restricted to managing content, however. They can create and manage users and roles, and edit rules and processes that run on the application. 
Finally, Consumers can analyze application traffic and tune settings in the Entando Operator configuration to maximize performance.

## In Conclusion
With Personas, Entando provides a useful framework for every member of a team, depending on their technical specialties and business knowledge, to quickly find where they would be most comfortable and able to contribute. Creators build components, Curators validate and organize them, Composers combine them, and Consumers fill in the details, keep content up to date, and respond to feedback. 

You can learn more about Entando by clicking [here](https://entando.com/), or more about JHipster [here.](https://www.jhipster.tech/) If you have any other questions, we would love to hear from you at [contact@ippon.tech](mailto:contact@ippon.tech). 


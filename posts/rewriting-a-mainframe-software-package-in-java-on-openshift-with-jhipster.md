---
authors:
- Richard Yhip
tags:
- JHipster
- Microservices
date: 
title: "Rewriting a Mainframe Software Package in Java on Openshift with JHipster"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/01/K-V-Laurikainen-1961-1.jpg
---

Ippon Technologies successfully helped transition a Federal Government Agency from a COBOL application to a Java Spring Boot Microservices back-end application and Angular front-end.  We were asked to pitch in because of our famed application generator, [JHipster](https://www.jhipster.tech/).  JHipster can be useful in certain situations such as:
* You are unfamiliar with the latest technologies and you would like to see a solid "best practices" implementation
* You need to accelerate application development and the target application to build is some form of a [CRUDL](https://en.wikipedia.org/wiki/Create,_read,_update_and_delete) application.  You might be asking what the "L" is for at the end of "CRUDL."  It stands for listing, or pagination, which JHipster can help you with.
* You have a large team of developers, with varying levels of expertise, and you need help standardizing development norms

In our particular case, we faced all of those challenges.  We also had a common problem that JHipster cannot solve that people performing migrations will: how do you extract every business rule and other pieces of functionality from the existing legacy system?

There is no great answer to this question, unfortunately.  There are tools out there that attempt to auto-migrate existing COBOL code to Java or another target language.  That can be fraught with its own set of [difficulties](https://compuware.com/converting-cobol-to-java/).  If you've ever worked with any code generation tool based on an existing source (I'm looking at you, WSDL), it can be a messy and confusing experience.

Another way of doing this is to hire developers who know COBOL and Java to perform the migration.  As the subset of developers who know COBOL and both a modern programming language, this is likely a *small* and *expensive* subset.  I feel as time goes on and companies realize the need to migrate the competition for those developers will be very high and the reality is that you may not end up finding those people.

Our approach was impressive given the following circumstances:
* Huge United States Federal Government agency
* A ratio of senior to junior developers of roughly 1:7
* A fixed time deliverable
* Little subject matter expertise from the development team on the business domain
* Security clearances needed for the entire team
* Because of in-process security clearances, no access to "real" or production data

The approach we used involved:
* Understanding what technology was available to us and what we could use within this Federal Agency's IT space
* Interactive modeling with the current application Subject Matter Experts (SMEs)
* Utilizing the out-of-the-box features of the JHipster application generator as often as possible to accelerate development

## Understanding what technology was available to us and what we could use within this Federal Agency's IT space
One major constraint we faced was being able to develop and deploy the application given the options we had within the Federal Government.  For example, there were many databases we could have chosen from, and if you change which one you use, there are changes that do need to be made to the application even if you use external configuration (YAML files with Spring) or an ORM like Hibernate with JPA.  You'll find things like column name character limits that work in some databases but not in Oracle.

Another constraint was the chosen application deployment platform.  We used the [OpenShift Container Platform](https://www.openshift.com/) which was a great learning experience and a pretty cool tool to use.  JHipster has a way to containerize applications to make it easy to deploy to Kubernetes, OpenShift, or any other container management solution.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/11/FlexTerm.png)

A significant curveball was how to display the front-end.  We would like to have used a modern UI with a modern CSS framework like Bootstrap, but due to the lack of training resources, we were to make the front-end look like and behave exactly like the existing "green screen" application.  Emulating a terminal application, on the web, in the year 2018!  Thankfully the flexibility and component reuse of Angular made this relatively easy.

## Interactive modeling with the current application Subject Matter Experts (SMEs)
One tool we used at the beginning of the process to build the application data model was [JHipster Domain Language Studio](https://start.jhipster.tech/jdl-studio/).  We didn't have access to fancy UML or other tools to develop our data model.  Furthermore, this was not as simple as looking at existing app code, a database, or an existing data model diagram.  The closest we got to that was a [Bachman diagram](https://en.wikipedia.org/wiki/Data_structure_diagram#Bachman_diagram) that the operations and maintenance team provided.  That helped but was not enough on its own to assist us.  We opened up JDL Studio, projected it on a screen, and quickly started iterating on the data model based on what we knew about the metadata surrounding the Bachman diagram, the SMEs, and knowledge about how modern relational databases work.

In my experience, this was a great way to "fail fast" and quickly build consensus.  The ability for JDL Studio to let us make live changes and see it on the screen saved much time and encouraged us to play around with the data model.  It's important to be flexible early on, because while you're not locked in, it can be more time consuming to make significant structural changes to your data model once it's generated.

That's another significant benefit of modeling in JDL Studio: the resultant artifact can be used to implement your entity data model in the application!  Collaborative development is an enormous timesaver, and you can generate an application off the bat with this to see visually if the data model makes sense, especially with relationships.  Did it not work out the way you thought it would?  Scrap it and try again.  The cost of failure is low at this point.

## Utilizing the out-of-the-box features of the JHipster application generator as often as possible to accelerate development
The JHipster application generator is one of the ways we were able to quickly bring a mostly junior staff of developers up to speed on enterprise application development.  I  caveat this by addressing a common misconception about JHipster: it will not magically turn new or junior developers into seniors overnight.  It doesn't replace the knowledge that years of development and running into issues give you.  Specifically:
* Clean code: JHipster strives to start that way by generating clean code, but developers have to maintain that for new functionality developed
* Java enterprise concepts: small CRUD apps are a different ballgame from the needs of enterprise-size companies.
* The Spring Framework: this is one of my favorite libraries I've ever worked with, but for new and junior developers this is a _daunting_ hill to climb to learn the concepts involved
* Angular: I've done most of my development in the days of multi-page apps (as opposed to today's single-page apps). Angular is probably one of the best SPA frameworks out there, but it has been described as "enterprise-y" and does take some studying to learn

That said, if you don't have a lot of mentors or seniors on your project you can't get a better teacher than JHipster.  As long as your developers are willing to learn, they can see enterprise software development best practices through that generated JHipster application.

Lastly, as the latest technologies are incorporated into the JHipster generator, they are available for use by your team.  Microservices were incorporated within the past few years, and we were able to use the generator to make an application that utilized a microservices architecture.  You have the choice to use a monolithic architecture (which has its merits), but due to our application's size, complexity, and deployment environment, we went with a microservices architecture.

## Results
I want to set the table that we used the Agile methodology with Scrum.  We utilized all of the usual ceremonies that come with Scrum and had two-week sprints.  The Scrum process gave us regular touchpoints with our customer to make sure that we were developing the applications to behave as close to the existing application as possible and that the behavior met business needs as close as possible.

We were able to develop 90% of the application within the tight timelines and roll that out to a subset of all production users to test.

Without JHipster and all of its components it would have been a much more difficult task to achieve.  It's not a silver bullet, for sure, but it is a powerful tool that enabled us to travel farther than I would have thought given the circumstances.

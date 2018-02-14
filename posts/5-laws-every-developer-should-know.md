---
authors:
- Raphael Brugier
categories:
- software craftsmanship
- design patterns
date: 2017-08-17T18:22:50.000Z
title: "5 laws every developer should know"
image: 
---

Laws - or principles - can give us guidance and teach us lessons from our peers’ mistakes. In this article, I will introduce you to five laws I always have in the back of my mind when designing or implementing a software. Some of them relate to pure development, some are related to system organizations. All of them should be useful for your growth as a software engineer.

# Murphy’s Law
> "If anything can go wrong, it will."

[This law](https://en.wikipedia.org/wiki/Murphy%27s_law) was coined by Edward Murphy - an aerospace engineer - in response to a failed rocket test in the early 50’s.
The idea captured in this law is to always create a defensive design for the critical parts of your system… because something will eventually go wrong at some point!

This law is easily translated to the software engineering field.
When you expose the software to the end-users, they will find creative ways to input something you had not planned and break the system. So you need to make your software is robust enough to detect and alert for unexpected behavior.

When you run the software on a machine, anything can break - from the disks supporting the OS to the data center’s electrical supply. So you need to make sure you have designed for failures at all levels of your architecture. 

I have had the chance to meet Murphy’s law several times already.
For example, I did not think using the default value “null” to represent null Strings in the batch framework I was using was harmful until someone actually named “Null” passed a trade order and broke our report chain for several hours…
Or, on another project, everything seemed ready to deploy the production environment until Azure had an infrastructure incident which took down the server we used to run the automation scripts. 
These real-world lessons reminded me the hard way that if anything can go wrong, it will.

So, always keep Murphy in mind and design robust software.

<p align="center">
![source: https://commons.wikimedia.org/wiki/File:Murphys-law.jpg](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/08/Murphys-law.jpg)
</p>

# Knuth’s Law
> “Premature optimization is the root of all evil (or at least most of it) in programming.”

[This law](https://en.wikiquote.org/wiki/Donald_Knuth#Quotes) - or I should say one of the most famous quotes from Donald Knuth - reminds us that you should never try to optimize the code of an application too early, or until it is actually necessary.

Indeed, a source code produced with simplicity and readability in mind will suffice for 99% of the performance needs and will greatly improve the maintainability of an application. Starting with a simpler solution will also make easier to iterate and improve when a performance problem arises.

Strings concatenation is often an example of a premature optimization for garbage collected languages. In Java or C#, Strings are immutable and we are taught to use other structures to build Strings dynamically, like a [StringBuilder](https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuilder.html). But in reality, until you have profiled the application, you don’t really know how many times a String is going to be created and what is the performance impact. So it often makes more sense to write it first with the clearest code possible and later optimize if necessary.

However, this rule should not prevent from learning the performance trade offs of your language and when to use the correct data structures.
And, like with every other performance problem, you should always *measure first* before starting to optimize anything.

<p align="center">
![source: http://www.si-units-explained.info/time/index_htm_files/3739.jpg](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/08/performance-timer.jpg)
</p>

# North’s Law

> “Every decision is a trade off.”

Ok, I admit this quote from one of Dan North’s talks - [Decisions, Decisions](https://www.youtube.com/watch?v=EauykEv_2iA) - is not (yet!) recognized as a law.
But this quote has had such an impact on the way I approach all my decisions I thought I should include it here.


In the day to day life of a developer, we make a ton of decisions - whether big or small - every day. From naming a variable to defining the architecture of a platform, through automating (or not) tasks. 


This quote emphasizes that whatever choice you are making, you are always giving up on something, one or more options.
But that’s not the most important.
The most important is to consciously make a decision and being aware of the other options and why you did not choose them. You should always thrive to make a decision by weighing the pros and cons based on what you know at that moment.
But it should also be fine to discover later that a decision you took was wrong if you new information comes to you after. The critical thing is to remember why you took the decision, reevaluate the new options and make a new conscious choice.


Again.
> "Every decision is a trade off."

So make choices and raise your awareness of your options.

<p align="center">
![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/08/balance.png)
</p>

# Conway’s Law
> “Organizations which design systems ... are constrained to produce designs which are copies of the communication structures of these organizations.”

In the 60s, an engineer named Melvin Conway noticed that how organizations are structured influences the design of the systems they produce.
He described this idea in a paper and the name stuck as “[Conway’s law](https://en.wikipedia.org/wiki/Conway%27s_law)”.


This law translates well into the software development world and is even reflected at the code level. The way teams are organized to deliver software components will directly influence the design of each component.
For example, a collocated team of developers will produce a monolithic application with coupled components. On the other hand, multiple distributed teams will produce multiple separated (micro) services with a clearer separation of concern for each. 
Neither design is good or bad, but they have both been influenced by the way the team(s) communicate.
Open source projects, with multiple individuals around the globe, are often great examples of modularity and reusable libraries.


Nowadays, the current trend is to break monolithic applications into micro-services. This is awesome and it will enable more velocity to deliver projects faster into production. But you should always keep in mind the Conway’s law and work as much on the organization of your company as on the technology choices.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/08/PreferFunctionalStaffOrganization.png)


# Law of triviality (Parkinson's law of triviality)
> “Members of an organization give disproportionate weight to trivial issues.”

The argument of [this law](https://en.wikipedia.org/wiki/Law_of_triviality) is that the time spent on any item of a meeting agenda is in inverse proportion to the sum of money involved.
Indeed, people have a tendency to give more attention to a subject they fully understand and have an opinion about than a complex problem.


Parkinson gives the example of a meeting during which a committee is reviewing two decisions: building a nuclear reactor for the company and building a bikeshed for the employees. Constructing a reactor is a vast and complicated task and people cannot grasp it entirely. Instead, they fully rely on their processes and system experts and quickly accept the project.
On the other hand, building a bikeshed is something that an average person can do and everyone can have an opinion on the color. In fact, every committee member will make sure to voice his opinion and the bikeshed decision will proportionally take way more time than the reactor’s.

This law has been popularized in the software world - and named after this story as the [bike-shed effect](https://en.wiktionary.org/wiki/bikeshedding).


Developers, for example, can spend more time discussing the correct indentation or naming of a function than actually discussing the responsibility of a class or the architecture of an application. That’s because, again, everyone can picture the effect of a few characters changes but it takes a bigger cognitive load to project an architecture change.

Another place where you will notice a lot of bikeshed effects are Scrum demos.
Don’t get me wrong, I love demos and I think that’s a great opportunity to face the users and get feedback on an application.
But often, the discussion during a Scrum demo will slip to cosmetic questions and specific behaviors instead of looking at the bigger picture. These discussions are also important but you have to be careful to balance these with the most important - and complicated - problems.


Once you know the pattern, you will start noticing this behavior in a lot of meetings and people interactions.
I am not telling you to cut every discussion about “small” problems, but raising your awareness will help you focus on the real problems and be better prepared for these meetings.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/08/pink-bikeshed.jpeg)

# Conclusion

These 5 laws are only a few examples of old lessons learned in our industry. There are many more to learn and discover when gaining more experience in the software development trenches.

Even if some of them may be seen as common-sense now, I strongly believe that knowing these principles will help you recognize patterns and react to them.


__Image sources:__

* [Murphy's law](https://commons.wikimedia.org/wiki/File:Murphys-law.jpg)
* [Knuth's law](http://www.si-units-explained.info/time/index_htm_files/3739.jpg)
* [Decision Decision](http://veterinaryleadershipinstitute.org/wp-content/uploads/2017/05/balance.png)
* [Microservices](https://martinfowler.com/articles/microservices.html)
* [Bike sheds](http://static1.squarespace.com/static/58594172893fc07d5b88dd15/585d17789a6bf7d18a957827/585d17939a6bf7d18a95791f/1482496938978/?format=1000w)

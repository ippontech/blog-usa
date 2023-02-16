---
authors: Lucas Ward
tags: entando, low code, pro code
title: "Where's the Pro Code? - A Comparison Between Developing with Low Code, Traditional Code and Pro Code"
image: 
---

![Low Code No Code Diagram](../images/2023/02/lowcodenocode.jpg) 

# "Where's the Pro Code?" - A Comparison Between Developing with Low Code, Traditional Code and Pro Code

In this blog, we want to tell you about what it's like to work with **Low Code** and **No Code** solutions as a developer. After we discuss the pros and cons of working in these types of platforms vs more traditional coding, we will introduce you to a concept called **Pro Code**. Let's start by covering some simple definitions.

# No Code and Low Code

These terms are somewhat interchangeable. Although some platforms do not require coding, these can also be considered SAAS (Software as a Service) applications. For most of this article, the term "Low Code" will be used, although some of the platforms we talk about may advertise a "No Code" approach.

Some example Low Code platforms include [Salesforce](https://www.salesforce.com/products/platform/low-code/), [TrackVia](https://trackvia.com/), [WordPress](https://wordpress.com/), [Wix.com](https://www.wix.com/), and [Zapier](https://zapier.com/). These platforms cover a wide range of intended use cases. Salesforce and TrackVia fall into the *Customer Relationship Management* and *Enterprise Resource Planning* categories, while WordPress and Wix.com are more akin to *website builders*. Then there are platforms like Zapier and Workato that offer drag-and-drop Integration Building, and fall firmly into the *API-management* category.

Although these platforms are different in implementing Low Code, they share a similar ethos. They allow business users to quickly and flexibly create websites, business processes, and integrations using a graphical UI and drag-and-drop technology. The Low Code part comes in different flavors for each.

## Low Code Customization

Low Code inside Salesforce and TrackVia allows developers, or tech-savvy business users, to add code snippets to different operations. In Salesforce, this typically comes in the form of **Apex code**; a strongly typed, object oriented programming language created by Parker Harris, one of the Salesforce founders. TrackVia utilizes Apache Groovy; an optionally typed dynamic programming language designed by James Strachan that compiles on the Java Virtual Machine. 

The code snippets can run scripts for different database operations. However, the end user probably isn't aware that these scripts are creating stored procedures with additional guardrails underneath. You could, for instance, tell a particular form, view or table to run a script before or after record insertion. Similarly, you could have a code snippet manipulate some data Before or After a record is updated or deleted.

## Customization Example

Let's take a look at an example of this type of customization. Let's say you have created a "table" in one of these platforms. This table stores home addresses. The table is set up to store the entire address in one field, but later it's decided the address should be split up by street, city, state and zip code. These fields / columns can be added to the table and a script written to "parse" the address into those fields. Using this "paradigm", applications built in Salesforce and TrackVia can achieve amazing levels of customization.

In other platforms, Low Code customization comes in the form of more code snippets and also plugins. So you end up with a bunch of opinionated code snippets plugins cobbled together doing things here and there and, in general, customizing the platform to fit your business processes and use cases.

## Limitations of Low Code

The prevalence of these types of platforms cannot be understated. They are hugely popular and widely used. There is, as always, a catch. Building your business-as-a-backend, meaning your business processes and procedures, on one of these platforms, inextricably links you to the platform. There is also a limit to the amount of customization adding stored procedures and plugins can provide. Not to mention the security concerns of who owns your code and who manages your IT.

Looking at the example about home addresses from the previous section, let's highlight some limitations. If, in addition to splitting the address up, you wanted to "look up" the address's geo-location using an external API, you may be in for some trouble. Salesforce and TrackVia do not support *reaching outside* the platform in their table scripts. Instead, you would need to leverage a third party api management tool (like Zapier) or depend on the platform to *add functionality*. Not only that, but once the functionality is available, it may not be in the exact way that you would have implemented it. 

For instance, an address lookup using a Google API via a third party API Management tool is going to "fire off" after the *record* has been added to the system. If a user has just created a home address and landed on the *view* of that record, they are not going to see the geo-location. In the background the API Management Tool is inserting the geolocation into the table. The user is now looking at a stale form. If they change something and press save they will now be overriding that update. This single problem alone hamstrings a ton of different customizations you may wish to achieve.

As a brief aside, TrackVia has indeed solved this issue by adding a *microservice* functionality. This functionality allows *after insert* and *after update* record transactions to hit external systems and makes the user wait on the executed function's return value. This way, they are not viewing a stale form after they interact with the data. 

## Low Code != Low Cost

Out the gate, it may seem like a cost effective solution to build on a low-code platform, but the hidden fees can really hold back a small to medium sized business. Often times, as the platform evolves and a business adapts more of the *premium* features, the cost goes up. Most of the pricing models are "per user, per month", meaning as your staff grows, so does the overall cost to operate on the platform. Lastly, when a business gets stuck and needs to utilize a platforms "Expert Support" team, the cost is often high. I have seen "Expert Support" teams charge upwards of $250 per hour just to listen to you tell them what the problem is, and then not be able to fix it. 

At the end of the day, there are going to be unachievable business endeavors. This is where more traditional development takes the prize.

# Traditional Development 

Building applications *the normal way* can look different depending on who you ask. The sheer number of different languages, patterns, frameworks, platforms, and paradigms is quite staggering. The cost to hire a development team and invest properly in their success is nothing to sneeze at. It is no wonder the Low Code / No Code platforms have become so popular. It's an easy sell to small and medium sized business, and even to some enterprises.

## A Tale of Success and Failure

![Drag and Drop Technology](../images/2023/02/draganddrop.jpg) 

I once had the amazing opportunity to build out the backend of a relatively small sized business using a Low Code platform combined with some other SAAS offerings. The deadlines were tight. The company had approximately 2 months to move off of a legacy platform. Wielding the power of drag and drop technology, we were able to completely recreate their existing systems and process, adding some really awesome improvements, and hit the deadline. 

Following this great success and enjoying building out one of my first systems, we just kept building. The system expanded and expanded and no stone was left unturned. As the data started to grow and the number of users increased, the need to "customize beyond the constraints of the platform" grew even larger. Inevitably, we started to build some of our processes entirely outside of the platform. As the development began to split, we realized the error of our ways.

Because of the amazing velocity we we're able to achieve using the low-code solutions, we had built too much, too fast. Although the company managed to pivot and figure things out before it all came crashing down, it wasn't without a large amount of frustration and "dialing back" our ambitions. If only there was a platform that could provide business users with the speed and flexibility of Low Code, but also allow developers to "escape the constraints" and customize to their hearts content.

# Introducing Pro Code

One company was brave enough to set out to build such a platform. A truly *Pro Code* platform that allows business users to build with drag and drop functionality and simultaneously lifts the curtain and exposes the underbelly of the beast. This company is called Entando and what they have created may be just what the doctor ordered. A *Pro Code* offering is one that combines the best parts of low code with the best parts of Traditional development, or high code. 

## Entando's Application Composition Platform

With Entando's Application Composition Platform, developers can build micro front-ends and microservices and launch them into a "Hub" to be assembled by business users. Need to run additional databases? No Problem. Want to use Angular instead of React, or both at the same time? Also, no problem. Entando utilizes the power of open source tools like Kubernetes and [JHipster](https://www.jhipster.tech/) to create a truly customizable Pro Code platform. Although the platform is not as easy to adopt into your business as something like Salesforce, all the pieces of the puzzle are there, and none of the training wheels.

## Opinionated in the Right Ways

People love low code because it provides a sense of security when building. The stress of choosing tech stacks and design patterns is largely abstracted away. If what you seek to build has any level of complexity, scale or security, those same abstractions become a thorn in your side. Entando fills a gap in the land between Traditional Code (High Code) and Low Code / No Code platforms.

The abstractions it creates are surrounding the things that are important to businesses. Scalability via kubernetes *abstracted*. Security via container management and deployment *abstracted*. Complexity via recommended architecture patterns, also *abstracted*. These are hard things to master when building software. For a platform to do some of the heavy lifting but still allow room to move around is a breath of fresh air.

## Mixed Teams with Clear Separation

Utilizing Entando's help, companies create interdependent teams *the right way*. To understand better the different Personas that Entando empowers employees to take, read up on them [here](https://blog.ippon.tech/entando-personas-make-modular-applications-more-manageable/).

I have seen some really scary things happen inside Low Code platforms. Some of them I created myself. Others were created by *too eager, non technical* business users writing code snippets when they really had no business or experience doing so. Sometimes this can be handled by properly setting up and using "roles" within the low code platforms, but more often than not, the wrong people are working on the wrong stuff. Entando allows developers to build software the way they are used to, with the tools that they like. At the same time, Business users can deploy that software in ways that makes sense to them, and that speaks better towards the business's goals. 

# Conclusion

Adopting a platform like Entando into your business can happen easier than you think. Due to the level of customization and flexibility, porting your existing applications over to work within the Entando ecosystem is not all that hard. You can read more about moving your existing applications to modular architectures [here](https://blog.ippon.tech/why-companies-are-moving-their-applications-to-modular-architecture/). 

If you want to learn more about Entando, check out the website [here.](https://entando.com/). If you need help implementing Entando in your organization, drop us a line at contact@ippon.tech. Ippon also has fully staffed practices that cover Data, Software, and Cloud. 


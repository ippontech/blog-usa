---
authors:
- Jonathan Scharf
tags:
- DevOps
date: 
title: "How do you measure DevOps success?"
image: TBD
---

As DevOps practices continue to grow, many organizations are looking to metrics as a way to both show the improvements their teams have made, as well as identify where there is room for improvement. But with so many different metrics, which should you focus on? Here are a few of the metrics I like to use when working with teams.


## Change, Failure Rate

Change failure rate is a way of measuring how often a change that goes to production leads to degraded service or even an outage. Having a low change failure rate is crucial and two fold. If the development teams are spending all of their time fixing production outages, they won’t have time to work on new features. In addition, if your customers can’t reliably use your product, they will likely look elsewhere for a product that meets their needs. 

One of the most important ways to limit failures is using automation for your deployments for both infrastructure and code changes. By using an automated and standardized deployment process in all of your environments, you should know very early on if your deployment will be successful. The added benefit here being that any changes to your environment are documented through code and are auditable if something does change.


## Defect Escape Rate

Defect escape rate is a measure of how often a bug in your code makes it to production. This can lead to unintended function and performance of your code and causes your customers to have a poor experience.  It could also take significant time for developers to go back and fix the bugs if other code has been built on top of it. 

One of the best ways to prevent defects from escaping is an expansive automation suite. Having unit, functional, and integration tests are a great way to ensure that your code is doing what you expect it to do. These should be run as part of your pipeline during every build and deployment. Many teams will also use a Test Driven Development (TDD) approach as a way to have unit tests cover all of the core functionality of their application.


## Deployment Frequency

It is reported that Amazon pushes code to production every 11.7 seconds. While not every company or project has the need to deploy that often, decreasing your time between deployments has many benefits. If you are doing development, you want to get those new features into the hands of your customers as soon as possible. This allows you to get an earlier return on investment of working on your code. The longer it sits waiting, the longer it will take for it to make you money.

Having the ability to do frequent deployments also allows you to remediate any vulnerabilities quickly. When the log4j vulnerability was released, many companies scrambled to push out fixes for their applications. Those with the ability to deploy more frequently were often able to push their updates faster than others, thus keeping themselves and their customers safer.

Improving your deployment frequency can be tricky since it is often affected by company policy. Having to go to a change approval board (CAB) meeting, or having to create a change order weeks in advance. The key here is working with all stakeholders to identify which parts of the process can be proven by automation instead of having to rely on a person reviewing. Being able to enforce quality gates in your pipeline allows everyone to be confident that the code being deployed has been thoroughly vetted and should be successful. Making or keeping the process unnecessarily difficult because “that’s the way its always been” only serves to deter deploying.


## Mean Time to Recovery

Mean time to recovery (MTTR) measures how long it takes you to bring your application back online after an outage. Despite your best efforts, outages will happen. Regardless of the cause, your next goal is to identify what caused the outage, prepare a fix, test it, and implement it. Does your team have the visibility and searchability of their logs they need to properly triage? Is their test automation built out enough to ensure that their fix won’t cause another, potentially more catastrophic, outage in the near future? Does your team know how to interface with support teams they may need help from? Ensuring that your team has the access to everything they may need during this time is crucial to restoring service.


## How to measure success?

Success is not a clear pass/fail scenario. It is a sliding scale with infinite room for improvement. This blog only touches on a few key metrics, but there are many more that can be used to track success. Applications all start in different places at the beginning of their DevOps journeys. Any improvement you can make in your process will provide value to your team and your organization. 

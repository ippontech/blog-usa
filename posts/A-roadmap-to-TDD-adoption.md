---
authors:
- Ben Scott
tags:
- Agile
- XP
- Testing
date:
title: "A roadmap to TDD adoption"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/08/TDD_Graphic.jpg
---

You've heard all about how Test Driven Development (TDD) will solve all your problems on a software project. Your defects go away, your time to market decreases, customer satisfaction goes through the roof. Everything is supposed to be awesome if your team just did TDD. However every time you try to adopt it, the adoption fails. Why?

# Test Driven Development

Let's just start with what is TDD. This practice states that for any code that you write you first start writing the unit test for it. This test should fail, then you write the code that will satisfy the test, if needed you refactor and finally you write the next failing test and start this cycle again.

The main reason for adoption is your IT manager or tech lead has read a blog or attended a talk on how TDD solves these main problems:

* Too many defects in production
* Long QA cycles
* New features break old features
* Devops adoption

# Traditional approach

The typical adoption pattern is to start with some classroom training on how to write unit tests, and on how to mock. You follow up with some coding katas where the team has to solve a common software problem by writing code using TDD (fizz-buzz, prime numbers, bowling score calculator, etc...). Once you've got a handle on those exercises you're supposed to take that back to your project and be able to develop 'real' code using TDD. This almost always fails. First, production code is much more complicated than exercises. Second, code that isn't written with testing in mind is ridiculously hard to convert to TDD. Finally, TDD isn't actually about testing and most teams focus on writing tests for test coverage and forget the most important part of the cycle. Refactoring.

TDD isn't as much about testing as it's about having testable code. If your code is easily testable, it's most likely modular and has followed good design principles. If you don't follow good design principles you'll find yourself in mocking hell where your test code becomes much more complicated than your production code, at which point you give up on unit testing and thus TDD fails. Check out [Giving up on TDD](https://blog.cleancoder.com/uncle-bob/2016/03/19/GivingUpOnTDD.html) by Robert Martin for more about how TDD and design are interlinked.

# An alternative approach

We have to all understand that TDD is an advanced coding practice. This is something that takes time to learn and requires you to have mastered some prerequisites.

First you'll need to want to write better code, this isn't something that can be forced upon you. To start your journey as a software craftsman familiarize yourself with [SOLID design principles](https://en.wikipedia.org/wiki/SOLID), and read the Clean Code book. Design is the single most important aspect of TDD. You need to design your production code, you need to design your test code and you need to design your interfaces so that they can be seamlessly used between production and test code.

Once you've got those basics you can start unit testing and refactoring your code so that the tests become cleaner and more robust. Note that we are still not doing TDD at this moment. It's important to be able to write tests after the fact first before we can really adopt TDD. There are a unique set of tools and skills in testing that need to be learned prior to able to do TDD effectively. Now would be a good time to enforce that any production code should be paired with its unit tests in the same commit.

At this point your team should be comfortable writing tests that you can start pushing for a switch to TDD. This is where TDD exercises are effective. The team already knows how to tests their code and how to design for testability. You can pair exercises with hand picked stories from your backlog that are especially well suited for TDD. One good example are bug fixes. Any production defect that need to be fixed should be replicated by an automated tests prior to fixing it. Not only does your team get to practice TDD but you also get the assurance that this defect won't be coming back.

# Summary

Here's my preferred way of getting teams to adopt TDD:

* Focus on design and clean code
* Learn how to write unit tests
* Enforce tests in the same commit as production code
* TDD exercises
* Complete well suited stories in TDD fashion
* Switch to TDD


I also found that not everyone is suited for TDD, some will adopt it and love it, others will hate it and won't adopt it no matter how much you try. I don't enforce TDD on my teams, I encourage it and provide a good foundation for them to succeed at it. Where I'm much more strict is committing your code with tests. If you write the tests in the same commit as your production code, and you commit at least daily you get most of the benefits of TDD.

---
authors:
- Ben Scott
tags:
- Agile
date: 2016-04-20T12:44:37.000Z
title: "Combatting software rot"
image: 
---

Software rot describes the decay at which a software project deteriorates over time. This can be experienced in various ways: reduced performance, increase in bugs and increased difficulty at pushing out new features. A multitude of project failures can be attributed to software rot.

If you’ve been a professional programmer for at least a couple years you’ve probably already experienced it first hand, have you ever tried to introduce a simple change that should have been a single method, only to realize that that you have to change multiple classes, none of which are covered by unit tests or acceptance tests? Have you ever spent a few hours just trying to understand code so poorly written that not even the original author can explain what it does?

As programmers, we tend to place the blame on management for unreasonable deadlines, and rush to implement features, this makes us short sighted and lazy programmers. We know very well the cost of badly designed code, we know the cost of unreadable code, yet we continue to produce it. We foolishly convince ourselves that once the deadline has been met, we will have time to clean up our mess, yet the deadlines never end. There is only one way to ensure continued success, we need to write good code from the beginning. We need the discipline to only committing great code.

In camping, boy scouts like to follow a rule: “Leave the campground cleaner than you found it”. In software engineering, we can adopt this rule by always improving the code base on each commit. Following this rule is the best way to combat software rot that is common in many projects. Improving the code base one commit at a time but with every commit is the only way to combat software rot.

### <span style="text-decoration: underline;">**Refactor for readability:**</span>

Readability is perhaps one of the most important factors of coding in large projects. A programmer reads more code than he writes. How well the developer understands how his feature fits in the current code base will determine the quality of said feature. Here are some rules to follow that can help you write better code:

####**Naming should reveal intent**

How you name a method, variable or a class should reveal why it exists and what it is used for.<span class="Apple-converted-space">  </span>A variable should be a descriptive known, avoid vague names or puns, too often we see variables such as:

==Original names ==
```language-java
//Variables
String str;
 int max;

//Methods
int convert(int i);
char evolve (char izard);

// Classes
class DocManager;
```

now refactored:

==Refactored names==
```language-java
// Descriptive variables
String username;
int maxNumberOfClientSessions;

// Descriptive Method:
int celsiusToFahrenheit(int temperature);
char toUpperCase(char lowerCase);

//Refactored the ambiguious Manager into 3
// classes with clear names and responsibilities
class DocumentFactory;
class DocumentReader;
class DocumentPDFExporter;
```

If you find yourself having a hard time naming a method or a class, then this is probably a sign that you are doing too much within the method or a class. Whatever you are trying to name should be refactored into multiple methods or classes.

There are some things that should be refactored as soon as you see them.

**Code duplication**:  Code duplication makes your code more complex, and if bugs exist inside of a duplicate code block, you have to ensure that you fix it in all of them. It also introduces confusion, if more than one method essentially does the same thing while looking different, then the programmer will spend extra time trying to differentiate them.

**Commented out code**: Commented out code should be deleted on sight. In all likelihood the code base has moved forward enough that the commented out code is now irrelevant. If we have ever have a need to reference it then you can bring it back using source control.

**Long methods**: Long methods are hard to read and often do more than they should. A good method should not be more than a few lines and should fit in its entirety on your screen. To shorten a method, extract a loop into a method with a name that describes the purpose of the loop, you can do the same with conditionals.

To learn more about refactoring for readability, I highly recommend reading Clean Code by [Martin Fowler](http://martinfowler.com/).

### <span style="text-decoration: underline;">**Refactor for testability:**</span>

A project covered in its entirety by automated tests is the holy grail of development. It’s what allows companies like Github or Amazon to deploy multiple times a day. The only way to be confident that your system hasn’t been broken by a new change is to verify it with automated unit, integration, and acceptance tests. Unfortunately it takes hard work and persistence to go from untested legacy code to being able to deploy multiple times a day. A good refactoring model to follow is the [SOLID principles](http://hackerchick.com/solid-code-with-emergent-design-part-1/) coupled with simple design. This means that you should refactor your classes to follow the SOLID principles, but only when new functionality would require changing those classes.

**Single responsibility principle**:  Your classes or modules should only be responsible for a single functionality of the system. This will lead to a clearer understanding of what needs to be done when changing a function. You will no longer create these unintended consequences that are so prevalent when adding a change to functionality.

**Open/Closed principle**: A class or module should be open to extension but closed to modifications. In practice this can be seen by interfaces that are locked, but their implementations can change. When a class interacts with another one, it should only do so through an interface.

**Liskov substitution principle**: This principle states that a subclass should implement all of its base type without changing its behavior. This allows you to substitute one type for another as long as they are derived without knowing their specific implementations. A classic violation is a square extending a rectangle, how would you implement the setWidth() and the setHeight() function of a rectangle within the square?

**Interface segregation**: You should not create big interfaces that cover a wide range of functionality. Instead you should divide it up into multiple small and focused interfaces that will allow the user to decide which portion he needs in his code.

**Dependency inversion**: In traditional OO design, we tend to make higher level modules depend on low level modules. This is bad design because if you need to change a low level module, then you need to introduce change in the high level modules as well. To solve this, we invert the dependency by ensuring that a high level module is not dependent on a low level module but instead both should depend on abstractions.

## Conclusion

One of the main benefits of following SOLID principles, is that it decouples your modules and allows them to be tested in isolation which is perfect for automated testing. As your dependencies should be abstracted through interfaces, you should be able to use mocks more effectively and create focused and robust testing. However achieving this will require dedication and persistence but it is well worth the effort.

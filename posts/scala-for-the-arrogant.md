---
authors:
- Randeep Walia
categories:
- Scala, Functional programming, algebraic design, domain design
date: 2018-06-01T06:25:27.000Z
title: "Scala For the Arrogant"
---
# What does it take to learn to Scala
 
To put it another way, what does it take to learn a language? For a Spanish speaker
learning Portuguese the process can be fairly straightforward: we need to
understand the differences in pronunciation, understand the critical
differences in vocabulary, and begin
the more difficult process of understanding the differences in grammar. But what
does the process look like for a French speaker trying to understand Sanskrit? Or
cooking Biryani?
 
OK- so the last one is not a traditional linguistic language but I mention to
emphasize the idea that sometimes learning a new language is not just a matter
of picking up a new grammar with a new vocabulary. Sometimes we are approaching
the issue of communication from a completely different perspective. I've seen
many restaurant kitchens in my travels where the owner and the staff do not speak the same
languages and yet they are able to convey recipes and tasks to another through the
universal principles that we all can understand.
 
For the arrogant Java developer, looking to learn Scala, it's easy to imagine
that the language can be learned by understanding the syntactical differences
between the two languages. If our understanding of Scala is that it is a simple
evolution of the Java language where we can avoid verbosity when declaring
variables and utilize some syntactic-sugar to make our code more compact, then
we might find ourselves quickly overwhelmed trying to learn the comprehensive
features of Scala. If we simply scan through a summary blog post (wait, is that
what this is?) with code examples, we might feel underwhelmed that the efforts
in learning Scala won't yield any meaningful results. The arrogant Developer has
years of experience and wants to learn the language **now!**. Why does it have
to be so difficult?
 
The answer lies in the intention of the Scala language. As its creator, Martin
Odersky [put it in an interview]
(https://www.signifytechnology.com/blog/2016/10/interview-with-scala-creator-martin-odersky),
> Scala was designed to show that a fusion of
functional and object-oriented programming is possible and practical." If we
understand this to be the motivation we can set the proper intention for our
study of Scala: when a developer learns Scala they are not just learning a new
language, but an approach to developing algorithms, data modeling, and design
with the most concise and efficient object-oriented and Functional design.
 
The arrogant developer might have years of OO design under their belt, they may
have memorized books of Design Patterns and might even know how to write some
functional expressions with Java 8's new Lambda and Streams features. But Scala
takes that to the next level with language features that support Algebraic Data
Types, implicit parameters, monads, and more. As someone who is going down the
beautiful rabbit hole of learning a new way of marrying functional with object-oriented
programming, I wanted to put together a syllabus for experienced developers who
want to learn a new language.
 
*This blog post is not a tutorial on how to learn
Scala*, that is a big task, but rather a
list of concepts to study to understand not just how to program in scala, but *why* to
do so in a specific manner. When approached this way you will learn many new concepts
and different ways of writing code, managing side effects and more. The arrogant
developer will probably find themselves being quickly humbled at the breadth of it all,
a testament to the language and its community of developers.
 
Let's get started!
 
# The path to learning Scala
1. Adopt the right mindset
2. (Re)learn functional programming
3. Apply these differences to your object-oriented principles
 
## Mindset
Humility, creativity, and discipline. Check your ego at the door and prepare to
realize that many of the concepts that make for a good scala developer could be
unfamiliar to you.
 
There have been many developments over the years in the world of functional programming
and if you have not been keeping up then many of this will be new ground.
 
1. **Scala is not "better Java"**: This is not [Kotlin] (https://kotlinlang.org/docs/reference/comparison-to-java.html) which attempts to erase some of the verbosity of the Java language and provide language features that facilitate better programming... although that is part of it. The primary goal of Scala is to facilitate Functional and Object-Oriented principles in a JVM based language.
2. **The Functional Programming Landscape is Complex**: There are many concepts that will most likely be unfamiliar to you. If you find yourself gravitating towards a resource like [*Scala for the Impatient*] (http://fileadmin.cs.lth.se/scala/scala-impatient.pdf) you might be skipping out on learning some of the functional paradigms that will help you on the larger journey of becoming a better programmer and taking full advantage of everything Scala has to offer.
 
## Functional programming
Functional programming has been described many different ways. I have always
considered it to be the creation and composition of **rules** to describe a
system.  In this context, rules are functions:
`f(x) = y`
for example, tells us that when we supply input x into the function we get y every time. From a mathematics
perspective that is great. But the astute computer scientist and the practical computer
engineer will observe that things are seldom so simple. If our function has to fetch
data from a database, how do we react to deadlocks? Can these rules work in a
multi-threaded environment with mutable objects?
 
### Pure Functions
Once you understand the concept of pure functions and what they are, you will
know a great deal of how Scala manages its functional state. At then end of it, you
should know:
- What pure functions are and how to create them.
- What mutation/mutability is, what problems they can cause, and how pure functions avoid
these problems
- What are **side-effects** in a function?
 
#### Tips for the Arrogant developer
*A side effect of worrying about side-effects can be sweaty palms and headaches. Scala tries
to mitigate these symptoms with built-in operators that help manage state when things go wrong
so don't get discouraged and start looking at how you can streamline your own functions and methods
to bring them closer to purity.*
 
### Algebraic Data Types
How smart will you appear at your next job interview if your start talking about
algebraic data types and no one else knows what you are talking about? Purity of
functions is great, but now let's talk about purity of our data types. When
you learn about ADT you should know:
- The two types of ADT: sum and product, and how each one is instantiated
- How Scala's **traits** and **case classes** relate to sums and products.
- Why immutability is important with ADTs and how Scala's ADT design favors
immutability.
 
#### Extra Credit
*What other languages on the JVM provide first-class support for ADT?*
 
### Managing side effects
Rare is the driver that will go through life without a speeding ticket. Similarly,
no Java developer will go through life without running into a few checked exceptions here and
there. Both are equally annoying and exhausting. In addition to wearing out keyboards,
the verbosity in writing out `try/catch/finally` logic does not play well with the
functional programming paradigms you were learning about in the previous sections.
 
Scala provides native types to facilitate things here that act as wrappers around
data similar to the way [Java's Optional]
(http://www.oracle.com/technetwork/articles/java/java8-optional-2175753.html) works.
 
- **Try** and how it can be used in situations where exceptions can occur.
- **Optional** and how it's implemented similarly as in Java (but integrated more
  tightly into the Scala API).
- **Future** and how it can be used with potentially higher-latency
  operations we want to manage asynchronously.
- **Either**, the red-headed step-child of Try and the problems that can occur in
trying to get this to compose (but why you still may need to use it).
 
### Monads
Everything you learned studying pure functions and ADTs leads directly into the
concept of monads. You will know you have a proper understanding of monads when
you are familiar with the three components of a monad:
1. How it's defined using a **trait** or **case class**.
2. How it's functionality is coded in its constructor for parameter `x` and it
returns its *possibility* of `y`.
3. The relevance of `flatMap` for accessing `y` and utilizing this monad as part
of an aggregate.
 
#### Side bar
Try reading *[What the heck is a monad?](http://khanlou.com/2015/09/what-the-heck-is-a-monad/),
because Monads aren't too difficult to understand, but can be kind of a nightmare to explain! Scala's implementation of monads can differ slightly from other implementations (Haskell, for example) adding to the confusion.*
 
## Object Oriented Programming
Good functional programming skills can have a profound influence on your OO development while the converse is not necessarily true.
Managing side effects makes your code cleaner, more concise, and easier to test. In addition, Scala offers several language features that make implementing certain patterns easier and more readable than Java. A Kotlin developer will probably recognize some of those concepts and Java itself is also trying to incorporate some of these (albeit slowly).
 
### Apply Method
See also: the `unapply` method for deriving an object from a parameter.
 
### Mutable variables vs the Copy method
While Scala tends to encourage immutability with some of its constructs and conventions, developers that are not yet ready to give up the ole setter method can still follow a traditional approach, while the native `copy` method provides a simple construct for creating cloned modified instances of existing objects.
- How **case classes** come with built-in support for `copy` and how updates can be communicated via copy.
- How `copy` works for complex objects with deep nesting. Also, how [lenses can be used](http://koff.io/posts/292173-lens-in-scala/) for trickier updates.
- How to use `var` to permit mutability of a field, and what are some of the valid use cases to consider this?
 
### Fine-grained access modifiers and sealed keyword Mixins
Java 9's modules are the most notable feature of this release and can find some symmetry in how scala allows for fine-grained control over access to a classes data.
- How Scala can restrict access to methods using **private** and **protected** and how protected differs from Java to Scala.
- How **private package** works and can be used to restrict access to a different package than the current class.
 
### Collections
How we manage Lists, Maps, Sets, and other data structures is always a huge part of any language and Scala is no different. Because it supports integration with Java, Scala's collections implementation is fairly similar to Java's implementation, but contains many enhancements that make working with it quite nice.
 
- How to distinguish mutable collections from immutable ones.
- One liners to declare, map, and filter a given collection.
- Using `zip` to combine two collections.
 
## Additional Topics
Operators
Lenses for immutable transformations on nested objects
 
# Course Materials
## Functional and Reactive Domain Modeling
## Essential Scala
## Scala with Cats
## Scala for the Impatient

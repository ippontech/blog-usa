---
authors:
- Dan Ferguson
tags:
- Java 8
- Java 11
date: 2019-01-14T14:06:00.000Z
title: "Comparing Java LTS Releases"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/01/MVP-1.png
---

# Introduction
The last 5 years have seen some rapid changes in the way new versions of the Java JDK is deployed and maintained.  Traditionally, new Java versions were always released in a 2 year life cycle.  Every 2 years, a new JDK would be released, containing features that emulated (and in some cases worked to replace) other popular languages at the time.  For example, Java 2, released in 1998, saw the addition of the `strictfp` keyword, which was essential in making the architecture independent Java language competitive with C and the verbosly specific floating point calculations it was used for.  Java 4, released in 2002, added regular expression libraries that behaved similarly to Perl, a language which began to see its usage decline in the 2000's.  The development of Java has been at a constant trajectory since 2014 with the release of Java 8.  Over the next 4 years, there were 3 new Java releases, effectively doubling the expected number of releases set by precedant.  In 2019, the fifth year since the release of Java 8, Java 12 is slated to be released.  As an application developer, this can be quite overwhelming.  How many new applications do you have to go back and update now?  As a systems engineer, this is a nightmare.  How many servers in your datacenter constantly need updating to the newest Java release?  Why is this happening in the first place?  If there are two things developers hate, one of them is unnecessary change.  I suggest the dramatic changes Java has undergone in the last 5 years have actually saved this language from going the way of COBOL and Fortran before it.

# The LTS Concept
It is absolutely crazy to think about keeping several applications or several hundred (thousand?) servers up to date with the newest Java release, especially when 5 have come out in as many years by the end of 2019.  That is why, when Oracle purchased Java from Sun in 2018, the concept of an LTS was established.  A Java LTS (long-term support) release is a version of Java that will remain the industry standard for several years.  To give you an example of this, Java 8 was released in 2014, it will continue to receive updates until 2020, and extended support will end by 2025.  This is a span of almost 10 years for a stable Java release to be considered an industry standard.  This gives plenty of OS vendors like Microsoft and Red Hat the time to repackage their releases with Java 8, time for application developers to update their applications to take full advantage of Java 8 features, and time for system administrators to update their data centers with Java 8 before a new version is released.  At this time, the only other Java version that is also an LTS version is Java 11, which was released in 2018.  The next release of Java, Java 12, is not slated to be an LTS release, and neither is 13.  If a trend is to be established from LTS releases vs. non-LTS releases, we can extrapolate the following: LTS releases should be upgraded to as they contain summary features which redefine how the language should be used, while non-LTS releases could be upgraded to as they refine some or all of the features introduced in the latest LTS release without impacting the language as a whole.  As you will see later in this article, Java 8 was an LTS release that introduced features that were perfected in Java 9 and Java 10.  Javas 9 and 10 were non-LTS releases that laid the ground work for new features would be introduced in the newest LTS, Java 11.

# New Release Features and Trends
Why have there been so many different releases of Java in recent years?  The answer lies in the fast-paced growth of development technologies across the board.  Java was born out of a need for applications to be architecture independent.  As consumer hardware slowly reaches a computing upper-limit, allowing web-based development and virtualization programs to be integral parts to the developer's workflow, the need to remain hardware agnostic has fallen to the wayside.  IDE's are being deployed with standard Android or iOS emulators, allowing developers to specify the architecture on which they intend to deploy their final application, regardless of the machine they are developing on.  Web-based IDEs like Cloud9 let you write and compile Java applications over the Internet.  The business need that birthed Java is no longer a requirement for businesses.  In fact, business requirements have changed.  

Application programming is a dying paradigm; functional programming is replacing application programming just like the cloud is replacing the data center.  Businesses no longer need a whole application with a CLI, or a GUI.  The technology world is moving to a place where we need to run a function on a stream of data and forward that result to some other application, without caring about where or how the function runs.  This requirement has seen the rise in popularity of Python and R, and the development of completely new languages like Clojure, Groovy, Rust and Swift.  How is a language like Java, which was made relevant because of how it handles hardware, supposed to stay relevant in a world without hardware?  If you take the last 4 releases (and the planned fifth) into consideration, it is easy to see why Java will remain a powerful tool for developers in a functional, serverless world.

# Java 8 LTS
The most powerful feature of Java 8 is undoubtedly the lambda expression.  Lambdas are not new to the programming world, they have been used in C-esque languages for many years now.  In fact, they could have been added to Java a long time ago, but the decision to keep them excluded was made on the grounds that lambda expressions have a tendency to be difficult to read.  Nevertheless, lambda expressions were making the lives of Python developers much easier, so they were a shoo-in for the version 8 Java release.  For those that are not familiar with lambda expressions, consider the following snippet of code
```java
for(String i : listOfStrings) {
  System.out.println(i);
}
```
With lambda expressions and the corresponding impact they have had on Java libraries and traditional iterative operations, you can condense the above snippet to:
```java
listOfStrings.forEach(i -> System.out.println(i));
```
At first, this feels like an insignificant win and, in truth, it is.  There is nothing more pretentious than a programmer touting how the turned three lines of code into one.  The real power behind lambda expressions comes with an explanation of functional interfaces, another feature introduced in Java 8.  A functional interface is just like a regular interface, except you define the body of the function specified in said interface anonymously, using lambda expressions.  Consider the following:
```java
interface FormValidator{
  boolean validate(String inputField);
}

public static void main(String[] args) {
  FormValidator isEmail = (s) -> (s.contains("@")) == 0;
  FormValidator isPhoneNumber = (s) -> (s.length()) == 9;
  FormValidator isGender = (s) -> (s.equalsIgnoreCase("M") || s.equalsIgnoreCase("F")) == 0;
  
  String email = args[0];
  String phoneNumber = args[1];
  String gender = args[2];
  
  boolean properEmail = isEmail.validate(email);
  boolean properPhone = isPhoneNumber.validate(phoneNumber);
  boolean properGender = isGender.validate(gender);
  
  if(properEmail && properPhone && properGender){
    System.out.println("Form Validated.");
  }
}
```
And just like that, we've defined three unique implementations for a form validation interface, without having to define three implementing classes.  Each method implementation contributes to the application in the same way, but operates differently based on the specific implementation.  Lambda expressions do not have to be so succinct as they are in the above snippet.  They can be multiline method bodies that return values.  You can even pass a functional interface as a parameter to another method, each time defining a new lambda expression to modify the behavior of that functional interface as a parameter.  For more information on this, check out this [well-written post](https://medium.freecodecamp.org/learn-these-4-things-and-working-with-lambda-expressions-b0ab36e0fffc) on Medium.  

To wrap up Java 8, it is worth mentioning default interfaces, type-based annotations, Optional objects, and Stream collections.  

Default interfaces are nothing more than interfaces whose method bodies have a default implementation.  This eliminates the need for abstract parent classes which define default behaviour for the interface, allowing all child classes to defer to `super`.  Now abstract parent classes will exist if and only if there is a good reason for them, keeping code neat, organized and easy to read.

Type annotations are annotations that are specified at the field level.  Type annotations are very popular in the Hibernate and JPA libraries, where you can define database relationships between two records on a foreign key.  

Optional objects take can be assigned null values without throwing NullPointerExceptions.  These objects do much in the way of cleaning boiler plate code.  Instead of checking if an object is equal to `null`, you can use an Optional type which contain constructs like `Optional.empty` or `Optional.isPresent` to replace your constant `null` checking.

Streams are Collections that have been modified for functional use.  Consider the List collection, typically a LinkedList or an ArrayList data structure.  These data structures were designed for traditional CRUD operations in a very object-oriented way.  But what happens when you need to iterate over all of elements in a list that meet a criteria, perform some chain of operations against those elements, and forward those elements on to a new list object?  You could spend a lot of time working on a "helper object" that is designed specifically to modify all of your list objects in your applciation to accomplish this, or you could use stream operations.  Streams have been integrated into many of the native Java data structures (just like many lambda functions) allowing them to be easily pulled from pre-Java 8 code.  Consider the following example pulled from [this excellent resource on the Streams API](https://winterbe.com/posts/2014/07/31/java8-stream-tutorial-examples/)
```java
List<String> myList = Arrays.asList("a1", "a2", "b1", "c2", "c1");
Stream<String> myStream = myList.stream();

myStream()
    .filter(s -> s.startsWith("c"))
    .map(String::toUpperCase)
    .sorted()
    .forEach(System.out::println);

// C1
// C2
```
We create a List object and from that List we create a Stream.  The filter method takes a lambda that pulls all elements out of the stream which start with a "c".  The map method passes the method reference for the `toUpposerCase` method, which is part of the String object, and applies it to each element in the Stream at this time (which is just c2 and c1).  The sorted method sorts the elements in the stream in alpha-numeric order (resulting in C1 and C2).  Finally the forEach method takes another method references which returns the type `void` and applies it to the remaining members of the stream, thus printing the strings "C1" and "C2".  Streams are another departure from traditional Java, much like lambda expressions.  They are very functional tools that cannot be re-used after they a terminating operation has been called on it (i.e. any method which returns `void`, in the above case the forEach method).  However, they are invaluable tools for parsing large data sets.  Whole classes designed to process large data structures can be reduced to a few lines of code using streams.

# Java 9 Non-LTS
Java 9 improved on some of the features introduced in Java 8, but not to such a degree that required it be an LTS release.  Some of the features like private interface methods, immutable sets defined in one-line, a new Garbage Collector, a new HTTP Client, and enhancements made to the `@Deprecated` annotation did not really do much to move the language forward.  These features deserve to be mentioned, but they did not shake up the Java development world the way functional interfaces and lambda expressions did for Java 8.  That being said, Java 9 is not without its bells and whistles.

The jshell command line tool was introduced in Java 9.  This tool, allowed users to flesh out Java code in the command line without having to set up a brand-new Java project with the inescapabale `public static void main(String[] args)` method.  Developers could now write Java code in the command line at will.  This brought Java one step closer to Python; Python always had a CLI component and now so too did Java.  The jshell is a whole topic on its own, and it exceeds the scope of this blog post.  For more information on this tool though, check out the [Oracle documentation on it.](https://docs.oracle.com/javase/9/tools/jshell.htm#JSWOR-GUID-C337353B-074A-431C-993F-60C226163F00)

Another component to Java 9 that is worth mentioning is the introduction of modules.  Modules introduce a re-organization of Java APIs, by grouping common libraries and specifying groups of dependent libraries.  These groups create modules, and they define an entirely new way to build applications.  Using the `javac` compiler, you can specify a `module-info.java` along with any relevant class files.  What results is a module that you can run in the command line via the `java` command.  You no longer need to compile JAR files or memorize the different program entry points you have in your JAR or your manifest file.  For example, given the class `HelloWorld` belonging to package `com.mydomain`, you can write a `module-info.java` file containing the line `module com.mydomain {}`.  Then with the following command, you can compile a java module
```bash
javac -d mymodule/myproject src/myproject/module-info.java src/myproject/com/mydomain/HelloWorld.java
java --module-path mymodule -m myproject/com.mydomain.HelloWorld
```
Modules belong to a wider initiative known as Project Jigsaw, which again extends beyond the scope of this blog post.  For information on modules, how they work, and Project Jigsaw, [check out this article](http://openjdk.java.net/projects/jigsaw/quick-start)

# Java 10 Non-LTS
Java 9 was the release that re-defined how Java programs could be organized and developed.  Java 10 was the release that built upon the foundation laid in Java 9.  The most subtle, but most important feature of this release were the performance enhancements introduced to the jshell tool.  The jshell tool was a powerful addition in Java 9, but it took awhile for it to get going.  Java 10 kicked it into gear a lot faster than Java 9 did, introducing performance enhancements that made the tool more desirable to use for developers.  

Additionally, Java 10 improved upon Java 9's new Garbage Collector, the Garbage-First Garbage Collector.  Java 9's G1GC changed the way Java performed garbage collecting by partitioning its heap into multiple small heaps, whereas traditionally the heap would be split into three chunks.  G1GC also introduced multi-threaded processing on the garbage collector for all events save a "stop-the world," a point in time during garbage collection where program execution halts and gives the garbage collector time to assess the state of the application.  "Stop-the-world" events are unaviodable in garbage collection.  Java 10 took the multi-threaded approach perfected by Java 9 and applied it to the "stop-the-world" event, thus making the G1GC fully concurrent.  Even though the G1GC is fully concurrent and therefore faster than prior garbage collectors on average, it is worth noting it takes a larger memory footprint because of the way it chops the heap into more chunks.

The big language feature introduced in Java 10 is the `var` keyword.  The `var` keyword intoduces implicitly typed variables to Java.  The concept of the implicit type has been around forever; Python and Perl use implicit typing out of the box, as does C# (with reflection or the `var` keyword).  Java has always traditionally been a statically typed language; the closest to dynamic typing with Java you could get before version 10 was with Generic classes and runtime casting.  With the introduction of the `var` keyword though, we can infer an object's type at compile time, making code easier to read.  That being said, I would caution excited fans of dynamically typed languages to use `var` only when necessary.  The following is a perfect example:
```java
MyFakeMongoDBUtilityClass.StaticInnerClassOfMyFakeClass<FakeKey, FakeValue> myStaticallyTypedObject = new MyFakeMongoDBUtilityClass.StaticInnerClassOfMyFakeClass<FakeKey, FakeValue>();
var myDynamicallyTypedObject = new MyFakeMongoDBUtilityClass.StaticInnerClassOfMyFakeClass<FakeKey, FakeValue>();
```
As you can see, there are obvious readibility use cases for the `var` keyword.  However, this keyword comes with a few caveats.  For example, `var` cannot be used to assign a type to class-level variables, method parameters, or method return types.  These values need to be known at all times, otherwise your application will not compile.  Furthermore, you should not use `var` when instantiating a variable that holds a value returned by a method.  Doing so prevents your code from being readable.  For example:
```java
// Do this
MyFakeMongoDBUtilityClass.StaticInnerClassOfMyFakeClass<FakeKey, FakeValue> myMethodReturnObject = someMethod();
// Do NOT do this
var myMethodReturnObjectOfUnknownType = someMethod();
```
One thing to note about `var` is it is not reserved.  This means you could feasibly have a situation like `var var`.  This feature is designed to protect the lazy programmers out there who have created variables named "var."

# Java 11 LTS
Java 11, the most recently released Java version, is the second LTS Java version released by Oracle.  This version continued where Java 10 and 9 left off.  Modifications were made to the ever popular `var` keyword, allowing them to be used within lambda expressions.  This means you can now have variables within your lambda expression that are locally-scoped to that lambda expression only.  This is just another feather-in-the-cap for lambdas; the integration of functional paradigms and dynamically typed objects shows a real embrace of modern programming styles.  Another great feature of Java 11 is the single line, single-file build.  You can now run the following without having to compile an entire application.
```java
java HelloWorld.java
```
If you want to test something out quickly, you could mock up your application using `jshell`.  Then you can take the commands you painstakingly tested and copy/paste them into a `poc.java` file.  This can be attached into an email for your co-worker and forwarded to them.  They can then run the application in one line and, assuming you are using native libraries only, the application will run perfectly.  This is truly a big step for Java; Oracle has worked tirelessly to make Java a language that can stand the test of the modern programming paradigm.  

Languages like Python have always been used to write enterprise level applications, just like Java and C++.  However, Python has always had an edge in that it can be used in a highly functional, almost ad-hoc way.  Until Java 9, this was impossible.  Now with Java 11 introducing single-file compilation under an LTS release with lambda functions that have locally scoped, dynamically typed variables, companies can almost replace their Python scripts with Java scripts.  This is a huge win for Java.  Take for example the machine learning world.  

Every tool used to automate model training and generation will generate a reference to the generated model as either a python script or a POJO (plain-old Java object).  The Python machine learning shops out there had a realy easy time testing deploying their model, they just drag and drop the file to the location where there application stack calls the model and it was deployed.  The Java machine learning shops would have to take the POJO and integrate it into their application, re-compile everything, and re-start the Java process running on their ML machine.  For some production environments, this may only be allowed on a weekend with the coordination of multiple teams.  Now, with Java 11 and single-file compile, deploying a new model in Java is as easy as deploying a new model in Python has always been.

# Future Directions and World Domination
Java has had quite the glow-up in the last 5 years.  Oracle took a language that was on the cusp of becoming one of the "old-school" languages like C or Fortran, and re-made it in the image of languages that are succeeding today for modern use-cases.  The great thing about Java used to be it was easy to learn.  There are no function pointers, there is only one type of casting, no multiple inheritance, you could run Java on any platform, and you do not need to know how to use any command line tools because your IDE takes care of most of it.  If you are a novice, the command line in your Eclipse IDE is how you interact with the application.  If you were curious, you built a JApplet.  If you were an enterprise developer, you built a REST interface underneath some HTML you copy/pasted off the web.  This worked for awhile, until our data sets got bigger, our databases all became columnar, our front-ends were stand-alone products like Grafana, and we became more interested in our future results over our current results.  How does a language like Java stand a chance in a world where the function of the object outweighs the object itself?  The short answer is, Java needed to be reinvented.  Now we have a robust object-oriented language that can easily fill the role of a functional scripting language.  By trimming the fat inherent to every Java application, Java once again becomes an accessible language.  We no longer need 20 lines of import statements, we can just make a module.  We no longer need to compile thousands of lines of code to see if one crucial piece works as expected, we can just use `jshell`.  We no longer have long-winded anonymous classes that define highly specific types (yes I am talking about you Apache HTTP Client), we have lambda expressions and dynamic types.  We no longer have helper classes designed to troll through your data structures for entries that meet specific criteria, we have the Streams API.  Pretty soon, [the iconic POJO may be replaced with a concept called a `record` in Java 12](https://dzone.com/articles/interesting-jdk-12-features-to-watch-out-for).  This initiative by Oracle to keep Java modern ensures it will be used for many more years to come.  

I only hope the folks at Oracle acquire C++ next and give it the same treatment.

---
authors:
- Chris Anatalio
tags:
date: 2015-10-27T20:33:00.000Z
title: "Java 9 and Project Jigsaw - JavaOne Day 2"
image: 
---

# Ippon USA at JavaOne(Day 2): Focus on Java 9 and Project Jigsaw

 Today’s highlights include a 4-part session on the Java 9 module system and Project Jigsaw presented by Alan Bateman, [Mark Reinhold](https://twitter.com/mreinhold), Chief Architect, Java Platform Group Oracle and Alexander Buckley and a hosted afterparty at Duke’s Cafe outside the Hilton with refreshments provided by [Atlassian](https://www.atlassian.com/).

![[Hotels Exterior Design](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/collage-hotel-ext.jpg)

- - - - - -

## Sessions Summary

CON5107: Prepare for JDK 9
CONS5118: Intro to Modular Development
CON6821: Advanced Modular Development
CON6823: Project Jigsaw: Under the Hood

### Getting Ready for Java 9

Java 9 will contain many disruptive changes.  Tooling will definitely need time to catch up and implement changes and many libraries and codebases may need changes as well to upgrade safety and successfully.
 It will be a long road to migrating the Java Ecosystem to modules. Potentially we could be looking at a 5-10 year timeframe. However, it is very important to the future of Java that we as a community implement these changes.

<blockquote class="twitter-tweet" data-lang="en"><p lang="en" dir="ltr">How important is the new module system in <a href="https://twitter.com/hashtag/JDK9?src=hash">#JDK9</a> <a href="https://twitter.com/hashtag/javaone?src=hash">#javaone</a> <a href="https://twitter.com/hashtag/JavaOne2015?src=hash">#JavaOne2015</a> <a href="https://t.co/HwVHU6UQjJ">pic.twitter.com/HwVHU6UQjJ</a></p>&mdash; Ippon USA (@IpponUSA) <a href="https://twitter.com/IpponUSA/status/659043428081111040">October 27, 2015</a></blockquote>
<script async src="//platform.twitter.com/widgets.js" charset="utf-8"></script>

**Steps to take to Prepare for Migrating to JDK9**

- Check for JDK-internal APIs with jdeps
- Check code that relies on version string structure
- Check tools that rely on rt.jar, tools.jar or the old file layout

**Check out the following:**

- [OpenJDK project Jigsaw Page](http://openjdk.java.net/projects/jigsaw)
- [Early Access Builds](https://jdk9.java.net/jigsaw/)
- [jdeps analysis tool](https://wiki.openjdk.java.net/display/JDK8/Java+Dependency+Analysis+Tool)
- [jdeps](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jdeps.html)
- [Maven jdeps plugin](https://github.com/marschall/jdeps-maven-plugin)

Livestream of sessions [here](http://j.mp/1LScfnH).

- - - - - -

### When is JDK9 Coming out?

Short answer:  *Fall 2016*

**Full Release Schedule**

- 2015/12/10: Feature Complete
- 2016/02/04: All Tests Run
- 2016/02/25: Rampdown Start
- 2016/04/21: Zero Bug Bounce
- 2016/06/16: Rampdown Phase 2
- 2016/07/21: Final Release Candidate
- 2016/09/22: General Availability

- - - - - -

### Why upgrade to JDK9

1. More flexible and scalable code
2. Better security and maintainability
3. Easier to create, maintain and deploy
4. Improved performance
5. More efficient way of storing classes
6. Simple and more intuitive layout

<blockquote class="twitter-tweet" data-lang="en"><p lang="en" dir="ltr">How important is the new module system in <a href="https://twitter.com/hashtag/JDK9?src=hash">#JDK9</a> <a href="https://twitter.com/hashtag/javaone?src=hash">#javaone</a> <a href="https://twitter.com/hashtag/JavaOne2015?src=hash">#JavaOne2015</a> <a href="https://t.co/HwVHU6UQjJ">pic.twitter.com/HwVHU6UQjJ</a></p>&mdash; Ippon USA (@IpponUSA) <a href="https://twitter.com/IpponUSA/status/659043428081111040">October 27, 2015</a></blockquote>
<script async src="//platform.twitter.com/widgets.js" charset="utf-8"></script>

- - - - - -

###

### What is Changing in JDK9

**[JSR-376 – Java Platform Module System](https://www.jcp.org/en/jsr/detail?id=376)**

*OpenJDK Project Jigsaw – Ref implementation for JSR-376*

- Encapsulate most(Non-critical) JDK-internal APIs
- Encapsulate critical APIs that have replacements in JDK9
- Critical APIs without replacements: Depreciate in JDK 9 and remove in JDK 10

**Non-critical Internal APIs:** No evidence of use

**Critical Internal APIs**

- Functionality that is difficult/impossible to implement outside JDK
- Examples:  Unsafe, Signal, Cleaner, Reflection::getCallerClass, ReflectionFactory

**[JEP 261: Module System](http://openjdk.java.net/jeps/261)**

- Upgrade module mechanism
- Application and extension class loaders will no longer URLClassLoader instance

**Binary Structure of JRE and JDK**

Only 3 folders will be present in the new file structure:

- bin
- conf
- lib

The following files will be removed:

- jre directory
- rt.jar
- tools.jar

**New version string format**

*[JEP223 New version-string scheme](http://openjdk.java.net/jeps/223)*

Example: *1.9.0_5-b20*

- - - - - -

## More about the Module System

### What is a module?

**Module:**  A *module* is a named, self-describing collection of code and data

Module Characteristics

- Strong encapsulation
- Reliable Dependencies

**Module Path:**  Module system version of classpath

Example:  java -modulepath dir1:dir2:dir3

***module-info.java***

![Example Module](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/10/module-ss-1.png)

- All modules have an Implied dependency to java.base
- Module names, like package names, must not conflict
- A module’s declaration does not include a version string

**Example Modular JAR**

A modular JAR file is like an ordinary JAR file in all possible ways, except that it also includes a module-info.class file in its root directory

![Module Artifact Example](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/10/module-artifact-example.png)

**Packaging as modular JAR**

app.jar

- module-info.class
- com/foo/app/Main.class
- … other classes

*jar —create —file mlib/app.jar*

*jar —file mlib/app.jar -p*

**Name**

com.foo.app

**Requires**

com.foo.bar

java.base -> MANDATED

java.sql

**Main Class**

com.foo.app.Main

**Example Compilations**

![Module Example Compilations](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/10/module-example-compilations.png)

**Module Graph Example**

Each module with a dependence that is fulfilled by some other module, contains a directed edge from the first module to the second.

![Module Graph Example](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/10/module-graph-example.png)

More info [here](http://openjdk.java.net/projects/jigsaw/spec/sotms/).

- - - - - -

## Application Migration(Very simplified top-down version)

### Basic Process

1. Use jdeps to find dependencies
2. Build modular JARs for your code
3. Create automatic modules by putting external libraries on module path

**Automatic modules:  **Place JAR files on module path to create automatic modules.  The module name will be derived from the JAR file name.  It will export all it’s packages and require all other modules.

### Some things to keep in mind

- Adopt modules at your own pace
- Your can modularize your application before its libraries are modularized
- Library authors can modularize libraries independently

- - - - - -

## Under the Hood of Jigsaw:  Some additional Details

### How Accessibility Works Now

public

protected

<package>

private

### How Accessibility will Work with JDK9 and Onward

**public to everyone:**  *exports public*

**public but only to specific modules:**  *exports to*

**public only within a module:**  *exports*

protected

<package>

private

**Notes**

- In JDK9 accessibility strongly encapsulates module internals
- Accessibility relies on readability

### Different kinds of modules

**Explicit Named modules – **Created modules with explicitly defined requirements and exported types.

**Unnamed modules:  **An unnamed module reads every other module, and it exports all of its packages to every other module

- JARs on the class path become unnamed modules
- You can treat everything on the classpath as one big unnamed module
- Unnamed modules read every named module
- Unnamed modules export all packages

**Automatic Named Modules** – Named module declared implicitly by placing unnamed module on module path

- Export all packages
- Reads all named modules

### Loaders and Layers

Module System works with existing loaders

- Boot Layer
- Bootstrap
- Extension
- Application

**Layer:**  A layer encapsulates a module graph and a mapping from each module in that graph to a class loader.  i.e. Family of loaders

- Graph of modules
- Function that maps modules to loaders

**Boot Layer:**  created by the Java virtual machine at startup by resolving the application’s initial module against the observable modules built-in to the run-time environment and also against those found on the module path

**Other Things to Keep in Mind**

- Modules do a better job of encapsulation than class loaders, but class loaders are still necessary
- Layers control the relationship
- Strong encapsulation of modules by the compiler uses VM Core reflection
- Named and Automatic Modules help with migration

- - - - - -

## Fun stuff at Parc 55

There are lots of fun things scattered around the convention to check out in your downtime between sessions and checking out the exhibition hall.

### **Make your own magazine cover**

On the 4th floor of the Parc 55 hotel by the Java One store, there is a large glass box you can pose in to shoot your own magazine cover.

![Magazine Cover](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/10/magazine-cover.jpg)

### **Free coffee at Buzz House(Free beer after 2)**

![buzz house](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/10/IMG_20151026_094911010-576x1024.jpg)

### **20 years of Innovation and History with Java Exhibit**

![Java 20 Year Exhibit](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/10/java-20-years-1024x338.jpg)

- - - - - -

## Duke’s Cafe Afterparty

Big thanks to [Atlassian](https://www.atlassian.com/) for providing free beer and coffee all day at JavaOne Buzzhouse locations and Duke’s Cafe.  Follow [Atlassian on Twitter](https://twitter.com/Atlassian).

![Duke's Cafe Free Beer](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/10/free-beer-dukes.jpg)

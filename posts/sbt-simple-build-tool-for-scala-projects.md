---
authors:
- Raphael Brugier
categories:
- sbt
- Scala
date: 2016-06-01T11:02:54.000Z
title: "SBT - simple build tool for Scala projects"
image: 
---

SBT is the favorite build tool for Scala developers, it is used by the Play framework and by the Activator module from Typesafe.

SBT is easy to get started with, offers a wide range of features and is extremely easy to extend. It is the perfect mix between Maven and Ant, but using a Scala DSL instead of XML.

In this post, we will introduce SBT and the key-value model behind it. We will also see how to use it for dependency management.

## Setup

<span style="font-weight: 400;">To install SBT locally, just follow the instructions on </span>[<span style="font-weight: 400;">this page</span>](http://www.scala-sbt.org/release/tutorial/Setup.html)<span style="font-weight: 400;">.</span>

## Directory layout

<span style="font-weight: 400;">Even with a fresh setup of the binary, you can already compile without any SBT configuration file.</span>

- All you have to do is create a class, java or scala, in an empty directory

```language-java
public class Hello {
  public static void main(String[] args){
    System.out.println("Hello");
  }
}
```

- run: `sbt compile`

```language-bash
$ sbt compile
[info] Compiling 1 Java source to /Users/jpbunaz/workspace/tutorial/sbt/blog/target/scala-2.10/classes…
```

- run: `sbt run`:

```language-bash
$ sbt run
[info] Running Hello
Hello.
```

<span style="font-weight: 400;">SBT follows the </span>*<span style="font-weight: 400;">convention over configuration</span>* principle<span style="font-weight: 400;">, and compiles any class in the root directory where it is executed. The </span>*<span style="font-weight: 400;">run</span>*<span style="font-weight: 400;"> command also looks for any executable class to launch.</span>

<span style="font-weight: 400;">For the directory layout, SBT follows Maven’s [standard directory layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html):</span>

<table style="width: 300px; border-collapse: collapse;border:1px solid #000000;"><tbody><tr style="border:1px solid #000000;"><td style="text-align: left;border:1px solid #000000;">src</td><td style="border:1px solid #000000;"></td></tr><tr style="border:1px solid #000000;"><td style="text-align: left;border:1px solid #000000;">&nbsp;&nbsp;main</td><td style="border:1px solid #000000;"></td></tr><tr style="border:1px solid #000000;"><td style="text-align: left;border:1px solid #000000;">&nbsp;&nbsp;java</td><td style="text-align: left;border:1px solid #000000;">Java sources</td></tr><tr style="border:1px solid #000000;"><td style="text-align: left;border:1px solid #000000;">&nbsp;&nbsp;resources</td><td style="text-align: left;border:1px solid #000000;">Application resources</td></tr><tr style="border:1px solid #000000;"><td style="text-align: left;border:1px solid #000000;">&nbsp;&nbsp;scala</td><td style="text-align: left;border:1px solid #000000;">Scala sources</td></tr><tr style="border:1px solid #000000;"><td style="text-align: left;border:1px solid #000000;">test</td><td style="border:1px solid #000000;"></td></tr><tr style="border:1px solid #000000;"><td style="text-align: left;border:1px solid #000000;">&nbsp;&nbsp;java</td><td style="text-align: left;border:1px solid #000000;">Java test sources</td></tr><tr style="border:1px solid #000000;"><td style="text-align: left;border:1px solid #000000;">&nbsp;&nbsp;resources</td><td style="text-align: left;border:1px solid #000000;">Test resources</td></tr><tr style="border:1px solid #000000;"><td style="text-align: left;border:1px solid #000000;">&nbsp;&nbsp;test</td><td style="text-align: left;border:1px solid #000000;">Scala test sources</td></tr></tbody></table>

## Usage

SBT has two modes. The **batch mode** runs a command and immediately exits while the **interactive mode**is the SBT command prompt.

### Batch mode

<span style="font-weight: 400;">To launch tasks, you just have to launch SBT followed by your list of commands:</span>

```language-bash
$ sbt clean compile`
```
<span style="font-weight: 400;">If a command takes arguments, enclose them in quotes:</span>

```language-bash
$ sbt clean compile "myTaksWithArguments arg1 arg2 arg3"
```

<span style="font-weight: 400;">Unlike Maven, to run SBT tasks on a sub-project, you stay in the root directory and give the relative path of the sub-project:</span>

```language-bash
$ sbt mySubProject/clean
```

### Interactive mode tour

<span style="font-weight: 400;">To launch the interactive mode, type </span>`sbt`<span style="font-weight: 400;"> without any argument:</span>

```language-bash
$ sbt
[info] Set current project to blog (in build file:/Users/jpbunaz/workspace/tutorial/sbt/blog/)
```

<span style="font-weight: 400;">You can then run any SBT command:</span>

```language-bash
> compile
[info] Updating {file:/Users/jpbunaz/workspace/tutorial/sbt/blog/}blog...
[info] Resolving org.fusesource.jansi#jansi;1.4 ...
[info] Done updating.
[info] Compiling 1 Java source to /Users/jpbunaz/workspace/tutorial/sbt/blog/target/scala-2.10/classes...
[success] Total time: 2 s, completed 28 oct. 2015 16:52:41
```

<span style="font-weight: 400;">You can now interact with SBT and take advantage of the auto-completion: </span>

```language-bash
 comp   // press tab
compile                   compile:                  compileAnalysisFilename   compileIncremental        compilerCache
compilers                 completions
```

<span style="font-weight: 400;">You can get a history of the commands with `!`:</span>

```language-bash
> !
History commands:
   !!    Execute the last command again
   !:    Show all previous commands
   !:n    Show the last n commands
   !n    Execute the command with index n, as shown by the !: command
   !-n    Execute the nth command before this one
   !string    Execute the most recent command starting with 'string'
   !?string    Execute the most recent command containing 'string'
>
```

<span style="font-weight: 400;">Get a list of tasks available from you SBT configuration file:</span>

```language-bash

> tasks
This is a list of tasks defined for the current project.
It does not list the scopes the tasks are defined in; use the 'inspect' command for that.
Tasks produce values.  Use the 'show' command to run the task and print the resulting value.

  clean    Deletes files produced by the build, such as generated sources, ...
  compile  Compiles sources.
...
  update   Resolves and optionally retrieves dependencies, producing a report.

More tasks may be viewed by increasing verbosity.  See 'help tasks'.
```

<span style="font-weight: 400;">One of the most interesting features in SBT is the continuous mode, to automatically recompile or run the tests whenever you save a source file. This is especially useful to run tests in the background when coding. All you have to do is to precede your task with “~”:</span>

```language-bash
 ~ test
[info] Updating {file:/Users/jpbunaz/workspace/tutorial/sbt/hello/}util...
[info] Updating {file:/Users/jpbunaz/workspace/tutorial/sbt/hello/}root...
[info] Resolving jline#jline;2.12 ...
[info] Done updating.
[info] Resolving org.apache.derby#derby;10.4.1.3 ...
[info] Compiling 1 Scala source to /Users/jpbunaz/workspace/tutorial/sbt/hello/util/target/scala-2.11/classes...
[info] Resolving jline#jline;2.12 ...
[info] Done updating.
[info] Compiling 1 Scala source to /Users/jpbunaz/workspace/tutorial/sbt/hello/target/scala-2.11/classes...
[success] Total time: 5 s, completed 28 oct. 2015 17:16:48
1. Waiting for source changes... (press enter to interrupt)
```

## How does a SBT build work?

<span style="font-weight: 400;">You can see SBT projects as a Map describing them, and the execution of the build will transform the </span>`Map`<span style="font-weight: 400;"> by applying a list of </span>`Setting[T]`<span style="font-weight: 400;"> (T being the type of value in the </span>*<span style="font-weight: 400;">Map</span>*<span style="font-weight: 400;">).</span>

<span style="font-weight: 400;">When you write the build definition, all you have to do is construct a list of </span>`Setting[T]`<span style="font-weight: 400;"> to apply to the </span>`Map`.

<span style="font-weight: 400;">For example, let’s assume we start with an empty Map and apply the following settings:</span>

1. <span style="font-weight: 400;">Add the value “hello” to the entry “name” (“name” being the project name by convention)</span>
2. <span style="font-weight: 400;">Generate a Jar file from the project name, associated to the entry “package”</span>

<span style="font-weight: 400;">In this example, step 2 depends on step 1. SBT is smart enough to order the list of settings to apply and execute step 1 before step 2.</span>

```
Map() -> Setting 1 -> Map("name" -> "Hello") -> Setting 2 -> Map("name" -> "Hello", "package" -> new File("target/hello.jar"))
```

<span style="font-weight: 400;">A Key could have 3 types:</span>

- `SettingKey[T]`: the value is executed once, when the build is started
- `TaskKey[T]`: the value is executed every time the task is run. The task could have side effects, such as writing files on disk
- `InputKey[T]`:A `TaskKey[T]` with command line arguments

<span style="font-weight: 400;">Beware, a `TaskKey` is always run only once per execution. SBT will look for all the dependencies and will deduplicate identical tasks. Therefore, you can not describe a task with the following algorithm:</span>

- *<span style="font-weight: 400;">call clean task</span>*
- *<span style="font-weight: 400;">generate classes from sources</span>*
- *<span style="font-weight: 400;">call clean task again</span>*

<span style="font-weight: 400;">The classes generated in the second step will not be cleaned because the clean task has already been run in step 1.</span>

<span style="font-weight: 400;">The last important thing to understand how SBT works is the </span>*<span style="font-weight: 400;">Scope</span>*<span style="font-weight: 400;">. In the </span>*<span style="font-weight: 400;">Map</span>*<span style="font-weight: 400;"> generated by the SBT execution, the key is not formed by the name but by the name + the scope. This allows you to have multiple values for the same key.</span>

<span style="font-weight: 400;">There are 3 scopes:</span>

- **Project**<span style="font-weight: 400;">: The value </span>*<span style="font-weight: 400;">name</span>*<span style="font-weight: 400;"> has a different value depending on the sub-project</span>
- **Configuration**<span style="font-weight: 400;">: The value is different depending on if you are using a </span>*<span style="font-weight: 400;">test</span>*<span style="font-weight: 400;"> or </span>*<span style="font-weight: 400;">compilation</span>*<span style="font-weight: 400;"> configuration.</span>
- **Task**<span style="font-weight: 400;">: The value is different depending on the task where it is declared</span>

## How to describe a build?

<span style="font-weight: 400;">There are 3 ways to describe a SBT build: </span>

- <span style="font-weight: 400;">A bare .sbt build definition</span>
- <span style="font-weight: 400;">A multi-project .sbt build definition</span>
- <span style="font-weight: 400;">A .scala build definition</span>

<span style="font-weight: 400;">The .scala build definitions are placed in a </span>*<span style="font-weight: 400;">project</span>*<span style="font-weight: 400;"> sub-directory. This directory is itself a new SBT project with the Scala source code necessary for the build. The build and application sources are then well separated. If you want to turn your Scala build sources into a SBT plugin, you already have a separated SBT project!</span>

<span style="font-weight: 400;">In this post, we will not use the .scala build definition but we will use the </span>*<span style="font-weight: 400;">project</span>*<span style="font-weight: 400;"> sub-directory to write tasks code and a </span>*<span style="font-weight: 400;">build.sbt</span>*<span style="font-weight: 400;"> file in the project’s root to describe the build.</span>

<span style="font-weight: 400;">The build.sbt can have two versions, depending on if you are using it for a simple project or multiple projects.</span>

==SimpleProject.sbt==
```
name := "My first project"
version := "1.0-SNAPSHOT"
organization := "usa.ippon.blog"
```

==MultipleProjects.sbt==
```
lazy val root = (project in file(".")).
  settings(
    name := "My first project",
    version := "1.0-SNAPSHOT",
    organization := "usa.ippon.blog"
  )
```

<span style="font-weight: 400;">A SBT build definition is just a list of Setting definitions processed to transform the </span>`Map`<span style="font-weight: 400;">. When you work with a simple project, without extending any feature, Keys are `SettingKey[T]` by default.</span>

## Dependency management

<span style="font-weight: 400;">There are two ways to deal with dependencies in SBT.</span>

### Unmanaged mode

<span style="font-weight: 400;">The unmanaged – or manual – mode just requires dropping JAR files in the </span>*<span style="font-weight: 400;">lib</span>*<span style="font-weight: 400;"> directory and they will be automatically added to the classpath by convention. Using scopes, you can have a separate directory for tests. We will not elaborate on this, but who still uses manual dependencies?</span>

### Managed mode

<span style="font-weight: 400;">In this mode you add values to the </span>*<span style="font-weight: 400;">libraryDependencies</span>*<span style="font-weight: 400;"> key which is of type </span>`SettingKey[Seq[ModuleID]]`. Therefore, your dependency declaration needs to be of type ModuleID, which is greatly simplified thanks to the DSL in build.sbt:

```
val derby: ModuleID = "org.apache.derby" % "derby" % "10.4.1.3" % "test"
                              |              |           |          |
                        organization       name       version     scope

lazy val root = (project in file(".")).
  settings(
    name := "My first project",
    version := "1.0-SNAPSHOT",
    organization := "usa.ippon.blog",
    libraryDependencies += derby,   // Add only one dependency
    libraryDependencies ++= Seq(scalaTest, hibernate)  // Add multiple dependencies
  )
```

<span style="font-weight: 400;">If you use “%%” instead of “%” between the organization and the name declaration, SBT will pick a dependency specific to your Scala version:</span>

```
"org.scalatest" %% "scalatest" % "2.2.4" % "test"
```

With a 2.11 Scala version declared, it will automatically translate like the following Maven declaration:

```language-xml
<dependency>
  <groupId> org.scalatest</groupId>
   <artifactId>scalatest_2.11</artifactId>
   <version>2.2.4</version>
   <scope>test</scope>
</dependency>
```

<span style="font-weight: 400;">To manage libraries, SBT uses Ivy under the hood. Therefore, it is possible to use dynamic version declaration. For example, you can declare a 2.2+ version to automatically upgrade to fixes on the 2.2.x version of your dependency.</span>

<span style="font-weight: 400;">See this </span>[<span style="font-weight: 400;">page</span>](https://ant.apache.org/ivy/history/2.3.0/ivyfile/dependency.html#revision)<span style="font-weight: 400;"> to know more about dynamic versions.</span>

<span style="font-weight: 400;">You can add new dependency </span>*<span style="font-weight: 400;">repositories</span>*<span style="font-weight: 400;"> by adding a value to the </span>*<span style="font-weight: 400;">resolvers</span>*<span style="font-weight: 400;"> key:</span>

```
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
```

## Scala / SBT options

Compiling for multiple scala versions:

```
scalaVersion := "2.11.4"  // Scala version for the project
crossScalaVersions := Seq( "2.10.4", "2.11.0")
// The project will be built for each declared version if you precede a task with “+”
> + compile
[…]
> + package
```

<span style="font-weight: 400;">Another very useful option to deal with different SBT versions installed in each developer local environment is the possibility to specify the SBT version to use. For this, you need to create a  build.properties file in the project directory:</span>

```
// No matter the SBT version installed, 0.13.9 will be downloaded and used for the build
sbt.version=0.13.9
```

## Conclusion

<span style="font-weight: 400;">SBT is a powerful tool to describe your build and manage your dependencies.</span>

<span style="font-weight: 400;">Understanding the key-value model behind the scenes and its immutable nature can help debugging and also writing plugins.</span>

<span style="font-weight: 400;">SBT is the </span>*<span style="font-weight: 400;">de facto standard</span>*<span style="font-weight: 400;"> tool for Scala projects and we encourage you to use it.</span>

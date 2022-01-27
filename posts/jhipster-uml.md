---
authors:
- Laurent Mathieu
tags:
date: 2015-07-06T12:54:21.000Z
title: "JHipster-UML"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/jroux.png
---

Authors: <span class="s1">Mathieu Abou-Aichi and Carl Klagba</span>

We are going to see what JHipster-UML is, why we created it, what’s its use and how to use it.

JHipster,  created by Julien Dubois, is a Yeoman generator of Spring + AngularJS apps. With it, you can create an application that is ready to be deployed ASAP, and you only need answer a few questions so that JHipster creates the perfect app for you.

Creating an entity is the same: questions are asked in order to know the entity’s name, its attributes’ name and details. Then, the needed files (Java classes, AngularJS objects) are generated from templates.

If repeating this process may seem easy for a few entities, doing it with more than 6 entities can be quite problematic for the user:

- Creating this many entities (or more) can take the user a lot of time, which could have been used to do some more important work;
- The more entities one has to create, the higher are the chances one may make mistakes creating the entities (typos, wrong types, etc.);
- If there’s an error, one has to either cancel the creation (and redo it again) or edit a JSON file (created by JHipster) by hand.

Hence the idea of replacing this process by another that wouldn’t have these drawbacks was quite interesting to us. That’s why JHipster-UML was created.

# What is JHipster-UML?

JHipster-UML is a [NPM](https://www.npmjs.com/package/jhipster-uml) package that can be used alongside JHipster in order to create your entities and their associations between them from a class model. Our project is available on [GitHub](https://github.com/jhipster/jhipster-uml) too.

#### ** How does it work?**

[![JHipsterUML](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/07/JHipsterUML.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/07/JHipsterUML.png)

You just have to create a class diagram using one of the four currently supported UML editors (UML Designer, Modelio, GenMyModel, Visual Paradigm). Once created, export your diagram to the XMI format. Finally, go to the root of your JHipster application, and enter:

`jhipster-uml <your_xmi_file>`

JHipster-UML will create in the .jhipster folder the JSON files corresponding to each entity, and will use JHipster to generate the files (using the command yo jhipster:entity)

#### The target audience

With JHipster-UML we target several demographics:

- The JHipster user are the ones that know our tool the most because they follow JHipster news (on GitHub, Twitter, etc.), and can test and give us feedbacks.
- The software and database architects, and more generally those who use UML are our second target. If they are not JHipster users, JHipster-UML can be an incentive for them to become one.

Our goal is to make the JHipster user pool bigger by making them use JHipster UML.

#### JHipster vs JHipster-UML : Entities creation comparison

Below, a tab displaying the time taken to create a whole domain with the classic JHipster Q&A method and the JHipster-UML’s:

<table border="1" style="border-color: black;"><tbody><tr><td></td><td>12 entities, 17 relationships</td></tr><tr><td>JHipster</td><td>1h15</td></tr><tr><td>JHipster-UML</td><td>**30 min**</td></tr></tbody></table>On complex projects, JHipster-UML enables you:

- to be more efficient,
- to avoid mistakes,
- to correct them more easily,
- to have a more global vision of your project,
- thus **gaining time**

#### Why XMI?

We have decided to use the XMI file format as an interface with an UML diagram. The advantage of this format is that it was created by the[ OMG](http://www.omg.org/index.htm) group with documented specifications. The issues with it is that, not all UML editors support the XMI export and when they do, their are slight syntax  differences from one to another, making the parsing a more tedious task. We will present our solution to this problem in the Architecture part.

Due to this constraint we must add a parser by editor. Currently the following editors are supported:

- [GenMyModel](https://www.genmymodel.com/) ;
- [UML Designer](http://www.umldesigner.org/) ;
- [Modelio](https://www.modelio.org/) ;
- and[ Visual Paradigm](http://www.visual-paradigm.com/).

# Architecture

Our architecture is based on the division of the main tasks:

- The XMI file parsing to extract the useful data,
- The creation of the JSON files that describe the entities,
- The entity scheduling according to their dependencies,

As previously mentioned, the differences between the XMI files’ syntaxes made us consider a solution which allows us to easily add a new parser, and to be able to make swift changes (minor or major) to the code.

That why we use, worst case scenario, one parser by UML editor (in the best case, a parser can be reuse).

The UML editor detection is done almost seamlessly for the user. Either the editor is detected and the parsing starts, or the user must choose one editor from the list of the supported editors (the implemented parsers).

Once the parsing is done, the entities creator generates the JSON files which will be used by JHipster to the entities’ application file. At this juncture we are clearly dependent of JHipster’s entity in-memory representation. The removal or, at least, the reduction of this dependence is something we want to explore.

Finally, since we can not simply start the entity generation in an unplanned order because of the dependencies between each entities (the entity on the owner side of the relationship must be generated prior to the other), we use a topological sorting algorithm which will, nondeterministically, give us the creation order. The algorithm can find circular dependencies.

Here our program’s sequence diagram:

[![seq_diag](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/07/seq_diag.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/07/seq_diag.png)

# Perspectives

JHipster-UML is a good leap towards making JHipster more simple to use, more accessible. But we are only at the first step, we still have avenues of improvement to explore. For instance, we think that type definition with an UML Editor can be tedious and the constraints creation for the fields is not possible with most editors. That why we have a lightweight class diagram editor made specifically for JHipster coming up the pipeline.

We also want to simplify the life of those who want to share their entities creation (with Google, GitHub, etc.) by creating a DSL (Domain Specific Language) which would be a much more pleasant way to create entities compare to the current .jhipster JSON files.

But we want to continue to improve the existent project with your help. That’s why we created a [**quick survey**](https://docs.google.com/a/ippon.fr/forms/d/1qRLWsBxErVz27FI3i0W-crNs6CDkxLn-6rNMFKur3ak/viewform) to know your habits. It will help us make JHipster catering more to your needs.

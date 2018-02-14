---
authors:
- Kile Niklawski
categories:
- Apache Spark
- Data Science
date: 2015-08-28T16:15:40.000Z
title: "Applying Data Science with Apache Spark Coding Dojo"
image: 
---

This week, at the power plant (Ippon Technologies USA headquarters), we had the pleasure of welcoming Alexis Seigneurin from Paris, France for a one week visit. Alexis is an experienced engineer currently working on a project with the Data Innovation Lab for a large insurance company. The team consists of 30+ data scientists who work with software engineers to apply their work to large datasets in production, with the goal of converting data into business value.

[![Hands On With Apache Spark, Big Data and Data Science.](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/08/dojo-300x225.jpg)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/08/dojo.jpg)

Hands On With Apache Spark, Big Data and Data Science.

One of the days during his visit, we brought a group of our developers to the power plant for a coding dojo, led by Alexis. We started with some donuts and coffee, gathered around the table, and dove right in. Alexis had created a VirtualBox VM so we could get up and running quickly. As Alexis began with the overview of Apache Spark, we passed around the USB drive to install the VMs. This took about 20-30 minutes to get everyone up and running. The VM came loaded with everything we’d need for the dojo, including IntelliJ IDEA with a project containing the basis for the code we’d be writing, the data files we’d be analyzing, and the presentation itself. Alexis discussed Spark history and performance a bit, citing benchmarks and comparing it to Hadoop MapReduce. He then discussed the ecosystem which includes Spark Shell, Spark Streaming, Spark SQL, Spark ML, and GraphX. We were acquainted and eager to get our hands dirty.

#### RDDs

The next portion of the presentation was an introduction to RDDs (Resilient Distributed Datasets), which are the building blocks for the encapsulated units of work run by Spark allowing it to be distributed and resilient. RDDs are actually immutable abstractions of collections that can be processed in parallel.

#### Scala

While Spark provides APIs in multiple languages such as Java, Python, and R, Alexis chose to use Scala for a few reasons. First, it is simple to learn for Java developers. Secondly it is concise and well supported. Since we’d be using Scala, the next section of the presentation included a brief overview of the language and syntax.

### Hands-On

We began the hands-on portion with the lower-level RDD API. This API provides direct access to your collections/tuples via map/reduce/filter and other low level data manipulation functions. This portion was beneficial for gaining an understanding of how the data is being processed.

The code itself was laid out in a clean fashion with comments providing a clear path to success:

[![Writing Apache Spark Code](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/08/dojo-code-ss.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/08/dojo-code-ss.png)Writing Apache Spark Code

#### DataFrames & SQL

Alexis then guided us through Spark DataFrames which provide a more familiar approach to accessing datasets and structured data. DataFrames seemed to be much simpler for everyone to grasp as we repeated many of the same exercises performed in the first round using RDDs. We then went through a Spark SQL exercise to perform SQL-like operations against an RDD.

#### Machine Learning

The machine learning portion took some of us out of our comfort zones. We learned new concepts and terminology. *Features*, in ML, refer to individual measurable properties that are being observed. In software development, a *feature* typically refers to a new enhancement to an application. *Performance*, in ML, refers to the accuracy of an algorithm, as opposed to the typical use of the term in software development which is application performance. *Overfitting* is a term that describes when a statistical model tries to fit random error or noise instead of the underlying relationship.

We learned about different categories of ML:

- **Supervised** – This refers to a ML algorithm which tries to predict the value of a known field.
- **Unsupervised** – This refers to a ML algorithm which tries to classify common features into clusters to attempt to determine where future data will end up.

##### Workflow

A typical ML workflow consists of the following steps:

1. **Data Cleansing** – Cleaning the data for consistency
2. **Feature Engineering** – Converting information into a format a machine can understand
3. **Training** – Using sample data to train the model
4. **Applying** – Applying the model to new data

#### Titanic

[Spark Machine Learning Outcomes](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/08/Screen-Shot-2015-08-28-at-3.31.53-PM-300x157.png)Spark Machine Learning Outcomes

In our ML hands-on, our goal was to guess whether or not a given set of passengers on the Titanic would survive, based on a model that we generated with a larger portion of the data. We used the 80/20 split for the sample data. 80% was used to train the model, 20% was used in our predictions. The results of the algorithm were impressive: only 8 false negatives and 4 false positives!

### Conclusion

- Spark is fast, fault-tolerant, and easy to use.
- Some of the concepts in data science are difficult for some software developers to grasp.
- Spark ML is powerful and robust
- Richmonders love the French

---
authors:
- Malcolm Thirus
categories:
- Big Data
- Apache Nifi
- Data Streaming
- ETL
- Dataflow
date: 2017-04-25T19:06:03.000Z
title: "Basics of Apache Nifi: 1"
id: 5a267e58dd54250018d6b64a
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/04/Nifi-Vlog-Slides-from-Olivia--4-.jpg
---

In [our previous article on Nifi](/why-nifi-2), we discussed the history, architecture, and features of Apache Nifi. This series will demonstrate the basics of how to create a dataflow within Nifi and the various ways to manipulate the data being ingested.
### Installation
Instructions for downloading and starting Nifi can be found [here](https://nifi.apache.org/docs/nifi-docs/html/getting-started.html#downloading-and-installing-nifi).
An alternative installation using Docker can be found [here](https://hub.docker.com/r/mkobit/nifi/).
I use [this Docker-Compose file](https://github.com/mal-virus/dockerfiles/blob/master/nifi/nifi-solo.yml) to start my personal Nifi instance.

<iframe width="560" height="315" src="https://www.youtube.com/embed/47efi5o2DyI?rel=0" frameborder="0" allowfullscreen></iframe>
### [Check out the video demonstration on our YouTube Channel!](https://youtu.be/47efi5o2DyI "Check out the video on our YouTube Channel!")

#### Excerpt
Apache Nifi is a Dataflow Management System that comes with a web-ui built to provide an easy way to handle dataflows in realtime. The most important aspect to understand for a quickstart into Nifi is Flow-Based Programming.

In plain terms, you create a series of nodes with a series of edges to create a graph that the data moves through. In nifi, these nodes are processors and these edges are connectors. The data is stored within a Packet of Information known as a FlowFile. This flowfile has things like content, attributes, and age.

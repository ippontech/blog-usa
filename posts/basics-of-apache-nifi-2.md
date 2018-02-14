---
authors:
- Malcolm Thirus
categories:
- Big Data
- Apache Nifi
- Data Streaming
- ETL
- Dataflow
date: 2017-11-15T16:41:16.000Z
title: "Basics of Apache Nifi: 2"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/11/Nifi-Vlog-Slides-from-Olivia--4-.jpg
---

On [our previous video on the basics of Nifi](/basics-of-apache-nifi), we covered a brief definition of Nifi, how flows are built, and the different types of processors that can be used. We also stepped through a very basic flow that pulls lines from a file and pushes them into AWS. Next on our list is to introduce a transformation step to get a better handle on our data coming through.

# Installation
Instructions for downloading and starting Nifi can be found [here](https://nifi.apache.org/docs/nifi-docs/html/getting-started.html#downloading-and-installing-nifi).
An alternative installation using Docker can be found [here](https://hub.docker.com/r/mkobit/nifi/).
I use [this Docker-Compose file](https://github.com/mal-virus/dockerfiles/blob/master/nifi/nifi-solo.yml) to start my personal Nifi instance.

<iframe width="560" height="315" src="https://www.youtube.com/embed/ORY1f2RIM_s?rel=0" frameborder="0" allowfullscreen></iframe>

[Check out the video on our YouTube Channel!](https://youtu.be/ORY1f2RIM_s "Check out the video on our YouTube Channel!")

# Excerpt
This is a good start, however all ETL processing needs to be prepared for malformed data. Rather than let the destination sort that out, we can add a transformation step between our extraction and our loading.

Flowfiles that move through Nifi have the data itself as the body and attributes associated with it as a header. Attributes can be added throughout the flow and they persist through the entirety of the flow. These attributes can be any data that describe where the flowfile came from, how it was created, what flowfile it may have been created from, or user defined data used to route flowfiles to specific destinations.

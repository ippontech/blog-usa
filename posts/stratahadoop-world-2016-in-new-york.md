---
authors:
- Alexis Seigneurin
tags:
- Culture
date: 2016-10-06T11:25:01.000Z
title: "Strata+Hadoop World 2016 in New York"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/01/strata-hadoop-2-1-1.jpg
---

<span style="font-weight: 400;">Strata+Hadoop World is probably the most important conference about Data Science and Data Engineering. The last edition took place in New York last week and gathered more than 2000 attendees. With 15 parallel tracks over 2 days of conference, that’s more than 160 sessions you could attend! Here is my take.</span>

## **Keynotes**

<span style="font-weight: 400;">As an introduction to the technical talks, the keynote speakers did a great job showing how our industry takes place in our -real- life, how data can be efficiently used to contribute to better healthcare, better education, better lives…</span>

<span style="font-weight: 400;">To name only one, Mar Cabra, head of the Data and Research unit at the International Consortium of Investigative Journalists, explained what techniques they used to analyze the huge amount of data from the Panama Leaks.</span>

<span style="font-weight: 400;">The least I can say is that these talks were inspiring.</span>

## **A few suggested talks**

<span style="font-weight: 400;">I obviously cannot give a comprehensive transcript of what was presented at Strata. Instead, let me give you a summary of a few sessions I liked.</span>

*<span style="font-weight: 400;">Making on-demand grocery delivery profitable with data science – </span>*<span style="font-weight: 400;">Jeremy Stanley, VP of Data Science at Instacart, explained how they attempt at optimizing their online grocery deliveries. Customers can order groceries online and shoppers will shop from local grocery stores and deliver to the customers. To keep the customers happy, you need to deliver quickly, meaning you need shoppers to be available. However, if you have too many shoppers, they are under-used and are a cost for the company. Jeremy showed how, version after version, they get better at routing the shoppers. A very complex problem.</span>

*<span style="font-weight: 400;">Apache Kafka: The rise of real-time data and stream processing</span>*<span style="font-weight: 400;"> – Neha Narkhede, cofounder and head of engineering at Confluent, gave an overview of the 3 products that are being developed and supported by Confluent: Kafka, Kafka Connect and Kafka Streams. I was already a great fan of Kafka and Kafka Streams but this talk showed me that Confluent has an extensive roadmap for Kafka Streams, and Neha described how the 3 tools can be assembled to make up the “modern ETL”.</span>

*<span style="font-weight: 400;">Parquet performance tuning: The missing guide</span>*<span style="font-weight: 400;"> – Ryan Blue, an engineer on Netflix’s Big Data Platform team, went through a description of the columnar storage format Apache Parquet. He gave a highly technical talk about how to take advantage of the features of this format and how to avoid some caveats. This shows that choosing a storage format is only the first part of the developer’s job, there’s a lot of tuning to be done after that step.</span>

*<span style="font-weight: 400;">Twitter’s real-time stack: Processing billions of events with Heron and DistributedLog</span>*<span style="font-weight: 400;"> – Karthik Ramasamy, tech lead for real-time analytics at Twitter, went through the challenges of setting up an analytics platform at the scale of Twitter. He described how they created a </span><span style="font-weight: 400;">high-performance replicated log service, </span>[<span style="font-weight: 400;">DistributedLog</span>](http://distributedlog.io/)<span style="font-weight: 400;">, and how they created </span>[<span style="font-weight: 400;">Heron</span>](http://heronstreaming.io/)<span style="font-weight: 400;">, a processing engine designed to be “a better Storm”. Heron has been in production at Twitter for 2+ years now and is 4-5 times faster than Storm.</span>

## **A torrent of technologies**

<span style="font-weight: 400;">Finally, if there is something to remember from this edition, it is that the industry is very active at creating new technologies. When you think you know the “Data ecosystem” quite well, Strata shows you you’re wrong! Here is a selection of the technologies that were mentioned, some I already knew about, some I found out about during the conference:</span>

- <span style="font-weight: 400;">Processing: </span>[<span style="font-weight: 400;">Apache Spark</span>](http://spark.apache.org/)<span style="font-weight: 400;">, </span>[<span style="font-weight: 400;">Apache Apex</span>](https://apex.apache.org/)<span style="font-weight: 400;">, </span>[<span style="font-weight: 400;">Apache Beam</span>](http://beam.incubator.apache.org/)<span style="font-weight: 400;">, </span>[<span style="font-weight: 400;">Heron</span>](http://heronstreaming.io)
- <span style="font-weight: 400;">Data stores: </span>[<span style="font-weight: 400;">Pinot</span>](https://github.com/linkedin/pinot)<span style="font-weight: 400;"> (OLAP), </span>[<span style="font-weight: 400;">Druid</span>](http://druid.io/)<span style="font-weight: 400;">, </span>[<span style="font-weight: 400;">Apache Kudu</span>](https://kudu.apache.org/)<span style="font-weight: 400;"> (analytics)</span>
- <span style="font-weight: 400;">Pub-sub: </span>[<span style="font-weight: 400;">Kafka</span>](http://kafka.apache.org/)<span style="font-weight: 400;">, </span>[<span style="font-weight: 400;">Google Cloud Pub/Sub</span>](https://cloud.google.com/pubsub/)
- <span style="font-weight: 400;">Threat detection: </span>[<span style="font-weight: 400;">Apache Spot</span>](http://spot.incubator.apache.org/)

## **How to see the presentations**

<span style="font-weight: 400;">Most of the talks have been recorded and will soon be available on O’Reilly’s platform, </span>[<span style="font-weight: 400;">Safari</span>](https://www.safaribooksonline.com/)<span style="font-weight: 400;">.</span>

<span style="font-weight: 400;">Since some speakers have already given the same talks in other conferences, you might also be able to find the recordings from these events or see one of the speaker in one of your local Meetups.</span>

<span style="font-weight: 400;">Finally, note that MapR has made available </span>[<span style="font-weight: 400;">6 Free ebooks by Ted Dunning and Ellen Friedman</span>](https://www.mapr.com/offers/6ebooks-pdf)<span style="font-weight: 400;">.</span>

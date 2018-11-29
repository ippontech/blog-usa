---
authors:
- Raphael Brugier
tags:
- AWS
- reInvent
date: 2018-11-29T00:08:09.000Z
title: "AWS re:Invent 2018 - Day 2"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/11/reinvent2.jpeg
---

We are already on day 2 of AWS re:Invent and there are even more announcements!

Here are our favorites:

# Data transfer

## AWS Ground Station
Definitely not something that every user will ever have to use, but a really cool technology: EC2 compute power to ingest satellite data!

A combination of various existing IoT and data services (Kinesis, S3, Rekognition, ...) allows satellite companies to apply on-demand and cost-effective analysis of their data.

Besides the fun and "wow effect" associated with this service, it’s interesting to see how AWS now invests in building services dedicated to specific industries. 

[AWS Ground Station – Ingest and Process Data from Orbiting Satellites](https://aws.amazon.com/blogs/aws/aws-ground-station-ingest-and-process-data-from-orbiting-satellites/)


# Analytics

## Amazon Comprehend for the healthcare industry

The NLP (Natural Language Processing) service - Comprehend - now offers language detection and extraction specialized for healthcare customers. Comprehend is adding an NER (Named Entity Recognition) feature dedicated to medical terms and is now capable of extracting critical medical terms.

Once again, it’s interesting to see AWS building a service for a specific industry.

[Amazon Comprehend Medical – Natural Language Processing for Healthcare Customers](https://aws.amazon.com/blogs/aws/amazon-comprehend-medical-natural-language-processing-for-healthcare-customers/)



# Storage

## DynamoDB Transactions

Ever wanted to have ACID properties for your NoSQL database? Well, it’s finally here for DynamoDB! Using three different consistency options for `Reads` and two for `Writes`, you can fine-tune transactions across multiple tables.

With this feature, DynamoDB now supports all four ACID properties: Atomicity, Consistency, Isolation, and Durability.

[Amazon DynamoDB Transactions](https://aws.amazon.com/fr/blogs/aws/new-amazon-dynamodb-transactions/)



# Monitoring

## CloudWatch Insight

One of the most expected features for CloudWatch: the ability to use advanced queries to analyze the logs and build dashboards. Using simple or complex queries, it’s now easier than ever to build personalized metrics dashboards. 

We are looking forward to playing with this new feature and see how it compares with traditional monitoring tools like Splunk.

[Amazon CloudWatch Logs Insights – Fast, Interactive Log Analytics](https://aws.amazon.com/fr/blogs/aws/new-amazon-cloudwatch-logs-insights-fast-interactive-log-analytics/)


# Streaming

## Kinesis Data Analytics for Java

Until now, Kinesis only supported SQL for analytics. If you wanted to integrate with more services during the processing, the typical solution was to use Lambda. With the addition of the Java support, Kinesis Data Analytics now also offers the option to use the popular stream processing framework [Apache Flink](https://flink.apache.org/) and write the processing code in Java.

[Amazon Kinesis Data Analytics for Java](https://aws.amazon.com/fr/blogs/aws/new-amazon-kinesis-data-analytics-for-java/)

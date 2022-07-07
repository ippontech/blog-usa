---
authors:
- Theo Lebrun
tags:
- Data
- Databricks
- Apache Spark
date: 2022-07-01T17:27:07.000Z
title: "Data+AI Summit 2022 - Top Announcements and Recap"
image: 
---

[Data+AI Summit 2022](https://databricks.com/dataaisummit/) is the world’s largest gathering among the Data and Analytics community and it took place in San Francisco (and virtually) across four days! With a large number of speakers that are experts across the data ecosystem, the conference was a huge success in term of attendees (~5k in person and ~50K virtually). With a packed agenda mixing Keynotes, 30-minute talks, and 2-hour industry forums, the conference was full of exciting announcements and interesting sessions. A large number of trainings and workshops were also available to everyone, from Data Engineers, to Data Scientists, to Business Leaders in order to improve any data skills.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/07/data-ai-summit.png)

# Top Announcements

## Delta Lake 2.0

The Delta format is based on Apache Parquet and is the standard storage layer for your Databricks Lakehouse. It provides several features, such as ACID transactions, audit history, time travel, etc. (check this [article](https://databricks.com/blog/2022/06/30/open-sourcing-all-of-delta-lake.html) for more details).

By open sourcing it, Databricks will let you produce Delta files that other applications will be able to read and use! That allows you to take advantage of all the features previously quoted while enjoying up to 4.3x faster processing compared to other storage layers.

## Project Lightspeed

With more and more real-time data pipelines and infrastructures, streaming data is becoming technically challenging. It has needs far different from and more complicated to meet than those of event-driven applications and batch processing.

Databricks is releasing "Project Lightspeed," a framework which advances Spark Structured Streaming into the real-time era as more and more new use cases and workloads migrate into streaming ([more details here](https://databricks.com/blog/2022/06/28/project-lightspeed-faster-and-simpler-stream-processing-with-apache-spark.html)).

## Spark Connect

Spark Connect is a simple client-server protocol that will let you run Spark anywhere! The demo on stage was done using an iPad, but the possibilities are endless (phones, embedded systems, etc.).

After configuring your Spark client, it will generate a query plan of your Spark code and send it to your Spark cluster so it can do the heavy lifting for you. You will be able to run Spark directly on your machine without having to have a local cluster using all your resources.

## Unity Catalog

The Databricks Lakehouse Platform combines the best elements of data lakes and data warehouses to deliver the reliability, strong governance, and performance of data warehouses with the openness, flexibility, and machine learning support of data lakes.

Unity Catalog is your unified governance solution for your Lakehouse, with key features like:
 - Automated Data Lineage
 - Built-in Data Search and Discovery
 - Simplified Access Controls

This [blog](https://databricks.com/blog/2022/06/28/whats-new-with-databricks-unity-catalog-at-the-data-ai-summit-2022.html) will give you a good overview of all the new features introduced to Unity Catalog.

## MLflow 2.0

Machine Learning is becoming more widely used in production and with MLflow 2.0, and with it, you will have access to MLflow pipelines to build production-grade ML pipelines.

Working with ML is never easy and has its bag of challenges that can quickly become a rotten tomato in your organization. Automating and scaling your pipelines should be relatively easy with the first release of MLflow Pipelines ([see this article](https://databricks.com/blog/2022/06/29/introducing-mlflow-pipelines-with-mlflow-2-0.html)).

## Databricks Marketplace

Powered by [Delta Sharing](https://databricks.com/product/delta-sharing), the Databricks Marketplace will let you discover and use a various number of data products (datasets, notebooks, dashboards, etc.) from third party vendors. In literally few seconds, you will able to obtain the datasets of your choice and have it in your own Lakehouse.

Please read [this introduction](https://databricks.com/blog/2022/06/28/introducing-databricks-marketplace-an-open-marketplace-for-all-data-and-ai-assets.html) for more details.

## Databricks SQL Serverless

The beauty of the Lakehouse is that you can combine Spark and SQL code so that your Data Engineers, Data Scientists, and Data Analysts can use the language in which they are most familiar.

In order to reduce infrastructure cost and provide a more elastic approach, you can now have a Serverless SQL Warehouse! For now, it is only available on Amazon Web Services (AWS), but Microsoft Azure and Google Cloud Platform (GCP) compatibilities are coming soon ([full article](https://databricks.com/blog/2022/06/28/databricks-sql-serverless-now-available-on-aws.html)).

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/07/data-ai-summit-swag.jpeg)

I am already looking forward to next year's Data + AI Summit by Databricks!

Need help using [Databricks](https://blog.ippon.tech/tag/databricks/)? Need help designing and implementing your future Lakehouse? Ippon can help! Send us a line at contact@ippon.tech.
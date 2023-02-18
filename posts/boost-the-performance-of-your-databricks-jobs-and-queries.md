---
authors:
- Theo Lebrun
tags:
- Data
- Databricks
- ETL
- Performance
date: 2023-02-18T16:09:09.000Z
title: "Boost The Performance Of Your Databricks Jobs And Queries"
image: 
---

Databricks is doing a lot of optimization and caching by default so jobs and query have a pretty fast runtime. Poorly designed tables (like having too many small partitions) or using a too small cluster will obviously impact your perfomances, I recommend paying attention to those details when creating tables or clusters. In this blog, I will explain multiple techniques that can be done in order to enhance what Databricks is doing under the hood.

# A fixed-size cluster is usually better than autoscaling

Having a cluster that autoscale is great if you want to save money and if you don't have strict SLAs on your jobs/queries runtime. But you will realize that it is usually better to use a cluster with a fixed-size, especially if you know the volume of data being processed in advance. Upsizing your cluster is not instant and it usually takes 3/4 minutes to have new worker nodes ready because of their initialization script. Because of that delay, you will notice that your cluster is constantly changing size and acting kinda like a "roller coaster":

![](https://raw.githubusercontent.com/Falydoor/blog-usa/blog-tips-and-optimization-databricks/images/2023/02/databricks-autoscaling.png)

Don't get me wrong, that is the whole point of autoscaling but the wait time between each upsizing event has an impact. A streaming job will have more data to process by the time the first upzise is finished and then will probably have to upsize again as more data must be processed.

# Small cluster with larger VMs is better for complex ETL

A complex ETL job that requires unions and joins across multiple tables will run faster on a cluster that uses very large instance types because having a reduce number of workers minimize the amount of data shuffled. Also your workers will have more RAM available which should avoid running into `OUT_OF_MEMORY` errors like below (DLT pipeline):

![](https://raw.githubusercontent.com/Falydoor/blog-usa/blog-tips-and-optimization-databricks/images/2023/02/databricks-out-of-memory.png)

For more details, I recommend reading [this Databricks page about cluster size consideration](https://docs.databricks.com/clusters/cluster-config-best-practices.html#cluster-sizing-considerations) but you will realize that a low number of large instance types is always better than a high number of small instance types.

# Optimize and Z-ordering are your friends

The [Optimize command](https://docs.databricks.com/delta/optimize.html) is very useful and will compact small files into larger ones to improve your query speed. You can even select a subset of your data by having a `where` in the `optimize` command. Databricks recommend running `optimize` daily and at night when the EC2 spot prices are low.

Z-ordering can be used on columns that are often used in your query's predicates and that are high cardinality. For example, let's say you have a table with daily stock price, you will probably want to z-order on the stock ticker column because there is a high number of different tickers and you usually filter by one ticker in your queries. Partitionning on the ticker column will create too many small partitions so that's why in that case z-ordering is more adapted. I recommend reading [the Databricks page](https://docs.databricks.com/delta/data-skipping.html#what-is-z-ordering) about z-ordering for more informations.

# Cache your data!

This advice gonna sound basic but caching your data using disk caching is still the standard method to make your queries faster. Databricks will automatically cache your data based on your queries but you can also preload data beforehand by using the `CACHE SELECT` command. It is surely very specific to a query and might not fit your use case especially if the query you want to cache is dynamic. But for a specific query that is often run, pre-caching it will have a big impact. Also, cache should be disabled when comparing queries speed to avoid having "wrong" results. For more details about caching, please read [this Databricks page](https://docs.databricks.com/optimizations/disk-cache.html).

# Let Databricks partition your data

Partitioning your data is very important and you should always think before picking which columns to partition by. For tables with less than 1TB of data, Databricks actually recommends defining no partition columns and let Delta Lake handle it for you with its built-in features and optimizations. Tables can still be partitioned on specific columns but avoid using one with high cardinality because a lot of small files will impact query speed. For more details about partitioning and on Databricks recommendations, [here is the offcial Databricks page](https://docs.databricks.com/tables/partitions.html).

# Generate columns for your custom partitions

If you decide to partition your table on specific columns, you might to take a look at [Delta Lake generated columns](https://docs.databricks.com/delta/generated-columns.html). This will be very useful if your data is partitionned on a date/timestamp column because you will be able to query on year/month/day and have your query use a partition filter. With our previous example with our daily stock price table, we would use our column `file_date` (date of the stock price) to generate a `file_year` and `file_month` column and partition our table with those 2 columns. Then we can easily retrieve all prices for a given year or month and have Delta Lake generate the correct partition filter:

![](https://raw.githubusercontent.com/Falydoor/blog-usa/blog-tips-and-optimization-databricks/images/2023/02/databricks-generated-columns.png)

To resume, multiple approaches can be done in order to improve query speed or job duration but having a poorly designed table will be very hard to optimize. Most of the time, nothing need to be done and the default behavior from Databricks will be enough. But depending on your end user queries, z-ordering and caching will help you have faster queries.

Need help with your existing Databricks Lakehouse Platform? Or do you need help using the latest Data lake technology? Ippon can help! Send us a line at [contact@ippon.tech](mailto:contact@ippon.tech).
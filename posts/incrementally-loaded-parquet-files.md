---
authors:
- Alexis Seigneurin
tags:
- Apache Spark
- Apache Parquet
date: 2017-05-17T13:48:11.000Z
title: "Incrementally loaded Parquet files"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/04/Incrementally-loaded-Parquet-files-Blog.png
---

In this post, I explore how you can leverage [Parquet](https://parquet.apache.org/) when you need to load data incrementally, let's say by adding data every day. We will see how we can add new partitions to an existing Parquet file, as opposed to creating new Parquet files every day.

# Sample data set for this example

I am going to use the data set of the [building permits in the Town of Cary](https://data.opendatasoft.com/explore/dataset/permit-inspections@townofcary/) for my demonstration. Here is a sample of the data (only showing 5 columns out of 15):

```
+-----------+--------+------------+-------------+--------------------+
|  PermitNum|InspType|ResultMapped|InspectedDate|    OriginalAddress1|
+-----------+--------+------------+-------------+--------------------+
|10-00002766|    B300|    Rejected|   2010-03-30| 616 POWERS FERRY RD|
|10-00002766|    S295|    Rejected|   2010-04-01| 616 POWERS FERRY RD|
|10-00002769|    M306|      Passed|   2009-11-24|       205 CANIFF LN|
|10-00002770|    B200|     Partial|   2009-11-24|     332 FELSPAR WAY|
|10-00002770|    B300|      Passed|   2010-07-19|     332 FELSPAR WAY|
|10-00002771|    B200|     Partial|   2010-01-12|     408 FELSPAR WAY|
|10-00002772|    E300|    Rejected|   2009-11-30|      2723 NC 55 HWY|
|10-00002775|    B100|      Passed|   2009-12-02|1033 HORTONS CREE...|
...
```

This data has a date (`InspectedDate`), and we will assume we receive new data every day, given these dates.

# Traditional structure - Multiple Parquet files

If we receive data every day, an easy way to store this data in Parquet is to create one "file" per day:

```
permits_2016-01-01.parquet/
    _SUCCESS
    part-00000-216e2843-88aa-4058-8c93-16192355dd85.snappy.parquet
    part-00000-adbb37d7-b461-4036-92f8-5a56d760872a.snappy.parquet
    part-00000-9a453efe-021d-43b4-a384-bfc9d4a3f41b.snappy.parquet
permits_2016-01-02.parquet/
    _SUCCESS
    part-00000-64ad465b-60a9-4ce4-88fd-1d1bb4f6fcef.snappy.parquet
    part-00000-ed584f51-6283-48ad-9161-89615c14ae4c.snappy.parquet
    part-00000-4ed0cf31-d59e-4373-922b-8491d70b20b5.snappy.parquet
permits_2016-01-03.parquet/
...
```

As a reminder, Parquet files are partitioned. When we say "Parquet file," we are actually referring to multiple physical files, each of them being a partition.

This directory structure makes it easy to add new data every day, but it only works well when you make time-based analysis. If you want to analyze the data across the whole period of time, this structure is not suitable. Using Spark, for instance, you would have to open each Parquet file and *union* them all together. Not only is this impractical, but this would also result in bad performance.

# Incrementally loaded Parquet file

Now, we can use a nice feature of Parquet files which is that you can add partitions to an existing Parquet file without having to rewrite existing partitions. That is, every day, we will *append* partitions to the existing Parquet file. With Spark, this is easily done by using `.mode("append")` when writing the DataFrame.

Let's see how this goes with our dataset of building permits. We first load the data into a DataFrame and strip off data without a date:

```scala
val permitsDF = spark.read
  .option("header", "true")
  .option("delimiter", ";")
  .csv("permit-inspections.csv")
  .where(!isnull($"InspectedDate"))
```

We will start with a few dates, so let's see how many records we have for the last few days of this data set:

```scala
val inspectedDatesDF = permitsDF
  .groupBy("InspectedDate")
  .agg(count("*"))
  .orderBy(desc("InspectedDate"))
  .coalesce(1)
```

```
+-------------+--------+
|InspectedDate|count(1)|
+-------------+--------+
|   2016-12-15|     314|
|   2016-12-14|     316|
|   2016-12-13|     346|
...
```

We can start by writing the data for `2016-12-13`. For simplicity, we reduce the number of partitions to 2.

```scala
permitsDF.where($"InspectedDate" === "2016-12-13")
  .coalesce(2)
  .write
  .parquet("permit-inspections.parquet")
```

The Parquet file shows 2 partitions, as expected:

```
$ ls -lrt permit-inspections-recent.parquet
...  14:53 part-00001-bd5d902d-fac9-4e03-b63e-6a8dfc4060b6.snappy.parquet
...  14:53 part-00000-bd5d902d-fac9-4e03-b63e-6a8dfc4060b6.snappy.parquet
...  14:53 _SUCCESS
```

Let's try to read the file and run some tests on it:

```scala
val permitsPerDayDF = spark.read.parquet("permit-inspections.parquet")
println(permitsPerDayDF.count)
permitsPerDayDF.where($"InspType" === "B100").show()
```

We get 346 records, as we expected, and a few of them are for inspections of type `B100`:

```
346
+-----------+--------+------------+-------------+--------------------+
|  PermitNum|InspType|ResultMapped|InspectedDate|    OriginalAddress1|
+-----------+--------+------------+-------------+--------------------+
|17-00002689|    B100|    Rejected|   2016-12-13|    632 ANGELICA CIR|
|16-00003612|    B100|    Rejected|   2016-12-13|    110 CANDYTUFF CT|
|17-00003314|    B100|      Passed|   2016-12-13|   704 HUNTSWORTH PL|
|17-00000387|    B100|    Rejected|   2016-12-13|4000 CAROLINA LIL...|
|17-00002489|    B100|      Passed|   2016-12-13|1622 VINEYARD MIS...|
|17-00000678|    B100|    Rejected|   2016-12-13|    626 ANGELICA CIR|
+-----------+--------+------------+-------------+--------------------+
```

Let's now *append* new partitions with data from `2016-12-14` (notice the `.mode("append")` option):

```scala
permitsDF.where($"InspectedDate" === "2016-12-14")
  .coalesce(2)
  .write
  .mode("append")
  .parquet("permit-inspections.parquet")
```

Now, our Parquet files has 2 new partitions (written at `14:59`) while the original partitions are left unchanged (written at `14:53`).

```
$ ls -lrt permit-inspections-recent.parquet
...  14:53 part-00001-bd5d902d-fac9-4e03-b63e-6a8dfc4060b6.snappy.parquet
...  14:53 part-00000-bd5d902d-fac9-4e03-b63e-6a8dfc4060b6.snappy.parquet
...  14:59 part-00001-b0557e58-5f21-4117-826d-0eae68e2cdb9.snappy.parquet
...  14:59 part-00000-b0557e58-5f21-4117-826d-0eae68e2cdb9.snappy.parquet
...  14:59 _SUCCESS
```

We can now re-run our read test. We get the expected 662 records (346 for `2016-12-13` + 316 for `2016-12-14`) and we can see the filtering on inspection type has retrieved data from all the partitions:

```
662
+-----------+--------+------------+-------------+--------------------+
|  PermitNum|InspType|ResultMapped|InspectedDate|    OriginalAddress1|
+-----------+--------+------------+-------------+--------------------+
|17-00002689|    B100|    Rejected|   2016-12-13|    632 ANGELICA CIR|
|16-00003612|    B100|    Rejected|   2016-12-13|    110 CANDYTUFF CT|
|17-00003314|    B100|      Passed|   2016-12-13|   704 HUNTSWORTH PL|
|17-00000387|    B100|    Rejected|   2016-12-13|4000 CAROLINA LIL...|
|17-00002489|    B100|      Passed|   2016-12-13|1622 VINEYARD MIS...|
|17-00000678|    B100|    Rejected|   2016-12-13|    626 ANGELICA CIR|
|17-00003301|    B100|    Rejected|   2016-12-14|        3537 OGLE DR|
|16-00003612|    B100|    Rejected|   2016-12-14|    110 CANDYTUFF CT|
|17-00002794|    B100|      Passed|   2016-12-14|741 PENINSULA FOR...|
|17-00003317|    B100|    Rejected|   2016-12-14|     903 WALCOTT WAY|
|17-00003428|    B100|      Passed|   2016-12-14|  1089 QUEENSDALE DR|
+-----------+--------+------------+-------------+--------------------+
```

# Conclusion

We have seen that it is very easy to add data to an existing Parquet file. This works very well when you're adding data - as opposed to updating or deleting existing records - to a *cold* data store (Amazon S3, for instance). In the end, this provides a cheap replacement for using a database when all you need to do is offline analysis on your data.

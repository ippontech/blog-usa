---
authors:
- Theo Lebrun
tags:
- Apache Spark
- Big Data
date: 2020-06-23T14:50:55.000Z
title: "Apache Spark 3.0"
image:
---

Databricks recently announced the release of [Apache Spark 3.0](https://databricks.com/blog/2020/06/18/introducing-apache-spark-3-0-now-available-in-databricks-runtime-7-0.html) with their Databricks Runtime 7.0. It's been a long road since the initial 1.0 release in 2014 which makes Spark one of the most active open-source big data projects with over 1000 contributors! This giant open-source community (one of the most active from the Apache Software Foundation) resolved over 3400 Jira tickets which is a pretty good way to celebrate its 10 year anniversary as an open-source project.

# New features introduced

- Java 11 support
- Hadoop 3 support
- Better ANSI SQL compatibility
- 2x performance improvement on TPC-DS over Spark 2.4
- Simpler/better PySpark exceptions
- Up to a 40x speedup for calling R user-defined functions
- Structured Streaming UI

Check the [migration guide](https://spark.apache.org/docs/3.0.0/migration-guide.html) in order to switch your code to the new version and also note that no major code changes should be required.

# Python API improvements

I've been always a fan of Python for its simplicity, libraries and capacity for doing more with less code. [PySpark](https://spark.apache.org/docs/latest/api/python/index.html) is great, especially if you want to use ML or NLP libraries, but the overall performance is always going to be better in a Scala environment.

Recently, Python passed Scala as the most used language on Spark which is not a surprise since PySpark has more than 5 million monthly downloads on PyPI. The goal of this release was to improve the functionality and usability of PySpark. This includes a redesign of the Pandas UDF API using Python type hints, new Pandas UDF types, and more Pythonic error handling. Pandas is still my most favorite library for data analysis/manipulation but it is a single-node processing library which doesn't really fit in a distributed environment. I recommend using [Koalas](https://koalas.readthedocs.io/en/latest/) which is basically the Pandas API on Apache Spark. The Koalas API gives close to 80% coverage of the Pandas API which is enough for most cases.

PySpark errors are not always user-friendly to Python users because Spark was originally designed to run on a JVM. This release will simplify PySpark exceptions by making them more Pythonic and will also avoid showing unnecessary JVM stack traces.

# Spark SQL engine

Most Spark applications are using the Spark SQL engine (based on Databricks stats) and one of the goal of this release was to improve performances and ANSI compatibility. Spark 3.0 will perform around 2x faster than a Spark 2.4 environment in the total runtime. In order to improve performances and query tuning a new framework was introduced: [Adaptive Query Execution (AQE)](https://databricks.com/blog/2020/05/29/adaptive-query-execution-speeding-up-spark-sql-at-runtime.html).

AQE will generate a better execution plan at runtime which is crucial because of the separation between storage and compute in Spark (which make data unpredictable). In other words, AQE will create a better plan even if your data are absent or inaccurate (the initial Spark plan would have been inadequate).

The ANSI compatibility aims to make workload migration from other SQL engines easier by improving the SQL compliance. Spark will forbid you to use SQL reserved keywords (LIKE, IN, SELECT, etc) to avoid having issues later on. Also, when inserting data in a predefined schema, Spark will validate the data types to improve data quality. The property `spark.sql.ansi.enabled` must be set to `true` in order to enable this feature.

# Other updates

Data streaming is a big part in every big data use case for its near-real-time processing. Structured streaming was initially introduced in Spark 2.0 and now a new [Web UI](https://spark.apache.org/docs/3.0.0/web-ui.html#structured-streaming-tab) is available to inspect your streaming jobs with two sets of statistics (aggregate information of jobs completed and detailed information about queries).

This major release is the result of contributions from over 440 contributors which demonstrate the power of a strong open-source community. It is also good to note that the contributors are part of some very big organizations like Google, Microsoft, Facebook, Netflix or Databricks.

More details about this release can be found on [the release notes here](https://spark.apache.org/releases/spark-release-3-0-0.html). You can also consult the [Jira project](https://issues.apache.org/jira/secure/ReleaseNote.jspa?projectId=12315420&version=12339177) for a list of all the tickets included in the release.

# How to use this version

In order to test out this new release, you will have to use Databricks since this version is currently not available on the main cloud providers (like AWS EMR). You can sign up for a [14 day trial account here](https://databricks.com/try-databricks). You can also install Spark locally (with [Homebrew](https://brew.sh/) and `brew install apache-spark` on a Mac) and play with the Spark Shell.

To conclude, Apache Spark 3.0 is a pretty important release that will make every Python developer happy while improving the performance of your workloads. This release, once again, proves the strength and benefits of the open-source community by successfully involving a very high number of contributors.
---
authors:
- Pooja Krishnan
tags:
- Cloud
- Snowflake
date: 2019-08-07T10:33:00.000Z
title: "Innovative Snowflake Features"
image:
---
I recently attended the Snowflake Partner Bootcamp in Baltimore, and was given the opportunity to learn more about Snowflake. In the following blog, I am going to give a run-down of the innovative features Snowflake offers to its clients and some of the benefits of using them.

# What is Snowflake?
[Snowflake](https://www.snowflake.com/) is a Data Warehouse as a Service in the cloud. Highly available and fully managed, Snowflake handles the following for you:
* Authentication
* Configuration
* Resource Management
* Data Protection
* Availability
* Optimization
* Cross Cloud Support

Snowflake runs on popular cloud providers, including Microsoft Azure, Amazon Web Services and, as of July 2019, Google Cloud Platform (available in Public Preview).

Snowflake also provides native connectors for a variety of languages in addition to a Web UI and Command Line Interface called SnowSQL.

## Snowflake Editions
Snowflake offers a variety of editions which offer differing levels of service. Look at [Snowflake Editions](https://docs.snowflake.net/manuals/user-guide/intro-editions.html "Snowflake Editions") in the Snowflake Documentation for more information.

---
# Why Snowflake?
Snowflake uses a central data repository for persisted data that is accessible to all compute nodes and processes queries in MPP (Massively Parallel Processing) compute clusters referred to as Virtual Warehouses (we'll address these more later). This unique architecture allows both for easy data management as well as performance benefits and increased scalability.

In addition, Snowflake is completely ANSI SQL compliant.

---
# Architecture:
Snowflake's Architecture consists of three layers:
* Database Storage
* Query Processing
* Global Services

## Storage
Snowflake automatically converts all data stored into an optimized compressed columnar format and encrypts it using AES-256 strong encryption.

### Micro-Partitions (FDN [^1])
When data is loaded into Snowflake, it is automatically divided into **micro-partitions**. Micro-partitions are automatically derived, physical data files which can contain between 50 and 500 MB of uncompressed data. Groups of rows in tables are mapped into individual micro-partitions and are organized in columnar fashion. These micro-partitions are not directly accessible or visible.

![Micro-Partitions](https://docs.snowflake.net/manuals/_images/tables-clustered1.png) [source](https://docs.snowflake.net/manuals/_images/tables-clustered1.png)

In the image above, the table has 24 rows which have been divided into 4 micro-partitions, organized and sorted by column. By dividing the data into micro-partitions, Snowflake can, upon receiving a query to process, first prune micro-partitions un-needed for the query and then prune by column for the remaining micro-partitions.

### Clustering
In addition to storing data as micro-partitions, Snowflake also sorts them naturally according to natural dimensions. This *Clustering* is very important to Snowflake data storage, as it can greatly impact query performance. Clustering occurs upon ingestion and, as data is loaded, Snowflake co-locates column data with the same values in the same micro-partition if possible.

Over time, as a table grows, its' naturally defined clustering keys can become stale, resulting in reduced query performance. When this occurs, periodic re-clustering is required. During re-clustering, Snowflake uses the clustering key for a table to reorganize the column data. This deletes all affected records, which Snowflake will re-insert grouped according to the Clustering Key.

#### Clustering Key?
A clustering key is one or more columns or expressions that can be used to sort a table. The number of distinct values in a column is a key aspect when selecting clustering keys for a table. It is important to choose a clustering key with a large enough number of distinct values to enable effective query pruning while containing a small enough number of distinct values to allow Snowflake to effectively group rows into micro-partitions.

You should select a clustering key for your table, if queries on your dataset typically use sorting operations or if you typically filter on a column or several. If so, you should consider designating the column or columns as a clustering key for a table [^2].

*You should only cluster when queries will substantially benefit. Much of the time, Snowflake's natural clustering will suffice!*

#### Time-Travel and Fail-Safe
Snowflake also provides two features which allow a customer to access historical data.
* Time-Travel facilitate the restoration of deleted objects, Duplicating and backing-up data from key points in the past and analyzing data usage over time.
* Fail-Safe data storage occurs during the 7 days immediately following the Time-Travel retention period.

Snowflake preserves the state of data for a data retention period (automatically set to one day for all table types). This data retention time limit cannot be disabled on an account level, but can be disabled for individual databases, schemas and tables.
![Table Types](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/08/Snowflake-Table-Types.png) [source](https://docs.snowflake.net/manuals/user-guide/tables-temp-transient.html#comparison-of-table-types)

As seen above, all table types have a Time-Travel period, but only permanent tables have a Fail-Safe period. Fail-Safe is non-configurable, and is not intended as a means for accessing historical data after the Time-Travel period has elapsed. Fail-Safe is intended for Snowflake to retrieve data that may have been lost or damaged due to extreme operational failures.

*In case retrieval of data from Fail-Safe is necessary, you must speak to the Snowflake sales team.*

##### Storage Costs Associated with Time-Travel and Fail-Safe
Storage fees are incurred for maintaining data in both Time-Travel and Fail-Safe. These costs are calculated for each 24 hour period from the time the data changed. Snowflake minimizes the amount of storage required for historical data by maintaining only the information needed to restore rows that were updated or deleted (This means that if a table were truncated or dropped, Snowflake will maintain the fully copy). As Temporary and Transient tables only use 1 day of Time-Travel and no Fail-Safe, these should be used to reduce costs.

*Temporary tables will be deleted once the session ends. One day of Time-Travel on Temporary Tables will only occur if the session is greater than 24 hours in length.*

## Query Processing
The layer of the Snowflake Architecture in which queries are executed using resources provisioned from a cloud provider.

### Virtual Warehouses
At their core, virtual warehouses are one or more clusters of servers that provide compute resources. Warehouses are sized in traditional t-shirt sizing, and each size determines the number of servers in each cluster of the warehouse. Size can be managed using SQL or in the Web UI.

| Warehouse Size | Servers per Cluster | Credits per Hour | Notes
| -------------- | ------------------- | ---------------- | -----
| X-Small        | 1                   | 1                | Default size for warehouses using CREATE
| Small          | 2                   | 2                |
| Medium         | 4                   | 4                |
| Large          | 8                   | 8                |
| X-Large        | 16                  | 16               | Default size for warehouses created in the UI
| 2X-Large       | 32                  | 32               |
| 3X-Large       | 64                  | 64               |
| 4X-Large       | 128                 | 128              |

*It is possible for users to script warehouses of size up to 5XL. 4XL is the maximum size when creating warehouses from the Web UI. If a warehouse of size greater than 5XL is required, the customer will need to work with the Snowflake Sales Team.*

Warehouses can be resized at any time with no latency. Any queries already running against the warehouse will complete using the current resource levels. Any queued and new queries will use the newly provisioned resource levels.

By default, Snowflake will automatically suspend an unused warehouse after a period of time. Similarly, Snowflake will automatically resume a warehouse when a query using it is submitted and the warehouse is the current warehouse for the session.

#### Multi-Cluster Warehouses
Multi-Cluster Warehouses allow for the scalability of compute clusters to manage user and query concurrency needs. Typically, a virtual warehouse contains a single cluster of servers. By setting the minimum and maximum number of server clusters, Snowflake will automatically scale warehouses horizontally according to demand.

*These cost more than standard Virtual Warehouses and are only available for Enterprise Edition customers and above*

#### Credit Usage and Billing
All costs for compute resources are based on Snowflake Credits. Credits are charged based on the number of Virtual Warehouses used, how long they run and their size. There is a one-to-one relationship between the number of servers in a warehouse and the number of credits they consume per hour. Warehouses are only billed when they are running. Credits are billed per-second, with a 60-second minimum. After 1 minute, all subsequent billing is per-second. When a warehouse is resized, credits are billed only for the additional servers.

## Global Services
The Global Services layer coordinates and manages the entire Snowflake system. It authenticates users, manages sessions and secures data. In addition, the Global Services layer performs query optimization and compilation as well as managing Virtual Warehouses so that once a transaction on a virtual warehouse is complete, all virtual warehouses see the new data.

All communication to Snowflake is encrypted from end-to-end.

---
# Snowflake Caches
Snowflake uses three caches to improve query performance.

## Metadata Cache
**Fully Managed in the Global Services Layer**

Snowflake automatically manages some clustering and micro-partition metadata. Information maintained includes:
* The total number of Micro-Partitions
* Number of Micro-Partitions containing values overlapping with each together
* The depth of overlapping Micro-Partitions
  * This is an indication of how well-clustered a table is, since as this value decreases, the number of pruned columns can increase.

This cache is maintained in the Global Services Layer.

All DML operations take advantage of micro-partition metadata for table maintenance. In addition, some operations are metadata alone, and require no Compute resources in order to complete.
> Deleting all rows from a table is one of these operations.

In addition, micro-partition metadata allows for the precise pruning of columns in micro-partitions:
1. Snowflake's pruning algorithm first identifies the micro-partitions required to answer a query.
2. Snowflake will only scan the portion of those micro-partitions that contain the required columns.
3. Snowflake then uses columnar scanning of partitions so an entire micro-partition is not scanned if a query filters by a single column.

## Query Result Cache
**Fully Managed in the Global Services Layer**

The query result cache is the fastest way to retrieve data from Snowflake. The query result cache contains a combination of Logical and Statistical metadata on micro-partitions. It used primarily for query compilation, but also for SHOW commands and queries against the INFORMATION_SCHEMA table.

If a user repeats a query that has already been run, and the data hasn’t changed, Snowflake will return the result it returned previously.

Typically, query results are reused if:
* The user executing the query has the necessary access privileges for all the tables used in the query.
* The new query matches the previously-executed query (with an exception for spaces).
* The table data has not changed.
* The persisted result is still available. *The Query Result Cache will purge query results after 24 hours. This time period will be reset if the query is run again at any point*
* The micro-partitions have not changed.

## Warehouse Data Cache
**Implemented in the Virtual Warehouse Layer**

All Snowflake Virtual Warehouses have attached SSD Storage. This SSD Storage is used to store micro-partitions that have been pulled from the Storage Layer. Reading from SSD is faster. As such, when a warehouse receives a query to process, it will first scan the SSD cache for received queries, then pull from the Storage Layer.

The SSD Cache stores query specific FILE HEADER and COLUMN data. This cache type has a finite size and uses the LRU (Least Recently Used) policy to purge data that has not been recently used.

---
# Data Sharing
Snowflake currently supports data-sharing between accounts in the same geographic region.

## Data Sharing Accounts
* Data Providers - Share the data. Providers own the data and get billed for its' storage.
* Data Consumers - Can access and query objects in shared data. Consumers pay for compute on shared resources.
* Reader Account - Consumer without a Snowflake Account or for Accounts not in the same geographic region as the provider. These accounts are created and completely paid for by the Providers. Reader Account Consumers can only read data shared with them.

###Data Sharing for HIPAA Protected Datasets
Data in an Enterprise for Sensitive Data (ESD) Account which is HIPAA protected can only be shared with other ESD accounts.

## Sharing Data
Snowflake uses shares, which are Snowflake objects containing all the information needed to share objects within a database. Shares contain privileges that grant access to the database, schema, or specific objects as well as the Consumer Account Name.

Shares can only currently be created for other accounts in our region. You receive a share as a consumer by creating a database for it either through the Web UI or SnowSQL.

---
# Supported Data Formats and Types
##Semi-Structured Data
Snowflake natively supports the load and access of several types of Semi-Structured data, including JSON, Avro, XML and Parquet.

In order to support loading these data-types, Snowflake has a few specialized data-types. These are:
* VARIANT - Universal type that can store values of any other type.
* ARRAY
* OBJECT - Collection of key-value pairs where the key is a non-empty string and the value is of VARIANT type.

### Accessing Values From JSON
It is possible to access values from JSON in the two ways below:

```plsql
SELECT v['key1'] FROM (SELECT PARSE_JSON('{"key1":"value1", "key2":2}') as v);
```
OR
```plsql
SELECT v:key1 FROM (SELECT PARSE_JSON('{"key1":"value1", "key2":2}') as v);
```

In addition, if required, data can becast from Variants into SQL types using the **::** operator.

### Accessing Data in Arrays (JSON)
LATERAL FLATTEN() is the function Snowflake provides for accessing data from nested arrays. [For more Information And a Tutorial Click Here!](https://community.snowflake.com/s/article/How-To-Lateral-Join-Tutorial)


---
[^1] FDN: *Flocon de Neige*, the proprietary file format which Micro-Partitions are stored as.
[^2] When defining a multi-column clustering key, the order of the columns matters. Snowflake recommends ordering columns from lowest to highest cardinality. In addition, when using a particularly high cardinality column, it is recommended to define the clustering key as an expression on that column in order to reduce the number of distinct values.

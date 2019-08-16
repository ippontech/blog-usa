---
authors:
- Pooja Krishnan
tags:
- Cloud
- Snowflake
date: 2019-08-07T10:33:00.000Z
title: "Innovative Snowflake Features Part 2: Caching"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/08/Snowflake.jpg
---
In the previous blog in this series [Innovative Snowflake Features Part 1: Architecture](https://blog.ippon.tech/innovative-snowflake-features-part-1-architecture), we walked through the Snowflake Architecture. In this follow-up, we will examine Snowflake's three caches, where they are 'stored' in the Snowflake Architecture and how they improve query performance.

---
# Snowflake Caches
Snowflake uses the three caches listed below to improve query performance. In the following sections, I will talk about each cache.
1. <a href="#metadata-cache">Metadata Cache</a>
2. <a href="#query-result-cache">Query Result Cache</a>
3. <a href="#warehouse-data-cache">Warehouse Data Cache</a>

<h2 id="metadata-cache">Metadata Cache</h2>

**Fully Managed in the Global Services Layer**

Snowflake automatically collects and manages metadata about tables and micro-partitions.

For Tables, Snowflake stores:
* Row Count
* Table Size in Bytes
* File references and table versions

For Micro-Partitions, Snowflake stores:
* The range of values (MIN/MAX values)
* Number of distinct values
* NULL Count

For Clustering, Snowflake Stores:
* The total number of Micro-Partitions
* Number of Micro-Partitions containing values overlapping with each together
* The depth of overlapping Micro-Partitions
  * This is an indication of how well-clustered a table is since as this value decreases, the number of pruned columns can increase.

All DML operations take advantage of micro-partition metadata for table maintenance. Some operations are metadata alone and require no Compute resources to complete, like the query below.

```plsql
SELECT MIN(L_SHIP_DATE), MAX(L_SHIP_DATE) FROM LINE_ITEM;
```
Snowflake also provides two system functions to view and monitor clustering metadata:
* SYSTEM$CLUSTERING_DEPTH
* SYSTEM$CLUSTERING_INFORMATION

Micro-partition metadata also allows for the precise pruning of columns in micro-partitions. When pruning, Snowflake does the following:
1. Snowflake's pruning algorithm first identifies the micro-partitions required to answer a query.
2. Snowflake will only scan the portion of those micro-partitions that contain the required columns.
3. Snowflake then uses columnar scanning of partitions so an entire micro-partition is not scanned if the submitted query filters by a single column.

<h2 id="query-result-cache">Query Result Cache</h2>

**Fully Managed in the Global Services Layer**

The query result cache is the fastest way to retrieve data from Snowflake. It contains a combination of Logical and Statistical metadata on micro-partitions and is primarily used for query compilation, as well as SHOW commands and queries against the INFORMATION_SCHEMA table.

If a user repeats a query that has already been run, and the data hasn’t changed, Snowflake will return the result it returned previously.

Typically, query results are reused if:
* The user executing the query has the necessary access privileges for all the tables used in the query.
* The new query matches the previously-executed query (with an exception for spaces).
* The table data has not changed.
* The persisted result is still available. *The Query Result Cache will purge query results after 24 hours. This period will be reset if the query is run again at any point*
* The micro-partitions have not changed.

Persisted query results can be used to post-process results. The query result cache is also used for the SHOW command.
```plsql
  SHOW tables; --Returns the tables in the current schema
```
To show the empty tables, we can do the following:
```plsql
  SELECT "schema_name", "name" as "table_name", "rows" FROM TABLE(RESULT_SCAN(LAST_QUERY_ID())) WHERE "rows" = 0;
```
In the above example, the RESULT_SCAN function returns the result set of the previous query pulled from the Query Result Cache!

<h2 id="warehouse-data-cache">Warehouse Data Cache</h2>

**Implemented in the Virtual Warehouse Layer**

All Snowflake Virtual Warehouses have attached SSD Storage. This SSD storage is used to store micro-partitions that have been pulled from the Storage Layer. Reading from SSD is faster. As such, when a warehouse receives a query to process, it will first scan the SSD cache for received queries, then pull from the Storage Layer.

The SSD Cache stores query-specific FILE HEADER and COLUMN data. This cache type has a finite size and uses the Least Recently Used policy to purge data that has not been recently used.

### How does Snowflake Reconcile Cached Data when Underlying Data Changes?
Each query submitted to a Snowflake Virtual Warehouse operates on the data set committed at the beginning of query execution. The query optimizer will check the freshness of each segment of data in the cache for the assigned compute cluster while building the query plan. This query plan will include replacing any segment of data which needs to be updated. Each virtual warehouse behaves independently and overall system data freshness is handled by the Global Services Layer as queries and updates are processed.

---
During this blog, we've examined the three cache structures Snowflake uses to improve query performance. For a study on the performance benefits of using the ResultSet and Warehouse Storage caches, look at [Caching in Snowflake Data Warehouse](https://www.analytics.today/blog/caching-in-snowflake-data-warehouse). Stay tuned for the final part of this series where we discuss some of Snowflake's data types, data formats, and semi-structured data!

As always, for more information on how Ippon Technologies, a Snowflake partner, can help your organization utilize the benefits of Snowflake for a migration from a traditional Data Warehouse, Data Lake or POC, contact sales@ipponusa.com.

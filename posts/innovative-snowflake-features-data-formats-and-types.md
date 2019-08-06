---
authors:
- Pooja Krishnan
tags:
- Cloud
- Snowflake
date: 2019-08-07T10:33:00.000Z
title: "Innovative Snowflake Features Part 3: Data Formats and Data Types"
image:
---

In the previous blog in this series [Innovative Snowflake Features Part 2: Caching](), we walked through the three Snowflake Caches and their effect on query performance. In this final blog of the series, we will examine Snowflake's data formats and types as well as how you can load and access Semi-Structured data using Snowflake.

---
# Supported Data Formats and Types
## Semi-Structured Data
Snowflake natively supports the load and access of several types of Semi-Structured data, including JSON, Avro, XML[^4] and Parquet.

In order to support loading these data-types, Snowflake has a few specialized data-types. These are:
* VARIANT - Universal type that can store values of any other type.
* ARRAY - Represents Arrays of arbitrary size with a non-negative integer index and containing values of VARIANT type[^3].
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

In addition, if required, data can be cast from Variants into SQL types using the **::** operator.

### Accessing Data in Arrays (JSON)
LATERAL FLATTEN() is the function Snowflake provides for accessing data from nested arrays. [For more Information and a Tutorial Click Here!](https://community.snowflake.com/s/article/How-To-Lateral-Join-Tutorial)


---
[^1] FDN: *Flocon de Neige*, the proprietary file format which Micro-Partitions are stored as.

[^2] When defining a multi-column clustering key, the order of the columns matters. Snowflake recommends ordering columns from lowest to highest cardinality. In addition, when using a particularly high cardinality column, it is recommended to define the clustering key as an expression on that column in order to reduce the number of distinct values.

[^3] Snowflake does not currently support fixed-size arrays or arrays of elements of a specific non-VARIANT type.

[^4] XML is supported but is currently on public preview (meaning support for XML parsing and storage is functional, just not released fully into Production).

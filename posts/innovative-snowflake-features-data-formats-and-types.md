---
authors:
- Pooja Krishnan
tags:
- Cloud
- Snowflake
date: 2019-08-07T10:33:00.000Z
title: "Innovative Snowflake Features Part 3: Some Data Types and Semi-Structured Data"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/08/Snowflake.jpg
---
In the previous blog in this series [Innovative Snowflake Features Part 2: Caching](), we walked through the three Snowflake Caches and their effect on query performance. In the final blog of the series, we will examine some of Snowflake's data formats and types as well as how you can access Semi-Structured data using Snowflake.

---
# Data Types
Snowflake supports all of the standard SQL data types, with a few differences.

## Object Names
Object names need to be unique within a given container. The same table name can exist in different schemas and/or different databases. The same schema name can exist in different databases. This is legal in Snowflake since the objects themselves are unique. Only their names are shared. You can access objects in Snowflake in one of 2 ways:

```plsql
USE DATABASE DB;
USE SCHEMA SCHEMA;
SELECT COUNT(*) FROM TABLE_NAME;
```
OR
```plsql
SELECT COUNT(*) FROM DB.SCHEMA.TABLE_NAME;
```

## Views and View Types
There are two types of views that Snowflake supports: Non-Materialized Views (simply referred to as 'Views') and Materialized Views.

### Non-Materialized Views (Views)
This view-type is the most commonly used in Snowflake. Since this view is the named definition of a query, its results are created by executing the query when the view is referenced. The results ***will not*** be stored for future use. As such, performance is slower than with materialized views.

#### Secure Views
When creating simple views in Snowflake, some of the internal optimizations require access to the underlying data in the base table(s) for the view. This may allow some data which is hidden from users of the view to be exposed through user-defined functions or other programmatic methods. The query expressions used to create a standard view is also visible to users in the SHOW VIEWS command, GET_DDL utility function, VIEWS Information Schema view, and the Query Profiler in the Web Interface.

For security reasons, you may not want to expose the underlying tables or internal structures for a view. With secure views, the view definition and details are only available to authorized users.

**Creating A Secure View:** specify the SECURE keyword in the [CREATE VIEW](https://docs.snowflake.net/manuals/sql-reference/sql/create-view.html) command. You can also convert a regular view to a secure view and vice versa by setting/unsetting the SECURE keyword in the ALTER VIEW command.

Secure Views are also the Snowflake recommended method for sharing views to other customers using Snowflake's [Data Sharing](https://docs.snowflake.net/manuals/user-guide/data-sharing-intro.html) option.

### Materialized Views
Behaves like a table. A materialized view's results are stored, which allows for faster access, but also requires storage space and maintenance, ***both of which will cost Snowflake Credits***.

Snowflake's implementation of Materialized Views provides the following unique characteristics:
* They can improve the performance of queries that use the same subquery results repeatedly.
* Materialized Views are automatically and transparently maintained by Snowflake. There is a background service which updates the materialized view after changes are made to the underlying table.
* Data accessed through materialized views is always current. If a query is run before the materialized view is updated, Snowflake either updates the view or uses the up-to-date portions of the view and retrieves the new data from the base table.

For a comparison of materialized views with tables, regular views and cached results, refer to [Working With Materialized Views](https://docs.snowflake.net/manuals/user-guide/views-materialized.html#comparison-with-tables-regular-views-and-cached-results).

#### Limitations on Creating Materialized Views
The following limitations apply when creating materialized views:
* A materialized view can query only a single table.
* The self-join of a materialized view is not allowed.
* The query cannot contain set operators like UNION or INTERSECT.
* A materialized view cannot query:
  * A materialized view
  * A regular view
  * A User Defined Table Function
* A materialized view cannot include:
  * User-Defined Functions
  * Window Functions
  * HAVING clauses
  * ORDER BY clause
  * LIMIT clause
  * GROUP BY keys that are not within the SELECT list. All GROUP BY keys in a materialized view must be a part of the SELECT list.
  * Nesting of subqueries within a materialized view
* Only the following Aggregate Functions are supported in materialized views:
  * AVG
  * BITAND_AGG
  * BITOR_AGG
  * BITXOR_AGG
  * SUM
  * COUNT
  * MIN
  * MAX
  ***The remaining aggregate functions are all NOT SUPPORTED in materialized views.***

> The aggregate functions supported in Materialized Views still have some restrictions:
> * Aggregate functions cannot be nested
> * Aggregate functions cannot be used in complex expressions
> * DISTINCT cannot be combined with Aggregate Functions
> * In a Materialized View, the aggregate functions COUNT, MIN, MAX, and SUM can be used as aggregate functions but not window functions. They also cannot be used with the OVER clause.

* Functions used in a materialized view must be deterministic
* Snowflake's Time Travel feature is not supported on materialized views

#### Limitations on Using Materialized Views
To ensure that materialized views remain consistent with the base table on which they are defined, most DML operations are banned. Truncating a materialized view is supported, but not recommended. See [TRUNCATE MATERIALIZED VIEW](https://docs.snowflake.net/manuals/sql-reference/sql/truncate-materialized-view.html) for more information. You also cannot clone a materialized view directly. If you clone a schema or DB containing, the materialized view, however, the view will be cloned and included in the new schema or DB.

Time Travel is not supported and Materialized Views are not monitored by Snowflake [Resource Monitors](https://docs.snowflake.net/manuals/user-guide/resource-monitors.html).

### Snowflake View Limitations
View definitions in Snowflake *CANNOT* be updated using ALTER VIEW. You must recreate the view with the new definition. Changes to tables are also not immediately propagated to views created on the table.
> Dropping a column on a table may make the views on that table invalid.

Views are read-only, meaning you cannot execute DML statements directly on a view. If behavior such as this is necessary, you can use a view in a subquery within a DML statement that updates the underlying base table.

```plsql
DELETE FROM MY_TABLE WHERE COL_1 > (SELECT AVG(COL_1) from VIEW_1);
```

## User-Defined Functions
Snowflake supports User-Defined Functions to perform operations not available through built-in functions provided by Snowflake. SQL and JavaScript are both supported. They can be insecure or secure and can be defined to return a singular scalar value or a set of rows.

### User-Defined Functions vs. Stored Procedures
Stored Procedure:
* Invoked as a call to a single statement
* ***MAY*** return a value
* Does not handle returned values
* JavaScript in a stored procedure calling another can handle return values.

User-Defined Functions:
* Called inside another statement
* ***MUST*** return a value
* DDL and UML operations are not permitted in User Defined functions

### When to Use What: Procedures vs. User Defined Functions vs. Materialized Views
| Stored Procedure                              | User Defined Function                                                                   | Materialized View                                                                                   |
|-----------------------------------------------|-----------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------|
| When migrating stored procedures.             | When migrating UDFs.                                                                    | When you have simple group buys/aggregation you need to persist for performance.                    |
| When you need to perform database operations. | When you need a function to work as part of a SQL statement and it must return a value. | When you have different clustering key needs.                                                       |
| For administrative tasks on database objects. | When you want to return a value for each row and each row provides a value.             | When query results don't change often but are used often and the query consumes a lot of resources. |

### Constraints:
Snowflake allows Constraints to be defined for both tables and columns. All traditional constraint types are supported: Unique, Primary, Foreign Key, NOT NULL.

***Only "NOT NULL" constraints are enforced! All other constraints are maintained only as a Metadata convenience for third-party tools.***

---
# Semi-Structured Data
Snowflake natively supports the load and access of several types of Semi-Structured data, including JSON, Avro, XML^[XML is supported but is currently on public preview (meaning support for XML parsing and storage is functional, just not released fully into Production).], ORC and Parquet.

To support loading these data structures, Snowflake has a few specialized data-types. They are:
* VARIANT - Universal type that can store values of any other type. Snowflake imposes a compressed size limit of 16MB per row.
* ARRAY - Represents Arrays of arbitrary size with a non-negative integer index and containing values of VARIANT type^[Snowflake does not currently support fixed-size arrays or arrays of elements of a specific non-VARIANT type.].
* OBJECT - Collection of key-value pairs where the key is a non-empty string and the value is of VARIANT type.

## Storing Semi-Structured Data
If you are not sure of the types of operations you will be performing on your semi-structured data, Snowflake recommends storing it in a VARIANT column. Data that is mostly regular and is comprised of mainly Strings and Integers will have similar storage requirements and query performance as VARIANT data as they will as separate columns.

For better pruning and less storage consumption, Snowflake recommends flattening object and key data into separate columns if the semi-structured data include the following:
* Dates and Timestamps as String values
* Numbers within Strings
* Arrays

Non-native values like dates and timestamps get stored as Strings when loaded into VARIANT columns, and operations on these columns can be slower and also consume more space than if these values are stored in columns with the corresponding type.

## Accessing Values From JSON
It is possible to access values from JSON in the two ways below:

```plsql
SELECT v['key1'] FROM (SELECT PARSE_JSON('{"key1":"value1", "key2":2}') as v);
```
OR
```plsql
SELECT v:key1 FROM (SELECT PARSE_JSON('{"key1":"value1", "key2":2}') as v);
```

## Casting and Null Values
If required, data can be cast from Variants into SQL types using the **::** operator. NULL values in VARIANT columns are stored as a string containing the word "null" which allows for the easy differentiation of "null" values from values that are absent, which would produce an SQL NULL. To convert a VARIANT "null" value to SQL NULL, cast it as a String.

## Accessing Data
The FLATTEN function explodes nested values into separate columns. You can use the function to query results in a WHERE clause in the following manner:

```plsql
CREATE TABLE PETS(v VARIANT);

INSERT INTO PETS SELECT PARSE_JSON ('{"species":"dog", "name":"Fido", "is_dog":"true"}');
INSERT INTO PETS SELECT PARSE_JSON ('{"species":"cat", "name":"Buddy", "is_dog":"false"}');
INSERT INTO PETS SELECT PARSE_JSON ('{"species":"cat", "name":"Dog Terror", "is_dog":"false"}');

SELECT a.v, b.key, b.value FROM PETS a, LATERAL FLATTEN(input => a.v) b
WHERE b.value LIKE '%dog%';
```
This will return the following result set:
| V                                                         | KEY     | VALUE        |
|-----------------------------------------------------------|---------|--------------|
| { "is_dog":"true", "name":"Fido", "species":"dog"}        | species | "dog"        |

### FLATTEN to List Keys
When working with unfamiliar semi-structured data, you can use the FLATTEN function with the RECURSIVE argument to return the list of distinct key names in all nested elements in an object like so:

```plsql
SELECT REGEXP_REPLACE(f.path, '\\[[0-9]+\\]', '[]') as "Path",
  TYPEOF(f.value) as "Type",
  COUNT(*) as "Count"
FROM <table>,
LATERAL FLATTEN(<variant_column>, RECURSIVE=>true) f
GROUP BY 1, 2 ORDER BY 1, 2;
```

I did this for the PETS table we created above and received the output below. Note that the types for all the columns are VARCHAR since we saved the entire JSON as a VARIANT column in the PETS table.
| Path    | Type    | Count |
|---------|---------|-------|
| is_name | VARCHAR | 3     |
| name    | VARCHAR | 3     |
| species | VARCHAR | 3     |

### FLATTEN to List Paths in Objects
As above, where we used FLATTEN with the RECURSIVE argument to list the keys, we can use the same combination to retrieve all the keys and paths in an object:
```plsql
SELECT
  t.<variant_column>,
  f.seq,
  f.key,
  f.path,
  REGEXP_COUNT(f.path, '\\.|\\[') +1 as level,
  TYPEOF(f.value) as "Type",
  f.index,
  f.value as "Current Level Value",
  f.this as "Above Level Value"
FROM <table> t,
LATERAL FLATTEN(t.<variant_column>, recursive=>true) f;
```

For a Tutorial on using JSON data with Snowflake look at [Tutorial: JSON Basics](https://docs.snowflake.net/manuals/user-guide/json-basics-tutorial.html). For a full list of built-in functions, Snowflake provides to build and access Semi-Structured Data, refer to [Snowflake SemiStructured Data Functions](https://docs.snowflake.net/manuals/sql-reference/functions-semistructured.html).

---
During this blog, we have examined Snowflake Views and discussed some of the limitations and best-recommended practices for using them. We also explored a few of the functions Snowflake provides to access Semi-Structured Data.

As always, for more information on how Ippon Technologies, a Snowflake partner, can help your organization utilize the benefits of Snowflake for a migration from a traditional Data Warehouse, Data Lake or POC, contact sales@ipponusa.com.

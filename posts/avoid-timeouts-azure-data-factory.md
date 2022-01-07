---
authors:
- Sam Portillo
tags:
- azure
- big-data
- data-engineering
date: 2022-01-07T12:21:50.000Z
title: "Avoid server-side timeouts when copying data from Azure Data Factory"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/01/adf-timeouts-cover.png
---

## Introduction
This article walks through an ingestion strategy called iterative range copy to bulk load SQL tables using Azure Data Factory (ADF). The major benefits of this approach are avoiding server-side timeouts and controlling the size of batch copies.  

This strategy only uses ADF native functions, no external services, tools, or code is required.  
<br/>

## High-level overview
This strategy uses an ADF “**Until**” function that loops through a table at a specific row range at a time. The “Until” function is a special type of loop that continues to run until a condition is met. 

Each iteration copies the rows from the given row range and writes them to a sink.For example, if you are copying 1,000 rows at a time from a table, the first iteration would copy rows from indexes 1 to 1,000, then the second iteration would copy rows 1,001 to 2,000 and so on.  
Since the row range is set to a size of 1,000 the “Until” function will stop after a set of results returning less than 1,000 rows has been processed. This means there are no more rows in the source table to copy.  

This approach works well for tables that have a unique and incrementing integer column for the primary key.  

The example in this article uses a small table but there are results provided at the end from a larger production workload on tables with hundreds of millions of records.  
<br/>

## Implementation
1. Open ADF studio.

2. Ensure you have source and sink datasets defined. I'm using a MySQL table (via Amazon Aurora) called "MySqlTable" that points to adf_main.users for the source and a Parquet file in Azure Blob Storage called "ParquetOutput" for the sink.

Refer to Figure 2 for a sample of adf_main.users (mock data generated from mockaroo.com).

![model_1](https://github.com/portillosc/blog-usa/blob/master/images/2022/01/adf-timeouts-1.png)

<p align = "center">
Figure 1: Screenshot of datasets
</p>


![model_2](https://github.com/portillosc/blog-usa/blob/master/images/2022/01/adf-timeouts-2.png)

<p align = "center">
Figure 2: First 10 rows and metadata of adf_main.users
</p>
<br/>

1. Create a new pipeline.

![model_3](https://github.com/portillosc/blog-usa/blob/master/images/2022/01/adf-timeouts-3.png)

<p align = "center">
Figure 3: Creating a pipeline in the ADF studio
</p>
<br/>

1. Add an "Until" function.

![model_4](https://github.com/portillosc/blog-usa/blob/master/images/2022/01/adf-timeouts-4.png)

<p align = "center">
Figure 4: Adding an "Until" function
</p>
<br/>

1. Initialize the limit and offset variables. These will be used within the "Until" function to keep track of the current index while copying the data chunk by chunk.

![model_5](https://github.com/portillosc/blog-usa/blob/master/images/2022/01/adf-timeouts-5.png)

<p align = "center">
Figure 5: Initializing the limit and offset variables
</p>
<br/>

1. Click the pencil icon in the "Activities" box. This area will be used to run tasks within the "Until" function.

2. Create a "Copy data" activity (under the "Move & Transform" dropdown).

3. Select the source dataset (MySqlTable for my example).

4. Select the sink dataset (Not pictured, but ParquetOutput for my example).

5.  Under the "Source" tab, choose the "Query" option next to "Use query".

6.  Enter a SQL query to select the range of data using the limit and offset variables created in step 5. 

Note, this is for a MySQL database and the SQL query will likely change for a different source SQL database.

Here's what I'm using in this example:

```
select 
    *
from adf_main.users
where id > @{variables('offset_start')}
limit @{variables('limit')}
```

![model_6](https://github.com/portillosc/blog-usa/blob/master/images/2022/01/adf-timeouts-6.png)

<p align = "center">
Figure 6: Adding a recurring "Copy data" activity
</p>
<br/>

1.  Create a "Lookup" activity.

2.  Select the same source as before as well as the "Query" option.

3.  Add a SQL statement that retrives the last ID from the set of results.

Here's what I'm using in Figure 7:

```
select
    max(t.id) as last_id
from (
    select
        id
    from adf_main.users
    where id > @{variables('offset_start')}
    limit @{variables('limit')}
) as t;
```

![model_7](https://github.com/portillosc/blog-usa/blob/master/images/2022/01/adf-timeouts-7.png)

<p align = "center">
Figure 7: A lookup activity to retrieve the last ID from the results
</p>
<br/>

1.  Add a "Set variable" activity to update the "offset_start" variable to the last seen ID. This allows the next set of results to pick up from where the previous left off.

Here's the value used in Figure 8:

```
@string(activity('users last_id').output.firstRow.last_id)
```

This is taking the output from the "Lookup" activity and setting it directly to the "offset_start" variable.

![model_8](https://github.com/portillosc/blog-usa/blob/master/images/2022/01/adf-timeouts-8.png)

<p align = "center">
Figure 8: "Set variable" activity to update the offset_start variable
</p>
<br/>

1.  Move back out into the "Until" function and set the end condition.

This is the value in Figure 9:

```
@less(activity('copy users range').output.rowsCopied, 100)
```

![model_9](https://github.com/portillosc/blog-usa/blob/master/images/2022/01/adf-timeouts-9.png)

<p align = "center">
Figure 9: Setting the end condition for the "Until" function
</p>
<br/>

This end condition tests to see whether the output of the inner "Copy data" activity is less than the limit that was set in the beginning.

If it is not, then that means there is more data to copy and the offset will increment.

Otherwise, the "Until" function will end.  
<br/>

## Results and performance
This is the result in Azure Storage of copying the adf_main.users table by 100 records at a time.

![model_10](https://github.com/portillosc/blog-usa/blob/master/images/2022/01/adf-timeouts-10.png)

<p align = "center">
Figure 10: Azure Storage output from the ADF iterative range copy
</p>
<br/>

The Parquet file format was chosen since its a good format for Data Lake storage and easily consumed for downstream transformations by Spark and Data Factory Data Flows. 
Despite the fact that each iterative copy results in its own Parquet file, **this entire directory can be treated as a single file**.

Here is a table of results that contains performance per copy iteration (not the full duration of copying the table) for the workload in this article's example as well as the aforementioned production workload on hundreds of millions of records:

![model_11](https://github.com/portillosc/blog-usa/blob/master/images/2022/01/adf-timeouts-11.png)

<p align = "center">
Table 1: Results from sample and production workloads
</p>  
<br/>
<br/>

## Conclusion
This article has detailed a method to help avoid server-side timeouts in Azure Data Factory when copying from relational SQL data sources.

Performance for every copy iteration is predictable so long as the table has an incrementing integer primary key column that's being leveraged for range copying.

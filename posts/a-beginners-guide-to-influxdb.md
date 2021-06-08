---
authors:
- Ketki Deshpande
tags:
- Data
date: 
title: "A beginner’s guide to InfluxDB - A time series database"
image: 
---

A time series database (TSDB) is specially made for data that can be evaluated as a time-series. Data like sensor outputs, market trends, cpu utilization or application performance metrics are some of the examples of data which can be best analyzed over a time period (a time-series). Most popular types of databases (SQL or No-SQL databases) are not optimized for handling large scale scans, summarization or aggregation of time-series data. Time-series databases have built-in functionality to work with aggregation, down-sampling, data lifecycle management, summarization and so on of time-series data. Let’s look at one of the popular time-series databases, InfluxDB. InfluxDB uses line protocol for sending and storing the data.

# Line Protocol
The line protocol is the text-based format that uses below syntax to write points to InfluxDB.

``` <measurement>[,<tag_key>=<tag_value>[,<tag_key>=<tag_value>]] <field_key>=<field_value>[,<field_key>=<field_value>] [<timestamp>] ```

The above syntax is used to insert a single point to an InfluxDB database. Since InfluxDB is a time-series database, the **point** here is analogous to a point on a graph that is representing a time-series data. And the collection of points is called a **series**. Now, let’s look at all the four components of the point.

The first component, **Measurement** for an InfluxDB is similar to a Table for a SQL database. Measurement holds all the tags, fields and the time values and it makes sense to name the measurement such that it describes the data contained in it.

The two components, **tag set** and **field set**, in the line protocol are sets, meaning a pair of key and value. **Tag Key** and **Tag Value** are both stored in InfluxDB as strings, while only **Field Key** is string but the **Field Value** can contain strings, integers, floats and booleans too.  The field value is the actual data that we want to save to a database and it is always accompanied by a timestamp, which makes sense since we are inserting a record to a time-series database. As you have guessed by now, the field value, which is the actual data or the field set in general as well as a timestamp are essential components without which we can not save a point to the database. Although tag set is an optional component, it is a good practice to use tag set, since tag set is indexed and would be useful in faster querying of data, as opposed to the field set, which is not indexed.

The last and the most important component of the line protocol is **time**, which is a **timestamp** in the RFC3339 UTC format. The timestamp precision can be in second, millisecond, microsecond or nanosecond. 


# Writing Data to InfluxDB
Data can be written to InfluxDB using command line interface using InfluxDB API or using client libraries. A simple curl, command line tool can be used to create and write to a database. InfluxDB provides client libraries for most of the famous languages like Java, PHP, Python, C++, Go etc. I have used Python influxdb-client and below is an example of how to write the data using python influxdb-client.

 - Start with importing the required modules in your program

    ``` python
    from influxdb_client import InfluxDBClient, Point, WritePrecision
    from influxdb_client.client.write_api import SYNCHRONOUS 
    ```

 - Then create an InfluxDB client (mention your url, token and org as below) and a writer object

    ```python
    url = "<my-url>"
    token = "<my-token>"
    client = InfluxDBClient(url=url, token=token, org=org)
    write_api = client.write_api(write_options=SYNCHRONOUS)
    ```

 - Use the module ‘Point’ that you have imported from influxes_client to convert your data to point, or alternatively create a point yourself

    ```python
    # Using built-in functionality to create Point
    p = Point("my_measurement").tag("location", "location1").field("temperature", 25).time(datetime.utcnow(),WritePrecision.NS)
    
    # Creating the point manually
    measurement = "my_measurement"
    tag_set = "location=location1"
    field_set = "temperature=25"
    date_in_nano = str(int(datetime.datetime.utcnow().timestamp() * 1000000000))
    p = f"{measurement},{tag_set} {field_set} {date_in_nano}" 
    ```
 - Finally, write the point to a database. 

    ```python
    org = "<my-org>"
    bucket = "<my-bucket>"
    write_api.write(bucket, org, p) 
    ```

Here the bucket is a location where the data is stored. It has a retention policy and org attached to it. Retention policy consists of duration, replication factor and shard group duration. The default values for these are infinite duration, one and seven days respectively.


# Querying Data from InfluxDB
Data stored in the InfluxDB can be queried either using a table structure or using a stream. 

 - Again start by importing the required modules and creating an influxes client as well as query object

    ```python
    from influxdb_client import InfluxDBClient

    url = "<my-url>"
    token = "<my-token>"
    client = InfluxDBClient(url=url, token=token, org=org) 
    query_api = client.query_api() 
    ```
 - You can query InfluxDB using a table structure, a sample code below exaplains how to do it.

    ``` python
    # query using table structure
    tables = query_api.query('from(bucket:"my-bucket") |> range(start: -10m)')
    for table in tables: 
        print(table) 
        for record in table.records: 
            print(record.values) 
    ```

 - You can use below sample code for querying using stream

    ```python
    # Query using stream
    records = query_api.query_stream('from(bucket:"my-bucket") |> range(start: -10m)')
    for record in records: 
        print(f'Temperature at {record["location"]} - {record["_value"]}')
    ```

For more information on Influx query language, please refer it's [documentation](https://docs.influxdata.com/influxdb/v1.8/query_language/spec/).


# Grafana with InfluxDB
Since InfluxDB is a time-series database, wouldn’t it be cool to actually be able to see the data like an actual graph. Grafana can be used with InfluxDB for this purpose. You can follow the integration of Grafana with the InfluxDB [here](https://docs.influxdata.com/influxdb/v1.8/tools/grafana/). Pretty dashboards can be created using Grafana that actually show how your data changes over time.

There is a lot more you can do with time-series data like checking metrics, tracking errors, keeping track with market trends and much more. Grafana integration provides visual data for analysis. Also, there are more ways you can use and query InfluxDB to make it work for your use case. I hope this post will get you started with using the InfluxDB.

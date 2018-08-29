---
authors:
- Alexis Seigneurin
tags:
- Apache Kafka
date: 2018-08-21T00:00:00.000Z
title: "Kafka tutorial #10 - KSQL"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/08/kafka-logo.png
---

We have been writing a lot of code [so far](https://blog.ippon.tech/tag/apache-kafka/) to consume a stream of data from Kafka, using Kafka's Java client, Kafka Streams, or Spark. In this post, we will see how we can use KSQL to achieve similar results.

# A few words about KSQL

KSQL is a SQL engine for Kafka. It allows you to write SQL queries to analyze a stream of data in real time. Since a stream is an _unbounded data set_ (for more details about this terminology, see [Tyler Akidau's posts](https://www.oreilly.com/ideas/the-world-beyond-batch-streaming-101)), a query with KSQL will keep generating results until you stop it.

KSQL is built on top of Kafka Streams. When you submit a query, this query will be parsed and a Kafka Streams topology will be built and executed. This means that KSQL offers similar concepts as to what Kafka Streams offers, but all with a SQL language: streams (_KStreams_), tables (_KTables_), joins, windowing functions, etc.

To use KSQL, you need to start one or more instances of KSQL Server. You can add more capacity easily by just starting more instances. All the instances will work together by exchanging information through a private topic (`_confluent-ksql-default__command_topic`).

Users can interact with KSQL through a REST API or, more conveniently, through KSQL CLI, a command line interface.

This is all we need to know for now. I will leave it to you to read [the documentation](https://docs.confluent.io/current/ksql/docs/index.html) for further details about KSQL.

# Starting the KSQL Server

The Confluent Platform ships with KSQL. I will be using the Confluent Platform version 5.0 in this post, and I will be assuming Kafka is already running.

You can start the KSQL Server with the `ksql-server-start` command:

```shell
$ ksql-server-start etc/ksql/ksql-server.properties
...
                  ===========================================
                  =        _  __ _____  ____  _             =
                  =       | |/ // ____|/ __ \| |            =
                  =       | ' /| (___ | |  | | |            =
                  =       |  <  \___ \| |  | | |            =
                  =       | . \ ____) | |__| | |____        =
                  =       |_|\_\_____/ \___\_\______|       =
                  =                                         =
                  =  Streaming SQL Engine for Apache Kafka® =
                  ===========================================

Copyright 2017-2018 Confluent Inc.

Server 5.0.0 listening on http://localhost:8088

To access the KSQL CLI, run:
ksql http://localhost:8088
```

The server is now ready for us to submit commands.

# First interactions with KSQL CLI

Let's start the CLI with the `ksql` command:

```shell
$ ksql

                  ===========================================
                  =        _  __ _____  ____  _             =
                  =       | |/ // ____|/ __ \| |            =
                  =       | ' /| (___ | |  | | |            =
                  =       |  <  \___ \| |  | | |            =
                  =       | . \ ____) | |__| | |____        =
                  =       |_|\_\_____/ \___\_\______|       =
                  =                                         =
                  =  Streaming SQL Engine for Apache Kafka® =
                  ===========================================

Copyright 2017-2018 Confluent Inc.

CLI v5.0.0, Server v5.0.0 located at http://localhost:8088

Having trouble? Type 'help' (case-insensitive) for a rundown of how things work!

ksql> 
```

The CLI offers a prompt that is very similar to the one offered by SQL databases. You can enter SQL commands or type `help` to see additional commands.

So far, KSQL doesn't _know_ our data. We have to declare either a stream or a table. As a reminder, we had created a producer in [part 1 of this series](https://blog.ippon.tech/kafka-tutorial-1-simple-producer-in-kotlin/), and we were generating JSON data in a topic called `persons`. The data looked like:

```json
{"firstName":"Quentin","lastName":"Corkery","birthDate":"1984-10-26T03:52:14.449+0000"}
{"firstName":"Lysanne","lastName":"Beer","birthDate":"1997-10-22T04:09:35.696+0000"}
{"firstName":"Neil","lastName":"Macejkovic","birthDate":"1971-08-06T18:03:11.533+0000"}
...
```

Luckily, KSQL can read data in JSON format, as well as delimited and Avro data. Let's declare a stream from our topic:

```sql
ksql> CREATE STREAM persons (firstName string, lastName string, birthDate string) WITH (kafka_topic='persons', value_format='json');
```

We can now use the `DESCRIBE` command to see the schema of the new stream:

```sql
ksql> DESCRIBE persons;

Name                 : PERSONS
 Field     | Type
---------------------------------------
 ROWTIME   | BIGINT           (system)
 ROWKEY    | VARCHAR(STRING)  (system)
 FIRSTNAME | VARCHAR(STRING)
 LASTNAME  | VARCHAR(STRING)
 BIRTHDATE | VARCHAR(STRING)
---------------------------------------
For runtime statistics and query details run: DESCRIBE EXTENDED <Stream,Table>;
```

Notice that, on top of the 3 fields we have declared, our stream has 2 extra fields: `ROWKEY` (the key of the messages) and `ROWTIME` (the timestamp of the message).

Let's run a `SELECT` command:

```sql
ksql> SELECT * FROM persons;
1534874843511 | null | Trinity | Beatty | 1958-06-16T13:10:49.824+0000
1534874844526 | null | Danyka | Hodkiewicz | 1961-04-05T20:51:32.358+0000
1534874845529 | null | Florida | Gorczany | 1992-09-01T22:44:33.114+0000
...
```

Hurray! KSQL is showing the data from our Kafka topic and, most importantly, the query never stops doing so: new lines are printed as new records are coming in the topic. Hit `Ctrl+C` to stop the query.

# Processing the data

We are now going to try to build the same processing as we did in [part 2](https://blog.ippon.tech/kafka-tutorial-2-simple-consumer-in-kotlin/), i.e. calculate the age of the persons and - if possible - output that in a topic (first name and last name as the key, age as the value):

```text
Quentin Corkery	33
Lysanne Beer	20
Neil Macejkovic	46
...
```

KSQL doesn't have a function to compare 2 dates. This leaves us with 2 options:
- we could create a UDF (User Defined Function) with Java code - a bit complicated for now
- or we can use built-in functions to get a good estimate - let's do that.

Let's start by parsing the date with the `STRINGTOTIMESTAMP` function:

```sql
ksql> SELECT firstName, lastName, birthDate, STRINGTOTIMESTAMP(birthDate,'yyyy-MM-dd''T''HH:mm:ss.SSSZ') FROM persons;

Genevieve | Turner | 1990-09-26T20:55:33.072+0000 | 654382533072
Adrian | Christiansen | 1996-01-23T14:24:06.140+0000 | 822407046140
Khalid | Ernser | 1961-08-24T17:33:27.626+0000 | -263629592374
...
```

Now, we need to compare these timestamps with today's timestamp. There is no function to get today's time, but we can use the `STRINGTOTIMESTAMP` function to parse today's date:

```sql
ksql> SELECT firstName, lastName, birthDate, STRINGTOTIMESTAMP(birthDate,'yyyy-MM-dd''T''HH:mm:ss.SSSZ'), STRINGTOTIMESTAMP('2018-08-21','yyyy-MM-dd') FROM persons;

Marquis | Friesen | 1960-02-01T11:07:04.379+0000 | -312900775621 | 1534824000000
Maci | Dare | 1983-05-17T11:56:26.007+0000 | 422020586007 | 1534824000000
...
```

Good. Now, let's compare these 2 timestamps and calculate the number of days between them:

```sql
ksql> SELECT firstName, lastName, birthDate, (STRINGTOTIMESTAMP('2018-08-21','yyyy-MM-dd')-STRINGTOTIMESTAMP(birthDate,'yyyy-MM-dd''T''HH:mm:ss.SSSZ'))/(86400*1000) FROM persons;

Buford | Wisoky | 1985-10-17T22:26:03.323+0000 | 11995
Watson | Gusikowski | 1984-01-15T13:49:54.942+0000 | 12636
...
```

Now, let's do an approximation by dividing the number of days by 365, so that we can get a number of years. A small gotcha is that, if we try to divide by `365*86400*1000`, we will run into an integer overflow: operations seem to be done with 32 bit integers and will give invalid results. Instead. let's divide the difference between the dates by 365, then by 86400, then by 1000:

```sql
ksql> SELECT firstName, lastName, birthDate, (STRINGTOTIMESTAMP('2018-08-21','yyyy-MM-dd')-STRINGTOTIMESTAMP(birthDate,'yyyy-MM-dd''T''HH:mm:ss.SSSZ'))/365/86400/1000 FROM persons;

Sherman | Hickle | 1984-06-26T03:38:21.564+0000 | 34
Alia | Gaylord | 1956-05-25T20:04:07.766+0000 | 62
Veronica | Littel | 1959-12-07T05:32:17.774+0000 | 58
...
```

Done! We have a pretty good approximation of the age of the persons.

# Writing the result to a topic

We have processed the data and printed the results in real time. Now is the time to output the results to another topic. We can use the `CREATE STREAM ... AS SELECT ...` construct. Let's start with a simple one, by writing the age as a string. We are writing the data in delimited format (remember, KSQL supports JSON, delimited and Avro) but that's fine because we will only be writing one value:

```sql
ksql> CREATE STREAM ages WITH (kafka_topic='ages', value_format='delimited') AS SELECT CAST((STRINGTOTIMESTAMP('2018-08-21','yyyy-MM-dd')-STRINGTOTIMESTAMP(birthDate,'yyyy-MM-dd''T''HH:mm:ss.SSSZ'))/365/86400/1000 AS string) AS age FROM persons;

 Message
----------------------------
 Stream created and running
----------------------------
```

The query is running in the background, in the KSQL Server. Let's run the console consumer to see the results:

```shell
$ kafka-console-consumer --bootstrap-server localhost:9092 --topic ages --property print.key=true
null	20
null	36
null	18
```

We're missing the key of the messages, so let's stop this query: use the `SHOW QUERIES` command to list the running queries, identify the query you want to stop, then use the `TERMINATE <query ID>` command to stop it. Once done, you need to delete the stream with the `DROP STREAM ages` command.

To write the key of the messages, we need to use a `PARTITION BY` clause. In our case, we want to use the concatenation of the first name and the last name as the key. Since the `PARTITION BY` clause doesn't accept calculated values, we need to create an intermediate stream. Let's do so:

```sql
ksql> CREATE STREAM persons_processed AS SELECT CONCAT(CONCAT(firstName, ' '), lastName) AS name, CAST((STRINGTOTIMESTAMP('2018-08-21','yyyy-MM-dd')-STRINGTOTIMESTAMP(birthDate,'yyyy-MM-dd''T''HH:mm:ss.SSSZ'))/365/86400/1000 AS string) AS age FROM persons PARTITION BY name;
```

We have created a stream with 2 fields (`name` and `age`), and we are not writing this stream to a topic. Let's now create the `ages` stream again, this time by selecting from the intermediate stream:

```sql
ksql> CREATE STREAM ages WITH (kafka_topic='ages', value_format='delimited') AS SELECT age FROM persons_processed;
```

The console consumer now shows the expected results:

```shell
$ kafka-console-consumer --bootstrap-server localhost:9092 --topic ages --property print.key=true
Karli Heathcote	46
Zack Cartwright	40
...
```

Now, if you exit the KSQL CLI, the queries keep running in KSQL Server and you can still see the results in the console consumer. We have basically built a Kafka Streams application with only a couple of SQL commands!

# Conclusion

KSQL is still quite young, but the product has already gained a lot of maturity. It will most likely keep being extended to support more data formats, more functions, etc.

In this post, we have seen for sure that we can do a lot with just simple SQL queries, without writing any Java code. This makes KSQL a very powerful tool for non-programmers, and definitely a serious contender for data exploration use cases.

Feel free to ask questions in the comments section below!

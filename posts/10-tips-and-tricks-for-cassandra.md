---
authors:
- Julien Dubois
categories:
- Cassandra
- JHipster
date: 2015-04-23T12:21:50.000Z
title: "10 Tips and tricks for Cassandra"
image: 
---

This is the 3rd article on this series on using Cassandra with JHipster, you can find [the first article here](http://www.ipponusa.com/using-cassandra-jhipster/), and [the second article here](http://www.ipponusa.com/modeling-data-with-cassandra-what-cql-hides-away-from-you/). Today, we are studying 10 tips and tricks we have found useful when creating new entities with JHipster!

## Use short column names

Column names take space in each cell, and if you use a big clustering key, it will be copied all over your clustered cells.

Eventually, we have found in some situations that column names (including clustering keys) take up more space than the data we wanted to store! So it is a good advice to use short column names, and short clustering keys.

## You can write data in the future

Using the CQL driver you can explicitly set up the timestamp of each of your key/value pairs. One nice trick is to set up this timestamp in the future: that will make this data immutable until the date is reached.

## Don’t use TimeUUID with a specific date

TimeUUID is a very common type for Cassandra column names, in particular when using wide rows. If you create a TimeUUID for the current time, this is no problem: your data will be stored chronologically, and your keys will be unique. However, if you force the date, then the underlying algorithm will not create a unique ID! Isn’t this surprising, for a “UUID” (Universal Unique Identifier) field?

As a result, only use TimeUUID if:

- You use them at the current date
- You force the date, but are OK with losing other data stored at the same date!

## Don’t use PreparedStatement if you insert empty columns

If you have an empty column in your PreparedStatement, the CQL driver will in fact insert a null value in Cassandra, which will end up being a tombstone.

This is a very bad behavior, as:

- Those tombstones of course take up valuable resources.
- As a result, you can easily reach the tombstone_failure_threshold (by default at 100,000 which is in fact quite a high value).

The only solution is to have one PreparedStatement per type of insert query, which can be annoying if you have a lot of empty columns! But if you have multiple empty columns, shouldn’t you have used a Map to store that data in the first place?

## Don’t use Cassandra as a queue

Using Cassandra as a queue looks like a good idea, as wide rows definitely look like queues. There are even several projects using Cassandra as a persistence layer for ActiveMQ, so this should be a good idea!

This is in fact the same problem as the previous point: when you delete data, Cassandra will create tombstones, and that will be bad for performance. Imagine you write and delete 10,000 rows, and then write 1 more row: in order to fetch that one row, Cassandra will in fact process the whole 10,001 rows…

## Use the row cache wisely

By default Cassandra uses a key cache, but whole rows can also be cached. We find this rather under-used, and we have had excellent results when storing reference data (such as countries, user profiles, etc) in memory.

However, be careful of two pitfalls:

- The row cache in fact stores a whole partition in cache (it works at the partition key level, not at the clustering key level), so putting a wide row into the row cache is a very bad idea!
- If you put the row cache off-heap, it will be outside the JVM, so Cassandra will need to deserialize it first, which will be a performance hit.

## Don’t use “select … in” queries

If you do a “select … in” on 20 keys, you will hit one coordinator node that will need to get all the required data, which can be distributed all over your cluster: it might need to reach 20 different nodes, and then it will need to gather all that data, which will put quite a lot of pressure on this coordinator node.

As the latest CQL driver can be configured to be token aware, you can use this feature to do 20 token aware, asynchronous queries. As each of those queries will directly hit the correct node storing the requested data, this will probably be more performant than doing a “select … in”, as you will gain the round trip to the coordinator node.

## Configure the retry policy when several nodes fail

This of course depends whether you prefer to have high consistency or high availability: as always, the good thing with Cassandra is that this is tunable!

If you want to have good consistency, you probably have configured your queries to use a quorum (or a local_quorum is you have multiple datacenters), but what happens if you lose 2 nodes, considering you have the usual replication factor of 3? You didn’t lose any data, but as you lost the Quorum for some data, you will start to get failed queries! A good compromise would be to tune the retry policy and use the DowngradingConsistencyRetryPolicy : this will allow you to lower your consistency level temporarily, the time for you to restore one of the failed nodes and get your quorum back again.

## Don’t forget to repair

The repair operation is very important in Cassandra, as this is what guarantees that you won’t have forgotten deletes. For example, this can happen when you had a hardware failure, and you bring the node back when some tombstones have expired on other nodes: Cassandra will see this deleted data as some new data (as tombstones have disappeared), and thus this data will be “resurrected” in your cluster.

Repairing nodes should be a regular and normal operation on your cluster, but as this has to be set up manually, we see many clusters where this is not done properly.

For your convenience, DataStax Enterprise, the commercial version of Cassandra, provides a “repair service” with OpsCenter, that does this job automatically.

## Clean up your snapshots

Taking a snapshot is cheap with Cassandra, and can often save you after doing a wrong operation. For instance, a database snapshot is automatically created when you do a truncate, and this has already been useful to us on a production system!

However, snapshots take space, and as your stored data grow, you will need that space at one time or another: so a good process is to save those snapshots outside of your cluster (for example, uploading them to Amazon S3), and then clean them up to reclaim the disk space.

---
authors:
- Lucas Ward
tags: 
- Postgresql
- DevOps
- RDS
- DMS
- AWS
date: 2023-07-06T12:00:00.000Z
title: "AWS MAP - Migrating Postgres from Self-Managed EC2 to RDS"
image: 
---

The Amazon Web Services  (AWS) Migration Acceleration Program (MAP) is a comprehensive and proven cloud migration program based on AWS’s experience migrating thousands of enterprise customers to the cloud. As an AWS MAP Competency Partner, Ippon Technologies can help you achieve your cloud migration and modernization goals. Almost every enterprise migration will require at least one database to be migrated to the cloud. 

This article will focus on the methodologies used by AWS MAP professionals to migrate a massive Postgresql database from self-managed Amazon Elastic Compute Cloud (EC2) to AWS Relational Database System (RDS) and discuss why you should consider making this change. AWS RDS is a fantastic piece of technology and will absolutely be the go-to [recommendation](https://s3-us-west-2.amazonaws.com/map-2.0-customer-documentation/included-services/MAP_Included_Services_List.pdf) by AWS MAP professionals when modernizing or migrating your environments. 

Migrating a database from one place to another, or from one version to another, does not have to be a painful experience. Below, I describe some of the lessons learned from migrating a huge Postgresql database running on self-managed EC2 to Amazon's RDS.  First, let’s discuss why you may want to migrate to RDS. Next, I will cover some of the gotchas you will need to look out for. Finally, I will describe multiple different strategies for migration and the pros and cons of each.  Buckle up, this is about to be a wild ride!!! 

# Why You Should Migrate From Self-Managed EC2 to AWS RDS

If your database came from an "earlier time" or your product started off small, you may be running a database on an AWS EC2 instance. This just means that there is a server in the cloud, and on that server, you have installed a database that serves one or many applications.  Most people in pre-RDS days will have set their database up just like this. There is nothing wrong with running your database like this for a very long time!  However, eventually, you may wish to migrate to something that offers a few quality-of-life upgrades and more flexible scaling options.  Here are a few reasons people love using RDS:
* Performance improvements
* Security
* Availability and durability
* Lower administrative burden (this one is underrated)

A more exhaustive list of RDS goodness can be found [here](https://aws.amazon.com/rds/features/).  I want to zoom in on the "lower Administrative Burden" benefit. It may not be totally clear what this means unless you have run a self-managed database for a significant period of time. Headaches like upgrading, creating read replicas, software patching, and blue-green deployments, can consume a large chunk of your time when you are running a self-managed database. All of these features can be automated and are available right out of the box with AWS’s RDS.

# Things to Consider When Planning a Migration

So you have committed to migrating, and now you are undertaking the task of planning the migration. What things do you need to consider? What should you look into and pre-plan for? I will describe some of the things that we ran into when migrating a 6.5-terabyte database from EC2 to RDS. Although this was a Postgresql Database running version 9.6, the lessons learned apply to almost every database system available on RDS. This blog is not meant to be a replacement for your own experiments and test runs, so please make sure you still do your due diligence.

## Business Impacts

There are a few areas of impact on the business when performing a database migration. First, disruption to business operations: Performing a database migration often requires downtime or reduced performance for the system, which can impact business operations. This can result in lost revenue, reduced productivity, and decreased customer satisfaction. Depending on your method of migration, you may even need to pause certain internal development efforts that would see the database being manipulated in certain ways. 

Second, cost and resource implications: Database migrations can be complex and time-consuming, which can require significant investment in terms of time, resources, and cost. Depending on the size and complexity of the database, your business may need to allocate significant resources to the migration project, which can impact other priorities and initiatives. In other words, not only are you potentially having to pause other projects, but you may even need to pull engineers off of other projects, to aid in the effort of migration.

Another thing that will impact your migration strategy from the business side is maintenance window length. If your product is largely not used overnight, then you could theoretically have a nice long maintenance window. The opposite side of this coin is no maintenance window. The length of the maintenance window will be a key factor in choosing your migration strategy. If migrating your database with little to no maintenance window sounds appealing to you, then be sure to read all the way to the end!

## Get to Know the Database Schema

The level of complexity of the database schema will also impact how you migrate the database. The schema defines the structure of the database, including the tables, columns, data types, and relationships between them. Understanding the schema can provide insights into how data is organized and stored, which can inform decisions about how to migrate the data to a new database. Here is a non-exhaustive list of things to identify:
* Dependencies between tables - impacts migration order
* Data types and constraints - impacts target database preparation
* Custom functions and procedures - impacts target database preparation
* Unused tables and columns - save time and resources by excluding
* Performance issues - optimize joins and indexes to ease migration
* Primary Keys and Unique Constraints - impacts migration strategy

It is paramount that you understand the database you are migrating at the deepest level possible. If the database is simple, you may be able to get away with just kicking off the migration, but if there is any level of complexity in the schema, you could run into errors or end up wasting resources. Also, be sure to have a way to capture changes in the schema during migration, or pause development that may result in schema changes.

## Get to Know the Data

Getting to know the database schema, which tells you the shape of the data, is not enough. You also need to have an intimate understanding of the size of the tables. Tables with many rows, tables with lots of columns (wide tables), and tables that store LOBS (large objects) can all have different impacts on migration. Pull some data, run some tests, and do some calculations. If your database contains LOBS, then find out what the maximum size of a single LOB is, as this will be crucial when utilizing certain migration strategies. Having a good idea of how data flows into the database tables is also crucial. For instance, if a table is truncated and then re-populated with new data on a daily basis by a cron job or some other mechanism, it makes sense to just migrate that table on the day of the cut-over. Setting up long-running replication on a table like this is a recipe for disaster!

Tables that are very wide, or have lots of columns, are gonna take longer to migrate than tables that are *thin*, or don't have very many rows. This is helpful information to have beforehand as it aids in building your migration strategy. You may decide to migrate these larger tables separately from the rest of the data or in parallel. Get to know the data, and don't make any assumptions.

## Traffic Trends and Throughput

There are several migration strategies that involve dumping data, either from a live database or a paused read replica. During the time between the data dump being initialized and the "full load" being complete, new data and updates can build up. If you have a large database, on the order of Terabytes, it may take several days to get all of the data transferred over. During these days, new data may be piling up that also needs to be migrated. This data then has to be strategically inserted into the new database in such a way that it fully catches up, and allows for a cutover.

To mitigate the impact of traffic and throughput on database migration, it is important to carefully plan and execute the migration with the support of experienced professionals. This may involve selecting a migration method that minimizes downtime and performance impacts, optimizing the source and target databases to ensure that they can accommodate the migration, and carefully testing the migration process to identify and resolve any potential issues before they impact business operations. Additionally, it may be necessary to communicate with users and stakeholders about the migration and its potential impacts on business operations, to ensure that expectations are properly managed and disruptions are minimized.

## Consider Database Versions

When migrating to RDS, you may need to find out if your current database version is supported. As an example, if you are running Postgresql 9.6 and are looking to migrate to RDS, the earliest supported version is Postgresql version 10. In order to complete the migration successfully, you will either need to update the source database or pick a migration strategy that allows migrating across major database versions. In addition to restrictions on strategy, there could be impacts on what features are available in the target database vs. the source database. This is another area that you will want to make sure to perform tests and experiments to verify your migration strategy. 

# Pick Your Strategy

Now that we have an in-depth understanding of the size, shape, and mechanisms of our source database, it's time to pick a strategy. Multiple strategies exist and some can even be mixed and matched. Do your research, test your strategy, and if you fail, learn from your mistakes, and don't be afraid to pivot. The goal here is to leverage a strategy that works for your database and also takes into consideration the business requirements. If all of this seems very overwhelming, fear not, AWS MAP partners, like the engineers at Ippon Technologies, are here to guide you or to perform the migration for you!

## Third-Party Tools

There are many third-party tools available to perform database migrations. These tools will utilize some of the strategies described below, but "under the hood". There are several benefits to using third-party tools:
* Reduced manual effort
* Improved accuracy
* Reduced downtime
* Simplified testing and validation
* Expert Support
* Cost-effectiveness

Tools like Amazon's Database Migration Service (DMS) can do a lot of the legwork for you and save valuable time and resources. It's important to acknowledge, however, that some databases are just too large and complex for some of the "one size fits all" solutions that are out there. If you have tried a tool and it has come up short, then you will want to employ a more finely tuned manual strategy.

## Using pg_basebackup

`Pg_basebackup` cannot be used to migrate to AWS’s RDS, but can be used to migrate from one self-managed EC2 instance to a different one. I mention it here simply for thoroughness, as you are likely to come across this method in your research. `pg_basebackup` creates a *physical* backup, at the file system level. This tends to be much faster than the logical backups that I will describe below. The reason this cannot be used to migrate to RDS is that the underlying filesystem is not exposed to the end user when using AWS’s RDS.

It is also worth noting, that on large databases, this method will require a decent length maintenance window, as the database needs to be offline for it to be successful. 

## Using pg_dump and pg_restore

`Pg_dump` and `pg_restore` will create a logical backup of your database. Instead of moving the actual data on the disk, it will use SQL-like statements to completely rebuild the database. There are loads of different options when employing this method. For instance, you can use `pg_dump` to dump your data into .csv files or have the files compressed. Once the data has been dumped onto the hard disk, it can then be loaded into the new database with `pg_restore`.

Another strategy utilizing `pg_dump` involves setting the output to stdin and piping it into SQL copy statements, so it doesn't hit the hard disk at all. This is helpful for really large databases where there may not be enough remaining room on disk, or where time is of the essence and it is better to just send the data over the network into the target database as it is being dumped. This strategy can also be used to migrate tables that may not be compatible with the `.csv` format.

When utilizing this strategy, you will want to make sure there are no schema differences between the source and target database. This strategy can be used to perform the entire migration, if you are allowed a long enough maintenance window. This strategy can also be used in the hybrid approach described below, to migrate *most* of the data, and then use some form of replication to get the last little bit (primarily for migrations that are not allotted a significant length maintenance window).

## Using pg_logical

Logical replication is an amazing tool that can be used to create read replicas of databases and can stream data near real-time from a primary to a secondary database. It leverages the underlying native technology of Postgresql to achieve this. It can also be used to sync databases and perform migrations. There are loads of different options and methodologies that can be used to set this up and utilize it for migrations. The primary benefit is migrating a live database with little to no downtime, resulting in a very short maintenance window for a "cut over".

In Postgresql, version 9.6 and below, `pg_logical` is one of the only ways to achieve logical replication. Without the plugin, replication is performed via "log shipping" and "log streaming". Although these methods of replication can be used to migrate to RDS, they do not support migrating across major versions. `pg_logical` can migrate a database across major versions and offers the most flexibility when it comes to specifying which tables to migrate, and when. This tool does not come without a steep learning curve, so be sure to do your research. If you are running Postgresql 10 or higher, logical replication is built in, although, using the `pg_logical` plugin may still provide some additional features.

## Hybrid approach

Sometimes you just need the best of both worlds. A full data load of an entire database, and then ongoing replication to get the last little bit and prepare for a cut-over maintenance window. Hybrid approaches can be achieved by utilizing `pg_dump`, `pg_restore`, and `pg_logical` in combination. The general idea is that you would create a logical replication slot on the primary to store up `wal_logs`. Next, you would dump the data from a paused read replica, being sure to grab the "log sequence number" from the operation. Once all the data has been loaded, you would start replicating the rest of the data using `pg_logical`, until the target database is *caught up*. Once the target database is replicating, it is just a matter of planning your cut-over procedure.

Be sure to check back frequently for an in-depth guide on utilizing this hybrid approach to perform migrations.

# Conclusion

Large database migrations can be a nasty bit of business. They are, however, a necessary evil. Mastering the different migration techniques described above and gaining an in-depth understanding of the source database can ease the process and help you build confidence. Be patient, do your research, execute your tests and experiments, and you will be sure to find success. Alternatively, drop us a line at sales@ipponusa.com. We would love to help you make your database migration a huge success. As an AWS MAP partner, Ippon Technologies is positioned to enable your company to move forward with its road map. Also, check back soon for several different detailed migration strategy write-ups on our blog site.

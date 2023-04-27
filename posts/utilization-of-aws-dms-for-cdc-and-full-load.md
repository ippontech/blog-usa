---
authors:
- Ricardo Corassa
- Noah Shaw
tags:
- AWS DMS
date: 2022-10-12
title: "From MySQL to Postgres: A Successful Database Migration using AWS DMS"
image: /images/2022/10/dms-banner.png
---


# From MySQL to Postgres: A Successful Database Migration using AWS DMS

The AWS Database Migration Service (DMS) is a powerful tool for migrating relational databases, data warehouses, NoSQL databases, and other types of data stores. In this blog post, we will explore the use of DMS for both full load and continuous data replication (CDC) during a database migration.

Recently, we received the challenge of migrating a database while simultaneously improving the performance and architecture of the data. After analyzing application performance and comparing with internal similar services, a Postgres Database was suggested as part of the improvements and a review on tables, relationships and indexes. The project also consisted on splitting a microservice by isolating areas of business logic and developing a new automated process, but keeping the services depending on the original database during the entire duration of the project.  We ultimately decided that we would create a new database with the necessary tables and synchronize them utilizing DMS. 

During this process, we also needed to improve the performance of some queries and the relationship of the new tables, which consisted of various composite primary keys. These keys would become a column primary key in the new data structure, thereby also introducing the need to create new foreign key relationships. 

These requirements turned into research and development, which we’ve decided to share in this post. Let's first start with an overview of what DMS is.

## What is a DMS?

AWS DMS (Database Migration Service) is a service used to migrate relational databases, data warehouses, NoSQL databases, and other types of data stores. Enabling the continuous replication of your data with high availability, keeping two different heterogeneous databases in real-time synchronization. 

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/10/dms.png)

## Database Defitions

For our example let's consider the database with the following entities to be migrated from a MySQL database to Postgres.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/10/dms-tables-no-fk.png)

First, we notice that this entity diagram does not have foreign keys and the relationship between the tables is "informal", as defined only on joins. On the new database, we are required to implement new relationships to improve the consistency and performance of our data.

To improve the relationship between entities, we targeted the following database design.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/10/dms-tables.png)

This requires the creation of a primary key on the target tables and the use of new primary and foreign keys. Consequently, each record will now have a unique id, as well as, a unique column for foreign keys. Improving the relationship between entities and increasing the performance for joins.

To solve this problem we decided to conduct the following steps: 
Create a new target database on Postgres
Create the tables on the new target database
Migrate the existing data (full load)
Create indexes, primary and foreign keys
Create an ongoing replication task (change data capture)

The AWS documentation recommends dropping all indexes and constraints before a full load. This best practice should be considered early on the project, as you'll need to have a final decision on the design of your entity diagram before starting an ongoing task in a production environment. Each modification on the databases will require stopping and updating the ongoing tasks, which might require a new full load before restarting the ongoing task. 

## Create the DMS to migrate the existing data

When setting up DMS, you must first complete the items listed below. As part of the task creation process you will need to have access to the following resources as a bare minimum: 

1.  **Create the source database** (Example: MySQL)

    Acts as your data source. This is the data that you intend to replicate in some manner. 

2.  **Create the target database** (Example: PostgreSQL)

    Acts as your data destination.

3.  **Populate data**

    Validate that the source database contains all the required information you intend to replicate. If you'd like to alter the data, this can be achieved using a combination of mapping and transformation rules as part of the DMS task. 

4.  **Create Endpoints for your databases**

    Inside the AWS console (*AWS > Endpoints*) you will need to create an entry for your source and target database utilizing the corresponding connection details.

5.  **Setup a Replication Instance** 
    
    Employed to connect your source database and format data for the consuming target data store. Most transactions happen in memory, but more significant transactions may be offloaded to the disk.

6.  **Create The DMS Task (Database Migration Service)**

    Responsible for the orchestration of each of the prior steps. The DMS Task creates a manageable job that can be executed to load and replicate data events.

## Execute the Full Load

The full load is responsible for propagating all data from your source database to your target database. For details on your full load enable metrics and view the Table Statistics section of your DMS task after execution.

## Generate the Primary Keys and other constraints

Primary keys can cause a headache when setting up DMS if not appropriately handled. Follow the steps listed below to ensure your migration process runs smoothly. 

1.  **Identify the primary keys and their data types **
2.  **Disable all primary key constraints on the target database **
    
    If new primary keys exist that were not part of the source database, don’t include them in your table schematic on your destination prior to the full load.

3.  **Execute the full load**

4.  **Create all new primary keys, indexes, and foreign keys in your target database**
    This includes both the primary key column as well as the constraint. 
    
    If you intend to populate any of the columns with new data do so now. 

5.  **Update Foreign Keys**

    This is the part where we had the most trouble solving. In order to improve the performance, we added new Primary keys on the target database, that were also used as Foreign keys. Introducing a problem for CDC syncronization as the values for those Primary Keys were generated after the record went from target to source. Then those value were used on other tables as the foreign key and we would only have it after the migration happens. In order to solve that we considered 2 approaches:

    i.  After the load was done, use a script to generate the keys inside our service.

    ii.  Use a Postgres Event Trigger to update the columns with the Foreign Key.


## Start Ongoing Replication

The ongoing replication is responsible for synchronizing both databases in real-time. Ongoing replication enables the total capture of all CDC events that affect your source table. For more details on the metrics of your ongoing replication task refers to the Table Statistics section of your DMS task.

## Create alerts for your DMS tasks

Once your ongoing replication starts, if for any reason you have a failure on some of the tasks executed, the task will stop the synchronization and you'll be required to solve the problem prior to resuming. One way to receive notifications for failures is to create an Event Subscription.

1.  Open the Amazon DMS at the console.

2.  Select **Event Subscription**.

3.  Select **Create an event subscription**.

4.  At **Target** select an **Existing SNS Topic**. (If you don't have an existing topic, [follow these steps to create one](https://docs.aws.amazon.com/sns/latest/dg/sns-create-topic.html)).

5.  Under **Events categories**, choose the option **Select specific event categories**.

6.  Then choose **State change** and **Failure** under the event categories.

7.  For **Replication tasks** and Select specific tasks.

8.  Then **select the ongoing** task you've created.
   
9.  **Review** and click on **Create event Subscription**.
    
You can create subscriptions for other events. In this case, we decided to capture in case of a Failure.

Overall, DMS is a useful tool for cases where we need to keep databases in sync and makes it easy to manage those integrations, as it's compatible with different types of databases. The most valuable feature in our project was the utilization of CDC for real time synchronization, allowing the team to develop the new microservice at the same time of maintaining the original databases in place. Although our use case did not call for integrations with other services,  DMS also offers integrations with Kinesis, which makes it a useful tool for streaming data and data warehouse solutions. Overall DMS was a good solution for our case but lacks more documentation, which is the main motivation for us to create this blog post. DMS could also benefit from  a better logging solution, as in some cases we didn't have an indication of the problem generating the failure of the service. 

If you want to know more about or are considering using DMS on your project, reach out to us at info@ippon.tech.

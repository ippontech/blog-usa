---
authors:
- Lucas Ward
tags:
- DevOps
- Cloud
- Postgres
date: 2022-05-24T10:33:00.000Z
title: "Hands on Guide to Migrating Postgres with pglogical and AWS DMS Part 2 of 2"
image:
---

Welcome back to the Hands on Guide to Migrating Postgres with `pglogical` and AWS DMS! This is part 2 of 2 for the series. In part 1, we prepared our environment, got to know our data, setup our maintenance read replica, setup our RDS instance and executed Phase I - Full Load. In part 2 of this series, this part, we will setup and configure Amazon Web Service's Database Migration System to finish the migration.

# Prerequisites

### Items from Phase I

If you are coming here from part 1, you should have everything that you need to proceed.
- A log sequence number from the paused read replica.
- The names of all the Logical Replication Slots that you setup in part 1.
- A list of tables that have a Primary Key and a separate Unique Constraint.
- An in-depth understanding of the data contained within the Source Database.
Before we dive into the steps to setup and configure DMS, let's talk about some things we need to figure out before we proceed. Remember when we compiled the list of tables that have a Primary Key and a separate Unique Constraint? We need another similar list, but this time, a list of tables that have Large Objects, also known as LOBs. If you are unfamiliar with LOBs, then read up on them in the [documentation](https://www.postgresql.org/docs/15/largeobjects.html).

### Large Objects

When performing Change Data Capture (CDC) with AWS DMS, tables with large objects have a couple of specific settings that need to be adjusted. In order to get the settings just right, we need to have a list of tables that contain LOBs. More importantly, for each table that has LOBs, we need a value, in kilobytes, of the largest possible LOB for that table. Here is an example, let's say we have a database table that is part of an integration, and one of the columns stores a large amount of JSON. We will need to figure out the maximum size of that JSON object across all the rows of the table. This will be a bit of a guessing game. You could take a few rows at random and check the size of the column and make an educated guess. 

In addition to knowing what tables have LOBs and what size to set for the max LOB, we also need to identify if there are any tables where "LOB Truncation" is acceptable. This will largely depend on the usage of the LOBs in this table, but if you are okay with *some *truncations*, i.e. - some missing JSON characters off the end of a large object that contains JSON, then there are different settings to be used that can speed up the process.

When you have finished your analysis, you should have something akin to this table as the result:
| Table       | Max LOB Size (Kb) | Truncations OKAY? |
| ----------- | ----------------- |-------------------|
| table_name_1| 325               | Yes               |
| table_name_2| 1200              | No                |
| table_name_3| 36000             | Yes               |

Armed with our list of tables that have Primary Keys and Separate Unique Constraints as well as our list of tables with LOBs and the *estimated max LOB size*, we are ready to setup and configure DMS. Please note, that with regards to the former list (primary keys and unique constraints), you should already have logically separated these tables into different replication sets (and therefore different replication slots) in the previous part of this series. If you need a refresher, refer to the top part of the section titled "Migration Steps - Phase I - Full Load". 

# Migration Steps - Phase II - Change Data Capture

Amazon Web Services Database Migration System is an awesome tool to have at your disposal and will greatly ease this part of the migration process. There is loads of documentation surrounding the tool, and I encourage you to familiarize your self with it before you attempt this part of the migration.

### Step 1 - Create a Replication Instance

Log in to your AWS management console and navigate to the DMS page. Follow [this guide](https://repost.aws/knowledge-center/create-aws-dms-replication-instance) to setup your DMS Replication instance. This is the server that will handle the CDC tasks - that is, receive the data from the source database and apply it to the target database. Be sure to take into consideration the volume and size of the data and spec your machine out based on that information. It may take some testing to get this value just right.

### Step 2 - Setup your DMS Endpoints

We will need a separate DMS source endpoint for each replication slot that we created in Part 1. Go through the process of creating the endpoint as you normally would - Specify source database connection information, give it a name that makes sense, like `dms_slot_1_endpoint`. Before you press create or even test the endpoint, we have one last setting to apply. 

At the very bottom of the endpoint creation screen is a checkbox that says `Use endpoint connection attributes`. Click this checkbox and a text field will appear. Add the following text to the text field *Extra endpoint connection attributes*:
```bash
PluginName=PGLOGICAL;slotName=dms_slot_01
```

Test the connection and then press save. Repeat this process for all of the replication slots that we created in Phase I of the migration.

Now that we have source endpoints for all of the replication slots that exist on the source database, we need to create a target endpoint. Follow the same process as above but instead specify the connection string for your RDS instance and skip the bit about using "extra endpoint connection attributes".

### Step 3 - Create the DMS Tasks

In order to setup our DMS Tasks, we will navigate to the DMS console and click on "Create a DMS Task". The absolute most critical step here, is to select "Replicate data changes only". We do not want to direct DMS to perform full load, as we have already done this step outside of DMS. Specify the Source Endpoint for your first "Replication Slot Endpoint" and choose the target database (RDS endpoint). This setup part, and the following part, will be the same across all of the DMS tasks you create.

One of the next settings is: `CDC Start mode for source transactions` and there is an option for `Custom CDC start point for source transactions`. Choose `Specify a log sequence number`. In the `System change number` text field, input the log sequence number that we ascertained from `Step 6 - Capture the Log Sequence Number` in Part 1 of this series. This critical step will tell DMS where it should "pick up" the change data capture on the Source Database.

The only other settings that will be the same across all DMS tasks are as follows:
- do create recovery table on target DB
- do nothing - target table prep (we already handled target table prep before full load)
- turn on cloudwatch and select all logs
- create recovery table in `public` schema (or what ever schema name you use)
- enable all awsdms control tables

Here is where things change based on which DMS Task, and therefore which replication slot, and therefore which tables we are considering...

If the tables that this task references `Have a primary key and a separate unique constraint`, make sure `batch-optimized apply` is disabled. For all other tasks (replication slots), it's okay to turn this on.

If the tables that this task references are included in your `Large Object` or `LOB` list that we compiled above, then we have specific settings to take care of. Set the large object settings to `Limited LOB Mode` and put in a value that makes sense for the "maximum LOB size" based off of your list. If the DMS Task has a table where some truncations are not okay, then we have some more special settings to add below. 

Navigate passed the "full load" settings, as they won't be used, and go to the "Mapping Rules". This is where we will tell DMS all of the tables that exist in the replication slot that we are setting up. I recommend using the JSON editor, especially if you have tables in the slot that cannot have LOB truncations. Using the list from phase I that we used to setup our replication sets, make a list of tables in JSON format for the DMS task that matches exactly.

Here is some sample JSON that will match the sample replication set from part I:
```JSON
{
    "rules": [
        {
            "rule-type": "selection",
            "rule-id": "348698101",
            "rule-name": "348698101",
            "object-locator": {
                "schema-name": "public",
                "table-name": "some_table_001"
            },
            "rule-action": "include",
            "filters": []
        },
        {
            "rule-type": "selection",
            "rule-id": "348698102",
            "rule-name": "348698102",
            "object-locator": {
                "schema-name": "public",
                "table-name": "some_table_002"
            },
            "rule-action": "include",
            "filters": []
        },
        {
            "rule-type": "selection",
            "rule-id": "348698103",
            "rule-name": "348698103",
            "object-locator": {
                "schema-name": "public",
                "table-name": "some_table_003"
            },
            "rule-action": "include",
            "filters": []
        },
        {
            "rule-type": "table-settings",
            "rule-id": "031787795",
            "rule-name": "some_table_001-lob",
            "object-locator": {
                "schema-name": "public",
                "table-name": "some_table_001"
            },
            "lob-settings": {
                "mode": "unlimited",
                "bulk-max-size": "64"
            }
        }
    ]
}
```

Notice the `rule-type` - `table-settings` portion of the above example. The first 3 JSON "chunks" reference a table selection rule - specifying to the DMS task what tables are in what slot. The 4th section, that says `table-settings` is how you specify a LOB setting where truncations are not allowed. This setting will override the previously set limited LOB mode for just the table specified.

Finally, check the option that says you will `start manually` after creation. This way we can create all of our tasks, double check all of the settings and table mappings, and then start them all back to back. 

If any of the above doesn't make sense, it probably means you should take a turn through the DMS documentation. There is a wealth of information contained there-with-in and the amount of possible configuration options is HUGE. If you are still stuck, we have AWS MAP professionals on stand by ready to help. Please don't hesitate to reach out to our sales team at sales@ipponusa.com. 

### Step 4 - Kick Off Phase II - CDC

Now that all of our DMS Tasks have been created, full load has finished, and settings have been double and triple checked, we are ready to start the DMS Tasks. It is my recommendation to start each task, one at a time, with a "few minutes" interval between each task.  As you are starting the tasks and monitoring cloud watch logs, there are a couple things to watch out for. 

In the cloud watch logs, you should see that it says "full load complete", this just means that `full load` has been skipped and DMS is acknowledging it. Eventually, the logs should start saying `replication has started`. Don't be alarmed if you see warnings or info messages about running out of swap space or pausing replication, this is perfectly normal during the "catch up" phase. 

### Step 5 - Monitoring the Catch Up Phase of CDC

The catch up phase is where DMS CDC attempts to migrate all of the data that has built up in the Logical Replication Slots while we were performing Full Load and Setup. In other words, you should expect to see some pauses, and if you check the metrics on each task, you should expect to see some latency. Keep an eye on the metrics for the replication instance to see if Memory and Storage amounts are well within range.

You can also monitor the progress of the catch up phase by logging into the Target RDS Instance with `psql`. Once logged in, run this SQL query to check on the progress:
```sql
--Run On Target RDS Instance
target_database=> select task_name, task_status, source_timestamp_applied from awsdms_status order by task_name desc;
                task_name                |    task_status    | source_timestamp_applied
-----------------------------------------+-------------------+--------------------------
 XCXNJODDOAXXJXNDZVOXTHREG7YHWZIZQ6DIKYA | CHANGE PROCESSING | 2023-05-25 08:45:11
 OAORYYEYKB4KE7P6BDGUSCOTIOUWIHVQ365ILDY | CHANGE PROCESSING | 2023-05-26 13:05:08
 IGGOX4DMKOL565NJKQCLK5Z3RBN4GNMH6WVJLTQ | CHANGE PROCESSING | 2023-05-27 11:11:32
 6NY4C53LQWXLOJ5LOQW6RXWDQAKDHZCDC42ZPDI | CHANGE PROCESSING | 2023-05-26 13:12:45
 4XHASLIMNZP2BDBZRUQGXAJAGAL3XLBZQLJP27A | CHANGE PROCESSING | 2023-05-28 23:05:12
 2DSG7USR6Q27VQJ2ZWRAJS5GLLHH3WXXTNFRMPI | CHANGE PROCESSING | 2023-05-26 13:07:07
(6 rows)
```
There are three things to note here:
- The `task_name` column will match up to the AWS ARN for the DMS Task.
  - This is the best way to reconcile the status of each AWS Task to the Replication Slot that it maps to, and therefore the tables.
- The `task_status` column will usually just say `CHANGE PROCESSING`. If the DMS task needs to pause for what ever reason, you may see this reporting `Paused`. 
  - This column is not a very reliable way to tell the actual status. When a DMS CDC task fails, this column will report `CHANGE PROCESSING` and the `source_applied_timestamp` will report the last time it was able to communicate. For this reason, it is best to check status of the Task in the DMS console, and to check the "catch up" postition using the above method.
- The `source_timestamp_applied` column will tell you at what point in time the replication has caught up to.
  - Notice that some of the above tasks are further behind then others. As long as the timestamp is catching up faster than *time is passing* then we are good to go!

The speed at which the *data delta* can be consumed and that CDC can catch up to present time will depend on a number of different factors. First and foremost is the tables, and therefore subsequent data, that is contained in the replication slot. A replication slot with really big tables that get updated a lot will take longer to catch up then a slot that just contains a few smaller tables. Pay close attention to the "speed of catch up" as well as the "metrics and monitoring" garnered from the DMS Console for the task at hand. This is the best way to gauge how well you did when separating out tasks from a data throughput stand point. If you end up having a task that can't catch up, you may need to split it into two replication slots, or more, to spread some of that data out. 

Once a single task has caught up to present time, it is safe to begin running QA on those tables.  Keep in mind that replication from the Source Database to the Target RDS instance through DMS will have a little bit of latency - possibly up to `minutes`. Reference the "Quick QA" section from part 1 for an example query to run. More information regarding QA analysis on the migration will be provided in subsequent sections.

# Handling Errors and Running Subsequent Attempts

Throughout this process, you are certain to see some errors. Let's cover a few common ones so that they don't catch you off guard.

### Truncations

First, let's talk about truncations. When dealing with Large Objects (LOBs), if DMS finds a LOB that is bigger than the size specified by the limited LOB mode setting, and the table does not have a special `table-setting` for LOBs in the table mapping, then you may see an error like this:
```bash
2023-05-24T13:52:50 [SOURCE_CAPTURE  ]W:  Value of column 'column_name_001' in table 'schema.table_name_001' was truncated to 131072 bytes, actual length: 143687 bytes  (postgres_pglogical.c:2550)
```
As you can see, our limited LOB size was set to 132kb and we encountered a value for a column that was more than that. Because of this, the data aws truncated. If it is okay for a few truncations to occur on this table, then we can safely ignore it. The issue is when we are seeing lots of truncations. In that case, take note of the "actual length" and readjust the max LOB size for your next attempt. 

Most truncations that you see in cloudwatch will have a matching row in the `awsdms_apply_exceptions` table on the target. This is a great place to go for additional information regarding truncations. For example, if a JSON column is truncated, it could result in an apply exception "malformed JSON" - meaning the row that the truncated column came from had some JSON that got cut off. 

### Batch Apply Errors

You may see some rows in Cloudwatch logs that read like this:
```bash
[TARGET_APPLY ]I: Bulk apply operation failed. Trying to execute bulk statements in 'one-by-one' mode (bulk_apply.c:2000
```
This by itself is not necessarily an error, rather just an info statement, but I have seen in my experience, that when you see this log statement quite frequently, data integrity issues can be present. It is most common on CDC replication tasks that have many small tables and also have `batch apply` mode enabled. If you see this in your cloudwatch logs, don't panic. Instead, mark down what slot and therefore what tables and make sure to perform QA on these tables. Compare the values for certain time frames contained with in the "catch up" phase of CDC. If you do see data inconsistencies, consider disabling this setting on subsequent attempts.

### Check the awsdms_apply_exceptions table

DMS will create a table on the target that will contain any rows that failed to be inserted. If you have wide spread misconfiguration, this table can quickly fill up. In a successful migration attempt, this table should be very small, containing only a few truncations, any duplicate key value errors that you would normally see in the production database operation logs, and failed "UPDATE" statements that say 0 rows affected.

Most of the error messages will contain the failed SQL, including the table name and id - meaning they can be back filled after a successful migration. Keep an eye on this table and adjust your task settings as you see fit.

### Inexplicable Failures

If you are running Phase II and you notice that one of the tasks have changed to the failure state - it can be a bit hard to diagnose the issue sometimes. I highly recommend, if the root cause is not obvious, to open up a support case with AWS Support. The support engineers will be able to see additional logs and information that is not readily available to the customer.

### Managing Restarts

In the above paragraphs I have said a few times "update the settings for subsequent migration attempts".  There are certain types of settings changes that will require you to start fresh, all the way back from Phase I. Other types of settings changes can be updated on the fly. Sometime it is pertinent to scrap the entire migration attempt and start back from step 1. I will try to describe how to manage these restarts and when a "full restart" is required versus a "partial restart". 

Partial Restart - If all of you DMS tasks are caught up except 1, and QA has passed the other slots and said the data looks good, then you may just want to re run the whole migration for just the tables contained within the one failed slot. Here are the steps to perform a partial re-migration:
1) Stop the failed DMS Task.
2) Truncate the tables on the target RDS instance that are contained with the slot.
3) Make any changes to the DMS task or to the full load steps that you need to fix the issue you have run into.
4) Return to step 1 from part 1, this time drop just the failed replication slot. Re-create the replication sets, pause the read replica, run the pg_dump commands etc... but just for the tables that are affected. 
5) Using `pglogical` and DMS, it is 100% feasible to just migrate a *slice* of the database.

Full Restart - If you identify through QA that wide spread data inconsistencies exist, of that there is a "data gap" between the time full load finished and the time CDC picked up, you will need to re-run the entire migration for the whole database. Don't be discouraged! Take this as an opportunity to learn from your mistakes, gain understanding, and polish your migration strategy. 

### Long Term Cross Version Replication is not a Design Target

Once the database has been fully migrated and CDC is on-going, it is time to plan your cut-over process. You do not want to depend on replication through CDC for a long period of time. This is especially true if you are replication across versions Postgres. Cross system, cross version replication is not a design target of `pglogical` or DMS. Use the tools to perform the migration, run QA as quick as possible, and then perform your cut-over. 

# Conclusion

In Part 2 of this series, we handled several pre-migration setup tasks and configured DMS to run change data capture tasks. We planned, tested, and completed Phase II of the database migration - Change Data Capture. Now that the data has been migrated, it is time to cut-over to the new database. Pat your self on the back and congratulate everyone involved. It's over!

If you get stuck of overwhelmed with the migration process, we have professionals standing by ready to provide assistance. Drop us a line at sales@ipponusa.com. 
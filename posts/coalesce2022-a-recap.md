---
authors:
- Pooja Krishnan
tags:
- Data
- dbt
- Python
- Snowflake
date: 2022-10-31T13:33:00.000Z
title: "Coalesce 2022 - The Analytics Engineering Conference hosted by dbt Labs (A Recap)"
image: 
---

Coalesce 2022 is dbt Labs' analytics engineering conference. For its third year, from October 17th to 21st, 2022, Coalesce was a whirlwind data extravaganza boasting speaker sessions and workshops in In-Person in London, Sydney, and New Orleans and virtually. Over 2500 people from all over the world attended in person, and over 20,000 individuals online. I had the pleasure of attending in-person in New Orleans over the week, and throughout this blog, I'll be presenting the Top Announcements of the week from dbt and some of my favorite sessions I attended while there.

# Top Announcements:
The Keynote Address started the week off, coinciding with the start of Coalesce+ London. Tristan Handy, CEO and Founder of dbt Labs, and Margaret Francis, Chief Product Officer at dbt Labs, discussed the modern data stack, changes therein, and changes coming to the data tooling ecosystem. Additionally, the following new dbt features were announced.

## Python Support in dbt

With the release of v1.3 of dbt Core, you can now create and execute Python models in dbt. This feature is available for users on BigQuery, Databricks, and Snowflake. Python models are:
- `.py` files in a dbt project, typically in the `models/` directory
- `returns` a data object in the form of a dataframe in the data platform
- compiled by dbt, in the `target/` directory and then executed in the data platform

Python support was not included to change how dbt functions, but introduced to handle problems better solved in Python, such as:
- Advanced statistical calculations
- Parsing and analysis of text inputs
- Complex date-time operations
- Basic forecasting models
- Generating synthetic data
- Enrichment of datasets via metadata
- Feature engineering for ML
- Light ML workloads

For more information, check out this [article](https://www.getdbt.com/blog/introducing-support-for-python).


## New dbt Cloud IDE

dbt also announced the release of their new and improved Cloud IDE available to all users now. They've made performance upgrades, ergonomics improvements, and some quality-of-life enhancements to improve the user experience. 

For more information, check out the new Cloud IDE [here](https://www.getdbt.com/blog/new-improved-cloud-ide).

## dbt Semantic Layer
The final product announcement in the keynote was of the dbt Semantic Layer. dbt prides itself on streamlining governance and managing the complexity of analytics data pipelines and business logic. The dbt Semantic Layer extends the portfolio of information managed in dbt. With the new semantic layer, you can find the information you need to make quality decisions. You can define metrics in your dbt project, and then data consumers can query those metrics in downstream tools.

dbt is additionally working with partners to ensure that integrations with the dbt Semantic Layer meet organizations' needs. 

Look [here](https://www.getdbt.com/blog/frontiers-of-the-dbt-semantic-layer/) for more information on the dbt Semantic Layer and integrations and use cases that the dbt Semantic Layer powers.

___

#Recap
Now that we've seen the new features coming to dbt, I wanted to discuss some of my favorite sessions from the 100+ at Coalesce 2022.

## SQL: The Video Game

Who hasn't been playing Wordle daily over the past year? Are you a data or analytics engineer who wishes there was something similar for SQL skills? That is the inspiration behind `QUERY;` a video game aiming to help data folks keep their querying sharp and newcomers increase their literacy. 

Joe Markiewicz, Analytics Engineer at Fivetran by day and video game developer at Cedar Cat Studios by night, leveraged dbt and BigQuery to create the game, and his session was fascinating. I enjoyed hearing about his inspiration as well as the technical implementation. 

[Watch his talk here!](https://www.youtube.com/watch?v=gzr4CbeVY5s)
[To install `QUERY;` go to the Apple App Store](https://apps.apple.com/us/app/query/id1636590940?mt=12)

## Building Scalable Data Products leveraging User Stitching
Tracking a user's journey can be rather tricky. Emiel Verkade from Snowplow discussed how Snowplow handles user tracking and user stitching in this talk. There are many ways people can enter into the domain of dbt. You can go directly to the docs site, or you are Googling the problem, which leads you to an answer you can try. You can also span multiple search sessions and multiple days to resolve a problem. Basic web analytics would look at each day's session separately. If you use user stitching, you can trace the pattern of use through a site and surmise if a visitor found a solution.

At a high level, user stitching (or identity stitching) says that if two events belong to the same identifier, they belong to the same entity. In this way, multiple identifiers can be defined per customer. 

How do you approach user stitching? Emiel recommends the following: 
 -a set of properties to identify a user
- an understanding of how the identifiers map to each other
- a way to identify all events for a user. 

At Snowplow, they use the domain_userid (set by first-party cookies), network_userid (set server-side by the Snowplow collector), idfa/idfv (set by mobile devices), and the user_id (set to a custom id when tracking). They then map the identifiers like so:

![Snowplow User Stitching](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/11/coalesce_snowplow_user_stitching.png)

Snowplow then runs a query using the mapping to generate a table containing the domain_userid, the most recent user_id, and the processed date. This table allows Snowplow to see a person's identifier before they even log in for the day. To scale this, Snowplow has a custom incremental materialization called snowplow_incremental, reducing the amount of data scanned. All of this is powered by dbt!

[Watch the full talk here!](https://www.youtube.com/watch?v=uCHtOLsCsAA&t=1443s)

## Beyond the buzz: 20 real metadata use cases in 20 minutes with Atlan and dbt Labs

In this talk, Prukalpa Sankar, Co-founder of Atlan presents 20 scenarios in which data teams have used metadata to drive their work as well as how metadata and the dbt Semantic Layer can transform how a team works with metrics. 

The chief question that Prukalpa and Atlan is trying to address is, `When I'm looking at data in a dashboard and am wondering if it is correct, is it possible to get the answers immediately?` 

Let's get right into the 20 real-life use cases mentioned in the title:
1. Streamline analyst 'service' requests
> Problem: Constant questions and asks for analysts from business users. 
> - What does metric X mean? How is it different from Metric Y? 
> - Can someone confirm if this is the right way to join these tables? Here's the SQL statement
> - I want to see MEtric X by geography instead of segment?
> 
> Solution: With metadata, you can actually go from requests to products. You can consider each of these requests as internal reusable products for their organization. Data, Metrics and Queries can all be products for your organization.

2. Streamline Data/Analytics Engineer 'service' requests
> Problem: Constant questions and asks for engineers from other data users
> - Hey, is this data updated?
> - Do we have this data? 
> - Do we ingest data from our survey tool into the warehouse?
> - Is there an existing Tableau extract for ROI by new/existing inventory?
> - How are columns for this table derived?
>
> Solution: You can extend data-as-a-product one step further. You can bring in context that a data engineer needs, such as Airflow SLAs (auto-updated), freshness from dbt tags, autoamted column level lineages, etc.

3. Speed data team onboarding and ramp up
> Problem: How do you speed up team onboarding and ramp-up?
> Typically analysts spend 18 months on the job, and it takes them 6 months to onboard into an organization. 
> 
> Solution: By making assets (such as your data objects, metrics and queries) you can reduce this time astronomically.

4. Speed up root-cause analysis
> Problem: Finding the solution to `It doesn't seem like Metric X is right?` is far from easy
> 
> Solution: Establish an automatic lineage across all of your data assets so you can easily trace data's journey through your process.

5. Prevent breaking changes
> Problem: How do prevent things from going wrong? How do you prevent breaking changes?
> 
> Solution: The first step to a data contract is knowledge; developers should know what is breaking downstream on a data platform for a change they're making. For this, you need a E2E source to BI lineage, Visual Impact Analysis and integrate metadata API to your CI/CD pipeline.

6. Active data trust management
> Problem: How do you go from metadata as a knowledge repository to activating the metadata to create actions in your environment?
> Atlan has a customer with a data quality tool which generates anomalies. The anomaly comes in at the physical data table.
> 
> Solution:  They have an automated action which connects the anomaly announcement to the downstream BI assets as well as the business metrics that those tables are connected to.
> 
> ![Data Trust Management Powered by Atlan](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/11/coalesce_20_metadata_data_trust_management.png)

7. Active Change Alerting
> Problem: How do you address changes to data in your platform?
> 
> Solution: If there is a change upstream, such as how Fivetran loads data, an alert propogates to the data team members as well as your business consumers.

8. Data Platform Migrations
> Problem: How do you migrate data?
> As data engineers we migrate data all the time. How do you evaluate the complexity of a migration? How do you create deprecation and change notices? How do you make sure that nothing breaks on migration?
> 
> Solution: With metadata and a data lineage you can send notifications downstream to data team members and business consumers

9. Manage security classifications
> Problem: How do you ensure the right folks have access to the data they need?
> Just like propogating announcements to create trust, you can do the same from a security perspective. A leading fintech client applying for a bank license had to create confidentiality, integrity, availability ratings in order to receive their license. 
> 
> Solution: In their final semantic layer, they created tags for confidenitality and used the lineage to back-propogate those tags to every column that would be creating that data.

10. Programmatically raise security alerts
> Problem: Rapid data change leads to major security and business risks
> There is a Fortune-100 tech company with a data lake powering a customer experience platform. They have __hundreds__ of metadata change event impacting __100K+ tables daily__. The notification mechanism for their security team or enfineering team. There was a lot of security risk on large volumes of sensitive data and business risk when considering the impact of incorrect data or failure on a real-time platform.
> 
> Solution: Atlan now opens up metadata change events on a Kafka queue. In this case, the Fortune-100 company began using these metadata change events to trigger actions. When a metadata event comes in, it triggers a chat notification, opens a JIRA ticket, and automatically triggers data-quality test suites. This helped them control and streamline their responses to security.
> ![Programmatic security alerts](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/11/coalesce_20_metadata_programmatic_security_alerts.png)

11. Programmatically archive data
> Problem: Lengthy, manual process for tracking and deleting data opened the company to legal risk and contractual breaches
> There is a media monitoring company who was dependent on manual processes to delete data on a regular basis. Sometimes data was not being deleted, leading to legal and compliance risks. Additionally, this process forced the data team to spend significant time on manual work.
> 
> Solution: Create a fully programmatic workflow to automate the data deletion process. Atlan produces a notification to Slack when the data lands in the data lake and then automatically creates a JIRA ticket 30 days before the contractual end date. As of now, a human is ultimately deciding whether the data should be deleted or not, but you could start automatically archiving the dataset downstream as well.

12. Data Access Management
> Problem: Who is accessing the data?
> 
> Solution: Use personas within Atlan to ensure only those who should access a dataset are. Atlan additionally auto-classifies PII data and provides tag-based access policies, supports column-level access controls and propogates those policies throughout the data lineage.

13. Compliance & Audit Reporting
> Problem: How do you handle audit requests across your data landscape?
> 
> Solution: Atlan allows you to trace your audit logs and easily tell:
> - Who's using which data asset and how frequently?
> - Which are the most frequently accessed data assets?
> - Who's accessing sensitive data and how often?

14. Data Deletion Requests
> Problem: How do you handle consumer requests to delete data?
> 
> Solution: Atlan allows you to track the data across your ecosystem and make sure it is actually deleted.

15. Optimize data stack spending?
> Problem: How can you identify the following:
> - Which queries are taking too long to run?
> - Which tables can we deprecate?
> - How long are jobs waiting to execute?
> - What tables are used by BI tools?
> - What are the most/least queried tables?
> 
> Solution: All of this should be generating metadata in your system. You can then leverage that metadata to better optimize usage.

16. Active spend optimization
> Problem: How do I handle peak usage at 10AM of my data warehouse?
> 
> Solution: You can leverage metadata to auto-scale your compute clusters at 10 AM and make sure your pipeline is updated at 9:45. You can then auto-scale everything down when users log-off.

17. Active performance optimization
> Problem: How do I ensure the best user experience for data consumers?
> 
> Solution: If you have metadata across your ecosystem and metadata driven actions, you can orchestrate your data platform to create the best user experience for your consumers.
> 
> ![Active Spend and Performance Optimizations](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/11/coalesce_20_metadata_active_spend_and_performance_optimizations.png) 

18. Write better SQL queries with context
> Problem: How can I do my job better?
> 
> Solution: Make the metadata context available to tools used on a daily basis. Integrate your metadata into your IDE so you can see recent queries run on a table, is it a popular table, who owns it, are there issues with the table, what a metric means, the column description and many other things.

19. Make decisions self-service fo ryour business
> Problem: How do you ensure the business is making data-driven decisions in a self-service manner?
> - Can I trust this data?
> - What does the metric mean?
> - Who can I reach out to about the data/metric?
> - Can I run a quick filter/pivot on this metric?
> 
> Solution: You can integrate Atlan into your BI tool to show all of the metric context from the dbt Semantic layer right in the dashboard. So you have all of the data context to make decisions

20. Enable data teams that can change the world
> Problem: How do you unify and enable a diverse team to facilitate data to create change?
> 
> Solution: Embrace the diversity in a team. It is a data team's biggest strenght and weakness. Atlan's metadata is what can help a team gel with each other and do great things.

[Watch Prukalpa's Full Talk here](https://www.youtube.com/watch?v=fxFxfKCZrhY&list=PL0QYlrC86xQlj9UDGiEwhXQuSjuSyPJHl&index=78)
[For more information on all that Atlan can offer](https://atlan.com/)

## Detecting Data Anomalies via an Inspection Layer
In most of our current data ecosystems, we ingest data from various vendors, IoT devices, and more. Unfortunately, for every data source that is perfect, there is another (and we've all been there) where the data isn't what we were expecting. When the data has nulls, contains duplicates, or has a different schema than initially communicated, your data pipeline can stumble. You can handle this natively with dbt native methods and implement an inspection layer to ensure that 'bad' data is flagged and quarantined while the remainder is loaded uninterrupted.

Neal Achord and John Rensberger from Red Pill Analytics walk us through their implementation of the inspection layer in this workshop. It was fascinating to see and implement this myself.

You can follow along with the workshop by [watching their talk](https://www.youtube.com/watch?v=EKPi-T_nBio&t=80s), cloning the [repo](https://github.com/RedPillAnalytics/dbt_coalesce_workshop) on Github and referring to their [slides](https://docs.google.com/presentation/d/11Q9wwMfyz6xuxMXCPizFg4DKSY_zOIHPNOrsNI8oBn8/edit?usp=sharing).

___
I'm cognizant that all of the above is a lot. Thanks so much for sticking with me as I condensed a jam-packed week of content into a short little blog like this. I had a ton of fun and while I felt like I was drinking from a firehose at times, Coalesce was the best experience I've had at a conference to date. I thoroughly enjoyed getting to chat with data professionals and commiserating over all of the common data problems we face daily.

----
For more information on how Ippon Technologies, a Snowflake Services Partner, can help your organization utilize the benefits of Snowflake for a migration from a traditional data warehouse, data lake, or POC, contact sales@ipponusa.com.
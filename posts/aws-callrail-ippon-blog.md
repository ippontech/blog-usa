---
authors:
- Lucas Ward, Wanchen Zhao
tags:
- Cloud
- AWS
- AWS Map
- Migration
- DB Migration
date: 2023-08-03T12:21:50.000Z
title: "CallRail Paves the Way for Further Cloud Modernization Efforts by Migrating to Amazon RDS for PostgreSQL"
image:
---

# CallRail Paves the Way for Further Cloud Modernization Efforts by Migrating to Amazon RDS for PostgreSQL

CallRail offers comprehensive return on investment calculations for marketing campaigns through their advanced Call Tracking Products, constituting their primary business application. Additionally, the company possesses a diverse range of capabilities beyond this core service. 

CallRail manages several extensive and vital databases, among which is a large database previously self-managed by their engineers on Amazon Elastic Compute Cloud (EC2). The migration of this particular database stands as a pivotal undertaking in their ongoing efforts towards modernizing their cloud infrastructure. The significance of this migration lies not only in its potential cost savings from maintenance and upkeep, but also in alleviating the strain caused by CallRail's substantial workload on the database.

## A Challenging Migration

Despite encountering challenges, CallRail successfully executed the migration of the database with valuable assistance from Ippon Technologies' consultants and AWS' database support specialists. The intricacy of the migration arose from three main factors. Firstly, the database in question held the utmost importance for the business, allowing for minimal to no downtime during the migration process. Secondly, the migration entailed a version upgrade of PostgreSQL v9.6 to PostgreSQL Engine v12, necessitating careful handling of the transition. Lastly, the sheer volume of data and the immense daily throughput presented obstacles that had to be carefully managed throughout the migration.

## A Truly Collaborative Effort

CallRail devoted considerable time to meticulously planning the migration process. However, they recognized the necessity of augmenting their efforts with a team of skilled and dedicated experts in cloud and database domains. Collaborating with the proficient Ippon consultants proved beneficial for planning and initial execution phases of the migration, leveraging their expertise in parallel with CallRail's domain knowledge.

Despite investing months in previous unsuccessful attempts, CallRail decided to seek additional specialized assistance. It was at this juncture that an AWS Database Specialist was brought on board to navigate the final, yet crucial phase of the migration. The synergy achieved by combining the three groups proved to be the catalyst that the project required.

Through six months of persistent efforts and despite encountering multiple challenges and setbacks, the migration was ultimately accomplished, leading to a successful outcome.

## What was Gained

In conjunction with the growing collaboration among Ippon consultants, CallRail team members, and AWS Support members, numerous benefits were realized. The successful migration of CallRail's final database to RDS marked a significant milestone in their cloud modernization endeavors. Consequently, the company can now proceed with optimizing the same critical database for enhanced operational efficiency. This newfound operational efficiency not only instills a sense of assurance, but also liberates valuable engineering resources, enabling them to focus on other projects.

## AWS Services Used

Amazon RDS for PostgreSQL
Amazon DMS for Change Data Capture
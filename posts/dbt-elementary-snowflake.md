---
authors:
  - Tyler Bridges
tags:
  - DBT
  - Snowflake
  - Elementary
  - Alerting
date: 2023-06-15T12:00:00.000Z
title: "Enhance your DBT Data Monitoring with Elementary"
---

# Enhance your DBT Data Monitoring with Elementary

<p> This blog aims to provide a guide on implementing the Elementary package with DBT. The primary functionalities of this guide focus on: </p>

- Integrating Elementary with the jaffle_shop DBT project
- Generating reports on datamart health.

***

## What is Elementary?
Elementary is an open-source data observability package designed for data and analytics engineers using dbt. It provides a comprehensive solution for monitoring and ensuring the quality of your dbt projects and data. With Elementary, you can generate data observability reports, detect data anomalies through dbt tests, analyze models' performance, track dbt artifacts and run results, receive Slack alerts for data issues, and explore data lineage to understand the impact and root causes of data problems. By seamlessly integrating with dbt and leveraging its powerful features, Elementary empowers you to gain immediate visibility, take proactive actions, and maintain the integrity of your data pipelines.

Elementary's functionality revolves around its dbt package, which enables data monitoring and collection of dbt artifacts. Configuration for monitoring is done within your dbt project, using dbt macros and models as monitors. The collected data is stored in a dedicated schema called "elementary" in your data warehouse. To generate UI reports and send Slack alerts, Elementary CLI comes into play. This command-line interface simplifies the process of creating reports and ensures timely notifications of data issues. With Elementary, you can effortlessly monitor your dbt projects, detect anomalies, and gain valuable insights into your data's health and performance, enabling you to proactively address issues and optimize your data workflows.

***

How to Setup
------------

This guide provides instructions for setting up Elementary using DBT's jaffle_shop as an example project.

1.  Create a `packages.yml` file in your dbt project directory if it doesn't already exist. Add the following content to include the Elementary package. Depending on your DBT version, you may need to specify a particular version of dbt utils. Refer to the [Elementary documentation](https://docs.elementary-data.com/quickstart) for version compatibility details.
       
```
 packages:
    - package: elementary-data/elementary
    version: 0.8.0
```
    
2.  Install the Elementary package by running the following command:
```    
    dbt deps
```   
3.  Open your `dbt_project.yml` file and make the following additions to include Elementary:
```    
models:
  jaffle_shop:
      materialized: table
      staging:
        materialized: view

  elementary:
    +schema: "elementary"
    # Include the following line if you only want Elementary to run when in a specified environment
    +enabled: "{{ target.name == 'prod' }}"
```   
4.  Generate the Elementary package models by running the command:
```
dbt run --select elementary
```
- This will create a schema named 'Elementary' within the database configured in your `dbt_project.yml`, along with tables for models used with the Elementary package for data monitoring.

5. Generate your connection profile to allow for the Elementary CLI tool to connect to your database
- This will print out a connection profile that you can add to your existing profiles.yml

```
dbt run-operation elementary.generate_elementary_cli_profile
```
*Result*
```
elementary:
  outputs:
    default:
      type: "<connection_type>"
      account: "<account_identifier>"
      user: "<username>"
      password: "<PASSWORD>"
      role: "<role>"
      warehouse: "<warehouse_name>"
      database: "<database_name>"
      schema: "<schema_name>"
```

6. Install the Elementary CLI tool
```
pip install elementary-data
```

7. Run whatever dbt models you would like
8. Run the following command to generate an html report to view your datamart health!
```
edr report
```

![Elementary HTML Report](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2023/06/Elementary-html-report.png)
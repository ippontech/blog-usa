---
authors:
- Pooja Krishnan
tags:
- Data
- Snowflake
- Snowflake Summit 2023
date: 2023-06-27T13:33:00.000Z
title: "Snowflake Summit 2023 Replay - Snowflake Iceberg Tables"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2023/06/snowflake_summit_2023_snowflake_iceberg_tables.png
---

Snowflake Summit is the world's largest data, apps, and AI conference. Coming to you live from Las Vegas, Nevada, from June 26 - 29, 2023, Snowflake shows off all of the latest advancements coming to the Snowflake Data Cloud - including generative AI and LLMs, Apache Iceberg, application development, and more. I'm not in sunny, hot Las Vegas, but wanted to highlight some of the improvements Snowflake announced and educate all of you on the cool stuff (cause ***Snowflake***, get it?) coming our way.

# Snowflake Summit 2023 Replay - Snowflake Iceberg Tables

Apache Iceberg, for all of you who don't know yet, is a high-performance format for large analytic datasets. Iceberg provides rich metadata on top of files in a cloud storage bucket. Iceberg is engine-agnostic and open-source, and it is experiencing a growing number of projects which are adopting support for or extending Iceberg. So, of course, Iceberg joined forces with Snowflake in 2022 to combine Snowflake's unique capabilities with Apache Iceberg and Parquet to help customers solve some common challenges they face with big data: control, cost, and interoperability. 

This year, Snowflake is developing the partnership further by introducing a new offering dubbed Snowflake Iceberg Tables which will extend Snowflake's performance and governance to data stored in open formats. Snowflake is unifying External Tables for Iceberg and Native Iceberg tables into one table type - an Iceberg Table (private preview coming soon). Customers, therefore, get the simplicity of a single Iceberg table type with options to specify catalog implementation and fewer performance trade-offs. Managed Iceberg Tables allow full read and write from Snowflake and uses Snowflake as the catalog for external engines to read from. Unmanaged Iceberg tables allow Snowflake to read Iceberg Tables from an external catalog. Snowflake is also working on an easy, cheap way to convert an unmanaged Iceberg Table to a managed one so customers can onboard easily without having to rewrite entire tables. 

----
For more information on how Ippon Technologies can help your organization utilize Snowflake or Apache Iceberg for all of your data needs, contact sales@ipponusa.com.
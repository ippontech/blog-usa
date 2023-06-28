---
authors:
- Pooja Krishnan
tags:
- Data
- Snowflake
- Snowflake Summit 2023
- Snowflake Performance Index
date: 2023-06-28T13:33:00.000Z
title: "Snowflake Summit 2023 Replay - Snowflake's 'Put (the) Customer First' Policy: Improving Customers' Price for Performance"
image: 
---

Snowflake Summit is the world's largest data, apps, and AI conference. Coming to you live from Las Vegas, Nevada, from June 26 - 29, 2023, Snowflake shows off all of the latest advancements coming to the Snowflake Data Cloud - including generative AI and LLMs, Apache Iceberg, application development, and more. I'm not in sunny, hot Las Vegas, but wanted to highlight some of the improvements Snowflake announced and educate all of you on the cool stuff (cause ***Snowflake***, get it?) coming our way.

# Snowflake Summit 2023 Replay - Snowflake's 'Put (the) Customer First' Policy: Improving Customers' Price for Performance
As engineers, we've often encountered the perennial problem of balancing spending with optimization and speed. That problem didn't disappear when adopting a cloud-based data platform like Snowflake. To combat this problem, Snowflake announced the public launch of the Snowflake Performance Index (SPI), an aggregate index for measuring the improvements in Snowflake performance that customers experience over time. The SPI is calculated based on metrics on stable and recurring workloads. Snowflake uses these metrics to compare improvements on specific workloads over time. Since Snowflake started tracking the SPI in August 2022, the query duration for stable workloads has improved by 15%.

As a Snowflake customer, what does this mean for me? Not a lot. To my knowledge, the SPI is an internal Snowflake tool that is a part of Snowflake's commitment to continuously improving their customerexperience. In other words, Snowflake will continue to invest in performance improvements, resulting in increased speed and reduced costs. Therefore, any platform improvements will directly impact your workloads and overall optimization.

Snowflake currently has a Search Optimization Service (SO), which speeds up query performance by maintaining a persistent data structure called a search access path. This data structure keeps track of which values of a table's columns can be found in each of its micro-partitions, allowing Snowflake to skip scanning some micro-partitions when scanning the table as a part of a query. Snowflake has opened the SO to accommodate more data types, including VARIANT, ARRAY, OBJECT, and GEOGRAPHY. Additionally, Snowflake is expanding the service to support more use cases in general-availability (GA), like speeding up substring searches in text columns and working with other performance services like the Query Acceleration Service (QAS).

Snowflake also introduced the addition of more pruning features (now GA) to reduce the need to scan across entire datasets, enabling faster searches. TOP-K analytics can now enable customers to retrieve only the most relevant answers from a result set by rank. Finally, to help customers more easily analyze expensive queries and identify clauses or operators causing performance problems, Snowflake will make the Query Profile accessible via Programmatic access in GA.

----
For more information on how Ippon Technologies, a Snowflake Partner, can help your organization utilize Snowflake for all of your data needs, contact sales@ipponusa.com.
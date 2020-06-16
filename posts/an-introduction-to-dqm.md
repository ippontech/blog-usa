---
authors:

tags:
date: 2020-06-15T14:50:55.000Z
title: "An Introduction to Data Quality Management (DQM)"
image:
---

#What is Data Quality Management (DQM)?
Data Quality Management (DQM) is a practice that aims to maintain a high quality of data. DQM extends from data acquisition to distribution of data. DQM is about employing processes, methods, and technologies to ensure the quality of the data meets specific business requirements. DQM is extensive, with far-reaching results if employed in a reliable, consistent, continual manner. Ultimately, DQM aims to deliver trusted data in a timely manner.

#Why do you need Data Quality Management Processes in Place?
Most of a company's operations and decisions rely heavily on data, so the emphasis on data quality is even higher. Data quality refers to the assessment of information collected relative to its purpose and its ability to serve that purpose. Low-quality data costs organizations daily. An estimate by IBM calculated that the annual cost of data quality related issues, amounted to $3.1 trillion in 2016.

In addition, good data quality increases the accuracy of analytics applications, which can lead to better business decision-making that boosts sales and improves internal processes. High-quality data can help expand the use of BI dashboards and analytics tools, as well. If analytics data is seen as trustworthy, business users are more likely to rely on it instead of basing decisions on gut feelings or their own spreadsheets.

#How do you measure Data Quality?
There are five categories of metrics that you can use to measure data quality. These metrics can help you track the effectiveness of your data quality improvement efforts. Let's go over the categories of metrics and what they track (and more importantly, why each is important).

##Accuracy
Accuracy tracks the amount of known errors, such as missing, incomplete or redundant entry. Accuracy is typically measured as a ratio of data to errors. This ratio should increase over time, indicating that the quality of your data is improving.

[Add simple, short real world examples of why accuracy is important]

How do you ensure Accuracy? Ask yourself:
*Do data objects accurately represent the values they will be modeling?*
*Is the information present in the datastore correctly spelled and current?*
*Does the data have the same value across all systems of record?*
*If the data changes type or format, does it still mean the same thing?*
*Do the various transformations your ETL pipelines incur create inaccurate data?*

##Consistency
Consistency means ensuring that data across all systems reflects the same information and is in synch with each other across the enterprise. This may seem easy enough at first, but consider the case where two systems have a field named customer_id. Customer_Id for each system means something different in each system. How does this fit in with the concept of consistency? This is, after all, a common scenario when looking at 
[Consider the case where two fields in 2 systems are named the same thing, but means something different. Introduce the data dictionary and data lineage here]

How do you ensure consistency? Ask yourself:
*Are there any instances in which data conflicts with itself?*

##Completeness
Data is considered "complete" when it meets the definition of comprehensive for a data set. Data can be complete even if optional data is missing.

How do you ensure completeness? Ask yourself:
*Is all the information available?*
*Do any data values have missing elements?*
*Is any data in an unusable state?*

[Consider Combining Completeness and Integrity, since if data is incomplete there will be integrity issues. Same will happen if data is not consistent.]

##Integrity
Integrity measures the validity of your data across relationships and ensures that all data in a database can be traced and connected.  The inability to link related records together may actually introduce duplicates across the system.

[Reference Security]
How can you ensure Integrity? Ask yourself:
*Is there any data that is missing important relationship linkages?*

##Timeliness
Timeliness references whether information is available when expected and needed. Timeliness depends on user expectation.

[Explore working with large datasets here]

[Conclusion: Tie into ABC Framework: How do you ensure your data is consistent, complete, accurate etc. etc. Well there are many ways to do so, all of them specific to your business use case blah blah blah]

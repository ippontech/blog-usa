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
Most of a company's operations and decisions rely heavily on data, so the emphasis on data quality is even higher. Data quality refers to the assessment of information collected relative to its purpose and its ability to serve that purpose. Low-quality data costs organizations daily. An estimate by IBM calculated that the annual cost of data quality related issues amounted to around [$3.1 trillion](https://www.ibmbigdatahub.com/infographic/four-vs-big-data).

In addition, good data quality increases the accuracy of analytics applications, which can lead to better business decision-making that boosts sales and improves internal processes. High-quality data can help expand the use of BI dashboards and analytics tools, as well. If analytics data is seen as trustworthy, business users are more likely to rely on it instead of basing decisions on gut feelings or their own spreadsheets.

#How do you measure Data Quality?
There are five categories of metrics that you can use to measure data quality. These metrics can help you track the effectiveness of your data quality improvement efforts. Let's go over the categories of metrics and what they track (and more importantly, why each is important).

##Accuracy
Accuracy tracks the amount of known errors, such as missing, incomplete or redundant entry. Accuracy is typically measured as a ratio of data to errors. This ratio should increase over time, indicating that the quality of your data is improving.

Consider NASA's Mars Climate Orbiter. On September 23rd, 1999 [NASA's Mars Climate Orbiter](https://www.wired.com/2010/11/1110mars-climate-observer-report/) lost communication with mission control as it approached orbit around the planet. It burned up in the Martian atmosphere as it descended towards the planet's surface. An investigation revealed the two software applications which controlled the orbiter's thrusters had miscommunicated the amount of force needed to reach the right altitude. One application calculated the force in *pounds*, while the other assumed the force was in *newtons*. Nobody was harmed as a result of this mistake, other than the 125 million dollar spacecraft, but it does highlight the impact that data accuracy issues can have.

How do you ensure Accuracy? Ask yourself:
*Do data objects accurately represent the values they will be modeling?*
*Is the information present in the datastore correctly spelled and current?*
*Does the data have the same value across all systems of record?*
*If the data changes type or format, does it still mean the same thing?*
*Do the various transformations your ETL pipelines incur create inaccurate data?*

##Consistency
Consistency means ensuring that data across all systems reflects the same information and is in synch with each other across the enterprise. This may seem easy enough at first, but consider the case where two systems have a field named customer_id. Customer_Id means something different in each system. This is, after all, a common scenario when looking at systems across an enterprise.

So how do you maintain consistency? You might consider defining a data dictionary and data lineage to track and distinguish your datasets from each other.

### Data Dictionary
A data dictionary stores and communicates metadata about the data in a database, system or data used by applications. Data dictionaries can include:
* A listing of data objects with names and definitions
* Detailed properties of data elements such as data type, size, nullability, optionality and indexes
* ER and other System-level diagrams
* Reference data including classification and descriptive domains
* Business rules

For people working with similar data, having a shared data dictionary facilitates standardization by documenting common data structures. In the case above, where two definitions of customer_id are available, a data dictionary can also distinguish between the meanings in systems.  

### Data Lineages
A data lineage is a record of your data's origins and where it moves over time. The ability to track, manage and view your data's lineage can help simplify the process of tracking errors back to the data source. With a data lineage in place, it is easy to perform root cause analysis while investigating data issues.

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
Timeliness references whether information is available when expected and needed. Timeliness means different things for different systems. A hotel booking website requires to the second updates so prospective customers can see what rooms are available or unavailable in a hotel. For a billing or transaction management system, however, nightly processing could suffice.



[Explore working with large datasets here]

[Conclusion: Tie into ABC Framework: How do you ensure your data is consistent, complete, accurate etc. etc. Well there are many ways to do so, all of them specific to your business use case blah blah blah]

---
authors:
- Pooja Krishnan
tags:
date: 2020-06-15T14:50:55.000Z
title: "An Introduction to Data Quality Management (DQM)"
image:
---

# What is Data Quality Management (DQM)?
Data Quality Management (DQM) is a practice that aims to maintain a high quality of data. DQM extends from data acquisition to distribution of data. DQM is about employing processes, methods, and technologies to ensure the quality of the data meets specific business requirements. DQM is extensive, with far-reaching results, if applied in a reliable, consistent, continual manner. Ultimately, DQM aims to deliver trusted data promptly.

# Why do you need Data Quality Management Processes in Place?
Most of a company's operations and decisions rely heavily on data, so the emphasis on data quality is even higher. Data quality refers to the assessment of information collected relative to its purpose and its ability to serve that purpose. Low-quality data costs organizations daily. An estimate by IBM calculated that the annual cost of data quality-related issues amounted to around [$3.1 trillion](https://www.ibmbigdatahub.com/infographic/four-vs-big-data).

Good data quality increases the accuracy of analytics applications, which can lead to better business decision-making that boosts sales and improves internal processes. High-quality data can help expand the use of BI dashboards and analytics tools, as well. If analytics data is seen as trustworthy, business users are more likely to rely on it instead of basing decisions on their gut feelings or spreadsheets.

# How do you measure Data Quality?
There are five categories of metrics that you can use to measure data quality. These metrics can help you track the effectiveness of your data quality improvement efforts. Let's go over the categories of metrics and what they track (and, more importantly, why each is important).

## Accuracy
Accuracy tracks the number of known errors, such as those resulting from missing, incomplete, or redundant data entry. Accuracy is typically measured as a ratio of data to errors. This ratio should increase over time, indicating that the quality of your data is improving.

Consider NASA's Mars Climate Orbiter. On September 23rd, 1999, [NASA's Mars Climate Orbiter](https://www.wired.com/2010/11/1110mars-climate-observer-report/) lost communication with mission control as it approached orbit around the planet. It burned up in the Martian atmosphere as it descended towards the planet's surface. An investigation revealed the two software applications which controlled the orbiter's thrusters had miscommunicated the amount of force needed to reach the right altitude. One application calculated the force in *pounds*, while the other assumed the force was in *newtons*. Nobody was harmed as a result of this mistake, other than the 125 million dollar spacecraft, but it does highlight the impact that data accuracy issues can have.

## Consistency
Consistency means ensuring that data across all systems reflect the same information and synch with each other across the enterprise. This may seem easy enough at first, but consider the case where two systems have a field named customer_id. Customer_Id means something different in each system. This is, after all, a common scenario when looking at systems across an enterprise.

So how do you maintain consistency? You might consider defining a data dictionary and data lineage to distinguish and track your datasets, respectively, from each other.

### Data Dictionary
A data dictionary stores and communicates metadata about the data in a database, system, or data used by applications. Data dictionaries can include:
* A listing of data objects with names and definitions
* Detailed properties of data elements such as data type, size, nullability, optionality, and indexes
* ER and other System-level diagrams
* Reference data including classification and descriptive domains
* Business rules

For people working with similar data, having a shared data dictionary facilitates standardization by documenting common data structures. In the case above, where two definitions of customer_id are available, a data dictionary can also distinguish between the meanings in systems.  

### Data Lineages
A data lineage is a record of your data's origins and where it moves over time. The ability to track, manage, and view your data's lineage can simplify tracking errors back to the data source. With a data lineage in place, it is easy to perform root cause analysis while investigating data issues.

## Completeness and Comprehensiveness
Data is considered complete or comprehensive when, according to the requirements, all of the data required for a particular use case is present and available to be used. Completeness is usually measured as a percentage.

In a healthcare dataset, completeness would be defined as containing personal details, insurance information, and dates for prior visits. An address or phone number may also be collected, but are not mandatory. The percentage of completeness here would decrease with the absence of each critical piece of information.

## Integrity
Integrity measures the accuracy and consistency of your data over its lifecycle.  Each time data is replicated or transferred it should remain unaltered between operations.

Maintaining data integrity is essential for several reasons. Data Integrity ensures recoverability, search-ability, traceability to the origin and connectivity. Improving the integrity of a dataset will increase your system's stability and performance while simultaneously improving reusability and maintainability.

Data security is one of many measures that can be employed to maintain data integrity.  If changes made to data are a result of unauthorized access, this is a failure to both your data integrity efforts and your data security measures. Depending on the dataset, an integrity violation could result in the loss of a critical datastore or loss of life due to the failure of a life-critical system.

## Timeliness
Timeliness references whether the information is available when expected and needed. Timeliness means different things for different systems. A hotel booking website requires to-the-second updates, so prospective customers can see what rooms are available or unavailable in a hotel. For a billing or transaction management system, however, nightly processing could suffice.

For large systems that rely on stream processing, timeliness is even more critical. The amount of data generated during peak periods can be considerably larger than the amount generated at low-activity periods. A stream processing platform needs to be adaptive so that it can scale according to the fluctuating demands.

---

How do you go about ensuring your data is accurate, consistent, complete, and timely? There are many ways to do so, dependent on your use case and particular needs. One way is to use the Audit, Balance, and Control (ABC) Framework. We'll be addressing this framework and how the three components ensure accuracy, consistency, completeness, timeliness, and integrity in a following blog.

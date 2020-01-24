---
authors:
- Amine Ouali Alami
- Pooja Krishnan
tags:
- HIPAA
- Spring Data
- Spring Boot
- JHipster

date: 2020-01-10T20:16:10.000Z
title: "Audit your data with JaVers"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/07/container-2539942_1920.jpg
---

As an IT consultant, the first thing that come to mind when you are working in the Heathcare Industry is HIPAA, The technology plays a significant role to ensure data remains secure and HIPAA-compliant. According to cybercrime experts, stolen PHI (Protected Healt Information) is 10 time more valuable than credit card information.

The data contained in PHI, can be used to generate identities, obtain health services, submit false insurance claims, order pharmaceuticals, and perform other illegal acts. That is why auditing PHI is critical : the ability to show who and how the data is used.

The two main tools for data auditing are Envers and JaVers.
- Envers is a core Hibernate module, it provides an easy auditing solution when using Hibernate or JPA, Envers is not an option if you are using a NOSQL database.
- JaVers is a lightweight java library for auditing changes in your data. it can be used with any kind of database and any kind of persistence framework, since audit data are decoupled from live data, we can choose where to store them.


# Audit trail with JaVers
Since our client is mainly using MongoDB, we choosed to use JaVers.
JaVers is an audit log framework that helps to track changes of entities in the application.

The usage of this tool is not limited to debugging and auditing only. It can be successfully applied to perform analysis, force security policies and maintaining the event log, too.

# Spring Boot Integration and Setup


# Retrieve the change
As mentioned earlier, we stored all our auditing in a separate Mongo collection. We've been storing all of the changes across one specific collection, but needed to find a way to display back the information we had collected. JaVers provides its' own JaVers Query Language (JQL), which can be used to query the Javers Repository for changes on a iven class, object or property.

Data can be retrieved from Javers in 3 possible ways: Shadows, Changes and Snapshots.
  * *Shadows* are historical versions of objects
  * *Change* shows the difference between two objects
  * *Snapshots* show the state of an object at a point in times

As per our client requirements, we needed to display the changes for an object from creation. We decided to use the **findChanges** method to do so. There are 3 types of changes tracked by JaVers: NewObject, ObjectRemoved and PropertyChange. JaVers uses the primary key on the table or collection in order to compile this information.

# Conclusion





### Sources
* [JaVers official website](https://javers.org/)

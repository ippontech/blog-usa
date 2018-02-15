---
authors:
- Justin Risch
categories:
- 
date: 2015-03-30T10:01:43.000Z
title: "The Challenges of Integrating NoSQL with MDM."
image: 
---

Previously, we discussed [how a business could benefit from implementing MDM](http://www.ipponusa.com/doyouneedmdm/). It remains true that there is a huge advantage to keeping a master set of data and allowing each part of your enterprise to access only the [relevant bits.](https://www.xkcd.com/1289/) It limits the amount of redundant information, and reduces the odds of getting out of sync with your partner applications.

But suppose your database is currently backed by a NoSQL solution, and making a normalized relational database out of it is not an option. Perhaps you need the high write speed, or the horizontal scalability of NoSQL. Unfortunately, NoSQL and MDM do not go well together conceptually– Master Databases are [relational by definition](https://en.wikipedia.org/wiki/Master_data#Types_of_master_data). So how, then, do we get the two to play nicely without feeling like we’re shoehorning concepts together? And what benefit can we gain from this?

Firstly, let’s see what type of NoSQL database schema would work for us. Key-Value would be interesting, but would gain little to no benefit over a standard relational. Since master data is used frequently and changed rarely, the benefits of handling high update volume aren’t high priority, and certainly not worth the more complicated database structure. As well, your master data is likely to have a rigid schema. For example, every user might consist of {First name, Last name, Address, Email, Username, Password}. Of those, all but the password could be considered master data depending on your needs. In any case, databases that would require an elastic schema are unlikely to be master data. Think of it this way; if we can’t guarantee that a field will be there, how can we rely on it to compute other fields? Because of this, key-value complicates retrieving the values without benefit– and the loss of ACID compliance means no transactions with Key-Value systems.

The next, more hopeful solution is a Document database. Some existing MDM solutions currently offer the ability to import and export xml, which is a stone-throw away from being a NoSQL system and offering the benefit of flexible schemas. In fact, [EBX](http://www.orchestranetworks.com/product/) (which is XML-backed) allows you to have child-elements, which conceptually are a permanent join between the child and its parent. Because of this, they can mimic a less rigid schema by spinning off child tables with different attributes, even if it isn’t truly a document database. But there’s a reason it isn’t a NoSQL solution: Master Data is relational. Every time you have a piece of master data, it’s going to be linked itself-to-many (or you’re not using it right). So even if you have documents, you also need some method of storing the relations between documents… at which point, you’ve reinvented RDBMS, and not even benefited from it.

Generally people point to the flexibility of document-schemas, but RDBMS solutions such as EBX have already fixed that by using far more efficient permanent joins and inheritance, without sacrificing the read and analysis performance of SQL. After all, let’s not forget that SQL does have benefits, the most important of which being ACID compliance. Social media sites may be fine using mostly NoSQL even if they lose ACID-ity, but master data is found in all web applications of sufficient size and complexity.

Is there hope for a NoSQL solution then, if Master Data is such a relational concept? Pitney Bowes would have you think so. They offer a Graph-based NoSQL solution by the name of [Spectrum Master Data Management Hub](http://www.pitneybowes.com/pr/customer-information-management-software/master-data-management/spectrum-data-hub-module.html). This software is a bit reclusive, offering no trial on their site. In fact, most of the information I gathered from it is offered on a [pdf](http://www.pitneybowes.com/content/dam/pitneybowes/australia/en/legacy/docs/International/Australia/PDF/Software/data-management/SpectrumMDMHub_DS_CDLI_APAC_A4_1305_v1.pdf), which is at least a year old.

Here’s the exciting part; if they are as advertised, they offer:
 • Map-Reduce based implementation for large batch processing
 • Horizontal and vertical scaling run-time instance configuration across functions
 • Hot Back-Up support
 • Full ACID-compliance
 • In-memory caching of large reference datasets

Or in short, they offer all the benefits of RDBMS and NoSQL combined. Of course this is to be taken with a grain of salt until the software can be properly vetted; Though if they are to accomplish the seemingly impossible, I would say they’re headed down the right track.

Suppose you have a product and a user– both of these things are potentially master data. An order, then, would contain 2 relations, one to each of the aforementioned, as well as some order-specific information (date ordered, shipping address, etc). If I had a particular user and wanted to find the product he ordered in RDBMS, I would need do a join on those three tables to narrow it down to the one particular product he ordered. Multi-table joins are, frankly, where RDBMS solutions tend to do very poorly, as it is such a costly operation. A graph database, however, could simply follow the edges between these associations. In this way, we become horizontally scalable and increase the performance of both lookups and adding new data.

Graph databases are also capable of maintaining ACID compliance. This is fantastic for our purposes! Master Data may not include purchases– a particular order on a particular day is only a single instance and unlikely to ever be reused– but a purchase is composed of at least two types of master data; product and purchaser. Thus, the ability to ensure ACID compliance means we can integrate this solution where we’re very likely to have master data: in e-commerce.

In the end, Graph Databases may be the key to merging the best of both of these systems. There may not be a well-known, easily implemented, out-of-the-box-solution for integrating the two concepts at the moment– but the future is bright. I look forward to seeing more from the best in the MDM industry.

---
authors:
- Kile Niklawski
categories:
- Big Data
date: 2015-06-04T10:38:37.000Z
title: "An analogous look at Big Data"
id: 5a267e57dd54250018d6b5ec
image: 
---

I am not going to harp on the details of particular Big Data tools or semantics in this article. I am going to compare our digital problem and solution with a physical scenario. I would also like to venture a guess at the future evolution of the Big Data ecosystem and its potential impact on technology as a whole. The purpose of the analogous approach is not to dumb it down, but to allow us to look at the problem from a simplistic perspective.

## Definition

First we need to define what “Big Data” is. To me, “Big Data” is a loaded term that represents multiple facets of the technological evolution we are witnessing. It stems from a need and desire to store more information as the Internet of Things (IoT) expands and technology becomes a larger part of our daily lives. First, computers, then cell phones, refrigerators, thermostats, scales, and so many more things have become connected. I will venture a guess that air fresheners will also be connected to the internet in the near future. As technology becomes more affordable, compact, and accessible, people will expect it to make their lives easier or more enjoyable. The growing network of connected things empowers technologists to consistently cultivate new information. This empowerment comes at a cost and presents several complex challenges. The ecosystem of solutions that solve this problem are what I think about when I hear the term “Big Data”.

## The Analogy

Data is the information that drives our software applications and serves as our digital filing cabinets. I am going to compare this to actual, physical filing cabinets. This example gives us a tangible situation facing the same challenges that we face in the digital world. As you would with computers, you must address capacity, cost, efficiency, and security concerns.

## The “Three ‘V’s”

#### Volume

*The more filing cabinets we add, the more difficult it will be to find the information we need, when we need it. We obviously cannot use just one large filing cabinet. How are we going to expand our office to store all these filing cabinets, and how can we find files efficiently?*

Most older technologies are built to scale up, or vertically, which simply requires more machinery. This is comparable to getting a bigger file cabinet. Of course, there is a practical and physical limitation to the size of a filing cabinet. Scaling out, or horizontally, presents an entirely new realm of challenges. This would be similar to adding more file cabinets. Once you fill up your current storage capacity, you need to scale to a different building or data center entirely. Imagine coordinating the logistics around quickly finding the right information scattered across multiple physical locations. The logistics around consistent, transactional applications in a horizontally distributed environment are a great challenge.

Luckily, with technology, we are solving for these problems…mostly. We have access to databases that provide linear horizontal scalability, which means that the storage and throughput increases consistently when more nodes are added to a cluster. These databases are distributed in a way that each node is aware of the other nodes in the cluster, and act as coordinators to direct requests to the appropriate node(s). This technology also provides replication across distributed nodes, ensuring availability and durability of information.

As with most situations, there is a trade-off. We are constrained by the CAP theorem in distributed environments. The CAP theorem states that we can achieve two out of three elements: Consistency, Availability, and Partition tolerance. Because of the nature of horizontally distributed data, achieving two of these in a large scale environment is an accomplishment. However, some emerging technologies, such as Cassandra, are introducing concepts such as “tunable consistency” which allow you to choose your side of the CAP theorem triangle per query.

#### Velocity

*If we add more files at a greater velocity, we need to be able to efficiently file them away in an organized manner, so we can find them later. *

We have a huge influx of files to store, but we only have one person filing them. One obvious solution is to add more people, but then you have coordination and timing concerns. We cannot risk that the information is unavailable or incomplete when we want to retrieve it. Furthermore, if we’ve scaled to multiple offices and file cabinets, how do we distribute that information to the correct office efficiently?

One of the concepts surrounding Big Data is the ability to quickly and efficiently store information as it is received. The “Data Lake” repository concept allows us to quickly store the data as it comes in, then decide what to do with it later. This allows us to fine tune the technology on the front end to handle a massive load of write requests.

If we were to apply the “Data Lake” approach to a real world scenario, it would be analogous to throwing all the files into a pile as they come in. The person responsible for the intake is not able to organize the files, so the retrieval of them would be a nightmare. At this point, we would need to hire people to come in and help sift through the pile and organize it, or we would need more skilled professionals to manage the intake simultaneously, as it streams in.

Many of the tools for Big Data provide batch analysis and streaming real-time analysis to prepare the data for retrieval. Tools are available that allow us to process analysis of information in parallel and efficiently store it in a format that suits our needs for data retrieval.

In addition to our application data needs, this creates a large demand for data scientists who are capable of dissecting a large pile of files and producing valuable insights from them. This opens the door to new potential revenue streams and business strategies. Pairing these piles of information with information outside our business can add tremendous value.

#### Variety

*How do we organize files coming from various locations in different formats? What if business needs change, and we need to reorganize and redistribute all of the information in all of the filing cabinets? *

For this example, let’s assume we are receiving packages containing multiple files. Those files are in no particular order. How do we manage the sorting and distribution of these files?

Big data technology typically supports the equivalent to throwing files into a pile. In most cases, we need to know enough about the data to interpret it from an application perspective, but this is not always required by a database. NoSQL databases support unstructured or semistructured data that can be queried later to analyze and reformat. After all, Big Data technology is driven by the need to performantly store massive amounts of data. When relational concepts like indexing and constraints are applied on insert, we are forced to accept a performance hit. However, we are prepared to manage and regulate data from multiple sources in various formats.

Now, we are faced with a new challenge. Our business requires us to respond to certain requests differently. We have organized our file cabinets in a way that allows us to respond to requests as efficiently as possible, but we are thrown a curveball. How do we store new files that belong in existing categories? Or, what if the business requires a new set of files to be retrieved for certain requests, which our current organization scheme does not support?

As business needs change, we are prepared for it with Big Data. Most often, we do not have to reconsider our data model to adapt to changing business needs. This would still require application modifications, but the sparsely populated NoSQL data model supports diversity. At this point, the way in which we analyze the information would most likely change, but we do not need to restructure our entire dataset to adapt.

## The Future

Technology evolves in cycles consisting of three main stages: Capability, Efficiency, and Innovation. With Big Data, we have fundamentally met the capability objective by developing the tools to allow us to store massive amounts of data and analyze it in parallel. Right now, major strides are being taken to improve efficiency of the distributed and real-time analysis of information. Adoption is a large part of this stage, as it helps to accelerate increased efficiency, especially in the open source community. We are now on the verge of innovation. Innovation is constituted by the initiation of a new cycle which requires new capabilities.

I believe that we have crossed the threshold in the efficiency stage that enables broad adoption of Big Data technology. Big Data has become a large part of many architectures across multiple industries. Companies are using Big Data for growth through effective marketing, targeted advertising, and personalization. Social media strategies are becoming more sophisticated. Customer profiling is improving contact strategies for companies in several industries, leading to new sales. In retail and e-commerce, analysis of patterns in page visits, purchases, and ratings indicate trends that enable targeted advertisements or recommendations. These are just a few examples of existing use cases.

In the near future, efficiencies will continue to improve, minimizing entry and operational costs, enabling further adoption. The current state of the Big Data ecosystem will begin to stabilize. We will see standardized specifications for the technology. Business users will gain access to sophisticated tools and more holistic views of their business operations and customers.

In the long term, information as an asset will drive further collaboration between IT and business operations and the superficial line dividing them will blur. New connections between seemingly unrelated data will emerge and data exchange technology will improve. Technology will become simpler and more efficient for end users. As we gain access to more information, collaborate, and become better at mining value from data, we will have a basis for more advanced artificial intelligence that can improve our everyday lives.

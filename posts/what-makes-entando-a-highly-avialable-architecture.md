---
authors:
- Lucas Ward
tags:
- entando
- kubernetes
- devops
- docker
title: "What Makes Entando a Highly Available Architecture?"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/12/scalingkubernetesstraws.jpg
---

High availability within the software world means a user can employ a technology to do a piece of work when needed. For software to be *highly available*, it needs to be **Scalable, Resilient, Reliable, and Durable**. To understand what makes an Application Composition Platform like Entando a Highly Available Architecture, we must understand what makes Kubernetes highly available.

# An Analogy

If you ever drink a Caprisun Cooler or a kid's Juice box, they have these tiny straws that require extra effort to draw the liquid up. Compare this to drinking through a straw from Mcdonald's or a giant smoothie straw: Less effort for more volume. This same idea is how *vertical* scalability works. Increase the size of the machine, and theoretically, throughput increases.

What would happen if the big straw were to crack, however? It would become even more difficult to suck the liquid through. Instead of a wide straw, let's use two, three, or even ten straws! Having multiple straws would still mean high throughput. And, if one of the straws were to crack, plenty of beverage would still flow through the other straws. A new straw could replace the broken straw without stopping the *slurp!*

This multiple-straw analogy is the central concept behind "horizontal" scalability. Kubernetes is a highly scalable, resilient, and reliable architecture. Entando is built on Kubernetes and shares all of these excellent benefits.

### Scalability

In our analogy, "scalability" would be defined as the ability to get more and more liquid on demand by adding more straws. Kubernetes similarly handles scalability. Engineers can *scale* that workload to have more ***pods*** if a particular workload in an application is struggling to keep up. When building applications with Entando, the underlying Kubernetes scaling functionality is exposed. A reflection of scaling in Kubernetes can be seen in the scaling structure with Entando's microservice bundles.

### Resiliency

The drinker could withstand and recover quickly from a difficult situation by replacing the first straw after it broke. Kubernetes is resilient in the same way. If a pod encounters an error or fails for whatever reason, the Kubernetes "Master Node" will replace the pod. It is possible to cluster the Entando App Engine without much additional setup. The App Engine's ease of use allows organizations with significant availability needs to do so while their applications continue to work. These are the steps to cluster the Entando App Engine [here](https://developer.entando.com/next/tutorials/devops/caching-and-clustering.html#clustering)

### Reliability

Kubernetes allows engineers to schedule pods to replace themselves after a given period. This setting would be akin to swapping out staws with new ones now and then, just in case. These replacements make for a highly reliable architecture that is there when you need it. Entando, too, benefits since it has Kubernetes underneath the hood. This situation is especially relevant with multiple availability zones and clustered setups using any of the big cloud providers.

### Durability

Entando has options to configure a shared cache across multiple nodes. This configuration lends to availability in that if one of the nodes crashes at an application or hardware level, the data you were working on will still be available. It is also possible to configure Entando to use Redis as a shared cache or data store.

# Databases

Out of the box, Entando will create databases inside the Kubernetes cluster. Each application will have a database and mechanism by which it can import and export data. Additionally, it is possible to use a newly-created or existing external database to set up a more scalable approach. Entando makes these a breeze to set up by using dedicated custom resource definitions. Customers will have to configure the external database themselves, but once complete, adding it to the custom resource definition is very straightforward. The Entando Operator will automatically create tables, indices, and foreign keys.

Scaling a database used with Entando is very similar to scaling databases in general.

### Horizontal Scaling

Database scaling can happen in one of two different ways. The first way has already been mentioned and is the exact mechanism for scaling an application server vertically. Once you have reached the point of maximum vertical scalability, it's time to start scaling horizontally. The horizontal scaling of a database includes both reads and writes.

### Scaling Database Reads

A database read happens when a user or program requests data stored in the database. The quickest and easiest way to scale reads is to increase the number of database replicas and then split reads and writes from there. Depending on the database, it will employ a proxy or database driver for functionality.

Caching techniques can also help scale database reads. Storing frequently accessed data in a separate cache keeps the load off of the database.  When a user or an application goes to access the data, it may not even hit the database, pulling from the cache instead.

A third way to scale database reads is to implement database sharding. This strategy is also the primary method used to scale database writes.

### Scaling Database Writes

A database write happens when a user or program wishes to store a piece of data on the database. Since every database replica will also need to contain this piece of data, increasing replicas does not fix the issue as it does for database reads. Enter what is known as "database sharding."

Sharding a database is the process of spreading the data across multiple databases. Spreading the data out can be as simple as putting records that start with the character 'A' through 'L' in one database and the remaining 'M' to 'Z' records in a different database. This arrangement means we need another component in front of the database to handle the routing. Some databases support sharding natively, and others will require a third-party tool.

### Scaling Database Automation in Kubernetes

Scaling a database in Kubernetes is similar to how we would for a cluster. Adding additional cluster nodes that run the database is the best way. And it is crucial to complete this scaling task before the added traffic makes its way to the new cluster(s). The process includes the following:
- Creating a new node.
- Initiating a backup save of the database.
- Initiating a backup restore on the new node.

# Many Masters

All this talk about scaling nodes and refreshing pods begs the question, what manages these resources? The answer is the Kubernetes "Master Node," also known as the "Primary Node." Scaling the worker nodes provides Kubernetes with more resources. More worker nodes bring more CPU and memory resources for pod scheduling. What about scaling the Primary Node itself? There are many benefits to scaling the Primary Node in addition to the worker nodes.

Configuring Kubernetes to have multiple Primary Nodes will bulletproof your application deployment. As mentioned before, Entando allows you to do this and supports it with minimal configuration. The recommended minimum number of Primary Nodes for a highly available Kubernetes deployment is 3.

Having multiple masters also improves the network performance of the cluster because the multiple masters act together as a unified data center.  This all serves to reduce the possible failure of the etcd datastore, API server, controller manager and scheduler, and all of the worker nodes.  To contrast, in a single Master Node setup, node failure will cause Application failure.

# Conclusion

Building applications on Kubernetes makes scaling easy. A cutting-edge platform like Entando ensures that your critical applications scale when and how they need to. If you want to learn more about Entando, check out the website [here](https://entando.com/). If you need help implementing Kubernetes or Entando in your organization, drop us a line at [contact@ippon.tech](mailto:contact@ippon.tech).
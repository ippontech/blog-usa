---
authors:
- Lane Jennison
- Courtney Parry
- Rob Zabriskie
tags:
- Hybrid
- Cloud
- JHipster
- POC
date: 2019-08-26T12:00:00.000Z
title: "Hybrid Cloud - Cloud Responsibly"
image: https://github.com/ippontech/blog-usa/blob/master/images/2019/08/hybrid-cloud.jpg
---

In this post, we’ll describe the <a href="#concept">hybrid cloud concept</a>, the <a href="#benefits">benefits</a> it provides, and a <a href="#architecture">reference architecture</a> to implement it.

Moving from on-prem to the cloud via “forklift migration”, where legacy systems are dumped into a cloud-based host, is inefficient and ineffective. The goal should be to strategically extend into the cloud without abandoning the on-prem infrastructure. We do this by incrementally moving services to the cloud, leveraging the advantages of the cloud platform while retaining the value of on-prem where appropriate. This approach allows us to treat cloud vendors as interchangeable based upon cost/value criteria driven by the needs of the business. 

---

<h2 id="concept">Concept</h2>
Hybrid Cloud is about logically bringing data center environments closer together. Network engineers may view this as the evolution of North-South traffic being treated as East-West. In this context a data center can be a traditional colocation facility, a cloud provider, serverless provider, or SaaS provider. 

As infrastructure technology has improved, the logical boundary where we naturally segment our infrastructure moved. Data center to data center was a natural boundary to designate as WAN traffic for years. Introducing adjacent data centers in close proximity as “availability zones” challenged the idea of a brick-and-mortar data center being the WAN-distance boundary for infrastructure. Regional replication and multi-master technology has further moved the boundary. Now, the only true boundary for infrastructure segmentation is latency.

<h2 id="benefits">What are the benefits?</h2>

- Cost optimization (select the most cost effective environment)
- Flexibility to host services in separate clouds
- Take advantage of cloud vendor-specific offerings (security, edge)
- Option of deployment to cloud and/or on-prem
- Manage risk of committing to a single vendor
- Support proximity to customers for location-sensitive applications
- Assure availability of enterprise-critical services
- Workflow portability (support corporate aquisition)

<h2 id="architecture">Ippon's Approach</h2>
Our approach allows clients to seamlessly integrate the best features from each cloud into one “best of breed” infrastructure. We are strategically combining and modernizing existing environments while expanding into one or more clouds. We have created a reference architecture using modern technologies that supports the view of multiple cloud vendors (including on-prem) as a single cloud. 

The first step is to conceive of an abstraction layer to allow us to handle all infrastructure in a common manner. In the diagram below we introduce this layer (using the HashiCorp offerings Nomad and Consul).


![alternate text](https://github.com/ippontech/blog-usa/blob/master/images/2019/08/IpponWay-Hybrid-Cloud.png)


The following diagram introduces the high-level software/application architecture supporting this approach.


![alternate text](https://github.com/ippontech/blog-usa/blob/master/images/2019/08/IpponWay-Software-Architecture.png)

Our proof of concept uses the HashiCorp products Consul and Nomad with JHipster to produce a hybrid cloud compatible solution. The solution can be deployed in the cloud or on-prem with the intent that the deployment target does not alter the architecture of the application software. Deployment of the application is performed and controlled using Nomad. All configuration information used by the application, and Nomad, is maintained within Consul which is distributed in a fault-tolerant manner across the hybrid infrastructure. Nomad, using Consul as a configuration store, deploys and invokes the application code in the target environments (cloud or on-prem) via a job specification. These configuration and deployment communications are represented by arrow-less lines connecting the components in the above diagram.

The JHipster Gateway communicates with Keycloak to perform authentication and authorization against a directory server (typically LDAP or Active Directory). User information is stored in a federated directory and securely managed with Keycloak, via a dedicated user interface, which provides user authentication and role based access control. The JHipster Gateway and microservices enforce role based access (using Spring Security) supported by Keycloak to allow users access to services according to their permissions. 

The microservices are load balanced by Fabio, a fine grained application router that routes subsets of URLs to different instances of micro services. The service discovery engine, Consul, finds services in the network, health checks them, and notifies Fabio of what services are healthy/available. The container technology, Nomad, defines services to be deployed (Docker) for Consul assigning them names, ports, and tags to tell Fabio what type of service it is.

The arrowed lines represent the request flow through the deployed application. The user interface is a single page application (SPA) that obtains content from the gateway and submits REST calls for data to the gateway. The gateway forwards requests to Fabio which are then load balanced and routed to the appropriate microservice to be serviced.

<h2>Example</h2>
Hybrid Cloud is about running your infrastructure where it makes sense. For example, the diagram below shows an application in AWS (so that it’s fault tolerant) and uses Fabio to route to microservices discriminating between on-prem and AWS by compute-heavy (cloud) vs enterprise data lookup (on-prem). The sensitive functions of federated directory and user authentication remain on-prem.


![alternate text](https://github.com/ippontech/blog-usa/blob/master/images/2019/08/IpponWay-Hybrid-Cloud-Example.png)


<h2>Software Selection</h2>
Ippon selected the following software for our solution:

- Scheduler/Orchestration - Nomad
- Service Discovery - Consul
- Configuration/Secrets - Vault, Consul
- Health/Performance Monitoring - Prometheus
- App Router/Load Balancer - Fabio
- Role-based Access Control (Authorization) - Spring OAuth - Keycloak
- Authentication - Keycloak OpenId Connect
- Code generation - JHipster
- Data modeling - JHipster JDL
- Microservice Framework - Spring Boot
- Front-end development framework/language - Angular/JavaScript/TypeScript
- Back-end/Microservice framework/language - Spring Boot/Java
- User Account Management - Keycloak
- Cloud Deployment - Nomad, Ansible, Docker

A key aspect of the above software selection was the preference for open source software over commercial software. For example, this guided the choice of Keycloak over Okta for
authentication and authorization services. HashiCorp software (Nomad, Consul) was selected to obtain the abstraction layer to insulate the solution from the specifics of individual cloud providers and on-prem. JHipster was selected because of its ease of use in quickly generating enterprise grade applications. The remaining software selections were guided by popularity in the industry and proven reliability.

 <span style="background-color:#f0f0f0"> While our selections represent Ippon’s preferences for building this solution, our understanding of the roles of each component allow us to interchange them as necessary to fit into specific environments dictated by the needs and desires of our clients.</span>

---
There are many nuances to what makes sense in the cloud, and what doesn’t, as well as which cloud to use. No one wants to admit it, but it’s true. In a lot of circumstances running things on your own hardware is cheaper and has better performance. With Ippon’s wisdom, we can guide you to the cloud where it makes sense and how it makes sense.

In short, Hybrid Cloud is about running your infrastructure and applications where it makes sense...Cloud responsibly.


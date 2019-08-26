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
title: "Hybric Cloud - Cloud Responsibly"
image: https://github.com/ippontech/blog-usa/blob/master/images/2019/08/hybrid-cloud-architecture.png
---

Moving from on-prem to the cloud via “forklift migration”, where legacy systems are dumped into a cloud-based host, is inefficient and ineffective. The goal should be to strategically extend into the cloud without abandoning the on-prem infrastructure. Instead, we will incrementally move services to the cloud, leveraging the advantages of the cloud platform while retaining the value of on-prem where appropriate. This flexibility allows us to treat cloud vendors as interchangeable based upon cost/value criteria driven by the needs of the business. 

In this post, we’ll describe the hybrid cloud concept, the <a href="#benefits">benefits</a> it provides, and a <a href="#architecture">reference architecture</a> to implementing it.

---
<h2 id="benefits">What are the benefits?</h2>

- Cost optimization (select the most cost effective environment)
- Flexibility to host services in separate clouds
- Take advantage of cloud vendor-specific offerings (security, edge)
- Option of deployment to cloud and/or on-prem
- Manage risk of committing to a single vendor
- Support proximity to customers for location-sensitive applications
- Assure availability of enterprise-critical services

<h2>Ippon's Approach</h2>
This approach allows customers to seamlessly integrate the best features from each cloud into one “best of the breed” infrastructure. Strategically combining and modernizing existing environments while expanding into one or more clouds.  We have created a reference architecture using proven technologies that supports the view of multiple cloud vendors (including on-prem) as a single cloud.  The following diagram illustrates the high-level architecture.

<div id="architecture"></div>

![alternate text](https://github.com/ippontech/blog-usa/blob/master/images/2019/08/hybrid-cloud.jpg)

Ippon selects the following software for our solution:
- Scheduler/Orchestration - Nomad
- Service Discovery - Consul
- Configuration/Secrets - Vault, Consul
- Health/Performance Monitoring - Prometheus
- App Router/Load Balancer - Fabio
- Role-based Access Control - Keycloak
- Authentication - Spring OAuth - Keycloak
- Code generation - JHipster
- Data modeling - JHipster JDL
- Microservice Framework - Spring Boot
- Streaming - Kafka
- CI/CD - GitLab pipeline
- SCM - Git
- Issue Tracking - GitLab
- Front-end development framework/language - Angular/JavaScript/TypeScript
- Back-end/Microservice framework/language - Spring Boot/Java
- Database - PostgreSQL
- User Account Management - Keycloak
- Cloud Deployment - Nomad, Ansible

---
There are many nuances to what makes sense in the cloud, and what doesn’t, as well as which cloud to use. No one wants to admit it, but it’s true. In a lot of circumstances running things on your own hardware is cheaper and has better performance. With Ippon’s wisdom, we can guide you to the cloud where it makes sense and how it makes sense...cloud responsibly.

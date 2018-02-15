---
authors:
- Laurent Mathieu
categories:
- 
date: 2014-10-13T04:33:28.000Z
title: "Manage your Web API with an API Gateway"
image: 
---

Written by David Martin and translated by Romain.

The primary purpose of a web API is to expose data to be consumed or changed. Fairly quickly, the question of securing access to such data presents itself. And other aspects become critical too, such as performance management API or the respect of access quotas.

When it comes to a single API, the most direct approach to treat these secondary but important needs is to directly add these responsibilities in the API itself. Many solutions exist for this. Security solutions can be more or less sophisticated: simple htaccess protecting access to resources, using a library such as Spring Security or Apache Shiro if developments are made in Java, Passport with NodeJS bookstore, etc. Once the caller’s identity is known, quota management is only a matter of reads & writes to a counter. Cache management can be done through dedicated libraries or by the same libraries used to build the API (Jersey, Restlet, etc.)

Since creating a Web API is now rather simple, APIs tend to proliferate. When the number of APIs becomes quite important, these requirements obviously can be considered cross-cutting needs. And therefore it is not unreasonable to suggest that they can be treated outside the API.
 How to implement these cross-cutting needs? A reverse proxy is a possible approach. This component is commonly called an API Gateway.

A typical API Gateway includes:

- Security (authentication, and potentially, authorization)
- Managing access quotas and throttling
- Caching (proxy statements and cache)
- API Composition and Processing
- Routing (possibly processing) to an “internal” API
- API health monitoring (performance monitoring, error …)
- Versioning (with the possible negotiation of automatic version)

[![API-Gateway](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2014/10/API-Gateway.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2014/10/API-Gateway.png)

Advantages:

- Implemented in one single place
- Simplifies the API source code itself, since these concerns are externalized
- Provides a central and unique view of the API and therefore be more likely to allow a consistent policy

Drawbacks:

- Possible single point of failure or bottleneck
- Risk of complexity, since all the API rules are in one place
- Risk of lock-in, and migrating away may not be simple

Is it a great idea that nobody has thought of yet? No, far from it, since many publishers now have solutions of this kind, with variable amounts of features and different maturity levels. Commercial products are now numerous and started to get acquired by large publishers cycles (example: Intel acquired Mashery in 2013, Computer Associates acquired Layer 7 in 2013) or to enter partnerships (SAP and Apigee 2014). Not all are equal, some stand out and have already gained notoriety (Layer 7 for instance, with his API guru workforce).

What about open-source?
 There are some open source initiatives but strangely, this market is yet to be taken (anyone up for it?):

- APIAxle ( [http://apiaxle.com/](http://apiaxle.com/%20) )
- Tyk ( [http://tyk.io/](http://tyk.io/) )
- apiGrove ( [http://apigrove.net/ ](http://apigrove.net/%20))
- WSO2 API Manager ( [http://wso2.com/products/api-manager/ ](http://wso2.com/products/api-manager/%20))

All these solutions do not have the same traction in the world of open source and are not equal, far from it, in terms of features. To date, WSO2 API Manager is the richest in features and is well-polished. It is a good platform candidate to implement an API Gateway at a reasonable cost (cost of learning the technology and deploying it). Even though this solution is advanced, it does not necessarily cover the full spectrum of features of its commercial competitors.

There is always the possibility of custom-assembling a solution when the needs do not seem to justify the adoption of a turnkey product. Several open source reverse proxy solutions can be configured to provide the basic functionality of an API Gateway. But beware the long-term costs of this approach.

In conclusion, carefully analyze your needs to identify the appropriate strategy: supporting these aspects in the API itself may look like a good and cheap option, but it will lose its appeal if your API portfolio grows. Soon enough, the adoption of a specialized solution will prove to be a good investment. Whether it is open source or not, free or not, custom or off the shelf, on-premise or SaaS, and so on, will depend on many other parameters that need to be examined.

---
authors:
- Jeet Gajjar
tags:
- kubernetes
- docker
- microservices

date: 
title: "How Kubernetes Can Benefit Your Business Needs"
image: https://github.com/ippontech/kakorrhaphio/blob/master/images/2019/6/kubernetes-image.png
---

Monolithic applications have been the industry norm for years, however, we here at Ippon have noticed a shift in the 
industry, wherein many, companies in industries such as insurance, banking, e-commerce, etc. are bound for generating more 
focused and specific applications. These applications exist in the form of [microservices](https://blog.ippon.tech/monolithic-to-microservice-consistency-in-distributed-systems/).  When talking about microservices, its difficult _not_ to talk about containers and container orchestration tools.
When reading into microservices, you and your developers will ask yourselves: `Would you want to spin up a VM,  
install an OS, find dependencies, manage security, etc or would you like to spin up a container which does all that 
for you in seconds?`

Today, we're going to discuss what Kubernetes is and why your business would benefit from this technology. First
we'll briefly discuss the differences in microservices and monoliths.

### A Brief History in Microservices
Before microservices, we had (and still have) monoliths. Applying good development practices, its best to break up 
your main component into modular pieces. These modular pieces, in the form of microservices, are transforming into 
containers.

Making monoliths modular into microservices have real cost saving benefits both financially and in developer time:
- Containers help with keeping deployment times low 
- The main service operates if a component is down or redeploying
- Adding new features is independent of the entire monolith 
- Reduces start-up time for developers

To add to the point, this [research](https://www.researchgate.net/publication/316532483_Cost_comparison_of_running_web_applications_in_the_cloud_using_monolithic_microservice_and_AWS_Lambda_architectures) paper from 2015 breaks down the actual cost of using a monolith vs 
microservice architecture using then-current relevant tech stacks. The researchers found that using a microservice 
architecture in place of monoliths save up to 70% under similar loads.

Now that I've convinced you that microservices have a place in the world, there are some managerial questions to
address: what happens when there are a bunch of microservices running and how does a development team manage them?
Enter: Kubernetes.

### Kubernetes: A Container Orchestration Solution

Kubernetes began as an internal Google project to manage and reduce costs for Google Search, Youtube, and G-mail. It 
is a scheduler to most efficiently utilize their clusters. Kubernetes, an open source project, is the result of that 
inner-source project, Borg. Container orchestration is managing the lifecycle of containers, most notably in larger 
projects or dynamic environments. 

There are many benefits of having a Container Orchestration system such as Kubernetes:
- provisioning and deploying containers
- redundancy
- scalability
- allocation of resources
- health monitoring

### Conclusion
Effectively, Kubernetes is powerful tool to use at your helm where your developers have more time to work on the core
 product and increase their velocity. To put it more simply, Kubernetes saves IT/Developers time. Time saved saves 
 money.
---
authors:
- David Elizondo
tags:
- Kubernetes
date: 2021-09-07T12:21:50.000Z
title: "Kubernetes Tutorial 1 Intro"
image: 
---

## Intro 

Devops Engineers are now dealing with large application systems in the cloud. One of the most popular orchestration systems currently being used in production across the board is Kubernetes (K8s) under the covers. I want to share some knowledge I have gathered over the years and break down what k8s does at it's core. Understanding of the K8s core construction will allow your to understand how new features are constructed for k8s and how these basic functions ensure a more stable operating environment. 

Kubernetes is an open source project on [github](https://github.com/kubernetes/kubernetes). They define their project as: 
> Kubernetes, also known as K8s, is an open source system for managing containerized applications across multiple hosts. It provides basic mechanisms for deployment, maintenance, and scaling of applications.

## Application Deployment

To understand K8s, I believe it's helpful to look at and compare how we deployed production applications in the past and what k8s brings to the table. First lets consider the our first old school setup. At company ABC there is a couple of servers in the basement for hosting our applications. Lets say we size our production servers to match our expected user base and concurrent usage. In our example we have four business applications (Green,Orange,Teal,Blue). Lets say all these applications share a network, database and load balancer with each other. We also have a couple of spare servers allocated in the event of a disaster recovery.

![onprem-deployment](https://github.com/David-Elizondo/blog-usa/blob/master/images/2021/09/onprem.png)

## Simulating a Disaster

Lets say the IT get alerted about an outage of application Blue. An electrical storm has come by that night and shorted the hardware on the server. During the outage IT determines that they will need to move the application to one of the spare servers. 

![onprem-deployment](https://github.com/David-Elizondo/blog-usa/blob/master/images/2021/09/onprem2.png)

During the outage IT will be scrambling to bring one of the spare servers online. Configure access the shared database, network load balancer and allowing firewall rules for the application to run on the new server. Each of these task can be time consuming if not planned for. 

In our example each application can not recover from a single machine failure. Recovery from said disaster will bring outage times and IT time from investigation to recovery. Naturally we'd ask how can be setup our environment such that our application can recover from this scenario?

## Break Down 

In our example losing one machine means having to manually recover. There are many ways to make your recovery time better in this scenario. For instance more equipment to protect against electrical storm or establish a process to build and test a DR plan in case of such disaster. These type of protections don't change the underlying issue which is single machine recovery. Another method used on-prem is to have one hot machine and a cold machine. The hot machine is used until a issue arises where the cold machine to switched to in case of backup. In this way now you can recover from a single failure but you've only moved the bar one tick and still would not be able to recover from a two machine failure. Ideally we wan't to be able to recovery from N machine failures. 

To achieve this K8s changes the way we deploy applications. Instead of having dedicated hardware and manual deployment processes. Our applications have automated deployments and containerized applications. 

[Kubernetes](https://kubernetes.io/) is one of many systems that enable this functionality by creating a distributed cluster of machines that all work together to host applications deployed. 

## Cluster communication

The way K8s operates is to have a pool of machines ready for any application to be deployed onto them. The machines communicate by having the same Kubelet application installed on all the machines. The Kubelet can manage the node it is installed on by running application containers and also has a communication endpoint for controllers nodes to send operations. 

## Visualize K8s 

Back to Company ABC with four application deployments. We convert our production deployment operations to k8s. First by installing a Kubelet onto all of our agent nodes. 

![k8s-deployment](https://github.com/David-Elizondo/blog-usa/blob/master/images/2021/09/k8s.png)

Next we segment three machines to be controllers that will be in-charge of maintaining communications with all the agent nodes and checking health of the cluster configuration. 

![k8s-deployment](https://github.com/David-Elizondo/blog-usa/blob/master/images/2021/09/k8s2.png)

We then define our application deployments as "Plans". The plans are defined by k8s configuration yaml files. These config files can configure many different aspects like resource sizes, network settings and health checks. These plans get communicated to our agent nodes for deployment on all active machines. 

![k8s-deployment](https://github.com/David-Elizondo/blog-usa/blob/master/images/2021/09/k8s3.png)

Lastly, if we repeat our disaster scenario from before and one of our machines in our agent pool is taken offline. Our controllers now can assess the health of this application automatically and redeploy our application on the next available node. 

![k8s-deployment](https://github.com/David-Elizondo/blog-usa/blob/master/images/2021/09/k8s4.png)


## Summary

Using Kubernetes enables many new exciting features for Devops engineers. These functionalities are enabled by how we form our operational environment like I showed here. Using a cluster of machines we can achieve high availability. This change in deployment method allows our application deployments to withstand N machine failures where N is the size of your cluster.

This high-level understanding of how clusters are formed and maintained should help your understanding of higher levels of complexities within larger and larger orchestration systems.
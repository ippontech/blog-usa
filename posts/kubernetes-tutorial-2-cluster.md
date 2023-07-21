---
authors:
- David Elizondo
tags:
- Kubernetes
date: 2021-09-07T12:21:50.000Z
title: "Kubernetes Tutorial #2: Cluster"
image: #New Image
---


## Intro 
In [Kubernetes Tutorial #1: Intro](https://blog.ippon.tech/kubernetes-tutorial-1-intro/) we went over the concepts of Kubernetes. In this Blog I hope to introduce you to how to get started with an empty cluster to start your cloud practice journey. Then moving to how I've started my cluster hosting journey to find the perfect set of tools for kubernetes development completely within kubernetes.

## Local Cluster Setup

There are many ways to run kubernetes. I will be showing one of the many options. Selected based on my familiarity with the product and backed by IPPON as the main engine we use internally after Docker switched to a pay to win model. This application should work for many of the different OS versions out there but as we move forward in this tutorial we want to eventually move to linux as our host which is the defacto standard.

Let me introduce you to [Rancher Desktop](https://rancherdesktop.io/). Self described as "Container Management and Kubernetes on the Desktop". This replaces the need for Docker Desktop and will run a kubernetes cluster with just a couple of clicks. 

I will not be re-writing their documentation on how to install their application as you can find that document here [Rancher Desktop Install](https://docs.rancherdesktop.io/getting-started/installation/). Please read that document carefully and come back when you're done.

After your installation, I recommend testing your local setup by following their [Hello World](https://docs.rancherdesktop.io/how-to-guides/hello-world-example) tutorial. After which you can have some confidence that your virtual k8s cluster is ready to go. 

## K3S
Under the hood Rancher Desktop is using [K3s](https://k3s.io/) for Kubernetes implementation. Rancher has a nice diagram to show how this is running on your machine. I'll show it here. 

![Rancher Diagram](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2023/07/how-it-works-rancher-desktop.png)

Now let's dig into what K3S is doing for us! Below is a diagram of the inner workings of the k3s system and what processes are running.

![K3S Diagram](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2023/07/how-it-works-k3s-revised.png)

In the diagram above, you'll find the key core services that make this implementation of K8s work. The few key services I want to point out are: API Server,Scheduler,Kube Proxy,Kubelet.
These services are important for communication to the control plane, workload reconciliation, network communication and node virtualization. These services are part of the core services that make orchestrating in a distributed system with kubernetes possible. The full list of official components can be found on [kubernetes components](https://kubernetes.io/docs/concepts/overview/components/).

The key difference between a full kubernetes cluster and k3s is how these services are bundled and distributed. A full deployment would mean that each of these services live in isolation from each other and run in separate processes. For smaller clusters not meant for production use cases this is overkill. So K3S bundles these services into a single executable process, this is what is meant by the gray box drawn above in the k3s diagram. Each service exists but run in a single process. 

Another key note is that Kublet is the main service to communicate with your container runtime. K3s by default will use [Containerd](https://containerd.io/) but within the rancher install you also have another option called [Moby](https://mobyproject.org/). Both of these runtimes will work for our cluster but have different capabilities and caveats.

Last thing to notice with this implementation is that we are in a virtual environment. In the diagram for Rancher you may have noticed QEMU or Windows Subsystem. These are in reference to how your cluster is running. Not directly on your machine but instead in a virtual machine within your machine. This last point is really important because since these systems are running virtually they operate in a different network namespace than your local machine and your local network. This will be important to know as we try to access services running on your cluster from your machine. We will look at the network in more detail later on. 


## Starting our K8s Tool Journey
Having a kubernetes cluster running is great. Now, it's time to start thinking about how we will use it. What functions/capabilities it needs to be able to handle. For our journey, I aim to build cluster services that will bootstrap you with the services you need to automate and deploy services via configuration as code using self-hosted (air gapped) methodologies. This means if we need it we can host it as a core service. 

As the road ahead isn't complete, I'll just list out the next few items we will tackle. 
1. Network Topology
2. Ingress (Certificates, DNS, Network Advertising, HTTPs routing)
3. CI/CD (Source to Service)
...(More to come)

## Summary of Part 1 of Cluster Setup

We've set up a cluster on your machine and hopefully armed you with more information about the individual services at play. For many, getting a cluster running is enough to start build and deploying apps. We will continue to add services to your cluster to make it more full service. 

Moving forward I'll be using a local cluster built with three [Odroid HC4](https://www.hardkernel.com/shop/odroid-hc4/). The cluster deployed and clustered with K3S. The installation was assisted by [k3sup](https://github.com/alexellis/k3sup). The Odroid is running a custom image built by Armbian. Armbian specializes in single board computers and have already done the hard work setting up the linux kernel and attached embedded devices, the image can be found on [Armbian ODroid HC4](https://www.armbian.com/odroid-hc4/). Using Rancher Desktop is great as a quick start for testing and local development. But to really dive into cluster administration we need to move closer to our local network and away from virtualized environments.

Here at IPPON we have experience with designing resilient large-scale application architectures. We do this by following the tech community's newest open-source tools and best practices. If you are designing your next system or in charge of a Kubernetes cluster, and in need of professional guidance, feel free to [contact me on LinkedIn](https://www.linkedin.com/in/daengr/) or [send us over a message](mailto:contact@ipponusa.com).


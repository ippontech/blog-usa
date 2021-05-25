---
authors:
- Dennis Sharpe
tags:
- DevOps
- AWS GovCloud
- OpenShift
- Infrastructure as Code
- IaC
date: 2015-04-23T12:21:50.000Z
title: "A Dev's Intro to DevOps"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/05/a-devs-intro-to-devops.png
---
I have recently been sucked into the field known as "DevOps" and I thought it would be fun to share my story. Having spent most of my career in application development, I am curious to see if others have had a similar experience to mine.

##The Project

The project started with a web application that was running on OKD (open source OpenShift) with many open-source libraries. The objective was to deploy this application in the AWS GovCloud with FedRAMP High compliance. This required _a lot_ of work. We had to move to several commercial products versus open source (i.e. OpenShift) and move to AWS native services wherever possible.

From an application perspective, code needed to be changed to interface with the new services. From a platform perspective, the whole environment needed to be built out from scratch in the GovCloud.

##Freight Train

The challenge here definitely hit me like a freight train. The first one was to set up the network. My first thought was, “What? The network was always already there when I did my work. _We_ have to set this up?” I am being a bit facetious, but I am used to worrying much more about my application architecture than network architecture. Luckily, I was on an effective team with talented engineers ready to build the network.

##Tools

As you might expect, there are many different tools out there to enable Infrastructure as Code (IaC). I relied on my teammates to make selections based on their experience. The primary tools we used were [Terraform](https://www.terraform.io/), [Packer](https://www.packer.io/), and [Ansible](https://www.ansible.com/). I have written applications in a variety of languages so learning these “languages” should be no sweat, right? Well, these tools and languages reminded me more of elaborate configuration files than a programming language. I hope you like [YAML](https://en.wikipedia.org/wiki/YAML), because it is _everywhere_ in IaC.

##Automation

Now that we can create scripts to build infrastructure, we can automate the complete setup of the environment. That came in handy when we needed to build the production environment similarly to the staging environment. The scripts can even be triggered by a pipeline just like application development code. But who builds the pipeline infrastructure? You need a bootstrap script or two to get the environment working that will build your environment. ;)

##Drift

Working on infrastructure can lead to an interesting problem that I never really experienced in application development. As far back as I can remember, we were using version control to manage our code and we could tag a version that would eventually end up in production. One of the first tasks when writing an enterprise application was to make sure the version could be verified in production. We did not typically have to worry about code being modified in the production environment itself.

It is much easier to modify production infrastructure using, for example, the AWS console. How tempting is it to tweak a security group on the console to make everything work? That leads to what we call drift. Now, the actual infrastructure is out of sync with the IaC; it requires the same discipline as application development, even if changes can be made directly.

##Complexity

A big eye-opener for me was the level of complexity involved in getting everything setup properly. If I had to name one of the biggest challenges, it would be handling all the certificates. It seemed every step of the way, we were fighting having the right certificates in the right places. For a project like this, you better have someone who knows how certificates work inside and out, or you are going to be in trouble. Of course there were plenty of other challenges related to backups, patching, pipelines, security, and more. Be prepared to spend a lot of time learning and don’t expect to become an expert overnight.

##How Does It End?

Truthfully, the project is still going. But we have conquered most of the major hurdles. Not to date myself, but I remember when DevOps was more like uploading a WAR file to WebLogic and watching it go. The field has come a very long way since then and it requires a whole new set of skills and expertise to master.

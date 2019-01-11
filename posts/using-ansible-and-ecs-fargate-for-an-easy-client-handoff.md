---
authors:
- Lane Jennison
tags:
- Ansible
- DevOps
- Fargate
date: 2019-01-06T12:21:50.000Z
title: "Using Ansible and ECS Fargate for an easy client hand-off"
image: 
---

A very small start-up approached us do a POC build of their core business product.  They had no technical staff or infrastructure, and we needed to build something quickly and easy to hand off to a freelance technical team of the client’s choosing.

We opted for a simple container based front and backend microservice that was easily bootstrapped with JHipster.  We were able to do so without any VMs, and were able to balance the simplicity of AWS’s modern serverless frameworks and the familiarity of some of the traditional services they provided:   

* ECS Fargate -> Container Management
* CloudWatch -> Logging
* ELB Application Load Balancer -> Proxying / App routing, availability
* Route53 -> DNS
* RDS -> PostgreSQL

That balance let us codify the deployment and hand it off to a freelance DevOps engineer to quickly bringing the site up in a live state.  Just by providing a handful of git repos and basic documentation, they were running in a weekend.

# Managing automation extremes--flexible vs simple 

> Automate everything.
<cite>Every DevOps talk, blog, and whitepaper.</cite>

There's a lot of pressure out there to automate, scale, and be infinitely flexible. There's also a lot of pressure to ship.  Don't get tangled in trying to automate everythign.  If you don't have some frameworks in play, don't expect to have much automation bundled into your project.  It takes time.  Balance what is flexible vs what just needs to work, while insulating yourself from the long-term impact of any [tempermanent](https://www.urbandictionary.com/define.php?term=Tempermanent) solutions.   

# Decisions that worked for us 

## Discrete front-end and back-end microservices with containers 
This is kind of microservices 101, but go ahead and separate front-end code early.   They will deploy and scale differently.  Segmenting also makes it easy to get truthful metrics about network traffic and system load.

## Simple automated builds and redeployment with Gitlab-CI 
Gitlab-CI's YAML job descriptions are really quick to implement and modify.  Our job would build, test, push to repo, and call for the service to redeploy in 50 lightweight lines of YAML.  The client was able to sign up for the base-level hosted Gitlab offering, and their engineer was able to drop in the repo and CI integration.

## AWS Fargate to minimize container management overhead
AWS Fargate has its place.  IMHO that's for small green field projects, where there's a very basic need to run containers without persistent storage, and no other infrastructure to integrate.  Our client's project met this criteria with flying colors. Ansible 2.7 has Fargate support, which further sweetened the proposition.

## Application routing delegated to ALB Rules
Rather than the front end forward API requests to the backend nodes, we let ALB rules route traffic to the appropriate front or backend target group based on URL.  Service discovery was not needed since the balancer took care of routing to the different services.   Deployments instantly become transparent because ALB's healthchecks will respond to a node's availability.

## Database connection information stored in AWS Systems Manager Parameter Store
Since we were using Spring Boot, we had a lot of configuration methods to choose from.   Due to the small scale of the application, we opted for 12-factor style config by setting environment variables for the JDBC path and credentials.  It did not require any code changes to use.  AWS Systems Manager Parameter Store was a simple solution that could easily be extended for more configurations.  To inject the configuration, a function was added to the Docker entrypoint shell script to extract the values from the parameter store and inject into the environment.


## Config-driven Ansible role to provision full AWS VPC environment and deploy ECS services 
Although Terraform would likely be fewer lines of code and CloudFormation would be the most AWS native, we chose to use Ansible for our VPC build.   Since the deployment code would be used by other people, we felt that Ansible code would be the easiest to read and modify.  The role was a great time server. We were able to build concurrent test environments by changing only a few configuration values.

The role will be covered in more depth in another blog post, but here are the functionality highlights:

* Config driven and sane defaults provided.
* Builds VPC
* Creates subnets for DMZ, data, web, and application roles
* Creates security groups for DMZ, data, web and application roles
* Configures ECS Fargate Cluster
* Creates task definitions and services in ECS
* Creates ALB target groups for services
* Configures Load Balancer for target groups.

# Additional Thoughts

## Spend enough energy to assure that back-end and front-end nodes can be scaled easily 
Don't worry about auto scaling out of the gate.  Do assure that _someone_ can easily launch additional nodes or replace nodes with larger instances.  A properly defined ECS task will make it easy to launch additional nodes. 

## Codify security practices up front
Have the pattern in place for secrets management, even if the preferred secrets provider isn't available.  12-factor style is a good place to start. 

Preconfiguring network subnets, and security in the infrastructure deployment code can assure that safe practices for network security make it all the way through deployment.   Having it codified also creates more visibility to help developers build with security in mind. 

## Make sure your toolbox has tools in it 
It takes time to build reliable infrastructure code.   Identify your common design patterns and build flexible deployment code before you need it.  We have been able to quickly re-use our Ansible Fargate VPC role for testing and prototyping.


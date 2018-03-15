---
authors:
- Theo Lebrun
categories:
- JHipster
date:
title: "How to use JHipster efficiently in your company"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/02/JHipster-4-Blog--4-.png
---

[JHipster](http://www.jhipster.tech/) is a fantastic development platform if you want to generate, develop and deploy Spring Boot + Angular Web applications. It generates a fully ready project so fast that you will be addicted of using it. I personnally use it to generate from proof of concept work to multiple Spring microservices. The goal of this blog post is to show how you can easily customize JHipster so it will fit your company's requirements and infrastructure. A common use case after generating a microservice is to add common dependencies, extra configuration or change the Jenkinsfile/Dockerfile. I will explain how to avoid doing manual changes after every application generation and save time.

# Two ways to customize JHipster
Since JHipster is 100% open-source, you can make all the changes you want and adapt it to your needs for free ! There is two ways to do that and I will explain below the pros/cons of them.
SPOILER ALERT: I will explain on how to achieve that using the module way.

## Fork the main generator
Here the [Github](https://github.com/jhipster/generator-jhipster) link, you can of course do a fork and do your custom changes directly in your new repo. That is actually the best way if you want to add new questions and customize JHipster's subgenerators. But keep in mind of something important, because JHipster is evolving every months, you will have to keep your fork up to date. That can be a lot of work especially if the changes are directly done on the original JHipster's files. Merging source and template files will take time and it will depend of the custom changes complexity.

A fork is the right way if you want to add new questions, change the generator's behavior and maintain your own JHipster.

## Create a JHipster module

# Module explanation and creation

## What the module will do

## Generate the module

# Skip already known questions and add custom templates
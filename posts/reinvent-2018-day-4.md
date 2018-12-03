---
authors:
- Raphael Brugier
tags:
- AWS
- reInvent
date: 2018-11-30T21:08:09.000Z
title: "AWS re:Invent 2018 - Day 4"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/11/lambda.jpeg
---

4th Day at re:Invent with Ippon and 2nd keynote led by Dr. Werner Vogels himself. And a lot of excitement for us because a lot of announcements were related to the serverless technologies.

# Serverless

## Everything you need for your IDE - AWS Toolkit

A good news for those of us who like working with an IDE. Developers can now add the AWS Toolkit plugin dedicated to Lambdas and SAM (Serverless Application Model) templates. For now, the plugin is only available for  IntelliJ (preview), PyCharm and Visual Studio code (preview)
[AWS Toolkits for PyCharm, IntelliJ (Preview), and Visual Studio Code (Preview)(]https://aws.amazon.com/blogs/aws/new-aws-toolkits-for-pycharm-intellij-preview-and-visual-studio-code-preview/)

## Lambdas in any language
Coder sur Lambda avec votre langage préféré

Something people have been asking for a long time, the ability to run any language in Lambdas functions. Today, C++ and Rust are already available, and Erlang, Elixir, PHP, Cobol (yes, Cobol!) are being worked on. In addition, Ruby is now natively supported by Lambdas.

[AWS Lambda – Use Any Programming Language and Share Common Components](https://aws.amazon.com/blogs/aws/new-for-aws-lambda-use-any-programming-language-and-share-common-components/)


## Lambda Layer

Managing dependencies and native binaries have always been complicated for Lambdas. The developer had to embed them in the artifact and unpack them at runtime. You can now prepare the dependencies using Lambda Layer, pretty much the same you would prepare an EC2 AMI.

[AWS Lambda – Use Any Programming Language and Share Common Components](https://aws.amazon.com/blogs/aws/new-for-aws-lambda-use-any-programming-language-and-share-common-components/)


## New services for Step Functions

Step Functions as always been the best solution to orchestrate several Lambdas. With the addition of the new services, you can now orchestrate other services like Compute, Database, Messaging, Analytics, and Machine Learning
https://aws.amazon.com/fr/blogs/aws/new-compute-database-messaging-analytics-and-machine-learning-integration-for-aws-step-functions/


# Best Practices

## AWS Well Architected Tool

All the AWS experts have read the [Well Architected framework and white papers](https://aws.amazon.com/architecture/well-architected/) and often look back at them during audits and migrations. AWS now offers a tool to guide you through a checklist and series of questions to validate your cloud follow all best practices.

[AWS Well-Architected Tool – Review Workloads Against Best Practices](https://aws.amazon.com/blogs/aws/new-aws-well-architected-tool-review-workloads-against-best-practices/)


# Streaming

## Amazon Managed Streaming for Kafka

You probably already know Kafka and that we are huge fans of [Kafka at Ippon](https://blog.ippon.tech/tag/apache-kafka/). We also know that Kafka could be complicated to deploy and manage at scale in production. Great news! AWS now offers a managed Kafka service which will allow us to get started quickly.

[Amazon Managed Streaming for Kafka (MSK)](https://aws.amazon.com/msk/)

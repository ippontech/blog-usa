---
authors:
- Raphael Brugier
tags:
- AWS
- reInvent
date: 2018-11-28T21:08:09.000Z
title: "AWS re:Invent 2018 - Day 1"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/11/reinvent2.jpeg
---


Hi everyone,

AWS re:Invent is huge! And each day has a lot of new announcements. Here are our favorites:

# Storage

## Managed SFTP Service for S3
S3 now offers a managed SFTP service. You can integrate with S3 using your “legacy” SFTP commands, and even use the IAM policies to control accesses.
A simple service, but one that can simplify the migration of legacy applications. 

[AWS Transfer for SFTP – Fully Managed SFTP Service for Amazon S3](https://aws.amazon.com/blogs/aws/new-aws-transfer-for-sftp-fully-managed-sftp-service-for-amazon-s3/)


## Optimized agent for data transfer
A tool to simplify and optimize your S3 to local or local to S3 data transfers.
Very useful to move a lot of data into or from S3.

[AWS DataSync – Automated and Accelerated Data Transfer](https://aws.amazon.com/blogs/aws/new-aws-datasync-automated-and-accelerated-data-transfer/)


## New S3 storage class: Intelligent-tiering

This new storage class allows to dynamically and automatically change the storage type of your S3 objects. AWS uses machine learning algorithms to switch your objects between the frequent and infrequent access classes depending on how they are accessed.

[Automatic Cost Optimization for Amazon S3 via Intelligent Tiering](https://aws.amazon.com/blogs/aws/new-automatic-cost-optimization-for-amazon-s3-via-intelligent-tiering/)


# Analytics

## Athena Workload Isolation (Beta)
The biggest thing of this tool is to be able to limit the amount of data used during the processing and being able to cancel the request.
This is especially useful when new users are running long (and costly) requests that may be blocking other resources.

# Connectivity 

## Transit Gateway

Transit Gateway simplifies the connection between all the VPCs. Before that, VPCs did not support transitive peering.
Using Transit Gateway you can now to build a hub-and-spoke network topology

[Use an AWS Transit Gateway to Simplify Your Network Architecture](https://aws.amazon.com/fr/blogs/aws/new-use-an-aws-transit-gateway-to-simplify-your-network-architecture/) 


# Compute

## Firecracker - Lightweight virtualization

AWS has open sourced a new virtualization technology - written in Rust - that promises to run microVMs (think serverless functions) in a fraction of a second. Firecracker is the technology behind the Lambda service, so it’s really exciting to see AWS open sourcing it.

[Firecracker – Lightweight Virtualization for Serverless Computing](https://aws.amazon.com/blogs/aws/firecracker-lightweight-virtualization-for-serverless-computing/)
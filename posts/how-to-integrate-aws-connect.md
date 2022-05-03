---
authors:
- John Strickler
tags:
- Javascript
- AWS Connect
- Agent Servicing
date: 2022-04-29T00:00:00.000Z
title: "How To Integrate AWS Connect"
image: 
---

Log in as an agent
Turn on/off availability
Receive live chats
Receive phone calls
All through a single interface
Integrates directly into your UI
No separate applications to manage.
Simple, Scalable, Managed.

Leverage AWS Connect's Admin backend and workflows.

If you're looking to add omnichannel communcation from your customers to your call center agents then look no further.  [AWS Connect](https://aws.amazon.com/connect) is a cloud contact center that can be seamlessly integrated to any existing agent servicing application.  In this post, I demonstrate how to get started with AWS Connect and how to integrate the contact control panel ("CCP") directly into a web-based application so that your agents can receive calls, chat with connects and manage interactions from a single user interface. 

The implementation is divided in to two phases:
- Setting up the AWS Connect instance on your AWS Account
- Integrating the CCP to your application using [Amazon Connect Stream](https://github.com/amazon-connect/amazon-connect-streams)

## Creating and configuring the AWS Connect instance

The first step is create your AWS Connect instance.  If you're experimenting then you'll be glad to hear that AWS Connect is covered by AWS's free tier.  You can check out all that is covered for this service [here](https://aws.amazon.com/connect/pricing#AWS_Free_Tier).   Follow these steps to set up your Connect instance:

1. Go to "AWS Connect" in your account and click "Create Instance"
1. Identity Management
  - Select "Store users in Amazon Connect" as your identity management option
  - Enter a name (alias) for your instance
1. Add administrator
  - Select "Specify an administrator".  This is a separate login from your AWS account.  It will be used to log in to and manage your Connect instance.
  - Fill in the remaining fields
1. Telephone Options
  - Allow incoming calls
  - Allow outgoing calls
1. Data storage
  - Keep defaults
1. Review and click "Create instance"

### Configuring yuor AWS Connect Instance

Log in to your newly created Connect instance using the credentials that you set up above in step three.  The first time that you log in you will be presented a Dashboard page with a step-by-step tutorial on configuring and tuning your Cloud contact center.  In this blog, we'll do just the configuration that is necessary to integrate the CCP to your application. 


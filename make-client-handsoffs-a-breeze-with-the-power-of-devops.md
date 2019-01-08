---
authors:
- Lane Jennison
tags:
- Ansible
- DevOps
- Fargate
- Professor Farnsworth
date: 2019-01-06T12:21:50.000Z
title: "Make client handoffs a breeze with the power of DevOps"
image: 
---

Recently we completed green field project for a _very_ small client.  Since the easy part was done, the only thing left was the [hard part](https://www.youtube.com/watch?v=hqm8_Du8V2Q)--handing off a whole cloudfirst platform to client without dediated technical resources and zero infrastructure.  The entire hand-off was a matter of pushing 4 git repos, and the client's new part-time DevOps Engineer was online in an evening.   

## Managing automation extremes--flexible vs simple ##

```
Automate everything.

			-- Every DevOps talk, blog, and whitepaper.
```

There's a lot of pressure out there to automate, scale, and be infinitely flexible.  There's also a lot of pressure to ship.   Don't get tangled in trying to automate everythign. If you don't have some frameworks in play, don't expect to have much automation bundled into your project.  It takes time.  Balance what is flexible vs what just needs to work, while insulating yourself from the long-term impact of any [tempermanent](https://www.urbandictionary.com/define.php?term=Tempermanent) solutions.

## Decisions that worked for us ##

### Discrete front-end and back-end microservices with containers ###

### Simple automated builds and redeployment with gitlab-ci ###

### AWS Fargate to minimize container management overhead ###

### Application routing delegated to ALB Rules ###

### Database connection information stored in AWS Systems Manger Parameter Store ###

### Config-driven ansible role to build VPC environment and deploy ###

## Some listilce-type takeaways ##

### Spend enough energy to assure that back-end and front-end nodes can be scaled easily ###
Don't worry about auto scaling out of the gate.  Do assure that _someone_ can easily launch additional nodes or replace nodes with larger instances.   

### Codify security practices up front ###
Have the pattern in place for secrets mangement, even if the provider isn't there.  12-factor style is a good place to start.   

Preconfiguring network subnets, and security in the infrastrucutre deployment code can assure that safe practices for network security make it all the way through deployment.


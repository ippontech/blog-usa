---
authors:
- Jonathan Scharf
tags:
- Jenkins
- CodePipeline
- AWS
- CICD
date: 2021-08-24T12:21:50.000Z
title: "Jenkins vs. AWS CodePipeline"
image: 
---

If you have worked with CI/CD, chances are you have used the likes of Jenkins, GitLab, GitHub Actions, etc. One service I don’t often hear folks talk about is AWS CodePipeline. In this blog, we will make some comparisons between Jenkins and AWS CodePipeline, and see if perhaps it’s time for you to make the switch on your projects.


# What is Jenkins?

Jenkins seems like the place to start. As of writing this blog, Jenkins is by far the market leader for CI/CD pipelines, and has the added benefit of being open source. Boasting over 1,800 plugins, Jenkins has been able to integrate with many different tools and services while requiring little additional work. Jenkins also easily integrates with all of the major public cloud providers and a vast number of developer tools. Jenkins’ other main benefit is its price -- free! While there are companies that offer Jenkins as a SaaS solution, anyone can download and start using it. This of course comes with the downside of needing to maintain everything about your Jenkins environment, which at scale can become difficult.


# What is AWS CodePipeline?

AWS CodePipeline is a fully-managed CI/CD service and is actually the orchestration of a few other AWS services, including CodeCommit, CodeBuild, and CodeDeploy. Because it is an AWS service, you can use CodePipeline to deploy to other servers running the agent. Since these are all managed services, there is no underlying infrastructure you will have to maintain. But managed services come with a price tag. There is a cost for having an active pipeline, as well as the compute time required for building and some deployment operations. 


# Price Comparison

We mentioned earlier that Jenkins is free and open source and that there are costs associated with aspects of CodePipeline, but there is more to the story than that. While Jenkins is free to run, you must consider both the cost of the infrastructure it is going to run on, as well as the cost of time and knowledge to maintain Jenkins, the infrastructure, and any plugins. CodePipeline on the other hand has no cost associated with infrastructure, but has a compute cost every time you kick off a build. These prices depend on the instance type you need to build on and are billed by the minute. The full price breakdown can be found [here in the AWS docs](https://aws.amazon.com/codebuild/pricing/?nc=sn&loc=3). 


# Scalability

When using CodePipeline, each pipeline run executes independently and in parallel with each other. Since CodePipeline is a managed service, you can run many pipelines in parallel without having to deal with additional infrastructure. Jenkins on the other hand is limited by the number of executors you have. While there are ways to have the Jenkins executor be a Docker container that spins up on demand, you will still be limited by your hardware. Jenkins also only allows a single instance of a pipeline to be running at once, so you must wait until your deployment is complete before starting your next build.


# Multibranch Pipelines

Running builds from multiple branches is important when working on a feature to know that your changes continue to build properly as well as running basic code analysis and deploying to a DEV server. Jenkins can be configured to automatically pick up any new branch that is created and run your pipeline. CodePipeline requires intervention to determine which branches are tracked for builds. If you are working on multiple short-lived branches, this process can become cumbersome.


# Integrations

Both services are great for deploying to AWS services, CodePipeline natively, and Jenkins through plugins. While it is nice having a native connection within your AWS account, setting up plugins are usually straightforward. With the 1,800+ plugins available for Jenkins, there are existing integrations for most tools you would use in your pipeline. You can utilize non-AWS integrations in your CodePipeline as well, but this typically involves writing custom scripts or API calls. 


# So which should you use?

If only it were that simple. In my time as a DevOps Engineer, I’ve found that the more tools I have in my belt, the stronger case I can make on which one is the best for the job. Both Jenkins and CodePipeline have their place in different organizations and on different types of projects, and hopefully this blog gave you a little more background on some of the key differences to consider when choosing one over the other.

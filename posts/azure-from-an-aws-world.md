---
authors:
- Jonathan Scharf
tags:
- Azure
- AWS
- Cloud
- DevOps
date: 2022-12-20T12:00:00.000Z
title: "Azure from an AWS World"
---

Coming from an AWS background, when I was asked to help out on a project that was exclusively using Azure for **EVERYTHING** including infrastructure, code repository, and CI/CD, I was nervous. I knew that both providers had many similarities in offerings as they continued to mature, but to what level? Every service had a different name. How did the pieces fit together? What menu do you even navigate to in order to verify a setting. Even the Terraform modules were different. Where are you even supposed to start?


## Infrastructure

Luckily for us, Microsoft has a very handy [guide for mapping an AWS service to its Azure equivalent](https://learn.microsoft.com/en-us/azure/architecture/aws-professional/services). Need an S3 bucket? Just create a blob storage. An EC2 instance? Spin up an Azure Virtual Machine. The list goes on and on, but I found myself constantly referring to this list to better map in my mind what the different services were designed to do. While the similar services still have their differences, this was an invaluable resource.

Continuing on the infrastructure side, I found that Azure Resource Groups were super helpful, especially for someone starting out in Azure. Resource Groups organize a group of resources in a way that logically makes sense to you. In this case, we have multiple applications hosted in Azure, each with its own Blob Storage (S3), a database, and Azure Functions (Lambda). Each application had its own resource group. You could easily see each service for that application, as well as the associated costs for the resources in that group. I also liked that when creating your infrastructure with Terraform, you could select which resource group you wanted to add the resource to.

Finally for the Infrastructure side, I liked how Azure uses Subscriptions.  This is another logical way to break up your infrastructure, but at a higher level. Subscriptions are made up of Resource Groups and Resource Groups are made up of resources. Subscriptions are handy to associate user accounts with the resources they are allowed to create and interact with. Essentially this is how you can set up a dev, UAT, and production environment all within the same overarching Azure account without having to create multiple accounts. I liked this feature as it allowed for a single log in and an easy way to view resources (and resource groups) for that particular environment. You can also view the costs associated with a particular subscription to better understand and control your costs.

Below is a picture showing how the different resource management levels fit together

![Azure resorce management levels](https://github.com/jscharf-ippon/blog-usa/blob/master/images/2022/12/azure-management-levels.png?raw=true)


## Azure DevOps

There are many features under the Azure DevOps umbrella. We utilized Azure Repos (hosted git), Azure Boards (a Jira like board for tracking stories), and Azure Pipelines for our full suite of CI/CD tools. The Jira-like feature operated the same as any other scrum board. You could add stories to the backlog with descriptions and acceptance criteria, assign story owners, drag stories across the board, and close them out. All of the standard things you would expect. 

Azure Repos had all of the features you would expect from a hosted git service. You can use your standard git command line commands you are used to with GitHub, GitLab, AWS CodeCommit, BitBucket, and so on. I found the UI to navigate around to different repositories and branches to have a bit of a learning curve, but this was likely due to it being unfamiliar. There were all of the standard security controls that you would expect as well including whitelisting which users can commit to a repository, restricting commits to a branch without a pull request, and setting default and required approvers for pull requests. 

Finally there is the Azure DevOps Pipeline, the Azure CI/CD offering. There are two different ways you can build out your pipelines, either with YAML or by using the UI. There are some features that the UI method of building pipelines can not handle, but overall you could build robust pipelines with both methods. I found it very helpful to be able to use the UI to try something out, and then export that step or task to YAML to use in our pipeline as code. This was especially useful when utilizing the large library of plugins that are available.This was very helpful when learning a new pipeline as code format, though the major players all seem to be coalescing to having similar syntax.


## Conclusion

Going into this project, I was worried about using a new cloud that I was not so familiar with, just as many are when learning and using a new technology. I was pleasantly surprised by how easy it was to transition my thought process to a new provider and take what I knew about AWS and apply it to Azure. While this blog highlighted a few of the things I liked about Azure, as I get to use it in future projects, I’m sure I will come to learn more about some of its quirks and things that it does not do as well as AWS. Until then, I have a newfound appreciation for Azure and better understand why so many organizations are turning to Microsoft when starting their cloud journeys. 

If you or your organization are looking to start your Azure journey, or are considering a cloud transformation, Ippon is here to help. Our Cloud and DevOps Practice is available to guide and assist with your migration. [Reach out on our website](https://us.ippon.tech/contact/) to see how we can evolve your cloud footprint.

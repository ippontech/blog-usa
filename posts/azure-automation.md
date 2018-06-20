---
authors:
- Jake Henningsgaard
tags:
- Azure
- Azure Automation
- Runbook
- VSTS
date: 2017-08-07T15:13:39.000Z
title: "Azure Automation: Leveraging Runbooks"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/08/Azure-Automation--Leveraging-Runbooks-Blog--1-.png
---

I've had the opportunity to build a platform to ingest 3rd party data from multiple sources from scratch.  The platform is designed and built entirely on Azure Cloud using a number of resources available within Azure.  As is the case with many Agile projects, my team designed the platform to consists of three identical environments: DEVELOPMENT (DEV), UAT, and PRODUCTION (PROD).

The replication of environments along with the need to maintain strict configurations for all components across the platform encouraged us to automate as much of the platform deployment as possible.  This would reduce the complexity and amount of time taken to deploy a new environment, as well as track the configuration changes to the platform.

## Runbooks
Runbooks are script-like files that are based on Windows PowerShell.  Runbooks are used to deploy, provision, and manage the various resources available in Azure (e.g. create resource groups, provision HDInsight cluster, or provision a VM). They are created and managed within an Azure Automation account ([Creating a Runbook](https://docs.microsoft.com/en-us/azure/automation/automation-creating-importing-runbook)).  Runbooks can be run manually or automatically by attaching a schedule.

There are two main components of Azure Automation runbooks that we found to be useful:

1. Deploying child runbooks from a master runbook
2. Versioning the runbooks with Git

## Structure the Runbooks for Multiple Environments
As mentioned previously, the platform consisted of three identical environments: DEV, TEST, and PROD.  Therefore, we adopted some best practices.

1. Group your resources in a resource group based on lifecycle.
2. Create map variables for naming resources based on their environment.
a.  Example: `$admin_rg = @{dev = "admin-dev-rg"; uat = "admin-uat-rg"; prod = "admin-prod-rg"}`
3. Define the order in which the runbooks should be executed
a. The `admin_rg` runbook is intended for resources that will exist 24/7, such as resource groups or virtual networks (VNet).

## Calling Child Runbooks
In order to reduce the complexity, time, and potential human error, the entire platform can be deployed from a single runbook.  As shown in the animation below, the `master` runbook is used to deploy both the `create_rg` and `storage` runbooks.  However, notice that the `master` runbook will wait until `create_rg` has finished before calling the `storage` runbook.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/07/runbooks.gif)

When writing your "master" runbook you must first define your parameters.  In this case I defined a set of parameters for each child runbook that I was calling:

```text
    $adminParams = @{"environment" = $environment}
    $storageParams = @{"environment" = $environment; "storageName" = "enginesa01"}
```
Each child runbook has a different set of input parameters that it expects (seen above).  After defining your child runbook parameters you can call the child runbook:

```text
Start-AzureRmAutomationRunbook -AutomationAccountName "automationBlog" `
     -ResourceGroupName "automationBlog" `
     -Parameters $adminParams `
     -Name "create_rg" `
     -wait

Start-AzureRmAutomationRunbook -AutomationAccountName "automationBlog" `
     -ResourceGroupName "automationBlog" `
     -Parameters $storageParams `
     -Name "storage"
     -wait
```
The `storage` runbook depends on a successful completion of the `create_rg` runbook before it can be started.  The `-wait` option on the `Start-AzureRmAutomationRunbook` cmdlet will force the `master` runbook to wait for the child runbook to finish before moving on.

## Implement Versioning
One challenge that we also encountered in Azure Automation was a good solution for versioning the runbooks.  If you intend to use Github, you're in luck. [Azure Automation integrates well with Github](https://docs.microsoft.com/en-us/azure/automation/automation-source-control-integration).  In our case, however, we were using VSTS so things were a little more complicated.  Luckily, Microsoft provides some documentation on how to [integrate Azure Automation with VSTS](https://docs.microsoft.com/en-us/azure/automation/automation-scenario-source-control-integration-with-vsts).

I won't go into detail on how to version your runbooks in VSTS since Microsoft already does a pretty good job on this.  However, I would like to highlight a few aspects regarding this solution.  Firstly, the development workflow is a bit obscure (I wasn't able to use Powershell ISE).  The workflow I find works best is:

1. **Make your changes/updates** to the runbooks in the Azure portal
a. This allows you to test the runbooks as you develop.
b. Be prepared for a slow feedback loop.
2. Identify good **incremental stopping points**, and SAVE your runbook.
3. **Copy** the entire runbook content. I recommend against manually adding your changes to the repository file, as you're more likely to introduce human error.
4. **Paste** the runbook content to your repository file (e.g. `create-rg.ps1`).
5. **Commit** the changes to the repository file.
6. **Push** your changes to the remote repository.
a. This will **automatically kick off the webhook** to publish those changes to the Azure Automation account.
b. This can take a few minutes.

I've put together some sample runbooks that demonstrate  the techniques described above.  Feel free to check them out [here](https://gist.github.com/jhennin/47d8e98e985137e36bbd94b8a8de4a4f).

If you have any questions or comments about what you've read, we'd love to hear from you! Please send your inquiries to [contact@ippon.tech](mailto:contact@ippon.tech).

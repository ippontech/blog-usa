---
authors:
- Jake Henningsgaard
categories:
- Agile
- Culture
- Git
- Workflows
date: 2016-12-16T16:59:00.000Z
title: "On-boarding… a developer’s tale"
id: 5a267e57dd54250018d6b624
image: 
---

I recently moved onto a new project that had already completed several of the first key milestones. I joined the team as a third developer and had the opportunity to spend a few days getting my environment setup and exploring the code base. I thought it might benefit me and possibly others to try and define some concrete checkpoints when on-boarding a developer onto existing projects given the number of varied projects in the consulting world.

### Step 0
Depending on the project, there are a couple key tasks that must be completed before anything else can be accomplished:

* **Account setup** (GitHub, GitLab, Jira, company specific accounts)
Identify primary team **communication channels** (HipChat, Email, and/or Slack)
Begin exploring team **knowledge sharing** documents

### Setting up Your Environment

This is arguably the most crucial step to on-boarding onto a new project. After all, there is not much that can be done until you have a working development environment.

1 Clone the project repository.
2 Choose your IDE if applicable and import your project.
3 Run the application and verify that your output is the same as the other team members.

Steps 1–3 are oversimplified and often include many other steps in between. Depending on the application, you may not need or want to use an IDE. Additionally, if you’re developing and testing locally, you may need to install other tools. In my case, the project was heavily dependent on [ElasticSearch](https://www.elastic.co/products/elasticsearch). I also had the option of testing on an AWS cluster, or using [Docker](https://www.docker.com/) to test locally. There are often a few tricks to getting the environment setup that are project specific and may require some assistance from a developer that has been on the team longer.

### Identify the Workflow
Every development project may have a slightly different workflow. Here are a few common questions you should ask yourself and/or your new teammates to get acclimated with the project workflow:

* What is the sprint cycle/schedule? Or, what is the current state of the ticket queue?
*  How are tickets assigned?
What is the lifecycle for a ticket, i.e. what are the different stages a ticket goes through?
* What is the peer review and testing process?

Furthermore, it is very important to know what Git workflow is being used ([centralized](https://www.atlassian.com/git/tutorials/comparing-workflows/centralized-workflow), [feature branch](https://www.atlassian.com/git/tutorials/comparing-workflows/feature-branch-workflow/), [gitflow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow/), [forking](https://www.atlassian.com/git/tutorials/comparing-workflows/forking-workflow), etc.). I recommend that you ask the project lead if they can create a ticket for you to setup your environment. This will give you a chance to experience the ticket creation, assignment, and completion stages. You may also find that you have to modify or create some configuration files as part of your environment setup. This is a perfect opportunity to get a feel for how your new team manages the project’s Git workflow and give you a chance to commit your first changes to the code.

### Tips, Tricks, and Tools
* Do your best to **incorporate the same tools your teammates are using**. If everyone is using a particular IDE or testing locally using Docker, do the same. At least until you are more comfortable with the project.
* Make your way to a working **compile/build** of the project as quickly as possible. This may seem pretty straightforward and easy, however there is no telling what strange obstacles could come up that would slow your ability to start contributing to the project.
* **Commit early and often**. Make your first commit to the project as soon as possible and make an effort to commit often. You can dial back the commit frequency as you get more comfortable, or just [squash](https://ariejan.net/2011/07/05/git-squash-your-latests-commits-into-one/).
* Utilize **pair programming**.  This will help in getting to know the code base more quickly and learning any undocumented knowledge specific to the project.
* Get familiar with and rely heavily on **code reviews**.  This will ensure that all developers on the team are on the same page and will aide junior developers in improving more quickly.

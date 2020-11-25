---
authors:
- Matt Reed
tags:
- AWS
- IAM
date: 2020-11-24T00:00:00.000Z
title: "In Real Life - Amazon Web Services: Identity Access Management"
image: 
---

## A Quick Note about In Real Life

For some reason, non-physical concepts have always confused a lot of people unfamiliar with the digital realm. Even those who do work in the IT-services industry can get tripped up by subtle trivialities. This is an attempt to distill the complexities down into more digestible ideas for everyone.

## Identity Access Management a.k.a. Sharing is Caring

To begin, someone who has never heard of "Identity Access Management" (or IAM for short) might be unsure about what it should do in the first place. The questions to ask here are:

1. What is being worked on?
1. Who is working on it?

Answers will range widely here; yet, a commonality amongst all the answers will require the ability to divide up and allocate the work. In the [getting started page](https://aws.amazon.com/iam/getting-started/), AWS states this about the service:
    "AWS IAM helps you manage access to your AWS resources. Using IAM, you can manage who can use your AWS resources (authentication) and what resources they can use and in what ways (authorization)."
It is a bit of a complicated explanation for a fundamental concept everyone learned when growing up: How to share properly with others. At its core, IAM is a tool for allowing other people to use one's AWS resources.

### Resources In Real Life vs Amazon Web Services

"Resources" could mean anything that a person contractually buys. In real life, people might own or lease things such as land, houses, cars, books, food, furniture, clothes, etc. Whereas in AWS, someone would pay for various technological services that can be utilized for making websites or crunching numbers behind the scenes.

### Letting Others In

Sharing can be a hard concept to grasp. Many questions exist not just about how much to share but also with whom and for how long. This is why permissions are given to guests entering a home for a dinner party, children when they want to stay up past their bedtime on New Years Eve, or soldiers when they need to speak freely to their superior. This system of people and permissions is intuitively understood.

But when translating this to the digital space, it is not as straightforward. AWS attempts to establish a sharing system by defining 4 key terms:

1. Users - people
1. Groups - a collection of users
1. Policies - a contract of permissions
1. Roles - entities assumed to operate under certain permissions

Here, what is best to keep in mind is Policies are paramount. They delineate the permitted actions a service can take on one or more resources (i.e. A maid service may enter the home). Everything else cascades from these Policies.

Groups have Policies that they must adhere to. These could be organizations or teams, but everyone is designated to act under the same Policy restrictions. These Users are identified by a name and given a password. It is a best practice to assign Users to a Group so that they only have certain job functions.

It is worth specifying that Roles are **not** typically Users in AWS. Instead, think of these entities like robots filling in for actors in a Broadway play. As robots, they do not improvise. They are permitted to perform only the set of directions they are told so that the play can proceed. In this way, they adhere to Policies similar to Groups.

### A Concrete Example

Imagine someone hires a cat sitting service while they are out on vacation. The company agrees to keep cats healthy and maintain a clean environment during your departure through feeding, administering any medications needed, and scooping the litter box. They will send a sitter, either Bob or Mary, for the job.

Policies

What if only one of the cats needs a particular medication?

What if only one of the cats is friendly towards strangers and others should be ignored by the sitter?

Groups

What if Mary or Bob gets sick and a replacement is called in by the cat sitting service? Groups easily allow for a flexibility of new Users and decommissioning inactive ones.

Users

The sitter is generated a passcode for the garage so she can enter and feed the cat.

What happens if Mary or Bob forgets their passcode?

Roles

Hopefully employees at any reputable cat service would have been instructed by the company's policy not to eat the cat food or use the cat's facilities themselves. But perhaps they are a little forgetful in the moment, Roles can be assigned to the cats so only they are permitted to use the food bowl or litter box.

### Locks, Keys, and Secrets

### Sources

* [Getting started with AWS IAM](https://aws.amazon.com/iam/getting-started/)
* [IAM FAQs](https://aws.amazon.com/iam/faqs/)

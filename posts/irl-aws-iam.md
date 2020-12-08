---
authors:
- Matt Reed
tags:
- AWS
- IAM
date: 2020-12-04T00:00:00.000Z
title: "In Real Life - Amazon Web Services: Identity Access Management"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/12/iam_kitten.png
---

## A Quick Note about In Real Life

Digital concepts can get confusing for a lot of people. Even those who work in the software industry can get tripped up. This is an attempt to reduce the complexities and turn them into fun ideas for everyone.

## Identity Access Management a.k.a. Sharing is Caring

Someone who has never heard of [Identity Access Management (IAM)](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html) might be unsure about what it should even do in the first place. When working on any project, there needs to be a way to divide up and allocate the team. In the [getting started page](https://aws.amazon.com/iam/getting-started/), AWS states this about the service:
    "AWS IAM helps you manage access to your AWS resources. Using IAM, you can manage who can use your AWS resources (authentication) and what resources they can use and in what ways (authorization)."
That is a complicated explanation for what everyone has learned when growing up: How to share with others. At its core, IAM is a tool for sharing a project's AWS resources.

### Resources In Real Life vs Amazon Web Services

"Resources" could mean anything that a person contractually buys. In real life, people might own or lease things such as land, houses, cars, books, food, furniture, clothes, etc. Whereas in AWS, someone would pay for various technological services utilized in making websites or crunching numbers behind the scenes.

### Letting Others In

Sharing can be a hard concept to grasp. Many questions exist not just about how much to share but also with whom and for how long. This is why permissions are given to guests entering a home for a dinner party, children when they want to stay up past their bedtime on New Year's Eve, or soldiers when they need to speak freely to their superior. These systems of people and permissions are intuitively understood.

But when translating them to the digital space, it is not as straightforward. AWS's solution to this establishes its own sharing system. There are four, key terms that need to be defined:

1. User - a person who needs access to AWS resources
1. Group - a collection of one or more users who *all* need the *same* access to AWS resources
1. Policy - a contract of permissions that will allow or restrict actions
1. Role - an entity (usually an AWS service) that operates under a policy

In AWS, Policy is paramount. It delineates the permitted actions a Group or service can take on AWS resources (i.e. A maid service may enter the home). Policies should be created first because Groups and Roles will use these next.

Groups have Policies that they must adhere to. These could be organizations or teams, but everyone acts under the same Policy restrictions. These Users are identified by a name and given a password. It is a best practice to assign Users to a Group so that they only have certain job functions.

It is worth specifying that Roles are **not** typically people in AWS. Instead, think of these entities like robots filling in for actors in a Broadway play. As robots, they do not improvise. They are permitted to perform only the set of directions they are told so that the play can proceed. In this way, a Role adheres to a Policy similar to how a Group would.

### Locks, Keys, and Secrets

IAM is about letting others in just as much as it is about keeping others out. Users authenticate themselves in AWS by a name and password. This is because they (and only they) should be trusted to access the services/resources outlined in their Group's Policies. Anything that is not in these Policies should be off-limits.

At a bank, the account owner only has access to their money. The bank's teller, though, needs access to all the vaults to withdraw/deposit the funds for any person's account. Both authenticate themselves in the system but with different levels of authorization.

### Going Through An Example

Instead of just talking about AWS in the real world, the following brings a real-world example into AWS. While this will not really work, it should help to organize the concepts discussed so far. Ignore the warnings displayed in the screenshots below for what IAM does not recognize.

Imagine someone hires a cat sitting service while he or she is out on vacation. The company agrees to keep cats healthy and maintain a clean environment during their departure through feeding, give any medications necessary, and change the litter box. They will send a sitter, either Bob or Mary, for the job. How do the Users, Groups, Policies, and Roles need to be configured to ensure success and security?

#### Start with the Policies

The company had agreed to scoop out the cat food into their bowl, administer medications, and perform waste management by cleaning the litter box. So what policies can be created around these services, actions, and resources?

1. Feeding -> Scooping -> Food Bowl
1. Medication -> Administering -> All Cats
1. Waste Management -> Cleaning -> Litter Box

The Medication Policy might look like this in AWS:

![AWS Medication Policy](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/12/aws_medication_policy.png)

What if only one of the cats needs a particular medication? A specific cat can be addressed in the policy (referred to in AWS as an [Amazon Resource Name (ARN)](https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html). Here the `Fluffy_Tabby` is the targeted resource:

![AWS Medication Policy for Fluffy](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/12/aws_medication_policy_for_fluffy.png)

Before creating the policy, there will be an opportunity for review and to name it. Here is what that looks like for the `FeedingScoopingFoodBowl` policy:

![AWS Feeding Policy Review](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/12/aws_feeding_policy_review.png)

It might be worth mentioning multiple policies can be imported to create a new, all-encompassing policy.

![AWS Importing Policy](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/12/aws_importing_policy.png)
![AWS All Encompassing Policy](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/12/aws_all_encompassing_policy.png)

But be aware if any underlying/imported policies are changed, it will **not** change this new policy. This is important for the next section because even though it may be easier to just attach a single policy to a group, it will be harder to maintain these various policies.

#### Creating A Group With Policies

What if the scheduled sitter gets sick and a replacement is called in by the company? Groups allow for greater flexibility when adding new Users and decommissioning inactive ones. The following shows the summary for a group of `CatSitters` in AWS including the attached policies:

![AWS CatSitters Group](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/12/aws_catsitters_group.png)

#### Users and their Accesses

Careful observers might have noticed the `CatSitters` group already contained a couple of users. Sitters "Bob" and "Mary" were first created then added to the group:

![AWS CatSitter Users](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/12/aws_catsitter_users.png)
![AWS CatSitter Users Added to Group](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/12/aws_catsitter_users_added_to_group.png)

If the "AWS access type" seems confusing, "Programmatic access" is basically giving Bob and Mary the door keys. "AWS Management Console access" would be akin to a keyless-entry door where the passcode is either a custom passcode or an autogenerated one. Upon generation, the passcode must be written down and sent to Bob and Mary later so that he/she can successfully enter the home. The advantage is passcodes are easier to rotate than keys. Bob and Mary may change their passcodes once they key-in to something more memorable.

The obvious question is what happens if Mary or Bob forgets their passcode altogether? It is easy to create a new one for them as is seen here:

![AWS CatSitter Users Summary](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/12/aws_catsitter_users_summary.png)
![AWS CatSitter Users Manage Console Access](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/12/aws_catsitter_users_manage_console_access.png)

#### Roles for Cats

To ensure the cats have access to their food bowl and litter box, the cats can be assigned to a role. Before a role can be created, though, a new policy is necessary:  Feeding -> Eating -> Food Bowl

![AWS Feeding Eating Policy](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/12/aws_feeding_eating_policy.png)
![AWS Create Role](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/12/aws_create_role.png)
![AWS Create Role Attach Policies](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/12/aws_create_role_attach_policies.png)
![AWS Create Role Review](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/12/aws_create_role_review.png)

### Wrapping Up Cat's Cradle

Hopefully, the employees at any reputable cat service would know what they are doing and not to eat the cat food or use the cat's facilities themselves... While this example conceptually outlines how Identity Access Management in AWS could be utilized to assure a cat owner their beloved pets are taken care of, it extends further. A person or organization using AWS should feel more confident and comfortable allowing groups of strangers to perform very specific tasks within their digital workspaces.

### Sources

* [Identity Access Management (IAM)](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html)
* [Getting started with AWS IAM](https://aws.amazon.com/iam/getting-started/)
* [IAM FAQs](https://aws.amazon.com/iam/faqs/)
* [Amazon Resource Name (ARN)](https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html)

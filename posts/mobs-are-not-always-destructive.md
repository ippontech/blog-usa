---
authors:
- Matt Reed
tags:
- Agile
- Software Development
date: 2018-11-12T00:00:00.000Z
title: "Mobs Are Not Always Destructive"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/11/mobbing-goal-rules.png
---

Since early November my team has started utilizing a software development strategy that I had never heard of before:  Mob Programming. I am sure many might envision situations as I did of an angry group of engineers pitching comments and improvements on my code with a multitude of GitHub Pull Requests from their forked repositories. Good news, it was nothing like this pitch-forked scenario. Instead of it being a destructive or distributed activity, my experience with mob programming was quite the opposite.

# The Basis

The easiest place to start understanding the concept of mob programming is with a tactic most have heard about or utilized at some point in their software-related careers: [paired programming](https://www.agilealliance.org/glossary/pairing/). Paired programming, or "pairing", on a problem consists of two individuals sitting and staring at the same screen while they share a single keyboard/mouse. There is an exchange between the two about what and how the code should be written in order to come to a well-formed solution. The "driver", or typer at the keyboard, writes out their solution.

So where does mob programming, or "mobbing", come into the picture? Consider the following situation. During a pairing session between two rather opinionated engineers, neither person will concede on their stance for the best course of action. They require outside consultation from their very busy lead. The lead comes in later and listens to both explain their rationales. While they do, a team member nearby listens in and can't help but interject themselves having just worked previously on the service class in question. What was a two person effort has now bloomed into a four person venture. And of course there is still functionally only one screen and one keyboard/mouse combination.

One person now sits at the computer, "driving", while the other three look over his/her shoulder. They all exchange on how best to move forward then make sure the individual at the computer is understanding what the team agreed upon as it is typed out. I think even if someone has not participated in paired programming previously we can all think of times like this in our professional careers. Mob programming is just the name we have given this situation.

# There are... Guidelines

Mobbing is meant to be an inclusive exercise so that no one individual on the team is always dictating what to do. Guidelines were created therefore to help enforce that mentality. They are as follows:

1. Everyone focused on the goal (if you have to do something else step out)
1. One person coding at a time
1. Person coding switches every N minutes
1. Problem solve as a group
1. Person on the keyboard does not type anything they don't understand

So the whole group is centered around completing a single goal, and since only one individual is driving at a time at the keyboard, this allows for the entire team to participate, share, and collaborate on the group's solution.

# How a Whole Mob can be Agile

Worth highlighting that mob programming first was mentioned in "Chapter 28: Mob Programming and the Transition to XP" of the _Extreme Programming Perspectives_ by Moses Hohman in 2003. However, just because it originated in the Extreme Programming (XP) methodology does not mean it cannot also be applied to other methodologies like scrum, kanban, scrum-ban, lean, etc. and here are two reasons why that is the case:

1. At its core mobbing has only one purpose which is to deliver a single piece of work.
1. A team does not jump into mobbing without a well-defined goal.

Prior to the designated mobbing time, a shared goal is selected for the exercise which may push the team in the right direction and be utilized further on down the line. It can be easily incorporated into the project's agile methodology whether it is scrum, kanban, or XP. It is a way to work on a story, a task, or whatever the team deems as an integral step for everyone to continue going forward. This step will be as complex or simple as necessary because it depends on what the participating group is capable of completing.

# Our Mobs and Recommendations

## Changing Switch Time (N)

One common aspect we have changed in the various sessions we have held is the switch time. We typically start by asking the group what duration everyone feels would be best for the piece of work we are looking to complete.

## Team Capability

We have been using this strategy now for several large stories. And in some cases, we have even combined forces with our sister team to have numbers large enough to start on new epics and make pretty good headway through them. This is actually somewhat preferable because it cuts down the sort of design sessions that are necessary for getting everyone on the same page going into an epic.

## Past Mobbing Goals

For our initial mobbing, the primary objective was to: "Implement First A/B Test". Our A/B Test would be to provide all users with either Experience A or Experience B in the application. With a fifty-fifty split between showing A or B, our team was to implement functionality where each user will be shown educational content in different locations on the page.

<u>Sources</u>:

* https://www.agilealliance.org/glossary/mob-programming/
* https://www.worldcat.org/title/extreme-programming-perspectives/oclc/49663563
---
authors:
- Matt Reed
tags:
- Agile
- Software Development
- Mob Programming
date: 2018-11-12T00:00:00.000Z
title: "Mobs Are Not Always Destructive"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/11/mobbing-goal-rules.png
---

Since early November my team has started utilizing a software development strategy that I had never heard of before:  Mob Programming. I am sure many might envision situations as I did of an angry group of engineers pitching comments and improvements on my code with a multitude of GitHub Pull Requests from their forked repositories. Good news, it was nothing like this pitch-forked scenario. Instead of it being a destructive or distributed activity, my experience with mob programming was quite the opposite.

# The Basis

The easiest place to start understanding the concept of mob programming is with a tactic most have heard about or utilized at some point in their software-related careers: [paired programming](https://en.wikipedia.org/wiki/Pair_programming). Paired programming, or "pairing", on a problem consists of two individuals sitting and staring at the same screen while they share a single keyboard/mouse. There is an exchange between the two about what and how the code should be written in order to come to a well-formed solution. The "driver", or typer at the keyboard, writes out their solution.

So where does mob programming, or "mobbing", come into the picture? Consider the following situation. During a pairing session between two rather opinionated engineers, neither person will concede on their stance for the best course of action. They require outside consultation from their very busy lead. The lead comes in later and listens to both explain their rationales. While they do, a team member nearby listens in and can not help but interject themselves having just worked previously on the service class in question. What was a two person effort has now bloomed into a four person venture. And of course there is still functionally only one screen and one keyboard/mouse combination.

One person now sits at the computer, "driving", while the other three look over his/her shoulder. They all exchange on how best to move forward then make sure the individual at the computer is understanding what the team agreed upon as it is typed out. I think even if someone has not participated in paired programming previously we can all envision times like this in our professional careers. Mob programming is just the name we have given this situation.

# There are... Guidelines

Mobbing is meant to be an inclusive exercise so that no one individual on the team is always dictating what to do. Guidelines were created therefore to help enforce that mentality. They are as follows:

1. Everyone focused on the goal (if you have to do something else step out)
1. One person coding at a time
1. Person coding switches every N minutes
1. Problem solve as a group
1. Person on the keyboard does not type anything they do not understand

The whole group is centered around completing a single goal. Since only one individual is driving at a time at the keyboard, this allows for the entire team to participate, share, and collaborate on the group's solution.

# How a Whole Mob can be Agile

Worth highlighting that mob programming first was mentioned in "[Chapter 28: Mob Programming and the Transition to XP](https://www.worldcat.org/title/extreme-programming-perspectives/oclc/49663563)" of the _Extreme Programming Perspectives_ by Moses Hohman in 2003. However, just because it originated in the Extreme Programming (XP) methodology does not mean it cannot also be applied to other methodologies like scrum, kanban, scrum-ban, lean, etc. and here are two reasons why that is the case:

1. At its core mobbing has only one purpose which is to deliver a single piece of work.
1. A team does not jump into mobbing without a well-defined goal.

Prior to the designated mobbing time, a shared goal is selected for the exercise which may push the team in the right direction and be utilized further on down the line. It can be easily incorporated into the project's agile methodology whether it is scrum, kanban, or XP. It is a way to work on a story, a task, or whatever the team deems as an integral step for everyone to continue going forward. This step will be as complex or simple as necessary because it depends on what the participating group is capable of completing.

# Our Mobs and Recommendations

## Team Capability and Capacity

Devil's advocates will be quick to point out that mobbing directly reduces capacity across any team since all developers are involved in the same piece of work. While this is a valid concern from those concerned about velocity and throughput within a sprint, our most effective mobbing sessions were used for tackling large stories that probably would not have been completed within a sprint by a single developer anyway. These sessions were so successful for us that we even combined forces with our sister team to start in on new feature epics. For multiple teams that share a single backlog, this could be a preferable method of getting everyone on the same page as it cuts down on the number of design sessions that are necessary afterwards. All team members are now familiar enough with this shared codebase and each person has actually been hands-on with it. This means all the developers involved are going to be that much more productive in their work going forward.

For more on this topic, check out this quick Medium article: [Here’s the science behind mob programming.](https://medium.com/comparethemarket/you-asked-me-to-prove-mob-programming-works-heres-the-proof-70eb6a1d0279)

## Balancing Switch Time (N)

One common aspect we have changed in the various sessions we have held is the switch time. We typically start by asking the group what duration everyone feels would be best for the piece of work we are looking to complete. But an outside observer might wonder two things:

1. How does anybody come up with a seemly arbitrary length of time?
1. How does the group arrive at a consensus?

The most straightforward way our team has arrived at a switch time is by considering the amount of work involved. If this is a feature epic or more nebulous piece of work with a loftier goal, our switch time will be set accordingly high. We have also used story points to decide on switch times. 1 and 2 point stories have not been considered for mobbing on our team, so it is a pretty easy 1:1 correlation of X story points to N minutes.

The next factor our team uses for determining switch time is the number of people in the mob. A group of three or four developers will require each developer to drive more frequently than one of seven. Now, because our team tends to get easily distracted in general, we need to combat short attention spans while not changing drivers so frequently that it hinders the integrity of the solution. We want to find the right balance of switch time so we can keep the group motivated and engaged.

Last major piece of criteria is the allotted time. For us it has been anywhere from a half hour to a whole, six-hour day. But why this matters is because, just as the agricultural revolution found, it is important to rotate break times into your product's growth. Either halfway in or after each person has gone through a full rotation, we introduce a dedicated break to give people a breather. (This is separate from the guideline which advises stepping away when one is forced to do something else outside of the mobbing topic.)

Say we are mobbing on a feature epic with eight people for an allotted time of five hours, our switch time will probably sit at around 15 minutes. In comparison, a smaller mobbing session of 45 minutes on a three-point story with four developers have had switch times of only five minutes. So why no formula to determining switch time (N)? Someone could propose one however this is analogous with story pointing which has no perfect formula either.

## Past Mobbing Goals

For our initial mobbing, the primary objective was to: "Implement First A/B Test". Our A/B Test would be to provide all users with either Experience A or Experience B in the application. With a fifty-fifty split between showing A or B, our team was to implement functionality where each user will be shown educational content in different locations on the page.

Subsequent mobbing goals have included implementations of:

* A new Account Management portal with several user-level features planned such as changing preferred/primary email address, which entities the user is associated with, a historical activity viewport, etc.
* Kafka streaming for capturing real-time, event-based analytics.
* Restructured application-wide analytics objects so Business Analysts can more easily query data.

# TL;DR

Our team flourishes by having mob programming as another technique in our wheelhouse for solving large, complex problems. A mobbing session is three or more developers all taking turns on the keyboard for N-time increments. By the end of a mobbing session, each developer shares an understanding of the solutions completed having physically typed through the code and not solely talking through high-level designs. It gives all involved the opportunity to communicate different perspectives/ideas which fosters team inclusivity and garners the ability to learn new coding styles.

Looking for a way to mix up your team's current dynamic? You may just want to start a mob.

## More Information and Sources

Still not sure about mob programming? For an even more in-depth perspective, [New Relic's 6-part blog on mob programming](https://blog.newrelic.com/engineering/mob-programming-introduction/) breaks it down even further. Not sure what story/piece of work to start a team on? DZone's article on [A Mob Programming Exercise for Well-Crafted Design](https://dzone.com/articles/practicing-well-crafted-design) introduces the idea of crafting a new game as a mobbing exercise.

<u>Sources</u>:

* [Mob Programming Wikipedia](https://en.wikipedia.org/wiki/Mob_programming)
* [Mob Programming from Agile Alliance](https://www.agilealliance.org/glossary/mob-programming/)
* [Chapter 28: Mob Programming and the Transition to XP](https://www.worldcat.org/title/extreme-programming-perspectives/oclc/49663563)
* [Here’s the science behind mob programming.](https://medium.com/comparethemarket/you-asked-me-to-prove-mob-programming-works-heres-the-proof-70eb6a1d0279)
* [New Relic's 6-part blog on mob programming](https://blog.newrelic.com/engineering/mob-programming-introduction/)
* [A Mob Programming Exercise for Well-Crafted Design](https://dzone.com/articles/practicing-well-crafted-design)
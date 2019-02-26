---
authors:
- Justin Risch
tags:
- data governance
- best practices
date: 2017-08-17T18:22:50.000Z
title: "Decoupling Data Governance"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/01/decoupling.png
---

## Why your data governance project needs an external driver.

Most Data Governance projects start the same way-- you have data, and some of it is bad. It’s suffering from ROT-- being Redundant, Obsolete, or Trivial. Not only that, but the old “quick fixes” of yesteryear have built up into an undocumented, hard to navigate codebase that’s distributed over the entirety of your data collection framework. Often, there’s as many data cleanup factories as there are data sources, and someone at the company thinks…

# There has to be a better way.

The good news is that there **absolutely is** a better way of managing your information: Data Governance. The bad news is that the first step is acknowledging your company has a problem. It’s a common problem, sure, but many companies often resist the idea that their system-- which is “working” in their mind-- is actually broken.

This can be exceptionally difficult to bring to the forefront of your company’s attention if the culture at your workplace does not foster open, honest communication, or if criticism is taken personally. Sometimes, even mentioning that something is wrong begins a witch hunt to assign blame. Further still, even in an environment that encourages innovation, you might not feel confident enough in the subject to take charge yourself. You tell yourself that you’re not an expert on the subject, or that it’s “not my place” to tell other people how to manage the data. Honestly though…

# It’s everyone’s job to point out bad data.

Imagine you’re sitting at a pristine lake, watching the sun reflect against the water. Fish dance across the surface, causing ripples that run far and wide. Ducks quack in the distance. It’s a beautiful day. Until someone pulls up their truck, full of junk, and begins tossing it in the lake. There are four options:

* You can think to yourself, “it’s not my lake and so it’s not my place to say anything”, and watch this place you enjoy become destroyed over time.
* You can wait for him to finish, then try to clean as much of it out of the lake yourself and properly dispose of it.
* You can confront the man yourself, and risk whatever consequences that may bring your way-- which could be anything from him telling you to mind your own business to violence.
* You can call the police, who can speak with more authority on the matter than yourself, and can absorb the consequences for you.

# Databases are pristine lakes.

They have ecosystems of people that rely on them. What people do inside them ripples far and wide over the entire globe. In a way, they’re even kind of beautiful… and when people are creating bad data, they’re tossing Junk in.

Every company has Redundant data that causes them to keep track of updates in several locations instead of a single source of truth. They have Obsolete data that needs correcting or updating before it can be consumed. They have Trivial data that no one actually needs but is taking up storage capacity and adds unnecessary operations. This Junk causes ROT, which slows down your company on a day to day basis and can cause expensive errors… So the question is:

# What are you going to do about it?

Do you take the first road, and do nothing about it? In all sincerity, this is a viable option-- but usually only if you plan on updating your resumé in the near future. Even if you aren’t to blame for the bad data, it will catch up to your company eventually. As Thomas Redman, a data governance expert, wrote in “Getting in Front on Data”-- *If you think that data quality is unimportant, irrelevant, or too difficult of an issue to tackle, then I recommend leaving that topic to your successor.*

The second option is only slightly better. You can drag the junk back out of the lake, sure. Clean up the data once it gets to you. You’ll have to hire more and more people to clean up the data in real time, causing unnecessary expenses-- whether they’re software engineers creating automated cleanup methods as new bad inputs are found, or even a team of spot-checkers. You’ll have more “firefighting” scenarios where some of this bad data got to a customer, and now you need to make serious repairs to that relationship. This bad data may even end up causing a lawsuit against your company, which could cost significant resources even if you win. Eventually, whether in a single wound or a thousand tiny cuts, your company will suffer if the source of the problem is left unaddressed.

The third option is exclusively for those who are provocateurs; you have my respect and encouragement: Go up to your boss and tell them their company has serious, deep-rooted issues that may lead to the company’s eventual collapse. That sort of bravery should be commended, because it takes serious integrity, and putting the good of the company over your own short-term career goals. It’s high risk, high reward, and I wish you the best. That will, however, put you in a position of overseeing the project. Even if you aren’t doing the technical aspects of it, this will be your initiative in the eyes of your company, and most people would find that they don’t have the excess hours over the week to add an entire new project to it, especially one that will take the cooperation of everyone in your company that creates data, consumes data, or is responsible for the underlying technology which manages and transfers data.

# My recommendation…

Would be to call in someone who has the technical expertise to speak to your IT departments in their own language, but doesn’t yield control of the project to them. These IT departments should not own the Data Governance project-- they don’t create data, and they don’t consume it, and so they don’t get a say in the definitions used, or what the data \*should\* look like. I like to think of it as an item on a restaurant’s menu-- the chef prepares the data the best way they know how, but the consumer of the data should also have a say in what’s given to them. The IT department is effectively a collection of waiters and waitresses-- they take the data and move it from one place to another, preferably without interfering with it in the middle.

You would also need someone with the business acumen to describe to upper management what the problem is. Simply throwing statistics and numbers at them will not do-- you need to describe to them the impact that will have on their business. Telling them that 30% of their data is in some way invalid will likely lead to a response along the lines of, “yeah, everybody’s is, that’s why we clean it up.” Showing them that bad data costs them money will get the project moving.

With the potential repercussions of being a provocateur, it should also be someone insulated from your company’s political landscape. These projects can be slow to begin because many companies make the mistake of assuming that, if there’s a problem, there’s someone to blame. With the case of bad data, the answer is often “everyone”, in some way or another, and so the project is dead on arrival as everyone points their fingers at each other as if filming a parody of an old western standoff. Having an external driver-- someone who was not part of creating the original system and therefore cannot be blamed-- stand up and declare that blame is not as important as solutions, will often be the only way of moving forward.

Additionally, this project should be someone’s full time job. It will require several meetings a week, oversight of technical aspects, as well as a thorough knowledge of and willingness to implement best practices. It cannot be completed as a side project or an afterthought.

These parameters paint the image of a consultant, free from office politics, who’s able to speak plainly, directly, and with integrity on the subject. Someone who’s able to stick their neck out when necessary, but knows which battles to fight, and which are a distraction from the main goal. They would be an external driver whose sole focus is this one goal: Focusing on the big picture to better improve your data quality.


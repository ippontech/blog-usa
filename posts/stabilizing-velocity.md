---
authors:
- Benjamin Scott
categories:
- Agile
date: 2015-04-15T12:07:27.000Z
title: "Stabilizing your velocity"
id: 5a267e57dd54250018d6b5e2
image: 
---

The velocity is the cumulative value, in story points, that a team has accomplished during a sprint. In an experienced team, this velocity will be stable and can easily be used to forecast a release. Take a look at this example chart:

<div class="wp-caption aligncenter" id="attachment_12902" style="width: 610px">![Stable velocity chart](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/04/good_velocity.png)Stable velocity chart

</div>

The velocity here is stable, and we can see the Done line growing steadily. We can assume that the first release will be done on the 8th sprint, and the final release most likely around the 16th sprint.

Now let’s look at another example, this one where the velocity is not stable:

<div class="wp-caption aligncenter" id="attachment_12904" style="width: 610px">[![Unstable velocity chart](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/04/bad_velocity1.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/04/bad_velocity1.png)Unstable velocity chart

</div>

It’s now much harder to estimate correctly. Of course we can take the average velocity and still be able to forecast somewhat accurately, however a velocity that jumps around is an indicator of problems within the team or with the organization and it would be beneficial to root them out.

When a team is at full capacity there isn’t much that we can do to increase the velocity, so we will assume that the major cause of an unstable velocity is drag. Something or several somethings are causing the team to slow down.

## Velocity drags

Here are a few potential reasons for an unstable velocity:

### Technical debt

The most likely internal reason for drag is technical debt. When you are dealing with legacy code, or prototype code they are often untested and poorly written. This makes adding new features unpredictable. Sometimes a new feature can be put in without any consequences. Sometimes you find out that by changing one thing you’ve revealed that two supposedly independent modules are deeply intertwined, and now you will need a couple extra days to refactor it.

### Lack of knowledge

Suppose you need to switch technologies to increase scalability. However, you quickly realize that the task is more complicated than you think because of the time needed to learn the new technology. This isn’t an uncommon scenario for a modern developer, and while this cost is usually factored into the stories or as a research spike, the actual cost may vary greatly as the team lacked the knowledge to estimate it properly.

### Interruptions

It isn’t unusual for a team to be required to shift focus mid sprint because a stakeholder with enough political pull has forced a bug fix into the sprint, or has taken one or more of your developers for a quick task. As these interruptions keep coming, the measured output of the team will slow down.

## What to try

The following techniques, borrowed from eXtreme Programming can help stabilize your velocity:

### Slack

Slack is a concept where you only commit to work that you can accomplish in a sprint minus 1 or 2 days. For slack to work well, it is important to avoid [Parkinson’s Law](http://en.wikipedia.org/wiki/Parkinson%27s_law) (work expands to fill all available time), so slack time should be filled with non critical work that developers will want to do sprint after sprint. This slack time can also be canceled during a sprint but only if the sprint commitment is at risk. Here are some good suggestions to do during slack:

Paying down technical debt. Technical debt slows down production and can even kill a project. By reducing your debt, you will have fewer bugs, a more robust testing harness, better designed code and be able to introduce new features quickly and with confidence.

Research time is another great filler. It is a great tool to encourage professional development. Your developers may find that a new library can improve your product robustness or performance. They may develop a tool that helps automate tedious tasks. Research time, even if unrelated to the project at hand, will start paying off after a few weeks or months.

While at first, slack will reduce your velocity you will find that it will be more stable and might even increase as the technical debt that was slowing down is getting paid.

### Batman

In some organization, it is impossible to have a sprint not be interrupted by emergency work. Maybe your Scrum Master is inexperienced or does not have the authority to stop these requests, or it may even be that you have legitimate reasons (24h bug fix contracts for example). A good solution is to introduce a batman to your team.

I first came across the batman idea via reading [The Art of Agile Development](http://www.jamesshore.com/Agile-Book/ "The Art of Agile Development")  by James Shore and Shane Warden.  The batman is a person that you remove from your sprint’s commitment work and is dedicated to only work on the emergency requests. While waiting for emergency work, the batman should not work on anything that he cannot put aside easily. He could do small refactoring projects, research, improving the build process, writing a few more tests.

You should rotate the batman duties each sprint. You should also look into why your batman is necessary and strive to no longer need him. In order to achieve this, you will have to educate the stakeholders in how to interact with an Agile team, and improve code quality by rigorous adherence to agile development techniques to reduce bugs that slip into production.

## Key things to remember

1. Unstable velocity hints at problems with your process
2. Find the cause of your problem
3. Attempt to resolve them by using Agile techniques
4. Always research ways to improve your process

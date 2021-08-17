---
authors:
- Ben Scott
tags:
date: 2021-08-09T11:00:13.000Z
title: "Git: Dealing with broken branches"
image: 
---

Software Engineers are constantly working with Pull Requests(PR). Most organizations have now adopted them as a best practice to ensure code that is merged upstream passes quality checks and peer reviews. I want to share a couple tips around dealing with PRs when issues arise. 

# Resolving Merge Conflicts

If you're in an organization that prefers to rebase their feature branches prior to merging them upstream, and your PR starts to have many commits than you've probably suffered from rebase hell (when conflicts just seem to go on forever). 

For example, let's say you branched off `develop` with your `feature` branch. Your branches have diverged and develop now has 10 new commits, and your feature branch has 100 commits. You've tried rebasing, but you end up in an endless loop of conflicts.

The first thing you should do is enable [reuse recorded resolution](https://git-scm.com/docs/git-rerere) . This will cause git to apply the same resolution when it finds the same conflict. To enable it by default for all your repos:

```$ git config --global rerere.enabled 1```

This can solve your issue, but if it doesn't you may just want to squash your commits before updating from `develop`. You could use [interactive rebase](https://git-scm.com/book/en/v2/Git-Tools-Rewriting-History) if you prefer to do everything in your terminal, however I have found that the easiest way to squash your branch to keep working on it is to use your git provider's squash feature. 

Here are the steps:

* Find the parent commit of your feature branch
* Create a new feature branch off that commit `feature-squashing`
* Create a PR from your `feature` branch to `feature-squashing`
* Click the merge and squash button

You can now update `feature-squashing` with `develop` without ending up in rebase hell.

# Invalid commits within your branch

The other problem I've run into is when some commits from upstream somehow end up mixed with your feature branch. This typically happens when someone has done a bad merge/rebase. If you catch the problem right away, you can reset your branch to just prior to the merge using [reflog](https://git-scm.com/docs/git-reflog) if you've pushed or by resetting if you haven't: 

```$ git reset --hard origin/feature```

Then try to merge/rebase again. However, if you have commits after the bad ones then you'll need to extract those commits. You could use interactive rebase for this, but I prefer to use [cherry-pick](https://git-scm.com/docs/git-cherry-pick). Let's assume you have 5 commits with a bad one in the middle:  1 2 **3** 4 5. With 3 being the bad one, and 0 the parent commit of the feature branch.
Create a new branch `merge-fix` from the same parent branch. It can be the same parent commit, or a newer commit from that branch if you also want to update with upstream at the same time. The syntax for cherry-picking a range is:

```$ git cherry-pick start..end```

It's important to note that start isn't inclusive and end is. To make things simpler I've decided to use a notation that will change it to both sides being inclusive. The `^` symbol refers to the parent of a commit, thus:

```
$ git checkout 0
$ git checkout -b merge-fix
$ git cherry-pick 1^..2
$ git cherry-pick 4^..5
```

You would then test your branch to make sure you haven't broken anything. You could force push this onto your previous broken branch, or just keep working off this one.

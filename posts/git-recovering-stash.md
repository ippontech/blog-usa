---
authors:
- Benjamin Scott
categories:
- 
date: 2015-06-03T11:00:13.000Z
title: "Git: Recovering a lost stash"
id: 5a267e57dd54250018d6b5eb
image: 
---

While using Git you'll probably end up using stash a lot, and if you do you'll probably end up losing a stash by mistake. Fear not, Git has ways of recovering lost stashes, or commits. For demonstration purposes, let's lose a stash.

First, I'll show some work in progress so that we know what we're recovering.

[![Work in progress](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/06/FileCreated-1024x484.jpg)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/06/FileCreated.jpg)Work in progress

Then let's stash these changes, followed by clearing the stash list. We've effectively lost the stash.

[![Losing the stash: git stash clear](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/06/LostStash-1024x484.jpg)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/06/LostStash.jpg)Losing the stash

Git provides a utility, [fsck](http://git-scm.com/docs/git-fsck) that can find unreachable commits:

[![git fsck --unreachable | grep commit](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/06/fsck-957x1024.jpg)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/06/fsck.jpg)Looking for unreachable commits

Now this is a lot of unreachable commits, we can find our lost stash with brute force by calling `git show` on each of these hashes, or to make our search a bit smarter we can grep for text that we know will be in the commit. For example: “Git is amazing”.

[![git fsck --unreachable | awk '/unreachable commit/ {print $3}' | xargs git show | grep -B 15 "Git is amazing" | grep commit](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/06/grepFsck-1024x176.jpg)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/06/grepFsck.jpg)Filtering results

We've now narrowed the search to just 3 hash, we can now brute force. For simplicity, the hash that I want is the second one 45e4d5e, and I can recover it by applying it:

![git stash apply 45e4d5e](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/06/FoundStash-1024x310.jpg)Recovering the stash

We've now recovered the stash. It's important to remember that this method works for any unreachable commits, this can happen in a variety of ways:

- Losing a stash.
- Deleting a branch.
- Resetting `head` to a previous commit.

You can also do a couple more things with the recovered hash:

- Create a branch: `git branch lost-stash 45e4d5e`
- Cherry-pick to another branch: `git cherry-pick 45e4d5e`

---
authors:
  - John Blat, Lucas Ward
tags:
  - ARM
  - Graviton
  - DevOps
  - Multi-arch
  - ARM64
  - aarch64
  - amd64
  - Ampere Altra
  - Pipeline
  - GitLab
date: 2023-03-17T00:00:00.000Z
title: "Modernize your pipeline to be ARM-ready with multi-arch container builds"
---

With the release of Amazon Web Service's Graviton processors and the pivot of Apple's ecosystem to ARM based processor, multi-arch images are becoming hugely popular. Don't get left behind for no reason, because transforming your workloads to run on multiple architectures is now more obtainable than ever. Converting your pipeline to build multi-arch images is a relatively straightforward and quick process that pays off in a big way. At Ippon Technologies, we utilize docker's buildx plugin to achieve building multi-arch images, and you can too! Follows is a short getting started guide, as well as some discussion as why, as an developer, it is in your best interest to build multi-arch images. 

# Where this work happens
Set up the article by talking about pipelines in general.. A few lines about why pipelines are a thing, and who this article is targeted at.  Discuss production and artifacts and lay out the ground work for understanding.

# Why this work happens
Convince reader to go for multiarch (lens of developer / devops) - talk future proofing, cost savings, cool tech, etc... keep it short, this is not a business pitch but rather, why you as a developer should invest your time in this technology

# What are the steps
Actual steps to do the work here (gitlab pipeline) - briefly touch on this and that between jenkins, gitlab, github, circle ci, etc...

# A peek under the hood
Talk about multi-arch in the pipeline, vs local, vs with jenkins, using buildx, discuss separate runners, get technical here if you want

# Small conclusion and ippon buisness pitch

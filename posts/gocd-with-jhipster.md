---
authors:
- Aaron Throckmorton
categories:
- Docker
- JHipster
- Devops
date: 2018-01-08T19:10:23.000Z
title: "GoCD with JHipster"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/11/Screen-Shot-2017-11-15-at-9.37.07-AM.png
---

A colleague recently introduced me to [GoCD](https://www.gocd.org/) and explained some of its benefits and why they prefer it for Continuous Delivery in their organization. I've used a number of popular CI/CD products and wanted to give it a try. My colleague presented GoCD as a strong contender when seeking Continuous Delivery and Pipelines for development software development. I will present a shallow dive into this automation software while using a [JHipster](http://www.jhipster.tech/) application as my artifact.

# Terminology

GoCD has some typical abstractions for an automation server with extra cool stuff thrown in.

*Resources* - Tagging. For this demonstration, I've tagged my agents with "Java" and "Docker".

*Environments* - Isolation for pipelines and agents. Objects in one environment cannot be used in another.

*Task* - Smallest pipeline unit. A single command to be run, like a shell command.

*Job* - Multiple tasks to be run together.

*Stage* - Collection of jobs that can be run in parallel.

*Pipeline* - Multiple stages that deliver your artifact. The first-class object of GoCD!

*Material* - Something that drives a pipeline. Very powerful and interesting feature of GoCD. Pipelines can be material for other pipelines. Stages can be material for multiple pipelines which offers more options for parallelism.

*Value Stream Map* - The visual representation of the CI/CD workflow. Another powerful feature that allows a top-down view of multiple pipelines running and consuming each other as material.

# Building The Infrastructure

For my test-drive of GoCD, I used Docker, Nexus, and the JHipster stack. Docker is a no-brainer for testing out new software. An artifact repository felt necessary and comfortable for my pipeline so I chose Nexus 3 for its capabilities as both a Java artifact repository and a Docker registry.

Grabbing images from [GoCD's official Docker images](https://github.com/gocd/docker-gocd-server), I quickly got set-up.

## Containers
![Docker Containers](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/11/Screen-Shot-2017-11-10-at-12.30.47-PM.png)

So that these containers could communicate with each other, I added them to the bridge network and used IP address instead of service discovery.

For Nexus set up, I created Docker repositories. One was used as a private registry and another was created to proxy all of the images from Docker Hub.

Maven was used to build the Java artifact and the Docker image with [Spotify's Dockerfile-Maven plugin](https://github.com/spotify/dockerfile-maven).

To add more cool factor to my pipeline, I created one of my agent's with:

`-v /var/run/docker.sock:/var/run/docker.sock`

This shares my host machine Docker socket with the container. After installing the Docker client inside the container, it can use this connection to manage Docker on the host. It was used to stop, destroy, and create JHipster app containers during my testing. I really like using this trick to simulate a Continuous Delivery Pipeline as it made artifact delivery super smooth. This isn't secure so I don't recommend trying this on prod.

## Infra
![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/11/Screen-Shot-2017-11-15-at-9.18.50-AM.png)

# The Pipeline

Mapping out the steps before jumping into the GoCD server:

- Test the Java code
- Create the Java artifact
- Create the Docker image
- Tag the Docker image
- Push the Docker image to Nexus
- Destroy running JHipster container on host machine (if it exists)
- Deploy new JHipster container to host machine

After some tinkering on the GoCD server, I created a JHipster environment, an Ippon pipeline group, and added my pipeline to them. I also added another pipeline for checking the Docker socket functionality.

## My Pipelines
![You may notice that I ran these pipelines quite a lot](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/11/Screen-Shot-2017-11-10-at-1.51.52-PM.png)

Creating a new pipeline requires a name, group, material, initial stage, job, and task. From there it was just plugging in the rest of my tasks.

When I began building the pipeline tasks, I really appreciated the command lookup tool.

## Lookup Commands
![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/11/Screen-Shot-2017-11-14-at-8.59.45-PM.png)

## Completed Tasks

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/11/Screen-Shot-2017-11-14-at-9.11.35-PM.png)

When building out my pipeline I initially had a bunch of failures related to my Docker configuration. Fixing these required many reruns of the job and subsequent failures. While that was frustrating, it was cool seeing the graph that GoCD creates to visualize stage duration and success.

## Stage Graph
![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/11/Screen-Shot-2017-11-14-at-9.21.19-PM.png)

Despite these setbacks, my pipeline was quickly successful and JHipster was delivered happily to his ephemeral home in a Docker container.

# Conclusions

This automation tool seems very powerful and perfect for those seeking a lot of parallelism in their pipelines but I was unable to tap this potential. My tasks were able to fit inside a single job, inside a single stage. UI navigation was a little convoluted, as I had to drill down into the pipeline, stage, and job to edit my tasks.

The console logging was very intermittent and slow compared to other automation servers. This was complicated by what I felt was a lack of visual representation for my pipeline.

## Value Stream Map
![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/11/Screen-Shot-2017-11-14-at-10.08.42-PM.png)

Above is the Value Stream Map for my pipeline while running. This doesn't convey enough information for my tastes. The VSM is great for displaying large, interconnected pipelines and materials but suffers when tasked to represent single pipelines.

I've built many pipelines but few so complex that I felt GoCD would have been my first choice for implementing them. When approaching CI/CD I think it is best to look for the right tool for the job. GoCD is still a strong contender if your job is highly complex and full of opportunities to utilize its first-class support for parallelism.

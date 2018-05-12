## Introduction
In this second part of a two part series, we will be setting up a [Jenkins Shared libraries](https://jenkins.io/doc/book/pipeline/shared-libraries/) to execute our Jenkins jobs. Additionally, we will redo the `seed.groovy` file we did in Part 1 to build a regular Pipeline and and Multibranch Pipeline for two services we plan to create jobs for in Jenkins. 

## Part 2 Goals
1. Setup 2 [JHipster](https://www.jhipster.tech/)  Microservices that will both have a Pipeline and Multibranch Pipeline Job set up in Jenkins
2. Configure `seed.groovy` to create a Pipeline and Multibranch Pipeline Job per service 
3. ...

## Goal 1
We are going to use JHipster to create two microservices. If you have never used JHipster or you need some help, checkout the [JHipster Quick Start](https://www.jhipster.tech/creating-an-app/) to spin up a microservice quickly. For this guide, the specifics do not really matter, but you should use a `<systemLevel>-<serviceLevel>` (e.g. `poc-micro`) naming convention when naming your applications to follow along. 

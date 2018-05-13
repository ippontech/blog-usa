## Introduction
In this second part of a two part series, we will be setting up a [Jenkins Shared libraries](https://jenkins.io/doc/book/pipeline/shared-libraries/) to execute our Jenkins jobs. Additionally, we will redo the `seed.groovy` file we did in Part 1 to build a regular Pipeline and and Multibranch Pipeline for two services we plan to create jobs for in Jenkins. 

## Prerequisites
1. Jenkins set up to use the [Jenkins Job DSL API](https://jenkinsci.github.io/job-dsl-plugin/)
2. A Shared Library set up for Jenkins to use when creating and running jobs
3. A couple [JHipster](https://www.jhipster.tech/) Microservices set up with repository name `<systemLevel>-<serviceLevel>` (e.g. `poc-micro`) naming convention
    * If you have never used JHipster or you need some help, checkout the [JHipster Quick Start](https://www.jhipster.tech/creating-an-app/) to spin up a microservice quickly.

## Part 2 Goals
1. Configure Jenkins to use our Shared Library for executing jobs. The `seedJob` has a stand alone configuration to use our Shared Library.
2. Configure `seed.groovy` to create a Pipeline and Multibranch Pipeline Job per desired services outlined in `pipeline-config.groovy` 
3. ...

## Goal 1
### Configure default Shared Library setup for Jenkins
Since we will be using a Shared library, Jenkins needs to know some default configurations for this Shared Library. 

   1. Navigate to `Jenkins Home` > select `Manage Jenkins` > select `Configure System` > scroll down to `Global Pipeline Libraries` > select `Add`
   2. Enter `jenkins-shared-library` in the `Name` field
   3. Ented `master` in `Default Version`
      * This tells jenkins which branch of our shared library we plan to use by default
   3. Under `Source Code Management`, select `Git` 
       * Enter your Shared Library url in the `Project Repository` field and select `Save`
   ![jenkins shared library configuration](https://raw.githubusercontent.com/kcrane3576/blog-usa/master/images/2018/05/jenkins-shared-library-2.1.png)

## Goal 2
We are going to modify the `seed.groovy` file to build a Pipeline and Multibranch Pipeline for all of our services we plan to oboard. We are going to keep the master branch of the Shared Lbrary alone to ensure it works with Part 1 of this series. We will introduce the changes to the `seed.groovy` job on branch `part2` of the Shared Library.
   **Reminder** Since we will plan on changing the Shared Library, any changes to `seed.groovy` will require a `In-process Script Approval`

1. 

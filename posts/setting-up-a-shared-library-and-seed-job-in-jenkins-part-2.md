---
authors:
- Kyle Crane
categories:
- Jenkins
date: 2018-05-28T20:37:10.000Z
title: "Setting up a shared library and seed job in Jenkins - Part 2"
id: 5b0c680730671b00bfc58dda
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/05/jenkins-1.png
---

# Introduction
In this second part of the series, we will be setting up a [Jenkins Shared library](https://jenkins.io/doc/book/pipeline/shared-libraries/) to execute our Jenkins jobs. As the complexity and number of jobs you maintain grow, the use of Shared Libraries provides the ability to share and reuse code across jobs for CI and CD processes.

In order to set up a practical application with our `seedJob`, we will modify `seed.groovy` to build a Pipeline job (deployment job) and a Multibranch Pipeline job (test job). By the end of this series, you will have a foundation set up to onboard micro services consistently as well as execute certain stages specific to the Jenkins jobs you are running.

## Source Code
The source code is available below
  * Jenkins Shared Library ([`microservice-pipelines`](https://github.com/kcrane3576/microservice-pipelines))
  * [`poc-micro`](https://github.com/kcrane3576/poc-micro) [JHipster](https://www.jhipster.tech/) microservice

# Prerequisites
1. Jenkins running in a Docker container built from `kcrane121/maven-jenkins:blog`.
2. `seedJob` set up from Part 1.

# Part 2 Goals
1. Configure Jenkins to use [`microservice-pipelines`](https://github.com/kcrane3576/microservice-pipelines)
as our Shared Library for executing jobs.
   * As the number of jobs you use in Jenkins grows, the amount of duplicate code between jobs can become overwhelming to maintain while new requirements for all of your jobs are introduced. The Shared Library functionality within Jenkins allows you to consolidate the code you use within one repository to be shared across your jobs.
2. Configure `seed.groovy` to create a Pipeline Job (deployment job) and Multibranch Pipeline Job (test job) per service.
3. Create `jenkinsJob.groovy` to be used by our micro services as the entry point into our Shared Library.
4. Set up a [`Jenkinsfile`](https://jenkins.io/doc/book/pipeline/jenkinsfile/) in `poc-micro` to use `microservice-pipelines` Shared Library.

# Goal 1
## Configure default Shared Library set up for Jenkins
Since we will be using a Shared library, Jenkins needs to be set up to use our Shared Library.

   1. Navigate to `Dashboard` > select `Manage Jenkins` > select `Configure System` > scroll down to `Global Pipeline Libraries` > select `Add`
   2. Enter `microservice-pipelines` in the `Name` field
   3. Enter `master` in `Default Version`
      * This tells jenkins which branch of our Shared Library we plan to use by default.
   4. Under `Source Code Management`, select `Git`
   5. In the `Project Repository` field, enter `https://github.com/kcrane3576/microservice-pipelines` > select `Save`.

![jenkins shared library configuration](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/05/jenkins-shared-library-config-2.png)

# Goal 2
We are going to modify `seed.groovy` to build a Pipeline job and Multibranch Pipeline job for all services we onboard.

## Update `seedJob` to use a `part2` branch we will create in `microservice-pipelines`
1. Navigate to `Dashboard` > select `seedJob` > select `Configure`.
2. Under `Source Code Management`, change the `Branch Specifier` to `*/part2`.

## Updating `microservice-pipelines` to build our deployment and test jobs
We are going to leave the `master` branch of `microservice-pipelines` alone to ensure it works with Part 1 of this series. In order for us to do this, we will introduce the changes to the `seed.groovy` job on branch `part2` of [`microservice-pipelines`](https://github.com/kcrane3576/microservice-pipelines).

### Adding `pipelineJob` and `multibranchPipelineJob` to `seed.groovy`
1. Create a new branch `part2` in `microservice-pipelines`.
2. In the `part2` branch, remove the original code in `seed.groovy` and paste in the below code.
   * For a better understanding of the `pipelineJob` and `multibranchPipelineJob`, make sure to go back and check the [Jenkins Job DSL API](https://jenkinsci.github.io/job-dsl-plugin/#).
```groovy
def createDeploymentJob(jobName, repoUrl) {
    pipelineJob(jobName) {
        definition {
            cpsScm {
                scm {
                    git {
                        remote {
                            url(repoUrl)
                        }
                        branches('master')
                        extensions {
                            cleanBeforeCheckout()
                        }
                    }
                }
                scriptPath("Jenkinsfile")
            }
        }
    }
}

def createTestJob(jobName, repoUrl) {
    multibranchPipelineJob(jobName) {
        branchSources {
            git {
                remote(repoUrl)
                includes('*')
            }
        }
        triggers {
            cron("H/5 * * * *")
        }
    }
}
```

### Add method to execute the building of the `pipelineJob` and `multibranchPipelineJob`
Finally we will need to trigger the building of the specific `poc-micro_deploy` and `poc-micro_test` jobs we are onboarding.
   * Hard code the  `repo` variable with `https://github.com/kcrane3576/`.
   * Set up the `deployName` variable by using the `repo` variable and the `jobName` (`poc-micro`) when creating the `pipelineJob`.
   * Set up the `testName` variable by using the `repo` variable and the `jobName` (`poc-micro`) when creating the `multibranchPipelineJob`.
   * You can see the full contents of `seed.groovy` in the `part2` branch of [`microservice-pipelines`](https://github.com/kcrane3576/microservice-pipelines/tree/part2) on github.
```groovy
def buildPipelineJobs() {
    def repo = "https://github.com/kcrane3576/"
    def repoUrl = repo + jobName + ".git"
    def deployName = jobName + "_deploy"
    def testName = jobName + "_test"

    createDeploymentJob(deployName, repoUrl)
    createTestJob(testName, repoUrl)
}

buildPipelineJobs()
```

# Goal 3
Since we will be setting up all of our stages in a Shared Library, we need to set up a groovy script (`jenkinsJob.groovy`) that the `poc-micro` service points to from its Jenkinsfile.

## Adding `jenkinsJob.groovy` to `microservice-pipelines`
We are going to set up our `jenkinsJob.groovy` file to checkout our micro service code from source control and execute specific maven commands depending on the job that is running.
  1. Check out our microservice repository.
     * Check out the [workflow scm steps](https://jenkins.io/doc/pipeline/steps/workflow-scm-step/) for more information.
  2. Read from the [`environment variables`](https://wiki.jenkins.io/display/JENKINS/Building+a+software+project#Buildingasoftwareproject-JenkinsSetEnvironmentVariables) (`env.JOB_NAME`) in order to obtain the Jenkins job name.
     * `deployName = poc-micro_deploy = env.JOB_NAME`
     * `testName = poc-micro_test = env.JOB_NAME`
     * Check out `Jenkins Set Environment Variables` section at the [`Building a software project`](https://wiki.jenkins.io/display/JENKINS/Building+a+software+project#Buildingasoftwareproject-JenkinsSetEnvironmentVariables) Jenkins wiki for more information on `env.JOB_NAME`.

Create and add the below code to `vars/jenkinsJob.groovy` in the `part2` branch of `microservice-pipelines`.
```groovy
def call(){
    node {
        stage('Checkout') {
            checkout scm
        }

        // Execute different stages depending on the job
        if(env.JOB_NAME.contains("deploy")){
            packageArtifact()
        } else if(env.JOB_NAME.contains("test")) {
            buildAndTest()
        }
    }
}

def packageArtifact(){
    stage("Package artifact") {
        sh "mvn package"
    }
}

def buildAndTest(){
    stage("Backend tests"){
        sh "mvn test"
    }
}
```

# Goal 4
In order for our micro services to execute in Jenkins, we need a `Jenkinsfile` in `poc-micro`.

## Setting up our `Jenkinsfile` in our microservice
We will configure a `Jenkinsfile` in our microservices to point to our `microservice-pipelines` Shared Library.
   * **Note** We are introducing the `version specifier` feature associated with the `@Library` annotation in Shared Libraries here. Within the `@Library` annotation, you will always need to provide the name of your Shared Library (e.g. `@Library("microservice-pipelines)`). However, if you add another `@` sign at the end of the Shared Library name (`microservive-pipelines`), you can tell your `Jenkinsfile` to read specific branches or tags from your Shared Library. In fact, in the code below, that is what we did. We are signaling our `Jenkinsfile` to use the `part2` branch of our Shared Library with `@part2` (e.g. `@Library("microservice-pipelines@part2")`).

1. At the root of your project, update the `Jenkinsfile` with the below code.
```groovy
#!/usr/bin/env groovy

// Configure using microservice-pipelines and using "part2" branch
@Library("microservice-pipelines@part2") _

// Entry point into microservice-pipelines
jenkinsJob.call()

```
## Running the `seedJob`
All of our configuration is set up and our repositories are ready to use. We will now run our `seedJob` to create our `pipelineJob` and `multibranchPipelineJob` based on our `seed.groovy` set up.
1. Navigate to `Dashboard` > select `seedJob` -> select `Build with Parameters` > enter `poc-micro` in `jobName` > select `Build`.
   * **Reminder** Since we changed `seed.groovy`on our `part2` branch of `microservice-pipelines` repository, this script will require an admin approval in Jenkins.
2. Navigate to `Dashboard` > select `Manage Jenkins` > select `In-process Script Approval` > select `Approve`.
3. Navigate to `Dashboard` > select `seedJob` -> select `Build with Parameters` > enter `poc-micro` in `jobName` > select `Build`.
4. Navigate to `Dashboard` > verify `poc-micro_test` and `poc-micro_deploy` jobs were created.
   * You will need to repeat this step for all micro services you plan to onboard.

![jenkins shared library configuration](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/05/jenkins-shared-library-final-poc-micro-2.png)

## Running `poc-micro_test` job
Now you have a `poc-micro_test` job that will run every 5 minutes based on the `cron` we set up, but you can also trigger it manually.

1. Navigate to `Dashboard` > select `poc-micro_test` > select `master` > select `Build Now`
2. Under `Build History`, select the blinking blue circle (red if previous failure) > Observe `mvn test` executing in `Console Output`.

## Running `poc-micro_deploy` job
We can also observe the `poc-micro_deploy` job executing `mvn package`.

1. Navigate to `Dashboard` > select `poc-micro_deploy` > select `Build Now`
2. Under `Build History`, select the blinking blue circle (red if previous failure) > Observe `mvn package` executing in `Console Output`.

# Conclusion
During this series we set up a seed job that was used to create a `pipelineJob` (deploy job) and `multibranchPipelineJob` (test job) when onboarding our `poc-micro` micro service with our `seedJob`. Additionally, we set up our Shared Library to use `jenkinsJob.groovy` to handle the logic that determines which stages are executed depending on the currently running job (`env.JOB_NAME`). By using a combination of a "seed" job and Shared Libraries, you now have a foundation set up to onboard any number of services the same way while maintaining job specific stage execution. This provides you the ability to streamline your CI and CD requirements for microservices.

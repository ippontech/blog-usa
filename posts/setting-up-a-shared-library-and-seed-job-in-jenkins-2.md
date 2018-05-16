
## Introduction
In this second part of the series, we will be setting up a [Jenkins Shared library](https://jenkins.io/doc/book/pipeline/shared-libraries/) to execute our Jenkins jobs. As the complexity and number of jobs you maintain grow, the use of Shared Libraries provides the ability to organize and version control your CI and CD processes. 

In order to get a practical application of our `seedJob`, we will also be including a regular Pipeline job and and Multibranch Pipeline job in `seed.groovy`. By the end of this series, you will have a foundation setup to onboard projects consistently as well as handle job specific stages during the running of your Jenkins jobs. 

### Source Code
The source code is available below
  * Jenkins Shared Library ([`microservice-pipelines`](https://github.com/kcrane3576/microservice-pipelines))
  * [`poc-micro`](https://github.com/kcrane3576/poc-micro) [JHipster](https://www.jhipster.tech/) microservice
  * [`blg-micro`](https://github.com/kcrane3576/poc-micro) [JHipster](https://www.jhipster.tech/) microservice

## Prerequisites
1. Jenkins set up to use the [Jenkins Job DSL API](https://jenkinsci.github.io/job-dsl-plugin/)
2. A [Jenkins Shared Library](https://jenkins.io/doc/book/pipeline/shared-libraries/) set up to use when creating and running jobs

## Part 2 Goals
1. Configure Jenkins to use our Shared Library for executing jobs. 
2. Configure `seed.groovy` to create a Pipeline and Multibranch Pipeline Job per service

## Goal 1
### Configure default Shared Library setup for Jenkins
Since we will be using a Shared library, Jenkins needs to know some default configuration in order to link to the repository. 

   1. Navigate to `Jenkins Home` > select `Manage Jenkins` > select `Configure System` > scroll down to `Global Pipeline Libraries` > select `Add`
   2. Enter `microservice-pipelines` in the `Name` field
   3. Ented `master` in `Default Version`
      * This tells jenkins which branch of our shared library we plan to use by default
   3. Under `Source Code Management`, select `Git` 
       * Enter `https://github.com/kcrane3576/microservice-pipelines` in `Project Repository` field and select `Save`
   ![jenkins shared library configuration](https://raw.githubusercontent.com/kcrane3576/blog-usa/master/images/2018/05/jenkins-shared-library-config-2.png)

## Goal 2
We are going to modify `seed.groovy` to build a Pipeline and Multibranch Pipeline for all services we oboard. 

### Updating `microservice-pipelines` to build our `pipelineJob` and `multibranchPipelineJob`
We are going to leave the `master` branch of `microservice-pipelines` alone to ensure it works with Part 1 of this series. In order for us to do this, we will introduce the changes to the `seed.groovy` job on branch `part2` of the Shared Library.
   * **Reminder** Since we are changing the Shared Library, any changes to `seed.groovy` will require an admin script approval in Jenkins 
1. Create a new branch `part2` in `microservice-pipelines`

#### Adding `pipelineJob` and `multibranchPipelineJob` to `seed.groovy`
1. In the `part2` branch, remove the original code in `seed.groovy` and paste in the below code
   * For a better understanding of the `pipelineJob` and `multibranchPipelineJob`, make sure to go back and check the [Jenkins Job DSL API](https://jenkinsci.github.io/job-dsl-plugin/#)
```groovy
def createPipelineJob(jobName, repoUrl) {
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

def createMultibranchPipelineJob(jobName, repoUrl) {
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

#### Execute the building of the `pipelineJob` and `multibranchPipelineJob` for each service
Finally we will tie it all together and add the call to build the `*_deploy` and `*_test` jobs. This method adds `_deploy` to our `jobName` when creating the `pipelineJob` and adds `_test` to our `jobName` when we create the `multibranchPipelineJob`. 
   * You can see the full conents of `seed.groovy` on [github](https://github.com/kcrane3576/jenkins-shared-library/tree/part2)
```groovy
def buildPipelineJobs() {
    def repo = "https://github.com/kcrane3576/"
    def repoUrl = repo + jobName + ".git"
    def deployName = jobName + "_deploy"
    def testName = jobName + "_test"

    createPipelineJob(deployName, repoUrl)
    createMultibranchPipelineJob(testName, repoUrl)
}

def pipelineConfig = getPipelineConfig()
buildPipelineJobs()
```
#### Update `seedJob` to use `part2` branch
1. Navitate to `Jenkins Home` > select `seedJob` > select `Configure` 
2. Under `Source Code Management`, change the `Branch Specifier` to `*/part2`

#### Configure `jobName` Paramater in `seedJob`
The `seedJob` will need a `jobName` `String Parameter` added to the configuration so our `seed.groovy` file will know what repository it needs to build. 

1. Navitate to `Jenkins Home` > select `seedJob` > select `Configure` 
2. Check `This job is parameterized` > select `Add Parameter` > select `String Parameter`
3. Enter `jobName` in `Name` field
4. Enter `The name of your repo (e.g. poc-micro)` in the `Description` field
![jenkins seed job configuration](https://raw.githubusercontent.com/kcrane3576/blog-usa/master/images/2018/05/jenkins-shared-library-2.4.png)


## Goal 3
In order for our microservices to execute in Jenkins, we need a Jenkinsfile. Since we will be setitng up all of our stages in a Shared Library, we need to set up groovy script our microservices need to point to when Jenkins loads up the service. 

### Adding the `jenkinsJob.groovy` file
We need to have an entry point for the `Jenkinsfile` in our JHipster microservices to access our Shared Library. 
1. Create `vars/jenkinsJob.groovy` in your shared library and add the below code
```groovy
def call(){
    node {
        stage('Checkout') {
            checkout scm
        }
    }
}
```
### Adding a `Jenkinsfile` to our JHipster services
We weill configure a `Jenkinsfile` in our microservices to point to our Shared Library. 
   * **Note** We are introducing a great feature associated with the Shared Library here. The `@Library` annotation provides a lot of flexibility. Within the annotation, you will always need to provide the name of your Shared Library (e.g. `jenkins-shared-library`). However, if you add another `@` sign at the end of the Shared Library name, you can tell your `Jenkinsfile` to read specific branches or tags from your Shared Library. In fact, in the code below, that is what we did with `@part2`.

1. At the root of your JHipster project, add a `Jenkinsfile` with the below code
```groovy
#!/usr/bin/env groovy

// Configure using jenkins-shared-library and using "part2" branch
@Library("jenkins-shared-library@part2") _

// Entry point into jenkins-shared-library
jenkinsJob.call()
```
### Running our `seeJob`, `*_deploy` and `*_test` jobs
1. Navigate to `Jenkins Home` > select `seedJob` -> select `Build Now`
   * The job is going to fail again because we need to approve the changes to the `seed.groovy` file
2. Navigate to `Jenkins Home` > select `Manage Jenkins` > select `In-process Script Approval` > select `Approve`
3. Navigate to `Jenkins Home` > select `seedJob` -> select `Build Now`
   * A `*_deploy` and `*_test` job has been created for your service
      * You will need to repeat this step for all services you plan to onboard
   * We set our `multibranchPipelineJob` `cron` to build every 5 minutes and will do a simple `checkout scm`. 
   * Building one of the `*_deploy` jobs will run `checkout scm` when triggered manually
      ![jenkins successful seed job execution](https://raw.githubusercontent.com/kcrane3576/blog-usa/master/images/2018/05/jenkins-shared-library-2.3.png)

## Goal 4
Now that everything is configured and running as expected, we really want our Stages to do more than just checkout our repositories. We are going to create and use a new Jenkins Docker container that supports maven commands to run `mvn test` and `mvn package`.

### Build a new Docker Container
1. Create a Dockerfile with the below contents
```
FROM jenkins/jenkins:lts  
USER root
RUN apt-get update && apt-get install -y maven
```
2. Build the Docker file and run the container
   * If you need some help, checkout the Docker [build](https://docs.docker.com/engine/reference/commandline/build/) and [run](https://docs.docker.com/engine/reference/commandline/run/) documentation
   ```
   docker build -t maven-jenkins .
   ```
   ```
   docker run -p 8080:8080 -p 50000:50000 maven-jenkins
   ```
**Note:** You will need to set up all of the configuration for Shared Libraries and your `seedJob` again
   * This should not take long and it is good practice.

### Add packaging and testing to `jenkinsJob.groovy`
We want our jobs to do more than check out our code. We are going to add `mvn test` to our `*_test` jobs and `mvn package` to our `*_deploy` jobs.
1. Update `jenkinsJob.groovy` with the below code
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

### Running `*_test` job
Now you have a `*_test` job that will run every 5 minutes based on the `crom` we set up, but you can also trigger it manually.

1. Navigate to `Jenkins Home` > select `*_test` > select `master` > select `Build Now`
2. Under `Build History`, select the blinking blue circle (red if previous failure) > Observe the `mvn test` executing in `Console Output`

### Running `*_deploy` job
We can also observe the `*_deploy` job executing `mvn package`.

1. Navigate to `Jenkins Home` > select `*_deploy` > select `Build Now`
2. Under `Build History`, select the blinking blue circle (red if previous failure) > Observe the `mvn package` executing in `Console Output`

## Conclusion
During this series we set up a seed job that was used to create a `multibranchPipelineJob` and `pipelineJob` for each service we onboarded through our `seedJob`. In order to be able to share configuration accross jobs, we set up `pipeline-config.groovy`(as of right now only the MultibranchPipelineJob is taking advantage). Additionally, we set up our Shared Library to use `jenkinsJob.groovy` to handle the logic that determines which stages are executed depending on that running job. 

My personal goal for this post is to assist you in setting up your own Shared Library and seed jobs to help speed up some of the repeated tasks you encounter during the process of onboarding new services.

## Repo Links
* [`jenkins-shared-library`](https://github.com/kcrane3576/jenkins-shared-library)
* [`poc-micro`](https://github.com/kcrane3576/poc-micro)
* [`blg-micro`](https://github.com/kcrane3576/blg-micro)

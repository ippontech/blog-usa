
## Introduction
In this second part of the series, we will be setting up a [Jenkins Shared library](https://jenkins.io/doc/book/pipeline/shared-libraries/) to execute our Jenkins jobs. As the complexity and number of jobs you maintain grow, the use of Shared Libraries provides the ability to organize and version control your CI and CD processes. 

In order to set up a practical application with our `seedJob`, we will also be including a regular Pipeline job and and Multibranch Pipeline job in `seed.groovy`. By the end of this series, you will have a foundation set up to onboard projects consistently as well as handle job specific stages during the running of your Jenkins jobs. 

### Source Code
The source code is available below
  * Jenkins Shared Library ([`microservice-pipelines`](https://github.com/kcrane3576/microservice-pipelines))
  * [`poc-micro`](https://github.com/kcrane3576/poc-micro) [JHipster](https://www.jhipster.tech/) microservice

## Prerequisites
1. Jenkins set up to use the [Jenkins Job DSL API](https://jenkinsci.github.io/job-dsl-plugin/).
2. A [Jenkins Shared Library](https://jenkins.io/doc/book/pipeline/shared-libraries/) set up to use when creating and running jobs.

## Part 2 Goals
1. Configure Jenkins to use our Shared Library for executing jobs. 
2. Configure `seed.groovy` to create a Pipeline and Multibranch Pipeline Job per service.
3. Create `jenkinsJob.groovy` to be used by our services as the entry point into our Shared Library.
4. Set up a [`Jenkinsfile`](https://jenkins.io/doc/book/pipeline/jenkinsfile/) to link to our Shared Library.

## Goal 1
### Configure default Shared Library setup for Jenkins
Since we will be using a Shared library, Jenkins needs to know some default configuration in order to link to the repository. 

   1. Navigate to `Jenkins Home` > select `Manage Jenkins` > select `Configure System` > scroll down to `Global Pipeline Libraries` > select `Add`
   2. Enter `microservice-pipelines` in the `Name` field
   3. Ented `master` in `Default Version`
      * This tells jenkins which branch of our Shared Library we plan to use by default.
   3. Under `Source Code Management`, select `Git` 
       * Enter `https://github.com/kcrane3576/microservice-pipelines` in `Project Repository` field and select `Save`.
   ![jenkins shared library configuration](https://raw.githubusercontent.com/kcrane3576/blog-usa/master/images/2018/05/jenkins-shared-library-config-2.png)

## Goal 2
We are going to modify `seed.groovy` to build a Pipeline and Multibranch Pipeline for all services we oboard. 

#### Update `seedJob` to use a `part2` branch we will create in `microservice-pipelines`
1. Navitate to `Jenkins Home` > select `seedJob` > select `Configure`.
2. Under `Source Code Management`, change the `Branch Specifier` to `*/part2`.

### Updating `microservice-pipelines` to build our `pipelineJob` and `multibranchPipelineJob`
We are going to leave the `master` branch of `microservice-pipelines` alone to ensure it works with Part 1 of this series. In order for us to do this, we will introduce the changes to the `seed.groovy` job on branch `part2` of the Shared Library.

#### Adding `pipelineJob` and `multibranchPipelineJob` to `seed.groovy`
1. Create a new branch `part2` in `microservice-pipelines`.
2. In the `part2` branch, remove the original code in `seed.groovy` and paste in the below code.
   * For a better understanding of the `pipelineJob` and `multibranchPipelineJob`, make sure to go back and check the [Jenkins Job DSL API](https://jenkinsci.github.io/job-dsl-plugin/#).
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

#### Add method to execute the building of the `pipelineJob` and `multibranchPipelineJob`
Finally we will tie it all together and add the call to build a `_deploy` and `_test` job for the service(`jobName`) we are onboarding. 
   * Set the `repo` (`https://github.com/kcrane3576/`) we will be building from.
   * Set up the `_deploy` job (`deployName`) by using the `repo` and `jobName` when creating the `pipelineJob`.
   * Set up the `_test` job (`testName`) by using the `repo` and `jobName` when creating the `multibranchPipelineJob`.
   * You can see the full contents of `seed.groovy` in the `part2` branch of [`microservice-pipelines`](https://github.com/kcrane3576/microservice-pipelines/tree/part2) on github.
```groovy
def buildPipelineJobs() {
    def repo = "https://github.com/kcrane3576/"
    def repoUrl = repo + jobName + ".git"
    def deployName = jobName + "_deploy"
    def testName = jobName + "_test"

    createPipelineJob(deployName, repoUrl)
    createMultibranchPipelineJob(testName, repoUrl)
}

buildPipelineJobs()
```

## Goal 3
Since we will be setitng up all of our stages in a Shared Library, we need to set up a groovy script (`jenkinsJob.groovy`) our microservices need to point to when Jenkins loads up the service. 

### Adding the `jenkinsJob.groovy` file
We are going to set up our `jenkinsJob.groovy` to checkout our microservice code from source control and execute specific maven commands depending on the job that is running.
  1. Check out our microservice repository.
     * Check out the [workflow scm steps](https://jenkins.io/doc/pipeline/steps/workflow-scm-step/) for more information.
  2. Read from the [`environment variables`](https://wiki.jenkins.io/display/JENKINS/Building+a+software+project#Buildingasoftwareproject-JenkinsSetEnvironmentVariables) (`env.JOB_NAME`) in order to obtain the Jenkins job name (`deployName` or `testName`).
     * Check out `Jenkins Set Environment Variables` section at the [`Building a software project`](https://wiki.jenkins.io/display/JENKINS/Building+a+software+project#Buildingasoftwareproject-JenkinsSetEnvironmentVariables) Jenkins wiki.

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

## Goal 4
In order for our microservices to execute in Jenkins, we need a Jenkinsfile. 
. 
### Setting up our `Jenkinsfile` in our microservice
We weill configure a `Jenkinsfile` in our microservices to point to our Shared Library. 
   * **Note** We are introducing a great feature associated with the Shared Library here. The `@Library` annotation provides a lot of flexibility. Within the annotation, you will always need to provide the name of your Shared Library (e.g. `microservice-pipelines`). However, if you add another `@` sign at the end of the Shared Library name, you can tell your `Jenkinsfile` to read specific branches or tags from your Shared Library. In fact, in the code below, that is what we did. We are signaling our `Jenkinsfile` to use the `part2` branch of our Shared Library with `@part2`.

1. At the root of your project, update the `Jenkinsfile` with the below code.
```groovy
#!/usr/bin/env groovy

// Configure using jenkins-shared-library and using "part2" branch
@Library("microservice-pipelinesy@part2") _

// Entry point into microservice-pipelines
jenkinsJob.call()
```
### Running the `seeJob`
1. Navigate to `Jenkins Home` > select `seedJob` -> select `Build with Parameters` > enter `poc-micro` in `jobName` > select `Build`.
   * **Reminder** Since we changed `seed.groovy`, this script will require an admin approval in Jenkins.
2. Navigate to `Jenkins Home` > select `Manage Jenkins` > select `In-process Script Approval` > select `Approve`.
3. Navigate to `Jenkins Home` > select `seedJob` -> select `Build with Parameters` > enter `poc-micro` in `jobName` > select `Build`.
4. Navigate to `Jenkins Home` > verify `poc-micro_test` and `poc-micro_deploy` jobs were created.
   * You will need to repeat this step for all services you plan to onboard.
![jenkins shared library configuration](https://raw.githubusercontent.com/kcrane3576/blog-usa/master/images/2018/05/jenkins-shared-library-final-poc-micro-2.png)

### Running `poc-micro_test` job
Now you have a `poc-micro_test` job that will run every 5 minutes based on the `cron` we set up, but you can also trigger it manually.

1. Navigate to `Jenkins Home` > select `poc-micro_test` > select `master` > select `Build Now`
2. Under `Build History`, select the blinking blue circle (red if previous failure) > Observe `mvn test` executing in `Console Output`.

### Running `poc-micro_deploy` job
We can also observe the `poc-micro_deploy` job executing `mvn package`.

1. Navigate to `Jenkins Home` > select `poc-micro_deploy` > select `Build Now`
2. Under `Build History`, select the blinking blue circle (red if previous failure) > Observe `mvn package` executing in `Console Output`.

## Conclusion
During this series we set up a seed job that was used to create a `multibranchPipelineJob` and `pipelineJob` when onboarding our `poc-micro` service with our `seedJob`. Additionally, we set up our Shared Library to use `jenkinsJob.groovy` to handle the logic that determines which stages are executed depending on the currently running job. By using a combination of the `seed` job and Shared Libraries, you now have the tools and foundational knowledge to streamline and simplify your CI and CD processes.

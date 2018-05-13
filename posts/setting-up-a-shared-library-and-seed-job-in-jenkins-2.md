* TODO: update seed job to grab service 1 at a time
* TODO: create packaging stage in deploy job
* TODO: create testing of service in test job

## Introduction
In this second part of a two part series, we will be setting up a [Jenkins Shared libraries](https://jenkins.io/doc/book/pipeline/shared-libraries/) to execute our Jenkins jobs. Additionally, we will redo the `seed.groovy` file we did in Part 1 to build a regular Pipeline and and Multibranch Pipeline for two services we plan to create jobs for in Jenkins. 

## Prerequisites
1. Jenkins set up to use the [Jenkins Job DSL API](https://jenkinsci.github.io/job-dsl-plugin/)
2. A Shared Library set up for Jenkins to use when creating and running jobs
3. A couple [JHipster](https://www.jhipster.tech/) Microservices set up with repository name `<systemLevel>-<serviceLevel>` (e.g. `poc-micro`) naming convention and stored in github
    * If you have never used JHipster or you need some help, checkout the [JHipster Quick Start](https://www.jhipster.tech/creating-an-app/) to spin up a microservice quickly.

## Part 2 Goals
1. Configure Jenkins to use our Shared Library for executing jobs. The `seedJob` has a stand alone configuration to use our Shared Library.
2. Configure `seed.groovy` to create a Pipeline and Multibranch Pipeline Job per desired services outlined in `pipeline-config.groovy` 
3. Configure the 2 JHipster microservices to use the `jenkins-shared-library`
4. adjusting functionality

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
We are going to modify the `seed.groovy` file to build a Pipeline and Multibranch Pipeline for all of our services we plan to oboard. 

### Updating `jenkins-shared-library` to build our `pipelineJob` and `multibranchPipelineJob`
We are going to leave the `master` branch of the Shared Lbrary alone to ensure it works with Part 1 of this series. We will introduce the changes to the `seed.groovy` job on branch `part2` of the Shared Library.
   **Reminder** Since we are changing the Shared Library, any changes to `seed.groovy` will require a script approval in Jenkins 
1. Create a new branch `part2` for your Shared Library


#### Adding `pipeline-config.groovy` configuration file
As a way to share configuraitons between jobs, we are going to store these configurations in `pipeline-config.groovy`
1. Create `dsl/pipeline-config.groovy` in your Shared Library with the below code
   * The code is very simple and sets up a `cron` that will be used in `multibranchPipelineJob`
```groovy
pipelineConfig {
    definition {
        scm {
            cron = "H/5 * * * *"
        }
    }
}
```

#### Adding `pipelineJob` and `multibranchPipelineJob` to `seed.groovy`
1. Remove the original code in `seed.groovy` and paste in the below code
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
                        branches('develop')
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
            cron(pipelineConfig.definition.scm.cron)
        }
    }
}
```

#### Reading in `pipeline-config.groovy` from the workspace
The `pipelineJob` and `multibranchPipelineJob` don't actually do anything yet. We need to configure `seed.groovy` to load `pipeline-config.groovy` and build the `pipelineJob` and `multibranchPipelineJob` for each of our services.

1. Add a method to read in our `pipeline-config.groovy` fom the workspace
```groovy
def getPipelineConfig() {
    def slurper = new ConfigSlurper()
    def workspacePath = "${new File(__FILE__).parent}"
    def pipelineConfigPath = workspacePath + "/pipeline-config.groovy"
    def config = slurper.parse(readFileFromWorkspace(pipelineConfigPath))

    return config.pipelineConfig
}
```
#### Execute the building of the `pipelineJob` and `multibranchPipelineJob` for each service
Finally we will tie it all together and build a `*_deploy` and `*_test` job for both of our services. We are going to add `_deploy` to our services name when creating the `pipelineJob` and add `_test` to our service name when we create the `multibranchPipelineJob`

1. Add a method that reads the `services` form `pipeline-config.groovy` and call the method
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
#### Configure `jobName` Paramater in `seedJob`
The `seedJob` will need a `jobName` `String Parameter` added to the configuration so our `seed.groovy` file will know what repository it needs to build

1. Navitate to `Jenkins Home` > select `seedJob` > select `Configure` 
2. Check `This job is parameterized` > select `Add Parameter` > select `String Parameter`
3. Enter `jobName` in `Name` field
4. Enter `The name of your repo (e.g. poc-micro)` in the `Description` field
![jenkins successful seed job execution](https://raw.githubusercontent.com/kcrane3576/blog-usa/master/images/2018/05/jenkins-shared-library-2.2.png)


## Goal 3
Now that Jenkins is ready to onboard jobs for our JHipster microservices, we are ready to set up the JHipster microservices to use the Shared Library. 

### Adding the `jenkinsJob.groovy` file
We need to have an entry point for the `Jenkinsfile` in our JHipster microservices to access our shared library. 
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
We weill configure a `Jenkinsfile` in our services to point to our Shared Library. This is necessary to execute the stages for our `pipelineJob` and `multibranchPipelineJob`.

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
   * A `*_deploy` and `*_test` job has been created for all services listed under `service` in `dsl/pipeline-config.groovy`
   * We set our `multibranchPipelineJob` `cron` to build every 5 minutes and will do a simple `checkout scm`. 
   * Building one of the `*_deploy` jobs will run `checkout scm` when triggered manually
      ![jenkins successful seed job execution](https://raw.githubusercontent.com/kcrane3576/blog-usa/master/images/2018/05/jenkins-shared-library-2.3.png)

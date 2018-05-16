* TODO: add explanation of why the job failed during first run of seedJob
* TODO: reword part2 overview


## Introduction
I have been working at a client the past few months that has adopted a desire for a consistent [JHipster](https://www.jhipster.tech/) microservice architecture. We have more than ten microservices that are supported by multiple teams. Developers support multiple services and we needed a uniform approach to deploy quality code quicly to the cloud. We set up Jenkins to support building, testing and deploying any branch of all services while maintaing releases along side feature development. As Jenkins grew, we decided to maintain our Jenkins related code through source control. 

In order for us to satisfy our requirements, we used a couple of the advanced Jenkins tools
  * [Jenkins Job DSL API](https://jenkinsci.github.io/job-dsl-plugin/)
  * [Jenkins Shared libraries](https://jenkins.io/doc/book/pipeline/shared-libraries/)
  
This is the first of a two part series where the target audience is anyone new to Jenkins as well as those individuals looking to stream line some of the work they need to repeat across services they maintain.

### Source Code
The source code is available below if you just want to follow along
  * Jenkins Shared Library ([`microservice-pipelines`](https://github.com/kcrane3576/microservice-pipelines))

### Series Overview
#### Part 1 
We will set up a Freestyle project (`seedJob`) that will be a `job that creates jobs`. We will use the Jenkins Job DSL API to set up the foundation for supporting onboarding any number and type of job we need for all services we onboard.

#### Part 2
We will extend the functionality of the `seedJob` to use a configuration file `pipeline-config.groovy`. This is done to allow for external configuration that may need to be shared across jobs. We will introduce the use of Shared Libraries. In order to maintain support for both of these posts in the same repository, we will take advantage of the `@Library` annotation. This allows us to configure our JHipster microservices to build from specific branches (or tags) in our Shared Library.

## Part 1 Goals
1. Set up Jenkins Freestyle Job `seedJob` to create and configure other jobs based on the Jenkins Job DSL API.
2. Store `seedJob` configuration in github.
3. Run `seedJob` to create a Freestyle Job. 

## Prerequisites
1. **Docker installed** 
   * If you do not have docker installed yet, proceed to Docker's [Getting Started](https://docs.docker.com/get-started/) guide first.
2. **Maven ready Jenkins running in a Docker container**
    * Pull the Jenkins image: `docker pull kcrane121/maven-jenkins:blog`
        * This image is based on the [Jenkins Official Repository](https://hub.docker.com/_/jenkins/)
    * Start the Jenkins container: `docker run -p 8080:8080 -p 50000:50000 kcrane121/maven-jenkins:blog`
    * Open a browser and navigate to `http://localhost:8080/` and follow the instructions to complete the setup
      **Set up Tips:**
      1. The first time you set this up, you will need to provide a password that was given to you after running `docker run -p 8080:8080 -p 50000:50000 kcrane121/maven-jenkins:blog`.  
         * The generated admin password is located in `/var/jenkins_home/secrets/initialAdminPassword`
      3. Select `Install Suggested Plugins` during the setup process 
3. **Familiarity with [Jenkins Pipeline](https://blog.ippon.tech/continuous-delivery-with-jenkins-pipeline/)**

## Goal 1
Now that the prerequisites are out of the way, the first thing we are going to do is set up a **Freestyle project** `seedJob` in Jenkins. This job will be used to generate all other jobs you want to create within Jenkins. 

### Install the `Job DSL` plugin
Navigate back to your browser at `http://localhost:8080/` and login to Jenkins with the credentials you set up or the default admin ones provided to you during the initial set up. We need to configure Jenkins to use the Jenkins Job DSL API. This provides us the functionality to configure how we want our new jobs built.

  1. Navigate to `Jenkins Home` > `Manage Jenkins` > `Manage Plugins` > select `Available` tab > search for `Job DSL` and install

### Creating the Freestyle Project `seedJob`
We will set up our `seedJob` or `job that creates jobs`. This creation is done the same way any other job is created.

  1. On the left hand side of the page, select `New Item`
  2. In the text box for `Enter an item name`, enter `seedJob` > select the `Freestyle project` > select `OK`
  ![jenkins freestyle project](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/05/jenkins-shared-library-1.1.png)

### Configure `seedJob` to use `microservice-pipelines` repository
Since we are using the `microservice-pipelines` repository to load `seed.groovy`, we need to configure `seedJob` to use this repository.

   1. Navigate to `Jenkins Home` > select `seedJob` > select `Configure` 
   2. Scroll to the `Source Code Management` section > select `Git`
   3. In the `Repository URL` field, enter the [`microservice-pipelines`](https://github.com/kcrane3576/microservice-pipelines) url
       * Leave everything else as the default configuration.
     ![seed-config-repo](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/05/jenkins-shared-library-seed-repo-config.png)

### Configure `seedJob` to use `dsl/seed.groovy` we will store in github
Now that we have configured Jenkins to use our `microservice-pipelines` repository, we need to tell `seedJob` to load (`seed.groovy`) from our `microservice-pipelines` repository. This is necessary for us to start using the Jenkins Job DSL API functionality.
 * **Note:** This is not a requirement. Directly inside of the `seedJob`, you could add a groovy script to do the same thing we are doing in our `microservice-pipelines` repository.

Since we will be using our `microservice-pipelines` repository, we will need to add some additional configuration to the `seedJob` to get this working.

   1. Navigate to `Jenkins Home` > select `seedJob` > select `Configure` 
   2. Scroll to the `Build` section > select `Add Build step` > Select `Process Job DSLs`
   3. Select `Look on Filesystem`
   4. In the `DSL Scripts` input field, enter `dsl/seed.groovy`
       * Leave everything else as the default configuration.
   ![seed-config-script](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/05/jenkins-shared-library-seed-script-config-2.png)

### Configure `seedJob` to use microservice name as the job name
We will give our job the name of the microservice we plan to build (`poc-micro`). In order to do this we will need to add a `String parameter` to the `seedJob` that will be used inside of `seed.groovy`.
   1. Navigate to `Jenkins Home` > select `seedJob` > select `Configure` 
   2. Select `This project is parameterized` > select `Add Parameter` > select `String Parameter`
   3. Enter `jobName` in `Name` field
   4. Enter `The name of your repo (e.g. poc-micro)` in the `Description` field
   ![jenkins seed job configuration](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/05/jenkins-shared-library-2.4.png)


## Goal 2
We are using the [`microservice-pipelines`](https://github.com/kcrane3576/microservice-pipelines) github repository. This repository will be used to store our `seed` code. In Part 2 of this series, we will include our Shared Library code in the repository.

  1. Inside of the `microservice-pipelines` repository, we have created a directory `dsl` with `seed.groovy` inside.
      * [`microservice-pipelines/dsl/seed.groovy`](https://github.com/kcrane3576/microservice-pipelines/blob/master/dsl/seed.groovy)
  2. Below are the contents of `seed.groovy`. 
      * We create a simple Freestyle Job and use the `String Param` named `jobName` from `seedJob` to name our Freestyle job. 
   ```groovy
job(jobName) {
    description("A simple Freestyle Job created from seed.groovy")
}
   ```
   
##  Goal 3
Now that we have our `seedJob` setup to read in `seed.groovy` from our github `microservice-pipelines` repository, we are ready to trigger our `seedJob` to create a Freestyle job with `jobName`.

### Running the `seedJob`
  1. Navigate to `Jenkins Home` > select `seedJob` > select `Build Now` 
  2. Under `Build History`, select the top red circle
  3. This will take you to the `Console Output`
     * The job **failed**
     * Due to [Script Security](https://github.com/jenkinsci/job-dsl-plugin/wiki/Script-Security), this will happen every time you change `seed.groovy`.
     ![run failure](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/05/jenkins-shared-library-seed-run-failure.png)
      
### Approving our `seed.groovy` script
  1. We need to tell Jenkins it is ok to use this script
    * Navivage to `Jenkins Home` > `Manage Jenkins` > `In-process Script Approval`
    * Select `Approve` for the `seed.groovy` script
     ![script approval](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/05/jenkins-shared-library-seed-script-approval.png)
    
### Rerunning the `seedJob`
Now that we have approved `seed.groovy`, we are ready for our `seedJob` to run (and succeed).
  1. Navigate to `Jenkins Home` > select `seedJob` > select `Build Now`
  2. Under `Build History`, select the top blue circle
  3. Inside of `Console Output`, you will see `GeneratedJob{name='freestyle'}`
    * Jenkins has created a new job called `poc-micro` from`seed.groovy`
  ![run success](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/05/jenkins-shared-library-seed-run-success.png)

### Verify creation of and run`poc-micro` job
  1. Navigate to `Jenkins Home` and confirm `poc-micro` job was created
  2. Select `poc-micro` > select `Build Now`
  2. Under `Build History`, select the top blue circle
  3. Inside of `Console Output`, you will see a successful exection of the `poc-micro` job
  ![jenkins created job success](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/05/jenkins-shared-library-seed-poc-micro.png)
    
## Conclusion
In this first part of a two part series, we set up the minimum configuration requirements to use `seed` jobs. Our `seedJob` onboards a very simple Freestyle Job that doesn't do anything, but in Part 2 we will be swapping this out for onboarding a regular Pipeline job and a Multibranch Pipeline job. The use of seed jobs makes onboarding/re-onboarding services quick and easy as requirements change while maintaining job history.

In our next post, we will configure the use of Jenkins Shared Library to execute different maven commands within stages depending on the type of job that is running.
      


* TODO: change image links
* TODO: clarify difference between seed Job in jenkins and the github seed.groovy
* TODO: Highlight Part 1 and Part 2 with link to Part 2
* TODO: Clearly explain what Jenkins Home means
* TODO: Make the github setup first with in goals
* TODO: only need to configure seedJob with repo for grabbing seed.groovy
  * may need toe manage jenkins config for shared library

## Introduction
I have been on a client these past few months that had specific requirements to organize their pipeline in such a way that would be repeatable accross all of their services. During my journey, I stumbled accross [Jenkins Shared libraries](https://jenkins.io/doc/book/pipeline/shared-libraries/). The shared library can be used by all of their services to handle CI and CD. During the discovery process of working with shared libraries, I found that using a `seed` job or `job that creates jobs` to be a great tool to accomodate the client as requirements for all services were amended. 

This is the first of a two part series where I hope to guide you through setting up a simple seed job (Part 1) and using it for real world applications (Part 2). In this series, I hope to target anyone new to Jenkins as well as those individuals looking to stream line some of the work they need to repeat accross services they maintain.

### Series Overview
* **Part 1:** We will read in a `seed.groovy` file from a github repository. We do not need a Shared Library to do this, but we will store our `seedJob` code and Shared Library code in the same repository
* **Part 2:** **TBD...**

## Part 1 Goals
1. Setup Jenkins Freestyle Job `seedJob` to use a Jenkins Shared Library stored in github to create and configure other jobs based on the [Jenkins Job DSL API](https://jenkinsci.github.io/job-dsl-plugin/)
2. Create a simple Jenkins Shared Library repository in github.
3. Run `seedJob` to create a Freestyle Job 

## Prerequisites
1. **Docker installed** 
   * If you do not have docker installed yet, please proceed to Docker's [Getting Started](https://docs.docker.com/get-started/) guide first.
2. **Jenkins Running in a Docker Container**
    * Download the Jenkins image: `docker pull jenkins/jenkins`
    * Start the Jenkins container: `docker run -p 8080:8080 -p 50000:50000 jenkins`
      * Check out the [Jenkins Official Repository](https://hub.docker.com/_/jenkins/) for using the Docker Image 
    * Open a browser and navigate to `http:localhost:8080` and follow the instructions to complete the setup
      * The first time you set this up, you will need to provide a password that was given to you after running `docker run -p 8080:8080 -p 50000:50000 jenkins`. 
         * **Tip:** The generated admin password is located in `/var/jenkins_home/secrets/initialAdminPassword`
3. **Familiarity with Jenkins UI**

## Goal 1
Now that the prerequisites are out of the way, the first thing we are going to do is create a new github repository for our Shared Library. This repository will be used to store our `seed` code. In Part 2 we will include our Shared Library code.

  1. Inside of the `jenkins-shared-library` github repository, create a directory `dsl` with `seed.groovy`
  2. Inside of `seed.groovy`, we are going to create a very simple Freestyle Job that just prints to the `Console` in Jenkins
    * Add, Commit and Push the below changes in `seed.groovy`
   ```groovy
    job('generatedJobFromSeedJob') {
        println("Hello from github seed.groovy")
    }
   ```

## Goal 2
Now, we will setup a **Freestyle Job** `seedJob` in Jenkins. This job will be used to generate all other jobs you want to create within Jenkins. 

### Creating the Freestyle Project `seedJob`
Navigate back to your browser at `http:localhost:8080` and login to Jenkins with the credials you set up or the default admin ones provided for you during the initial setup

  1. On the left hand side of the page, select `New Item`
  2. In the text box for `Enter a item name`, enter `seedJob` > Select the `Freestyle Project` > Select `OK`
  ![jenkins freestyle project](https://raw.githubusercontent.com/kcrane3576/blog-usa/master/images/2018/05/jenkins-shared-library-02.PNG)

### Install the `Job DSL` plugin
We need to configure Jenkins to use the Jenkins Job DSL API. This provides us the functionality to have a job that creates other jobs.

  1. Navigate to `Jenkins Home` > `Manage Jenkins` > `Manage Plugins` > `Available` tab > Search for `Job DSL` and install

### Configuring the `seedJob` to use `dsl/seed.groovy` stored in github
Now that we have configured Jenkins to us the Jenkins Job DSL API, we can create a Jenkins Job to create jobs. We are going to use our `jenkins-shared-library` repository to load in the groovy script(s) we want to use to create other jobs. 
 * **Note:** This is not a requirement. Directly inside of the `seedJob`, you could add a groovy script to do the same thing we are doing in our `jenkins-shared-library`

Since we will be using our `jenkins-shared-library`, we will need to add some additional configuration to this job to get this working.

   1. Navigate to `Jenkins Home` > `seedJob` > `Configure` 
   2. Scroll to the `Build` section > select `Add Build step` > Select `Process Job DSLs`
   3. Select `Look on Filesystem`
   4. In the `DSL Scripts` input field, enter `dsl/seed.groovy`
       * Leave everything else as the default configuration
  
##  Goal 3
Now that we have our `seedJob` setup to read in our `seed.groovy` script from our github `jenkins-shared-library` repository, we are ready to trigger our `seedJob` to create another job.

  1. Navigate to `seedJob` > Select `Build Now` 
    * **WARNING** The job will fail
    * As of right now, due to [Script Security](https://github.com/jenkinsci/job-dsl-plugin/wiki/Script-Security), you will run into this issue every time you change `seed.groovy` unless you take alternative steps.
  2. On the left hand side of the page, under `Build History`, you should see a red circle followed by a number and a Date.
    * Select the red circle 
      * This will take you to the `Console Output` of the job where you will see your failure messaage
      **TODO error image**
  3. We need to tell Jenkins it is ok to use this script
    * Navivage to `Jenkins Home` > `Manage Jenkins` > `In-process Script Approval`
    * Select `Approve` for the `seed.groovy` script
  4. Now we are actually ready for our `seedJob` to build another job
    * Navigate to `Jenkins Home` > `seedJob` > select `Build Now`
  5. After the `seedJob` runs, you should now see a blue circle at the top of `Build History`
     * Select the blue button and at the bottom of the `Console Output`, you should see `GeneratedJob{name='generatedJobFromSeedJob'}`
      * Jenkins has created a new job from your `seed.groovy` file called `generatedJobFromSeedJob`
  6. Navigate to `Jenkins Home` and confirm `generatedJobFromSeedJob` job was created
  7. Finally, we are going to run the new `generatedJobFromSeedJob` and confirm the output in `Console`
    * Select `generatedJobFromSeedJob`
    * Select `Build Now`
    * Once the job finished, select the blue circle and inside of the `Console`, confirm you see `Hello from github seed.groovy`
    
## Conclusion
...
      

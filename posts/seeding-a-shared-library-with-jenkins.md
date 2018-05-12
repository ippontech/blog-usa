
* TODO: change image links
* TODO: clarify difference between seed Job in jenkins and the github seed.groovy
* TODO: Highlight Part 1 and Part 2 with link to Part 2
* TODO: Clearly explain what Jenkins Home means
* TODO: Make the github setup first with in goals
* TODO: only need to configure seedJob with repo for grabbing seed.groovy
  - may need toe manage jenkins config for shared library

## Introduction
I have been on a client these past few months that had specific requirements to organize their pipeline in such a way that would be repeatable accross all of their services. During my journey, I stumbled accross [Jenkins Shared libraries](https://jenkins.io/doc/book/pipeline/shared-libraries/). The shared library can be used by all of their services to handle CI and CD. During the discovery process of working with shared libraries, I found that using a `seed` job or `job that creates jobs` to be a great tool to accomodate the client as requirements were amended. 

This is the first of a two part series where I hope to guide you through setting up a simple seed job (Part 1) and using it for real world applications (Part 2). In this series, I hope to target anyone new to Jenkins as well as those individuals looking to stream line some of the work they need to repeat accross services they maintain.

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
3. **Jenkins UI**
    * **Tip:** If you are lost while following along, search for the word(s) in your browser window

## Goal 1
Now that the prerequisites are out of the way, the first thing we are going to do is create a new github repository for our Shared Library.

  1. Inside of the jenkins-shared-library github repository, create a directory `dsl` with `seed.groovy`. 
      * Commit and push these changes to your shared library in github.
5. Ok, we are finally ready to run our Jenkins seed job.

## Goal 2
We need to do is setup a **Freestyle Job** `seedJob` in Jenkins. This job is used to generate all other jobs you want to create within Jenkins. As we progress to where we are using our Shared Library to onboard new jobs in Jenkins, this `seedJob` will be used to generate a set of jobs we want associated with all services we onboard into Jenkins.

### Creating the Freestyle Project `seedJob`
Navigate back to your browser at `http:localhost:8080` and login to Jenkins with the credials you set up or the default admin ones provided for you during the initial setup

  1. On the left hand side of the page, select `New Item`
  2. In the text box for `Enter a item name`, enter `seedJob` > Select the `Freestyle Project` > Select `OK`
  ![jenkins freestyle project](https://raw.githubusercontent.com/kcrane3576/blog-usa/master/images/2018/05/jenkins-shared-library-02.PNG)

### Install the `Job DSL` plugin
This is necessary to set up the Jenkins for using Shared Libraries

  1. Navigate to `Jenkins Home` > `Manage Jenkins` > `Manage Plugins` > `Available` tab > Search for `Job DSL` and install
  
### Configure default Shared Library setup for Jenkins
Since we will be using a shared library, Jenkins needs to know some default configurations for this Shared Library. 

   1. Navigate to `Jenkins Home` > `Manage Jenkins` > `Configure System` > Scroll down to `Global Pipeline Libraries`
   2. Enter `jenkins-shared-library` in the `Name` field
   3. Under `Source Code Management`, select `Git` and enter your Shared Library url in the `Project Repository` field
       * We are going to use the `master` branch of our Shared Library for now
         * Leave everything else as the default configuration
   * TODO: image here

### Configuring the `seedJob` to use `dsl/seed.groovy` stored in github
  Now that Jenkins knows that we want to use a Shared Library and has default configurations, we can go in and configure our `seedJob` to look for our `seed.groovy` file to build additional jobs. We will need to add some additional configuration to this job to get this working.

   1. Navigate to `Jenkins Home` > `seedJob` > `Configure` 
   2. Scroll to the `Build` section > select `Add Build step` > Select `Process Job DSLs`
   3. In the `DSL Scripts` input field, enter `dsl/seed.groovy`
       * Leave everything else as the default configuration
       
## What have we done so far?
  1. We configured Jenkins to use Shared Libraries
  2. We configured our `seedJob` to use the `seed.groovy` file from our Shared Library stored in github
  3. Now, we are ready to run or `seedJob` to create other jobs
  
##  Goal 3
Now that we have our Jenkins set up to use Shared Libraries, we are ready to run our `seedJob` to create a simple Freestyle Job
  1. Navigate to `seedJob` > Select `Build Now` 
   * Since our `seedJob` is pretty small, the job likely finished pretty quickly
   * On the left hand side of the page, under `Build History`, you should see a red circle followed by a number and a Date.
   * Select the red circle (**WARNING** The job failed)
      * This should take you to the `Console Output` of the job. 
        * **Tip:** 
        * As of right now, due to [Script Security](https://github.com/jenkinsci/job-dsl-plugin/wiki/Script-Security), you will run into this issue every time you change `seed.groovy` unless you take alternative steps.
  2. Navivage to `Jenkins Home` > `Manage Jenkins` > `In-process Script Approval`
     * Select `Approve` for the `seed.groovy` script
  3. Navigate to `Jenkins Home` > `seedJob` > select `Build Now`
  4. After the seedJob runs, you should now see a blue circle at the top of your "Build History" Tab
     * Select the blue button and at the bottom of the `Console Output`, you should see `GeneratedJob{name='seed'}`
     * Navigate to `Jenkins Home` and confirm that you have now creed 
      

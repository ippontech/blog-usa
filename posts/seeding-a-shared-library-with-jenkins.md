
* TODO: change image links
* TODO: clarify difference between seed Job in jenkins and the github seed.groovy
* TODO: Highlight Part 1 and Part 2 with link to Part 2
* TODO: Clearly explain what Jenkins Home means
* TODO: Make the github setup first with in goals

## Introduction
I have been on a client these past few months that had specific requirements to organize their pipeline in such a way that would be repeatable accross all of their services. During my journey, I stumbled accross [Jenkins Shared libraries](https://jenkins.io/doc/book/pipeline/shared-libraries/). The shared library can be used by all of their services to handle CI and CD. During the discovery process of working with shared libraries, I found that using a `seed` job or `job that creates jobs` to be a great tool to accomodate the client as requirements were amended. 

This is the first of a two part series where I hope to guide you through setting up a simple seed job (Part 1) and using it for real world applications (Part 2). In this series, I hope to target anyone new to Jenkins as well as those individuals looking to stream line some of the work they need to repeat accross services they maintain.

## Part 1 Goals
1. Setup Jenkins Freestyle Job `seedJob` to use a Jenkins Shared Library stored in github to create and configure other jobs based on the [Jenkins Job DSL API](https://jenkinsci.github.io/job-dsl-plugin/)
2. Create a simple Jenkins Shared Library repository in github.

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
* TODO: github setup

Now that the are out of the way, the first thing we need to do is setup a **Freestyle Job** `seedJob` in Jenkins. This job is used to generate all other jobs you want to create within Jenkins. As we progress to where we are using our Shared Library to onboard new jobs in Jenkins, this `seedJob` will be used to generate a set of jobs we want associated with all services we onboard into Jenkins.

### Creating the Freestyle Project `seedJob`
Navigate back to your browser at `http:localhost:8080` and login to Jenkins with the credials you set up or the default admin ones provided for you during the initial setup

1. On the left hand side of the page, select `New Item`
2. In the text box for `Enter a item name`, enter `seedJob` > Select the `Freestyle Project` > Select `OK`
![jenkins freestyle project](https://raw.githubusercontent.com/kcrane3576/blog-usa/master/images/2018/05/jenkins-shared-library-02.PNG)

### Configuring the `seedJob` to use the `dsl/seed.groovy` stored in github
We will now set up the `seedJob` to point to your github repository and load up the seed.groovy file we will be using to onboard new jobs. We will need to add some additional configuration to this job to get this working.

1. Install the `Job DSL` plugin
    * This is necessary to tell jenkins where to look for the `seed.groovy` file within our github Shared Library
    * Navigate to `Jenkins Home` > `Manage Jenkins` > `Manage Plugins` > `Available` tab > Search for `Job DSL` and install

2. Now that Jenkins knows that we want to use `Job DSL`, we can go in and configure our `seedJob` to look for our `seed.groovy` file to build additional jobs.
    * Navigate to `Jenkins Home` > `seedJob` > `Configure` 
     * Scroll to the `Build` section > select `Add Build step` > Select `Process Job DSLs`
     * In the `DSL Scripts` input field, enter `dsl/seed.groovy`
       * Leave everything else as the default configuration

1. We need to set up the `seedJob` to link to the github repository and load up the `seed.groovy` file.
  * Navigate to `Jenkins Home` > "Source Code Management" section and provide the github repository you plan to use as a shared library. For now, there is no need to provide Credentials (as long as your repository is public). Also, we will just work off of the "master" branch to get things started. 
![seedJob Source Code Management](https://raw.githubusercontent.com/kcrane3576/blog-usa/master/images/2018/05/jenkins-shared-library-05.PNG)

2. On the left hand side of the page, select `Configure`
4. Ok, now lets make our seedJob create another job when ran. For now we will just do a simple Freestyle job that print's "Hello from github seed.groovy". Inside of your seed job github repository, create a directory "dsl" and inside of that, create a seed.groovy file. Commit and push these changes to your shared library in github.
![github seed job setup](https://raw.githubusercontent.com/kcrane3576/blog-usa/master/images/2018/05/jenkins-shared-library-06.PNG)
5. Ok, we are finally ready to run our Jenkins seed job.
 * `Navigate to seedJob > Select "Build Now"`
 * Since our seedJob is pretty small, the job likely finished pretty quickly. On the left hand side of the page, under "Build History", you should see a red circle followed by #1 and a Date.
 * Select the red circle
 * This should take you to the "Console Output" of the job. The red circle is actually an indicator that the job failed (by default a blue circle is a success)
 * We have 1 last hurdle to get through to get our job working. As of right now, due to [Script Security](https://github.com/jenkinsci/job-dsl-plugin/wiki/Script-Security), you will run into this issue every time you change seed.groovy unless you take alternative steps.
 * Navivage to `Jenkins Home > Manage Jenkins > In-process Script Approval`
 * Select "Approve" for your seed.groovy script
 * Navigate to `Jenkins Home > seedJob > Select "Build Now"`
 * After the seedJob runs, you should now see a blue circle at the top of your "Build History" Tab
 * Select the blue button and at the bottom of the "Console Output", you should see `GeneratedJob{name='seed'}`


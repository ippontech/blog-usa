
* TODO: change image links
* TODO: clarify difference between seed Job in jenkins and the github seed.groovy

I have been on a client these past few months that had specific requirements to organize their pipeline in such a way that would be repeatable accross all of their services. During my journey, I stumbled accross Jenkins shared libraries. The shared library can be used by all of their services to handle CI and CD. During the discovery process of working with shared libraries, I found that using a "seed" job or job that creates jobs to be a great tool to accomodate the client as requirements were amended. 

This is the first of a two part series where I hope to guide you through setting up a simple seed job and using it for real world applications. In this series, I hope to target anyone new to Jenkins as well as those individuals looking to stream line some of the work they need to repeat accross services they maintain.

## Goals
1. Create a shared library in github that uses a **seed** job. The **seed** job will be used to create jobs within jenkins. 
2. 

## Prerequisites
1. **Docker installed.** If you do not have docker installed yet, please proceed to Docker's [Getting Started](https://docs.docker.com/get-started/) guide first.
2. **Jenkins Running in a Docker Container**
  * Run the below Docker commands
    * Download the Jenkins image: `docker pull jenkins/jenkins`
    * Start the Jenkins container: `docker run -p 8080:8080 -p 50000:50000 jenkins`
      * Check out the [Jenkins Official Repository](https://hub.docker.com/_/jenkins/) for using the Docker Image 
    * Open a browser and navigate to `http:localhost:8080` and follow the instructions to complete the setup
      * The first time you set this up, you will need to provide a password that was given to you after running `docker run -p 8080:8080 -p 50000:50000 jenkins`. 
      * **You will need this to finish the installation process.**
      * **Reminder:** You can always find the generated admin password at `/var/jenkins_home/secrets/initialAdminPassword`

## Goal 1
Now that the setup is out of the way, the first thing we need to do is setup a **Freestyle Job** in Jenkins. This job is considered the **seed** Jenkins job. As we progress to where we are using our shared library to onboard new jobs in Jenkins, this **seed** job will be the way Jenkins reaches out to github to obtain the configuration of these jobs.


### Setting up the Freestyle Project Seed Job
Navigate back to your browser at `http:localhost:8080` and login to Jenkins with the credials you set up or the default admin ones provided for you during the initial setup

1. On the left hand side of the page, select `New Item`
2. In the text box for `Enter a item name`, provide the name of your seed job. For simplicity, I am naming mine `seedJob`
3. Select the `Freestyle Project`
4. Select `OK` at the bottom right corner
![jenkins freestyle project](https://raw.githubusercontent.com/kcrane3576/blog-usa/master/images/2018/05/jenkins-shared-library-02.PNG)

### Configuring the Seed Job to use the seed job stored in github
We will not set up the seedJob to point to your github repository to load up the seed job we will be using. We will need to add some additional configuration to this job to get this working.

1. Select your job (seedJob) from under the "Name" column in the Home page of Jenkins
![jenkins home page](https://raw.githubusercontent.com/kcrane3576/blog-usa/master/images/2018/05/jenkins-shared-library-03.PNG)
2. Select "Configure" from the left hand side of the page
![jenkins configure job](https://raw.githubusercontent.com/kcrane3576/blog-usa/master/images/2018/05/jenkins-shared-library-04.PNG)
3. Next, you will need to configure Jenkins to use the "Job DSL" plugin. This is necessary to tell jenkins where to find the seed.groovy file is located in order to exectute our seed job. 
  * Navigate to `Jenkins Home > Manage Jenkins > Manage Plugins > "Available" tab > Search for "Job DSL" and install`
  * Once you install the plugin, you will need to navigate to `Jenkins Home > seedJob > Configure` 
   * Scroll to the `"Build" section > select "Add Build step" > Select "Process Job DSLs"`
   * Provide the path from the root of the project to where we will store our seed.groovy file (dsl/seed.groovy) in "DSL Scripts" 
   * Leave everything else as the defuault configuration
3. Now you need to set up your seedJob to link to github to load in the seed job. Navigate to the "Source Code Management" section and provide the github repository you plan to use as a shared library. For now, there is no need to provide Credentials (as long as your repository is public). Also, we will just work off of the "master" branch to get things started. 
![seedJob Source Code Management](https://raw.githubusercontent.com/kcrane3576/blog-usa/master/images/2018/05/jenkins-shared-library-05.PNG)
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



 You can use [mine](https://github.com/kcrane3576/jenkins-shared-library) if you just want to follow along.

TODO: change image links

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
      * The first time you set this up, you will need to provide a password that was given to you after running `docker run -p 8080:8080 -p 50000:50000 jenkins`. You will need this to finish the installation process.
      ![jenkins password](https://raw.githubusercontent.com/kcrane3576/blog-usa/master/images/2018/05/jenkins-shared-library-01.PNG)

## Goal 1
Now that the setup is out of the way, the first thing we need to do is setup a Freestyle Job in Jenkins. This job is considered the **seed** Jenkins job. As we progress to where we are using our shared library to onboard new jobs in Jenkins, this **seed** job will be the way Jenkins reaches out to github to obtain the configuration of these jobs.

### Setting up the Seed Job


 You can use [mine](https://github.com/kcrane3576/jenkins-shared-library) if you just want to follow along.

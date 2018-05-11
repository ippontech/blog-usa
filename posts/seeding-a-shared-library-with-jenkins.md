
## Goals:
1. Create a shared library in github that uses a **seed** job. The **seed** job will be used to create jobs within jenkins. 
2. 

## Prerequisites:
1. **Docker installed.** If you do not have docker installed yet, please proceed to Docker's [Getting Started](https://docs.docker.com/get-started/) guide first.
2. **Jenkins Running in a Docker Container**
  * Run the below Docker commands
    * Download the Jenkins image: `docker pull jenkins/jenkins`
    * Start the Jenkins container: `docker run -p 8080:8080 -p 50000:50000 jenkins`
      * Check out the [Jenkins Official Repository](https://hub.docker.com/_/jenkins/) for using the Docker Image 
    * Open a browser and navigate to `http:localhost:8080` and follow the instructions to complete the setup
      * The first time you set this up, you will need to provide a password that was given to you after running `docker run -p 8080:8080 -p 50000:50000 jenkins`. You will need this to finish the installation process.
     

## Goal 1:
<span style="font-weight: 400;">

</span>

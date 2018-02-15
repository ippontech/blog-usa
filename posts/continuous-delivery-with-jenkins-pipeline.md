---
authors:
- Aaron Throckmorton
categories:
- Agile
date: 2016-10-14T04:45:33.000Z
title: "Continuous Delivery with Jenkins Pipeline"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/jenkins-pipeline-1-1-1.png
---

**Integration & Delivery**

<span style="font-weight: 400;">Jenkins is the favorite Continuous Integration platform for developers. Continuous Integration, at its core, is the integration of code into a code base. You can think of it as a strategy to mitigate poor organization and headaches. Continuously synchronizing code of developers into a shared resource and encouraging use of this main-line to keep teams on the same page.</span>

Increasingly, Jenkins has seen use as a Continuous Delivery platform as well. Continuous Delivery is the next logical step of CI: automating the frequent deployment of your code into environments. The benefits of CD are great for agile teams and organizations. Delivering your software to pre-production and production environments to ensure that your product can always deploy.

[![CI&&CD](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/CICD.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/CICD.png)

<span style="font-weight: 400;">For this role in delivery and deployment, Jenkins has created a new job type. With the 2.0 release of Jenkins, developers can now leverage </span>[<span style="font-weight: 400;">Pipelines</span>](https://jenkins.io/solutions/pipeline/)<span style="font-weight: 400;">. Pipelines is the de-facto method for implementing multi-step deliveries with Jenkins and it does so with a sleek UI. Before Jenkins 2.0, you could use plugins like </span>[<span style="font-weight: 400;">Build-Flow</span>](https://wiki.jenkins-ci.org/display/JENKINS/Build+Flow+Plugin)<span style="font-weight: 400;"> and </span>[<span style="font-weight: 400;">Build-Pipeline</span>](https://wiki.jenkins-ci.org/display/JENKINS/Build+Pipeline+Plugin)<span style="font-weight: 400;"> to implement similar UI and logic. Now, creating delivery and deploy pipelines is easier than ever.</span>

**Pipeline Scripting**

<span style="font-weight: 400;">Pipelines are defined with a Domain Specific Language, based on Groovy, that can be checked into version control with your source code. All build-steps can be modeled in the </span>[<span style="font-weight: 400;">Jenkinsfile</span>](https://jenkins.io/doc/pipeline/jenkinsfile/)<span style="font-weight: 400;"> and can be as complex or simple as you wish.</span>

==Pipeline Script==
```language-javascript
node {
   stage 'check environment'
   sh "node -v"
   sh "npm -v"
   sh "bower -v"
   sh "gulp -v"

   stage 'checkout'
   checkout scm

   stage 'npm install'
   sh "npm install"

   stage 'clean'
   sh "./mvnw clean"

   stage 'backend tests'
   sh "./mvnw test"

   stage 'frontend tests'
   sh "gulp test"

   stage 'packaging'
   sh "./mvnw package -Pprod -DskipTests"
}
```

<span style="font-weight: 400;">No longer do you need to edit your jobs in the Jenkins web interface. Teams can apply changes to the pipeline by editing the Jenkinsfile and pushing it up for delivery. Complex stages can even be copied and saved for use with other jobs. </span>

<span style="font-weight: 400;">Pipeline scripts can be easily broken down into steps, stages, and nodes. </span>The stages will generate a UI segment in the pipeline and can be made up of multiple steps.

```language-javascript
stage 'backend tests'  //stage - creates segment
sh "./mvnw test"     //step - executed in stage and generates logs
```

<span style="font-weight: 400;">Nodes are steps that can encapsulate stages and steps to schedule them for use on a build executor. Nodes can also be encapsulated by stages. Adding node specific stages is encouraged for splitting up material work. </span>

```language-javascript
stage 'deploy'
node (‘tatami’) {
   sh "cp target/*.war.original /var/lib/tomcat8/webapps/ROOT.war"
}
```

Labeled nodes will only run on executors and agents that have been labeled for it’s use. Utilizing nodes makes it easy to scale up your integration and delivery needs programmatically.

**Stage View**

<span style="font-weight: 400;">Successfully running the Jenkinsfile will create a pipeline stage view</span>

[![Stage View Success](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/Stage-View-Success.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/Stage-View-Success.png)

<span style="font-weight: 400;">Errors in the pipeline are easy to locate at a glance</span>

[![Stage View Failure](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/Stage-View-Failure.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/Stage-View-Failure.png)

The ability to view your pipeline’s progress in the stage view is a powerful feature. Problems in your pipeline can be found immediately and without diving into log files. Runtimes for individual stages is also useful for understanding where your pipeline is slowing down. Pipelines create a persistent record of your builds and the history can also be viewed from the stage view.

I encourage adoption of Pipelines to any developers and organizations that seek easy and logical delivery for their applications. Definitely check out the [Jenkins docs](https://jenkins.io/doc/pipeline/) for more info.

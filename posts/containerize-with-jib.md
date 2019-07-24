---
authors:
- Amine Ouali Alami
tags:
- DevOps
- Jenkins
- Docker
- Nexus
date: 2019-07-24T20:16:10.000Z
title: "Containerize your Spring Boot app with JIB"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/07/container-2539942_1920.jpg
---
Java developers are familiar with building war or jar files using their preferred tools, In a Microservice architecture, they will also need to build docker images. 
Building an image is not always an easy task, a minimum docker knowledge is required, writing a docker file, runing a docker daemon and finally building and publishing an image to a registry.
Making the Docker build process integrate with your app build tool is becoming more obvious, There are several Maven/Gradle docker plugins that emerge like [Spotify docker plugin](https://github.com/spotify/dockerfile-maven) and [fabric8 Docker plugin](https://github.com/fabric8io/docker-maven-plugin).
In this article we are going to focus on Jib, an open-source container image builder developed by Google that uses a new approach,
Jib lets Java build containers using Maven or Gradle without a dockerfile or a docker daemon installed.



# Docker build flow
There is many steps between your project and your container being on a registry

![01](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/07/docker_build_flow.png)

We need a Dockerfile and the docker daemon installed as root which can bring complexity to a CI/DC pipeline.

# JIB build flow
![02](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/07/jib_build_flow.png)

Jib provide a build process that hide all the complexity of a docker build flow

# Add JIB to your application
Jib requires minimal configuration, we just need to add the plugin to the pom.xml and configure the target image :

```xml
<plugin>
  <groupId>com.google.cloud.tools</groupId>
  <artifactId>jib-maven-plugin</artifactId>
  <version>1.3.0</version>
  <configuration>
    <to>
      <image>gcr.io/my-project/image-built-with-jib</image>
    </to>
  </configuration>
</plugin>
```

# Build and Push the image
We can simply build the image with ``` mvn compile jib:build ```,
or add this step to a Jenkins pipeline to automatically build and publish the image, no docker daemon is required on the Jenkins server.

```js
stage('build and push docker image') {
    steps {
    // Using Google JIB plugin                                                                                                         
    sh 'mvn compile com.google.cloud.tools:jib-maven-plugin:1.3.0:build'
    }
}
```
Jib's build approach separates the Java application into multiple layers, so when there are any code changes, only those changes are rebuilt, rather than the entire application. Jib packs the Java application into a container by deducting what it needs from your Maven or Gradle project, which results in a faster build process.

![03](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/07/JibBuildJenkins.png)


# Advanced Configuration
For users who require authenticating with private registries, Jib provides [Docker credential helpers](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin#using-docker-credential-helpers) for AWS Elastic Container Registry and other cloud provider, it also support Nexus private repository.

Jib also provide [advanced configuration](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin#extended-usage) for choosing the image, ports etc ...
### Example
This is example of advanced configuration from the Jib website
1. The image is built from a base of openjdk:alpine (pulled from Docker Hub)
1. Pushed to a local private nexus repository localhost:5000/my-image:built-with-jib, localhost:5000 my-image:tag2, and localhost:5000/my-image:latest
1. Runs by calling java -Xms512m -Xdebug -Xmy:flag=jib-rules -cp app/libs/*:app/resources:app/classes mypackage.MyApp some args
1. Exposes port 1000 for tcp (default), and ports 2000, 2001, 2002, and 2003 for udp
1. Has two labels (key1:value1 and key2:value2)
1. Is built as [OCI format](https://github.com/opencontainers/image-spec)

```xml
<configuration>
  <from> (1)
    <image>openjdk:alpine</image> 
  </from>
  <to> (2)
    <image>localhost:5000/my-image:built-with-jib</image>
    <credHelper>osxkeychain</credHelper>
    <tags>
      <tag>tag2</tag>
      <tag>latest</tag>
    </tags>
  </to>
  <container> 
    <jvmFlags> (3)
      <jvmFlag>-Xms512m</jvmFlag>
      <jvmFlag>-Xdebug</jvmFlag>
      <jvmFlag>-Xmy:flag=jib-rules</jvmFlag>
    </jvmFlags>
    <mainClass>mypackage.MyApp</mainClass>
    <args>
      <arg>some</arg>
      <arg>args</arg>
    </args>
    <ports> (4)
      <port>1000</port>
      <port>2000-2003/udp</port>
    </ports>
    <labels> (5)
      <key1>value1</key1>
      <key2>value2</key2>
    </labels>
    <format>OCI</format> (6)
  </container>
</configuration>
```



# Conclusion
Now that the container image is stored in a registry, we need to create a container cluster to run the container image.
We will see in the next post how to deploy our images to Amazon ECS using Jenkins.




### Sources
* [Build containers faster with Jib](https://www.youtube.com/watch?v=H6gR_Cv4yWI)
* [Google JIB](https://github.com/GoogleContainerTools/jib)
* [JIB Maven Plugin](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin)

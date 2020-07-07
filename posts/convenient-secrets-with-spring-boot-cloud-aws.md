---
authors:
- John Strickler
tags:
- Secrets Management
- Spring Boot
- Spring Cloud
- AWS Parameter Store
- Elastic Container Service
date: 2020-07-07T00:00:00.000Z
title: "Secret Management w/ Spring Cloud and AWS Parameter Store"
image: 2020/07/screen-web-design-developing-codes-1936299.jpg
---

Today's applications are broken into smaller and smaller pieces.  We've been slowly transitioning away from managing our own infrastructure; from using virtual instances to deploying to clusters.  Your environment may have one or more clusters.  Those clusters have many services.  Those services have many tasks.  Each task is an application running in its own container.  And more than likely, that application requires **configuration**.   

I want to share with you a simple way to store your sensitive configuration and to inject that configuration into your application.  This solution is appealing because there's no infrastructure to manage, is very low maintenance, and scales beautifully.  So let's get started.

# Storing your configuration

For a contrived example, we'll store a *GitHub Token* in AWS Parameter Store and make it available to our Spring Boot application that is deployed to ECS Fargate.

## Pre-reqs

What you need to get started, or most likely, what you already have in place:

- A **Spring Boot** application 
- Deployed on **AWS**


## Store your secrets in AWS Parameter Store

Open up AWS Systems Manager then go to Parameter Store under Application Management.

- Click `Create Parameter`.
- In the *name* field, enter `/config/application/github.token`.  
- In the *description* field, enter `GitHub API Token`
- Select `SecureString` from the *type* field.  Accept the default KMS key source.
- In the *text* field, enter `ABC123`.  
- Click `Create Parameter` to save it.

![Create Parameter Screenshot](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/07/aws-param-store-create-parameter.png)

A few things to note, for the *type* field we could of used a String but instead I chose SecureString to keep the value encrypted at rest.  You can do either and it will be transparently decrypted when it is retrieved.  For the `name` field, the prefix `/config/application/` is important but everything else is just made up.  I'll go into why the prefix is important in just a bit.


## Retrieving Secrets

The "secret" sauce of this solution is in the integration.  Now that your configuration is securely stored in AWS, you need a good way to retrieve it.  A poor way to integrate would be to specifiy each property in the ECS task definition using ValueFrom mappings.  That's just more configuration and more to maintain.  Unsurprisingly, there's a great Spring integration that can help us out called **Spring Cloud Starter AWS Parameter Store Config**.  

### Add the Spring Boot starter dependency

In your application's pom.xml, add:

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-aws-parameter-store-config</artifactId>
</dependency>
```

Additionally, if Spring Cloud isn't setup in the project yet, add this to the pom.xml as well:

```xml
<project>
   ...

  <properties>
    ...
    <!-- This is the latest version as of the 7/7/2020 -->
    <spring-cloud.version>Hoxton.SR6</spring-cloud.version> 
  </properties>
   
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-dependencies</artifactId>
        <version>${spring-cloud.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>

</project>
```

### Deploy

That is it.  And now, there is some magic happening as is the case with most Spring integrations.

Deploy your application to AWS and Spring Cloud will access the Parameter Store on AWS.  Properties are retrieved and injected based on the following **prefix** conventions:

- `/config/application/` - applies to all applications 
- `/config/application_dev/` - applies to all applications with an active `dev` profile 
- `/config/my-api/` -  applies to only the `my-api` application (defined by `spring.application.name`)
- `/config/my-api_dev` -  applies to only the `my-api` application with an active `dev` profile  

These conventions provide the flexibility to define global, application-specific, and environment-specific parameters.  An added benefit is that parameters can be added/changed/removed in AWS Parameter Store and will sync on application restart.  There's no middle layers or task definitions to update.

### Access the injected property 

The `github.token` property should now be retrieved at application startup from Parameter Store.  The parameter's full key is `/config/application/github.token`.  The prefix, `/config/application`, is omitted and what is left is our application property key.  The value, `ABC123`, is transparently decrypted and provided to the application at runtime.

# Wrapping Up

It's that easy.  Spring Boot's starter packages tend to do a good job out of the box with minimal to no configuration.  

I hope you found this to be easy to follow and beneficial.  And remember, keep your secrets out of your source code!


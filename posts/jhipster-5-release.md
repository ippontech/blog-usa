---
authors:
    - Kenneth Hegeland
    - Theo Lebrun
    - Raphael Brugier
tags:
    - JHipster
    - Spring-Boot
    - React
    - Angular 
date: 2018-06-23T15:43:55.000Z
title: "Brace yourself, JHipster 5 is out!"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/06/JHipster-5-banner.png
---

JHipster 5 has just been released, and we thought it would be nice to write a post compiling all the new features. Throughout this post, we will be linking to the official docs, which provide some more detailed explanations of how to use these new features.

If you area veteran JHipster developer, then you will benefit greatly in two possible ways. As always with JHipster, if you know the technology stack, then you are able to very quickly set up a project and begin delivering business value. It is not surprising this also occurs when generating a React application. Second is another great JHipster benefit, you can quickly learn things that JHipster provides, that may be new to you. If you are new to JHipster, then you will get to see an _opinionated_ approach to React.

If you are entirely unfamiliar with JHipster, we still have good news! Learning JHipster + the various technologies is a great way to quickly become a strong developer. As this post is meant to be about the latest and greatest in JHipster, we will not dive into basic JHipster use, instead, we will point you towards a post by Bruce: [JHipster Deep Dive](https://blog.ippon.tech/jhipster-deep-dive-part-one/).

With this new release, we think that beginners and experts alike will be excited. We have new technologies being introduced, upgraded versions, and quality of life improvements. Let’s begin with front-end changes.

# Frontend changes

## React support

Frontend technologies move fast, and it’s at times difficult to keep up with what is currently the most popular framework. Largely, in the past, Angular has dominated as one of the most popular front-end frameworks, but recently, popularity has been shifting towards React. As of this writing, React has 97,250 stars on GitHub, and Angular has 36,800 stars - while this isn’t proof, it does show that popularity is changing a bit, so it may become important as a developer to learn both technologies. Personally, I find that JHipster is an amazing learning tool, as well as speeding professional development speed, so whether you are new to React, or a veteran, you will find this feature useful. 

If you fall into the former camp (new to React), well then familiarity with JHipster will greatly decrease the time it takes to learn React. JHipster for Angular already taught us how to structure the application, how API calls are made, authentication is handled, etc. So now with a JHipster React app, we have a perfect guideline for learning. We can now quickly catch up on React, based on our existing knowledge of JHipster and Angular.

That’s it for React, as you can see, the details of the technology are not needed in order to use this new feature of JHipster, generate your applications to fit your needs (Angular or React), and take comfort in knowing that you are in good hands.

You can see [the GitHub](https://github.com/jhipster/generator-jhipster/issues/6044) for more details.


## Angular support

The whole support for Angular has been improved in JHipster 5 with multiple optimizations and rework. The main changes are the focus on Angular 6 and the deletion of popup dialogs for the entity screens.

Here is a detailed list of all the changes related to Angular:
- JHipster is focusing on Angular 6, that means the support for the older AngularJS 1.x has been removed
- As a side effect, the support for Gulp and Bower has been removed as well
- Migration to Webpack 4 (both Angular and React), which gives a nice performance boost
- The admin modules are now lazy loaded, that speeds up the initial load of the application
- The folder structure of entities has been improved, especially for microservices as now they are grouped by services. You can use a new flag `--skip-ui-grouping` to retain the old behavior
- The entity screen is not using popups anymore for creation/edition

[![jhipster5-entity-screen](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/06/jhipster5-entity-screen.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/06/jhipster5-entity-screen.png)


## Misc:

Here the miscellaneous changes:
- Both Angular and React now uses Prettier to format code
- Migration from PhantomJS to Jest as PhantomJS is not developed anymore. The frontend tests are now using the Headless Chromium browser provided by Puppeteer


# Backend changes

## Spring Boot 2

On the backend side, the biggest change is the upgrade of Spring Boot to the latest major version. Spring Boot 2 was released on March 1st and upgrades all the major Spring dependencies, the main one being the newest Spring Framework 5.

The JHipster team has put a lot of effort to upgrade the generated java classes to Spring Boot and the updated dependencies.


Among the new cool features, we can list:

- Java 8 is the baseline and Java 9 is supported
- Spring web MVC is now an optional dependency
- Gradle plugin has been rewritten
- HTTP/2 for Tomcat, Undertow and Jetty
- Security is auto-configured by Spring-Boot auto-configure (unless JHipster overrides it)
- new Actuators endpoints


### Spring Cloud

With the upgrade to Spring boot 2, JHipster also brings the latest Spring Cloud release train support: Finchley

The Spring Cloud projects are the cornerstone of the JHipster microservices architecture. The Finchley release upgrades all the Spring- Cloud projects to the latest and introduces new components. The JHipster team has migrated all the dependencies following this new release train and has fixed the regressions for you.

### Kotlin support

This is still in early stage, but the team always follows the latest trends and has announced a Kotlin support for the backend!

Use `jhipster --blueprint generator-jhipster-kotlin` to get started with KHipster, the new evolution of JHipster.


### What's missing

JHipster being opinionated about the Microservice architecture and the best practices in general, the team has decided - with the feedback of the community - to not include some features.

#### Reactive programming with Spring Web Flux
One of the biggest change introduced by Spring 5 and Spring Boot 2 is the reactive programming support with spring-webflux. Unfortunately, the JHispter team found it to be difficult to integrate with the frontend for limited benefits. They have decided to not use Spring Web Flux for now. Ippon and the JHipster team has run an extensive benchmark of Spring Web Flux and you can read all the details in [this article](https://blog.ippon.tech/spring-5-webflux-performance-tests/).


### Micrometer 
Micrometer is the new metrics library provided by Spring-cloud. It aims to be a facade for all the metrics backend and to replaces the dropwizwards-metrics library. Because Dropwizard is already deeply integrated with JHipster, the team has decided to exclude micrometer from this version until they can provide a better support. You can follow the [issue on GitHub](https://github.com/jhipster/generator-jhipster/issues/7100).

### Spring Cloud Components
Not all the new components from Spring Cloud have been integrated (Spring-cloud-vault, Sleuth) and the team is still debating to use or not the newest Spring Cloud Gateway as the new foundation for the JHipster gateway.

# Miscellaneous changes

## JHipster blueprints

Blueprints will help you customizing JHipster by providing your own templates, that way you don’t have to fork the main generator and maintain your own version. That can be very useful if you want to use a different language for the backend (Kotlin is the best example) or simply provide your own HTML files.

You can take a look at [jhipster-kotlin](https://github.com/jhipster/jhipster-kotlin) blueprint for the best example.


## AWS support
JHipster already had several cloud deployment options, but for this version, the focus was on AWS.

There are now 2 options to deploy on AWS:

- `aws` for Beanstalk- JHipster creates a new Beanstalk environment and generates all the deployment configuration
- `aws-containers` for Fargate - JHipster creates a new Fargate cluster for you on AWS and publishes a docker image to the registry. With this generator, it’s easy to get started on deploying an application on docker cluster.

In order to configure AWS, JHipster needs to have the AWS credentials configured (but you don’t necessarily need the AWS CLI installed). This is described in the official [JHipster AWS](https://www.jhipster.tech/aws/) documentation.

To use the `aws` generator, run the command `jhipster aws` and you will be prompted with the following questions:

[![jhipster5-entity-screen](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/06/jhipster-5-console-aws.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/06/jhipster-5-console-aws.png)


Please note that this generator is still very new and only limited to monolithic applications at the moment.

For more details on the 2 AWS generators see [the official docs](https://www.jhipster.tech/aws/).


## JDL V2

One of the coolest features and something that may be initially overlooked is the JHipster Domain Language (JDL). In the past, this allowed you to write code describing the entities in your application, then you can generate all the Spring, Liquibase, UI code needed to perform basic CRUD operations on these entities. This is clearly a huge time saver and allows you to spend more time working on business logic.

In JHipster 5, the team has taken this even further, and you can now fully describe your application options and generate it based on the JDL. To celebrate this new version, JHipster has open-sourced an online editor to write your JDL document and view UML like graphs of the entities called the [JDL Studio](https://start.jhipster.tech/jdl-studio/)!

In order to generate the application via the import-jdl sub-generator, we simply create out JDL files with extra flags dictating the answers to questions as seen in use by JHipster in the past. Here is a simple sample of a JDL file to create three applications, and some entities for them. To run it, save the jdl file, and run `jhipster import-jdl <jdl-file>`

```groovy
application {
  config {
    baseName myFirstApp,
    databaseType postgresql,
    applicationType monolith,
    packageName com.mycompany.myfirstapp,
  }
  entities * except C, D
}

application {
  config {
    baseName mySecondApp,
    databaseType postgresql,
    applicationType microservice,
    serverPort 8091,
  }
  entities E
}

application {
  config {
    baseName myThirdApp,
    databaseType postgresql,
    applicationType microservice,
    serverPort 8092,
  }
  entities F
}

entity A
entity B
entity C
entity D
entity E
entity F

microservice E with mySecondApp
```

After running `import-jdl` on this file, you will be informed to manually run JHipster on the corresponding folders it creates. Navigate to each directory, and run the `jhpster`. This will create the application as specified in the JDL file, and once completed, commit with git.

See [the JDL documentation](https://www.jhipster.tech/jdl/) for more about JDL.


# Conclusion

With this new version, the JHipster team proves again they follow the cutting edge trends of the industry (React, Spring boot 2, Kotlin). The opinionated generator is still the best option to get started quickly with a new project using the best practices.

To celebrate this new release, the first [JHipster conference](https://blog.ippon.tech/jhipster-conf-2018) was held in Paris last week and the videos will be published soon.

If you are new to JHipster, now is a perfect time to start using it. Join the community, help contribute by following [the guide](https://github.com/jhipster/generator-jhipster/blob/master/CONTRIBUTING.md), 

Ippon is proud to use and support JHipster to offer quick, quality solutions to our clients. If you need help getting started with JHipster or migrate to the new latest version, please [reach out to us](https://blog.ippon.tech/contact/)!
# Title(s)
- Deep Dive into creating JHipster applications
- Comprehensive Look Into Creating Various JHipster Applications
- JHipster Deep Dive (made the tutorial ocean themed?)
- ~~A JHipster Tutorial Series, Part 1~~
- ~~Deep Dive Into JDL Studio/Generating Entities~~
- ~~Generating Entities in JHipster~~

**Motivation: Dive into the many types of JHipster applications and configurations**

**another scrapped paragraph:** In this tutorial series we will explore the many options for generating high-quality Java Spring Boot applications with JHipster, creating entities with the entity generator and [JDL Studio](https://start.jhipster.tech/jdl-studio/), generating Angular components, and deploying these applications to the cloud with Amazon Web Services. This series will serve as a comprehensive compliment to the [official JHipster documentation](https://www.jhipster.tech/) for developing both monolithic and microservice JHipster applications, starting first with monolithic architecture and later recreating an application with microservices.

**initial opening paragraph** Getting started with JHipster can feel daunting. There are many options to choose from and decisions to be made, from generating the project itself with selection of database, frontend framework, testing framework and more, to generating entities, developing within the project, and deploying it somewhere with some configuration. There are several beginner tutorials, and detailed documentation on the official site, but there are few examples highlighting the differences between generator options, entity models with various relationships, and deployment configurations.

**this is cute, but maybe not needed:** It is assumed the reader is familiar with Java, Spring, and Angular (the newer Typescript version, not to be confused with the old AngularJS). Prior experience with Amazon Web Services is also recommended, but not required, for part three of this blog: Deploying JHipster applications to AWS.

**where do I put this? at the very end, or beginning, or right here?** At the time of this writing, the current version of JHipster is v4.14.3 and the next major version, v5.0, is in beta. Version 5 will bring official support for another frontend framework [React](https://reactjs.org/) among many improvements. We will use Angular for these tutorials. A follow-up post will be written for JHipster 5's release and its improved support for Angular, but we will not dive into React. This series is written using a MacBook Pro running High Sierra macOS. Some steps will be different for other operating systems. \nLet's begin!

**Extracted this beginner-friendly stuff into its own blog: a "Learning Enterprise Software Development - with JHipster!"**
I aim to dispel new developers' anxiety when working with large and complex applications, as I too used to be intimidated by the dozens of folders and files, unsure of how everything fits together. The reader should understand software architecture at a high-level (a general understanding, not low-level specifics), knowing that there is a **database** that stores data, a **backend** that interacts with the database to retrieve, store, update and delete data in addition to sending and receiving information to and from a **frontend** that displays information and processes user input. It is assumed the reader has experience with Java ~~and is at least familiar with Spring, Angular, Docker, and Amazon Web Services~~.

# JHipster Deep Dive, Part One
*maybe change title from "Deep Dive"?*

**In a simple way:** JHipster is a godsend.

**In a more professional way:** [JHipster](https://www.jhipster.tech/) is a wonderful and ever-growing Open Source development platform for creating high-quality Java Spring Boot + Angular/React applications, but unfortunately some developers tend to only use the initial project generation feature and neglect entity generation and other tools available for continued development, deployment and maintenance. In this tutorial series we will explore those tools, starting with generating applications with various configurations, to creating complex entities with [JDL Studio](https://start.jhipster.tech/jdl-studio/), generating frontend components, monitoring our applications, and finally deploying both monolithic and microservice applications to the cloud with Amazon Web Services. This series will serve as a compliment to the [official JHipster documentation](https://www.jhipster.tech/).

## Getting Started

Under the hood, JHipster uses a custom [Yeoman](http://yeoman.io/) generator to scaffold a Spring Boot application with choices of frontend framework, database, build tool, authentication type, testing framework and more. There are also several community-created modules that can be included during initial setup or imported later.

> ["Why should organizations use JHipster?" by Mallik Sambaraju ](https://blog.ippon.tech/use-of-jhipster-in-organizations/)

There are a few ways to create a JHipster application: with an online tool, with a locally-installed command line interface (CLI), with the CLI inside a [Docker](https://www.docker.com/) container, and inside a [Vagrant](https://www.vagrantup.com/) virtual development environment. The most popular option is local installation with Yarn.

[JHipster Online](https://start.jhipster.tech/) is a Web tool to generate one without the need to install JHipster on your computer; Java and Yarn must still be installed on your computer to run the application unless Docker or Vagrant are used. By filling out a Web form which closely resembles the options given in the command line interface (as we will see later), an application will be generated and can be downloaded in a zip file or committed to a GitHub repository that you link.

Next we will look at the various options in JHipster's application generator. Note that unless development is done inside a Docker or Vagrant container (not highly recommended), you will need to have installed [Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html), [Git](https://git-scm.com/downloads), [Node.js](https://nodejs.org/en/download/), [Yarn](https://yarnpkg.com/en/) (or [NPM](https://www.npmjs.com/get-npm)) and [Yeoman](http://yeoman.io/) installed, in addition to the database you will choose. Optionally, install [Docker](https://docs.docker.com/install/) for running the database inside of a container instead of on your operating system; I prefer managing my databases inside containers.

## Generator Options
There are several choices to make when generating a new application. Using the Web form or typing the command `yo jhipster` or now simply `jhipster`, we are presented with many options, from type of application, name, type of database for production and local development, build tools, frontend frameworks, testing frameworks, and more! Let's look into each option:

**drawing a (better!) flow chart for these options and replacing this bulleted list**
- Registry: yes/no
    - If No, type of authentication: JWT, OAuth 2.0/OIDC w/ Keycloak + Okta, HTTP Session Authentication
- Production database: SQL (H2, MySQL, MariaDB, PostgreSQL, Oracle, MSSQL), MongoDB, Cassandra, [BETA] Couchbase
    - If SQL, development database: H2 w/ disk-based, H2 w/ in-memory, MySQL
- Spring cache abstraction: Yes w/ Ehcache (local cache single node), Yes w/ Hazelcast (distributed cache multiple nodes), [BETA] Yes, with the Infinispan (hybrid cache, for multiple nodes), No (disables Hibernate L2 cache if using SQL)
    - Hibernate 2nd level cache: yes/no
- Building backend: Maven, Gradle
- Framework: Angular 5
- LibSass: yes/no
- i18n: yes/no
- Testing frameworks: Gatling, Cucumber, Protractor

Find out more about each generator option [here](https://www.jhipster.tech/creating-an-app/#2).

**TODO: pull down the beta for version 5 to check for additions or deletions, or simply revisit once released**

#### Databases for local development
The database option with the least hassle for local development would be H2 when using SQL for production. If MongoDB is chosen for production, H2 is not an option and a local Mongo database must be running. Docker makes this easy; simply run `docker-compose -f src/main/docker/mongodb.yml up -d` in the project's root directory. Without Docker you must install Mongo and start an instance with the `mongod` command. [Find instructions for installing Mongo here](https://docs.mongodb.com/manual/installation/#tutorial-installation). Type `mongo` to verify you can connect to the instance (`ctrl+c` to exit mongo interface).

> Unsure of which type of database to use for your own project? [Check out this article written by Jeannine Stark](https://blog.ippon.tech/use-cassandra-mongodb-hbase-accumulo-mysql/).

A SQL database can be chosen for local development as well, instead of using H2. The command to start a Docker container for a SQL server is similar to that of Mongo: `docker-compose -f src/main/docker/mysql.yml up -d`. Without Docker you must install MySQL (or your chosen SQL database) on your machine and set up proper configurations to connect with the JHipster application.

#### Testing Frameworks
*Write a little blurb about each one here*
- junit
- karma
- gatling
- protractor

#### Modules

*Forgot about modules! write this section and add this somewhere* [Creating a JHipster module by Theo Lebrun](https://blog.ippon.tech/how-to-efficiently-use-jhipster-in-your-company-2/)

## Finishing Up
Once you've finished picking your options, either in the command line interface or Web form, the scaffolded application will be generated. If the online Web form was used, simply extract the zip file to your chosen directory or pull the code from the linked GitHub repository. Run `yarn install` (or `npm install` if npm was chosen) to install dependencies; once completed, execute `./mvnw` to build and run the backend project with Maven, and in another terminal window in the same folder execute `yarn start` (or `npm start`) to run the frontend with auto-reload.

> [Learn how to configure your IDE for JHipster here.](https://www.jhipster.tech/configuring-ide/)

In the next post we will dive deep into JHipster's default tools and generating several entities with relationships of various complexities.

---

# Blog #2 - JHipster Entity Generator Deep Dive
- Entity generation, using both JDL Studio and CLI.
- Spring MVC REST Controller generation `spring-controller`, much simpler than `entity sub-generator` that creates full CRUD entities. https://www.jhipster.tech/creating-a-spring-controller/
- Spring Service bean generator `spring-service`. https://www.jhipster.tech/creating-a-spring-service/ mention each of the tips on this page, and maybe find new tips?
- Managing relationships https://www.jhipster.tech/managing-relationships/
- Angular generation! https://www.jhipster.tech/using-angular/

## Using SwaggerAPI/other default tooling
*maybe move this to part 2*
- navigate to swagger page, explore where this information comes from

Login and navigate to /swagger/api to [view asdjklaskljdaklsdaslkjdaslkjd]. The [asdasda is generated from the Spring controllers].

- Another article to mention - API Management with JHipster https://blog.ippon.tech/api-management-with-jhipster/

## Entities
We will explore many types of entities and their relationships.

This set of entities is designed for a website with a store for sell merchandise:
*example here, with customer, order, item_order, item, etc.*

*add to part 3 deployment and monitoring* [Monitoring application with third-party tool DataDog by Alexis Seigneurin](https://blog.ippon.tech/monitoring-a-jhipster-app-with-datadog/)


- Monolithic vs microservice (app + gateway)
- Microservice high level: https://blog.ippon.tech/jhipster-3-0-introducing-microservices/
- adding SSL? (later)

---

# ~~Blog #3 - Development~~
*Probably scrap and create a separate blog post for integrating w/ 3rd party library*
- New pages (w/ & w/o ng-generate?)
- Integrate third-party library such as a CMS

---

# Blog #4 - Deployment
- Deploying to AWS w/ various configurations (solo EC2 instance (frowned upon), w/ multi-EC2, mirrored databases, etc.)
- Deploying monolithic. Deploying microservices later.
- Use CI! Check out this article by our own Richard Yhip: https://blog.ippon.tech/continuous-integration-with-jhipster/

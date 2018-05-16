# JHipster Deep Dive, Part One

[JHipster](https://www.jhipster.tech/) is a wonderful and ever-growing Open Source development platform for creating high-quality Java Spring Boot + Angular/React applications, but unfortunately some developers tend to only use the initial project generation feature and neglect entity generation and other tools available for continued development, deployment and maintenance. In this tutorial series we will explore those tools, starting with generating applications with various configurations, to creating complex entities with [JDL Studio](https://start.jhipster.tech/jdl-studio/), generating frontend components, monitoring our applications, and finally deploying both monolithic and microservice applications to the cloud with Amazon Web Services. This series will serve as a compliment to the [official JHipster documentation](https://www.jhipster.tech/).

## Getting Started

First, we need to generate an application before we can get our hands dirty creating entities and exploring what more JHipster has to offer. Under the hood, JHipster uses a custom [Yeoman](http://yeoman.io/) generator to scaffold a Spring Boot application with choices of frontend framework, database, build tool, authentication type, testing framework and more. There are also several community-created modules that can be included during initial setup or imported later.

> ["Why should organizations use JHipster?" by Mallik Sambaraju ](https://blog.ippon.tech/use-of-jhipster-in-organizations/)

There are a few ways to create a JHipster application: with an online tool, with a locally-installed command line interface (CLI), with the CLI inside a [Docker](https://www.docker.com/) container, and inside a [Vagrant](https://www.vagrantup.com/) virtual development environment. The most popular option is local installation with Yarn.

[JHipster Online](https://start.jhipster.tech/) is a Web tool to generate one without the need to install JHipster on your computer; Java and Yarn must still be installed on your computer to run the application unless Docker or Vagrant are used. By filling out a Web form which closely resembles the options given in the command line interface (as we will see later), an application will be generated and can be downloaded in a zip file or committed to a GitHub repository that you link.

Next we will look at the various options in JHipster's application generator. Note that unless development is done inside a Docker or Vagrant container (not highly recommended), you will need to have installed [Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html), [Git](https://git-scm.com/downloads), [Node.js](https://nodejs.org/en/download/), [Yarn](https://yarnpkg.com/en/) (or [NPM](https://www.npmjs.com/get-npm)) and [Yeoman](http://yeoman.io/) installed, in addition to the database you will choose. Optionally, install [Docker](https://docs.docker.com/install/) for running the database inside of a container instead of on your operating system (I personally prefer managing my databases inside containers).

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

Other technologies such as social login (with Google, Facebook, Twitter), Elasticsearch, Spring Websocket, Swagger-codegen, and Apache Kafka are also listed for integration. Find out more about each generator option [here](https://www.jhipster.tech/creating-an-app/#2).

#### Databases for local development
The database option with the least hassle for local development would be H2 when using SQL for production. If MongoDB is chosen for production, H2 is not an option and a local Mongo database must be running. Docker makes this easy; simply run `docker-compose -f src/main/docker/mongodb.yml up -d` in the project's root directory. Without Docker you must install Mongo and start an instance with the `mongod` command. Find instructions for installing Mongo [here](https://docs.mongodb.com/manual/installation/#tutorial-installation). Type `mongo` to verify you can connect to the instance (`ctrl+c` to exit mongo interface).

> Unsure of which type of database to use for your own project? [Check out this article written by Jeannine Stark](https://blog.ippon.tech/use-cassandra-mongodb-hbase-accumulo-mysql/).

A SQL database can be chosen for local development as well, instead of using H2. The command to start a Docker container for a SQL server is similar to that of Mongo: `docker-compose -f src/main/docker/mysql.yml up -d`. Without Docker you must install MySQL (or your chosen SQL database) on your machine and set up proper configurations to connect with the JHipster application.

#### Testing Frameworks
*Forgot about this section! Write a little blurb about each one here*
- junit & karma (not choices)
- gatling
- cucumber
- protractor

#### Modules

Modules are other generators and plugins made by the community that offer integrations with third-party services or provide some other functionality to JHipster applications. A few of the popular modules include adding the [Ionic framework](https://ionicframework.com/), [PrimeNG user interface](https://www.primefaces.org/primeng/#/), and [React](https://reactjs.org/), which will be officially supported in the main JHipster generator in version 5.

For a complete list of modules visit [the marketplace](https://www.jhipster.tech/modules/marketplace/#/list). You can also create and contribute your own module! Find the instructions for [creating a module here](https://www.jhipster.tech/modules/creating-a-module/) and check out [this article by Théo Lebrun](https://blog.ippon.tech/how-to-efficiently-use-jhipster-in-your-company-2/) where he explains how to create a module to customize JHipster to fit your needs.

## Finishing Up
Once you've finished picking your options, either in the command line interface or Web form, the scaffolded application will be generated. If the online Web form was used, simply extract the zip file to your chosen directory or pull the code from the linked GitHub repository. Run `yarn install` (or `npm install` if npm was chosen) to install dependencies; once completed, execute `./mvnw` to build and run the backend project with Maven, and in another terminal window in the same folder execute `yarn start` (or `npm start`) to run the frontend with auto-reload.

> [Learn how to configure your IDE for JHipster here!](https://www.jhipster.tech/configuring-ide/)

In the next post we will dive deep into JHipster's default tools and generating several entities with relationships of various complexities.

---

*At the time of this writing, the current version of JHipster is v4.14.4 and the next major version, v5.0, is in beta. Version 5 will bring official support for another frontend framework [React](https://reactjs.org/) among many improvements. A follow-up post will be written for JHipster 5's release. This series is written using a MacBook Pro running High Sierra macOS. Some steps will be different for other operating systems.*

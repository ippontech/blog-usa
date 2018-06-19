---
authors:
- Raphael Brugier
categories:
- Docker
date: 2016-06-27T15:15:20.000Z
title: "Using Docker to simplify Cassandra development in JHipster"
id: 5a267e57dd54250018d6b611
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/using-docker-1-2.jpg
---

[![JHipster](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/06/logo-jhipster2x-1.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/06/logo-jhipster2x-1.png)JHipster is an open source project that generates a fully working application in seconds. With a minimal configuration, JHipster accelerates the start of new projects by integrating frontend, backend, security and a database.

Cassandra is one of the supported databases and JHipster generates all the configuration needed to access the cluster.

But it is often hard for the developers to configure and maintain a local Cassandra cluster.

Moreover, there is no standard tooling to manage the schema migrations, like Liquibase or Flyway for SQL databases, making it difficult to synchronize the schema between every environment and a local configuration.

JHipster’s goal is to provide the most simple and productive development environment out of the box for the developers, and this tool has been added in [the latest (3.4.0)](https://jhipster.github.io/2016/05/27/jhipster-release-3.4.0.html) version.

In this post, I’ll describe the design of the tool and the basic commands to use it.

## Design

A Docker Compose(1) configuration is generated to start a Cassandra cluster locally with one command. A set of bash scripts automatically save the executed migrations in a dedicated table. This allows you to automatically(2) execute only the new migration scripts when deploying a new version of the application.
 The tool can be used both by the developers to synchronize their local cluster and in production to keep track of the schema migrations.

The standardization of the migration scripts is also used by the integration tests to start an in-memory Cassandra cluster.

(1) JHipster uses the v2 of the Docker Compose file format and Docker 1.10+ and Compose 1.6+ are required.

(2) Because the migration scripts are read from a Docker volume, the sources must be located in the use directory.

## Basic commands to run the migration tool

##### Launch a JHipster application and a Cassandra cluster:

First you need to build a Docker image containing your application:

```language-bash
./mvnw package -Pprod docker:build
```
Then you can run this image and the other services with Docker Compose:

```language-bash
docker-compose -f src/main/docker/app.yml up -d
```
Compose starts 4 Docker containers:

- The application image
- A first Cassandra node acting as the contact point
- A second node joining the cluster
- A service executing the schema migrations

Thanks to Docker Compose, it is easy to add new nodes in the cluster:

```language-bash
docker-compose -f src/main/docker/app.yml scale yourapp-cassandra-node=2
```

 The new node automatically joins the Cassandra cluster using the first node as a contact point.The container executing the migrations reads a dedicated folder – *config/cql/changelogs/ *by convention – to find the migration scripts.

Like Liquibase, the migration tool stores the metadata of the executed scripts in a table named schema_version to keep track of executed scripts.

##### Modifying the schema with JHipster and using the migration tool:

With JHipster, it is possible to create a new domain entity in one command with a few questions:

```language-bash
$ yo jhipster:entity book
Generating field #1? Do you want to add a field to your entity? Yes
? What is the name of your field? title

? What is the type of your field? String

? Do you want to add validation rules to your field? No
Generating field #2

? Do you want to add a field to your entity? Yes

? What is the name of your field? author

? What is the type of your field? String

? Do you want to add validation rules to your field? No
Generating field #3

? Do you want to add a field to your entity? Yes

? What is the name of your field? releaseDate

? What is the type of your field? LocalDate (Warning: only compatible with cassandra v3)

? Do you want to add validation rules to your field? No
? Do you want to use a Data Transfer Object (DTO)? No, use the entity directly

? Do you want to use separate service class for your business logic? No, the REST controller should use the repository directly
```

JHipster generates AngularJS code for the frontend and java code for the basic CRUD operations. It also generates the CQL script to create the Cassandra table for this new entity:

```language-sql
CREATE TABLE IF NOT EXISTS book (
   id uuid,
   title text,
   author text,
   releaseDate date,
   PRIMARY KEY(id)
);
```

Without stopping the cluster, you can execute the migration tool to run the CQL script:

```language-bash
docker-compose -f src/main/docker/app.yml up yourapp-cassandra-migration
```

 Package the application into a new image and relaunch only its container:
```language-bash
./mvnw package -Pprod docker:build
docker-compose -f src/main/docker/app.yml up -d --no-deps yourapp-app
```

<span style="font-weight: 400;">Without restarting the Cassandra cluster, JHipster has created all the screens, the java code and has executed the migration script to create the new Cassandra table:</span>

[![JHipster generated](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/06/Screen-Shot-2016-05-24-at-4.10.47-PM-1024x486.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/06/Screen-Shot-2016-05-24-at-4.10.47-PM.png)

<span style="font-weight: 400;">By providing a simple tool to manage a Cassandra environment for development, tests and deployment JHipster is also providing best practices to start new applications based on Cassandra.</span>

<span style="font-weight: 400;">You can find more on the [JHipster project website](http://jhipster.github.io).</span>
<span style="font-weight: 400;">Ippon USA is hosting a master class on JHipster with material designed by the JHipster creator, </span>[<span style="font-weight: 400;">register here</span>](https://www.eventbrite.com/e/jhipster-master-class-ippon-usa-tickets-21358779685)<span style="font-weight: 400;"> !</span>

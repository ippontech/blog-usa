---
authors:
- Theo Lebrun
tags:
- Cassandra
- Big Data
date: 2021-01-14T14:50:55.000Z
title: "Use Stargate by DataStax to effortlessly store and query your data"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/01/stargate-main.png
---

[Stargate](https://stargate.io/) is one of the latest shiny tools from [DataStax](https://www.datastax.com/) that will act as a data gateway to abstract Cassandra-specific concepts and remove barriers of entry for new software developers!

Stargate is a full open source project ([GitHub repository](https://github.com/stargate/stargate)) that supports the most popular ways of querying data (REST, GraphQL, Document and CQL API). By choosing their favorite way of querying data, developers will spend less time learning the API and more time on implementing new features.

# Docker and Stargate for an easy setup

Thanks to [Docker](https://www.docker.com/), setting up Stargate is very simple and will not even require a Cassandra cluster. Here is the command that will start the Stargate container:

```bash
docker run --name stargate \
  -p 8080:8080 \
  -p 8081:8081 \
  -p 8082:8082 \
  -p 127.0.0.1:9042:9042 \
  -d \
  -e CLUSTER_NAME=stargate \
  -e CLUSTER_VERSION=3.11 \
  -e DEVELOPER_MODE=true \
  stargateio/stargate-3_11:v1.0.0
```

The flag `DEVELOPER_MODE` tells Stargate to use its own Cassandra cluster but you can also use your own cluster. In order to provide your own cluster, you can follow the [official documentation](https://stargate.io/docs/stargate/1.0/developers-guide/install/install_existing_cstar.html).

The Stargate container exposes 4 different services:
- GraphQL interface for CRUD (**8080**)
- REST authorization service for generating tokens (**8081**)
- REST interface for CRUD (**8082**)
- CQL service (**9042**)

Make sure that the container was started without any errors before continuing.

![Output](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/01/stargate-3.png)

# Inserting data using the REST API

## Overview

The rest API is secured using a classic token stored in the header `X-Cassandra-Token`. The curl request below will generate a token and then you just have to store it in the environment variable `AUTH_TOKEN`:

```bash
curl -L -X POST 'http://localhost:8081/v1/auth' \
  -H 'Content-Type: application/json' \
  --data-raw '{
    "username": "cassandra",
    "password": "cassandra"
}'

# paste value of authToken
export AUTH_TOKEN=
```

Make sure that everything is working by checking that the keyspace `system` exists:

```bash
curl -L -X GET 'localhost:8082/v2/schemas/keyspaces/system' \
-H "X-Cassandra-Token: $AUTH_TOKEN" \
-H 'Content-Type: application/json'
```

For more details about the REST API, please refer to the [documentation](https://stargate.io/docs/stargate/1.0/developers-guide/rest-using.html).

## Data insertion

In order to easily generate data, I wrote a small Node.js script that will generate "fake" vehicles using the library [Faker.js](https://github.com/marak/Faker.js/) and make REST requests using [Axios](https://github.com/axios/axios).

Start by cloning my [repository](https://github.com/Falydoor/blog-stargate) and then run `npm install` to install the required libraries.

Run the script with `node server.js` to create a keyspace `blog` and insert 50 different vehicles in the table `vehicle`. Your console should output the type and manufacturer of all inserted vehicles:

![Output](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/01/stargate-1.png)

Here is the table definition of `vehicle`:

```json
{
  "data": {
    "name": "vehicle",
    "keyspace": "blog",
    "columnDefinitions": [
      {
        "name": "manufacturer",
        "typeDefinition": "varchar",
        "static": false
      },
      {
        "name": "type",
        "typeDefinition": "varchar",
        "static": false
      },
      {
        "name": "color",
        "typeDefinition": "varchar",
        "static": false
      },
      {
        "name": "model",
        "typeDefinition": "varchar",
        "static": false
      },
      {
        "name": "vin",
        "typeDefinition": "varchar",
        "static": false
      }
    ],
    "primaryKey": {
      "partitionKey": [
        "manufacturer"
      ],
      "clusteringKey": [
        "type"
      ]
    },
    "tableOptions": {
      "defaultTimeToLive": 0,
      "clusteringExpression": [
        {
          "order": "ASC",
          "column": "type"
        }
      ]
    }
  }
}
```

The partition key is `manufacturer` and the clustering key is `type` so we can get all vehicles by manufacturer and sort them by type.

# Querying the data

## GraphQL API

Stargate will provide a GraphQL playground available at http://localhost:8080/playground and don't forget to set the header `x-cassandra-token` with the correct value at the bottom of the UI.

Data can be queried by opening a new tab and then using `http://localhost:8080/graphql/blog` for the schema.

Here is a query that will retrieve all vehicles manufactured by `Tesla`:

```
query vehicles {
  vehicle(value: { manufacturer: "Tesla" }) {
    values {
      manufacturer
      type
      color
      model
      vin
    }
  }
}
```

The query should output something like below:

![Output](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/01/stargate-2.png)

What makes GraphQL great is that you can define which fields to retrieve directly in your query to simplify things a lot when querying complex APIs.

More details on how to use the GraphQL API can be found [here](https://stargate.io/docs/stargate/1.0/developers-guide/graphql-using.html).

## REST API

The query below can also be done in a more traditional way with the REST API and using predefined search terms.

The curl command below will return the same result as above:

```bash
curl -L -X GET 'http://localhost:8082/v2/keyspaces/blog/vehicle?where=\{"manufacturer":\{"$eq":"Tesla"\}\}' \
-H "X-Cassandra-Token: $AUTH_TOKEN" \
-H 'Content-Type: application/json'
```

More details on how to use the REST API can be found [here](https://stargate.io/docs/stargate/1.0/developers-guide/rest-using.html).

## Document API

This API is a bit different from the others because it will let you save unstructured JSON documents. This is pretty useful when you don't want to spend time defining the columns of your table.

Let's insert a vehicle with the curl command below:

```bash
curl --location \
--request POST 'localhost:8082/v2/namespaces/blog/collections/vehicles' \
--header "X-Cassandra-Token: $AUTH_TOKEN" \
--header 'Content-Type: application/json' \
--data '{
  "vin": "7OS5T5HQLGT073117",
  "model": "Spyder",
  "type": "Sedan",
  "color": "teal",
  "manufacturer":"Tesla"
}'
```

And then retrieve it:

```bash
curl --location \
--request GET 'localhost:8082/v2/namespaces/blog/collections/vehicles' \
--header "X-Cassandra-Token: $AUTH_TOKEN" \
--header 'Content-Type: application/json'
```

But what makes Stargate awesome is that I can also use GraphQL to retrieve my document:

```
query vehicles {
  vehicles(value: {key: "DOCUMENT_ID"}) {
    values{
      leaf
      text_value
    }
  }
}
```

The query output might be confusing because the document is flattened but a condition can be added to only retrieve the needed field.

More details on how to use the Document API can be found [here](https://stargate.io/docs/stargate/1.0/developers-guide/document-using.html).

## CQL API

Finally, the CQL API will let you access the data using your favorite native language drivers like you would normally do with a regular Cassandra cluster.

In our case, after being connected to our Stargate container using the [instructions here](https://stargate.io/docs/stargate/1.0/developers-guide/cql-using.html), we can run the following query to get all our vehicles:

```sql
SELECT * FROM blog.vehicle;
```

# DataStax Astra

To conclude, Stargate is a very powerful data gateway that will save you a lot of time by avoiding having to create and support APIs for your developers. You will also not have to worry about scalability and availability since Stargate uses Cassandra for its datastore.

Feel free to connect Stargate on your existing Cassandra cluster or start using [DataStax Astra](https://astra.datastax.com/register) for a Cassandra-as-a-Service experience.

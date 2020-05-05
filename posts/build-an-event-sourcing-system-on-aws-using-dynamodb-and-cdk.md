---
authors:
- Theo Lebrun
tags:
- AWS
- AWS DynamoDB
- AWS CDK
- Event Sourcing
date: 2020-05-01T14:50:55.000Z
title: "Build an event sourcing system on AWS using DynamoDB and CDK"
image: https://raw.githubusercontent.com/Falydoor/blog-usa/event-sourcing/images/2020/05/event-sourcing-logo.png
---

Event sourcing is a very popular pattern and can be used in a modern microservices architecture. The pattern's goal is to have a reliable way of updating the database while being able to publish messages on a queue/topic. A good explanation of the pattern can be found [here](https://microservices.io/patterns/data/event-sourcing.html).

In this blog, I will explain how to design and deploy an event sourcing solution on Amazon Web Services (AWS). The principal AWS services used will be [Amazon DynamoDB](https://aws.amazon.com/dynamodb/), [AWS Lambda](https://aws.amazon.com/lambda/) and [AWS Cloud Development Kit](https://aws.amazon.com/cdk/). The solution will simply track the requests made by the gateway to the other services and persist them. It is very useful to have that kind of feature because it will let you identify which service can be faulty.

# AWS Architecture

The architecture will be composed of 4 main components:
- Queue system
- Event producer
- Event consumer
- Datastore

The queue system is necessary in order to decouple, scale and avoid blocking the event producers. In our case, only the gateway will produce events but other producers can be plug-in later for other use cases.

The gateway will produce a lot of events and we need a datastore that is very performant and scalable. There is a lot of different NoSQL databases available (MongoDB, Cassandra, DynamoDB, etc) but because this blog is focused on AWS, I decided to go for the full managed solution: DynamoDB.

Finally, the remaining piece is which service will be in charge of persisting the events. [AWS Lambda](https://aws.amazon.com/lambda/) is the perfect service for that and will let you benefit of all the features of the serverless world.

Overall, the architecture is pretty simple and here is a diagram to summarize it:

![Diagram](https://raw.githubusercontent.com/Falydoor/blog-usa/event-sourcing/images/2020/05/event-sourcing-diagram.png)

# DynamoDB table modeling

I recomend using [NoSQL Workbench for Amazon DynamoDB](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/workbench.html) to help you modeling the DynamoDB table. It is a very useful tool that will help you and will provide data visualization, and query development features. I recommend reading [the best practices](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/best-practices.html) from the AWS developer guide and also [this blog](https://www.trek10.com/blog/the-ten-rules-for-data-modeling-with-dynamodb) that I found pretty useful.

Here is how I decided to design my event table:
- Id
    - UUID of the event
- RequestId
    - UUID to group events of same request
- Timestamp
    - The creation time of the event: `2020-05-01T20:00:00Z`
- Type
    - The type of the event: `MICROSERVICE_REQUEST`
- Source
    - The source of the event: `Gateway`
- Data
    - Additional data as a nested Map: `{"Name":"Microservice A"}`

![Table](https://raw.githubusercontent.com/Falydoor/blog-usa/event-sourcing/images/2020/05/event-sourcing-table.png)

Two GSI (Global Secondary Indexes) are also created on `Type` and `Source` in order to handle access patterns like getting events of from one type or source.

Here is how the aggregate view of the table looks like with data:

![Data](https://raw.githubusercontent.com/Falydoor/blog-usa/event-sourcing/images/2020/05/event-sourcing-data.png)

The events above describe requests made from the gateway to microservice A and B. And they are all grouped by the `RequestId` will allow us to inspect the whole flow.

# Lambda

The code of the Lambda is pretty simple since its role is to simply insert an item to DynamoDB.

```js
var AWS = require('aws-sdk');
var dynamodb = new AWS.DynamoDB();

exports.handler = (event, context, callback) => {
    Promise.all(event.Records.map((record) => {
        var message = JSON.parse(record.body);
        console.log('MESSAGE: %j', message);

        // Build data object and ignore null values
        var data = {};
        for (let key in message.data) {
            if (message.data[key]) {
                data[key] = {
                    'S': message.data[key].toString()
                };
            }
        }

        // Build dynamodb item
        var item = {
            'TableName': 'event',
            'Item': {
                'RequestId': { S: message.requestId },
                'Timestamp': { S: message.timestamp },
                'Data': { M: data },
                'Id': { S: message.id },
                'Type': { S: message.type },
                'Source': { S: message.source }
            }
        };
        console.log('ITEM: %j', item);

        // Insert dynamodb item
        return dynamodb.putItem(item).promise()
            .then(() => {
                console.log('Item inserted');
            })
            .catch((err) => {
                console.error(err);
            });
    }))
        .then(v => callback(null, v), callback);
};
```

The Lambda's code can be found on [the repository](https://github.com/Falydoor/event-sourcing-dynamodb/blob/master/resources/lambda.js).

# Deployment and test

## Deployment using CDK

[AWS Cloud Development Kit](https://aws.amazon.com/cdk/) is a very powerful framework that will let you model and provision your cloud application resources using familiar programming languages. All our stack will be configured/deployed using CDK and I decided to use TypeScript for the programming language.

The code below is pretty self-explanatory and it will create all the components for our stack:

```ts
import * as cdk from '@aws-cdk/core';
import * as sqs from '@aws-cdk/aws-sqs';
import * as dynamodb from '@aws-cdk/aws-dynamodb';
import * as lambda from '@aws-cdk/aws-lambda';
import { SqsEventSource } from '@aws-cdk/aws-lambda-event-sources';

export class EventSourcingDynamodbStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // Create queue
    const queue = new sqs.Queue(this, 'queue', {
      queueName: 'event-queue'
    });

    // Create lambda
    const fn = new lambda.Function(this, 'fn', {
      code: new lambda.AssetCode('resources'),
      handler: 'lambda.handler',
      runtime: lambda.Runtime.NODEJS_12_X,
      functionName: 'event-to-dynamodb'
    });

    // Add lambda source
    fn.addEventSource(new SqsEventSource(queue));

    // Create dynamodb table
    const sortKey = { name: 'Timestamp', type: dynamodb.AttributeType.STRING };
    const table = new dynamodb.Table(this, 'table', {
      partitionKey: { name: 'RequestId', type: dynamodb.AttributeType.STRING },
      sortKey: sortKey,
      tableName: 'event'
    });

    // Create GSI
    table.addGlobalSecondaryIndex({
      indexName: 'Type-Index',
      partitionKey: { name: 'Type', type: dynamodb.AttributeType.STRING },
      sortKey: sortKey
    });
    table.addGlobalSecondaryIndex({
      indexName: 'Source-Index',
      partitionKey: { name: 'Source', type: dynamodb.AttributeType.STRING },
      sortKey: sortKey
    });

    // Grant write to lambda
    table.grantWriteData(fn);
  }
}
```

The Lambda's code can be found on [the repository](https://github.com/Falydoor/event-sourcing-dynamodb/blob/master/lib/event-sourcing-dynamodb-stack.ts). CDK really simplifys your stack definition/deployment and saves you from getting a lot of headaches.

The commands below will deploy the whole stack, make sure to have your `AWS CLI` configured:

```sh
cdk synth
cdk deploy
```

## Testing

In order to test the whole flow, we would need a Gateway that publishes a message to our SQS queue. Let's just use the `AWS CLI` and publish a message to the queue, feel free to change [its content](https://github.com/Falydoor/event-sourcing-dynamodb/blob/master/test/message.json).

```sh
aws sqs send-message --queue-url ${QUEUE_URL} --message-body file://test/message.json
```

Then check in the DynamoDB table that the event was correctly created:

![Event](https://raw.githubusercontent.com/Falydoor/blog-usa/event-sourcing/images/2020/05/event-sourcing-event.png)

If it is not the case, you will have the check the Lambda's logs on CloudWatch.

# Conclusion

Designing and deploying a whole event sourcing system on AWS was pretty simple with the help of CDK and DynamoDB. And by using AWS, the whole system is serverless, scalable and enterprise ready! AWS CDK will really save you time and provide more features than AWS Cloud​Formation. Also, the AWS CDK supports TypeScript, JavaScript, Python, Java, and C# so that you can use your favorite language.

The code used in this blog post is available at [this GitHub repository](https://github.com/Falydoor/event-sourcing-dynamodb) in case you want to clone a working example.

Need help using the latest AWS technologies? Or do you need help modernizing your legacy systems to use these new tools? Ippon can help! Send us a line at [contact@ippon.tech](mailto:contact@ippon.tech).
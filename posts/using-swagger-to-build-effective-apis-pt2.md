---
authors:
- Dan Ferguson
tags:
- API
- Swagger
date: 2019-02-07T20:22:38.000Z
title: "Using Swagger to Build Effective APIs Part 2"
image:
---

This post is a continuation in a discussion on developing APIs which prescribe to the Open API 3.0 specification using Swagger tools.  In this post, we will continue our dicussion with writing paths for your API and testing those paths using Swagger Hub.

## Paths Sections
The third and most important component to an API specification is the paths section.  Here is the meat and potatoes of the API, the definitions behind how it will be used by developers in the future.  Let's look at a simple example of two paths this API could take for the exercise resource:
```YAML
paths:
  /exercises:
    summary: Returns a paginated list of exercises
    description: Returns a paginated list of exercises
    get:
      tags:
        - Exercise
      summary: 'Sample GET operation on the /exercises endpoint.'
      description: 'This operation, defined by the noun "exercises" and the verb "GET" implies we will be making an HTTP GET request against the API, will receive multiple Exercise objects in the response.'
      operationId: 'getAllExercises'
      responses:
        '200':
          description: Successfully retrieved paginated list of all Exercise objects
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/Exercise'
        '500':
          description: Server error prevented successful retrieval
  /exercises/{id}:
    summary: Returns an exercise based on the ID passed
    description: Returns an exercise based on the ID passed
    get:
      tags:
        - Exercise ID
      summary: "Returns an exercise based on the ID passed"
      description: "Returns an exercise based on the ID passed"
      parameters: 
        - name: id
          in: path
          description: Exercise ID
          required: true
          schema:
            type: integer
            format: int64
      operationId: 'getExerciseByID'
      responses:
        '200':
          description: Successfully retrieved a single Exercise object
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Exercise'
  /exercises/exercise:
    summary: POST a new exercise
    description: POST a new exercise
    post:
      tags:
      - Exercise
      operationId: 'postExercise'
      summary: "POST a new exercise"
      description: "POST a new exercise"
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/Exercise'
      responses:
        '202':
          description: Payload was accepted
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Exercise'
        '400':
          description: Back-end was unavailable
```
The example above shows two GET endpoints that define operations on the exercise resource defined in the components section of the specification.  You'll notice for each endpoint we define an HTTP Method (at least one), any relevant parameters associated with that method request, and the expected responses that come from that request path are mapped to components with the `$ref` tag.

Pathing is a complex topic in API development.  There are many things to consider when defining API paths, like HTTP Headers, HTTP Methods, endpoint names, response types, versions, etc.  For example, you will notice each API path I have specified is a noun.  This noun is either singular or plural, depending on the number of objects returned by the response definition.

The only verbs in this API design are the HTTP verbs, which is as it should be.  Some developers may be tempted to say `http://www.myapi.com/getAllExercises`.  This is not good practice; behind the scenes, this operation is a `GET getAllExercises`, which is redundant.  Instead, define your API paths as nouns and use the HTTP operations as verbs.

If you can concatenate the two and create a sensible phrase, you have successfully defined a logical endpoint like `POST exercise` or `GET exercises`.  The use of verbs to define REST API operations is a tricky business and is generally considered bad practice.  However, if you are defining a different kind of API, like SOAP or more generally RPC, this is good practice.  RPC APIs are beyond the scope of this post, but if you would like to do more reading, I would suggest [this article](https://www.smashingmagazine.com/2016/09/understanding-rest-and-rpc-for-http-apis/), which neatly defines the differences between REST APIs and RPC type APIs.

Usually, APIs are written by architects, for developers.  This immediately implies a paradigm shift will be required in order to keep your API future proof from developer requests.  If you want to design your API so it is easily upgradeable, easily maintainable, and easily understood by both users and developers, check out these articles: [API Design](https://hackernoon.com/restful-api-design-step-by-step-guide-2f2c9f9fcdbf) and [API Best Practices](https://hackernoon.com/restful-api-designing-guidelines-the-best-practices-60e1d954e7c9).

# Test your Swagger using Swagger Hub tools
Once you've built your API Specification using the Swagger Editor, you have to test it.  By far, the easiest way you can test your API specification is to copy it from the Swagger Editor and paste it into Swagger Hub.  This will, of course, require you to sign-in to Swagger Hub and create a new API; but the ease of testing involved make the process well worth it.  Additionally, you can make any new API private on Swagger Hub, if you are concerned others may poach your API.

Once you've pasted your API into the Swagger Hub, you'll notice the UI will take on a very similar look and feel as the Swagger Editor.  They are not identical displays however; in fact, you may notice that Swagger Hub adds a server line for you.  This is what Swagger Hub added to the server section of my API
```YAML
# Added by API Auto Mocking Plugin
  - description: SwaggerHub API Auto Mocking
    url: https://virtserver.swaggerhub.com/dferguson992/aspotr/1.0.0
```
This is a virtual server used to mock requests to your API.  Your username, API name, and API Version number all define the URL of the mock server.  You cannot ping the server, but you can run curl commands against API endpoints hosted on the server.  These API endpoints are pulled straight from your specification and will send mock data to your endpoints so you can physically see the responses.  Furthermore, you are free to modify the mock data to fit any edge cases you may want to program into your application in the future.  It is important to note, this is just a sanity test designed to allow you to see the specified output of your API.  You should not use the mock endpoints to actually test your business logic.  The mock endpoint is created solely for the purpose of viewing and verifying the expected output.  

Let's look at a few of the examples from the demo API:
```SHELL
$ curl -X GET "https://virtserver.swaggerhub.com/dferguson992/aspotr/1.0.0/exercises" -H "accept: application/json"
[ {
  "id" : 0,
  "sets" : [ {
    "id" : 0,
    "exercise" : {
      "id" : 0,
      "name" : "string",
      "description" : "string",
      "primaryMuscleGroupId" : {
        "id" : 0,
        "name" : "string"
      },
      "movementModifier" : "isolation"
    },
    "repetitions" : [ {
      "id" : 0,
      "weight" : 0,
      "system" : true,
      "repetitionModified" : "tempo"
    } ],
    "name" : "string"
  } ],
  "avgRestTime" : 0
}
$ curl -X GET "https://virtserver.swaggerhub.com/dferguson992/aspotr/1.0.0/exercises/1" -H "accept: application/json"
{
  "id" : 0,
  "sets" : [ {
    "id" : 0,
    "exercise" : {
      "id" : 0,
      "name" : "string",
      "description" : "string",
      "primaryMuscleGroupId" : {
        "id" : 0,
        "name" : "string"
      },
      "movementModifier" : "isolation"
    },
    "repetitions" : [ {
      "id" : 0,
      "weight" : 0,
      "system" : true,
      "repetitionModified" : "tempo"
    } ],
    "name" : "string"
  } ],
  "avgRestTime" : 0
}
```
As you can see, these two GET requests return basic Exercise objects.  This mocking allows us to view the exercises as they would be returned from the API.  This is important, as it allows us to easily define improved endpoints for future releases of the API.  Seeing the results of the endpoint requests, even if they are mocked, will always be more valuable than writing the specification and never seeing it in action until business logic is written.

We can even modify the request body of the POST endpoint we defined earlier:
```SHELL
$ curl -X POST "https://virtserver.swaggerhub.com/dferguson992/aspotr/1.0.0/exercises/exercise" -H "accept: application/json" -H "Content-Type: application/json" -d {"id":1,"sets":[{"id":0,"exercise":{"id":0,"name":"stricription":"string","primaryMuscleGroupId":{"id":0,"name":"string"},"movementModifier":"isolation"},"repetitions":[{"id":0,"weight":0,"system":true,"repetitionModified":"tempo"}],"name":"string"}],"avgRestTime":0}
{
  "id" : 0,
  "sets" : [ {
    "id" : 0,
    "exercise" : {
      "id" : 0,
      "name" : "string",
      "description" : "string",
      "primaryMuscleGroupId" : {
        "id" : 0,
        "name" : "string"
      },
      "movementModifier" : "isolation"
    },
    "repetitions" : [ {
      "id" : 0,
      "weight" : 0,
      "system" : true,
      "repetitionModified" : "tempo"
    } ],
    "name" : "string"
  } ],
  "avgRestTime" : 0
}
```
We defined the payload as an exercise containing just default values for each field, and passed it in JSON to the curl statement under the `-d` flag.  As you can see, our API returned a sample exercise object that deviated from our payload.  It is difficult to see at first, but if you look closely you will see we passed an exercise with an id of "1" to the curl statment, and received an exercise with an id of "0" in the response body.  This is a short-coming inherent to mocking.  In our API specification, we may have intended our API to return the exact response we sent, but there is no way to emulate this behavior in a mock.  This is because REST APIs are stateful.  There is no way for us to emulate this intended behavior without designing business logic.  Situations like these are important to pay close attention to when mocking an API, as they may reveal flaws in your assumption about how your API will work.  

# Summary
Swagger is a great toolset for anyone looking to develop an API.  For those of us looking to write APIs, you should always adhere to a common standard which will make your API usable.  That is why you should use the OAS 3.0.n specification for your API.  APIs that do not adhere to this design have the potential to be difficult to use and maintain.  Any tool that is difficult to maintain will not be used.

Inversely, Swagger is a tool that is very easy to use, which is why I have developed this demo API using Swagger tools in the first place.  Some of the additional benefits of Swagger tools include their portability.  Swagger Hub will let you generate templated client and server code in nearly any language you want.  This allows you to rapidly prototype APIs for the future.

For example, AWS API Gateway will generate all of the features of your OAS compliant API like models and routes, will help you test your API, and will even deploy your API in a serverless context using AWS Lambda to handle endpoint calls.  The only thing API Gateway needs to get started is a Swagger document.  Maven has a swagger plug-in which takes a definition YAML file and generates client models on the fly so you can rapidly prototype an application for your API.  If the API changes, you just update the definition file and your API code will change with it.  Swagger is the toolset for helping architects write APIs quickly, and OAS 3.0.n allows these APIs to be used by everyone.

If something in this article piqued your interest and you would like more information on JHipster or the services we offer at Ippon USA we’d love to hear from you! Please contact us at contact@ippon.tech with your questions and inquiries.

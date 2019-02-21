---
authors:
- Dan Ferguson
tags:
- API
- Swagger
date: 2019-02-07T20:22:38.000Z
title: "Using Swagger to Build Effective APIs"
image:
---

The rise of serverless computing and platform agnostic, app-based services use has forced the casual developer to learn proper API development, a task usually reserved for platform architects.  Unfortunately, there are many programmers, start-ups, and full-fledged companies that do not adhere to proper API development and maintenance. The skillsets and mindsets involved in creating APIs are different from those involved in developing the business logic which uses an API.
 
This schism does not affect developers or architects; it only affects the users of the API.  Poorly developed or poorly documented APIs which are susceptible to frequent changes or misleading design decisions are difficult to use.  If a product is difficult to use it likely will not be used at all, especially if that product is an API.  The good news is there are specifications and tools which aid in the development of easy to use APIs that change as fast as your business.  I am speaking specifically about the Open API Specification and Swagger.

# OpenAPI Initiative and Swagger
As REST APIs became more prevalent across the Internet, the standards used to develop them became more varied.  To fix this issue, a bunch of companies got together and created the [OpenAPI Initiative](https://www.openapis.org/about).  This initiative sought to codify and finalize a standard ruleset for REST API development.  Since the inception of this open source group, the OpenAPI Specification (OAS) has gone through 3 versions, the last two of which are fully supported by the [Swagger API Editor](https://swagger.io/solutions/getting-started-with-oas/).

Swagger is a company that creates and supports open source API development tools.  Many of these tools are available on their website, or as we will shortly see as Docker containers.  These tools like Swagger Hub, Swagger Editor, Swagger UI, and Swagger Validator all work together to aid in developing proper APIs.  Furthermore, all Swagger tools support the OAS 3.0.n specification.  This fact makes adhering to OAS standards very easy when using Swagger tools.  

The exact rules defining OAS are on [Github](https://github.com/OAI/OpenAPI-Specification) for any user to peruse, but tools like Swagger make conforming to these rules easy for anyone.  You do not have to be familiar with every detail of the OpenAPI specification to develop an API which conforms to said specification.  The rules themselves are very detailed, and describing each one in this blog post would be redundant.  Instead, we are going to use Swagger to build a demo REST API which conforms to OpenAPI standards and syntax.  After walking through this tutorial, I encourage the reader to browse the specification in more detail.  I will cover as many features of the OpenAPI version 3.0.0 specification as possible in this article, but the most detail can be found in [the specification itself](https://swagger.io/solutions/getting-started-with-oas/).

# What is this API going to do?
Every API needs to have a purpose which defines how it will be used.  Personally, I love to exercise.  Going to the gym, taking a fitness class, and running are all ways I like to workout.  But as my workouts become more varied, it is difficult to track my progress from workout to workout.  We are going to develop a sample API which will help me track my exercises, no matter how varied they are.  

Take a look at this graphic:

![](https://github.com/ippontech/blog-usa/blob/master/images/2019/02/aspotr_umls.png)

Here we define a workout as an array of exercises.  Each exercise is a combination of sets with a rest time.  Each set is an array of repetitions defined primarily by a weight, as well as a lift, which is defined primarily by an affected muscle group.  Now we have the framework to easily define a weight lifting workout.  How do we translate this diagram of loose fitting parts into an API?

There are three steps we will need to take to turn these UML diagrams into tangible JSON packets.  The first step is to set up our local Swagger development environment.  The second is to populate our API definitions with some sample paths.  The last step is to test these sample paths using Swagger's testing tools on Swagger Hub.  It is important to note that because we are using Swagger tools to develop our API, we will automatically be prescribing to OAS 3.0 standards, ensuring our API will be easy to use for developers.

# Setting up Swagger-Editor
If you prefer to work entirely online, feel free to check out [Swagger.io](https://swagger.io/).  Their service, Swagger Hub, functions exactly like Swagger Editor, and it even includes testing features which we will explore later.  Personally, I prefer to use Docker containers so I do not always have to be connected to the Internet.  If you do decide to use Swagger Hub, you can skip over this portion of the post.
## MacOS
For MacOS, install Docker on your laptop by running the following in a terminal:
```
brew install docker
```
## Windows
For Windows, navigate to the [Docker download link](https://download.docker.com/win/stable/Docker%20for%20Windows%20Installer.exe) and follow the prompts until Docker is installed.  
## Next Steps
Once Docker is installed, run the `docker search swagger` command using the docker CLI to search for the swagger editor container.  It is important to note here, we want the swaggerapi/swagger-editor image.  The swagger-ui image is better for presenting API documentation in a web browser.  The swagger-generator image starts a web server that will generate clients or servers in a RESTful context when given an API as input.  The swagger-validator image is used for assigning Swagger badges to Github repositories.  In this instance, we only care about the swagger-editor image, as it will allow us to deploy a local web service that we can use to edit our API without logging into Swagger's online services.
```
$ docker search swagger
NAME                                     DESCRIPTION                                     STARS               OFFICIAL         
swaggerapi/swagger-editor                The swagger-editor web service                  240                                   
swaggerapi/swagger-ui                    A simple docker container for hosting swagge…   145                                   
swaggerapi/swagger-generator             The swagger codegen web service                 66                                    
......
$
$
$ docker pull swaggerapi/swagger-editor
```
Once you have your container downloaded, run the following command to start the container and map the container port 8080 to your computer's port 8080
```
$ docker run -p 127.0.0.1:8080:8080 swaggerapi/swagger-editor &
```
This should start the container, but we can confirm by running `docker ps` which should give output similar to the following: 
```
$ docker ps
CONTAINER ID        IMAGE                       COMMAND                  CREATED             STATUS                  PORTS                      NAMES
258a99d971e9        swaggerapi/swagger-editor   "sh /usr/share/nginx…"   1 second ago        Up Less than a second   127.0.0.1:8080->8080/tcp   elated_margulis
```
By navigating to port 8080 in our browser, we can see the default Swagger Editor UI.  You should see the standard Pet Store sample API definition in the Swagger UI:

![](https://github.com/ippontech/blog-usa/blob/master/images/2019/02/swagger-default-ui.png)

The Pet Store API is the sample API for the Swagger 2.0 specification.  In the next section, we'll explore how we can convert this API into an OpenAPI 3.0 Specification API, in accordance with the UML diagrams above.

### Swagger-Editor vs. Swagger Hub
If the above steps were at all difficult or prohibitive for you, check out [Swagger.io](https://swagger.io/).  This website hosts all of the web services discussed here in their newest versions.  The only drawback is you will have to login to the Swagger website and host your API there.  If this is not a problem for you, feel free to use Swagger Hub for the remainder of this tutorial.  Both platforms feature Ctrl+Space code completion tips that are essential if you find yourself struggling to format your API properly.  We will be using Swagger Hub later in this post to test our API.

# Configuring Swagger for OpenAPI 3.0 Specification
The default Swagger file in the Swagger Editor conforms to Swagger 2.0 (also known as Open API Specification 2.0).  The Swagger Editor supports the generation of clients and servers in nearly any language for the given API so long as that API conforms to the 2.0 specification.  This alone is a strong impetus to retain the 2.0 API specification; but since the goal of this project is to eventually build an API and program its functions using serverless technology, we do not need to generate client and server code to handle API calls.  Even so, it should be noted for those of you using Swagger Hub, the online tool, you can still generate clients and servers against OAS 3.0, you just cannot generate clients and servers against OAS 3.0 using the Swagger Editor container.

## API Metadata & Additional Docs Section
The first component of an OAS 3.0 API is not relevant to the function of the API.  This section of an API specification retains information about the API like which version it is, who wrote the API, servers where you can contact the API, additional documentation, licensing information, and the APIs schema.  In our case, this block of YAML should look similar to the following:
```YAML
openapi: 3.0.0
info:
  title: ASpotr
  version: 1.0.0
  description: This is the ASpotr API.
  contact:
    email: dferguson@ippon.fr
    name: Daniel Ferguson
    url: 'http://dferguson992.github.io'
  license:
    name: Apache 2.0
    url: 'http://www.apache.org/licenses/LICENSE-2.0.html'
servers:
  - url: 'http://www.aspotr.com'
    variables: {}
    description: ASpotr API Gateway, ASpotr is serverless
externalDocs:
  description: Find out more about Swagger
  url: 'http://swagger.io'
```
The first line defines the specification this API adheres too.  This will usually be `swagger: 1.0`, `swagger: 2.0`, or `openapi: 3.0.n` where n is some minor version number.  Here I use 3.0.0 for simplicity's sake.  This first line tells the swagger validator what to expect in the rest of the document, much like defining XSD files in an XML file.  The next block, `info`, is for information about the API.  This is an optional component, but it is good practice to specify this block, as well as `servers` and `externalDocs` anyway.  This entire section is completely optional, but a good API, especially an open source one, will maintain license and contact information for use by contributors.

## Components Section
The second major component to an OAS 3.0 API are the components.  For those of you with an object-oriented programming background, it is easy to think of components as plain objects without any business logic.  In order to build strong path definitions in your API, it is imperative we build strong, well-defined models that can be referenced from your paths.  This is where the UML diagram from above comes in handy.  Considering the UML diagram, we can create API models defined below.  As you read this section, pay close attention to the use of the `$ref` tag:
```YAML
components:
  schemas:
    MuscleGroup:
      type: object
      properties:
        id:
          type: integer
          format: int64
        name:
          type: string
          description: The name of the Muscle Group
    Lift:
      type: object
      properties:
        id:
          type: integer
          format: int64
        name:
          type: string
          description: The name of the Exercise
        description:
          type: string
        primaryMuscleGroupId:
          $ref: '#/components/schemas/MuscleGroup'
        movementModifier:
          type: string
          description: >-
            Select these modifiers to further describe the exercise you're
            doing.
          enum:
            - isolation
            - supination
            - wide
            - narrow
      required:
        - id
        - name
        - primaryMuscleId
    Repetition:
      type: object
      properties:
        id:
          type: integer
          format: int64
        weight:
          type: integer
          format: int64
        system:
          type: boolean
          description: English(T) or Metric(F) units
        repetitionModified:
          type: string
          description: >-
            Select these modifiers to further describe the repetition you're doing.
          enum:
            - tempo
            - static
            - plyometric
            - isometric
      required:
        - id
    Set:
      type: object
      properties:
        id:
          type: integer
          format: int64
        exercise:
          $ref: '#/components/schemas/Lift'
        repetitions:
          type: array
          items:
            $ref: '#/components/schemas/Repetition'
        name:
          type: string
          description: The name of the set.  Should be autogenerated by the API based on the information within the set.
      required:
        - id
        - exercise
        - repetitions
    Exercise:
      type: object
      properties:
        id:
          type: integer
          format: int64
        sets:
          type: array
          items:
            $ref: '#/components/schemas/Set'
        avgRestTime:
          type: integer
          format: int64
      required:
        - id
        - sets
    Workout: 
      type: object
      properties:
        id:
          type: integer
          format: int64
        exercises:
          type: array
          items:
            $ref: '#/components/schemas/Exercise'
      required:
        - id
        - exercises
```
Starting with our most primitive objects built on basic data types like MuscleGroup, we can define complex objects like Workout.  The glue that binds these objects together is the `$ref` tag.  This tag references another location in your API specification.  When defining models, we use it heavily to build our complex objects based on definitions of simpler objects.  This tag works by defining a structure similar to directories, where the `#` character is the root tag, and the `/` character seperates each level of the YAML document.  This structure helps to define components in your API using other, previously defined components.  This tag is incredibly powerful, as it allows you to not only reference components in this document, but to reference components in other API specification documents as well.  This is a great strategy is you are linking two or more extraneous APIs, or if you want to keep your component descriptions seperated from the rest of your API document.

It is important to note, the demo API in this post may not be a scalable API design.  For our purposes, this design may work just fine; but it is glaringly obvious this design will create large, nested JSON payloads for even the shortest workouts.  The example API in this post implies each transaction is stateful, and payload components are dependent upon other potentially unrelated components.  For example, say you simply want to track the number of push-ups you've done in a day.  With this API design, you have two choices.  The first is to slowly build your JSON workout object by periodically adding set objects to it throughout the day.  The second option is to send small workout requests objects throughout the day, each containing one exercise of about 20 or so push ups.  The first option is not convenient, but it lends itself well to exercise tracking, especially if you wanted to model the data you've collected and build progress projections over time.  In fact, this first approach is how one would assume any workout tracking is to be done.  The second approach is much more convenient for the user, even if the machine learning conducted on your data set is blown out of the water if multiple workouts of the same exercise are logged per day.  For our purposes now, this API will work just fine; but as you continue to read, consider optimizations to the API that would make it scalable and easy to use.  It is important to think about the API from all perspectives, those modeling the business logic on the back-end, and those submitted REST requests on the front-end.  If you keep these tenants in mind, you can build a much more atomic API that maintains its usability by both developers and users.

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

Pathing is a complex topic in API development.  There are many things to consider when defining API paths, like HTTP Headers, HTTP Methods, endpoint names, response types, versions, etc.  For example, you will notice each API path I have specified is a noun.  This noun is either singular or plural, depending on the number of objects returned by the response definition.  The only verbs in this API design are the HTTP verbs, which is as it should be.  Some developers may be tempted to say `http://www.myapi.com/getAllExercises`.  This is not good practice; behind the scenes this operations is a `GET getAllExercises`, which is redundant.  Instead, define your API paths as nouns and use the HTTP operations as verbs.  If you can concatenate the two and create a sensible phrase, you have successfully defined a logical endpoint like `POST exercise` or `GET exercises`.  The use of verbs to define REST API operations is a tricky business and is generally considered bad practice.  However, if you are defining a different kind of API, like SOAP or more generally RPC, this is good practice.  RPC APIs are beyond the scope of this post, but if you would like to do more reading, I would suggest [this article](https://www.smashingmagazine.com/2016/09/understanding-rest-and-rpc-for-http-apis/), which neatly defines the differences between REST APIs and RPC type APIs.

Usually, APIs are written by architects, for developers.  This immediately implies a paradigm shift will be required in order to keep your API future proof from developer requests.  If you want to design your API so it is easily upgradeable, easily maintanable, and easily understood by both users and developers, check out this cool articles on [API Design](https://hackernoon.com/restful-api-design-step-by-step-guide-2f2c9f9fcdbf) and [API Best Practices](https://hackernoon.com/restful-api-designing-guidelines-the-best-practices-60e1d954e7c9).

# Test your Swagger using Swagger Hub tools
Once you've built your API Specification using the Swagger Editor, you have to test it.  By far, the easiest way you can test your API specification is to copy it from the Swagger Editor and paste it into Swagger Hub.  This will of course require you to sign-in to Swagger Hub and create a new API; but the ease of testing involved make the process well worth it.  Additionally, you can make any new API private on Swagger Hub, if you are concerned others may poach your API.

Once you've pasted your API into the Swagger Hub, you'll notice the UI will take on a very similar look and feel as the Swagger Editor.  They are not identical displays however; in fact you may notice that Swagger Hub adds a server line for you.  This is what Swagger Hub added to the server section of my API
```YAML
# Added by API Auto Mocking Plugin
  - description: SwaggerHub API Auto Mocking
    url: https://virtserver.swaggerhub.com/dferguson992/aspotr/1.0.0
```
This is a virtual server used to mock requests to your API.  Your username, API name, and API Version number all define the URL of the mock server.  You cannot ping the server, but you can run curl commands against API endpoints hosted on the server.  These API endpoints are pulled straight from your specification,  and will send mock data to your endpoints so you can physically see the responses.  Furthermore, you are free to modify the mock data to fit any edge cases you may want to program into your application in the future.  It is important to note, this is just a sanity test designed to allow you to see the specified output of your API.  You should not use the mock endpoints to actually test your business logic.  The mock endpoint is created solely for the purpose of viewing and verifying expected output.  

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
Swagger is a great toolset for anyone looking to develop an API.  For those of us looking to write APIs, you should always adhere to a common standard which will make your API usable.  That is why you should use the OAS 3.0.n specification for your API.  APIs that do not adhere to this design have the potential to be difficult to use and maintain.  Any tool that is difficult to maintain will not be used.  Inversely, Swagger is a tool that is very easy to use, which is why I have developed this demo API using Swagger tools in the first place.  Some of the additional benefits of Swagger tools include their portability.  Swagger Hub will let you generate templated client and server code in nearly any language you want.  This allows you to rapidly prototype APIs for the future.  For example, AWS API Gateway will generate all of the features of your OAS compliant API like models and routes, will help you test your API, and will even deploy your API in a serverless context using AWS Lambda to handle endpoint calls.  The only thing API Gateway needs to get started is a Swagger document.  Maven has a swagger plug-in which takes a definition YAML file and generates client models on the fly so you can rapidly prototype an application for your API.  If the API changes, you just update the definition file and your API code will change with it.  Swagger is the toolset for helping architects write APIs quickly, and OAS 3.0.n allows these APIs to be used be everyone.

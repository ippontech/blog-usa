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

The rise of serverless computing and platform agnostic, app-based service use has forced the casual developer to learn proper API development.  Unfortunately, there are many programmers, start-ups, and full-fledged companies that do not adhere to proper API development and maintenance.  This creates problematic scenarios for users.  Poorly documented or APIs that change too frequently are difficult to use, and if a product is difficult to use it likely will not be used at all.  The good news is there are ways to easy to use APIs that change as fast as your business.

# OpenAPI Initiative
As REST APIs became more prevalant across the Internet, the standards used to develop them became more varied.  To fix this issue, a bunch of companies got together and created the [OpenAPI Initiative](https://www.openapis.org/about).  This initiative sought to codify and finalize a standard ruleset for REST API development.  Since the inception of this open source group, the OpenAPI Specification has gone through 3 versions, the last two of which are fully supported by the [Swagger API Editor](https://swagger.io/solutions/getting-started-with-oas/).  This makes studying the specifications of all OpenAPIs very easy when using Swagger tools.  The rules are on [Github](https://github.com/OAI/OpenAPI-Specification) for any user to peruse, but tools like Swagger make conforming to these rules easy for anyone.  You do not have to be familiar with every detail of the OpenAPI specification in order to develop an API which conforms to said specification.  The rules themselves are very detailed, and describing each one in this blog post would be redundant.  Instead we are going learn how to use Swagger to build your REST API which conforms to OpenAPI standards.  For the purposes of this blog post, I will be creating an example API for users to follow along.  After walking through this tutorial, I encourage the reader to browse the specification in more detail.  I will cover as many features of the OpenAPI version 3.0.0 specification as possible in this article, but the most detail can be found in [the specification](https://swagger.io/solutions/getting-started-with-oas/) itself. 

# What is this API going to do?
Every API needs to have a purpose which defines how it will be used.  Let's define how our test API should function.  Take a look at this graphic: ![](https://github.com/dferguson992/blog-usa/blob/master/images/aspotr_umls.png) Here we define a workout as an array of exercises.  Each exercise is a combination of sets with a rest time.  Each set is an array of repetitions defined primarily by a weight, as well as a lift, which is defined primarily by an affected muscle group.  Now we can easily define a workout using JSON in our API.  To do this, we use the OpenAPI 3.0 Specification designed to define models.  We could easily define our model using a standard text editor, but it would be much more efficient to use Swagger.

# Setting up Swagger-Editor
## MacOS
First and foremost, install Docker on your laptop.  For MacOS, run the following in a terminal:
```
brew install docker
```
## Windows
For Windows, navigate to the [Docker download link](https://download.docker.com/win/stable/Docker%20for%20Windows%20Installer.exe) and follow the prompts until Docker is installed.  
## Next Steps
Once Docker is installed, run this command using the docker CLI to search for the swagger editor container.  It is important to note here, we want the swaggerapi/swagger-editor image.  The swagger-ui image is better for presenting API documentation in a web browser.  The swagger-generator image starts a web server that will generate clients or servers in a RESTful context when given an API.  The swagger-validator image is used for assigning Swagger badges to Github repositories.  In this instance, we only care about the swagger-editor image, as it will allow us to deploy a local web service that we can use to edit our API without logging into Swagger's online services.
```
docker search swagger
NAME                                     DESCRIPTION                                     STARS               OFFICIAL            AUTOMATED
swaggerapi/swagger-editor                The swagger-editor web service                  240                                     
swaggerapi/swagger-ui                    A simple docker container for hosting swagge…   145                                     
swaggerapi/swagger-generator             The swagger codegen web service                 66                                      
schickling/swagger-ui                    Swagger UI 2.2.8 with API_URL, API_KEY and O…   49                                      [OK]
swaggerapi/swagger-codegen-cli                                                           31                                      
swaggerapi/swagger-validator             The swagger validator web service               29                                      
palo/swagger-api-mock                    Runs an api mock server, by parsing a swagge…   7                                       [OK]
swagger2markup/swagger2markup            A Swagger to AsciiDoc or Markdown converter.    5                                       
swaggerapi/petstore                      This is the sample petstore application         3                                       
yewton/swagger2html                      Generate HTML document from swagger spec usi…   3                                       [OK]
swaggerapi/swagger.io-editor             A modified version of `swaggerapi/swagger-ed…   3                                       
sjeandeaux/docker-swagger-ui                                                             2                                       [OK]
thanhson1085/swagger-combined            Combines all swagger documents in microservi…   1                                       [OK]
asimio/multiversion-api-jersey-swagger   Multiple REST API versions using Spring Boot…   1                                       
rovecom/swagger-tools                    Docker image containing swagger-tools           0                                       [OK]
wikiwi/swagger-ui                        Container running swagger-ui in NGINX           0                                       [OK]
clever/swagger-explorer                  swagger UI                                      0                                       
pipelineci/swagger                                                                       0                                       
apaleo/swagger-codegen                   swagger-codegen contains a template-driven e…   0                                       [OK]
petemyron/swagger-ui                     adapted from schickling's swagger-ui, but sp…   0                                       
stempler/swagger-ui                      Swagger UI with JWT support (adapted from sc…   0                                       [OK]
neemuchaordic/swagger-ui                 swagger-ui                                      0                                       [OK]
lmazuel/swagger-to-sdk                   Docker image for SwaggerToSDK                   0                                       [OK]
lovullo/swagger-ci                       Swagger for CI builds                           0                                       [OK]
```
It's very easy at this point know `swaggerapi/swagger-editor` is the best container to use.  Once we've identified the proper container, run the following
```
docker pull swaggerapi/swagger-editor
```
Oncd you have your container downloaded, run the following command to start the container and map the container port 8080 to your computer's port 8080
```
docker run -p 127.0.0.1:8080:8080 swaggerapi/swagger-editor &
```
This should start the container, but we can confirm by running `docker ps` and we'll see the image in the output
```
CONTAINER ID        IMAGE                       COMMAND                  CREATED             STATUS                  PORTS                      NAMES
258a99d971e9        swaggerapi/swagger-editor   "sh /usr/share/nginx…"   1 second ago        Up Less than a second   127.0.0.1:8080->8080/tcp   elated_margulis
```
By navigating to port 8080 in our browser, we can see the default Swagger Editor UI.  You should see the standard Pet Store sample API definition in the Swagger UI ![](https://github.com/dferguson992/blog-usa/blob/master/images/swagger-default-ui.png) .  The Pet Store API is the standard API definition for the Swagger 2.0 specification.  In the next section, we'll explore how we can convert this API into an OpenAPI 3.0 Specification API, in accordance with the UML diagrams above.

### Swagger-Editor vs. Swagger Hub
If the above steps were at all difficult or prohibitive for you, check out [Swagger.io](https://swagger.io/).  This website hosts all of the web services discussed here in their newest versions.  The only drawback is you will have to login to the Swagger website and host your API there.  If this is not a problem for you, feel free to use Swagger Hub for the remainder of this tutorial.  Both platforms feature Ctrl+Space code completion tips that are essential if you find yourself struggling to format your API properly.

# Configuring Swagger for OpenAPI 3.0 Specification
The default Swagger file in the Swagger Editor conforms to Swagger 2.0 (also known as Open API Specification 2.0).  The Swagger Editor supports the generation of clients and servers in nearly any language for the given API so long as that API conforms to the 2.0 specification.  This alone is a strong impetus to retain the 2.0 API specification; but since the goal of this project is to eventually build an API and program its functions using serverless technology, we do not need to generate client and server code to handle API calls.  

## API Metadata & Additional Docs Section
The first component of an OAS 3.0 API is not relevant to the function of the API.
```YAML
openapi: 3.0.0
info:
  title: ASpotr
  version: 1.0.0
  description: This is the ASpotr API.
  contact:
    email: dferguson992@gmail.com
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
The first line defines the specification this API adheres too.  This will usually be `swagger: 1.0`, `swagger: 2.0`, or `openapi: 3.0.n` where n is some minor version number.  Here I use 3.0.0 for simplicity sake.  This first line tells the swagger validator what to expect in the rest of the document, much like defining XSD files in an XML file.  The next block, `info`, is for information about the API.  This entire section is completely optional, but a good API will maintain license and contact information for use by contributors.

## Components Section
The second major component to an OAS 3.0 API are the components.  For those of you with an object-oriented program background, it is easy to think of components as plain objects, without any business logic.  In order to build strong path definitions in your API, it is imperative we build strong, well-defined models that can be referenced from your paths.  This is where the UML diagram from above comes in handy.  Considering the UML diagram, we can create API models defined below:
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
Starting with our most primitive objects built on basic data types like MuscleGroup, we can use the OAS 3.0 model notation to define complex objects like Workout.  The glue that binds these objects together is the `$ref` tag.  This tag references another location in your API specification.  When defining models, we use it heavily to build our complex objects based on definitions of simpler objects.

It is important to note, this may not be a scalable API design.  For our purposes, this design may work just fine; but it is glaringly obvious this design will create large, nested JSON payloads for even the shortest workouts.  The example API in this post implies each transaction is stateful, and payload components are dependent upon other potentially unrelated components.  For example, say you simply want to track the number of push-ups you've done in a day.  With this API design, you two choices.  The first is to slowly build your JSON workout object by periodically adding set objects to it throughout the day.  The second option is to send small workout requests objects throughout the day, each containing one exercise of about 20 or so push ups.  The first option is not convenient, but it lends itself well to exercise tracking, especially if you wanted to model the data you've collected and build progress projections.  The second one is much more convenient for the user, even if the machine learning conducted on your data set is blown out of the water with multiple workouts of the same exercise are logged per day.  For our purposes now, this API will work just fine; but as you continue to read, consider optimizations to the API that would make it scalable and easy to use. 

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
The example above shows two GET endpoints that define operations on the exercise resource defined in the components section of the specification.  You'll notice for each endpoint we define an HTTP Method (at least one), any relevant parameters associated with that method request, and the expected responses that come from that request path.  The language is very readable, and it conforms to the OAS 3.0 specification.

Pathing is a complex topic in API development.  There are many things to consider when defining API paths, like HTTP Headers, HTTP Methods, endpoint names, response types, versions, etc.  Furthermore, as the designer of the API, you will likely not be the one using it.  Usually, APIs are written by architects, for developers.  This immediately implies a paradigm shift will be required in order to keep your API future proof from developer requests.  If you want to design your API so it is easily upgradeable, easily maintanable, and behaves the way your developers expect it to without them telling you so, check out this cool articles on [API Design](https://hackernoon.com/restful-api-design-step-by-step-guide-2f2c9f9fcdbf) and [API Best Practices](https://hackernoon.com/restful-api-designing-guidelines-the-best-practices-60e1d954e7c9).

### Code Generation
If you convert from a swagger specification to openapi, you will notice the Swagger Editor GUI does not allow you to generate clients and servers any longer (it's worth noting this is not true for Swagger Hub which still allows you to export clients and servers in accordance with your API specification).  For the purposes of this blog post, we do not care whether we can generate code based on our API at this time.  If this API is deployed in a serverless architecture, we will never need to generate a code base, but the option is always present by copy/pasting your API definition into Swagger Hub instead of using the editor image.

# Test your Swagger using Swagger Hub tools
Once you've built your API Specification using the Swagger Editor, you have to test it.  By far, the easiest way you can test your API specification is to copy it from the Swagger Editor and paste it into Swagger Hub.  This will of course require you to sign-in to Swagger Hub and create a new API; but the ease of testing involved make the process well worth it.  Additionally, you can make any new API private on Swagger Hub, if you are concerned others my poach your API.

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

We can even modify the request body of the POST endpoint we defined earlier
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

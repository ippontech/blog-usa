---
authors:
- Matthew Reed
- Amine Ouali Alami
tags:
- API
- TDD
- Testing
- Postman

date: 2020-09-26T20:16:10.000Z
title: "Automated API testing with Postman"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/01/Ippon_Javers.jpg
---

Introduction:
Postman breef introduction
Postman is used by all the developers to develop and test api
let's automate
Cucumber comparaison ?
What is automated testing?
Why automate your API tests?
Consumer-Driven contract tests

# What is wrong with Cucumber?

In short, nothing is wrong with Cucumber. For a long time now many of us have been productively using Cucumber or one of its variations for Acceptance Test Driven Development (ATDD). The English-written tests are the gates to our applications which keep breaking changes out of the codebase. This approach of bridging the business and development sides is going to continue even after this blog. Yet, hardly every Product Owner is actively writing and maintaining these tests on their projects. Only the high-level test results come up anyway when the CI/CD pipeline runs, so developers are often needed to investigate no matter what.

Why does there need to be such a syntactically complex system in place for a group who looks at the feature file only every few months?

Well, good news everyone! There actually is another way to write integration tests already built into the common development testing tool, [Postman](https://www.guru99.com/postman-tutorial.html). It is simple to get started and, as will be shown, they can be easily integrated with [Newman](https://learning.postman.com/docs/running-collections/using-newman-cli/command-line-integration-with-newman/) to run automatically within a CI/CD pipeline. Soon you will wonder if Cucumber is necessary for your project, and may even choose to forget it altogether.

# Postman's Structure

Readers who have utilized Cucumber in their projects will be familiar with a multitude of configuration, step definition, and feature files that synchronize together for ATDDs. In this alternative, only 2 key file types will be needed:

1. A file with a <b>Collection</b> of API requests containing information on path, params, and body for each. It also will contain the tests for each request. It has a `.postman_collection.json` extension.

This is an example of a Collection:

### T.LY API Postman Collection
![T.LY API Postman Collection]()

And this is one of its requests:

### T.LY Postman Shorten POST Call
![T.LY Postman Shorten POST Call]()

2. A configuration file for the <b>environment</b> variables necessary to run a Collection in DEV, QA, PROD, etc. It has a `.postman_environment.json` extension.

This shows a simple environment configuration with variables like `host` that are substituted in the Collection calls (like above):

### T.LY Postman Environment
![T.LY Postman Environment]()

# Writing Tests in Postman

Assuming an API Collection has already been created, to start writing its tests in Postman navigate to the <b>Tests</b> tab within any request.

To test a proper `200` response was returned from the API, for example, the following is all that is necessary:

```javascript
pm.test("Status code is 200", function () {
  pm.response.to.have.status(200);
});
```

The screenshot here shows an example of response string parsing:

### T.LY Postman Shorten POST Call
![T.LY Postman Expand POST Call]()

Any JavaScript-based functions placed under this <b>Tests</b> tab will be executed in succession. Note that the `"Status code is 200"` or similar string will be the description shown at a high level when running multiple tests. The `pm` object is built into Postman so there is no need to import any dependencies (underneath it uses [the BDD Chai testing framework](https://www.chaijs.com/) to make assertions via chains of `to.have`). The built-in `pm` library may not always be intuitive to use but there is ample documentation and code examples. For a great overview to more writing test basics, the ["Getting started with tests"](https://learning.postman.com/docs/writing-scripts/script-references/test-examples/#getting-started-with-tests) from Postman is an essential resource.

There are quite a few tricks that can be employed when writing a robust Postman test suite. Some of the capabilities include creating broader folder-level tests, equating request params/bodies to response data, setting variables for use in subsequent tests, verifying time stamps, writing scripts that execute before requests to the API, and more. For brevity, this blog will not walk through all of these in depth.

To further explore test writing concepts, refer to [the Postman docs](https://learning.postman.com/docs/writing-scripts/test-scripts/) or this [working Collection and Test Suite example](https://github.com/matthewreed26/postman-tests-tly). The GitHub repo contains the 2 file types outlined above and can easily be imported into Postman. It has the few happy path GET and POST calls as well as their error scenarios that are needed to test this public and free service called the [T.LY URL Shortening](https://t.ly/docs/) API.

# Running Tests in Postman

To ensure all the tests are passing against the API, under the Collection's right-pointing arrow that opens the expanded menu hit the blue "Run" button as is shown below.

### How to Run Written Tests
![How to Run Written Tests]()

Select any of the available options located in the next screen including desired tests for the run order.

### Tests Options Screen
![Tests Options Screen]()

Hit the blue "Run" button to start running the tests. The outcome should be all green/passed. It is important to note how each of the individual outcomes have a high-level description. These are the same outputs that will be displayed in the pipeline's console later.

### Test Suite Passing
![Test Suite Passing]()

If the tests fail here, there is a good likelihood that they will not pass in the pipeline either. This is the place to debug/modify any failing tests and can be returned to later on. For more, visit ["Using the Collection Runner"](https://learning.postman.com/docs/running-collections/intro-to-collection-runs/) within the Postman docs.

With all of the tests passing, any changes must be exported/downloaded and pushed to source control. Export the collection via the "..." a.k.a. "View More Actions" in the collection root:

### Exporting the Postman Collection
![Exporting the Postman Collection]()

Download the environment via the "Environment Management" pop-up in the upper-right corner of Postman:

### Downloading the Postman Environment
![Downloading the Postman Environment]()



# Integrate your test suites with your CI/CD Pipeline
It's time to integrate them with your CI/CD pipeline using Newman.
Newman is a command line runner for Postman that allows you to run and test a Postman Collection directly from the command line which will facilitate the integration with a CI/CD server.
For the purpose of this blog we will use Jenkins as CI/CD server.

## Newmman instalation
Newman is written in Node.js, make sure [Node.js is already installed](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm) on your Jenkins server.
Install Newman globally on your system using NPM, which will allow you to run it from anywhere
'''
npm install -g newman
'''

## Add a new test state to your pipeline
We assume that your Postman Collection and Environment files are under /tests
Create a new stage in the [pipeline](https://blog.ippon.tech/continuous-delivery-with-jenkins-pipeline/) to run the API Test
'''
stage('Checkout and build') {
        ...
    }
stage('Quality check') {
        ...
    }
stage('Deploy API') {
        steps {
            // script to deploy your API
        }
    }

stage('Test API Rest') {
        steps {
            sh 'newman run tests/our.postman_collection.json -e tests/env/test.postman_environment.json'
        }
    }
'''

As you can see, we invoke the run command of Newman, providing our collection an the test environment file as parameters

### Results
Show a screenshot for a succesfull test






# Conclusion
Exploratory Testing





### Sources
* [Postman official website](https://www.postman.com/automated-testing/)
* ["Getting started with tests"](https://learning.postman.com/docs/writing-scripts/script-references/test-examples/#getting-started-with-tests)
* ["Using the Collection Runner"](https://learning.postman.com/docs/running-collections/intro-to-collection-runs/)
* [The BDD Chai testing framework](https://www.chaijs.com/)
* [T.LY URL Shortening Docs](https://t.ly/docs/)

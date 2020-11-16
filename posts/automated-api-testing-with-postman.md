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
title: "Start Automating API Tests with Postman"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/10/postman_logo.png
---

## What is Wrong with Cucumber?

For a long time now many of us have been productively using Cucumber or one of its variations for Acceptance Test Driven Development (ATDD). The English-written tests are the gates to our applications that keep breaking changes out of the codebase. This approach of bridging the business and development sides is going to continue even after this blog. Yet hardly every Product Owner is actively writing and maintaining these tests on their projects. Only the high-level test results come up anyway when the CI/CD pipeline runs, so developers are often needed to investigate no matter what.

Why does there need to be such a syntactically complex system in place for a group that looks at the feature file only every few months?

Well, good news everyone! There is another way to write integration tests already built into a familiar engineering tool. [Postman](https://www.postman.com/) is touted as a comprehensive platform with a variety of features to make API development and collaboration easier. Many have used Postman in local verification, but its capabilities go further than that. As will be shown, tests are written and integrated with a CLI called [Newman](https://www.postman.com/automated-testing/) to run automatically within a CI/CD pipeline. Soon, one might wonder if Cucumber is necessary on their project and may even choose to forget it altogether.

## Postman's Structure

Readers who have utilized Cucumber in their projects will be familiar with a multitude of configuration, step definition, and feature files that synchronize together for ATDDs. In this alternative, only 2 key file types will be needed:

1. A file with a <b>Collection</b> of API requests containing information on path, params, and body for each. It also will contain the tests for each request. It has a `.postman_collection.json` extension.

This is an example of a Collection:

### T.LY API Postman Collection

![T.LY API Postman Collection](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/10/api_postman_collection.png)

And this is one of its requests:

### T.LY Postman Shorten POST Call

![T.LY Postman Shorten POST Call](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/10/postman_shorten_post_call.png)

2. A configuration file for the <b>environment</b> variables necessary to run a Collection in DEV, QA, PROD, etc. It has a `.postman_environment.json` extension.

This shows a simple environment configuration with variables like `host` that are substituted in the Collection calls (like above):

### T.LY Postman Environment

![T.LY Postman Environment](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/10/postman_environment.png)

## Writing Tests in Postman

Assuming an API Collection has already been created, to start writing its tests in Postman navigate to the <b>Tests</b> tab within any request.

To test a proper `200` response was returned from the API, for example, the following is all that is necessary:

```javascript
pm.test("Status code is 200", function () {
  pm.response.to.have.status(200);
});
```

The screenshot here shows an example of response string parsing:

### T.LY Postman Stats Error GET Call Tests

![T.LY Postman Stats Error GET Call Tests](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/10/postman_stats_error_get_call_tests.png)

Any JavaScript-based functions placed under this <b>Tests</b> tab will be executed in succession. Note that the `"Status code is 200"` or similar string will be the description shown at a high level when running multiple tests. The `pm` object is built into Postman so there is no need to import any dependencies (underneath it uses [the BDD Chai testing framework](https://www.chaijs.com/) to make assertions via chains of `to.have`). The built-in `pm` library may not always be intuitive to use but there is ample documentation and code examples. For a great overview to more writing test basics, the ["Getting started with tests"](https://learning.postman.com/docs/writing-scripts/script-references/test-examples/#getting-started-with-tests) from Postman is an essential resource.

There are quite a few tricks that can be employed when writing a robust Postman test suite. Some of the capabilities include creating broader folder-level tests, equating request params/bodies to response data, setting variables for use in subsequent tests, verifying time stamps, writing scripts that execute before requests to the API, and more. For brevity, this blog will not walk through all of these in-depth.

To further explore test writing concepts, refer to [the Postman docs](https://learning.postman.com/docs/writing-scripts/test-scripts/) or this [working Collection and Test Suite example](https://github.com/matthewreed26/postman-tests-tly). The GitHub repo contains the 2 file types outlined above and can easily be imported into Postman. It has the few happy-path GET and POST calls as well as their error scenarios that are needed to test this public and free service called the [T.LY URL Shortening](https://t.ly/docs/) API.

## Running Tests in Postman

To ensure all the tests are passing against the API, under the Collection's right-pointing arrow that opens the expanded menu hit the blue "Run" button as is shown below.

### How to Run Written Tests

![How to Run Written Tests](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/10/how_to_run_written_tests.png)

Select any of the available options located on the next screen including desired tests for the run order.

### Tests Options Screen

![Tests Options Screen](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/10/tests_options_screen.png)

Hit the blue "Run" button to start running the tests. The outcome should be all green/passed. It is important to note how each of the individual outcomes has a high-level description. These are the same outputs that will be displayed in the pipeline's console later.

### Test Suite Passing

![Test Suite Passing](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/10/test_suite_passing.png)

If the tests fail here, there is a good likelihood that they will not pass in the pipeline either. This is the place to debug/modify any failing tests and can be returned to later on. For more, visit ["Using the Collection Runner"](https://learning.postman.com/docs/running-collections/intro-to-collection-runs/) within the Postman docs.

With all of the tests passing, any changes must be exported/downloaded and pushed to source control. Export the collection via the "..." a.k.a. "View More Actions" in the collection root:

### Exporting the Postman Collection

![Exporting the Postman Collection](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/10/exporting_the_postman_collection.png)

Download the environment via the "Environment Management" pop-up in the upper-right corner of Postman:

### Downloading the Postman Environment

![Downloading the Postman Environment](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/10/downloading_the_postman_environment.png)

## Integrating with a CI/CD Pipeline

This test suite can be integrated with any CI/CD pipeline using a command line Collection Runner called Newman. Newman can be utilized to run and test a Postman Collection directly from the command line which will facilitate the integration with a CI/CD server. For this blog, Jenkins will serve as the CI/CD server.

### Newman Installation

Newman is written in Node.js, make sure [Node.js is already installed](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm) on the Jenkins server. Install Newman globally using NPM, which will allow it to be run it from anywhere:

```bash
npm install -g newman
```

### Add the New Pipeline Test Stage

Assuming that the Postman Collection and Environment files are a structured under a `/tests` directory, [create a new stage in the pipeline](https://blog.ippon.tech/continuous-delivery-with-jenkins-pipeline/) to run the API Test:

```groovy
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

stage('Test API') {
        steps {
            sh 'newman run tests/our.postman_collection.json -e tests/env/test.postman_environment.json'
        }
    }
```

Seen above, invoking the run command of Newman requires the path of a collection and test environment file as parameters.

### Results

The following is an example of a failing set of collection tests. The handy output for categories of iterations, requests, scripts, and assertions makes it easy to deduce what failed, why, and how long it took to run. Here, the failure was due to a request that should have resulted in a `200` response but instead got a `404`.

![Newman result](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/10/newman_result.png)

## Is It Truly That Easy?

Yes! As we seek better continuous-integration and delivery practices, developing automated scenarios should be straightforward for everyone involved. Having the ability to write functional tests within Postman means more engineers will use procedures like BDD/TDD because quick feedback is crucial in these development strategies. Business-minded individuals will celebrate running tests quickly and reliably because it enables the detection of issues as soon as they arise in the codebase. Ultimately, it reduces the overall number of bugs (and their significant costs to fix) in production-level code.

Introducing automation testing to any project has the potential to make its development more complicated. Adding a robust and complex tool like Cucumber into a product's testing strategy too early on can stunt growth. Therefore, it matters quite a lot that a simple plan be adopted when getting started. As has been outlined above, Postman provides a convenient and lightweight structure for anyone to incorporate tests into their processes.

### Sources

* [Cucumber Testing Tool](https://cucumber.io/)
* [Postman + Newman Official Website](https://www.postman.com/automated-testing/)
* [Postman Test Scripts Docs](https://learning.postman.com/docs/writing-scripts/test-scripts/)
* ["Getting Started With Tests"](https://learning.postman.com/docs/writing-scripts/script-references/test-examples/#getting-started-with-tests)
* ["Using the Collection Runner"](https://learning.postman.com/docs/running-collections/intro-to-collection-runs/)
* [The BDD Chai Testing Framework](https://www.chaijs.com/)
* [Working Collection and Test Suite Example](https://github.com/matthewreed26/postman-tests-tly)
* [T.LY URL Shortening Docs](https://t.ly/docs/)
* [Installing Node.js](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm)
* [Creating a Jenkins Pipeline](https://blog.ippon.tech/continuous-delivery-with-jenkins-pipeline/)
* [Newman Further Explained](https://learning.postman.com/docs/running-collections/using-newman-cli/command-line-integration-with-newman/)

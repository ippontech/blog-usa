---
authors:
- John Zaccone
categories:
date: 2014-11-14T17:05:55.000Z
title: "Mocking Dependencies on REST APIs to Speed Up Development"
id: 5a267e57dd54250018d6b5d7
image: 
---

“The service is down.”

In the age of consumer-driven APIs and HTTP REST interfaces, we don’t have to tolerate a dependency on an unresponsive service which impacts development. If we know the interface of the service, we can avoid lost time caused by issues with the API by mocking our dependency.

In addition to isolating our software in development, we would like to automate unit tests to validate our interactions with external dependencies. With a mock we can provide predictable output in which to base these tests.

As these are problems experienced by any software developer working with web services, there is existing software in the open-source community to make mocking these dependencies easier. At Ippon, our team utilizes two tools in particular that have increased our productivity and eliminated frustration caused by flakey dependencies.

**WireMock**

Use [WireMock](http://wiremock.org/) to validate requests and simulate responses. With the fluent Java library, we can make use of this behavior directly in our JUnit tests:

```language-java
@Test
public void exampleTest() {
    stubFor(get(urlEqualTo("/my/resource"))
            .withHeader("Accept", equalTo("text/xml"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "text/xml")
                .withBody("<response>Some content</response>")));

    Result result = myHttpServiceCallingObject.doSomething();

    assertTrue(result.wasSuccessFul());

    verify(postRequestedFor(urlMatching("/my/resource/[a-z0-9]+"))
            .withRequestBody(matching(".*<message>1234</message>.*"))
            .withHeader("Content-Type", notMatching("application/json")));
}

```

Run these during the maven build cycle as part of continuous integration.

At Ippon, we also use WireMock’s standalone instance as a reliable dependency for our front-end development. We keep a simple switch to change easily between a live environment and our mock service. We supply mock responses via JSON configuration files that are automatically mapped when WireMock starts:

```language-javascript
{
    "request": {
        "method": "GET",
        "url": "/api/mytest"
    },
    "response": {
        "status": 200,
        "body": "More content\n"
    }
}
```

In additional to this, WireMock provides other great features:

- Use as a proxy and record responses to save later for offline use
- Improve network robustness by injecting faults
- Manage server lifecycle with JUnit rules

WireMock can be up and running within minutes, with some powerful features to help against external http dependencies. With even less time, a different tool can be setup to help manage service dependencies called Fiddler.

**Fiddler**

[Fiddler](http://www.telerik.com/fiddler) is a free platform agnostic web debugging proxy. By listening to http traffic and printing out the requests/responses, you can debug raw data and find problems quickly. Fiddler comes ready to use out of the box with *zero* configuration- a couple lines of code to configure your client to use fiddler as a proxy and you are good to go. At Ippon, we find Fiddler especially useful as a tool for solving integration issues within a multi-service architecture.

Fiddler and WireMock are just two tools we use to help speed up development by isolating our environments from flakey external dependencies. Post your favorite tools that you like to use in the comments below!

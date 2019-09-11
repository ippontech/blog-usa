---
authors:
- Gerard Gigliotti
tags:
date: 2019-09-10T17:11:00.000Z
title: "Colliding with Quarkus"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/09/quarkus.png
---

Recently at [Voxxed Days Melbourne](https://australia.voxxeddays.com/) there was a Red Hat booth, and they were handing out [Quarkus](https://quarkus.io/) stickers. Obviously if it's good enough to put on a sticker, it must be important. So, what is Quarkus? According to their website, Quarkus is a Cloud Native, (Linux) Container First framework for writing Java applications. It's really a mix of two tightly integrated components: a Java Framework and GraalVM.

![Laptop with Stickers](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/09/quarkusLaptop.png)

## Java Framework
On first impressions, the framework seems very similar to SpringBoot, which isn't a bad thing at all. At its core, it supports all the dependency injection features you could want, based on [this](http://docs.jboss.org/cdi/spec/2.0/cdi-spec.html). From there, Quarkus combines a list of curated extensions to allow you to easily bolt on new functionality as required. If you need to include a REST server, you can include the `quarkus-resteasy` dependency. If you need access to AWS' DynamoDB, there is ` quarkus-amazon-dynamodb`. You don't need to go hunting for dependencies or versions to use; all the important stuff is available for you to pull in via the `listExtensions` command.

## GraalVM
[GraalVM](https://www.graalvm.org/) is a relatively new universal virtual machine for running JVM-based languages, as well as JavaScript, Python, Ruby and R. The feature that Quarkus really embraces is the [SubstrateVM](https://github.com/oracle/graal/tree/master/substratevm), which allows Java applications to be compiled into native executable images. Instead of running your Java code against a JVM, you can compile it down to a portable, native executable. Quarkus abstracts all this complexity from you, and allows you to just run `nativeImage` to build a executable binary.

# Taking it for a spin
So I have put together a [small HTTP server example](https://github.com/ggotti/kanye-quarkus) which basically just proxies requests to the `https://api.kanye.rest` service, which returns a random Kanye West quote. The application has three small classes:
- VoteKanyeResource
- KanyeService
- KanyeQuote

## VoteKanyeResource
The class uses dependency injection to inject the `KanyeService`, which is then invoked in the `quote` method, which is defined as HTTP accessible using the `@GET` annotation. 

```java
package au.com.gigliotti.quarkus;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Path("/quote")
@ApplicationScoped
public class VoteKanyeResource {
    @Inject
    @RestClient
    KanyeService quoteService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> quote() {
        return CompletableFuture.supplyAsync(() -> quoteService.getRandomQuote().getQuote());
    }
}
```

### KanyeService
This service uses the [MicroProfile](https://github.com/eclipse/microprofile-rest-client) REST client. This allows the application to map the REST call onto an interface structure, similar to Spring Data. You'll notice that there is no reference to the HTTP target; that's stored in an `application.properties` file; you don't want to be embedding those kinds of values.

```java
package au.com.gigliotti.quarkus;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
@RegisterRestClient
@Consumes("application/json")
public interface KanyeService {

    @GET
    @ClientHeaderParam(name="User-Agent", value="curl/7.54.0")
    KanyeQuote getRandomQuote();
}
```

### Container Magic
The real super power for Quarkus is the [building native images](https://quarkus.io/guides/building-native-image-guide) feature. All you need to do with this example is to run `gradle buildNative` to compile into a native executable. On MacOSX, Quarkus actually spins up a Docker container in the background to do the compilation, so you don't need to configure GraalVM locally. The compilation on my 2015 MacBook Pro isn't super fast, at approximately 2.5 minutes to compile, but this process is something that you'd only do when you're ready to deploy into your container. 

As a native executable, this application starts in 0.010 seconds, which is blisteringly quick.

# Still In Beta
At the time of writing, Quarkus was at version 0.21.1, so it's still very much in beta. I struggled to get the `Gradle` plugin working with `Kotlin`, so I needed to revert to using standard Java. Speaking of build tooling, the integration works nicely if you're familiar with the Java ecosystem,  but there is still a steep learning curve for newcomers. Saying that, the [documentation](https://quarkus.io/get-started/) is really top notch, as are the [samples](https://github.com/quarkusio/quarkus-quickstarts). 

# Conclusion
You probably want to wait to version 1 before running your production workloads using Quarkus. Saying that, it's refreshing to see a viable alternative to Spring, particularly one with such a focus on developer productivity and cloud deployment options.
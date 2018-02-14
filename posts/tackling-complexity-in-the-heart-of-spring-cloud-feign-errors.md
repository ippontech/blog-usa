---
authors:
- Colin Damon
categories:
- 
date: 
title: "Tackling complexity in the heart of Spring Cloud Feign errors"
image: 
---

Feign, Hystrix, Ribbon, Eureka, such great tools, all nicely packed in Spring Cloud, allowing us to achieve great resilience in our massively distributed applications, with such great ease!!! This is true, at least till the easy part... To be honest, it's easier to get all the great resilience patterns working together with those tools than without, but making everything work as intended needs some studying, time and testing.

Unfortunately (or not) I'm not going to explain how to set all this up here, I'll just point out some tricks on  error management with those tools. I chose this topic because I’ve struggled a lot with this (really)!!! 

If you are looking for a getting started tutorial on those tools I recommend the following articles: 

- [Feign, encore un client HTTP ? (french)](https://blog.ippon.fr/2016/09/21/feign-encore-un-client-http/)
- [The Spring Cloud documentation](http://projects.spring.io/spring-cloud/spring-cloud.html#_spring_cloud_netflix)
- [The source code](https://github.com/spring-cloud/spring-cloud-netflix) because we always end up here...

*There will be code in this article, but not that much, you can find the missing parts [in this repository](https://gitlab.ippon.fr/cdamon/feign-configuration)*
## Dependencies

Let's say, after some troubles, you ended up with a dependency set looking like this one: 

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
 
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-hystrix</artifactId>
</dependency>
 
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-feign</artifactId>
</dependency>
 
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-ribbon</artifactId>
</dependency>
 
<dependency>
  <groupId>org.springframework.retry</groupId>
  <artifactId>spring-retry</artifactId>
</dependency>
```

Ok, so you are aiming at the full set: 

- Of course, you are going to use `Eureka client` to get your service instances from your `Eureka server`
- So `Ribbon` can provide proper client-side load-balancer using service names and not URLs (and decorate `RestTemplate` to use names and load-balancing)
- Then comes `Hystrix` with lots of built-in anti-fragile patterns, another awesome tool but you need to keep an eye on it (not part of this article...)
- Finally, everything is packed up by `Feign` for really easy-to-write rest clients

I wrote this article using the following versions: 

```xml
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>1.5.8.RELEASE</version>
</parent>
 
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-dependencies</artifactId>
      <version>Dalston.SR4</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```
## Configuration

Ok, those tools need configuration, let's say you ended up with something like this in your `application.yml`: 

```yml
spring: 
  application:
    name: my-awesome-app
    
eureka:
  client:
    serviceUrl: 
      defaultZone: http://my-eureka-instance:port/eureka/
 
feign:
  hystrix:
    enabled: true
 
hystrix: 
  threadpool: 
    default: 
      coreSize: 15
  command: 
    default: 
      execution: 
        isolation: 
          strategy: THREAD
          thread: 
            timeoutInMilliseconds: 2000
 
ribbon: 
  ReadTimeout: 400
  ConnectTimeout: 100
  OkToRetryOnAllOperations: true
  MaxAutoRetries: 1
  MaxAutoRetriesNextServer: 1
```

Ok, this configuration will work if your application can register with the registered hostname and the Spring Boot application port. For production / cloud / any environment with proxies you have another one with: 

- `eureka.instance.hostname` with the real hostname to use to reach your service
- `eureka.instance.nonSecurePort` with the non-secure-port to use or `eureka.instance.securePort` with `eureka.instance.securePortEnabled=true`

Also this configuration isn't authenticated, this can be a good idea to add authentication to `Eureka`, depending on your network.

Then I see you have confidence in your Web Services, 400ms for a ReadTimeout is quite short, the shorter the better! I also notice that all your services are idempotent because you accept to have 4 calls instead of 1 if your network / servers starts to get messy (yes, this `Ribbon` configuration will make 4 calls if the response takes too long because it is doing ( 1 + MaxAutoRetries ) x ( 1 + MaxAutoRetriesNextServer), so if you set 2 and 3 you’ll have up to 12 call only from `Ribbon`). 

This gets us to the 2 000ms hystrix timeout, a shorter value will result in calls being done without the application waiting for the result so this seems legit (due to ribbon configuration : (400 + 100) * 4).

## Customization

Everything goes well, you quickly understand that, for all `FeignClient` without fallback you only get `HystrixRuntimeException` for any error. This exception is mainly saying that something went bad and you don't have a fallback but the cause can tell you a little bit more. You quickly build an `ExceptionHandler` to display neater messages to users (because you don't want to put fallbacks on all `FeignClient`).

One day you call a new external service and this service can have normal responses with HTTP 404 for some resources, so you add `decode404 = true` to your `@FeignClient` to get a response and avoid circuit breaking on those (if this option isn’t set a 404 will be counted for circuit breaking). But you don't get responses, what you get is a 

```
...
Caused by: feign.codec.DecodeException: Could not extract response: no suitable HttpMessageConverter found for response type [class ...
...
```

This is because the 404 from this service have a different form than "normal" responses (can be a simple String saying that the resource wasn't found). A cool idea here would be to allow `Optional<?>` and `ResponseEntity<?>` types in `FeignClient` to get an empty body for those 404. 

*AutoConfigured Spring Cloud Feign can map to `ResponseEntity<?>` but will fail deserialization in incompatible objects. It can't by default put results in `Optional<?>` so it's still a cool feature to implement*

One way to achieve this is to define a `Decoder` that can look like this: 

```java
package fr.ippon.feign;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.DecodeException;
import feign.codec.Decoder;

public class NotFoundAwareDecoder implements Decoder {

  private final Decoder delegate;

  public NotFoundAwareDecoder(Decoder delegate) {
    Assert.notNull(delegate, "Can't build this decoder with a null delegated decoder");

    this.delegate = delegate;
  }

  @Override
  public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
    if (!(type instanceof ParameterizedType)) {
      return delegate.decode(response, type);
    }

    if (isParameterizedTypeOf(type, Optional.class)) {
      return decodeOptional(response, type);
    }

    if (isParameterizedTypeOf(type, ResponseEntity.class)) {
      return decodeResponseEntity(response, type);
    }

    return delegate.decode(response, type);
  }

  private boolean isParameterizedTypeOf(Type type, Class<?> clazz) {
    ParameterizedType parameterizedType = (ParameterizedType) type;

    return parameterizedType.getRawType().equals(clazz);
  }

  private Object decodeOptional(Response response, Type type) throws IOException {
    if (response.status() == 404) {
      return Optional.empty();
    }

    Type enclosedType = Util.resolveLastTypeParameter(type, Optional.class);
    Object decodedValue = delegate.decode(response, enclosedType);

    if (decodedValue == null) {
      return Optional.empty();
    }

    return Optional.of(decodedValue);
  }

  private Object decodeResponseEntity(Response response, Type type) throws IOException {
    if (response.status() == 404) {
      return ResponseEntity.notFound().build();
    }

    return delegate.decode(response, type);
  }
}
```

Then, a `@Configuration` file: 

```java
package fr.ippon.feign;
 
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.support.ResponseEntityDecoder;
import org.springframework.cloud.netflix.feign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
 
import feign.codec.Decoder;
 
@Configuration
@EnableCircuitBreaker
@EnableDiscoveryClient
public class FeignConfiguration {
 
  @Bean
  public Decoder notFoundAwareDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
    return new NotFoundAwareDecoder(new ResponseEntityDecoder(new SpringDecoder(messageConverters)));
  }
  
}
```

Of course it's up to you to fit it to your exact needs, but this way you'll be able to get proper responses. 
# Integration testing

All this really cool stuff can change from one minor version to another (ex : [Hystrix enabled by default to Hystrix disabled by default](https://github.com/spring-cloud/spring-cloud-netflix/commit/54c052c5eb9e1ec87428e267ecaac9fdbb5a9388#diff-177f70f71e8d0a77b90c2b31b4255ec4)) so unless you aren't missing any update (I don't think it's possible) I strongly recommend adding good integrations tests for this stack usage (because Unit Tests won't be of any help here).  

But, integration testing this is all but easy, if we want to be as close as possible to reality we need: 

- A running `Eureka` instance
- A running service registered on `Eureka`
- A running client using this service

One way to do this is to set up a dynamic test environment with `Eureka` and some applications but, depending on your organization, this can be really hard to achieve. Another way is to start all this in a Single JVM managed by JUnit thus integration with any build tool and CI platform will be really easy. 

*The drawback of this can be strange behaviors due to Spring auto-configuration mechanism, it’s up to you too chose to make it in containers or this way, depending on what you can do.* 

To achieve this we'll need to solve: 

- The fact that we can't use the native SpringTest stuff because it can only manage one app. Ok, let's start applications simply using `SpringApplication.run(...)` and play with the resulting `ConfigurableApplicationContext`, easy.
- The need to start on available ports. Simply add `--server.port` in `SpringApplication.run(...)` with `SocketUtils.findAvailableTcpPort()`, not even a problem.
- The impossibility to use any kind of default configuration path unless we want all our apps to get this configuration. This one is also easy, just add `--spring.config.location` with a specific configuration in our `SpringApplication.run(...)` and we can have separate configurations.
- The need for our applications to have configurations depending on the `Eureka` server port. For this one we'll need to ensure that `Eureka` is the first one to start (not needed for production, our client can handle this very well but will be annoying for tests) and then give the `Eureka` port one way or another to the other applications. 
- The fact that we can't, by default, start multiple Spring Boot applications on the same JVM instance because of JMX mbean name. Let’s disable it using "--spring.jmx.enabled=false" (or change the default domain using `--spring.jmx.default-domain` with a different name) and we are OK.
- Finally, a strange one, you know that Spring Cloud tools uses `Archaius` to manage their configuration, not the default Spring configuration system. `Archaius` takes Spring Boot configuration into account when the first application starts on the JVM, for the next one they aren't taken into account at the moment I'm writing this (check [ArchaiusAutoConfiguration.configureArchaius(...)](https://github.com/spring-cloud/spring-cloud-netflix/blob/master/spring-cloud-netflix-archaius/src/main/java/org/springframework/cloud/netflix/archaius/ArchaiusAutoConfiguration.java) there is a static `AtomicBoolean` used to ensure that the configuration isn't loaded twice and "else" there is a TODO and a warn log). For our tests need we'll go for an ugly fix for this, reloading this configuration in an `ApplicationListener<ApplicationReadyEvent>` will do the trick.

[I have done this here](https://gitlab.ippon.fr/cdamon/feign-configuration) using mainly JUnitRules to handle the applications parts, feel free to take it if you like it and adapt those tests to your needs.

At the time of this writing, the project takes ~45sec to build, which is pretty terrible considering that most of this time is for integration tests on an already battle tested code... but I really don’t want to miss a breaking change in my usages of this great stack so I consider this time to be fair enough. 

*If you don’t need it remove the part testing circuit breaking on all http error codes since those tests are very slow due to the sleeping phase…*

**Once again, really take the time to make strong integration tests on your usages of this stack to avoid really bad surprises after some months!!!**
# Going further

Depending on what you want to build, what we have here can be more than enough on application side but if you are planning to use this in the real world, you really need some good metrics and alerts (at least to keep an eye on your fallbacks and circuit breakers openings). 

For this you can check [Hystrix dashboard](https://github.com/Netflix/Hystrix/wiki/Dashboard) and [Turbine](https://github.com/Netflix/Turbine/wiki) to provide you lots of useful metrics to get dashboards with lots of those: 

![Turbine dashboard](https://github.com/Netflix/Hystrix/wiki/images/dashboard-annoted-circuit-640.png)

You'll then need to bind it to your alerting system, this will need some work and you are going to need to handle LOTS of data since those tools are really verbose (if you want to persist those data pay attention to your eviction strategy and choose a solid enough timeseries infrastructure). Depending on your needs and organization tools a simple [metrics Counter](http://metrics.dropwizard.io/3.2.3/getting-started.html) on your fallbacks can do a good job. Once setted up in your applications this will only need an `@Counted(...)` on your fallbacks methods.

It is also possible that the few tools discussed here aren't antifragile enough for your needs, in that case you can start by checking: 

- [Hystrix configurations](https://github.com/Netflix/Hystrix/wiki/Configuration) you'll see that there are plenty of things you can do (playing with circuit breakers configuration can really help in some cases). Don't forget to add integration tests to ensure that the configuration you are adding is really behaving as expected.
- Feign retries: I totally skipped this part but there is a built in retry mechanism in Spring Cloud Feign coming on top of Hystrix and Ribbon mechanisms. You can check `Retryer.Default` to see the default retries strategy but this is kind of misleading in two ways: 
  - First: if you have Hystrix Feign enabled the default retrier is `Retryer.NEVER_RETRY` (check [FeignClientsConfiguration.feignRetryer()](https://github.com/spring-cloud/spring-cloud-netflix/blob/master/spring-cloud-netflix-core/src/main/java/org/springframework/cloud/netflix/feign/FeignClientsConfiguration.java))
  - Second : even if you define a `Retryer` Bean to `Retryer.Default` you won't get feign levels retries by default because it's also important to check `ErrorDecoder.Default` to see that we have a `RetryableException` only when there is a well formatted date in the `Retry-After` HTTP header. 
So if you want to play with this you'll need to :
  - define an `ErrorDecoder` that ends up in `RetryableException` in the cases you want (or add the `Retry-After` header in your services).
  - change the `Retryer` to one actually retrying.
  - probably redefine the `Feign.Builder` Bean (be careful to keep the `@Scope("prototype")`) to suit your needs.
  
# So, do we go live ? 

This stack really is great and every developer using it daily will enjoy it, at least after one guy in the team spend days settings all this up to check some “vital” points :
 
- avoid retries on POST, PATCH and any non-idempotent services ([should follow this](https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html))
- ensure that fallback calls and opened circuit breakers are tracked and explained
- ensure that `Eureka` is secured and not a SPOF (even without an `Eureka` up and running the apps can talk to each others, at least for a fair amount of time)
- ensure that some minor version change will not silently break all this anti-fragile stuff with strong integration tests

IMHO this really great stack needs work and understanding so use it if you need it, otherwise stick to `RestTemplate` until you have time to give it a good try!

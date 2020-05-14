---
authors:
- Sébastien Mazade
tags:
- Feedback
- Keycloak
- IAM
- Security
- Cloud
- Amazon ECS
- Gatling
date: 2020-05-14T14:00:00.000Z
title: "FEEDBACK: Keycloak High Availability in Cloud environment (AWS) - PART 3/4"
image: https://raw.githubusercontent.com/smazade/blog-usa/master/images/2020/05/feedback-keycloak-high-availability-in-cloud-environment-aws-part-3-img1.jpg
---

In [previous part](https://blog.ippon.tech/feedback-keycloak-high-availability-in-cloud-environment-aws-part-2-4/), we have explained our context and how to configure our infrastructure and Keycloak server. From now on, we will focus on the load test environment set up, in order to be able to launch the tests at the destination of our cloud Keycloak cluster and interpret them.

# How to ensure our HA configuration is reliable and effective?

Once your infrastructure is ready to host our HA Keycloak servers, it is necessary to check a couple of things (even if we are dealing with an IAM, security concerns are out-of-scope here):
*   Memory consumption:

    Bad cache configuration or too low instance types, can lead to memory leaks or overrun the JVM garbage collection that would impact performance.
    
 > <img style="float: left;padding-right: 0.7em;width: 2.2em;" alt="Information" src="https://raw.githubusercontent.com/smazade/blog-usa/master/images/2020/05/2139.png"> Note that in AWS this metric is absent, it is mandatory to add an agent into the EC2 instance.

*   CPU usage:

    In Keycloak, we have seen that CPU is the first thing to monitor. It is interesting to see how the CPU reacts to the load and also unloads.

*   Response time:

    We need to control that elapsed time for each type of request (important to distinguish the different types) remains nearly the same. We will mainly keep an eye on login requests as they are the firsts to be called and the heaviest.

*   Number of errors:

    IAM session availability is not of the highest criticity, losing some could be annoying but it will only force the customer to login again, this would not really impact the business. I think it is more important to prefer performance to errors here.

*   Caches are well ‘sharded’ between nodes:

    We could prove as an example that sessions are not closed when scaling-in. We can benefit from our load tests to verify that we do not have request errors on refreshed sessions during the scale-in.

*   ...

## Significative load testing requires many Gatling clients

It is necessary to find the best suited tool that will be able to inject a relatively big amount of requests: the main open source ones that do not need to prove themselves are JMeter, The grinder and Gatling.

We will not do a comparison here, each of them have their cons and pros. We have picked Gatling for 3 reasons:
*   its community and we are used to it,
*   beautiful reports,
*   ability to scale many clients without purchasing the distributed mode enterprise offer (the main criteria).

Indeed, while setting up your test and testing it locally, you would not encounter any problem, but when you will begin to add more and more users (i.e. requests) you will soon reach a limit with sockets for opening files such as css/js inferred resources and with the number of users. Thus, we need to use several clients.

Few people know that it is possible to launch Gatling with a specific mode that will write only logs without reports. When it is over, it is possible to concatenate them in a merged report.

If we take a look to the Gatling command usage:

```
Usage: gatling [options]

 -h, --help               Show help (this message) and exit
 -nr, --no-reports        Runs simulation but does not generate reports
 -ro, --reports-only <directoryName>
                          Generates the reports for the simulation in <directoryName>
 -rsf, --resources-folder <directoryPath>
                          Uses <directoryPath> as the absolute path of the directory where resources are stored
 -rf, --results-folder <directoryPath>
                          Uses <directoryPath> as the absolute path of the directory where results are stored
 -sf, --simulations-folder <directoryPath>
                          Uses <directoryPath> to discover simulations that could be run
 -bf, --binaries-folder <directoryPath>
                          Uses <directoryPath> as the absolute path of the directory where Gatling should produce compiled binaries
 -s, --simulation <className>
                          Runs <className> simulation
 -rd, --run-description <description>
                          A short <description> of the run to include in the report
```

You would notice the `-nr` option that prevents report generation, and the `-ro` that generates only the reports.

Thus, we will use for example the following kind of commands:
*   For running the test with ‘n’ Gatling clients:

    ```shell
    gatling -rf $RESULT_CONTAINER_DIR/$(uuid) -rd '$DESC ### $JAVA_OPTS' -s keycloak.ha.ScalabilityRecordedSimulation -nr
    ```

    _where:_

1. `$RESULT_CONTAINER_DIR` represent the name of a repository which will be the same for every client that are related to the same run,
2. `$(uuid)` generate a unique sub repository to avoid writing the same file,
3. `$DESC` is the description of the scenario and of what we want to test (for instance: T3A.small - shard 2 - start 3 EC2 - upscale 39p - no affinity etc…), try to be exhaustive here it will be mandatory to understand well all the context of the test later,
4. the `$JAVA_OPTS` will contain all the parameters used by your Scala script if any (for instance you can parameterize the number of users, duration, protocols, domain name, pause duration etc.)
*   For the reporting command that will generate our report index.html:

    ```shell
    gatling -ro $RESULT_CONTAINER_DIR
    ```

## Scaling Gatling clients with AWS ECS

The use of containers was prohibited in our context on production environments, but nothing keeps us from using it for our tests, even if it will be deployed on our AWS VPCs.

The easiest way to scale our containers that contains the Gatling client, was to use the orchestrator managed service supplied by AWS: ECS (Elastic Container Service).

> <img style="float: left;padding-right: 0.7em;width: 2.2em;" alt="Pushpin" src="https://raw.githubusercontent.com/smazade/blog-usa/master/images/2020/05/1f4cc.png">  The ECS configuration would not be provided here, as there are no real difficulties with all the resources that you will find on the Internet.

Here is the load test architecture diagram:

![gatling ecs architecture](https://raw.githubusercontent.com/smazade/blog-usa/master/images/2020/05/feedback-keycloak-high-availability-in-cloud-environment-aws-part-3-img2.png)

This diagram melt applicative and infrastructure notions but this way, it is easier to describe the overall picture of what is at stake.

Before entering into the details of the Gatling scenario, it is still interesting to notice that the same ALB (our only Keycloak public door) will be requested using 2 different domain aliases:
*   the one used for getting access to the console, is defined onto our company DNS server,
*   the internal one, the route53 alias (we could have used the public frontal one but we should have opened an additional port and requested internet access to reach it from our ECS VPC and everyone knows the difficulty in big companies to make it pass the security team agreement).

Why these 2 domains? 
*   The first one (Domain A) will only be used for the requests done without cookies (otherwise, the affinity session cookie would bias our tests results). In our case, it concerns only the `/userinfo` endpoint requests. It would have been too complex for our tests, to add another proxy simulating the API management layer (or as well any backend security checking layer) and as well, we did not want to measure this layer overheads.
*   The second one (Domain B) is used for all requests that the browser will execute.

<img style="float: left;padding-right: 0.7em;width: 2.2em;" alt="Warning" src="https://raw.githubusercontent.com/smazade/blog-usa/master/images/2020/05/26a0.png">  The generated files are stored into S3, thus, do not forget to set the required AWS permissions policy to the container:
```json
{
 "Version": "2012-10-17",
 "Statement": [
   {
       "Sid": "gatling-compartiment-policy-ecs",
       "Effect": "Allow",
       "Principal": {
           "AWS": "arn:aws:iam::XXXXXXXX:role/ecsTaskExecutionRoleGatlingS3"
       },
       "Action": "s3:*",
       "Resource": [
           "arn:aws:s3:::gatling-perf-test",
           "arn:aws:s3:::gatling-perf-test/*"
       ]
   }
 ]
}
```

## Preparation is the key: the least details can bias the interpretations...

On previous lines, we have seen that we have highly complexified the tests, just to be able to remove the affinity session cookie on the single `/userinfo` endpoint. If this had not been done, the results would not have represented exactly the reality, and we would not have been able to witness which behavior Keycloak would have had when the cluster grows and when requests would have fallen onto the wrong node.

Thus, you should not simply focus on the endpoints you will interrogate, but on the whole final architecture. Be careful, it does not mean that we need to have a full test scope with the true backend APIs etc. Here, the goal remains to test Keycloak, not the network nor the global DNS calls etc.

For this we have thought about a scenario that will reproduce all of our OAuth grant type steps which was an IFramed Implicit Grant (I see you coming, another article will soon talk about this choice), that includes login, silent refreshes and token checks (`/userinfo`). The results with an authorization code grant type scenario would probably not have been very far.

Then we have extrapolated the injectors parameters to go a little above the expected volumes.

Here are the different steps:
 > * For each user in a random feeder ({random number}_{default mail})
 >   * Wait a random duration (pause) *-- in order to spread the logins*
 >   * Do an ***authentication***:
 >     1. call **navToLogin**: do a GET on `/openid-connect/auth` to get the login page and retrieve the login form action
 >     2. call **login**: provide mail/pwd and do a POST on `/login-actions/authenticate` (dynamically retrieved from the previous form) of the OIDC endpoint. Retrieve the *access_token* and *expires_in* in the *Location* header response.
 >   * put the token in the session for all following requests with the  *authorization* Bearer header
 >   * Calculate a random number of silent refresh loop
 >   * ***For each silent refresh*** loop
 >     * ***While session is not expired***, at a parameterized rate:
 >         * call **userinfo**: request that will retrieve user information on a specific endpoint host if configuration has specified a different one from the others requests.
 >     * call **silentrefresh**: execute a `/openid-connect/auth` with prompt=none url argument to make a silent refresh thanks to the login cookie stored in gatling thread. Retrieve the *access_token* and update the session with the new token.
 >   *  call **logout**: `/openid-connect/logout` endpoint X% of the time

You may have noticed some tiny details that have their importance here:
*   We start with a customer feeder -- using only one user would have also worked with Keycloak as it separates the user data from his context session (i.e. you can login many times with the same user). The problem is that you would not see the memory consumption if the target is to have more than 100K users. Additionally, it can show others things that we do not have in mind yet.
*   We retrieve the `expires_in` duration in order to call the silent refresh just before the token will expire to avoid unnecessary `401` (of course the real browser app should have the same behavior).
*   Random number of silent refresh loop: this will simulate different scenario durations.
*   Call logout X% of the time: in reality most users don’t call the logout endpoint (on e-commerce sites it is very seldom, on company internal tools it is more often)...

We do not see it here, but it is also important to respect realistic pause durations, right number of `/userinfo` endpoint calls (i. e. each time a backend API call is made _-- it can be substantial_) etc.

Then, you may not always be able to simulate every whit of the reality, but you should be aware that some weird behaviors can happen, example:

_“Our scenarios begins with 2 login step requests (the second one is costly), if the load test increase too much the CPU, some responses will fail (even the tiny ones like `/userinfo`), thus, the user scenario will stop and if you use a constant rate injector like us, as soon as the user ends, it will be replaced with another one which will start again from the login steps.”_

You could easily imagine that the logins spread over time and the other requests, will soon be replaced by only login requests that will increase exponentially the load and I cannot tell which of the Keycloak server or the Gatling client will crash first!

So it is mandatory to not start another scenario without a significant pause. In real life, if all the users are disconnected, they will try to login again 2 or 3 times and then wait many minutes before trying another attempt, so, always try to think of what will probably happen in real life and try to reproduce it.

One thing left to handle and that is not natively possible with Gatling, is to suppress all client request cookies of the Domain A.

As the session is unique for the Gatling scenario that calls the 2 domains, the trick is to save all the cookies and then restore all of them, except the ones on domain A before executing the call.

Here is a little helping Scala code for this:

```scala
   val KC_COOKIES_PATH = "/auth/realms/" + REALM_NAME + "/"
   // Put here all cookies used by keycloak
   val COOKIE_LIST_TO_KEEP = List("AWSALB", "AWSALBCORS", "AUTH_SESSION_ID", "KEYCLOAK_SESSION", "KEYCLOAK_LOCALE",
                   "KEYCLOAK_IDENTITY", "KEYCLOAK_SESSION_LEGACY", "KEYCLOAK_IDENTITY_LEGACY")

   def saveCookieInSessionIfExists(cookieNames: List[String], host: String, path: String, secure: Boolean): io.gatling.core.structure.ChainBuilder = {
       var chain = exec()
       for (cookieName <- cookieNames) {
           chain = chain
           .exec(session => session.set(cookieName, ""))
           .doIf(session => session("gatling.http.cookies").as[String] contains "(" + cookieName + "=" ) { // if cookie name can be found into cookies
               exec(getCookieValue(CookieKey(cookieName).withDomain(host).withPath(path).withSecure(secure).saveAs(cookieName)))
           }
       }
       chain
   }

   def restoreCookieInSession(cookieNames: List[String], host: String, path: String): io.gatling.core.structure.ChainBuilder = {
       var chain = exec()
       for (cookieName <- cookieNames) {
           chain = chain
           .exec(addCookie(Cookie(cookieName, "${" + cookieName + "}").withDomain(host).withPath(path)))
       }
       chain
   }
```

Then in our scenario:
```scala
   val scnScaleOnLoginsAndSilentRefresh = scenario("ScalabilityRecordedSimulation")
       …
    .exec(saveCookieInSessionIfExists(COOKIE_LIST_TO_KEEP, HOST, KC_COOKIES_PATH, true))
    …
    // delete all cookies: we use the native gatling flushSessionCookies()
    .exec(flushSessionCookies)
    // restore all cookies except the ones on domains different from HOST (ie skip /userinfo requests cookies)     
    .exec(restoreCookieInSession(COOKIE_LIST_TO_KEEP, HOST, KC_COOKIES_PATH))
    .exec(callToUserInfo)
```

Now that you are aware of what and how to launch the load tests with Gatling using an orchestrator, we will gather the results and strive to analyze them in the [last part](https://blog.ippon.tech/feedback-keycloak-high-availability-in-cloud-environment-aws-part-4-4/) of our article.
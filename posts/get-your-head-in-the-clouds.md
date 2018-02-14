---
authors:
- Malcolm Thirus
categories:
- JHipster
- Cloud
date: 2017-07-06T18:04:33.000Z
title: "Get Your Head in the Clouds"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/06/Get-Your-Head-in-the-Clouds-Blog--1-.png
---

There is a massive shift in mentality surrounding deployment and system maintenance. With cloud services handling the bulk of our DevOps for a fraction of the cost, companies are moving whole environments into the Cloud. But just how far can we take this? In the Amazon Webservices EcoSystem, we have the opportunity to build everything we need on cheap infrastructure using EC2. However, we're still responsible for maintaining that EC2 instance. Can we do better?  Let's see how [JHipster](https://jhipster.github.io/) handles  serverless solutions and alternative deployment methods.

### Elastic Beanstalk
Elastic Beanstalk is less of a replacement to managing the servers themselves and more of a resource deployment manager that is meant to handle the orchestration of your machines. 

You can determine ahead of time the resources necessary for your environment and allow Amazon to orchestrate the deployment. Behind the Elastic Beanstalk configuration parameters and version numbers is a couple EC2 and RDS machines. You have the ability to go into each service and do whatever you want to each of them. Elastic Beanstalk just makes it easier to coordinate the deployment of each resource. 

Pushing JHipster up using Elastic Beanstalk was relatively simple. There were only two bits that got me.

1. When using Nginx, you have to make sure you are deploying to port 5000. That's the default port it is listening to and acting as proxy for.
2. The exact address of your database isn't created until you request it. If you want the address rather than Environment Variables, you'll have to do an initial Dummy Release.

### Static Code on S3
My first instinct was actually to separate my front-end code from my back-end by deploying my html and javascript to Amazon's Simple Storage Service. S3 provides a [static website hosting](http://docs.aws.amazon.com/AmazonS3/latest/dev/WebsiteHosting.html) solution that makes sense for my use case. Setting up the S3 bucket is painless; the properties tab of the bucket contains an option to enable website hosting. Deploying was just a matter of whipping up a quick gulpfile.

```javascript
var gulp       = require('gulp'),
    awspublish = require('gulp-awspublish');

gulp.task('publish-s3', function() {
  var publisher = awspublish.create(
    {params: { Bucket: '****'}},
    {cacheFileName: '.cache'});
  var headers = {'Cache-Control': 'max-age=315360000, no-transform, private'};

  return gulp.src('target/www/**/*')
    .pipe(publisher.publish(headers))
    .pipe(publisher.cache())
    .pipe(awspublish.reporter());
});
```
Now `gulp publish-s3` will push my code up to S3, making it immediately available for the next http request. That simple!

Well, not exactly. A couple issues needed to be resolved before JHipster would play nice with a static file host.

The beautiful thing about Angular and other Model, View, Controller frameworks is that the pages are all built off of templates. The downside is that a static website host is looking for an html file or javascript file, and there just isn't one. To workaround this, I added this XML to the redirection rules of my S3 bucket:
```xml
<RoutingRules>
    <RoutingRule>
        <Condition>
            <HttpErrorCodeReturnedEquals>404</HttpErrorCodeReturnedEquals>
        </Condition>
        <Redirect>
            <HostName>%MY_BUCKET_ENDPOINT%</HostName>
            <ReplaceKeyPrefixWith>#!/</ReplaceKeyPrefixWith>
        </Redirect>
    </RoutingRule>
</RoutingRules>
```
This way, every 404 will reroute back to index.html at the root of the bucket and will be handled by Angular properly. While this works from a user perspective, Search Engines recognize the 404 and do not cache it. For an internal website, that may be acceptable, but larger public websites may want to be wary.

I also needed to remove everywhere my JHipster application specified `{ useHash: true }`. By default, JHipster prepends a `#/` before every path and we need it to not do that, such that index.html can be found. 

The code I used to remove the hash from all components was
```bash
for i in $(grep -iRl ', { useHash: true }' *); do sed -e 's/, { useHash: true }//g' -i .old $i; done
```

### Back-End Code
Here's where the tricky parts come in and preferences reign supreme.

I began this adventure combining AWS Lambda with AWS API Gateway. In theory, this is the cheapest and most efficient way to monitor your resources and control access to each endpoint. You are only charged for usage and storing the code. In practice, testing and deployment quickly became an issue.

Each Lambda function is meant to be stand-alone from each other and JHipster (which is built with Spring) is centered around one Service class providing multiple REST endpoints. Separating out each individual method call in a Service for both testing and deployment is not trivial. Lambda + API Gateway may be useful for microservices or a project that begins with AWS in mind, but JHipster was not meant for that.

A much more viable solution is the Elastic Beanstalk mentioned above. I can deploy my packaged jar in the appropriate JRE, spin up a PostgreSQL instance in RDS, and create any other resources I may need with one configuration file.

For this to work, you must allow cross origin resource sharing (CORS) between the instance that Elastic Beanstalk set-up and the url that S3 is associated with.

### Pricing and Benefits
With all of that work done, what have we accomplished?

* Our front-end is being served by S3. We are being charged for the storage of the html, javascript, and assets at $0.090 per GB of data out. Seeing as your html and javascript is negligible, your main cost is site activity and whether your site also serves some sort of media files. Any large images or audio files will become an issue, but for a JHipster app only displaying information from the database or text the cost should be minimal.

* Our back-end is being deployed by Elastic Beanstalk. Beanstalk handles the server configuration, spins up your database, and provisions any other resources you may need. Everything created is available under its respective service, so any alterations or inspections that need to be made can happen.

* The database sits in RDS, created during the Elastic Beanstalk deployment.

What have we sacrificed? The main issue I would caution against is Amazon's lack of transparency in their resources. Discovering what happened if S3, EC2, RDS, or whatever other service you are using goes down is quite difficult. If a deployment fails, you aren't given much information as to why (though a failed deployment is very rare). That's the nature of depending on the Cloud, however; you are entrusting the availability of your service to the availability of AWS. Easily avoidable with a better Support Package and a dedicated Cloud Specialist.

For the future, I'd like to build my microservices in AWS Lambda pointing to a database with my front-end pointing to API Gateway. I believe that is the most Cloud-oriented solution available right now. 

If you have any questions about Ippon or what you read in this article, we'd love to hear from you! Please send your comments and inquiries to [contact@ippon.tech](mailto:contact@ippon.tech).

---

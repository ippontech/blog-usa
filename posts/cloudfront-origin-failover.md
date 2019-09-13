---
authors:
- Matt Reed
tags:
- AWS
- CloudFront
- Origin Groups
date: 2019-5-07T00:00:00.000Z
title: "When A CloudFront Origin Must Fail for High Availability"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/03/mobbing_guidelines.png
---

Amazon's [CloudFront service](https://aws.amazon.com/cloudfront/) does its best to speed up ***content delivery*** by caching frequently accessed files and it is obvious the service does a superb job of this by attracting an extensive [list of customers](https://aws.amazon.com/cloudfront/case-studies/). But what happens when you turn on the prescribed failover solution and its seemingly simple implementation on your project mysteriously causes more content delivery issues under the hood? Get ready for a deeper dive into CloudFront's caching strategies and how they talk to the associated Lambda@Edge Functions.

# Proper Failover

In the event of a cache miss at an edge location, CloudFront goes to retrieve the file and replenish the cache. So then how does it work if the origin is unavailable at the time CloudFront goes looking for it? Fortunately, [support for Origin Failover](https://aws.amazon.com/about-aws/whats-new/2018/11/amazon-cloudfront-announces-support-for-origin-failover/) was introduced this past November 2018, which was another important advancement in making AWS-based web applications highly available. Origin Failover is achievable through the ability to specify a primary and a secondary origin into what is called an [Origin Group](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/high_availability_origin_failover.html). When CloudFront is unsuccessful in connecting to the primary origin, an ***error status code*** is returned which prompts the failover action. CloudFront will then attempt the same request with the secondary origin. This is customizable in that any combination of the following status codes can be selected: 500, 502, 503, 504, 404, or 403.
![Origin Failover on cache miss](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/images/origingroups-overview.png)

# Life On The Edge

CloudFront also allows for one or more Lambda@Edge Functions to be executed in between fetches. Why Lambda@Edge Functions? For those familiar with the more common Serverless Lambda Functions, the basic premise of Lambda@Edge Functions is similar however they are run on [much less computationally capable](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/cloudfront-limits.html#limits-lambda-at-edge) servers in more diverse locations. This is so that they exist closer to the end user to decrease load time on their side. What Lambda@Edge Functions primarily are purposed for is minor customizations of the fetched ***content*** during ***delivery***. Amazon docs even show [quite a few example functions for popular topics](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/lambda-examples.html) such as:

* A/B Testing
* Overriding a Response Header
* Serving Static Content
* Redirecting Viewer Requests to a Country-Specific URL
* Using an Origin-Response Trigger to Update the ***Error Status Code*** to 200-OK

# Failover The Edge



# The Error Status Code Complication

Based on our knowledge of CloudFront's Origin Failover based on ***error status code***, we are going to focus on the last, listed topic of "Using an Origin-Response Trigger to Update the ***Error Status Code*** to 200-OK". Let's look at the code:

    exports.handler = (event, context, callback) => {
        const response = event.Records[0].cf.response;

        /**
        * This function updates the response status to 200 and generates static
        * body content to return to the viewer in the following scenario:
        * 1. The function is triggered in an origin response
        * 2. The response status from the origin server is an error status code (4xx or 5xx)
        */

        if (response.status >= 400 && response.status <= 599) {
            response.status = 200;
            response.statusDescription = 'OK';
            response.body = 'Body generation example';
        }

        callback(null, response);
    };

<u>Sources</u>:

* [CloudFront](https://aws.amazon.com/cloudfront/)
* [CloudFront Case Studies](https://aws.amazon.com/cloudfront/case-studies/)
* [CloudFront Origin](https://docs.aws.amazon.com/en_pv/cloudfront/latest/APIReference/API_Origin.html)
* [Origin Group Failover](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/high_availability_origin_failover.html)
* [How CloudFront retrieves files and replenishes the cache](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/HowCloudFrontWorks.html)
* [Making AWS-based web applications highly available](https://dzone.com/articles/designing-web-apps-for-high-availability-in-aws)
* [Triggering Lambda@Edge Functions execution in CloudFront](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/lambda-edge-add-triggers.html)
* [Lambda@Edge Limits](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/cloudfront-limits.html#limits-lambda-at-edge)
* [Only semi-recently was support introduced for writing Lambda@Edge functions in Node.js v8.10](https://aws.amazon.com/about-aws/whats-new/2018/05/lambda-at-edge-adds-support-for-node-js-v8-10/)
* [Lambda@Edge Example Functions for Popular Topics](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/lambda-examples.html)
---
authors:
- Matt Reed
tags:
- AWS
- CloudFront
- Origin Groups
date: 2019-5-07T00:00:00.000Z
title: "When A CloudFront Origin Must Fail for Testing High Availability"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/11/cloudfront_failover.png
---

Amazon's [CloudFront service](https://aws.amazon.com/cloudfront/) does its best to speed up content delivery by caching frequently accessed files and it is obvious the service does a superb job of this by attracting an extensive [list of customers](https://aws.amazon.com/cloudfront/case-studies/). But what happens when you turn on the prescribed failover solution and need to test its seemingly simple implementation? Get ready for a deeper dive into CloudFront's Origin Groups.

## Application Set-Up

To understand the problem at hand, it is best to set up the use case as it was intended (and before mentioning anything about failover). The [Ippon Podcast](https://podcast.ipponway.com/) exploratory project is possible through a combination of CloudFront and S3. The application's `index.html`, styles, scripts, and media files are all located in `us-east-1` S3 buckets with the property for "Static website hosting" enabled. The site is served via a CloudFront distribution concerning these S3 buckets as [Origins](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/DownloadDistS3AndCustomOrigins.html). So what happens to the site if a bucket is deleted or the files are removed? What about if the entire `us-east-1` region goes down?

Failure to retrieve these files will result in 4xx error codes since they will either be considered not found or forbidden. Enabling the "Versioning" property or "Cross-Region Replication" management of these buckets can help ensure the buckets and their files survive; but, the CloudFront distribution still needs a way to retrieve those files in the event of a region shutdown.

## Prescription Failover

In the event of a cache miss at an edge location, CloudFront goes to retrieve the file and replenish the cache. So then how does it work if the origin is unavailable at the time CloudFront goes looking for it? Fortunately, [support for Origin Failover](https://aws.amazon.com/about-aws/whats-new/2018/11/amazon-cloudfront-announces-support-for-origin-failover/) was introduced in November 2018, which was another important advancement in making AWS-based web applications highly available. Origin Failover is achievable through the ability to specify a primary and a secondary origin into what is called an [Origin Group](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/high_availability_origin_failover.html). When CloudFront is unsuccessful in connecting to the primary origin, an error status code is returned which prompts the failover action. CloudFront will then attempt the same request with the secondary origin. This is customizable in that any combination of the following status codes can be selected: 500, 502, 503, 504, 404, or 403.
![Origin Failover on cache miss](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/images/origingroups-overview.png)

## Administer the Prescription

In its current state, the [Ippon Podcast](https://podcast.ipponway.com/) site relies on S3 buckets located in the `us-east-1` region. Proper failover cannot be implemented without redundant S3 buckets in another region like `us-west-2`. This can be implemented in any of the following ways (using the AWS CLI for operations and an [infrequently accessed storage class](https://docs.aws.amazon.com/AmazonS3/latest/dev/storage-class-intro.html) for the new bucket is recommended):

* Make an S3 bucket in `us-west-2` then copy the objects over from the original `us-east-1` bucket. Object consistency between the two buckets will require manual intervention.
* Enable "Cross-Region Replication" on the `us-east-1` bucket and specifying the creation of a new S3 bucket. This will force the "Versioning" property to be enabled on both buckets and also requires the objects to be re-uploaded to the original `us-east-1` bucket so they automatically get copied over into the new `us-west-2` bucket.
* A combination of the first two operations, make an S3 bucket in `us-west-2` and copy the objects over from the original `us-east-1` bucket. Then, enable "Cross-Region Replication" on the `us-east-1` bucket and specifying the new S3 bucket which was just created in `us-west-2`. This will still force the "Versioning" property to be enabled on both buckets but re-uploading is no longer necessary for redundancy.

Returning to the CloudFront distribution, under the **Origins and Origin Groups** tab, enter the new S3 bucket in `us-west-2`'s information through the **Create Origin** interface. Make sure to specify "Restrict Bucket Access" as "Yes" and allow the grant read permission to the desired Origin Access Identity. There should now be at least two origins listed before moving on to the **Create Origin Group** interface.

From the "Origins *" drop-down, add the two S3 bucket origins in order of request priority. The 4xx status codes are necessary but feel free to select any of the errors as "Failover criteria *". Hit **Create** and confirm the Origin Group is now listed.

The final step under the **Behaviors** tab is to replace the intended behavior's origin field that was previously using just the single S3 bucket in `us-east-1` for the new origin group. And with that, the application should now be better equipped for handling failures! So how can that be tested?

## Testing Active-Active

At least in the case of the [Ippon Podcast](https://podcast.ipponway.com/), there are two buckets utilized in its functionality. One is for holding the `index.html`, styles, and scripts of the application. The other contains the podcast audio recordings. This allows for two different opportunities to test the origin group's failover capability. Just a note, if any of the following described does not reflect immediately, it is because it takes time for the changes to propagate. Invalidating the cache using `/*` will allow differences to take effect more quickly but [may have a cost](https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/Invalidation.html#PayingForInvalidation).

Removing the `index.html` is an easy one to test with because the site will break completely. To see this, revert the associated behavior back to the single origin and then delete the `index.html` file from this primary bucket (have no fear about losing the file since it will still be versioned within the bucket and also located in the alternate). Hard refresh the page and the site should come to a screeching halt (open the browser's developer tools to confirm a 4xx status code). Now, back in CloudFront, change the behavior to use the origin group again so that failure will be redirected to the alternate S3 bucket that contains the backup `index.html` file. Hard refresh the page once more and the site should spring back to life!

A similar test can be performed on the media files. While this will not break the site completely, reverting the associated behavior to the single origin and then deleting one or any of the files should cause failure to load/play. Changing the behavior to use the origin group will allow the backup file to be loaded and played as intended.

Remember to restore removed primary files either by deleting the "Delete marker" under show versions or re-upload the file. Ensure that any changed behaviors use the origin groups.

# Extra Info

## Life On The Edge

CloudFront allows for one or more Lambda@Edge Functions to be [executed in between fetches](https://docs.aws.amazon.com/lambda/latest/dg/lambda-edge.html) to the Origin. Why Lambda@Edge Functions? For those familiar with the more common Serverless Lambda Functions, the basic premise of Lambda@Edge Functions is similar however they are run on [much less computationally capable](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/cloudfront-limits.html#limits-lambda-at-edge) servers in more diverse locations. This is so that they exist closer to the end user to decrease load time on their side. What Lambda@Edge Functions primarily are purposed for is minor customizations of the fetched content during delivery. Amazon docs even show [quite a few example functions for popular topics](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/lambda-examples.html).

## Failover The Edge

Different Lambda@Edge Functions are allowed to be executed on request and response events. So how does this work in conjunction with Origin Failover? If the Primary Origin returns an error status code, keep in mind it will pass through the response's Lambda@Edge before the request is re-tried with the Secondary Origin. If there is any custom error handling in the response's Lambda@Edge this could interfere with the built-in failover functionality.
![Origin Failover with Lambda@Edge Functions on origin request and response events](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/images/origingroups-with-lambda-edge.png)

## Adding Headers

Typically whenever a user navigates a modern website, the response comes back with a header called the ["Content-Security-Policy" (CSP)](https://content-security-policy.com/). The reason for that is because most frameworks need to load various styles and scripts from a whole host of different sources. As [Mozzila's spec](https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP) states, "A CSP compatible browser will then only execute scripts loaded in source files received from those allowlisted domains..." For those unfamiliar, here is an example of a CSP header (take a guess which site it comes from):

`Content-Security-Policy: default-src * data: blob: 'self';script-src *.facebook.com *.fbcdn.net *.facebook.net *.google-analytics.com *.virtualearth.net *.google.com 127.0.0.1:* *.spotilocal.com:* 'unsafe-inline' 'unsafe-eval' blob: data: 'self';style-src data: blob: 'unsafe-inline' *;connect-src *.facebook.com facebook.com *.fbcdn.net *.facebook.net *.spotilocal.com:* wss://*.facebook.com:* https://fb.scanandcleanlocal.com:* attachment.fbsbx.com ws://localhost:* blob: *.cdninstagram.com 'self';`

Intended functionality using this example will send the user the CSP header by overriding the response in a specified Lambda@Edge Function like so:

    exports.handler = (event, context, callback) => {
        const response = event.Records[0].cf.response;
        const headers = response.headers;

        const  = 'Content-Security-Policy';
        const headerCspValue = "default-src * data: blob: 'self';script-src *.facebook.com *.fbcdn.net *.facebook.net *.google-analytics.com *.virtualearth.net *.google.com 127.0.0.1:* *.spotilocal.com:* 'unsafe-inline' 'unsafe-eval' blob: data: 'self';style-src data: blob: 'unsafe-inline' *;connect-src *.facebook.com facebook.com *.fbcdn.net *.facebook.net *.spotilocal.com:* wss://*.facebook.com:* https://fb.scanandcleanlocal.com:* attachment.fbsbx.com ws://localhost:* blob: *.cdninstagram.com 'self';`";

        headers[headerCsp.toLowerCase()] = [{key: headerCsp, value: headerCspValue}]; 

        callback(null, response);
    };

For more information, see [Adding HTTP Security Headers Using Lambda@Edge and Amazon CloudFront](https://aws.amazon.com/blogs/networking-and-content-delivery/adding-http-security-headers-using-lambdaedge-and-amazon-cloudfront/).

<u>Sources</u>:

* [CloudFront](https://aws.amazon.com/cloudfront/)
* [CloudFront Case Studies](https://aws.amazon.com/cloudfront/case-studies/)
* [How do I use CloudFront to serve a static website hosted on Amazon S3?](https://aws.amazon.com/premiumsupport/knowledge-center/cloudfront-serve-static-website/)
* [Using AWS Lambda with CloudFront Lambda@Edge](https://docs.aws.amazon.com/lambda/latest/dg/lambda-edge.html)
* [Using Amazon S3 Origins, MediaPackage Channels, and Custom Origins for Web Distributions](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/DownloadDistS3AndCustomOrigins.html)
* [CloudFront Origin API Doc](https://docs.aws.amazon.com/en_pv/cloudfront/latest/APIReference/API_Origin.html)
* [Origin Group Failover](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/high_availability_origin_failover.html)
* [How CloudFront retrieves files and replenishes the cache](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/HowCloudFrontWorks.html)
* [Making AWS-based web applications highly available](https://dzone.com/articles/designing-web-apps-for-high-availability-in-aws)
* [Triggering Lambda@Edge Functions execution in CloudFront](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/lambda-edge-add-triggers.html)
* [Lambda@Edge Limits](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/cloudfront-limits.html#limits-lambda-at-edge)
* [Only semi-recently was support introduced for writing Lambda@Edge functions in Node.js v8.10](https://aws.amazon.com/about-aws/whats-new/2018/05/lambda-at-edge-adds-support-for-node-js-v8-10/)
* [Lambda@Edge Example Functions for Popular Topics](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/lambda-examples.html)
* [Content-Security-Policy Header](https://content-security-policy.com/)
* [Mozilla Content Security Policy (CSP)](https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP)
* [Adding HTTP Security Headers Using Lambda@Edge and Amazon CloudFront](https://aws.amazon.com/blogs/networking-and-content-delivery/adding-http-security-headers-using-lambdaedge-and-amazon-cloudfront/)
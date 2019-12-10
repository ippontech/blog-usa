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

Amazon's [CloudFront service](https://aws.amazon.com/cloudfront/) does its best to speed up content delivery by caching frequently accessed files and it is obvious the service does a superb job of this by attracting an extensive [list of customers](https://aws.amazon.com/cloudfront/case-studies/). But what happens when you turn on the prescribed failover solution for redundancy and need to test its seemingly simple implementation? Get ready for a deeper dive into CloudFront's Origin Groups.

## Application Set-Up

To understand the problem at hand, it is best to set up the use case as it was intended (and before mentioning anything about failover). The [Ippon Podcast](https://podcast.ipponway.com/) exploratory project is possible through a combination of CloudFront and S3. The application's `index.html`, styles, scripts, and media files are all located in `us-east-1` S3 buckets with the property for "Static website hosting" enabled. The site is served via a CloudFront distribution concerning these S3 buckets as [Origins](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/DownloadDistS3AndCustomOrigins.html). So what happens to the site if a bucket is deleted or the files are removed? What about if the entire `us-east-1` region goes down?

Failure to retrieve these files will result in 4xx error codes since they will either be considered not found or forbidden. Enabling the "Versioning" property or "Cross-Region Replication" management of these buckets can help ensure the buckets and their files survive; but, the CloudFront distribution still needs a way to retrieve those files in the event of a region shutdown.

## Prescription Failover

In the event of a cache miss at an edge location, CloudFront goes to retrieve the file and replenish the cache. So then how does it work if the origin is unavailable at the time CloudFront goes looking for it? Fortunately, [support for Origin Failover](https://aws.amazon.com/about-aws/whats-new/2018/11/amazon-cloudfront-announces-support-for-origin-failover/) was introduced in November 2018, which was another important advancement in making AWS-based web applications highly available. Origin Failover is achievable through the ability to specify a primary and a secondary origin into what is called an [Origin Group](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/high_availability_origin_failover.html). When CloudFront is unsuccessful in connecting to the primary origin, an error status code is returned which prompts the failover action. CloudFront will then attempt the same request with the secondary origin. This is customizable in that any combination of the following status codes can be selected: 500, 502, 503, 504, 404, or 403.
![Origin Failover on cache miss](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/images/origingroups-overview.png)

## Administer the Prescription

In its current state, the [Ippon Podcast](https://podcast.ipponway.com/) site relies on S3 buckets located in the `us-east-1` region. Proper failover cannot be implemented without redundant S3 buckets in another region like `us-west-2`. Setup a cross-regional bucket in either of the following ways:

* [Enable "Cross-Region Replication"](https://geekylane.com/cross-region-replication-on-s3/) on the `us-east-1` bucket and specify the creation of a new S3 bucket. This will force the "Versioning" property to be enabled on both buckets, and also requires the objects to be [copied](https://docs.aws.amazon.com/cli/latest/reference/s3/cp.html) or [synced](https://docs.aws.amazon.com/cli/latest/reference/s3/sync.html) between the original `us-east-1` bucket and the new `us-west-2` bucket, but going forward the latest files will automatically be replicated.
* [Make](https://docs.aws.amazon.com/cli/latest/reference/s3/mb.html) or repurpose an S3 bucket in `us-west-2` and [copy](https://docs.aws.amazon.com/cli/latest/reference/s3/cp.html) or [sync](https://docs.aws.amazon.com/cli/latest/reference/s3/sync.html) the objects over from the original `us-east-1` bucket. Then, [enable "Cross-Region Replication"](https://geekylane.com/cross-region-replication-on-s3/) on the `us-east-1` bucket and specify the new S3 bucket which was just created in `us-west-2`. This will still force the "Versioning" property to be enabled on both buckets and automatically going forward the latest files will be replicated.

Note that "Cross-Region Replication" is not required but, without enabling, object consistency between the two buckets will require manual intervention. Using the [AWS CLI for better control over S3 operations](https://docs.aws.amazon.com/cli/latest/reference/s3/) and an [infrequently accessed storage class](https://docs.aws.amazon.com/AmazonS3/latest/dev/storage-class-intro.html) for a new, cost-effective bucket is recommended. Possible commands might include:

```language-shell
aws s3 mb s3://mybucket_west --region us-west-2
aws s3 cp s3://mybucket s3://mybucket_west --recursive
aws s3 sync s3://mybucket s3://mybucket_west
```

Returning to the CloudFront distribution, under the **Origins and Origin Groups** tab, enter the new S3 bucket in `us-west-2`'s information through the **Create Origin** interface. Make sure to specify "Restrict Bucket Access" as "Yes" and allow the grant read permission to the desired Origin Access Identity. There should now be at least two origins listed.

Moving on to the **Create Origin Group** interface, from the "Origins *" drop-down, add the two S3 bucket origins in order of request priority. The 4xx status codes are necessary but feel free to select any of the errors as "Failover criteria *". Hit **Create** and confirm the Origin Group is now listed.

The final step under the **Behaviors** tab is to replace the intended behavior's origin field that was previously using just the single S3 bucket in `us-east-1` for the new origin group. And with that, the application should now be better equipped for handling failures! So how can that be tested?

## Testing... Testing... Redundancy

At least in the case of the [Ippon Podcast](https://podcast.ipponway.com/), there are two buckets utilized in its functionality. One is for holding the `index.html`, styles, and scripts of the application. The other contains the podcast audio recordings. This allows for two different opportunities to test the origin group's failover capability. Just a note, if any of the following described does not reflect immediately, it is because it takes time for the changes to propagate. Invalidating the cache using `/*` will allow differences to take effect more quickly but may have a [cost involved if performing over 1,000 in a month](https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/Invalidation.html#PayingForInvalidation).

Removing the `index.html` is an easy one to test with because the site will break completely. To see this, perform the following steps:

1. Revert the associated behavior back to the single origin
1. Delete the `index.html` file from this primary bucket (have no fear about losing the file since it will still be versioned within the bucket and also located in the alternate)
1. Hard refresh the page and the site should come to a screeching halt
1. Open the browser's developer tools to confirm the expected 4xx status code through the Network tab
1. Back in CloudFront, change the behavior to use the origin group again so that failure will be redirected to the alternate S3 bucket that contains the backup `index.html` file
1. Hard refresh the page once more and the site should spring back to life!

A similar test can be performed on the media files. While this will not break the site completely, reverting the associated behavior to the single origin and then deleting one or any of the files should cause failure to load/play. Changing the behavior over to use the origin group will allow the backup file to be loaded and played as intended.

Remember to restore removed primary files either by deleting the "Delete marker" under show versions or re-upload the file. Ensure that any changed behaviors use the origin groups.

## Wrap Up

The [Ippon Podcast](https://podcast.ipponway.com/) site is still very much a work-in-progress. Since it is more exploratory, high availability is not as critical. This makes it a good application to test proper failover through CloudFront's origin groups. There are more subtle details to utilizing origin groups which require some exploration. Yet, hopefully, this provided an easy first step towards building and testing redundancy in a CloudFront/S3-hosted application.

<u>Sources</u>:

* [CloudFront](https://aws.amazon.com/cloudfront/)
* [CloudFront Case Studies](https://aws.amazon.com/cloudfront/case-studies/)
* [How do I use CloudFront to serve a static website hosted on Amazon S3?](https://aws.amazon.com/premiumsupport/knowledge-center/cloudfront-serve-static-website/)
* [Using AWS Lambda with CloudFront Lambda@Edge](https://docs.aws.amazon.com/lambda/latest/dg/lambda-edge.html)
* [Using Amazon S3 Origins, MediaPackage Channels, and Custom Origins for Web Distributions](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/DownloadDistS3AndCustomOrigins.html)
* [S3 AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/reference/s3/)
* [How to enable the cross region replication on S3?](https://geekylane.com/cross-region-replication-on-s3/)
* [CloudFront Origin API Doc](https://docs.aws.amazon.com/en_pv/cloudfront/latest/APIReference/API_Origin.html)
* [Origin Group Failover](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/high_availability_origin_failover.html)
* [How CloudFront retrieves files and replenishes the cache](https://docs.aws.amazon.com/en_pv/AmazonCloudFront/latest/DeveloperGuide/HowCloudFrontWorks.html)
* [CloudFront Invalidating Files](https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/Invalidation.html)
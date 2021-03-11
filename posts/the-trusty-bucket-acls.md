---
authors:
- Brian Knight
tags:
- Cloud
- AWS
- AWS S3
date: 2021-03-03T01:14:26.000Z
title: "The Trusty Bucket - Access Control Lists"
image: https://raw.githubusercontent.com/brianknight10/blog-usa/master/images/2021/02/trusty-bucket-1-main.png
---

_"Just stick it in an S3 bucket."_ 

_It is a line I have heard countless times. While it is true that AWS's S3 service is a convenient and easy-to-use storage solution, its initial simplicity belies a wide array of configuration options and possibilities that often get overlooked._

_This post is the second of an ongoing series about S3 at Amazon Web Services. Today we will introduce the concept of object and bucket security using Access Control Lists (ACLs). Subsequent posts will dive deeper into various aspects of S3 that will touch on considerations such as cost management, security, performance, lifecycle management, and more._

_In the end, I hope you will see how the trusty and ubiquitous S3 bucket, while initially simple, gives you plenty of possibilities - and things to think about! - as you add them to your solution._

# S3 Resource Access Overview

In [the last post](https://blog.ippon.tech/the-trusty-bucket-introduction-and-basics/), we introduced the S3 service. We created a bucket, uploaded some objects, and examined the attributes and actions of both resources. Ultimately, those _buckets_ and _objects_ are the resources to which we want to control access.

In Amazon S3, every resource has a single owner. By default, only the resource owner can access these resources. Easy enough, right? So what happens when the owner wants to give others access to their buckets and objects?

All requests to a bucket or object are either _authenticated_ or _unauthenticated_. Resource owners can decide to make their objects available to everyone in the world (the unauthenticated public) or a subset of users authenticated by AWS.

## Access Policy Choices

Based on the section above, access policies define _who_ has access to _what_ resources. There are two ways we can approach this definition: from the perspective of the _resource_ or the perspective of the _user_. For instance, we can say "these _users_ have access to this _resource_" or we can say "these _resources_ can be accessed by this _user_". The difference is subtle but essential. The first example is a _resource-based policy_ because it is attached to a specific resource. The latter example is a _user-based policy_.

When [Amazon launched S3 in 2006](https://press.aboutamazon.com/news-releases/news-release-details/amazon-web-services-launches-amazon-s3-simple-storage-service), it offered the ability to create resource-based policies to secure access to buckets and objects. Only with the introduction of [AWS Identity and Access Management (IAM)](https://aws.amazon.com/iam/) were we able to create user-based policies. As such, **there are two different ways to control access to S3 resources** - the older resource-based approach and the newer user-based approach.

User-based policies are created with AWS IAM. By definition, we cannot use a user-based policy for an anonymous (or unauthenticated) user because the policy must be attached to an authenticated user. The next post in this series will discuss using this approach to secure S3 resources, but for now, here's an example of a user-based policy. It is written in JSON and allows the associated user to perform five different S3 actions on a bucket and its objects. You can attach this policy to a specific IAM user, group, or role.

```json
{
 "Version": "2012-10-17",
 "Statement": [
 {
 "Sid": "AssignUserActions",
 "Effect": "Allow",
 "Action": [
 "s3:PutObject",
 "s3:GetObject",
 "s3:ListBucket",
 "s3:DeleteObject",
 "s3:GetBucketLocation"
 ],
 "Resource": [
 "arn:aws:s3:::the-trusty-bucket/*",
 "arn:aws:s3:::the-trusty-bucket"
 ]
 }
 ]
}
```

The remainder of this post discusses the resource-based policy approach.

![ACLs and Policy Structures](https://raw.githubusercontent.com/brianknight10/blog-usa/master/images/2021/03/trusty-bucket-2-1.png)

# Access Control Lists

Every bucket and object is associated with an access control list (ACL) containing a list of grants identifying grantees and permissions granted. You use ACLs to give necessary read and write permissions to other AWS accounts using an S3–specific XML schema. The following is an example bucket ACL. The grant in the ACL shows a bucket owner as having full control permission. Incidentally, this ACL is the default used on any new bucket you create in S3 - it gives you complete control as the bucket owner.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<AccessControlPolicy xmlns="http://s3.amazonaws.com/doc/2006-03-01/">
 <Owner>
 <ID>*** Owner-Canonical-User-ID ***</ID>
 <DisplayName>owner-display-name</DisplayName>
 </Owner>
 <AccessControlList>
 <Grant>
 <Grantee xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
 xsi:type="Canonical User">
 <ID>*** Owner-Canonical-User-ID ***</ID>
 <DisplayName>display-name</DisplayName>
 </Grantee>
 <Permission>FULL_CONTROL</Permission>
 </Grant>
 </AccessControlList>
</AccessControlPolicy> 
```

Writing XML is no fun (am I right?!), so, fortunately, AWS provides [a list of "canned" ACLs](https://docs.aws.amazon.com/AmazonS3/latest/userguide/acl_overview.html#canned-acl) for our use. We will focus on those for our examples below.

## Playing with ACLs

Demonstration time! I have included some code snippets below, but if you'd like to follow along on your computer, I have a [GitHub repository](https://github.com/brianknight10/the-trusty-bucket) that contains accompanying [scripts for this post](https://github.com/brianknight10/the-trusty-bucket/tree/main/2-acls). We will start by creating a bucket and some objects.

```python
import boto3

import constants

s3 = boto3.resource('s3')
bucket = s3.Bucket(constants.BUCKET_NAME)

# Create the bucket
bucket.create(
 ACL='private',
 CreateBucketConfiguration={
 'LocationConstraint':constants.BUCKET_REGION
 }
)

# Create some objects
for x in range(0, 2):
 # Create private objects
 bucket.put_object(
 ACL='private',
 Body=b'private!',
 Key=f"private-{x}"
 )

 # Create public objects
 bucket.put_object(
 ACL='public-read',
 Body=b'public-read!',
 Key=f"public-read-{x}"
 )
```

This script creates a bucket in AWS and adds four objects. Notice that we used the `private` canned ACL for the bucket and the first two objects. We then used the `public-read` ACL for the last two objects. The `private` ACL gives no rights to anyone but the owner. As the name implies, ' public-read' provides the ability to read the file to anyone in the whole world.

To test this out, remember that besides just storing our objects, S3 also makes interacting with them accessible through a web interface. Every object has a unique URL. The convention for determining the object URL is as follows:

```
https://<bucket-name>.s3.<AWS region>.amazonaws.com/<object key>
```

Our bucket is called `ttb-acls-us-east-2` and is in the Ohio (us-east-2) region. Therefore the URL to my `public-read-0` object is: "https://ttb-acls-us-east-2.s3.us-east-2.amazonaws.com/public-read-0".

When we use the `curl` command-line tool for that URL, we receive a successful response that shows us the file's contents.

![Reading a public object](https://raw.githubusercontent.com/brianknight10/blog-usa/master/images/2021/03/trusty-bucket-2-2.png)

That makes sense because we used the `public-read` ACL on that object. What happens if we try to access an object with a `private` ACL or even the bucket root?

![Access denied on private objects](https://raw.githubusercontent.com/brianknight10/blog-usa/master/images/2021/03/trusty-bucket-2-3.png)

You guessed it - we get an "Access Denied" message in response.

## Which Policy Wins?

Our example above shows an object with a `public-read` ACL in a bucket with a `private` ACL attached. What if we switched those around and put a `public-read` policy on the bucket and uploaded a new object?

```python
import boto3

import constants

s3 = boto3.resource('s3')
bucket = s3.Bucket(constants.BUCKET_NAME)

# Set bucket ACL to 'public-read'
bucket.Acl().put(ACL='public-read')

# Create object with default ACL
bucket.put_object(
 Body=b'default!',
 Key=f"default"
)
```

After changing the bucket policy to `public-read` and uploading a new object called `default` without specifying an ACL, we can see if it is accessible.

![Private ACL wins](https://raw.githubusercontent.com/brianknight10/blog-usa/master/images/2021/03/trusty-bucket-2-4.png)

It turns out we cannot access the object. The object ACL wins! Interestingly, if you try to access the root of the bucket, though, you can see a list of the objects in the bucket because listing objects in the bucket is part of the bucket's policy, which is `public-read`.

![Viewing bucket contents](https://raw.githubusercontent.com/brianknight10/blog-usa/master/images/2021/03/trusty-bucket-2-5.png)

Below is a diagram that shows the authentication flow when using resource-based policies. The only time a bucket policy can trump an object policy is when it explicitly denies access to the bucket. Otherwise, the object policy takes precedence. Next post, we'll add user-based policies to the same diagram.

![S3 ACL Authorization Flow](https://raw.githubusercontent.com/brianknight10/blog-usa/master/images/2021/03/trusty-bucket-2-6.png)

## Keeping Safe

One of the challenges of using resource-based policies is that every resource can have its own policy. What if we had a bucket full of sensitive or secret data that we _never_ wanted to share with the public? And what if our bucket contained millions of objects? Keeping track of all those ACLs would be difficult. What if a developer made a mistake and accidentally put a `public-read` policy on an object? There are [lots of stories](https://businessinsights.bitdefender.com/worst-amazon-breaches) of "leaky" S3 buckets causing major data breaches.

Fortunately, after several high-profile S3 breaches, Amazon created a "safety switch" we can use called "Block Public Access". There are four "sub-switches", two of which address ACLs and two of which address user-based policies.

The script below turns on the ACL safety switches for our bucket.

```python
import boto3

import constants

client = boto3.client('s3')

client.put_public_access_block(
 Bucket=constants.BUCKET_NAME,
 PublicAccessBlockConfiguration={
 'BlockPublicAcls': True,
 'IgnorePublicAcls': True
 }
)
```

The `BlockPublicAcls` switch makes it impossible to upload an object to S3 that contains an ACL that permits the public any access to the object. OK, that's great, but what about the objects already in the bucket that may allow public access? The `IgnorePublicAcls` switch tells S3 to ignore any public ACLs at all.

![Block public access](https://raw.githubusercontent.com/brianknight10/blog-usa/master/images/2021/03/trusty-bucket-2-7.png)

In the AWS console for the S3 bucket, we can see that the switches have been toggled to deny any public access via ACLs. We will save the other two items for the next post.

Finally, we can see that even though the bucket and object both have `public-read` ACLs attached, we cannot access them via the web.

![Access denied on public bucket](https://raw.githubusercontent.com/brianknight10/blog-usa/master/images/2021/03/trusty-bucket-2-8.png)

# Wrapping Up

Even though they are mostly made unnecessary by the new user-based IAM policies, understanding resource ACLs is essential. Next time we'll walk through the IAM policies for S3 buckets, bucket policies, and other security considerations. We'll follow that post up with a security best practices article for S3 based on what we will have learned.

# Sources

- [AWS S3 Access Control Lists Overview](https://docs.aws.amazon.com/AmazonS3/latest/userguide/acl_overview.html)
- [Blocking public access to your Amazon S3 storage](https://docs.aws.amazon.com/AmazonS3/latest/userguide/access-control-block-public-access.html)
- [WWE S3 Breach Profile](https://www.forbes.com/sites/thomasbrewster/2017/07/06/massive-wwe-leak-exposes-3-million-wrestling-fans-addresses-ethnicities-and-more/)
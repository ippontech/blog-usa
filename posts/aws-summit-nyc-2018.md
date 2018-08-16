---
authors:
- Randeep Walia
categories:
- AWS
date: 2018-08-13T06:25:27.000Z
title: "AWS Summit NYC 2018"
---
The glass ceiling of the Javits Center cracked, literally, on a rainy day in July. As water poured from the roof to the floor several stories below, one couldn't be blamed for attributing it to the collective energy in the room radiating out from the hundreds of software engineers and architects that filled the space. The AWS Summit had made its way to New York City and I was in attendance to witness it.

AWS Summit is a whirlwind- even the most experienced Amazon Web Services developer would probably be impressed by some new revelation during this day of technology in the cloud. The ecosystem is huge and the capabilities of its toolset can be jaw dropping. Over the course of the day I attended workshops, keynote speeches, sales pitches and sat down here to try and separate some of they hype from the reality. Here is a summary of some things I learned and products being pushed out of AWS' diverse offerings.

# Databases 
Amazon is pushing its managed database services hard. There were recurring workshops on [Aurora](https://aws.amazon.com/rds/aurora/), Amazon's fully managed solution intended to replace your MySQL or PostgreSQL. This includes the release of Aurora Serverless, which can autoscale up or down depending on the needs of a severless application. If you are not looking to go all-in on AWS there are other options as well. MongoDB was present pitching [Atlas](https://www.mongodb.com/cloud/atlas), their fully-managed NoSQL database in the cloud for AWS, GCP, and Azure. 

Measuring the true cost of a fully-managed database vs a cloud hosted solution vs an on-premise solution is not a trivial task and a lot of factors have to be considered. But for business solutions that don't want to worry about the headache of patching, upgrading, scaling and fine-tuning a solution, a managed solution offers many nice features.

# Serverless Architectures
Full stack developers and Solution Architects have a myriad of options at their disposal. In addition to all the backend storage options mentioned above, [serverless architecture patterns](https://blog.ippon.tech/going-serverless/) are transforming the way Internet solutions can be developed in the cloud. 

Applications that are data intensive can use Kinesis Streams to ingest and process massive amounts of data (a presentation from Chris Dyl, Director at Epic Games, discussed their wildly popular game Fortnite and revealed that the developers are ingesting and processing petabytes of data each day). 

While Lambdas are not new, the way they are being integrated into AWS is. Your EC2 instances might go the way of the Dodo and you'll probably see your monthly bill go down dramatically.

But if they don't cut it Amazon is introducing new high performance instance types including the [Z1d instances](https://aws.amazon.com/ec2/instance-types/z1d/) are speced out at Ferrari levels with 4.0 GHz clock speeds, SSD drives, 48 CPUs and over 380 gigs of RAM. 

# Machine Learning
If there was one topic that inspired the most excitement and had the biggest wow-factor it would have to be the Machine Learning/AI technologies that are being offered. AWS has solutions in place for managing training data sets and gathering insights from them. The algorithms for doing this type of processing have been around for decades, but the ability to process them at scale has always been a limiting factor until recently. Amazon offers [SageMaker](https://aws.amazon.com/sagemaker/) with coming support for [TensorFlow](https://www.tensorflow.org/) and other ML Frameworks to leverage this capability for an organization.

High level AI Services allow a developer to utilize advanced Machine Learning functionality without having to get into the weeds with how ML works. Speech recognition, language translations, facial recognition, etc are just some of the capabilities offered to a software engineer and, once you understand the breadth of what is there a capable developer will only be limited by the power of their imagination.

# Tying it together

So what could your imagination dictate? Could you train a system to analyze driving patterns and predict traffic accidents? Perhaps a system that ingests data of patient mortalities from medical complications against their demographics to create training data that will go on to prevent future deaths from prescription errors? What about determining ancestry information from a selfie?

With over 125 services available there is a huge world of possiblity. Check out the [AWS Summit schedule](https://aws.amazon.com/summits/) and see what is possible when it rolls into town near you.

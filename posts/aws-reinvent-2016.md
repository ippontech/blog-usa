---
authors:
- Josh Shipp
tags:
- Cloud
- Docker
- DevOps
date: 2016-12-16T16:47:39.000Z
title: "AWS re:Invent 2016"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/aws1.png
---

re:Invent is Amazon Web Services’s yearly conference. As usual, it was held at the Venetian / Sands Expo Center in Las Vegas Nevada, but new to 2016, Amazon expanded the event space by utilizing most of the meeting space at the Mirage, which is across the street from the Venetian. Last year’s re:Invent was host to over 19,000 Cloud-inspired attendees. This year that record was blown away by gathering 32,000 participants and it showed.

### What is re:Invent?
re:Invent is a meeting that brings together Cloud-oriented professionals to mingle, hear new company and tech announcements, check out vendors offering technologies on and off AWS, as well as, explore a large diverse series of sessions targeting aspects of AWS and surrounding technologies. 2016 marks the 10 year anniversary of AWS and more than just the size of this event was evidence of its success. Every year there is plenty of fun to be had as well: a Vegas pub crawl, wing eating contests, hackathons, and of course the re:Play party. With so much happening, there is no way to do everything but in trying to, I wanted to share my experience with you.

### What was announced?
Every year re:Invent serves as a platform for the delivery of all sorts of great announcements from Amazon about their latest offerings. These usually are stated via the keynote speeches by Andy Jassy (CEO of AWS), Werner Vogels (CTO of Amazon.com), and added this year, a presentation by James Hamilton (VP and distinguished engineer of AWS).

### James Hamilton

On Tuesday (Nov 29th) Hamilton spoke, and while he did not announce any new products or offerings, he took us through some of the challenges and solutions that his teams have found in data-center management. Speaking about the limitations of commonly available network hardware, and the decisions to design and produce their network hardware internally. It was a wonderful and fascinating speech.

### Andy Jassy

The next morning Andy Jassy took the stage at 8am and told us about all sorts of wonderful new AWS features. Jassy started out with a heavy focus on EC2 Compute services, starting with the following new instance types:

* **t2.xlarge and t2.2xlarge** burstable-performance instances: well-suited for workloads that require a consistent baseline performance with the ability to burst.
* **EC2 R4 Memory Optimized instances**, which are 20% more price performant than R3 instances: boasting more and faster RAM coupled with 2x compute capacity.
* **I3 instances**: Storage Optimized High I/O instances, featuring NVMe based SSDs for the most demanding I/O intensive relational, NoSQL, transactional, and analytics workloads.
* **EC2 C5 instances**: the most powerful Compute Optimized instances, featuring the highest performing processors and the lowest price/compute performance in EC2.
* **Elastic GPUs**: the most cost effective and flexible way to add graphics acceleration to Amazon EC2 Instances. Attach GPUs to EC2 instances like you currently do EBS volumes.
To assist in the ease of setup of simple installation and to accelerate ramp-up time AWS announced **Amazon Lightsail**: Virtual Private Servers made easy. Behind the scenes this manages the VM setup, storage, IAM permissions, security groups, DNS, and Public IP.
* **EC2 F1 Instances** come with customizable FPGA’s. Field Programmable Gate Arrays are configurable processors that provide custom hardware acceleration for applications
These new innovations are cheaper and faster offerings across the board. The standout announcement of interest to myself is the F1 instance offering. FPGAs will essentially allow customers to design custom processors for specific tasks, and put them to work giving new meaning to the term “hardware acceleration.” It will be interesting to see what comes out of this.

Next, after a quick jab at Larry Ellison of Oracle, Andy goes on to announce **Amazon Athena**. Athena is a fully managed service that allows you to launch ad-hoc queries against data stored in S3 in an SQL query format similar to many big-data offerings but without the setup and management necessary in RedShift or EMR solutions.

Pivoting from Big Data to AI with a quick mention of support for MXNet. Three new AI service offerings were announced:

* **Amazon Rekognition**: Image analysis, able to recognise, categorize, and identify people, things, and facial expressions.
* **Amazon Polly**: A little bit beyond your standard text to speech. Having AI elements, Polly is able to recognise standard acronyms, metrics, colloquialisms to produce a human sounding result.
* **Amazon LEX**: The Alexa backend. Including speech and intention recognition LEX is capable of maintaining the state of a conversation and respond contextually. LEX can connect to a host of services in and outside of the AWS service list.
After a couple of guest speakers, **PostgreSQL compatibility for Amazon Aurora** was announced. Aurora will now offer both MySQL and PostgreSQL compatibility simultaneously. In addition, migrating over to Aurora from RDS has now been streamlined.

Hybrid operations is still a huge topic for AWS, with the VMWare Cloud connection for AWS announced a couple of weeks ago, Andy talks with Pat Gelsinger for a while on their partnership.

Next he announced **AWS Greengrass**. Greengrass is an embeddable framework for supporting on-device lambda functions for connected and potentially disconnected mobile and IoT devices. This seemed out of place in the current context, but the next announcement cleared that up.

In 2015, AWS announced the Snowball. AWS Snowball is a storage device whose goal is to assist the transition to the Cloud by providing immense shippable storage. This year Amazon announced **Snowball Edge**. With the Edge comes increased storage (100TB), clustering capabilities –  sharding your data across multiple Edges for increased speed and storage, and an S3 endpoint that can synchronously transmit data to and fro. The Snowball Edge will also have Greengrass embedded, giving you the equivalent of an M4.4XLarge compute power, and the ability to script modifications to, and intentions for your data in transfer.

The snowstorm wasn’t over with that announcement. Andy went on to conclude the session with the announcement of the **Amazon Snowmobile**. Quite literally a shipping crate delivered via tractor-trailer to your datacenter that can deliver 100PB of storage to the Cloud at a time.

### Werner Vogels

Werner Vogels took the stage on Wednesday morning to continue the barrage of innovation coming out of AWS. Firstly, he touched on some DevOps concerns surrounding Chef servers. Chef is a configuration management system and AWS now offers **OpsWorks for Chef Automate** – a fully managed version of the new Chef Automate as a service. Next, Vogels introduced **Amazon’s EC2 Systems Manager**, touted as a collection of tools for package installation patching, resource configuration and task automation.

Moving on to the discussions of CI/CD pipelines, AWS CodePipeline was introduced in 2015 and to help round out that offering of tools, Werner revealed **AWS CodeBuild**. CodeBuild, as its name implies, coordinates the building and testing of source into deployable artifacts. Continuing on the topic of operations, Werner revisited cloudwatch and cloudtrail monitoring solutions to introduce **AWS X-Ray**. X-Ray is a fully managed service that allows you to debug your applications in detail, seeing the interactions of the components of your applications in real-time.  To assist with ongoing events within the AWS ecosystem,they have added the AWS Personal Health Dashboard: a personalized view of AWS service health.

Attacks on applications across the internet have been of large concern this year. Witnessing an unprecedented rise in volumetric attacks, **AWS Shield** was announced and aimed to provide AWS-Wide protection from volumetric and state exhaustion attacks. This service is on by default for every AWS customer, or you can choose to upgrade this service to **AWS Shield Advanced**, which provides additional protection from application level incidents, alarms and notifications about network wide incidents, and support through the setup process.

Moving on to Big Data, reiterating the announcements from the previous data and recapping other products that have been announced in the past, Vogels announced **Amazon Pinpoint**. Pinpoint is a solution for gathering data around and targeting your mobile users to enhance analytics and your current data-set.

Describing common data pathways utilized by customers, Werner points out a number of missing components within AWS’s offerings and sets off describing the new service **AWS Glue**. AWS Glue is a fully managed ETL service that makes it easy to understand your data sources, prepare the data, and move it reliably between data stores. It simplifies and automates data discovery, transformation, and job scheduling tasks.
![data architecture on aws](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/aws2.png)

In processing data, large scale batch processing can be painful, especially when you are trying to take advantage of the spot market-to-save costs. **AWS Batch** is set to help you manage these processes, providing a fully managed service that provides cost-optimized dynamically provisioned-and-scaled priority-based queues and scheduling of your batch jobs.

Werner then takes some time to explain the existing container ecosystem provided by AWS, and announce **Blox**: A collection of open source projects for container management and orchestration.

In a quick revisit of some of the new features and additions to Lambda over 2016, **C# support for Lambda** was announced. In addition to that, **Lambda@Edge** was debuted. Lambda@Edge is the ability to execute Lambda functions from cloudfront, allowing CDN delivery of dynamic content. These additions are in line with the advocacy of serverless application design, and to contribute to that, he also announced **AWS Step Functions**, which coordinate the components of distributed applications using visual workflows.

### What was hot?
With new announcements across the board, there was a clear direction for the buzz at the conference. Whether it was in sessions or just talk in the crowd, it was obvious that “serverless” was on everyone’s minds. Serverless is the use of (mostly) Lambda functions in conjunction with AWS ALB (Application Load Balancer) to provide a home for your code that does not have an instance or resources associated with it. Serverless designs can be used for everything from web applications to IoT solutions, and they are quick to point out that Serverless isn’t just Lambda but leveraging the entire ecosystem of AWS service offerings. Speaking of IoT, it was easily the “buzz” runner-up. Innovation is moving very quickly in these areas, and it is fueling massive growth.

### What was it like?
When I attended re:Invent in 2015 I had never seen anything quite like it. I had attended conferences before, but I had never been exposed to this format. The focal-point of the conference was no longer the vendor floor but now the sessions provided by AWS engineers and engineers of customer companies. At one-hour intervals, the hallways would flash-flood with engineers rubbing shoulder-to-shoulder, flowing towards their next session with excitement.

Attendance in 2016 almost doubled that of 2015. This increase in foot traffic, from 19,000 to 32,000, often jeopardised getting to-and-from your next session in a timely fashion. At some points, the hallways got so gridlocked that cell phones would start rising over the crowd in an effort to snap some pictures and identify the holdup, which was always just more people.

The keynotes this year were amazing, as they always are. I enjoyed the addition of James Hamilton to the list of speakers. Amazon announced more additions in 2015 than the previous year, and even more this year. The ambiance of the keynote hall synched with the info-decor that had been installed throughout the Venetian and the Mirage, featuring lighted moving arches and interactive info-posters throughout.
![aws relay](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/aws3.png)

Every year there is the Wednesday Night pub crawl and the Thursday night re:Play party. Due to a late running session, I was not able to attend the Pub Crawl, but I was happily able to attend the re:Play party. Somehow, re:Play has the capability of outdoing itself year after year. With far too many events and features to mention here, it was a tremendous party featuring Dutch DJ Martin Garrix, an open bar, and team rocket-league played on what to had to have been an 180-inch screen.

The one slight disappointment to me this year had to be the sessions. Last year, I found them very engaging and informative. This year, while they we still informative, they were somewhat less involved. Sessions tended to lean more towards a 50,000 ft view of operations, techniques and implementations instead of multi-tabbed console windows and large amounts code being belted out onto huge projected screens of the previous year. I would very much like to see these sessions get back into the “weeds” again and be loaded with detail, instead of being peppered with management and corporate political concerns. Then again, maybe I attended the wrong sessions.

### What I got out of it?
It’s always impressive to be at the mouth of an information flood such as Amazon Re:Invent. The exhilaration of the experience is not only all of the services that AWS provides at the event but the access and to the diverse skill-sets all around you. In any given corridor, you can find people engineering solutions for oil rigs, web applications, robotics, data mining, and business intelligence. They are all geeks, and most likely the majority of them introverts, but everyone usually responds nicely to a friendly face for a quick conversation. I was happy to take away a few new contacts from my interactions there.

Being around this caliber of crowd during the announcement of these new features on the platform was great. You get to watch the gears turn and see innovation happening as they ingest the potential exposed by these new services. In addition, new sessions popped up on the spot when announcements are made. While it can be difficult to incorporate these into an already packed schedule, the opportunity to get to hear details about these innovations from the engineers who created them hours after their announcement is a home-run.

Of course the sessions surrounding existing aspects of the platform are by-in-large wonderful as well. Being able to fill in the gaps in your knowledge with a quick walk-thru, in-depth description, or demo of specific applications and methodologies is invaluable.

Being a fan of all things tech, it’s amazing, entertaining, and informative to see the big-scale productions that these events inspire, such as the huge screens & interactive posters, animated archways and the mobile app that helps coordinate your movements, providing maps and notifications about events, games, and social interaction.

### What’s next?
Amazon rarely announces their roadmap. They emphasize they are extremely customer driven, letting customer needs and requests drive their path and feature-set. However, I would predict that there is going to be intensified focus on serverless computing. I would not be surprised to see a large expansion in Lambda languages and functionality in 2017, potentially in the direction of analytical operations, R or possibly Haskell and possibly with some more direct interconnect with QuickSight or other BI visualization. Also, I believe that with the focus on easy-data from this year’s convention, we are going to see an uptick in increased functionality around machine learning and AI. All of this is just speculation about next year, but one thing I know for certain is that I will be there again.

### Conclusion
re:Invent is an unmissable conference for the cloud-minded engineer, and this year was certainly no exception. Amazon has an implementation or involvement in almost every aspect of modern computing. It’s quickly becoming a defining feature for businesses, if you aren’t on AWS then you are behind.

Please check out the keynote speeches, and a number of the breakout sessions on the AWS YouTube Channel here:
[https://www.youtube.com/channel/UCd6MoB9NC6uYN2grvUNT-Zg](https://www.youtube.com/channel/UCd6MoB9NC6uYN2grvUNT-Zg)

A recap of the announcements and accompanying blog posts are here:
[https://aws.amazon.com/new/reinvent/](https://aws.amazon.com/new/reinvent/)

Many of the breakout sessions also eventually make it over to SlideShare:
[http://www.slideshare.net/AmazonWebServices](http://www.slideshare.net/AmazonWebServices)

Most recorded sessions are available here:
[https://gist.github.com/stevenringo/5f0f9cc7b329dbaa76f495a6af8241e9](https://gist.github.com/stevenringo/5f0f9cc7b329dbaa76f495a6af8241e9)

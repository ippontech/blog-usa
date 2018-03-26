---
authors:
- Laurent Mathieu
categories:
- 
date: 2014-10-10T04:15:23.000Z
title: "Amazon Makes A Power Play For Expanding DynamoDB Adoption"
image: 
---

This week (October 8, 2014) Amazon made an update announcement for its managed NoSQL service DynamoDB. Reading AWS announcements is one of my new favorite things to do because it typically means that the service is getting better, prices are getting cheaper or that the Free Tier quota has been elevated.

[Read the announcement here if you would like before continuing.](http://aws.amazon.com/about-aws/whats-new/2014/10/08/amazon-dynamodb-now-supports-json-document-data-structure-and-large-items/)

My experience with DynamoDB has so far been limited but I think for good reason. As a developer, there are a few principles that I look for when working with a new service:

- It must be easy to get started
- There needs to be some free of cost path to get started
- It has to offer a benefit beyond the tools I already use
- No major architecture concessions in order to adopt it

As with any technology decision, there’s always tradeoffs. Although Amazon’s DynamoDB met my criteria, I still couldn’t justify it. The deal just wasn’t sweet enough. The free tier was very limited at just 100MB of storage and a relatively low number of read and write operations each month. That’s an issue because one of the key advantages DynamoDB should be offering is high performance and with the previous free tier, a developer did not really have the ability to see that realized and could more realistically make a few tables just for a proof of concept. There were also a lot of alternatives. By selecting DynamoDB, I would have been binding myself to Amazon as the only vendor and infrastructure whereas if I went with a MongoDB service provider, I can migrate a backup to another provider or even to Amazon if I wanted to configure my own sharded environment there.

Amazon’s announcement yesterday, however, ups the storage limit to 25GB (whoa!) and 200 million requests per month. [Tech Crunch points out](http://techcrunch.com/2014/10/09/amazons-dynamodb-gets-hugely-expanded-free-tier-and-native-json-support/) that the CTO, Werner Vogels, states that this is enough to run a game with 15,000 active users and 500,000 impressions per month. That is really excellent and dwarfs its competitors’ offerings (Google Cloud Datastore and Microsoft’s DocumentDB).

The second portion of the announcement is native JSON document support, matching specifically the feature offered by Microsoft DocumentDB. You can now store JSON documents up to 400KB with DynamoDB, which I am really anxious to download the latest [Java SDK](http://aws.amazon.com/sdk-for-java/) and test out this weekend.

Making both announcements in tandem was a great strategic move by Amazon also. It addressed two of its greatest counterpoints at the same time. Any development teams that were opting out of using DynamoDB for either of these reasons now have some potential reconsiderations for moving forward or when starting future projects.

The only lingering drawbacks that I can still list are a somewhat nebulous pricing model (but this is often true of service providers and I have become fairly well experienced in estimating and budgeting costs in AWS) and that you are tying your datastore to a proprietary offering. Migrating off would become a significant undertaking as a one time custom migrator solution would need to be executed and then the data access layer of the application would need to be re-written. If selecting DynamoDB, it is crucial that is a carefully weighed decision and the project is ready to commit to it for the long term. But with the expanded free tier, that becomes a lot easier to do!

[Amazon DynamoDB Video on YouTube](https://www.youtube.com/watch?v=ZFdX3xcDOzg)

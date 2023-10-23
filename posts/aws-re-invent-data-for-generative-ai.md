---
authors:
- Lucas Ward
tags:
- Data
- AWS
- AI
date: 2023-10-23T14:50:55.000Z
title: "Revolutionizing Knowledge Management with Amazon Kendra: A Comprehensive Guide"
image: 
---

In today's fast-paced world, information is generated and stored at an unprecedented rate. Organizations are grappling with vast knowledge bases, making it increasingly difficult to find relevant data efficiently. The problem statement is clear: knowledge bases are everywhere, but nobody knows all of them, and searching through them can be a daunting task.

As a business owner or operator, how can we empower our employees to more easily consume the vast amounts of knowledge we, as an organization, have created and stored in various locations? What could we achieve if we could even put to use the conversations that happens surrounding these knowledge bases? 

Let's look at an example. A slide show is stored in google drive detailing a sales pitch we are to make to a potential client. The conversations that happen with the client are recorded into a transcript and also placed into google drive. The work is won, and a slack channel is created to facilitate communications. Over the course of the next few months or years, ongoing conversation, debate, key decisions and insights, and documents are all pushed through slack. In addition, more side conversations happen in email, and more documents are stored in google drive. The work is finished, a few years go by, employees come and go, and now the client has reach back out for more work.

Digging through archived slack channels and searching google drive just doesn't cut it when it comes to "getting back up to speed". Enter Amazon Kendra - A fully managed intelligent search product that is so cool, it's almost unbelievable. *Simply* add your slack, google drive, and email to Amazon Kendra data connectors, then gain the ability to efficiently search through all the knowledge related to this client and instantly gain insights into the issues that were solved. Go beyond keyword searching and unlock the power of your data.

# Data is King

When you think about artificial intelligence and data, some terms may come bubbling to the surface. Terms like *tagging* and *labelling* and data lakes and training and just loads of stuff that probably means a lot more to a data scientist than it does to an executive. If I say instead - Google Doc, Powerpoint Slide, Teams Conversation, Confluence Page, or Website - That is much easier to reason about. Now I won't lie to you, your "unstructured data" still needs to be "massaged" into being useful, but the field has come a long long way.

## Indexing

When it comes to making all these different data sources searchable, indexing is the key component. Amazon Kendra makes it easy to index your data sources. To create an index, you just add the data source as a connector. You can also manually add documents to the index, but adding a connector ensures the continued syncing of the sources. A connector can be any of the things I have previously mentioned, and [the list](https://docs.aws.amazon.com/kendra/latest/dg/hiw-data-source.html) continues to grow. Once you have created an Index, which can contain any number of data sources, you are ready to start searching!

## Queries and Retrievals

Queries in Amazon Kendra are performed against an index. The returned "answer" is usually a text blurb, the original document, and you can even leverage large language models to further "synthesize" the relevant information. Amazon Kendra can also be used in conjunction with a Large Language model to improve the returned results of said model by leverage Retrieval Augmented Generation using the Retrievals API. In other words, your Generative AI can search for information using Amazon Kendra and then plop the relative bits into the promopt before performing inference. I cannot overstate the power of these tools being used in these ways, it truly is an incredible time to be alive.

# Making things Better

There are a number of different mechanisms and best practices to improve your "enterprise search capabilities" while leverage Amazon Kendra. The true power here comes from Document Meta Data. Now you may be saying "hey, I thought you just add a connector and go!" - and while this is true, there is just so much more to unlock by leveraging the documents meta data. Let's take a look.

## Feedback Loops

When a user performs a search using Amazon Kendra, they have the ability to *rate* the response. This can aid in keeping document relevance fresh, and ensure that users have a play in the ongoing evolution of the quality of the search. This is called incremental learning. In addition to feedback loops, Amazon Kendra Admins can boost certain document attributes, such as `last_updated_date`. Adjusting the waits of different attributes can "tune" the intelligence of the look up. Lastly, Amazon Kendra will keep indexing newly added documents and updated documents, making sure the knowledge base is always fresh.

## Enrichment Features

Amazon Kendra enrichment features can achieve many cool things. You can have documents stripped of Personal Identifiable Information (PII) to make sure sensitive data stays hidden. You can run Optical Character Recognition on screenshots and images, so that pictures of text, or scanned documents can also be part of the search results. You can even translate text, allowing knowledge bases that are in different languages to be combined and leveraged together, as one unified knowledge base.

## Amazon Comprehend

If the incremental learning and enrichment features were not enough already, try throwing Amazon Comprehend into the mix. Amazon Comprehend is a Natural Language Processor (NLP). When leveraged alongside indexing in Amazon Kendra, the results are truly amazing to behold. Amazon Comprehend can look at the documents that are being indexed and then "extract" entities from them. This can include people, places, things - think high-level key words being categorized and organized. The output of Amazon Comprehend can then be fed into the indexing process that Amazon Kendra uses, drastically reducing the time it takes to get awesome attributes attached to your documents that actually matter and make sense. Here is a quick outline of what that looks like in practice: 
1) put documents into s3 bucket.
2) run Amazon Comprehend against the bucket, using entity analysis.
3) Create an index in Amazon Kendra, using the entity analysis as an input into the custom attributes. 
4) Be amazed by the search capabilities now provided.

# Deployment, Cost and Security

## Deployment

An Amazon Kendra Search Experience can be deployed in a number of different ways. You can make it a component or page of a react app using their SDK. You can perform search from the AWS Web Console. You can also leverage the Amazon Kendra Experience Builder, a no-code drag and drop solution for building out your search experience. 

## Cost

Cost is controlled by using "capacity units". This is a fixed amount of storage and request per second. Additional storage and requests can be added by increasing the capacity units. This makes it easy to understand and somewhat easy to optimize. The "Gotcha" is that if you use a lot of storage, but not a lot of requests, or vice versa, then you are not really very optimized. Amazon Kendra provides a wealth of different monitoring options so that you can get it to the proper level.

## Security

It may be scary passing all of your documents and conversations over to a "black box" service such as Amazon Kendra. Rest assured that it is as secure, if not more so, then most of the tools you are probably already using. Documents and indexes are encrypted at rest, information is encrypted in transit, and you can even leverage a VPC endpoint combined with AWS Private link to keep everything off the internet. 

# Conclusion

In conclusion, Amazon Kendra is a game-changer for knowledge management. It offers a holistic solution to the challenges posed by the ever-expanding knowledge landscape. With powerful indexing, efficient querying, collaboration features, and robust security, Kendra empowers organizations to harness their knowledge like never before. Whether you're looking to streamline internal knowledge sharing or enhance your customer-facing search experience, Amazon Kendra is the answer. If you are experiencing issues putting your data contained within your various *knowledge repositories* to work, then drop us a line `sales@ipponusa.com`. We would be happy to lend you a hand.
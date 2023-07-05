---
authors:
- Pooja Krishnan
tags:
- Data
- Snowflake
- Snowflake Summit 2023
date: 2023-06-28T13:33:00.000Z
title: "Snowflake Summit 2023 Roundup"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2023/06/snowflake_summit_2023_roundup.png
---

# Snowflake Summit 2023 Roundup

Snowflake Summit is the world's largest data, apps, and AI conference. This year, hosted in Las Vegas, Nevada, from June 26 - 29, 2023, Snowflake showed off all of the latest advancements coming to the Snowflake Data Cloud - including generative AI and LLMs, Apache Iceberg, application development, and more. Here are some of the discussions and new announcements that I found the most impactful.

## Generative AI

This is a big buzzing topic in the industry right now. Everyone wants to know how they can use Generative AI in order to revitalize and revolutionize their business and offerings. To that extent, in a fireside chat, Jensen Huang - Founder and CEO of NVIDIA, and Frank Slootman - Chairman and CEO of Snowflake, discussed trends in Generative AI and accelerated computing. Some of the discussions include the rapid rise of AI, the revitalization of enterprise from Customer Support to problem-solving, and how every developer is likely to become an Application Developer.

Want to know more? Check out my blog on this session (here)[https://blog.ippon.tech/snowflake-summit-2023-replay-generative-ais-impact-on-enterprise-data-innovation/]!

## Unified Iceberg Tables

Apache Iceberg is a high performance open table format for large analytics datasets. With Iceberg you can bring the simplicity of SQL to big data wwhile allowing engines like Spark, Presto and Hibe to work with the same tables at the same time. In 2022, Snowflake partnered with Iceberg to combine Snowflake's unique capabilities with Apache Iceberg and Parquet to help customers solve some common challenges they face with big data: control, cost, and interoperability. This year, Snowflake is deepening the partnership by introducing Snowflake Iceberg Tables, which will extend Snowflake's performance and governance to data stored in open formats.

Want to know more? Check out my blog on Unified Iceberg Tables (here)[https://blog.ippon.tech/snowflake-summit-2023-replay-iceberg-tables/]!

## Platform Improvements

I know, I know. It seems like we're taking a bit of a left turn from talking about all of the new announcements to Snowflake and the data processing industry as a whole. Just as important as the new features released, are the platform improvements Snowflake has been working on. During Summit this year, Snowflake announced a slew of platform updates, including the publich launch of the Snowflake Performance Index, the expansion of the Search Optimization Service (SO), and the introduction of TOP-K analytics.

I detail all of the new Platform Performance updates (here)[https://blog.ippon.tech/snowflake-summit-2023-replay-snowflakes-put-the-customer-first-policy-improving-customers-price-for-performance/]!

## Snowpark Container Services

Another key announcement from Snowflake this week was the announcement of Snowpark Container Services. With Snowpark Container Services, you can now run data applications full stack in any language you'd like. The best part? You can package, distribute, and even monetize the applications you build in Snowflake by publishing them to the Snowflake Marketplace.

I dive a bit deeper into Snowpark Container Services (here)[https://blog.ippon.tech/snowflake-summit-2023-replay-snowpark-container-development/]!

## Document AI
SSnowflake acquired Applica, a small company offering an AI-based text and document automation platform, late last year. Now, Snowflake has announced the launch of Document AI, now available in private preview. A new large language model (LLM), built from Applica's generative AI technology, Document AI, can help customers understand documents and put their unstructured data to work. Document AI allows you to ask questions in natural language from documents you have stored in Snowflake and operationalize an end-to-end pipeline in which you can provide feedback on the answers the model gives you to fine-tune and retrain it.

![The Data Processing Pipeline involving Document AI as Snowflake sees it](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2023/06/snowflake_summit_2023_roundup_data_processing_pipeline_document_ai.png)

During the demo, Snowflake's Polita Paulus discussed a snow goggle company called Ski Gear Co., a company that produces Ski Goggles. They want to ensure they manufacture the goggles on time and in the required quantity. But recently, they've been having problems with their injection molding machine. Polita would like to analyze the forms used to inspect the goggles to understand the issues. To start the process, Polita built a new project and loaded about a year's PDF inspection reports directly into the Snowflake UI. The documents contain a mix of fields and free text. Analyzing these will either be error-prone and time-consuming or require ML expertise which Ski Gear Co.'s team does not have. That's where Document AI comes in. To extract the information, you only need to add questions in plain text to the Training window in Document AI. 

![Sample Document Processing with Document AI](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2023/06/snowflake_summit_2023_roundup_document_ai_sample_doc.png)

Polita doesn't stop there. Like with all AI model, we sometimes get an incorrect answer when adding a new question as we're training the model. The cool part of Document AI is that the user can go in and manually correct the answer, providing feedback to Snowflake on how we can fine-tune the model, press a button to re-train, and then re-publish the model.

Once the model has been published, it can be shared with other teams who can also use the new fine-tuned model. We can run the model on all inspection documents with a simple SQL query. Examining the results of that query, Polita showed us how it seems like every three months or so, the injection molding machine failed its inspection. She can share these insights with the quality engineering team. They can use this to run inspections every two to three months so the inspections stop failing. 

Now, to enhance the model to process any new documents for inspections that come in. Since this model is fully integrated into the Snowflake Platform, we can create a pipeline using streams and tasks to process new documents as they come in. Every time a new document comes in, you can set up an email alert to notify you when an inspection fails. You can even use Document AI to parse text-heavy documents.

Pretty cool, right? I can't wait to see how Document AI grows and learns over the upcoming weeks, months, and years!

## Snowflake's Native Application Framework
Another announcement coming out of the Keynote address this year is the release of the Snowflake Native Application Framework. With the Native Application Framework, applications can use Snowflake core functionalities to build their applications, then distribute and monetize them in the Snowflake Marketplace. Once an application is in the Snowflake Marketplace, customers can deploy it directly in their own environments.

The key advantage Snowflake Native Applications provides is *bringing your application to your customers' data*. With Snowflake, enterprises are already centralizing their data. What if you could create multiple apps on a central copy of data? Snowflake already has a [connected application deployment model](https://www.snowflake.com/blog/powered-by-snowflake-building-a-connected-application-for-growth-and-scale/), wherein multiple applications can connect to the same data in a customer's data platform. With the Native Application Framework, Snowflake is taking connected applications to the next level, allowing providers to bring application code to their customers' data.

Here's how the deployment model changes with Snowflake's Native Application Framework:

![Snowflake Native Application Deployment Model](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2023/06/snowflake_summit_2023_roundup_native_app_deployment_model.png) [source](https://www.snowflake.com/blog/introducing-snowflake-native-application-framework/)

With the Native Application Framework, you can build, sell, and deploy applications with the Data Cloud. These applications can include UDFs, Stored Procedures, and even Streamlit integration (once this feature is general availability) and can then be sold via the Snowflake Marketplace. The true power of these apps is how they can be deployed once installed in a client environment. Customers retain all control of their data while reducing data silos and improving governance since the data doesn't need to move or be exposed to the application. Additionally, the application procurement cycle can be shortened and simplified to a duration of a few clicks. These applications will be ready to use out of the box. 

Many customers and Snowflake partners are already experimenting with the Snowflake Native App Framework, including Goldman Sachs, LiveRamp, and Capital One. Their apps, currently in development or private preview, cover a wide array of use cases from global data clean rooms, identity resolution, financial transaction analytics, and more. I can't wait to see the wide variety of apps that come onto the Snowflake Data Marketplace with the Native Application Framework!

# Conclusion
These are just six of all of the announcements and discussions that took place over Snowflake Summit this year. I hope this helped you get the lay of the land in the Snowflake Data Cloud with all of the new announcements! I can't wait to see how they grow and change over the upcoming months or how they're going to be used. Who knows? Maybe someone here at Ippon will explore a bit too.

----
For more information on how Ippon Technologies, a Snowflake Partner, can help your organization utilize Snowflake for all of your data needs, contact sales@ipponusa.com.
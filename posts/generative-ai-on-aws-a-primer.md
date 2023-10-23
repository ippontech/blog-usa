---
authors:
- Lucas Ward
tags:
- AI
- AWS
- Data
date: 2016-04-13T10:32:21.000Z
title: "Generative AI on AWS - A Primer"
image: 
---

In this blog I want to highlight two Amazon Web Services Generative AI products, SageMaker and Bedrock. SageMaker enables users to build, train, and deploy Machine Learning models. It has fully managed infrastructure, tools and workflows. Bedrock goes a step further, enabling fine tuning, creation of specialized agents, and Retrieval Augmented Generation (RAG), the last of which sounds kind of like a hot new buzz word. If you do not understand what any of that stuff is or why you might want to implement it into your organization, then stay tuned! 

# Generative AI Simplified

In simple terms, Generative AI promises to "take a bunch of data" and use it gain hidden insights and *put the data to work* in various ways. It does this in a way that is very similar to how our brains work. It looks at all the data in all the different places, then says something based on what it saw. Fine tuning is like going back to school to learn more, and Retrieval Augmented Generation is like reviewing your notes before you attempt to say something. 

The current Generative AI landscape is in high flux, but as time goes on, key services will be identified that aid in the adoption of generative AI. Amazon makes it to where you do not have to be a data scientist, or even a strong programmer to start leveraging AI and enabling your business to be more *artificially intelligent*. The road to adoption involves lots of new tech, new workflows, and *curating* your data.

# Amazon SageMaker

## Bring Your Own Data

If you are part of an organization that has a huge amount of data (any kind of data really) then you can likely find a place where Generative AI makes sense in your product roadmap. This product can be customer facing, or an internal tool. The process to "train" a Generative AI Model, which just means "consume the data and build a statistical representation of it", may start in different areas for different orgs. For instance, some organizations may start from scratch, curating a huge data set that will be used to train a model. 

## Leverage Existing Models

Other organizations may want to leverage off the shelf *foundational models* that they then fine tune on their data set or use their data to perform RAG. Either way, SageMaker and Bedrock have you covered on the processes from data ingestion and curation to model training, deployment and scaling.

## Simplified Data Ingestion and Labeling

Amazon SageMaker has products with the ability to meet your data where it is stored, namely, Amazon SageMaker Data Wrangler. Data Wrangler has loads of connectors to things like s3 buckets, Redshift, Snowflake, etc... However, it's not enough to just "get the data". Sometimes you need to manipulate it to be more "Machine Learning Enabled". This can be achieved with a process called labeling via the Amazon SageMaker Data Labeling product. 

## Industry Standard Tooling

If you already employ data scientists, or are looking to hire one, Amazon SageMaker will allow them to use the tools they are already familiar with, such as Jupyter Notebooks and PyTorch. Leveraging Amazon SageMaker Data Studio, a integrated development environment with a single web based interface, users can ingest data, train a model, and save the model and generated artifacts to an s3 bucket. These artifacts, things like training sets and validation data, can than be leveraged from within Amazon Bedrock.

# Amazon Bedrock

Once you have created a killer generative AI model, or have decided to use an existing model from off the shelf, like those created by Meta, Amazon, or Anthropic to name a few, then it's time to put the model to work. Amazon Bedrock aids in the process of making your generative AI models do really cool things... Also known as doing "real work". 

## Inference and Beyond

Amazon SageMaker has the ability to expose an endpoint to your model to perform inference, or batch inference. First and foremost, what the heck even is *inference*? When someone asks you a question, you may go inside your head for a little bit and think about the words you want to say and in what order. As the words are coming out of your mouth, they are used to infer what words will be said next. Generative AI works in a similar fashion, taking some text (like a prompt) and spitting out words that it *thinks* should come next. Bedrock takes it a step further with fully managed agents, capable of invoking APIs to perform tasks, orchestrating complex tasks by breaking them up, and Retrieval Augmented Generation.

## Tweaking Foundational Models

Amazon Bedrock allows you to tweak foundational models and change just how that inference happens. You do this by giving it additional training and validation data - called fine tuning. Bedrock gives you the ability to tweak the models hyperparameters. Hyperparameters control some pretty technical aspects of how a model functions, like epochs, batch size, learning rate, and warmup steps.

## Fully Managed Agents

Once the model is working just how you like, you can create an "agent" that has the ability to leverage APIs. In other words, the model gains the ability to do complex business tasks - like schedule travel, create ad campaigns, and more. As an example, imagine you fine tuned a foundational model on every sales campaign and ad campaign that your company has ever ran. Now, you ask the model to create a new ad campaign with x, y, and z as inputs. The model will not only create a similar "feeling" campaign to what it was trained on, but can even launch the campaign in it's "connected ecosystem", via an agent.

# Conclusion

In conclusion, Amazon Web Services offers a powerful suite of Generative AI products that can revolutionize the way organizations leverage data and automate complex tasks. SageMaker and Bedrock provide a comprehensive ecosystem for anyone, regardless of their technical expertise, to harness the potential of artificial intelligence. In review SageMaker will ensure a smooth journey from data preparation to model deployment and Bedrock will enable you to put your generative AI models to work effectively.

The future holds exciting possibilities for those willing to explore and leverage these technologies, and we look forward to unraveling the full potential of Generative AI in the years to come. Stay tuned for more insights on how these innovations can transform your organization into a more artificially intelligent and efficient entity. If you work for an organization with big generative AI ambitions and want to leverage the expertise of Ippon Technologies Consultants, then drop us a line at sales@ipponusa.com.
---
authors:
- Lucas Ward
tags:
- AI
- AWS
date: 2016-04-13T10:32:21.000Z
title: "Generative AI on AWS - A Primer"
image: 
---

aws sagemaker - build, train, and deploy ML models for any use case - fully managed infrastructure, tools, and workflows
- jupyter notebooks
- data labeling and transformation
- training (use their algorithms or bring your own)
- Apply sagemaker services to your training process (like hyperparameter optimization service) or (SageMaker Model Endpoints Auto Scaling)
- model evaluation
- deploy as endpoint for realtime inference or batch inference
- monitor it - retrain when necessary
- security features
- can integrate with Lambda, API gateway, and step functions, etc...

aws bedrock - the easiest way to build and scale generative ai applications with foundation models
- Use existing models
  - ai21 labs, anthropic, stability AI, etc...
- customize FM with existing training data and validation data sets (stored in s3) - a.k.a. - the stuff that sagemakers spits out along the way - this is fine tuning 
- Can create fully managed agents to invoke APIs (a.k.a. put the model to work)
- Knowledge bases for amazon bedrock - Retrieval Augmented Generation - a.k.a. add your private org data to the prompt

aws sagemaker pipelines

aws sagemaker mlops

AWS Kendra
- intelligent search
- llms + information data sources (slack, crm, etc...)
- can power chat bots (aws lex)
- can use a s3 bucket as it's storage source (for indexing
- uses iam and access pocilies)
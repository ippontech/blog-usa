---
authors:
- Malcolm Thirus
categories:
- 
date: 
title: "Alexa is not built for conversation"
image: 
---

Today’s proof of concept is going to be the ultimate FAQ section. The idea is a combination of services into something akin to the holographic system from the 2004 film iRobot.
Any user can ask various questions to get a proper response.
All questions are recorded for later auditing.
Any question without a proper answer can be reviewed and updated with an answer.
The set up is not as hard as it seems. We’ll combine Amazon Alexa with a backend AWS Lambda service that searches for your question in ElasticSearch and responds with the closest match over a certain relevance. The question gets added to DynamoDB for auditing that we can reach via a simple web application. Bonus points if we can update new questions from the Web App and a gold medal if I can update it via Alexa.
To start off, I wanted to get my infrastructure set up first before getting into the meaty things like scoring and updating new questions on the fly.
[DIAGRAM OF ES, LAMBDA, DYNAMODB, AND ALEXA]
My model for the Questions will only be the question and the answer. A more detailed project would have more things like timestamps and what user asked what, and so on.
At the center of the whole application is our lambda function. When given a string input (the parsed text taken from Alexa), it does a query on ElasticSearch and returns text back to Alexa if and only if the returned data matches the question over a certain relevance score. If not, we’ll have Alexa apologize for not having an answer and we’ll store that for later.

~~~~~~~~

So after a long time theorizing and looking into a lot of workarounds, you can’t easily make a bot to do all of that. Specifically, this page here explains why you can’t define something that accepts all answers. https://developer.amazon.com/docs/custom-skills/custom-interaction-model-reference.html#literal
In short, working in Alexa doesn’t allow you to pull free-form text from the user. You can use a built-in slot type

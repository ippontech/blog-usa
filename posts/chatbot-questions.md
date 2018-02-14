---
authors:
- Malcolm Thirus
categories:
- chatbot
date: 2017-11-29T20:31:12.000Z
title: "Questions about Chatbots"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/11/Chatbot-Evolution-1.png
---

# What is a chatbot?
Since the early days of IRC back when chat rooms first started appearing, chatbots have assisted us in all manner of things. The concept is simple but infinitely powerful: when someone says the correct words, respond accordingly. With the continued growth of social applications and the expanding usage of Voice Assistants like Alexa, Cortana, and Google Assistant; these Bots are becoming even more of a hot commodity.

# Why are chatbots popular?
![Stacked Bar graph of Social Media usage indicating a substantial number of users check their social media daily or weekly](http://assets.pewresearch.org/wp-content/uploads/sites/14/2016/11/10123456/PI_2016.11.11_Social-Media-Update_0-07.png)


The graphic above, provided by [PEW Research Center of Internet and Technology](http://www.pewinternet.org/2016/11/11/social-media-update-2016/) provides a compelling picture of how often social media users are actively online. According to Statista, [Facebook had 2.07 billion monthly active users as of the third quarter of 2017](https://www.statista.com/statistics/264810/number-of-monthly-active-facebook-users-worldwide/). With those numbers, chatbots have the potential to reach countless users daily. The ease of being able to communicate quickly for what you need is invaluable as a convenient feature. The value of providing such ease of use to customers, encouraging them to come back, and altogether having meaningful interactions has fueled this market trend.

# Where are chatbots used?
Chatbots are built where the people are. In fact, that’s their purpose: an assistant that’s always nearby that is available to answer your questions and take care of tasks for you. Chatbots are built into messaging platforms like Kik, Slack, Skype, LINE, and Telegram. They’re built into Twitter and Facebook Messenger. Chatbots are the very backbone that makes Alexa, Cortana, Google Assistant, and Siri function! Every time you use the built-in assistant in your smartphone, you are communicating with a chatbot. The chatbot translates what you say into text and responds accordingly to simulate conversation. 

# Who is building bots?
With the unprecedented growth of chatbots assisting users in their day to day lives, many tools have entered the market to streamline this development.

IBM’s Watson is the oldest and arguably the most refined; Watson Conversation was designed to handle open-ended questions backed by a repository of information. Watson Conversation is only one part of the Watson stack, however, it can be used to build an intelligent chatbot connected to your own services.

In 2015, Facebook acquired Wit.ai, a website designed around simplifying the usage of natural language processing by making it into a free service available to developers. As of July 2017, Facebook has integrated Wit.ai directly into its Messenger platform.

Google offers a similar free service called DialogFlow (previously Api.ai) that handles entity recognition, intent determination, as well as integration into your application of choice.

# How are chatbots built?
To build a full virtual assistance bot, input needs to be taken in, translated from its raw form into entities and intent via Natural Language Processing, then acted upon via external services and databases. The first step is handled by the platform the chatbot is deployed to (ie. Facebook Messenger, Cortana, etc). The external services and databases are all in-house applications. Services like DialogFlow, Wit.ai, and Alexa's developer portal work on translating the text/speech from the input into actionable items to send to the external services. 

As an example, imagine a scenario where someone sees an advertisement for a new product from their favorite store. They ask their assistant if their local store has any of the product in stock. Based on the user's profile and location, the assistant checks local inventory for more information. It can then respond in the affirmative with both the location and a follow-up question as to whether the user would like the store to hold it for them. If the product isn't available nearby, the assistant can instead offer to order it online or add it to the user's cart for later perusal.

In the language of ChatBots, you define an entity that contains a list of products sold by the company. Then you create an Inventory Intent that listens for when a user asks if that entity is available. When input received is matched to an Intent with its Entities, those are sent to your backend service. That service would then respond back with what to say enriched by company data, simulating a conversation with its user. Separately, the developer would define a Purchase Intent that listens for anything that sounds like a purchase.

# Resources
[DialogFlow provides the best visual for understanding chatbots](https://dialogflow.com/docs/getting-started/basics), in my opinion.

Here are a few tutorials to get you started

* [How to build an app in Alexa](https://developer.amazon.com/blogs/alexa/post/TxKALMUNLHZPAP/new-alexa-skills-kit-template-step-by-step-guide-to-build-a-how-to-skill)
* [...and DialogFlow](https://dialogflow.com/docs/getting-started/building-your-first-agent)
* [...and Wit.ai](https://wit.ai/docs/quickstart)
* [...and Slack too!](https://github.com/mccreath/isitup-for-slack/blob/master/docs/TUTORIAL.md)

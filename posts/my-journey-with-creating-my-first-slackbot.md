---
authors:
- Cory Walker
tags:
- Slackbot
- Slack API
- Node.js
date: 
title: "My Journey With Creating My First Slackbot"
image: 
---

# My Journey With Creating My First Slackbot

My first approach when attempting to develop any type of software project is to locate the owner of the software's documentation that I will be using. As this is probably the ‘go to’ approach for most software developers. What better way to learn about a software product than from the people who actually created it. Even though, in some cases, the software owner’s documentation may be more complex then some other websites explanations, instructions, or use cases. 

However, in the case of creating my first Slackbot, step one for me was to google the keywords "Slack API". After I conducted my google search, I landed on the [SlackApi.com](https://api.slack.com) website. I was pleased to see clear instructions on creating a Slackbot and even a getting started [instructional video](https://youtu.be/Rufh3MjJz9g). After watching and following along with the brief instructional video, I was able to easily connect my Slackbot with my company's Slack App within a few minutes. Then I moved along to the code snippets in the documentation on some of the common things you can do with your Slackbot. 


## Your Slackbot Allows You To Do Things Such As:

**Post a message in your Slack App.**

**Schedule a future post in your Slack App.**

**Retrieve conversation history within your Slack App.**

**Retrieve an individual message within your Slack App.**


I decided to keep it simple and only stick to conguering these four task. There are a few different programming languages or tools you can use to configure your Slackbot such as Java, JavaScript, Python, or HTTP. I chose JavaScript and Node JS as my programming language of choice with VS Code as my IDE. I must admit, the documentation was clear and easy to follow.

Before getting started with the code, I needed to request the following permissions below to accomplish the four task above:

**channels:read**
**channels:history**
**chat:write**
**chat:write:bot or chat:write:user**


## Slackbot Code or Markup


### The markup to [post a message](https://api.slack.com/messaging/sending) to your Slack channel is as follows:

``` javascript
// Post a message to a channel your app is in using ID and message text
async function publishMessage(id, text) {
  try {
    // Call the chat.postMessage method using the built-in WebClient
    const result = await app.client.chat.postMessage({
      // The token you used to initialize your app
      token: "xoxb-your-token",
      channel: id,
      text: text
      // You could also use a blocks[] array to send richer content
    });

    // Print result, which includes information about the message (like TS)
    console.log(result);
  }
  catch (error) {
    console.error(error);
  }
}

publishMessage("C12345", "Hello world :tada:");
```


### The markup to [schedule a message](https://api.slack.com/tutorials/tracks/scheduling-messages) to post at a later date or time to your Slack channel is as follows:

``` javascript
// Unix timestamp for tomorrow morning at 9AM
const tomorrow = new Date();
tomorrow.setDate(tomorrow.getDate() + 1);
tomorrow.setHours(9, 0, 0);

// Channel you want to post the message to
const channelId = "C12345";

try {
  // Call the chat.scheduleMessage method using the WebClient
  const result = await client.chat.scheduleMessage({
    channel: channelId,
    text: "Looking towards the future",
    // Time to post message, in Unix Epoch timestamp format
    post_at: tomorrow.getTime() / 1000
  });

  console.log(result);
}
catch (error) {
  console.error(error);
}
```


### The markup to [retrieve conversation history](https://api.slack.com/messaging/retrieving) from your Slack channel is as follows:

``` javascript
// Store conversation history
let conversationHistory;
// ID of channel you watch to fetch the history for
let channelId = "C24601";

try {
  // Call the conversations.history method using WebClient
  const result = await client.conversations.history({
    channel: channelId
  });

  conversationHistory = result.messages;

  // Print results
  console.log(conversationHistory.length + " messages found in " + channelId);
}
catch (error) {
  console.error(error);
}
```


### The markup to [retrieve an individual post](https://api.slack.com/messaging/retrieving) from your Slack channel is as follows:

``` javascript
// Store message
let message;

// Fetch conversation history using the ID and a TS from the last example
async function fetchMessage(id, ts) {
  try {
    // Call the conversations.history method using the built-in WebClient
    const result = await app.client.conversations.history({
      // The token you used to initialize your app
      token: "xoxb-your-token",
      channel: id,
      // In a more realistic app, you may store ts data in a db
      latest: ts,
      // Limit results
      inclusive: true,
      limit: 1
    });

    // There should only be one result (stored in the zeroth index)
    message = result.messages[0];
    // Print message text
    console.log(message.text);
  }
  catch (error) {
    console.error(error);
  }
}

// Fetch message using a channel ID and message TS
fetchMessage("C12345", "15712345.001500");
```


## My ‘Scheduling A Future Post’ Bug Issue Encounter

I was able to achieve three of the task above with my Slackbot. I was able to post a message, retrieve conversation history, retrieve an individual message from the Slack channel. However, I ran into an issue when it came to scheduling a post at a later date and time. When I attempted to schedule a post, I kept getting an error message. One of the lines of the error message, I deemed important, is below.

`Error: An API error occurred: invalid_arguments`


So, I decided to use my friend Google and see if anyone else may have had this same issue. I was able to locate a small hand full of websites of others who had the same issue but, their solutions didn’t quite work for me. The unusual thing about the issue is the fact that I copied the exact documentation code snippet from the website and placed it in my code, and it still didn’t work.

During my online research, I read an article of someone that appeared to be a Slack API employee or partner that said they are aware of the issue and will update the documentation on their website. However, that didn’t help me at the time, therefore, I concluded, I wasn’t going to get anywhere by just googling the error message.


### How I Was Able To Solve The Scheduling A Future Post Bug Issue

I chose to go another route and do some research and digging into the Javascript date and time function. After my research, I came up with an idea to use an integer for the `post_at:` value, and replace the `tomorrow.getTime() / 1000` in the code snippet below.

``` javascript
// Time to post message, in Unix Epoch timestamp format
    post_at: tomorrow.getTime() / 1000
```

Ah ha, I had a new error message now (see below); one that gave me more details and information than before. Based on the new error message `time_in_past`; that was an indicator for me that I was on the right track or in the ball park. 

`Error: An API error occurred: time_in_past`

Based on the above error message, I took a new path to create an integer of the exact seconds of when I wanted the post to take place. Therefore, I calculated the amount of seconds from 1970, based on the starting year of the [Javascript Date() function](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date), to the date and time I wanted the message to post. Using a calculator or course, I started with 60 seconds in a minute, to the amount of seconds in a day. Then, I multipled the number days I needed times the number of seconds, started from January 1, 1970.  When I gave the `post_at:` value the new integer, there was no error message. I was confident, that I had a winning solution. The next day, my solution was verified when I seen my scheduled post. I included a unique message in the post that stated the date and time I scheduled the post, to make it easier to determine that it was the post I created the day before.

```javascript
    // example integer solution
    post_at: 1631870268
```

## Wrap Up

I’m not sure if this is the only solution or if the Slack API documentation has been updated since the writing of this blog. I’m also not sure if this is an issue when using the other programming languages or tools. All I know for sure is that I was happy to get 'a' solution working. In the meantime, you could create a JavaScript function that takes in parameters to calculate the amount of seconds from Janurary 1, 1970 to the current or future date and time. In a nut shell, that was my journey when creating my first Slackbot with the Slack API.






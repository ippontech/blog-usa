---
authors:
- Cory Walker
tags:
- slackbot
- slack
- node
- nodejs
date: 
title: "getting started with creating a slackbot in nodejs"
image: 
---

# Getting Started with Creating a Slackbot in Nodejs

In this article, we are going to look at how to get started using the [Slackbot](https://api.slack.com/) api and create a slackbot. We will cover the following topics below.

what is this blog about
what is slack

what is a slackbot
slackbot benefits

what is the slack api
why use the slack api  

things we will accomplish in this blog using [NodeJs](https://nodejs.org)
by the end, you should be able do everything we cover

1. retrieve individual message from slack
2. retrieve conversation history from slack
3. post a message in slack
4. schedule a message to post in slack at a later date



## create slack app

-what is the slack app

-install it to your slack

## Setting Up Your Node Session

-imports

```javascript
// Require the Node Slack SDK package (github.com/slackapi/node-slack-sdk)
const { WebClient, LogLevel } = require("@slack/web-api");

// WebClient insantiates a client that can call API methods
// When using Bolt, you can use either `app.client` or the `client` passed to listeners.
const client = new WebClient("xoxb-153765346967-2397356520113-VlkhBeFTOhEwmyBNhBD5J8xY", {
  // LogLevel can be imported and used to make debugging simpler
  logLevel: LogLevel.DEBUG
});
```

-one function per task we want to accomplish

******* post a message m ********
*********************************
*********************************

## Post a Message to a Slack Channel 

### Post Messages to Slack Function

-function components

-each component duties

```javascript
// Post a message to a channel your app is in using channel ID and message text
async function publishMessage(id, text) {
    try {
      // Call the chat.postMessage method using the built-in WebClient
      const result = await client.chat.postMessage({
        // The token you used to initialize your app
        token: "xoxb-153765346967-2397356520113-VlkhBeFTOhEwmyBNhBD5J8xY",
        channel: channelId,
        text: "this is a text aug 20 11:00 meeting",
      });
    }
    catch (error) {
      console.error(error);
    }
  }
```

### Call the Post Message Function ###

```javascript
// call the publish message function
  publishMessage("channelId", "message");
```

******* schedule post -m ********
*********************************
*********************************

## Schedule a Post to Slack at a Future Date

### Schedule Post to a Channel Function

-function components

-each component duties

```javascript
// Schedule a message to a channel your app is in using channel ID and message text
async function publishScheduledMessage(id, text) {
  try {
    // Call the chat.scheduleMessage method using the built-in WebClient
    const result = await client.chat.scheduleMessage({
      // The token you used to initialize your app
      token: "xoxb-153765346967-2397356520113-VlkhBeFTOhEwmyBNhBD5J8xY",
      channel: channelId,
      text: "schedule a message for later",
      // post_at: seem to only take an integer
      post_at: parseInt(Date.now() + 2)
    });
  }
  catch (error) {
    console.error(error);
  }
}
```

### Call the Scheduled Post Function ###

```javascript
// call the scheduled message function
publishScheduledMessage("channelId", "schedule message:");
```

***** conversation history ******
*********************************
*********************************

## Retrieve Conversation History from Slack Channel

### Retrieve Conversation History Function

-function components

-each component duties

```javascript

```

### Call the Conversation History Function ###

```javascript

```

***** retrieve one message ******
*********************************
*********************************

## Retrieve One Individual Message from Slack Channel

### Retrieve Individual Message Function

-function components

-each component duties

```javascript
// Retrieve Individual Message
  async function fetchMessage(id, ts) {
    try {
      const result = await client.conversations.history({ 
        token: "xoxb-153765346967-2397356520113-VlkhBeFTOhEwmyBNhBD5J8xY",
        channel: channelId,
        latest: ts,
        inclusive: true,
        limit: 1
      });
      message = result.messages[0];
      console.log(message.text);
    }
    catch (error) {
      console.error(error);
    }
  }
```

### Call the Individual Message Function ###

```javascript
// call the retrieve individual message function
  fetchMessage("channelId", "1629472690.001100");
```

## Conclusion

Let’s recap, by comparing 'apples' to 'apples' with what we learned in this blog. We learned that the process of getting started with both Mocha and Jest is very similar. Then, when we began the actual testing process, things started to differ slightly.



I must admit, we barely scratched the surface with unit testing in Mocha & Jest. But hopefully you got a decent foundation or at least the concepts of unit testing with both frameworks. Which one would you choose to get started unit testing with?
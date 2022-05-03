---
authors:
- John Strickler
tags:
- Javascript
- AWS Connect
- Agent Servicing
date: 2022-04-29T00:00:00.000Z
title: "How To Integrate AWS Connect"
image: 
---
<!-- 
Log in as an agent
Turn on/off availability
Receive live chats
Receive phone calls
All through a single interface
Integrates directly into your UI
No separate applications to manage.
Simple, Scalable, Managed.

Leverage AWS Connect's Admin backend and workflows. -->

[AWS Connect](https://aws.amazon.com/connect) provides omnichannel communication capabilities directly to customer service agents.  It enables agents to receive calls, chat with contacts and manage interactions from a single user interface.  Connect is developer friendly, easy to integrate and highly customizable.  In this post, I demonstrate how to create and configure an AWS Connect instance and how to integrate it directly to your new or existing web application.

## Creating and configuring the AWS Connect instance

The first step is create your AWS Connect instance.  If you're experimenting then you'll be glad to hear that AWS Connect is covered by AWS's free tier.  You can check out all that is covered for this service [here](https://aws.amazon.com/connect/pricing#AWS_Free_Tier).   Follow these steps to set up your Connect instance:

1. Go to "AWS Connect" in your account and click "Create Instance"
1. Identity Management
   - Select "Store users in Amazon Connect" as your identity management option
   - Enter a name (alias) for your instance
1. Add administrator
   - Select "Specify an administrator".  This is a separate login from your AWS account.  It will be used to log in to and manage your Connect instance.
   - Fill in the remaining fields
1. Telephone Options
   - Allow incoming calls
   - Allow outgoing calls
1. Data storage
   - Keep defaults
1. Review and click "Create instance"
1. After the instance has been created, click the Alias name
1. Select "Approved origins' from the left-side navigation menu
1. Add the base URL where your UI exist.
   - Example - `http://localhost:4200` for local testing on a UI running on port 4200
   - Example - `https://my-dev-url` if you are integrating with a specific url on a deployed environment

## UI Integration with Contact Control Panel

The [Amazon Connect Streams library](https://github.com/amazon-connect/amazon-connect-streams) makes it possible to integrate your web applications with AWS Connect.  It allows for the embedding of a Contact Control Panel ("CCP") enabling you to handle events and access information in real time.

1. Install the Connect Streams library using NPM/YARN.

    ```js
    npm install amazon-connect-streams
    ```

1. Import the Connect Streams library in your application.  

    ```js
    // import this one time in your app
    // it makes the "connect" global variable available
    import "amazon-connect-streams";
    ```

1. Create a placeholder tag to add to your page.  

    ```html
    <!-- Create a placeholder tag -->
    <div id="ccp"></div>
    ```

1. Initialize the control panel

    ```js
    /* initialize the control panel and bind to an element on your page */
    connect.core.initCCP(document.getElementById('ccp'), {
        ccpUrl: 'https://uqwbruudhd7834.my.connect.aws/connect/ccp-v2',
        region: 'us-east-1',
        loginPopup: true,
        loginPopupAutoClose: true,
        softphone: {
            allowFramedSoftphone: true
        },
        pageOptions: {
            enableAudioDeviceSettings: true,
            enablePhoneTypeSettings: true
        }
    })
    ```

1. Hook in to [Connect events](https://github.com/amazon-connect/amazon-connect-streams/blob/master/Documentation.md).

    ```js
    // When an agent is logs in
    connect.agent(agent => {
        console.log(agent)
    })

    // On incoming calls or chats
    connect.contact(contact => {
        console.log(contact)
    })
    ```

## Configuring your AWS Connect Instance

Log in to your newly created Connect instance using the credentials that you set up above in step three.  The first time that you log in you will be presented a Dashboard page with a step-by-step tutorial on configuring and tuning your Cloud contact center. 

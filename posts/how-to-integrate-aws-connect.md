---
authors:
- John Strickler
tags:
- AWS
- Amazon Connect
- Agent Servicing
- JavaScript
date: 2022-04-29T00:00:00.000Z
title: "Integrating Amazon Connect with a Web Application"
image: 
---

In this post, I will demonstrate how to easily integrate [Amazon Connect](https://aws.amazon.com/connect) ("Connect") to any new or existing web application. Connect is AWS' managed solution for enabling omnichannel communication capabilities to your organization. It provides the ability to fully handle human interactions from a single, streaming user interface, including features such as receiving inbound calls, making outbound calls, and having real-time chats.

If you are at all intimidated by this process, don't be! Amazon has done a fantastic job in making Connect easy to get started with by providing a step-by-step setup process with a healthy set of defaults. They built it to be highly configurable and developer-friendly by providing Connect-specific SDKs, APIs, and a robust administrator portal that enables no-code workflows and drag-and-drop configuration.

If you are experimenting, then you will be glad to hear that Connect is covered by the [AWS free tier](https://aws.amazon.com/connect/pricing#AWS_Free_Tier). There is a good amount of free coverage for Connect services. You should not receive any charges by using a baseline setup, but as always, keep a close watch on usage so you can manage any incurred costs.

## Creating and Configuring the Amazon Connect Instance

The first step is to create an Amazon Connect instance. Log in to your AWS account, navigate to the Amazon Connect service, and select "Create instance" from the button in the top right corner. This will bring up a setup wizard. The default settings are enough to get you started, but adjust accordingly based on your needs. Just remember to save the administrator **username** and **password** that you configure because you will be using it later to configure your instance.

![Amazon Connect Instance Creation](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/05/connect-instance-creation.png)

You should now have an instance created and ready with the settings that you specified through the setup process.  Once the instance has launched, you can immediately preview the Contact Control Panel ("CCP") using the following URL `https://<instance_name>.my.connect.aws/ccp-v2/`, replacing `instance_name` with your Connect instance's name.  

![Contact Control Panel](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/05/connect-initial-ccp.png)

The CCP (shown above) represents the managed portion of the interface provided by AWS that allows for online voice and chat. It is the cornerstone for building a streamlined communication platform.

The CCP as a standalone UI has a couple drawbacks. For starters, it is another window that has to be juggled in addition to any existing applications in use by the agent. In addition, we lose real-time event information that we could be leveraging to aid the agent in servicing requests.  The rest of this post focuses on how to integrate the CCP into a new or existing web application, thereby gaining the full benefits of the Connect platform.

## Integrating the Contact Control Panel

The [Amazon Connect Streams library](https://github.com/amazon-connect/amazon-connect-streams) makes it simple to integrate your web applications with Amazon Connect. It provides the functionality to directly embed the CCP to an existing (or new) web application and a robust API for hooking in to real-time events.

For demonstration purposes, I created a Vue.js app and then fully integrated the CCP to my newly-created application.  The end result is a [locally-runnable demo](https://gitlab.ippon.fr/jstrickler/aws-connect-demo). The relevant pieces of the demo application are generic enough so that they can be repurposed to fit any web framework (ReactJS, Angular, etc.).

You can follow these steps to integrate the CCP to any web application:

1. Install the Connect Streams library using npm or yarn.

    ```js
    yarn add amazon-connect-streams
    ```

2. Import the Connect Streams library in your application.

    ```js
    // import this one time in your app
    // the "connect" singleton is available on the global scope
    import "amazon-connect-streams";
    ```

3. Create a placeholder tag to add to your page.

    ```html
    <!-- Create a placeholder tag -->
    <div id="ccp"></div>
    ```

4. Initialize the control panel. Use your Connect instance identifier where it says `instance-id` below.

    ```js
    /* initialize the control panel and bind to an element on your page */
    connect.core.initCCP(document.getElementById('ccp'), {
        ccpUrl: 'https://instance-id.my.connect.aws/connect/ccp-v2',
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

5. Hook in to [Connect events](https://github.com/amazon-connect/amazon-connect-streams/blob/master/Documentation.md).

    ```js
    // When an agent is logs in
    connect.agent(agent => {
        // gather information about the agent
        const agentName = agent.getName()
        const agentContacts = agent.getContacts()
        const agentPermissions = agent.getPermissions()
    })

    // On inbound communication
    connect.contact(contact => {
        // receive contact metadata
        const contactAttributes = contact.getAttributes()
    })
    ```

## Configuring your Amazon Connect Instance

The last step is to specify an **approved origin**. This is the base URL, and hence application(s), that you are giving permission to integrate with your newly-created Connect instance. Follow the below steps to specify one or more approved origins based on your needs:

1. Go to the Account overview page for your instance by clicking the `Instance alias` name from the Instances table
2. Select `Approved origins` from the left-side navigation menu
3. Add the base URL for your web application. Examples include:
   - `https://production-url`
   - `https://development-url`
   - `http://localhost:4200`

Log in to your newly-created Connect instance using the credentials that you set up above in step #3. The first time that you log in, you will be presented a **Dashboard** page with a step-by-step tutorial on how to configure and tune your new Cloud Contact Center.

You should now be able to run your application after setting the approved origins.  The image below shows a runnable demo (source available [here](https://gitlab.ippon.fr/jstrickler/aws-connect-demo)).

![Local Demo](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/05/connect-local-demo.png)

## Experience your new Functionality

Now that you are set up, let's do a brief run-through of the functionality that you have just unlocked.

### Receive a Phone Call

In order to receive a phone call, you first need to **claim a phone number**:

- Log in to your Connect admin console at *https://YOUR_INSTANCE_ID.my.connect.aws*
  - Use the admin credentials from your Connect setup (**_not_** your AWS account creds)
- On the left hand menu, navigate to Routing -> Phone Numbers
- Click "Claim a number" from the top left buttons
- Create either a toll-free or a DID (direct inward dialing) phone number

Now you have a phone number, you are ready to receive inbound calls.  Go to your application that you have integrated with the CCP and set yourself to **available** from the top-most dropdown menu.  Now call freshly-claimed phone number and it will appear as an inbound call!  

![Receive a call](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/05/connect-receive-call.png)

### Receive a Chat

AWS provides a test simulation page for receiving inbound chats.  This is great for our testing purposes.  However, if you want to engage customers through your website, you can do so by embedding [AWS' chat widget](https://docs.aws.amazon.com/connect/latest/adminguide/add-chat-to-website.html) directly to your public website.

The first step to receiving a chat is to mark yourself as **available** on the CCP.  By doing so, you are ready to receive inbound communication.

Next, visit *https://YOUR_INSTANCE_ID.my.connect.aws/test-chat*.  This page will load a simulation to initiate a chat with an agent.  Simply click the chat widget (shown below) and you will receive the inbound request on the CCP.

![Receive a call](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2022/05/connect-chat-simulation.png)

### Receive Contact Events

One of the benefits of embedding the CCP to an application is being able to leverage the Amazon Connect Streams library.  One example of this is simply reading information about the inbound contact.  This is demonstrated below:

```js
// On inbound communication
// https://github.com/amazon-connect/amazon-connect-streams/blob/master/Documentation.md#connectcontact
connect.contact(contact => {

    // is it a chat or a call?
    // ex. "chat" 
    contactType = contact.getType();

    // if its an active session, how long as it been open?
    // ex. 120
    contactDuration = contact.getContactDuration();

    // is the call unanswered, answered, ended (pending close)
    // ex. { "type": "ended", "timestamp": "2022-05-17T13:23:33.462Z" }
    contactStatus = contact.getStatus();

    // which queue did this come from? i.e. was it a high-priority contact?
    // ex. { "queueARN": "arn:aws:connect:us-east-1:xxxxxxxxxxxx:instance/463534cd-1ba4-44f0-acdf-dfec3bd69104/queue/12dd58f7-9772-4e6b-bcc4-cb05b8fdd28c", "name": "BasicQueue", "queueId": "arn:aws:connect:us-east-1:xxxxxxxxxxxx:instance/463534cd-1ba4-44f0-acdf-dfec3bd69104/queue/12dd58f7-9772-4e6b-bcc4-cb05b8fdd28c" }
    contactQueue = contact.getQueue();

    // get capability information.  ex. are file transfers enabled on the client's device?
    // ex. { "attachmentsEnabled": false, "messagingMarkdownEnabled": false, "multiPartyConferenceEnabled": null }
    contactFeatures = contact.getContactFeatures();
})
```

## Wrapping Up

This is just a fraction of what is possible with Amazon Connect.  By going all in with Connect, you now have full control over how to customize your agent and customer workflows by leveraging [all that Connect has to offer](https://aws.amazon.com/connect/features).  Explore your channels of communication, set yours hours of operations, create queues to route contacts, utilize contact flows (IVR) to build your customers' experience, and [much more](https://docs.aws.amazon.com/connect/?id=docs_gateway).

At Ippon, we have extensive knowledge when it comes to agent servicing platforms and [a track record that proves it](https://us.ippon.tech/success-stories/accelerate/top-10-us-bank).  If you need expertise or guidance in this area, don't hesitate to [reach out](https://us.ippon.tech/contact) to us as your trusted partner.

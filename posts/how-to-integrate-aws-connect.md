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



In this post, I demonstrate how to easily integrate [AWS Connect](https://aws.amazon.com/connect) ("Connect") to any new or existing web application. Connect is AWS's managed solution for enabling omnichannel communication capabilities to your organization.  It provides the ability to fully handle human interactions from a single, streamling user interface including features such as receiving inbound calls, making outbound calls, and having real-time chats.  

If you're at all intimiated by this, don't be.  AWS has done a fantastic job in making Connect easy to get started with by providing a step-by-step setup process with a healthy set of defaults.  They built it to be highly configurable and developer friendly by providing Connect-specific SDKs, APIs, and a robust administrator portal that enables no-code workflows and drag-and-drop configuration.

If you're experimenting then you'll be glad to hear that Connect is covered by the [AWS free tier](https://aws.amazon.com/connect/pricing#AWS_Free_Tier).  There's a good amount of free coverage for Connect services.  You shouldn't receive any charges by just experimenting, but as always, keep a close watch on usage so you can manage any incurred costs.

## Creating and configuring the AWS Connect instance

I'm going to assume you have an AWS account already.  So the first step is to create an AWS Connect instance.  Log in, head to AWS Connect, and click Create instance.  This will bring up a setup wizard.  The defaults are good enough to get you started but adjust to your needs accordingly.  The most important part is to save the administrator **username** and **password** because you will be using it later to configure your instance.

![AWS Connect Instance Creation](https://github.com/johnstrickler/blog-usa/raw/aws-connect/images/2022/05/connect-instance-creation.png)

You should now have an instance created with the settings that you specified through the setup process.  Once the instance has launched, you can launch the Contact Control Panel ("CCP") using the following URL `https://<instance_name>.my.connect.aws/ccp-v2/`, replacing `instance_name` with your instance's name.

![Contact Control Panel](https://github.com/johnstrickler/blog-usa/raw/aws-connect/images/2022/05/connect-initial-ccp.png)

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

1. Initialize the control panel.  Use your Connect instance identifier where it says `instance-id` below.

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

The next step is to specify an **approved origin**.  This is base URL, and hence application(s), that you are giving permission to integrate with your newly created Connect instance.  Follow the below steps to specify one or more approved origins based on your needs:

1. Go to the Account overview page for your instance by clicking the `Instance alias` name from the Instances table
2. Select `Approved origins` from the left-side navigation menu
3. Add the base URL for your web application.  Examples include:
   - `https://production-url`
   - `https://development-url`
   - `http://localhost:4200`



Log in to your newly created Connect instance using the credentials that you set up above in step three.  The first time that you log in you will be presented a Dashboard page with a step-by-step tutorial on configuring and tuning your Cloud contact center. 

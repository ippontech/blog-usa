---
authors:
- Tyler John Haden
tags:
- aws
- serverless
- security
- blockchain
date: 
title: Integrating Civic into a Static Serverless Website
image: https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/02/civic_jumbo.png
---


This article describes an application that takes advantage of AWS serverless services to provide a static website and anonymously-access-controlled resources. To satisfy the static website hosting, we will use Route53, ACM, CloudFront, and S3. Https should be the standard so we will skip "S3 only" hosting.

Our application will also have some super secret resources that will require our user to supply some form of identity. [Civic](https://www.civic.com/) is a third party secure identity ecosystem, that we can integrate for seamless anonymous (or non anonymous) user management. We can use this identity to uniquely control resource access, and customize the user's experience. Civic uses "Blockchain attestation-based access" instead of relying on username and password, allowing decentralized authorization.

Lambda is a great option to run Civic's server side Javascript SDK, and will also satisfy our _serverless_ fascination. We can employee the [Serverless framework](https://serverless.com/) to spin up an API Gateway, and Lambda function for us. It'll also include a tool for specifying the same SSL certificate we used to secure our static site for our API.

*Disclaimer:* You may incur some small charges to your AWS account depending on how you use the tech stack described in this article. Route53 may charge per Hosted Zone for example. AWS has explicit [billing details and documentation](https://docs.aws.amazon.com/account-billing/index.html#lang/en_us) that you can use to estimate the cost. Generally, serverless is the cheapest way to integrate compute and storage for small applications like this.


# Project Architecture

![missing pic: Civic Architecture](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/02/civic_architecture.png)

From the user's perspective, there are three general steps. 

1. When the user navigates to the static site, Civic's SDK will download either a QR code, or a code for linking to their mobile app if it is a mobile browser.
2. Once the user has scanned the QR code or used the deep link to open the Civic mobile app, Civic can return a signed JWT token unique to the app, user, and scope request. The token itself cannot be used to identity the user as it is essentially a signed UUID generated for every request and must be decoded on the backend.
3. The client now sends this token to the app's API which will be able to exchange the token for the user's data. If it is an anonymous scope request, only the user ID will be returned by Civic. The API backend must now use the user ID to craft the resources sent back to the client.


# Create static website with custom domain and SSL on AWS

There are many ways to accomplish this, and many online resources to help you in this process. I will give an overview, but for more detailed instructions, follow AWS's [instructions for CloudFront static websites](https://aws.amazon.com/premiumsupport/knowledge-center/cloudfront-serve-static-website/).

### Register a domain and set Route53 as it's DNS
   
If you did not get the domain through AWS, check out their [instructions for migrating DNS to Route53](https://docs.aws.amazon.com/Route53/latest/DeveloperGuide/migrate-dns-domain-inactive.html). It essentially involves setting the name servers for your domain to AWS's name servers. That way, we can use route53 to quickly set up CNAME and ALIAS records under that domain.

### Request a certificate for your domain
   
Here are AWS's [instructions for requesting a public certificate](https://docs.aws.amazon.com/acm/latest/userguide/gs-acm-request-public.html). It's important that you add an additional sub-domain to the certificate, i.e. `api.yourdomain.com` or `*.yourdomain.com`. This is necessary for us, since we wish to use sub-domains to differentiate between the static CloudFront frontend, and the API Gateway backend. Now we can use the same certificate for both resources.

   For this project, I'm using [https://color.tylerjohnhaden.com](https://color.tylerjohnhaden.com) for the website and [https://api.color.tylerjohnhaden.com](https://api.color.tylerjohnhaden.com) for the API endpoint. If this app is still up when you are reading this, you can see the demo by going to first link.

### Create an S3 bucket and upload initial static files

Since CloudFront takes the longest to spin up, let us speed up the process by skipping any integration yet. We can put at least an `index.html` in a public S3 bucket. Here is an [intro to getting started with S3](https://docs.aws.amazon.com/quickstarts/latest/s3backup/welcome.html). We will not need to configure static website hosting because CloudFront will take care of that (the S3 built-in hosting does not support SSL for custom domains).

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title>Color Identity!</title>
    <link rel="icon" href="color.png" />
</head>
<body onload="requestAnonymousIdentity()">
</body>
</html>
```

We will define `requestAnonymousIdentity()` later on. And what's a website without a [fun favicon](https://opensea.io/assets/0xaefa27a665d48e19c38437ba7135c8107bb5928f/17)?

### Create a CloudFront distribution with your domain

Using the S3 bucket as an origin, spin up a new distribution. You will need to use the certificate previously created and add the domain you want the site to use as a CNAME in CloudFront.

You will also want to make sure that CloudFront has access to the bucket. This is done through "Origin Access Identity" and you can have CloudFront create a new identity and register it with the S3 origin bucket. Otherwise, you will get an `Access Denied` response when hitting the domain.
   
Finally, to get that domain to route to your distribution, we can add an alias to Route53. The easiest way to do this is to copy the domain name of the CloudFront distribution (not its CNAME), and setting it as the Alias Target in Route53. It should look something like this:

![missing pic: Civic Alias](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/02/civic_alias.png)

p.s. This does not need to be a sub-domain. I just happen to be using the root domain `tylerjohnhaden.com` for something else.

Once created, it will take a while to spin up, so let us continue with the rest of the stack. Uploading a new `index.html` or other resource to S3 takes a few seconds, so we can easily update our client side code later.

# Setup your Civic integration

In order to use the developer tools for Civic, we need to use our identity to create an account and Civic app. [Login](https://integrate.civic.com/login) to the their developer's portal. This will also give you an identity that can be used to test out the app later on. They have straight forward instructions on downloading the mobile app and verifying your identity.

Checkout their [developer's guide](https://docs.civic.com) for an explanation of the service and APIs. They describe the flow using the following graphic.

![missing pic: Civic Flow](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/02/civic_flow.png)

As you can see, they are pretty open about their data flow, which can give confidence to the application owner and users about what and how their data is being shared. Civic integration was designed as a security solution and is intended to be used with confidential and secure applications (so security through obscurity is not an option).

Referencing the above diagram, we will need to implement the "User Agent" and "Application Server" which is where Civic's front and backend SDKs come in. But first, we have to set up our application using Civic's portal.

### Create a new application

![missing pic: Civic Enrollment](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/02/civic_enrollment.png)

This step should be straight forward, following their clean developer interface. All you need will be the name, domain, and a logo to display to users in their mobile app. The name and logo can be whatever you like, and we already went through the trouble of setting up our domain registration.

The domain you whitelist here should be the one used for the static site. Only the client side code will need this origin set to initially retrieve the JWT token. Our API endpoint is not needed since the backend uses app secrets to verify it's identity. Whitelisting domains ensures Civic only sends the QR code to our website.

### Generate app secrets and save them to AWS Parameter Store

Once you have your app configured, you will need to generate keys for use in Civic's SDKs. In Civic's integration portal for your app, click on the "Generate Keys Now" button. It will display several public and several private strings and will prompt you to save them before exiting. **This is the last time you have the chance to save them!** You can always revoke and regenerate new keys, or specify a time-to-live later on.

![missing pic: Civic Keys](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/02/civic_keys.png)

For this project, we will need to securely store the *Private Signing Key*, and the *App Secret*. The *App ID*, which stays constant for our application and is not necessarily secret, is also needed.

Since AWS Lambda will eventually need to use these parameters, let us save these into [Systems Manager - Parameter Store](https://console.aws.amazon.com/systems-manager/parameters) (also so we don't lose them). Both the Private Signing Key and the App Secret should be encrypted at rest. Anyone with those values could capture the JWT token mid-flight and potentially access user's data by pretending to be us.

### Add Civic's client side SDK to our website

Civic's [client side documentation](https://docs.civic.com/#Browser) shows how to integrate their code into your website. Their client side is CDN hosted: [https://hosted-sip.civic.com/js/civic.sip.min.js](https://hosted-sip.civic.com/js/civic.sip.min.js).

To use it we can simply add it into our `index.html`:

```html
<html>
...
<body onload="requestAnonymousIdentity()">
    <script src="https://hosted-sip.civic.com/js/civic.sip.min.js"></script>
    <script src="color.js"></script>
</body>
</html>
```

We also need to add some Javascript to send the scope request, pass it along to our backend (non-existent at the moment), and get back the user's identity data. Using Civic's SDK, we can build out the scope request. 

```javascript
const appId = "pfdsARzpj";
const apiColorIdentificationEndpoint = "https://api.color.tylerjohnhaden.com/identification";

// initialize Civic client
const civicSip = new civic.sip({ appId });

// kickoff function, is run on document load (doesn't have to though)
function requestAnonymousIdentity() {
    civicSip.signup({ style: 'popup', scopeRequest: civicSip.ScopeRequests.ANONYMOUS_LOGIN });
}
```

This particular application does not need any user's personally identifiable data like phone number or email. We can choose `ANONYMOUS_LOGIN` scope request to ensure we don't receive this data. As of writing this article, this feature is still in beta, but the use case is still achievable by simply ignoring the user's data and only passing the unique user ID with any scope. Find more about these options in their [scope request docs](https://docs.civic.com/#ScopeRequests).

> In order for Civic to function as an MFA solution, you must use the userID field returned in the Anonymous or Basic scope request response as your MFA credential. Additional information such as email address or phone number can be requested as needed; however, this data is not necessarily a unique identifier for the Civic user.

After we send our scope request to Civic, the SDK exposes several events that are triggered based on the results or user interactions.

```javascript
civicSip.on('auth-code-received', event => {});
civicSip.on('user-cancelled', event => {});
civicSip.on('read', event => {});
civicSip.on('civic-sip-error', event => {});
```

The `auth-code-received` event is where we want to capture a successful scope request. If you would like, you can add error logging or retries for the other events shown. Civic uses long polling after the QR code is displayed to the user, which you might observe if debugging in the browser.

After we get a successful response, we can parse it for the JWT token. The token will be enough to identify the user, application, and scope, however this information is not available to the user yet. We have to pass it back to Civic to "decrypt" this information.

```javascript
civicSip.on('auth-code-received', event => {
    // receive jwt token from Civic
    const anonymousIdentityToken = event.response;

    console.info(`Civic anonymous identity token: ${anonymousIdentityToken}`);
    console.info(`Decoded token: ${JSON.stringify(JSON.parse(atob(anonymousIdentityToken.split('.')[1])), null, 4)}`);

    // pass the token to our backend for processing
    getColorIdentity(anonymousIdentityToken);
});
```

The decoding of the token above is just for debugging. You can see how we can't yet access the identity information inside the token. Civic requires the token to be verified on the backend before sharing the user's data. It just so happens that in this use case, we eventually pass back the user ID but we don't have to.

![missing pic: Civic JWT Token](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/02/civic_jwt_token.png)

We can get a little bit of insight into the token by decoding, like that it expires after 30 minutes. Here, `codeToken` will eventually be converted into our user id. For more information on JWT in general, checkout the [jwt.io docs](https://jwt.io/introduction/).

Next, we send this token to our backend. There is nothing wrong with passing the token back in the body, but the standard way is using the `Authorization` header. This also makes it simpler for us to use AWS Lambda Authorizers (although they can use custom locations for the token). 

This can be accomplished with a simple GET request with authorization credentials specified.

```javascript
function getColorIdentity(token) {
    fetch(apiColorIdentificationEndpoint, {
        withCredentials: true,
        credentials: 'include',
        headers: {
            'Authorization': token,
            'Content-Type': 'application/json'
        }

    }).then(response => response.json()).then(identities => {
        console.info(`Received anonymous identity information from API: ${JSON.stringify(identities, null, 4)}`);

        // convert integer into hex color format
        const hexValue = '#' + identities.yourColorIdentity.toString(16).padStart(6, '0');

        // display color
        document.body.style.backgroundColor = hexValue;

        setTimeout(() => {
            alert(`Your personal identity is ${identities.yourUserIdentity}!\n\n` +
                  `Your color identity is ${hexValue}!`);
        }, 20);

    // or display error
    }).catch(err => console.error(err));
}
```

Our backend will turn the Civic JWT token into an anonymous user identity, and color identity. The user ID is just for debugging, and our use case uses the color identity to set the background color of the web page.

The Javascript [Fetch API](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API) and most HTTP request APIs abstract away the CORS negotiation. Since our backend is at a different origin (even sub-domains count as cross-origin), there is a request to `OPTIONS` that will return various important headers agreeing with our credentials and cross-origin request, more on this in the next section.

### Upload new static files

With `index.html` and `color.js` finished, upload these to your S3 bucket we created before. You may want to [invalidate your CloudFront cache](https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/Invalidation.html) to speed up seeing the new site populate. 

To test our latest static web site, navigate to it in a browser. You should get Civic to respond with a QR code, but the request to our backend will fail. Now we can continue on to writing our API!

# Create a backend to verify identity

The way we designed our architecture, the backend API is a completely separate component from our static website. This offers several advantages and is common in a serverless architecture. Of course there is only one API call needed so it is not a great concern, but scalability should always be considered.

Our solution is to use AWS Lambda to both interface with Civic and to perform our backend "color processing". Writing a special [Lambda authorizer function](https://docs.aws.amazon.com/apigateway/latest/developerguide/apigateway-use-lambda-authorizer.html) will best suite our needs for interfacing with Civic, although we could use one function for the entire backend. Isolating our app secrets to one runtime, and abstracting away Civic is desirable and will increase extensibility if we were to add more functionality to the app later on.

Using the Serverless framework, we can specify both functions, setup CORS, and even auto configure our custom domain in the API Gateway. For a more general tutorial on Serverless, checkout [the first steps with the Serverless Framework](https://blog.ippon.tech/first-steps-with-the-serverless-framework/) or you can get more information on [using JWT tokens as authentication in a serverless environment](https://yos.io/2017/09/03/serverless-authentication-with-jwt/) if you would like.

### Start a Serverless application

To start, install the [Serverless npm package](https://www.npmjs.com/package/serverless). I won't go into detail about npm or Node (maybe [this article](https://www.w3schools.com/nodejs/nodejs_npm.asp) would be a place to start).

`npm install -g serverless`

p.s. You will be able to use `sls` and `serverless` commands interchangeably.

We could use a template to generate the directory and config file, which would take the command `sls create --template aws-nodejs --path ServerlessCivicApp`, but we're gonna change almost everything generated. You could see Serverless's [list of templates](https://github.com/serverless/serverless/tree/master/lib/plugins/create/templates) for possible template options. They support a large range of languages and hosting services.

Our project structure should look like the following.
```text
ServerlessCivicApp: 
  - functions
      - auth.js
      - color.js
  - package.json
  - serverless.yml
```

`package.json` just needs to keep track of our dependencies. (don't forget to `npm install`!)

```json
{
  "name": "serverless-civic-app",
  "version": "1.0.0",
  "dependencies": {
    "civic-sip-api": "^1.1.0",
    "jsonwebtoken": "^8.4.0"
  },
  "devDependencies": {
    "serverless-domain-manager": "^2.6.13"
  }
}
```

`serverless.yml` lets us configure the functions, environments, AWS settings, etc. [This Serverless article](https://serverless.com/framework/docs/providers/aws/events/apigateway/) is probably the best resource for understanding the layout of this file. 

```yaml
service: my-serverless-civic-app

provider:
  name: aws
  runtime: nodejs8.10
  stage: dev
  region: us-east-1

functions:
  color-identification:
    handler: functions/color.colorIdentification
  custom-civic-authorizer:
    handler: functions/auth.customCivicAuthorizer
```

Here is the core of the config file. We will be adding more later too.
- `service` is just the title that is used to generate various resource names in AWS. It should be descriptive and will tie each deployment together (if you keep deploying from the same project).
- `provider` lets Serverless know where to deploy our resources. `name` and `runtime` are the only ones required, as the others have defaults. 
- `functions` lists out our lambdas. We only have two so you can see where Serverless picks up the function handlers. Those functions will need to be exported from each file as we will see later.

### Write our Lambda authorizer

A good resource that AWS provides for learning how to define Lambda functions using NodeJS can be found [here](https://docs.aws.amazon.com/lambda/latest/dg/nodejs-prog-model-handler.html). The main pieces of information that we need to know are what the function signatures should be and how to return or error out of the function. All of the code here will be taking advantage of [ES2017 async and await](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/async_function), but there is no problem with refactoring with promises and callbacks.

We will be using [Civic's server side SDK](https://www.npmjs.com/package/civic-sip-api) which comes as an npm package. First, we need to setup a Civic client for sending tokens. This can be done outside of the function handler itself because multiple calls could use the same client without issue. Importing `jsonwebtoken` is just used for debugging the incoming tokens and is not required for this particular authorizer. Here is the start of `auth.js`:

```javascript
'use strict';

const civicSip = require('civic-sip-api');
const jwt = require('jsonwebtoken');

const civicClient = civicSip.newClient({
    appId: process.env.CIVIC_APP_ID,
    appSecret: process.env.CIVIC_APP_SECRET,
    prvKey: process.env.CIVIC_PRIVATE_SIGNING_KEY,
});

exports.customCivicAuthorizer = async event => {};
```

We are going to use environment variables for injecting app secrets and the app id. This follows best practices and Serverless can easily pull these values from our AWS Parameter Store. The asynchronous function `customCivicAuthorizer` will be used as the actual Lambda function handler. In `serverless.yml`, we need to link the Parameter Store values to the function variables.

```yaml
...

functions:
  ...
  custom-civic-authorizer:
    handler: functions/auth.customCivicAuthorizer
    environment:
      CIVIC_APP_ID: ${ssm:/tylerjohnhaden/civic-color/APP_ID}
      CIVIC_APP_SECRET: ${ssm:/tylerjohnhaden/civic-color/APP_SECRET~true}
      CIVIC_PRIVATE_SIGNING_KEY: ${ssm:/tylerjohnhaden/civic-color/PRIVATE_SIGNING_KEY~true}
```

Add the above environment variables. Serverless allows us to add them to the [individual functions, or to the whole stage](https://serverless.com/framework/docs/providers/aws/guide/variables/) but we can improve security by limiting the scope of the app secrets to just the authorizer. The `ssm` links to AWS System Manager Parameter Store, and the `~true` tells Serverless to [use your local credentials to decrypt vis KMS](https://hackernoon.com/you-should-use-ssm-parameter-store-over-lambda-env-variables-5197fc6ea45b).

Let us continue with defining the handler. According to [AWS's authorizer documentation](https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-lambda-authorizer-input.html), the Lambda authorizer input `event` will have the following pattern:

```json
{
    "type": "TOKEN",
    "authorizationToken": "<caller-supplied-token>",
    "methodArn": "arn:aws:execute-api:<regionId>:<accountId>:<apiId>/<stage>/<method>/<resourcePath>"
}
```

Once we get the token from the input, we can pass it directly to Civic. If this fails, we assume Civic rejected the token and return a `401 Unauthorized` response. Without adding CORS headers to this API response, the browser will actually throw a cross-origin error instead of a 401. This is fixed by setting default 4XX response CORS in API Gateway, which we can do using our Serverless config but more on this later.

```javascript
...

exports.customCivicAuthorizer = async event => {
    const { methodArn, authorizationToken } = event;
    
    console.info(`Received Civic jwt token from client: ${authorizationToken}`);
    console.info(`Decoded token: ${JSON.stringify(jwt.decode(authorizationToken, {complete: true}), null, 4)}`);
        
    let identityData = null;

    try {
        identityData = await civicClient.exchangeCode(authorizationToken);

    } catch(error) {
        console.error(`civicClient.exchangeCode threw on await: ${JSON.stringify(error)}`);
        throw new Error('Unauthorized');
    }
    
    console.info(`Received identity data from Civic: ${JSON.stringify(identityData, null, 4)}`);
};
```

A successful response will be different depending on the scope request. The `ANONYMOUS_LOGIN` will return the user ID as the only form of identity inside `identityData`.

```json
{
  "data": [
    {
      "label": "verifications.levels.CIVIC:IAL1",
      "value": "CIVIC:IAL1",
      "isValid": true,
      "isOwner": true
    }
  ],
  "userId": "c6d5795f8a059ez5ad29a33a60f8b402a172c3e0bbe50fd230ae8e0303609b42"
}
```

Use `BASIC_SIGNUP` or `PROOF_OF_IDENTITY` to get user information such as phone number, email, address, and other PII. Civic has a full [KYC platform](https://www.civic.com/solutions/kyc-services/) for sharing identity documents and other personal artifacts.

In our authorizer, all that is left is to check that `isValid` flag and return the `userId`. However it is not that easy inside a Lambda authorizer...

>A Lambda authorizer function's output must include the principal identifier (principalId) and a policy document (policyDocument) containing a list of policy statements. The output can also include a context map containing key-value pairs. If the API uses a usage plan (the apiKeySource is set to AUTHORIZER), the Lambda authorizer function must return one of the usage plan's API keys as the usageIdentifierKey property value.

Our response should follow [these specs](https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-lambda-authorizer-output.html):

```json
{
  "principalId": "yyyyyyyy",
  "policyDocument": {
    "Version": "2012-10-17",
    "Statement": [
      {
        "Action": "execute-api:Invoke",
        "Effect": "Allow|Deny",
        "Resource": "arn:aws:execute-api:{regionId}:{accountId}:{appId}/{stage}/{httpVerb}/[{resource}/[child-resources]]"
      }
    ]
  },
  "context": {
    "stringKey": "value",
    "numberKey": "1",
    "booleanKey": "true"
  },
  "usageIdentifierKey": "{api-key}"
}
```

We won't need `usageIdentifierKey` because our API isn't using a usage plan with keys. `context` can be any non-object mapping and that is where we will return our anonymous user id. Now we can finish our authorizer handler with the following:

```javascript
...

exports.customCivicAuthorizer = async event => {
    ...
    
    if (
        identityData &&
        identityData.data &&
        identityData.data.length &&
        identityData.data[0].isValid
    ) {

        return {
            principalId: identityData.userId,  // whatever our app uses, helps track usage
            policyDocument: {
                Version: '2012-10-17',
                Statement: [
                    {
                        Action: 'execute-api:Invoke',
                        Effect: 'Allow',
                        Resource: methodArn,  // parsed from the input event
                    }
                ],
            },
            context: {
                "anonymousUserId": identityData.userId  // duplicates principalId to be more explicit
            },
        };

    } else {
        throw new Error('Unauthorized');
    }
};
```

We duplicate values for `principalId` and `context.anonymousUserId` so we actually could get away with an empty context. However, this makes it a little more explicit what data we are passing between Lambdas.

### Write our main Lambda function

Now we can take care of the core functionality of our backend. The goal is to take a unique user ID and turn it into a unique color id. It would also be nice if this function was one-way so that once a user know's their user id, they still need our service to calculate their color id. This is easily done by hashing the user ID with a server side secret and then casting to a 24 bit integer (3 bytes for RGB).

The setup is the same as the authorizer, except we won't need any Civic code.

```javascript
'use strict';

const crypto = require('crypto');

exports.colorIdentification = async event => {};
```

`crypto` is built into Node so we do not need to install it. Its hashing library will help us with the one-way requirement.

The `event` input is different than for the authorizer, and will be specific to [AWS Lambda proxy integration](https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-input-format). All we need is the context that was add by our authorizer which we will find in `event.requestContext.authorizer`.

```javascript
...

exports.colorIdentification = async event => {
    const { requestContext: { authorizer: { anonymousUserId } } } = event;

    try {
        // hash the user's unique ID and our secret together to generate the user's unique color hash
        // ps Node's crypto doesn't store intermediate state so we can't "cache" the secret part :(
        const colorIdDigest = crypto
            .createHash('sha256')
            .update(process.env.CIVIC_PRIVATE_SIGNING_KEY, 'ascii')
            .update(anonymousUserId, 'ascii')
            .digest('hex');

        // take last 24 bits of entropy and cast to integer as color identification
        const colorId = parseInt(colorIdDigest.slice(-6), 16);

        console.info(`Success! Turned userId: ${anonymousUserId} into colorId: ${colorId}`);

        return {
            statusCode: 200,
            headers: {
                'Access-Control-Allow-Credentials': 'true',
                'Access-Control-Allow-Origin': 'https://color.tylerjohnhaden.com',
            },
            body: JSON.stringify({
                yourUserIdentity: anonymousUserId,
                yourColorIdentity: colorId,
            }),
        };

    } catch (error) {
        throw new Error(`Unable to hash userId: ${anonymousUserId} into colorId because: ${JSON.stringify(error)}`);
    }
};
```

Notice the headers returned if successful. These only need to be added if using proxy integration vs standard Lambda. Set the origin to where ever the static site is served at.

We are using the same signing key from the Civic integration to hash into the color id. This could be any server side secret, but it is already available to us. Now we need to add it to the function environment, along with a few other configuration details.

```yaml
...

functions:
  color-identification:
    handler: functions/color.colorIdentification
    events:
      - http:
          path: 'identification'
          method: get
          authorizer: custom-civic-authorizer
    environment:
      CIVIC_PRIVATE_SIGNING_KEY: ${ssm:/tylerjohnhaden/civic-color/PRIVATE_SIGNING_KEY~true}
  ...
```

`events` will be exposed by the API Gateway. `custom-civic-authorizer` is the name of the other function specified in this file. This can also refer to a Lambda not specified here, you will just have to use the ARN of the Lambda.

### Configuring CORS for our back end

So far there have been two places that CORS came up. When we set the API endpoint on the frontend we used a sub-domain, although we did not need to add anything special to the headers except `Authorization`. Later, when building the Lambda proxy response, we included `Access-Control-Allow-Credentials` and `Access-Control-Allow-Origin` as headers. However these headers will only be returned if everything is otherwise successful. 

If our Lambda proxy integration fails at some point, then API Gateway will send back it's own response, i.e. `401` or `500`. These have defaults, but will not include CORS enabling headers. The result is, if we are unauthorized or there is a server error, the browser will display a CORS error instead of the proper descriptive response. Serverless lets us change the default responses just like we could do in the API Gateway console.

```yaml
service: my-serverless-civic-app

...

resources:
  Resources:
    GatewayResponseDefault4XX:
      Type: 'AWS::ApiGateway::GatewayResponse'
      Properties:
        ResponseParameters:
          gatewayresponse.header.Access-Control-Allow-Origin: "'https://color.tylerjohnhaden.com'"
          gatewayresponse.header.Access-Control-Allow-Headers: "'*'"
        ResponseType: DEFAULT_4XX
        RestApiId:
          Ref: 'ApiGatewayRestApi'
    GatewayResponseDefault5XX:
      Type: 'AWS::ApiGateway::GatewayResponse'
      Properties:
        ResponseParameters:
          gatewayresponse.header.Access-Control-Allow-Origin: "'https://color.tylerjohnhaden.com'"
          gatewayresponse.header.Access-Control-Allow-Headers: "'*'"
        ResponseType: DEFAULT_5XX
        RestApiId:
          Ref: 'ApiGatewayRestApi'
          
...
```

The last place we need to add special CORS configuration is in the GET event.

```yaml
...

functions:
  color-identification:
    handler: functions/color.colorIdentification
    events:
      - http:
          ...
          cors:
            origin: 'https://color.tylerjohnhaden.com'
            allowCredentials: true
            headers:
            - Content-Type
            - X-Amz-Date
            - Authorization
            - X-Api-Key
            - X-Amz-Security-Token
    ...
```

This will allow the incoming request to be cross-domain with the authorization token in the header. The headers are the default for AWS proxy integration, and may not all be necessary for our usage. Our API will now include an `OPTIONS` along with the `GET` when created in API Gateway.

To debug problems with CORS, it helps to watch the network requests in the browser. The `OPTIONS` pre-flight check should be successful and allow the next method to be sent. Here is a [Serverless  specific CORS guide](https://serverless.com/blog/cors-api-gateway-survival-guide/).

![missing pic: Civic Options](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/02/civic_options.png)

### Specify a custom domain and certificate

Last thing to add to our `serverless.yml` configuration is the API's custom domain. API Gateway allows us to specify a custom domain (technically CloudFront) that will allow us to attach an SSL certificate too. Yay security!

```yaml
...

plugins:
  - serverless-domain-manager

custom:
  customDomain:
    domainName: api.color.tylerjohnhaden.com
    certificateName: 'color.tylerjohnhaden.com'
    basePath: ''
    stage: ${self:provider.stage}
    createRoute53Record: true
    endpointType: edge
    enabled: true

...
```

We already installed [serverless-domain-manager](https://www.npmjs.com/package/serverless-domain-manager) in `package.json`. This plugin will add the record to Route53, link the certificate, and add this domain to our API, simple as that.

### Deploy to the cloud

Finally, we have our project built. Two commands left to run.

`sls create_domain` needs to be run first to add the custom domain to API Gateway. It will take about 40 minutes to fully spin up, but we can immediately deploy the rest of API.

`sls deploy` is the magic command. This will create the CloudFormation template, a new bucket to store or Lambda code, our Lambda functions, and the API Gateway to connect them.

![missing pic: Civic Sls Deploy](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/02/civic_sls_deploy.png)

You can use the AWS console to explore the resources generated using the CloudFormation template. The CloudFormation, S3 bucket, Lambdas, CloudWatch logs, and API Gateway should all be available. You can individually test the Lambdas by crafting the test event payloads for quick debugging feedback. 

Since we already have our static site available, all we need to do is wait till the CloudFront inside the API spins up with the custom domain.

# Success!
![missing pic: Civic Success](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/02/civic_success.png)

Looks like my personal identity maps to some greenish color.

You can find the [source code for this project here](https://github.com/tylerjohnhaden/ServerlessWithCivicIdentity). If there are any mistakes found, please submit an issue or pull request.

P.S. To tear down the Serverless stack, you can use the `sls remove` [Serverless command](https://serverless.com/framework/docs/providers/aws/cli-reference/remove/). 

---
authors:
- Ashley Moy
tags:
- Postman
- tokens
- api
- javascript
- testing
date: 2023-01-10T00:00:00.000Z
title: "Tips and Tricks for Token Management in Postman"
image:
---

![postman-logo](https://github.com/amoy-ippon/blog-usa/blob/master/images/2024/01/postman-logo.png)

# Overview
Postman streamlines the API development process by offering a unified platform for designing and testing APIs. With powerful features like environment variables, automated testing scripts, and collection runners, developers can debug efficiently by simulating various scenarios.

Postman tokens are instrumental in securing API communications. These tokens, obtained through various authentication methods like OAuth 2.0 or API keys, are then included in API requests.

This blog post will review tips and tricks on how to easily manage the tokens used for API calls more effectively. We will also look at how to manage token-related workflows using Postman JavaScript objects, Postman environment variables, and test scripts.

# What’s a Pre-Request script?
The pre-request script is Javascript code that Postman executes before an API request is made. Pre-request scripts can exist at the API request level or at the collection/folder level.

Any pre-request scripts located at the collection level will execute before EACH endpoint is called within the collection folder. Since this is not necessary the purposes of this blog post, we will only review at the pre-request tab at the individual request level.

# Pre-request scripts vs Test scripts
A pre-request script is executed BEFORE an endpoint makes its request, and a test script is excited AFTER the endpoint makes its request. Both Pre-request and test scripts use javascript. Together, they allow for seamless set-up and testing for each endpoint.

# Fetching a token with pre-request scripts
Rather than making two separate calls: one to fetch a token, and a second to actually call the API endpoint, it is possible to build in the token fetching endpoint into the pre-request script of the actual endpoint that you want the token to be used for.

In this scenario, the pre-request script will this code snippet below:

```
const settings = {
  "url": "https://example.com/get_token",
  "method": "GET",
  "timeout": 0,
  "headers": {

    "Accept": "application/json",
    "Content-Type": "application/x-www-form-urlencoded",
    "clientId": "<CLIENT_ID>",
    "clientSecret": "<CLIENT_SECRET>"
  }
};

pm.sendRequest(settings, (err, res) => {
    console.log(res);
    pm.environment.set('token', res['access_token']);
    pm.environment.set('token_expiration', res['exp']);
});
```

The above script sends a GET request to fetch the token with the necessary clientId and clientSecret, and saves the token to the postman environment variable ‘token’.

![get-token-screenshot](https://github.com/amoy-ippon/blog-usa/blob/master/images/2024/01/postman-get-token.png)

# Token expiration management
Postman introduced a feature in early 2023 that allowed for [token refresh support for OAuth 2.0](https://blog.postman.com/oauth-2-0-token-refresh-and-id-token-support/). This feature has made it much easier for developers to refresh OAuth 2.0 access tokens without needing to go through the entire authorization process again.

However, for tokens that are not OAuth 2.0, it is **still** possible to automate the token refresh process with the Pre-requests scripts of your endpoint call. You can do so by having the pre request script read the `exp`, or expiration, value of any existing token and fetch a new token if the current one is expired. This can be done with the following example:

```
var atob = require('atob');
const [jwtHeader, jwtPayload, jwtSignature] = pm.environment.get('token').split('.')
const jwtPayloadJsonString = atob(jwtPayload)

const jwtPayloadJson = JSON.parse(jwtPayloadJsonString)
var exp = pm.environment.get('token_expiration') * 1000;
const now = new Date();

if (now > Date(exp)) {
    const settings = {
        "url": "https://example.com/get_token",
        "method": "GET",
        "timeout": 0,
        "headers": {

            "Accept": "application/json",
            "Content-Type": "application/x-www-form-urlencoded",
            "clientId": "<CLIENT_ID>",
            "clientSecret": "<CLIENT_SECRET>"
        }
    };

        pm.sendRequest(settings, (err, res) => {
            console.log(res);
            pm.environment.set('token', res['access_token']);
            pm.environment.set('token_expiration', res['exp']);
        });
}
console.log(jwtPayloadJson)
```

Here, we are using the `atob` library to decode the token that currently exists in our environment variable named `token`. We then compare the token expiration value to the current Date() value to determine if the token is expired.

A new token is fetched ONLY if the current token in the environment variable `token` has expired. This reduces unnecessary calls for a token refresh before your request is made. If the token has not yet expired, then the token fetching endpoint is not called and the main API call is called as usual using the existing token.

# Automating token deletion after a request is made
For security reasons, it is also possible to erase a token value from your environment variables after a successful request is made. This is done in the Test script of the request, with a simple block of code that unsets the `token` environment variable - as long as the response of the API call is `200 OK`. This way, we are able to use the token that we need for the request without persisting the token value in the Postman environment any longer than necessary.

```
if (pm.response.code === 200) {
    pm.environment.unset("token");
}
```

![unset-token-screenshot](https://github.com/amoy-ippon/blog-usa/blob/master/images/2024/01/postman-unset-token.png)

# Conclusion
With these features that allow for efficient management within the Postman platform, developers can seamlessly integrate their API workflows. The use of Postman’s various script features contribute to a streamlined and efficient development. As Postman comes out with new features in the future, it is possible that these features may be  integrated into its platform altogether. For now, hopefully this post has given you some tips and tricks for how to do so on your own.
---
authors:
- Theo Lebrun
tags:
- JHipster
- AWS
- AWS Cognito
- OAuth2
date: 2020-02-05T14:50:55.000Z
title: "AWS Cognito and JHipster for the love of OAuth 2.0"
image: 
---

OAuth 2.0 is a stateful security mechanism and OpenID Connect (OIDC) is an authentication layer on top of OAuth 2.0. Spring Security provides excellent OAuth 2.0 and OIDC support, and this is leveraged by JHipster. If you’re not sure what OAuth and OpenID Connect (OIDC) are, please see [What the Heck is OAuth?](https://developer.okta.com/blog/2017/06/21/what-the-heck-is-oauth). JHipster lets you use two OpenID Connect server: Keycloak (default) and Okta. If Keycloak is used, you will have to make sure that it is always up and running or it will be impossible to login into your JHipster application. Okta takes care of the hosting part so you don't have to worry on the availability of your OpenID Connect server.

An alternative to [Okta](https://www.okta.com/) is to use [AWS Cognito](https://aws.amazon.com/cognito/), especially if your whole architecture is running on AWS. Other features are the possibility to trigger Lambdas on specific events (ex: when a user is confirmed) or to link a user's group with an IAM role. In this blog, I will explain how a JHipster application can be configured to use AWS Cognito instead of Okta/Keycloak. All the working code is accessible at this [GitHub repository](https://github.com/Falydoor/cognito-jhipster).

# Application generation

I decided to generate a standard JHipster application that uses `OAuth 2.0/OIDC` for the authentication. After installing JHipster, you just have to run `jhipster` and use the default values except for the authentication question:

![](https://raw.githubusercontent.com/Falydoor/blog-usa/cognito-jhipster/images/2020/02/cognito-generation.png)

This [.yo-rc.json](https://raw.githubusercontent.com/Falydoor/cognito-jhipster/master/.yo-rc.json) can be used in case you want to generate the same application referenced in this article. The file is placed in the application directory and then running `jhipster` will generate the application without asking any questions.

# User pool creation

After login into the [AWS Management Console](), browse to the [Cognito service](https://console.aws.amazon.com/cognito/users/?region=us-east-1) and click on `Create a user pool`. A user pool with the default configuration like below is required:

![](https://raw.githubusercontent.com/Falydoor/blog-usa/cognito-jhipster/images/2020/02/cognito-user-pool.png)

## Add users and groups

JHipster uses two groups that must be added to the user pool: `ROLE_USER` and `ROLE_ADMIN`:

![](https://raw.githubusercontent.com/Falydoor/blog-usa/cognito-jhipster/images/2020/02/cognito-roles.png)

After creating the two groups, an `admin` user can be created in order to access the application. Don't forget to assign the user to the two groups!

![](https://raw.githubusercontent.com/Falydoor/blog-usa/cognito-jhipster/images/2020/02/cognito-user.png)

## Create an app client and domain

The last step is to create an app client that will be used by the JHipster app. It is required to allow people authenticate using users created in the user pool. Like before, an app client with the default parameters can be created:

![](https://raw.githubusercontent.com/Falydoor/blog-usa/cognito-jhipster/images/2020/02/cognito-app-client.png)

The app client id and secret are required by the JHipster app, make sure to not share them or push them to a public repository. And also, keep in mind that secrets cannot be changed after an app is created. You can create a new app with a new secret if you want to rotate the secret that you are using.

The callback, sign out URL and the allowed OAuth Flows/Scopes need to have the values below:

![](https://raw.githubusercontent.com/Falydoor/blog-usa/cognito-jhipster/images/2020/02/cognito-app-client-settings.png)

Finally, a domain is required in order to have the login flow working. You can configure the domain in the **Domain name** section (you can create or use your own domain).

![](https://raw.githubusercontent.com/Falydoor/blog-usa/cognito-jhipster/images/2020/02/cognito-domain.png)

# Code changes

## Secrets

The first thing to do is to replace the `client-id` and `client-secret` with the correct values in `src/main/resources/config/application.yml`. Those secrets can be viewed in the **App clients** section:

![](https://raw.githubusercontent.com/Falydoor/blog-usa/cognito-jhipster/images/2020/02/cognito-secrets.png)

Then the `issuer-uri` must be updated with the correct URI: `https://cognito-idp.REGION.amazonaws.com/POOL_ID` (the pool id can be viewed in the **General settings** section).

![](https://raw.githubusercontent.com/Falydoor/blog-usa/cognito-jhipster/images/2020/02/cognito-pool.png)

## Roles

The way JHipster extracts the roles from the token doesn't work for Cognito since they stored in the `cognito:groups` attribute. The [commit](https://github.com/Falydoor/cognito-jhipster/commit/e2cceba1f5e9844cb0ab4ca6f7601b1c3a8b96a4) contains the changes that will make the roles extraction works.

## Login and logout fixes

The method `getUser` in `UserService.java` extracts the information from the Cognito's token and save it to the local database. The login value is not set correctly and uses the `id` of the user instead of the email. A simple override with the email value fixes the issues, more details can be found on this [commit](https://github.com/Falydoor/cognito-jhipster/commit/69eae0a890de8401174214e8db3381e21f8e9789).

The last fix is related to the logout since JHipster generates the logout URL for Okta/Keycloak but not for Cognito. The correct URL is formatted like this: `https://DOMAIN.auth.REGION.amazoncognito.com/logout`, this [commit](https://github.com/Falydoor/cognito-jhipster/commit/53acb5c2c53873eafc6a9631ee0fad9630d3ea05) changes how the URL is retrieved (don't forget to replace `DOMAIN` and `REGION`).

# Conclusion

To resume, the AWS Cognito integration with JHipster is very easy and doesn't require a lot of changes. It is a good alternative to Keycloak, especially if you don't want to take care of hosting your own identity management solution. I used both Okta and AWS Cognito, I don't recommend one specifically since it depends of your needs and architecture. Hovewer, if your architecture is fully hosted on AWS, Cognito will be probably a better pick.
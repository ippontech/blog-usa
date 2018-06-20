---
authors:
- Kenneth Hegeland
categories:
date: 2015-08-27T10:35:38.000Z
title: "Migrating from OpenID to OAuth 2.0 for Google Login Support"
id: 5a267e57dd54250018d6b5ef
image: 
---

One exciting feature of Tatami is being able to log in through a google account (since users don’t have to remember yet another password). While in the process of migrating from Backbone.js to AngularJS, it was discovered that OpenID 2.0 was to be deprecated on April 20, 2015. The [release of Tatami 4.0](https://blog.ippon.tech/vcu-ippon-angularjs-tatami/) included an update which handles this migration, allowing our users to continue logging in through their google accounts.

#### **What Should Happen When a User Logs in Through Google**

The program flow for authentication with google auth is really not too challenging, the basic idea follows the [OAuth 2.0 workflow](http://requests-oauthlib.readthedocs.org/en/latest/oauth2_workflow.html). Our user clicks the login button, is redirected to a pre built url which is used to authorize the user with google, then the user is provided with an authorization code. With the authorization code, we can ask google for an access token, which can be used to request user information. For Tatami’s purpose, we only care about the access token initially – once we know the users login information, we retrieve the user information from our database and store it in the Security Context Holder of Spring Security.

#### **The Authorization Framework – Pac4j and Google login**

The original implementation of google login for Tatami used Spring Security OpenID, and this would no longer work for google login. It was decided that using a tested, publicly used framework would provide a safe implementation of OAuth in a timely manner, thus we chose [Spring Security Pac4j](https://github.com/pac4j/spring-security-pac4j), which also has support built in for many other websites (Facebook, Twitter, Github, etc).

#### Configuring Pac4j for Tatami

Configuring Pac4j requires us to first update the applicationContext-security xml configuration. We must provide access to our list of clients, where each client generally consists of a key and a client secret. The following code snippet first configures our client (in Tatami’s case, we only want a google client), then our list of clients (which again, only contains the single google client).

```language-xml
<!-- Defining our client -->
<beans:bean id="googleClient" class="org.pac4j.oauth.client.Google2Client">
    <!-- tatami.google.clientId and tatami.google.clientSecret must be set in pom.xml -->
    <beans:property name="key" value="${tatami.google.clientId}"/>
    <beans:property name="secret" value="${tatami.google.clientSecret}"/>
</beans:bean>

<beans:bean id="clients" class="org.pac4j.core.client.Clients">
    <!-- The callback url registered with google must be value with ?client_name=Google2Client appended -->
    <beans:property name="callbackUrl" value="${tatami.url}/tatami/callback"/>
    <beans:property name="clients">
        <beans:list>
            <beans:ref bean="googleClient"/>
        </beans:list>
    </beans:property>
</beans:bean>
```
Configure our client

When configuring your google information through the google dev console, it is very important that the callback url matches the description given in the comment. Also, client keys and secrets should not be stored in an open source project ([Google dev terms](https://developers.google.com/terms/)).

We want to add a custom filter to our [filter chain](http://docs.spring.io/spring-security/site/docs/3.0.x/reference/security-filter-chain.html), which is going to tell us that our user has attempted to login through google, and will start the OAuth process. The filter is created as follows

```language-xml
<beans:bean id="clientFilter" class="org.pac4j.springframework.security.web.ClientAuthenticationFilter">
    <beans:constructor-arg value="/tatami/callback"/>
    <beans:property name="clients" ref="clients"/>
    <beans:property name="authenticationManager" ref="authenticationManager"/>
</beans:bean>
```
Client Filter

In our client filter, we reference three things, the list of clients (which we have already discussed), a callback url, and our authentication manager, which is a list of authentication providers (shown below), of which we have three – one for ldap, dao, and google. The only one of interest to us is the google authentication provider (the others did not change for the release of Tatami 4.0).

==Authentication Providers==
```language-xml
<authentication-manager alias="authenticationManager">
    <authentication-provider ref="ldapAuthenticationProvider"/>
    <authentication-provider ref="daoAuthenticationProvider"/>
    <authentication-provider ref="googleAuthProvider"/>
</authentication-manager>
```

With Tatami, we are not interested in interacting with google other than for the initial handshake – we are simply relying on google to authenticate the user (and give us an initial access point to the username). From there, we will load the user based on the information received. The google authentication provider handles this exchange for us, and it is defined as follows:

```language-xml
<beans:bean id="googleAuthProvider" class="fr.ippon.tatami.security.GoogleAuthenticationProvider">
    <beans:property name="clients" ref="clients"/>
    <beans:property name="userDetailsService" ref="googleAutoRegisteringUserDetailsService"/>
</beans:bean>
```
Google Authentication Provider

We run into a complication quite quickly, the client authentication token used by pac4j is not compatible with a token that our user details service requires. The authenticate method shown below is responsible for taking the token provided by pac4j and uses it’s capabilities (to get user information), in order to create a token that is compatible with what our user details service expects.

```language-java
@Override
public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    logger.debug("authentication : {}", authentication);
    if(!this.supports(authentication.getClass())) {
        logger.debug("unsupported authentication class : {}", authentication.getClass());
        return null;
    } else {
        ClientAuthenticationToken token = (ClientAuthenticationToken)authentication;
        Credentials credentials = (Credentials)authentication.getCredentials();
        logger.debug("credentials : {}", credentials);
        String clientName = token.getClientName();
        Client client = this.clients.findClient(clientName);
        UserProfile userProfile = client.getUserProfile(credentials, (WebContext)null);
        logger.debug("userProfile : {}", userProfile);
        Object authorities = new ArrayList();
        ClientAuthenticationToken result = null;
        logger.debug("userDetailsService: {}", this.userDetailsService);
        result = new ClientAuthenticationToken(credentials, clientName, userProfile, (Collection)null);
        UserDetails userDetails = this.userDetailsService.loadUserDetails(result);
        logger.debug("userDetails : {}", userDetails);
        if(userDetails != null) {
            authorities = userDetails.getAuthorities();
            logger.debug("authorities : {}", authorities);
        }
        GoogleAuthenticationToken res = new GoogleAuthenticationToken(userDetails, clientName, (Collection)authorities);

        logger.debug("Client name : {}", clientName); // -> Google2Client
        logger.debug("Client Credentials: {}", credentials); // -> OAuth Credentials
        logger.debug("Client Profile: {}", userProfile); // -> GoogleProfile, i.e. data from google
        res.setDetails(authentication.getDetails());
        logger.debug("result : {}", res);
        return res;
    }
}
```
Now that we have a token that we expect, our user details service can finish the authentication process:

```language-java
@Override
public UserDetails loadUserDetails(ClientAuthenticationToken token) throws UsernameNotFoundException {
    String login = getAttributeValue(token, EMAIL_ATTRIBUTE);

    if (login == null) {
        String msg = "OAuth response did not contain the user email";
        log.error(msg);
        throw new UsernameNotFoundException(msg);
    }
    if (!login.contains("@")) {
        log.debug("User login {} from OAuth response is incorrect.", login);
        throw new UsernameNotFoundException("OAuth response did not contains a valid user email");
    }

    // Automatically create OpenId users in Tatami :
    UserDetails userDetails;
    try {
        userDetails = userDetailsService.loadUserByUsername(login);
        // ensure that this user has access to its domain if it has been created before
        domainRepository.updateUserInDomain(DomainUtil.getDomainFromLogin(login), login);

    } catch (UsernameNotFoundException e) {
        log.info("User with login : \"{}\" doesn't exist yet in Tatami database - creating it...", login);
        userDetails = getNewlyCreatedUserDetails(token);
    }
    return userDetails;
}
```
Get the User

This simply performs sanity checks on the token, and then if the user exists already, it is loaded from the database, otherwise, it creates a new user. The UserDetails returned here is used to create an Authentication object, which is stored in springs SecurityContextHolder — that is, we are authenticated.

#### Tying Everything Together

We now have just about everything we need to authenticate a user through google, we just need something to start the authentication. First we add our filter to the filter chain:

```language-xml
<http auto-config="true" use-expressions="true" create-session="ifRequired">
    <custom-filter after="BASIC_AUTH_FILTER" ref="clientFilter" />
    <remember-me key="tatamiRememberKey" token-validity-seconds="1209599"/>
    <intercept-url pattern="/tatami/presentation" access="permitAll()"/>
    <intercept-url pattern="/tatami/tos" access="permitAll()"/>
    <intercept-url pattern="/tatami/license" access="permitAll()"/>
    <intercept-url pattern="/tatami/404-error" access="permitAll()"/>
    <intercept-url pattern="/tatami/500-error" access="permitAll()"/>
    <intercept-url pattern="/tatami/rest/users" method="POST" access="permitAll()"/>

    <intercept-url pattern="/metrics/**" access="hasRole('ROLE_ADMIN')"/>
    <intercept-url pattern="/**" access="isAuthenticated()"/>
    <access-denied-handler error-page="/tatami/login"/>
    <form-login
            login-processing-url="/tatami/authentication"
            login-page="/tatami/login"
            authentication-failure-url="/tatami/login?action=loginFailure"
            default-target-url="/tatami/"
            authentication-success-handler-ref="tatamiAuthenticationSuccessHandler"/>

    <logout logout-url="/tatami/logout"
            logout-success-url="/tatami/login"/>
    <session-management invalid-session-url="/tatami/login"/>
</http>
```
Then we guard the pattern we want to start everything

```language-xml
<http pattern="/tatami/j_spring_pac4j_security_check" use-expressions="true" entry-point-ref="googleEntryPoint">
    <intercept-url pattern="/tatami/j_spring_pac4j_security_check" access="isAuthenticated()"/>
</http>
```
Finally, we redirect users that access this url (it is only used to start the process) in the [Pac4jSecurityCheckController](https://github.com/ippontech/tatami/blob/master/src/main/java/fr/ippon/tatami/web/controller/Pac4JSecurityCheckController.java), which simply listens on ‘/j_spring_pac4j_security_check’ and redirects the user to the home page.

We have now successfully configured Tatami to login to google through OAuth 2.0 using Spring Security Pac4j. You can access [Tatami](http://app.tatamisoft.com/#/login), and try it out yourself. Completing this step in the upgrade of Tatami was part of a year long capstone course at VCU, and Google login was a critical requirement.

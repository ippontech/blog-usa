---
authors:
- Laurent Mathieu
categories:
- OWASP
- Sécurité
- Web
date: 2013-12-09T08:45:08.000Z
title: "OWASP Top 10 - A7 Missing Function Level Access Control"
image: 
---

By Philippe Céry.

## Description

In a web application with different user roles, authentication is not enough. Each request must be controlled against user’s role to ensure the user is authorized to use the requested function or access the requested page.

## Examples

On my bank’s website, the clerk has a link in his navigation bar to manage the client accounts. The client can’t see that link because clients are not allowed to access this section. But the item in the menu exists in the code, it is simply hidden dynamically using a Javascript function. By analyzing the HTML code, an attacker can easily find the URL to the account management section. And as the access to this section is not controlled, thus the attacker can steal a lot of sensitive data (personal information, account data…).
 Moreover, functions available in that account management section for clerks are not controlled either. The attacker can then transfer money to his own account. And hopefully, the attacker’s actions are not logged.

## Mitigations

### Control access

You need to be sure access to all pages and functions requiring authentication and specific authorization is controlled. To simplify and enforce security, your access control mechanism should be centralized and role-based. It is also highly recommended to always apply a “deny-by-default” rule, i.e. explicitly define what is allowed and disallow everything else.
 You can use standard java filters to achieve this goal. Some third-party frameworks also provide API for centralized and role-based authentication and access control.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/authorization-277x300.png)

source: xkcd.com

You need to first identify the different roles available on your web site. E.g. you can have the simple USER role for authenticated users with no additional rights, the MANAGER role to manage users and the ADMIN role with administration privileges.
 When possible, organize your website according to these roles:
```
home.xhtml
login.xhtml
access_denied.xhtml
/secure
|_ home.xhtml
|_ /user
   |_ accounts.xhtml
   |_ account_details.xhtml
   |_ contact_my_clerk.xhtml
   |_ ...
|_ /mgmt
   |_ users.xhtml
   |_ messages.xhtml
   |_ ...
|_ /admin
   |_ managers.xhtml
   |_ monitoring.xhtml
   |_ ...
```

Then, you have to properly configure your preferred session management framework.
 If the requested URL requires authentication, the framework should redirect unauthenticated users to the login page. If the requested URL requires a specific role, the framework should redirect to an “access denied” error page.

With Spring Security, the configuration should look like the following:
```language-xml
<sec:http auto-config="true" access-denied-page="/access_denied.xhtml">
  <sec:form-login login-page="/login.xhtml" default-target-url="/secure/home.xhtml" />
  <sec:intercept-url pattern="/login.xhtml*" access="IS_AUTHENTICATED_ANONYMOUSLY" />
  <sec:intercept-url pattern="/secure/user/**" access="ROLE_USER,ROLE_MANAGER" />
  <sec:intercept-url pattern="/secure/mgmt/**" access="ROLE_MANAGER" />
  <sec:intercept-url pattern="/secure/admin/**" access="ROLE_ADMIN" />
  <sec:intercept-url pattern="/secure/**" access="ROLE_USER,ROLE_MANAGER,ROLE_ADMIN" />
  <sec:session-management invalid-session-url="/login.xhtml" />
</sec:http>
```

### Hide unauthorized functions

If users don’t know a function exists, they won’t be tempted to use it.
 When possible, only show the functions the user can access with its privileges. This way, you won’t expose sensitive URL to potential attackers. Of course, that means you should not have anything in the generated HTML/javascript code related to these hidden functions. Avoid HTML blocks hidden with CSS or javascript…
 With your preferred web framework, you will find different mechanisms to display or hide an HTML block depending on the user’s privileges.
 E.g. Spring Webflow provides `<authorize>`, a Spring Security tag for JSF:
```language-xml
<security:authorize ifAnyGranted="ROLE_USER,ROLE_MANAGER" ifNotGranted="ROLE_ADMIN">
  <em>Links to client functions (also accessible to clerks)</em>
</security:authorize>
<security:authorize ifAllGranted="ROLE_MANAGER" ifNotGranted="ROLE_USER,ROLE_ADMIN">
  <em>Links to Management functions (only for clerks)</em>
</security:authorize>
<security:authorize ifAllGranted="ROLE_ADMIN" ifNotGranted="ROLE_USER,ROLE_MANAGER">
  <em>Links to Administration functions (only for administrators)</em>
</security:authorize>
```

### Log sensitive actions

Last but not least: log in a secure place all sensitive actions done on your website. If your website is attacked, even if the attack failed (because the mitigations I proposed are all applied on your website), it is still interesting to know how and when you were attacked and by who. Thus, you can’t take specific actions against the source of the attack (blocking IP, deactivating account, revoking client certificate…) and investigate further.

An even better option would be to be alerted when a suspicious action is requested. For example, if an authenticated simple user tries to access a management function, of course, the access will be rejected but it may be interesting to know that there was a potential attack attempt.

To see all articles related to OWASP Top 10, follow the tag [#owasp](http://www.ipponusa.com/tag/owasp/ "OWASP Top 10")

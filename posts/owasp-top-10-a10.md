---
authors:
- Philippe Cery
tags:
- OWASP
- Security
- Web
date: 2014-01-29T09:00:30.000Z
title: "OWASP Top 10 - A10 Unvalidated Redirects and Forwards"
image: 
---

## Description

If a user is redirected or forwarded to a page defined by an unverified entry, the target URL can be manipulated by an attacker.
 This attack is used to redirect a user to a malicious website through a website with a trusted domain name (phishing) or to access an unauthorized internal page.

## Examples

My bank provides a very interesting page “My favorites” to easily access all available services and manage my own favorites. Thanks to this page, I can retrieve all my favourites from any computer, everywhere in the world.
 For example, when I want to consult my emails, I click on “My mailbox” I have added in that page. The URL `https://www.mybank.com/redirect.jsp?url=www.mymailbox.com` is called then I am redirected to `http://www.mymailbox.com/`.

Last time I’ve checked my mailbox, I have received an email from my bank to confirm my login and passcode for security reasons. I click on the provided link, enter my login and passcode and that’s it!
 The problem is the link I have clicked on was `https://www.mybank.com/redirect.jsp?url=www.trustme.com` and I was silently redirected to `http://www.trustme.com/` on a page with the look-and-feel of my bank’s website.
 A few days later, I received a call from my bank. Someone has used my personal credentials to access my accounts and sent my money abroad.

## Mitigations

It is very simple to mitigate this risk: never redirect or forward to unverified URLs or URLs modifiable with user entry.

An unverified URL is an URL entered by the user and not verified/trusted by the server.
 A modifiable URL is a URL with parts (domain name, path, parameters…) that can be modified with unverified/untrusted values entered by user.
 In both cases, the URL can be a full URL (for redirections) or a path to an internal resource (for forwards).

If you really need to redirect a user to another page (external or internal) registered by that user, the page URL must not be in a request parameter. Use an indirect reference instead. Refer to one of my previous posts about [Insecure Direct Object References](https://blog.ippon.tech/owasp-top-10-a4/ "OWASP Top 10 - A4 Insecure Direct Object References") for details on generating an indirect object reference.
 E.g. use `https://www.mybank.com/redirect.jsp?urlId=abc321` instead of `https://www.mybank.com/redirect.jsp?url=www.mymailbox.com`, where `urlId` is an indirect reference to the URL `http://www.mymailbox.com` added in user’s favorites.
 Besides, you can also check the URL is well-formed and can be trusted before storing it in your database. Some commercial antivirus/firewall products and online services provide blacklist of suspicious websites that can’t be trusted.

If the user can choose a landing page after some actions (e.g. its default homepage after login), instead of giving him a blank field to enter what he wants, propose a list of acceptable landing pages. This way, the user won’t be tempted to enter something like `/secure/mgmt/users.xhtml` to access user management without authorization! Obviously, if you have applied what I have said in my post about [Missing Function Level Access Control](https://blog.ippon.tech/owasp-top-10-a7/ "OWASP Top 10 - A7 Missing Function Level Access Control"), this attempt should not work 😉
 And again, remember you must avoid direct references. Don’t give the real path to the internal resource in the list!

To see all articles related to OWASP Top 10, follow the tag [#owasp](https://blog.ippon.tech/tag/owasp/ "OWASP Top 10")

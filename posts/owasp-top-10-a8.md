---
authors:
- Laurent Mathieu
categories:
- OWASP
- Sécurité
- Web
date: 2014-01-14T09:00:55.000Z
title: "OWASP Top 10 - A8 Cross-Site Request Forgery (CSRF)"
image: 
---

By Philippe Céry.


## Description

An attacker sends a request to a website you are authenticated on to execute an operation without your formal approval.  
 Attackers usually use [XSS](http://blog.ippon.fr/2013/10/28/owasp-top-10-a3/ "A3 Cross-Site Scripting (XSS)") to make you or your browser send this malicious request, but many other flaws exist to achieve the same goal.


## Examples

Every month, to pay my rent, I authenticate on my bank’s website, then I use the following link saved in my favorites:  
*`https://www.mybank.com/transfer.xhtml?toAccount=FR1234567890&amount=1000&currency=EUR`*

One day, I find several transfers executed to an account in a foreign country.  
 An attacker made my browser silently execute these transfers. I was authenticated on my bank’s website while browsing on other sites. Several pages on the visited sites had the same malicious code:  
```language-html
<script>
x=new Image();
x.src='https://www.mybank.com/transfer.xhtml?toAccount=XY0000000000&amount=100&currency=EUR'
</script>
```
  
 Each time this code was interpreted by my browser, as long as I was authenticated on my bank’s website, I was giving 100 euros to someone I don’t even know!


## Mitigations

### CSRF prevention token

The most effective mitigation against CSRF attacks is to attach a token to each sensitive request. The principle is very simple.

- When the server generates a form, a token is generated, stored on server side and associated to that generated form on client side.
- When receiving a request for a protected action, - If the token is present and exists on server side, the action is executed.
- Otherwise, the request is rejected and the user should be redirected to a landing page. When applicable, the user’s session should be invalidated. Of course, an alert should be thrown to administrators and a log should be generated with available information for investigation.

![source: xkcd.com](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/random_number-300x108.png)

Obviously, the value of the generated token is not confidential so it must be unpredictable. It should be a Base64-encoded random value (at least 32 bytes long) or a UUID. Avoid counters, timestamps, simple suites or strings generated using user information.

The simpliest method consits in generating a single token for each authenticated user (per-session protection). It is usually stored in user’s HTTP session. The same token is injected in all forms that must be protected until user’s session is invalidated. You can also generate a different token for each sensitive page (per-URI protection) or even a token for each sensitive request (per-request protection).

JSF (version 2.1 and above) and frameworks like [CSRFGuard](https://www.owasp.org/index.php/Category:OWASP_CSRFGuard_Project "CSRFGuard") provide automatic protection against CSRF attacks.  
 You can also implement your own protection depending on your needs.

- Identify the actions (forms) to protect. You can protect all pages but you should focus on request that will modify the state of the application.
- Generate your random value (SecureRandom is recommended) and save it in the new session. - For a per-request protection, a JavaEE `Filter` can be used to update the token in the session when sending back the response that will display a protected form
- For a per-session protection, simply implement method `sessionCreated` from `HttpSessionListener` interface to create your token once.
- For a per-URI protection, you can create all tokens once using an HttpSessionListener implementation or create each token on-demand, the first time the page to protect is called using a `Filter`
- In the form to protect, add a hidden parameter with the token retrieved from the current session as value.
- Implement and declare a JavaEE `Filter` before servlets to protect to verify a token is provided in the request and matches the token saved in user’s session. If an attack is suspected, log available information then send an alert, invalidate the attacked session…

Note that, for per-session and per-URI protections, you can use a Filter to renew your token after a few usages, after a delay or when an attack is suspected (with per-request protection, token renewal is systematic).

And the same `Filter` can be used to verify the provided token and then update/renew the token for the next request.

### Human approval

With an unpredictable token, using a link in your favorites is not possible anymore. If you want to allow your users to save a link in their favorites, you will need the user’s approval to make sure it is a human-initiated authorized action

Again, you have several differents methods. I will only focus on the two most used: CAPTCHA and re-authentication.

[CAPTCHA](http://en.wikipedia.org/wiki/CAPTCHA "CAPTCHA (Wikipedia)") is used to avoid automatic request sent by robots to be executed. It is an acceptable solution for anonymous actions.  
 E.g. you can use this protection on your website to prevent account creation requested by robots.

Re-authentication simply consists in asking the authenticated user to enter its credentials again before performing the requested action. Nowadays, all banks (except mybank.com) use this mechanism before a transfer. Of course, with this solution, the user could use a link in its favorites and would have to authenticate before the transfer can be done. Unless you also require a CSRF prevention token in the request (recommended).

Note: As I explained in the [introduction](http://www.ipponusa.com/owasp-top-10-introduction/ "OWASP Top 10 - Introduction"), in the past, CSRF was one of the highest risks identified by OWASP team (rank 5). Then, application and framework developers improved the security on their products. Now, CSRF attacks are rare, so the risk was recently moved to the end of the list (rank 8). But stay alert and apply the recommended mitigation… CSRF is still in OWASP’s list of risks!

To see all articles related to OWASP Top 10, follow the tag [#owasp](http://www.ipponusa.com/tag/owasp/ "OWASP Top 10")

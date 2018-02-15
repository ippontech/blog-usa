---
authors:
- Laurent Mathieu
categories:
- OWASP
- Sécurité
- Web
date: 2013-10-10T08:30:10.000Z
title: "OWASP Top 10 - Introduction"
image: 
---

When starting a new web application, the security risks are sadly often underestimated by everyone (developers, architects, IT, managers…).

Web applications are more vulnerable to attacks compared to standalone applications as they usually expose a service over a network to a potentially large population of users. Of course, the risk is higher when the population is not clearly identified (web site on internet) but it exists also with a closed list of known persons (entreprise application on intranet) because we always have data exchanged between the client (browser, web service client) and the server.

When you choose to ignore these risks, your data can be deleted, corrupted or stolen. And maybe worst, your customers can become victims of attacks because of your service. Their session can be hijacked by an attacker that will be granted all permissions on their personal data.

What about your reputation then ? Remember the « [PSNgate](http://en.wikipedia.org/wiki/PlayStation_Network_outage "PSNgate") » two years ago…

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/800px-Owasp_logo.jpg)

[OWASP](https://www.owasp.org/ "OWASP") provides an ordered list of the most important the risks according to their risk rating methodology and prevalence statistics provided by different organizations. While the methodology gives a theorical rating based on a few criteria like detectability, ease of exploit and technical impact, the prevalence statistics can change the order with concrete data from the real world.

E.g. in the last release this year, CSRF risk was moved down from rank 5 to rank 8. According to OWASP, « this is because CSRF has been in the OWASP Top 10 for 6 years, and organizations and framework developers have focused on it enough to significantly reduce the number of CSRF vulnerabilities in real world applications. »[ref: [OWASP Top 10 2013 – Release Notes](https://www.owasp.org/index.php/Top_10_2013-Release_Notes "OWASP Top 10 2013 - Release Notes")].

Here are the most important risks identified by OWASP.

- [A1 Injection](http://blog.ippon.fr/2013/10/11/owasp-top-10-a1/ "A1 Injection")
- [A2 Broken Authentication and Session Management](http://blog.ippon.fr/2013/10/21/owasp-top-10-a2/ "A2 Broken Authentication and Session Management")
- [A3 Cross-Site Scripting (XSS)](http://blog.ippon.fr/2013/10/28/owasp-top-10-a3/ "A3 Cross-Site Scripting (XSS)")
- [A4 Insecure Direct Object References](http://blog.ippon.fr/2013/11/04/owasp-top-10-a4/ "A4 Insecure Direct Object References")
- [A5 Security Misconfiguration](http://blog.ippon.fr/2013/11/14/owasp-top-10-a5/ "A5 Security Misconfiguration")
- [A6 Sensitive Data Exposure](http://blog.ippon.fr/2013/11/18/owasp-top-10-a6/ "A6 Sensitive Data Exposure")
- [A7 Missing Function Level Access Control](http://blog.ippon.fr/2013/12/09/owasp-top-10-a7/ "A7 Missing Function Level Access Control")
- [A8 Cross-Site Request Forgery (CSRF)](http://blog.ippon.fr/2014/01/14/owasp-top-10-a8/ "A8 Cross-Site Request Forgery (CSRF)")
- [A9 Using Components with Known Vulnerabilities](http://blog.ippon.fr/2014/01/28/owasp-top-10-a9/ "A9 Using Components with Known Vulnerabilities")
- [A10 Unvalidated Redirects and Forwards](http://blog.ippon.fr/2014/01/29/owasp-top-10-a10/ "A10 Unvalidated Redirects and Forwards")

Although OWASP gives very interesting advices to prevent these risks, these are only generalities. If you want to protect your application against these risks, you will need to find by yourself the appropriate mitigations depending on your environment (OS, programing language, application server, database…).

In my next 10 articles (1 article per risk), I will share with you some concrete mitigations (frameworks, best practices, code snippets…) I have put in place to reduce these risks in a Java-based web application.

To see all articles related to OWASP Top 10, follow the tag [#owasp](http://blog.ippon.fr/tag/owasp/ "OWASP Top 10")

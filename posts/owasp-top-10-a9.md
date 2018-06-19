---
authors:
- Philippe Cery
categories:
- OWASP
- Sécurité
- Web
date: 2014-01-28T09:00:35.000Z
title: "OWASP Top 10 - A9 Using Components with Known Vulnerabilities"
id: 5a267e57dd54250018d6b5c4
image: 
---

## Description

Known software vulnerabilities are available to everyone on the Internet. If an attacker knows which components you use, he can retrieve these vulnerabilities and find a way to exploit them.

## Examples

Somehow, an attacker found out my bank’s website uses Apache web server version 1.3.22 on Win32. This version has a [critical vulnerability](http://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2002-0061 "CVE-2002-0061") that allows remote attackers to execute arbitrary commands. As, on Windows, the web server runs with SYSTEM privileges, this vulnerability can be exploited by the attacker to do what he wants (steal information, stop services…) on that server.
 The attacker first stop the local firewall that blocks outbound connections from the web server to the Internet. Then he can download a network traffic monitoring tool, install it and start it to intercept all traffic behind the Apache web server (after SSL termination). All login requests are intercepted and relevant information (obviously, login and passcode) is extracted and uploaded to its server using a simple Batch script.

## Mitigations

The first protection is of course to obfuscate information related to the components (servers, frameworks, operating systems…) you use to run your application. Refer to the previous article related to [Security Misconfiguration](https://blog.ippon.tech/owasp-top-10-a5/ "OWASP Top 10 – A5 Security Misconfiguration") for details.

### Know your application

To reduce the risk of using vulnerable components, you need to know exactly what components your application uses (directly or not) and the version of each of these components.

If you use Maven to build your application, you should add [Versions Maven plugin](http://mojo.codehaus.org/versions-maven-plugin/ "Versions Maven plugin") to display the list of dependencies used by your application which have newer versions available. You can even generate an HTML report for your project documentation.
 This Maven plugin is just a first step to verify your application uses newest versions of its dependent librairies.
 But obviously, you can’t update all the dependencies only because there is a new version available. Updating a dependency’s version implies running non regression tests. And updating libraries sometimes can be very expensive if the API has changed. Besides, latest versions could have no security fixes and it could contain new vulnerabilities.

### Know your vulnerabilities

As I explained in [a previous post](https://blog.ippon.tech/owasp-top-10-a5/ "OWASP Top 10 – A5 Security Misconfiguration"), security patches should be installed as soon as possible. But you also need to know these patches exist and what they fix to know whether your application is at risk or not.
 OWASP proposes a very interesting free/open-source tool named [Dependency Check](https://github.com/jeremylong/DependencyCheck "OWASP Dependency Check") and focused on known vulnerabilities. It is available as a simple java command line, but also as plugins for Ant, Maven or Jenkins.
 This tool shall scan your application to identify third-party libraries (with versions). Then for each library, the tool will check if the identified version has Common Vulnerabilities and Exposures (CVE) referenced in the [National Vulnerability Database](https://nvd.nist.gov/ "National Vulnerability Database (NVD)"). With this tool, you will have a clear view of the known vulnerabilities in your application

### Run your own security assessment tests

Even if there are no known CVE identified, the combination of different libraries and how you use these APIs could create a vulnerability. Before deploying a new major version of your application on production, consider doing a full static code analysis and a manual code review. Running a penetration test is also recommended for very sensitive applications.

### Educate your developers

Developer should not add a new dependency in the application without a strict validation process.
 First, before adding a new library, check that no existing dependency can do the same thing.
 E.g. on a previous project, a developer that wanted to manipulate a String decided to add Google Guava as a dependency. I have nothing against Guava, but Apache Commons Lang was already a dependency and could do exactly the same thing. Why adding a new library (and its own dependencies) only for one method? Your application will be bigger, more difficult to maintain and more important, you could add new vulnerabilities!
 If you really need that library, check it has no known vulnerabilities on the NVD. If vulnerabilities exist, verify they are fixed in the targeted version.
 If you use Maven with a repository manager, you should also apply this validation process on this repository to reject a library which has known vulnerabilities.

### Enable the Java Security Manager

My last recommendation about this topic would be to enable and properly configure the Security Manager according to your needs. It will be your last line of defense to protect your system against some vulnerability exploits on Java libraries.
 Of course, it is only for Java applications. It won’t protect your web server or your database…

To see all articles related to OWASP Top 10, follow the tag [#owasp](https://blog.ippon.tech/tag/owasp/ "OWASP Top 10")

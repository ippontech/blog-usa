---
authors:
- Philippe Cery
categories:
- OWASP
- Sécurité
- Web
date: 2013-10-21T08:56:26.000Z
title: "OWASP Top 10 - A2 Broken Authentication and Session Management"
image: 
---

## Description

The attacker steals his victim’s credentials or any information that will help him impersonating the victim on your application.

## Examples

### Client attack

To authenticate on my bank website, my password is very simple to remember, it’s my birthdate. But I want to change it with my wife’s birthdate and I receive the following email from my bank :
 Dear Philippe,
 Your identifier is : philippe
 Your new password is : 01021975
 Regards,
 Your Bank.

Any attacker could easily guess your password. If he knows your bank website requires a 8-digits password, with social engineering, it is easy to have all your family members’ birthdates (parents, spouse, children…).
 And even if your password was not easy to guess, an experienced attacker could also intercept your email and get your identifier and your password.

### Server attack

An experienced attacker has downloaded the login and password of all customers of your bank. Hopefully, all data are in clear text, so the attacker can use the stolen credentials before the administrator can alert the customers…

## Mitigations

### Educate the users of your application

This attack can be very easy to use (even without any knowledge in computer sciences) because all users are not aware of the risks. I have already seen DBA hiding the database credentials on a post-it under their keyboard !
 Users should :

- keep their credentials safe
- should not use easy to guess passwords (0000, 123abc, password or even P@ssw0rd)
- should not use personal information (names, phone numbers, birth date) in their passwords
- not use the same password on different application. If one of these applications is attacked, all your other accounts are potentially compromised.
- be careful when using a public wifi network. An attacker can easily intercept all communication (encrypted or not).

And of course, be careful with “social engineering”. Social engineering consists in manipulating a victim to obtain confidential or personal information. It is usually a good method for example to get the personal phone number or address of a VIP (a CEO, a HR director…). But it can also be used to get your mother’s maiden name which is often a question proposed by most of the web sites to recover your lost password.

<div class="wp-caption aligncenter" id="attachment_8897" style="width: 750px">![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/security.png)
source: http://xkcd.com

</div>### Identity and Access Management (IAM)

You can implement your own IAM framework from scratch or use an existing solution like [Fortress](http://iamfortress.org/ "Fortress").

Anyway, you should apply the following rules with more or less restrictions depending on how sensitive the information you need to protect are.

#### Password strength

The password must be strong and difficult to guess.
 Usually, a strong password should have at least 8 characters. There are 4 sets of characters : digits, uppercase letters, lowercase letters and special characters. The characters of the password should be from at least 3 of the 4 sets of characters.

#### Password generation and distribution

The application must generate a temporary strong password.
 The password and the identifier must be sent separately in 2 different emails or, better, using 2 different channels (e.g. Email + SMS). For very sensitive applications, you can even use PGP to encrypt the email (possible for an intranet, more difficult for a public internet web site).
 The user must modify the temporary password at first login time.

#### Multi-factor authentication

For users that have access to sensitive information and operations, you should require at least 2 credentials of different types. There are 3 types of credentials :

- « Something I Know » like a password,
- « Something I Own » like an USB token,
- « Something I am » like a fingerprint.

#### Password renewal

All credentials (password, certificate…) must expire regularly. The frequency will depend on what the owner of these credentials can do on your application.
 When a password expires, the user must change it at login time.

#### Password recovery

If the user forget its password, the application must generate a temporary password, valid for a couple of hours maximum. The temporary password is sent to the user by email, eventually PGP encrypted. Then the user must change it at login time.

#### Account locking

To prevent brute force attacks, the account must be locked after too many (3) failed login attempts.
 Usually, it is also preferable to inactivate the account when it is not used for a long time (a couple of months).

### Authentication credentials protection

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/security_question.png)
source: http://xkcd.com

The passwords stored in your database must be encrypted or hashed. If an attacker can download your table of users, it will be difficult for him (but not imposible) to retrieve the passwords in clear text.
 Two different options: hash or encryption.

#### Hash (one-way encryption)

If you don’t need to retrieve the real password in clear text, then hash is preferable. It is like a one-way encryption. Once hashed, it is theoretically impossible to retrieve the original value. Of course, it is still possible to « guess » the password. If your bank had stored the password hash value, the attacker that have downloaded the list of credentials would have to apply the hash algorithm on a list of frequently used passwords and verify if the resulting hash value exists in the stolen list.
 Note that several passwords could give the same hash value. If the attacker finds a matching hash value, he can use the password even if it is not the real one.
 Several hash functions exist. The most used are MD5 and SHA-1. MD5 is known to have a high risk of collision (several messages give the same hash value) so it is not recommended. SHA-1 is acceptable on a simple website with no sensitive data to protect but SHA-2 is preferable when security really matters.
 But remember a hash value is very fast to compute (less than 0.1ms on an old computer). Imagine how many passwords the attacker can try per second to find matching hash values in the stolen list with a recent computer or a set of “zombie” computers…

#### Password-based key derivation function

For a better protection, I recommend using a password-based key derivation function. The goal is simple : make it longer for the attacker to decrypt the passwords in the database.

The most used is PBKDF2 (simply Password-Based Key Derivation Function 2) a.k.a PKCS#5.
 The principle ? You generate a salt (i.e a random value) for each password. The function will apply a pseudorandom function on the password to protect along with the generate salt. Then it will apply again the same pseudorandom function on the same password along with, this time, the result obtained at the previous step. And so on…
 Obviously, you store the resulting hash value and the initial salt in the database.
 When the user enters his password, you apply the same PBKDF2 algorithm to the provided password, using the stored salt and the appropriate number of iterations and then you compare the resulting hash value with the expected value.
 For a strong protection, you should apply at least 10 000 iterations. Don’t worry, if a simple hash takes less than 0.1ms, it will stay very fast to generate (a few milliseconds), but for the attacker, it will be a lot more difficult because he will have to find first the number of iterations (this must be a secret value), then apply this « slow » algorithm on each password of his dictionary.

SunJCE provides an implementation with HMAC-SHA1 as the pseudorandom function.

```language-java
// Generating a 32-bytes salt<br></br>
SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
byte[] salt = random.generateSeed(32);
// Apply PBKDF2 algorithm on the password, with the generated salt and 10000 iterations
SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1", "SunJCE");
SecretKey key = f.generateSecret(new PBEKeySpec(password, salt, 10000, 256));<br></br>
byte[] hashedPassword = key.getEncoded()
```

Note: If you need yet stronger protection, check out [bcrypt](http://www.openbsd.org/papers/bcrypt-paper.ps "bcrypt") or [scrypt](http://www.tarsnap.com/scrypt/scrypt.pdf "scrypt"). Sun does not support these KDFs in their security providers but you can find open-source implementation for Java like [jBCrypt](http://www.mindrot.org/projects/jBCrypt/ "jBCrypt") and [wg/scrypt](https://github.com/wg/scrypt "wg/scrypt").

#### Encryption

In some rare cases, you may need to retrieve the password in clear text. For example, if you have a password recovery policy that requires to send the password to the user after he has provided some secret answers. Note that this policy is not recommended (refer to my recommended password recovery policy above).
 You can choose symmetric or asymmetric emcryption depending on your needs.
 If your application encrypts the password before storage then decrypts it when needed, symmetric algorithm is preferable. The secret key is stored in a keystore accessible only to your application.
 If the password is encrypted by a third-party component before storage (e.g an administration tool), it is preferable to use asymmetric algorithm (public-private key pair) instead of sharing a secret key. The third-party component encrypts the password using your public key and send it to you for storage. And you keep your private key safe in your keystore for decryption.

### Session protection

#### Session ID

As you know, HTTP protocol is stateless but with dynamic web applications, we need to keep conversational information between pages. You can add this information as request parameter in the URL, as HTTP headers or in HTTP cookies. One of the most important information you will need to keep is the session identifier. This session ID is generated by the server and shared with the client in a cookie or in the URL when cookies are not accepted by the client.
 This identifier is very sensitive. It can be used by an attacker to impersonate an authenticated user. So, obviously, this information cannot be exposed in the URL and must be protected in the session cookie.
 This can be done by properly configuring your server or application.

If your application is Servlet 3.0 compliant, you can configure it by adding the following in your `web.xml`:
```language-xml
<session-config>
  <tracking-mode>COOKIE</tracking-mode>
  <cookie-config>
    <secure>true</secure>
  </cookie-config>
</session-config>
```

Otherwise, you will need to configure your server. For example, if you use Apache Tomcat 6, you can remove the `jsessionid` from URL by adding the attribute `disableURLRewriting="true"` in your `context.xml`:
```language-xml
<Context ... <em>disableURLRewriting="true"</em>
</Context>
```

And to protect the session cookie, just add secure=”true” to the appropriate connector in `server.xml`:
`<Connector port="..." protocol="..." <em>secure="true"</em>/>`

#### Session fixation

Obviously, the session ID must be changed after each login or logout.
 When anonymous, the session ID can be exposed because nothing sensitive is accessible. But once authenticated, if you keep the same session ID after a successful authentication, even if you protect it, it would be already known. An attacker could use this session ID and gain access to your personal account.
 For the same reason, after logout, the previous session must be invalidated thus the session ID will be changed.

### Session timeout

Each active session is a potential open door on your application for an attacker. If you keep the sessions alive forever, you give more time to the attackers to steal active session identifiers and impersonate more victims. That’s why user sessions must expire after a few minutes of inactivity. The acceptable timeout can be from less than 10 minutes for sensitive applications up to 30 minutes for simple public application.
 On Apache Tomcat 6, the default session timeout is defined in `$TOMCAT_HOME/conf/web.xml` at 30 mn. If you want a different value for a particular deployed application, just define the desired value in its `web.xml`.

### Encrypted transport

All the protections described in the previous sections are useless if an attacker can easily intercept the password provided by the user at login time. To prevent that, it is highly recommended to send the credentials over a secure SSL connection.
 Be careful though. Vulnerabilities were found in SSL in the past and no doubt new weaknesses will be found in the future. The most important mitigation against SSL vulnerabilities is to have your SSL stack (usually JSSE or OpenSSL) up-to-date with the latest security patches.
 You should also choose carefully the SSL versions you will support and the cipher suites you will accept during handshake.

#### SSL versions

I recommend at least TLS 1.0 (SSL 3.1) released in 1999. Older versions have known important vulnerabilities. TLS 1.1 (SSL 3.2 in 2006) and TLS 1.2 (SSL 3.3 in 2008) are preferable but some server environments and clients won’t support them. TLS 1.1+ is supported by JSSE starting from JRE 1.7) and some old browsers do not support these versions.

#### Cipher suites

A cipher suite is a combination of a key exchange algorithm, a cipher algorithm (with key size) and a message authentication code (MAC) algorithm that will be used to negotiate how the exchanges between the client and the server will be protected.
 During handshake, the client sends the list of cipher suites he supports then the server chooses the one he prefers amongst its own acceptable cipher suites. Of course, if none of the cipher suites accepted by the server match the cipher suites proposed by the client, the handshake will fail and the server will refuse the connection.
 But some cipher suites have vulnerabilities and it is easy for an attacker to propose only weak cipher suites. Of course, you should never accept them. Usually, your SSL stack will propose a default list that do not have the weakest cipher suites but I don’t recommend to rely on that list. Exactly like the white-list input validation described in the previous article about Injection, it is highly recommended to give explicitly the list of acceptable cipher suites. No surprise if you want to change your SSL stack.

#### Known vulnerabilities

Three important vulnerabilities were discovered recently on TLS. They can be used to decrypt the session ID in the header.
 In 2011, [BEAST](http://vnhacker.blogspot.com/2011/09/beast.html "BEAST attack") (Browser Exploit Against SSL and TLS) found by two security researchers, Juliano Rizzo and Thai Duong, exploits a CBC vulnerability. TLS 1.0 and older versions are impacted if you use block ciphers like AES or 3-DES.

- Long-term mitigation: support only TLS 1.1 and TLS 1.2 but most of the browsers do not support theses versions and if they are supported, they are usually disabled by default.
- Short-term mitigation: with TLS 1.0 or older, only accept cipher suites with RC4 cipher algorithm. Although a recent vulnerability has been found on RC4, it is still the best mitigation with TLS 1.0 or older. Be aware that most of the servers will select the first cipher suite they support in the ordered list provided by the client. So don’t accept any vulnerable cipher suites.

In 2012, the same two security researchers revealed [CRIME](https://docs.google.com/a/ippon.fr/presentation/d/11eBmGiHbYcHR9gL5nDyZChu_-lCa2GizeuOfaLU2HOU/edit#slide=id.g1d134dff_1_222 "CRIME attack") (Compression Ratio Info-leak Made Easy) that exploits a vulnerability in TLS when data compression is activated. The mitigation is to not use data compression along with TLS. If the client (the browser) proposes compression algorithms, the server must refuse all of them.
 Last but not least, in 2013, three other security researchers, Angelo Prado, Neal Harris and Yoel Gluck, improve CRIME with [BREACH](https://media.blackhat.com/us-13/US-13-Prado-SSL-Gone-in-30-seconds-A-BREACH-beyond-CRIME-Slides.pdf "BREACH attack") (Browser Reconnaissance and Exfiltration via Adaptive Compression of Hypertext). This time, they exploit a vulnerability in data compression at HTTP protocol level so BREACH works even if TLS data compression is disabled. The best mitigation is to have an efficient CSRF protection (dynamic CSRF token per request). You could also disable HTTP compression but this will have a big impact on the performances.
 Tip : To know if your server is vulnerable to BEAST and CRIME, you can download [TestSSLServer](http://www.bolet.org/TestSSLServer/ "TestSSLServer") tool. For BREACH, you can wait for an updated version or download the source and add the BREACH detection by yourself. The tool is under MIT-like license.

Other attacks can, for example, force a SSL/TLS renegotiation or downgrade the protocol version.

Although the risk is real, to be exploitable, most of the TLS vulnerabilities require the attacker and the victim to be on the same network (public Wifi, LAN…) or the attacker to have access to the victim’s network (ISP or government employees).

To see all articles related to OWASP Top 10, follow the tag [#owasp](http://blog.ippon.fr/tag/owasp/ "OWASP Top 10")

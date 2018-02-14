---
authors:
- Laurent Mathieu
categories:
- OWASP
- Sécurité
- Web
date: 2013-11-18T08:50:12.000Z
title: "OWASP Top 10 - A6 Sensitive Data Exposure"
image: 
---

## Description

We have seen in the previous articles that an experienced attacker can easily intercept data in transit (e.g. on a public Wifi hotspot) or have access to data stored in your database (e.g. using SQL injection). If the stolen information is sensitive (password, credit card number, personal data…), it must have been encrypted.


## Examples

### Data in transit

My favorite restaurant provide a free wifi access point. Between the appetizer and the main meal, I want to check if my credit card will be accepted. I go to my bank’s website, enter my login and password. Well, I’m sure you remember my bank’s website do not use a secure connection. An attacker on the next table intercepts the network traffic and steals my session cookie. You know the end of the story… If you don’t, you missed my article about “[Broken Authentication and Session Management](http://blog.ippon.fr/2013/10/21/owasp-top-10-a2/ "Broken Authentication and Session Management")”

### Data at rest

Using SQL [Injection](http://blog.ippon.fr/2013/10/11/owasp-top-10-a1/ "Injection"), an attacker can download the credentials of all clients of my bank. Of course, the passwords are in clear text…  
 Note that this scenario is also applicable with an insider attack. If sensitive data are not encrypted, they are accessible to the database administrator.


## Mitigations

### Avoid storing sensitive data

Store sensitive data only when it is absolutely necessary. Never store sensitive data in log files.  
 As soon as the sensitive data is not necessary anymore, delete it.  
 Note: some sensitive data must never be stored. E.g. if your application processes credit/debit card data, according to PCI-DSS, you must never keep any trace of the CVV number, the 3/4 digits you will usually find behind the card.

### Protect sensitive data

In [Broken Authentication and Session Management](http://blog.ippon.fr/2013/10/21/owasp-top-10-a2/ "OWASP Top 10 – A2 Broken Authentication and Session Management"), we have already discussed how to protect specific sensitive data like credentials in transit and at rest. The same mitigations can be applied for other sensitive data.

#### In transit

Encrypt all exchanges containing sensitive data during transit. The encryption can be done at transport level (SSL/TLS) or at message level (e.g. WS-Security Encryption for SOAP messages).  
 Regarding the transport, choose properly the version of SSL and the cipher suites to make sure your sensitive data won’t be decrypted on the wire. For the details, have a look on [Broken Authentication and Session Management](http://blog.ippon.fr/2013/10/21/owasp-top-10-a2/ "OWASP Top 10 – A2 Broken Authentication and Session Management").  
 Regarding WS-Security, AES-256 is recommended for data encryption, 3DES and shorter AES keys are weak. For key wrapping, RSA with OAEP padding is preferable because the other choice, RSA 1.5 is known to have vulnerabilities. RSA keys of at least 2048 bits is recommended.

Tip: To use AES 256 or RSA 2048bits keys, you will need to upgrade your JRE with JCE unlimited strength policy files.

#### At rest

Besides the passwords to access your web application, you can have other data you may consider sensitive enough to protect them in your database like credit/debit card data, medical data, financial information…  
 If you don’t need to retrieve these sensitive data in the clear, use PBKDF2 mechanism to protect them like explain in [Broken Authentication and Session Management](http://blog.ippon.fr/2013/10/21/owasp-top-10-a2/ "OWASP Top 10 – A2 Broken Authentication and Session Management"). Otherwise, you will need a 2-way encryption mechanism.

The easiest way is to use a secret key stored in a secure keystore to encrypt and decrypt the sensitive data. I recommend this option to encrypt sensitive data stored in the database.

Note: Never share a secret key. If the component that encrypts the sensitive data is not the one that will decrypt the data, use asymmetric algorithm instead. E.g. the front-end server encrypts the entered data before sending it to the back-end server for storage and then the back-end uses the encrypted data when needed.

Of course, only your application should have access to your decryption key. If you use your database to encrypt and decrypt the sensitive data, the encrypted data will be automatically decrypted after a SQL injection attack. If your DBA has access to the decryption key, he can decrypt any sensitive data in your database.  
 Last but not least, you should use different keys for different purposes. E.g. the key used to protect the credit card data should not be used to also protect the financial information. If one decryption key is compromised, your sensitive data won’t be totally exposed.

Obviously, with that option, you will have to protect the keystore password and the key password if any…  
 Moreover, you may need to protect other configuration data like the password to access the database.  
 For these configuration data and the password of the keystore, I recommend another encryption mechanism, based on a machine key, instead of using a shared key stored in a keystore.

The principle is quite simple:  
 1. Take 3/4 information associated to the host machine and that won’t be changed (e.g. IP address, host name, MAC address, user.name or user.home system properties, hard disk ID or CPU ID if accessible, OS name (without version), OS licence number if accessible…).  
```language-java
byte[] part1 = System.getProperty("user.home").getBytes("UTF-8");
byte[] part2 = InetAddress.getLocalHost().getAddress();
byte[] part3 = System.getProperty("user.name").getBytes("UTF-8");
byte[] part4 = InetAddress.getLocalHost().getHostName().getBytes("UTF-8");
```

2. Generate a hash with the selected values  
```language-java
MessageDigest md = MessageDigest.getInstance("SHA-256");
md.update(part1);
md.update(part2);
md.update(part3);
md.update(part4);
byte[] passwordAsByteArray = md.digest();
```

3. Generate a random value  
`byte[] salt = SecureRandom.getInstance("SHA1PRNG", "SUN").generateSeed(32);`

4. Generate a secret key using PBKDF2 mechanism with the hash as password and the random value as salt. The number of iterations you apply is up to you.  
```language-java
SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1", "SunJCE");
ByteBuffer bb = ByteBuffer.wrap(passwordAsByteArray);
byte[] passwordAsCharArray = Charset.forName("UTF-8").decode(bb).array();
SecretKey secretKey = skf.generateSecret(new PBEKeySpec(passwordAsCharArray, salt, 10000, 256));
```

5. Encrypt the sensitive data and store the encrypted data in the configuration file and store the generated salt anywhere (the salt is not a sensitive data).

Besides the ability to encrypt keystore password, the main advantage of this mechanism is, if you have several instances of your application, the encryption key will be different on each instance. So the configuration files will be different even if they contain exactly the same configuration.  
 Of course, you will have to modify the configuration file reader to automatically generate the key again (step 1 to 4) and decrypt the encrypted data when on-the-fly. E.g. if you store your configuration in properties files, you just need to override the getProperty method.

Tip: Using the same mechanism, you could add more entropy by generating a salt for each configuration file. And if you need even more, you can generate a salt for each configuration entry to be encrypted… Don’t forget to store the salt at the right place. Add an entry in the file if you use one key per configuration file or along with each encrypted data if you prefer one key per entry.

One last advice: don’t forget to deactivate cache and auto-completion in sensitive pages of your web application.

To see all articles related to OWASP Top 10, follow the tag [#owasp](http://blog.ippon.fr/tag/owasp/ "OWASP Top 10")

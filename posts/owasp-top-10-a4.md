---
authors:
- Philippe Cery
categories:
- OWASP
- Sécurité
- Web
date: 2013-11-04T08:45:32.000Z
title: "OWASP Top 10 - A4 Insecure Direct Object References"
id: 5a267e57dd54250018d6b5bf
image: 
---

## Description

The application exposes a direct reference (functional identifier, database key, file path…) to a resource. Thanks to that direct reference, an attacker can guess other direct references and access to other resources.
 Usually, you will find direct references in links and selection lists (drop-down list, radio buttons and checkboxes) built dynamically with objects retrieved from database.

## Examples

Let’s take again the same example with my bank’s website.
 I can access to my account page simply by providing my IBAN in the URL:
*http://www.mybank.com/details?iban=FR2711111222333444555666777*

An attacker could have access to any other account by simply providing a valid IBAN!

## Mitigations

### Access control

It may be obvious for most of you but it’s important: before giving access to a protected resource, verify that the requestor is authorized to access that resource.
 For my example, you must verify the account with the requested IBAN belongs to the authenticated user before you display the account details.

### Indirect object reference

Replace the direct reference with an indirect reference.
 The principle is simple: on server-side, for each direct reference, generate another identifier and use this identifier in your web page instead of the direct reference. The generated identifier can be a random value, a hash value, a numeric suite… Anything you want as long as it is not possible for an attacker to guess a direct reference from the exposed identifier. Of course, on server-side, you will need a mapping table to retrieve the direct reference.
 When you create your view objects, replace all direct references with generated indirect references:
```language-java
...
Map<String, String> accountIndirectRefs = new HashMap<String, String>();
List<AccountVO> accountVOList = new ArrayList<AccountVO>();
String accountIndirectRef = null;
AccountVO accountVO = null;
for(String account : accountList) {
accountVO = new AccountVO();
accountIndirectRef = generateIndirectReference();
accountIndirectRefs.put(accountIndirectRef, account.getIban());
accountVO.setReference(accountIndirectRef);
accountVOList.add(accountVO);
}
session.setAttribute("accountIndirectRefs", accountIndirectRefs);
...
```

In this sample, you can see the indirect reference generated using generateIndirectReference() method does not rely on the direct reference. This method can return a simple suite of numbers (1, 2, 3…) or characters (A, B, C…) but I recommend a random alphanumeric string. Thus an attacker won’t be able to guess the possible indirect values. We will talk about CSRF attacks in another article.

In the “My Accounts” page, the links to the account details won’t have the real IBAN anymore as a parameter but the indirect reference in a new `ibanRef` parameter. When you process the request, you retrieve the direct reference from the provided indirect reference:
```language-java
...
String ibanRef = request.getParameter("ibanRef");
Map<String, String> accountIndirectRefs = session.getAttribute("accountIndirectRefs");
String iban = accountIndirectRefs.get(ibanRef);
...
```

If the indirect reference is not found in session, it is certainly an attack.
 In this example, it could be also a customer that have saved the link in his favorites but, in the real world, you would not have a direct link to the account page, right?

You can also use third-party framework like ESAPI to generate and manage your indirect references. ESAPI provides the [AccessReferenceMAP<K>](http://owasp-esapi-java.googlecode.com/svn/trunk_doc/latest/org/owasp/esapi/AccessReferenceMap.html "ESAPI's AccessReferenceMAP interface") interface and proposes out-of-the-box two implementations to generate either an integer or a random string indirect reference. Of course, you can create your own implementation to generate whatever you want…

To see all articles related to OWASP Top 10, follow the tag [#owasp](http://blog.ippon.fr/tag/owasp/ "OWASP Top 10")

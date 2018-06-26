---
authors:
- Philippe Cery
tags:
- OWASP
- Security
- Web
date: 2013-10-11T08:30:53.000Z
title: "OWASP Top 10 - A1 Injection"
image: 
---

## Description

The attacker sends untrusted data that will be injected in the targeted application to change its behaviour. The goal of this attack is usually to steal data but it can also be used to delete or corrupt your data or result in denial of service.

## Example

I’m connected on my bank website and I want to view information on one of my accounts. A link in the page contains the iban parameter.

The SQL query is built by injecting the IBAN value in a query template.

```language-java
String iban = request.getParameter("iban");
String sqlQuery = "select * from ACCOUNTS a where a.IBAN='" + iban + "'";
ResultSet resultSet = connection.executeQuery(sqlQuery);
```

An attacker could call the URL in the link with any value he wants in iban parameter. Let’s assume he enters `' or '1'='1`.
 The SQL query `select * from ACCOUNTS a where a.IBAN='' or '1'='1'` will be executed and will return all accounts stored in the database!

### Notes

Remember injection attacks are possible everywhere you have an interpreter. E.g. HTML, Javascript, CSS, HTTP headers, XML parsers, SQL, NoSQL, Xpath, LDAP…

In « A3 – Cross-Site Scripting » article, we will see an example of Javascript injection to hijack a session.
 OS injection is also possible using Runtime.getRuntime().exec(…) methods. Although the threat is real, web applications rarely execute OS external applications on the server.

## Mitigations

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/xkcd_inject.jpg)

### White-list input validation

The first protection is to know exactly what you expect as input from your users, so you can verify the entered data is valid. The recommended way is a white-list input validation on server side. That means you verify all the characters entered by the user are explicitly allowed. Moreover, the length and the format must be verified when applicable.
 E.g. The IBAN format is known thus it can be verified before using it in an SQL query.

To validate your input, you can use the standard [JSR-303 Bean Validation API](http://docs.oracle.com/javaee/6/api/javax/validation/package-summary.html "Java Documentation") if your preferred presentation framework supports it (e.g. JSF 2, Spring MVC 3, GWT 2.5). You can also use the validation API provided by your presentation framework (e.g. [JSF’s Validator API](http://docs.oracle.com/javaee/6/api/javax/faces/validator/package-summary.html "Java Dcoumentation")) or third-parties like [ESAPI](https://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API "ESAPI").
 The Bean Validation API can also be used with your preferred persistence framework to validate (again) your data before persisting it in your database.

Tip: When you define a regular expression, don’t forget to specify the beginning `^` and the end `$` of the pattern otherwise an invalid input could be accepted with unwanted characters.

### Canonicalization (C14N)

Sometimes, entered data can have different acceptable formats. E.g. a phone number can be entered with only digits or with spaces, dashes, dots between groups of digits and prefix between parentheses. A path can be absolute, relative or a symbolic link. A character in an HTML page can be encoded.
 Canonicalization consists in transforming entered data to its simplest fixed format. For a phone number, it consists in removing all formatting characters to keep only digits. A canonicalized path is the absolute path. Once canonicalized, HTML data doesn’t contain encoded characters.
 While canonicalization is optional, it is recommended when special characters (like &, , %…) are allowed in input data.
 If you want to canonicalize your input data, it must be done before input validation otherwise an attacker could pass the validation step by encoding some characters with allowed characters.
 E.g. If `&` and `;` are allowed while `<` and `>` are not, an attacker can inject `<` and `>` simply by replacing these characters with their encoded values `<` and `>`. If you apply canonicalization after input validation, the input data will be considered as valid, canonicalized then displayed and interpreted by the browser as an HTML tag.

ESAPI proposes amongst other features, canonicalization for almost all interpreters (HTML, Javascript, URL, OS specific, databases…)
```language-java
Encoder encoder = ESAPI.encoder();
String inputHTML = "<script>alert("hello");</script>";
String normalizedInputHTML = encoder.canonicalize(inputHTML);
System.out.println(normalizedInputHTML);
```
 ==> `<script>alert(“hello”);</script>`
```language-java
String inputURL = "%3Cscript%3Ealert("hello");%3C/script%3E";
String normalizedInputURL = encoder.canonicalize(inputURL);
System.out.println(normalizedInputURL);
```
 ==> `<script>alert(“hello”);</script> `
```language-java
String potentialIntrusion = "%3Cscript>alert%28%22hello&#34%29%3B%3C%2Fscript%3E";
String normalizedPotentialIntrusion = encoder.canonicalize(potentialIntrusion);
```
throws IntrusionException because Mixed encoding was detected

Tip: To protect your application against Unicode-based injection attacks, don’t forget to set the appropriate language locale and character set.

### Prepared statement (SQL)

A prepared statement is a precompiled SQL statement. Beyond the performance advantage, prepared statements is highly recommended to prevent SQL injection attacks. Instead of creating an SQL statement by injecting an untrusted string in a template, you create a static string in which you declare bind variables.
```language-java
String iban = request.getParameter("iban");
// iban should be canonicalized then validated before continuing
String sqlQuery = "select * from ACCOUNTS a where a.IBAN=?";
PreparedStatement stmt = connection.prepareStatement(sqlQuery);
stmt.setString(1, iban);
ResultSet resultSet = stmt.executeQuery();
```

In this case, if the attacker replaces the IBAN value with `' or '1'='1`, the executed query will be equivalent to `select * from ACCOUNTS a where a.IBAN=''' or ''1''=''1'`. Obviously, this query will return no result.
 Of course, you have applied the mitigations in the previous sections, you should have rejected this invalid IBAN before you try to execute the query as it does not match the IBAN pattern.
 An acceptable alternative to the prepared statement is a stored procedure. The most important thing to understand is to use bind variable instead of string concatenation to create your query.

Tip: With [JPA](http://docs.oracle.com/javaee/6/api/javax/persistence/package-summary.html "Java Documentation"), you can create queries (or named queries) with bind variables to declare your SQL statements. Behind the scene, your preferred JPA implementation (Hibernate, OpenJPA…) will create a prepared statement for you.

With simple queries, you create the query directly in your DAO class:
```language-java
public class AccountDaoImpl {
  …
  public Lisy<Account> findAll() {<br></br>
    TypedQuery<Account> query = em.createQuery("SELECT a FROM Account a", Account.class);
    return query.getResultList();
  }
```

```language-java
public Account findByIban(String iban) {
    TypedQuery<Account> query = em.createQuery("SELECT a FROM Account a WHERE a.iban = :iban", Account.class);
    // we assume the iban is already canonicalized and validated<br></br>
    query.setParameter("iban", iban);<br></br>
    return query.getSingleResult();<br></br>
  }...
}
```

With named queries, you declare your queries on your entity class:
```language-java
@Entity
@NamedQueries({
  @NamedQuery(name="Account.findAll", query="SELECT a FROM Account a"),
  @NamedQuery(name="Account.findByIban", query="SELECT a FROM Account a WHERE a.iban = :iban"),
})
public class Account {
  …
}
```

Then you use them in your DAO class:
```language-java
public class AccountDaoImpl {
  …
  public Lisy<Account> findAll() {
    TypedQuery<Account> query = em.createNamedQuery("Account.findAll", Account.class);
    return query.getResultList();
  }
```

```language-java
  public Account findByIban(String iban) {
    TypedQuery<Account> query = em.createNamedQuery("Account.findByIban", Account.class);
    // we assume the iban is already canonicalized and validated
    query.setParameter("iban", iban);
    return query.getSingleResult();
  }
  …
}
```

Personnaly, I recommend named queries when possible. With queries created at runtime, you can still be vulnerable to SQL injection if you use string concatenation. With named queries, the query is prepared at initialization time. At runtime, you can only pass your parameters and execute the query.

To see all articles related to OWASP Top 10, follow the tag [#owasp](http://blog.ippon.fr/tag/owasp/ "OWASP Top 10")

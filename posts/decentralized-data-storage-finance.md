---
authors:
- Greg Kontos
tags:
- Verified Credentials
- Personal Data Stores
- Solid
- Decentralized Internet
- Distributed Internet
date: 2021-08-30T11:00:13.000Z
title: "Distributed Personal Data Stores and Verified Credentials: Potential Opportunities in Finance"
image: 
---

## Summary 
In this article we will explore the possible impacts and benefits of personal data ownership ( aka personal data stores) and decentralized digital identity ( aka verified credentials ) within banking and finance.  We'll explore what if any are the benefits to customers if banks integrate with these personal data stores?  Are there benefits to banks themselves if they undertake these integrations?  We'll also take a look at how likely these distributed data stores are to reach a tipping point of adoption? 

# Distributed Personal Data Stores and Verified Credentials: Potential Opportunities in Finance

Various corners of the internet envision a future in which users own and control the content they create online.  These visions go by a variety of names such as Personal Data Stores, Trusted Digital Web, Self-Sovereign Identity, Verified Credentials, and more.  Here we will indelicately lump all of these concepts under the general umbrella of 'distributed personal data stores' to indicate both that 1) the data is user owned and that 2) the data is architecturally distributed across many locations.  There are a variety of projects that implement some version of this user controlled data paradigm.  These solutions are variously built using peer-to-peer technologies, assorted cryptography, and blockchain to provide access control and immutability.  To achieve an internet where this user owned data can be securely accessed and used by multiple online applications will require standards.  Those standards are beginning to take shape and an ecosystem is in the early stages of development.  As the ecosystem develops they may offer opportunities to address financial customers' privacy concerns and create more personalized banking experiences.

## The Context For Change
According to [a 2020 Harvard Business review article](https://hbr.org/2020/01/why-companies-make-it-so-hard-for-users-to-control-their-data) 'A [2019] IBM study found that 81% of consumers say they have become more concerned about how their data is used online'. According to that same [2019 IBM study](https://www.axios.com/consumers-kinda-sorta-care-about-their-data-3292eae9-2176-4a12-b8b5-8f2de4311907.html), 89% say technology companies need to be more transparent about their products.  However, 71% are willing to accept a loss of privacy for the benefits of technology.  And only 30% of the survey group were aware of data breaches that have occurred.  The HBR article calls this a '“privacy paradox” where users’ concerns aren’t reflected in their behaviors'. 

While people are concerned about their privacy, there are also more reasons than ever to be concerned.  According to this [Forbes article from March 2021](https://www.forbes.com/sites/chuckbrooks/2021/03/02/alarming-cybersecurity-stats-------what-you-need-to-know-for-2021/?sh=3c7ca16e58d3) "Nearly 80% of senior IT and IT security leaders believe their organizations lack sufficient protection against cyberattacks" and "Commission received 1.4 million reports of identity theft last year, double the number from 2019".  Which makes this statement from 
[2020 World Economic Forum:'Reimagining Digital Identity'](https://canada-ca.github.io/PCTF-CCP/docs/RelatedPolicies/WEF_Digital_Identity.pdf) seem more poignant: 
> The digital revolution has also brought with it new forms of fraud, identity theft and misuse or abuse of personal data. Cybersecurity incidents have become a pervasive and seemingly permanent threat to personal data privacy, threatening the foundations of trust on which society functions. 

In a world where people are concerned about how their data is used, cybersecurity breaches are pervasive, but people will trade privacy for the benefits of technology; how can banks offer more personalized experiences to their customers?  Against the backdrop of this customer sentiment, Tim Berners-Lee has been working since 2015 on the (sort of, depending on who you talk to) high profile Solid Platform which promises both to create interoperability that permits new experience as well as transparency in how data is used. An important foundational step towards creating a functional decentralized ecosystem was [the 2019 W3C recommendation of the specification for 'Verified Credentials'](https://www.w3.org/standards/history/vc-data-model) which aims to create a system of verification to permit authorization for these decentralized records.

## Solid Platform
The Solid Platform was started by Tim Berners-Lee, a founder of the modern web, in 2015.  Berners-Lee and team began updating the W3C specifications in 2018 to include the use of a distributed personal data store ([source: wikipedia.org](https://en.wikipedia.org/wiki/Solid_(web_decentralization_project))).  The Solid Project calls these distributed data stores 'pods'.  "Pods are like secure personal web servers for data. When data is stored in someone's Pod, they control which people and applications can access it." ([source: SolidProject.org](https://solidproject.org/)   In short, the Solid Specification allows the owner of data to make specific allowances for other organizations to access that data. For instance, [a physician could ask if you want to share your medical history](https://www.rsa.com/en-us/blog/2021-07/verifiable-credentials-the-key-to-trust-on-the-next-web) or a bank would ask if you wanted to share your tax returns for a loan application.


## Verified Credentials

The W3C Verified Credentials works with the Solid Platform to provide a decentralized layer of trust.  This is how you know that physician is who they say they are and the physician knows they are viewing and updating the right patient record.  Verified Credentials focuses on the storage and retrieval of 'claims' about identity that enable 'authorization'.  In the decentralized web, all this would still be stored on decentralized user controlled pods. For instance, a claim could be added by the government which includes a credential to drive.  This would authorize you to drive a car.  This decentralized Verified Credentials approach has attracted the attention of large groups such as OWSAP, RSA, and the UK NHS as a solution to the growing problem of data breaches.


## Use Cases
To date, distributed stores have seen a few use cases implemented within finance. The primary use cases implemented thus far fall into two categories.  1) customer convenience and 2) personalized customer experience. 
 
In 2020 the Solid Platform was used by the NatWest Bank to provide more personalized experiences for customers [(source: Inrupt.com](https://inrupt.com/solid-enterprise-natwest-bbc). The NatWest integration 
> .. demonstrated how a customer could use Solid-powered applications from  different organizations to change their name or register a business. Since all the apps write data to the user’s NatWest Pod, changes only need to be made once. And they could even receive new offers based on their newly recorded “life moment”.

[An example of using decentralized customer information to improve customer convenience](https://hbr.org/2020/01/why-companies-make-it-so-hard-for-users-to-control-their-data) comes from India.
> 'Many Indian banks are taking this forward by preparing to give consumers access to their financial data so that they can directly share it to apply for credit, investment products, or insurance, bypassing the credit ratings agencies as gatekeepers or the lack of credit histories as barriers. This initiative will use third-party mediators and is backed by the country’s central bank.'

The hype and hope is that these implementations are just the tip of the iceberg.  [The Verifiable Credentials W3C specifications](https://www.w3.org/TR/vc-use-cases/#finance) point to several interesting use cases.  For instance, they describe the reuse of 'Know Your Customer' checks in which the 'Know Your Customer' check from one bank can be used by a second bank to verify the customer.  They also describe that the customer could move these verifications to a new verification provider.  Early implementations for Verified Credits seem to focus on health care applications with [RSA partnering with the UK NHS for the sharing of health information](https://www.rsa.com/en-us/blog/2021-07/verifiable-credentials-the-key-to-trust-on-the-next-web).  But the idealized use cases of Solid and Verified Credentials go further, as [Berners-Lee described](https://thenewstack.io/sir-tim-berners-lees-solid-protocol-puts-data-back-in-the-control-of-the-end-user/) enabling
'a more complex financial profile that could help bank the unbanked.'


## Ecosystem and Conflict

With anything new there will always be stumbling blocks.  One of which is that there aren't many engineers working on the decentralized credentials or storage projects.  The solid server project has around 70 Github contributors.  Other projects which are listed as in the early adopter or early majority phase on ThoughtWorks or The Cloud Native Computing Foundation have between 100 and 300 contributors in a sample of about 20 projects.  [Microsoft also has an implementation of the specification](https://docs.microsoft.com/en-us/azure/active-directory/verifiable-credentials/decentralized-identifier-overview) that they independently claim is fully compliant.  However, the W3C site for Verified Credentials lists [13 different implementations of the specification](https://w3c.github.io/vc-test-suite/implementations/), while some are close none are fully compliant with the specification.  Several of these are private groups, but the public groups have only a handful engineers working on them.  As far as the adoption curve is concerned, verified credentials and decentralized storage appears to be in the innovators phase of the adoption curve.  ![the technology adoption curve](../images/2021/08/innovation-curve-chasm.png)

Perhaps a larger potential stumbling block is that there are clear disagreements over the Verified Credentials protocol.  
[Mozilla, the EFF, and the ACLU of California](https://blog.mozilla.org/netpolicy/2020/08/06/by-embracing-blockchain-a-california-bill-takes-the-wrong-step-forward/) in 2020 listed concerns over the use of the W3C Verified Credentials in a pilot program to communicate COVID-19 test results and 'other medical information'.   

Given the W3C use cases, a pivotal dependency appears to be involvement of government at various levels.  The use cases for finance could seem like they would primarily use data from other banks as trusted data.  But foundational verifications like confirmation of a national ID or driver certifications would be best done via government involvement to record data to individual pod storage. This level of software development lift would require a massive undertaking within the government space.  

## Conclusion

The personal data store and verified credentials ecosystem is just beginning to take it's first steps.  With the direction of the W3C specifications to promote interoperability there are a few open projects and companies which are gaining the capability to address the issues of data use transparency, data security, and customer experience.  At the moment VC and personal data stores are not ready for wide scale adoption.  But these solutions are blooming in an online environment with serious concerns around data security, the toxification of personal data as an organizational asset, and the lack of alternate solutions.  Given that, Verified Credentials specifically and Personal Data Stores generally appear to be worth familiarizing with, investigating use cases, monitoring implementations, and monitoring development of government initiatives in the space.  Overall these technologies appear to be a solution which will continue to develop over the next 2-4 years and provide opportunities for banks to reduce duplication, improve their customer's experience, and also improve data privacy.

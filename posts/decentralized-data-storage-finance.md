
## Summary 
In this article we will explore the possible impacts and benefits of decentralized digital identity and personal data ownership ( aka personal data stores) within banking and finance.  Are there benefits to customers if banks integrate with these personal data stores?  Are there benefits to banks themselves if they undertake these integrations?  How likely are these data stores to reach a tipping point of adoption? 

# Distrubuted Personal Data Stores: Opportunity or Disruptor in Finance

Various corners of the internet envision a future in which users own and control the content they create online.  These visions go by a variety of names such as Personal Data Stores, Trusted Digital Web, Self-Sovereign Identity, Verified Credentials, and more.  Here we will indelicately lump all of these concepts under the general umbrella of 'distributed personal data stores' to indicate both that 1) the data is user owned and that 2) the data is architecturally distributed across many locations.  There are a variety of projects that implement some version of this user controlled data paradigm.  These solutions are variously built using peer-to-peer technologies, assorted cryptography, and blockchain to provide access control and immutability.  While adoption of these technologies do not appear to be in a position to force a paradigm shift on the market, they may offer opportunities to address financial customer's privacy concerns and create more personalized banking experiences.

## Context
According to [a 2020 Harvard Business review article](https://hbr.org/2020/01/why-companies-make-it-so-hard-for-users-to-control-their-data) 'A [2019] IBM study found that 81% of consumers say they have become more concerned about how their data is used online'. According to that same [2019 IBM study](https://www.axios.com/consumers-kinda-sorta-care-about-their-data-3292eae9-2176-4a12-b8b5-8f2de4311907.html), 89% say technology companies need to be more transparent about their products.  However, 71% are willing to accept a loss of privacy for the benfits of technology.  And only 30% of the survey group were aware of data breaches that have occurred.  The HBR article calls this a '“privacy paradox” where users’ concerns aren’t reflected in their behaviors'. In a world where people are concerned about how their data is used, but will trade privacy for the benefits of technology; how can banks offer more personalized experiences to their customers?  The Solid platform is a high profile solution which promises both transparency in how data is used along with creating an interoperability that permits new experience.

## Solid
The Solid Platform was started by Tim Berners-Lee, a founder of the modern web, in 2015 with a grant from Mastercard to MIT.  Berners-Lee's began updating the W3C specifications in 2018 to include the use of a distributed personal data store ([source: wikipedia.org](https://en.wikipedia.org/wiki/Solid_(web_decentralization_project))).  The solid project calls these distributed data stores 'pods'.  "Pods are like secure personal web servers for data. When data is stored in someone's Pod, they control which people and applications can access it." ([source: SolidProject.org](https://solidproject.org/) Access to data in a pod is controlled through a decentralized extension of OpenId Connect which itself is an API friendly identity layer on top of the OAuth 2.0 protocol.  With these pieces the Solid specification allows the owner of data to make specific allowances for other organizations to access that data. A reasonable metaphor would be when your browser asks if you want to share your location information with a website. In the case of personal data stores a physician could ask if you want to share your medical history or a bank would ask if you wanted to share your tax returns.

## Use Cases
To date, distributed stores have seen a few use cases implemented within finance and a few important pushes by influencers. The primary use cases implemented thus far of interest to banking fall into two categories.  1) customer convenience and 2) personalized customer experience.  Both these categories of use case are enabled by the concept of interoperability.  [explain concept]. Presumably this same interoperability is what would enable what [Berners-Lee described as ](https://thenewstack.io/sir-tim-berners-lees-solid-protocol-puts-data-back-in-the-control-of-the-end-user/)
'a more complex financial profile that could help bank the unbanked.' 
 
The solid project has been used by groups such as the NatWest Bank and the BBC to provide more personalized experiences for customers [(source: Inrupt.com](https://inrupt.com/solid-enterprise-natwest-bbc). The NatWest integration in particular
> .. demonstrated how a customer could use Solid-powered applications from  different organizations to change their name or register a business. Since all the apps write data to the user’s NatWest Pod, changes only need to be made once. And they could even receive new offers based on their newly recorded “life moment”.

[An example of using decentralized customer information to improve customer convenience](https://hbr.org/2020/01/why-companies-make-it-so-hard-for-users-to-control-their-data) comes from India.
> 'Many Indian banks are taking this forward by preparing to give consumers access to their financial data so that they can directly share it to apply for credit, investment products, or insurance, bypassing the credit ratings agencies as gatekeepers or the lack of credit histories as barriers. This initiative will use third-party mediators and is backed by the country’s central bank.'

The examples of implemented use cases for personal data stores is relatively thin.  A great deal of the action is in the specifications.  For verified credentials, which in some sense is a subset of decentralized personal data storage, [the Verifiable Credentials W3C specifications](https://www.w3.org/TR/vc-use-cases/#finance) point to several interesting use cases particularly around reuse of 'Know Your Customer' checks and money transfer verifications.

https://www.w3.org/TR/vc-data-model/#data-schemas
https://solid.github.io/specification/


## Disruptor or Opportunity/Conclusion
- esp 
  - Are there benefits to customers if banks integrate with these personal data stores?  Are there benefits to banks themselves if they undertake these integrations?  How likely are these data stores to reach a tipping point of adoption? 
* effort into the specifications.  Solid Technical Reports not yet published
* definitely in the 'innovators' part of the adoption curve.  
  * not many contributors in comparison to other projects
  * FWIW, W3C specifications have been published.
* not a disruptor in the 1-5 year term
* potential opportunities for institutions with the right sort of customer base
* difference between personal data stores and verified credentials
* push for features of verified credentials from world economic forum
* W3C specification published
* Not fully supported ( EFF, ACLU, Mozilla disagreements as of 2020)
* ecosystem to continue developing
* not ready for wide scale adoption, but worth familiarizing, investigating use cases, and monitoring implementations.  Framework and specification may change
  * currently the proof methods appear experimental
Public development on these distributed, decentralized data stores is anemic at best.  Looking at a few companies and projects that are promoting this approach, they are all smaller companies. The only open source project available is Solid.  The solid server project has 70 github contributors.  Other projects which are listed as in the early adopter or early majority phase on ThoughtWorks or The Cloud Native Computing Foundataion have between 100 and 300 contributors in a sample of about 20 projects.  As far as the adoption curve is concerned, these projects are in the long tail of the innovator's part of the curve.  ![the technology adoption curve](img/innovation-curve-chasm.png)In it's current form it's unlikely that decentralized identity or content management will be an industry disruptor for the financial community.  However, given some of the use cases that have already been onboarded to the solid platform there may be some opportunities for financial institutions or banks to better serve or expand their market.  


### Verifiable Credentials
- [w3c specification/usecases](https://www.w3.org/TR/vc-use-cases/#example-verifiable-credentials)
  - "Now that the account is open, Jane is issued a digitally-signed credential for her checking account at MidBank. This credential verifies that Jane has   an account at MidBank and has access to her associated checking account. Since MidBank (and all banks in Finland) are required to perform "Know Your Customer" checks on accounts, this credential can also be used as sufficient verification by other financial institutions. This can help Jane assure destination banks that she is verified, thereby allaying concerns about misdirected transactions and money laundering."
  - recommended by W3C 2019/11/19
- [implementations](https://w3c.github.io/vc-test-suite/implementations/)
- [azure implementation](https://docs.microsoft.com/en-us/azure/active-directory/verifiable-credentials/decentralized-identifier-overview)
  - "the following standards have been implemented in our services.

    W3C Decentralized Identifiers
    W3C Verifiable Credentials
    DIF Sidetree
    DIF Well Known DID Configuration
    DIF DID-SIOP
    DIF Presentation Exchange"
- [normative references](https://w3c-ccg.github.io/vc-extension-registry/#normative-references)
  - proof and status methods are currently experimental
- [owasp.org](https://owasp.org/www-event-2020-NewZealandDay/assets/presentations/Chadwick--W3C_Verifiable_Credentials--20200221.pdf)
  - discussion slides from the world economic forum
  - quote from davos world economic forum
    - "User empowerment through user control of what data they
share, improving their experiences across their digital context"
- [rsa labs](https://www.rsa.com/en-us/blog/2021-07/verifiable-credentials-the-key-to-trust-on-the-next-web)
  - rsa labs is providing verifiable credentials w/ the UK National Health Service
- [mozilla](https://blog.mozilla.org/netpolicy/2020/08/06/by-embracing-blockchain-a-california-bill-takes-the-wrong-step-forward/)
  - mozilla 2020/8/6 is against some California legislation to pilot a VC system for medial status.  EFF and ACLU of California are also against the bill.
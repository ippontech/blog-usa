---
authors:
- Chris Lumpkin
tags:
- digital-identity
- blockchain
- self-sovereignty
date: 
title: "Hacking the Gordian Knot of Digital Identity"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/03/identity-main.jpg
---

In this post I will discuss some of the concepts and problems surrounding digital identity. I will then introduce the concept of federated decentralized identity management and explain its importance in the context of digital commerce and governance. For a more technical look at decentralized identity management, see [Tyler's post on integrating Civic Secure ID with a static web application](https://blog.ippon.tech/integrating-civic-into-a-static-serverless-website-part-1-of-2/).

# Age of Innocence

The Internet 1.0 was not built with security baked-in. In the Bad Old Days, we built security for systems by storing user credentials in databases, sometimes in cleartext. The standard was upgraded to storing passwords encrypted, or preferably _hashed_, using an [irreversible encryption algorithm](https://en.wikipedia.org/wiki/Hash_function) that guarantees the same source string will always produce the same result (without exposing passwords, even to system admins).

Finally, we have come to the age of [federated identity](https://medium.com/@robert.broeckelmann/authentication-vs-federation-vs-sso-9586b06b1380): trusted third parties like Google, Facebook, and Twitter provide standards-based APIs so that their users can use their platform to verify their identities in other applications without reinventing the wheel.

Using federated identity solutions, we have some guarantees that our digital identities are implemented with information security best practices, and they have excellent uptime on their sites. However, there are some problems with the corporate-backed federated identity model.

# Identity Crisis

For most of the Internet revolution, users haven't cared much about data privacy or security, enduring requirements for increased password strength or multi-factor authentication with eye-rolls and exchanging XKCD comics.

![Password strength](https://imgs.xkcd.com/comics/password_strength.png)

Enter [Cambridge Analytica](https://en.wikipedia.org/wiki/Cambridge_Analytica).

According to [Pew Research studies](https://www.pewresearch.org/fact-tank/2018/03/27/americans-complicated-feelings-about-social-media-in-an-era-of-privacy-concerns/):

> Overall, a 2014 survey found that 91% of Americans “agree” or “strongly agree” that people have lost control over how personal information is collected and used by all kinds of entities. Some 80% of social media users said they were concerned about advertisers and businesses accessing the data they share on social media platforms, and 64% said the government should do more to regulate advertisers.

People are still clicking 'I Agree' on those Terms and Conditions, though perhaps with an increased awareness of the problems with centralized data. We should feel a similar trepidation authenticating to sites that store our personal data using the same providers who have been so flagrant with personal information in the past.

_How did all that data get there, anyway?_

We are pouring an unprecedented amount of time into social media platforms. I was fortunate to see a presentation by Tristan Harris, design ethicist and former Googler, describing the advanced engineering of persuasion technologies to "trap" us in front of their content. If you're curious about how this works, especially you parents out there, [Tristan's TED talk](https://www.ted.com/talks/tristan_harris_the_manipulative_tricks_tech_companies_use_to_capture_your_attention) is well worth 17 minutes of your time.

Tristan characterizes the operating environment of competing digital advertisers as a "race to the bottom of the brain stem". His apt dissection of the ad tech industry begs the question: do we trust these companies to define our identity?

# Self-Sovereign Identity

So far we've discussed the importance of freeing our identities from Big Bad Tech. There is a rising tide of decentralist thinkers (_raises hand_) who believe there is also a case for separation of identity from nation-states, or [self-sovereign identity](https://bitsonblocks.net/2017/05/17/gentle-introduction-self-sovereign-identity/).

![Self-Sovereignty](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/03/identity-self-sovereignty.jpg)

I think the simplest articulation of self-sovereignty is that your home country may rightfully control your rights as a _citizen_, but every human has the right to control their own proof of _identity_. Let's unpack that a bit and look at some examples to illustrate the differences.

Just like an online retailer or social media platform, your local, state, and national government probably has some stake in data about you that results from civic interaction (or "imperial entanglements"). Examples are the date of your birth or first entry into the country, any visas or federal licenses that you may have been granted. These agencies are the source of truth for these facts about you.

Other agencies or nations may have similar claims to facts about you. Maybe you are a dual passport holder, or one of the growing class of [digital nomads](https://www.reddit.com/r/digitalnomad/). Some countries may have requirements around your status in your home country, but generally they just want to know _who you are_.

Case in point: In a world with self-sovereign identity, Edward Snowden may have been permitted to reach his originally intended destination in Latin America. Instead, the government whose criminal actions he exposed was able to revoke his passport, his only internationally recognized identity. It's almost as if he ceased to be a person.

## Use Cases for Self-Sovereign Digital ID

The following use cases are inspired by conversations I've had, mostly with people working in healthcare, public health, and nonprofits generally interested in the public good. There is immense stakeholder interest in a digital federated identity, dependent on no one company or organization, that can support the following:

 * Healthcare aid workers in refugee camps can access a family's vaccination records using a parent's fingerprint
 * Healthcare providers in general can access a patient's medical records, as shared by the client, and verification of payment for services, without having to know their name or address
 * Everyone can access a global directory of healthcare providers, search by geography, specialty, and client ratings, and make appointments
 * Digital ID holders are eligible to participate in a global emergency notification service, like 911 in the US, using a smartphone from any mobile or wifi network
 * Citizens can cast votes on proposed legislation or platform issues using digital ID
 * Digital ID holders can share their personal information e.g. contact information, billing information, KYC data for financial transactions, locked under a single secure identity

That's right folks, one day you may be able to move and _only change your address in one place_. All it takes is a little elbow grease and `trust in numbers`.

# Hacking the Gordian Knot

![Flying Spaghetti Monster](https://i.pinimg.com/originals/69/09/ef/6909ef0d0bd64ed91aa17a08363edde1.png)

There is no single technical panacea for self-sovereign identity. You can't just "put it on the blockchain". As Vitalik Buterin of [Ethereum](https://www.ethereum.org/) has pointed out, blockchains only care about accounts, and someone with sufficient funds can create thousands of "[sock puppets](https://www.coindesk.com/understanding-the-radicalxchange-movement-and-its-cypherpunk-appeal)" to overpower democratic votes. The solutions require a bit more nuance.

The good news is that there are several digital identity solutions, with varying degrees of decentralization and organizational transparency, which may fit our use cases. There is also a [decentralized digital identity standard](http://identity.foundation/) emerging which will allow for interoperability and portability.

I hope you have a better grasp of some reasons you may want to consider decentralized federated identity solutions, and a basic understanding of the tenets of self-sovereign identity. In my next post I will dig in to the emerging DID standards and the platforms providing federated authentication.

# Resources

 * [Tyler's post on integrating Civic Secure ID with a static web application](https://blog.ippon.tech/integrating-civic-into-a-static-serverless-website-part-1-of-2/)
 * [Hash function on Wikipedia](https://en.wikipedia.org/wiki/Hash_function)
 * [Authentication versus Federation on Medium](https://medium.com/@robert.broeckelmann/authentication-vs-federation-vs-sso-9586b06b1380)
 * [Cambridge Analytica on Wikipedia](https://en.wikipedia.org/wiki/Cambridge_Analytica)
 * [Pew Research](https://www.pewresearch.org/fact-tank/2018/03/27/americans-complicated-feelings-about-social-media-in-an-era-of-privacy-concerns/)
 * [Tristan's TED talk](https://www.ted.com/talks/tristan_harris_the_manipulative_tricks_tech_companies_use_to_capture_your_attention)
 * [Self-sovereign identity at Bits on Blocks blog](https://bitsonblocks.net/2017/05/17/gentle-introduction-self-sovereign-identity/)
 * [Vitalik on 'sock puppets'](https://www.coindesk.com/understanding-the-radicalxchange-movement-and-its-cypherpunk-appeal)
 * [Decentralized Identity Foundation](http://identity.foundation/)

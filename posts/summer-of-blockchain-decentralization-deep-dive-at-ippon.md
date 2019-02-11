---
authors:
- Chris Lumpkin
tags:
- Blockchain
- JHipster
date: 2017-10-17T13:05:53.000Z
title: "Summer of Blockchain: Decentralization Deep Dive at Ippon"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/10/blockchain-decentralize.jpg
---

# Inception
You may have heard some buzz lately about blockchain, cryptocurrencies, ICOs, and the growing ecosystem of decentralized technologies like IPFS and IoT. Our Ippon USA team took a deep dive into blockchain over the summer months of 2017 in what we now see as the birth of a new strategic and technical focus area of our business. This is the origin story of #blockchain at #ippon.

I first heard of Bitcoin and blockchain technology shortly after Bitcoin launched in 2012. I wasn’t particularly interested in investing at the time; wasn’t it just a risky digital pyramid scheme? In more recent years I began reading about Ethereum, and the trend of decentralization earned my fascination. Then, shortly after I joined Ippon USA in November 2016, I heard our co-founder [Romain Lhéritier](https://www.linkedin.com/in/romainlheritier/) remarking on the uptick of interest from clients and prospects: “Blockchain is so hot right now!”.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/10/romain-1.JPG)
*Our fearless leader*

One of our core values at Ippon USA is passion, and the convergence of the political, economic, and social implications of blockchain technology with opportunities to help our clients enter into this fascinating and complex new ecosystem quickly ignited the passion and imagination of our engineers. Our management team agreed with no hesitation to allow me to direct a team of our summer interns and any consultants between projects (aka “benchies”) to explore use cases of blockchain. I assembled the team and our adventure began in May 2017 with 2 full stack software engineers, 3 college interns, a scrum master, and myself as their product owner and architect. We christened this new project Unchained.

# “Dogfooding”
Ippon is an agile engineering shop, from discovery to delivery, and we eat our own dog food! Our project would be reliant on the ever-changing bench resources at a successful consulting firm, so we should deliver working software as quickly as possible and deliver valuable features within each two week sprint. We began by assembling some key team members to start defining our high level goals, grooming our backlog, and digging into the architectural challenges of blockchain technology and infrastructure.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/10/griffin.jpg)
*Unofficial Unchained mascot Griffin*

We brainstormed some simple, achievable use cases gleaned from reading about areas of interest or disruption from blockchain. Our team settled on a prototype real estate ledger using Hyperledger Fabric. Software Engineer Greg Kontos set up the Fabric network and began learning how to customize the chaincode. Greg has been coding since he was in middle school and he had experience in golang, which was very helpful. Fabric 1.0 was released in July after our project was already underway, and the documentation was not always up to date.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/10/greeeeg.jpg)
*Greg is right at home with blockchain*

Our other Software Engineer on the team, Kyle Crane, focused on generating and customizing the full stack JHipster application that would serve as the user interface to the chaincode using Fabric’s Java SDK. We would have only 9 weeks with our summer interns, and we wanted them to hit the ground running to maximize their learning experience and contributions. Kyle and Greg wrapped up discovery and preparatory work before our three interns arrived.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/10/kyle.jpg)
*Kyle at Ippon HQ*

Rodrigo, Julian, and Andrea arrived in June, and the team went to work defining and organizing backlog tasks and planning their first sprint. They began giving weekly demos by the end of their first 2 week sprint, implementing features and making iterative improvements to the applications.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/10/interns.jpg)
*Ippon USA summer intern crew*

They completed the real estate application within 2 sprints while learning their way around Java, Spring Boot, Angular, golang, and transaction ledger concepts. The application allows an admin to record real estate transactions between participants in the Fabric ledger, including the concept of partial ownership. Metadata about properties and owners is stored in MariaDB.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/10/property-history.png)
*Property history*

As the real estate demo was wrapping up, Kyle and Greg were getting busy with other projects, so the interns demonstrated their knowledge by creating a new application. They used the same stack of JHipster and Fabric to create an e-voting application. They were interested to see how a distributed ledger could make voting more secure. They had a working demo up in about a week and had begun adding features when suddenly-

# PLOT TWIST!
One of our consultants was approached by a friend running a small blockchain mining startup, QubeAI. They had an idea for soliciting investments in their business, allowing them to go to market more quickly. They wanted an application which would allow them to open up rounds of investment, inviting investors to commit funds, and then automate dividend distribution by leveraging smart contracts. We invited QubeAI to our office to begin brainstorming.

# Public or Private?
Our interns had already begun to grasp many of the nuances of security in the blockchain world, and they were asking all the right questions. Initially we agreed to continue using Fabric as our ledger, since we had a mere few weeks left of their internship and we were concerned there wasn’t enough time to tackle something new. However the team quickly realized that there was little value in using a permissioned blockchain for a public-facing application. Why would an investor have any greater confidence in smart contracts that they could never see nor validate? This created a black box just as opaque as any centrally hosted database. The team decided the best solution was to use a public blockchain, and we settled on Ethereum.

Largely on their own now, our interns put together their prototype dividends application in just 3 weeks! They learned to bootstrap an Ethereum test client, wrote smart contracts for investment and dividend distribution, and used web3j to integrate their JHipster application with the Ethereum client.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/10/round-of-investments.png)
*Round of investments*

Admins of the application can configure, open, edit, and track rounds of investment and initiate dividend distributions. Investors can join a round of investment and transfer funds from their Ethereum wallet. One gotcha about using an immutable ledger is, well, it’s immutable. The team chose to store investment amounts in MariaDB until the close of that investment round, so investors can edit their contribution amounts right up until close.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/10/dividends.png)
*Dividends*

The team presented the prototype to our friends at QubeAI in early August. Our “client” was impressed with how quickly our young engineers were able to code the functionality and UI for them to set up, edit, and track rounds of investment while investors could allocate funds and look at graphs of their investments.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/10/unchained-demo.jpg)
*Unchained demo*

# Lessons Learned

* Lesson 1: **JHipster is AWESOME**
...but you probably already knew that. Ippon sponsors this open source project for the good of the community, but also for our own nefarious purposes: we are a growing software engineering consultancy, and we frequently have to turn around proof-of-concept applications for clients on a few days’ notice. We hire the best engineers, and our clients expect them to be productive on Day One. If our Unchained team had to manually assemble all the plumbing of a working full stack web application on top of learning the ins-and-outs of Fabric and Ethereum, we could never have accomplished so much with mostly junior engineers and a rotating cast of characters.

* **Lesson 2: Welcome to the bleeding edge**
Blockchain tech is new and rapidly evolving. Software outpaces documentation, and training can quickly become obsolete. Cryptocurrencies can flash crash, or their exchanges can be shut down by authorities who are struggling to cope with the realities of decentralization and anonymity. Welcome to the jungle.

* **Lesson 3: Public versus private**
This is THE foundational decision in blockchain architecture. Does your use case require participants to be trusted partners, such as banks clearing transactions? Permissioned ledgers like Hyperledger Fabric can provide a distributed, though centralized, trusted network among peers. If, on the other hand, you want to leverage the network effect to crowdfund a new venture, you will benefit from the rich ecosystem, and transparency, of the Ethereum network.

* **Lesson 4: It’s not all about the tech**
By far the most difficult aspect of Ippon’s entrance into blockchain tech was figuring out what to build. While we learned a lot from our initial endeavors, it was a relief when QubeAI stepped forward with a real-life use case. The hardest thing about blockchain is knowing what to do with it, what kinds of problems it can solve. Ippon already specializes in Big Data, piping huge streams of data to distributed data stores and querying them efficiently to analyze statistical trends. I have come to think of blockchain as Little Data; in this space, details are crucial.
In order to fully comprehend the implications of blockchain, I started attending meetups, following the writings of deep thinkers like Naval Ravikant, and began to invest in cryptocurrencies (there are over 2,000 at the time of this writing). Our team is experimenting with cryptocurrency mining and exploring novel solutions to fundamental challenges with security, scalability, and liquidity of cryptoassets.

Thanks for coming on this adventure with us! Stay tuned as we drill down into some fascinating use cases and explore solutions to the challenges of dynamic, disruptive decentralization.

# Projects reference:

* Unchained real estate ledger
 * [Main project](https://github.com/misterzero/unchained)
 * [Chain code dependency](https://github.com/misterzero/chain-code)
* [QubeAI dividends proof-of-concept](https://github.com/misterzero/unchained-dividends)

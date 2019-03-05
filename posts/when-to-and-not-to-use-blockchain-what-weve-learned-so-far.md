---
authors:
- Rachel Gaddis
tags:
- Blockchain
date: 2017-11-06T18:36:33.000Z
title: "When to Use Blockchain and When Not to - What We've Learned So Far"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/10/Blockchain-Use-Cases-blog-header.png
---

by [Rachel Gaddis](http://blog.ippon.tech/author/rachel/) and [Jake Henningsgaard](http://blog.ippon.tech/author/jhenningsgaard/)

Blockchain is the new hotness, no doubt about it, and when you're in an industry like ours (uh, for those of you just tuning in that would be the software industry), that means it's time to apply the shiny new tech to everything.

Why? Because it's the new shiny! (Duh, I thought you were tuning in.)
![Excited looking kids at a laptop](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/10/kids-at-laptop.jpg)
*The new shiny is more fun than whatever is old and dull.*

There are two primary benefits that are causing blockchain to skyrocket in popularity:

* **Trust** - Blockchain provides a way to do business without having to trust a middleman (be that a person or an organization). Some of the ways it does that are:
    * *Transparency* - Each entry is recorded for all in the blockchain to see. This adds to the environment of trust, where smart contracts are stored and run. Smart contracts are self executing and implement a high level of precision.
    * *Immutability* - Once a block is written into the public blockchain, it cannot be altered. This has been tested and proven out, and provides powerful protection of existing blockchains, which in turn enhances trust.  ![quote that says A family is a gift that lasts forever](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/10/quote-1460237_960_720.jpg)
*So does a blockchain.*
    * *Validation of transactions* - A transaction, in blockchain, is really any information transfer. These transactions are then added to a block until that block is full, and once the block is full of transactions, the block is mined and validated. This process is known as the blockchain consensus protocol. There are a couple in use today, Proof of Work being the most common and well known. And, as I said earlier, once added to the public blockchain, that block cannot be modified.
* **Decentralization** - The blocks are distributed across hundreds or thousands of computers. This provides benefits in terms of reducing risk, as there's no longer a single point for failure to occur, and it's not possible for the blockchain to be controlled by a single entity. For example, anyone trying to tamper with the blockchain would have to do it for thousands or tens of thousands of machines simultaneously. Sounds harder than hacking a single source? That's because it is.

![gif showing how an internet business before incorporating blockchain technology and then showing the transaction path after blockchain is incorporated, where blockchain helps to decentralize and eliminate any middleman](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/10/pic.gif)

But it turns out that some scenarios may be better suited for blockchain than others. We're starting to assemble a decision tree of sorts that is helping us to determine why you might want to use blockchain (and why, other times, you might not).

Here are some questions we're asking ourselves about potential blockchain projects.

# Does this have to do with little data? (as opposed to Big Data)

When we say "little" data, we mean situations where:

* You want to see each individual piece of data and all of its details.
* The details of each piece of data matter a lot.

Some examples of "little" data might be:

* Ledgers, where you might want a close and careful accounting.
* IoT, where you might be tracking the items themselves and their journey from one place to another.
* Commerce, where you might be tracking shopping items to optimize for future sales.
* A clothing store that tracks inventory with RFID-embedded fabric would like to recover the store a dress was sold from to help resolve a quality control issue.

With "big" data, on the other hand, you would find yourself collecting large amounts of data so that you can aggregate the data, look at trends, and basically look at the data as a group.

# Are there auditors involved?
The presence of financial auditors is a signal that the details are likely to matter a lot. And in those cases, the transparency of blockchain - where the detailed entries are available immediately in a distributed fashion - can be a perfect fit for this type of situation.

![calculator and notepaper](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/10/accountant-accounting-adviser-advisor-159804.jpeg)

Take this example of [blockchain being used to track tunafish](https://www.provenance.org/tracking-tuna-on-the-blockchain). Through the transparency of the blockchain, tunafish chain of custody can easily be audited to help combat inhumane and environmentally unfriendly fishing practices.

# Is trust an issue?

If so, blockchain may be an efficient way to eliminate any unknown or untrusted middleman while still providing transparency for all parties, validation of any information that is being passed, and immutability of the blockchain once it is created.

# Are there transactions involved?

Don't get too distracted by the word "transaction" - in the computer world, and *especially* in the blockchain world, a transaction can be any kind of transfer of information.

Here are some examples of transactions.

* With physical items, you might track transactions like item received, item shipped, item checked out, or item location
* With some types of documents (such as estate plans or certain types of purchase agreements), you might write smart contracts into the blockchain to trigger payments or other to happen at certain points or under certain conditions.

So, to summarize:

<table style="border: 1px solid black">
  <col width="33%">
  <tr>
    <th style="text-align:center; padding: 10px; border: 1px solid black; background-color:gray">Blockchain may be a good fit for these…</th>
    <th style="text-align:center; padding: 10px; border: 1px solid black; background-color:gray">But not for these…</th>
  </tr>
  <tr>
    <td style="padding: 10px; border: 1px solid black">✅ Little data</td>
    <td style="padding: 10px; border: 1px solid black">✘ Big data</td>
  </tr>
  <tr>
    <td style="padding: 10px; border: 1px solid black">✅ Financial auditors</td>
    <td style="padding: 10px; border: 1px solid black">✘ Financial auditors not involved</td>
  </tr>
  <tr>
    <td style="padding: 10px; border: 1px solid black">✅ Trust is an issue</td>
    <td style="padding: 10px; border: 1px solid black">✘ Trust is not an issue</td>
  </tr>
  <tr>
    <td style="padding: 10px; border: 1px solid black">✅ Details matter a lot</td>
    <td style="padding: 10px; border: 1px solid black">✘ Details don't matter as much</td>
  </tr>
</table>

Since we're finding it useful to think through when to use blockchain as well as when it might not be the best approach, we thought we'd offer this thinking up to the community and see if anyone else finds this useful as well. Please let us know your thoughts or reach out to us on LinkedIn to share thoughts, comments, or additions.

# References & Sources

* [What is Blockchain?](https://blockgeeks.com/guides/what-is-blockchain-technology/)
* [What are smart contracts?](http://solidity.readthedocs.io/en/develop/introduction-to-smart-contracts.html)
* [Blockchain & Smart Contracts](https://medium.com/startup-grind/gentle-intro-to-blockchain-and-smart-contracts-part-1-3328afca62ab)

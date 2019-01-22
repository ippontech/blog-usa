---
authors:
- Chris Lumpkin
tags:
- Blockchain
- Ethereum
- Non-Fungible Tokens
- ERC-721
date: 
title: "Non-Fungible Tokens on the Ethereum Blockchain"
image: https://raw.githubusercontent.com/misterzero/blog-usa/blockchain-nft-blog/images/2019/01/non-fungible-tokens-01.jpg
---

In this post I will explain a bit about Non-Fungible Tokens (NFTs) and walk through an implementation of the ERC-721 standard in Ethereum. If you're looking for a non-technical intro to blockchain, check out my friend [Johnny Dollar's explanation](https://www.youtube.com/watch?v=EyytRm0j2EY), and follow up with a more [in-depth explanation from Andreas Antonopoulos](https://www.youtube.com/watch?v=eMoc4zU39hM).

# What are Tokens?
Most people think of blockchains as cryptocurrencies, but open distributed ledgers are a great way to track many kinds of assets. Think of tokens as poker chips that can be traded in a marketplace without hauling the physical assets around. When you hold digital tokens, you have cryptographic security and 24/7 access to exchanges to buy/sell. Unlike poker chips, blockchain tokens do not require you to trust "the house". `Vires in numeris`!

## Tokens on the Ethereum Blockchain
When discussing token standards on the Ethereum network, usually the first thing that comes to mind is [ERC-20](https://en.wikipedia.org/wiki/ERC-20), the fungible token standard. ERC-20 is _fungible_ because every token is the same as every other token. This is why so many startups were using ERC-20 tokens to kick start their projects during the ICO craze; they were (mostly) tokenizing their business ventures, products, or services and selling pieces of the value.

_Side note:_ "Fungibility" is often touted as a desirable trait in assets, but it's more complicated than it seems to create truly fungible assets. Gold is one of the best examples, because it can be smelted and reshaped with only weight and purity as variables, but what about modern money? Are all US Dollar bills the same? Almost; there are those pesky serial numbers which can be used to track bad actors like bank robbers. Are Bitcoins fungible? Same problem as USD, each and every Bitcoin transaction has a unique ID and can be traced back to its origin. If you require near-perfect fungibility, [use Monero](https://www.getmonero.org/).

The advent of ERC-20 opened a Pandora's Box of differing requirements for tokens. Check Ethereum's site for an [exhaustive list](https://eips.ethereum.org/erc). For this post, I'm focusing on ERC-721: non-fungible tokens.

## Why Non-Fungible Tokens?
There are many assets one could "digitize", or at least create a digital tradable proxy, which are unique in some respect. Event tickets often have a particular seat associated with each ticket; real estate parcels have unique location and characteristics; virtual items in online games are often unique (sometimes _terminally_ unique); art and music are particularly good use cases, because uniqueness is part of their intrinsic value.

When you purchase a painting or sculpture, there are usually documents or other artifacts to assure you of their provenance and authenticity. ERC-721 attempts to provide these assurances by associating the digital token with digital metadata. Let's dig in and see how that works under the covers.

# ERC-721

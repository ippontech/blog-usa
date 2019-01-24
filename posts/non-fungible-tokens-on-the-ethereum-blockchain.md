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
Since Ethereum is a smart contract platform, token implementations are interfaces which define functions that must be implemented. Like ERC-20, your implementation of these functions defines behavior of tokens minted from the contract during transactions.

## The Interface
```solidity
        interface ERC721 /* is ERC165 */ {

            event Transfer(address indexed _from, address indexed _to, uint256 indexed _tokenId);

            event Approval(address indexed _owner, address indexed _approved, uint256 indexed _tokenId);

            event ApprovalForAll(address indexed _owner, address indexed _operator, bool _approved);

            function balanceOf(address _owner) external view returns (uint256);

            function ownerOf(uint256 _tokenId) external view returns (address);

            function safeTransferFrom(address _from, address _to, uint256 _tokenId, bytes data) external payable;

            function safeTransferFrom(address _from, address _to, uint256 _tokenId) external payable;

            function transferFrom(address _from, address _to, uint256 _tokenId) external payable;

            function approve(address _approved, uint256 _tokenId) external payable;

            function setApprovalForAll(address _operator, bool _approved) external;

            function getApproved(uint256 _tokenId) external view returns (address);

            function isApprovedForAll(address _owner, address _operator) external view returns (bool);
        }

        interface ERC165 {
            function supportsInterface(bytes4 interfaceID) external view returns (bool);
        }

        interface ERC721TokenReceiver {
            function onERC721Received(address _operator, address _from, uint256 _tokenId, bytes _data) external returns(bytes4);
         }
```

## The Use Case
As is often the case in emerging technologies, the most prevalent use cases for non-fungible tokens emerging to date are in art and gaming. I was approached by my pal [Johnny Dollar](https://johnnydollar.biz/) for help implementing and deploying an NFT contract. Johnny wanted to provide digital artists with an open source solution for minting NFTs for digital art. Thus the [Artists' Liberation Front](https://the-alf.com/) was born!

![ALF](https://raw.githubusercontent.com/misterzero/blog-usa/blockchain-nft-blog/images/2019/01/non-fungible-tokens-02.jpg)

## Implementing ERC-721

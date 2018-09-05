---
authors:
- Louis Fournier
- Hugo de Almeida Rodrigues
tags:
- Blockchain
- Ethereum
- Dai
- Stablecoin
- Solidity
- Truffle
- Remix
- ZeppelinOS
- web3
- Metamask
- geth
date: 
title: "Ethereum Development Tips"
image: "/images/2018/09/blockchain-interns-01.png"
---

When we arrived at Ippon USA for our summer internship, we had little knowledge about blockchains. The goal of this article is to give you the basic tools one can use to build a decentralized application, and some tips about the language we used: Solidity. Of course our list is not exhaustive, and is just here to offer you a peek at Ethereum development.

# Introduction to Blockchains and Ethereum

Let’s start with the Wikipedia definition:
>A blockchain, originally <b>block chain</b>, is a growing list of records, called blocks, which are linked using cryptography. Blockchains which are readable by the public are widely used by cryptocurrencies. Private blockchains have been proposed for business use.

To put it in layman’s terms, a blockchain is simply a transaction record that is decentralized. It means anybody can store and run a copy of the blockchain (becoming a "node”) and therefore read it.

There is not a single blockchain, but different ones for different purposes. The Bitcoin blockchain is the most famous one of all, but there is also the widely popular Ethereum blockchain which we use, and many others. These are public, but you can as easily create a private blockchain, which won’t be available to everyone.

As on a classic ledger, you can only add data to a blockchain. To add new pages to the ledger or new blocks to a blockchain "miners” have to solve complicated and costly algorithm problems. Your transaction will be validated after a few blocks have been mined. This is why transactions cost a fee to reward miners.

Finally, security is a major asset of blockchains, with the help of high-end cryptography and the distributed network effect.

![Ethereum](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/09/blockchain-interns-02.png)

During our internship we worked on the Ethereum blockchain using the Ethereum environment. Proposed by Vitalik Buterin in 2013 (at 19 years old!), it is an open-source public and blockchain-based distributed computing platform and operating system featuring smart contract (scripting) functionality.

What you need to understand is that like the Bitcoin Blockchain, it has a cryptocurrency: Ether (ETH) and you can handle transactions with it. However it is also possible to deploy "smart contracts” which enable you to build decentralized applications. These contracts can be any kind of script which can handle storage, logic, transactions and call from other contracts.

A contract can be a decentralized exchange for instance (Etherdelta), a casino game, or just a token. They are run on the Ethereum Virtual Machine, and are written in a Turing complete language. Naturally, it allows a new range of possibilities for blockchain development. Before being compiled to bytecode for the EVM.

Smart contracts are developed in high-level languages created for Ethereum, the most common one being Solidity. Although bearing similarities to Java and C, Solidity is its own peculiar identity, and is a fascinating development environment.

![Ethereum ecosystem](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/09/blockchain-interns-03.png)
You may have encountered a lot of applications and "tokens” (cryptocurrencies running on Ethereum): OmiseGO, BNB (Binance Token), CryptoKitties are some examples. The tokens of the Ethereum Blockchain can be traded and bought like other currencies, however they are different from Ether which is the only way to pay "gas fees” and reward miners.

Yes, we are still talking about blockchain development, gas is actually a fee you have to pay to miners for each transaction. A transaction can be anything, you can call a contract, send any ETH to a wallet, trade tokens... We’ll be expanding on this subject later.

If you have already used Coinbase or similar exchanges, you may know that your cryptos are stored on a wallet, represented by a public key or address and secured by a private key. To be clear, a user is represented by an address (his public key), and the same goes for a Smart Contract or even a Transaction hash, the only difference is that users have private keys which contracts do not have. For instance, here is the address of the deployed DAI contract:<br/>
0x89d24A6b4CcB1B6fAA2625fE562bDD9a23260359<br/>
But what is DAI? Don’t worry, this is just a teaser...

# The Ethereum developer toolkit

![Testnets](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/09/blockchain-interns-04.png)

This all seems really interesting, and you can’t wait to dive in. But first let’s give you a tour of the different tools and techniques to develop on Ethereum. We may have lied a bit about the fact that there is "a Ethereum blockchain”. There are actually several! The "mainnet”, is the only one that really matters, and where all the "real” money circulates.

However, you cannot develop a smart contract and then just launch it on the mainnet. Well you could, but it is not free, and a contract on the mainnet will be there until Doomsday, its code forever unalterable. Developers use "testnets” which grant you free ether to test and faster mining time. Here is a list of tools that can help you get connected and develop:

![Truffle framework](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/09/blockchain-interns-05.png)

1. [Truffle](https://truffleframework.com): A popular and complete development framework for Ethereum, enabling you for example to run automated tests of your contracts, compile, migrate and deploy them. It also has a lot of promising features we did not use in the first phase of the project.

![Ganache](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/09/blockchain-interns-06.png)

2. [Ganache](https://truffleframework.com/ganache): A tool of the Truffle development suite that enables you to simulate a local ethereum blockchain on your computer. It generates ten different user wallets with public and private keys so you can test your smart contract features locally.

![web3js](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/09/blockchain-interns-07.png)

3. [web3.js](https://github.com/ethereum/web3.js): web3.js is the Ethereum JavaScript API. You will use it for the frontend development of your decentralized application, as the way to link your frontend and the blockchain.

![Metamask](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/09/blockchain-interns-08.png)

4. [Metamask](https://metamask.io): Metamask is a Chrome/Firefox plugin using web3.js. It gives a convenient way to connect to the different Ethereum Blockchains (mainnet and testnets). It stores your public and private key and allow you to call and send transaction to smart contracts without running a full ethereum node. It is the most user friendly way to connect to Ethereum.

![geth](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/09/blockchain-interns-09.png)

5. [geth](https://github.com/ethereum/go-ethereum/wiki/geth) and [Mist](https://github.com/ethereum/mist): geth is a tool that enables you to run a local or a public blockchain node. It can be used with Mist, a browser that makes it easy to interact with smart contracts and your accounts.

![Remix](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/09/blockchain-interns-10.png)

6. [Remix](https://remix.ethereum.org): Remix is an online Solidity IDE allowing you to compile your code, and deploy it on the mainnet or testnets with the previous environments: Metamask, Ganache, geth... Afterwards you can directly interact with it with simple buttons, and receive all the information needed about the results and the transactions in the Remix console.

![Dai](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/09/blockchain-interns-11.png)

7. [Dai](https://makerdao.com): Dai is a stablecoin i.e. an ERC20 token which price is stable and equal to the value of the USD. It is the only stablecoin where users do not need to trust a centralized instance. Implementing it on a decentralized application means a user balance value will not fluctuate.

![Etherscan](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/09/blockchain-interns-12.png)

8. [Etherscan](https://etherscan.io/): Etherscan is a "Blockchain explorer”, providing you with all the informations you want about a wallet, contract, or a transaction on the blockchain! You just need to give it an address to look at.

![ZeppelinOS](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/09/blockchain-interns-13.png)

9. [ZeppelinOS](https://zeppelinos.org): We were on the brink of using ZeppelinOS, a development platform with a clear promise : easily upgradable smart contracts with the help of proxy contracts.

![IPFS](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/09/blockchain-interns-14.png)

10.  [IPFS](https://ipfs.io/): A really promising tool we didn’t personally need, the Interplanetary File System (IPFS) is a decentralized storage system. It is vital to build a completely decentralized application as you can not store big amounts of data on a smart contract (approximately $1868 for... only 1 MB!). So if you want to trade a cat picture on the blockchain, you might prefer using only its IPFS hash.

Perfect, you know the different Ethereum blockchains and how to connect yourself and your applications to them. Now you just need to actually code and deploy them. You can code as you would other project using an environment such as Visual Studio Code which has `.sol` extensions (for Solidity, or directly use Remix. It has several major uses. Its compiler will directly say if you smart contract can work and all the possible warnings attached to it. After, you can deploy your contract on the Ethereum net you want using one of the web3.js solutions aforementioned. But it doesn’t stop there! You can directly interact with the deployed contracts with simple buttons to call the different functions, and you will receive all the informations needed about the results or the transactions in the Remix console.

# Solidity tips

![Solidity](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/09/blockchain-interns-15.png)

If you made it this far, you are really interested on how the Ethereum blockchain works, or you want to get to it and develop in Solidity! There is a lot to know about Solidity: although it was made with a really high-level appearance close to Java, the aforementioned specifics of blockchain and smart contracts differentiate it a lot. A lot of trial and error was necessary for us and do not hesitate to ask for help on StackExchange or all your favorite communities, Ethereum development is very active.

The basic idea is that a contract is a complete package, containing state variables, functions, functions modifiers, events, struct types, and enum types. You can also inherit from other contracts. It looks really similar to a Java class, it even also has a constructor if you want to initialize some variables. You can also easily access to the transaction attributes :

- msg.data (bytes): complete calldata
- msg.gas (uint): remaining gas - deprecated in version 0.4.21 and to be replaced by gasleft()
- msg.sender (address): sender of the message (current call)
- msg.sig (bytes4): first four bytes of the calldata (i.e. function identifier)
- msg.value (uint): number of wei sent with the message (1ETH = 10^18 wei)

A function, if it changes any state variable, will cost gas fees to the user or contract calling it.

```solidity
pragma solidity ^0.4.0;  

contract SimpleMarket {  
    function sell() public  onlySeller { // Function  
    // ...  
    }
}
```

You can add a function modifier to a function, which is basically code common to all functions using it. A general case if having a "onlyAdmin” modifier as below. We’ll explain the "require” later!

```solidity
modifier onlySeller() { // Modifier  
    require(msg.sender == seller, "Only seller can call this.");  
    _;
}
```

The last word of the list that should have been alien to you is event, which is something that will be "fired” by the contract when it appears in the code, and will be logged inside the transactions using your contract.

```solidity
contract SimpleMarket {  
    event HighestSell(address seller, uint amount); // Event  

    function sell() public {  
    // ...  
    emit HighestSell(msg.sender, msg.value); // Triggering event  
    }
}
```

The types used in Solidity are pretty much the ones you know and are fairly close to C. String are dynamic arrays of characters, and as such can be tricky to manage. But you may be surprised to not see float, as they do not exist. You will need to be cautious and use multipliers! The types developers seem to affectionate the most are uint256 as they offer the largest numbers (2^256 integers), and addresses, as they are used for most objects such as accounts and contracts.

```solidity
uint256 i = 42;

address x =  0xC02aaA39b223FE8D0A0e5C4F27eAD9083C756Cc2;
```

Another fun thing to manage close to C is of course the storage. Objects do not always behave the way you want them to and you need to remember that everything has a price on a blockchain.

You have to know if the object you are defining in your function needs to be "memory” (stored temporarily) or "storage” (knowing that it will be stored in the blockchain). For example, a dynamic array has to be storage, so you may need to find a way to know the exact number of object it should need.

Other oddities may include structures not being able to be defined recursively, or more importantly, all state variables being initialized to their default value. This means that a boolean will be automatically false, and also that a mapping (Solidity’s widely used equivalent of a hashmap) will be initialized for every key at the byte 0. This means that you cannot iterate on the keys!

```solidity
LinkedList.Data  storage list;

uint256[]  memory _parts;
```

Complex storage structures can be useful, such as the Linked List we needed to implement. By using a mapping from a uint id to a Node structure (with the data we want, the information by which it will be sorted, and the id of the next Node) and keeping the id of the head of the list, we could make an easily sortable list where we can add and delete nodes.

One of the burdens of Solidity is how to code a secure smart contract. Writing a contract that behaves as you expect is quite easy, however making sure it won’t later act strangely is harder. Solidity is a constantly evolving language, but can still fall short of being a secure language for creating decentralized applications.

One of the tools provided by Solidity is for Error handling. You will probably want to have quite a few "Require” functions used throughout your code, as they will "Revert” (which means cancelling the execution of the transaction and revert back any gas used to the sender) when you need them to. They can be used to check users balance before transfers for instance.

Now let’s go over a few problems or considerations you may encounter while coding in Solidity (well, that we have encountered or seen in solidity hacking challenges). A lot of them are really specific to the way blockchain works.

The most common and biggest problem to check out is the classic reentrancy problem. For example, you do not want a user to be able to call two times a function (or two different functions) that will give them tokens before depleting their balance!

```solidity
contract Fund {
    /// Mapping of ether shares of the contract.  
    mapping(address  =>  uint) shares;  
    /// Withdraw your share.  
    function withdraw() public {

        /// Sending the shares beforing depleting!

        if (msg.sender.send(shares[msg.sender]))

        shares[msg.sender] =  0;
    }
}
```

Close to this problem and more specific to blockchain, is the fact that every transaction being public, a malicious user could see a transaction he’s interested in, and before it is mined, do the exact same transaction but with way more gas, making his transaction be processed faster!

If the code of a soon-to-be-deployed app is public, why not do more, and precompute its future address to send ETH to it before, allowing unexpected events? You can precompute a lot actually, and doing random functions in your code that use block number or timestamp (the time a block is mined) is not safe as it is pretty easy to figure out!

```solidity
pragma solidity ^0.4.18;  

contract CoinFlip {
    uint256 public consecutiveWins;
    uint256 lastHash;
    uint256 FACTOR = 57896044618658097711785492504343953926634992332820282019728792003956564819968;

    function CoinFlip() public {
        consecutiveWins = 0;
    }

    function flip(bool _guess) public returns (bool) {
        uint256 blockValue = uint256(block.blockhash(block.number-1));

        if (lastHash == blockValue) {
            revert();
        }

        lastHash = blockValue;
        uint256 coinFlip = blockValue / FACTOR;
        bool side = coinFlip == 1 ? true : false;

        if (side == _guess) {
            consecutiveWins++;
            return  true;
        } else {
            consecutiveWins = 0;
            return  false;
        }
    }
}
```

Be very cautious when you call other contracts, because they could hide data to execute, or use the call stack limit depth or gas to halt the execution where they want it to.

However, do not be fooled thinking the risks can only come from malicious users, since Solidity may behave in ways you would not expect: overflow and underflow errors are legion in Solidity, nonexistent float type means your division will always be rounded...

There is a special function that exists in a contract to address the need for floating point. It does not have a name, and will be executed when no other appropriate function is found. It is called the fallback function, and can be used for quite a few interesting hacking tricks. However as we found out, you can not close it up completely, "just to be sure”, as you need to have a payable fallback function if you want it to receive ETH from another contract!

```solidity
function() public payable { //Fairly open now!

}
```

Security in Solidity, as in every language, is a difficult system of checks and balances between features and protection.

- [Official solidity docs](https://solidity.readthedocs.io/en/v0.4.24/)

A classic game of increasingly difficult solidity hacking challenges. It gets extremely tough!
- [Solidity hacking challenges](https://ethernaut.zeppelin.solutions/)

A coding contest that highlights a lot of mistakes or malicious attacks possible in Solidity. There’s lot of exemples to check out on GitHub!
- [Solidity coding contest winners](https://medium.com/@weka/announcing-the-winners-of-the-first-underhanded-solidity-coding-contest-282563a87079)

A collection of "best practices" in the Ethereum development world by Consensys, a major actor in the Ethereum market.
- [Smart contract best practices](https://consensys.github.io/smart-contract-best-practices/)

# Conclusion

We hope this short introduction to Ethereum development has helped you. We also would like to sincerely thank the entire team of Ippon USA who hosted two French interns in Washington DC: our mentor Chris Lumpkin, our co-workers Dagmawi Mengistu, Priya Rajanna and Matt Reed, who all welcomed us and showed us a priceless glimpse of the American melting pot culture.
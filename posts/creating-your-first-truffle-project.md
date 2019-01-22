---
authors:
- Tyler John Haden
tags:
- ethereum
- blockchain
- ether
date: 
title: Creating your First Truffle Project
image: 
---

An introduction on how to get started with a Truffle-based Solidity project. This article series will not only explain how to get started with [Truffle](https://truffleframework.com/docs/truffle/overview) as an Ethereum smart contract framework, but will also describe boilerplate code that will make your life a ton easier (think linting, local blockchain clients, and improved testing). The goal will to be as transparent as possible about all the tools and configurations that are used, because it usually takes a while to start really getting the whole blockchain thing.

If you would like to explore the source code as a simple boilerplate project, checkout the [github](https://github.com/tylerjohnhaden/__truffle-boilerplate)(blog written as of [commit 0x435f745](https://github.com/tylerjohnhaden/__truffle-boilerplate/tree/435f745a21edfbff6904153b81d65e2d1ee1a6a6)). 

### Assumptions
This tutorial will assume that you have experience with basic bash, node, and npm. 


All of the dependencies we need should be OS agnostic, however if you may need to change the commands to suite your specific shell/OS.

### Dependencies
- node, npm

  We will be installing all of our necessary node modules into the local project. You may also install globally, however you may want to keep it local, because these languages and tools change very fast compared to most tech stacks. You can track the next [ethereum/solidity updates](https://github.com/ethereum/solidity/projects) are scheduled.

# Truffle Initialization
1. Create a new directory to contain your truffle project.

        mkdir MyFirstTruffleProject && cd MyFirstTruffleProject

2. Initialize your project with `package.json` file. You can use other versions of node, but we recommend you use the latest. This article is written with the versions specified below.
        
        {
          "name": "my-first-truffle-project",
          "version": "0.0.1",
          "engines": {
            "node": "11.6.x",
            "npm": "6.5.x"
          }
        }

3. Install the [Truffle module](https://www.npmjs.com/package/truffle)
        
        npm install truffle --save-dev

   - If you want to double check all of our dependencies, run `node_modules/.bin/truffle version` ![Truffle version (pic missing)](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/01/truffle_version.png)
   
4. Time to run Truffle's project init script

        node_modules/.bin/truffle init
        
    ![Truffle init (pic missing)](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/01/truffle_init.png)

Technically you now have a truffle project! It is pretty bare, but lets try to understand what that script did for us.

## Truffle's generated files

When you run `ls -l` you should see:

![Truffle directory (pic missing)](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/01/truffle_directory.png)

Truffle init was responsible for creating three directories (`contracts/`, `migrations/`, and `test/`) along with three files (`Migrations.sol`, `1_initial_migrations.js`, and `truffle-config.js`). Here are their descriptions, but examples on how to add to them, and organize your project will continue below.

- `contracts/` will store all your [Solidity](https://solidity.readthedocs.io/en/v0.5.2/) (.sol files). This is where you will add any smart contracts, libraries, or interfaces that you need at compile time. 
  - `Migrations.sol` is a complete, fully working smart contract written in Solidity. It is used by truffle to ensure that your project's deployment to the blockchain is carried out in the proper sequence. 
  
        pragma solidity >=0.4.21 <0.6.0;
        
        contract Migrations {
        
            address public owner;
            uint public lastCompletedMigration;
            
            constructor() public {
                owner = msg.sender;
            }
            
            modifier restricted() {
                if (msg.sender == owner) _;
            }
            
            function setCompleted(uint completed) public restricted {
                lastCompletedMigration = completed;
            }
            
            function upgrade(address newAddress) public restricted {
                Migrations upgraded = Migrations(newAddress);
                upgraded.setCompleted(lastCompletedMigration);
            }
         }
- `migrations/` will store truffle "deployer" Javascript files. Every time you want to deploy a contract, you will need to tell truffle which one, and what constructor arguments you may need.
  - `1_initial_migration.js` is the script that deploys our Migration contract. It is the most basic type of deployment because it requires no library linking, or constructor arguments.
  
        var Migrations = artifacts.require("./Migrations.sol");
        
        module.exports = function(deployer) {
          deployer.deploy(Migrations);
        };
- `test/` is pretty self explanatory. It can contain js or sol files, depending on your choice of testing language. It starts off empty, but we will add a test later on.
- `truffle-config.js` is the main configuration for your Truffle project. This is where we define what networks to use, gas usages, addresses to deploy with, and a few other variables.
        
        module.exports = {
            networks: {}
        }

## Compiling your contracts

### Truffle comes with many useful commands, such as:

![Truffle help (pic missing)](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/01/truffle_help.png)

  Check out the documentation of [Truffle command line arguments](https://truffleframework.com/docs/truffle/reference/truffle-commands). We will only be using `init`, `compile`, `migrate`, and `test`.

Let us see if we can compile the single contract that was generated for us. It is very easy to run truffle commands for now, but we will eventually build up the script library in our `package.json`.

        node_modules/.bin/truffle compile

![Truffle compile (pic missing)](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/01/truffle_compile.png)

Truffle will create `build/contracts/Migrations.json`. This json file contains a lot of information about the compiled contract. It includes the whole compiled bytecode, along with function signatures, events, docs, and compiler information. 
        
        {
            "contractName": "Migrations",
            "abi": [
                ...
            ],
            "bytecode": "0x...",
            "deployedBytecode": "0x...",
            "source": "pragma ...",
            "ast": {
                ...
            },
            ...
            "schemaVersion": "3.0.1",
            "updatedAt": "2019-01-10T15:34:39.612Z",
            "compiler": {
                ...
            }
            ...
        }
The two most important parts are the "abi" and "bytecode"/"deployedBytecode". 
- Ethereum has specified a common way of interacting with contracts using the [Application Binary Interface](https://solidity.readthedocs.io/en/develop/abi-spec.html) (ABI). It is what tells clients how to create transactions that will run on the contract, and what events you should expect. There is more in-depth information in the documentation, and we highly recommend reading it if you plan on developing smart contracts with Solidity.
- The bytecode is what gets run on the [Ethereum Virtual Machine](https://solidity.readthedocs.io/en/v0.5.2/introduction-to-smart-contracts.html#index-6) (EVM). In order for you to "deploy" a contract onto the Ethereum blockchain, you must submit the deployedBytecode as a transaction. Technically, the bytecode is what is stored as the contract, and when you call a function, it can only interact with that bytecode in the EVM.

Remember when we said Solidity makes many breaking changes? It is standard to use the specific commit hash as the compiler version, i.e.
        
        {
            ...
            "compiler": {
                "name": "solc",
                "version": "0.5.0+commit.1d4f565a.Emscripten.clang"
            },
            ...
        }
    
  This becomes important when others want to prove that your source matches your bytecode. [Etherscan's verification tool](https://etherscan.io/verifyContract2) will allow users to interact with your public contract, but they will not let you upload your source by faith. They will try to compile your code, and the bytes must match exactly. 

Truffle compile will search through your `contracts/` directory, and compile any contracts, or libraries. If you have any import statements in those .sol files, truffle will attempt to find them. 

There is also one config variable in `truffle-config.js` that will affect compilation. These are the solc defaults, but you can go ahead and add these to your config file.

        module.exports = {
            ...
            solc: {
                optimizer: {
                    enabled: true,
                    runs: 200
                }
            }
        }
The number of runs will affect the tradeoff between contract creation gas and subsequent function call gas. If you don't mind a large upfront cost, you should have more runs of the compiler. Here is the quote from the [Soldity docs](https://solidity.readthedocs.io/en/develop/using-the-compiler.html#using-the-compiler):
        
>"By default, the optimizer will optimize the contract assuming it is called 200 times across its lifetime. If you want the initial contract deployment to be cheaper and the later function executions to be more expensive, set it to --runs=1. If you expect many transactions and do not care for higher deployment cost and output size, set --runs to a high number."

# Starting your very own blockchain

To understand how to run your smart contracts, you must first understand what a blockchain is. Unfortunately, this is not the place to have "the talk" with you. We will only discuss the bare minimum needed to develop and deploy your first contract.

Definitions:
- **blockchain**: a data structure that looks like a linked list
- **Ethereum**: a "platform", or a set of protocols that are responsible for turning "transactions" into "blocks"
- **transaction**: a list of bytes that may contain bytecode, data, or anything technically (improperly formatted transactions just get reverted)
- **block**: one of the linked nodes in the above stated "blockchain"
- **gas**: some cash that is used to inventivize strangers to add your "transaction" to a "block"
- **ether**: a mapping from "address" to a uint256, every address as a balance
- **address**: a number that is 20 bytes long
- **private key**: a number that is 32 bytes long, every "private key" has exactly one "address" associated with it
- **mnemonic**: a list of 12 words that serve to generate your (private key, address) pair
- **network**: a unique number that tells the client which chain to use. Think of each network as a separate blockchain that have all forked form Mainnet

[Ethereum's own description of itself ...](http://www.ethdocs.org/en/latest/introduction/what-is-ethereum.html)
> Ethereum in the narrow sense refers to a suite of protocols that define a platform for decentralised applications. At the heart of it is the Ethereum Virtual Machine (“EVM”), which can execute code of arbitrary algorithmic complexity. In computer science terms, Ethereum is “Turing complete”. Developers can create applications that run on the EVM using friendly programming languages modelled on existing languages like JavaScript and Python.

As a smart contract *developer*, you don't really need to care about the exchange rate between [fiat](https://en.wikipedia.org/wiki/Fiat_money) and ether, or stock fluctuations. In fact, you can create your very own blockchain, where everything is free. The introduction of buying power is just necessary to ensure that the blockchain will live on, distributed across the world, with no grand organizer or power differential.

## Using Ganache to create a local blockchain

Anyone who knows the protocols that Ethereum layed out, can run the EVM, or connect to the main network. For now, we would like to run a local client that will act as our little EVM. The Truffle Suite has an easy to use client called [Ganache](https://github.com/trufflesuite/ganache-cli). It comes in two flavors, console and cli. We will be using the cli for this project, because we will be running it programmatically, and we can run it with our own configuration. If you want to get a better feel for what Ganache is doing in the background, check out the [console](https://truffleframework.com/ganache) version.

1. First, lets install it into our project

        npm install ganache-cli --save-dev

2. To make our lives easier, lets add a script to our project to run our ganache. Add the following to `package.json`:
        
        {
            ...
            "scripts": {
                "ganache": "ganache-cli --networkId $npm_package_config_ganache_networkId --allowUnlimitedContractSize --gasLimit $npm_package_config_ganache_gasLimit --gasPrice $npm_package_config_ganache_gasPrice --mnemonic \"$MNEMONIC\""
            },
            "config": {
                "ganache": {
                    "networkId": 3431,
                    "gasPrice": 25000000000,
                    "gasLimit": 6500000
                }
            }
        }
    This lets us run the Ganache command line with a few config variables. `networkId` is arbitrary, as long as you are not using one of the public ids such as 1, 3, 4, or 42 (see the [test network disambiguation](https://ethereum.stackexchange.com/a/17101/46043)). You can update `gasPrice` or `gasLimit`, but for now lets leave these defaults.

3. Ganache will also take an optional mnemonic. This mnemonic serves to generate the private keys to be given starting balances of ether. Every Ethereum blockchain must start with some starting amount of ether, otherwise there is nothing to trade. Without a mnemonic, Ganache will randomly generate these private keys, but let's create one just so it is consistent between runs.

        export MNEMONIC="cause dry tilt taste hamster document hen over acoustic explain game distance"
    **Warning: This mnemonic should be secret!** You should treat this like a password. This is why we will always be using environment variables to inject into our scripts.
    
    You can randomly generate by running ganach-cli without one. For example `node_modules/.bin/ganache-cli | grep Mnemonic` will output the single line with it. Then you can just kill the process with ^C. ![Ganache cli mnemonic (pic missing)](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/01/ganache_cli_mnemonic.png)

4. Run Ganache and see what is generated

        npm run ganache

    ![Ganache cli (pic missing)](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/01/ganache_cli.png)
    Ganache will generate accounts based on what parameters you run it with. The default is 10 with starting balances of 100 Ether. The cli will display the addresses, private keys, mnemonic, gas price, and gas limit. These addresses can technically be used on any Ethereum blockchain, not just you local one (but they probably have 0 real Ether).
    
    This Ganache client will sit around, waiting for someone to send it a transaction on port 8545 by default. When it receives that transaction, it will attempt to run it on the EVM (see if the bytecode is correct) and it will then immediately create a single block with that transaction (mine the block) . On the main Ethereum blockchain, several transactions will be added to any given block, but we can be less efficient on our local version. All clients like this one should have a specific set of api calls that can read or write to the blockchain. This is why all transactions are public to everyone in the network.
    
5. Test the Ganache client by sending an api call from another terminal

        curl http://127.0.0.1:8545 \
            -X POST \
            -H "Content-Type: application/json" \
            -d '{"jsonrpc": "2.0", "method": "web3_clientVersion"}'

    ![Ganache cli curl (pic missing)](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/01/ganache_cli_curl.png)
    ![Ganache cli log (pic missing)](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/01/ganache_cli_log.png)
    As you can see, our client responds with a client version, which tells us which protocol to use. Don't worry, you probably won't have to deal with different protocol versions if you are reading this article. This is just a way to test that your client is running properly.
    
    These api calls (like `web3_clientVersion`) are part of Ethereum's protocols. Most clients will support the majority of these methods. For an explanation of these methods see the [JSON RPC docs](https://github.com/ethereum/wiki/wiki/JSON-RPC). 


## Deploy to your local blockchain

Now that we have a blockchain client to store our transactions, lets deploy our contract from before to it. In Truffle lingo, we say that were going to migrate our project to our development network. Well need to add some more configurations and then it will be as simple as running a command.

1. Add configuration for our development network to `truffle-config.js`:
        
        // environment variables set in the package config
        var networkId = process.env.npm_package_config_ganache_networkId;
        var gasPrice = process.env.npm_package_config_ganache_gasPrice;
        var gasLimit = process.env.npm_package_config_ganache_gasLimit;
        
        module.exports = {
            networks: {
                development: {
                    host: '127.0.0.1',  // ganache defaults
                    port: 8545,         // ganache defaults
                    network_id: networkId,
                    gas: gasLimit,
                    gasPrice: gasPrice
                }
            },
            ...
        }
    As you can see, we use the same configs that we used to run ganache. If we use arbitrary numbers, it will not necessarily fail, but these ensure all the numbers we see for gas usage are consistent.
    
2. Let's add some more scripts to `package.json`:
        
        {
            ...
            "scripts": {
                ...
                "start": "concurrently \"npm run ganache\" \"npm run migrate\"",
                "migrate": "rm -rf build && truffle migrate --reset --compile-all --network development"
            },
            ...
        }

3. We need to install [concurrently](https://www.npmjs.com/package/concurrently) so we can coordinate ganache and truffle together in the same command.

        npm install concurrently --save-dev

4. Run the script

        npm run start
    
    When we run this, truffle will first compile, and then run its migration steps using the development network. The development network simply points to our Ganache client.
    
    ![Truffle migrate 0 (pic missing)](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/01/truffle_migrate_0.png)
    ![Truffle migrate 1 (pic missing)](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/01/truffle_migrate_1.png)
    ![Truffle migrate 2 (pic missing)](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/01/truffle_migrate_2.png)
    Ganache's output will contain a lot of good information about what was going on. You get back a list of all the API calls made to it, such as "eth_getBlockByNumber" or "eth_sendTransaction". When you send a transaction, it will display things like the transaction hash, gas usage, block number, and contract address (if the transaction created a contract).
    
As you can see, the client is still running. You can now send transactions to localhost:8545 from browser Javascript libraries ([Web3js](https://web3js.readthedocs.io/en/1.0/)), Java libraries ([Web3j](https://github.com/web3j/web3j)) or even curl ... although the syntax starts to become cumbersome.

If you deployed a contract, you could now send transactions that run specific functions on those contracts. You have to specify which address to send these to. In order to find out which address your contract was deployed on, you can either follow the Ganache logs, or look it up in `build/contracts/Migrations.sol`:

          {
              ...
              "networks": {
                  "3431": {
                      "events": {},
                      "links": {},
                      "address": "0x2fAeC1D9fC41FC63976187cf912264a632BDc05D",
                      "transactionHash": "0x4915f3413fc1b6b9e973d983de35f68d6874572b5d3093ea8ccae3eb618464f2"
                  }
              },
              ...
          }
   Here, you can see the network is specified by a number, our local one we chose as 3431 (arbitrarily chosen). `transactionHash` is a unique identifier of that transaction. Anyone can look up that specific transaction based on it, and will be able to see all the events emitted, or other internal transactions that occurred during it. Even if it reverts because of some runtime error, it will still be present and forever recorded that you made a mistake!
   
   So now we need to specify the address `0x2fAeC1D9fC41FC63976187cf912264a632BDc05D` if we want to talk to the contract "Migrations". We can test it out by sending an RPC to our running ganache client. Let's get the bytecode deployed at that address...
   
       curl -X POST --data '{"jsonrpc":"2.0","method":"eth_getCode","params":["0x2fAeC1D9fC41FC63976187cf912264a632BDc05D", "latest"],"id":1}' localhost:8545
       
   ![Ganache cli getCode (pic missing)](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/01/ganache_cli_getCode.png)
   
   You should see something like the above response. The address I used in the screenshot is different because this was forked at a different time. You can always get the code deployed at an address if it a contract address. You can get the balance of any address including contracts. 

# Using Infura to connect to public networks

So far, we have created a real working blockchain. However there are already a few out there that are running "decentralized". In fact, the one called Mainnet is what most people are talking about when they say Ethereum. The developers of Ethereum, and other organizations created some test networks that public and decentralized, but you do not need money to start using it. They usually require [Proof of Authority](https://medium.com/poa-network/proof-of-authority-consensus-model-with-identity-at-stake-d5bd15463256) but their differences are explained [here](https://medium.com/coinmonks/ethereum-test-networks-69a5463789be) For our purposes, we are going to work with the three main test networks Kovan, Rinkeby, and Ropsten, along with Mainnet (the big one).

Again, as long as you know the protocols (which are public domain), you can connect to these public networks with your own custom clients. However, this is a lot of work, and hipsters don't like working more than they have to. So we can either use Ethereum's open source Go client [Geth](https://github.com/ethereum/go-ethereum/wiki/geth) or we can be even lazier, and use a free hosted service called Infura.

[Infura](https://infura.io) is a super easy way to connect to the public networks because you do not have to worry about running code on a server, or keeping it available. We are going to sign up for a free project. It will give us a few important things to work with. For this article, we will need the project id, and the endpoint url. ![Infura console (pic missing)](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/01/infura_console.png) We will call this our Infura client because in the background, Infura is running a Geth client as a node in one of the public networks. [Infura's Getting Started Guide](https://blog.infura.io/getting-started-with-infura-28e41844cc89)

We also want to use public networks because we might want to call other contracts that may not exist on our local development network. For example, if you want to exchange Dai tokens (a stable coin), you have to test on Kovan because only Kovan and Mainnet have the Dai contract.

1. Add this project id as an environment variable

        export INFURA_PROJECT_ID=1234whatever

2. Let's check to see if it is working properly.

        curl https://mainnet.infura.io/v3/$INFURA_PROJECT_ID \
            -X POST \
            -H "Content-Type: application/json" \
            -d '{"jsonrpc":"2.0","method":"web3_clientVersion","params": [],"id":1}'

    ![Infura curl (pic missing)](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/01/infura_curl.png)
    Awesome, looks like we hit our new Infura client

## Forking from Infura

You can use this new client to do essentially everything we just did on our Ganache client, see Infura's [api docs](https://infura.io/docs). However, if you want to write transactions to these public blockchains, it will cost Ether. Even on the test networks, you still have to acquire Ether. Follow [these links](https://www.google.com/search?q=ethereum+test+faucets) to lean more about *faucets*. Also, all of these public transactions are persisted across the world. For development, we still want to spin up a local ganache client to keep our code base relatively private, and so we are not incurring unnecessary costs.

We can leverage our Infura client by forking it into our local Ganache. Forking is a very literal term in the sense that we are taking the "linked list" that makes up the public blockchain, and then adding our own transactions onto the end of it. We do not care about public transactions "post fork" because our linked list continues off from the other (never linking back up again). 

![Blockchain fork (pic missing)](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/01/blockchain_fork.png)

If we reference a contact that was created before the fork, our local ganache will simply search our Infura client for that particular code. It will never change, even if other people interact with the same contract on a different blockchain. Notice how the blocks only retain information about the past blocks (the previous hash). Think of that as a checksum on the past. 

Luckily, it is very easy to fork into Ganache. We just have to specify the Infura client endpoint along with which network. 

1. Lets add to our scripts in `package.json`

        {
            ...
            "scripts": {
                ...
                "start:kovan": "concurrently \"npm run ganache:kovan\" \"npm run migrate\"",
                "start:rinkeby": "concurrently \"npm run ganache:rinkeby\" \"npm run migrate\"",
                "start:ropsten": "concurrently \"npm run ganache:ropsten\" \"npm run migrate\"",
                "ganache:kovan": "npm run ganache -- --fork \"https://kovan.infura.io/v3/$INFURA_PROJECT_ID\"",
                "ganache:rinkeby": "npm run ganache -- --fork \"https://rinkeby.infura.io/v3/$INFURA_PROJECT_ID\"",
                "ganache:ropsten": "npm run ganache -- --fork \"https://ropsten.infura.io/v3/$INFURA_PROJECT_ID\""
            },
            ...
        }

2. We need to add to our `truffle-config.js` to point to these new networks:

        var HDWalletProvider = require('truffle-hdwallet-provider');

        // environment variables not set in the package config
        var infuraProjectId = process.env.INFURA_PROJECT_ID;
        var mnemonic = process.env.MNEMONIC;
        
        // naive environment assertions, since these aren't present by default
        if (infuraProjectId === undefined || infuraProjectId === '') {
            throw new Error('truffle-config.js needs the environment variable "INFURA_PROJECT_ID"');
        } else if (mnemonic === undefined) {
            throw new Error('truffle-config.js needs the environment variable "MNEMONIC"');
        } else if (mnemonic.split(' ').length != 12) {
            throw new Error('The environment variable "MNEMONIC" must be 12 words (space delineated)');
        }
        
        ...
        
        modules.export = {
            networks: {
                ...
                kovan: {
                    provider: () =>
                        new HDWalletProvider(mnemonic, `https://kovan.infura.io/v3/${infuraProjectId}`),
                    network_id: 42, // Kovan Id
                    gas: 3000000,
                    gasPrice: 100000000000
                },
                rinkeby: {
                    provider: () =>
                        new HDWalletProvider(mnemonic, `https://rinkeby.infura.io/v3/${infuraProjectId}`),
                    network_id: 4, // Rinkeby Id
                    gas: 3000000,
                    gasPrice: 100000000000
                },
                ropsten: {
                    provider: () =>
                        new HDWalletProvider(mnemonic, `https://ropsten.infura.io/v3/${infuraProjectId}`),
                    network_id: 3, // Ropsten Id
                    gas: 3000000,
                    gasPrice: 100000000000
                },
                live: {
                    provider: () =>
                        new HDWalletProvider(mnemonic, `https://mainnet.infura.io/v3/${infuraProjectId}`),
                    network_id: 1, // Mainnet Id
                    gas: 4000000,
                    gasPrice: 100000000000
                }
            },
            ...
        }
    We have to use the mnemonic here, because when we start sending transactions to the main networks, we have to use existing addresses with balances. 
    
3. Now we need to install [HDWalletProvider](https://github.com/trufflesuite/truffle-hdwallet-provider) so we can turn mnemonics into our addresses and private keys.

        npm install truffle-hdwallet-provider --save-dev

4. Try running one or all of the forking scripts:
        
        npm run start:kovan

This command will start our local ganache (which forked from kovan), and then will migrate our contracts to it. Our local client will keep running, and we can view the contracts which have been successfully deployed.

Run `node_modules/.bin/truffle networks`: ![Truffle network (pic missing)](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/01/truffle_networks.png)

You can send function calls as transactions to this address on our local client. 

# Adding linting to your project

When working with contract code, you should always follow [best practices](https://solidity.readthedocs.io/en/v0.5.2/security-considerations.html#security-considerations). Not because your technical lead is particularly stubborn, but because this code will be handling your client's money. You may assume that your solidity code will be audited for security and optimization. Linting is always the first step in this process.

1. Add [Ethlint](https://www.npmjs.com/package/ethlint)(formally know as Solium) to your project
    
        npm install ethlint --save-dev

2. Init Solium

        node_modules/.bin/solium --init
    This generated two files `.soliumrc.json` and `.soliumignore`.
    - `.soliumrc.json` will store your rules for running lint on the Solidity code.
            
            {
              "extends": "solium:recommended",
              "plugins": [
                "security"
              ],
              "rules": {
                "quotes": [
                  "error",
                  "double"
                ],
                "indentation": [
                  "error",
                  4
                ],
                "linebreak-style": [
                  "error",
                  "unix"
                ]
              }
            }
    - `.soliumignore` tells Ethlint what to skip. 
    
            node_modules
            contracts/Migrations.sol
        
        There will end up being Solidity code from third parties, such as [OpenZeppelin](https://github.com/OpenZeppelin/openzeppelin-solidity). Obviously you will want to vet any code you include, but as far as getting started with development, you do not want to have to fix other people's styling errors.

3. Update `.soliumrc.json` to include better security and general linting:

        {
          "extends": "solium:all",
          "plugins": ["security"],
          "rules": {
            "arg-overflow": "off",
            "blank-lines": "off",
            "error-reason": "off",
            "indentation": ["error", 4],
            "lbrace": "off",
            "linebreak-style": ["error", "unix"],
            "max-len": ["error", 120],
            "no-constant": ["error"],
            "no-empty-blocks": "off",
            "quotes": ["error", "double"],
            "uppercase": "off",
            "visibility-first": "error",
        
            "security/enforce-explicit-visibility": ["error"],
            "security/no-block-members": ["warning"],
            "security/no-inline-assembly": ["warning"]
          }
        }

4. Optionally, update `.soliumignore` so that Ethlint will lint `Migrations.sol`

5. Add Ethlint to your list of scripts. Then run the linter:
        
        {
            "scripts": {
                ...
                "lint:sol": "solium -d ."
            }
        }
        
        npm run lint:sol
    ![Solium Lint (pic missing)](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/01/solium_lint.png)
    
    As you can see, the `Migrations.sol` generated for us has some styling errors. You will have to come up with your own criteria linting.
    
# Testing

The best part of the whole article, we finally get to write some tests.

Truffle comes with the command `truffle test` which will run all the unit tests, or specific ones if you specify them, [see docs](https://truffleframework.com/docs/truffle/testing/testing-your-contracts#command). First, we will add some scripts to abstract away the running of our local blockchain in conjunction with running the tests.

1. Install two cool modules to help us run the unit and acceptance tests:

        npm install truffle-test-utils eth-gas-reporter --save-dev

2. Add a script to `package.json`

        {
            ...
            "scripts": {
                ...
                "test": "concurrently \"npm run ganache\" \"npm run migrate && truffle test\" --kill-others --success first"
            },
            ...
        }
    All this does is start a local Ganache client, migrate the current contracts, then run units/acceptance tests on that deployed code. `--kill-others --success first` just tells [Concurrently](https://www.npmjs.com/package/concurrently#usage) to stop running the Ganache client after the tests have finished.
    
3. We can update `truffle-config.js` to use [eth-gas-reporter](https://www.npmjs.com/package/eth-gas-reporter) in its [Mocha](https://mochajs.org/) configuration

        ...
        
        modules.export = { 
            ...
            mocha: {
                reporter: 'eth-gas-reporter',
                reporterOptions : {
                    currency: 'USD',
                    gasPrice: 2
                }
            }
        }
    This will give us more information during the testing, including gas usage for each function called. We'll see this in our testing output later.

4. Now we can add our first test. Create the file `test/Deployment.test.js`:

        require('truffle-test-utils').init();
        
        const Migrations = artifacts.require('Migrations');
        
        const MAX_DEPLOYED_BYTECODE_SIZE = 24576;
        
        
        contract('Migrations', (accounts) => {
        
            let migrations;
        
            // build up and tear down a new Migrations before each test
            beforeEach(async () => {
                migrations = await Migrations.deployed();
                console.log(migrations);
            });
        
            it('has a validated contract size', async () => {
                // bytecode is in hexadecimal, where each byte is represented by two characters: 0x00 -> 0xff
                let bytecodeSize = migrations.constructor._json.bytecode.length / 2;
                let deployedBytecodeSize = migrations.constructor._json.deployedBytecode.length / 2;
        
                console.info('Migrations deployed at address: ' + web3.utils.toChecksumAddress(migrations.address))
                console.info(' -- size of bytecode in bytes = ', bytecodeSize);
                console.info(' -- size of deployed in bytes = ', deployedBytecodeSize);
                console.info(' -- initialisation and constructor code in bytes = ', bytecodeSize - deployedBytecodeSize);
        
                // Make assertion on deployed since the initial transaction takes constructor bytecode into account
                assert(deployedBytecodeSize <= MAX_DEPLOYED_BYTECODE_SIZE, 'Contract bytecode is too big to deploy!');
            });
        
        });
    Here is an acceptance test. It can be writen for every contract because it makes no assumptions about the contract functions or data. Instead, it tests the compiled bytecode's size. It is important when writing smart contracts, to be aware of the transactions with the blockchain because not only do you pay for every byte of code, but some transactions are too large and will fail.

5. We can also write a unit test

        it('sets lastCompletedMigration by the owner', async () => {
            let expectedCompleted = 1234;
    
            await migrations.setCompleted(expectedCompleted, { from: accounts[0] });
    
            assert.equal(expectedCompleted, await migrations.last_completed_migration({ from: accounts[0] }),
                'setComplete did not update last_completed_migration');
        });
    Here is a unit test that tests the happy path for the function `setCompleted`. Notice the use of async and await in these tests. Every time we call the contract, we must wait for our client to respond. 

6. Run the tests

        npm run test
    ![Truffle test (missing pic)](https://raw.githubusercontent.com/tylerjohnhaden/blog-usa/master/images/2019/01/truffle_test_1.png)
    
    Here, we don't see any listed methods in the table. This is because the only non-contract-creation transactions we tested was `last_completed_migration` which was a [*view*](https://solidity.readthedocs.io/en/v0.5.2/contracts.html#view-functions) function, which does not get picked up by Mocha.
    
    We will get back a lot of logs, and the more the better. First, we see ganache starting up and truffle migrating our contract. Next we start to see the tests getting run. Once the pass, we can see the gas usages generated by Mocha.
    
# Wrap up

Hopefully you have learned enough from this article to get started with your own smart contract projects. The languages and tools are constantly improving, and smart contract applications can start relying on tried and tested software development patterns. Don't forget to keep security in mind, and never take someone's word on the state of a contract. Trustless applications are intended to be verified, and bytecode is intended to be public. 

To see the latest version of our boilerplate code, submit issues, or contribute, check out our public [repository](https://github.com/tylerjohnhaden/__truffle-boilerplate).


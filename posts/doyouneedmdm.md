---
authors:
- Justin Risch
categories:
- 
date: 2015-01-14T13:15:53.000Z
title: "Do you need Master Data Management?"
image: 
---

## Qualities of Master Data

 Microsoft’s white paper on the subject<sup>2</sup> lists several criteria to look for, and here I will provide alternate explanations to help further establish what qualities master data has.

**Behavior**  
 Master Data is usually involved with transactional data. If you only keep track of purchases (transactional data), you probably don’t have master data. But if you keep a table of users and a table of products, then create a transactions table that maps the two, it’s probable that you have master data in the form of your customers and products tables. Note that transactional data (the one-time relation between a customer and a product) is *not* seen as Master Data, for one shining reason…  
**Reusability**  
 For any data to be considered Master Data, it needs to be reused in two dimensions. Firstly, it needs to be used in multiple physical locations, but also multiple temporal locations. Once a transaction is completed, it’s usually kept in records in at least one location, but it will only ever reference that one event in time– you would never have two purchases point to the same transaction, even if the same person bought the same products.  
 Compare that to a user’s home address– which may be referenced daily for things like identity verification, localization, shipping, and billing. Since the user’s address is pulled to generate shipping addresses for the transactions, we may consider the address to be master data. This one-to-many relationship is a fair indicator that we may have master data, since it marks one data being used over and over again.  
**Lifetime**  
 In order to be reused, as we covered above, the data must have two distinct qualities. Firstly, it needs to represent a long term condition. A user will exist and be active on the system for a long time; an order may exist in the database but once it’s completed (and becomes inactive) the data inside will likely never be referenced again except to see user history. In this way, we see that transactional data such as orders is short lived in its relevancy, while master data may be relevant for years.  
**Volatility and Life Cycle**  
 During its lifetime, the data should exist in a certain limbo of volatility. Information that will never change is unlikely to need MDM as it will never need updating across the databases to keep them in sync. On the other hand, data that is updated constantly is both unlikely to be reusable and would make an MDM difficult to maintain, with conflicts arising often. Again, we can look to user account information for a good example of what Master Data should look like. It can be updated with new passwords, new contact information, and often new financial information, but the likelihood is that this will not be regularly needed. In this way, an MDM solution allows us to keep the records in multiple locations and keep them all up to date to prevent data conflicts. For example, if a user was to update his password in one location, it should reverberate through the system and have that information changed in all locations; the end goal being consistency throughout the databases.  
 Examples of data too volatile to keep up to date are more rare than information that’s not volatile enough (such as one-time events). Though it’s a bit extreme, one such category of over-volatile information would be anything streamed from an analog signal. Imagine if your database wanted to keep track of the location of the user’s login, for security reasons of course, and he went for a drive. With his mobile device hopping from cell tower to wifi network and back, you could find your MDM system cluttered with updates and sync requests… from one user. With a million users, the issue is compounded.  
**Complexity**  
 Simple information is rarely Master Data. In computer science terms, primitive types are rarely master data. If I’m building an inventory system, it’s less likely to contain information on each item in stock (such as the condition of the item), but rather to contain groups of items based on description (iPod, black) and a count of how many are in stock. Low complexity entries such as this are often higher in volatility (as the number of iPods could change by the minute) and not worth considering as Master Data. In comparison, Amazon may consider each product sold on their site as Master Data, as the condition, seller, and delivery information may have several entries for a single product.  
**Value**  
 Value in this sense has a two fold meaning. There is the obvious, literal value of the data, such as if the data represents a source of income for the business. If the data represents a product or service the business offers, it’s likely to share many of the other listed qualities.  
 The other meaning of value being how much that data is worth to the individuals it represents. A user’s personal information may not be worth much to the business, but it represents that user, and so they will be inconvenienced if it’s wrong. For example, the billing address of a debit card is unlikely to be needed– you may use the card without ever referencing it. But suppose that card is lost, and the information in the database is wrong because it failed to update when you moved. Now, your replacement card was sent to your old address. Of course, avoiding this situation is a dire necessity, and so the billing address (though unnecessary for anything other than customer support/relations) is Master Data.  
**Cardinality**  
 Suppose your system has a user login. Each user could have a very long list of attributes associated with them. Suppose the information was stored in 3 locations– one for each office location you have. Now suppose the total number of employees was 30… there really wouldn’t be a benefit to adding a MDM system. Updating a user’s information could be done simultaneously in all three locations, without really generating any significant number of requests. Even though user information is almost always Master Data, it’s simply not worth it in very small data sets.


## Where to look for Master Data?

 Though Master Data is as diverse as the companies that have them, there are several themes that we can look to in order to help us refine our internal search for Master Data within our own company.

**People**  
 Who do you do business with? Customers, clients, users, suppliers, distributors.

**Locations**  
 Brick and mortar has the wonderful quality of low volatility, high value, complexity and reusability. Addresses and anything associated with them.

**Things**  
 What does your business need to continue? Not only internal inventory for sale, but potentially what your business consumes and how much it has. An airline, for example, needs to keep track of it’s fuel in many locations and distribute that information to all the other locations. Otherwise, they risk a plane landing to refuel and finding out there is none left. As well, products or services you directly sell or buy are often Master Data.

**Financial and Administrative**  
 What information do you often use to generate reports? What sort of accounting information is relevant to your business? This could include the structure of your organization (sales territories, regions you provide service to, cost centers, profit centers, price lists).


## Evaluating your MDM needs

Now that you see how much Master Data you have, do you need to manage it? Answer the questions below to see if you should!

- Do you have multiple databases that require the same elements in each, which should remain synchronized?
- Do you either currently have large sets of Master Data, or need the scalability to those levels in the near future?
- Is the effort invested in setting up an MDM going to offset future conflict?

 With those considerations in mind, you should have a better idea on what Master Data is, why we have management systems for it, and whether or not you should be implementing an MDM yourself!

<sup>1</sup>[Semarchy ](http://www.semarchy.com/en/overview/what-is-master-data/)  
<sup>2</sup>[Microsoft](http://msdn.microsoft.com/en-us/library/bb190163.aspx#mdm04_topic2)

---
authors:
- Dan Ferguson
tags:
- data
- q
- kdb
- kx systems
- data science
date: 2018-14-12T14:57:45.000Z
title: "Generating the data for your ML project with q"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/10/blog_thumbnail.jpeg
---

# Introduction
About a week ago, Spotify created a new playlist for me entitled "Your Top Songs 2018."  Curious, I listened to the playlist and realized it was more accurate than if I submitted a list of my top songs from 2018 manually.  This got me thinking about machine learning and how it has permeated even the most casual aspects of our lives, like music.  What struck me most about this realization was how accurate the playlist seemed.  Listening to that playlist, I felt Spotify knew better than I know me.  Then I got to thinking, how much did Spotify know me better?  Can we measure that?  Can we know for certain how much Spotify knows me versus how well I know me?  I posit yes, but music and taste are far too subjective an arena for fleshing out this concept.  Let us ask this question with something more concrete, stock prices.

# The Problem
In my sordid past, I would generate pseudo-random data using a language called _q_ and run machine learning algorithms against the data sets.  The results would be useless prediction models against an entire month of fake data that I generated with a few keystrokes.  For me, this meant one of the biggest barriers to machine learning was removed almost entirely: data collection.  I no longer had to gather my own data, establish an ingest pipeline, or work around the copious fields present in some online data set.  I had all of the data I wanted and nothing more than that.
The bad part about using _q_ to generate pseudo-random data sets is exactly that, I ran a machine learning algorithm against pseudo-random data.  This is a lazy practice and is frankly ignorant of the tenants of data science.  However, if you take a moment to think about it, how different is my pseudo-random set of generated data from my personal list of top 2018 songs?  How wide is that gap?  What factors influence the size of the gap, and can we learn to close it?  Can we automate the analysis of these factors and possible teach machines to fix our data for us?  Could a data set gathered over a period of a month be enough to seed accurate data for a week, a month, a year in advance or more?

# The Motivation
Seasoned data scientists and mathematicians have understood and tackled this problem for a long time.  The approach to handling this is concrete, but it requires a model to have already been generated.  This model is then projected out to generate data that fits the model (i.e. a prediction).  This is all perfecly acceptable and much more efficient than what I am describing in this post.  This post is more about challenging our own interpretations of existing data.  Just like Spotify knows me better than I know myself, what other institutions are being automated whose fundamental understanding we are starting to lose?  This post could quickly become dystopian, so for the sake of avoiding a robot-fearing tirade on our automation driven future, I am going to switch gears here and introduce q, a language that I think could be helpful in generating large data sets of pseudo-random data for training your _understanding_ of the data you are modeling.  

# What is Q?
The q language is a language that combines DDL and DML for a columnar, schemaless, in-memory database known as KDB.  KDB is a very fast, very expensive time-series database.  Often times you will find KDB installations running in financial institutions performing the bulk of the calculations required for algorithmic trading.  The language q is what gives KDB its intelligence.  Q is a small, incredibly finiky language, with confusing syntax, no man pages or help menus, and only one published source of documentation.  However, with a little bit of trial and error, you can easily generate large sets of random data.  From there, it's a small, SQL-esque step to apply characteristics and qualities to your data that make it unique to the problem you want to analyze.  From here, you can run your tried and true model against your pseudo-random data set to challenge your own understanding of the data you are modeling.

# Installation
To start, go to [kx download](https://kx.com/download/) and fill out a small form that allows KX Systems to keep track of their downloads.  32-bit q is completely free, but KX Systems likes to keep a mailing list of users.  Once you've downloaded 32-bit q, make sure it is installed in `C:` on Windows or under your User directory for Mac.  Then, double click on the executable `q/m32/q.exe` and a terminal window should pop up, with the `q)` prompt.  This is the q CLI.  I could spend hours talking about q syntax and keywords, but this blog post is not about that.  For curious readers, check the single best source of documentation on q, an eBook called "Q for Mortals," available [here](https://code.kx.com/q4m3/) or for purchase on Amazon.

# Trust Me On This One...
## Generating Data
Okay, before proceeding, I need you to trust me on what follows.  The q syntax is difficult to understand and notoriously cryptic.  Everytime I use q in one of my projects, I have to refresh my memory on what the syntax looks like and what each operation does.  For the below example, it's best to type it all in and read my explanation after.  Once your ready, type the following into the console: 
```
q) IDs:1 + til 499
q) IDs
```
If everything worked, you should have a series of numbers from 1 to 500 assigned to the variable "id".  The next few lines create similar variables with different data:

```
q) Dates:2018.10.01+500?31
q) Times:500?24:00:00.000
q) Symbols:500?`FB`AMZN`AAPL`NFLX`GOOG
q) Volumes:10*1+500?1000
q) Prices:90.+(500?2001)%100
```

Here is a breakdown of the above, line by line:

1. Generates 500 dates in the month of October, 2018 ranging from the 1st to the 31st.  
2. Generates 500 timestamps in 24-hour notation.  
3. Generates 500 symbols randomly taking the values of FB, AMZN, AAPL, NFLX, and GOOG.  
4. Generates 500 numbers ranging from 1 to 1000, and multiplies those numbers by 10, creating buckets of 10 (i.e. order volumes of 10).
5. Generates 500 floats from 0 to 2000, performs a modulus on each number against 100, and applies the addition of 90 to each value.  This creates a list of 500 prices from $90 to $110.

## SQL-isms
If everything worked you should have several lists of pseudo-random data 500 elements long.  This is fine and good, but how can we make this data usable?  If you remember from earlier, I said q is a CLI onto a KDB instance, which is a database.  Therefore, it stands to reason q has some native functions that let you make data tabular.  Type in the following and you'll have a table of 500 elements stored in application memory:

```
q) Trades:([] Id:IDs; Date:Dates; Time:Times; Symbol:Symbols; Volume:Volumes; Price:Prices)
```

If your q instance is connected to KDB, you'll have created a KDB table.  Since KDB is prohibitibely expensive to install and deploy, it's best to use the default in-memory location.

The acute reader may have noticed by now this is a table of stock prices for the FAANG stocks.  The investment savvy reader may have noticed these prices do not make sense.  Everything was generated based on the same price, somewhere between $90 and $110 dollars.  This is true, and while FAANG stocks are strong investments, they are not priced equally.  To fix this, we can update our table using SQL-esque language like below:

```
q) Trades:update Price:Price + (.5*Price) from Trades where Symbol=`FB
q) Trades:update Price:Price + (15*Price) from Trades where Symbol=`AMZN
q) Trades:update Price:Price + (10*Price) from Trades where Symbol=`GOOG
q) Trades:update Price:Price + (1.5*Price) from Trades where Symbol=`NFLX
q) Trades:update Price:2*Price from Trades where Symbol=`AAPL
```

This means Facebook will be priced around $45 - $55 dollars, Amazon around $1450 - $1550, Google around $900 - $1100, Netflix around $135 - $155, and Apple around $1800 - $2200.

This same process can be applied to the Volume column as well.  If you want to give your fake investors the flexibility to buy in buckets smaller than 10 items, re-run the command accordingly where N is the bucket size:

```
q) Volumes:N*1+500?1000
```

Once you are satisfied with your data, run the following and a file will be saved in the directory where you opened the q prompt.
```
q) save `Trades.csv
```
## Why Did You Trust Me?
At this point, you may be either confused or excited, but equally lost.  As you can see from the above, you just created a lot of pseudo-random data that fits a real world use-case and has characteristics of actual data sets.  Depending on your use case, you can run some insanely powerful modifications to your entire data set, quickly and accurately, imparting statistically significant changes to a once random smattering of numbers.  This is not always an easy task, and yet we've done so in 13 lines.  

# Glaring Issues
## Cryptic
Q is anything but straightforward.  It's a very mathematical language, and its syntax was designed with mathematics in mind.  If you're good at math, learning q will be easier for you than it will be for the laymen.  Even so, understanding the cryptic syntax will always be a challenge.  I believe that is why q does not have the following it could or should have.  There's simply a prohibitive learning curve that forces users down the path of R or Python for data generation, when q could solve their problem in half as many lines of code.

## In-Memory NoSQL Database
Oftentimes when I discuss Q with other people, I say Q is an in-memory NoSQL database, because it's easier to think of it that way.  Q is not a database, but it has a database built in out of the box.  There's no connection strings, there's no additional libraries to manage, there's no concept of a dataframe; everything is right there for you.  This is an issue because it's very difficult to export your data in a custom way to an external application.  Above, we used the save command.  The real decision to use the save command here comes from scanning section of 11 of the Q for Mortals book, getting frustrated with discussions of file handles, web sockets, and interprocess communications, and settling on the simplest option that requires the least explanation.  In short, Q doesn't play well with others.

## Machine Learning Against Random Data
This issue is at the heart and soul of this discussion.  Running machine learning algorithms against random data is an exercise in futility; you will end up with nothing but a useless model and an empty sense of accomplishment.  The real value to this exercise is in challenging a preliminary understanding of the data.  Given a data set, you could generate pseudo-random data (either with a model or manually as described here) to create multiple lines of reasoning for why each data set is related, but slightly different.  Now you have a fleet of data sets to run predictions against, some of which may be more accurate than the initial model.  There are products that do similar things like [DataRobot](https://www.datarobot.com), but those products are just as expensive as 64-bit q and a live KDB instance.

# Still, You Should Try It
I'll admit it, Q is a tough language, and the premise under which I introduce q could be perceived as tenuous.  It's functional, cryptic, designed for timespan operations on a database, and contains its own DDL/DML syntax.  It's tiny and fast, and it likes to play in the sandbox alone.  Its only friends are algorithmic traders that have spent 25 years learning how to write q code effectively (and some of them still have trouble).  Additionally, Python and R do a lot of the same things q can do when it comes to generating data and running timespan calculations.  Still, it's a language that deserves to be looked into, especially if you work with big data and like machine learning.  

## I Gave It A Shot
I gave this technique a shot and the results were interesting.  Using Fidelity as a data source, I gathered finance data on the Google symbol and generated a very basic ML model using H2O.  For more information on this, check out this blog post on [Realtime ML Predictions](https://blog.ippon.tech/realtime-machine-learning-predictions-wth-kafka-and-h2o/) written by my colleague Alexis Seigneurin.  The model I generated used many of the default settings H2O supplies, and the predictions generated against that model followed the same trend as the original data set.  [Here's the Grafana snapshot](https://snapshot.raintank.io/dashboard/snapshot/7qqXjLstyGqcIuYWv2YBzAbzo3birjaB) for my initial predictions.

Taking this idea one step further, I looked at the GOOG data and guilt a table in _q_ that followed some primitive rules derived from the data.
```
q)Dates:2018.10.01+5000?31   
q)Open:750.+(5000?45001)%100
q)High:1000.+(5000?20001)%100
q)Low:750.+(5000?25001)%100
q)Close:750.+(5000?45001)%100
q)Volume:900000.+(5000?200000)
q)Trades_Google_Generated:([] Date:Dates; Open:Open; High:High; Low:Low; Close:Close; Volume:Volume)
q)save `Trades_Google_Generated.csv
```
You'll notice I increased the number of data points to 5000, so I could really push the limits of my model.  I made predictions on this data against the same model generated from the Fidelity data set and [the results](https://snapshot.raintank.io/dashboard/snapshot/XBXFMQ57zJqqHCuvkfkgGa6jrx1z7Fkr?orgId=2) were a little disheartening.  The resulting graph looks chaotic and messy.  But if you zoom in, you can start to see the predictions (green) reflected the behaviour of the actual (yellow) in a more subdued way.  The predictions became more of a trend line, showing the direction of the actual closing value.  

# In Conclusion
My little experiment in machine learning using pseudo-random data sets is probably more of a proof 

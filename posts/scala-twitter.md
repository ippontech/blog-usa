---
authors:
- Justin Risch
categories:
- LifeAtIppon
- Big Data
- Scala
date: 2017-03-08T20:06:55.000Z
title: "Streaming With Scala: The Nuance of Real-Time Twitter Data"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/03/skynetbanner-1.png
---

At Ippon Technologies USA, we're lucky enough to have "Coding Dojos" every 2-4 months. Every programmer takes a day off project to sit down together and learn something new. As I have [already been learning Big Data](http://blog.ippon.tech/pokemon-go-big-data/), I was particularly excited for this month's dojo. It was on Spark Streaming with Scala, a powerful Big Data tool that promises a degree of elegance as well.

I had avoided Scala at first. With a decade of coding in Java, I have enough of a mastery of that language to prefer it in most scenarios. That being said, Scala piqued my interest when building a live stream of data from Twitter took nothing more than this: 
```language-scala
//configure the Streaming Context
val sparkConf = new SparkConf().setAppName("TwitterStreamingDojoApp")
val sparkContext = new SparkContext(sparkConf)
val streamingContext = new StreamingContext(sparkContext, Seconds(60))
//create the stream
val tweets = TwitterUtils.createStream(streamingContext, None)
tweets.foreachRDD((rdd, time) => {...})
//start the stream
streamingContext.start()
streamingContext.awaitTermination()
```

From there, we were given the opportunity to try some analysis on the live-data. Some people wanted to find trending topics; others wanted to perform sentiment analysis on particular subjects to get a feel of how Twitter felt about certain topics. One particularly romantic programmer compared how often things were "loved" or "hated" on Twitter and, despite expecting the worst, was happy to find that love did indeed win that day. I went a slightly different route. 

```language-scala
//perform action on the stream
tweets.foreachRDD((rdd, time) => {
//group tweets by who tweeted it.
val groupedByUser = rdd.groupBy(f => f.getUser)
groupedByUser.foreach(f => {
   //how many tweets they sent
   val total = f._2.size
   //how much positive feedback they got, as a double to prevent truncation in score.
   var feedback = 0d
   System.out.println("User: " + f._1.getScreenName)
   System.out.println("_Tweets: " + f._2.size)
   f._2.foreach(e =>
      feedback += e.getFavoriteCount)
      System.out.println("_Score: " + feedback / total)
   })
})
```
What the above algorithm does is essentially group the tweets by who tweeted them, then assign a score to that user by how many people reacted positively to that person's tweets. A favorite or a retweet both show a degree of agreement with the message, and so both are counted in that category. 

Now there are two things I must say about the code above: Firstly, the code is good and works as designed. Secondly, it does not do what I want it to do. Allow me to explain. 

After I first ran this code, I noticed an immediate issue -- everyone scored 0. No exceptions thrown, no other strange behaviors. So I went into my feed and deliberately favorited tweets I knew would be picked up by the feed...only to still see every person scoring a perfect 0. 

Rather than giving into the increasingly worrisome idea that I had created a [misanthropic AI](https://en.wikipedia.org/wiki/Skynet_(Terminator)), I decided to check the math. When I printed out the variables involved (both "total" and "feedback"), I noticed that "total" was properly set...but "feedback" was always 0. 

At this point, I asked the other 30 or so programmers to check their tweets. Does *anyone* have any favorites or retweets? The no longer surprising answer? **No**. 

It's at this point that I realized my code was perfect. The above code does exactly what I told it to do: when a tweet is created, it is fetched, and then scored. The issue is in the design of it. Since it fetches all tweets at the moment of creation, every tweet has no favorites or retweets. How could it? It has only just been created. 

The solution would be to create a double-buffer system. One thread constantly grabs and stores new tweets, storing them into a temporary cache. The other thread parses the cache, waiting for a particular tweet to reach a certain age (2 or 3 days would be a good start), then looks up the tweet's actual score. 

Unfortunately, this would require several days to return the first good results...and we only had one day in the dojo. Pleased with the knowledge that (given the time) we had a working solution, I closed my laptop proud of what I had learned: real-time data has a personality of its own.

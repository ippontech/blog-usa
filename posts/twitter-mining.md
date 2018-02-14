---
authors:
- Justin Risch
categories:
- 
date: 2017-12-21T01:45:27.000Z
title: "Twitter Mining: Winning By Design"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/11/twittermining.png
---

One popular topic in today's industry is Blockchain technology, which of course brings to mind Bitcoin. This month, Ippon Technologies is hosting a [Dojo](http://blog.ippon.tech/first-coding-dojo-for-ippon-usa/) on Blockchain. While excited for this, it made me wonder if the concept of "mining" could be applied to other feeds of data. This lead me to think of Twitter - what better example of a constant stream of data is there? Well, in order to consider "mining" it, I had to first better define what I would consider "mining" to be.

# How do we define "Mining"?
Firstly, it needs to provide an actual service. For example, when you mine Bitcoins, you're doing two things: you're making sure a transaction is valid, and then you are letting people know it's valid. In any case, if I'm not providing some service, then it doesn't seem right to receive payment. Therefore, I should limit the mining bot to only propagate valid, useful information.

Secondly, it needs some way to return profit. When you mine Bitcoins, the profit you make is based on winning a sort of lottery, to over simplify. In essence, every time you validate a certain number of transactions, you have a small chance of receiving some bitcoins for the work you did. If you have enough miners going at once, you can actually run a whole business out of it. Therefore, my bot should be able to acquire something of value for its work, if not necessarily money.


# How to Mine Twitter
Back to the Twitter data, I had to ask myself: is it possible to consume a stream of tweets and receive a reward for validating and propagating the good tweets? Obviously propagating the information is easy, simply retweet the tweets; but how do we know which are worth retweeting?

Fortunately, Twitter offers a built-in validation system. Unfortunately, it's kind of garbage. Of the tweets I consumed, it averaged between 94-100% unverified tweets. Part of the reason for this is that becoming "verified" is an opt-in service that many don't bother with. Smaller, local companies might even have difficulty getting verified even if they choose to. Because of this, I had to come up with my own three step verification system.

1. Is this user verified by Twitter? If so, go ahead and trust it.
2. Does this tweet have more than 1000 retweets? If so, consider it verified by mass-opinion.
3. If this tweet has been retweeted less than 1000 times, has any of the retweets been by verified accounts? If so, consider it second-hand verification.

The first 2 make up about 10% of tweets I consumed, meaning my bot was ignoring 90% of tweets it saw simply based on the user alone. From there, I was able to increase the number of valid tweets by seeing if any verified account retweeted it. This covers a very specific use case: User A mentions User B. User A is unverified, but User B retweets it. User A is then considered verified by proxy. That use case covers another 5% of tweets, at the time of consumption.

With now an average of 15% of tweets being considered "valid", I began to wonder what type of information I should propagate, and how I would profit from it. People would have to be willing to give me things for retweeting them, or the whole point of "mining" it seemed moot.

Before I began this project, I was blissfully unaware of the fact that many people on Twitter will actually give you free stuff for simply following, retweeting, and favoriting their posts. This seemed like a fairly good start, and eventually the list of keywords expanded to: giveaway, giveaways, give aways, give away, giving away, win, sweepstake, sweepstakes. The code below is the core of how I defined my stream:

```java
public static void listenForTweets() {
    System.out.println("Listening for tweets!");
    TwitterStream t = TwitterStreamFactory.getSingleton();
    t.setOAuthConsumer(ConsumerKey, Secret);
    t.setOAuthAccessToken(new AccessToken(AccessToken, AccessTokenSecret, userId));
    t.addListener(new StreamListenerImpl());
    FilterQuery fq = new FilterQuery();
    fq.language(new String[] { "en" });
    fq.filterLevel("low");
    fq.track(StreamListenerImpl.keyWords));
    System.out.println(fq.toString());
    t.filter(fq);
}
```


Once turned on, the mining bot had an immediately obvious flaw in it. Let's look at the text of some of the original retweets and see if we can rediscover the flaw together:

1. "I can't believe that the [sports team] would just [explicative] give away the game like that."
2. "10 ways to win her heart! [link to article]"
3. "Hey guys! I'm new to twitter!"

So in examples 1 and 2, we see that sometimes certain words don't mean what we think they will. We need more context. In example 3, we see that Twitter has a sense of humor when it comes to what their Streaming service will send you. The solution to this was simple: Require certain words to be in the tweet and block other words.

Swears and controversial topics make for good block words, but so does any prize that you would be uninterested in claiming: for example, there are a LOT of CS:GO giveaways that only give away prizes related to one particular game. If a word is particularly vile, you can also set up your mining bot to block the user to guarantee your bot will never accidentally retweet something crude.

The mining bot was then also restricted to only looking at tweets that both contain a keyword I'm looking for (such as "giveaway"), but also has some instructions on what to do to enter the contest. These words are often "retweet" and "follow", but also different variations of that such as "rt" or "flw" or even "fav". Once those were in place, the feed looks much more promising:

1. "RT to win: $200 Amazon e-gift card. must be following me and [other user] to win "
2. "#Giveaway!! To win a copy of the brand new [video game] on Xbox One, simply RT & Follow me! Winner announced tomorrow"
3. "RT, Click & Sign Up to win a pair of Velo Socks. Winner will be picked at the end of the day."

Make sure your bot actually follows the instructions too. If you don't retweet or follow when it says to, you won't win! But if you retweet and follow every post, you'll run out of daily status updates without the added chance of winning.

```java
private void process(Status tweet) {
    log("| ENTERING: " + tweet.getText());
    if (text.contains("like") || text.contains("fav")) {
        TwitterLookup.favorite(tweet.getId());
    }
    if (shouldFollow(tweet.getText())) {
        TwitterLookup.follow(tweet.getUser());
        Arrays.stream(tweet.getUserMentionEntities())
                .filter(u -> u.getId() != tweet.getUser().getId())
                .map(u -> u.getId())
                .forEach(TwitterLookup::follow);
    }
    if (shouldRetweet(tweet.getText())) {
        TwitterLookup.retweetStatus(tweet.getId());
    }
}
```

# So what did I win?
Once my mining bot was in place, it was simply a waiting game. Over the course of a month, it collected:
- Tickets to a show in Texas.
- Several scams offering me free stuff if I just sent them money first.
- Tickets for me and three of my friends to a show in Ireland, along with the first round of shots.
- Two tee shirts (it was the same shirt).
- Two free downloads of a Kelly Clarkson album (the same one).
- A Funko Pop (small collectible figure)
- A Steam Key for a 4$ video game.
- A 10$ gift card for a store that does not ship outside of Texas.

If you were to add up just the prizes I was able to claim, it would be worth about 40$. Since that's about half my power bill for the month, we can assume that it is in fact making more profit than I spent in power. However, if you consider the fact that I did not actually want any of this stuff (except perhaps the funko pop), it becomes less impressive.

However, let's suppose I had instead tried to mine Bitcoin on the same computer: a MacBook Pro. Luckily, some redditor has already done [precisely that](https://www.reddit.com/r/Bitcoin/comments/6cf1wn/i_mined_bitcoin_for_33_straight_hours_with_my/), and found that over the course of a month, he made 0.00000001 BTC, or $0.0001 dollars, far less than if the redditor had simply mined Twitter for giveaways.

That sobering comparison makes the whole thing feel a lot more like a success, and I may even continue running my mining bot. If I win anything interesting, I'll be sure to update this post later!

Good luck everyone!

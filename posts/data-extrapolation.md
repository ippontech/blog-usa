---
authors:
- Justin Risch
tags:
- Big Data
- Data Science
date: 2017-06-08T19:56:37.000Z
title: "Data Extrapolation: Learning From Your Big Data"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/06/Data-Extrapolation-Blog.png
---

The first step in answering any Big Data-oriented question is to simply obtain the data. If you're lucky, you already have it. Perhaps your company already captures significant data to answer the question that's been posed, or at least can capture it themselves once you begin doing so.

The next best scenario is that, though you don't have the data, you know someone who does. Pay a fee to them, offer data in exchange, trade and barter to get what you need.

Suppose, instead, you're an individual programmer trying to learn Big Data. Well then, you and I have a bit in common. You will, like myself, probably need to make a large amount of API calls to obtain the data you want, and so I would like to share with you how I cut my API calls from 7.5 million to only 50,000 -- or roughly 0.5% of the overall dataset.

<h2>The Question:</h2>
If you noticed in my <a href='blog.ippon.tech/pokemon-go-big-data' target='_blank'>last Big Data post</a>, I've been using Pokemon GO's spawn data as a large dataset to perform functions on. Using this data set, we've already proved that cell phone usage -- or at least high density population -- influences spawn rates. One other claim that the Pokemon GO community made is that weather influences spawn rates; Fire Pokemon appearing more often during hot weather, and Water Pokemon appearing more during rainfall. As I already had the Pokemon GO data, I simply needed the weather.

<h2>Obtaining a Small Sample:</h2>
My first attempt was simply to make a call per Pokemon-record to verify the precise weather at that location and on that day -- 7.5 million calls. Unfortunately, all API's I found that could offer this data were bound in two ways: a limit on calls per minute, and a limit on calls per day. [Wunderground](https://www.wunderground.com/), however, had an interesting "Raindrop" system. Effectively, I could go way outside my limits for one day per raindrop...and my free account started with one raindrop. So then it became a matter of "How many calls can I make during my 3 days of unlimited use?"

Assuming I stayed in the limit of 10/minute, the most I could make in a day is 14400...at this rate, it would take 520 days to do the whole set. Unacceptable. However, I didn't really have a better option at the time, so I ran the code while I tried to get a bit more clever.

<h2>Compare What You Have To What You Want:</h2>

First up, make some POJOs. This will help type your data, and show what they have in common.

For myself, I created a Pokemon class and a Weather class, and noticed that both contain a Date of the event, a Latitude, and a Longitude. The sci-fi nerd inside me excited, I quickly created a class called "SpaceTime" and had both Pokemon and Weather extend it.

While looking at the commonality of the two, I realized that there's also a commonality between two Pokemon that occur in the same SpaceTime. That is to say, at a particular longitude, latitude, and time, there is only one "Weather" in which all Pokemon at that spacetime would experience.

```language-java
JavaPairRDD<SpaceTime, Iterable<Pokemon>> pRDD = context.textFile("filename")
.map(p -> new Pokemon(p))
.groupBy(p -> p.getSpaceTime());
```

If I grouped Pokemon by Spacetime, I saw the total number drop to 6.8 million -- a good start, but not sufficient.

<h2>Apply Fuzzy Logic:</h2>
Have you ever noticed how every stranger you meet on your day-to-day errands typically agree on the weather, but only generally? You both might say it's "cold" but disagree on the exact temperature. It's sort of obvious in retrospect but the solution to reducing the amount of calls needed is simply this:

*If I am experiencing weather, those in my nearby proximity are likely experiencing roughly the same weather.*

With that, I went back to <a href='http://blog.ippon.tech/why-address-not-string/' target='_blank'>a post I had written on comparing geolocations</a> and found my old algorithm to quickly test to see if two coordinates are close to each other, given a radius, and added that as a method to SpaceTime:
```language-java
public boolean isNear(SpaceTime b, double radius) {
double dlat = Math.abs(lat - b.lat), dlong = Math.abs(lng - b.lng);
if (dlat > radius || dlong > radius)
  return false;
return (dlat * dlat) + (dlong * dlong) <= radius * radius;
}
```

With this code in hand, I built a caching system, built on a hashmap: In Pseudocode, it is essentially "if this space-time exists in the weather cache, use the same weather as what was used for that location. Else, grab it from the weather API, and add that to the cache."

In Java, the same was:
```language-java
HashMap<SpaceTime, Weather> weatherCache = new HashMap<>();
...
Optional<SpaceTime> key = weatherCache.keySet().stream()
.filter(spaceTime -> spaceTime.equals(thePokemonsSpaceTime)).findFirst();
Weather realWeather;
if (key.isPresent()) {
   realWeather = weatherCache.get(key.get());
} else {
   realWeather =
   Weathergrab.getHistoricalWeather(w.lat, w.lng, w.day);
weatherCache.put(realWeather.getSpaceTime(), realWeather);
}
```

If you notice, I did not use .contains() on the keyset, but instead choose to filter on .equals. This is because it made it simpler to define why a space-time would equal another space-time. In the Java Docs, it does say that .contains() *calls* .equals(), but running the same code with .contains() breaks for unknown reasons.

In SpaceTime.class, I have overridden the equals method to apply the fuzzy logic here:
```language-java
public boolean equals(SpaceTime obj) {
  boolean isNear = this.isNear(obj, .01);
  if (!isNear)
    return false;
  boolean sameDay = Weathergrab.sdf.format(this.getDay())
.equals(Weathergrab.sdf.format(obj.getDay());
  return isNear && sameDay;
}
```
Essentially, this code boils down to "consider these two space-times the same if they occur on the same day (via that weird date-formatting line) and within 0.01 of each other (roughly 1.1 km or 0.7 miles). Because they are very close and within 24 hours of each other, we can assume that the weather didn't vary much. However, to avoid over-assumption, we do not add assumed-data to the cache. That would cause a bug where the following would happen:

We have point x,y and point x,y+0.01. We assume that the weather at x,y+0.01 is the same as x,y, and add it to the cache. After n iterations, it would be assumed that the weather at x,y is the same as x,y+(n*0.01).

To solve this, we do not cache x,y+0.01's result, resulting in x,y+0.02 needing an API call.

<h2>The Result:</h2>

Since the majority of Pokemon spawn in cities, this cut down the number of requests greatly. It's not particularly accurate -- as weather changes throughout the day -- but it was as accurate as the weather data I had, as the weather was measured on a daily basis, not an hourly one. Thus, there was no loss of information in this extrapolation.

What we gained, however, was only having to make 37,500 rest calls to Wunderground, rather than the original 7,500,000. This only works because of the nature of the data. It was only a few months, so the "days" didn't divide it up too much, and the Pokemon are almost all within a couple miles of any other Pokemon. Uniquely located Pokemon represent a trivial amount of the overall data.

In that same way, you can use the data you have to calculate what you want to know. The steps I would recommend are as follows:

1. Define the properties of each set of data, both what you have and what you want to know.
2. Map their properties to each other to see what is in common.
3. Generalize that data to map more of what you have to what you want, without loss of precision if possible.
4. Cache the data you have from within the data set that you want (In this case, I started with 5,000 of the points of weather data).
5. Fetch more data as needed, but include new data into your existing cache.
6. Once fetched or retrieved from cache, append it to your existing records (the "Pokemon" in this case).

<h2>The Answer To Our Question:</h2>

With this data in hand, I performed some analysis on the data and found mixed results. Yes, the vast majority of the spawns happened at above 70 degrees Fahrenheit for fire Pokemon. However, there were some that happened well below 50. It's possible that our data set, since it is not over the course of an entire year, simply was composed of hotter months than if we had been able to include winter. It is notable, though, that these spawns *did* occur less often further away from the equator, where even summer heat is less significant, choosing instead to mostly occur in San Jose California where the temperatures were mostly above 80 degrees.

In short, the results showed nothing as conclusive as if there has been no spawns below a certain temperature, but it certainly seems as though there is a trend there.

If something in this article sparked your interest about Big Data or Ippon Technologies USA, we'd love to talk with you! Please send all questions and inquiries to [contact@ippon.tech](mailto:contact@ippon.tech).

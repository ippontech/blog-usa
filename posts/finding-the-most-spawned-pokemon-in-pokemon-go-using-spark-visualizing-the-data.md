---
authors:
- Nicholas Peterson
tags:
date: 2017-09-07T02:03:17.000Z
title: "Finding the most spawned Pokemon in Pokemon GO using Spark & visualizing the Data"
image: 
---

My colleague Justin Risch recently obtained some data from the popular game Pokemon GO. He cleansed the data into a much more usable _CSV_ format and I decided to use this to do some practice in Apache Spark.

It was a fairly simple Spark class written in Scala using the Eclipse Scala IDE.

Here is the code:

```scala
object MostSpawnedPokemon {

  def loadNames() : Map[Int, String] = {
    Source.fromFile("../Data/pokemon.csv")
      .getLines()
      .map(_.split(','))
      .filter(_.length > 1)
      .map(fields => fields(0).toInt -> fields(1))
      .toMap
  }

  def main(args: Array[String]) {

    // Set the log level to only print errors
    Logger.getLogger("org").setLevel(Level.ERROR)

    // Create a SparkContext using every core of the local machine
    val sc = new SparkContext("local[*]", "PopularMoviesNicer")

    // Create a broadcast variable of our ID -> Pokemon Name map
    var nameDict = sc.broadcast(loadNames)

    // Read in each Spawn line
    val lines = sc.textFile("../Data/AllData.csv")

    // Map to (spawnedPokemonId, 1) tuples
    val spawned = lines.map(x => (x.split(",")(2).toInt, 1))

    // Count up all the 1's for each Pokemon
    val spawnedCounts = spawned.reduceByKey( (x, y) => x + y )

    // Flip (spawnedPokemonId, count) to (count, spawnedPokemonId)
    val flipped = spawnedCounts.map( x => (x._2, x._1) )

    // Sort
    val sortedCount = flipped.sortByKey()

    // Fold in the Pokemon names from the broadcast variable
    val sortedCountWithNames = sortedCount.map( x  => (nameDict.value(x._2), x._1) )

    // Collect and print results
    val results = sortedCountWithNames.collect()
    results.foreach(println)
  }

}
```

I go through each step of my code in the comments. This ended up being a simple yet solid example of using _Broadcast Variables_ in Spark. Broadcast variables allow the programmer to keep a read-only variable cached on each machine rather than shipping a copy of it with tasks. The output from this was the Pokemon's name and the number of spawn occurrences.

Here is a snippet from the output showing the most uncommon Pokemon:

*  (machamp,8)
* (kabutops,14)
* (charizard,14)
* (farfetchd,14)
* (muk,15)
* (gyarados,20)
* (raichu,23)
* (omastar,25)
* (alakazam,27)
* (ninetales,27)

This is a fun dataset to work with and I am going to continue using it as I begin learning more advanced Spark programming. This simple bit of information regarding what were the most uncommon Pokemon ended up helping Justin in some work he did visualizing the data.

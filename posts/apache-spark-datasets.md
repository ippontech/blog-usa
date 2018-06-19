---
authors:
- Malcolm Thirus
categories:
- Big Data
date: 2016-06-15T11:19:40.000Z
title: "Apache Spark Datasets"
id: 5a267e57dd54250018d6b60e
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/BlogIMAGES-SparkAPIs-1.jpg
---

With a [Spark 2.0 release imminent](https://databricks.com/blog/2016/05/11/spark-2-0-technical-preview-easier-faster-and-smarter.html), the previously experimental [Datasets API](https://databricks.com/blog/2016/01/04/introducing-spark-datasets.html) will be a core feature. Spark Datasets were introduced in the 1.6 release as a bridge between the Object Oriented type safety of RDDs and the speed and optimization of Dataframes utilizing Spark SQL. Databricks has stated a development focus around Datasets with improvements to the core Spark Ecosystem in 2.0. In this post, we’ll explore migrate code utilizing Resilient Distributed Datasets and test the basic use cases of the new core Datasets.

To start, we’ll take [code](https://github.com/jetondreau/spark-blog-examples) from [a previous blog entry ](https://blog.ippon.tech/blog/using-apache-spark-basic-statistics/)and move it to Scala. This allows us to use the full features of case classes provided by Spark Datasets. We will again be using Retrosheet [game logs](http://www.retrosheet.org/gamelogs/index.html). The data is an excellent starting point for first learning a framework like Spark.

[The information used here was obtained free of charge from and is copyrighted by Retrosheet. Interested parties may contact Retrosheet at “[www.retrosheet.org](http://www.retrosheet.org/)“.]

First, we’ll set up our environment and import the `SQLContext.implicits` package. This will allow us to use most standard classes and all case classes in our Datasets.

```language-scala
// Setup our environment
val conf = new SparkConf()
  .setAppName("Blog Example: Statistics and Correlation")
val sc = new SparkContext(conf)
val sqlContext = new SQLContext(sc)
// This import allows us to cast to Datasets[T] easily
import sqlContext.implicits._

```

Dataset input is retrieved using the standard Dataframe format. We use this as a method to turn each Dataframe `Row` into a `Dataset[String]`. In the 2.0 release, Databricks has clarified that a Dataframe will become a Dataset of type `Row`. This code and what to expect in the new release will likely be different.

```language-scala
// raw lines of our source file stored in a Dataset[String]
val gameLogs = sqlContext.read.text(args(0)).as[String]
```

To simulate the array used in Jeannine’s code, we will create a case class of only the fields we will be using for the data analysis. Case classes are a Scala standard way of creating a class with getters and setters. This is especially useful in creating quick Data Models without as many lines as Java.

```language-scala
case class Game(year: String, visitingTeam: String, homeTeam: String, visitingScore: Int,
                homeScore: Int, visitingHomeRuns: Int, homeHomeRuns: Int)
```

We can then use our `Game` class to map our file from a `String` to only the fields we need. Now we have a `Dataset[Game]` that we can treat like the traditional RDD.

```language-scala
// Rather than just split the lines by the comma delimiter,
// we'll use those splits to create a new instance of a case class
// When using a case class with Datasets, the SQL schema is automatically inferred
val mappedFile = gameLogs map { game =>
  val splits = game.split(",")
  val year = splits(0).substring(1,5)
  val visitingTeam = splits(3)
  val homeTeam = splits(6)
  val visitingScore = splits(9)
  val homeScore = splits(10)
  val visitingHomeRuns = splits(25)
  val homeHomeRuns = splits(53)
  Game(year, visitingTeam, homeTeam, visitingScore.toInt, homeScore.toInt,
       visitingHomeRuns.toInt, homeHomeRuns.toInt)
}
```

Spark Dataset is still an experimental API for now, so there are some strange issues I encountered while developing. Using a `groupBy` creates a `GroupedDataset` object. While that is conceptually what we want, `GroupedDataset`s don’t have a relatively simple `join` method like RDDs do.

Instead, we’ll iterate through each `Game` and return two `Result`s. A result has the key we want to `groupBy`, the number of homeruns that team had, and an `Int` signifying if the team won. By using an `Int` now, we can simply sum our wins together later.

```language-scala
/**
 * This function will parse the Result of the Game for each team
 * A result has the key as teamName+season,the homeruns for this game, and an Int signifying if the team won
 */
def calculateResults(game: Game) = {
  val visitorKey = game.visitingTeam + game.year
  val homeKey = game.homeTeam + game.year
  var visitingWin, homeWin = 0
  if(game.visitingScore > game.homeScore) visitingWin = 1
  if(game.homeScore > game.visitingScore) homeWin = 1
  Seq(Result(visitorKey, game.visitingHomeRuns, visitingWin),
      Result(homeKey, game.homeHomeRuns, homeWin))
}
```

```language-scala
// Grouped datasets don't have a relatively easy join method
// Instead, we will iterate through each "game" once and return two objects
// Each object contains:
// 1. teamName+year
// 2. Number of homeruns for that team
// 3. an Int signifying if that team accrued a win
val mappedByTeam = mappedFile.flatMap(calculateResults)
```

Now we need to get the total homeruns and the win percentage for each key. Consolidating our `Result`s is just a matter of sums and divisions.

```language-scala
case class Season(homeRuns: Double, games: Int, wins: Int, winningPercentage: Double)
/**
 * Takes all Results and collects them into one season
 */
def consolidate(results: Iterator[Result]) = {
  val list = results.toList
  val games = list.size
  val wins = list.map(_.win).sum
  val winningPercentage = wins.toDouble / games.toDouble
  Season(
    list.map(_.homeruns).sum,
    games,
    wins,
    winningPercentage)
}
```

```language-scala
// Group the data by each team per season
val joined = mappedByTeam.groupBy(_.key)

// Finally, we collect all the data per team per season
// into one object containing the information we need
// We don't need the key listed below, but mapGroups takes a function of (key,group)=>T
val mappedTo = joined.mapGroups((key, results) => consolidate(results))
```

Datasets do not have a statistics library yet. Instead, we will turn our dataset into its underlying Dataframe and use the `sql.DataFrameStatFunctions` package to calculate the Pearson coefficient.

```language-scala
// retrieve the values we care about and cast them back to RDDs
// since Statistics.corr requires RDD[Double]
val homeruns = mappedTo map(_.homeRuns) rdd
val winningPercentage = mappedTo map(_.winningPercentage) rdd

// This function takes a dataframe and two rows to return the correlation as a double
val correlation = mappedTo.toDF.stat.corr("homeRuns","winningPercentage","pearson")
println("**************Pearson coefficient for homeruns to winning percentage " + correlation)
```

Datasets can also be cast to an RDD if we had a hard requirement to use it as such for analysis.

## Conclusion

Once I converted Jeannine’s code from Java to Scala, changing it to use Datasets instead of RDDs took less than a day of work. The speed difference is clear even in this experimental version; the project running locally went from an average of 251 seconds to around 23.

The raw code is [available on GitHub](https://github.com/mal-virus/spark-blog-examples-scala).

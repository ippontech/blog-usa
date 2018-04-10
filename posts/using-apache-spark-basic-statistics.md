---
authors:
- Jeannine Stark
categories:
- Cloud
- Language
date: 2016-02-17T12:13:45.000Z
title: "Using Apache Spark to do Basic Statistics"
image: 
---

In a previous [blog](http://www.ipponusa.com/blog/how-to-get-started-with-spark-hdfs-and-cassandra/), examples were given about the basic API functions that the Apache Spark core JAR provides to users to be able to analyze large datasets. Apache Spark also provides more API functions in addition to reducing, mapping and other similar API functions in the machine learning library. This API provides some basic and advanced Statistical API calls to allow some more insights to be gained from the data. Information on the type of statistics that are provided in the MLlib library is located [here](http://spark.apache.org/docs/latest/mllib-guide.html). Note, there are now two libraries: one that is built on RDD and another built on DataFrames/DataSets which is a concept that was added in Spark 1.6. This blog is written using Spark 1.5 and RDD.

## Adding Spark MLlib Dependency

In order to use the Spark MLlib API functions, the following dependency needs to be added to the project. Below is the example for the dependency to add to the Maven pom.xml.


```xml
<dependency>
     <groupId>org.apache.spark</groupId>
     <artifactId>spark-mllib_2.10</artifactId>
     <version>1.5.0</version>
</dependency>
```

Maven Spark Mllib Dependency

## Determine a Problem to Solve

I decided to use baseball data to come up with an example that would use the Spark MLlib functions. Baseball data is a good starting point because it is easy to find, and there are lots of different data elements in one game and many games in each season. One question that can be asked is if there is any correlation between the amount of homeruns a team scores in a season, and the winning percentage for that season. A Pearson correlation can be run to determine if there is a positive, negative or no correlation between these two data elements. The Pearson coefficient will be between 1 and -1. A score of 1 means the two elements are positively correlated and a high number of homeruns leads to a high winning percentage. A score of -1 means the two are negatively correlated and a high number of homeruns means that the team would have a low winning percentage. And a score of 0 means the two are not correlated at all so homeruns are a poor indicator to a team winning percentage. This can be a complex mathematical equation since the standard deviation and covariance must be calculated so there are functions to calculate this value in many languages. Spark also provides an API function to allow this to be performed over a large set of data and then that outcome to be easily given to other applications or features unlike performing this correlation in a pure Statistical tool.

I used Retrosheet [game logs](http://www.retrosheet.org/gamelogs/index.html) because it provides information about every game played.  I downloaded the full game logs from 2010 to 2015 and converted them to CSV files but the information can still be processed as text files as well. For this example, I used HDFS to store the data because I did not need to create a schema. Since this data is structured, a database like Cassandra would actually be preferred over HDFS for a production application.

Each game log row consists of the different game. The row consists of the date of the game, day of the week, the visiting and home team, team league information, score, offensive, pitching and defensive statistics for example. To answer the question that I posed above the score of the game and the offensive statistics for homerun count are needed. Below is an example of one row in the game log file.

```
"20100404","0","Sun","NYA","AL",1,"BOS","AL",1,7,9,51,"N","","","","BOS07",37440,226,"020300200","01001331x",37,12,2,0,2,6,0,0,0,6,0,2,2,0,2,0,9,5,8,8,1,0,24,9,1,1,1,0,34,12,3,1,1,8,0,1,0,4,0,5,0,0,0,0,6,6,7,7,1,0,27,15,0,0,2,0,"westj901","Joe West","herna901","Angel Hernandez","schrp901","Paul Schrieber","drakr901","Rob Drake","","(none)","","(none)","giraj001","Joe Girardi","frant001","Terry Francona","okajh001","Hideki Okajima","parkc002","Chan Ho Park","papej001","Jonathan Papelbon","","(none)","sabac001","CC Sabathia","beckj002","Josh Beckett","jeted001","Derek Jeter",6,"johnn001","Nick Johnson",10,"teixm001","Mark Teixeira",3,"rodra001","Alex Rodriguez",5,"canor001","Robinson Cano",4,"posaj001","Jorge Posada",2,"granc001","Curtis Granderson",8,"swisn001","Nick Swisher",9,"gardb001","Brett Gardner",7,"ellsj001","Jacoby Ellsbury",7,"pedrd001","Dustin Pedroia",4,"martv001","Victor Martinez",2,"youkk001","Kevin Youkilis",3,"ortid001","David Ortiz",10,"belta001","Adrian Beltre",5,"drewj001","J.D. Drew",9,"camem001","Mike Cameron",8,"scutm001","Marco Scutaro",6,"","Y"
```

Game Log[The information used here was obtained free of charge from and is copyrighted by Retrosheet. Interested parties may contact Retrosheet at “[www.retrosheet.org](http://www.retrosheet.org)“.]

## Java Code

In the main method, the first aspect is to validate the input parameters if any are used to pass the input or output locations and to load the data from HDFS.

==Beginning of Main Method==
```java
public static void main(String[] args) {
if (args.length < 1) {
System.err.println("Please provide an input location");
System.exit(0);
}
SparkConf conf = new SparkConf().setAppName("Blog Example: Statistics and Correlation");

JavaSparkContext sc = new JavaSparkContext(conf);

// context.
JavaRDD<String> gameLogs = sc.textFile(args[0]);
```

The next step is to manipulate the data to get summary information for each season. The Retrosheet data is structured so each row represents one game.  Loading all the data from 2010 to 2015 at once means that the data needs to be grouped by season. Since the game log has the day the game was played, that can be used to determine the season. As the date is a string I chose to turn the game date into year only since the day the game is played is not necessary for the calculations in this example.
```java
// Break up the lines based on the comma delimiter.
JavaRDD<String[]> mappedFile = gameLogs.map(line -> line.split(",", -1));
// Parse each line to transform the date into only a year to be able to
// determine what game was in what season.
JavaRDD<String[]> parsedDate = mappedFile.map(HomerunWinStatisticsController::manipulateDate);
```
Mapping Data
```java
	/**
	 * Function that will manipulate the date so we can determine winning
	 * percentage and homeruns per team per season.
	 */
	private static String[] manipulateDate(String[] game) {
		// Thread local variable containing each thread's date format
	     final ThreadLocal<DateTimeFormatter> dtf =
	         new ThreadLocal<DateTimeFormatter>() {
	             @Override protected DateTimeFormatter initialValue() {
	                 return DateTimeFormat.forPattern("yyyymmdd");
	         }
	     };
	     DateTime gameDate = dtf.get().parseDateTime(game[0].replace("\"", ""));
	     String [] gameLog = game.clone();
	     gameLog[0] = String.valueOf(gameDate.getYear());

		return gameLog;
	}
```

Method to Manipulate DateThis mapping allows the data to be grouped by season. Since each game log has the information for two teams, the home team and the visiting team, two group bys need to be performed. One to get all the home games played by a team and the second to get all the away games played by a team in a season. Since the first element in the String array to be the year so we group each game log by the year and either the home team and the visiting team.

==Spark group by==
```java
// Group by the home team and year played in.
JavaPairRDD<String, Iterable<String[]>> mappedByVisitingTeam = parsedDate
.groupBy(line -> (line[3] + "," + line[0]));

// Group by the visiting team and year played in.
JavaPairRDD<String, Iterable<String[]>> mappedByHomeTeam = parsedDate
.groupBy(line -> (line[6] + "," + line[0]));
```

 Now that we have the two RDDs, we can join the two together by the key to get all the games played by a team in a year. The result of the join is the Scala implementation of a Tuple with the visiting team games as the first element and the home team games as the second element.

==Spark join==
```java
// Join the visiting team and home team RDD together to get a whole
// season of game logs for each team
JavaPairRDD<String, Tuple2<Iterable<String[]>, Iterable<String[]>>> joined = mappedByVisitingTeam
.join(mappedByHomeTeam);
```

The data is now structured in a way that we can manipulate the data to get the total number of homeruns a team had in a season, and the team’s winning percentage in that season. These summary statistics are needed to determine the correlation. We can again use the mapping function to calculate these values for each game and return a new RDD with only these values. Using doubles is important so we can get the actual percentage. Doubles will also be needed to do the correlation.

==Spark Map by team==
```java
// Using the map function to transform the data
JavaRDD<Double[]> mappedTo = joined.map(HomerunWinStatisticsController::calculateByTeam);
```

==Map Function for Calculate by Team==
```java
/**
	 * This function will parse out and calculate the total number of homeruns,
	 * games won and games played for a team in a single season. Once this is
	 * calculated the team, homerun total and winning percentage is returned.
	 * Tuple is made up Tuple2<teamKey, Tuple2<gamesAsVisitor, gamesAsHome>>
	 */
	private static Double[] calculateByTeam(Tuple2<String, Tuple2<Iterable<String[]>, Iterable<String[]>>> v1) {
		double teamHomeRuns = 0;
		double teamWins = 0;
		double gamesPlayed = 0;
		/**
		 * Grab and loop through the games played as a visitor. Parse out
		 * the number of homeruns, and determine if the game was a win or
		 * loss. Also increment the total games played.
		 */
		for(String [] visitingGames : v1._2._1) {
			double visitingTeamScore = Double.parseDouble(visitingGames[9]);
			double homeTeamScore = Double.parseDouble(visitingGames[10]);
			if (visitingTeamScore > homeTeamScore) {
				teamWins++;
			}
			teamHomeRuns += Integer.parseInt(visitingGames[25]);
			gamesPlayed++;
		}

		/**
		 * Grab and loop through the games played at home. Parse out the
		 * number of homeruns, and determine if the game was a win or loss.
		 * Also increment the total games played.
		 */
		for(String [] homeGames : v1._2._2) {
			double visitingTeamScore = Double.parseDouble(homeGames[9]);
			double homeTeamScore = Double.parseDouble(homeGames[10]);
			if (homeTeamScore > visitingTeamScore) {
				teamWins++;
			}
			teamHomeRuns += Integer.parseInt(homeGames[53]);
			gamesPlayed++;
		}

		Double[] gameInformation = { teamHomeRuns,
				teamWins / gamesPlayed };
		return gameInformation;
	}
```

The final step before being able to perform the correlation is to create two RDDs to pass to the correlation function by using the flatMapToDouble function.

==Flat map to double==
```java
JavaDoubleRDD homeruns = mappedTo.flatMapToDouble(HomerunWinStatisticsController::getHomerun);

JavaDoubleRDD winningPercentage = mappedTo.flatMapToDouble(HomerunWinStatisticsController::getWinningPercentage);
```

==Homerun Function==
```java
/**
	 * This function will parse out the homerun total for each team in a single
	 * season.
	 */
	private static Iterable<Double> getHomerun(Double [] t) {
		return Arrays.asList(t[0]);
	}
```

==Winning Percentage Function==
```java
/**
	 * This function will parse out the winning percentage for each team in a
	 * single season.
	 */
	private static Iterable<Double> getWinningPercentage(Double [] t) {
		return Arrays.asList(t[1]);
	}
```

These two RDDs can then be passed to the correlation API function. For this example, I printed the result to the console. This is not a best practice to use System.out but this result could be passed on to other applications that can either use the value to make decisions or to visualize the result. For the game logs from 2010 to 2015, the correlation was 0.3948095282394066 which is a weak correlation which shows that a large amount of homeruns does not highly correlate to a high winning percentage.

==Spark Correlation==
```java
Double correlation = Statistics.corr(JavaDoubleRDD.toRDD(homeruns), JavaDoubleRDD.toRDD(winningPercentage),"pearson");
// temporarily print to console

System.out.println("**************Pearson coefficiant for homeruns to winning percentage " + correlation);
```

There are also other built in basic statistics that, once the data is manipulated, can be performed to get further insights into homeruns and winning percentage. StatCounter provides an API to get the mean, standard deviation and variance without having to calculate each individually.

==Spark StatCounter==
```java
// List of the main statistics for homeruns;
StatCounter homerunStats = homeruns.stats();

// temporarily print out to console some example statistics that are
// included in StatCounter - see Javadocs for complete list
System.out.println("**************Mean of homeruns " + homerunStats.mean());

System.out.println("**************Standard deviation of homeruns " + homerunStats.stdev());

System.out.println("**************Variance of homeruns " + homerunStats.variance());
// List of the main statistics for winning percentage.
StatCounter winningPercentageStats = winningPercentage.stats();

// temporarily print out to console some example statistics that are
// included in StatCounter - see Javadocs for complete list
System.out.println("**************Mean of winning percentage " + winningPercentageStats.mean());

System.out.println("**************Standard deviation of winning percentage " + winningPercentageStats.stdev());

System.out.println("**************Variance of winning percentage " + winningPercentageStats.variance());
```

Overall the Spark MLLib was incredibly helpful in this case so that calculating these types of statistics are easy to perform over a large amount of data. There are other statistical tools but this allows ease of development with the power that Spark provides to analyze the large data set. As this example shows it is easy to mix the traditional API functions of Spark with the MLLib library which opens up more use case possibilities that need a mix of traditional analytics and basic statistics.

You can find the raw code on [GitHub](https://github.com/jetondreau/spark-blog-examples).

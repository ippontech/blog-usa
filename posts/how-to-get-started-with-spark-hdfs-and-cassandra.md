---
authors:
- Jeannine Stark
tags:
date: 2015-10-15T10:15:25.000Z
title: "How To Get Started with Spark, HDFS and Cassandra"
image: 
---

<span style="font-weight: 400;">Sometimes, getting started with a new technology can be overwhelming because of the volume of information out there. I am currently getting started on Apache Spark, and so I have consolidated all of the best resources which I used and included my own examples using Spark, HDFS and Cassandra. </span>

#### **Getting Started**

<span style="font-weight: 400;">Once Spark is downloaded and setup, walk through the Quick Start Guide located on the [Spark website](http://spark.apache.org/docs/latest/quick-start.html).</span><span style="font-weight: 400;"><span style="font-weight: 400;">In addition to this quick start guide there is a great supplemental chapter in Learning Spark by </span><span style="font-weight: 400;">Holden Karau; Andy Konwinski; Patrick Wendell; Matei Zaharia. That chapter provides more details about the Quick Start Guide to be used when walking through the examples, as well as extra information about what Spark is doing during the example. One point of interest is the connection between the examples in the Scala/Python Shell and the Spark Jobs console; The Spark Job Console shows that every command run within the shell kicks off a Spark Job in the Console. Seeing this visual connection can help demonstrate what the flow of an analytic should be. However, there is a lot of other useful information in this chapter.</span></span>

[![Spark](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/12/Spark-300x98.jpg)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/12/Spark.jpg)

#### **Java 7 versus Java 8 with Spark**

<span style="font-weight: 400;">There are many changes between Java 7 and Java 8 but the biggest impact to Spark is the introduction of lambda expressions. This makes many Spark expressions in Java 8 much simpler. A lot of people seem to choose to use Scala when writing Spark applications, but Java’s lambda expressions have made coding in Java a lot better.  A lot of the examples that I found are still using Java 7, so if  your code is currently using Java 8 or needs to be upgraded from Java 7 to Java 8, [this blog](http://blog.cloudera.com/blog/2014/04/making-apache-spark-easier-to-use-in-java-with-java-8/) has the best examples that I have found of implementing Spark in that environment; It walks through examples in Java 7 and Java 8 to explain the changes that need to be made.</span>

#### **Building Your First “Hello World” Application with Data Storage**

<span style="font-weight: 400;">Most sites out there have a Word Count example that is used as a “Hello World” type project and read from a flat file that is stored in the same location as the Spark installation. In addition to the Apache Spark Quick Start example, there is Roberto Marchetto’s “Hello World” example found [here](http://www.robertomarchetto.com/spark_java_maven_example), which does a great job of walking through the entire Spark setup, with code! </span>

#### **Getting Started with HDFS and Spark**

<span style="font-weight: 400;">In production environments and actual applications for Spark, the data will not simply be stored on the file system but rather one of the Big Data Storage Solutions. HDFS is a storage system with  many advantages for storing data. Since it can store a large amount of both flat files and structured files, it can be used for storing all types of data sources. Hadoop MapReduce isn’t the only type of analytics that can be run over HDFS; Spark analytics can also be run. Spark is a very powerful tool to run analytics over HDFS because of its speed advantage; it stores data in memory while Hadoop MapReduce stores the data on disk.  There are also other ways that each treats the data that create significant differences which for many use cases make Spark a better fit than MapReduce.</span>

<span style="font-weight: 400;">A simple way to get started with HDFS is to use the same flat file from the Word Count examples and place the file in HDFS.</span>

`hdfs dfs -put <local file> <hdfs file location>`

Once the file is placed in HDFS, the local url can be replaced by the HDFS url.

`hdfs://<local path>:<port>/<path to hdfs file>`

I decided to take the “Word Count” example a bit further for my application and try to analyze an actual data set. One of the best free data sets that I found to use is located at [Retrosheet](http://www.retrosheet.org/schedule/index.html).

<span style="font-weight: 400;">[The information used here was obtained free of </span><span style="font-weight: 400;">charge from and is copyrighted by Retrosheet.  Interested </span><span style="font-weight: 400;">parties may contact Retrosheet at “</span>[<span style="font-weight: 400;">www.retrosheet.org</span>](http://www.retrosheet.org)<span style="font-weight: 400;">“.]</span>

<span style="font-weight: 400;">The website has many years of baseball data, from schedules to game data. The files can be downloaded as text files but most are comma delimited and maintain a certain structure. For the example below, I downloaded the 2013, 2014 and 2015 baseball schedules. Suppose for my use case I want to get all games played by the Boston Red Sox at home and a second list of the day games played by the Red Sox at another stadium. For this particular example, I saved the data as csv files and loaded it directly into HDFS using the put command. I wrote the example using Java 8 since the code is much more legible.</span>

```language-java
public class BaseballDataController {

	// Function to transform string array into a comma seperated String
	private static final Function<String[], String> APPEND_GAME = new Function<String[], String>() {

		private static final long serialVersionUID = -6442311685154704731L;

		@Override
		public String call(String[] game) throws Exception {
			StringJoiner gameString = new StringJoiner(",");
				for (int i = 0; i < game.length; i++) {
					gameString.add(game[i]);
				}

			return gameString.toString();
		}

	};

	public static void main(String[] args) {
		if (args.length < 3) {
			System.err.println("Please provide an input output and team as arguments");
			System.exit(0);
		}

		SparkConf conf = new SparkConf().setAppName("Boston Red Sox Scheduled Day Games");
		JavaSparkContext sc = new JavaSparkContext(conf);
		// context.
		JavaRDD<String> schedules = sc.textFile(args[0]);
		// Break up the lines based on the comma delimiter.
		JavaRDD<String[]> mappedFile = schedules.map(line -> line.split(",", -1));

		// Filter out away games played by the team being examined.
		JavaRDD<String[]> awayGames = mappedFile.filter(line -> line[3].equals(args[2]) && line[9].equals("d"));

		//Map array back to a String
		JavaRDD<String> mappedAwayGames = awayGames.map(APPEND_GAME);

		// Filter out home games played by the team being examined.
		JavaRDD<String[]> homeGames = mappedFile.filter(line -> line[6].equals(args[2]) && line[9].equals("d"));

		//Map array back to a String
		JavaRDD<String> mappedHomeGames = homeGames.map(APPEND_GAME);

		// Save back to HDFS
		mappedAwayGames.saveAsTextFile(args[1] + "/awayGames");

		// Save back to HDFS
		mappedHomeGames.saveAsTextFile(args[1] + "/homeGames");

		sc.close();
	}

}
```

To run the above code in a new Spark environment:

`./spark-submit --class com.jetondreau.blog.spark_blog_examples.controller.BaseballDataController --master local[2] /<path to jar> hdfs://<server name>:<port>/<file location> hdfs://<server name>:<port>/<folder path> <team code eg. BOS>`

**NOTE**: If Spark is already running replace local[2] with the master URL to run on the master server.

There are two ways to see the output in HDFS.

`hdfs dfs -ls <output directory path>`

This will show the list of all the output files which can then be moved and inspected on the local file system using:

`hdfs dfs -get <path to hdfs file> <local folder>`

The data will be stored in files titled part-0000x. HDFS also has a browse directory feature that can be accessed through the browser so any file can be downloaded directly. This can be accessed at [http://localhost:50070/explorer.html#/](http://localhost:50070/explorer.html#/).

<span style="font-weight: 400;">I found this data set to be a great foundation for learning Spark and HDFS because there are many different use cases that can be applied. Through these use cases I could dig into the Spark API and understand how and when to use the different commands.</span>

#### **Getting Started with Cassandra and Spark**

<span style="font-weight: 400;">Another data storage solution that is popular with Spark analytics is Apache Cassandra. Cassandra is a structured data storage compared to the HDFS. Since the data used in the example above was comma separated the data can be loaded into Cassandra and perform the same type of operations without the need to transform the String Array into a String to be stored. I used Cassandra 2.2.1 locally to rewrite the above example. If you are using a different version of Cassandra there may be some discrepancies with the api calls as some were deprecated between Cassandra 1.x and 2.x.</span>

##### **Maven Setup**

<span style="font-weight: 400;">If Maven is being used for dependency management, the following two dependencies need to be added in addition to those that were necessary for the basic Spark example.</span>

```language-xml
<dependency>
	<groupId>com.datastax.spark</groupId>
	<artifactId>spark-cassandra-connector_2.10</artifactId>
	<version>1.4.0</version>
</dependency>
<dependency>
	<groupId>com.datastax.spark</groupId>
	<artifactId>spark-cassandra-connector-java_2.10</artifactId>
	<version>1.4.0</version>
</dependency>
```

 The jar that is run on Spark also needs to include these dependencies so an “uber jar” needs to run. An “uber jar” will package up the dependencies similar to how jars are packaged up in a war. The Maven Shade Plugin is for this exact purpose. By default the plugin will include all jars, if all the jars are not required the plugin allows for dependencies to either be excluded or included. I choose to include only the Spark Cassandra dependencies that are necessary so there are no conflicting dependencies between the application and Sparks internal dependencies.
```language-xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-shade-plugin</artifactId>
	<version>2.4.1</version>
	<executions>
		<execution>
			<phase>package</phase>
			<goals>
				<goal>shade</goal>
			</goals>
			<configuration>
				<artifactSet>
					<includes>
						<include>com.datastax.spark:spark-cassandra-connector-java_2.10</include>
						<include>com.datastax.spark:spark-cassandra-connector_2.10</include>
						<include>org.apache.cassandra:cassandra-clientutil</include>
						<include>com.datastax.cassandra:cassandra-driver-core</include>
						<include>com.google.guava:guava</include>
						<include>org.joda:joda-convert</include>
						<include>joda-time:joda-time</include>
						<include>com.twitter:jsr166e</include>
						<include>org.scala-lang:scala-reflect</include>
					</includes>
				</artifactSet>
			</configuration>
		</execution>
	</executions>
</plugin>
```

##### **Cassandra Set Up**

Once Cassandra is up and running, data from csv files can be loaded into already created tables and an already created keyspace.  Below is the command that I used to create the keyspace and the table using the cql command shell. I used SimpleStrategy for the keyspace and replication factor 1 but the Cassandra documentation has other classes that may work best for other types of projects.

```language-SQL
CREATE KEYSPACE IF NOT EXISTS baseball_examples WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1} AND DURABLE_WRITES = true;
CREATE TABLE GAME_SCHEDULE (GAME_DATE text, GAME_NUMBER text, DAY_OF_WEEK text, VISITING_TEAM text, VISITING_LEAGUE text, VISITOR_GAME_NUM text, HOME_TEAM text, HOME_LEAGUE text, HOME_GAME_NUM text, TIME_OF_DAY text, POSTPONED text, MAKEUP_DATE text, PRIMARY KEY (GAME_DATE, VISITING_TEAM, HOME_TEAM));
```

Tables also need to be created for the output data to be stored in. I chose to keep the same structure as this original table and create two output tables, one for the visiting games and the other for the home games, but the code could easily be rewritten to store both sets of output to the same table.

```language-SQL
CREATE TABLE AWAY_DAY_GAMES (GAME_DATE text, GAME_NUMBER text, DAY_OF_WEEK text, VISITING_TEAM text, VISITING_LEAGUE text, VISITOR_GAME_NUM text, HOME_TEAM text, HOME_LEAGUE text, HOME_GAME_NUM text, TIME_OF_DAY text, POSTPONED text, MAKEUP_DATE text, PRIMARY KEY (GAME_DATE, VISITING_TEAM, HOME_TEAM));
CREATE TABLE HOME_DAY_GAMES (GAME_DATE text, GAME_NUMBER text, DAY_OF_WEEK text, VISITING_TEAM text, VISITING_LEAGUE text, VISITOR_GAME_NUM text, HOME_TEAM text, HOME_LEAGUE text, HOME_GAME_NUM text, TIME_OF_DAY text, POSTPONED text, MAKEUP_DATE text, PRIMARY KEY (GAME_DATE, VISITING_TEAM, HOME_TEAM));
```

The final step is to store the csv into the newly created Cassandra table. If the data has a header, adding WITH HEADER = TRUE to the end of the command will tell Cassandra to skip the first row when loading the data. The most important part of this command is that the order of the columns must match the order the data is found in the csv file.

`COPY GAME_SCHEDULE (GAME_DATE, GAME_NUMBER, DAY_OF_WEEK, VISITING_TEAM, VISITING_LEAGUE, VISITOR_GAME_NUM, HOME_TEAM, HOME_LEAGUE, HOME_GAME_NUM, TIME_OF_DAY, POSTPONED, MAKEUP_DATE) FROM '<path to file>';`

##### **Spark Code**

Now that the data is loaded into Cassandra the code can be rewritten to read and store to Cassandra. Since Cassandra is structured in comparison to HDFS, the code that split the lines apart based on the comma delimiter is unnecessary. In addition the function that transforms the array into a comma delimited string is unnecessary. However, backing model objects that correspond to the structure from the Database will need to be written. In this backing object the order of the columns in the columns method needs to match the exact order they are in the Cassandra Table.

```language-java
public class GameSchedule implements Serializable {

	private String gameDate;
	private String gameNumber;
	private String dayOfWeek;
	private String visitingTeam;
	private String visitingLeague;
	private String visitorGameNum;
	private String homeTeam;
	private String homeLeague;
	private String homeGameNum;
	private String timeOfDay;
	private String postponed;
	private String makeupDate;

	public GameSchedule(final String gameDate, final String visitingTeam, final String homeTeam, final String dayOfWeek,
			final String gameNumber, final String homeGameNum, final String homeLeague, final String makeupDate,
			final String postponed, final String timeOfDay, final String visitingLeague, final String visitorGameNum) {
		this.gameDate = gameDate;
		this.gameNumber = gameNumber;
		this.dayOfWeek = dayOfWeek;
		this.visitingTeam = visitingTeam;
		this.visitingLeague = visitingLeague;
		this.visitorGameNum = visitorGameNum;
		this.homeTeam = homeTeam;
		this.homeLeague = homeLeague;
		this.homeGameNum = homeGameNum;
		this.timeOfDay = timeOfDay;
		this.postponed = postponed;
		this.makeupDate = makeupDate;
	}

	public String getGameDate() {
		return gameDate;
	}

	//The columns in order from the database
	public static List<String> columns() {
		List<String> columns = new ArrayList<String>();
		columns.add("game_date");
		columns.add("visiting_team");
		columns.add("home_team");
		columns.add("day_of_week");
		columns.add("game_number");
		columns.add("home_game_num");
		columns.add("home_league");
		columns.add("makeup_date");
		columns.add("postponed");
		columns.add("time_of_day");
		columns.add("visiting_league");
		columns.add("visitor_game_num");
		return columns;
	}

}
```

Now that the setup has been completed the main controller can be rewritten. The main method is much simpler for this particular example. The code boils down to just a read, filter and a write.

```language-java
public class BaseballDataControllerCassandra {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Please provide an input output and team as arguments");
			System.exit(0);
		}

		SparkConf conf = new SparkConf().setAppName("Boston Red Sox Scheduled Day Games");
		conf.set("spark.cassandra.connection.host", args[0]);

		JavaSparkContext sc = new JavaSparkContext(conf);

		CassandraTableScanJavaRDD<GameSchedule> schedules = CassandraJavaUtil.javaFunctions(sc)
				.cassandraTable("baseball_examples", "game_schedule", mapRowTo(GameSchedule.class));

		// Filter out away games played by the team being examined.
		JavaRDD<GameSchedule> awayGames = schedules.filter(gameSchedule -> gameSchedule.getVisitingTeam().equals(args[1]) && gameSchedule.getTimeOfDay().equals("d"));

		// Filter out home games played by the team being examined.
		JavaRDD<GameSchedule> homeGames = schedules.filter(gameSchedule -> gameSchedule.getHomeTeam().equals(args[1]) && gameSchedule.getTimeOfDay().equals("d"));

		// Save back to Cassandra
		javaFunctions(awayGames).writerBuilder("baseball_examples", "away_day_games", mapToRow(GameSchedule.class)).saveToCassandra();

		// Save back to Cassandra
		javaFunctions(homeGames).writerBuilder("baseball_examples", "home_day_games", mapToRow(GameSchedule.class)).saveToCassandra();

		sc.close();
	}

}
```

To run the example some changes also need to be made to the spark submit based on these changes.

`./spark-submit --class com.jetondreau.blog.spark_blog_examples.controller.BaseballDataControllerCassandra --master local[2] /<path to jar> <url for cassandra instance> <team code eg. BOS>`

Now that the job has been run the new tables can be queried using the cql shell and the subset of data can be examined. You can find the raw code on [GitHub](https://github.com/jetondreau/spark-blog-examples). Happy Coding.

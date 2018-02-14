---
authors:
- Raphael Brugier
categories:
- Apache Spark
- Databricks
- streaming
- Big Data
date: 2016-04-13T10:32:21.000Z
title: "A tour of Databricks Community Edition: a hosted Spark service"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/01/databricks_logoTM_rgb_TM.svg
---

With the [recent announcement](https://databricks.com/blog/2016/02/17/introducing-databricks-community-edition-apache-spark-for-all.html) of the Community Edition, it’s time to have a look at the Databricks Cloud solution. Databricks Cloud is a hosted Spark service from Databricks, the team behind Spark.

Databricks Cloud offers many features:

- A cluster management service. The [service](https://docs.cloud.databricks.com/docs/latest/databricks_guide/02%20Product%20Overview/01%20Clusters.html) will spin up Amazon EC2 instances with Spark nodes already set up for you. Free 6GB memory cluster for the Community Edition and billed hourly per node for the regular version. The price will depend on the size of the instances and you can even mix on-demand and spot instances.
- A notebook, to write Spark code either in Scala, Python or R, with version control and user role management.
- A scheduling service to turn notebooks or fat JARs into managed jobs. The service also allows to manage streaming jobs and have failure notifications, as well as auto restart capabilities.
- And [more recently](https://databricks.com/blog/2016/02/17/introducing-databricks-dashboards.html), a dashboarding service to turn your notebooks snippets into custom dashboard components.

The notebook is where you will spend most of your time. It offers a fully interactive Spark environment, with the capabilities to add any dependencies from the Maven Central repository or to upload your own JARs to the cluster.

Notebooks have been used for years for data exploration, but with the rise of Data Science, there has been a lot of traction for tools such as [Jupyter](http://jupyter.org/), [Spark notebook](http://spark-notebook.io/), or [Apache Zeppelin](https://zeppelin.incubator.apache.org/).

Jupyter is an historical Python notebook (formerly known as IPython) that have been added a Spark extension, you could use Python of course, but also R and Scala. It’s more mature than Zeppelin and Jupyter notebooks are even integrated in [GitHub](https://github.com/blog/1995-github-jupyter-notebooks-3). But it would requires some extra configuration to get Scala and Spark support.

Spark notebook was one of the first notebook to appear for Spark. It is limited to the Scala language, so it might not be the best choice if you have data analysts working primarily with Python.

Zeppelin is still an incubating project from the Apache Foundation but it has received a lot of traction lately and it is promising. Compared to Databricks Cloud’s built-in notebook, Zeppelin is not dedicated to Spark but supports many more technologies via various connectors such as Cassandra or Flink. You will of course have to manage the deployment and configuration by yourself, but with the main benefit of having a fined-grained control over the infrastructure. While the Community Edition of Databricks Cloud involves some restrictions – smaller Amazon EC2 instances and no access to the scheduling component – it is still a great tool to get started with Spark, especially for learning and fast prototyping.

To complete this introduction, let’s write an example of a Twitter stream processing and some visualizations.

In this example, we’ll subscribe to the Twitter stream API which delivers roughly a 1% sample of all the tweets published in realtime. We’ll use Spark Streaming to process the stream and identify the language and country of each tweet.  
 We will store a sliding window of the results as a table and display the results as built-in visualizations in the notebook.


## Step 0: Community Edition access

You first need to [subscribe](http://go.databricks.com/databricks-community-edition-beta-waitlist) to Databricks Community Edition. This is still a private beta version but you should receive your invitation within one week.

Once you have the Databricks Cloud, [import my notebook](https://databricks-prod-cloudfront.cloud.databricks.com/public/4027ec902e239c93eaaa8714f173bcfc/7631468844903857/1641217975453210/8780643444584178/latest.html). This notebook is a partial reuse of the Databricks [Twitter hash count](https://docs.cloud.databricks.com/docs/latest/databricks_guide/index.html#08%20Spark%20Streaming/03%20Twitter%20Hashtag%20Count%20-%20Scala.html) example.


## Step 1: prerequisite libraries and imports

The example uses the Apache Tika library for the language recognition of the tweets.  
 To attach the dependency to your Spark cluster, follow these steps:

- In the workspace, in your user space, open the “Create” dialog box and choose “library”
- Choose “maven coordinate” as a source
- Use “org.apache.tika:tika-core:1.12” as the Coordinate
- Make sure the “Attach automatically to all clusters.” box is checked in the library details of your workspace.
- The library and its dependencies will now be deployed to the cluster nodes.
- To verify this, you can access the “Cluster” tab and see “1 library loaded” in the your cluster.

[![Databricks cluster view](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/03/clusterView-1024x487.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/03/clusterView.png)


## Step 2: Twitter credentials

Because this example requires a connection to the Twitter stream API, you should create a Twitter application and acquire an OAuth token.

- Go to [https://apps.twitter.com/](https://apps.twitter.com/) and follow the steps to create your Twitter application.
- You should then answer Step 2 questions to enter your credentials.
- These credentials will then be automatically picked by the Twitter4j library and the Spark Streaming wrapper to create a Twitter stream.

[![twitterCredentials](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/03/twitterCredentials-1024x354.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/03/twitterCredentials.png)


## Step 3: Run the Twitter streaming job

Execute step 3’s code in the notebook, so as to create a StreamingContext and run it in the cluster.

The code will initialize the Twitter stream, and for each tweet received, it will:

- Transform the country code from a two-letter code to a three-letter code. This is because the Databricks notebook requires an ISO 3166-1 alpha-3 code for the country.
- Detect the language of the tweet using the Tika library. Because tweets are small portions of text containing hashtags, usernames, etc; the detection could unfortunately be inaccurate.
- Wrap the result in a Tweet case class.

The output of the stream, a sliding window of the last 30 seconds tweets, is then written to a temporary “SQL” table, to be queryable.

```language-scala
case class Tweet(user: String, text: String, countryCode: String, language: String)

// Initialize the language identifier library
LanguageIdentifier.initProfiles()

// Initialize a map to convert Countries from 2 chars iso encoding to 3 characters
val iso2toIso3Map: Map[String, String] = Locale.getISOCountries()
  .map(iso2 => iso2 -> new Locale("", iso2).getISO3Country)
  .toMap

// detect a language from a text content using the Apache Tika library
def detectLanguage(text: String): String = {
    new LanguageIdentifier(text).getLanguage
}

// This is the function that creates the SteamingContext and sets up the Spark Streaming job.
def creatingFunc(): StreamingContext = {
  // Create a Spark Streaming Context.
  val slideInterval = Seconds(1)
  val ssc = new StreamingContext(sc, slideInterval)
  ssc.remember(Duration(100))
  // Create a Twitter Stream for the input source. 
  val auth = Some(new OAuthAuthorization(new ConfigurationBuilder().build()))

  val twitterStream = TwitterUtils.createStream(ssc, auth)
          .filter(t=> t.getPlace != null)
          .map(t => Tweet(t.getUser.getName, t.getText, iso2toIso3Map.getOrElse(t.getPlace.getCountryCode, ""), detectLanguage(t.getText)))
                .window(windowDuration = Seconds(30), slideDuration = Seconds(10))
            .foreachRDD { rdd => 
              val sqlContext = SQLContext.getOrCreate(SparkContext.getOrCreate())
                  // this is used to implicitly convert an RDD to a DataFrame.
                import sqlContext.implicits._
                rdd.toDF().registerTempTable("tweets")
            }
  ssc
}
```

Create StreamingContext Scala function
## Step 4: Visualizations

Now, tweets are automatically stored and updated from the sliding window and we can query the table and use the notebook’s built-in visualizations.

<span style="text-decoration: underline;">Tweets by country:</span>

[![tweetsByCountry](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/03/tweetsByCountry-1024x459.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/03/tweetsByCountry.png)

<span style="text-decoration: underline;">Tweets by language:</span>

[![tweetsByLanguage](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/03/tweetsByLanguage-1024x494.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/03/tweetsByLanguage.png)

We can run virtually any SQL query on the last 30 seconds of the 1% sample of tweets emitted from all-over the world!

Even if the visualizations can be exported to a dashboard, they still need to be refreshed manually. This is because you cannot create Spark jobs in the community edition. However, the non-Community version allows [to turn this notebook into an actual Spark Streaming job](https://community.cloud.databricks.com/?o=7631468844903857#externalnotebook/https%3A%2F%2Fdocs.cloud.databricks.com%2Fdocs%2Flatest%2Fdatabricks_guide%2Findex.html%2302%2520Product%2520Overview%2F06%2520Jobs.html) running indefinitely while refreshing a dashboard of visualizations.


## Conclusion

Databricks Community Edition offers a nice subset of Databricks Cloud for free. It is a nice playground to start with Spark and notebooks. It also offers the integration of the very complete [Introduction to Big Data with Apache Spark](https://community.cloud.databricks.com/?o=7631468844903857#externalnotebook/https%3A%2F%2Fdocs.cloud.databricks.com%2Fdocs%2Flatest%2Fcourses%2Findex.html%23Introduction%2520to%2520Big%2520Data%2520with%2520Apache%2520Spark%2520(CS100-1x)%2FIntroduction%2520(README).html) course taught by Berkeley University.

Besides this, before jumping to the professional edition, you will have to consider the tradeoffs between an all-in-one service like Databricks Cloud – that can become pricey for long running jobs – versus managed clusters ([Amazon EMR](https://aws.amazon.com/elasticmapreduce/), [Google Dataproc](https://cloud.google.com/dataproc/), …) or in-house hosting with fine grained control of the infrastructure of the nodes but with additional maintenance costs.

See the notebook in action in [Databricks cloud](https://databricks-prod-cloudfront.cloud.databricks.com/public/4027ec902e239c93eaaa8714f173bcfc/7631468844903857/1641217975453210/8780643444584178/latest.html).

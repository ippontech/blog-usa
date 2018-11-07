---
authors:
- Theo Lebrun
tags:
- Apache Kafka
- JHipster
- Grafana
- InfluxDB
date: 2018-11-07T12:00:00.000Z
title: "NYC subway data with Kafka Streams and JHipster (part 2 of 2)"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/11/mta-kafka-logo.png
---

In the [part 1](https://blog.ippon.tech/nyc-subway-data-with-kafka-streams-and-jhipster-part-1/) of this blog post I explained how to retrieve data from the MTA feed and publish them to Kafka. I will now explain how to easily visualize those data using [InfluxDB](https://www.influxdata.com/) and [Grafana](https://grafana.com/). InfluxDB is an open-source time series database that can be used as a data source for Grafana. Grafana is an open-source, general purpose dashboard and graph composer, which runs as a web application. Grafana will be used to visualize the number of active subways for each lines through a day and see how this number changes with time.

All the code of this blog post can be found on this [GitHub repository](https://github.com/Falydoor/mta-kafka) and I recommend cloning it instead of copying the code examples.

# Saving measurements to InfluxDB

## InfluxDB setup with the microservice

Starting an InfluxDB instance can be achieve using the below Docker command:

```docker run -p 8086:8086 -v $PWD:/var/lib/influxdb influxdb```

A database is required to save the measurements, here is the cURL command to create a database named `mta`:

```curl -G http://localhost:8086/query --data-urlencode "q=CREATE DATABASE mta"```

The microservice can now connect to the instance and starts saving measurements in the `mta` database using the Java driver. In order to use the Java driver, the below Maven dependency must be added first:

```xml
<dependency>
    <groupId>org.influxdb</groupId>
    <artifactId>influxdb-java</artifactId>
    <version>2.14</version>
</dependency>
```

Configuring the java client is very simple and the interface `InfluxDB` will be later used to query the database.

```java
// Init influxDB
InfluxDB influxDB = InfluxDBFactory.connect("http://127.0.0.1:8086", "root", "root");
influxDB.setDatabase("mta");
```

## Measurements saving

In the [part 1](https://blog.ippon.tech/nyc-subway-data-with-kafka-streams-and-jhipster-part-1/), the Kafka topic `mta-stream` was used to store the results from the streaming process. This topic will be used to save each messages in InfluxDB since a message represents the number of active subways for a given line and time.

```java
@StreamListener(MessagingConfiguration.MtaStream.INPUT)
public void saveSubwayCount(SubwayCount subwayCount) {
    // Save measurement in influxdb
    influxDB.write(Point.measurement("line")
        .time(subwayCount.getStart().toEpochMilli(), TimeUnit.MILLISECONDS)
        .tag("route", subwayCount.getRoute())
        .addField("count", subwayCount.getCount())
        .build());
}
```

The method above get triggered when a message is published in the topic `mta-stream` and will then write the message in InfluxDB. Spring Cloud Stream makes things simple by converting the Kafka message from a JSON format to the custom `SubwayCount` class.

InfluxDB's measurement is conceptually similar to a table and all messages are inserted in the `line` measurement. The subway's route is used as a tag to allow filtering when it will be used in Grafana. There is only one field for the point and it is the number of active subways. Finally the time of the point is the start timestamp of the Kafka's window.

# Grafana configuration and dashboard creation

Once again, thanks to Docker for making the Grafana setup easy:

```docker run -d -p 3000:3000 grafana/grafana```

A data source for InfluxDB must be created in order to build a dashboard, here is a screenshot of my configuration:

![mta-kafka-datasource](https://raw.githubusercontent.com/Falydoor/blog-usa/mta-kafka-2/images/2018/11/mta-kafka-datasource.png)

The ip must be the one of the InfluxDB's container, it can be retrieved using the command below:

```docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' container_name_or_id```

Any dashboard can be created with the datasource, I decided to have one with a panel that will display the number of subways per line.

![mta-kafka-2018-11-05](https://raw.githubusercontent.com/Falydoor/blog-usa/mta-kafka-2/images/2018/11/mta-kafka-2018-11-05.png)

Lines are grouped to avoid having too many series and the value is the average number of running trains for a 30mins interval. The above screenshot shows the busiest lines on Monday November 5 in 2018 which is a regular work day.

This [JSON configuration](https://raw.githubusercontent.com/Falydoor/mta-kafka/master/dashboard.json) contains a dashboard with the panel from the screenshot and it can be easily imported in a different Grafana instance.

# Conclusion

From the screenshot, we can see that the two times where the number of active subways reach its peak are 9am and 5pm. The time of the day where the number is the lowest is between midnight and 5am. The red line (or 1-2-3) is the one with the most running trains in average during a day which makes sense since it is one of the longest line.

Using JHipster with Kafka and Spring Cloud Stream is pretty straightforward and the integration with the MTA API was easy to do. Setting up InfluxDB/Grafana with Docker takes few minutes and it gives you a nice way to visualize the data. The next step would be to differentiate the direction for each lines because there is probably a small difference between two directions of the same line.

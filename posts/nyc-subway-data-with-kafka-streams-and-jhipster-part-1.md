---
authors:
- Theo Lebrun
tags:
- Apache Kafka
- JHipster
- Spring Cloud Stream
date: 2018-11-05T12:00:00.000Z
title: "NYC subway data with Kafka Streams and JHipster (part 1 of 2)"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/11/mta-kafka-logo.png
---

The NYC subway network is pretty big and with its 468 stations and 27 lines it is the world largest subway system. New Yorkers complain a lot about delays caused by signal problems, schedule changes or super packed cars. As an engineer and curious NYC resident, I've always wanted to know statistics about the subway like the number of running trains and which line is the busiest.

In this blog post, I will explain how the [MTA Real-Time Data Feeds](http://datamine.mta.info/) can be used to retrieve statistics about each subways lines. I will be using [Apache Kafka](https://kafka.apache.org/) (Kafka Streams more precisely) and [JHipster](http://www.jhipster.tech/) to achieve that. The second part of this blog post will explain an easy way to visualize the data using [Grafana](https://grafana.com/).

All the code of this blog post can be found on this [GitHub repository](https://github.com/Falydoor/mta-kafka) and I recommend cloning it instead of copying the code examples.

# Getting started with the MTA Real-Time Data Feeds

To use the MTA API, you first have to register [here](https://datamine.mta.info/user/register) to receive a key that will allow you to call the API. The API uses Google's mechanism for serializing structured data called [Protocol buffers](https://developers.google.com/protocol-buffers/) instead of a regular JSON format. More information about the structure definition can be found on the [GTFS Realtime Reference page](https://developers.google.com/transit/gtfs-realtime/reference/).

The drawback of Protocol buffers is that you can't use a simple cURL command to visualize the data. You need to generate data access classes using the structure definition and then parse the API's response using a popular language (C++, C#, Go, Java or Python). The compiler can be downloaded [here](https://developers.google.com/protocol-buffers/docs/downloads), if you're on macOS you can run this [Homebrew](https://brew.sh/) command `brew install protobuf`.

# Microservice generation and MTA feed polling

## Microservice generation using JHipster

I will be using [JHipster](http://www.jhipster.tech/) to generate a standard Spring Boot microservice configured with Kafka. If you never used JHipster, I suggest you to take a look at the [video tutorial](https://www.jhipster.tech/video-tutorial/). The microservice will use Kafka as a message broker and will have no database configured.

Here the `.yo-rc.json` in case you want to generate you own microservice:
```json
{
  "generator-jhipster": {
    "promptValues": {
      "packageName": "io.github.falydoor.mtakafka.producer"
    },
    "jhipsterVersion": "5.5.0",
    "applicationType": "microservice",
    "baseName": "producer",
    "packageName": "io.github.falydoor.mtakafka.producer",
    "packageFolder": "io/github/falydoor/mtakafka/producer",
    "serverPort": "8081",
    "authenticationType": "jwt",
    "cacheProvider": "hazelcast",
    "enableHibernateCache": false,
    "websocket": false,
    "databaseType": "no",
    "devDatabaseType": "no",
    "prodDatabaseType": "no",
    "searchEngine": false,
    "messageBroker": "kafka",
    "serviceDiscoveryType": "eureka",
    "buildTool": "maven",
    "enableSwaggerCodegen": false,
    "jwtSecretKey": "",
    "enableTranslation": false,
    "testFrameworks": [],
    "jhiPrefix": "jhi",
    "clientPackageManager": "npm",
    "skipClient": true,
    "skipUserManagement": true
  }
}
```

The next step is to generate the Protobuf's classes using the command `protoc` and the [GTFS definition](https://developers.google.com/transit/gtfs-realtime/gtfs-realtime.proto). Save the definition in the resources folder and then run the below command:

```protoc --java_out=src/main/java/ src/main/resources/gtfs-realtime.proto```

You should now have a java class named `GtfsRealtime` that will be used to parse the API's response.
A Maven dependency is required as well, here the xml:
```xml
<dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java</artifactId>
    <version>3.6.1</version>
</dependency>
```

## MTA feed polling

The goal is to retrieve all running subways on each lines using the MTA feed API. To achieve that, each feed id will be polled and a custom filter will be applied to only keep running subways. This can be easily done using the `@Scheduled` annotation provided by Spring, see below for the full code:

```java
@Scheduled(cron = "0 */10 * * * *")
public void publishMtaFeeds() {
    // Feed ids, more details at https://datamine.mta.info/list-of-feeds
    Stream<String> feedIds = Stream.of("1", "2", "11", "16", "21", "26", "31", "36", "51");

    // Read each feed and build a list of active subways
    List<Subway> subways = feedIds
        .flatMap(this::readMtaFeed)
        .collect(Collectors.toList());
}

private Stream<Subway> readMtaFeed(String id) {
    log.info("Reading feed for id {}", id);
    try {
        // Call MTA api
        ResponseEntity<byte[]> response = restTemplate.getForEntity("http://datamine.mta.info/mta_esi.php?key={0}&feed_id={1}", byte[].class, "YOUR_KEY", id);

        // Parse response using protobuf
        GtfsRealtime.FeedMessage feedMessage = GtfsRealtime.FeedMessage.parseFrom(response.getBody());

        // Build departure limit using timestamp from response
        long departureLimit = feedMessage.getHeader().getTimestamp() + 10 * 60;

        // Only active subways are returned
        return feedMessage.getEntityList().stream()
            .filter(feedEntity -> isActive(feedEntity, departureLimit))
            .map(Subway::new);
    } catch (Exception e) {
        log.error("Error while parsing MTA feed", e);
        return Stream.empty();
    }
}

private boolean isActive(GtfsRealtime.FeedEntity feedEntity, long departureLimit) {
    // A subway is active if he has a scheduled departure before the limit
    return feedEntity.hasTripUpdate()
        && feedEntity.getTripUpdate().getStopTimeUpdateCount() > 0
        && feedEntity.getTripUpdate().getStopTimeUpdate(0).getDeparture().getTime() < departureLimit;
}

public class Subway {
    private String trip;
    private String route;

    public Subway(GtfsRealtime.FeedEntity feedEntity) {
        this.route = feedEntity.getTripUpdate().getTrip().getRouteId();
        this.trip = feedEntity.getTripUpdate().getTrip().getTripId();
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getTrip() {
        return trip;
    }

    public void setTrip(String trip) {
        this.trip = trip;
    }
}
```

# Kafka integration with Spring Cloud Stream

Spring Cloud Stream provides a number of abstractions and primitives that simplify the writing of message-driven microservice applications. Kafka will be used as the message broker with two topics:
* `mta` to store the data coming from the API polling
* `mta-stream` to store the result from streaming the `mta` topic

The below dependency must be added to use the Kafka Streams API:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-stream-binder-kafka-streams</artifactId>
</dependency>
```

The below configuration for Spring Cloud Stream declares 4 bindings that the microservice will use:
```yml
spring:
    cloud:
        stream:
            kafka:
                binder:
                    brokers: localhost
                    zk-nodes: localhost
                streams:
                    binder:
                        configuration:
                            default.key.serde: org.apache.kafka.common.serialization.Serdes$StringSerde
                            default.value.serde: org.apache.kafka.common.serialization.Serdes$StringSerde
            bindings:
                # used to publish MTA's data
                mta-output:
                    destination: mta
                # used to read MTA's data as a stream
                input:
                    destination: mta
                # used to publish the stream result
                output:
                    destination: mta-stream
                # used to read stream result (will be used in the part 2 of this blog)
                stream-input:
                    destination: mta-stream
```

Since JHipster is used, the `MessagingConfiguration` class must be changed to reflect the above configuration:
```java
// Kafka Streams provides a pre-configured interface called KafkaStreamsProcessor that uses the bindings 'input' and 'output'
// The interface MtaStream defines the other two bindings as regular channels
@EnableBinding(value = {KafkaStreamsProcessor.class, MessagingConfiguration.MtaStream.class})
public class MessagingConfiguration {

    public interface MtaStream {
        String OUTPUT = "mta-output";
        String INPUT = "stream-input";

        @Output(OUTPUT)
        MessageChannel output();

        @Input(INPUT)
        SubscribableChannel input();
    }

}
```

One last thing is to change the file `kafka.yml` to have the two topics created. The environment variable to change is `KAFKA_CREATE_TOPICS`:
```yml
KAFKA_CREATE_TOPICS: "mta:1:1,mta-stream:1:1"
```

# Data processing using Kafka Streams

## Publishing MTA's data

The interface `MtaStream` is used to publish messages to the topic `mta`, it must be injected as a Bean. That's how the method `publishMtaFeeds` will look now:
```java
@Scheduled(cron = "0 */10 * * * *")
public void publishMtaFeeds() {
	// Feed ids, more details at https://datamine.mta.info/list-of-feeds
	Stream<String> feedIds = Stream.of("1", "2", "11", "16", "21", "26", "31", "36", "51");

	// Read each feed and build a list of active subways
	List<Subway> subways = feedIds
	    .flatMap(this::readMtaFeed)
	    .collect(Collectors.toList());

	// Publish all subways
	if (!subways.isEmpty()) {
	    mtaStream.output().send(MessageBuilder.withPayload(subways).build());
	}
}
```

The message is automatically serialized to a JSON array that contains all active subways.

## Counting subways per line

```java
@StreamListener("input")
@SendTo("output")
public KStream<?, SubwayCount> streamMtaFeeds(KStream<Object, List<Map<String, String>>> input) {
    // Count subways for each route with a window of 10 mins
    // Then publish the stream
    return input
        .flatMapValues(value -> value.stream().map(subway -> subway.get("route")).collect(Collectors.toList()))
        .map((key, value) -> new KeyValue<>(value, value))
        .groupByKey()
        .windowedBy(TimeWindows.of(10 * 60 * 1000))
        .count(Materialized.as("subwaycounts"))
        .toStream()
        .map((key, value) -> new KeyValue<>(null, new SubwayCount(key, value)));
}
```

The above method does all the magic, it streams the topic `mta` and group by the route (which is the line). A window of 10mins is used since the API is polled every 10mins and the stream result is published to the topic `mta-stream`.

The Kafka Streams binder allows to directly use the `KStream` object, the stream processing method will be more clear and simple. The first step is to map the route from the `Subway` class and then map it to a `KeyValue` so it can be grouped by the key. Then the second step defines the window and count the number of records in the stream. And finally the last step converts the stream back to a `KStream` and map a `KeyValue` with a value representing the count:

```java
public class SubwayCount {
    private String route;
    private long count;
    private Instant start;
    private Instant end;

    public SubwayCount() {
    }

    public SubwayCount(Windowed<String> key, long value) {
        this.route = key.key();
        this.count = value;
        this.start = Instant.ofEpochSecond(key.window().start() / 1000);
        this.end = Instant.ofEpochSecond(key.window().end() / 1000);
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }
}
```

# Running using Docker

Before running the microservice, 3 Docker containers must be started:
* JHipster Registry using `docker-compose -f src/main/docker/jhipster-registry.yml up -d`
* Kafka using `docker-compose -f src/main/docker/kafka.yml up -d` (contains Zookeper and Kafka)

After having all containers running, the microservice can be started using `./mvnw`. One way to view the messages in Kafka is to run the command `kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic mta` in the container.

Having the data in Kafka is great but it would be even better to visualize those data! In the part 2 of this blog post I will explain how the data can be easily visualized using Grafana and InfluxDB.
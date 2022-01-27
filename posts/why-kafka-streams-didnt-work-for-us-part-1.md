---
authors:
- Alexis Seigneurin
tags:
- Apache Kafka
date: 2017-08-22T13:29:07.000Z
title: "Why Kafka Streams didn't work for us? - Part 1"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/08/apache-kafka-1.png
---

This is a post in 3 parts in which I explain how we started a project on Kafka Streams, and why we had to stop using this library because it had a scalability issue. I took a lot of inspiration from Streams, though, and I will explain how we re-implemented our application on top of plain Kafka Consumers.

My goal here is not to say that Kafka Streams is not a good library (I still encourage you to use it, if it is appropriate for your use case), but to describe the engineering that is required to implement a fault-tolerant application that performs a simple aggregation.

# The project

Our goal is fairly simple: we want to monitor the quality of the messages in all the topics of a Kafka cluster. To simplify a little, for each message, we want to say if it is `valid` or `invalid`, and we want to publish metrics: the number of `valid` or `invalid` messages per second.

The metrics are published to another Kafka topic (one topic for all the metrics of the cluster). We then have another application that reads from this topic, and pushes the metrics to [InfluxDB](https://www.influxdata.com/). Finally, we are using [Grafana](https://grafana.com/) to visualize the metrics:

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/08/grafana.png)

This sounds simple but there is a lot of complexity to handle:

- In Kafka, a _topic_ is made of multiple _partitions_, and the counting should be made across partitions, i.e. we want to display aggregated results.
- We expect the volume of data to be high (tens to hundreds of thousands of messages per second), meaning we want to be able to _scale out_ by running multiple instances of the applications, each instance taking care of reading from a subset of the partitions.
- The application should be fault-tolerant: if an instance stops, other instances should take over the work of the dead instance, and the results should eventually be accurate (messages should be counted **exactly once**).
- Counts should be calculated per second, using the timestamps of the messages (messages include a timestamp [since Kafka 0.10](https://issues.apache.org/jira/browse/KAFKA-2511)), and messages processed lately should simply update the counters of the respective time windows (see example below).

## Example

Let's assume we're using a topic called `tx` with 2 partitions (`tx-0` and `tx-1`), with valid (`v`) and invalid (`i`) messages, and windows of 1 second (`t0` = from second 0 to 1, `t1` = from second 1 to 2, etc.).

```text
          t0          t1          t2
     ----------- ----------- -----------
tx-0 |v|v|v|i|v| |v|i|       |i|v|v|
     ----------- ----------- -----------
tx-1 |v|i|v|v|   |v|v|v|     |i|i|v|v|
     ----------- ----------- -----------
```

We would expect the following results:

- At `t0`, 7 valid messages (4 in partition `tx-0` + 3 in partition `tx-1`) and 2 valid messages (1 in each partition)
- At `t1`, 4 valid messages and 1 valid message
- At `t2`, 4 valid messages and 3 valid messages

This would materialize as follows in the `metrics` topic, assuming that `t0=1501273548000`:

```json
$ kafka-console-consumer --topic metrics --property print.key=true ...
{"topic":"tx","window":1501273548000,"status":"valid"}      7
{"topic":"tx","window":1501273548000,"status":"invalid"}    2
{"topic":"tx","window":1501273549000,"status":"valid"}      4
{"topic":"tx","window":1501273549000,"status":"invalid"}    1
{"topic":"tx","window":1501273550000,"status":"valid"}      4
{"topic":"tx","window":1501273550000,"status":"invalid"}    3
```

And, in InfluxDB, we would have a _measurement_ called `tx` with two fields, `valid` and `invalid`:

```text
> select valid, invalid from tx
name: tx
time                valid invalid
----                ----- -------
1501273548000000000 7     2
1501273549000000000 4     1
1501273550000000000 4     3
```

## Example with late data

Now, in the case of late-arrival / late-processed data, we would simply send an update to the `metrics` topic. For instance, if we received one more valid message for `t1`, we would output the following _update_:

```json
{"topic":"tx","window":1501273549000,"status":"valid"}      5
```

In InfluxDB, this would **update** the existing value of field `valid` for the given timestamp:

```text
> select valid, invalid from tx where time=1501273549000000000
name: tx
time                valid invalid
----                ----- -------
1501273549000000000 5     1
```

# To be followed...

This is all for now. In [the next post](/why-kafka-streams-didnt-work-for-us-part-2/), we will see how we implemented this using Kafka Streams.

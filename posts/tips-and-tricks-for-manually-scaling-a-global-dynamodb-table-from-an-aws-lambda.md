---
authors:
- Dennis Sharpe
tags:
- AWS
- Lambda
- DynamoDB
- Java
date: 2020-11-16T15:52:00.000Z
title: "Tips and Tricks for Manually Scaling a Global DynamoDB Table from an AWS Lambda"
image:
---
# Objective
Write an AWS Lambda that manually scales a global DynamoDB table

# Why?
DynamoDB tables can be set to automatically scale based on load. However, this process can take quite a while. We needed to scale up at a precise time to be ready for large batch loads.

# MVP
Scale a single (non-global) table
- Turn off auto-scaling for the table (Provisioned)
- Use the DynamoDB SDK v1
- Explicitly call the SDK to set the RCU/WCU on the table
- Initialize
```java
    private ClientConfiguration clientConfiguration() {
        final ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setProxyHost(AWS_PROXY_HOST);
        clientConfiguration.setProxyPort(Integer.valueOf(AWS_PROXY_PORT));
        clientConfiguration.setClientExecutionTimeout(CLIENT_TIMEOUT);
        clientConfiguration.setProtocol(Protocol.HTTP);
        return clientConfiguration;
    }

    private AmazonDynamoDB getClient() {
        return AmazonDynamoDBClientBuilder.standard()
                .withClientConfiguration(clientConfiguration())
                .withRegion(Regions.valueOf(REGION))
                .build();
    }
```
- Call the SDK
```java
ProvisionedThroughput table_throughput = new ProvisionedThroughput(
      read_capacity, write_capacity);

try {
    getClient().updateTable(table_name, table_throughput);
} catch (AmazonServiceException e) {
    System.err.println(e.getErrorMessage());
    System.exit(1);
}
```

# Full Version
Scale a global table
- In this example, the primary table is in _us-east-1_. A global table was created with a replica in _us-west-2_
- Global tables must use auto-scaling
- Setting the RCU/WCU explicitly on an auto-scaled table does not actually change the RCU/WCU (approach for MVP will not work)
- Upgrade to SDK v2 to simplify setting the auto-scaling parameters
- Set the minimum RCU/WCU on the auto-scaler instead
- Initialize
```java
    private DynamoDbClient getClient() {
        ApacheHttpClient.Builder httpClientBuilder = ApacheHttpClient.builder()
                .connectionTimeout(CONNECTION_TIMEOUT)
                .socketTimeout(SOCKET_TIMEOUT);
        RetryPolicy.Builder retryBuilder = RetryPolicy.builder()
                .numRetries(RETRY_LIMIT);
        ClientOverrideConfiguration.Builder clientOverrideBuilder =
                ClientOverrideConfiguration.builder()
                        .apiCallAttemptTimeout(API_CALL_ATTEMPT_TIMEOUT)
                        .apiCallTimeout(API_CALL_TIMEOUT)
                        .retryPolicy(retryBuilder.build());

        return DynamoDbClient.builder()
                .httpClientBuilder(httpClientBuilder)
                .overrideConfiguration(clientOverrideBuilder.build())
                .build();
    }
```
- Call the SDK
```java
        AutoScalingTargetTrackingScalingPolicyConfigurationUpdate policyConfigurationUpdate =
                AutoScalingTargetTrackingScalingPolicyConfigurationUpdate.builder()
                        .targetValue(SCALING_POLICY_TARGET_VALUE)
                        .build();
        AutoScalingPolicyUpdate policyUpdate = AutoScalingPolicyUpdate.builder()
                .targetTrackingScalingPolicyConfiguration(policyConfigurationUpdate)
                .build();
        AutoScalingSettingsUpdate.Builder updateBuilder = AutoScalingSettingsUpdate.builder()
                .maximumUnits(MAX_AUTOSCALE_CAPACITY)
                .scalingPolicyUpdate(policyUpdate);

        AutoScalingSettingsUpdate rcuUpdate = updateBuilder.minimumUnits(HIGH_READ_CAPACITY_UNITS).build();
        AutoScalingSettingsUpdate wcuUpdate = updateBuilder.minimumUnits(HIGH_WRITE_CAPACITY_UNITS).build();

        ReplicaAutoScalingUpdate eastReplicaUpdate = ReplicaAutoScalingUpdate.builder()
                .regionName("us-east-1")
                .replicaProvisionedReadCapacityAutoScalingUpdate(rcuUpdate)
                .build();
        ReplicaAutoScalingUpdate westReplicaUpdate = ReplicaAutoScalingUpdate.builder()
                .regionName("us-west-2")
                .replicaProvisionedReadCapacityAutoScalingUpdate(rcuUpdate)
                .build();
        UpdateTableReplicaAutoScalingRequest autoScalingRequest = UpdateTableReplicaAutoScalingRequest.builder()
                .tableName(TABLE_NAME)
                .replicaUpdates(eastReplicaUpdate, westReplicaUpdate)
                .provisionedWriteCapacityAutoScalingUpdate(wcuUpdate)
                .build();

        try {
            getClient().updateTableReplicaAutoScaling(autoScalingRequest);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
```

# Issues
The code above worked great until the Lambda was deployed in _us-west-2_ for redundancy. The SDK calls from _us-west-2_ were timing out.
- For testing purposes, no SDK v2 calls worked but v1 calls did
- The solution is to initialize the SDK v1 and then make the SDK v2 calls
- Add a call to `getTable(TABLE_NAME)` from the SDK v1 before the call to `updateTableReplicaAutoScaling`
- **Note:** SDK v1 needs the proxy specified but SDK v2 does not

## Disclaimer
These issues may have been specific to my particular environment. If you experience similar issues, this should help you resolve them.
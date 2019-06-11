---
authors:
- Aaron Throckmorton
tags:
- AWS
- Data
date: 2019-06-11T21:24:13.000Z
title: "EC2 Options for Cassandra on AWS"
image: 
---

There may seem to be endless options for deploying Cassandra clusters to Amazon Web Services. As an engineer at Ippon Technologies, I have deployed, tested, and productionalized both Apache and Datastax flavors of Cassandra and seen the pitfalls and benefits of several approaches with EC2. I will especially be going over the consequences to your monthly cloud bill and engineering resources. 

# Scenario
First let us walk through some numbers on instance-store vs EBS. In this scenario, we want to have a 12 node cluster so we can replicate our data across 3 availability zones in US-East-1. All pricing was done using the [AWS Simple Monthly Calculator](https://calculator.s3.amazonaws.com/index.html). We will be looking at basic pricing for 2xlarge VMs from the i3 and m5 families.

Why m5 and i3? These EC2 families are [recommended by Datastax](https://docs.datastax.com/en/dse-planning/doc/planning/planningEC2.html) and the [Apache Cassandra Community](http://cassandra.apache.org/doc/latest/operating/hardware.html) as optimal depending on choice of instance-store or EBS volumes. These sizes are typical of production use but consider tweaking them if you intend on tuning for more vCPUs. You can find more specific tuning information in the [Apache Cassandra docs](http://cassandra.apache.org/doc/latest/operating/hardware.html). 

You'll also notice, and need to consider, that EBS volumes must be sized at 3.5 terabytes to ensure the 10,000 IOPS recommended for Cassandra performance.

Finally, as a disclaimer, these numbers are heavily estimated and there are always other hidden costs to consider when using the cloud.

## Numbers at a glance
#### 12 nodes in US-East-1: 
| EC2        | vCPU/Memory| Storage               | Network Perf | Monthly cost |
|------------|------------|-----------------------|--------------|--------------|
| m5.2xlarge | 8/32       | 3.5 TB EBS provisioned| 10 Gbps      | $ 7573.08    |
| i3.2xlarge | 8/61       | 1 x 1.9 TB provided   | 10 Gbps      | $ 5481.24    |

Without any other constraints, you can see m5 instances with EBS are significantly more expensive. But what if we want to take EBS Snapshots?

Being charitable, lets say there's a 5% daily change for incremental EBS snapshots. With 42000 gigabytes of S3 storage at 5% change, you've added **$3753.84** to your monthly bill for a total of **$11326.92**. 

## Details, details
So on the surface, EBS starts getting expensive fast when looking at storage costs for disaster recovery. But what about recovery options for instance-store volumes? Numerous types of Cassandra snapshot options exist but ultimately you will still need to store the snapshots somewhere. For a deeper dive into these options, check out The Last Pickle's [blog post](https://thelastpickle.com/blog/2018/04/03/cassandra-backup-and-restore-aws-ebs.html#backup-and-restore-solutions).

Cassandra snapshots need not be the same size as the volume. Where EBS must store the entire volume on S3, you can get by with just storing your actual Cassandra data in-use. If doing this incrementally, we still need to calculate for daily change. With roughly 1 terabyte of data per node, the monthly cost for instance-store rises by roughly **$300**. 

You might look at this number and wonder why it's so much cheaper than the EBS snapshots on S3, even after accounting for the difference in size. You can think of EBS snapshots as a managed service that you're subscribing to. The service costs significantly more than normal S3 operations and data.

## Our New (Estimated) Numbers 
| EC2        | Monthly cost |
|------------|--------------|
| m5.2xlarge | $ 11326.92   |
| i3.2xlarge | $ 5781.24    |

We can estimate that using EBS volumes almost doubles our monthly bill. Why would you want to take on such costs for EBS?

## Making a Case for EBS
As an engineering consultant that has dealt with building EBS-backed Cassandra clusters, I can attest to their operational simplicity. There is no better option when it comes to easy, safe, disaster recovery. Again, I want to encourage you to take a look at The Last Pickle's [blog](https://thelastpickle.com/blog/2018/04/03/cassandra-backup-and-restore-aws-ebs.html) on this topic as he does a deeper dive from an operational point-of-view. But I want to add to this with my developer perspective.

## More Scenarios
#### #1 You just received an alert that AWS is retiring the hardware that one of your nodes is running on
**EBS recovery**
1. Detach the EBS volume 
2. Decommission old node VM
3. Bootstrap new node and attach old EBS volume

**Instance-store recovery**
1. Bootstrap new node 
2. Configure new node to replace old node in cassandra.yaml
3. Stream data into new node 
4. Decommission old node

Not much difference here. EBS has the advantage of recovery time because data is already present in the reattached volume. 

#### #2 Your organization requires that you refresh all services with new AMIs every quarter
**EBS recovery**
Basically the same as above, for each node in the cluster.
One-by-one, detach EBS volume, decommission old node, and then bootstrap new node and attach old volume. Only do one at a time. As long as your replication factor is higher than one, this method has no downtime. This is very simple and safe. Can be made even easier if you use the same private ips for new nodes as the ones they are replacing. 

**Instance-store recovery**
There are several ways you could approach this but ultimately they will be just as complex as the following:
1. Create new cluster and run it alongside old one
2. Change topology on old cluster to add new cluster as a datacenter
3. Change configuration in cassandra.yaml on both old and new such that the clusters recognize each other
4. Nodetool rebuild on new cluster from old cluster
5. Swap DNS to new cluster and kick cache or redeploy integrating software to refresh DNS
6. Decommission old cluster

This process is arduous, difficult to automate, and much more inferior to the EBS option. I want to stress the hidden costs of engineering time spent on a process like this. It is relatively impossible to estimate the real cost and time spent on such a process. Especially in regards to development time for automation. Factor in the topology concerns as well when your cluster is running multi-region or in several availability zones.

#### #3 The worst has happened, your cluster was bricked and you must implement the disaster recovery plan
**EBS recovery**
1. Create volumes from the snapshots you've been taking, for each node
2. Bootstrap new cluster and attach EBS volumes from snapshot

That's mostly it. It's dead simple disaster recovery. Your recovery time is short and can be done manually or automated painlessly.

**Instance-store recovery**
1. Create new cluster 
2. Bulk load the new cluster with SSTableLoader from the incremental backups on S3
3. nodetool refresh

Not very complex at a glance, but difficult to automate. Recovery time is very long as the nodes have to replay some of the snapshot data to determine changes needed on SSTables. The bulk loader will need time to stream data to the new cluster. Consider the time and resources needed to automate the creation and removal of snapshots from the nodes to S3. As a warning, I've never seen this option work and I can't find any real data on anyone attempting it so this is mostly theoretical. In a perfect world, your cluster data is replicated in several availability zones and you only lost one AZ to whatever disaster occured.

## Making a case for Instance-Store
Thus far I think I've made it clear that EBS shines for operational simplicity. Maybe it works for your budget and you don't want to expend the resources to automate backups and restoration for instance-store volumes. I believe there is still significant reason to use instance-store.

Looking at it from the perspective of IOPS, the i3 family of EC2 is capable of 3.3M IOPS. This is a big advantage for an I/O heavy workload like Cassandra.

Long-term, you will be saving a very significant amount of money on monthly bills if you choose to go with instance-store. I think the engineering expenditure needed to build out the automation, recovery, and backups, is justified.

# In Summation
When you look at the larger picture, I believe this can be summed up thusly: EBS volumes offer data integrity and recovery options but you must consider the costs to your monthly bill. Instance-store volumes, offered by the community approved i3 family of instances, are a cheaper option if you can spare the engineering resources to build out backup and recovery.
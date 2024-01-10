---
authors:
- Lucas Ward
tags:
- postgresql
- devops
- docker
title: "Learn by Doing - Postgres Logical Replication"
image: 
---

Database Replication is the corner stone of highly available and horizontally scaled relational databases. Database replication comes in many different flavors and can be achieved by multiple different types of mechanisms. In order to be better engineers, and architect better systems, it is paramount to have a base understanding of these replication mechanisms. Today, I want to share with you a *Learn by Doing* blog featuring Postgresql's built in Logical Replication. 

# Before you Begin

To build a highly available database cluster, we need multiple databases! Instead of spending a bunch of money on servers and taking the time to prop up the infrastructure to support it, we will instead leverage Docker. Although this may not reflect a *production ready* setup, it will give us just enough to learn and grasp the concepts of logical replication in Postgresql 15. 

In order to follow along, you will need to have docker installed on your system. If you don't already have it installed, I recommend using [Rancher Desktop](https://rancherdesktop.io/). Rancher Desktop can enable you to run a local kubernetes cluster, but also comes with the docker daemon. We will also need the `psql` tool installed on our local system. If you don't already have it installed, check out the [postgresql downloads page](https://www.postgresql.org/download/). Once you have a working docker daemon installed as well as the `psql` tool, we are ready to begin!

# Setup the Database Cluster

In this hands on guide, we are going to setup two Postgresql 15 databases. This will allow us to create a Primary database, that will accept read and write queries, and a Secondary Database, that will be primarily for read queries. Any data written, updated, or deleted from the primary will also show up on the secondary database. This is a very common and widely used paradigm and will serve well to aid in our understanding of postgresql's built-in logical replication.

### Test the Docker Daemon

Open up your favorite terminal program and run the following command:
```bash
docker version
```
If your docker environment is configured properly, you should see a bunch of versioning information printed to the terminal window. At the time of this writing, I am running Docker Engine - Community, version 20.10.22. 

### Pull the Postgres Image

Before we can set up our two databases, we are required to *pull* the postgres docker image that we wish to use. Run the command:
```bash
docker pull postgres
```
This command will pull down the latest official version of the Postgresql Docker Image. 

### Launch two Postgresql Containers

Now that we have pulled the Postgresql image, we are ready to run our containers. Run the follow commands, feel free to replace the names `postgres-primary`, `postgres-secondary` and the password `topsecretpassword` with your own values.
```bash
docker run --name postgres-primary -e POSTGRES_PASSWORD=topsecretpassword -d postgres
docker run --name postgres-secondary -e POSTGRES_PASSWORD=topsecretpassword -d postgres
```
To keep things simple, I have used the same password for both databases. This is fine for a learning exercise, but I implore you to make better choices for a production setup.

# Connecting to the Databases

Throughout the rest of this blog, I will ask you to connect to the database containers using `bash`, as well as connect via `psql`. The three sub-sections below should enable you to do just that. If you get stuck later on, reference this section.

### Get the Two Containers IP address

In order to connect to our containers with `psql`, we need their *local docker IP Addresses*. To ascertain these values, run these commands:
```bash
docker inspect postgres-primary
docker inspect postgres-secondary
```
These commands will return a plethora of information regarding your docker containers. We are looking for the value: `Networks.bridge.IPAddress` for both of our containers. Make sure to mark this value down as we will need it later. My values are as follows
- **postgres-primary** - 172.17.0.2
- **postgres-secondary** - 172.17.0.4

### Connect to the Database using PSQL

We can connect to our database using the `psql` command. To connect to the primary dataabase, run the following command, replacing the IP Address with the one you got from the previous step:
```bash
psql -h 172.17.0.2 -p 5432 -U postgres
```
In order to connect to the secondary database run the following command:
```bash
psql -h 172.17.0.4 -p 5432 -U postgres
```
For the remainder of the blog, I will simply say "Connect to the database using PSQL".

### Run Bash on a Container

In order to change some of the Postgresql settings, we will also need to access the command line running inside the postgres containers. In order to do so, run the following command, be sure to replace your container name with the name you used to launch the containers from above.
```bash
# For our Primary
docker exec -it postgres-primary bash
# For our Secondary
docker exec -it postgres-secondary bash
```
Okay, now that all of that is out of the way, we are ready to tweak our database settings, add some tables, and enable logical replication.

# Enable Logical Replication

In order to utilize Postgresql built in logical replication, which was introduced in Postgresql version 10, we need to enable it in the settings. To update the settings, launch `bash` in the container that is running your primary database. Once you have access to the command line inside the container, run the following commands:
```bash
apt-get update
apt-get install vim
apt-get install iputils-ping # For later

cd /var/lib/postgresql/data
vim postgresql.conf

# edit the following lines (settings) in postgresql.conf
wal_level = logical
max_wal_senders = 10
```
Next, we need to update `pg_hba.conf` to allow our secondary database to connect to the primary.
```bash
cd /var/lib/postgresql/data
vim pg_hba.conf

# add this line anywhere in the file
host    all     postgres    0.0.0.0/0   md5
```
Again, these settings are only to be used for educational purposes and are not sufficiently secure for a production workload. If you are curious about either of these configuration files, I recommend perusing the official documentation for the [configuration file](https://www.postgresql.org/docs/current/config-setting.html) and the [client authentication file](https://www.postgresql.org/docs/current/auth-pg-hba-conf.html).

Once you are done updating the settings on the primary, type `exit` to exit `bash` inside the container and then use the Docker CLI on your local machine to restart the container, which will apply the settings.
```bash
docker restart postgres-primary
```
These settings only need to be updated on the Primary Database.

# Test Container to Container Connectivity

Before we go on to setup logical replication, we need to make sure the two database containers can communicate with each other. Launch `bash` on the primary database container and attempt to ping the secondary database.
```bash
# using bash on the primary database container run
ping 172.17.0.4
```
If the connection is successful, you should see something like this:
```bash
PING 172.17.0.4 (172.17.0.4) 56(84) bytes of data.
64 bytes from 172.17.0.4: icmp_seq=1 ttl=64 time=0.160 ms
64 bytes from 172.17.0.4: icmp_seq=2 ttl=64 time=0.093 ms
```
Pressing `[ctrl]+c` will stop the ping.
Exit the container and launch `bash` on the Secondary Database Container, run these commands:
```bash
# using bash on the secondary database container run
apt-get update
apt-get install iputils-ping
ping 172.17.0.2
```
Be sure to replace the IP address that you are pinging with the IP address of the container you are testing the connection to. Again, a successful connection will look like the output from above. Once connection has been verified, exit the container by typing `exit`.

# Add a table

Before we setup logical replication, we need to create a database and add a table to both the Primary and Secondary. It's important that the schema's of the two databases match, so I recommend connecting to the primary and running these commands, then immediately connecting to the secondary and running the same commands. 

Using `psql`, connect to the primary database - `psql -h 172.17.0.2 -p 5432 -U postgres`. Once connected run the following commands:
```sql
CREATE DATABASE test_database;
\c test_database
CREATE TABLE foo (
    record_id serial PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    created_on TIMESTAMP NOT NULL
);
```
Type `\q` to quit psql. Connect to the Secondary and run the exact same commands. 

Once this is complete, we should have a Primary database, and a Secondary database with the exact same schema. Our next step is to setup logical replication (finally!).

# Setup Logical Replication

First, connect to the primary database using `psql`. Once connected, run the following commands:
```sql
--> connect to the test_database
\c test_database
--> create a publication
CREATE PUBLICATION testpub FOR TABLE foo;
```

Next, connect to the secondary database using `psql` and run the following commands:
```sql
--> connect to the test_database
\c test_database
--> create a subscription that points to the primary database
CREATE SUBSCRIPTION testsub CONNECTION 'dbname=test_database host=172.17.0.2 user=postgres password=topsecretpassword' PUBLICATION testpub;
```

Let's break down what we just did.
- `CREATE PUBLICATION testpub FOR TABLE foo;` - This command tells postgres that any changes made to the table `foo` should be "published".
- `CREATE SUBSCRIPTION testsub CONNECTION 'dbname=test_database host=172.17.0.2 user=postgres password=topsecretpassword' PUBLICATION testpub;` - This command tells postgres that we want to subscribe to a publication `testpub` on the database specified by the `CONNECTION` parameter.

Now that the Publisher and Subscriber relationship is setup, we should be able to test it out!!!

# Test the Logical Replication

Using `psql` connect to the primary database. Add some data to the table `foo`.
```sql
\c test_database
INSERT INTO foo (first_name, last_name, email, created_on) VALUES ('Lucas', 'Ward', 'lucasward@gmail.com', CURRENT_TIMESTAMP);

--> then retrieve the row to see the data
SELECT * FROM foo;
```
You should see the self same data that you just added returned from the `SELECT` statement. Next, use `psql` to connect to the secondary database and run this command:
```sql
\c test_database
SELECT * FROM foo;
```
If you see the same data returned as before, then congratulations!!! You have now officially setup logical replication.

# In Conclusion

In this article we created a "test database cluster" using Docker and Postgresql. After launching the primary and secondary database containers, we tweaked the settings on the primary to enable logical replication. Next we verified the connection between the containers worked and then we created a publisher and subscriber relation. After all that setup, we saw logical replication in action with our `INSERT` statement into the primary being replicated to the secondary database.

I hope this "Learn by Doing" article has shed some light on how database clusters operate "under the hood". Obviously, there is a lot more complexity involved when replicating many many database tables with complex relations. If you find yourself in need for a setup like this, or if you need help navigating the intricacies of Postgresql in general, feel free to drop us a line at `sales@ippon.fr`, we are here to help!

# Clean Up

To remove the two database containers from your system, simply run:
```bash
docker rm postgres-primary
docker rm postgres-secondary
```

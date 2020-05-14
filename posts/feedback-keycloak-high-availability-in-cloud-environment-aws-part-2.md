---
authors:
- Sébastien Mazade
tags:
- Feedback
- Keycloak
- IAM
- Security
- Cloud
- AWS
date: 2020-05-14T14:00:00.000Z
title: "FEEDBACK: Keycloak High Availability in Cloud environment (AWS) - PART 2/4"
image: https://raw.githubusercontent.com/smazade/blog-usa/master/images/2020/05/feedback-keycloak-high-availability-in-cloud-environment-aws-part-2-img1.jpg
---

In the [previous first part](https://blog.ippon.tech/feedback-keycloak-high-availability-in-cloud-environment-aws-part-1-4/), we have described the main cloud architecture, according to the restrictions we have, with some EC2 infrastructure details, to be able to run a Keycloak cluster.

Now, we will discuss what is inside our nodes (the Keycloak servers) and how to configure them using the Wildfly/JBoss best practices.

On Keycloak, there are 3 main things to configure:
*  The realms
*  The themes
*  The server

We are going to focus on this last element: the server.

# A little of pure Wildfly configuration

When you are configuring it, in fact, you are modifying the core of the underlying technology, which is nothing else than a WildFly server (formerly JBoss).

Even if some parts are specific to Keycloak (like cache definitions for example), it remains standard to the way you will configure an old JBoss.

The principle, apart from JDBC drivers files, is that all you will modify is gathered into one unique file: `.../standalone/configuration/standalone.xml`.

Nevertheless, as we are dealing with the high availability (HA) clustering mode, the one that we are interested in is: `.../standalone/configuration/standalone-ha.xml`. 

> <img style="float: left;padding-right: 0.7em;width: 2.2em;" alt="Pushpin" src="https://raw.githubusercontent.com/smazade/blog-usa/master/images/2020/05/1f4cc.png">  domain mode uses the `.../domain/configuration/domain.xml` configuration file, but it is out of scope here as we did not choose to use it.

Even in HA mode, the server is supposed to work with only one node, so you can either maintain the 2 files or only the HA one that can be used as well with 1 node in the local environment for more simplicity.

**Editing those files directly will be a big mistake!**

JBoss uses a CLI to edit them through their own formalism, it is a kind of Xpath notation that will modify the XML nodes without modifying the original installation bundled file. Thus, every time a new distribution version is available (Keycloak usually increases its version to be up to date to the last Wildfly one), if you decide to point to it, you will retrieve a new standalone-ha.xml file. If you are using the CLI, you will not need to compare what has been modified and report it manually on the new XML file each time you make an update (and with IAM products, it is mandatory to be always up to date). You will rarely get any conflict with the configuration you edited (it should not prevent you to ensure that new functionalities do not handle things that you have implemented in old versions).

The CLI will launch a very light embedded server based on the configuration file (standalone-ha.xml for instance) and the commands will be launched against this server. Example `my-simple-configuration.cli`:


```shell
embed-server --server-config=standalone-ha.xml --std-out=echo -c
   run-batch --file=/opt/keycloak/cli/modules/specific-ha.cli

   # example of command execution without batching
   /subsystem=undertow/server=default-server/https-listener=https: write-attribute(name=proxy-address-forwarding, value=${env.PROXY_ADDRESS_FORWARDING:false})

stop-embedded-server
```

In order to launch this file:

```shell
${KEYCLOAK_BIN}/jboss-cli.sh --file=${KEYCLOAK_CLI}/my-simple-configuration.cli
```

Inside the official image of Keycloak, you will find an example that uses it the right way to configure everything (that could be a start to be inspired on).

If we analyze closely the following line (no matter what it means for now):

```shell
/subsystem=undertow/server=default-server/https-listener=https: write-attribute(name=proxy-address-forwarding, value=${env.PROXY_ADDRESS_FORWARDING:false})
```


We see that we are writing the attribute ‘proxy-address-forwarding’ of a node ‘https-listener’ with a generic value that will be replaced at the runtime with the PROXY_ADDRESS_FORWARDING environment variable if defined, ‘false’ if not.

As a result, the XML node code will become (the file is really written on the server):

```xml
       <subsystem xmlns="urn:jboss:domain:undertow:10.0" default-server="default-server" default-virtual-host="default-host" default-servlet-container="default" default-security-domain="other" statistics-enabled="${wildfly.undertow.statistics-enabled:${wildfly.statistics-enabled:false}}">
           ...
           <server name="default-server">
               ...
               <https-listener name="https" socket-binding="https" proxy-address-forwarding="${env.PROXY_ADDRESS_FORWARDING:false}" security-realm="ApplicationRealm" enable-http2="true"/>
               ...
           </server>
```


I would recommend keeping an eye on the container logs to check if there are no errors and then to enter inside the container (with a `docker-compose exec keycloak bash`) to look at the modified standalone-ha.xml if all is correct.

You will notice that it is not very intuitive to target a node (the value for the ‘subsystem’ node matches with the 4th field of the ‘xmlns’, and for some nodes it is the ‘name’ attribute etc.). The easiest way to handle this is to rely on internet examples (usually you would not modify nodes that others do not modify).

 > <img style="float: left;padding-right: 0.7em;width: 2.2em;" alt="Warning" src="https://raw.githubusercontent.com/smazade/blog-usa/master/images/2020/05/26a0.png"> It is better to write idempotent instructions or group of instructions. Otherwise, when the server will get restarted (after a crash or a local restart), the nodes that have already been updated or removed, would not exist anymore and the .cli script would log errors whereas the configuration was already well achieved.

> <img style="float: left;padding-right: 0.7em;width: 2.2em;" alt="Warning" src="https://raw.githubusercontent.com/smazade/blog-usa/master/images/2020/05/26a0.png"> A .cli run-batch file or an inline batch instruction, will rollback all the embedded instructions if one fails.

<img style="float: left;padding-right: 0.7em;width: 2.2em;" alt="Pushpin" src="https://raw.githubusercontent.com/smazade/blog-usa/master/images/2020/05/1f4cc.png">  We have not used it, but if you wish to ease the command creation, you could use a GUI editor that needs to run against a running server ([for more documentation](https://www.keycloak.org/docs/latest/server_installation/#cli-gui-mode)):

```shell
${KEYCLOAK_BIN}/jboss-cli.sh --gui
```

# Now that CLI editions have no secret for you, let’s focus on our own configuration!

## Behind a proxy

Before addressing the clustering configuration, it is important to be aware first that usually, in a production environment, we will be behind one or more proxies. Hence, it is necessary to indicate it to Keycloak thanks to the environment variable `PROXY_ADDRESS_FORWARDING=true`.

In the latest official docker images, you don’t need to do anything else, normally the equivalent of the following line already exists:

```shell
/subsystem=undertow/server=default-server/https-listener=https: write-attribute(name=proxy-address-forwarding, value=${env.PROXY_ADDRESS_FORWARDING:false})
```

Thus, Keycloak will use the standard headers added by our proxies: `X-FORWARDED-PROTO`, `X-FORWARDED-HOST`, `X-FORWARDED-FOR`.

If the underlying protocols (HTTPS protocol rupture on ELB for instance) and domain names are different from the ones used by the server, Keycloak will know what to use for the internal redirections.

The last one (`X-FORWARDED-FOR`) will provide the chain list of all IPs (the first one is the client that initiated the request) and it is mandatory for brute forcing detection.

## Defining Interfaces

When dealing with TCP exchanges, it is necessary to define the network interfaces that we will use. JGroup will use its own interface whose name is very eloquent: `private`.

In our case, as we are in an EC2 VM (close to containers case), we can usually use the ‘eth0’ interface (you can check it for more safety by executing the `ip` command -- if not installed, on Linux, you will find it in `iproute/iproute2` packages):

```shell
#eth0 interface will be used to retrieve the server ip
/interface=private:write-attribute(name=nic, value=eth0)
/interface=private:undefine-attribute(name=inet-address)
```

If you want more explanation, I advise you to consult the official [JBoss EAP documentation](https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/7.0/html/configuration_guide/network_and_port_configuration) (The Redhat supported product branch).

You can sometimes find interesting things that will help you on this documentation website, but as we are using the Wildfly community version, it should be taken with a pinch of salt.

The interfaces (regarding the consoles, API and web servers) will be automatically configured by internal scripts if you specify the `-b` server parameter.

## Logger set up

Depending on your strategy, maybe you will not store any logs in your container or instance and all will be pushed directly to CloudWatch or an ELK for example. Nevertheless, for more safety, you can store temporarily the logs and use a rotating file appender in order to be sure the disk size will not overflow. In our case, we have removed all appenders, and we log everything into a file that a Fluentd agent uses to send the logs:

```shell
/subsystem=logging/root-logger=ROOT:remove-handler(name="FILE")
/subsystem=logging/root-logger=ROOT:remove-handler(name="CONSOLE")
/subsystem=logging/root-logger=ROOT:add-handler(name="SIZE-ROTATE-FILE")
/subsystem=logging/size-rotating-file-handler=SIZE-ROTATE-FILE:add(file={"path"=>"keycloak.log", "relative-to"=>"jboss.server.log.dir"},rotate-size=${env.KEYCLOAK_LOG_ROTATE_SIZE:10240k},max-backup-index=${env.KEYCLOAK_LOG_MAX_BACKUP_INDEX:50},append=true,formatter="%d{yyyy-MM-dd'T'HH:mm:ss.SSS'Z'} %-5p [%c] (%t) %s%e%n")
```

You will notice that we can define the number of files and the size respectively with `KEYCLOAK_LOG_ROTATE_SIZE` and` KEYCLOAK_LOG_MAX_BACKUP_INDEX`.

Keycloak does not seem to propose a hot configuration for log level (like Log4J v2 could do with JMX actions for instance) _-- to be confirmed_.

Consequently, you will probably define some specific configuration for some packages (useful for verifying the set up) and you will not forget to parameterize it through environment variables:

```shell
/subsystem=logging/root-logger=ROOT:change-root-log-level(level=${env.ROOT_LOGLEVEL:INFO})

/subsystem=logging/logger=org.jgroups:add
/subsystem=logging/logger=org.jgroups:write-attribute(name=level,value=${env.JGROUP_LOGLEVEL:INFO})

/subsystem=logging/logger=org.infinispan:add
/subsystem=logging/logger=org.infinispan:write-attribute(name=level,value=${env.INFINISPAN_LOGLEVEL:DEBUG})

/subsystem=logging/logger=org.keycloak:add
/subsystem=logging/logger=org.keycloak:write-attribute(name=level,value=${env.KEYCLOAK_LOGLEVEL:INFO})

/subsystem=logging/logger=org.keycloak.models.sessions.infinispan:add
/subsystem=logging/logger=org.keycloak.models.sessions.infinispan:write-attribute(name=level,value=${env.INFINISPAN_LOGLEVEL:DEBUG})

/subsystem=logging/logger=org.keycloak.events:add
/subsystem=logging/logger=org.keycloak.events:write-attribute(name=level,value=${env.EVENT_LOGLEVEL:DEBUG})

# To remove cumbersome DEBUG logs
/subsystem=logging/logger=org.keycloak.services.scheduled:add
/subsystem=logging/logger=org.keycloak.services.scheduled:write-attribute(name=level,value=INFO)
/subsystem=logging/logger=org.keycloak.transaction:add
/subsystem=logging/logger=org.keycloak.transaction:write-attribute(name=level,value=INFO)
```

## Infinispan/JGroup configuration

If we go a bit further into the Keycloak website, we can find this [article](https://www.keycloak.org/2019/04/keycloak-cluster-setup.html ) that contributes slightly to a starting point for the PING configuration.

Many PING protocols exist and you will need to choose one according to your architecture.

The `TCP_PING` approach contains a static list of IP addresses of each member of the cluster. While this works in an on-premise context, it doesn’t really help when cluster nodes are dynamically added to the cluster, so this one will naturally be pushed aside.

Then, you have the choice between (the list is not exhaustive): `S3_PING`, `AZURE_PING`, `GOOGLE_PING`, `JDBC_PING`, `KUBE_PING`, `DNS_PING`.

Maybe it would deserve a performance/stability study, but as we necessarily have a database (you might as well have one), we are using `JDBC_PING`.

Thus, the cluster state is managed through a `JGROUPSPING` table.

Each time a node is created, it must know its own IP address in order to be able to add it to this table and join the cluster.

Here are our commands:

```shell
# remove the unused UDP stack
/subsystem=jgroups/stack=udp:remove()
/socket-binding-group=standard-sockets/socket-binding=jgroups-mping:remove()
# remove the default TCP stack
/subsystem=jgroups/stack=tcp:remove()

#create our new TCP stack based on JGROUP_PING protocol
/subsystem=jgroups/stack=tcp:add()
/subsystem=jgroups/stack=tcp/transport=TCP:add(socket-binding="jgroups-tcp")
/subsystem=jgroups/stack=tcp/protocol=JDBC_PING: add(data-source="KeycloakDS", properties=[initialize_sql="CREATE TABLE IF NOT EXISTS JGROUPSPING (own_addr varchar(200) NOT NULL, cluster_name varchar(200) NOT NULL, updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, ping_data varbinary(5000) DEFAULT NULL, PRIMARY KEY (own_addr, cluster_name)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin",remove_old_coords_on_view_change="true",remove_all_data_on_view_change="true"])
/subsystem=jgroups/stack=tcp/protocol=MERGE3:add()
/subsystem=jgroups/stack=tcp/protocol=FD_SOCK:add(socket-binding="jgroups-tcp-fd")
#/subsystem=jgroups/stack=tcp/protocol=FD:add()
/subsystem=jgroups/stack=tcp/protocol=FD_ALL:add()
/subsystem=jgroups/stack=tcp/protocol=VERIFY_SUSPECT:add()
/subsystem=jgroups/stack=tcp/protocol=pbcast.NAKACK2:add()
/subsystem=jgroups/stack=tcp/protocol=UNICAST3:add()
/subsystem=jgroups/stack=tcp/protocol=pbcast.STABLE:add()
/subsystem=jgroups/stack=tcp/protocol=pbcast.GMS:add()
/subsystem=jgroups/stack=tcp/protocol=pbcast.GMS/property=max_join_attempts:add(value=5)
/subsystem=jgroups/stack=tcp/protocol=MFC:add()
/subsystem=jgroups/stack=tcp/protocol=FRAG3:add()

#set the tcp stack as the one used for the channel
/subsystem=jgroups/channel=ee:write-attribute(name=stack, value=tcp)
```

_This configuration handles the case when all IP addresses present into the `JGROUPSPING` table belong to unhealthy nodes. When a new node starts, if the node cannot contact the other addresses, it cleans the table on its own (`remove_all_data_on_view_change=true` and also `remove_old_coords_on_view_change=true` -- not sure about the necessity of this last one). It nevertheless generates some slight network overhead, but it does not require manual interventions if all nodes crash._

As we stated before, we are using an embedded Infinispan. Instead of using complex replicated cache configuration, it implies distributed caches.

You will notice that Keycloak uses many, but not all of them need to be distributed on many nodes, it will depend on what is more relevant for you. Anyway, the number of nodes on which data will be sharded, will depend on the accepted risk that a node can be removed at any time from the cluster and the level of availability you are expecting for data. When we are talking about data at the Infinispan level, we are referring of course to volatile data (sessions etc.).

If you choose only 1 owner (shard) and the node is stopped, the data will be lost forever. If you opt for a high number of owners, like it was mentioned earlier, it will impact performance but it can allow you to have a more aggressive downscale strategy…

Nonetheless, we recommend using at least 2 owners for handling failover and scale-in. The risk to have a low number of 2 can be acceptable, as a loss will not interrupt the business (it may be less acceptable for an e-commerce website), but will only force the user to login again or reorder new action tokens (in password workflows for instance).

Cache configuration with statistics commands:

```shell
# In order to enable all Keycloak statistics
/subsystem=infinispan/cache-container=keycloak:write-attribute(name=statistics-enabled,value=${env.ENABLE_INFINISPAN_STATISTICS:false})

/subsystem=infinispan/cache-container=keycloak/local-cache=users:write-attribute(name=statistics-enabled,value=${env.ENABLE_INFINISPAN_STATISTICS:false})

/subsystem=infinispan/cache-container=keycloak/replicated-cache=work/component=expiration:add(max-idle="3600000")
/subsystem=infinispan/cache-container=keycloak/replicated-cache=work:write-attribute(name=statistics-enabled,value=${env.ENABLE_INFINISPAN_STATISTICS:false})

/subsystem=infinispan/cache-container=keycloak/distributed-cache=sessions:write-attribute(name=owners,value=${env.DIST_CACHE_SHARD_COUNT:2})
/subsystem=infinispan/cache-container=keycloak/distributed-cache=sessions:write-attribute(name=statistics-enabled,value=${env.ENABLE_INFINISPAN_STATISTICS:false})

/subsystem=infinispan/cache-container=keycloak/distributed-cache=clientSessions:write-attribute(name=owners,value=${env.DIST_CACHE_SHARD_COUNT:2})
/subsystem=infinispan/cache-container=keycloak/distributed-cache=clientSessions:write-attribute(name=statistics-enabled,value=${env.ENABLE_INFINISPAN_STATISTICS:false})

# Login failure not synchronized between node -> brute force count not very accurate, but it is not very important
/subsystem=infinispan/cache-container=keycloak/distributed-cache=loginFailures:write-attribute(name=owners,value=1)
# authentication sessions not synchronized between node -> handle by affinity sessions (if node removed very few will be lost as authent phase is short)
/subsystem=infinispan/cache-container=keycloak/distributed-cache=authenticationSessions:write-attribute(name=owners,value=1)
/subsystem=infinispan/cache-container=keycloak/distributed-cache=authenticationSessions:write-attribute(name=statistics-enabled,value=${env.ENABLE_INFINISPAN_STATISTICS:false})

# Offline caches if applicable
/subsystem=infinispan/cache-container=keycloak/distributed-cache=offlineSessions:write-attribute(name=owners,value=${env.DIST_CACHE_SHARD_COUNT:2})
/subsystem=infinispan/cache-container=keycloak/distributed-cache=offlineSessions:write-attribute(name=statistics-enabled,value=${env.ENABLE_INFINISPAN_STATISTICS:false})
/subsystem=infinispan/cache-container=keycloak/distributed-cache=offlineClientSessions:write-attribute(name=owners,value=${env.DIST_CACHE_SHARD_COUNT:2})
/subsystem=infinispan/cache-container=keycloak/distributed-cache=offlineClientSessions:write-attribute(name=statistics-enabled,value=${env.ENABLE_INFINISPAN_STATISTICS:false})

# action tokens are the ones send by mail, 1 owner means that if a node is removed the mail will expire
/subsystem=infinispan/cache-container=keycloak/distributed-cache=actionTokens:write-attribute(name=owners,value=${env.DIST_CACHE_SHARD_COUNT:2})
/subsystem=infinispan/cache-container=keycloak/distributed-cache=actionTokens:write-attribute(name=statistics-enabled,value=${env.ENABLE_INFINISPAN_STATISTICS:false})
```

Herein above, we notice that `authenticationSession` and `loginFailures` have only one owner. For the first one, as the authentication phase does not last very long (elapsed time between login form appearance and credential validation), very few tokens will be lost and the user will just need to re-enter his credentials, it is up to you to accept this risk.

For the second, it is even less important as `loginFailures` is the count or status of unsuccessful attempts, so it will just reset the status/count that should be restrictive/low in any case (i.e.: it gives a few more chances to an attacker in this rare case).

Regarding the `actionTokens` cache, when a mail (password reset or creation etc.) is sent with a token, it could be annoying to have it expire rapidly when a node is removed, so it is as much important as the authenticationSession ones to shard it well.

You may have noticed that we included JMX statistics metrics activation. By default, they are accessible through the `9990/9993` ports of the Wildfly (the ports are used as well for accessing the Wildfly console).

Be careful to let remote IPs access those ports by adding the` -bmanagement` parameter.

Here is the full launch command -- where `SYS_PROPS` contains all the `-D` parameters you want (such as `-Dkeycloak.profile=preview` for example):

```shell
"${KEYCLOAK_BIN}"/standalone.sh -b=0.0.0.0 -bmanagement=0.0.0.0 -c standalone-ha.xml $SYS_PROPS
```

0.0.0.0 is used to accept all IPs, thus of course it supposes to restrain the range at a higher level in our VPC configuration…

One last tip is to override your Java Options by defining the `JAVA_OPTS` environment variable:

```
-server -Xms128m -Xmx1024m -XX:MetaspaceSize=96M -XX:MaxMetaspaceSize=384m -Djava.net.preferIPv4Stack=true -Djboss.modules.system.pkgs=org.jboss.byteman -Djava.awt.headless=true -Djboss.as.management.blocking.timeout=3600
```

We have increased default JAVA_OPTS `-X` values that will be tuned according to your memory space consumption, garbage collection activity, etc.

We have also added a bigger timeout value on `-Djboss.as.management.blocking.timeout` as we have experienced troubles with some nodes that took more time to stabilize themselves when under heavy CPU load with many nodes…

With all these cards in hand, you should now be able to run a Keycloak cluster on your local environment or on your Cloud environment.

In the [next part](https://blog.ippon.tech/feedback-keycloak-high-availability-in-cloud-environment-aws-part-3-4/), we will explain how to set up a low-cost load testing environment that will simulate a real amount of users on our cluster.
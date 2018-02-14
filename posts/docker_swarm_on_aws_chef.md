---
authors:
- Mallik Sambaraju
categories:
- Devops
- AWS
- Docker
- Chef
date: 2017-05-01T14:34:31.000Z
title: "Docker Swarm on AWS using Chef"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/04/Docker-Swarm-on-AWS-Blog.png
---

## Synopsis

While there are many ways to initialize and configure a Docker Swarm on AWS infrastructure, this experimental project provides a particular way to configure a Docker Swarm using Chef. The Chef recipes can be provided to Opsworks stacks to automate the Docker Swarm configuration or can be executed manually on individual EC2 instances using chef-solo.

## Architecture

The architecture uses AWS DynamoDB for storing the swarm-related metadata. Docker Swarm is initialized on a leader node by running appropriate recipes. The leader node is responsible for storing the swarm metadata into DynamoDB. The metadata includes Docker Swarm join tokens for manager as well as worker nodes and the manager address. The manager node(s) retrieve the metadata from the DynamoDB and uses it to join the swarm; similarly, the worker node(s) retrieve the metadata from DB and uses the information to join the swarm. 

The image below illustrates the architecture:

<img src="https://raw.githubusercontent.com/msambaraju/DockerSwarmChef/master/DockerSwarm.jpg" width="400" height="400" />

##### Tools and services used in this approach

* **AWS S3**:  S3 service is used to store the packaged cook books which will be used by Opsworks/chef-solo to execute the recipes. Alternatively, any source code repository such as Github can be used. <br><br>

* **AWS Dynamo DB**:  A DynamoDB table is used to store the information needed to configure the swarm such as join tokens and manager address information. <br>

* **AWS CLI**: AWS CLI is used to execute AWS DynamoDB APIs, such as putting/getting swarm information. <br>

* **Chef development kit**:  The ChefDK is used to create and manage cookbooks. <br>

* **Berksfile**: The Berksfile, which is now part of ChefDK, is used to manage dependencies and also package the cookbooks.


## Recipes
* **settings**: The settings recipe is used to initialize settings, such as providing a unique name to the swarm. The unique name is used to add the swarm settings into the DynamoDB such as *<uniquename>_worker_token* or *<uniauename>_manager_token*. The settings recipe is executed on all the swarm nodes. A settings.json can be passed to the chef which has the unique name information along with other information needed for the recipes. <br>

* **default**: The default recipe is used to install Docker service. The default recipe is executed on all the swarm nodes. <br>

* **swarminit**: The swarminit recipe is used to initialize the swarm on a leader node, and the worker and manager tokens are determined. The identified tokens are added into DynamoDB. <br>

* **addtokens**: The addtokens recipe is used to add the manager token, worker token, and manager address into the DynamoDB. The recipe uses the AWS CLI commands to put items into the DynamoDB. <br>

* **joinmanager**: The joinmanager recipe is executed on the manager nodes so that the node can be joined into the swarm as a manager node. The joinmanager retrieves the token and address information from the DynamoDB and executes the _docker swarm join_ command to  join the swarm. <br>

* **joinworker**: The joinmanager recipe is executed on the worker nodes so that the node can be joined into the swarm as a worker node. The joinworker retrieves the token and address information from the DynamoDB and executes the _docker swarm join_ command to  join the swarm cluster.


##### Recipes to be executed

* On a leader node: settings, default, swarminit, and addtokens recipes need to be executed.
* On a manager node: settings, default, and joinmanager recipes need to be executed.
* On a worker node: settings, default, joinworker recipes need to be executed.
<br><br>
##### Settings can be passed to recipes using json

	{
	  "swarmname": "TestSwarm",
	  "run_list": [
		  "recipe[dockerchef::settings]",
	       recipe[dockerchef::joinmanager]"
	  ]
	}
The run_list is provided when running chef-solo manually.

## Code examples

##### Initializing Swarm

	swarmInitCommand = "docker swarm init"
	Chef::Log.info("Initializing Swarm")
	shell = Mixlib::ShellOut.new("#{swarmInitCommand}")
	shell.run_command
	Chef::Log.info("Swarm Initialized "+ shell.stdout)

<br>
##### Determine the swarm tokens

	swarmManagerToken "docker swarm join-token -q manager"
	Chef::Log.info("Obtaining Swarm Manager Token")
	shell = Mixlib::ShellOut.new("#{swarmManagerToken}")
	shell.run_command
	token = shell.stdout
	// read the token received from docker and store it for subsequent recipes
	node.set['swarm']['managertoken'] = token 
	Chef::Log.info("Obtained Swarm Manager Token : #{token}")

<br>
	
##### Add manager, worker tokens, and manager address to the DB
    
    managerToken = node.set['swarm']['managertoken'] // determined by swarminit recipe
    
    // json template based on the db structure
	putItemTemplate =   "{\"TokenKey\": {\"S\": \"<key>\"},\"TokenCode\":{\"S\": \"<value>\"}}"
	item_hash = JSON.parse(putItemTemplate)
	
	item_hash['TokenKey']['S'] = managerKey 
	item_hash['TokenCode']['S'] = managerToken
	managertokenitem = item_hash.to_json
	
	command = "aws dynamodb put-item --table-name SwarmMetaTable --item '#{managertokenitem}'"
	Chef::Log.info("Putting item")
	shell = Mixlib::ShellOut.new("#{command}")
	shell.run_command

<br>
##### Join nodes into the swarm
    // Obtain the token and manager address by getting the item from the dynamo db
     
	swarmJoinCommand = "docker swarm join --token #{token}  #{managerAddr}"
	Chef::Log.info("Worker Joining Swarm with command #{swarmJoinCommand}")
	shell = Mixlib::ShellOut.new("#{swarmJoinCommand}")
	shell.run_command
	Chef::Log.info("Worker Joined Swarm "+ shell.stdout)

<br>
##### Example command to execute recipes
	chef-solo -j <path to json> -r <url to the cookbook tar file>

## Further Enhancements
The cookbook can be further enhanced to support autoscaling of the worker/manager nodes and provide support for TLS for communication. Cookbook can be further enhanced to provide configuration parameters such as custom port numbers, DB table names, etc.

## Sample implementation
A sample implementation can be found at <a href="https://github.com/msambaraju/DockerSwarmChef">DockerSwarmChef</a>.

If you have any questions about Docker Swarm, Chef, or Ippon Technologies USA, we'd love to hear from you! Please send your comments and inquiries to [contact@ippon.tech](mailto:contact@ippon.tech).

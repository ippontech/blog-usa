---
authors:
- Lane Jennison
tags:
- Ansible
- DevOps
- Fargate
date: 2019-01-06T12:21:50.000Z
title: "Make client handoffs a breeze with the power of DevOps"
image: 
---

A very small start-up approached us do a POC build of their core business product.  They had zero technical staff or infrastructure, and we needed to build something quickly and easy to hand off to a gig-based technical team of the client’s choosing.

We opted for a simple container based front and backend microservice that was easily bootstrapped with jhipster.    We were able to do so without any VMs, and were able to balance the simplicity of AWS’s modern serverless frameworks and the familiarity of the some of the traditional services they provided:   

* ECS Fargate -> Container Management
* Cloudwatch -> Logging
* ELB Application Load Balancer -> Proxying / App routing, availability
* Route53 -> DNS
* RDS -> PostgreSQL

That balance let us codify the deployment and hand it off to a part-time devops professional to quickly bringing the site up in a live state.   Just by providing a handful of git repos and basic documentation, they were running in a weekend

## Managing automation extremes--flexible vs simple ##

```
Automate everything.

			-- Every DevOps talk, blog, and whitepaper.
```

There's a lot of pressure out there to automate, scale, and be infinitely flexible.  There's also a lot of pressure to ship.   Don't get tangled in trying to automate everythign. If you don't have some frameworks in play, don't expect to have much automation bundled into your project.  It takes time.  Balance what is flexible vs what just needs to work, while insulating yourself from the long-term impact of any [tempermanent](https://www.urbandictionary.com/define.php?term=Tempermanent) solutions.   

## Decisions that worked for us ##

### Discrete front-end and back-end microservices with containers ###
This is kind of microservices 101, but go ahead and separate front-end code early.   They will deploy and scale differently.  Segmenting also makes it easy to get truthful metrics about network traffic and system load.

### Simple automated builds and redeployment with gitlab-ci ###
Gitlab-CI's YAML job descriptions are really quick to implement and modify.  Our job would build, test, push to repo, and call for the service to redeploy in 50 lightweight lines of YAML.  The client was able to sign up for the base-level hosted Gitlab offering, and their engineer was able to drop in the repo and CI integration.

### AWS Fargate to minimize container management overhead ###
AWS Fargate has its place.  IMHO that's for small green field projects, where there's a very basic need to run containers without persistent storage, and no other infrastructure to integrate.  Our client's project met this criteria with flying colors. 

### Application routing delegated to ALB Rules ###
Rather than the front end forward API requests to the backend nodes, we let ALB URL rules route traffic to the appropriate front or backend target group.   This provided a few advantages.  Service discovery was not needed since the balancer took care of routing to the different services.   Deployments instantly become transparent because ALB's healthchecks will respond to a node's availability.

### Database connection information stored in AWS Systems Manger Parameter Store ###
Since we were using Spring Boot, we had a lot of configuration methods to choose from.   Due to the small scale of the application, we opted for 12-factor style config by setting environment variables for the JDBC path and credentials.   AWS Systems Manager Parameter Store was a simple solution that could easily be extended for more configurations.  We created Secure Strings for the database configuration where the keys were spring environment variables prefixed with the `/ourapp/db/` path.

To inject the configuration, the parameter store path prefix `/ourapp/db/` was defined as an environment variable in the service's task definition.   A function was added to the docker entrypoint shell script to extract the values from the parameter store and inject into the environment.   The only additional package we had to add to our container image was `jq`.


```
get_parameter_store() {

if [ -z "$PARAMETERS_PATH" ]; then
  echo "please set PARAMETERS_PATH in environment to parameter store path"
  exit 1
fi

echo "checking paramter store path ${PARAMTERS_PATH}"
PARAMETERS=$(aws ssm get-parameters-by-path --path ${PARAMETERS_PATH} --with-decryption)

for row in $(echo ${PARAMETERS} | jq -c '.Parameters' | jq -c '.[]'); do
    KEY=$(basename $(echo ${row} | jq -c '.Name'))
    VALUE=$(echo ${row} | jq -c '.Value')

    KEY=`echo ${KEY} | tr -d '"'`
    VALUE=`echo ${VALUE} | tr -d '"'`

    export ${KEY}=${VALUE}
done
}
```  

The aws cli tools in the container will automatically have access to any resources specified in the [Task Execution Role](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task_execution_IAM_role.html).   We added a policy for the task's role to access the parameter store.

```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "VisualEditor0",
            "Effect": "Allow",
            "Action": [
                "ssm:GetParametersByPath",
                "ssm:GetParameters",
                "ssm:GetParameter"
            ],
            "Resource": "arn:aws:ssm:us-east-1:XXXXXXXXXXXXX:parameter/ourapp/*"
        }
    ]
}
```


### Config-driven ansible role to build VPC environment and deploy ###
Although Terraform would likely be fewer lines of code and Cloudformations would be the most AWS native, we chose to use Ansible for our VPC build.   Since the deployment code would be used by other people, we felt that Ansible code would be the easiest to read and modify.  The role was a great time server. We were able to build concurrent test environments by changing only a few configuration values.

The role will be covered in more depth in another blog post, but here are the functionality highlights:

* Config driven and sane defaults provided.
* Builds VPC
* Creates subnets for DMZ, data, web, and application roles
* Creates security groups for DMZ, data, web and application roles
* Configures ECS Fargate Cluster
* Creates task definitions and services in ECS
* Creates ALB target groups for services
* Configures Load Balancer for target groups.

## Additional Thoughts ##


### Spend enough energy to assure that back-end and front-end nodes can be scaled easily ###
Don't worry about auto scaling out of the gate.  Do assure that _someone_ can easily launch additional nodes or replace nodes with larger instances.   

### Codify security practices up front ###
Have the pattern in place for secrets management, even if the provider isn't there.  12-factor style is a good place to start. 

Preconfiguring network subnets, and security in the infrastructure deployment code can assure that safe practices for network security make it all the way through deployment.   Having it codified also creates more visibility to help developers build with security in mind. 

### Make sure your toolbox has tools in it ###
It takes time to build reliable infrastructure code.   Identify your common design patterns and build flexible deployment code before you need it.


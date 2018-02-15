---
authors:
- Aaron Throckmorton
categories:
- Cloud
- Devops
- AWS
date: 2017-03-22T09:19:00.000Z
title: "Infrastructure as Code with CloudFormation"
image: https://s3.amazonaws.com/uploads.hipchat.com/113432/4463186/8OFPzOzq2TlVvxi/Infrastructure%20as%20Code%20Blog.png
---

[CloudFormation](https://aws.amazon.com/cloudformation/) is an Amazon Web Service that allows users to manage and provision AWS resources with templates. This method of implementing resources is known as Infrastructure as Code (IaC). 

___

# Infrastructure as Code 

IaC, to put it simply, is a programmatic means of configuring and provisioning infrastructure. With cloud and DevOps efforts gaining importance in modern software development, IaC helps us make rapid and powerful changes to our processes. Solving many common problems such as:

**Visibility** - If you’re working in large teams, it’s easy to lose track of what resources and configurations are sitting in your environment. With IaC, your infrastructure is checked into version control and (hopefully) filled with descriptive comments. 

**Consistency** - Developers are always chasing [idempotence](https://en.wikipedia.org/wiki/Idempotence#Computer_science_meaning) in their software efforts. We expect certain operations to return the same result repeatedly when called. Likewise, we search for idempotence in our infrastructure. IaC can be relied on to return the same result, deploying the correct configurations and resources, every time.

**Complexity** - Provisioning infrastructure shouldn’t require a sysadmin. Developers should be empowered to change the environment as new challenges are presented. Writing IaC is easy with descriptive and simple domain specific languages. 
___
# CloudFormation 
![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/02/cloudformlogo.png)

IaC has been implemented by many companies and open-source projects like Chef, Puppet, and Ansible. CloudFormation, introduced in 2011, stands out in a big way. The major caveat being that you can only use CloudFormation with Amazon Web Services. With Amazon dominating the cloud market, that shouldn’t be a problem for developers seeking IaC in the cloud.

CloudFormation definition files are templates (CFT) that model your resources, configurations, dependencies, and parameters. Templates are created with JSON or YAML files but can be generated with programming languages. 

The basic template anatomy in JSON:

```JSON
{
  "AWSTemplateFormatVersion" : "version date",

  "Description" : "JSON string",

  "Metadata" : {
    template metadata
  },

  "Parameters" : {
    set of parameters
  },

  "Mappings" : {
    set of mappings
  },

  "Conditions" : {
    set of conditions
  },

  "Transform" : {
    set of transforms
  },

  "Resources" : {
    set of resources
  },

  "Outputs" : {
    set of outputs
  }
}
```
CloudFormation is powerful and draws that power from AWS integration. You can utilize services like EC2, IAM, S3, and many more to create and update your cloud environment.

Define resources such as S3 buckets:
```JSON
{
    "Resources" : {
        "IpponBucket" : {
            "Type" : "AWS::S3::Bucket"
        }
    }
}
```

and EC2 Instances:
```JSON
{
    "Resources" : {
        "MyInstance" : {
            "Type" : "AWS::EC2::Instance",
            "Properties" : {
                "KeyName" : "myKey",
                "ImageId" : ""
            }
        }
    }
}
```

Stacks can be launched from the CloudFormation console or from the AWS Command Line Interface. All resources created from the template maintain a stack group that can be rolled back and destroyed together. Updating CloudFormation stacks is as simple as updating the original template and then submitting the change. Only resources that are affected by the update will be recreated or changed.

___

To reiterate, CloudFormation is a powerful implementation of IaC. Only cloud providers like Amazon can offer IaC at such a high-level. The ability to model and reproduce virtual and physical infrastructure can offer much to any organization or developer. Personally, I keep templates on hand for quickly deploying Jenkins, JHipster, and other such tools. Any services or configurations I use often in AWS are added to my templates. Harnessing IaC with CloudFormation has been a big boost to my own efficiency as well as Ippon's. CloudFormation should be a consideration for any developers that utilize Amazon Web Services.

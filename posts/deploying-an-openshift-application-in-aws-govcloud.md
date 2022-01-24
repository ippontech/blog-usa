---
authors:
- Dennis Sharpe
- David Elizondo
tags:
- AWS
- GovCloud
- OpenShift
- FIPS
- FedRAMP
date: 2021-12-15T12:21:50.000Z
title: "Deploying an OpenShift Application in AWS GovCloud"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/12/deploying-an-openshift-application-in-aws-govcloud.png
---
# Context
Our project entailed moving an existing web application from a commercial AWS account to AWS GovCloud. In addition to moving to GovCloud, we had to adhere to FedRAMP High security guidelines. The original application was never designed to run in GovCloud. We will offer some tips on how to make a move like this as smooth as possible.

The original application was built using the following technologies:
- JHipster
- ReactJS
- Java
- Spring Boot
- PostgreSQL
- Kafka
- Redis
- OKD (Open Source RedHat OpenShift)

The target environment required:
- FedRAMP High Compliance
- RedHat OpenShift
- AWS GovCloud

# Additional Requirements
For security purposes, we limited the connections from GovCloud out to the internet. We also created our own repository in GovCloud. Lastly, we installed Nexus in GovCloud. However, *only* approved libraries are downloaded from the development environment Nexus. Of course, we made a few exceptions when necessary, such as downloading libraries from a trusted source like RedHat.

# Collaboration
In this specific case, we had a development team in another country while our team resided on the east coast of the US. For a project like this, collaboration is key. Any changes that we made to the application had to be backported to the development environment. This added an extra testing burden on the development team.

# FedRAMP High
[FedRAMP](https://www.fedramp.gov/) is a government-wide program that promotes the adoption of secure cloud services across the federal government by providing a standardized approach to security and risk assessment for cloud technologies and federal agencies. There are FedRAMP Low, Moderate, and High security levels.

Several AWS [services](https://aws.amazon.com/compliance/services-in-scope/) are not certified as FedRAMP High compliant. For example, AWS-Managed Kafka is not compliant, but Kinesis is compliant. In our case, we were able to replace the minimal Kafka functionality with Redis, which is compliant.

Partnering with a firm that specializes in FedRAMP compliance can save a lot of time. Some firms provide an environment that is ready for Authority to Operate (ATO). The security requirements are quite extensive!

_Tip #1: Check to make sure the infrastructure and application dependencies you need are FedRAMP High-certified, assuming that is the security level required._

# GitLab Setup
The original application code resided on [GitLab](https://www.gitlab.com/), but we needed that code to also reside on our private GitLab instance in GovCloud. GitLab supports “mirroring” between two repositories. However, the entire project had to be mirrored – including any development or experimental branches. Mirroring also allows two-way updates and our requirement was to not allow code updates from inside GovCloud. According to documentation, specific branches could be mirrored, but that did not work for us.

We ended up building a script to move code from GitLab to GovCloud with a configuration file that listed the particular projects and branches / tags to move. This also allowed a review gate before moving code, instead of automatically syncing like a mirror.

_Tip #2: GitLab mirroring keeps **all** branches in sync from either direction. Make sure that is what you want!_

# "Offline" Root Certificate Authority
We had an additional security requirement to house the root certificate authority in a completely isolated and “offline” environment. This needed to be a separate account from the account running the application. More user management was required because RBAC policies needed to be applied to both accounts.

Due to stricter access requirements, engineers were not given access to this new account. That meant training was required to launch an EC2 instance, connect via the AWS Console, create a Root Certificate Authority (CA), sign certificates from the application account correctly, change the CA certificate path length variable, and then package the Root Certificate in an encrypted WORM S3 bucket.

Management of the Root CA must be done manually to update the CRL or revoke certificates. Any changes to this component have impacts downstream.

_Tip #3: Create your root certificate account early so delays are not caused during the infrastructure setup due to missing certificates._

# Route53 in GovCloud
Public-hosted zones are not allowed in GovCloud, but were needed to do DNS validation for our ACME provider (i.e. Let’s Encrypt). In this case, we needed yet another commercial AWS account. Similarly to the offline Root CA, we needed more user management to apply the RBAC policies to this account as well.

![Route53 in GovCloud](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/12/route53.png)

_Tip #4: Create your public Route53 account early so delays are not caused during the infrastructure setup due to lack of DNS validation._

# OpenShift 4.x CoreOS
RedHat strongly recommends running CoreOS as the “operating system” for OpenShift nodes. RedHat also does not recommend manually modifying the base operating system and suggests changes should be done through a privileged pod that mounts directly to the host. The preferred approach is to use operators.

Operators can be installed to handle security and compliance tasks, log forwarding, and other functions that a Linux agent would normally handle. Custom operators can be built if absolutely necessary. For security purposes, we disabled root access to the nodes as well.

_Tip #5: If you need agents installed on your infrastructure nodes, verify operators exist with the needed functionality instead._

# Custom Images
Custom images were required since CIS hardened images cost additional money and we were already paying for RedHat licenses. Standard RedHat Enterprise Linux (RHEL) licenses are not hardened. We added the following items to build the custom images:
- [OpenSCAP](https://www.open-scap.org/) to validate and apply security changes
- FIPS module
- Amazon SSM for terminal access
- SIEM vendor tools such as TrendMicro, Filebeat, Auditbeat, and Nessus

_Tip #6: Use a tool like [Packer](https://www.packer.io/) to build custom images for auditability._

# FIPS Compliance
[Federal Information Processing Standards](https://www.nist.gov/standardsgov/compliance-faqs-federal-information-processing-standards-fips) (FIPS) is a mandatory standard for the protection of sensitive or valuable data within federal systems. One of the key challenges with FIPS is using compliant cryptographic modules throughout the application and infrastructure. This is particularly important for any encrypted communication between components.

Tools like GitLab are not fully FIPS compliant. We had to get an exception to use GitLab. Since GitLab itself does not run the application, FIPS compliance was deemed less critical.

Many open source tools are not FIPS compliant. For example, several tools use MD5 for hashing, which is not FIPS compliant. Even widely-used open source products such as PostgreSQL are not compliant. We needed another exception to use PostgreSQL.

AWS Elastic Load Balancer (ELB) SSL termination is not FIPS compliant. There is a “beta” version that is compliant, but has been in beta for several years. This can be used with a special request to AWS.

_Tip #7: Verify that **all** your components are FIPS-compliant and start asking for exceptions early if tools you need are not compliant._

# Turning on FIPS
Software that is FIPS-compliant frequently requires a configuration change to turn on compliance.

For example, to enable FIPS on RHEL, we had to install and run “dracut,” update the grub config file, and restart the machine. There are differences between configuring RHEL 7 and RHEL 8 to enable FIPS.

The OpenShift installer configuration file needed to be modified to turn on FIPS compliance.

Turning on FIPS in Java required more than just turning it on for the OS / container.

_Tip #8: In many cases, the best approach was to turn on FIPS-compliance and simply see what broke._

# FIPS with Java
Since Java runs in a virtual machine that is intended to run consistently everywhere, it does not rely on the underlying operating system for cryptology libraries by default. The default keystore and truststore are not FIPS-compliant. The stores must be switched to “NONE,” which tells Java to use an operating system specific store (i.e. /etc/pki/nssdb/). The store type must be changed to PKCS11.

The Java security file must be updated to use FIPS-compliant encryption schemes. For example, the first security provider must be a FIPS-compliant security provider (i.e., SunPKCS11).

The keystore must be set up in the init container because it is a best practice to create it as read-only for the main application. Additionally, the operating system must support an external keystore. We used the ubi8 JDK instead of the standard AdoptOpenJDK for FIPS compliance.

_Tip #9: PKCS11 cannot be used on a non-FIPS-enabled machine._

# Conclusion
There are many steps required to take an existing application and deploy it into AWS GovCloud. If you are building an application from scratch for GovCloud, keep in mind the tips above to save time when it comes time to deploy. If these steps seem too daunting, reach out for help.


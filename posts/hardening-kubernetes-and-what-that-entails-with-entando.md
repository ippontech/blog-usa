---
authors:
- Lucas Ward
tags:
- entando
- kubernetes
- devops
- docker
date: 2022-12-20T13:33:00.000Z
title: "Hardening Kubernetes and What That Entails With Entando"
image: 
---

Kubernetes is an open-source system for automating containerized applications' deployment, scaling, and management. In recent years Kubernetes has gained immense popularity, likely due to its declarative nature. With Kubernetes, most infrastructure details are abstracted away from the user, which makes for an "agile-ready" environment. However, securing applications in this environment can be challenging because of this abstraction and Kubernetes' underlying architecture. This security landscape is *not* the same one that enterprise companies are used to, let alone small to medium-sized businesses and startups.

# Entando and Kubernetes Security

Entando is an application composition platform. It leverages free and open-source technologies, like JHipster and Kubernetes, to build modularized applications. Think Microservices plus Micro Frontends plus a drag-and-drop editor (check out their [blog page](https://entando.com/page/en/blog)).  How can a low code/pro code application composition platform built on top of Kubernetes help with security?  The answer lies between opinionated architecture guardrails and even more abstractions surrounding application development. Mistakes occur because of the choices surrounding how to build an application. Flexibility is a double-edged sword. With an opinionated framework, particular security best practices can be applied by default. Let's start by looking at the security landscape of K8s (shorthand for Kubernetes).

Over the past few years, you may have heard the term "shift security left." Even though this is a "buzz phrase" for security companies to use, it is good advice. Shifting security left means handling security early and often in the development cycle. When using Kubernetes for your project, you will need to consider security during development, infrastructure setup, the build process, and deployment. This setup, of course, is in addition to your runtime observability and threat detection strategies, which look a bit different in Kubernetes compared to a more traditional infrastructure setup.

# The Kubernetes Threat Matrix

What types of threats do we need to protect against when building with Kubernetes? There are many ways for a threat actor to compromise your system. When building with Entando, the same threat vectors exist. Here is a short, non-exhaustive list summarized from the Threat Matrix for Kubernetes published by Microsoft.
 * Threat actor gains Initial Access through:
   * Compromised images in the image registry.
   * An exposed kubeconfig file.
   * A vulnerability in the application running on k8s.
 * Threat actor executes malicious code via:
   * Sidecar injection.
   * Spinning up a new container.
   * Running a shell inside an existing container.
 * Threat actor seeks to:
   * Persist within your environment through various means.
   * Escalate their privileges within your environment to...
   * Evade detection, access credentials, and move laterally through your systems.

All this is to collect valuable data or cause damage to your organization. Entando, built on top of Kubernetes, is open to these same attack vectors and potential security vulnerabilities. The good news is that many best practices and tools have recently emerged to help organizations protect against these threats, and Entando comes with a lot of them "baked in" to the framework. After reviewing some of the tools at our disposal and some of the best practices, we will discuss how Entando pushes the envelope of good security practices to the max, with very little pain to the developers to boot.

# Shift Security Left

As mentioned above, "shift security left" means thinking about security early and often during the software development life cycle (SDLC). There are several best practices to follow when building an application on Kubernetes and building the infrastructure that Kubernetes runs on.

## Development, Build, and Infrastructure Best Practices

### Entando and JHipster

Entando relies on JHipster for code generation and project scaffolding.  Using code generators is a great way to ensure that your project has a structure and base setup that follows best practices. When developing applications, always follow your language of choice's best practices. Once a component is created, whether a microservice or a micro frontend, you will likely be containerizing it for deployment onto k8s. 

### Base Image Vulnerabilities

A strong foundation is key.  Base images are a common and well known threat vector that are a constant source of risk.  Entando provides hardened base images for Java and Postgres.  Their core component images are built on top of Redhat's ubi8 (universal base images).  These base images make for a quick pass regarding image scanning; they are minimal and have only the necessary software installed to perform their dedicated task.

Entando comes with a handy command line tool that will help you containerize your "micro components".  Image scanning comes in handy at this point in the process. There is a load of different tools out there that can scan your docker images for common vulnerability enumerations (CVEs). A popular choice is "docker scan." This suite of scanning tools looks for known vulnerabilities within operating systems, packages, and languages.  Speaking of image scanning, let us talk about where image scanning should live.  In the pipeline of course!

### Entando Pipelines

Having secure containers is excellent, but if the pipelines they move through aren't secure, it will make little difference.  Entando has well documented CI/CD process that provide a GitFlow-like workflow.  These pipeline recommendations align with the same ethos we have seen thus far.  Add opinionated architecture and guardrails to minimize the number of human errors through the reduction of guesswork. More information can be found about Entando's CI/CD support [here](https://developer.entando.com/next/tutorials/create/pb/github-actions-workflow.html#overview) or Entando's own opinionated build pipelines [here](https://github.com/entando/entando-pipelines).

### Additional Security Considerations

In addition to using Entando's set of built-in images and tools, some other things that can lend to a strong security posture include: 
* Using managed services from any of the major cloud providers. 
* Use a secrets manager and encrypt any secrets at rest.
* Reduce Pipeline permissions. In other words, use the [***Principle of Least Privilege***.](https://csrc.nist.gov/glossary/term/least_privilege)

Using Entando as your platform of choice covers a lot of ground in the Development, Build, and Infrastructure scape. For one, the base images for their cluster resources are thoroughly vetted and frequently updated. Their platform dependencies and OS processes are simple and specific to the application components. Instead of building your own image, you are using an opinionated image developed by an organization. Just like using managed resources, this makes security a ***shared concern***.  Combined with their pipeline tools and image scanning, Entando users are off to a great start when it comes to "shifting security left".

## Deployment and Runtime Best Practices

### Out of Box With Entando

Utilizing a platform like Entando can help maintain a secure environment. For instance, when the Entando CLI (ENT CLI) is used to deploy and install images, they exist within the context of the platform. Therefore when they are composed into an application, the permissions and component-to-component communications are subject to the trust model employed by the platform. This model removes many manual human interaction mistakes commonly made when configuring your applications without the help of a low-code platform. It only takes one slip-up to hang a big "come in, we're open" sign on the cluster. Having a platform automate most of the deployment process is a big win regarding maintaining speed and security posture.

### Cluster Hardening

A well-vetted and secure Kubernetes production environment is a bit of a lift. The initial Entando setup seamlessly walks through several areas of best practices regarding cluster hardening.  Cluster hardening entails reviewing cluster configuration and ensuring that the right components have the proper permissions to talk to precisely whom they need to talk to.  This review also means using RBAC (role-based access control), labels, and admission controls. In addition, the Kubernetes API server will need to be secured using something like TLS. This action will encrypt the traffic between the components within the cluster.  If Entando is set up using the guide on their website, all of the aforementioned will be configured and ready to go.

### Host Hardening

Once the cluster is adequately secured and internal traffic is trusted, consider hardening the cluster's host. Much of this is available by default if using a managed service such as GKE or EKS.  When self-hosting a k8s cluster, the perimeter network must be secured.  This is more akin to traditional web server best practices: employing a firewall and web access control gateways. Be thorough and think through the trust model when exposing services to the outside world.


## Runtime Best Practices

After launching everything into the cluster, how can it continue to be monitored? Observability can be achieved via many tools that are well-defined, and well-documented. One such tool, made by the *masters of kubernetes observability*, elastic.co, can be found [here](https://www.elastic.co/guide/en/observability/current/monitor-kubernetes.html).  Although it is paramount to understand how to observe, it is essential to consider ***what*** to observe. To keep things secure and be able to detect anomalous activities, insight is needed into your cluster.

### Application Monitoring

One solution to gain monitoring context is to utilize Kubernetes Namespaces and resource tagging.  When deploying your applications with Entando, namespaces and tags are going to be applied by default.  This is especially apparent for the initial Entando Hub deployment.  Since everything is already nicely segmented into different namespaces and naming conventions are easy to follow, observability should be much easier to tackle.

### Network Monitoring

Having visibility into the application and its logs is not enough.  It is also best to have visibility into network communications within the cluster. Historically, monitoring networks would involve logging the destination and source IP Addresses and Ports. However, Kubernetes can switch the pods' IP addresses, so this is no longer a viable solution. This is why it is best to monitor the network communications with the added context of the pods' purpose or its namespace.

All traffic to/from the cluster and application should always be encrypted.  As mentioned before, the default Entando setup includes instructions on how to enable TLS and add SSL certificates to the cluster and applications.  In addition to these secure protocols, enabling cluster auditing in Kubernetes to keep an eye on all of the request and response cycles is paramount.  Deploying a machine-learning-enabled endpoint monitoring tool will be the best bet.

## Entando has it Covered

Entando shines through as a step in the right direction in this wild west security scene that is known as Kubernetes. Because the platform manages the development (via JHipster), the deployment (via ent cli), and the composition (via the low code environment) of your application, it dramatically reduces the margin for error. Having your application be made up of tiny, atomic, composable units allows you to utilize all the power of Kubernetes, with all the guardrails of an Enterprise development platform.  Entando aids in the process of shifting security left by *enabling* best practices to be followed with minimal effort from the Engineering team.

# Conclusion

Building application's on Kubernetes is difficult. Securing said applications is even more of a heavy lift. In an environment that is this cutting edge, things change fast. A platform like Entando can help you maintain a secure and stable environment. If you want to learn more about Entando, check out the website [here.](https://entando.com/). If you need help implementing Kubernetes or Entando in your organization, drop us a line at contact@ippon.tech.

---
authors:
- Florian Garcia
categories:
- Docker
- Devops
date: 2017-02-05T14:29:00.000Z
title: "Please welcome Docker 1.13"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/03/Welcome-Docker-1.13-Blog.png
---

On January 19th, version 1.13 of Docker was released! As with every minor version (for Docker because it is not so minimal), it brings a lot of fixes and improvements but also some nice new features. Let’s take a look.

### Unification and improvement of the CLI
Previously to display the containers list, you had to type `docker ps`, `docker images` for the images, or `docker volume is` for the volumes.

It was time to harmonize all that!

With Docker 1.13, we say goodbye to `docker ps` and `docker run` and say hello to `docker container ls` and `docker container run`. In reality they are still supported, but it’s time to change our habits. We now have a much more uniform and organized CLI with those "management commands." Easy and much more intuitive!

By executing the `docker help` command, you will be able to see the new management commands as well as the historical ones.
<p align="center">
![Management commands help](https://blog.ippon.fr/wp-content/uploads/2017/01/management_commands.png)
</p>

Note also the appearance of “system” commands which make it possible to obtain information on the interactions between Docker and the host system. For example, we can see the used disk space by the various Docker objects (containers, images, and volumes) using the `docker system df` command.

![Docker system df in action](https://blog.ippon.fr/wp-content/uploads/2017/01/df.png)

Avid Docker users will enjoy another new “system” command. Until now, if you wanted to do some cleaning and remove the unused containers, you had to run `docker rm $ (docker ps -aq) `. Tedious, don’t you think?

Now, using the `docker system prune` command, you can delete all unused objects.

![Docker system df after prune](https://blog.ippon.fr/wp-content/uploads/2017/01/prune.png)

And voilà, a clean machine!

###Introduction of the “stacks” notion
When you need to start multiple containers with a list of parameters often several miles long, you use Docker Compose. To launch containers on a cluster of machines, you use Docker Swarm, but the long list of parameters remained.

With this latest version, you can now use your docker-compose.yml files to deploy your containers on a Swarm cluster. This is the new power of Stack!

For those who had tested the stacks in an "experimental" version, it was necessary to go through a "bundle" (.DAB) transient stage between Compose and Swarm. Tedious!
<p align="center">
![Docker compose for stacks](https://blog.ippon.fr/wp-content/uploads/2017/01/compose.png)
</p>

Note that the syntax of the file is equivalent. When you switch to version 3, it allows you to use new functionalities. You can specify, as in the previous case, instructions for deploying with Swarm, the number of replicas and policy updates, or rules on the machines used to deploy our instances. You can then deploy your stack on the Swarm cluster using the `docker stack deploy -compose-file stack.ym` command. Easy!

### The newcomers
This version of Docker further improves the support of Docker on Windows. Swarm in particular is completely supported.

But that's not all! "Docker for AWS" and "Docker for Azure" are no longer in beta!

What are they? These are tools that will simplify the use of Swarm for these cloud providers. For example, for AWS after the initial configuration of the tool, "Docker for AWS" will create security groups and subnets, instantiate managers and workers, and allow you to deploy applications on your cluster Swarm in the cloud in a few commands!

This list is obviously not exhaustive. I could talk about the addition of secret management in Swarm or the possibility to switch to "experimental" using a simple flag. For more detailed information, I invite you to read the release's notes and of course to try everything on your computer.

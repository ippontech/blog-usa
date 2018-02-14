---
authors:
- John Zaccone
categories:
- Docker
date: 2016-06-16T12:48:33.000Z
title: "Docker Engine 1.11: Understanding Runc"
image: 
---

The release of Docker 1.11 is the first time that the Docker Engine is compliant with the OCI specification.

What is OCI?  
 The [Open Container Initiative (OCI)](https://www.opencontainers.org/about) is an open governance project for the purpose of creating open industry standards around container formats and runtime. Container technologies have seen rapid growth over the last couple years and an open standard is needed to allow portability of containers across different vendors. Docker has been the de facto standard based on its overwhelming popularity, but there is a need for a more collaborative effort with the backing of a [representative group of industry leaders](https://www.opencontainers.org/about/members).

The result of the OCI is the OCI specification, that defines standards around container formats and runtime. The first implementation of the specification is [runc](https://github.com/opencontainers/runc), which was contributed to open-source by Docker. Starting with Docker 1.11, the docker engine uses the runc format as the underlying technology for creating and running docker containers.

Today, I will go through the steps to run a container using only runc. We will then run a container using Docker Engine 1.11 and find the evidence of runc underneath the covers.

#### Setup

This tutorial uses runc 0.1.1 and Docker Engine 1.11 that both use OCI spec 0.6.0.

Install [Docker Engine Version 1.11](https://docs.docker.com/engine/installation/).

Install [runc 0.1.1](https://github.com/opencontainers/runc/releases):

I used [these commands](https://gist.github.com/jzaccone/f1e292c42d9e1b89ae83c35b1b5e96cf) to successfully install runc on a RHEL instance on aws.

#### Create a Runc Container

The open container specification is defined by a [file system bundle](https://github.com/opencontainers/runtime-spec/blob/master/bundle.md) and a compliant runtime (like runC) that can consume the bundle. Now that we have runc installed, let’s create the bundle.

The bundle consists of:  
 1. a folder named “rootfs” that represents the root file system of the container.  
 2. config.json that defines configuration

To make things easy, let’s use docker to export a rootfs, then we can use the `runc spec` command to generate a config.json file.

```
<code class="language-bash code-embed-code">mkdir rootfs
sudo docker pull busybox
sudo docker export $(sudo docker create busybox) | tar -C rootfs -xvf -
```

Use the `runc spec` command to generate the config.json file:

```
runc spec
```

You will now have a config .json file and a rootfs in your current directory. This file will have the OCI version in it:

[![configjson](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/06/Screen-Shot-2016-06-09-at-1.49.21-PM.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/06/Screen-Shot-2016-06-09-at-1.49.21-PM.png)

Start a container with the name ‘test’: `sudo runc start test`:

[![Runc Container](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/06/Screen-Shot-2016-06-09-at-1.42.29-PM.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/06/Screen-Shot-2016-06-09-at-1.42.29-PM.png)

Leave this container running for the next part of the tutorial.

#### Inspecting Docker Engine 1.11

Open up a new terminal. List the running containers using `sudo runc list`

[![Listing Containers Runc](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/06/Screen-Shot-2016-06-09-at-1.32.26-PM.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/06/Screen-Shot-2016-06-09-at-1.32.26-PM.png)

You should see your test container running. Now, lets start a container using docker engine.

```
sudo docker run -d nginx
```

Run `sudo runc list` to see the new docker container alongside your runc container. You can use `sudo docker ps` to compare the IDs.

![Comparing Docker to Runc](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/06/Screen-Shot-2016-06-09-at-1.37.40-PM.png)

You can find the runc config.json for your docker container in /var/run/docker/libcontainerd/{imageID}.

![Docker Config Json](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/06/Screen-Shot-2016-06-09-at-1.53.40-PM.png)

The config.js will show you the OCI version as well as the location of your rootfs which in my case (using devicemapper storage driver) is in the /var/lib/docker/devicemapper/mnt directory.

#### Docker Engine and Runc

Docker is built on top of runc, but there are actually a couple more components involved.

![Docker Runc Architecture](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/06/dockerrunc-3.png)

[Containerd](https://github.com/docker/containerd) is a daemon to control runc, built for performance and density. You will find evidence of this if you run `ps -ef | grep docker` on your host machine.


![Docker ContainerD PS ](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/06/Screen-Shot-2016-06-09-at-2.06.03-PM.png) 

 The first two (Docker and docker-containerd) are daemons that run on the host. The docker-containerd-shim process is the result of starting my one docker container.

Detailed information about the OCI specifications:  
 Image Specification: [https://github.com/opencontainers/image-spec](https://github.com/opencontainers/image-spec)  
 Runtime Specification: [https://github.com/opencontainers/runtime-spec](https://github.com/opencontainers/runtime-spec)

I hope this was helpful in understanding how docker interacts with the OCI specification. Send me your thoughts on twitter [@JohnZaccone](https://twitter.com/JohnZaccone).

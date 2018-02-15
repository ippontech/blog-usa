---
authors:
- Laurent Mathieu
categories:
- Docker
- vagrant
date: 2014-10-08T04:12:27.000Z
title: "Docker & Vagrant Introductions"
image: 
---

The following post takes a look at two virtualization technologies; where they came from, their role in the current Development and DevOps world and where they might be going.

**Virtualization Previously**

Before we really dive in to either Docker or Vagrant, we should talk briefly about virtualization.  The earliest traces of virtualization in computer science history occurred around the [late 1960’s and early 1970’s in IBM research facilities](http://docs.oracle.com/cd/E26996_01/E18549/html/VMUSG1010.html).  Data centers today use virtualization techniques to make abstraction of the physical hardware, create large aggregated pools of logical resources consisting of CPUs, memory, disks, file storage, applications, networking, and offer those resources to users or customers in the form of agile, scalable, consolidated virtual machines. Even though the technology and use cases have evolved, the core meaning of virtualization remains the same: to enable a computing environment to run multiple independent systems at the same time. (1. Oracle Brief History).  VMWare and other tech industry leaders all have somewhat proprietary implementations of a Hypervisor to manage virtualized environments.

Virtualization optimizes computing resources, makes infrastructure management more efficient, improves uptime, reduces damage of server hardware failures and so on and so on.  If you still aren’t sold that it’s a good idea, go order parts from Newegg and construct a bare metal server from scratch while I click around on Amazon Web Services for six minutes and we can compare the amount of time and money I saved provisioning my new test server vs. yours.

**Virtualization Now**

Recently, you could say virtualization has been adopted and somewhat redefined by cloud offerings.  AWS leads the race in PaaS and IaaS products and every week there is seemingly a new player.  Without exaggerating, one can have a new dev server up in just a few minutes which is a really exciting time for the industry.  But even if I choose an AMI (I’ll just stick to Amazon terms for consistency) that has mostly what I need in my new server, there is still some buildout overhead to get it fully operational and consistent with my other existing environments.  Docker and Vagrant have both emerged as DSL’s (domain specific languages) of sorts to provision virtual environments in a prescribed manner.

**What is Docker?**

Docker is actually a re-engineered, open source version of a proprietary effort that dotCloud (another PaaS company) was working on to make deploying software applications easier and consistent in their environments.  Solomon Hykes began the project and its first versions (up until 0.9) were an abstraction layer to lxc, Linux Containers.  In version 0.9, Docker has its own libcontainer implementation, written in the Go programming language.  Docker has become the primary focus of dotCloud and has raised over $55 million in funding over the last two years.

**What is Vagrant?**

Vagrant was a project started by Mitchell Hashimoto in 2010, written in Ruby.  Then in 2012, HashiCorp was founded to develop the product on a full-time basis.  With each release, new features are implemented and adoption for different providers and provisioning tools is expanded AND as of version 1.6, Vagrant offers support for Docker!  HashiCorp’s focus is now beyond Vagrant now with products like Packer, Serf, Consul, and Terraform all emerging and the company positioning itself to become a DevOps leader across the board.

**Vagrant & Docker Similarities**

It is important to note before discussing any similarities between Vagrant and Docker, that they address similar problems but at a different level of abstraction.  And as we just noted, they can now even be used in tandem.  When first delving into both, I actually found a pretty cheeky Stack Overflow post with both product founders squaring off a bit!

[![Screen-Shot-2014-10-08-at-10.11.27-AM](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2014/10/Screen-Shot-2014-10-08-at-10.11.27-AM.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2014/10/Screen-Shot-2014-10-08-at-10.11.27-AM.png)

Image credit:  http://stackoverflow.com/questions/16647069/should-i-use-vagrant-or-docker-io-for-creating-an-isolated-environment

Fundamentally both are virtualization consistency tools, both are open source projects, both implement a style of version control and repositories (Docker Hub, Vagrantbox.es) and they both are great tools to finally address the “works on my machine” developer blanket excuse for their code not working properly.

**So What Are The Differences?**

1. Level:  Docker manages Virtual Environments, Vagrant manages Virtual Machines.
2. Supported OS Platforms:  Docker:  Linux** (we will talk about this),  Vagrant:  Linux, OS X, Windows
3. Vagrant guarantees hardware resources, Docker does not
4. Vagrant has a larger footprint on a machine
5. Docker environments start in just seconds, Vagrant ~minute depending on what is being performed on “vagrant up”
6. Docker environments are partially isolated, Vagrant is full isolation

[![vagrant-docker-weight](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2014/10/vagrant-docker-weight.jpg)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2014/10/vagrant-docker-weight.jpg)

In summary, Docker is lighter weight.  Vagrant is still lighter weight than traditional VM approaches.  It’s important to remember though, that lighter weight does not always just translate into “better”.  It depends on your use case.

**An Example Use Case For Each**

A good example to fully realize the benefits of Vagrant might be a distributed development team where members work remotely and each on their own development machines which could be running Mac OS X, Windows or some flavor of Linux.  To further the scenario, let’s say that the continuous integration server is an old bare metal machine that lives at the project headquarters, then the test servers are single nodes running on AWS and production is a distributed architecture, load balanced across different availability zones.  The same Vagrantfile and provisioning formula will keep consistency between developers, then fully up the swimlane to test and production.  Cool!

Docker would be a great solution for an agile development shop that has an array of different projects going on.  Some web, some mobile.  Different languages maybe.  Different data stores where some are relational, some are NoSQL.  In this scenario, a developer can switch from one project to another without any need for worrying about what version of X, Y, Z that each is running.  He/she also doesn’t need to have a full VM set up for everything that is being worked on.  All he/she needs is the right Docker containers for that specific app (which can be source controlled right with the project) and he/she has switched development contexts with no wasted cycles.

Now these scenarios also are not hard lines in the sand.  Docker could also be used as a solution for the first, Vagrant could be applied to the second.  To make the right decision for your development teams takes open discussion.  What are you trying to achieve and at what virtualization level?  It’s important to have a plan of action before making a decision.  The same applies for any new tool or technology.  It is about gaining the most benefit for your particular needs and not just doing something because some blog says that you should.

**How can I get started?**

Check out this great post [From Development To Production With Vagrant & Packer](http://www.ipponusa.com/from-development-to-production-with-vagrant-and-packer/).

The bar of entry for both technologies is very low.  You can have something working in a matter of minutes with each.

But if Docker is only for Linux, how can I get started on my MacBook Pro?!  No worries, Docker getting started will walk you through boot2docker setup also, which is a minimal Linux VM that will get Docker running locally right away.

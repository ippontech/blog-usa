---
authors:
- Kenneth Hegeland
categories:
date: 2015-11-10T11:10:00.000Z
title: "Vagrant and Ansible for new developer on-boarding"
id: 5a267e57dd54250018d6b62a
image: 
---

I recently discovered [Vagrant](https://www.vagrantup.com/), a tool which facilitates the task of setting up development environments, at Alexis Seigneurin’s Ippedej presentation “Vagrant for developers” (slides [here](https://speakerdeck.com/aseigneurin/vagrant-pour-les-developpeurs)).

After the presentation, we asked Alexis whether he could help us try Vagrant out on one of our projects. A new phase of the project will be starting soon, and new members will be joining the team. It seemed like a good opportunity to try out a real-life Vagrant use case. This is my account of our experience.

#### **What to put into the Vagrant box?**

The first question to ask ourselves was which parts of our project environment could be installed using Vagrant, and which it would best to leave outside the VM. Our project uses:

- Java 7
- The Liferay portal
- A MySQL database (for each developer)
- An Oracle database (shared – already installed on a VM)
- Apache ServiceMix (an entreprise service bus)
- Eclipse
- Maven

With Alexis’ advice, we decided to separate the application (on the VM) from the IDE (outside the VM). As we use an Eclipse plugin to run Liferay, it was therefore not feasible to install Liferay on the VM, so we decided to install the following elements:

- Java 7 (required as a dependency for the other two items)
- MySQL database
- Apache ServiceMix

#### **Making Vagrant (and Ansible) work under Windows**

Some of our team members work under Linux and others under Windows. One of the first challenges was to see whether it would be possible to install our Vagrant box, and provision it using Ansible, on a Windows machine. We had created the original Vagrant box on Alexis’ Mac, so it was a matter of getting that box to work under Windows.

Although Vagrant is compatible with Windows, and worked immediately, it turned out that Ansible was going to be a hurdle:

> ###### **“Reminder: You Must Have a Linux Control Machine**
>
> ###### Note running Ansible from a Windows control machine is NOT a goal of the project […].”
>
> ###### *Extract from *[http://docs.ansible.com/ansible/intro_windows.html#installing-on-the-control-machine](http://docs.ansible.com/ansible/intro_windows.html#installing-on-the-control-machine)

#### **First attempt…**

Undeterred, at first we tried the workaround described [here](http://www.azavea.com/blogs/labs/2014/10/running-vagrant-with-ansible-provisioning-on-windows/) (“Running Vagrant with Ansible Provisioning on Windows”). It was quite a long and involved process, which required the installation of various different tools (Cygwin, Python, pip – Python’s package installer…) and then several adjustments using .bat and .cfg files. When we finally reached the end of the process, we came upon an SSH authentification issue which we were not able to resolve (private key file permission error described in the comments [here](https://gist.github.com/maurizi/325387aee9ea94fbf903)).

#### **Second attempt…**

We therefore decided upon a different (and less complex) approach : installing Ansible directly on the VM using shell provisioners. This solution would allow us to bypass the problem of installing Ansible on a Windows machine. These are the steps we took:

1. Creation of an “ansible” folder in our root directory, containing a sub-folder with all our roles, a custom hosts file and our “playbook.yml” file.
2. Creation of a synced folder on the VM in order for this folder to be visible from the VM: config.vm.synced_folder "ansible/", "/home/ansible"
3. Creation of a shell provisioner to install ansible: config.vm.provision "shell", path: "scripts/install_ansible.sh" **install_ansible.sh **apt-get -y install software-properties-common apt-add-repository ppa:ansible/ansible apt-get update apt-get -y install ansible
4. Creation of a shell provisioner to install a custom hosts file for ansible: config.vm.provision "shell", path: "scripts/add_custom_hosts_file.sh" **add_custom_hosts_file.sh******cp /home/ansible/my-hosts /etc/ansible chmod -x /etc/ansible/my-hosts **my-hosts******[all] localhost ansible_connection=local
5. Use of a shell provisioner to run ansible from the VM: config.vm.provision "shell", inline: "ansible-playbook /home/ansible/playbook.yml -i /etc/ansible/my-hosts"

This approach was decidedly more successful, and we now had a Vagrant box with full Ansible provisioning capabilities!

An important point to note is that any files which were in a synced folder (for example our “my-hosts” file) were not usable by the VM, which regarded them as .exe files, provoking the following error:

> ###### ERROR: The file my-hosts is marked as executable, but failed to execute correctly. If this is not supposed to be an executable script, correct this with `chmod -x my-hosts`.

It was not possible to change the permissions on the file from Windows, so the solution was to move the file to a remote folder, and then change its permissions (cf. step 4 above).

#### **Some hitches under Linux…**

We also encountered a few problems when installing the original Vagrant box (created on a Mac) under Linux:

- It was necessary to activate “VT-x virtualisation” in the BIOS.
- The box “ubuntu/trusty64” had to be used instead of “ubuntu/vivid64”: we had problems establishing a connection with the “vivid64” version.

#### **Making improvements to our box**

Once our Vagrant box was up and running, and we could access both the remote MySQL server and the remote Apache ServiceMix from the host machine, we decided to see if we could take things a step further and create our database, install a configuration file and import a dump of our data using Vagrant. We did this as follows:

1. Creation of a “mysql” folder containing the configuration file and the dump in our local root directory and of a synced folder to expose it to the VM: config.vm.synced_folder "mysql/", "/home/vagrant/mysql"
2. Creation of a shell provisioner to execute the necessary commands via script: config.vm.provision "shell", path: "scripts/init_database.sh" **init_database.sh** cp /home/vagrant/mysql/case_insensitive.cnf /etc/mysql/conf.d chmod 644 /etc/mysql/conf.d/case_insensitive.cnf service mysql restart mysql -ulogin -ppw -e "CREATE database lportal character set utf8;" mysql -ulogin -ppw lportal < /home/vagrant/mysql/Dump.sql

Note again the need to move the configuration file to the appropriate directory on the VM, and then change its permissions.

#### **What we didn’t manage to do with Vagrant**

The only configuration work we couldn’t figure out how to do using Vagrant was the execution of some installation commands for Apache ServiceMix. These commands need to be executed *inside *the ServiceMix console itself, which is in turn accessible via the command line. We decided that for the sake of typing in a handful of commands, it might be easier just to do this part of our set-up process manually.

#### **Conclusion**

The experience of putting Vagrant into practice on a real-life project was very instructive. I learnt a lot about Vagrant and Ansible and the eventual pitfalls of using Vagrant on different operating systems. I also discovered a new tool along the way, Babun ([http://babun.github.io/](http://babun.github.io/)), a Windows shell which I now use for running Vagrant.

I certainly felt a sense of achievement when we were able to commit our completed Vagrantfile and root directory to SVN. Hopefully our Vagrant box will be useful for new team members, and save them some time when setting up the project!

A copy of our complete Vagrantfile is available [here](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/11/Vagrantfile.txt).

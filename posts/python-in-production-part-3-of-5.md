---
authors:
- Lucas Ward
tags:
- python
- api
- devops
title: "Python in Production Part 3 of 5"
image: 
---

Welcome back to the Python in Production series.  In the [second part](https://templinkplaceholder) of the series, we learned how to create python modules and packages and why it is important to use them.  We packaged our simple api program and ran it from the command line.

In part 3 of this series (this part), we will learn how to generate a single file binary executable and why doing so will aid your path to production.  You will learn how, *and why,* to:
* Write a Build Script
* Create an Executable using Shiv
* Further codify your dependencies.

# When is it Appropriate to Generate a Single Binary

At this point in the series, we are ready to *build* our application.  If you are wondering why this is necessary, continue reading and I will explain everything!  If you look at this program setup through the lens of a "newly setup machine", you may come to realize, there are quite a few steps!  In some scenario's it may be fine to just *dockerize* the whole thing.  What happens though, if this application is just a part of a larger whole?  

When you have a system that has many distinct parts that could be updated separately by separate people, things get a little more complicated.  Imagine a scenario where there are multiple different applications running on a single board computer, say a home automation system.  It doesn't make sense to rebuild every part of the system when just one part is updated, especially if the system is resource constrained.  Instead, we can build that application separately, into a single binary, and then load it into the system as an "update".

Having a single binary also means that storage and versioning become easier.  Building a binary locks that version of the program into a single file.  This file when, stored and distributed properly, can be used to roll back updates, or test new features on old versions of your program.  As far as distrubution over the web goes, it makes sense to transmit a single binary file, vs cloning and building a repo on a target device.

There are *many* different ways to create a single executable file from a Python program.  There are managed services that can accomplish this.  There are also built in tools within the Python ecosystem to accomplish this, namely; dist tools and setup tools.  [This Page](https://setuptools.pypa.io/en/latest/setuptools.html) has a wealth of information on this topic and is worth reading through.

# Creating an executable with Shiv

For our project, we are going to use a tool called [shiv](https://shiv.readthedocs.io/en/latest/).  Shiv piggy backs on python's "zipapps" capability and produces a single runnable binary.  The great thing about this binary, is it doesn't need any other dependencies.  Everything required to run the program will be included in the binary.  

In order to do this *cleanly*, we will need a separate Python Virtual Machine.  This virtual machine will only be used for building our application.  It will inspect the contents of our other development virtual machine, along with the contents of our setup.py file to build our application.

First, let's create a new requirements file in our project directory for pip that will include our package, as well as shiv.  In the sample-python-project dir, run:
```bash
touch build_requirements.txt
echo "shiv==1.0.2" >> build_requirements.txt
echo "src/" >> build_requirements.txt
```

Take **note** of the the `src/` line.  This tells pip to install a local package using the relative path.

Now let's create a **bash script** that will handle the build process.  You could just run the commands individually, but creating a build script will come in handy later when we put this process into a Jenkins Pipeline.  Running the process locally using a script is also beneficial because it reduces the chance of human error.
```bash
touch build.sh
```

Open up your favorite text editor and write this code to your build script.
```bash
#!/usr/bin/env bash
set -e
set -x

python3 -m venv dist/venv
source dist/venv/bin/activate
python3 -m pip install -r requirements.txt
python3 -m pip install -r build_requirements.txt
shiv --site-packages venv/lib/python3.10/site-packages \
	--compressed \
	-o simple_api \
	-e simple_api.__main__:main src/ \
	--upgrade
```
Here is an explanation of each command.
* `set -e` tells bash to stop execution if an error is encountered.
* `set -x` tells bash to print the executed commands to shell for debugging.
* `python3 -m venv dist/venv` creates a second python virtual environment inside a folder called `dist`.  This will serve as our build virtual environment.
* `source dist/venv/bin/activate` activates the new virtual environment.
* `python3 -m pip install -r build_requirements.txt` installs the additional dependencies required for building our program into an executable, including our programs package.
* `shiv --site-packages venv/lib/python3.10/site-packages` shiv takes many different arguments, the site packages argument simply tells shiv where to find all of our dependencies.  We specify the "non-builder" virtual environment here (no `dist`), because the packages used to build our binary, are not the same packages that our binary uses to function.
* `--compressed` tells shiv to compress the files for the smallest possible binary.
* `-o simple_api` tells shiv what to name our executable.
* `-e simple_api.__main__:main src` tells shiv the entry point to our program and where to go looking for it.
* `--upgrade` tells shiv to replace our binary if this is a subsequent run.

### Keep your Requirements Files Separate

Keeping your requirements files for pip separate makes it easy to setup your environment for development mode vs. building.  This is especially true when cloning this project onto a new machine.  The steps in this series are to aid in the development and release of large python projects. We use a simple example here to give you the tools to build on in the future.  

### Keep your Virtual Environments Separate

Having multiple virtual environments helps to keep things clean.  Notice how in the build script we activate the virtual environment that lives in the dist folder, but we get the site packages for our binary from our development virtual environment.  That is because the dependencies required to run shiv are different then the ones required to build our program.

### A Note About Building Remotely

In a subsequent part of this series, we will put this process into a Jenkins build server.  When that happens, it will be necessary to create a separate bash script for setting up the initial virtual environment (refered to as the development virtual environment above) and installing our programs dependencies, because it will not exist on a newly provisioned machine.

# Running the Executable Binary

Congratulations! You have just created a single file, binary executable.  In order to run the binary, we must go back to the terminal.
```bash
./simple_api
```
I encourage you to put this executable binary onto a different machine and run it!  As long as that machine has the proper version of Python installed, the program will run.  

### CPU Architecture Matters

There are a few things worth mentioning that could cause you some errors along the way, especially if your program is not *pure python*.  If you build this binary on a machine running an ARM processor, and then copy it to a machine running an AMD processor, you may run into some issues.  In a later part of the series, we will discuss building for different architectures in more depth.  Remember from part 1, I mentioned that it is good to know which version of python you are building for?  Well, it is also good to know which architecture you are building for too!  

Some packages that are architecture specific may include things like Audio Controllers, Systemd bindings, or SPI dev kits.  These packages interact with the hardware of the computer and therefore are more architecture-specific.

# Update your .gitignore and  Push Your Changes

Now that we are building an executable, we have lots of *artifacts* in our repo.  For instance `simple_api.egg-info` is not something we want in our source control repository.  There is a really awesome template `.gitignore` file [here](https://github.com/github/gitignore/blob/main/Python.gitignore).  This is what I will be using moving forward.

In [part 4](https://temporarylink) of this series, we will load our binary onto a linux system and run it using systemd.  We will learn how to create a systemd unit file, pipe logging to journald, and how to use an Environment File to configure our program.  For more information on how Ippon Technologies can help your organization utilize Python, contact sales@ipponusa.com.
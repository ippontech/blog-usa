---
authors:
- Lucas Ward
tags:
- python
- api
- devops
title: "Python in Production Part 1 of 5"
image: 
---

Writing Python code is fast and easy.  It is thanks to this fact that Python as a language has gained immense popularity.  Putting Python code into production can also be fast and easy, if you follow a few guidelines.  In this series of blog posts, I will cover the how <em>and why</em> of several different aspects of writing <em>ready for production</em> python code. 

Please note: this is not a Python programming best practices guide, but rather, a road map that any python project can follow to **ease the future push to production.** This series will teach you how to:
* Avoid Dependency Issues
* Write Portable Code
* Package an Application
* Integrate and Monitor an Application Using Systemd
* Test and Build an Application

For this series, I will be using a wonderful Python Web Framework called FastAPI.  If you are not using FastAPI but still want to follow along, fear not, these steps can be used for almost any type of application.  I cannot promise that these steps will make your application successful, but they will certainly make the launch, maintenance, and continued development much easier.  Most of these things I would consider as being <em>adjacent</em> to your Python application development.

This series is made up of 5 Parts:
1. Introduction and Setup, Using a Virtual Environment to Manage Dependencies.
2. Creating Python Modules and Packages.
3. Turning a Python Program into a Runnable Binary with Shiv.
4. Running Python in Production with Systemd.
5. Putting it all into a Build Pipeline with Jenkins.

# Creating a Virtual Environment for Python

The first step to creating a virtual environment requires us to think ahead, to the future.  What version of Python will this project ultimately run on? Will this project be a part of a legacy system running Python 2.7 or a cutting edge system running the latest version of Python 3?  Figuring this out ahead of time will make your life easier in the long run.  Once you have identified which version of Python you want to develop for, we need to make sure we have said version on our local machine.  If you are on a Mac, you likely already have Python installed. To check, simply run these two commands.

To check which version of python you are running, type
```bash
python3 --version
```

For this series, we will be using Python version 3.10, although I will include some notes on how to get things working on earlier versions.  If you want to create a program to run on a different version of Pyton, you will need to make sure that you have that version of python installed, and that you create the environment using that version.

To install Python 3.10 on a Mac, simply run the following command:
```bash
brew install python@3.10
```

Python 3.3 and later comes with venv (short for virtual environment) as part of the Python Standard Library.  If you are running an earlier version of Python, you will need to install the virtualenv package via pip. 

**Python 3.2.6 or earlier**
```bash
pip install virtualenv
```

In order to keep things clean for the future, we need to create some directories to house our project.  The name of this directory will likely end up being what you name the git repo for this project, so think hard about a good name.
```bash
mkdir sample-python-project
cd sample-python-project
```

Now that we have the proper software installed and a folder to house our project, lets create a virtual environment.

**Python 3.3** or later (note: you will need to use what ever version of python you are building for in this next command, I will assume python3.10 from here on out):
```bash
python3.10 -m venv venv
```

**Python 3.2.6** or earlier:
```bash
virtualenv venv
```

This will create a folder called *venv* inside your current working directory.  In order to *start* the virtual environment, simply run:
```bash
source venv/bin/activate
```

After you have *activated* the virtual environment, you should see the name of your environment prefixed to your command prompt.  Mine looks like this:
```bash
(venv) lucas@Lucass-MacBook-Pro sample-python-project %
```

Inside the virtual environment, we no longer have to specify which version of python we are running.  Typing ***python --version*** (without the appended '3') should show us the same version that we used to create the virtual environment.  You can also still type the 3 if it is habit, I understand.  In order to *exit* the virtual environment, simply type deactivate.
```bash
deactivate
```

Congratulations! You have created a Python Virtual Environment!  The rest of this post is about why you have done what you have just done, and why it should be a habit to do this every time you start a new python project.  In addition, I will provide some project setup tips that will be useful in the next parts of this series.

# Why Use a Python Virtual Environment

Let's start with an example of how *not using* a virtual environment may lead to some headaches.  Let's say that you have 3 versions of python installed on your development machine.
* Python 3.8
* Python 3.9
* Python 3.10

You have done your due diligence as far as managing your PATH goes, and python 3.10 runs when you simply type
```bash
python3
Python 3.10.5 (v3.10.5:f377153967, Jun  6 2022, 12:36:10) [Clang 13.0.0 (clang-1300.0.29.30)] on darwin
Type "help", "copyright", "credits" or "license" for more information.
>>>
```

Once you have written some amount of code, you may find that you have some packages that aren't quite up to Python 3.10 yet and might require python 3.8 or python 3.9.  If you are building this program using your global python 3.10 that lives on your path, you will now have to type this every time you want to run your project.
```bash
python3.8 my-program.py
```

Furthermore, if there was a long list of packages that you installed for python 3.10, you may find that they are not available to your python 3.8 version.  Next, you do some googling and find a way to *beat the system*, and you run:
```bash
python3.8 -m pip install somepackage
```

This can work for you, but if you have a lot of packages or dependencies, you may find it difficult to remember what you have already installed.  To make things even more messy, you run this next command only to find a list of every package you ever installed for every project you have worked on using this machine...
```bash
pip freeze
```

This nightmarish scenario can be avoided entirely by being intentional about your python code and the environment that it runs in.  With virtual environments you don't have to worry about overlapping dependencies, global dependencies, and you don't have to remember to type out which version of python3 you are running every time you need to install a package or run your program.

# Managing Dependencies with Pip and a Virtual Environment

If you have been following along, you should be in a directory named *sample-python-project* and have a folder named *venv* that contains your virtual environment.  Go ahead an *activate* the environment by sourcing it.
```bash
source venv/bin/activate
```

As you are writing code for your project, you will eventually need to install some packages.  For the remainder of this series, we will be building out a simple API using FastAPI.  Lets go ahead and install FastAPI.
```bash
python3 -m pip install fastapi
```

If you are new to python virtual environments and managing dependencies, this command may look a little different than what you are used to.  By running ***pip*** with the ***python3 -m*** command, we are ensuring that we are using the version of pip that is present inside of our virutal evironment.  The **-m** command simply means module.  By using this flag, we are telling our virtual python3 interpretter to use it's version of pip.  This becomes especially important if you have multiple projects all with different virtual environments. 

It is helpful to keep a running list of packages installed for this project in a file called ***requirements.txt***. Let's create this file, and add our newly installed FastAPI package to this list.
```bash
touch requirements.txt
python3 -m pip freeze > requirements.txt
```

This will create a file to hold your requirements, also known as **dependencies** or **packages**. The ***freeze*** command outputs all of the currently installed packages.  The '>' greater than symbol writes that output to our file.  If you take a look inside the file, you will see not only fastapi listed, but all of fastapi's dependencies as well.
```bash
(venv) lucas@Lucass-MacBook-Pro sample-python-project % cat requirements.txt
anyio==3.6.1
fastapi==0.85.0
idna==3.4
pydantic==1.10.2
sniffio==1.3.0
starlette==0.20.4
typing_extensions==4.4.0
```

It's good practice to frequently update this file with the latest requirements for your project, and to keep this file in source control as well.  Speaking of source control, since our python interpretter lives inside of our project directory, we want to be sure to *exclude* or *ignore* it from our source control.  We can do this with a .gitignore file for git.
```bash
touch .gitignore
echo "venv/" >> .gitignore
```

We also now have quite a bit of setup before ever even writing any project code, let alone running it.  It is a good idea to go ahead and create a readme file for your project before commiting everything to source control.  Inside your readme, include instructions about how to start your virtual environment, and how to install your dependencies.  Maybe even drop a link to this article!
```bash
touch readme.md
cat <<EOT >> readme.md
# sample-python-project setup!
In order to run this project, you need to create a virtual environment and populate it with the dependencies listed in requirements.txt.
Run these commands to get started quickly.
python3.10 -m venv venv  
source venv/bin/activate  
python3.10 -m pip install -r requirements.txt  
EOT
```

The command ***python3.10 -m pip install -r requirements.txt*** will install all of the requirements listed in that file.  To get our *boiler plate project* into git, simply go to github and create an empty repo named sample-python-project.  Once you have done that, we can get our local files into github by running a few simple commands.
```bash
git init
git add -A
git commit -m "initial commit"
git remote add origin https://github.com/{{your_username}}/sample-python-project.git
git push -u -f origin main
```

If you have been following along, then your directory listing should look something like this.
```bash
.
├── .git
├── .gitignore
├── readme.md
├── requirements.txt
└── venv
    ├── bin
    ├── include
    ├── lib
    └── pyvenv.cfg
```

There you have it! A *fine* start to any python project.  Feel free to save this in a special repo as boiler plate that you always pull down to start your projects with.  It's worth mentioning that there are other ways to structure projects that you can read about online.  This is just one way that makes sense to me, and has been proven successful when it comes to future packaging, building, testing and distribution. 

In Part 2, we will go over where to put our actual project code within this file structure, and how to lay things out to maximize modularity, and extensibility. 
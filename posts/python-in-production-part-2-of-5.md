---
authors:
- Lucas Ward
tags:
- python
- api
- devops
title: "Python in Production Part 2 of 5"
image: 
---

Welcome back to the Python in Production series.  In the [first part](https://templinkplaceholder) of the series, we learned about Python virtual machines and why it is important to use them.  We also layed some initial ground work for our sample-python-project.

In part 2 of this series (this part), we will learn how to create Python Modules and Packages, and how doing so will improve your path to production.  You will learn how, *and why,* to:
* Create a python module.
* Create a python package.
* Further codify your dependencies.

# Structuring Directories for Packaging

In the last part of this series, we created a directory called `sample-python-program`, we created a virutal environment, added some files, and we downloaded the FastAPI package from PyPi using pip.  To continue following this guide, make sure you are inside the sample-python-program directory and have acitvated your Python Virtual Environment.

If you have written Python programs before, you probably appreciated the fact that you could just create a .py file and start adding code right away.  A single python file with just one print statement is a viable program that can be ran strait from the command line with no problems.  When ever you create larger projects though, the "single file" approach with "every thing in one directory" becomes a bit unmanageable.  Changing the structure of your project later on isn't too hard, but can be a point of stress if your program has some level of complexity.

To start, lets create a directory to house our source code, and setup file.
```bash
mkdir src
cd src
mkdir simple_api
touch setup.py
```

This is where all of the code for our project will live.  Inside of the src directory we have created another directory named simple_api.  This is the name of our Python program.  It's a good idea to name this directory what ever your python package will be called.  There are some rules surrounding naming packages and modules that you cand find [here](https://visualgit.readthedocs.io/en/latest/pages/naming_convention.html).

The other file we created, setup.py, is a crucial piece of the packaging puzzle.  Open up your favorite text editor and begin editing `setup.py`.
```python
#!/usr/bin/env python

from setuptools import setup, find_packages

setup(name='simple_api',
      version='1.0',
      description='A Simple API',
      author='Lucas Ward',
      author_email='lward@ipponusa.com',
      url='https://us.ippon.tech/',
      packages=find_packages(),
     )
```
# Why Package?

Okay, I think an explanation is in order.  The question, why do this?  It can be hard to understand the benefit of this if you haven't created a package before.  I am willing to bet that you have used a package before though.  A simple example is the python datetime package.  Let's take a look, start your python interpreter by typing `python` at the command line.  Once you have started the interpreter run these commands.
```bash
>>> import datetime
>>> print(datetime.datetime.now().timestamp())
1665670371.765653
>>>
```

When you call `import datetime`, you are importing the datetime package.  The datetime package contains a bunch of different modules, one of which is also called datetime.  At the end of the day, packages are technically just modules that contain more modules, but it helps to think of a package as a collection of modules, or the folder that stores them.

By creating a setup.py file, we are setting forth the intention to package our simple api program.  This has many benefits: testing, building for release, extending and reusing our program.  Some of these benefits may not be totally clear until you get to the later parts of this series.

# Finally, Let's Write our Program

Change directories into our program folder (don't forget to exit the python interpreter by pressing `[ctrl]+d`) and create some more files.  We will be filling them with code soon enough!
```bash
cd simple_api
touch __init__.py
touch __main__.py
touch api.py
```

Sanity check, your entire project should be *shaped* like this (some files inside of `.git` and `venv` have been hidden for brevity):
```bash
.
├── .git
├── .gitignore
├── readme.md
├── requirements.txt
├── src
│   ├── setup.py
│   └── simple_api
        ├── __init__.py
        ├── __main__.py
        └── api.py
└── venv
```

The `__init__.py` file will remain empty, it's purpose is to turn our directory into a module.  Our `api.py` file will contain our program, and our `__main__.py` file will be the entry point into our program.  Open `api.py` in your favorite text editor.  Let's code!
```python
from fastapi import FastAPI

app = FastAPI()

@app.get("/")
async def read_root():
    return {"Hello": "World"}
```

This is a very simple program.  The source code is straight from the [FastAPI documentation](https://fastapi.tiangolo.com/), and will serve as a great example for us.  To test your api, use uvicorn (installed in part 1) to start the development server.  This is the command, run from the command line.
```bash
uvicorn api:app --reload
```

Running the application like this is how most people run their python programs, as a script.  There is anoter way, using our other python files and our folder structure, we can turn this into a package.  Open up `__main__.py` and write the following code.
```python
import uvicorn
from simple_api.api import app

def main():
    uvicorn.run(app)

if __name__ == "__main__":
    main()
```

This ***main*** file will serve as our package's entry point.  The ***init*** file makes our simple_api directory a python module.  With a few additional steps, we can turn our module into a package.  

# Installing Your Program as a Local Package

How do you use packages? Well, you have to install them with pip first!  Let's do that now.  From the command line, navigate to the src directory and install your *local* package.
```bash
cd ..
python3 -m pip install .
```

**Note:** make sure you type the ' . ' after install.  This tells pip to install any packages it finds in the current directory.  You should see something familiar, like this...
```bash
Processing /Users/lucas/PersonalProjects/sample-python-project/src
  Preparing metadata (setup.py) ... done
Using legacy 'setup.py install' for simple-api, since package 'wheel' is not installed.
Installing collected packages: simple-api
  Running setup.py install for simple-api ... done
Successfully installed simple-api-1.0
```

Congratulations, you have just created a python package!!! The pure simplicity with which we have gotten here is one of the beautiful things about Python.  This package isn't available on PyPi or anything like that, but *it could be!*  The next step is to run our program and see it in action.

From the src directory...
```bash
python3 simple_api
INFO:     Started server process [4054]
INFO:     Waiting for application startup.
INFO:     Application startup complete.
INFO:     Uvicorn running on http://127.0.0.1:8000 (Press CTRL+C to quit)
```

You should see the familiar FastAPI startup text, and be able to access your "hello world" endpoint served at the root location by clicking the URL.  **Notice** that we did not have to append `.py` to simple_api when we ran it.  That is because it is now a package.  Similarly to how when you import something, you do not write `import datetime.py`.

Another *cool trick* that has now been made possible, is running our program from the python interpreter.  Try this in your terminal.  Type `python` to start the interpreter.
```bash
>>> from simple_api.api import app
>>> import uvicorn
>>> uvicorn.run(app)
INFO:     Started server process [4087]
INFO:     Waiting for application startup.
INFO:     Application startup complete.
INFO:     Uvicorn running on http://127.0.0.1:8000 (Press CTRL+C to quit)
```

WHOA! You just ran your newly packaged project straight from the interpreter, as if it was part of the standard library!

One thing to note, at this point in time, you **do not** want to add your local package to the requirements.txt file.  Your package cannot be a dependency to itself!  If you are in the habit of running `pip freeze > requirements.txt`, then you may need to go in and delete your package from this file.  In a later part of this series, we will create a separate virtual environment and `requirements.txt` file that will be used to run unit tests, integration tests, and build scripts.  Following the steps outlined here will make that future work much easier.

In [part 3](https://temporarylink) of this series, we will turn our program into a single file. We can create a runnable binary using a program called Shiv.  Don't forget to commit your changes up to this point into your source control repository, and to update your readme.md file with information about how to run your python package.
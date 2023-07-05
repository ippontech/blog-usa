---
authors:
- Lucas Ward
tags:
- python
- api
- devops
- linux
date: 2023-03-30T13:33:00.000Z
title: "Python in Production Part 4 of 5"
image: 
---

Welcome back to the Python in Production series.  In the [third part](https://blog.ippon.tech/python-in-production-part-3-of-5/) of the series, we learned how to convert our program into a runnable binary file.  Now that we have a runnable binary, we are ready to *integrate* our program into a system.  

Putting your python program into production can look different depending on your use case.  The methods discussed in this series will work for a wide range of applications.  I will be using a remote server running Debian Linux.  Most linux distros have a software suite called **SystemD**.  

**Note:** You can also use a Raspberry Pi, a local linux VM running on virtual box, or any platform that has systemD available on it.

# Understanding Systemd

**SystemD** is a system and service manager.  The 'D' in SystemD stands for daemon.  A daemon is a common unix troupe.  Daemon's are utilities used to run things in the background and typically take care of a bunch of *subsystem* stuff.  

We will be using systemd to handle several aspects of running our python program in production:
* Start up management
* Environment configuration
* Crash Handling
* Logging

In the next section, we will create a systemd unit file.  This file is the main way that you interface with systemd.  The unit file will tell systemd what resources are required to *bootstrap the user space* and *manage our program as a linux process*.  In a later section, we will create an Environment File that we can use to configure certain aspects of our simple api program.

# Create a Unit File

Systemd unit files live on the file system of your linux server.  A commonly used place for *user application unit files* is `/etc/systemd/user/{file_name}.service`.  The `{file_name}` is usually the name of your program.  For our application, the full path plus the file name will be `/etc/systemd/user/simple_api.service`.  For now, we can create the file here in our local repo, and then *transfer* it to our linux server once we are finished editing it.

You should be in the top level directory of your project.  For us, that is `sample-python-program/`.  With your terminal, create the unit file.

```bash
touch simple_api.service
```

Now with your text editor, write the following code to the file.  Don't worry, we will review what each line means right after we save the file.

```ini
[Unit]
Description=Simple API Service
After=network.target

[Service]
ExecStart=/usr/local/bin/simple_api
Restart=always

[Install]
WantedBy=multi-user.target
```

Here is an explanation of each line.
* `[Unit]` This section describes your service in the context of the rest of the system.
* `Description=Simple API Service` is a short description of the service.
* `After=network.target` tells systemd to wait until the network is up before starting our service.  This makes sense for most web applications.
* `[Service]` This section handles our application specific configuration.
* `ExecStart=/usr/local/bin/simple_api` tells systemd where your program binary lives.  We will need to transfer our binary to this location.
* `Restart=always` tells systemd to restart our program if it closes for any reason.
* `[Install]` This section handles your service in the context of the user space.
* `WantedBy=multi-user.target` tells systemd to make our program available to multiple users on the linux system.

Now that we have our systemd unit file, we are ready to put it, along with our program binary, onto our linux server.

# Putting it on the Server

**Note:** This section involves some pretty advanced Linux topics.  It's totally cool if you don't fully understand everything.  I will offer some minor explanations, but I highly encourage you to read up on any and all commands that you run on your machines.  Please don't *blindly run* linux commands that you find on the internet.

If you have been following along since the beginning of the series, then you will have a *binary* file called `simple_api` and a *systemd unit file* called `simple_api.service` in your current working directory.  In order to get these files onto our linux server, we are going to use a utility called ***scp***.

SCP stands for **S**ecure **C**opy **P**rotocol.  It is a simple command line utility that transfers files from your local machine, to a remote server (or the other way around!). SCP uses the same mechanism as SSH to secure your "over the wire" communications. If you are more comfortable with an ftp utility, or a program such as filezilla, feel free to use that instead.  

### Copy the Binary File onto Your Linux Server

If you are using an SSH key to access your server, then run this command on your local machine, replacing the stuff in brackets with *your stuff*:

```bash
scp -i {path_to_ssh_private_key_file} simple_api {user}@{server}:/home/{user}/simple_api
```

My command looks like this (***DON'T RUN THIS ONE!***): 

```bash
scp -i ~/.ssh/id_ed25519 simple_api lward@python-blog.example.com:/home/lward/simple_api
```

If you are accessing your server via username and password, simply run this command and follow the on screen prompts for your username and password:
```bash
scp simple_api {user}@{server}:/home/{user}/simple_api
```

An example (***DON'T RUN THIS ONE!***):

```bash
scp simple_api lward@python-blog.example.com:/home/lward/simple_api
```

### Copy the SystemD Unit File onto Your Linux Server

Again, run this command from your local machine, replacing the stuff in brackets with *your stuff*:

```bash
scp -i {path_to_ssh_private_key_file} simple_api.service {user}@{server}:/home/{user}/simple_api.service
```

After copying both files onto our server, the next step is to complete our setup.  This next step will only need to be completed once; it is good forever!

## Create Symlinks to our Files

Log into your server using ssh.  Once you are in your home folder, verify your files are present.
```bash
lward@python-blog:~$ ls
simple_api  simple_api.service
```

Create a symlink for each file, connecting it to the locations specified in the previous parts.  Our simple_api file should be symlinked to `/usr/local/bin/simple_api`, and our unit file should be symlinked to `/home/{user}/.config/systemd/user/simple_api.service`.  In your server terminal, run this command, replacing the stuff in brackets with *your stuff*:
```bash
sudo ln -s /home/{user}/simple_api /usr/local/bin/simple_api
sudo ln -s /home/{user}/simple_api.service /home/{user}/.config/systemd/user/simple_api.service
```

If you list the files in either one of those diretories, you should see your file there.
```bash
lward@python-blog:~$ ls /usr/local/bin
simple_api
```

Before we load our program and unit file into systemd for the first time, let me explain briefly the choice to use symlinks.  If you had tried to SCP these files onto your server, you may have gotten a permission denied error.  You cannot run SCP from your local machine as root, meaning you can't write to a *root only* location on your server from your local machine.

By copying the files to our user space, and then creating symlinks, it keeps the sudo operation from happening 'over the air' and instead, can be more securely ran on the actual server.  Sure you could have just moved those files from your home directory to their final destinations, but then you would have to do that *everytime* you wanted to upload a new version.  The symlinks will still be there, even if we replace our files.

## Reload SystemD and Enable your Service

Just having our files in place does not mean that systemd will just start running our application.  We have to reload the daemon to see our new unit file, and then enable and start our service.  Systemd includes `systemctl`, it is used to manage our system and user unit files.  While logged into your server, run these commands:
```bash
systemctl --user daemon-reload
systemctl --user enable simple_api.service
systemctl --user start simple_api.service
```

These three commands tell systemd to look for new unit files, add them to the list of things to start on boot, and then start the service.  To verify our service is indeed running, we will use another piece of software included with systemd, called `journald`.  In order to read the logs stored in `journald`, we use the command `journalctl`.  You may notice a pattern emerging here! 
```bash
journalctl --user-unit simple_api.service
```

If everything went according to plan, you should see this as the output:
```text
Oct 18 14:49:00 lward-0 systemd[23950]: Started Simple API Service.
Oct 18 14:49:01 lward-0 simple_api[24391]: INFO:     Started server process [24391]
Oct 18 14:49:01 lward-0 simple_api[24391]: INFO:     Waiting for application startup.
Oct 18 14:49:01 lward-0 simple_api[24391]: INFO:     Application startup complete.
Oct 18 14:49:01 lward-0 simple_api[24391]: INFO:     Uvicorn running on http://127.0.0.1:8000 (Press CTRL+C to quit)
```

If you would like to *follow* the logs in a separate terminal, you can do so by passing the `-f` flag.
```bash
journalctl --user-unit simple_api.service -f
```

Pressing `[ctrl]-[c]` will exit the logs. You can also check on the status of your service by using systemctl's `status` command.  Here is the my output.
```bash
lward@python:~$ systemctl --user status simple_api
● simple_api.service - Simple API Service
     Loaded: loaded (/home/lward/simple_api.service; enabled; vendor preset: enabled)
     Active: active (running) since Tue 2022-10-18 18:34:37 UTC; 14min ago
   Main PID: 24875 (python3)
      Tasks: 1 (limit: 18716)
     Memory: 21.0M
        CPU: 1.796s
     CGroup: /user.slice/user-1001.slice/user@1001.service/app.slice/simple_api.service
             └─24875 python3 /usr/local/bin/simple_api

Oct 18 18:34:37 lward-0 systemd[24822]: Started Simple API Service.
Oct 18 18:34:38 lward-0 simple_api[24875]: INFO:     Started server process [24875]
Oct 18 18:34:38 lward-0 simple_api[24875]: INFO:     Waiting for application startup.
Oct 18 18:34:38 lward-0 simple_api[24875]: INFO:     Application startup complete.
Oct 18 18:34:38 lward-0 simple_api[24875]: INFO:     Uvicorn running on http://127.0.0.1:8000 (Press CTRL+C to quit)
Oct 18 18:34:41 lward-0 simple_api[24875]: INFO:     127.0.0.1:40054 - "GET / HTTP/1.1" 200 OK
Oct 18 18:35:23 lward-0 simple_api[24875]: INFO:     127.0.0.1:40056 - "GET / HTTP/1.1" 200 OK
```

Since our program is a locally running webserver, we can also use `curl` to verify it is up and running.
```bash
lward@python-blog:~$ curl http://localhost:8000
{"Hello":"World"}
```

You won't be able to hit this endpoit from your local machine without some further configuration, which falls outside of the scope of this series.  If you really want to go down that route, look into serving a FastAPI site with NGINX.

# Configure Your Program With an Environment File

Let's make a slight update to our Application.  I want our application to say `{Message: Hello, World! From North Carolina}`.  I would prefer not to hard code my location, North Carolina, into our application.  In order to get around this hard coding issue, we will use an environment file. 

Switching back to our local terminal, in our project directory, let's make some changes:
```bash
touch simple_api.conf
echo "DEV_LOCATION='North Carolina'" > simple_api.conf

cd src/simple_api
```

Open up `api.py` and replace the contents with this updated version:
```python
from fastapi import FastAPI
import os

app = FastAPI()

@app.get("/")
async def read_root():
    location = os.environ.get('DEV_LOCATION', "The Underworld!")
    message = f"Hello, World! From {location}"
    return {"Message": message}
```

Next, run your build script:
```bash
cd ../..
bash build.sh
```

Let's test our application before sending it to the server.
```bash
export DEV_LOCATION="North Carolina"
./simple_api
```

Use your browser to navigate the site and see the new message `{"Message":"Hello, World! From North Carolina"}`.  When testing locally, we set an Environment Variable, but systemd has an option to load an environment file.  Earlier, we created a file called `simple_api.conf`.  We will put that file on our Linux server in the `/etc/default/simple_api.conf` location.  Let's go ahead and update our unit file and then we will load it all onto our server.

Change your simple_api.service file to reflect these changes:
```ini
[Unit]
Description=Simple API Service
After=network.target

[Service]
EnvironmentFile=/etc/default/simple_api.conf
ExecStart=/usr/local/bin/simple_api
Restart=always

[Install]
WantedBy=multi-user.target
```

We simply added the line `EnvironmentFile=/etc/default/simple_api.conf` to our unit file under the `[Service]` section.  This line tells systemd to go to this file location, and read the contents into Environment variables, making them available to our application.  Finally, we copy our new configuration file, our updated binary, and our updated service file to the server.   Then we create a new symlink for our config file. 

```bash
scp -i {path_to_ssh_private_key_file} simple_api {user}@{server}:/home/{user}/simple_api
scp -i {path_to_ssh_private_key_file} simple_api.service {user}@{server}:/home/{user}/simple_api.service
scp -i {path_to_ssh_private_key_file} simple_api.conf {user}@{server}:/home/{user}/simple_api.conf
```

SSH into your server and create the symlink.
```bash
ssh -A {user}@{server}
sudo ln -s /home/{user}/simple_api.conf /etc/default/simple_api.conf
```

Reload the daemon to get the update unit file, and then restart your service.
```bash
systemctl --user daemon-reload
systemctl --user restart simple_api.service
```

You should now see the updated message when using Curl.
```bash
lward@python-blog:~$ curl http://localhost:8000
{"Message":"Hello, World! From North Carolina"}
```

# Why You Should Use an Environment File vs Hard Coding

If you have made it this far, then I salute you, dedicated reader.  This series consists of 5 parts, and you are nearing the end of part 4.  Each part builds upon the previous part, and each part offers more value as far as ***lessons learned***.  To illuminate this point, I will now tell you why you are creating this configuration file versus hard coding 'North Carolina' into the source code.

Let's say that down the road, I move to Virginia.  If I had hardcoded my location into my source code, I would need to rebuild and relaunch my application to reflect this change.  Using an environment file in our systemd unit file enables us to change aspects of our program *without* having to rebuild it.  With a small application like this, rebuilding would be manageable, but things get complicated as applications get bigger.  Let's update our location to Virginia and then call it a day.

Make sure you are logged into your remote server.  Run the following commands to update your Environment File.
```bash
echo "DEV_LOCATION='Virgina'" > simple_api.conf
systemctl --user restart simple_api
curl http://localhost:8000
{"Message":"Hello, World! From Virgina"}
```

In the section 'Configure Your Program With an Environment File' we had to go through the arduous process of rebuilding our code, and relaunching it on our server.  In part 5 of this series, we will automate most of this process using a build pipeline inside of a program called Jenkins.

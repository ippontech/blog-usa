---
authors:
- Alexandre Pocheau
tags:
- Docker
date: 2018-02-02T19:01:36.000Z
title: "Docker and permission management"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/02/docker_logo.png
---

This article is part of a series of three posts about *Docker*:

1. ***Docker* and permission management**
1. Set up a reverse proxy with *Nginx* and *Docker-gen* (Bonus: *Let's Encrypt*)
1. Tips and reminders for using *Docker* daily

In this first post, I will show how you can deal with file permissions when a container is using `root` and you want to keep access to the files as an unprivileged host user.

# The problem

Let's start by looking at the problem.

We will run an `alpine` container which is using `root`. We will bind a host directory to a volume making every file in this directory available inside the container. Then, from the container, we will create a new file inside the volume.

From a host point of view, who will be the owner of this new file? What rights will be applied to it?

Let's run an `alpine` container with a volume:

```
$ docker run -it -v [host directory]:[container directory] alpine
/ #
```

Replace `[host directory]` and `[container directory]` with any path that fits to you.

Doing this, you are prompted inside an Alpine distribution and every file inside the host's directory will be available inside the container's directory, and vice versa.

From the container, create a new file inside the volume:

```sh
$ touch /apps/files/test
$ ls -l /apps/files/test
-rw-r--r--    1 root     root            0 Nov 14 15:53 /apps/files/test
```

As you can see, inside the container, the file is owned by `root`. What about from the host?

```sh
$ ls -l
-rw-r--r-- 1 root root 0 nov.  14 16:53 test
```

The file is owned by `root` too!

Now, from the host and with an unprivileged user, how can I access this file? Turns out I can't!

```sh
$ echo "foo" > test
permission denied: test
```

*Permission denied*

# Translating users and groups

As every container can use a set of users and groups, we cannot just translate every container's user into a single host's user without breaking the rights. We have to map them into host's sub-users. Doing this is a feature called “*User namespaces*”.

A user namespace extends user's rights by allowing it to access files owned by other users or groups. Its configuration is made by editing two files:

* `/etc/subuid`
* `/etc/subgid`

These two files have the same structure and are made of lines with this format:

```text
[USER]:[UID or GID]:[count of next UIDs or GIDs]
```

Each line means that the user will gain access to a number of *UID* or *GID* starting from a certain number. As an example for `subuid`:

```text
foo:1000:3
```

This means that user `foo` will have access to files owned by users with a *UID* of 1000, 1001 and 1002. This works the same for groups!

In a Docker context, container's users are mapped with host's users. Since *UID* 0 is always `root`, the container's `root` will always match the host's `root`! This is the reason why, sometimes, we hear about "*Docker process isolation*" or "*Process breaking out of a Docker container*" because a user in a container has too many rights on the host.

We will now create two configurations:

* Tell Docker to use a different user than `root`
* Map this user's sub *UID*s with container's users

These two configurations will give us the ability to access files created by the container with a lower privileged user but also to avoid a container's user having `root` rights on our host. To illustrate this point, we will use an Alpine image with a volume.

In our case, our user `apocheau` will become the owner of our containers instead of `root`:

```sh
$ grep apocheau /etc/passwd
apocheau:x:1000:1000:apocheau,,,:/home/apocheau:/bin/bash
```

We can see here that `apocheau` has a *UID* and a *GID* of 1000.

We now have to define `subuid` and `subgid` with which our user will be allowed to access.

Edit the files `/etc/subuid` and `/etc/subgid` and add:

```text
apocheau:1000:65536
```

Here, I also recommend to add the following line to the `/etc/subgid` file:

```text
apocheau:999:1
```

We are here saying that user `apocheau` will have access to one group with *GID* starting from 999. In other words, `apocheau` user will have access to group 999.

You should replace `apocheau` with your username and 999 with the `docker` group GID which you can grab with this command:

```text
$ getent group docker
docker:x:999:apocheau
```

As the owner of the container will not be `root` anymore, he does not have the permission to access the Docker socket that is owned by the `docker` group. This additional line will give your user the right to access `docker` group files and so the Docker socket. With this additional right, you'll be able to continue to bind you Docker socket. Useful for Portainer/Rancher/Træfik/Docker-gen/etc.!

Now that this is done, the last thing to do is to tell Docker to use our user. Create or edit the `/etc/docker/daemon.json` file and add the following:

```json
{
  "userns-remap": "apocheau:apocheau"
}
```

Different formats can be used here: `username`, `UID` or `UID:GID` for example.

All what's left is to restart Docker:

```sh
$ sudo systemctl daemon-reload
$ sudo systemctl restart docker
```

Start the container and get into it:

```sh
$ docker run -it -v /apps/docker-articles/:/apps/files alpine
```

And create a file in `/apps/files` directory inside this running image:

```sh
$ touch /apps/files/test
```

Now, from the host, go to the volume and notice that the owner of the `test` file is not `root` anymore but your user!

```sh
-rw-r--r--  1 apocheau apocheau    0 nov.   6 16:24 test
```

You'll also notice that the owner of the process is not `root` anymore.

# Conclusion

We have seen how common and recurrent these permission problems are. We used a really minimalist example to showcase the problem and the solution.
User namespaces should always be used when it comes to controlling your data and pushing your containers into production.

# A few things to note

* You will have to re-download your images (after deactivating the user namespace, you will again have access to your previous images)
* This configuration applies to all your daemon's containers. The user namespace [will only reduce the risk of a process breaking out of its container](https://security.stackexchange.com/a/153016).

# References

* https://docs.docker.com/engine/security/userns-remap/
* https://raesene.github.io/blog/2016/02/04/Docker-User-Namespaces/

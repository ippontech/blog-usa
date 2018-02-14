---
authors:
- Alexandre Pocheau
categories:
- 
date: 
title: "Tips and reminders for using Docker daily"
image: 
---

> This article is part of a series of three articles about *Docker*:

>1. *Docker* and permissions management
1. Set up a reverse proxy *Nginx* and *Docker-gen* (Bonus: *Let’s Encrypt*)
1. **Tips and reminders for using *Docker* daily**

For this last article about *Docker*, I wanted to share with you what makes my daily usage of *Docker* pleasant. Besides all of this, I feel it is interesting to provide some reminders on *Docker* usage.

<u>**Reminder n°1:** Exposing a port is not always necessary, and can even be a security hole</u>
Getting back to our first *Owncloud* example where we’ve launched a *Mysql* and an *Owncloud* container:
```
version: "3"

services:

  mysql:
    image: mysql:8.0.3
    hostname: mysql
    ports:
        - 3306:3306
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: owncloud
      MYSQL_USER: ippon
      MYSQL_PASSWORD: ippon
    networks:
      - ippon

  owncloud:
    image: owncloud:10.0.3
    hostname: owncloud
    ports:
      - "8080:80"
    networks:
      - ippon

networks:
  ippon:
```
That way, the database is accessible from the host as if it was installed on it.
But, do we really need that ?
Let’s remove port 3306 exposure, that way, with *Mysql-client* on host, you will not be able to connect to it through:
```
$ mysql -h 127.0.0.1 -u ippon -p
Enter password: ippon
```
But through:
```
$mysql -h [mysql-container-ip] -u ippon -p
Enter password: ippon
```
Being in the same network, *Owncloud* will continue to access the *Mysql* container’s ports (as did the link function) and so, will not have any problem to connect to it.
Now, let’s say *Mysql-client* is not installed on host or your user is not allowed to use it. So, you will have limited the database access to containers on the same network ! Security improved !

<u>**Reminder n°2:** *Docker* DNS is not activated by default!</u>
Yes, none of the active containers in the default bridge network (*docker0*) will be able to communicate through their hostnames. They will be able to communicate using their IP address, but in order to use their hostname (which you can set beforehand) you will need to create and use your own network. Even if containers cannot find each other using their hostname, hosts can.

<u>**Tip n°1:** Customize ‘*docker ps*’</u>
This command is definitely one of the most used, so, why not customize it ?
Here is what the ‘*docker ps*’ response looks like by default:
```
$docker ps
CONTAINER ID        IMAGE                                         COMMAND                  CREATED             STATUS              PORTS                                      NAMES
9a039e8c80d1        mysql:8.0.3                                   "docker-entrypoint..."   18 seconds ago      Up 15 seconds       3306/tcp                                   reverseproxy_mysql_1
592d5cb33180        owncloud:10.0.3                               "docker-entrypoint..."   18 seconds ago      Up 16 seconds       0.0.0.0:8080->80/tcp                       reverseproxy_owncloud_1
3c9a4976da73        jrcs/letsencrypt-nginx-proxy-companion:v1.5   "/bin/bash /app/en..."   7 days ago          Up 16 seconds                                                  reverseproxy_letsencrypt_1
1c2c18ca14b3        jwilder/nginx-proxy                           "/app/docker-entry..."   7 days ago          Up 17 seconds       0.0.0.0:80->80/tcp, 0.0.0.0:443->443/tcp   reverseproxy_nginx-proxy_1
185859d2f7c6        portainer/portainer                           "/portainer"             2 months ago        Up 5 hours          0.0.0.0:9000->9000/tcp                     portainer
```

First of all, not all of the information is useful to me, and even worse, the column display should bring visibility but is totally broken due to the many informations to be displayed per line.
Personally, the useful information is: *ID*, *Image*, *Status*, *Ports* and *Name*.
*Docker* allows us to specify a response format with desired columns.
Here is an example:
```
$docker ps --format "table {{.ID}}\t{{.Image}}\t{{.Names}}\t{{.Ports}}\t{{.Status}}"
CONTAINER ID        IMAGE                                         NAMES                        PORTS                                      STATUS
9a039e8c80d1        mysql:8.0.3                                   reverseproxy_mysql_1         3306/tcp                                   Up 7 minutes
592d5cb33180        owncloud:10.0.3                               reverseproxy_owncloud_1      0.0.0.0:8080->80/tcp                       Up 7 minutes
3c9a4976da73        jrcs/letsencrypt-nginx-proxy-companion:v1.5   reverseproxy_letsencrypt_1                                              Up 7 minutes
1c2c18ca14b3        jwilder/nginx-proxy                           reverseproxy_nginx-proxy_1   0.0.0.0:80->80/tcp, 0.0.0.0:443->443/tcp   Up 7 minutes
185859d2f7c6        portainer/portainer                           portainer                    0.0.0.0:9000->9000/tcp                     Up 5 hours
```
Now, all the information fits in lines in a beautiful table.
Well, for me, there’s missing information: *IP addresses*.
I have chosen an [*ajohnstone*’s proposition](https://github.com/moby/moby/issues/8786#issuecomment-70937823) which I find efficient but can still be improved:
```
$function docker-ips() {
    docker ps | while read line; do
        if `echo $line | grep -q 'CONTAINER ID'`; then
            echo -e "IP ADDRESS\t$line"
        else
            CID=$(echo $line | awk '{print $1}');
            IP=$(docker inspect -f "{{ .NetworkSettings.IPAddress }}" $CID);
            printf "${IP}\t${line}\n"
        fi
    done;
}

$docker-ips
IP ADDRESS	CONTAINER ID        IMAGE                                         COMMAND                  CREATED             STATUS              PORTS                                      NAMES
	9a039e8c80d1        mysql:8.0.3                                   "docker-entrypoint..."   17 minutes ago      Up 17 minutes       3306/tcp                                   reverseproxy_mysql_1
	592d5cb33180        owncloud:10.0.3                               "docker-entrypoint..."   17 minutes ago      Up 17 minutes       0.0.0.0:8080->80/tcp                       reverseproxy_owncloud_1
	3c9a4976da73        jrcs/letsencrypt-nginx-proxy-companion:v1.5   "/bin/bash /app/en..."   7 days ago          Up 17 minutes                                                  reverseproxy_letsencrypt_1
	1c2c18ca14b3        jwilder/nginx-proxy                           "/app/docker-entry..."   7 days ago          Up 17 minutes       0.0.0.0:80->80/tcp, 0.0.0.0:443->443/tcp   reverseproxy_nginx-proxy_1
172.17.0.2	185859d2f7c6        portainer/portainer                           "/portainer"             2 months ago        Up 6 hours          0.0.0.0:9000->9000/tcp                     portainer
```

It displays the containers’ addresses in the first column but only for the ones on the default bridge network… we can do better.
The problem is at the command level...:
```
$docker inspect -f "{{ .NetworkSettings.IPAddress }}" $CID
```
...which uses the old *Docker* API. Let’s fix this with the new one:
```
$docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}} {{end}}' $CID
```
Which gives us:
```
$docker-ips             
IP ADDRESS	CONTAINER ID        IMAGE                                         COMMAND                  CREATED             STATUS              PORTS                                      NAMES
172.23.0.2 	9a039e8c80d1        mysql:8.0.3                                   "docker-entrypoint..."   26 minutes ago      Up 26 minutes       3306/tcp                                   reverseproxy_mysql_1
172.21.0.3 172.23.0.3 	592d5cb33180        owncloud:10.0.3                               "docker-entrypoint..."   26 minutes ago      Up 26 minutes       0.0.0.0:8080->80/tcp                       reverseproxy_owncloud_1
172.21.0.4 	3c9a4976da73        jrcs/letsencrypt-nginx-proxy-companion:v1.5   "/bin/bash /app/en..."   7 days ago          Up 26 minutes                                                  reverseproxy_letsencrypt_1
172.21.0.2 	1c2c18ca14b3        jwilder/nginx-proxy                           "/app/docker-entry..."   7 days ago          Up 26 minutes       0.0.0.0:80->80/tcp, 0.0.0.0:443->443/tcp   reverseproxy_nginx-proxy_1
172.17.0.2 	185859d2f7c6        portainer/portainer                           "/portainer"             2 months ago        Up 6 hours          0.0.0.0:9000->9000/tcp                     portainer
```
Way better ! Well, not quite, because the display is broken again due to the IP’s length which is not always the same. Let’s delete the columns *Command* and *Created* and put *IP ADDRESS* in the last position:
```
$function docker-ips() {        
    docker ps --format "table {{.ID}}\t{{.Image}}\t{{.Names}}\t{{.Ports}}\t{{.Status}}" | while read line; do
        if `echo $line | grep -q 'CONTAINER ID'`; then
            echo -e "$line\tIP ADDRESS"
        else
            CID=$(echo $line | awk '{print $1}');
            IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}} {{end}}' $CID);
            printf "${line}\t${IP}\n"
        fi
    done;
}

$docker-ips
CONTAINER ID        IMAGE                                         NAMES                        PORTS                                      STATUS	IP ADDRESS
9a039e8c80d1        mysql:8.0.3                                   reverseproxy_mysql_1         3306/tcp                                   Up 30 minutes	172.23.0.2 
592d5cb33180        owncloud:10.0.3                               reverseproxy_owncloud_1      0.0.0.0:8080->80/tcp                       Up 30 minutes	172.21.0.3 172.23.0.3 
3c9a4976da73        jrcs/letsencrypt-nginx-proxy-companion:v1.5   reverseproxy_letsencrypt_1                                              Up 30 minutes	172.21.0.4 
1c2c18ca14b3        jwilder/nginx-proxy                           reverseproxy_nginx-proxy_1   0.0.0.0:80->80/tcp, 0.0.0.0:443->443/tcp   Up 30 minutes	172.21.0.2 
185859d2f7c6        portainer/portainer                           portainer                    0.0.0.0:9000->9000/tcp                     Up 6 hours	172.17.0.2
```
Beautiful ! But still not perfect…
The ‘*docker ps*’ command can take several parameters like filters for example:
```
$docker ps -
--all       -a  -- Show all containers
--before        -- Show only container created before...
--filter    -f  -- Filter values
--format        -- Pretty-print containers using a Go template
--help          -- Print usage
--last      -n  -- Show n last created containers (includes all states)
--latest    -l  -- Show only the latest created container
--no-trunc      -- Do not truncate output
--quiet     -q  -- Only show numeric IDs
--since         -- Show only containers created since...
--size      -s  -- Display total file sizes
```
To enable the user to use those parameters, I made a last modification to the ‘*docker ps*’ call to forward command line parameters:
```
$function docker-ips() {        
    docker ps $@ --format "table {{.ID}}\t{{.Image}}\t{{.Names}}\t{{.Ports}}\t{{.Status}}" | while read line; do
        if `echo $line | grep -q 'CONTAINER ID'`; then
            echo -e "$line\tIP ADDRESS"
        else
            CID=$(echo $line | awk '{print $1}');
            IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}} {{end}}' $CID);
            printf "${line}\t${IP}\n"
        fi
    done;
}

$docker-ips --filter ancestor=owncloud:10.0.3                                   
CONTAINER ID        IMAGE               NAMES                     PORTS                  STATUS	IP ADDRESS
592d5cb33180        owncloud:10.0.3     reverseproxy_owncloud_1   0.0.0.0:8080->80/tcp   Up 37 minutes	172.21.0.3 172.23.0.3
```
There, it's perfect!

<u>**Tip n°2:** Some aliases</u>
Here are some aliases that I frequently use:
```
alias d='docker'
alias dc='docker-compose'
alias dx='d exec -it '
```
Because, yes, I am a pretty lazy kind of guy!

One more, which is not an alias but a function. You already know this one:
```
function dps() {
    docker ps $@ --format "table {{.ID}}\t{{.Image}}\t{{.Names}}\t{{.Ports}}\t{{.Status}}" | while read line; do
        if `echo $line | grep -q 'CONTAINER ID'`; then
            echo -e "$line\tIP ADDRESS"
        else
            CID=$(echo $line | awk '{print $1}');
            IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}} {{end}}' $CID);
            printf "${line}\t${IP}\n"
        fi
    done;
}
```

<u>**Tip n°3:** *Portainer* (FTW)</u>
This is a tool that I like so much that I start it at my laptop’s startup. *Portainer* is a local *Docker* daemon and *Swarm* cluster manager. This is a web application which allows you to (among others things):

* List containers, images, networks
* Start, stop, delete containers
* See container’s logs
* Start a console inside a container
* See container’s statistics

*Disclaimer* : Once tried always used !:
```
$docker run -d -p 9000:9000 -v /apps/portainer:/data -v /var/run/docker.sock:/var/run/docker.sock --restart always --name portainer portainer/portainer
```
With these few tips and reminders on *Docker*’s usage, I hope you will take as much pleasure as me using it!

> This article marks the end of this series about *Docker*, we talked about very different subjects which feel like useful concepts for *Docker*’s understanding and usage.

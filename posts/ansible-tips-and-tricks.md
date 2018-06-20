---
authors:
- Pierre Baillet
tags:
- Devops
date: 2017-03-15T09:30:00.000Z
title: "10 Ansible tips and tricks"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/02/ansible.gif
---

<center>
<div markdown="1">

<i>In French, Ansible can be pronounced as "en cible," meaning "in target."</i>

</div>
</center>

If you have been doing a bit of Ansible, there is probably some coding style that has emerged from your work, especially if you work in a team around the same code. During my time as an Ansible developer, I had the opportunity to collect some tips I use on a regular basis when writing scripts. These tips will probably help you save some time.

I assume that you already know Ansible, Playbooks, YAML syntax, and the roles of Ansible Galaxy.

### 1 - Always name everything.

It is possible to run `Playbooks` or `Tasks` without ever naming them:

```yaml
---
- hosts: local
  tasks:
  - user:
	  name: testuser1
	  state: present
	  groups: wheel
```

...which gives:

```
PLAY ***************************************************************************
TASK [user] *******************************************************************
[...]
```

This log is not particularly interesting. We can probably do better.

For example:

```yaml
---
- name: "Prepare localhost"
  hosts: local
  tasks:
  - name: "Create testuser1"
	user:
	  name: testuser1
	  state: present
	  groups: wheel

```

...will return:

```
PLAY [Prepare localhost] ******************************************************
TASK [Create testuser1] *******************************************************
[...]
```

It's much more readable. To do even better, you can add more `DEBUG` information in the `Tasks` names, such as:

```yaml
- name: "Create folder {{ target_folder }}"
  file:
	path: "{{ target_folder }}"
	recurse: yes
	state: directory
```

As always, the message should contain as much information as possible to allow the developer to `DEBUG` in case of error. I always try to do that so that future analysis is easier to do. You never know; you might really be in a hurry next time you want to understand why your code fails.

**Always name your Ansible Tasks.**

### 2 - Use the YAML syntax, not the Ansible one.

With Ansible, you can use a mix of two syntaxes when writing code. Either pure YAML:

```yaml
- name: add user testuser1
  user:
	name: testuser1
	state: present
	groups: wheel
```

...or the hybrid YAML/Ansible syntax:

```yaml
- name: add user testuser1
  user: name=testuser1 state=present groups=wheel
```

These two examples are identical in terms of Ansible description. In the first case, Ansible will parse the YAML document and then run the code. In the second case, Ansible will also parse `name=testuser1 state=present groups=wheel` before running the task `user`. This syntax is useful but forces you to do some intellectual work (= or :, YAML or inline, ...) before being able to write working code, especially when it is complex.

My suggestion is to use the YAML syntax every time, avoiding the `=` syntax. It will help you detect errors earlier, and make your Ansible code easier to read.

With some practice, it also becomes easier to write than the usual Ansible/YAML mix.

**Always use the YAML syntax.**

### 3 - Document your variables.

Ansible supports variable overriding according to where it finds their declaration. In the official documentation, the [priority](http://docs.ansible.com/ansible/playbooks_variables.html) of these declarations is as follows:

```
		role defaults
		inventory vars
		inventory group_vars
		inventory host_vars
		playbook group_vars
		playbook host_vars
		host facts
		play vars
		play vars_prompt
		play vars_files
		registered vars
		set_facts
		role and include vars
		block vars (only for tasks in block)
		task vars (only for the task)
		extra vars (always win precedence)
```
As you can see, the list is rather long and allows you to override a variable almost anywhere. This means that you have to be careful:

- not to spread your variable in your code too much. If you are the only author/user of your code, it's easier to do than if you use someone's code (roles from [Galaxy](https://galaxy.ansible.com/), for example).
- to document all the variables you use, wherever you declare them.

I tend to use group and role variables as much as possible before resorting to other declaration levels.

I also try to document these settings (for example, with a real life file):

```yaml
---
# file: group_vars/all
# For data synchronisation from the server to localhost
local_source_folder: /Users/octplane/src/wiseman_r
remote_production_folder: /home/oct/prod

# app name to look for in the local registry
app_name: wiseman
# image name to search for in the local image registry
image_name: "octplane/{{ app_name }}"
# version to search for in the local image registry
version: 4
# where to export the docker image
export_folder: /tmp
# Exported file from docker after zipping
artefact_name: "{{ app_name }}-{{ version }}.docker.gz"

# where to copy the data on the remote server
# where is deployed the test application
remote:
	landing_folder: /home/oct/tmp
	test_folder: /home/oct/data/wiseman.test/
```

This way, when I come back to this code, or if anybody (such as you) reads it, it's easier to understand how it works rather than switching between roles and variables all the time.

**Always document your variables.**

**Try to minimize where you declare your variables.**

### 4 - Use asserts to validate the parameters.

With the documentation you've just written, you can check the parameters before using them and avoid many pitfalls. Not running things that will break down is a good thing to do.

```yaml
	- name: "Validate version is a number, > 0"
	  assert:
		that:
		  - "{{ version | int }} > 0"
		msg: "'version' must be a number and > 0, is \"{{version}}\""
```

As always, write **meaningful error messages**. There is a huge difference between a generic error message printed by Ansible and something you might have written yourself such as: `'version' must be a number and > 0, got "coucou"`.

**Use assert to bail early in case of error.**

**Write meaningful error messages.**

### 5 - Change the *stdout* default logger.

The default *stdout* Ansible logger is pretty awful. It lacks useful timing information and takes up a lot of screen real estate. That's why I'd like to suggest you try an alternate logger:

[https://github.com/octplane/ansible_stdout_compact_logger](https://github.com/octplane/ansible_stdout_compact_logger)

I am the author of this logger and here are some of its features:

- its output is more compact by default
- it displays the execution duration and current time
- in verbose mode, it displays the tasks' content in a structure and readable way (indented YAML and \n in text are actually converted to carriage returns...)
- important fields are displayed first (stdout, stderr, to name a few)
- if you use a higher verbosity, the regular Ansible logger is used instead
- it contains bugs, but it is open-source!

**Don't forget to see what's new on the internet.**

**If you use open-source code, contribute back, please.**

### 6 - Write roles in a product-oriented way.

As soon as you start writing an Ansible role, it's very tempting to put everything inside the same role in order to have some kind of reusable toolbox.

You actually need to separate role content by technical object: one role for *MongoDB*, one for *HAProxy*, another for *Tomcat*, and then use the playbook-provided glue to assemble and describe the behavior you are looking for.

This segregation allows you to merge together all the administrative tasks you might need for a given component: installation, configuration, startup, halt, update, maintenance...and only for this component.

If your code needs to act on another technical component, try to move it to another role as soon as possible. If you really need to orchestrate these tasks from a role, write a role whose sole purpose is to call other roles' actions.

The goal of this separation is to lower the dependency between the roles and the technical products, lowering the number of variables needed to run it. In the end, this will allow you to reuse roles much more quickly, which is really what we are looking for.

**A role must only handle a single technical component.**

**Write an abstract role to call the technical ones when needed.**

### 7 - Use the same role for several operations.

Technically, a role presents a single entry point, its `tasks/main.yml`. Because of this design, one might think it's difficult to have a role accomplish several actions (without using tags, cf below, or `includes`).

And still, there is a way to write reusable roles easily, including different unrelated operations.

Using a variable to decide which action to perform, you can have a small operation router inside your role:

```yaml
---
# roles/service/vars/main.yml
# by default, we ensure that service is present, configured and running.
# allowed values: present, absent, install, configure, start, stop
state: present
```

```yaml
---
# roles/service/tasks/main.yml
- include: "{{ state }}.yml"
```

```yaml
---
# roles/service/tasks/present.yml
- include: "install.yml"
- include: "configure.yml"
- include: "start.yml"
```

```yaml
---
# roles/service/tasks/install.yml
- name: add user testuser1
  user:
	name: testuser1
	state: present
	groups: wheel
```

**Allow a role to fulfill several tasks.**

### 8 - Be careful with **set_fact.**

`set_fact` creates variables in your Ansible script at runtime to have a more dynamic behavior. It's very useful. However, these on-the-fly-created variables have several drawbacks, and you need to carefully think about it before creating one:

- its priority is rather high
- it's created dynamically: you must ensure its creation won't fail and give a strange result (for example, `shell` might sometimes give you something unexpected)
- it's created dynamically: it doesn't exist until you create it. This might seem obvious but most other Ansible variables are available during the whole run.
- your code user might not be able to override it, even when she absolutely needs to

Moral of the story: be careful with the `set_fact` that can, like the proverbial poisoned apple, explode in your face at the worst time.

**Limit the `set_fact` to where they are absolutely necessary.**

### 9 - Use tags in moderation.

As soon as you write a role with several uses, it's very tempting to use tags to filter out some tasks at runtime. It works in most cases and allows you to run your deployments using the `-t` option (or `--skip-tags` to ignore them).

However, at least two problems can rise when using these in Ansible:

- the same tag can be used over and over in all of your roles and collide with each other, preventing you from using exactly the tag you want to use
- this dispersal of tags makes it difficult to understand exactly what they do

In the end, I strongly encourage you to use the routing system presented above. It does not use any additional functionality in Ansible and forces the role developer to clearly separate its role functionalities. Tags will be reserved for the interactive use of Ansible in CLI, as opposed to the one driven through a script or an orchestrator.

**Use tags, but wisely.**

### 10 - Don't hesitate to reconfigure Ansible.

Thanks to a very simple overloading model, it's possible to give Ansible an `ansible.cfg` in which you can reconfigure parts of Ansible according to your needs: for an alternative `hostfile` (no need to `-i myhosts` in CLI every time), to remove the useless `.retry` files, or anything else. You can just create an `ansible.cfg` file where you run your playbooks so that Ansible can **automatically** fetch and merge its content with the global configuration. The precedence order is as follows:

```
* ANSIBLE_CONFIG (an environment variable)
* ansible.cfg (in the current directory)
* .ansible.cfg (in the home directory)
* /etc/ansible/ansible.cfg
```
(source http://docs.ansible.com/ansible/intro_configuration.html)

**Customize your Ansible experience.**

### Wrap-up

Together we saw that the regular use of Ansible allows the emergence of good practices that will make using this tool easier every day. These tips are obviously extracted from my own personal experience and chats with developers. I probably over-simplified some of these things and forgot others. Let's recap together:

 - Always **name** your Ansible Tasks.
 - Always use the **YAML** syntax.
 - Always **document** your variables.
 - Try to **minimize** where you declare your variables.
 - Use assert to bail **early** in case of error.
 - Write **meaningful** error messages.
 - Don't forget to see what's **new** on the internet
 - If you use open-source code, **contribute** back, please.
 - A role must only handle a **single** technical component.
 - Write **abstract** roles to call the technical ones when needed.
 - Extend roles so that they can fulfill **several** tasks.
 - **Limit** the `set_fact` to where they are absolutely necessary.
 - Use tags, but **wisely**.
 - **Customize** your Ansible experience using `ansible.cfg`

What about you? How do you use Ansible? Have any tips to share? Now it's your turn!

---
authors:
  - John Blat, Lucas Ward
tags:
  - ARM
  - Graviton
  - DevOps
  - Multi-arch
  - ARM64
  - aarch64
  - amd64
  - Ampere Altra
  - Pipeline
  - GitLab
date: 2023-03-17T00:00:00.000Z
title: "Modernize your pipeline to be ARM-ready with multi-arch container builds"
---

With the release of Amazon Web Service's Graviton processors and the pivot of Apple's ecosystem to ARM based processors, multi-arch images are becoming hugely popular. Don't get left behind, transforming your workloads to run on multiple architectures is now more obtainable than ever. Converting your pipeline to build multi-arch images is a relatively straightforward and quick process that pays off in a big way. At Ippon Technologies, we utilize docker's buildx plugin to achieve building multi-arch images, and you can too! Follows is a short getting started guide, as well as some discussion as why, as a developer, it is in your best interest to build multi-arch images. 

# Why this work happens

## Larger User Base

Multi-arch images are great for shipping end-user software that may need to run on multiple different types of platforms. For instance, let's say there is a developer tool that has been Dockerized that is widely popular. With Apple's latest switch to ARM processors, not supporting the newly adopted architecture may decrease the size of the potential user base. If an organization is shipping software that is intended to run on an end-user's machine, supporting multiple architectures means the code will run natively on their machines. Some users may opt to not use the software if it is not supported natively. *Native* just means that a program can run on a machine without having to utilize a virtualization layer (for instance, Apple's Rosetta, or QEMU on windows). 

## Larger Device Pool and Cost Savings

The term cloud agnostic means being able to run your application on any cloud provider.  The same goes for architecture. Architecture agnosticism means a piece of software can run on any cpu architecture (within reason).  Some examples include x86 (32 bit), arm64 (sometimes called aarch64), and amd64 (also known as x86_64).  These are by far the most popular choices.  Examples of arm64 offerings include Apple's M1 and M2 processors, AWS Graviton Compute Instances, Raspberry Pi's Cortex-A72 chip, Googles Tau T2A compute instances, and Azures Ampre Alta machines. I know what you are thinking, Graviton, Tau, and Ampre Alta are really cool names, I agree! Again, building multi-arch containers increases the number of devices that piece of software can run on.  Not only that, but certain workloads work better on different cpu architectures.  Also, not every architecture is created the same.  Certain architectures are more power efficient and can save you money!

One final use case example before we dive into the how.  IOT applications have to run on resource constrained Single Board Computers.  This is especially true for greenfield IOT development where many different test devices may be used.  Having multi-arch images ready to go means being able to test across multiple devices.  It can even help keep a company alive.  If you had specifically built a piece of software to run on a Raspberry PI (ARM64), your company may have gone out of business during the pandemic when the things became impossible to find! 

# Where this work happens

## Either way, it's turtles all the way down<sup>1</sup>

It may be confusing at first to understand exactly what it means to convert your existing workloads to be able to run on multiple architectures. Let us look at a gross oversimplification.... All programming languages inevitably end up just being ran as machine code, or cpu instruction sets, it's all 1s and 0s eventually. Some example instruction sets include CISC, and RISC. These acronyms just mean Complex or Reduced Instruction Set Computer repectively. The main point here is, that when your application runs, it may look different at the lowest level depending on what cpu you are running on.

The cool part about this whole thing is, changes in your application code may not even be required. Often times it is as easy as using a different compiler flag or instructing docker to use a plugin called `buildx`. That's right, these changes are made during a phase of the software development lifecycle called the release phase, specifically the "build process". For this article, we will be focused on a build process that takes place in a build pipeline, namely, gitlab pipelines. 

## A Program's Birthplace Matters

A general rule of thumb, is that in order for a program to run on a certain architecture, it needs to be built on the same architecture. Roughly, this translates to, putting your source code on an ARM machine and building it will result in a binary that can run on another ARM machine. If you have low level dependencies in your code that talk to hardware, then some code changes in the application code may be required, but we are gonna save that for a different blog.

So if you have a gitlab pipeline and it uses a gitlab runner, how do you go about building for different architectures? This is where the docker buildx plugin comes in. The buildx plugin will "emulate" the desired architecture and build your artifact on that virtualization layer.  In other words, docker running inside docker. Another option would be to have two or three separate runner machines that have the base architecture. We are going to focus on the former example using buildx. The code changes that you make will happen inside your *pipeline as code* source file. For gitlab, this is the `.gitlab-ci.yml` file. If you are using Jenkins, it will be the Jenkinsfile, and github also has their own flavor, the concepts remain quite similar.

# What are the steps

For some context, we will frame the following example with a Kubernetes cluster running on Amazon Web Service's EKS. For brevities sake, I am going to assume that you already have a kubernetes cluster running and some facility operating it. If you don't, here is a very brief version of how to get up and running. Please note, this is not the only application for running multi-arch images, but definitely a relevant one if you live and breath DevOps work as your day to day. 

## Setting up an EKS cluster that can run ARM containers

Let's create an AWS EKS cluster to run our examples on and to frame some ideas. First...
* **Create an EKS cluster**
  * Create with your desired security and network settings
* **Create an ARM node group**
	* Under the compute tab of the created cluster, select `Add Node Group` 
		under the `Node Group` section
		* During Step 2, "Set Compute and Scaling Configuration," choose the AMI (Amazon Machine Image) called
			`Amazon Linux 2 Arm (AL2_ARM_64)`

Just like that, your kubernetes cluster is ready to run ARM work loads on Graviton. You could also add a regular AMD64 compute instance to the node group and operate a mixed architecture cluster. 

## Telling Kubernetes where to launch your pods

If you are like me, you may not have realized that you can actually tell kubernetes which architecture a pod should be launched on. This can be achieved by utilizing some different fields within the `yaml` file. Let's take a look at an example deployment using the public ARM busybox image `arm64v8/busybox`. Fire up your favorite code editor and create a pod. 
```yaml
# busybox-arm64.yaml
apiVersion: v1
kind: Pod
metadata:
  name: busybox
  namespace: default
spec:
  containers:
  - image: arm64v8/busybox
    command:
      - sleep
      - "3600"
    name: busybox
restartPolicy: Always
nodeSelector:
  kubernetes.io/arch: arm64
```

The important fields to notice here are:
* `spec.containers.images: arm64v8/busybox` - notice the arm specification.
* `nodeSelector.kubernetes.io/arch: arm64` - this tells kubernetes to run the pod on an arm64 node.

The label `kubernetes.io/arch` is supplied to the nodes when they are added in EKS to the node group. This is specified so that if non-arm nodes are added to the cluster, the pod will still get assigned to the matching node based on the architecture (arch for sure). Authenticate to the cluster and run `kubectl apply -f busybox-arm64.yaml`. This will deploy this pod to a node containing the `arm64` label, as desired.

We could have easily ran this same example with an AMD64 node and specifying our image as `amd64/busybox`. That is because the busybox images is *multi-arch*. If you look at the busybox page on [docker hub](https://hub.docker.com/_/busybox/tags), you should see an `OS/Arch` section under tags. Every version of every image supports multiple architectures. When you pull this image as `busybox:latest` on your local docker daemon, docker will detect your native architecture and pull the correct image.

## Building a multi-arch image and deploying to the EKS cluster

For this section, we will be building a tiny python program. This program, when it runs, prints out the architecture of the machine it is running on. After we code up the program, we will build it inside a gitlab pipeline using docker's buildx. Once built, it will be pushed to a docker registry. Finally, we will deploy it to our kubernetes cluster and see the architecture print out. Please note, additional networking/security steps may be required in order for EKS to pull from a specified container registry.  It's okay if you can't follow along for every step, the important part is understanding the bigger picture. 

In your favorite code editor, write the python program into a file named `print-platform-info.py`:
```python
import platform
print(platform.uname())    
```

Next, create a `Dockerfile` in the same directory as your python program

```Dockerfile
FROM python
COPY ./print-platform-info.py /print-platform-info
CMD ["python3", "/print-platform-info"]
```
Now this is typically where you would scamper off to the command line and run `docker build -t my_image .`, but hold off for just a minute! We have to set docker to use a different *build instance*. In the command line inside the project directory where you have your `Dockerfile` run:
```bash
docker buildx create --use
```
You may need to add some additional arguments to authenticate to your docker registy here. Check the [docs](https://docs.docker.com/engine/reference/commandline/buildx_use/) for any information or google any errors you may run into. Now it is time to run the actual build command. Back at the command prompt, run:
```bash
`docker buildx build --platform linux/arm64,linux/amd64 -t <registry-url>/<image-name>:<image-tag> . --push`
```
Let's take a look at the different pieces of this command...
* `docker buildx build` - This simply tells docker to use the buildx build instance that we set before.
* `--platform linux/arm64,linux/amd64` - tells buildx to produce an image for arm64 and amd64. The builder instance actually pulls down the native architecture image to build on.
* `-t <registry-url>/<iamge-name>:<image-tag>'` - This is where you specify your registry url and give your image a name and a tag. If you don't specify a registry, one of your images may end up living in the docker cache.
* `. --push` - tells docker to use the local directory Dockerfile and then push the built images to our registry.

Note: if you are using a private docker registry, you may need to create a `Secret` in kubernetes that contains the registry login information. The command to create such a secret looks something like this: `kubectl create secret docker-registry registry-credentials --docker-server=<registry-url> --docker-username=<username> --docker-password=<password>`

## Deployment

Okay, now that we have a cluster with an ARM node running on a Graviton compute instance, and a multi-arch image we are ready to deploy our python *architecture printing* app. Write the following yaml file and run it with `kubectl apply`.  Be sure to specify your `image-registry`, `image-name`, and `image-tag`.
```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: print-platform-info
  namespace: default
spec:
  template:
    spec:
      containers:
      - image: <image-registry>/<image-name>:<image-tag>
        name: print-platform-info
      nodeSelector:
        kubernetes.io/arch: arm64
      # Remove imagePullSecrets if using a public registry
      imagePullSecrets:
        - name: registry-credentials
      restartPolicy: Never
```
The manifest you just deployed contains a job spec. It will run the program until it's task is completed. Grab the pod name with `kubectl get pods` and then check the logs with `kubetcl logs <pod-name>`. You should see the system info printed to the output. 

## Multi-arch cluster
It is possible to set a different AMI for additional node groups added to the EKS cluster. In other words, you can have a multi architecture cluster running amd64/x86, arm64, or any other number of architectures. To deploy a pod for the created image on `amd64`, use this manifest:
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: print-platform-info
  namespace: default
spec:
  containers:
  - image: <my-registry>/<image-name>:<image-tag>
    name: print-platform-info
  restartPolicy: Always
  nodeSelector:
    kubernetes.io/arch: amd64
```
Notice that the `kubernetes.io/arch` field now reads `amd64` instead of `arm64`. This will make it so that the pod deploys to an x86 node and will thus pull down the x86 image. If a manifest without a `nodeSelector` is used, then it will be deployed to whichever node, and then pull down the appropriate image. 

## Running it in Gitlab

If you have been testing this out locally, that's great! But ideally, a process like this lives in a build pipeline. This is especially true if you are converting an existing application that already has a pipeline setup. As mentioned before, this article will focus on a Gitlab pipeline, but the concepts are similar in other pipelines too.

Jumping right in, let's take a look at the `.gitlab-ci.yml` file we eluded to earlier. This article assumes you already have a gitlab runner set up and running.
```yaml
stages:
  - docker
docker-build:
  image: docker:latest
  stage: docker
  tags:
    - docker
  services:
    - name: docker:23.0-dind
      alias: docker
  script:
    - docker buildx create --use --driver-opt network=host --buildkitd-flags '--debug --allow-insecure-entitlement network.host'
    - echo $CI_REGISTRY_PASSWORD | docker login -u $CI_REGISTRY_USER $CI_REGISTRY --password-stdin
    - docker buildx build --platform linux/amd64,linux/arm64 --provenance false -t $CI_REGISTRY_IMAGE resources/python-code --push
```
Let's take a look at some of these pieces here.
* `docker-build: -image: docker:latest` and `services: -name: docker:23.0-dind` simply tells the gitlab runner we are going to use "docker in docker".
* `docker buildx create --use --driver-opt network=host --buildkitd-flags '--debug --allow-insecure-entitlement network.host'` is a similar step to what we did locally, but with some added options to get the docker inside docker image running on a gitlab runner talking to our registry.
* `echo $CI_REGISTRY_PASSWORD | docker login -u $CI_REGISTRY_USER $CI_REGISTRY --password-stdin` authenticates our gitlab image registry. It uses "available by default" variables. You can read more about predefined variables [here](https://docs.gitlab.com/ee/ci/variables/predefined_variables.html).
* `docker buildx build --platform linux/amd64,linux/arm64 --provenance false -t $CI_REGISTRY_IMAGE resources/python-code --push` This is the meat and potatoes of the process. The only difference from before is the provenance flag, which you can read about [in a gitlab issue](https://gitlab.com/gitlab-org/gitlab/-/issues/389577) and also in the [buildx docs](https://github.com/docker/buildx/releases/tag/v0.10.0).

Congratulations! If you made it this far then you can consider yourself well equipped to start multi-arching it up! In the final section, *a peek under the hood*, we will take a look at the docker manifest as it lives in our registry.

## A peek under the hood

If we inspect the docker manifest for our multi-arch image, we can see some pretty interesting things. Go ahead and run in your command line the command to inspect the docker manifest...
```bash
docker manifest inspect <my-registry>/<image-name>:<image-tag>
```
This will output some JSON text that has a bunch of information related to the *shape* of your docker image. You may see something similar to this.
```json
{
   "schemaVersion": 2,
   "mediaType": "application/vnd.docker.distribution.manifest.list.v2+json",
   "manifests": [
      {
         "mediaType": "application/vnd.docker.distribution.manifest.v2+json",
         "size": 2283,
         "digest": "sha256:c61fddadc8e41eb862ad2336ede44f8dd54482339285019f70d2a86637ff4ed3",
         "platform": {
            "architecture": "amd64",
            "os": "linux"
         }
      },
      {
         "mediaType": "application/vnd.docker.distribution.manifest.v2+json",
         "size": 2283,
         "digest": "sha256:a9c5d27a21073b642b8f24b11661f7fbd26e39965d8b76de895b5d049ef58a2d",
         "platform": {
            "architecture": "arm64",
            "os": "linux"
         }
      }
   ]
}
```
Notice the `platform.architecture` bit in the two separate spots? This is how the registry knows that it contains a multi-arch image. Docker does this so that it can reuse certain layers of the image. Those lower level pieces that interact with the CPU have different layers, but some of the layers higher up may be exactly the same and can be reused. 

# Conclusion

Building Multi-Arch Docker images is now easier than ever. This is thanks to docker's buildx plugin, and the level of maturity that our pipelines have reached. If you want to learn more about multi-arch images or need help migrating your workloads to Graviton, drop us a line at contact@ippon.tech.

## Foot Notes
1 - "Turtles All the Way Down" is a pop culture reference. It has many different origins, but I was first introduced to the concept in Stephen King's Dark Tower series. Basically it implies that the world is built on top of a turtles back. That turtle also lives on a world, which is subsequently on top of a different turtle. This goes on ad infinitum. I like this analogy for how high level languages work, because at the end of the day, it's all just 1's and 0's.

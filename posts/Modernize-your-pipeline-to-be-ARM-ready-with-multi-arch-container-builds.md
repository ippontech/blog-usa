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

## 

Multi-arch images are great for shipping end-user software that may need to run on multiple different types of platforms. For instance, let's say there is a developer tool that has been Dockerized that is widely popular. With Apple's latest switch to ARM processors, not supporting the newly adopted architecture may decrease the size of the potential user base. If an organization is shipping software that is intended to run on an end-user's machine, supporting multiple architectures means the code will run natively on their machines. Some users may opt to not use the software if it is not supported natively. *Native* just means that a program can run on a machine without having to utilize a virtualization layer (for instance, Apple's Rosetta, or QEMU on windows). 

The term cloud agnostic means being able to run your application on any cloud provider.  The same goes for architecture. Architecture agnosticism means a piece of software can run on any cpu architecture (within reason).  Some examples include x86, arm64 (sometimes called aarch64), and amd64.  These are by far the most popular choices.  Examples of arm64 offerings include Apple's M1 and M2 processors, AWS Graviton Compute Instances, and Raspberry Pi's Cortex-A72 chip. Again, building multi-arch containers increases the number of devices that piece of software can run on.

One final use case example before we dive into the how.  IOT applications have to run on resource constrained Single Board Computers.  This is especially true for greenfield IOT development where many different test devices may be used.  Having multi-arch images ready to go means being able to test across multiple devices.  It can even help keep a company alive.  If you had specifically built a piece of software to run on a Raspberry PI (ARM64), your company may have gone out of business during the Pandemic when the things became impossible to find! 

Convince reader to go for multiarch (lens of developer / devops) - talk future proofing, cost savings, cool tech, etc... keep it short, this is not a business pitch but rather, why you as a developer should invest your time in this technology
John - many consumers - don't know what platform - platform agnosticism 
John - Not just end users, but also for local development (M1 macs)
Lucas - IOT - support multiple devices

# Where this work happens
Set up the article by talking about pipelines in general.. A few lines about why pipelines are a thing, and who this article is targeted at.  Discuss production and artifacts and lay out the ground work for understanding.
WHERE - devops and infrastructure peeps
where code lives in pipeline, during local build process, etc... Touch on Jenkins, Gitlab, github actions, circle CI

# What are the steps
Actual steps to do the work here (gitlab pipeline) - briefly touch on this and that between jenkins, gitlab, github, circle ci, etc...
## Setting up an EKS cluster that can run ARM containers
- Create EKS cluster
	- Create with your desired security and network settings
- Create node group
	- Under the compute tab of the created cluster, select `Add Node Group` 
		under the `Node Group` section
		- During Step 2, "Set Compute and Scaling Configuration," choose the AMI (Amazon Machine Image) called
			`Amazon Linux 2 Arm (AL2_ARM_64)`
			
- Deploying an arm pod
    - For this example, the public arm busybox image will be used `arm64v8/busybox`
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
    - The important fields to notice are:
        -  `spec.containers.image` where the arm version of the image is specified
        - `nodeSelector.kubernetes.io/arch` which will tell kubernetes to select nodes with the arm64 label. `kubernetes.io/arch` is a supplied label that the nodes will get when. they are added in the EKS node group. This is specified so that if non-arm nodes are added to the cluster, the pod will still get assigned to the matching node based on architecture.
    - Authenticate to the cluster and set the correct `kubectl` context
    - Running `kubectl apply -f busybox-arm64.yaml` will deploy this pod to a node containing the `arm64` label

## Building a multiarch image and deploying to the EKS cluster
- The following example demonstrates:
    - A python application `print-platform-info.py` that prints system info
    - Building `print-platform-info.py` into a multi-arch image `print-platform-info` for amd64 (x86) and arm64
    - Pushing the `print-platform-info` image to a container registry of choice
    - Creating a kubernetes pod that will pull the arm `print-platform-info` image
- Additional networking/security steps may be needed in order for the EKS cluseter to pull from the container registry of choice. Checking the status of the pod and seeing an image pull error is the likely error if this is not configured correctly.
- Place the following files into the same directory
- Contents of `print-platform-info.py`:

```python
import platform
print(platform.uname())    
```

- Contents of `Dockerfile`:

```Dockerfile
FROM python
COPY ./print-platform-info.py /print-platform-info
CMD ["python3", "/print-platform-info"]
```
- To build the image for amd64 arch, simply run the following commands in the same directory as `print-platform-info.py` and the `Dockerfile`: 
    - `docker buildx create --use`
        - Add any additional options in the above command for network settings if needed to push to the container registry of choice
    - `docker buildx build --platform linux/arm64,linux/amd64 -t <registry-url>/<image-name>:<image-tag> . --push`
        - After this command runs, the image should be pushed to the registry
        - Because of the platform options specified, when docker pulls down this image in the run environment, it will pull down whatever platform it is currently on. 
- If using a private registry, you will have to create a Kubernetes `Secret` with the registry credentials. One method to do this is running: `kubectl create secret docker-registry registry-credentials --docker-server=<registry-url> --docker-username=<username> --docker-password=<password>`
- For deployment of this image in the EKS cluster, `kubectl apply` the following manifest
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
- The above manifest is a Job since we're just running something that completes a task
- Use `kubectl get po` to find the name of the pod
- The logs for this pod can be checked with `kubectl logs <pod-name>` and the system info will be visible from the output of the python program

## Multi-arch cluster
- It is possible to set a different AMI for additional node groups added to the EKS cluster. In other words, you can have a multi architecture cluster running amd64/x86, arm64, or any other number of architectures.
- To deploy a pod for the created image, use the manifest:
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
- Notice that the `kubernetes.io/arch` field now reads `amd64` instead of `arm64`. This will make it so that the pod deploys to an x86 node and will thus pull down the x86 image.
- If a manifest without a `nodeSelector` is used, then it will be deployed to whichever node, and then pull down the appropriate image

# A peek under the hood
Talk about multi-arch in the pipeline, vs local, vs with jenkins, using buildx, discuss separate runners, get technical here if you want

BuildX - architecture emulation for building on non-native platforms. OR, using multiple runners to build native multi-arch
Example mac m1 rosetta - running amd images on arm and vice-versa - why is this bad and slow. - you can rely on virtualization, but speed will take a hit.
`docker manifest inspect docker-registry.ippon.fr/usa-bench-projects/arm64-graviton/bench-wave2/jblat/blog-multi-arch-code:latest`

```
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
# Small conclusion and ippon buisness pitch


# LINKS
https://docs.gitlab.com/ee/ci/variables/predefined_variables.html - gitlab variables - registry - runner information


---
authors:
- Amine Ouali Alami
tags:
- Spring Boot
- JHipster
- GitHub
- GitHub Actions
- DevOps
- AWS
- Amazon ECS
- AWS Fargate
- Amazon ECR

date: 2020-20-10T20:16:10.000Z
title: "Deploy to ECS using GitHub Actions"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/01/deployToEcsWithGithubAction.jpg
---

Building a DevOps platform nowadays can become a challenge for architecture and platform infrastructure teams, due to the integration complexity with many platforms: Cource Control, CI/CD, Artifact Repositories, Quality, SAST/SCA, Deployment, etc. Many DevOps leader believes that the challenge of DevOps is to ultimately reduce the number of platforms involved in one single pipeline.
That's why tech giants are trying to integrate all pipeline steps into their platforms.
In this article, we will deploy a simple [Spring Boot](https://spring.io/projects/spring-boot) application to [Amazon Elastic Container Service (ECS)](https://spring.io/projects/spring-boot) using the new GitHub feature called [GitHub Actions](https://github.com/features/actions).

# What is GitHub Actions?
GitHub Actions enables you to create custom software development lifecycle pipelines directly in your GitHub repository.
You can write individual tasks, called "actions" and combine them to create a custom pipeline.
Behind the scenes, your pipeline is run on GitHub-hosted machines, called "runners".


# Create Your First Pipeline With Github Actions
On your GitHub repository select the **Actions** tab.
GitHub propose popular pipelines to start with. We will choose: **Deploy to Amazon ECS**

![01](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/01/deployToEcsWithGithubAction-01.png)

It will add a file to your repository (/.github/workflows/aws.yml) that represents your GitHub Actions.
We add two more steps to set up the JDK 8 and the maven command to build the Spring Boot JAR file.


```json
on:
  push:
    branches:
      - master

name: Deploy to Amazon ECS

jobs:
  deploy:
    name: Deploy
    runs-on: ubuntu-latest

    steps:
    - name: Checkout
      uses: actions/checkout@v1

    - name: Set up JDK 11
      uses: actions/setup-java@v1
      with:
        java-version: 11
    - name: Build with Maven
      run: mvn clean install

    - name: Configure AWS credentials
      uses: aws-actions/configure-aws-credentials@v1
      with:
        aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
        aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
        aws-session-token: ${{ secrets.AWS_SESSION_TOKEN }}
        aws-region: us-east-2

    - name: Login to Amazon ECR
      id: login-ecr
      uses: aws-actions/amazon-ecr-login@v1
    
    - name: Build, tag, and push image to Amazon ECR
      id: build-image
      env:
        ECR_REGISTRY: ${{ steps.login-ecr.outputs.registry }}
        ECR_REPOSITORY: dcdojo-amine
        IMAGE_TAG: ${{ github.sha }}
      run: |
        # Build a docker container and
        # push it to ECR so that it can
        # be deployed to ECS.
        docker build -t $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG .
        docker push $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG
        echo "::set-output name=image::$ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG"

    - name: Fill in the new image ID in the Amazon ECS task definition
      id: task-def
      uses: aws-actions/amazon-ecs-render-task-definition@v1
      with:
        task-definition: task-definition.json
        container-name: dcdojo-container
        image: ${{ steps.build-image.outputs.image }}

    - name: Deploy Amazon ECS task definition
      uses: aws-actions/amazon-ecs-deploy-task-definition@v1
      with:
        task-definition: ${{ steps.task-def.outputs.task-definition }}
        service: dc-dojo-service-github
        cluster: dcdojo-cluster
        wait-for-service-stability: true

```

This pipeline  will checkout the code, build the JAR file, build and push the Docker image to [Amazon Elastic Container Registry (ECR)](https://aws.amazon.com/ecr/), and then  deploy a new task definition to Amazon ECS, on every push to the master branch.

Before using this pipeline, you will need to complete all the necessary infrastructure configuration on AWS:
- Create an ECS repository
- Create an ECS task definition, an ECS cluster, and an ECS service. [ECS getting started](https://us-east-2.console.aws.amazon.com/ecs/home?region=us-east-2#/firstRun)
- Store your ECS task definition as a Json file in your repository

The task-definition used in that project:

task-definition.json
```json
{
  "family": "dcdojo",
  "executionRoleArn": "arn:aws:iam::112233445566:role/ecsTaskExecutionRole",
  "containerDefinitions": [
      {
          "name": "dcdojo-container",
          "image": "112233445566.dkr.ecr.us-east-2.amazonaws.com/dcdojo-amine:4622390a2b34f72y5f389cb62128a305641942cd",
          "cpu": 512,
          "portMappings": [
            {
              "hostPort": 8888,
              "protocol": "tcp",
              "containerPort": 8888
            }
          ],
          "memory": 1024,
          "essential": true,
          "logConfiguration": {
            "logDriver": "awslogs",
            "options": {
              "awslogs-group": "/ecs/dcdojo",
              "awslogs-region": "us-east-2",
              "awslogs-stream-prefix": "ecs"
            }
          }
      }
  ],
  "requiresCompatibilities": [
    "FARGATE"
  ],
  "networkMode": "awsvpc",
  "cpu": "1024",
  "memory": "2048"
}
```

and a Dockerfile:

```json
FROM openjdk:8-jdk-alpine
RUN apk --no-cache add curl
VOLUME /tmp
EXPOSE 8888
ADD target/dcdojo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]
```

After running this pipeline, the results looks like:

![02](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2020/01/deployToEcsWithGithubAction-02.png)



# Conclusion
GitHub is actively working to integrate all necessary DevOps features in their platform such as GitHub Package, which stores your artifacts, and security alerts that track security vulnerabilities that apply to your project dependencies (Software Composition Analysis).
If you are using GitHub at an enterprise level or hosting an open-source project, these new features can be a serious alternative to the classic DevOps toolkit.

### Sources
* [GitHub Actions Documentation](https://help.github.com/en/actions)

---
authors:
- Amine Ouali Alami
tags:
- Continuous Code Quality
- Jenkinsfile
- Quality Gate
- DevOps
- Jenkins
- SonarQube
- Fixing the water leak
date: 2010-01-23T10:00:00.000Z
title: Implement a Continuous Code Quality approach
---
Imagine your self waking up in the morning and find a huge puddle of water in the middle of your kitchen, the first thing people start doing is to find the water leak and fix it. Once is done we can start to clean up the mess.
Code quality in a development project should be treated exactly the same.

In a traditional approach of quality, the development team should audit the code before each publication. This is not always the right approach in the long term:
- The code review occurs late in the process. All parties are waiting for the new product.
- The team is not necessarily aware of code quality at the time of development.


In a continuous code quality approach, the team focuses on new code:
- It is difficult for the team to review the product's legacy code but is generally delighted to fix the most recent code changes.
- The team is responsible for the quality of the code.
- Go or no-go criteria are clear and shared by everyone because they apply to the new code regardless of the context of the project.
- The quality cost is reduced because it is part of the development process.


# Quality gate
The SonarQube Quality Gate is a way to enhance the quality of your project. It is an integral part of your DevOps pipeline and answers a simple question: can I deliver my code?

To answer this question the Quality Gate is based on a series of conditions. For example :
* No new blocking issues
* Code coverage > 80%

![01](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/02/continuous-code-quality-01.PNG)

The quality gate status is visible in the homepage of the project space

![02](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/02/continuous-code-quality-02.PNG)

![03](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/02/continuous-code-quality-03.PNG)

To implement the quality gate in your pipeline you can add a step in your Jenkinsfile after the build and unit test steps.

The Jenkins [SonarQube Scanner plugin](https://plugins.jenkins.io/sonar) must be installed and configured

```
stage('Quality Analysis') {
    withSonarQubeEnv('sonarqube-server') {
       sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar'
    }
}
```

The details of the scan can be accessed directly in your SonarQube project space:

![04](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/02/continuous-code-quality-04.PNG)

In a Continuous Code Quality approach, it is necessary to retrieve the result of the quality gate directly into the pipeline. If the quality gate failed the pipeline must stop.

We can add the next step in our Jenkinsfile just after the sonar analysis step

```
stage("Quality Gate"){
  timeout(time: 1, unit: 'HOURS') {
    def qg = waitForQualityGate()
    if (qg.status != 'OK') {
        error "Pipeline aborted due to quality gate failure: ${qg.status}"
    }
  }
}
```

# Conclusion
To be successful the quality gate must be run at an early stage of developement, implemeting a quality gate in your project guaranty that your code quality moves in the right direction and that will be no surprise at release time.

##### References
* [Quality Gate](https://docs.sonarqube.org/latest/user-guide/quality-gates/)
* [Fixing the Water Leak](https://docs.sonarqube.org/latest/user-guide/fixing-the-water-leak/)

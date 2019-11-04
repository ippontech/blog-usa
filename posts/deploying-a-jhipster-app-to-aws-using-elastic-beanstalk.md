---
authors:
- Theo Lebrun
tags:
- JHipster
- AWS
- Elastic Beanstalk
date: 2019-11-04T14:50:55.000Z
title: "Deploying a JHipster app to AWS using Elastic Beanstalk"
image: https://raw.githubusercontent.com/falydoor/blog-usa/jhipster-on-aws-with-beanstalk/images/2019/11/jhipster-beanstalk.png
---

[JHipster](https://www.jhipster.tech/) is a great development platform to help you bootstrap a modern Web app in less than 5 minutes. One small detail is that everything runs on your local laptop which can be an issue if you want to showcase your app to external people. Cloud providers like [AWS](https://aws.amazon.com/), [Azure](https://azure.microsoft.com) or [GCP](https://cloud.google.com/) are the current leaders and they provide services to help you deploy your JHipster app to the Cloud.

In this blog, I will focus on AWS and especially on Elastic Beanstalk which is an orchestration service. Elastic Beanstalk helps you create a production-ready infrastructure that has the architecture below:

![](https://raw.githubusercontent.com/falydoor/blog-usa/jhipster-on-aws-with-beanstalk/images/2019/11/diagram-eb.png)

# App generation with JHipster

Currently the Elastic Beanstalk sub-generator only supports SQL databases (Oracle and Microsoft SQL Server excluded). Our JHipster app will be a basic monolith that uses React and PostgreSQL as a database.

```json
{
  "generator-jhipster": {
    "promptValues": {
      "packageName": "com.company",
      "nativeLanguage": "en"
    },
    "jhipsterVersion": "6.4.1",
    "applicationType": "monolith",
    "baseName": "jhipster",
    "packageName": "com.company",
    "packageFolder": "com/company",
    "serverPort": "8080",
    "authenticationType": "jwt",
    "cacheProvider": "ehcache",
    "enableHibernateCache": true,
    "websocket": false,
    "databaseType": "sql",
    "devDatabaseType": "h2Disk",
    "prodDatabaseType": "postgresql",
    "searchEngine": false,
    "messageBroker": false,
    "serviceDiscoveryType": false,
    "buildTool": "maven",
    "enableSwaggerCodegen": false,
    "embeddableLaunchScript": false,
    "useSass": true,
    "clientPackageManager": "npm",
    "clientFramework": "react",
    "clientTheme": "none",
    "clientThemeVariant": "",
    "testFrameworks": [],
    "jhiPrefix": "jhi",
    "entitySuffix": "",
    "dtoSuffix": "DTO",
    "otherModules": [],
    "enableTranslation": true,
    "nativeLanguage": "en",
    "languages": [
      "en"
    ],
    "blueprints": []
  }
}
```

The `.yo-rc.json` above can be used to avoid answering all the questions manually.

# Deployment to AWS with Elastic Beanstalk

 First, make sure that your AWS CLI is [correctly configured](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-configure.html#cli-quick-configuration) and the account must have the `AWSElasticBeanstalkFullAccess` policy. If you use a named profile different than `default`, you just have to set the environment variable `AWS_PROFILE` with the correct profile.

 The command `jhipster aws` will ask questions about the AWS deployment:

 ![](https://raw.githubusercontent.com/falydoor/blog-usa/jhipster-on-aws-with-beanstalk/images/2019/11/jhipster-aws.png)

 The AWS account used needs access to the S3 bucket since it will be used as a storage location for the application's Jars. The sub-generator will also create a RDS instance so make sure that the database credentials are correct and match the ones in `application-prod.yml`. The application will be always packaged using the `prod` profile, which means that all the tests must be green!

 Once the Elastic Beanstalk application is created, navigate to the [Elastic Beanstalk console](https://console.aws.amazon.com/elasticbeanstalk/home) to view the details of the application. Use the public URL to check that everything was correctly deployed and running.

 ![](https://raw.githubusercontent.com/falydoor/blog-usa/jhipster-on-aws-with-beanstalk/images/2019/11/eb-url.png)

 If the deployment failed, the logs page can be used to view all the logs of the application. The logs from `/var/log/tomcat8/catalina.out` are the JHipster logs, those should be used to find out why the application did not start.

# Elastic Beanstalk application improvements

By default, your Elastic Beanstalk application uses a classic load balancer with auto scaling up to 4 instances. The default configuration is enough for demo purpose, but if you expect an heavy load I recommend updating the configuration. Also, it might be a better idea to set the scaling trigger to the `CPUUtilization` metric instead of `NetworkOut`.

An other good practice is to enable `S3 log storage` or `CloudWatch Logs` in `Configuration > Software`. It is always a good idea to have a strong log retention policy with a production application in order to track faulty/suspicious behavior.

 ![](https://raw.githubusercontent.com/falydoor/blog-usa/jhipster-on-aws-with-beanstalk/images/2019/11/eb-configuration.png) 

 The `Rolling updates and deployments` configuration should also be changed, especially if the application can't be out of service for few minutes. More details can be found on [the AWS documentation page](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/using-features.rolling-version-deploy.html?icmpid=docs_elasticbeanstalk_console).

 # Conclusion

Elastic Beanstalk saves you a lot of time and removes all the complexity of creating a production ready infrastructure from scratch. The JHipster AWS sub-generator is easy to use and let you create your Elastic Beanstalk environment in few steps.

Since the sub-generator uses a default Beanstalk configuration, I recommended tweaking it a little bit to fulfill your production requirements.
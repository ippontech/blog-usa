---
authors:
- Romain Lheritier
categories:
- angular.js
- angular.js training
- AngularJS
- framework
- JavaScript
date: 2014-05-06T09:42:07.000Z
title: "Save your AngularJS code!"
image: 
---

By [Marina Sinama-Pongolle](https://www.linkedin.com/in/marinasinamapongolle)

At this year’s [Devoxx](http://cfp.devoxx.fr/devoxxfr2014), I loved the session titled “Help! My AngularJS code is horrible”!  
 Thierry Chatel ([@ThierryChatel](https://twitter.com/ThierryChatel)) outlined the best practices needed to keep AngularJS code clean and maintainable.


# General best practices

The first best practice is to write simple and maintainable code. Thierry advises against premature optimization. For instance, don’t hesitate to execute a function multiple times. It won’t cost more resources. Then, to have a data model suitable to the application, it is best to work on an actual model instead of just using the JSON returned by backend services.


# Application structure

To structure the application, Thierry advises to start with a functional organization.

For instance:

- front/ - profiles/
- repositories/
- activity/
- contributions/
- back/ - statistics/
- common/

Then, subfolders are organized by type (controllers, services, etc.) To simplify tests, it is good to have: one JS file = one module.

For instance:

- front/ 
 - front.js
 - profiles/ 
     - profiles.js
     - profiles-controller.js
     - profiles-service.js
     - profiles-directive.js
 - repositories/ 
     - repositories.js



# Routes

To better manage routes, Thierry advises to create one routes declaration file per module. To get a certain route, use: **$route.current**


# Controllers

A controller is used to initialize the scope. Try not to use it to process data and do a lot of business logic. A controller is the link between services and scope.

To have lightweight controllers, we can create local controllers to manage view sections: one controller per view section.

We can create controllers on repeated scopes:

```language-html
<ul><br></br>
<li ng-repeat="item in list" ng-controller="ItemCtrl">...</li><br></br>
</ul><br></br>
```


# Services

- Services contain the business logic.
- Functionality is encapsulated in a service. The entire functionality needs to reside in the service. That means: no business logic in templates and controllers.



For instance:

- Bad:  
`ng-class="{alert: quantity(row) >= 100}"`
- OK:  
`ng-class="{alert: orderSrv.isAlert(row)}"`

- Services can save data and process it.
- Organize your services by layer.
- To handle errors and notifications, use this organization: - An interceptor **$http**
- Service override **$exceptionhandler**
- A service for errors
- A service for server logging
- A notification service




# Directives

Use directives to create your own elements or to personalize HTML elements.  
 Try to leverage HTML instead of replacing it (like in JSF). For instance, you can add a directive to an existing HTML element. It personalized the HTML element. Then, DOM manipulation is rather easy.


# Conclusion

- Start with HTML
- Have a good knowledge of Javascript to make the most of its features.
- Use object-oriented programming best practices.

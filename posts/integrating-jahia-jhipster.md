---
authors:
- Dennis Sharpe
categories:
- jahia
- JHipster
date: 2015-02-09T09:02:16.000Z
title: "Integrating Jahia and JHipster 2"
image: 
---

## Introduction

The purpose of this article is to discuss and demonstrate one design pattern for integrating [Jahia](https://www.jahia.com/home) (CMS) with a [JHipster](https://jhipster.github.io/) application.

## Problem Statement

The customer needs to be able to create content for their website but the website is currently a single page application. The customer wants to be able to create and publish articles on the site but without any manual file publishing or HTML knowledge. The customer may also want to use some of the workflow features (in the future) that a CMS provides.

## Solution Summary

Several CMS products (such as Jahia) provide their own environment for building an entire website using their tools and architecture. However, instead of being dependent on the CMS tool’s design patterns, it is preferred to design the website independently of any product’s architecture. That allows for rapid development tools (like JHipster) to be used to speed up application development but still take advantage of the CMS features.

## High-Level Design

The architecture for this design entails a web server running the JHipster application and a separate web server running Jahia. The Jahia Digital Factory console is where all the articles are created/modified. The JHipster application makes REST calls to the Jahia server to retrieve the articles. This design has the AngularJS front-end code calling Jahia directly instead of calling a Spring service running in the JHipster application that then makes the calls to Jahia. Depending on the specific requirements, calling a Spring service is a perfectly acceptable alternative. For simplicity, security is turned off on all the content (for read-only) and that could be a reason to prefer funneling all the content through Spring.

## Implementation

##### Jahia

For this example, a REST service is called to retrieve all content of the type “jnt:article”. This means that articles can be entered on any page anywhere on the Jahia site and they will get picked up. Jahia uses what is called a “Content:Structured” type “Article (title and introduction)” in the console to create the content. Of course this could be made much more complex if desired. The Jahia console provides a full rich text editing environment and also allows for images. The images are imported into Jahia and then are referenced correctly with absolute URL links inside the content.

![](http://i.imgur.com/yphr22I.png "source: imgur.com")![](http://i.imgur.com/B5T8WTl.png "source: imgur.com")

![](http://i.imgur.com/uSAc8W3.png "source: imgur.com")

### JHipster

All of the code required in JHipster lives in the AngularJS tier of the application. The service, controller, and HTML contain the code of interest in this example. The audit code included in JHipster provided a nice starting point to create the necessary modules.

#### Service

```language-javascript
‘use strict’;

angular.module(‘jhipster21App’)
	.factory(‘ArticlesService’, function ($http) {
		return {
			findAll: function () {
				return $http.get(‘http://localhost:8080/modules/api/jcr/v1/live/en/types/jnt__article’).then(function (response) {
					return response.data;
				});
			},
			findById: function (id) {
				return $http.get(‘http://localhost:8080/modules/api/jcr/v1/live/en/nodes/’ + id).then(function (response) {
					return response.data;
				});
			}
		};
	});
```

 This service provides “findAll” and “findById” by calling different REST services provided by Jahia. There are many different options but these two work for the simple case. The hostname and port would be configurable in a real world scenario. The first URL returns all nodes of type “jnt:article”. Since that mainly just returns the node ids, a query must be done on the id to get the article’s title and intro text.

#### Controller

```language-javascript
‘use strict’;

angular.module(‘jhipster21App’)
	.controller(‘ArticlesController’, function ($scope, $translate, $sce, ArticlesService) {
		$scope.articles = {};
		var articleIds = [];

		$scope.fullArticle = function (id) {
			$scope.fullArticleTitle = $scope.articles[id].title;
			$scope.fullArticleText = $sce.trustAsHtml($scope.articles[id].text);
		};

		ArticlesService.findAll().then(function (data) {
			var i = 0;
			angular.forEach(data, function (value) {
				articleIds[i++] = value.id;
			});

			var j = 0;
			angular.forEach(articleIds, function (value) {
				ArticlesService.findById(value).then(function (data) {
					$scope.articles[j++] = {
						’title’: data.properties.jcr__title.value,
						’text’: data.properties.intro.value
					};
				});
			});
		});
	});
```

 This controller provides the “fullArticle” function which is called from the HTML. It populates variables for displaying the full article title and text under a table containing just the titles. The remainder of the controller calls the “findAll” service and then calls the “findById” service on every article to get the title and text. This is a simplified approach that would have to be modified if there were a large number of articles. An array of article objects is created and put into $scope for display on the HTML page.

#### HTML

```language-html
<div>
	<h2 translate=”articles.title”>Articles</h2>

	<table class=”table table-condensed table-striped table-bordered table-responsive”>
		<thead>
		<tr>
			<th>{{‘articles.table.header.title’ | translate}}</th>
			<th>{{‘articles.table.header.link’ | translate}}</th>
		</tr>
		</thead>

		<tr ng-repeat=”(k, v) in articles”>
			<td>{{v.title}}</td>
			<td><a href=”” ng-click=”fullArticle(k)”>{{‘articles.table.data.fullArticle’ | translate}}</a></td>
		</tr>
	</table>

	<div ng-show=”fullArticleText”>
		<h3>{{fullArticleTitle}}</h3>
		<span ng-bind-html=”fullArticleText”></span>
	</div>
</div>
```

 This HTML contains a table with all the article titles and links to display the full article below. The index of the article array is passed to the “fullArticle” function to populate the “fullArticleText” and “fullArticleTitle” variables for display.![](http://i.imgur.com/W7eEGVn.png "source: imgur.com")

## Conclusion

The design pattern explained above solves the problem of integrating Jahia with JHipster in order to take advantage of many of the features of both. This is intended to be a starting point that can be taken as far as needed to solve for complex problems.

## Full Code

The full code for this example is in [github](https://github.com/dsharpe/blog-jahia-jhipster2).

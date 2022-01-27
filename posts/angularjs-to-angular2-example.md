---
authors:
- Matt Ritter
tags:
- Angular
- Front-End
date: 2017-07-20T16:29:00.000Z
title: "AngularJS to Angular2(V4): A basic example"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/07/AngularJS-to-Angular2-V4--Blog.png
---

>To avoid confusion, I will be referring to AngularJS as AngularJS and Angular as Angular2.  Angular is currently at what Google refers to as Angular4 while AngularJS is at v1.6.X.

While Angular2 can be built with Javascript, it was developed with Typescript in mind; most documentation and information assumes you will be using Typescript. I’m going to take a few simple examples of AngularJS and rewrite them in Angular2.  I will be using the latest version of AngularJS. As of AngularJS v1.5, AngularJS introduced components to allow a better path to Angular2. If you are used to AngularJS pre v1.5, a component wraps the controller and template into one.

This example is the usual tutorial list, which includes two modules, list and item, and a service.  They will be named the same in both AngularJS and Angular2.  Both are in two separate repos, [AngularJS repo](https://bitbucket.org/IpponMattRitter/angularjsto2-angularjs.git)  and [Angular2 repo](https://bitbucket.org/IpponMattRitter/angularjsto2-angular2.git).  In the AngularJS example, the app is named "JSv2js," while in Angular2 the app is named "JSv22."

Angular2, in my example and most others, is developed in Typescript. This requires compiling, so while the AngularJS version is straight html and js, the Angular2 project requires the repo’s NPM packages to be installed. You will need to compile the typescript `npm run webpack` in the console to update the Javascript if you make changes to the Typescript.

## Starting from the top.
Let's take a look at the index files.
### AngularJS
```html
<!doctype html>
<html lang="en" ng-app="JSv2js">
  <head>
    <meta charset="utf-8">
    <title>AngularJS and Angular2 AngularJS Version</title>
    <script src="angular-1.6.4/angular.js"></script>
    <script src="angular-1.6.4/angular-route.js"></script>
    <script src="app.module.js"></script>
    <script src="app.config.js"></script>
    <script src="genericService/genericService.module.js"></script>
    <script src="genericService/genericService.service.js"></script>
    <script src="genericList/genericList.module.js"></script>
    <script src="genericList/genericList.component.js"></script>
    <script src="genericDetail/genericDetail.module.js"></script>
    <script src="genericDetail/genericDetail.component.js"></script>
  </head>
  <body>
    <div ng-view></div>
  </body>
</html>
```
In AngularJS, we have `ng-app` attached to the html tag. It could be attached to the body or another tag. `Ng-app` tells Angular what app to use, and `ng-view` is where the Angular app is to be displayed.
### Angular2
```html
<html>
  <head>
    <base href="/">
    <title>Angular 2</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
  </head>
  <body>
    <JSv22>Loading...</JSv22>
  </body>
  <script type="text/javascript" src="./js/app.js"></script>
</html>
```
In Angular2, we have the tag `<JSv22></JSv22>` that tells Angular2 where to display the app.  For Angular2, we also have `<base href=“/“>` at top of the `<head>` tag.  This is necessary for routing.

In AngularJS, I am loading all JS files into index.html, while in Angular2 I’m just loading app.js. This is necessary, as Angular2 Typescript must be compiled to Javascript, so I went ahead and bundled them all together as well. You can bundle JS files into one for AngularJS.
## Drilling down into app.module.js and app.module.ts.
### AngularJS
```javascript
// app.module.js for AngularJS
'use strict';
angular.module('JSv2js', [
  'ngRoute',
  'SharedServices',
  'genericList',
  'genericDetail'
]);
```
In AngularJS, we create our module with `angular.module()`, named `JSv2js`, and list its decencies (ngRoute, SharedServices, genericList, and genericDetail).
### Angular2
```typescript
// app.module.ts for Angular2
import { NgModule }      from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import{ AppComponent } from './app.component';
import {AppRoutingModule } from "./app-routing.module";
import {GenericListModule } from "./genericList/generic-list.module";
import {GenericDetailModule} from "./genericDetail/generic-detail.module";
import {GenericServiceModule} from "./sharedServices/generic-service.module";
@NgModule({
	imports: [
		BrowserModule,
		GenericServiceModule,
		GenericListModule,
		GenericDetailModule,
		AppRoutingModule
	],
	declarations: [
		AppComponent,
	],
	bootstrap: [ AppComponent ]
})
export class AppModule { }
```
In Angular2, an Angular Module is created with `@NgModule` and imports dependencies.  BrowserModule is an Angular2 module necessary for rendering the app in the browser. GenericServiceModule, GenericListModule, and GenericDetailModule are modules created for this app. In addition to those modules, a routing module, AppRoutingModule, is imported. Unlike AngularJS, a component goes with this module which must be declared (all components must be declared), and then it is bootstrapped since it is the most parent module. In Typescript, all modules, components, services, and types used in that file must be imported.
```typescript
import { NgModule }      from '@angular/core';
```
This is for Typescript, and should not be confused with the imports that are part of Angular2's modules.
```typescript
imports: [
        BrowserModule,
    ],
```
## Routing
AngularJS uses `app.config.js`, while Angular2 uses a routing module, `app-routing.module.ts`.
### AngularJS
```javascript
// app.config.js for AngularJS
'use strict';
angular.
  module('JSv2js').
  config(['$locationProvider' ,'$routeProvider',
    function config($locationProvider, $routeProvider) {
      $locationProvider.hashPrefix('!');
      $routeProvider.
        when('/list', {
          template: '<generic-list></generic-list>'
        }).
        when('/item/:itemId', {
          template: '<generic-detail></generic-detail>'
        }).
        otherwise('/list');
    }
  ]);
```
In `app.config.js`, configuration is being added to the module `JSv2js`, with the dependencies `$locationProvider` and `$routeProvider`, both of which are necessary for routing urls to their associated components.
```javascript
$routeProvider.
        when('/list', {
          template: '<generic-list></generic-list>'
        })
```
### Angular2
```typescript
// app-routing.module.ts
import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GenericListComponent }      from './genericList/generic-list.component';
import { GenericDetailComponent }  from './genericDetail/generic-detail.component';
const routes: Routes = [
  { path: '', redirectTo: '/list', pathMatch: 'full' },
  { path: 'list',  component: GenericListComponent },
  { path: 'item/:id', component: GenericDetailComponent },
];
@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}
```
Like AngularJS, each path is associated with a component.
```typescript
const routes: Routes = [
  { path: '', redirectTo: '/list', pathMatch: 'full' },
  { path: 'list',  component: GenericListComponent },
```
As mentioned earlier, we mostly import these components at the top of the Typescript module, even though in `app.module.ts` we import their modules. These components are also exported in their modules, and that will be covered lower.
## Services and Providers
In both the AngularJS and Angular2 examples, a basic service is used. An array of objects is created and returned by `getAll()`, and an individual item is returned by `getById(id)`.
### AngularJS
```javascript
// genericService.service.js for AngularJS
angular.
  module('SharedServices').factory("GenericService", function(){
    var genericService = {};
    genericService.genericItems = [
        {
          id: '1',
          name: 'Aurelia',
          description: 'Aurelia is a genus of scyphozoan jellyfish, commonly called moon jellies. There are at least 13 species in the genus Aurelia including many that are still not formally described.'
        },
        {
          id: '2',
          name: 'Deepstaria',
          description: 'A genus of jellyfish known for their thin, sheet-like bodies.'
        },
    ];
    genericService.getAll = function(){
        return genericService.genericItems;
    }
    genericService.getById = function(id){
        for(var item in genericService.genericItems){
            if(genericService.genericItems[item].id === id) {
                return genericService.genericItems[item];
            }
        }
    };
    return genericService;
});
```
I am omitting the code for the `genericService.module.js`, as it just attaches the service GenericService to the SharedServices module. GenericService factory is created with the genericService service returned.
### Angular2
```typescript
// generic-service.module.ts for Angular2
import { NgModule,  }      from '@angular/core';
import {GenericService } from "./generic-service.service";
@NgModule({
	providers: [
		GenericService
	]
})
export class GenericServiceModule { }
```
The SharedServicesModule imports the `generic-service.service`.  It then provides this service to all modules that import the SharedServicesModule.  This creates a singleton, a single service that is shared. This is important to note in because if you are using the service to store information between different components, having the services provided twice, in two different modules, you would create two different services. You could end up storing data in one service and trying to retrieve it another, which wouldn’t have that data.
```typescript
// generic-service.service.ts for Angular2
import {Injectable } from "@angular/core";
@Injectable()
export class GenericService {
    // code closely mirrors the AngularJS version so am not displaying here
}
```
In order for a service to be injected into a component or other service, it must be decorated with `@Injectable()`.  The code within the Angular2 service mirrors the AngularJS example, so it is omitted here.
## GenericList, the list of items.
GenericList is a component that lists an array of items. Those items are provided by GenericService from SharedServicesModule.
### GenericListModule
#### AngularJS
```javascript
//genericList.module.js for AngularJS
'use strict';
angular.module('genericList', ['SharedServices']);
```
I'm adding the `genericList` module, with a dependency of `SharedServices` to the AngularJS app.
#### Angular2
```typescript
//generic-list.module.ts for Angular2
import { NgModule,  }      from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {GenericListComponent } from "./generic-list.component";
@NgModule({
	imports: [
		BrowserModule
	],
	declarations: [
		GenericListComponent,
	],
	exports: [
		GenericListComponent
	]
})
export class GenericListModule { }
```
A difference from AngularJS is that SharedServices is not being imported into the GenericListModule.  It was imported in `app.module.ts`. That instance of SharedServices is then made available to all modules that are imported by `app.module.ts`.  GenericListComponent is declared and exported.  The declaration adds the component to the module, while the export makes the component available to other modules that import GenericListModule. In this case, GenericListModule is imported by AppModule and GenericLisComponent is available to AppRoutingModule.
### GenericListComponent
#### AngularJS
```javascript
//genericList component for AngularJS
'use strict';
angular.
  module('genericList').
  component('genericList', {
    templateUrl: 'genericList/genericList.template.html',
    controller: ['GenericService', function genericListController(GenericService) {
      this.listItems = GenericService.getAll();
    }]
  });
```
The component directive `genericList` is added to the module genericList with the template genericList.template.html and the controller genericListController. genericListController has a dependency, GenericService, which is then passed into the genericListController function.  genericList component’s `listItems` is populated by the GenericService method `getAll`. In `app.config.js`, the route 'list' uses the template `<generic-list></generic-list>`. AngularJS automatically takes the component name and changes it to hyphenated case.
```html
        <li ng-repeat="item in $ctrl.listItems">
          <a href="#!/item/{{item.id}}" >
            <h4>{{item.name}}</h4>
            <p>{{item.description}}</p>
          </a>
        </li>
```
In genericList.template.html we have a list item that gets repeated by `ng-repeat`.  `$ctrl.listItems` is populated by the controller above.  This file in the repo has some additional static html that I have not included here.
#### Angular2
```typescript
// generic-list.component.ts for Angular2
import {Component, OnInit} from '@angular/core';
import {GenericService} from '../sharedServices/generic-service.service';
@Component({
    selector: 'generic-list',
    template: `
		        <li *ngFor="let item of listItems">
		          <a href="/item/{{item.id}}" >
		            <h4>{{item.name}}</h4>
		            <p>{{item.description}}</p>
		          </a>
		        </li>
    `
})
export class GenericListComponent implements OnInit{
	private title:string = "Generic List";
	private listItems:Array<Object>;
	constructor(private genericService: GenericService) { }
	ngOnInit():void {
		this.listItems = this.genericService.getAll();
	}
}
```
`@Component()` stores some metadata for GenericListComponent.  First, `selector` is the name that will be used for the element directive, `<generic-list></generic-list>`, in AngularJS. This was done automatically by AngularJS from the component name. With the use of Typescript, we can use template strings and include the template html in the Typescript file. You can use `templateUrl` and point that to an html file. That is best practice, but for the sake of simplicity, it is included in the file.

In the Angular2 GenericListComponent component, we have a private variable, a constructor, and Angular2 function `ngOnInit`.  First, we declare a private `private listItems:Array<Object>` array.  Then the constructor sets up the service genericService. `ngOnInit` is one of Angular2’s component lifecycle listeners. It fires when the the component is initiated, and in this component it populates `listItems` from genericService’s `getAll()` method.

The template is largely the same, the big difference being the syntax for iterating.  AngularJS’s
```javascript
<li ng-repeat="item in $ctrl.listItems”>
```
 is
```typescript
<li *ngFor="let item of listItems”>
```
in Angular2.

## Conclusion
Both AngularJS and Angular2 are rich in features, but hopefully these examples demonstrate some of the differences in the frameworks and provide a good first step in understanding what is involved in an upgrade from AngularJS to Angular2.

If you have any questions about upgrading from AngularJS to Angular2, please send your comments and inquiries to [contact@ippon.tech](mailto:contact@ippon.tech).

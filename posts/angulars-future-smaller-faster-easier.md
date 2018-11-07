---
authors:
- Guillaume Granger
tags:
- Angular
date: 2017-07-20T16:29:00.000Z
title: "Angular’s Future - Smaller, faster, easier"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/11/devFest.png
---

Last Thursday, I had the chance to attend the DevFest. The annual meeting for developers in Nantes, France. Here is a brief report of the conference on Angular by Wassim Chegham, Senior Developer Advocate and the Head of the Open Source Program Office at SFEIR. Wassim presented what coming next in the Angular Ecosystem. 

# Bazel

Bazel is a build and test software developed by Google. Bazel is fast, is fully incremental, there is local and distributed caching and parallel execution. It builds only modified files. 

Bazel is scalable, it handles codebases of any size, in multiple repositories or a huge monorepo. 

Bazel is polyglot, it builds and tests Java, C++, Android, iOS, Go and a wide variety of other language platforms. 

The switch to Bazel will be transparent with the ng build command.

To go further:
* https://bazel.build/
* https://docs.google.com/document/d/1OlyiUnoTirUj4gecGxJeZBcjHcFr36RvLsvpBl2mxA8/preview

# Schematics

It’s a tool to extend Angular CLI. With Schematics, you can create custom commands, plugins, your own guidelines namespace etc. You will have autocomplete with Algolia (a search-as-a-service solution).

To go further:

* [Schematics — An Introduction](https://blog.angular.io/schematics-an-introduction-dc1dfbc2a2b2)
* [Schematics](https://material.angular.io/guide/schematics)

# Component Dev Kit

UI components to building blocks. The Component Dev Kit (CDK) provide behaviors for your components. With CDK, you can overwrite components and reuse them. 

To go further:
* [A Component Dev Kit for Angular](https://blog.angular.io/a-component-dev-kit-for-angular-9f06e3b4b3b4)
* [Component Dev Kit](https://material.angular.io/cdk/categories)

# Observables

Streaming is everywhere. The future of Angular is to make Observables as standard. This will permit to unify code across libraries, to get fewer polyfills to maintain. Also, to process DOM events in a standard way. To help make observables a Standard in the DOM: https://github.com/whatwg/dom/issues/544

# Angular Elements

Custom Element is a part of Web Components Specifications. CE allow web developers to define new types of HTML elements. So, Angular team want to use Angular component as Custom Element.

To go further:
* [Elements in v6 and Beyond - Rob Wormald](https://www.youtube.com/watch?v=Z1gLFPLVJjY&t=4s)


# Angular Ivy

Ivy is the angular’s new renderer. It reduces the amount of code generating and it is much simpler code debugging. It provides a non-breaking api, and produces smaller size apps. Expected to be enabled in v8.

To go further:
* [GitHub - Ivy Renderer (beta)](https://github.com/angular/angular/issues/21706)


# To see the conference

* [DevFest Nantes 2018 - Ready for Angular version 8](https://www.youtube.com/watch?v=zCsFUzEEfto)

---
authors:
- Kenneth Hegeland
categories:
- JHipster
date: 2017-02-07T15:43:55.000Z
title: "JHipster 4"
id: 5a267e57dd54250018d6b637
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/02/JHipster-4-Blog--4-.png
---

A new version of JHipster was recently released, and the changes take the existing JHipster generator that we all love and updates it so we can continue building cutting edge applications in a short time frame. With this release, we have a streamlined development process (if using Angular 2), faster dependency management, less generated code, and we’ve added support for Angular 2. If you know Angular 2, and are interested in continuing development with it, JHipster 4 is perfect. If you are new to Angular 2, and you're looking to learn, this is one of the best places to start. As of this writing, JHipster 4 is the only available full stack generator for Angular 2.

In order to experiment with the new technologies being used, let’s generate a new application. Besides choosing to use Angular 2, select all other default options. You’ll notice a few things immediately; in particular, project generation is fast! In the past, using NPM, we had to wait a few minutes while NPM finished installing everything. Now, let’s look at what is contained in the project directory. We will quickly notice that there is no `bower.json`, no `gulpfile.js`, and no `node_modules` directory. When we generate an application with Angular 2, none of these are needed! Instead we use Yarn ([yarn installation](https://yarnpkg.com/docs/install/)) for dependency management, and [Webpack](https://webpack.js.org/) as our build tool. We simply use `yarn start`, and our Webpack build will be started for us.

When we finish generating our application, we get some useful hints on how to proceed. For an experienced JHipster user, this is simply a friendly reminder, but for someone new, this is a nice feature to guide them on using their newly generated application. When running `yarn start`, we may get errors, and generally these errors are related to incompatible versions of Node. It is likely that the error we see will explicitly tell us the lower bound on the Node version required for Yarn. In this case, simply install a version of Node compatible with Yarn, and you’re all set. When we are working with Angular 2, there are some nice efficiencies added to our build process -- we no longer have to manage NPM, Bower, and Gulp. We can simply use Yarn and not worry about anything else. When we need to update dependencies, we use `yarn install`, otherwise we can just use `yarn start`. Unfortunately, if we are still using Angular 1, we must still manage NPM, Bower, and Gulp ourselves.

People familiar with Angular 2 may be familiar with [Angular CLI](https://cli.angular.io/). Good news! JHipster 4 adds support for Angular CLI, so the workflow you may have developed while using Angular 2 can still be used in JHipster 4. As an example, we can use `ng generate component my-component`, and we get a new directory named `my-component`, with an Angular 2 component and a template. So now we have two types of generation: JHipster entity sub generator can generate an actual entity for us, and Angular CLI can generate components for us to use as a base which we can build off of.

If you’re familiar with Typescript, you may be used to seeing constructor-based injection, which looks like this:

```language-typescript
export class TestComponentComponent{

  constructor( private Service: any ) { }

}
```

Here, the service is injected via the constructor. This is slightly different from Angular 1, where you inject the service into a function. To keep a consistent approach, we’ve updated the backend to also use constructor based injection, rather than using `@Inject` (or `@Autowired`). Our backend code now looks something like this:

```language-java
public class TestClass {
	private Service myService;

	TestClass( Service myService ) {
		this.myService = myService;
	}
}
```

And our service will be injected to the constructor!

In the past, JHipster would generate a bunch of utilities for us (on the front end and the backend). For the sake of limiting generated code and removing the need to have the same code in every generated project, we've made these libraries into project dependencies. In `package.json`, you’ll see a dependency of `ng-jhipster`, which is the UI library, [ng-jhipster](https://github.com/jhipster/ng-jhipster). Similarly, we have  [backend utilities](https://github.com/jhipster/jhipster), added via a new dependency in our `pom.xml`

```language-xml
<dependency>
    <groupId>io.github.jhipster</groupId>
    <artifactId>jhipster</artifactId>
    <version>${jhipster.server.version}</version>
</dependency>
```

These are some of the big changes. For more details on the release, check out the official release notes for [JHipster 4](https://jhipster.github.io/2017/02/02/jhipster-release-4.0.0.html). Be sure to follow [@java_hipster](https://twitter.com/java_hipster) for all the latest updates.

To get involved, visit [JHipster GitHub](https://github.com/jhipster/generator-jhipster), [JHipster Stack Overflow](https://stackoverflow.com/tags/jhipster/), or contact Ippon Technologies USA.

---
authors:
  - Matt Reed
tags:
  - Vue.js
  - Front-End
  - JavaScript
  - TypeScript
  - Test-Driven Development
date: 2021-10-19T00:00:00.000Z
title: 'The Game of Vue Migrations'
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VueLogo.png
---

## A Word on Transformation and Migration

Our lives are made up of transitions. We move from place A to place B. Our own feelings can bounce from happy to sad without much of a reason. World events seem to escalate and evolve before our very eyes. The constant is that change is inevitable.

In the following post you will read about my attempts at transforming and migrating a simple [Vue 2](https://vuejs.org/) App implementation of [The Game of Life](https://codingdojo.org/kata/GameOfLife/) that has no tests. The objective is to take [this bare-bones and badly-designed application](https://github.com/matthewreed26/game-of-life) through a metamorphic process that will include:

1. Bringing up test coverage with a focus on Test-Driven Development
1. Removing complexity through refactoring and types in TypeScript
1. A full version upgrade to the newer [Vue 3](https://v3.vuejs.org/) framework

It might be ambitious to tackle in a single post but hopefully the thought process used will make an impact.

## Handling Multiple Migratory Objectives

As is typical of most projects, there are often many ways to proceed when updating multiple aspects. In this case, there are 3 factorial ways:

1. Tests -> TypeScript -> Vue 3
1. Tests -> Vue 3 -> TypeScript
1. TypeScript -> Tests -> Vue 3
1. TypeScript -> Vue 3 -> Tests
1. Vue 3 -> TypeScript -> Tests
1. Vue 3 -> Tests -> TypeScript

Luckily, we have the help of our trusty [Vue CLI](https://cli.vuejs.org/) to get the job done for two out of the three steps. It has plugins for adding [Jest](https://cli.vuejs.org/core-plugins/unit-jest.html#vue-cli-plugin-unit-jest) and [TypeScript](https://cli.vuejs.org/core-plugins/typescript.html) which are relatively painless (though require some manual intervention). Upgrading to Vue 3 has [helpful docs that provide guidance](https://v3.vuejs.org/guide/migration/migration-build.html) but is seemingly the most complex step.

So what is the best way to proceed? This is best answered by, first, understanding the state of where this app is currently and, second, testing out the transitions via git history and branches.

### State of the Vue-nion

A quick way to get a project overview is through the [Vue CLI GUI](https://cli.vuejs.org/dev-guide/plugin-dev.html#installing-plugin-locally). This can be opened by running the command:

```bash
vue ui
```

Import the project by navigating to it in your file system. Now it is easy to observe existing plugins, dependencies, configurations, and package manager tasks of the project. Possible plugins might include the [Vue router](https://router.vuejs.org/installation.html#vue-cli) or [Vuex for state management](https://vuex.vuejs.org/) whereas dependencies could include [Axios for network calls](https://axios-http.com/) or [Vee-Validate forms](https://vee-validate.logaretm.com/v3).

It is also possible to add new plugins and dependencies graphically via the dashboard here. It is one way to add testing and TypeScript plugins, but more on this later.

![Vue Plugin UI Dashboard](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VuePluginUI.png)

Depending on the size of the project there could be a long list of plugins and dependencies to consider when exploring migration feasibility. Does a 3rd party library like Vee-Validate have native TypeScript support, or will a custom solution/alternative need to be considered? Is the Vue router integrated differently in Vue 2 versus 3? The coupling degree of app to dependencies will be a large factor in level of effort.

### Testing the Waters

The first step in our migration will be to add tests. While it does add the [Jest](https://cli.vuejs.org/core-plugins/unit-jest.html#vue-cli-plugin-unit-jest) plugin (and thus complexity during the version upgrade), this is the best first step as a good testing suites are crucial to our confidence everything works as expected. These tests will ensure functionality without having to manually check every part of the application not just right now, but as the app evolves.

Add this [Jest](https://cli.vuejs.org/core-plugins/unit-jest.html#vue-cli-plugin-unit-jest) plugin via the Vue CLI GUI or by running the following command in the root of the project:

```bash
vue add unit-jest
```

As well as modifying the `package.json`, it adds a `jest.config.js` file to the root and `example.spec.js` file under a `tests` folder. Running the new unit testing command:

```bash
npm run test:unit
```

Yields pretty minimal output:

![Fresh Unit Testing Output](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VueFreshUnitTestingOutput.png)

There is no information on the `GameOfLife.vue` or `GameOfLife.component.js` files right out of the box. It only runs the `example.spec.js` for testing `HelloWorld.vue`. Adding a `GameOfLife.spec.js` with a basic evaluation is picked up when we run the unit testing command again:

![Basic GameOfLife Test](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VueBasicGameOfLifeTest.png)

![Unit Testing Output with Basic GameOfLife Test](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VueUnitTestingOutputBasicGameOfLifeTest.png)

Despite the shown "<span style="color:green">PASS</span>", clearly it is not enough to ensure proper functionality. Introducing the concept of [Code Coverage](https://en.wikipedia.org/wiki/Code_coverage) should help direct attention towards the lines which require more testing. This can be done by modifying the `jest.config.js` from earlier. With the intent that this app will utilize TypeScript in a future step, the following configuration will tell Jest to collect coverage reports and display them in the console:

```js
module.exports = {
  preset: '@vue/cli-plugin-unit-jest',
  transform: {
    '^.+\\.vue$': 'vue-jest',
  },
  coverageDirectory: '<rootDir>/coverage',
  collectCoverageFrom: [
    'src/**/*.{js,ts,vue}',
    '!src/**/*.component.{js,ts}',
    '!src/main.{js,ts}',
    '!**/*.d.ts',
  ],
  coverageReporters: ['html', 'json', 'text', 'lcov', 'clover'],
  collectCoverage: true,
};
```

Removing the useless `example.spec.js` and `HelloWorld.vue` files and adding a basic test for `App.js`, the output is now much more helpful:

![Unit Testing Output with Code Coverage](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VueUnitTestingOutputCodeCoverage.png)

One last tip before it is time to bring up the Code Coverage. To run the tests continuously, a command can be added to the `"scripts"` section of the `package.json`:

```json
"test:unit:watch": "vue-cli-service test:unit --watch --coverage"
```

### Test-Driven Development vs. Testing as an Afterthought

When testing is the core first step of an application's development cycle, this is known as [Test-Driven Development (TDD)](https://en.wikipedia.org/wiki/Test-driven_development). This is a different mindset than when tests are written after the application code. In this case, testing is more of an afterthought than a designing methodology. The good news is once there are tests then the power of TDD can take hold.

Once enough tests have been written to bring the coverage reports up to compliant levels (generally think >80% but be sure to hit any edge cases!), the console output may look something more like this:

![Unit Testing Output with Full Code Coverage](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VueUnitTestingOutputFullCodeCoverage.png)

Thinking ahead while writing the tests to the refactoring/TypeScript step, here are a few opportunities for improvements:

1. The default `grid` value of `[[false]]` within `data()` is an incorrect placeholder.
1. There are variable name typos, inconsistent function names, and confusing function responsibilities.
1. The displayed grid could look nicer.
1. It might have been found the `checkNeighbors` method requires more intricate tests. Due to this, and as the [SonarLint extension](https://www.sonarlint.org/vscode) suggested, it is apparent that the method's complexity needs to be reduced.

![CheckNeighbors Method Complexity](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VueCheckNeighborsComplexity.png)

So what is the next step? Is it to refactor/add TypeScript or upgrade to Vue 3? Well, considering that Vue 3 is natively in TypeScript, it might be best to perform the transition to TypeScript first. That way might make an upgrade to Vue 3 even easier. It could allow for more options like following [the aforementioned guidance](https://v3.vuejs.org/guide/migration/migration-build.html) or perhaps generating a new Vue 3 project then copying over the existing Vue components.

### The New Vue Upgraded

The guidance previously shared did highlight some [limitations](https://v3.vuejs.org/guide/migration/migration-build.html#known-limitations) and [preparations](https://v3.vuejs.org/guide/migration/migration-build.html#preparations) before [installation of an intermediary build](https://v3.vuejs.org/guide/migration/migration-build.html#installation). While that is probably the preferred method for large-scale applications, perhaps for smaller ones another way could be to generate a new Vue 3 project via the Vue CLI then copy over the existing Vue components.

## Keep On Changing

This blog may not stand the test of time as individuals/companies/teams move on from Vue 2. We know Vue 2 will become outdated just as Struts has in front-end programming, and Python 2 has made way for Python 3, and various other technologies that have come before these. What makes software engineering especially hard is this fast rate of change.

Life is our ability to cope with these shifting constructs and being ready for whatever is next. And a large part of that is recognizing where the winds of change are blowing from/to. So maybe this blog reaches an audience somewhere that needs the information now. Then it has served its purpose in facilitating a transformation.

This is the "Positive Technology" mindset that Ippon tries to teach all its consultants. If you resonated with the article you just read, get in contact with us so we may help guide you or your company's transformation.

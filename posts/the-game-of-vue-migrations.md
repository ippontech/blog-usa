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

In the following post you will read about my attempts to transform a [Vue 2](https://vuejs.org/) App implementation of [The Game of Life](https://codingdojo.org/kata/GameOfLife/). The objective is to take [this simple application with no tests](https://github.com/matthewreed26/game-of-life) through a metamorphic process that will include:

1. Bringing up test coverage with a focus on Test-Driven Development
1. Improving readability via types in TypeScript and removing complexity through refactoring
1. A full version upgrade to the newer [Vue 3](https://v3.vuejs.org/) framework

It might be ambitious to tackle in a single post but I promise it will be rewarding to understand the thought process used.

## Handling Multiple Objectives

There are many ways to update a multifaceted project. Luckily, the trusty [Vue CLI](https://cli.vuejs.org/) can get the job done for two out of the three steps. It has plugins for adding [Jest](https://cli.vuejs.org/core-plugins/unit-jest.html#vue-cli-plugin-unit-jest) and [TypeScript](https://cli.vuejs.org/core-plugins/typescript.html) which are relatively painless (though require some manual intervention). Upgrading to Vue 3 has [docs that provide guidance](https://v3.vuejs.org/guide/migration/migration-build.html) but is seemingly the most complex step.

So what is the best way to proceed? This is best answered by, first, understanding the state of where this app is currently and, second, testing out the transitional steps via git history and branches.

### State of the Vue-nion

A quick way to get a project overview is through the [Vue CLI GUI](https://cli.vuejs.org/dev-guide/plugin-dev.html#installing-plugin-locally). Run the following command to open it:

```bash
vue ui
```

Import the project by file system navigation. Now it is easy to observe existing plugins, dependencies, configurations, and package manager tasks of the project. Possible plugins might include the [Vue router](https://router.vuejs.org/installation.html#vue-cli) or [Vuex for state management](https://vuex.vuejs.org/) whereas dependencies could include [Axios for network calls](https://axios-http.com/) or [Vee-Validate forms](https://vee-validate.logaretm.com/v3). New plugins and dependencies can be added graphically via the dashboard here.

![Vue Plugin UI Dashboard](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VuePluginUI.png)

Depending on project size, the list of plugins and dependencies might be extensive when exploring migration feasibility. Does a 3rd party library like Vee-Validate have native TypeScript support, or will a custom solution/alternative need to be considered? Is the Vue router integrated differently in Vue 2 versus 3? The coupling degree of app to dependencies are a large factor in level of effort.

## Testing the Waters

The first step will be to add tests. While it does add the [Jest](https://cli.vuejs.org/core-plugins/unit-jest.html#vue-cli-plugin-unit-jest) plugin (and thus complexity during the version upgrade), this is the best step to start with as testing suites are crucial to overall confidence everything works as expected. These tests will ensure functionality without manually checking every part of the application. They are important not just right now, but as the app evolves.

Add this [Jest](https://cli.vuejs.org/core-plugins/unit-jest.html#vue-cli-plugin-unit-jest) plugin via the Vue CLI GUI or by running the following command in the root of the project:

```bash
vue add unit-jest
```

As well as modifying the `package.json`, it adds a `jest.config.js` file to the root and `example.spec.js` file under a `tests` folder. Run the new unit testing command:

```bash
npm run test:unit
```

See how it yields pretty minimal output:

![Fresh Unit Testing Output](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VueFreshUnitTestingOutput.png)

There is no information on the `GameOfLife.vue` or `GameOfLife.component.js` files right out-of-the-box. It only runs the `example.spec.js` for testing `HelloWorld.vue`. Adding a `GameOfLife.spec.js` file with a basic evaluation is picked up when we run the unit testing command again:

![Basic GameOfLife Test](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VueBasicGameOfLifeTest.png)

![Unit Testing Output with Basic GameOfLife Test](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VueUnitTestingOutputBasicGameOfLifeTest.png)

Despite the shown "PASS", it is not enough to ensure proper functionality. Introducing the concept of [Code Coverage](https://en.wikipedia.org/wiki/Code_coverage) should direct attention towards the untested lines. With the intent that this app will utilize TypeScript soon, [this configuration to the `jest.config.js`](https://github.com/matthewreed26/game-of-life/blob/unit-jest/jest.config.js) will tell Jest to collect coverage reports and display them in the console.

Removing the useless `example.spec.js` and `HelloWorld.vue` files and adding a basic test for `App.js`, the output is now much more helpful:

![Unit Testing Output with Code Coverage](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VueUnitTestingOutputCodeCoverage.png)

One last tip before bringing up the code coverage. To run the tests continuously, [this command](https://github.com/matthewreed26/game-of-life/blob/unit-jest/package.json#L9) can be added to the `"scripts"` section of the `package.json`.

### Test-Driven Development vs. Testing as an Afterthought

When testing is at the core of an application's development cycle, this is known as [Test-Driven Development (TDD)](https://en.wikipedia.org/wiki/Test-driven_development). Writing tests first is a different mindset than when tests are written after the application code. Unless special discipline is given to writing tests before changing the codebase, testing will always be more an afterthought than a designing methodology. The good news is once there are written tests, then the shift in mindset can begin to occur and the power of TDD can begin to take hold.

With enough tests to bring the coverage reports up to compliant levels (generally think >80% but be sure to hit any edge cases!), the console output may look more like this:

![Unit Testing Output with Full Code Coverage](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VueUnitTestingOutputFullCodeCoverage.png)

Thinking ahead while writing the tests to the refactoring/TypeScript step, here are a few opportunities for improvement:

1. The default `grid` value of `[[false]]` within `data()` is an incorrect placeholder.
1. There are variable name typos, inconsistent function names, and confusing function responsibilities.
1. The `checkNeighbors` method requires more intricate tests. Due to this, and as the [SonarLint extension](https://www.sonarlint.org/vscode) suggested, the method's complexity needs to be reduced.

![CheckNeighbors Method Complexity](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VueCheckNeighborsComplexity.png)

So what is the next step? Is it to refactor/add TypeScript or upgrade to Vue 3? Considering that Vue 3 is natively in TypeScript, it might be best to perform the transition to TypeScript first. An upgrade to Vue 3 might be even easier that way. It could also allow for more options like following [the aforementioned guidance](https://v3.vuejs.org/guide/migration/migration-build.html) or generating a new Vue 3 project then copying over the existing Vue components.

## Reading the Room

What does everyone do at networking events when they initially enter into the space? They scan the room looking for their coworkers, groups who are dressed/acting similarly, or easy-congregation areas like water coolers. The reason? Gaining quick familiarity on the ins-and-outs of an event can allow any individual to network with people more efficiently.

An analogous behavior occurs when joining a project for the first time. To be a high-functioning contributor, a new individual will hit the parts of an application which tell its various functionalities (hence the look at the Vue GUI earlier). Are there tests that exist to show distinctions in functionality? What is the folder or component structure of [parent-to-child relationships](https://vuejs.org/v2/guide/instance.html)? How are the methods and variables named? Quick acclimation is the goal here.

Strongly-typed languages like TypeScript allow for better readability because methods and objects are clearly structured. It is all about real-time feedback while developing the working code. Perhaps even more valuable is how the types can prevent exceptions from occurring during runtime too.

### Adding TypeScript and Fixing Inconsistencies

Add [TypeScript](https://cli.vuejs.org/core-plugins/typescript.html) via the Vue CLI by running the command:

```bash
vue add typescript
```

**Important note:** The first prompt, "`Use class-style component syntax?`" should be answered "`no`". While in Vue 2 it is a popular style for writing [TypeScript components](https://vuejs.org/v2/guide/typescript.html), it is not fully supported in Vue 3 (because of [reasons](https://github.com/vuejs/rfcs/pull/17#issuecomment-494242121)). Without getting too ahead, answering "`no`" will make the [Options API](https://v3.vuejs.org/guide/typescript-support.html#using-with-options-api) and [Composition API](https://v3.vuejs.org/guide/typescript-support.html#using-with-composition-api) implementations easier later on.

Deleting the `HelloWorld.vue` and reverting back the `App.vue`, one of the first observations in `GameOfLife.vue` is how the `generateEmptyGrid` method shows a problem when adding the constructed row to the grid. This comes from the incorrect declaration within `data()` for the `grid` value of `[[false]]`.

![Incorrect Grid Declaration](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VueIncorrectGridDeclaration.png)

Instead, the initial value for `grid` should have been `[[{id:-1, alive:false}]]`. The inconsistency is resolved once that is changed. Confusion is avoided thanks to TypeScript.

After adding to all various methods' outputs and inputs like is requested for `toggleCell` above, the app runs as expected! Now to fix the `GameOfLife.spec.ts`. The `GameOfLife` import is now broken due to the [use of `Vue.extend`](https://github.com/matthewreed26/game-of-life/blob/unit-jest-typescript/src/components/game-of-life/GameOfLife.component.ts#L3). There is no easy solution other than [this quick fix](https://github.com/vuejs/vue-test-utils/issues/255) on the [`shallowMount` return type](https://github.com/matthewreed26/game-of-life/blob/unit-jest-typescript/tests/unit/components/game-of-life/GameOfLife.spec.ts#L144). But, with the addition of that, all tests pass and coverage is restored.

### Increasing Intentionality

## Say Hello to The New Vue

The guidance previously shared did highlight some [limitations](https://v3.vuejs.org/guide/migration/migration-build.html#known-limitations) and [preparations](https://v3.vuejs.org/guide/migration/migration-build.html#preparations) before [installation of an intermediary build](https://v3.vuejs.org/guide/migration/migration-build.html#installation). While that is probably the preferred method for large-scale applications, perhaps for smaller ones another way could be to generate a new Vue 3 project via the Vue CLI then copy over the existing Vue components.

## Keep On Changing

This blog may not stand the test of time as individuals/companies/teams move on from Vue 2. We know Vue 2 will become outdated just as Struts has in front-end programming, and Python 2 has made way for Python 3, and various other technologies that have come before these. What makes software engineering especially hard is this fast rate of change.

Life is our ability to cope with these shifting constructs and being ready for whatever is next. And a large part of that is recognizing where the winds of change are blowing from/to. So maybe this blog reaches an audience somewhere that needs the information now. Then it has served its purpose in facilitating a transformation.

This is the "Positive Technology" mindset that Ippon tries to teach all its consultants. If you resonated with the article you just read, get in contact with us so we may help guide you or your company's transformation.

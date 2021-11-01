---
authors:
  - Matt Reed
tags:
  - Vue.js
  - Front-End
  - JavaScript
  - TypeScript
  - Software Migration
  - Test-Driven Development
  - Software Craft
date: 2021-10-19T00:00:00.000Z
title: 'The Game of Vue Migrations'
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VueLogo.png
---

## A Word on Transformation and Migration

Our lives consist of transitions. We move from place A to place B. Our feelings can bounce from happy to sad without much of a reason. World events seem to escalate and evolve before our very eyes. The constant is that change is inevitable.

In the following post, you will read about my attempts to transform a [Vue 2](https://vuejs.org/) App implementation of [The Game of Life](https://codingdojo.org/kata/GameOfLife/). The objective is to take [this simple application with no tests](https://github.com/matthewreed26/game-of-life) through a metamorphic process that will include:

1. Bringing up unit test coverage with a focus on Test-Driven Development
1. Improving readability via types in TypeScript and removing complexity through refactoring
1. A version upgrade to the newer [Vue 3](https://v3.vuejs.org/) framework

It might be ambitious to tackle in a single post; but, I promise it will be rewarding to understand the thought process.

## Handling Multiple Objectives

There are many ways to update a multifaceted project. Luckily, the trusty [Vue CLI](https://cli.vuejs.org/) can get the job done for two out of the three steps. It has plugins for adding [Jest](https://cli.vuejs.org/core-plugins/unit-jest.html#vue-cli-plugin-unit-jest) and [TypeScript](https://cli.vuejs.org/core-plugins/typescript.html), which are relatively painless (though they require some manual intervention). Upgrading to Vue 3 has [docs that provide guidance](https://v3.vuejs.org/guide/migration/migration-build.html) but is seemingly the most complex step.

So what is the best way to proceed? First, understand the state of where this app is currently. Second, test out the transitional steps via git history and branches.

### State of the Vue-nion

A quick way to get a project overview is through the [Vue CLI GUI](https://cli.vuejs.org/dev-guide/plugin-dev.html#installing-plugin-locally). Run the following command to open it:

```bash
vue ui
```

Import the project by file system navigation. Now it is easy to observe existing plugins, dependencies, configurations, and package manager tasks of the project. Possible plugins might include the [Vue router](https://router.vuejs.org/installation.html#vue-cli) or [Vuex for state management](https://vuex.vuejs.org/), whereas dependencies could include [Axios for network calls](https://axios-http.com/) or [Vee-Validate forms](https://vee-validate.logaretm.com/v3). New plugins and dependencies can be added graphically via the dashboard here.

![Vue Plugin UI Dashboard](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VuePluginUI.png)

Depending on project size, the list of plugins and dependencies might be extensive when exploring migration feasibility. Does a 3rd party library like Vee-Validate have native TypeScript support, or will a custom solution/alternative need to be considered? Is the Vue router integrated differently in Vue 2 versus 3? The coupling degree of app-to-dependencies is a sizeable factor in the level of effort.

## Testing the Waters

The first step will be to add tests. While it does add the [Jest](https://cli.vuejs.org/core-plugins/unit-jest.html#vue-cli-plugin-unit-jest) plugin (and thus complexity during the version upgrade), this is the best step to start with as testing suites are crucial to overall confidence everything works as expected. These tests will ensure functionality without manually checking every part of the application. They are indispensable now and as the app evolves.

Add this [Jest](https://cli.vuejs.org/core-plugins/unit-jest.html#vue-cli-plugin-unit-jest) plugin via the Vue CLI GUI or by running the following command in the root directory of the project:

```bash
vue add unit-jest
```

As well as modifying the `package.json`, it adds a `jest.config.js` file to the root and `example.spec.js` file under a `/tests` folder. Run the new unit testing command:

```bash
npm run test:unit
```

See how it yields pretty minimal output:

![Fresh Unit Testing Output](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VueFreshUnitTestingOutput.png)

There is no information on the `GameOfLife.vue` or `GameOfLife.component.js` files right out of the box. It only runs the `example.spec.js` for testing `HelloWorld.vue`. Adding a `GameOfLife.spec.js` file with a fundamental evaluation is picked up when we run the unit testing command again:

![Basic GameOfLife Test](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VueBasicGameOfLifeTest.png)

![Unit Testing Output with Basic GameOfLife Test](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VueUnitTestingOutputBasicGameOfLifeTest.png)

Despite showing "PASS", it is not enough to ensure proper functionality. Introducing the concept of [Code Coverage](https://en.wikipedia.org/wiki/Code_coverage) should direct attention towards the untested lines. With the intent that this app will utilize TypeScript soon, [this configuration to the `jest.config.js`](https://github.com/matthewreed26/game-of-life/blob/unit-jest/jest.config.js) will tell Jest to collect coverage reports and display them in the console.

Removing the useless `example.spec.js` and `HelloWorld.vue` files and adding a basic test for `App.js`, the output is now much more helpful:

![Unit Testing Output with Code Coverage](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VueUnitTestingOutputCodeCoverage.png)

One last tip before bringing up the code coverage, [add this command](https://github.com/matthewreed26/game-of-life/blob/unit-jest/package.json#L9) to the `"scripts"` section of the `package.json` to run the tests continuously.

### Test-Driven Development vs. Testing as an Afterthought

When testing is at the core of an application's development cycle, this is known as [Test-Driven Development (TDD)](https://en.wikipedia.org/wiki/Test-driven_development). Writing tests first is a different mindset than writing tests after the application code. Writing tests before changing the codebase requires exceptional discipline. Unless it is well-practiced, testing will always be more an afterthought than a designing methodology. The good news is once there are written tests, then the shift in mindset can begin to occur, and the power of TDD can begin to take hold.

With enough tests to bring the coverage reports up to compliant levels (generally think >80% but be sure to hit any edge cases!), the console output may look more like this:

![Unit Testing Output with Full Code Coverage](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VueUnitTestingOutputFullCodeCoverage.png)

Thinking ahead while writing the tests to the refactoring/TypeScript step, here are a few opportunities for improvement:

1. The default "`grid`" value of "`[[false]]`" within "`data()`" is an incorrect placeholder.
1. There are variable name typos, inconsistent function names, and [confusing function responsibilities](https://en.wikipedia.org/wiki/Single-responsibility_principle).
1. The "`checkNeighbors`" method requires more intricate tests. Due to this, and as the [SonarLint extension](https://www.sonarlint.org/vscode) suggests, the method's complexity needs reducing.

![CheckNeighbors Method Complexity](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VueCheckNeighborsComplexity.png)

So what is the next step? Is it to refactor/add TypeScript or upgrade to Vue 3? Considering that Vue 3 is natively in TypeScript, performing the transition to TypeScript first is probably best. That way, an upgrade to Vue 3 would be even more straightforward. It could allow for more than just following [the above guidance](https://v3.vuejs.org/guide/migration/migration-build.html), like generating a new Vue 3 project then copying over the existing Vue components.

## Reading the Room

What does everyone do at networking events when they initially enter into the space? They scan the room for their coworkers, groups dressing/acting similarly, or easy-congregation areas like water coolers. The reason? Gaining quick familiarity with the event's ins and outs allows anyone to network with people more efficiently.

The same behavior occurs when joining a project for the first time. To be a high-functioning contributor, a new individual will hit the parts of an application that tell its various functionalities (hence the look at the Vue GUI earlier). Are there tests that exist to show distinctions in functionality? What is the component or folder structure of [parent-to-child relationships](https://vuejs.org/v2/guide/instance.html)? How are the methods and variables named? Quick acclimation is the goal here.

Strongly-typed languages like TypeScript allow for better readability because methods and objects are well-structured. It is all about real-time feedback while developing the working code. Perhaps even more valuable is how the types can prevent exceptions from occurring during runtime too.

### Adding TypeScript and Fixing Inconsistencies

Add [TypeScript](https://cli.vuejs.org/core-plugins/typescript.html) via the Vue CLI by running the command:

```bash
vue add typescript
```

**Important note:** The first prompt, "`Use class-style component syntax?`" should be answered "`no`". While it is a popular style for writing [TypeScript components](https://vuejs.org/v2/guide/typescript.html) in Vue 2, it is not fully supported in Vue 3 (because of [reasons](https://github.com/vuejs/rfcs/pull/17#issuecomment-494242121)). Without getting too ahead, answering "`no`" will make the [Options API](https://v3.vuejs.org/guide/typescript-support.html#using-with-options-api) and [Composition API](https://v3.vuejs.org/guide/typescript-support.html#using-with-composition-api) implementations easier later on.

Deleting `HelloWorld.vue` and reverting `App.vue`, one of the first observations in `GameOfLife.vue` is how the "`generateEmptyGrid`" method shows a problem when adding the constructed row to the grid. This comes from the incorrect declaration within "`data()`" for the "`grid`" value of "`[[false]]`".

![Incorrect Grid Declaration](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/10/VueIncorrectGridDeclaration.png)

Instead, the initial value for "`grid`" should have been "`[[{id:-1, alive:false}]]`". Changing it resolves the problematic inconsistency. There is no more confusion, thanks to TypeScript.

As is seen requested for "`toggleCell`" above, adding method outputs and inputs is required in TypeScript. Once done, the app runs as expected! But the tests do not run. Checking `GameOfLife.spec.ts`, it seems the [use of `Vue.extend`](https://github.com/matthewreed26/game-of-life/blob/unit-jest-typescript/src/components/game-of-life/GameOfLife.component.ts#L3) has broken the `GameOfLife` import. Unfortunately, in Vue 2, there is no easy solution other than [this quick fix](https://github.com/vuejs/vue-test-utils/issues/255) on the [`shallowMount` Wrapper type](https://github.com/matthewreed26/game-of-life/blob/unit-jest-typescript/tests/unit/components/game-of-life/GameOfLife.spec.ts#L145). The addition of that means a new tradeoff. For Visual Studio Code users, the IDE's [IntelliSense](https://code.visualstudio.com/docs/editor/intellisense) now cannot help write new tests. In turn, though, it restores functionality to the existing test suite (modifying an "`expect`" will confirm). The good news is an upgrade to Vue 3 will correct this IDE type-checking problem.

### Increasing Intentionality and Separating Concerns

Addressing the other two opportunities for improvement is possible with a passing test suite. Adhering to TDD by modifying the tests before the working code, change any variables or methods that could be more aptly named. All tests must pass again before continuing.

Breaking down existing, complex methods in a codebase is an example of [code refactoring](https://en.wikipedia.org/wiki/Code_refactoring) and necessary when their [responsibilities are ambiguous](https://en.wikipedia.org/wiki/Single-responsibility_principle). Look at "`checkNeighbors`", for example. Are diagonal neighbors considered? Does it handle grid boundary cells? How do existing neighbors determine the next generation's grid of cell lives? Despite renaming "`checkNeighbors`" to a more descriptive name, this method's functionality remains vague. More work is necessary for further clarity.

Though the Vue template only calls "`generateNextGenerationGrid`", overall scoping smaller methods in and outside a component is better for a project's longevity. Of course, utilizing TypeScript's [`enum`](https://www.typescriptlang.org/docs/handbook/enums.html) and [`type`/`interface`](https://www.typescriptlang.org/docs/handbook/2/everyday-types.html#differences-between-type-aliases-and-interfaces) features can help too. Consolidating [similar lines of code](https://github.com/matthewreed26/game-of-life/blob/unit-jest-typescript/src/components/game-of-life/GameOfLife.component.ts#L53) is a worthwhile place to start. Extracting these lines into anonymous functions and externalizing them to [separate files](https://github.com/matthewreed26/game-of-life/blob/unit-jest-typescript-intentionality/src/components/game-of-life/NeighborCell.ts) allows [direct testing without going through the Vue Testing Utils](https://github.com/matthewreed26/game-of-life/blob/unit-jest-typescript-intentionality/tests/unit/components/game-of-life/NeighborCell.spec.ts). Not to mention abstracting out code [decouples](<https://en.wikipedia.org/wiki/Coupling_(computer_programming)>) the dependency on the "`this`" context of the Vue component.

## Say Hello to the New Vue

The guidance for upgrading 2 to 3 previously shared highlights some [limitations](https://v3.vuejs.org/guide/migration/migration-build.html#known-limitations) and [preparations](https://v3.vuejs.org/guide/migration/migration-build.html#preparations). It is worth keeping in mind while performing the upgrade. And for large-scale/production-level applications, it makes a lot of sense to do it that way. It reduces the risk of change by having an [intermediary build](https://v3.vuejs.org/guide/migration/migration-build.html#installation).

Perhaps for smaller ones like this one, though, there is another way. The Vue CLI is capable of generating a new Vue 3 project. If it is possible to copy the existing Vue components directly over to this newly-generated one, upgrading will be effortless. If the files do not copy for whatever reason, the intermediary build is a fallback plan.

In a different directory, create the Vue 3 app via the Vue CLI using the same plugins as before: Unit-Testing (Jest) and TypeScript. In the original project, switch to a new branch. Delete the `/src` and `/tests` folders that came pre-loaded in the Vue 3 app and copy the Vue 2 corresponding directories over. Delete all files (except the .git and .gitignore) in the original project. Next, move the Vue 3 files over to the original project. Use the new branch to git compare changes between versions 2 and 3.

The most extensive differences are in the [`package.json`](https://github.com/matthewreed26/game-of-life/blob/unit-jest-typescript-intentionality-upgrade/package.json) and [`main.ts`](https://github.com/matthewreed26/game-of-life/blob/unit-jest-typescript-intentionality-upgrade/src/main.ts). Manually replace [`Vue.extend`](https://github.com/matthewreed26/game-of-life/blob/unit-jest-typescript-intentionality/src/components/game-of-life/GameOfLife.component.ts#L17) with [`defineComponent`](https://github.com/matthewreed26/game-of-life/blob/unit-jest-typescript-intentionality-upgrade/src/components/game-of-life/GameOfLife.component.ts#L17) in the component files. This replacement restores IntelliSense completion in the tests after modifying the [`shims-vue.d.ts`](https://github.com/matthewreed26/game-of-life/blob/unit-jest-typescript-intentionality-upgrade/src/shims-vue.d.ts) as it then no longer requires [this aforementioned quick fix](https://github.com/vuejs/vue-test-utils/issues/255). With that, the app upgrade to Vue 3 is complete!

## Keep On Changing

In this blog, we explored a transformational journey of the digital kind. Starting with a primitive Vue 2 app lacking any test suite or proper design principles, we converted it to a Vue 3 application written in TypeScript that can further employ Test-Driven Development. Change is hard to wrangle with, but it is happening all around us in the software industry. What makes software engineering especially difficult is keeping up with this fast pace of change.

We know Vue 2 will become outdated just as Struts has in front-end programming, Python 2 has made way for Python 3, and various other technologies that have come before now. This blog may not stand the test of time as individuals/companies/teams move on from Vue 2; but, maybe this blog reaches an audience somewhere that needs the information now to take more steps forward.

Our openness to cope with these shifting constructs and be ready for whatever is next is the key to innovation. So much of that mentality is recognizing the winds of change and having the courage to unfurl the sails. That is the ["Positive Technology" mindset that Ippon](https://us.ippon.tech/) tries to teach all its consultants. If this article helped facilitate your project's migration, [get in contact with us](https://us.ippon.tech/contact/). We want to hear about your success story! Or, if you resonated with the potential activity you just read about, [let's discuss](https://us.ippon.tech/contact/) how we may further guide you and your company's transformation.

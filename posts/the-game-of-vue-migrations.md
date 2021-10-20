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
image:
---

## A Word on Transformation and Migration

Our lives are made up of transitions. We move from place A to place B. Our own feelings can bounce from happy to sad without much of a reason. World events seem to escalate and evolve before our very eyes. The constant is that change is inevitable.

In the following post you will read about my attempts at transforming and migrating a simple [Vue 2](https://vuejs.org/) App implementation of [The Game of Life](https://codingdojo.org/kata/GameOfLife/) that has no tests. The objective is to take this bare-bones and badly-designed application through a metamorphic process that will include:

1. Bringing up test coverage with a focus on Test-Driven Development
1. Removing complexity through refactoring and types in TypeScript
1. A full version upgrade to the newer [Vue 3](https://v3.vuejs.org/) framework

## Handling Multiple Migratory Objectives

As is typical of most projects, there are often many ways to proceed when updating multiple aspects. In this case, there are 3 factorial ways:

1. Tests -> TypeScript -> Vue 3
1. Tests -> Vue 3 -> TypeScript
1. TypeScript -> Tests -> Vue 3
1. TypeScript -> Vue 3 -> Tests
1. Vue 3 -> TypeScript -> Tests
1. Vue 3 -> Tests -> TypeScript

Luckily, we have the help of our trusty [Vue CLI](https://cli.vuejs.org/) to get the job done for two out of the three steps. It has plugins for adding [Jest](https://cli.vuejs.org/core-plugins/unit-jest.html#vue-cli-plugin-unit-jest) and [TypeScript](https://cli.vuejs.org/core-plugins/typescript.html) which are relatively painless (though require some manual intervention). Upgrading to Vue 3 has [helpful docs that provide guidance](https://v3.vuejs.org/guide/migration/migration-build.html) but is certainly the most complex step.

So what is the best way to proceed? This is best answered by first understanding the state where this app is currently.

## Keep On Changing

This blog may not stand the test of time as individuals/companies/teams perform their inherent migratory efforts. We know Vue 2 will become outdated just as Struts has in front-end programming, and Python 2 has made way for Python 3, and various other technologies that have come before these. What makes software engineering especially hard is the rate of change.

Life is our ability to cope with these shifting constructs and being ready for whatever is next. And a large part of that is recognizing where the winds of change are blowing from/to. So maybe this blog reaches an audience somewhere that needs this information now. Then it has served its purpose in facilitating a transformation.

This is the positive mindset that Ippon tries to teach all its consultants. This is what we call the "Ippon Way". Be sure to get in contact with us if we may help guide you or your company's transformation.

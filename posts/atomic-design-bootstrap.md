---
authors:
- Anthony Rey
tags:
- Atomic Design
- Front-End
- Bootstrap
date: 2020-01-16T15:09:00.000Z
title: "The cohabitation between Atomic Design and Bootstrap"
image: https://raw.githubusercontent.com/gnuk/blog-usa/atomic-bootstrap/images/2020/01/atomic-design-bootstrap.png
---

My previous [blog post](https://blog.ippon.tech/atomic-design-in-practice/) was about Atomic Design in practice. I used this organisational method for several years on many teams and different projects.

At the same time, colleagues are promoting [Bootstrap](https://getbootstrap.com) (or equivalent) usage to make the graphical interfaces integration easier on the web. In addition, a large number of tools and frameworks integrate it natively.

However, it is difficult to use Bootstrap in an Atomic Design approach. We quickly lose the consistency of our application in favor of Bootstrap's graphical identity.

I want to share with you my experience with _Bootstrap_ so that you know how to make it coexist with your own styles rather than fighting against it.

# But, what is Bootstrap ?

> What is Bootstrap ?

This question may seem trivial at first, but it is not!

From the official website, it is a framework for building [responsive](https://alistapart.com/article/responsive-web-design), [mobile-first](https://www.lukew.com/ff/entry.asp?933) web applications. According to them, it's the world's most popular front-end component library on the web and it's based on:

* HTML structure
* CSS style file (also Sass sources)
* JavaScript for component behaviour (display a modal, alert messages, …) with libraries like jQuery.

To help us, _Bootstrap_ gives us a grid system to organise components using columns that will be adapted to the screen size (we will talk about it later in the blog post) and a lot of components are ready to use.

In summary, on one hand, _Bootstrap_ proposes a Pattern Library. This is an organised representation of reusable components using code snippets, design guidelines and use cases. On the other hand, it is a set of mechanic components (Alerts, Toggle, Carousel, Collapse, Modal …) are made with [JavaScript, jQuery and Popper](https://getbootstrap.com/docs/4.4/getting-started/introduction/#js)

![Descriptive schema about a Pattern Library](https://raw.githubusercontent.com/gnuk/blog-usa/atomic-bootstrap/images/2020/01/atomic-design-bootstrap-pattern-library.png)
_Pattern Library_

The rest of this blog post will focus on the `Pattern Library` aspect.

# Use the strength of Bootstrap

As a reminder, _Atomic Design_ allows you to create and assemble graphical components by granularity.

The _atoms_ combine to form _molecules_, which themselves assemble into _organisms_ to form templates which constitute the models of our pages.

The global idea is to define a graphic design. _Bootstrap_ being an existing Pattern Library, there may be a risk of conflict between the _Bootstrap_ choices and ours.

Nevertheless, using this type of framework brings advantages (reusable components, graphic coherence, numerous contributors ...); it may be difficult to remove it.

That's why I'm going to show you several examples to know how to make _Atomic Design_ and _Bootstrap_ coexist.

# Try to not use Bootstrap components

First of all, I recommend that you avoid the use of _Bootstrap_ [Components](https://getbootstrap.com/docs/4.4/components). Why? Because they are graphic elements with a strong impact on what the application is doing.

[Buttons](https://getbootstrap.com/docs/4.4/components/buttons) are an example. They have some variants depending on their states (hover, focus, disabled…) and it gets more complicated when assembling them with other buttons.

![Bootstrap button with states: normal, hover, focus, disabled](https://raw.githubusercontent.com/gnuk/blog-usa/atomic-bootstrap/images/2020/01/atomic-design-bootstrap-buttons.png)
_Button states from the Bootstrap website_

That's why, if you choose to use _Bootstrap_, you will impact the brand image but also the mechanics of your application. Therefore, it is better to make your own buttons with your own semantics.

> Yes, but they are already ready, why not reuse them? Especially since many people maintain them?

This is the argument I hear systematically when we talk about this kind of tool: "it exists", "why reinvent the wheel?". I will try to answer these remarks beginning with two quotes from __Alla Kholmatova__ book, [Design Systems](http://designsystemsbook.com):

> __Functional Patterns__ are tangible building blocks of the interface. Their purpose is to enable or encourage certain user behaviors.

_page 63_

> Example of __perceptual patterns__ […] include tone of voice, typography, color palette, layouts, illustrations and iconography styles, shapes and textures, spacing, imagery, interactions or animations, and all the specific ways in which those elements are combined and used in an interface.

_page 82_

Thus, for our buttons, it is necessary to reflect before choosing a ready to use component:

* Why do you need a blue button with rounded corners? all states? all sizes? 9 semantic variants proposed?
* Do you need glued buttons when grouped?
* How many times do you need to change colors? add new variants? remove rounded corners? shadows?
* What will happen if you need to upgrade Bootstrap? Even if it is a minor release?

You can follow these types of remarks about other [Components](https://getbootstrap.com/docs/4.4/components/buttons) to understand it is not relevant to use them when you need a graphical identity ; because you may take a lot of time to override and maintain them.

Even if I think it's not the Atomic Design principles, some use cases don't need a lot of energy in an important graphical identity. Sometimes the Bootstrap behaviour may be adapted. In this case, it is interesting to __use__ `Components` or [edit parameters](https://getbootstrap.com/docs/4.4/getting-started/theming/#variable-defaults) without overriding them.

# Ok, but, what should I use if I want to use Atomic Design?

Like I said, you must avoid _Bootstrap_ components usage because they may come into conflict with __Functional Patterns__ and __Perceptual Patterns__  in your design.

However, _Bootstrap_ has some useful elements with less impact on __Functional Patterns__ and  __Perceptual Patterns__.

Let's start with some examples.

## Grid system

![A Bootstrap grid system disposition](https://raw.githubusercontent.com/gnuk/blog-usa/atomic-bootstrap/images/2020/01/atomic-design-bootstrap-grid.png)
_Bootstrap grid system example from the website_

One of these elements is the [grid system](https://getbootstrap.com/docs/4.4/layout/grid), because it allows a grid simulation with 12 columns and manages some responsive design issues. Even if gutters (space between columns: 30px by default) may have a lot of effects on the building of a grid, they can be removed using the `no-gutters` modifier.

Related to `Components`, the grid has less impact on a perceptive and functional value. It is used to structure elements inside columns and can be used on the `organism` part to manage `molecules` and `atoms`.

## Utilities

Some elements are used as utilities, they usually have a lower graphical value than `Components` and are used by composition..

As an example, we will use [text](https://getbootstrap.com/docs/4.4/utilities/text).

This utility lets you do generic actions like:

* alignment
* prevent line return and truncate
* transform to lowercase, uppercase …
* change weight and use italic
* change font style to monospace

Some rules are more generic than others: where alignment is common and will have a small effect on style, the specific font choice for something like monospace may have a greater impact:

```css
.text-monospace {
    font-family: SFMono-Regular,Menlo,Monaco,Consolas,"Liberation Mono","Courier New",monospace;
}
```

Perhaps, you are thinking that it is not an important effect, but, depending on the utilities, this type of effect may vary, this is the point of [colors](https://getbootstrap.com/docs/4.4/utilities/colors) which has its own semantic and refers to _Bootstrap_ branding.

You must be pragmatic to choose utilities.

# Go further with Sass

Extending and customising _Bootstrap_ can be done using  [Sass (SCSS)](https://github.com/twbs/bootstrap) [sources](https://sass-lang.com). It is possible to change container size depending on window size, colors, and there are many other exposed variables using `!default` Sass principle. Moreover, file fragmentation lets you use parts of _Bootstrap_ like using only the grid system for example by importing the Sass file you want to use.

In this case, you can use them for:

* `Components` : Changing colors for perception
* `grid system` : Changing columns number to fit what you need

More information on what is available to extend _Bootstrap_ are on the [Theming Bootstrap](https://getbootstrap.com/docs/4.4/getting-started/theming) page.

I recommend not overriding too many properties of _Bootstrap_ because your coupling can impact your maintainability and version upgrades.

# Conclusion

There are a lot of ways to use _Bootstrap_ in an _Atomic Design_ project. With the experience and depending on the context, we learn how not to override and not to use built-in __Bootstrap__ components without thinking about how they will be used and how they are perceived. Sometimes it is not necessary to add to the framework because it may not add value.

Personally, I prefer developing new components myself to add the best graphical value I can to the application.

In other words, being pragmatic helps structure components into atoms, molecules and organisms based on granularity and the way to assemble them. My usage of _Bootstrap_ today, when I am in a project with this kind of framework, is only with some utilities like `d-none` or alignments like `text-right`. I also use the `grid-system` but as a source of inspiration because most of the time the graphical identity doesn't match with what _Bootstrap_ offers.

When we must use this (as an example: in an existing application), this blog post may help you to understand when you can use the framework and when you should create your own styles.

If, like me, you make graphical components, I encourage you to make your own components without any framework before trying to coexist with an existing solution.

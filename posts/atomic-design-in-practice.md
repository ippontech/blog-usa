---
authors:
- Anthony Rey
tags:
- Atomic Design
- Front-End
date: 2019-11-11T14:04:00.000Z
title: "Atomic Design in practice"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/10/atomic-design.png
---

For several years now, a component approach has been adopted by most JavaScript frameworks and libraries, whether it's [React](https://reactjs.org/docs/components-and-props.html), [Vue](https://vuejs.org/v2/guide/components-registration.html) or [Angular](https://angular.io/guide/component-interaction). Traditional static web pages are becoming increasingly rare and are now real dynamic applications.

To make a dynamic application, we need to:

* Structure graphical components
* Create (or not) a graphical identity

Creating a graphical identity using adapted components for specific use is difficult using an existing library like Bootstrap.

Rather than trying to use these libraries or frameworks, I'll try, with this blog post, to introduce you to Atomic Design principles with a simple example.

In a second blog post, I'll explain how to make Atomic Design and Bootstrap coexist.

# Russian dolls

Atomic Design provides good practices to structure and nest reusable components. The idea is to be able to maintain and evolve a graphical style.

This method requires graphical interface design and implementation skills. It must not be a constraint, you have to be pragmatic.

This type of assembly can be thought of like Russian dolls:

![A set of floral themed matryoshkas](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/10/atomic-matryoshka.jpg)
_[A set of floral-themed matryoshkas](https://commons.wikimedia.org/wiki/File:Floral_matryoshka_set_1.JPG) from BrokenSphere_

# Origin of the method

Atomic Design came from a [blog post by Brad Frost](http://bradfrost.com/blog/post/atomic-web-design/) published in 2013 followed by a [book](http://bradfrost.com/blog/post/atomic-design-book/) published in 2015.

Inspired by chemistry principles, Brad Frost explains that atoms combine to make molecules that join together to become more complex organisms.

After that, he describes the following:

1. Atoms
2. Molecules
3. Organisms
4. Templates
5. Pages

In order to understand these file levels, let's start with an example of a header composed by a logo, a list of links, and a search form. Now, let's look together how to break them into atoms, molecules and organisms.

If you want to have the full example, I encourage you to go to my Codepen: https://codepen.io/gnuk/pen/odeMpy

Note: In the Codepen, I use the [Pug](https://pugjs.org/api/getting-started.html) syntax. The syntax uses CSS selectors (classes start with `.`, id with `#` …) to generate HTML. In addition, the language is based on indentations, which prevent you from forgetting a closing tag, here is an example:

```pug
.my-division
    span.my-span Text
```

Will become:

```html
<div class="my-division">
    <span class="my-span">Text</span>
</div>
```

# Atoms

They are the most basic part of our design, in our case, we can consider the following elements:

* Label
* Field
* Search button
* Menu entry
* Logo

![Atoms: label, field, search button, menu entry, logo](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/10/atomic-atoms-en.png)

## Label by example

For the label, see below:

* HTML structure to describe tags to write for use
* CSS style that gives its graphical value

### HTML (Pug)

```pug
.atom-label Search the site
```

### CSS (SCSS)

```scss
.atom-label {
  @extend %atom-font;
  color: $atomColorLabelText;
}
```

# Molecules

Like chemistry: molecules are made of atoms.

In the example, we have:

* Search form
* Main navigation

![Molecules: search form, main navigation](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/10/atomic-molecules.png)

## Form by example

The **search form** molecule positions a **label** atom above a **field** atom followed by a **search button** atom.

### HTML (Pug)

```pug
.molecule-form-search
    .molecule-form-search-label
        +atom-label
    .molecule-form-search-fields
        .molecule-form-search-input
            +atom-input
        .molecule-form-search-search
            +atom-search
```

### CSS (SCSS)

```scss
.molecule-form-search {
  .molecule-form-search-fields {
    display: flex;
    flex-wrap: nowrap;
    .molecule-form-search-input {
      margin-right: 8px;
      flex-grow: 1;
      input.atom-input {
        width: 100%;
      }
    }
  }
}
```

# Organisms

Organisms arrange molecules and atoms together, and in our case, it's the **header**.

> There are no absolute rules to categorize atoms, molecules and organisms, so, for me, I put complex assemblies or molecule repetition inside this category.

> In fact, some components have more than three hierarchy assembly levels like you can have in the Russian dolls analogy. So, I think it's relevant to communicate between team designers and developers to know what is the best category.

![Organism: header](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/10/atomic-organisms.png)

## Header by example

The **header** organism represents the **logo** atom with the **main navigation** molecule and the **search form** molecule assemblies.

> Notice that molecules are aligned to the bottom and the main navigation is extended. These properties should be described in our organism.

### HTML (Pug)

```pug
.organism-header
    .organism-header-logo
        +atom-logo
    .organism-header-navigation
        +molecule-navigation
    .organism-header-form-search
        +molecule-form-search
```

### CSS (SCSS)

```scss
.organism-header {
  display: flex;
  > [class^="organism-header"] {
    margin-right: 40px;
    align-self: flex-end;
    &:last-child {
      margin-right: 0;
    }
  }
  .organism-header-navigation {
    flex-grow: 1;
  }
  .organism-header-form-search {
    width: 250px;
  }
}
```

# Templates and pages

![The template on the left side and the page, the instance, on the right side](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/10/atomic-templates-pages.png)

Templates represent the page structure. This step is just a canvas for our organisms. The template will be the model of our page, so the page is an instance of a template.

![Template and page example](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/10/atomic-templates-pages-example.png)
_Template and page inspired from the Brad Frost [article](http://bradfrost.com/blog/post/atomic-web-design)_

# Organisation

From previous examples, notice the decoupling of structures (HTML) and styles (CSS). These parts can be organised in many separate files using template languages like [Pug](https://pugjs.org) and [Sass](https://sass-lang.com/).

For an organisational structure, I have the same example stored in several files on GitLab: https://gitlab.ippon.fr/arey/atomicdesign

# As a conclusion and to go further

As we have seen, making Atomic Design components require breaking up elements. These elements have their responsibilities (label atom, form molecule …) and they can be modified independently. Moreover, their assembly is done in a structured way (atoms, molecules, organisms): they are consistent and maintainable.

Decoupling can go even further by trying to use your own components instead of a specific framework.

On Angular and Vue, I encourage you to make your style outside of your components using Sass in an external library, because, if you have a strong coupling, you may meet the following problems:

* Global tokens (colors, font size, font family …) are less accessible and more complex to maintain.
* Component assembly (molecules, organisms) is less consistent and more difficult to manage.
* Global style vision is more complex because of components containing a lot of responsibilities: HTML template, TypeScript or JavaScript logic and CSS style.

Sass usage to make a single file with several parts from atoms, molecules and organisms have the following advantages:

* Management of token [variables](https://sass-lang.com/guide#topic-2)
* Molecules and organisms management and definition using [imports](https://sass-lang.com/guide#topic-5)
* Unique style distribution from a single CSS file that can be imported in your HTML using `<link>` tag

> This separation is not mandatory but I strongly encourage you to do it because in my experience, I had a lot of similar Angular or Vue components with domain responsibilities but a common appearance.

So, I advise you to make your own graphical Pattern Library using languages like Pug or Sass in order to describe implementations and usages of your atoms, molecules, organisms, templates and pages examples. You can use tools like [Tikui](https://github.com/tikui/tikui) to achieve this in order to describe your components, the title, the descriptions, the rendering and the code template to implement it.

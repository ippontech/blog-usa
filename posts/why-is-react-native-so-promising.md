---
authors:
- Valentin Carmignac
categories:
- JavaScript
- React
- React Native
- Mobile App
- Front-End
date: 2018-02-27T20:16:13.000Z
title: "Why is React Native so promising?"
id: 5a8a04dbf7f5580022184b14
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/02/excited.png
---

This year with Ippon in Melbourne, Australia we've had the chance to build an iPad application. Being the designated front-end developer in the agency, I got to choose the stack for this project. I'm not very big on native technologies but I do enjoy a fair bit of React; that's why I was excited to try out [React Native](http://facebook.github.io/react-native/).

Now this article is not an introduction to React. This is merely a gathering of the thoughts I had and discoveries I made during this project. React Native is very good and hopefully this article will bring more people to use it and help bring the framework to maturation.

# React is **not** a framework. React Native is one

I find that in the frontend world, lots of people compare Angular and [React](https://reactjs.org/) as if they were equals. But there's an important distinction, React is only a rendering library. Whereas Angular bundles a lot of features together, you will need to use a lot of different libraries to have equivalent functionality with React. Redux being the most popular state management library to use with React you'll ship them together, making the stack a kind of framework. I think it is important to know the difference and understand that you cannot really compare them.

Enter React Native. React Native looks a lot like React: roughly the same syntax, classes, JSX, state, props... but React Native is a framework, which means you have more features than just rendering in React Native. And that's simply because React Native needs to interact with the mobile it's running on. To do that, the Facebook team created a bunch of APIs to interact with the camera, the keyboard, the vibrations...

# It spits out native code, but runs in a single thread

One of the big advantages of using React Native is that, while the core app runs in a JavaScript Engine, it uses JSX to render native code. It's not an application that displays HTML5 or a responsive web application or a hybrid app.

And that means multiple things:

* The look and feel of the UI is great. No pixels, every corner and border is smooth, Animations are flowing. You actually get 60fps. It's just great.
* It'll perform as well as a native application (at least on iOS). If you want more in-depth information about it, you should read the article ["Comparing the Performance between Native iOS (Swift) and React-Native"](https://medium.com/the-react-native-log/comparing-the-performance-between-native-ios-swift-and-react-native-7b5490d363e2) by John A. Calderaio

There is however one thing to consider when developing a React Native application. Because Javascript is a single threaded environment, you might want to limit your business code (as you'd do for any front-end app, [SoC](https://en.wikipedia.org/wiki/Separation_of_concerns ) for the win) or you'll have a "freeze" effect on your screen. One solution to this is to use one of ES6's best features, Promises. But even then, this is not exactly another thread being executed. Alexander Zlatkov explains it very well in his ["How JavaScript works"](https://blog.sessionstack.com/how-javascript-works-event-loop-and-the-rise-of-async-programming-5-ways-to-better-coding-with-2f077c4438b5) article.

# React Native is a lot like React

As mentioned previously, in terms of "developer look and feel" React Native and React are very similar. You use classes, ES6, states and the like, with a few notable differences:

* The atomic JSX elements won't be the same: No HTML tags here, you'll have to use placeholders. `<View>` is the new `<div>`, `<Text>` is the new `<p>`, `<Image>` is the new `<img>`... and then you have other specific components to create buttons, lists, etc.
* Because it's a native application you have to manage the navigation. Basic links won't do. The [documentation](https://facebook.github.io/react-native/docs/navigation.html) explains it very well.

## Redux works with React Native

Not much to say here except that Redux, the React developers' favourite state management library, also works with React Native with absolutely no difference in the way you'll dev.

## You can do stateless components and use HOC in React Native

Stateless components and High Order Components (HOC) are, for me, the revelation of the year. It allows a greater flexibility in your component creation and is way lighter than the use of React Component class inheritance. Coming from a backend world, I started React by using classes because I'm used to OOP (Java, guys, Java) and I didn't know much about functional programming. It took a senior front-end dev to open my eyes on this wonder.

But  sometimes new languages yield new philosophies. And in this new era of webcomponents, HOCs are the next best thing. I'll try to explain the core principles while staying as concise as possible.

### HOC in a few words

A Higher Order Component is essentially composed of one React component and one or many Higher Order Functions. A Higher Order Function is a function that accepts a function as a parameter and returns a React Component. In practice it will act as a component wrapper, you'll be able to add unrelated props or new mechanics; enriching the React component in any way you want. It is particularly interesting to hide logic, complexity or a concern that the Component shouldn't care about.

The most known Higher Order Function is the Redux `connect` function that allows you to bind the props of a "dumb" Component to the Redux Store. Here's a quick reminder of the function's profile:

```javascript
connect(mapStateToProps, mapDispatchToProps)(Component)
```

I've deliberately left out the two other arguments of connect because they're not used as often as mapStateToProps and mapDispatchToProps.

* `mapStateToProps` is a function that returns the mapping between the Component props and the Redux's store.
* `mapDispatchToProps` is a function that returns the mapping between the Component props and the Actions.

This is quite a special case because here connect is a function that will return the actual Higher Order Function. Let's break it down:

```javascript
// Creating the Higher Order Function with mapStateToProps and mapDispatchToProps
const hof = connect(mapStateToProps, mapDispatchToProps);
// Creating the HOC with our Higher Order Function.
const hoc = hof(MyComponent);
```

We now have a whole new component with the rendering capability of MyComponent while being linked to the Redux Component.

On top of that, the HOC's philosophy is to use stateless components instead of the standard React Component class. A stateless component is a function that only returns JSX and takes some props as arguments. ES6 classes being only syntactic sugar for a language that doesn't have any object-oriented inheritance model, using them in React or React Native can lead to a heavier DOM generation (a div for the class that will wrap the render content).

And, guess what, the best way to manage state in a stateless world is to use a Higher Order Component! Using the excellent [recompose](https://github.com/acdlite/recompose) library that provides a lot of React specific Higher Order Components, you can easily create reusable snippets for a lot of use cases.

Here's an example of a small Menu component:
<script src="https://gist.github.com/carvallegro/6167b34b0cdf9b6b19cb4358320d5856.js"></script>

### Further reading

This article is not about HOCs; just know they allow a greater flexibility around the way you compose your components and you can use them in React Native. This was only a short introduction of what HOCs are. If you want more information about it, here are just a few good article about it:

* [ReactJs documentation about HOC](https://reactjs.org/docs/higher-order-components.html)
* ["React Higher Order Components in depth"](https://medium.com/@franleplant/react-higher-order-components-in-depth-cf9032ee6c3e) by franleplant
* ["Stateless components in React Native"](https://medium.com/front-end-hacking/stateless-components-in-react-native-e9034f2e3701) by Dale Jefferson
* ["Tackling HOC in React Native"](https://medium.com/@bosung90/use-higher-order-component-in-react-native-df44e634e860) by Eric Kim

# React Native is a whole new execution environment.

For a few years we got used to having two main JS environments : NodeJS and the browser. The case of React Native is a little peculiar. On both devices and simulators, React Native runs the JS in the [JavaScriptCore](http://trac.webkit.org/wiki/JavaScriptCore) engine. Although it won’t make a difference for most of the code you'll write, you can exclude NodeJS functions and need to be really careful with which dependency (from npm or equivalent) you're using. You might find some differences in the execution if you're using the [Chrome Debug function](https://facebook.github.io/react-native/docs/javascript-environment.html) as it's using the V8 engine.

React Native still being in development, the dev team is experimenting with some other engines. They recently updated the JSCore version, resulting in a 40% increase in performance.

# Styling and Animations

Although not being CSS, the styling in React Native is heavily inspired by it. Design is made using the StyleSheet module and consists merely of declaring a JS object with CSS inspired properties. Not every CSS feature is supported. For example, no nesting is allowed, and animations are supported through a separate Animation API mechanism.

Concerning the components positioning, you'll only use the flex properties (it's like CSS flex work), which is great when you want your application to work across a variety of device resolutions.

```javascript
const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: 'column',
    justifyContent: 'space-between',
    borderRadius: 4,
    borderWidth: 0.5,
    borderColor: '#d6d7da',
  },
  title: {
    fontSize: 19,
    fontWeight: 'bold',
  },
  activeTitle: {
    color: 'red',
  },
});
```

A big advantage of defining your style in JavaScript is dynamic styling: you don't have to have a style defined only during code time or compile time. With React Native you can compose your styles and properties, combine them and then have them applied onto your component.

This dynamic styling is basically how the animations are made. React Native provides two APIs: Animated and LayoutAnimation; the first one is used to make specific interactions whereas the latter, as its name suggest, is more layout oriented. It will offer less control than Animated but won't require as much calculation to render the animation. Note that this is an experimental feature on Android.

# Or you can use styled-components 💅

Along with the HOC and the stateless components, [styled-components](https://github.com/styled-components/styled-components) are my 2nd revelation of the year. This growing React library allows you to create atomic CSS styled components by mixing HTML components and CSS style.

First of all, no, it is not a CSS in JS kind of library. It spits out CSS classes (with obfuscated class names). The main feature of this library is that it uses the power of ES6's string templates; you can use the component's props and state directly in its style and let style-component do its magic.

Because the style-components dev team is awesome,~~they made it compatible with React Native with only one simple change~~ because they're **really awesome** they improved the use for React Native in their 3rd major release by making it seamless. A single import will be compatible with React and React Native.

**Pro tips:** The [react-primitives](https://github.com/lelandrichardson/react-primitives) project (still in beta) is aiming to provide a common interface between React Native and React. This means that you'll be able to have one single codebase for your mobile app and your web app.

# Testing is to be perfected.

Although the unit testing is fairly standard in RN (you won’t find much difference than with React testing), functional testing is not as easy.

You won’t be able to add functional tests in your CI pipeline, without investing in Mac specific hardware, or a hosted solution such as [Bitrise](https://github.com/lelandrichardson/react-primitives). Compared to webapp functional testing where a lot of solutions exist, it would be very fastidious to set up an online environment for Android and iOS.

Update: Wix, one of React Native's main contributors, is working on [Detox](https://github.com/wix/detox), a greybox e2e testing tool that can run on a CI.

# Dev environment (HMR…).

My recommendation to avoid any hassle with the dev environment is to use the [create-react-native-app](https://github.com/react-community/create-react-native-app) package. It will generate a basic RN app with a default configuration; which is more than enough for most cases. If necessary, you can drop create-react-native-app at any time by running `npm run eject` but be careful, there's no going back! Keep that option away unless you really need to include your own native code.

Once that's done you have two choices to run your app:
* Use Android/iOS simulators with all of the features of each respective simulators
* Use Expo, a mobile application created by Facebook to run your React Native app on your device just by scanning a QR code (some caveats apply).

Once that choice is made you have access to a powerful Hot Module Replacement feature. Any change made to the code will be reflected on the running app, avoiding long compilation time after each tiny change, which I think improves the development time.

# React Native is not in 1.0 yet

One thing to keep in mind for everyone willing to give React Native a go is that the framework has no official release yet (0.52-RC as of Jan 2018). While the Facebook team is being considerate, you can still expect considerable changes before 1.0 is released, so develop with care and avoid any massive tweak with the framework. They are at least now providing a git-based upgrade utility to help projects upgrade between versions, but you will need to upgrade often if you don't want to be left behind.

# Who uses/contributes to React Native?

As of today, [heaps of companies](http://facebook.github.io/react-native/showcase.html) are using React Native in production. Amongst them you can find Tesla, Instagram, Facebook, AirBNB, Skype, Wix...

While still being young, React Native is for me a great framework with promising support. Based on the very popular React, it will be very easily adopted and will facilitate migrations between webapps and native applications. If you're excited to see what's next for RN, you can stay up to date with their blog, their Twitter account or the releases notes.

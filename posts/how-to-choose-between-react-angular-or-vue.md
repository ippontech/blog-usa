---
authors:
- Cory Walker
tags:
- React
- Angular
- Vue
- JS
- Javascript
- Javascript Library
- Javascript Framework
- Front End Development
- Front End Framework
- Front End Library
date: 
title: "How to Choose Between React Angular or Vue?"
image: 
---

# How should you choose Between React, Angular, or Vue for your next project?


It can be a difficult deciding whether to use [ReactJS](https://reactjs.org), [Angular](https://angular.io), or [Vue.js](https://vuejs.org) as your user interface (UI) for your next project. Here we will discuss some ways to simplify the process when deciding between the three frameworks. There are a few things to consider before making your selection. First, we will look at React, Angular, and Vue at a high-level. Then, we will compare and contrast a few key features and see how they stack up against one another. Next, we will discuss how to decide which framework or library to use. Then, we will wrap up with some final thoughts. 


## Front End Framework/Library Overview

The [React](https://reactjs.org) website states that the platform (created by Facebook) is a JavaScript library for building user interfaces. On the other hand, the [Angular](https://angular.io) website states that Angular (created by Google) is an application design framework and development platform for creating efficient and sophisticated single-page apps (SPAs). [Vue](https://vuejs.org) (community-developed) says it is a progressive framework for building user interfaces. Right off the bat, we notice that one key difference between the three in their respective definitions is that ReactJS is considered a library, while Angular and Vue.js are considered frameworks. Despite the terms "framework" and "library" being similar with some slight technical differences between the two, for the sake of this article, we are going to try to make things as simple as possible and use the labels "framework" and "library" interchangeably going forward. If you would like to learn more about the differences between frameworks versus libraries, here is a great article about such [freecodecamp.org](https://www.freecodecamp.org/news/the-difference-between-a-framework-and-a-library-bd133054023f/). 


## Key Similarities Between Frameworks

### Single Page Applications (or SPA)

With that out the way, I believe that the real challenge of selecting which framework to use stems from all three JavaScript frameworks being similar in several aspects. They are all considered “Single Page Applications” ("SPA," for short), which are designed to have a fast, dynamic end user experience. SPAs make this possible by allowing end users to interact with websites without loading whole new pages from the server, which can enhance performance. According to the [Mozilla](https://developer.mozilla.org/en-US/docs/Glossary/SPA) website, an SPA is a web app implementation that loads only a single web document and then updates the body content of that single document via JavaScript APIs. 


### Component-Based

All of these frameworks are component-based driven. [Component-based development](https://en.wikipedia.org/wiki/Component-based_software_engineering) is what fuels single page applications and make them so fast, where only the component reloads as opposed to the entire web page. Components are also reusable, which can reduce the amount of development code. Components are also easy to test and maintain as each component is self-maintained and independent.


## Key Differences Between Frameworks

My disclaimer going forward is that some of the key points about each framework can be subjective depending on a person's experience, background, or independent research. However, the goal is to get you somewhere in the ball park of the things you should be considering or looking into when deciding between which framework to use.


### ReactJS: A Brief Overview

React is considered a lightweight library, meaning that out-of-the-box it does not come with well-known, pre-installed features or tools, such as routing and form management. However, React has tons of packages you can install to add additional functionality to your application. Therefore, as a developer, you will have more control over your application toolset by installing only the tools you need as you need them.

Some will argue that React is the most popular out of the three frameworks and that it has a large [community](https://reactjs.org/community/support.html) of support and [forums](https://reactjs.org/community/support.html#popular-discussion-forums) on topics around application development. There is also a consensus that React is the _easiest_ framework to learn among the three. In my option, React has a somewhat medium-sized learning curve due to its [JSX](https://reactjs.org/docs/introducing-jsx.html) syntax, where you mix HTML, CSS, and JavaScript in the same file or component. React is the only framework that uses JSX syntax, whereas Angular and Vue uses an HTML template. The HTML is typically isolated from the core [Javascript](https://developer.mozilla.org/en-US/docs/Web/JavaScript) logic.


### React JSX Example

``` javascript
// html is mixed in with the javascript
function App() {
  return (
    <div className='App'>
      <h1>Hello World</h1>
    </div>
  )
}

export default App
```


### Angular: A Brief Overview

Angular is more of a heavyweight framework, meaning that out-of-the-box, it includes _many_ built-in features you will more than likely need for a large, enterprise application. Functionality such as routing, form validation, and testing are all included with Angular. One of its features is its [Command Line Interface](https://angular.io/cli) (CLI), which is a useful tool when developing Angular projects. As a result of Angular's many-included features, it has a reputation of having a steeper learning curve. Angular also uses [Typescript](https://www.typescriptlang.org/) out-of-the-box, which is known to make your code more robust and less prone to bugs. However, there is still no JSX to learn, being that Angular uses an HTML template, which is normally in a separate file away from the core JS logic. Lastly, according to Angular's website during the time of this article's publication, there are not as many resource links for [community / forums](https://dev.to/t/angular), and [support](https://stackoverflow.com/questions/tagged/angular) on the website compared to that of React's. 


### Angular Component + Template Example

``` javascript
// component logic
@Component({
  selector: 'app-myApp',
  templateUrl: './myApp.component.html',
  styleUrls: ['./myApp.component.css']
})

export class myAppComponent implements OnInit {
  constructor() { }
  ngOnInit(): void { }
}

// component html template in separate file
<p>myApp works!</p>
```


### Vue.js: A Brief Overview

Vue may be considered the least in popularity due to it being the newer of the frameworks and many developers or businesses haven't had the opportunity to explore the possibilities. This may have an effect on it's smallar [community](https://dev.to/t/vue) size as well. There is also a factor that Vue is built by a community of people versus an actual company like Facebook and Google, but that's open for discussion. Hence, they do have a community [forum](https://forum.vuejs.org/) as a resource link on their website.

Vue sits somewhere in the middle of React and Angular as far as what is included with the initial install. It does not come as bare bones as React, nor does it include as many features as Angular out-of-the-box. However, it does include common features like routing and state management as part of its core library. Vue also has its own [CLI](https://cli.vuejs.org/), similar to Angular. Vue arguably has a reputation of having the smallest learning curve out of the three. Similar to Angular, Vue uses an HTML template, but does not use the JSX syntax. Moreover, unlike Angular, Vue does not come packaged with TypeScript, which is another tool to learn. It is certainly possible that with Vue having the most recent release date, it gave its original developers an opportunity to build the framework to improve on the areas where the other two frameworks were too complexe or cumbersome. 


### Vue Component Example

``` javascript
// component html template
<template>
  <h1>Hello World</h1>
</template>

// component logic
<script>
export default {
  name: 'App',
  Components: { },
}
</script>

<style></style>
```


## Framework Breakdown Recap Table

| --- | React | Angular | Vue |
| --- | --- | --- | --- |
| Commnuity | Large | Medium | Small |
| Popularity | Large | Medium | Small |
| Out of the Box | Small | Large | Medium |
| Learning Curve | Medium | Large | Small |
| Project Size | Small | Large | Medium |
| HTML Syntax | JSX | HTML Template | HTML Template |


## Your Project Needs

Let’s step away from code for a moment and look at a real world project. As an example, say that you wanted to put up some new drywall in your home. What tools would you need to complete such a project? Well, would you agree that you may need some sand paper, mud, nails, hammer, spatula, to name a few? You would also know what tools you do _not_ need, such as a pipe wrench. So really, it depends on the project to dictate what tools you should use, right? This is the same for choosing a front end framework or library like React, Angular, and Vue. Depending on the type of project you are building, your use case helps determine which tool to use to build it out. Essentially, React, Angular, and Vue are merely tools in a toolbox. And some tools are simply better than others.

The very first thing you should consider when choosing React, Angular, or Vue is what _type_ of project you want to build. You should consider things like your project's overall needs, its main features, and the main objective. Then you can go back and determine which framework or library matches closest to your needs.

Another example -- you are looking to build a simple application and speed is a priority.  You probably want to go with a lightweight tool that is, well, fast! Also, if you do not need a lot of bells and whistles, then go with React. On the opposite end of the spectrum, if you are looking to build a more robust, heavy-featured enterprise application, Angular may be your go-to. If it becomes a case where you may need an application that is not really lightweight, but not overly-featured either, Vue can be a great tool to meet your needs somewhere in that middle ground.


## Other Things To Consider

As you have seen earlier, each framework (or library) has its pros and cons. You can look at it as React being the most lightweight, Angular being the most heavyweight, and Vue being somewhere in the middle; not too light, not too heavy. Vue also appears to have the lowest learning curve, being that it does not use the JSX syntax and has more shorthand syntax, ultimately meaning less code to write. However, ease of use is obviously subjective depending on an individual’s experience and background. There are less subjective differences, such as the amount of support or community size, out-of-the-box features, and the syntax necessary to achieve the same output within the code.

When considering support or the size of community, determine if your developers rely heavily on support and outreach. How often does your team lean on help from the community to solve problems? Is using support a pivotal part of your production process? At this point, React and Angular have larger communities than Vue.

In addition, are your developers on the more senior end? Which framework or library do your developers currently have the most knowledge with? Will the complexity of learning a framework or library make a significant impact on development time or robustness of the application? Do you have the funds to outsource the development of your application to more experienced developers? If the complexity of the framework becomes a factor, Vue just might be your safe bet.


## Wrap Up

Is one framework or library better than the other? I would answer "no," mainly because all of these tools have a purpose in web application development, all rooting primarily to _your_ application needs. Then consider all the other secondary factors about the pros and cons of each framework or library, such as the learning curve, syntax, and support. Evaluate your developers' experience and level of expertise. It may be more beneficial to work with the framework or library that your developers are most experienced with. This can save you time and labor by reducing the amount of learning, researching, trial and error, etc., as well as the potential amount of mistakes or bugs in code. It all boils down to using the right tool for the job. Learn more about the latest features of [ReactJS](https://reactjs.org), [Angular](https://angular.io), and [Vue.js](https://vuejs.org) from their websites and discover which one can work best for your situation.






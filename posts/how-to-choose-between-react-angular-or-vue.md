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

# How to Choose Between React Angular or Vue for Your Next Project?


It can be a difficult deciding whether to use [React.js](https://reactjs.org), [Angular.js](https://angular.io), or [Vue.js](https://vuejs.org) as your user interface or UI for your next project. Here we will discuss some ways to simplify the process when deciding between React, Angular, or Vue. There are a few things to consider before you make your selection. First, we will briefly look at React, Angular, & Vue at a high level overview. Then, we will compare and contrast a few key features and see how they stack up against one another. Next, we will discuss how to decide which framework or library to use. Then, we will finally wrap up with some final thoughts. 


## Front End Framework/Library Overview

The [React](https://reactjs.org) website states, that React, (created by Facebook) is a JavaScript library for building user interfaces. On the other hand, the [Angular](https://angular.io) website states, that Angular, (created by Google) is an application design framework and development platform for creating efficient and sophisticated single-page apps. Lastly, the [Vue](https://vuejs.org) website says that Vue, (developed by the community) is a progressive framework for building user interfaces. Right off the bat, we notice that one key difference between the three in their respective definitions is that React is considered a library and Angular and Vue are considered frameworks. Even though, the terms framework and library are very similar with some slight technical differences between the two, for the sake of this article, we are going to try to make things as simple as possible and use the labels framework and library interchangeably going forward. If you would like to learn more about the differences between frameworks verses libraries, it's a great article about it at [freecodecamp.org](https://www.freecodecamp.org/news/the-difference-between-a-framework-and-a-library-bd133054023f/). 


## Some Key Similarities Between Frameworks

### Single Page Applications or SPA

Now, with that out the way, I believe that the real challenge of selecting which framework to use, stems from all three javascript frameworks being very similar in many ways. They're all considered “Single Page Applications” or “SPA”, which are designed to have a fast dynamic end user experience. Single Page Applications make this possible by allowing end users to interact with websites without loading whole new pages from the server, which can enhance performance. According to the [Mozilla](https://developer.mozilla.org/en-US/docs/Glossary/SPA) website, an SPA is a web app implementation that loads only a single web document, and then updates the body content of that single document via JavaScript APIs. 


### Component-Based

Furthermore, all of these frameworks are component-based driven. [Component-based development](https://en.wikipedia.org/wiki/Component-based_software_engineering) is what fuels single page applications, and make them so fast; where only the component reloads versus the entire web page. Components are also reuseable which can reduce the amount of development code. Components are also easy to test and maintain as in, each component is self-maintain and independent.


## Some Key Differences Between Frameworks

My disclaimer going forward is that, some of the key points about each framework can be subjective depending on a person's experience, background, or independent research. However, the goal is to get you somewhere in the ball park of the things you should be considering or looking into when deciding between which framework to use.


### React Brief Overview

React is considered a light-weight library being that, out of the box, it doesn't come with many pre-installed features or tools, such as routing and form management. However, React has tons of packages you can install to add additional functionality to your application. Therefore, as a developer, you will have more control over your application toolset, by installing only the tools you need, as you need them. 

Many people argue that React is the most popular out of the three frameworks and that it has a huge large [community](https://reactjs.org/community/support.html) of support and [forums](https://reactjs.org/community/support.html#popular-discussion-forums) on topics around application development. It's also a popular consensus that React is the easiest framework to learn between the three. Where, in my option, React has a somewhat medium size learning curve due to it's [JSX](https://reactjs.org/docs/introducing-jsx.html) syntax, where you mix html, css, and javascript in the same file or component. React is the only framework that uses JSX syntax, where Angular and Vue uses an html template, where the html is normally isolated from the core [Javascript](https://developer.mozilla.org/en-US/docs/Web/JavaScript) logic.


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


### Angular Brief Overview

Angular is more of a heavy-weight framework being that, out of the box, it includes many built-in features you will more than likely need for a large enterprise type application. Funtionality such as routing, form validation, and testing are all included with Angular. One of it's features is it's [Command Line Interface](https://angular.io/cli) or CLI, which is a very useful tool when developing Angular projects. As a result of Angular's many included features, it has a reputation of having a steep learning curve. Angular also uses [Typescript](https://www.typescriptlang.org/) out of the box, which is known to make your code more robust and less prone to bugs. However, there's still no JSX to learn, being that Angular uses an html template which is normally in a separate file away from the core JS logic. Lastly, according to Angular's website during the time of this article publication, there aren't as many resource links for [community, forums](https://dev.to/t/angular), and [support](https://stackoverflow.com/questions/tagged/angular) on the website compared to React's website. 


### Angular Component & Template Example

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


### Vue Overview

Vue may be considered the least in popularity due to it being the newer of the frameworks and many developers or businesses haven't had the opportunity to explore the possibilities. This may have an effect on it's smallar [community](https://dev.to/t/vue) size as well. There is also a factor that Vue is built by a community of people versus an actual company like Facebook and Google, but that's open for discussion. Hence, they do have a community [forum](https://forum.vuejs.org/) as a resource link on their website.

Vue sits somewhere in the middle of React and Angular, as far as, what's included with the inital install. It doesn't come as bare bones as React nor does it include as many features as Angular out the box. However, it does include common features like routing and state management as part of it's core library. Vue also, has it's own [Command Line Interface](https://cli.vuejs.org/) or CLI similar to Angular. Now, Vue arguably has a reputation of having the least learning curve out of the three frameworks. Similar to Angular, Vue uses an html template and doesn't use the JSX syntax. Moreover, unlike Angular, Vue doesn't come packaged with Typescript, which can be an additional tool to learn. Lastly, it's possible that, Vue having the latest release date, gave the original developers of Vue an opportunity to build a framework, improving on areas where the other frameworks were complexed or cumbersome. 


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
| HTML Syntax | Jsx | html template | html template |


## Your Project Needs

Let’s step away from code for a moment, and let’s look at a real world or physical project. Let’s say that you wanted to put up some new dry wall in your home. What tools do you think you need to complete the dry wall project? Well, would you agree that you may need some sand paper, mud, nails, hammer, spatula, to name a few? You also probably going to know what tools you don’t need, such as, maybe a pipe wrench or a alley wrench. So, it really depends on the project, of what tools you should use, right? This is the same for choosing a front end framework or library like React, Angular, and Vue. Depending on the type of project you are building, helps you determine which tool to use to build it. Essentially, React, Angular, and Vue are merely tools in a toolbox used to build projects. And some tools are better than others depending on the project.

Therefore, the first thing you should consider when choosing React, Angular, or Vue is, what type of project you want to build? You should consider things like, what are your project overall needs, main features, and main objective? Then, go back and determine which framework or library match closest to your project needs right out the box.

For example, if you are looking to build a simple application and speed is a priority, then you probably would want to go with a light weight tool, that’s probably fast. Also, if you don’t need a lot of bells and whistles, then you should go with something like React. On the opposite end of the spectrum, if you are looking to build a more robust, heavy featured enterprise application, Angular may be your tool that you go for in your toolbox. Then, there’s the case where you may need an application that’s not really light weight, nor large and heavy featured either, hence somewhere in between. Vue can be a great tool to use from your toolbox that kind of meet your needs somewhere in the middle.


## Other Things To Consider

As you have seen earlier, each framework or library has it’s pros and cons. You can look at it as, React being the most light weight library and Angular being a more heavy or larger framework. And Vue is somewhere in the middle, not too light, and not too heavy. Vue can also appear to have the lightest learning curve being that it doesn’t use the jsx syntax, and has more shorthand syntax, meaning less code to write. However, easy of use is definitely subjective depending on the individual’s experience and background. Then, there are the less subjective differences such as the amount of support or size of community, out of the box or packaged features, syntax used to achieve the same output within the code. 

When considering support or the size of community; determine if your developers weigh heavily on support and community. How often or frequent do you or your developers lean on help from the community to solve problems. Is using support a huge part of your production process? React and Angular have larger communities than Vue.

Also, are your developers more senior or mid to junior level? Which framework or library does your developers currently have the most knowledge with? Will the complexity of learning the framework or library make a significant impact on the development time or robustness of the application? Moreover, do you have the funds to outsource the development of your application to experienced developers? If the complexity of the framework a major factor, Vue maybe a good choice.


## Wrap Up

Is one framework or library better than the other? I would answer 'no' because all these tools have a purpose in the web application development space, depending on your application needs. Remember to consider your application needs most importantly. Then consider all the other secondary factors about the pros and cons of each framework or library, such as the learning curve, syntax, and support. Also, evaluate your developer's experience and level of expertise. It may be more beneficial to work with the framework or library that your developers are most experienced with. This can save you time and labor, by reducing the amount of learning, researching, trial & error, as well as, the potential amount of mistakes or bugs in the code. However, it all boils down to using the right tool for the job. Learn more about the latest features of [React](https://reactjs.org), [Angular.js](https://angular.io), and [Vue.js](https://vuejs.org), from their websites and discover which one will work best for your situation.






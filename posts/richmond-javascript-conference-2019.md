---
authors:
- Cody Frenzel
tags:
- Front-End
- Conference
- JavaScript
date:
title: "RVA JavaScript Conference 2019"
image: 
---

RVA JavaScript Conference is, unsurprisingly, Richmond's first conference dedicated to everything JavaScript. This was my first year attending, as well as, my first time attending a local conference. I was the only person from Ippon to attend but had the chance to see many friends, former co-workers, and fellow [RVA.js meetup](https://www.meetup.com/rva-js/) attendees. This was very different from my usual conference experiences where I know virtually no one and what I like most about the Richmond JavaScript community. While all the speakers I saw were great, I wanted to write about a couple of the talks I had strong feelings about for one reason or another.

## **Micro Frontends in Enterprise Applications**
Nick Holmstedt talked about how CoStar is implementing micro frontends, which is a very large topic of interest for me. If implemented poorly I believe micro frontends introduce more friction to the daily process of the developer than they are worth. I attended this talk because the only micro frontend I ever worked with was extremely interdependent, utilized version locking that would break the app if other components were using different npm versions, and it was so opinionated that an Angular app built using it couldn't utilize the AngularCLI because it broke Angular standards. CoStar's solution utilizes a reverse proxy to route to separate apps. With their solution, they can easily develop in the context of one component, as well as, the whole app context. This leaves the proxy middleware to hold all of the shared contexts between apps and creates a reliance on the owning team to update that, however, I still find this more suitable than keeping versions in sync with npm packages.

## **Human Readable JavaScript: Using ES2015+ to Craft Better Code**
Laurie Berth was a delight to listen to this year. As someone who is currently writing a lot of vanilla JavaScript, this talk was very interesting to me and made me think about the weirdness of JS, consistency in syntax I'm using throughout my code, and readability for others when I have to pass along my work. Laurie has a blog post [here](https://dev.to/laurieontech/human-readable-javascript-337o) that covers some of the concepts of her talk. I wanted to point out three examples she uses that don't even encompass the entirety of that blog post on how identical code can be written in so many different ways.

### Example
``` JavaScript
const arr = [1,2,3]
let multipliedByTwo = arr.map(el => el*2)
// multipledByTwo is [2,4,6]
```
Now add optional parenthesis
``` JavaScript
const arr = [1,2,3]
let multipliedByTwo = arr.map((el) => el*2)
```
Now add curly braces and a return statement
``` JavaScript
const arr = [1,2,3]
let multipliedByTwo = arr.map((el) => { return el*2})
```

### Who decides readability
The talk, in general, was very intriguing to see all of the variations you can have in JavaScript but left me with the lingering question of what choices we make when programming will make our code the most readable. The answer is not always the same, but especially as a consultant it is something that must be kept in mind. Every single project I am on will be handed off to someone else at one point or another. Generally, I think if you are following clean coding principles and keeping the idea in your head that someone else will have to work on this after you leave, good decisions will be made. Will your choices be perfect? No, but how often do you look at your old code and think it can't be improved?

### Things I'm excited for in JS
Laurie also mentioned two items that would be making their way into the JavaScript standards and those were **nullish coalescing** and **optional chaining**. If you don't know what they are, here's an example:
``` JavaScript
// Nullish Coalescing
var value = null ?? 'default string' // in this case || will work for null/undefined, but not for falsy values like 0, '', and false


// Optional Chaining
// This helps get property values in a tree like structure without checking intermediary nodes
var street = response.body?.address?.street;

// without optional chaining
var street = response.body && response.body.address ? response.body.address.street : undefined;
```

## **Delicious JavaScript: Delectable Explanations of the Power of JS**
Adrienne Tacke gave a good talk on how she broke down the essential and most commonly used methods in JavaScript using examples centered around dessert, which she loves. None of these functions were new to me, but I found her perspective on boiling down the explanations to something simple and framing it in a more relatable manner very helpful. I've been in situations where I feel like I can't explain something any more clearly. However, I already have the familiarity with the function I used and the context I used it in. What ultimately led to the misunderstanding was introducing a new function in an unfamiliar context to someone else. Relating the new concept to a context they are more familiar with removes half of the mystery! This is also similar to how I learned RxJS and fell in love with reactive programming, by using [RxMarbles](https://rxmarbles.com/). When I wasn't looking at the functions in an abstracted code snippet from a project I didn't know about, it was a lot easier to see how the functions behave. When you're first learning some documentation is very intimidating, that's why viewing how the functions work in a more relatable or interactive manner is so helpful.

## **A Tinder Love Story: How we created our WordPress blog in React...not PHP.**
I don't plan to create a WordPress blog anytime soon, but Kyle Boss from Tinder was a great speaker and got me interested in trying out [Gatsby.js](https://www.gatsbyjs.org/) to create a site. Gatsby is a static Progressive Web App generator that leverages React. Gatsby only loads the critical HTML, CSS, JavaScript, and data. Then Gatsby will prefetch resources for other pages, which will make clicking around your site feel fast.
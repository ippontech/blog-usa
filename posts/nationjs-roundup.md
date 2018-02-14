---
authors:
- Leigha Wilson
categories:
- Conference
- JavaScript
- JHipster
- Language
date: 2015-11-24T17:08:15.000Z
title: "NationJS Roundup"
image: 
---

Recently we had the opportunity to attend NationJS, a Mid-Atlantic JavaScript conference held in Silver Springs, Maryland which had 300 attendees and thirteen speakers. A wide variety of interesting JavaScript topics were discussed, ranging from testing, writing documentation, interfacing with hardware, and much more.

 

Click on a link below to be taken directly to that section of the article.

- **[Becoming A Happy Javascripter by Julia Allyce](#julia)**
- **[How to Stop Hating your Test Suite by Justin Searls](#justin)**
- **[documentation.js by Tom MacWright](#tom)**
- **[Hardware for the Softworld by Stacey Mulcahy](#stacey)**
- **[Writing Command Line Applications in Node.js by Josh Finnie](#josh)**
- **[Building a musical instrument with the Web Audio API by Steve Kinney](#steve)**
- **[Analytics and Tracking Tools on Single Page Apps with AngularJS by Lizz Somerfield](#lizz)**
- **[High Performance in the Critical Rendering Path by Nicolas Bevacqua](#nicolas)**
- **[JavaScript Rehab for Grunters and Gulpers by Erik Isaksen](#erik)**
- **[React is “Functional” by John K Paul](#john)**

 

### **Becoming A Happy Javascripter by Julia Allyce**

<span style="font-weight: 400"> Julia Allyce, a java developer and consultant for Open Source Software, started the conference off. She began her talk by expressing to everyone that “Happiness != Expertise”. She said this is something that you need to keep in mind when attempting to make that leap from just starting out to being a proficient front-end JavaScript master. She went on to say that happiness is not constant; It is something that, as a developer, you need to be aware of your level of happiness and find things that increase it greatly. According to Julia there are three steps to making this easier on you: Confidence, productivity and security. She called these the “tools to learning” and are what you need to succeed in your career.</span>

<span style="font-weight: 400">Taking the initiative to leave when you don’t have the tool or knowledge to complete a task should be at the top of your go-to list. Julia said that feeling stupid is just part of the everyday life of a developer; it shouldn’t stop you from continuing on your path and succeeding in what you set your mind to. Pushing yourself to “shamelessly ask questions” will only get you that much further as a JavaScript developer.</span>

<span style="font-weight: 400">One key take-away was the idea that learning the JavaScript, not the framework, is where your developer mind needs to go. Frameworks require a strong foundation. Learning a framework will get you a good way, but learning the basics of what the framework uses will get you ever further. The frameworks are not always going to exist; Even right now, they are changing, but the JavaScript behind it will be used all the same.</span>

<span style="font-weight: 400">Julia mentioned some tools to learn and use in order to better foster the growth and development of new developers looking into JavaScript: Backbone.js (even though it may seem like an outdated library because of the little “magic” being used, it has excellent concepts behind the single-page application [spa]) and Express/Mongoose/MongoDB (specifically for the rest API’s).</span>

<span style="font-weight: 400">Next Julia went over some skills to keep in mind when interviewing as well as when giving the interviews. She said that even though you may want to put anything and everything you have ever come across on your resume, honesty on your real skill set is better. Putting down what you really know and can express well in an interview is best for getting the job. Julia said that considering the culture and people in the company before deciding on a place to work should be just as important as the interview itself. </span>

<span style="font-weight: 400">As for giving interviews, as a company, Julia mentioned that the strategy of “whiteboarding” questions has changed. Many companies are now asking developers to complete a project instead of writing functions out on a whiteboard. Take a look at the concept </span>[<span style="font-weight: 400">here</span>](https://modelviewculture.com/pieces/techinical-interview-are-bullshit)<span style="font-weight: 400">. Following this type of interview process will better allow you to see whether the candidate can follow requirements and whether they are able to actually ask questions when they run into problems on a project. Another strong point Julia made was to always explain the </span><span style="font-weight: 400">correct</span><span style="font-weight: 400"> answer at the end of an interview. Even if they do not get the job at your company it is best that you teach them and that they learn.</span>

<span style="font-weight: 400">The last thing Julia touched on was what to do when you actually get the job: help out the new guy, make sure they are learning to the best of their ability. Become a mentor by going to Meetups and schools; try to pass on your knowledge to those that are wanting to learn.</span>

<span style="font-weight: 400">A good read to look into: </span><span style="text-decoration: underline"><span style="font-weight: 400">Eloquent Javascripts</span></span><span style="font-weight: 400"> by Majijn Haverbeke</span>

 

### How to Stop Hating your Test Suite by Justin Searls

<span style="font-weight: 400">Following Julia Allyce was Justin Searls. He actually sat at our table prior to the conference’s start and made a comment about taking notes during the talks today. His comment was more directed at his talk and the fact that he had 365 slides to get through in the 45 minutes he had. He warned us that we would not be able to take notes, but that he would enjoy seeing us try. This warning did not quite prepare me for the extremely speedy, but greatly educational talk Justin gave.</span>

<span style="font-weight: 400">Justin co-founded </span>[<span style="font-weight: 400">Test Double</span>](http://testdouble.com/)<span style="font-weight: 400">, a software company that helps build better software for businesses, developers and customers. Justin is a speaker at conferences all over the world, and he was definitely great to hear from.</span>

<span style="font-weight: 400">Justin’s focus was on consistency. He spoke about the lengthy tests developers use on their applications and the wasteful time they take. He said that deciding on testing conventions saves time and headaches and should absolutely be conceived of early on. His talk introduced a testing guide for how to setup your rules and templates and prepare better test cases for the ease of your application development.</span>

<span style="font-weight: 400">One of the first slides Justin showed expressed that most developers are stuck in a bad way, with the thought that when tests are not working, that we need to test harder. This does NOT work. He said that when the tests are not working as they should be there is a problem elsewhere and focusing on testing harder is a bad solution. One thought would be to stay away from creating large objects. These objects will require many, more difficult test cases. </span>

<span style="font-weight: 400">Justin mentioned the rule of product here to put things in perspective on the writing test cases: The number of products (in this case objects) times each other equals the total amount (in this case, test cases). Limiting new, large objects and instead making lots of little objects would be better. He said to minimize each phase to just one line.</span>

<span style="font-weight: 400">Next he dove into his version of the testing pattern Arrange-Act-Assert which he called Given-When-Then; these patterns help format code for unit testing. He started by saying that when you come across the problem of having too many steps or just too much happening to do the obvious, and slim it down. Justin continued to give tips on how to keep consistency by saying to give the subject the name of what is being tested. Make the test cases readable to those that are NOT YOU. This will keep the meaning and usefulness of the test from being buried.</span>

<span style="font-weight: 400">“Keep test data minimal and meaningful”. I enjoyed this slide. Justin wrote it out perfectly. Keeping your test data clean, meaningful and as minimal as possible makes for better testing. Everything is much quicker and understandable, and if – heaven forbid – you run into issues, your test cases will be easy to follow to find the problem. Working on message failure workflow will keep any issues from arising here, because even if your test are extremely fast – if you have terrible failure messages you will spend all of your time trying to figure out what is failing and why, and intelligible failure messages resolve that.</span>

<span style="font-weight: 400">One issue we all run into is scope. No matter what a developer does, the customer will always want to push it a </span>*<span style="font-weight: 400">little</span>*<span style="font-weight: 400"> further. They want just a tiny bit more, no big deal. In testing this causes an issue. Before testing/development starts define your boundaries. Know what browsers you will be using, know how much money you have to work with the develop everything, know the load time of the application. Talking about these issues in advance will solve the boundaries issue down the road. Everyone needs to be on the same page!</span>

<span style="font-weight: 400">One of the last topics Justin covered, still not out of breath mind you, was the difference between a true negative and a false negative and the need to know the difference and keep track of when they happen. A true negative is when the test fails because you have broken code somewhere, and causes a developer to come and fix the code. A false negative is when the test fails because the test was written poorly or just plain wrong, and does not tell you if the code is broken or not which results in someone having to rewrite the test case and test again. Obviously it is best if the test case fails for code-breaking reasons. Keeping track of when a true negative happens versus a false negative will allow you to know if test cases are frequently being created incorrectly. This will help to sort through good and bad testers/developers.</span>

 

### **documentation.js by Tom MacWright**

<span style="font-weight: 400">Tom MacWright presented a library called </span>[<span style="font-weight: 400">JSDoc</span>](http://usejsdoc.org/)<span style="font-weight: 400">, which is very similar to </span>[<span style="font-weight: 400">Javadocs</span>](https://en.wikipedia.org/wiki/Javadoc)<span style="font-weight: 400"> and has similar notation which allows one to document their JavaScript. Often people argue that document generators are unnecessary because we strive to write self documenting code, or that it is overly verbose to describe all the required properties. </span>

<span style="font-weight: 400">Tom introduces us to the idea of an Abstract Syntax Tree (AST) to reduce the verbosity of the documentation tags. With an AST we can infer a lot about what we are trying to document, such as recognizing a function and the name of the function. </span>

<span style="font-weight: 400">The purpose of JSDoc is to bring back the use of documentation generators as opposed to handling this manually in a markdown file. This library becomes more useful thanks to the ES6 module system, while our code is modular, the documentation should not be. With JSDoc all the documentation will be in one place, even if the code is not.</span>

<span style="font-weight: 400">JSDoc is a cool library for generating documentation for your JavaScript code in multiple forms (it can generate HTML, and Markdown for you), and worth checking out.</span>

 

### **Hardware for the Softworld by Stacey Mulcahy**

<span style="font-weight: 400">Next was Stacey Mulcahy. She was very bright and easily held everyone’s attention with her great videos and big personality. Stacey is a web developer at Microsoft. She has spoken internationally and is a technical editor.</span>

<span style="font-weight: 400">Stacey started her talk by mentioning all the devices that as developers, we should all want to use. She said that she prefers working with her hands over coding, but loves mixing the two together to make fun, not always practical, things. Her list of Open Source, microcontroller devices included: Arduino, Raspberry Pi, Tessel, Intel Edison, Particle Photon (only $20, wifi-enabled, leverages Node, has an SDK), Teensy (very small), and NetDuino (used .Net).</span>

<span style="font-weight: 400">Next Stacey shared with the conference some very neat inventions using some of the devices she mentioned previously. My favorite was the “Booty Bump”. Her description of the dilemma she faced really hit home. Have you ever written some amazing code, and it really works and everything is awesome, and absolutely no one around you cares. So you go from feeling like you want to fly to wanting to punch someone in the face? Yeah, it happens. Stacey came up with a very interesting solution. She created Booty Bump. The idea was that you could go up and booty bump this Booty Bump (booty shape on the wall of the office) and a light show would happen for several seconds. You can dance around and celebrate your success, then go back to work. This project used Raspberry Pi, two sensors, and some lights. It was perfection (and super annoying, so it did not last long in the office). Another she mentioned was </span>[<span style="font-weight: 400">SkrillTrex</span>](http://thebitchwhocodes.com/2014/10/29/skrilltrexthe-dubstep-dino-with-arduino-and-processing/)<span style="font-weight: 400">.</span>

<span style="font-weight: 400">After hearing of the oh-so-fun technology you can build with just a tiny device and some JavaScript Stacey went through some good libraries to look into: </span>[<span style="font-weight: 400">SpaceBrew</span>](https://github.com/Spacebrew/spacebrew)<span style="font-weight: 400"> (makes connecting interactive things to one another over a local network; built for prototyping development), </span>[<span style="font-weight: 400">cylon.js</span>](http://cylonjs.com/)<span style="font-weight: 400"> (allows interaction with sensors easily), </span>[<span style="font-weight: 400">johnny-five.js</span>](http://johnny-five.io/)<span style="font-weight: 400"> (robotics JavaScript framework), and </span>[<span style="font-weight: 400">device.js</span>](https://github.com/matthewhudson/device.js?)<span style="font-weight: 400"> (operating system based, allows for easy manipulation based on the device being used). </span>

<span style="font-weight: 400">Follow </span>[<span style="font-weight: 400">@bitchwhocodes</span>](https://twitter.com/bitchwhocodes?ref_src=twsrc%5Egoogle%7Ctwcamp%5Eserp%7Ctwgr%5Eauthor)<span style="font-weight: 400"> if any of the above sounds intriguing.</span>

 

### **Writing Command Line Applications in Node.js by Josh Finnie**

<span style="font-weight: 400">Following a short break, Josh Finnie got on the stage to talk about Node.js. Josh is a full-stack engineer at </span>[<span style="font-weight: 400">TrackMaven</span>](http://trackmaven.com/)<span style="font-weight: 400"> and runs NodeDC’s Node Nights, checkout their meetup events </span>[<span style="font-weight: 400">here</span>](http://www.meetup.com/node-dc/)<span style="font-weight: 400">.</span>

<span style="font-weight: 400">Josh walked us through the steps in writing a Node.js application in command line. He went through everything from how to get yourself setup and being, some fallbacks he has seen in the time he has spent creating these Node.js applications through command line, as well as some tips to create the best practices and make for a better application in the end.</span>

<span style="font-weight: 400">Node package manager (NPM) is used in a lot of application. Npm has over 200,00 packages included and is very form-fitting to most applications out there. Josh started with the tip to try for a better argument handler that .argv (the default). Come of his suggestions were: commander.js, yarge.js, minimist.sj, or nopt.js. This tip comes from keeping developers from needing to hunt through the array of process for what the user input using .argv. There are better ways to handle arguments.</span>

<span style="font-weight: 400">ECMA Script 6 (ES6, the first update to the language since 2009) was then mentioned, primarily to reference some of the fantastic new features implemented in this release, such as: </span>[<span style="font-weight: 400">arrows</span>](https://github.com/lukehoban/es6features#arrows)<span style="font-weight: 400"> and </span>[<span style="font-weight: 400">let + const</span>](https://github.com/lukehoban/es6features#let--const)<span style="font-weight: 400">. Using ES6’s newest features allows great things with many of the main JavaScript libraries used in web applications. Node.js, Babel.js and Gulp are all taking strides to stay as up-to-date as possible and allow for easy use of these features.</span>

<span style="font-weight: 400">Watch for updates on ES6 implementation for </span>[<span style="font-weight: 400">Node.js</span>](https://nodejs.org/en/docs/es6/)<span style="font-weight: 400"> and </span>[<span style="font-weight: 400">Babel.js</span>](https://babeljs.io/docs/learn-es2015/)<span style="font-weight: 400">. </span>

 

### **Building a musical instrument with the Web Audio API by Steve Kinney (**[**Talk Resources**](https://github.com/stevekinney/making-music)**)**

<span style="font-weight: 400">Following Josh Finnie, we have Steve Kinney who will be talking about the </span>[<span style="font-weight: 400">Web Audio API</span>](https://developer.mozilla.org/en-US/docs/Web/API/Web_Audio_API)<span style="font-weight: 400"> (a synthesizer that was added to </span>[<span style="font-weight: 400">most browsers</span>](http://caniuse.com/#feat=audio-api)<span style="font-weight: 400">). Steve Kinney is the Co-Director of Academics at </span>[<span style="font-weight: 400">Turing School</span>](http://turing.io)<span style="font-weight: 400">. This talk speaks of how to interact with the web audio api at multiple levels and in multiple applications (which use different techniques).</span>

<span style="font-weight: 400">First the basic api is discussed to make sample sounds at varying frequencies, then we begin adding on complexity. In the simpler stages, we simply use web audio context to create a source oscillator, which allows us to start creating sounds when hooked up to a destination (like your speakers). To add more complexity, we can add nodes between the oscillator and the destination, and in doing so make more complicated sounds. The example given uses a gain node to adjust the volume of the oscillator at a specific frequency and wave type.</span>

<span style="font-weight: 400">Afterward, we built something a bit more complex using an interesting helper library, </span>[<span style="font-weight: 400">Octavian</span>](https://github.com/stevekinney/octavian)<span style="font-weight: 400">. With this library we can forget about what frequency a given musical note is, and instead reference it by name; now we can simulate more interesting instruments (or even create our own instruments). To do this, we simply bind some event to create and start an oscillator at a given note (which is easy thanks to Octavian).</span>

<span style="font-weight: 400">The piano example used simple key press events, but this is not required, as the next example shows. Using a face tracking library we can emit sounds based on facial position as recognized by the camera.</span>

 

### **Analytics and Tracking Tools on Single Page Apps with AngularJS by Lizz Somerfield**

<span style="font-weight: 400">Lizz Sommerfield works at the Washington Post as a technical architect. She spends most of her time investigating better ways to build dynamic interfaces using HTML, CSS and JavaScript. </span>

<span style="font-weight: 400">Single-page development has become more and more popular over that last few years, which has caused problems in the analytics world where everything is based on a page load of the URL changing. Now that we have all moved on, so has web analytics! Lizz’s talk goes through these advances. She covers what you should look into now as well as what you should look out for in the future for web analytics.</span>

<span style="font-weight: 400">She mentions a few options when it comes to getting analytics back, such as: </span>[<span style="font-weight: 400">Tealium</span>](http://tealium.com/resources/the-roi-of-tag-management/)<span style="font-weight: 400">, </span>[<span style="font-weight: 400">Adobe Tag Manager</span>](http://www.adobe.com/solutions/digital-marketing/dynamic-tag-management.html)<span style="font-weight: 400"> and </span>[<span style="font-weight: 400">Google Tag Manager</span>](https://developers.google.com/tag-manager/?hl=en)<span style="font-weight: 400"> (GTM). Since GTM is the most popular right now, this is the one she ran through an example of. She explained the different steps to create an account in GTM as well as how to create tags and triggers for your site.</span>

<span style="font-weight: 400">If you go to </span>[<span style="font-weight: 400">https://github.com/angulartics</span>](https://github.com/angulartics)<span style="font-weight: 400"> you will see a fantastic overview of step-by-step how to setup an angularJS application and create the GTM tags to go with it. This is a great way to start things out and see all of the options you have before applying anything to your actual site.</span>

[<span style="font-weight: 400">Here</span>](https://support.google.com/analytics/answer/6166898?hl=en&ref_topic=6163649)<span style="font-weight: 400"> is another great tutorial with much more in-depth steps on GTM. </span>

 

### **High Performance in the Critical Rendering Path by Nicolas Bevacqua (**[**Talk Resources**](https://speakerdeck.com/bevacqua/high-performance-in-the-critical-path)**)**

<span style="font-weight: 400">Nicolas Bevacqua is a JavaScript and web performance consultant and author of </span>[<span style="font-weight: 400">JavaScript Application Design</span>](https://bevacqua.io/buildfirst)<span style="font-weight: 400"> from Buenos Aires, Argentina. Speed is very important in web development, Amazon recently found that a 100 ms increase in latency led to a 20% drop in traffic. In this talk Nicolas outlines many methods to increase performance, and also suggests tools to monitor performance.</span>

<span style="font-weight: 400">The chrome dev tools are the first spot to find what is affecting performance in your app. Using the audit tab we can receive hints as to where we are performing poorly. More useful tools are found at </span>[<span style="font-weight: 400">PageSpeed Insights</span>](https://developers.google.com/speed/pagespeed/insights/)<span style="font-weight: 400"> and </span>[<span style="font-weight: 400">WebPagetest</span>](http://www.webpagetest.org/)<span style="font-weight: 400">. The former will provide you with a score from one to one hundred and give you advice on how to improve performance, and the latter will give a grade for varying topics.</span>

<span style="font-weight: 400">These tools are great for testing speed and finding problems, but an automated approach would be more useful. This way we can measure the performance from the beginning and see what performance impact a change has. To do this we use a node module called </span>[<span style="font-weight: 400">PageSpeed Index</span>](https://www.npmjs.com/package/psi)<span style="font-weight: 400"> and create a gulp or grunt task to do this for us. Many other node modules exist, and some can be found in the slides, but the message should be clear — test for performance and strive for fast load times. </span>[<span style="font-weight: 400">Grunt Performance Budget</span>](https://github.com/tkadlec/grunt-perfbudget)<span style="font-weight: 400"> can even be used to ensure that every commit stays within some required, pre determined performance constraints.</span>

<span style="font-weight: 400">Other performance increasing methods exist besides the obvious minification of code. Gziping text, reducing the number of requests and turning on Keep-alive are some things to look into. Next we can look into optimizing HTML, CSS, Images, fonts, and JavaScript. Inline critical CSS where necessary, compressing image sizes, and using a font loaders are great to start this process.</span>

<span style="font-weight: 400">Performance is important and should be monitored consistently from the beginning. Set a budget and enforce it to keep the app from growing, and becoming bloated, and make sure you optimize your content. These will lead to a better experience for your users, and should be a major priority.</span>

 

### **JavaScript Rehab for Grunters and Gulpers by Erik Isaksen (**[**Talk Resources**](https://docs.google.com/presentation/d/1HxPQNMtoTM_CBLjkC-khiQ_o556WRHj6XkQ2FZXJlL4/edit#slide=id.p)**)**

<span style="font-weight: 400">Erik Isaksen is a web developer with Deloitte Digital and speaks about the growing complexity of web development. Lots of new tools are appearing, and managing everything can become overwhelming in a world where the UI/UX expectations continually grow.</span>

<span style="font-weight: 400">Grunt and Gulp are two tools used to automate tasks that are no longer possible to do manually, but abusing these with overly complex tasks can lead to more time automating the task than it would have taken to do it manually. The takeaway of this is that a task should be small and meaningful.</span>

<span style="font-weight: 400">Sticking with the concept of increasing productivity, web developers should look into using yeoman app generators, such as an angular app generator, a default one exists (but I encourage readers to look into </span>[<span style="font-weight: 400">JHipster</span>](http://jhipster.github.io/)<span style="font-weight: 400"> if they haven’t already).</span>

 

### **React is “Functional” by John K Paul (**[**Talk Resources**](http://johnkpaul.github.io/presentations/nationjs/2015/react-is-functional/assets/player/KeynoteDHTMLPlayer.html#0)**)**

> <span style="font-weight: 400">“In 2016, freaking out about virtual DOM in react will be like freaking out about gzip in HTML” – John K Paul</span>

<span style="font-weight: 400">Our final speaker of the day provided the above quote as well as plenty of excellent hand drawn diagrams. He is a VP of Engineering who can be found on twitter as @Johnkpaul, and also has his own node package which can be installed with the following command ‘npm install johnkpaul’</span>

<span style="font-weight: 400">In this talk we discuss aspects of React and what it means to be functional. The Virtual DOM is something of a buzzword when speaking of React, but it is simply a tree. This allows us to quickly tell the difference between two Virtual DOM elements.</span>

<span style="font-weight: 400">To understand how React is functional, it is important to understand what it means to be  functional. Refreshing basic math skills, we in turn must learn what a function is. The basic idea is that an input to a function should produce one and only one output. It follows that a functional language is one where this holds.</span>

<span style="font-weight: 400">In the past, many strategies were used to handle communications between the server and the web page view when the user interacted with it. As it turns out, the server side is predictable, and only takes a single step, but this was not always true with the front end. Many times we have a multistep front end process where we make our original HTML, then update data as the user interacts with the page, and finally we try to keep the view and the data in sync. In this way, we are trying to keep the front end predictable (but trying isn’t good for a function). With react, we simply make the view and we know exactly what will happen — we have a predictable front end.</span>

<span style="font-weight: 400">When the virtual DOM changes, we can very quickly compare it to the previous virtual DOM, and we see a performance increase due to this speed.</span>

---
authors:
- Justin Risch
categories:
date: 2015-05-25T12:53:58.000Z
title: "Increasing the performance of your Web Application!"
id: 5a267e57dd54250018d6b5ea
image: 
---

Recently, [Tatami received a massive overhaul](https://blog.ippon.tech/vcu-ippon-angularjs-tatami/) thanks to our partnership with VCU and their team of senior computer science students. Though the app worked beautifully, there were inefficiencies that needed to be addressed. Being halfway through transitioning from Backbone to Angular JS meant that there was more bloat than acceptable– this can happen anytime an app is overhauled and is to be expected occassionally.

When you go to the Facebook login page, your browser makes about 34 GET requests. This is normal. Generally, any number below 50 is acceptable– beyond that, you should consider what you can do to reduce that number. Most web applications are fairly small in size– with Tatami itself being smaller than a single music file (1.4 MB, currently), but GET requests to a server that’s over seas can really stall your application. If I remember my networking class correctly, the total delay would be similar to:

[![bandwidth](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/05/bandwidth.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/05/bandwidth.png)

Since we’re being hosted in France, it was alarming to see that we were making well over 50 GET requests– in fact, it was closer to 450. This was absolutely unacceptable, and so I spent some time boiling that number down to a mere 24– notably better than even Facebook, which I had been using as a milestone of what would be acceptable.

But how did I do it? What was wrong?

Firstly, when switching front-end technologies (such as our switch from Bootstrap to Angular), it’s important to double check your dependencies.

1. What dependencies do you have?
2. What will you still need?
3. What can be deleted?
4. Was anything duplicated?

Because our old app and our new app (Backbone and Angular, respectively) had similar needs, they used one or two of the same packages, and so we accidentally hosted 2 copies of certain dependencies– most notably OpenLayers.js, which is huge in and of itself. Deleting old dependencies from both the machine and the index.html reduced our GET requests down to 150– this was a great start.

Once you strip down the the application to its bare bones, you can take further steps to both shrink the size of your application and the number of requests it needs. For this task, I chose to use a crowd-favorite, [Grunt.js](http://gruntjs.com/). Using its appropriately named [“Uglify”](https://github.com/gruntjs/grunt-contrib-uglify) task, we can concatenate all of our JS files down to a single one. By doing so, you make it so the entirety of your javascript code is sent in one go, rather than forcing the user to ask for each piece. After doing this, we shaved another 126 requests– down to a total of 24. Just be sure to edit your POM to no longer preload those dependencies separately, and your index.html to not include the  <src> tag separately. Everything should point to your new, concatenated file.

After being concatenated into a single file, Grunt is also going to “uglify” it for you– removing comments and whitespace while reducing variable names to be as short as possible. This shaved off ~0.2 MBs of our application, even when using already-minified third party libraries! Quick warning though; if you do “mangle” and you’re using AngularJS, you will need to inject your parameters with String Literals. For example:

```
TatamiApp.run([function($rootScope, $state) {
    $rootScope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState) {...
```

would become…

```TatamiApp.run(['$rootScope', '$state', function($rootScope, $state) {
    $rootScope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState) {
```
This is because angularJS injects based on variable name (`$rootScope` being an example). When grunt goes to uglify the application, this variable gets changed to as short of a variable name as possible, and thus angular doesn’t know what to inject. By including it as a string literal, you let angularJS know which parameter is which. Alternatively, if your application is too complex to add all the necessary strings, or if you won’t see much benefit from mangling, simply disable it in your gruntfile.js, as below:

```
  uglify: {
            options: {
                mangle: false
            }, ...
}
```
From there, you can continue shrinking the application size through CSS and Html minification– though be careful when minifying and concatenating CSS, as I’ve seen some trouble when so with CSS used by dependencies.

That’s the story of how an application that grew to double its size proceeded to shrink to 10% of it’s former self, making only 5% of the requests it once made. Grunt.js is truly a powerful tool, requiring minimal configuration to do a wonderful job, with significant performance upgrades!

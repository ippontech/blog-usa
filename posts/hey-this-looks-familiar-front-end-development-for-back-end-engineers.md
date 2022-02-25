---
authors:
- Christina Annas
tags:
-back-end
-career-development
-front-end
-javascript
date: 2022-02-24T22:00:00.000Z
title: "Hey, This Looks Familiar: Front-End Development for Back-End Engineers"
---

Hey, welcome back! We learned a _ton_ in the last post, and this one will be a bit more of an easy ride for those of us coming to the front-end from a strong foundation in the back-end. As a reminder, there is an accompanying [GitHub repository](https://github.com/christinaannas/weather-at-the-office) to follow along with this series. When we left off, we had created a custom element and incorporated it into our overall page, but we could still only use the component to show outdated information about weather in Richmond. Let's fix that!

## Component State

The first concept we'll approach in this post is component state, which is more like the state in "state machine" than it is like the state in "the Commonwealth of Virginia." Well. You get my point. 

Sometimes, we want to store data in a component. In our case, we'd like to store the location, temperature, weather conditions, and time of last update in the component and then use that data to generate the inner HTML. Our eventual goal in doing this is to use our component to represent offices in cities other than Richmond. Let's start just by extracting our current values into an object. 

``` js
constructor() {
  var that = super();
  that.model = {
    location: "Richmond, Virginia",
    tempFahrenheit: 36,
    weatherCondition: "clear",
    lastUpdated: "2022-01-31 20:30"
  };
  // ...
}

getInnerHTML() {
  return `
<h3>In ${this.model.location},<br/>it is currently ${this.model.tempFahrenheit}&deg;F and ${this.model.weatherCondition}.</h3>
<p>Last updated: ${this.model.lastUpdated}</p>
  `;
}
```

We've used object literal notation to create a model to hold our data, and string interpolation to use that data in the rendering of our element. That's basically all the state of a component is: the data the component holds. 

## Data Binding

if we're storing data in a component with the intent of rendering it, then we want to update the view whenever that data changes. When it happens automatically, this update of the view triggered by an change of the linked data is called _data binding_. We have not explicitly added anything to our app to enable data binding between our state `model` and our innerHTML, and it is not something we get for free from `HTMLElement`. We can demonstrate this by programmatically changing the state and observing the results. Recall that the `connectedCallback` can be used to add code that we want to run when our custom element is attached to the DOM. Let's use that method to add a timeout:

``` js
connectedCallback() {
  var that = this;
  setTimeout(() => {
    that.model.location = "Nowhere, USA";
    console.log("Updated location to Nowhere!");
    console.log("Did the location name change on the webpage?");
  }, 5000);
}
```

This is our first timeout, so let's check it out. We effectively schedule an arrow function to be invoked after `5000` milliseconds. The arrow function -- these are similar to the lambda functions from Java, but remember to watch out for scope! -- updates the location in our model object, and logs some messages to the console so that we can confirm the arrow function has run. 

After refreshing the page and waiting five seconds, we do see the messages in the console but do not see the location name changed on the webpage. Because we changed the data but the view was not updated automatically, we have shown that our app does not have data binding. In order to update the view, we will need to explicitly update the innerHTML whenever we change the data in the state, as we do in the following snippet. We continue to use `that` as an alias for `this`.

``` js
constructor() {
  var that = super();
  // save divElement as part of our object 
  // so we can manipulate it outside of this method
  that.divElement = document.createElement('div');
  // ...
}

connectedCallback() {
  var that = this;
  setTimeout(() => {
    that.model.location = "Somewhere Else, USA";
    console.log("Updated location to Somewhere Else!");
    that.divElement.innerHTML = that.getInnerHTML();
    console.log("Explicitly set innerHTML of the div element.");
    console.log("Did the location name change on the webpage?");
  }, 3000);
}
```

With this change made, we see that the location does update to "Somewhere Else, USA" in the output view. As it turns out, we don't need this component to update its own data right now. So while there are ways to implement data binding within a component, we won't consider them here. Instead, we'll move on to making our weather card components customizable, so they are able to represent the weather at Ippon's other offices.

## Attribute Binding

We know that we will want to pass the information to populate our model to the component from outside. Let's take a look at what that might look like. The information has to come from somewhere, and right now our only real option is from our `index.html` file. To refresh our memory, here's the `div` we currently have representing the weather card for Washington, DC.

``` html
<div class="weather-card purple">
  <h3>In Washington, District of Columbia,<br/>it is currently 30&deg;F and partly cloudy.</h3>
  <p>Last updated: 2022-01-31 20:30</p>
</div>
```

We know we'll need to use the tags for our custom element, `<weather-card-component></weather-card-component>`, somehow incorporating the additional information into or around the tag. The title of this section gives away that we'll eventually bind data to attributes of the element. (Recall that `class` on the `div` element in our HTML snippet is an example of an attribute.) Before we get to the details of attribute binding, I want to briefly present an alternative we won't use in this example and explain why I chose not to use it in this case.

### Templates and Slots

One way to approach inserting information into a custom element is by using `templates` and `slots`. Essentially, you define a template in HTML that is the common bits of the inner HTML you want to end up with, and provide slots within that template to insert more HTML. When we think of this from the DOM perspective, the template has some nodes defined and some nodes awaiting content - and that content can be a little "tree" in itself. This approach is useful when you want to create a custom element with a flexible or complex structure. 
 
 The structure of our weather card is simple and rigid; we will always want a `div` element containing only an `h3` element and a `p` element. While we could pass our information as pieces of HTML, we have no need to do so. Our information is better represented as strings, and we can easily pass strings into our element as attributes.

 ### Attribute Binding, For Real This Time




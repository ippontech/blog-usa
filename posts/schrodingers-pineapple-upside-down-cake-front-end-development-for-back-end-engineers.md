---
authors:
- Christina Annas
tags:
-back-end
-career-development
-front-end
-javascript
date: 2022-02-28T22:00:00.000Z
title: "Schrödinger's Pineapple Upside Down Cake: Front-End Development for Back-End Engineers"
---

Hey, good to see you again! Let's start by reviewing where we left off. We have a webpage that displays weather information for each of the Ippon offices in the US. However, although our page is much more dynamic than it started out, we are still displaying out-of-date information. 

```
todo: add in from-last-time screenshot here.
```

It's the moment we've all been waiting for -- let's use current data! If you're coming to front-end development from a back-end perspective, like I did, you may already know that we'll be fetching the current weather data by making an HTTP request to an API endpoint. As a reminder, there is an accompanying [GitHub repository](https://github.com/christinaannas/weather-at-the-office) to follow along with this series. 

## Our API

We'll be getting our data from a API at `http://api.weatherapi.com`, which requires an free API key. Instructions for getting your own API key are in the README in the GitHub repo we've been using. However, so that you don't have to request one in order to follow along, we'll use a straightforward little Node.js server as a bit of middleware between our front end and that API. Our server looks for a `WEATHER_API_KEY` environment variable and will use it to call the API if present, but otherwise will pull data from some cached responses for each of our four cities. It also tries the cached data in case the API call fails. I've copied a few details in the listing below, but left some bits out so we can focus on a couple of aspects at play.

``` js weather-server.js
const requestListener = async function(req, res) {
  const apiKey = process.env.WEATHER_API_KEY;
  const queryString = url.parse(req.url, true).query.q;
  var body;
  if (apiKey) {
    body = await useApi(queryString, apiKey);
  } 
  if (!body) {
    body = useCache(queryString);
  }
  // build the response with body and headers
}

async function useApi(queryString, apiKey) {
  const baseUrl = 'http://api.weatherapi.com/v1/';
  const endpoint = `${baseUrl}current.json?key=${apiKey}&q=${queryString}`;
  var weatherApiResponse;
  try {
    weatherApiResponse = await axios.get(endpoint);
  } catch (error) {
    console.log(`Error hitting ${endpoint} -- ${error}`);
    return;
  }
  const data = await weatherApiResponse.data;
  return data;
}

function useCache(queryString) {
  // details on GitHub
}

const server = http.createServer(requestListener);
server.listen(5000);
```

The most interesting bit of this is within our `useApi` function. Notice that it is marked with the `async` keyword, which means that it implicitly returns a Promise. Promises are another front-end concept that perplexed me at first. Let's take a look at Promises now, and we'll return to that "implicit" bit in a moment.

### Promises

Promises are a bit like Schrödinger's cat. I'm hungry, though, so let's call it Schrödinger's pineapple-upside-down-cake. Suppose I have plans to see my friend Sarah tomorrow, I ask her to bring some dessert for us to share, and she agrees. At this point I've already gotten _something_ from my friend, but it certainly isn't dessert. It's more like a "promise" of dessert, right? The social contract between us allows me to expect that either I'll see my ~~cake~~ friend tomorrow _or_ I'll at least get a text from her saying she can't make it (or the grocery store was out of pineapple chunks, or whatever). 

So right now, I have a "promise" that may or may not turn into cake, but I know that at some point in the near future I'll hear back about the result, favorably or not. In Promise language, we call the favorable outcome "resolved," while the unfavorable outcome is called "rejected". While my Schrödinger's box is still closed, I have to prepare as if the cake is both there and not there. Naturally, I'll want to do different things based on the result -- eat cake, or not. With Promises, we can specify the behavior we want in either case.

We can explicitly pass that behavior to a Promise object using callbacks and the `then` function. This can be an elegant solution for simple cases, but it can get complicated when multiple Promises are used which depend on one another. We'll return to callbacks, but let's take a baby step first and use an abstraction JavaScript provides.

For those of us who don't necessarily want to have to think about quantum superposition in order to make an API call, the `async/await` construct allows us to write our code as if it were synchronous, throwing exceptions. So while our call to `axios.get` returns a Promise, we decide to just wait until the Promise is resolved or rejected. We handle the rejection the same way we would handle an exception being thrown in Java, with a `catch` block. Even though we've ended up with syntax that looks very familiar, it's good to know that Promises are behind it and that they can be used other ways. 

## HTTP Requests

We've gotten a sneak peak of how to make an HTTP request, so let's incorporate this into our webpage. 

First, let's set up the HTTP call to our server in a utility/service file, so that we can abstract out those details from our component code. We'll create a file `util/weather-info.service.mjs` with a function `getWeatherData` that, given a location, makes a request to our web server (running locally on port 5000) and transforms the output into an object like the ones we've been using to describe the `props` on our `WeatherCardComponent`. 

Recall from our web server that `async` functions "implicitly" return a Promise -- that is, even if the `async` function returns the string `"pineapple upside down cake"`, any user of that function actually receives a Promise (or Schrödinger's box) wrapped around that value, rather than the value itself. Here we see that we can create the Promise objects explicitly if we wish to, specifying whether the Promise is `resolve`d or `reject`ed. However, we continue to use `async` and `await` to handle the Promise that comes back from our `axios` call.

``` js
export async function getWeatherData(location) {
  if (! location) {
    return Promise.reject("No location specified.");
  }
  try {
    const response = await axios.get('http://localhost:5000/weather-api?q=' + location);
    const data = await response.data;
    return Promise.resolve({
      location: `${data.location.name}, ${data.location.region}`,
      temp: data.current.temp_f,
      condition: data.current.condition.text.toLowerCase(),
      updated: data.current.last_updated
    });
  } catch (error) {
    Promise.reject(`Invalid location queried: ${location}. ${error.message}`);
  }
}
```

We'll want to use this method from within our `IpponWeatherListComponent` to set the `props` on each of its child card components. In order for this method to be available, we'll import it in our list component file.

``` js
import { getWeatherData } from "../util/weather-info.service.mjs";
```

And we'll also add the `axios` module to our `index.html` file so the browser knows what we're talking about. (We didn't need to do this for our web server, because it runs in a Node runtime rather than the browser. We only needed to add `axios` as a dependency in our `package.json` file and use `npm` to install it, then Node knew where to find it.)

``` html
<script src="./node_modules/axios/dist/axios.min.js"></script>
```

Now we're ready to use the `getData` method within our `IpponWeatherListComponent`! For now, we'll continue using the `async`/`await` construct that feels familiar, so we'll put our changes into an `async connectedCallback` method. 

``` js
async connectedCallback() {
  for (const weatherCard of this.shadowRoot.querySelectorAll('weather-card-component')) {
    const weatherData = await getWeatherData(weatherCard.props.location)
    weatherCard.props = weatherData;
  }
}
```

And this _mostly_ works. We end up with the page looking how we expect, but if we look closely, it's obvious that the cards are being updated one at a time, and the small delays to get responses back for each request add up, since we are waiting for the first one to finish before even sending the next one. I was able to grab a screenshot of an intermediate state of the page, when one card had up-to-date weather data, but the others have the default text. 

![Page output using an asynchronous, iterative connectedCallback method - the first card has up-to-date weather data, but the others have "unknown" data.](../images/2022/02/fed4bees3-iterative-loading.png)

Because we examined Promises closely earlier, we know that in this case it is perhaps preferable to use a callback construct rather than `await`-ing in the loop. We explicitly provide a callback for the `rejected` case here, although it does nothing; we could also have simply left it out. And, because we won't be using the `await` keyboard anymore, we can move this back into a non-async function -- namely, our constructor. 

``` js
for (const officeObject of that.offices) {
  const componentElement = document.createElement('weather-card-component');
  containerElement.appendChild(componentElement);
  componentElement.props = officeObject;

  getWeatherData(componentElement.props.location).then(
    (resolved) => {
      componentElement.props = resolved;
    }, 
    (rejected) => {}
  );
}
```

We've opted to do nothing in the case of a rejected promise. That might be fine, but let's bring back one of our "default" test cases to check it out. 

``` js
that.offices = [
  {},
  // ...
]
```

Since we're not passing a `location` property to the first card, we might expect that our `getWeatherData` service method would return a rejected Promise saying that there is no location specified.

``` js
if (! location) {
  return Promise.reject("No location specified.");
}
```

![Page output with a no-information office at the top - the first card has location "Harts Location, New Hampshire" and current weather data, where we expected it to show the defaults.](../images/2022/02/fed4bees3-unexpected-location.png)

Oh, drat. One glance at our `WeatherCardComponent` shows us what we forgot: the default location is the string `"an unknown location"`, so even though we aren't passing a value from the `IpponWeatherListComponent`, there is a value in `weatherCard.props.location` that evaluates to `true` when coerced to a boolean. This can easily be fixed by setting the value to null in the constructor and adding a ternary operator in `getInnerHTML`, and in fact it might be best to follow suit with the other properties as well. 

``` js
constructor() {
  var that = super();
  that.model = {
    location: null,
    temp: null,
    condition: null,
    updated: null,
    color_class: "transparent"
  };
  // ...
}

getInnerHTML() {
    return `
<h3>In ${this.model.location ? this.model.location: "an unknown location"},<br/>it is currently ${getTemperatureString(this.model.temp)} and ${this.model.condition ? this.model.condition : "unknown conditions"}.</h3>
<p>Last updated: ${this.model.updated ? this.model.updated : "unknown"}</p>
    `;
}
```

![Page output with a no-information office at the top - the first card now properly displays our default text.](../images/2022/02/fed4bees3-fixed-unknowns.png)

It might be even better for the `IpponWeatherListComponent` to delegate getting the location and updating the properties to the `WeatherCardComponent` itself, to avoid one from relying too much on implementation details of the other. In fact, adding some functionality to our weather card component so that it can update its own data would be a good next step. I'm sure you're also itching to make our webpage respond to some kind of user interaction. Let's feed both those birds with our next scone.

## DOM Events
One feature of the DOM that we haven't spoken about yet is events. One type of event is those fired due to user interaction, perhaps by mouse or keyboard. A simple and familiar example is a button being clicked. Let's create a simple button as part of our weather card component.

Ideally, the button to update the weather data would be nicely located right next to the information about when the last update happened. So we'll just add a button tag within the same paragraph.

``` js
return `
<h3>In ${this.model.location ? this.model.location : "an unknown location"},<br/>it is currently ${getTemperatureString(this.model.temp)} and ${this.model.condition ? this.model.condition : "unknown conditions"}.</h3>
<p>Last updated: ${this.model.updated ? this.model.updated : "unknown"} <button>Update now</button></p>
`;
```

For now, let's simply log a statement to the console when the button is clicked, since we want to iterate quickly rather than waiting around for the API-returned data to be updated. 

``` js
printButtonClick() {
  console.log(`Button clicked for ${this.model.location ? this.model.location : "unknown location"}.`);
}
```

So that's the method that we'll want to call when we click the button on a card; we need to tell the button to listen for the `click` event and use this method as the callback. We can do that in our constructor. 

``` js
that.divElement = document.createElement('div');
that.updateInnerHTML();
that.updateClass();
shadowRoot.appendChild(that.divElement);

const buttonElement = that.shadowRoot.querySelector('button');
buttonElement.addEventListener('click', that.printButtonClick);
```

The button is created in `updateInnerHTML`, so we after that's done, we can grab the `button` element and add an event listener -- namely, our simple `printButtonClick` method. Looking at the page, our buttons are present; I must say they look rather nice placed where they are! Let's try them out. 

![Page output with the buttons present on each card.](../images/2022/02/fed4bees3-buttons-present.png)

If we open the console and press a few buttons, we see... nothing good. Pressing the button in the no-information card logs an `Uncaught TypeError` to the console, and pressing the button in the Richmond card does absolutely nothing. 

![Page and console output after pressing buttons on the no-information and Richmond cards.](../images/2022/02/fed4bees3-button-console-output.png)

It seems that although what we're trying to do is simple, it isn't as straightforward as we might have thought. We'll have to do some guided debugging. (Fun!) We have a couple of problems here, since our no-information and Richmond cards are both acting unexpectedly, but in different ways. We'll see that one problem has a quick fix but couldn't have been anticipated based on what we've covered so far, while the other is understandable but a bit more involved to fix. Let's start with the quick fix. 

### Execution Contexts and Function Binding

We can fix the error caused by clicking on the no-information card's button by updating our event listener ever so slightly, from `buttonElement.addEventListener('click', that.printButtonClick);` to `buttonElement.addEventListener('click', that.printButtonClick.bind(that));`. 

![Page and console output with binding in place, after pressing buttons on the no-information and Richmond cards.](../images/2022/02/fed4bees3-function-binding.png)

Okay, fine. But _why_? 

We'll start by illustrating what's going on. We can update the `printButtonClick` to first log `this` to the console, and add both versions of the function (the regular one and the one bound to `that`) as event listeners. 

![Page and console output demonstrating that `this` is the button element when not bound, whereas it is the whole card component element when bound.](../images/2022/02/fed4bees3-demonstrating-what-this-means.png)

We see that within the unbound function, `this` refers to the `button` element itself, whereas within the bound function, `this` refers to the `weather-card-component` element, which was what we wanted. 

[on YouTube](https://www.youtube.com/watch?v=Bv_5Zv5c-Ts&t=4552s)

### Disappearing Buttons
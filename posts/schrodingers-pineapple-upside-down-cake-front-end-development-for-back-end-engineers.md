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

Hey, good to see you again! Let's start by reviewing where we left off. We have a webpage that displays weather information for each of the Ippon offices in the US. However, although our page is much more dynamic than it started out, we are still displaying out-of-date information. It's the moment we've all been waiting for -- let's use current data! If you're coming to front-end development from a back-end perspective, like I did, you may already know that we'll be fetching the current weather data by making an HTTP request to an API endpoint. As a reminder, there is an accompanying [GitHub repository](https://github.com/christinaannas/weather-at-the-office) to follow along with this series. 

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

That looks pretty nice! A good next step would be to add some functionality to our weather card component so that it can update its own data, independent of its parent component. I'm sure you're also itching to make our webpage respond to some kind of user interaction. Let's feed both those birds with our next scone.

## DOM Events
One feature of the DOM that we haven't spoken about in depth yet is events. One type of event is those fired due to user interaction, perhaps by mouse or keyboard. One simple and familiar example is a button being clicked. Let's create a simple button as part of our weather card component. 
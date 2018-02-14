---
authors:
- Justin Risch
categories:
- Culture
date: 2016-06-28T16:26:00.000Z
title: "Pokemon Go and Hipchat: A 5 minute tutorial on building a Java -based Hipchat plugin."
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/pokemon-go--1-1.jpg
---

### Hipchat API

While [Atlassian ](https://www.atlassian.com/) offers great tutorials and quick set ups for making a plugin, I found it personally difficult to find all the documents specifically related to making a Java based plugin. Much of the documentation is either not related to Java, or requires you to know the basics before you even get started. In essence, you have to know how to make an add-on before you know how to make a Java one. This tutorial aims to fix that.

As a secondary objective; let’s have a little fun. This plugin will integrate with Pokemon Go’s (Unofficial) API and notify us when a pokemon is in catching distance of our office!

First up, let’s make a suitable Maven Project in Eclipse (or your favorite IDE). We’ll need to set up our POM as well. If you’re not interested in the Pokemon Go aspect, you’ll only need the first dependency:

Be sure to run Maven Install, then go ahead and make a main class that will act as our launcher– It will look something like this:


==Your launcher==
```language-java
private static HipChat hc;
private static RoomsAPI rooms;

public static void main(String[] args) {
    hc = new HipChat(HipchatProp.getApiKey(), HipchatProp.getBaseUrl());
    rooms = hc.roomsAPI();
}
```

In the above, I’m using a simple POJO that contains my API Key, our Base URL, and the Room Name that we’re interested in. That keeps my info safe from being posted on this blog, and makes it easier for you to tell which value I’m using!

Now to get these values, we sign into [Hipchat](www.hipchat.com).

Click “Group Admin”  
![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/VbpCTnj.jpg)
  
 Click “Integrations”  
![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/xVgknzE.jpg)

 Choose the room you want to install it on in the dropdown menu (mine is “PokePop”)  
![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/LYnFVIR.jpg)

 Choose “Build your own integration”, then give it a name and submit.  
![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/GQ5tEVP.jpg)

Look for this field on the next page. The URL (and the black parts) should be your “baseURL”, as referenced in my Main Method. This basically says where to send the request. At the end of this URL is the line auth_token=YOURAPIKEY. Copy the URL (without the API key) as your Base URL, and copy the API key into the appropriate spot in the main method.  
![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/YEitFJ4.jpg)

Congrats! Now you can send a message by simply using this method:

```language-java
public static void sendMessageToHipchat(String message) {
  try {
    SendMessage sendMessage = new SendMessage(HipchatProp.getRoomName(), message);
    rooms.sendRoomMessage(sendMessage);
  } catch (Exception e) {
    e.printStackTrace();
  }
}
```

### Pokemon GO API

But what about the Pokemon? Firstly, I like how you think. Secondly: We’re going to need a Pokemon Trainer Club (PTC) login– I don’t recommend using your actual account as this may be bannable at some point. Go ahead and [register one](https://club.pokemon.com/us/pokemon-trainer-club/sign-up/). You may want to sign into the game first to make sure it works, and to finish creating your Pokemon Go Account.

Then, head to google maps, and click on the map where you want the add-on to be “active”. This is your player position. The latitude and longitude can be found below. (37.5…, -77.4…)

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/bEEG7Fb.jpg)

Once you have all this info, go ahead and implement this method and member:


```language-java
static PokemonGo go;
public static boolean login(PokeProp prop) {
  try {
    OkHttpClient httpClient = new OkHttpClient();
    AuthInfo auth = new PtcLogin(httpClient).login(prop.getUsername(), prop.getPassword());
    go = new PokemonGo(auth, httpClient);
    return true;
  } catch (Exception e) {
    e.printStackTrace();
    return false;
  }
}
```

All it will do is, given a username and password, attempt to log in, and return “true” iff successful. In your main method, be sure to set the latitude and longitude that you want to use like so:


```language-java
go.setLatitude(prop.getLat());
go.setLongitude(prop.getLng());
```

Next: let’s build a Watcher class. All it will do is contain the behaviors related to finding nearby pokemon.


```language-java
public class WatchForPokemon {
static List reportedAlready = new ArrayList();

private static void notifyHipchat(CatchablePokemon pokemon) {
  try { 
    String message = pokemon.getPokemonId().name() + " IS NEARBY!";   
    System.out.println(message);
    PokePop.sendMessageToHipchat(message);
    reportedAlready.add(pokemon);
  } catch (Exception e) {
    e.printStackTrace();
  }
}

private static boolean haventNotifiedYet(CatchablePokemon pokemon) {
  return !reportedAlready.contains(pokemon);
}

public static void lookForPokemon() {
  try {
    PokePop.go.getMap().getCatchablePokemon().stream() .filter(WatchForPokemon::haventNotifiedYet).forEach(WatchForPokemon::notifyHipchat);
  } catch (Exception e) {
    e.printStackTrace();
  }
}
```

And add to your main class:

```language-java

while (true) {
  WatchForPokemon.lookForPokemon();
  Thread.sleep(10000);
}
```
And voila! Every 10 seconds, your hipchat room will be notified of pokemon in catching distance of those coordinates, but will also only notify of each pokemon once.

If you have any trouble, check out the repo for PokePop at [the PokePop Repo](https://github.com/JustinRisch/Pokepop). It also contains several other features on demo, like ignoring certain very common pokemon.

---
authors:
- Christina Annas
tags:
- back-end
- career-development
- front-end
- javascript
date: 2022-02-23T12:21:50.000Z
title: "The Documents Are In The Computer: Front-End Development for Back-End Engineers"
image: 
---

I'm a back-end Java engineer, mostly, but I've been breaking out of that. Historically, whenever I've tried to sift through front-end code, I felt like there was something I was missing. Several somethings. About as many somethings as Owen Wilson and Ben Stiller were missing when they tried to break open a Mac with some animal's thigh bone to get to the documents "in the computer." But I digress.

I think part of my struggle is that the way that I learn is concept-oriented -- before I get into syntax, I like to understand what I'm trying to do. Because of this, I've never really been someone who can hear a problem and immediately start typing in their IDE. I generally need to spend some time at the whiteboard first, identifying the core pieces about the problem and reasoning about them. 

I also came to programming from a different direction compared to many of my peers. While many of my classmates were creating themes for their Xanga sites with HTML and CSS, I remember being _super_ intimidated. I was a kid and I really didn't need a website, anyway, so I gave up and chalked up programming as something that wasn't for me. In college, though, I encountered opportunities to use a computer in the context of domains I was already interested in -- agent-based modeling of population growth, and "Why bother to do the integral when Mathematica can do it for me?", and so on. The problems I was trying to solve normally didn't include a graphical user interface, and never required me to create a web-based front-end. 

All of that to say: I'm writing this blog post as a way of summing up what I've learned about the "somethings" of front-end development, including naming and defining some of the concepts I had no idea existed. I'll also give some examples of what the concepts look like applied in a simple application we'll build along the way. 

# Setting the Stage

Let's start by briefly getting a feel for the environment we're entering. For more in-depth descriptions of all things web development, I highly recommend [MDN Web Docs](https://developer.mozilla.org/en-US/docs/Web). 

## Content: HTML

The main structure of a web page is written in HTML (HyperText Markup Language). The HTML holds the page content. If you've ever visited a website that had a bunch of black Times New Roman text on a white background, with maybe some bigger text or bolded text or links, that's basically what HTML is bringing to the table. Let's look at a quick sample.

``` html
<!DOCTYPE html>
<html>
  <h3 class="lorem">Hello World</h3>
  <p id="ipsum">Some more text here</p>
</html>
```

In this example, we have a heading element, opened and closed with `h3` tags, with some text enclosed in it and a `class` attribute with value `"lorem"` applied to the opening tag, as well as a paragraph `p` element. Notice that in the rendered output below, there's nothing about the attributes -- no `class` or `id` or `lorem` or `ipsum` -- we see only the text enclosed in each element, with the heading element having a big, bold style that HTML gives us for free.

![Classic HTML rendering with no stylistic changes.](https://github.com/christinaannas/blog-usa/blob/master/images/2022/02/fed4bees1-plain-html.png)

```
Todo: Insert image of output here -- alt text: Classic HTML rendering with no stylistic changes.
```

Although they don't show up directly in the output, attributes are useful ways of providing extra information to or about an element. Elements' tags, classes, and ids may be used in CSS selectors, which we'll talk about in the next section. 
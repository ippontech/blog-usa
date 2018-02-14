---
authors:
- Alexis Seigneurin
categories:
- Culture
date: 2016-10-04T17:28:49.000Z
title: "DevFest DC Washington DC"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/A-Very-Potter-Door-to-the-Future-Yufeng-Guo-Google-1024x730-1-3.jpg
---

A team of five Ipponites hungry for knowledge descended upon a bustling Capital One for DevFest DC 2016 – A two day Google Developer Group Washington DC Event consisting of over 600 attendees. There were three educational tracks to talk Cloud, Mobile, Big Data, & Design featuring speakers from domestic and abroad as well as Ippon’s own USA Hipster – Dennis Sharpe. Here’s our take of what was hot at the biggest Developer Tech conference in the Washington Metro area!


## A Very Potter Door to the Future – Yufeng Guo – Google

Yufeng started with the idea that, in Harry Potter, doors are paintings that can recognize people and ask for passwords. He wanted to reproduce the same principle with a Raspberry Pi and a few accessories, among which a webcam and a microphone. He showed us how he leveraged the Google Cloud APIs to perform speech and face recognition.

[![A Very Potter Door to the Future - Yufeng Guo - Google](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/A-Very-Potter-Door-to-the-Future-Yufeng-Guo-Google-1024x730.jpg)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/A-Very-Potter-Door-to-the-Future-Yufeng-Guo-Google.jpg)

Yufeng first showed us how he integrated the [Google Speech API](https://cloud.google.com/speech/) in his application. This API can recognize over 80 languages and can be enhanced by providing some context. In his demo, he gave “DC DevFest” as a context to help the API detect appropriate words.

He then showed how powerful the [Google Vision API](https://cloud.google.com/vision/) is. It can detect objects, faces, text, and much more from an image.

These APIs are powered by Machine Learning algorithms that are constantly improved by Google. Free tiers (60 minutes/month for the Speech API and 1000 images per month for the Vision API) make it easy to build a Proof of Concept application.


## TensorFlow at the Museum – Josh Gordon – Google

In this talk, Josh showed how he trained a Deep Neural Network and used it to identify paintings in a museum on his smartphone. This is all made very easy thanks to the open source library [TensorFlow](https://www.tensorflow.org/).

[![TensorFlow at the Museum - Josh Gordon - Google](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/TensorFlow-at-the-Museum-Josh-Gordon-Google-300x208.jpg)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/TensorFlow-at-the-Museum-Josh-Gordon-Google.jpg)

Through his talk, Josh demonstrated powerful features of the library (including image categorization) and gave an overview of the high-level Python API. Thanks to the portability of the library, one can train a model on a cluster of servers in the cloud and then use the model on a smartphone, all of this without requiring extensive knowledge of neural networks.

Josh finally gave many pointers to help people get started with TensorFlow. Here are a few:

- [TensorFlow for Poets](https://codelabs.developers.google.com/codelabs/tensorflow-for-poets/index.html), a codelab
- [Machine Learning Recipes](https://www.youtube.com/playlist?list=PLOU2XLYxmsIIuiBfYad6rFYQU_jL2ryal), a series of introductory videos on YouTube
- [Deep Learning by Google](https://www.udacity.com/course/deep-learning--ud730), a Udacity course


## Visualization Data in Elasticsearch – Dave Erickson – Elastic

In this panel, Dave Erickson Director of Solutions Architecture, demonstrated many of the features available within Elastic’s big data search and analytic tools. In particular, he focused in on [Elasticsearch](https://www.elastic.co/products/elasticsearch) and [Kibana](https://www.elastic.co/products/kibana). The [Elastic Stack](https://www.elastic.co/products) consists of several tools that are designed to “help you take data from any source, any format, and search, analyze, and visualize it in real time.” Elastic Search is core to the Elastic Stack and is designed for high compatibility by utilizing a RESTful API.

[![the-elastic-stack-thumb](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/the-elastic-stack-thumb-300x175.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/the-elastic-stack-thumb.png)

After giving a brief history of Elastic and the various products available, Dave went on to discuss the importance of visualization. Data by itself is difficult to process by humans even with the help of search and analytic tools like Elasticsearch. Kibana allows for a more efficient way for humans to digest and process the data.

In his talk Dave provided a demo of Kibana using the DC cycle share data from the first year the program started. He zeroed in on July 4th. Kibana displayed a simple bar graph that showed a drastic decline in activity right around 9:00 pm and a sharp increase in activity shortly thereafter. Dave explained that this anomaly may very likely be something that machine learning couldn’t identify. However, having a visual representation of the data allowed us to determine that the dramatic change in bike share usage was due to fireworks celebrating Independance Day.  Dave went on to discuss how Kibana can add value to your big data projects and can be particularly attractive to clients or non-technical members of a team who may need to interact with data.

[![visualizing-data-in-elasticsearch-devfest-dc-2016-23-1024](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/visualizing-data-in-elasticsearch-devfest-dc-2016-23-1024-300x169.jpg)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/visualizing-data-in-elasticsearch-devfest-dc-2016-23-1024.jpg)


## Implementing HTTP/2 Server with Node and Express – Azat Mardan – Capital One

The Web has utilized HTTP 1 for more than fifteen years. However, it is beginning to present more and more issues to web developers. Azat used this talk as an opportunity to discuss the change to HTTP/2 and how it will affect development when using NodeJS and ExpressJS. He first assured us that HTTP/2 is in fact here and that all the major web browsers are already supporting it!

[![h2-is-here](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/h2-is-here-300x73.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/h2-is-here.png)

Azat then highlighted some of the benefits of HTTP/2:

- **Multiplexing** – several requests can be sent in rapid succession on the same TCP connection.
- **Server Push** – push web assets (CSS, JS, images) before a browser knows it needs them which speeds up page load times by reducing number of requests.
- **Stream Priority** – allows browsers to specify priority of assets. For example, browser can request HTML first to render it before any styles or JavaScript.
- **Header Compression** – all HTTP/1.1 requests have to have headers which are typically duplicate the same info, while HTTP/2 forces all HTTP headers to be sent in a compressed format.

He finished up his presentation by taking the audience through a short demo on setting up and starting a node server. Azat used the *spdy* module to create a quick and easy Express web application.

[![spdy install](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/spdy-install-300x56.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/spdy-install.png)

After some additional setup, he concluded by pulling up the application on the local host in a web browser and using the browsers developers tools to demonstrate the use of HTTP/2.

[![http2 developer tools](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/http2-developer-tools-300x185.png)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/http2-developer-tools.png)


## Keynote: Pivotal Time to be a Developer: A Global Perspective – Yuriko Horvath, 1776

Yuriko gave a brief walk through tech history. One point she made was increasingly better developer working conditions over time. I personally see this as having been working for many years, the opportunity to have flexible working arrangements has only increased. I’ve found that many companies are starting to value productivity over facetime which is a win for everybody. Some more interesting points:

- Work from home (WFH) opportunities increase over time
- 36% of developers choose WFH vs getting a raise (this statistic is very interesting to me–employers take note!)
- Teleworkers seem to be more productive
- Businesses can save on real estate cost
- Collaboration tools like Slack, HipChat, and Google Docs can help make things easier for distributed teams. Here, at Ippon, we use HipChat and it’s crucial for all of us to keep in touch with each other when we have many people working on different projects at different companies, thus making facetime difficult


## gRPC 101 – building small and efficient microservices – Ray Tsang – Google

This was my favorite technical talk of the day. Google is bringing back RPC from the dead! Remember fun technologies like CORBA, RMI/IIOP, and SOAP? Just to refresh your memory, here’s a “Hello World” for CORBA: [Tutorial for CORBA Hello World using Java](http://www.ejbtutorial.com/programming/tutorial-for-corba-hello-world-using-java). Google has had many years of experience with distributed systems and this is their take on it.

gRPC is Google’s implementation. It’s designed to be low latency, bandwidth and CPU efficient for today’s modern applications. The protocol it uses is binary and supports many different languages. How many of you have struggled with using the wrong protocol for the right job? I’m looking at you RPC over JMS…

The gRPC tech stack includes:

- Interface Definition Language (IDL): [protocol buffers](https://developers.google.com/protocol-buffers/)
- HTTP/2 (Transport). Supports multiplexed connections (no overhead due to open/close connections), bidirectional streaming: [https://http2.github.io/faq/](https://http2.github.io/faq/)
- Protobuffer 3: payload and encoding

[![ray](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/ray-300x206.jpg)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/ray.jpg)

Ray then did a live coding demo where he progressively built a chat server and client from scratch. It was quite impressive due to the lack of ceremony involved in wrangling with the protocol as compared to how I’d envision doing it with something like REST. gRPC is absolutely in play for my next greenfield project. Check it out at: [http://www.grpc.io/](http://www.grpc.io/). The getting started guide is one of the better ones I’ve seen out there. Give it a shot!


## Say it With Video – Annyce Davis, Off Grid Electric

This was one of my favorite talks of the day. Annyce creates technical courses. First, this is a great way of making some extra money using skills you already have. Second, it’s a great way to build your brand as a developer. More detailed information about the full talk [is here](http://www.adavis.info/2016/06/talk-say-it-with-video.html).

[![annyce](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/annyce-300x164.jpg)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/10/annyce.jpg)

Here are some key takeaways I got from the talk:

- You can get audio foam from Amazon (see picture above). I never thought to do this when I’m even in the office to deal with echo and to ensure you sound clearer.
- She pays great attention to detail in her production process. There are tools she uses to record separate audio and video (to let you record what you’re doing and go back and annotate it later) and ripple effects to show where you click on the screen. Also using cursor highlighting and showing key commands displayed on the screen can help users glean information that is not immediately obvious.
- Similar to the point above, when recording, speed up your code insertion/typing so that the user doesn’t have to sit through that.
- You can market your video with hashtags (like #AndroidDev) on Twitter, Facebook, and other social media.
- Use Buffer to schedule tweets about your upcoming content.

Annyce boiled it down to Preparation, Execution, and Marketing. This is a great rule of thumb when creating tutorial content in mind. In addition, if you click on the presentation link above, you’ll notice that she has other related material to her talk: slides and a blog post. There are plenty of times where I’m not able to watch a video so having a blog post to walk through would be more suitable for me.

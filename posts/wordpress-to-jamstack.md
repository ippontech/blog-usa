---
authors:
  - Brandon Landfried
tags:
  - Javascript
  - NextJS
  - React
  - JAMstack
date: 2021-04-06T00:00:00.000Z
title: "Wordpress to JAMstack"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2021/01/stargate-main.png
---

I was approached a few months into the pandemic by my parents church asking if I would be able to update their website to make it modern. With everyone in lockdown and having restrictions on gatherings the church had started to rely more on livestreams and posting their sermons on the website. This means their website was getting more traffic and it was being brought up by members that it was not a very good experience.

In this blog post I will walk through the steps I took and some of my decision making on converting the outdated [Wordpress](https://wordpress.com/) website to a [JAMstack](https://jamstack.org/) powered website that is modern, responsive, and best of all free!

Since this was something I was doing as a favor I took the opportunity to make it a learning experience and use some services and frameworks that were newer to me and have fun with it.

# Technologies Used

- [NextJS](https://nextjs.org/)
- [TinaCMS](https://tina.io/)
- [Vercel](https://vercel.com/)
- [DataStax Astra](https://www.datastax.com/)
- [SendGrid](https://sendgrid.com/)
- [EasyCron](https://www.easycron.com/)

# Where To Start?

I started going through the exisiting site and marking down all the pages and what content lived on each page. It became apparent pretty early on that there was a lot of duplicate content and that I could trim down the number of pages. One I determined what pages I would use I ended up creating my NextJS app with TinaCMS using their [guide](https://tina.io/guides/nextjs/github/initial-setup/) to get a basic framework setup. TinaCMS allows for real-time editing of your site and gives you the ability to use Markdown or JSON to drive the static content on your pages. The static content for your site is then stored in Github and whenever you make a change and publish it is just a commit to your repo. TinaCMS allows for users to create branches from within the browser so whenever they make changes it can go to a branch and get reviewed before being published. I went with NexJS and TinaCMS instead of a traditional CMS is because I wanted full control of the code and to be able to use more modern frameworks.

In NextJS it gives you the ability to fetch data for static generation using `getStaticProps`. This is where you define the static content that you want your page to use and what the TinaCMS form will use.

```javascript
export const getStaticProps = async ({ preview, previewData }) => {
  if (preview) {
    return getGithubPreviewProps({
      ...previewData,
      fileRelativePath: "content/im-new.json",
      parse: parseJson,
    });
  }
  return {
    props: {
      sourceProvider: null,
      error: null,
      preview: false,
      file: {
        fileRelativePath: "content/im-new.json",
        data: (await import("../content/im-new.json")).default,
      },
    },
  };
};
```

This then allows you to use `file.data` in your component along with the TinaCMS form Reqact hook to link your static content with the TinaCMS editor which looks like the image below.

![TinaCMS on website](https://raw.githubusercontent.com/blandfried/blog-usa/master/images/2021/04/tinacms.png)

# Automating Sermon Videos

On the Wordpress site they had to manually update the sermon video section every Monday and I decided that this could be an automated task so they would not have to worry about it. I ended up accomplishing this automation by using the serverless function capabilities that come with using NextJS and hosting it using Vercel. I wrote a Node JS function that calls the Youtube API to retrieve the five latest uploads. To save on api call quotas I made the decision to store the response data in a database since new sermons are uploaded on Sundays and do not change for a week. I figured there was no need to call the Youtube api anytime someone visited the site.

I wanted to learn about Cassandra and document style databases so I signed up for the new [DataStax Astra Serveless](https://www.datastax.com/blog/2021/03/astra-serverless-heres-what-you-should-know) database. This is probably overkill and could have gone with something simpler but I wanted to learn from this experience so I chose something I was not familiar with. With this approach when someone visits the website I make a call to the database and retrieve the video data from there instead of Youtube API.

## NodeJS API

```javascript
const { createClient } = require("@astrajs/collections");

export default async (_req, res) => {
  // create an Astra client
  const astraClient = await createClient({
    astraDatabaseId: process.env.ASTRA_DB_ID,
    astraDatabaseRegion: process.env.ASTRA_DB_REGION,
    applicationToken: process.env.ASTRA_DB_APPLICATION_TOKEN,
  });
  const videoCollection = astraClient
    .namespace(process.env.ASTRA_DB_KEYSPACE)
    .collection(process.env.VIDEOS_COLLECTION);
  const vids = await videoCollection.find({});

  res.status(200);
  res.send(JSON.stringify(vids));
};
```

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

I was approached a few months into the pandemic by my parents church asking if I would be able to update their website to make it modern. With everyone in lock down and having restrictions on gatherings the church had started to rely more on live streams and posting their sermons on the website. This means their website was getting more traffic and it was being brought up by members that it was not a very good experience.

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

I started going through the existing site and marking down all the pages and what content lived on each page. It became apparent pretty early on that there was a lot of duplicate content and that I could trim down the number of pages. One I determined what pages I would use I ended up creating my NextJS app with TinaCMS using their [guide](https://tina.io/guides/nextjs/github/initial-setup/) to get a basic framework setup. TinaCMS allows for real-time editing of your site and gives you the ability to use Markdown or JSON to drive the static content on your pages. The static content for your site is then stored in Github and whenever you make a change and publish it is just a commit to your repo. TinaCMS allows for users to create branches from within the browser so whenever they make changes it can go to a branch and get reviewed before being published. I went with NexJS and TinaCMS instead of a traditional CMS is because I wanted full control of the code and to be able to use more modern frameworks.

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

This then allows you to use `file.data` in your component along with the TinaCMS form React hook to link your static content with the TinaCMS editor which looks like the image below.

![TinaCMS on website](https://raw.githubusercontent.com/blandfried/blog-usa/master/images/2021/04/tinacms.png)

# Automating Sermon Videos

On the Wordpress site they had to manually update the sermon video section every Monday and I decided that this could be an automated task so they would not have to worry about it. I ended up accomplishing this automation by using the serverless function capabilities that come with using NextJS and hosting it using Vercel. I wrote a Node JS function that calls the YouTube API to retrieve the five latest uploads. To save on api call quotas I made the decision to store the response data in a database since new sermons are uploaded on Sundays and do not change for a week. I figured there was no need to call the YouTube api anytime someone visited the site.

I setup a cron job using [EasyCron](https://www.easycron.com/) that runs on Sunday mornings when the sermons begin and this calls the YouTube API that I created to fetch the latest videos. Since they have a live stream happening I display the live stream on the website so members do not have to go to YouTube to view the sermons and can watch on the website directly.

I originally went with [Github Actions](https://docs.github.com/en/actions/reference/events-that-trigger-workflows#schedule) but I found it to be very unreliable with running at the correct time so I was finding myself on Sundays making the calls manually until I found EasyCron. They have an integration with Vercel that made it easy to hook up with my site.

# Database

I wanted to learn about Cassandra and document style databases so I signed up for the new [DataStax Astra Serveless](https://www.datastax.com/blog/2021/03/astra-serverless-heres-what-you-should-know) database. This is probably overkill and could have gone with something simpler but I wanted to learn from this experience so I chose something I was not familiar with. With this approach when someone visits the website I make a call to the database and retrieve the video data from there instead of YouTube API.

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

With the new serverless database that they offer it allows me to have a database for the Church's website and costs nothing. DataStax gives you $25 monthly credit and since the storage and read/writes are so low right now that the monthly credit covers it.

# Contact Us

On the old site their contact us form was no longer working and they wanted to have that feature back on their website. I ended up creating a simple form and adding a Google Captcha to it to help cutdown on potential spam they could get. The only thing I was unsure of was how to actually send the form responses. If this had been hosted on a traditional hosting platform I would have just used PHP to send the responses. I have a template for this on my [Github](https://github.com/blandfried/phpMailForm) that I put together a few years ago that might need some updates but it did the job.

Since I am using serverless functions and NodeJS I had to take a different approach. I ended up signing up for an account on SendGrid because they have a very generous free tier and using [nodemailer](https://nodemailer.com/about/). Once I signed up on SendGrid and got the smtp settings it was pretty straightforward. The contact form calls the api I created and then that creates the email based on the template I defined and sends it to the Church's inbox.

## Mail API

```javascript
const nodemailer = require("nodemailer");

// async..await is not allowed in global scope, must use a wrapper
export default async (_req, res) => {
  const req = _req.body;

  // create reusable transporter object using the default SMTP transport
  const transporter = nodemailer.createTransport({
    host: "smtp.sendgrid.net",
    port: 587,
    secure: false, // true for 465, false for other ports
    auth: {
      user: "apikey",
      pass: process.env.SMTP_API,
    },
  });

  // send mail with defined transport object
  const info = await transporter.sendMail({
    from: process.env.SMTP_FROM, // sender address
    to: process.env.SMTP_TO, // list of receivers
    subject: req.subject, // Subject line
    html: `<p><strong>Name:</strong> ${req.name}</p><p><strong>Email:</strong> ${req.email}</p><p><strong>Message:</strong> ${req.message}</p>`, // html body
  });

  if (info.response.includes("250 Ok")) {
    res.status(200);
    res.send(info.messageId);
  } else {
    res.status(500);
    res.send("Error Sending Message");
  }
};
```

# Conclusion

Overall this was a pretty fun and sometimes challenging side project. I was able to explore some new technologies while also saving my parents church some yearly expenses by getting rid of their old Wordpress site. During all of this they also decided to do some re-branding and got a new logo for the church and color scheme. It was fun to be apart of this transformation and see everything come together. My biggest concern when doing this was not to accrue any expenses for the church if at all possible. I was able to accomplish this because most of the services I chose offer free tiers that this website fit in or offered credits like DataStax does. So instead of paying someone around $200/year to maintain their old website they will just now have to pay for their domain renewal every couple years.

## Before

![Before of the Church Website](https://raw.githubusercontent.com/blandfried/blog-usa/master/images/2021/04/wp-to-jamstack-before.png)

## After

![Before of the Church Website](https://raw.githubusercontent.com/blandfried/blog-usa/master/images/2021/04/wp-to-jamstack-after.png)

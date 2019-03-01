---
authors:
- Theo Lebrun
tags:
- AWS
- Twilio
- JHipster
date: 2019-02-21T14:50:55.000Z
title: "Build a speech-enabled application using Twilio and AWS with JHipster"
image: 
---

The popularity of smart home devices like Amazon Echo or Google Home increased a lot for the past years. All this traction improved the text to speech experience to a point where it is actually hard to tell that you are actually not talking to a real person. And this experience is not only available in English, those home devices support multiple different languages.

[Amazon Polly](https://aws.amazon.com/polly/) is a AWS service that uses advanced deep learning technologies to synthesize speech that sounds like a human voice. This service lets you transcribe any text to a high-quality natural sounding voice in a very simple way without spending outrageous amount of money. Amazon Polly’s pay-as-you-go pricing, low cost per character converted, and unlimited replays make it a cost-effective way to voice your applications. Amazon Polly is used by many companies like [Duolingo](https://aws.amazon.com/blogs/machine-learning/powering-language-learning-on-duolingo-with-amazon-polly/) or telephony companies since the speech experience is very realistic.

In this blog post, I will explain how Amazon Polly and Twilio can be used to build a speech-enabled application. The application will make phone calls and use Polly to talk with the callee. [Twilio](https://www.twilio.com/) is the leader in the Cloud communications platform world, and as a developer, I really enjoyed using their Java SDK.

# Application generation with JHipster

## Entities

# Twilio

## Twilio and Amazon Polly integration

Twilio has its own speech synthesis but it is language limited and not very realistic. A workaround is to use Amazon Polly to generate `mp3` and have the files available on S3 so they can be used by Twilio. The good news is that Twilio recently added [Amazon Polly](https://www.twilio.com/blog/2018/08/introducing-50-additional-text-to-speech-voices-with-amazon-polly-integration.html) so it can be directly used without having to manage your own files. That makes the setup pretty easy and everything can be configured in the Twilio console. You can follow the instructions from the Twilio's blog post to configure the text-to-speech provider to use Amazon Polly.

## TwiML

TwiML (the Twilio Markup Language) is a set of instructions you can use to tell Twilio what to do when making a call or sending a SMS. For now, the application will just call a number and play predefined text with a configurable voice. TwiML verbs are actions to take on a given call, cool things can be done like recording the callee's voice or collecting typed digits. Here is the [documentation page](https://www.twilio.com/docs/voice/twiml) about TwiML, you can find a list of all verbs that can be used.

The TwiML used for the application will be very simple:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Response>
    <Say voice="Polly.Joanna" language="en-US">Hello from Twilio and Amazon Polly</Say>
</Response>
```

# Front-end changes

The last step is to update the front-end and add a page that will let the user perform calls. An entity `VoiceCall` will be created each time a call is created so the app can keep track of all calls. [The Twilio Call log](https://www.twilio.com/console/voice/logs/calls) can also be used and will give you useful details like duration or status.

# Conclusion

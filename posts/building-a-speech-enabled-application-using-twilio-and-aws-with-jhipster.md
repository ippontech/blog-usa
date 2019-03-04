---
authors:
- Theo Lebrun
tags:
- AWS
- Twilio
- JHipster
date: 2019-03-04T14:50:55.000Z
title: "Building a speech-enabled application using Twilio and AWS with JHipster"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/03/twilio-logo.png
---

The popularity of smart home devices like Amazon Echo or Google Home increased a lot for the past years. All this traction improved the text to speech experience to a point where it is actually hard to tell that you are actually not talking to a real person. And this experience is not only available in English, those home devices support multiple different languages.

[Amazon Polly](https://aws.amazon.com/polly/) is a AWS service that uses advanced deep learning technologies to synthesize speech that sounds like a human voice. This service lets you transcribe any text to a high-quality natural sounding voice in a very simple way without spending outrageous amount of money. Amazon Polly’s pay-as-you-go pricing, low cost per character converted, and unlimited replays make it a cost-effective way to voice your applications. Amazon Polly is used by many companies like [Duolingo](https://aws.amazon.com/blogs/machine-learning/powering-language-learning-on-duolingo-with-amazon-polly/) or telephony companies since the speech experience is very realistic.

In this blog post, I will explain how Amazon Polly and Twilio can be used to build a speech-enabled application. The application will make phone calls and use Amazon Polly to talk with the callee in a realistic way. [Twilio](https://www.twilio.com/) is the leader in the Cloud communications platform world, and as a developer, using their Java SDK is very simple and powerful.

# Twilio

## Twilio and Amazon Polly integration

Twilio has its own speech synthesis but it is language limited and not very realistic. A workaround is to use Amazon Polly to generate `mp3` and have the files available on S3 so they can be used by Twilio. The good news is that Twilio recently added [Amazon Polly](https://www.twilio.com/blog/2018/08/introducing-50-additional-text-to-speech-voices-with-amazon-polly-integration.html) so it can be directly used without having to manage your own files. That makes the setup pretty easy and everything can be configured in the Twilio console. You can follow the instructions from the Twilio's blog post to configure the text-to-speech provider to use Amazon Polly.

## TwiML

TwiML (the Twilio Markup Language) is a set of instructions you can use to tell Twilio what to do when making a call or sending a SMS. For now, the application will just call a number and say custom text with a configurable voice. TwiML verbs are actions to take on a given call, cool things can be done like recording the callee's voice or collecting typed digits. Here is the [documentation page](https://www.twilio.com/docs/voice/twiml) about TwiML, you can find a list of all verbs that can be used.

The TwiML to say `Hello from Twilio and Amazon Polly` with the Amazon Polly `Joanna` voice looks like below:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Response>
    <Say voice="Polly.Joanna">Hello from Twilio and Amazon Polly</Say>
</Response>
```

Twilio requires a TwiML to initiate a call and the file must be accessible from a regular URL. To fulfil this requirement, the application will simply upload the generated TwiML to Amazon S3 with a public read access. A different way would be to have a simple `GET` endpoint in the application that returns the TwiML.

# Speech-enabled application

## Generation with JHipster

I decided to use [JHipster](https://www.jhipster.tech/) to generate my speech-enabled application since I want a complete and modern Web app. JHipster uses Spring Boot for the back-end and I decided to use [Vue.js](https://vuejs.org/) for the front-end. This [.yo-rc.json](https://raw.githubusercontent.com/Falydoor/jhipster-twilio-polly/master/.yo-rc.json) can be used in case you want to generate the same application referenced in this article. The file is placed in the application directory and then running `jhipster --blueprint vuejs` will generate the application without asking any questions.

## Entities generation

The entity `VoiceCall` will be used to keep track of the calls made by the application. Here is `entities.jdl` and the command `jhipster import-jdl entities.jdl` will generate everything for you.

```
entity VoiceCall {
	number String required, // Phone number to call
	message String required, // Message to be converted to speech
	voice Voice required, // Amazon Polly's voice
	twiml String, // TwiML used by Twilio
	date ZonedDateTime // Date of the call
}
enum Voice {
	JOANNA, MATTHEW, CELINE, MATHIEU // 4 voices are used but 50 are available
}
```

## Create realistic calls with Twilio

The Twilio and AWS client need to be initialized with working credentials so they can be later used:

```java
private static final String BUCKET = "MY_BUCKET";

private static final String TWILIO_NUMBER = "NUMBER";

private final S3Client s3;

public VoiceCallResource(VoiceCallRepository voiceCallRepository) {
    this.voiceCallRepository = voiceCallRepository;
    // Initialize Twilio
    Twilio.init("USERNAME", "PASSWORD");
    // Create S3 client using default credential profile
    s3 = S3Client.builder().region(Region.US_EAST_1).credentialsProvider(ProfileCredentialsProvider.create()).build();
}
```

You can get your Twilio's credentials [from the console](https://www.twilio.com/console). I used the [AWS CLI](https://aws.amazon.com/cli/) with the command `aws configure` to create a default credential profile.

The last step is to update the method `createVoiceCall` so Twilio can create a call when a `VoiceCall` is created by the user:

```java
@PostMapping("/voice-calls")
public ResponseEntity<VoiceCall> createVoiceCall(@Valid @RequestBody VoiceCall voiceCall) throws URISyntaxException {
    log.debug("REST request to save VoiceCall : {}", voiceCall);
    if (voiceCall.getId() != null) {
        throw new BadRequestAlertException("A new voiceCall cannot already have an ID", ENTITY_NAME, "idexists");
    }

    // Generate TwiML
    Say say = new Say.Builder(voiceCall.getMessage()).voice(Say.Voice.valueOf("POLLY_" + voiceCall.getVoice().name())).build();
    VoiceResponse twiml = new VoiceResponse.Builder()
        .say(say)
        .pause(new Pause.Builder().length(1).build())
        .build();

    // Update VoiceCall
    voiceCall.twiml(twiml.toXml())
        .date(ZonedDateTime.now());

    VoiceCall result = voiceCallRepository.save(voiceCall);

    // Save TwiML to S3 with public read
    PutObjectRequest putRequest = PutObjectRequest.builder()
        .bucket(BUCKET)
        .key(result.getId() + ".xml")
        .contentType("text/xml")
        .acl(ObjectCannedACL.PUBLIC_READ)
        .build();
    s3.putObject(putRequest, software.amazon.awssdk.core.sync.RequestBody.fromString(result.getTwiml()));

    // Make call using Twilio
    String twimlUri = "https://s3.amazonaws.com/" + putRequest.bucket() + "/" + putRequest.key();
    Call call = Call.creator(new PhoneNumber(voiceCall.getNumber()), new PhoneNumber(TWILIO_NUMBER), new URI(twimlUri))
        .setMethod(HttpMethod.GET)
        .create();
    log.debug("CALL SID : {}", call.getSid());

    return ResponseEntity.created(new URI("/api/voice-calls/" + result.getId()))
        .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
        .body(result);
}
```

Here is the workflow:
1. Generate a TwiML with user's message and voice
2. Update VoiceCall with TwiML and current date
3. Save TwiML to S3
4. Call user's number with Twilio using the TwiML from S3
5. Return created VoiceCall with a HTTP 201

To keep this post simple, I decided to upload the TwiML to S3 with a public read access. I recommend using a [presigned URL](https://docs.aws.amazon.com/AmazonS3/latest/dev/ShareObjectPreSignedURL.html), especially for an application going to production.

## Testing

To test that everything is working fine, the application must be first started with the command `./mvnw`. You can then browse to [http://localhost:8080/#/entity/voice-call/new](http://localhost:8080/#/entity/voice-call/new) to create a VoiceCall (don't forget the `+1` in front of your number).

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/03/twilio.png)

If you don't receive the call after saving, the [Twilio's calls log](https://www.twilio.com/console/voice/calls/logs) keeps track of all the calls and can be used for troubleshooting.

# Conclusion

Building a speech-enabled application might sound difficult but with the help of Twilio and AWS it is actually a very easy duty. The developer experience is also very good since Twilio provides a SDK in 6 languages (Node, C#, PHP, Ruby, Python and Java) but they also provide a [REST API](https://www.twilio.com/docs/usage/api). Amazon Polly gives you a large panel of realistic voices to make your application fully international by supporting multiple languages.

The code used in this blog post is available at [this GitHub repository](https://github.com/Falydoor/jhipster-twilio-polly) in case you want to clone a working example.

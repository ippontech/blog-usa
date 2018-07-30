---
authors:
- Ben Edridge
tags:
- AWS
- IoT
date: 2018-07-19T11:41:57.000Z
title: "Exploring AWS IoT Core and Greengrass Offerings (Part 2)"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/07/aws_iot_esp_device.jpeg
---

This a second part post following from the [part one](https://blog.ippon.tech/exploring-iot-infrastructure-support-in-aws-part1) of "Exploring AWS IoT Core and Greengrass Offerings". In part one I discussed the background and theoretical aspects of the AWS IoT offerings including [AWS IoT Core](https://aws.amazon.com/iot-core/), [Greengrass](https://docs.aws.amazon.com/greengrass/latest/developerguide/what-is-gg.html#gg-platforms) and [Amazon FreeRTOS](https://aws.amazon.com/freertos/). I also introduced a proof of concept which will be further developed and discussed in this blog post. More example code will be provided and the the development options will be evaluated.

# The Journey:

It's always been a hobby of mine to explore low-powered/mobile computing devices and hack around. Whether it was revitalising old routers, increasing page limits for printers or customising the Android OS. The basic goal for me was I wanted to have some fun whilst learning about the software and supporting OS. The questions I always had were:

> Can we get a shell? maybe we can install a custom Linux?

> We have Linux... Are there ways in which we can give the device more capability?

Surprisingly a huge number of devices run embedded Linux/Unix. I'm not talking about the standard devices I mentioned before. But devices you would never expect like music players, fridges, cars, coffee machines and drones. Many of these make use of open source software/drivers with additional proprietary components.

Over the last few months I ordered a number of Espressif based IoT devices from the Amazon of China: Taobao.com for experimentation, research and potential use in coding Dojos or teaching. Below is an example of one of the [ESP32 devices](http://www.nodemcu.com/index_en.html) I had purchased. Google translate might help here a bit but if you wish you can also purchase these off a well known English based Aliexpress.com but the selection is more limited and not up to date.

![Example Taoabao device](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/07/taobao_esp8266.png)

Yes you read that right. **15.40 Chinese Yuan!**  At the current exchange rate that is about 2 USD. The following table shows the basic specifications for the ESP32 and ESP8266 chips. A fully fledged ESP32 board with OLED and onboard USB TTL is only 8 USD and has a thriving community behind it. There is no lack of software, blog posts or research. The perfect experimentation platform for IoT!

### Basic Specifications for ESP32 and ESP8266 chips:

Product | CPU | RAM | I/O | Features |
------- | ------- | ------- | ------ | ----
[ESP8266](https://en.wikipedia.org/wiki/ESP8266) |  160 Mhz |  32 KiB instructions, 80 KiB user data | 16 GPIO | WiFi
[ESP32](https://en.wikipedia.org/wiki/ESP32) | Dual core 240Mhz |  520 KiB SRAM | 40 GPIO | WiFi, BlueTooth (BLE)

# Initial Setup

Before I started to develop the proof of concept with these devices I wanted to be sure I understood the prerequisites, setup and possible alternative firmware.

Firstly, like most IoT devices I needed to have some means of connection to upload firmware. This required the usual downloading of drivers, unload/load kernel modules, permissions and debugging USB/serial access. This is not the focus of this blog post but I have a few resources in the final section for device setup. Some common issues faced are usually related to user and file permissions, drivers and serial port setup.

Once they were setup I wanted to dig right in and see how I could give these beauties access to AWS!

## The questions that I needed to answer were:

- What are the possibilities with this hardware and how mature are they?
- How can I improve work flow in developing software and infrastructure?
- How do I deploy my code/infrastructure?
- What are the issues and downfalls?


![Overview of the idealised IoT POC infrastructure](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/07/aws_iot_idealised_poc.png)


# Something Easy First - Greengrass

Initially, I decided to setup the Greengrass software on the Raspberry Pi, this would act as the controller and allow me to have the Greengrass core acting as the Raspberry Pi. The following links are available and made highly visible on the AWS documentation. AWS provides Greengrass docs for the setup on the RPI 3, EC2, Nvidia Jetson and general Unix setup.

- https://docs.aws.amazon.com/greengrass/latest/developerguide/module1.html

> **Rookie mistake number 1**: I did not realise the Raspberry Pi 0 was ARMv6 not ARMv7 (Required by Raspberry Pi Greengrass software) so spent a few hours trying to debug the issues running the Greengrass daemon. I assume it wasn't working because of this but it was hard to tell since the dependency checker passed and all steps work as they should except for the execution of the daemon.

If you have a Raspberry Pi 2 B you can work through the above steps to get `greengrassd` up and running. I will not go through the whole setup as the full documentation is provided on AWS but will note some interesting points and issues I had. 

- Requires the specific `2017-03-02-raspbian-jessie.zip` image
- Kernel requires updating, I am guessing this might have something to do Greengrass Lambda and OTA features.
- Built in dependency checker still succeeded on ARMv6
- Needed to manually setup a Greengrass group using "easy creation" in the AWS console. This does a number of steps behind the scenes including creation of certificates/keys, roles, policies and definitions
    - Security resources including the cert, private key, public key and config.json must be downloaded after creation **(they will not be available again)**.
- Two compressed files need to be manually imported to the RPI using `scp`, USB or some other form of transfer.
- A Symantec CA should be downloaded and is required for MQTT TLS

Once this is done, we should be able to run the `greengrassd` daemon.

- https://your-aws-endpoint/greengrass/discover/thing/thing-name
- Allows discovery of group to which a device belongs, the GG core in group, groups CA cert.

- Using Lambda to customise OPC-UA protocols
- https://docs.aws.amazon.com/greengrass/latest/developerguide/opcua.html

# Discussion
The setup is quite large and I would have expected a easier setup like the one click certificate generation. Ideally AWS should provide some kind of package or even a custom RPI image to be downloaded and flashed based on the setup. Or even a one of S3 temporary link to software, certificates. The public key, is not required to to be secure and nor is the config.json or software. `Why make us manually transfer it across`? None the less I had still issued the certificates, downloaded and setup the whole configuration so the Raspberry Pi was now acting as a "Thing" in Iot Core, it was just failing to run `greengrassd` even though the dependency checker was passing.


# Getting low - AWS FreeRTOS

# SnakeOil - MicroPython

MicroPython [setup](https://docs.micropython.org/en/latest/esp8266/esp8266/tutorial/intro.html) for the ESP8266 consisted of downloading the MicroPython [software](https://micropython.org/download), erasing the flash and uploading the new firmware using [`esptool`](https://github.com/espressif/esptool). Lucky enough the ESP8266 and ESP32 boards I was using had USB-serial converters built in so all I was required to do was hook up the REPL at: `/dev/USB`. If your board doesn't have a convertor or you don't like serial. MicroPython has also been automatically configured to set up a WiFi AP at `MicroPython-xxxx` with the password `micropythoN`. Once connected all you need to do is run `screen /dev/ttyUSB -baud 115200` to connect to the through the USB-serial converter.
Wow very cool! Setup was awesome and hassle free!

For WiFi just check out the IP of the station and login. Boom! REPL at your fingertips!

```
>>> print('hello esp8266!')
hello esp8266!
```

Now that is cool, I was very impressed by this considering it worked out of the box and the first try! Unlike most of my IoT experimentations.

## Now how do I connect MicroPython to AWS?

Hmmmm... **Starts Googling "MicroPython AWS libraries".

This lead me down the path of trying to figure out if it was possible to use the AWS Python SDK on MicroPython. I had a feeling that MicroPython was somewhat limited when it came to larger libraries and wouldn't have enough storage. Boto3 is over 9M, Python AWS is over 3M. It looks like we need to freeze the AWS packages in and reflash the device according to Tom Manning in his hackster [post](https://www.hackster.io/user3282664/micropython-to-aws-iot-cc1c20). 

> Due to ESP8266 RAM limitations, the python modules for this project need to be included in the flashed firmware in frozen bytecode

Sounds like fun! I could do something with RTOS but I'd be spending most of my time making sure my C/C++ code is correct rather than exploring the rapid development opportunities with MicroPython and other frameworks.

There also appears to be issues with the mutual TLS authentication. It looks like MicroPython has it's benefits but maybe there is something better for integrating with AWS
https://github.com/micropython/micropython/issues/2781

# The King - MongooseOS

I happened to come across MongooseOS mentioned in some of the AWS Blog [posts](https://aws.amazon.com/blogs/apn/aws-iot-on-mongoose-os-part-1/), some these blogs provide very useful information that the documentation does not. So I thought I'd explore more.

It appears MongooseOS and the ESP devices work very nicely. Mongoose provides a nice IDE with updates, real time code saving.  It looks like I've found what I am after, no repackaging, modification of source for TLS1.2 or injection of libraries
MongooseOS provides a abstract implementation of IoT working with AWS, Azure and GCP. Sounds fantastic!

So I decided to download the `mos` tool and setup the IDE to see if this was really the case!
https://mongoose-os.com/software.html

Once Mongoose is installed all I needed to do was start `mos` and then download the sample project through the web IDE and flash to the device. As soon as this was done, the device rebooted and console output was routed to the Web IDE.

To communicate with our IoT devices we will be using MQTT, this allows us to access the default MQTT topics for shadow updates and any custom topics created. We will create 1 extra topic for publishing sensor data to AWS as follows: `devices/deviceId/data` (for sensor data) To update device shadows, sensor
https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html

Logging Humidity and Temperature every 5 minutes to MQTT. Set up action to save this to public S3 bucket publicly accessible bucket. Lambda functions can use parse this into data that can be read by the android application, Not sure on what to do with the data, maybe csv or database. Can also set a temperature alert and humidity alert.

# Device State and Settings:

```Javascript
let dht = DHT.create(dhtPin, DHT.DHT11);
GPIO.set_mode(ledPin, GPIO.MODE_OUTPUT);
let deviceId = Cfg.get('device.id');

// Timer delays
let msToSendSensorData = 1000 * 30;

// Topics
let sensorTopic = 'devices/' + deviceId + '/data';
let settingsUpdateTopic  = 'devices/' + deviceId + '/settings';

//Device state by default led on and sending data
let state = {
  sendData: true,
  ledOn: true
};
```

# Subscription handler
```Javascript
MQTT.sub(settingsUpdateTopic, function(conn, settingsUpdateTopic, msg) {
  print('Topic: ', settingsUpdateTopic, 'message:', msg);
  let obj = JSON.parse(msg);

  if(obj.sendData === false){
    print('Disabling Sensor data upload');
  }
}, null);
```

# AWS shadow state handling
```Javascript
AWS.Shadow.setStateHandler(function(ud, ev, reported, desired) {
  print('Event:', ev, '('+AWS.Shadow.eventName(ev)+')');

  if (ev === AWS.Shadow.CONNECTED) {
    AWS.Shadow.update(0, state);
    return;
  }

  updateState(reported);
  updateState(desired);

  if (ev === AWS.Shadow.UPDATE_DELTA) {
    AWS.Shadow.update(0, state);
  }
}, null);
```

# IoT needs a an Android friend

The devices are now setup, our decision has been made, MongooseOS is the OS of choice for this experiment. 

What use are IoT devices if your data cannot visualised, seen or controlled in some manner. Data is there to be used. Current data will be output to S3 by default but there are many other options available including Kinesis, DynamoDB. The IoT rules can be modified to do this if needed.

MongooseOS provides an unified Cloud library and specialised AWS libraries to interact with the AWS shadows. We can subscribe to messages and shadow updates.

I wanted to create an application to use this data. I had two choices, a web app and a mobile application. I decided Android would be

So I decided to make a simple companion/controller application that utilises AWS Cognito to connect to AWS IoT core infrastructure and read device shadow information and list current devices. Ideally this application if fully implemented would allow both read and write access to Iot settings. 

What is really cool is I can set my own options for the mobile application, eg. Set notifications, emails or do something else when an event comes in. Select “message” from MQTT
Giving me the result I need for further analysis or aggregation.

This data will be stored in our Shadow document for each device in Aws IoT core and we have two options for this: Local shadow and Shadow syncing to cloud. We will be using the Shadow sync as we need to aware of the changes made to shadows.

The code below pulls down our  thing list using the `iotClient` and returns a list of things that can be used in the UI. This will display the list of things in AWS IoT core.

```Java
@Override
protected AsyncTaskResult<List<ThingAttribute>> doInBackground(Void... voids {
    // Getting all in IoT core as a list
    ListThingsRequest listThingsRequest = new ListThingsRequest();
    ListThingsResult listThingsResult = iotClient.listThings(listThingsRequest);
    attributeList = listThingsResult.getThings();
    return new AsyncTaskResult<>(attributeList);
}
@Override
protected void onPostExecute(AsyncTaskResult<List<ThingAttribute>> result) {
    // Updating our table of things in the UI
    ((MainActivity) context).updateThingUI(result.getResult());
}
```

Getting device state of the selected device:

```Java
@Override
protected AsyncTaskResult<String> doInBackground(Void... voids) {
    // Setting up our request for shadow state
    GetThingShadowRequest getThingShadowRequest = new GetThingShadowRequest();
    getThingShadowRequest.setThingName(name);

    // Getting our shadow and parsing into string
    GetThingShadowResult getThingShadowResult = dataClient.getThingShadow(getThingShadowRequest);
    byte[] bytes = new byte[getThingShadowResult.getPayload().remaining()];
    getThingShadowResult.getPayload().get(bytes);
    result = new AsyncTaskResult<>(new String(bytes));
    return result;
}

@Override
protected void onPostExecute(AsyncTaskResult<String> result) {
    // Updating our ThingDetails UI with Shadow Information
    JSONObject parsedJson = new JSONObject(result.getResult());
    String parsedToString = parsedJson.toString(spacesToIndentEachLevel);
    ((ThingDetailsActivity) context).updateJson(parsedToString);
}
```

Now we need to setup deployment options, what happens if we have a 100 devices! We want to deploy this infrastructure in a more manageable way rather than dealing with the console all the time.

This is where some options in AWS fall shorts, CloudFormation and Terraform are not up to speed in this area. There are a few other options on Github but I've yet to test but these they might be worth checking out.

# Deployment

## Setting up our identity pool giving unauthenticate or authenticated access to S3 and IoT

```json
resource "aws_cognito_identity_pool" "cognito_pool" {
  identity_pool_name = "identity pool"
  allow_unauthenticated_identities = true
}

resource "aws_iam_role_policy" "unauthenticated" {
  name = "unauthenticated_policy"
  role = "${aws_iam_role.cognito_unauthenticated.id}"

  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "mobileanalytics:PutEvents",
        "cognito-sync:*",
        "cognito-identity:*",
        "iot:*",
        "s3:*"
      ],
      "Resource": [
        "*"
      ]
    }
  ]
}
EOF
}
```

## Setting up topic rules for our S3 bucket output

```json
resource "aws_iot_topic_rule" "topic_rule_write_to_s3" {
  name = "rule_write_to_s3"
  enabled = true
  sql = "SELECT * FROM 'devices/${aws_iot_thing.thing1.name}/data'"
  sql_version = "2016-03-23"
  s3 {
    bucket_name = "${aws_s3_bucket.s3_bucket.bucket}"
    key = "$${topic()}/data_$${timestamp()}.json"
    role_arn = "${aws_iam_role.role_iot.arn}"
  }
}

resource "aws_s3_bucket" "s3_bucket" {
  bucket_prefix = "iot-data-"
  force_destroy = true
}
```

## Giving IoT access to Amazon for storing data

```json
resource "aws_iam_role" "role_iot" {
    name = "role_iot"
    assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "iot.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "s3_policy_attachment" {
    role = "${aws_iam_role.role_iot.name}"
    policy_arn = "arn:aws:iam::aws:policy/AmazonS3FullAccess"
}
```

## The problem we have is our "Thing" is still not setup in AWS!


We have a few options, we can manually set up certs, link these certs to our device, upload the certs to the device and AWS IoT core. Then TLS mutual auth will be setup. Or we can deploy our full infrastructure then simply us the magic `mos` tool to set things up. Since we see the power of `mos` we will use this tool to setup AWS IoT MQTT auth.

`mos aws setup-aws`

- AWS Labs Greengrass group creation
- https://github.com/awslabs/aws-greengrass-group-setup
- Greengo
- https://github.com/dzimine/greengo

# Show me the Code

- [MongooseOS Project on ESP](https://github.com/BenEdridge/Ippon_IoT_ESP) with the required Terraform files and docs.
- [Android application](https://github.com/BenEdridge/Ippon_IoT_Android) for interacting with our AWS IoT devices once the initial ESP project is setup.

# Modifying the application?

1. setup up custom push alerts to the mobile when changes are made in the device shadow or MQTT messages eg. If temp is over 30 send email or text message
2. Delete, grouping of devices
3. Remove push alerts
4. Show large scale information
5. Set up more devices, group them allow management of groups through app
6. Use greengrass for local ML or Lambda functions
7. Manage this on the app

# The struggle is real

AWS IoT provides a solid deployment offering for IoT devices in particular the actions provided and shadow documents decouple state management of IoT device state the reaction to changes. Data comes into AWS IoT through connected devices and cores. This data comes in as messages in MQTT or devices shadows (Implemented as MQTT). Actions can then be setup based on the IoT inputs queried using SQL. These actions invoke certain AWS services such as SQS, S3, Kinesis, CloudWatch and many more.

### The Good

- Advanced SQL querying of MQTT messages 
- Grouping of devices at multiple layers coupled with Greengrass SDK to manage such groups
- Automatic setup of mutual TLS certs through the console
- Policies based on certs, automatic policy setting, revocation and expiry
- Custom authentication if needed
- Some great little examples provides for MongooseOS and SDKs
- Came across a number of awesome IDEs/dev environments for IoT devices specifically the ESP including the most promising:
    - MongooseOS
    - Micropython
    - Platform.IO
    - Zerynth
    - Espruino

### The Bad

- Raspberry Pi Zero issues with Greengrass
- No Linux support for RTOS simulator
- UI is somewhat strange
- Heavy push for RTOS no talk of other options like MicroPython/Mongoose or actual IoT device implementations
- Separate SDKs? Not clear docs around the separate SDKs or differences
- Tried for a long time to set it up but it was actually wrong. And there is not much info using the AWS IoT api Endpoint is this not `<uuid>.iot.ap-southeast-2.amazonaws.com` but actually `iot.ap-southeast-2.amazonaws.com` and the IoT endpoints differ depending on API used?
- Terraform does not have so much [support](https://github.com/terraform-providers/terraform-provider-aws/issues/143) for IoT so ended up doing all I can with Terrafrom packaging up the rest using AWS Console and `mos` tool
- Even CloudFormation has limited support for AWS IoT and no support for Greengrass so `boto3` and the aws cli is your friend
-  Would be nice to have some other authentication methods for less secure devices or limited devices. Not all devices support TLS mutual auth or need it
- Issues with device stability (Yeah $3 USD) what do you expect!
- `mos` IDE stability for device flashing and connections

# What next?

However, If you are familiar with AWS IoT you may have noticed there are still a number of components I did not cover as this was a brief overview of the core features. 

**Some interesting areas I did not cover are:**

- IoT Core
    - Jobs
    - Custom Authenticators
    - RTOS programming (I have had little experimentation with RTOS and it is something to further explore!)
- Greengrass
    - OTA updating
    - Machine learning inference
    - Local volume/dev access
    - More complicated group management, sync and larger scale setup
- Inner workings of message brokering
- Large scale commercial deployments

# Stand on the shoulders of giants

[MongooseOS Github](https://github.com/cesanta/mongoose-os)  
[MicroPython setup tutorial on the ESP8266](https://docs.micropython.org/en/latest/esp8266/esp8266/tutorial/intro.html)  
[Large AWS Connected vehicle solution](https://aws.amazon.com/answers/iot/connected-vehicle-solution)  
[Apple Driver setup tips](https://kig.re/2014/12/31/how-to-use-arduino-nano-mini-pro-with-CH340G-on-mac-osx-yosemite.html)  
[AWS IoT Troubleshooting](https://docs.aws.amazon.com/iot/latest/developerguide/iot_troubleshooting.html) 
[Greengrass Troubleshooting](https://docs.aws.amazon.com/greengrass/latest/developerguide/gg-troubleshooting.html)  
[Confused about ESP8266 boards versions?](https://frightanic.com/iot/comparison-of-esp8266-nodemcu-development-boards)  
[AWS FreeRTOS Demos](https://docs.aws.amazon.com/freertos/latest/userguide/freertos-next-steps.html)  
[Great blog post about MongooseOS](https://www.mjoldfield.com/atelier/2017/07/mongoose.html)





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

This a second post following from the [part one](https://blog.ippon.tech/exploring-iot-infrastructure-support-in-aws-part1) of "Exploring AWS IoT Core and Greengrass Offerings". In part one I discussed the background and theoretical aspects of the AWS IoT offerings including [AWS IoT Core](https://aws.amazon.com/iot-core/), [Greengrass](https://docs.aws.amazon.com/greengrass/latest/developerguide/what-is-gg.html#gg-platforms) and [Amazon FreeRTOS](https://aws.amazon.com/freertos/). I also introduced a proof of concept which will be further discussed in this blog post. More example code will be provided and the deployment options will be evaluated.

# The Journey:

It's always been a hobby of mine to explore low-powered/mobile computing devices and hack around. Whether it was revitalising old routers, increasing page limits for printers or customising the Android OS. The basic goal for me was I wanted to have some fun whilst learning about the software and supporting OS. The questions I always had were:

> Can we get a shell? maybe we can install a custom Linux?
> We have Linux... Are there ways in which we can give the device more capability?

Surprisingly a huge number of devices run embedded Linux/Unix. I'm not talking about the standard devices I mentioned before. But devices you would never expect like music players, fridges, cars, coffee machines and drones. Many of these make use of open source software/drivers with additional proprietary components.

Over the last few months I purchased a selection of Espressif based IoT devices from the Amazon of China: Taobao.com for experimentation, research and potential use in coding Dojos or teaching. Below is an example of one of the [ESP32 devices](http://www.nodemcu.com/index_en.html). Google translate might help a bit to purchase these but if you wish you can also purchase these off a the well known Aliexpress.com but the selection is more limited and not up to date.

![Example Taoabao device](https://raw.githubusercontent.com/BenEdridge/blog-usa/master/images/2018/07/taobao_esp8266.png)

Yes you read that right. **15.40 Chinese Yuan!**  At the current exchange rate that is about 2 USD. The following table shows the basic specifications for the ESP32 and ESP8266 chips. A fully fledged ESP32 board with OLED and onboard USB TTL is only 8 USD and has a thriving community behind it. There is no lack of software, blog posts or research. The perfect experimentation platform for IoT, so lets dig right in!

### Base Specifications for ESP32 and ESP8266:

Product | CPU | RAM | I/O | Features |
------- | ------- | ------- | ------ | ----
[ESP8266](https://en.wikipedia.org/wiki/ESP8266) |  160 Mhz |  32 KiB instructions, 80 KiB user data | 16 GPIO | WiFi
[ESP32](https://en.wikipedia.org/wiki/ESP32) | Dual core 240Mhz |  520 KiB SRAM | 40 GPIO | WiFi, BlueTooth (BLE)

Before I started developing the proof of concept outlined below. I wanted to be sure I understood the prerequisites, setup and alternative firmware.

![Overview of the idealised IoT POC infrastructure](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/07/aws_iot_idealised_poc.png)

Firstly, I needed to have some means of connection to upload firmware. This required the usual download of drivers, unload/load kernel modules, permissions and debugging of USB/serial access. This is not the focus of this blog post but I have a few resources in the final section for device setup. Some common issues faced usually relate to user and file permissions, drivers or serial port setup.

# Generic Thing setup for both IoT core and Greengrass

From my experimentation I've compiled a generalised setup that may differ slightly depending on actual device used but this is typically what you will be following with any of the sample projects.

1. Download software (MongooseOS, MicroPython, RTOS, Greengrass)
2. Configure Toolchain/IDE to work with the device
3. Configure project/code with AWS settings and WiFi settings for connectivity
4. Configure IoT credentials (keys for TLS mutual authentication) on both the device and AWS
5. Install or flash the firmware files to the device

# Something Easy First - Greengrass

Initially, I had decided to setup the Greengrass software on the Raspberry Pi, this would act as the controller and allow me to have the Greengrass core acting as the Raspberry Pi. The following links are available on AWS providing the [Greengrass docs](https://docs.aws.amazon.com/greengrass/latest/developerguide/module1.html) for the setup on the RPI 3, EC2, Nvidia Jetson and general Unix setup.

> **Rookie mistake number 1**: I did not realise the Raspberry Pi Zero was ARMv6 not ARMv7 (Required by Raspberry Pi Greengrass software) so I spent a few hours trying to debug the issues running the Greengrass daemon the setup. I assume it wasn't working because of this but it was hard to tell since the dependency checker passed and all steps work as they should except for the final execution of the daemon.

If you have a Raspberry Pi 2 B you can work through the above steps to get `greengrassd` daemon up and running. I will not go through the whole setup but will note some interesting points and issues I had before before I realised I was using the wrong Raspberry Pi:

- Requires the specific `2017-03-02-raspbian-jessie.zip` image
- Kernel update suggested (This might have something to do Greengrass Lambda and OTA features)
- Built in dependency checker still succeeded on ARMv6, it only checks for required binaries
- Manual setup a Greengrass group using "easy creation" in the AWS console. This does a number of steps behind the scenes including creation of certificates/keys, roles, policies and definitions
    - Security resources including the cert, private key, public key and config.json must be downloaded after creation **(they will not be available again)**.
- The Security resources and the Greengrass software tar.gz files need to be manually imported to the RPI using `scp`, USB or some other form of transfer.
- A Symantec CA cert should be downloaded to the RPI and is required for MQTT TLS

There is a repository for generalised GG setup, AWS just tells us to type a bunch of commands without any real explanation of what these do!

Even though I failed to run the `greengrassd` on the RPI I decided setup the daemon on EC2 had still managed to issue the certificates, download and setup the whole configuration so the Raspberry Pi was now acting as a "Thing" in Iot Core, it was just failing to run `greengrassd`. Next step was to abandon the Raspberry Pi greengrass setup and use an EC2 instance instance.

Once this is done, we should be able to run the `greengrassd` daemon on the local device. After we can starting experimenting with device [discovery](https://your-aws-endpoint/greengrass/discover/thing/thing-name) giving information on groups to which a device belongs, the GG core in group and certificates.

# Discussion

The setup for Greengrass is quite long and I would have expected an easier setup overall. Ideally AWS should provide some kind of package or even a custom RPI image to be downloaded and flashed based on the setup. Or even a S3 bucket generated with a temporary link to software and certificates. For example: The public key, is not required to to be secure and neither is the `config.json` or software. 

# Getting low - AWS FreeRTOS

Now we have a Greengrass core device for controlling and being the hub for our lower powered devices. 
There wasn't actually much information provided on AWS FreeRTOS but I wanted to figure out and understand what I could do with RTOS since this one of the services by AWS I should be trying it out.

Starting from FreeRTOS [prerequisites](https://docs.aws.amazon.com/freertos/latest/userguide/freertos-prereqs.html) I started working through the examples. 

Just as we setup Greengrass before with EC2, RTOS also follows a very similar setup. The device representation on AWS is not to different from a Greengrass device. The ESP32-devkit has a more streamlined setup compared to other devices but does not support OTA, lightweight IP, SMP  and BLE.

We also need to flash the RTOS to the device. Once flashed we should get output similar to the following if we run the MQTT echo example.

```
354 8667 [MQTT] Received message 0 from queue.
355 8668 [MQTTEcho] Command sent to MQTT task passed.
356 8668 [MQTTEcho] MQTT echo demo finished.
```

Interesting points to note with setup:
- Amazon RTOS needs to be downloaded from AWS  you have a choice of integrated dev environments or standalone software.
- `<BASE_FOLDER>/demos/common/tools/aws_config_quick_start/configure.json` requires setup with Wifi and IoT endpoint.
- Constants must be setup to match configuration in the AWS Console
- Just as the cert and private key were given to `greengrass` we must also give them to the RTOS setup and update the file in `<BASE_FOLDER>\demos\common\include\aws_clientcredential_keys.h`
  
[ESP32 devkit]() ESP32-DevKitC i

[Windows RTOS Simulator](https://docs.aws.amazon.com/freertos/latest/userguide/getting_started_windows.html)

[RTOS](https://github.com/aws/amazon-freertos/tree/master/demos/common) demos

# Discussion

AWS RTOS documentation is certainly limited and doesn't provide many answers on actual examples or dealing with sensors like some of the other examples provided with Greengrass. It's provided as a software download. It is very low level and requires vast knowledge of peripheals and driver setup for these devices. For example you can see the difference in code below for the ESP32 AWS FreeRTOS MQTT example:

AWS FreeRTOS requires local building and will differ depending on local environments. Come on AWS maybe you should purchase MongooseOS!

# SnakeOil - MicroPython

MicroPython [setup](https://docs.micropython.org/en/latest/esp8266/esp8266/tutorial/intro.html) for the ESP8266 consisted of downloading the MicroPython [software](https://micropython.org/download), erasing the flash and uploading the new firmware using [`esptool`](https://github.com/espressif/esptool). Lucky enough the ESP8266 and ESP32 boards I was using had USB-serial converters built in so all I was required to do was hook up the REPL at: `/dev/USB`. 

If your board doesn't have a convertor or you don't like serial. MicroPython has also been automatically configured to set up a WiFi AP at `MicroPython-xxxx` with the password `micropythoN`. Once connected all you need to do is run `screen /dev/ttyUSB -baud 115200` to connect to the through the USB-serial converter. Wow very cool!

For WiFi just check out the IP of the station and login. Boom! REPL at your fingertips:

```
>>> print('hello esp8266!')
hello esp8266!
```

Now that is cool, I was very impressed by this considering it worked out of the box and the first try! Unlike most of my IoT experimentations.

## I have a REPL but how do I connect MicroPython to AWS?

> Hmmmm... Starts Googling MicroPython AWS libraries.

The first post brings up an interesting post by Tom Manning. It looks like we need to freeze the AWS packages in and reflash the device according to the hackster [post](https://www.hackster.io/user3282664/micropython-to-aws-iot-cc1c20).

> Due to ESP8266 RAM limitations, the python modules for this project need to be included in the flashed firmware in frozen bytecode

Sounds like fun! I could do something with RTOS but I'd be spending most of my time making sure my C/C++ code is correct rather than exploring the rapid development opportunities with MicroPython and other frameworks.

So this lead me down the path of trying to figure out if it was possible to use the AWS Python SDK on MicroPython. I had a feeling that MicroPython was somewhat limited when it came to larger libraries and wouldn't have enough storage. Boto3 is quite large and would likely suffocate the memory on the device.

There also appears to be issues with the mutual [TLS authentication](https://github.com/micropython/micropython/issues/2781). It looks like MicroPython is very useful it's probably not so suitable for AWS integration right now. It has it's benefits but maybe there is something better for integrating with AWS. Not necessarily snake oil but not what I am looking for right now.

# The King - MongooseOS

After abandoning MicroPython, I happened to come across MongooseOS mentioned in some of the AWS Blog [posts](https://aws.amazon.com/blogs/apn/aws-iot-on-mongoose-os-part-1/), some these posts provide useful information that the documentation does not necessarily mention. So I thought I'd explore more.

It appears MongooseOS and the ESP devices work very nicely. Mongoose provides a nice IDE with updates, real time code saving. No repackaging, modification of source for TLS1.2 or injection of libraries into bytecode. MongooseOS also provides a abstract implementation of IoT working with AWS, Azure and GCP. Sounds fantastic!

So I decided to [download](https://mongoose-os.com/software.html) the `mos` tool and setup the IDE to see if this was really the case!

Once MongooseOS was installed all I needed to do was start the `mos` IDE and then download the sample project through the web interface then flash it to the device.

![MOS tool setup](https://raw.githubusercontent.com/BenEdridge/blog-usa/master/images/2018/07/mos_initial_setup_screenshot.png)

Once I had done the device rebooted and console output was routed to the Web IDE!

MongooseOS provides an unified Cloud library and specialised AWS libraries to interact with the AWS shadows. We can subscribe to messages and shadow updates.

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

AWS Access requires the mos tool setup for permissions and certificatse uploaded to the ESP8266 device. The IoT setup can be found in the mos IDE or setup using the commandline `mos` tool: Then use "provision with AWS IoT"

![MOS tool AWS Setup](https://raw.githubusercontent.com/BenEdridge/blog-usa/master/images/2018/07/mos_iot_setup_screenshot.png)

# MongooseOS needs a friend

The devices are now setup and I have decided to go with MongooseOS for this experiment. But what use are IoT devices if your data cannot visualised, seen or controlled in some manner? Currently the data will be output to S3 by default but there are many other options available including Kinesis, DynamoDB and Lambda function. The IoT rules can be modified to do this if needed.

I wanted to create an application to use this data generated. I had two choices; a web application or a mobile application. I decided Android would be ideal due to ability to emulate this device using Android studio and the solid Android SDK provided by AWS.

So I decided to make a simple companion/controller application that utilises AWS Cognito to connect to AWS IoT core infrastructure and read device shadow information and list current devices. Ideally this application if fully implemented would allow both read and write access to Iot settings. 

What is really cool is I can set my own options for the mobile application, eg. Set notifications, emails or do something else when an event comes in. Select “message” from MQTT. Giving me the result I need for further analysis or aggregation.

This data will be stored in our Shadow document for each device in Aws IoT core and we have two options for this: Local shadow and Shadow syncing to cloud. We will be using the Shadow sync as we need to aware of the changes made to shadows.

The Android code below pulls down our Thing list using the `iotClient` and returns a list of Things that can be used in the UI. This will display the list of things from AWS IoT core.

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
## Here is a screenshot of the output we get in our Android activity from the above code:

![Android Screenshot](https://raw.githubusercontent.com/BenEdridge/blog-usa/master/images/2018/07/aws_iot_app_screenshot_shadow.png)

You can can see we are getting the the attributes associated with the device including the id, mac, temperature and humidity. This is retrieved from the device shadow state. 

# Deployment

We now have devices setup on AWS and a companion application running but we also need to setup a means of repeatable deployment. What happens if we have a 100 devices? We want to deploy this infrastructure in a more manageable way rather than dealing with the console all the time. 

This is where some options in AWS fall shorts, CloudFormation and Terraform are not up to speed. There are a few other options on GitHub but I've yet to test but these they might be worth checking out:

- [AWS Labs Greengrass group setup](https://github.com/awslabs/aws-greengrass-group-setup)
- [Greengo](https://github.com/dzimine/greengo)

However we have decided to go with Terraform as it heavily used at Ippon, Terraform in combination with custom initialisation scripts or setup via MongooseOS is not too difficult once setup.

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

## Setting up our identity pool giving unauthenticated or authenticated access to S3 and IoT

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

## After this deployment we have our "Thing" and permissions setup in AWS correctly but we do not have TLS Auth setup

We have a few options, we can manually set up certs like before and link these certs to our device, upload the certs to the device and AWS IoT core. Then TLS mutual auth will be setup. 

Or we can deploy our full infrastructure then simply us the magic `mos` tool to set things up. Since we see the power of `mos` we will use this tool to setup AWS IoT MQTT auth and magically deal with these issues.

`mos aws setup-aws`

# Show me the Code

- [MongooseOS Project on ESP](https://github.com/BenEdridge/Ippon_IoT_ESP) with the required Terraform files and docs.
- [Android application](https://github.com/BenEdridge/Ippon_IoT_Android) for interacting with our AWS IoT devices once the initial ESP project is setup.

# Potential extensions or modifications of the App

1. Custom push alerts to the mobile when changes are made in the device shadow or MQTT messages eg. If temp is over 30 send email or text message
2. Set up more devices, group them allow management of groups through app
3. Show large scale deployment information
4. Use greengrass for local ML or Lambda functions
5. Use the device like a local greengrass device

# The struggle is real

AWS IoT provides a solid deployment offering for IoT devices in particular the actions provided and shadow documents decouple state management of IoT device state the reaction to changes. Data comes into AWS IoT through connected devices and cores. This data comes in as messages in MQTT or devices shadows (Implemented as MQTT). Actions can then be setup based on the IoT inputs queried using SQL. These actions invoke certain AWS services such as SQS, S3, Kinesis, CloudWatch and many more.

### The Good

- Advanced SQL querying of MQTT messages 
- Grouping of devices at multiple layers coupled with Greengrass SDK to manage such groups
- Automatic setup of mutual TLS certs through the console
- Policies based on certs, automatic policy setting, revocation and expiry
- Custom authentication if needed
- Some great little examples provides for MongooseOS and SDKs
- Setup custom OPC-UA protocols using [Lambda](https://docs.aws.amazon.com/greengrass/latest/developerguide/opcua.html)
- Came across a number of awesome IDEs/dev environments for IoT devices specifically the ESP including the most promising:
    - MongooseOS
    - Micropython
    - Platform.IO
    - Zerynth
    - Espruino

### The Bad

- Raspberry Pi Zero issues with Greengrass
- No Linux support for RTOS simulator
- Little talk of other options like MicroPython/MongooseOS or actual IoT device implementations with them
- Separate SDKs? No clear docs if the separate SDKs or differences in SDKs
- AWS IoT api Endpoint Android is not `<uuid>.iot.ap-southeast-2.amazonaws.com` but actually `iot.ap-southeast-2.amazonaws.com` and the IoT endpoints differ depending on API components used.
- Terraform has little [support](https://github.com/terraform-providers/terraform-provider-aws/issues/143) for IoT so ended up doing all I can with Terrafrom packaging up the rest using AWS Console and `mos` tool
- CloudFormation has limited support for AWS IoT and no support for Greengrass so `boto3` and the `aws` cli is your friend
- No other authentication methods for less secure devices or limited devices. Not all devices support TLS mutual auth or need it
- Issues with device stability (Yeah $3 USD) what do you expect!
- `mos` IDE stability for device flashing and connections

# What next?

If you are familiar with AWS IoT you may have noticed there are still a few components I did not cover:

- IoT Core
    - Jobs
    - Custom Authenticators
    - RTOS programming (I have had little experimentation with RTOS)
- Greengrass
    - OTA updates
    - Machine Learning inference
    - Local volume/dev access
    - More complicated group management, sync and larger scale setup
- Inner workings of message brokering systems and MQTT
- Large scale commercial deployments
  - IoT management, grouping

Hopefully in the future we see more support by AWS Cloudformation and Terraform deployments.

# Stand on the shoulders of giants

[MongooseOS on GitHub](https://github.com/cesanta/mongoose-os)  
[Great blog post about MongooseOS](https://www.mjoldfield.com/atelier/2017/07/mongoose.html)  
[MicroPython setup on the ESP8266](https://docs.micropython.org/en/latest/esp8266/esp8266/tutorial/intro.html)  
[Confused about ESP8266 boards versions?](https://frightanic.com/iot/comparison-of-esp8266-nodemcu-development-boards)  
[Large AWS Connected vehicle solution](https://aws.amazon.com/answers/iot/connected-vehicle-solution)  
[AWS IoT Troubleshooting](https://docs.aws.amazon.com/iot/latest/developerguide/iot_troubleshooting.html) 
[Greengrass Troubleshooting](https://docs.aws.amazon.com/greengrass/latest/developerguide/gg-troubleshooting.html)  
[AWS FreeRTOS Demos](https://docs.aws.amazon.com/freertos/latest/userguide/freertos-next-steps.html)   
[Apple USB Driver Setup](https://kig.re/2014/12/31/how-to-use-arduino-nano-mini-pro-with-CH340G-on-mac-osx-yosemite.html)  





---
authors:
- Ben Edridge
tags:
- AWS
- IoT
- Melbourne
- Australia
- Greengrass
date: 2018-07-19T11:41:57.000Z
title: "Exploring AWS IoT Core and Greengrass Offerings"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/07/esp_device.jpeg
---

According to [IEEE](https://iot.ieee.org/newsletter/march-2017/three-major-challenges-facing-iot.html). The biggest challenges and issues facing Internet of Things (IoT) are security, privacy, connectivity, compatibility, standardisation and intelligent actions/analysis. This is what is holding us back from large scale developments in IoT. A large number of academic research [papers](https://ieeexplore.ieee.org/abstract/document/7823334/) have discussed the security and privacy aspects of IoT deployments. 

The closer and more inter-connected a network is the more strain it puts on developers to perform due diligence and lock down the network infrastructure and maintain ethical privacy practices. AWS provides a number of IoT related services since the release of [AWS IoT Core](https://aws.amazon.com/iot-core/) at the end of 2015. These services are quite complex and try to address a number of these concerns. Ideally developers want to spend most of their time on the development of the IoT application and not the infrastructure.

In this two-part series I will look at AWS services for IoT, their setup, configuration and the issues encountered during development. Dmitri Zimine wrote an interesting post on [A Cloud Guru](https://read.acloud.guru/aws-greengrass-the-missing-manual-2ac8df2fbdf4) earlier in 2018 describing AWS IoT core and [Greengrass](https://docs.aws.amazon.com/greengrass/latest/developerguide/what-is-gg.html#gg-platforms) as being a bit of mess. There are a number requirements to setup Greengrass and the complexity of AWS calls behind the scenes is no easy task to understand.

Current AWS offerings for IoT are somewhat fragmented with lacking documentation in some areas but great documentation in others. The AWS dashboard has 6 services but the key product offered by AWS appears to be AWS IoT Core which is for directly managing device infrastructure, device shadows and data flow within the network. It also includes Greengrass management interface in the UI which is actually a seperate product in itself. 

I aim to dissect the structure of AWS IoT Core and Greengrass with a broad focus on full end-to-end usage. I will compare offerings by other cloud providers and note some of the difficulties I’ve had through the development of the proof of concept. 

The source code will available in part 2 and can be deployed on your AWS account provided you have access to the resources and understand you will be charged a small sum for resource usage. Even though AWS IoT is not a recent offering there is still limited information and documentation related to it. The StackOverflow family has limited answers when something breaks and “Googling” doesn’t always get you where you want to be!

# What will I be covering?
1. AWS IoT services and how they relate to current infrastructure, the theory, the practicality and how it works together.
2. Working with a small POC utilising a number of AWS IoT services.
3. Reviewing what has been built, the concerns, ambiguities and what went wrong.
4. Final thoughts and ideas.
5. Some links, resources and source code.

![ESP8266](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/07/esp_device.jpeg)

# IoT Configuration
AWS provides a hub like [infrastructure](https://docs.aws.amazon.com/iot/latest/developerguide/what-is-aws-iot.html) which incorporates IoT devices as "[Things](https://docs.aws.amazon.com/iot/latest/developerguide/iot-thing-management.html)" in the network. This network is based heavily on "[Device Shadows](https://docs.aws.amazon.com/iot/latest/developerguide/iot-device-shadows.html)" which store device state and the MQTT network protocol for sending and receiving messages. A shadow is simply a JSON document containing specified, expected and delta of device state. These devices are categorised into either control/management devices called a “Greengrass Core” or lower powered device called a “Thing” which interacts with “Cores” within a Greengrass group using the AWS SDK. The terminology is somewhat confusing with “Greengrass cores”, “Greengrass groups”, “IoT core”, “Things” etc. A Greengrass core is not needed to setup IoT devices but gives additional benefits.

The questions you might have; Are "Greengrass Cores" "Things"? Can a Greengrass core also be a thing? The diagram below will hopefully explain the structure of AWS IoT offerings.

These lower powered devices usually have a CPU less than 1Ghz, an interface with sensors and run in an environment with limited connectivity.  The “Greengrass Core” has a custom “Greengrass” daemon and a number of software builds depending on the platform. There is support for Raspberry Pi, EC2, x86 and Arm. The lower powered “Thing” devices can use [AWS RTOS](https://aws.amazon.com/freertos/), AWS SDKs or the REST API to interact with IoT services. 

Communication is supported over MQTT and HTTP protocols with additional Websocket support. Both Greengrass Cores, groups and IoT things are managed through the same UI and appear to be very similar with the exception of additional interfaces for Greengrass Lambda functions and setting up groups in Greengrass.

## AWS IoT Product Overview:

The following table gives my opinions on the AWS IoT services, SDK and a summary of the intended uses.
Product | Summary | SDK/HW | Verdict
------- | ------- | ------- | ------
[IoT Core](https://aws.amazon.com/documentation/iot/) | AWS management interface, IoT device SDK, Device Gateway, Message Broker, Authorisation, Device Registry, Rule engine and Device Shadows. | Python, Java, Android, C/C++, Javascript. | The crux of the AWS IoT offerings; management interface.
[Greengrass](https://aws.amazon.com/documentation/greengrass/) | Works as an extension of IoT core and runs on the devices providing local compute, message, sync and ML inference. | Raspberry Pi, EC2, Texas Instruments (x86/Arm >1GHz, 128MB+ of RAM).| A controller and SDK provided with more compute power with tacked on Lambda and group management, OTA and management device can be anything really. Ubuntu, Raspberry Pi, EC2. Flexible Lambda functionality and ML inference.
[FreeRTOS](https://aws.amazon.com/documentation/freertos/) | Extension of the standard FreeRTOS. This is the local OS for nodes in our IoT network for devices with low compute power.| Espressif, Microchip, NXP, STM and Texas instruments < 128MB of RAM. | FreeRTOS with additional libs attached to carry out AWS service calls. A low level OS.
[IoT 1-Click](https://aws.amazon.com/documentation/iot-1-click/) |  Simple service invokes Lambda functions with out of the box device support. | The Enterprise, AT&T or Soracom LTE-M [buttons](https://aws.amazon.com/iot-1-click/devices/). | Button invokes Lambda function. Not very powerful or useful compared to other offerings (Only US).
[IoT Analytics](https://aws.amazon.com/documentation/iotanalytics/) |  Managed service to collect and query data from IoT devices with Jupyter notebook and Amazon quicksight integration. | - | Have not explored yet (not available in Sydney).
[IoT Device Management](https://aws.amazon.com/documentation/iot-device-management/) |  Part of the AWS IoT Core feature for onboarding IoT devices, fleet management, serial number management and security policies. | - | Commercial management of devices used in a production environment (larger deployments).

# Current development options (Software, SDKs)
Before deploying we need to understand the AWS infrastructure and potential options. As a new user in of AWS IoT you might find options confusing and documentation split across many areas. There are many examples for setting up each part of the IoT offerings but little information on actual implementation and integration of features.

## General devices and clients interacting with IoT devices:

- [AWS IoT SDK](https://aws.amazon.com/iot/sdk/) on devices
  - This includes the standard Java, Python, NodeJs SDKs that can access most AWS services including IoT Core and Greengrass
- [Greengrass Software](https://docs.aws.amazon.com/greengrass/latest/developerguide/gg-config.html)
  - Standard Greengrass Software (only supported on some devices) runs the greengrass daemon
  - Will need to use other AWS SDKs if devices are not supported

## IoT devices at the edge (sensors and lower level hardware) 
**These require upload and download of sensor data and config including OTA, Sync, etc**

- [AWS FreeRTOS](https://github.com/aws/amazon-freertos) operating system.
    - Access to a lower level control on our IoT devices.
    - Low level usage requires knowledge of IoT hardware, FreeRTOS and C.
    - Manual setup.
- [MongooseOS](https://mongoose-os.com) (official partner and backed by AWS).
    - Integrated browser IDE, build tools and config setup.
    - Javascript primarily (C for flexibility of additional libraries if needed).
    - Automated setup.
- Other options:
    - [Platform.IO](https://platformio.org/)
    - [MicroPython](https://micropython.org/)


# The Plan: Low-level meets High-level
## See part 2 for POC

I started off my experiment with a number of devices: This included the [Espressif](https://espressif.com/) ESP32, ESP16, ESP8266 and the more powerful [Raspberry Pi Zero](https://www.raspberrypi.org/products/raspberry-pi-zero/). These are well known boards with great flexibility, portability and ease of development at low costs.

These devices can run a number of operating systems and need to be securely and reliably connected to a the network for data storage and updates. When developing with these devices you are usually required program low-level device sensor integrations, network management, synchronisation and memory management. All in the space of a tiny device with limited compute power, memory and storage. Abstracting away the low level internals with a higher level language potentially speeds up development of POCs and test infrastructure. This is the reason I have decided to explore a number of higher level libraries and IoT based IDEs/tools such as [MongooseOS](https://mongoose-os.com) and [MicroPython](https://micropython.org/).

## Idealised POC Infrastructure:

![Overview of the idealised IoT POC infrastructure](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/07/idealised_struct.png)

**Example data structure relating to device state:**
```Javascript
//Message sent to AWS IoT Core from sensor device
{
    "message": {
        "device": "123456abcde",
        "temp": "20",
        "humidity": "10"
    }
}

//Device Shadow State
{
    "state" : {
        "desired" : {
            "sendData" : "true",
            "ledOn" : "false"
         }
     }
}
```

`sendData` and `ledOn` are used to managed individual device settings. If `sendData` is false the sensor data will not be sent to AWS.

## MongooseOS device code:

[Code snippet](https://github.com/mongoose-os-apps/example-dht-js/blob/master/fs/init.js) for reading from the DHT11 humidity and temperature sensor used in our POC. You can see the flexibility and power provided by MongooseOS! 

_Note: MongooseOS uses an embedded C++ powered subset of Javascript created by MongooseOs called [mjs](https://github.com/cesanta/mjs)_

```javascript
// Load Mongoose OS API
load('api_timer.js');
load('api_dht.js');

// GPIO pin which has a DHT sensor data wire connected
let pin = 16;

// Initialize DHT library
let dht = DHT.create(pin, DHT.DHT22);

// This function reads data from the DHT sensor every 2 seconds
Timer.set(2000, Timer.REPEAT, function() {
  let t = dht.getTemp();
  let h = dht.getHumidity();

  if (isNaN(h) || isNaN(t)) {
    print('Failed to read data from sensor');
    return;
  }

  print('Temperature:', t, '*C');
  print('Humidity:', h, '%');
}, null);
```

# Initial Impressions
- Heavily slanted towards MQTT and the Raspberry Pi. No other devices or messaging protocols are mentioned in such a way.
- Lambda is very powerful on Greengrass devices. This this allows the portability of Lambda code on Greengrass devices, EC2 instances and any supporting Greengrass devices. Also reduces development time.
- Very little is talked about MongooseOS or other providers. However, MongooseOS is very polished and provides an easy deployment environment and automatic setup.
- Greengrass OTA updates is an important and much needed feature for IoT devices to counter the concerns of security and update regularity in devices (eg. Lacking Android and Router updates).
- I enjoyed the automatic deployment and cert setup but didn’t enjoy the typing for RTOS setup (Surely this could be automatically set up?) and pulled down from AWS according to the device used.
- Only a Windows simulator for RTOS (no Linux support yet).
- WebSockets, HTTPs and MQTT only.
- Lack of integration documentation. Most documentation focuses on different IoT offerings but fails to talk about general setup and wiring of components together.
- Terminology is slightly confusing around the direct splitting of groups, cores and IoT cores. Is a Greengrass core a "thing" as well as a Greengrass Core??
- It’s quite a lot more complicated than originally put forward by AWS and my preconceptions about the services.

# How AWS deals with the challenges of IoT?
 
- Security and Privacy
    - TLS mutual auth
    - Custom authorisers
    - IAM roles
    - Cert management
- Connectivity 
    - Greengrass for management of offline devices
    - OTA Updates
- Compatibility
    - Generalised JSON storage structures
    - SQL query of MQTT
    - Custom Lambda access to Volumes/Devices
- Standardisation
    - MQTT
    - WebSockets/HTTPS
- Intelligent Actions and Analysis
    - Actions
    - Jobs
    - Lambda function flexibility

# How do other cloud providers compare?
 Provider  | Features | + | - | Support 
----- | ------- | --- | --- | ------
AWS | IoT, Greengrass | RTOS and SDK support for varying devices Custom authorisers Professional support. | Little documentation on RTOS or Greengrass, fragmented documentation. | AWS RTOS: 6 boards plus simulator Greengrass.
GCP | IoT Core | JWT auth (Interesting) Limited docs. | No CA checking by [default](https://cloud.google.com/iot/docs/how-tos/credentials/verifying-credentials). Seems to be very limited so far. | Large selection of starter kits supported: 13 kits with individual docs .
Azure | IoT Hub | AMQP, Data simulation docs, Standard MQTT protocol with extensions Clear less ambiguous docs compared to AWS. | Limited SDK support, seems more hobbyist, No CA checking by default, C and Arduino docs. No higher level docs. | 7 non-commercial devices: Hobbyist devices like the adafruit MXChip IoT devkit and VScode extension integrations.

# $$$
Pricing is quite standard across all of the top 3 services we will be using. As at June 2018 AWS IoT it is included in the AWS free tier offer. You can run 1000’s devices very cheaply. AWS RTOS is free of charge to download and use. AWS Greengrass has a slightly different pricing model and is based on the connectivity of each “Core” this is quite likely the most expensive part of the setup as each core will have standard monthly cost associated with it but you won’t have an issue unless you are running 1000’s of Greengrass device connections with large data flows. Overall AWS IoT Core is very cheap unless you are connecting a huge number of devices for commercial operation.

# Only limited to IoT devices?
AWS IoT offerings are not necessarily just for IoT but can be a generalised platform for device state management and messaging across MQTT or WebSockets. There are a number of SDKs provided including Android, Java and Python. As long as the device and language is supported by AWS IoT there are a number of potential use cases. Currently the costs are very low and quite likely lower compared to other services. The flexibility of Lambda with Greengrass and Actions on response to MQTT messages means there is a great deal of adaptability for other mobile devices or applications requiring a managed state, messaging and entity management.

## Part 2:
Thanks for reading! Keep a look out for part two including a small proof of concept and discussions around developement.


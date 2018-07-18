---
authors:
- Ben Edridge
tags:
- AWS
- IoT
- Melbourne
- Australia
- Greengrass
date: 2018-06-15T11:41:57.000Z
title: "Exploring AWS IoT Core and Greengrass Offerings"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/06/iot_devices.png
---

According to [IEEE](https://iot.ieee.org/newsletter/march-2017/three-major-challenges-facing-iot.html) The biggest challenges and issues facing IoT networks are security, privacy, connectivity, compatibility, standardisation and intelligent actions. This is what is holding us back from large scale developments in IoT. A large number of academic research [papers](https://ieeexplore.ieee.org/abstract/document/7823334/) have discussed the security and privacy aspects aspects of IoT deployments. 

The closer and more interconnected a network is the more strain it puts on security engineers and application developers to perform due diligence and lock down the network infrastructure and maintain ethical privacy practices. AWS provides a number of IoT related services since the release of [AWS IoT Core](https://aws.amazon.com/iot-core/) at the end of 2015. These services are quite complex and try to address a number of these concerns. 

In this two-part series I will look at the different IoT services provided, setup and configuration with various devices and issues encountered during development of a proof of concept. As Dmitri Zimine has discussed in an [acloud guru post](https://read.acloud.guru/aws-greengrass-the-missing-manual-2ac8df2fbdf4) earlier in 2018 AWS IoT core and Greengrass is still a bit of mess. There are a number requirements to setup Greengrass and complexity of AWS calls behind the scenes is no easy task to understand.

# Why am I writing this?
Current AWS offerings for IoT are somewhat fragmented with lacking documentation in some areas and great documentation in others. The AWS dashboard has 6 services as above but the core IoT product offered by AWS appears to be AWS IoT which is for directly managing device infrastructure, shadows and data flow from the IoT network. It also includes Greengrass management interface which is a seperate product in itself. I aim to dissect the structure of AWS IoT and the components of with a broad focus on the full end to end usage. I will compare other offerings by other providers and note some of the difficulties I’ve had through the development of the POC. The source code will available below and can be deployed on your AWS account provided you have access to the resources and are happy with being charged a small amount. Even though AWS IoT is not a recent offering there is still limited information or documentation related to it. The StackOverflow family has limited answers and “Googling” like most doesn’t always get you where you want to be!

# What will I be talking about?
1. AWS IoT services and how they relate to current infrastructure, the theory, the practicality and how it works
2. Working with a small POC utilising a number of AWS IoT services
3. Reviewing what has been built, the concerns, ambiguities and issues with what went wrong.
4. Some links, resources and source code

# AWS IoT Configuration
AWS provides a hub like [infrastructure](https://docs.aws.amazon.com/iot/latest/developerguide/what-is-aws-iot.html) which incorporates IoT devices as “Things” in the network. This network is based heavily on “Shadows” of device state and the “MQTT” network protocol for sending and receiving messages. The “shadow” is simply a “JSON” document containing specified and expected device state. These devices are categorised into either a control/management devices called a “Greengrass Core” or lower powered device called a “Thing” which interacts with “Cores” within a Greengrass group using the AWS SDK. The terminology is somewhat confusing with “Greengrass cores”, “Greengrass groups”, “IoT core”, “Things” etc. The questions you might have are Greengrass cores things? Can a Greengrass core also be a thing. The diagram below will hopefully explain the structure. dia

These lower powered devices usually have a CPU < 1Ghz and interface directly with sensors and the environment sometimes with limited connectivity.  The “Greengrass Core” has a custom “Greengrass” daemon and a number of builds depending on the platform. There is support for Raspberry Pi, EC2, x86, Arm. The lower powered “Thing” devices can use AWS RTOS, AWS SDKs or the REST API to interact with AWS services. 

Protocols supported are MQTT  and HTTP with Websocket support. Both Greengrass cores, groups and IoT things are managed through the same UI and appear to be very similar with the exception of additional interfaces for adding “Cores” and specific groups for [Greengrass](https://docs.aws.amazon.com/greengrass/latest/developerguide/what-is-gg.html#gg-platforms).

![Overview of the planned IoT POC infrastructure](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/07/iot_devices.png)


Product | Summary | SDKs/HW | Verdict
------- | ------- | ------- | ------
IoT Core | AWS management interface, IoT device SDK, Device Gateway, Message brokering, Authorization, Device Registry, Rule engine and Device shadowing| Python, Java, Android, C, C++, Javascript| The crux of the AWS IoT offerings, the management interface
Greengrass | Works as an extension of IoT core and runs on the devices providing local compute, message, sync and ML inference. For the x86/Arm >1GHz (either ARM or X86), 128MB+ of RAM | Raspberry Pi, EC2, Texas Instruments | A controller and SDK provided with more compute power tacked on as a local compute, OTA and management device can be anything really. Ubuntu, Raspberry Pi, EC2
FreeRTOS | Extension of the standard FreeRTOS. This is the local OS for nodes in our IoT network for devices with low compute power < 128MB of RAM | Espressif, Microchip, NXP, STM and Texas instruments | FreeRTOS with additional libs attached for AWS interactions. A low level OS.
IoT 1-Click |  Simple service invokes Lambda functions with out of the box device support | The AT&T LTE-M button | Button invokes Lambda function -> Only US (A toy)
IoT Analytics |  Managed service to collect and query data from IoT devices with Jupyter notebook and Amazon quicksight integration | - | Haven’t explored so much (not available in Sydney)
IoT Device Management |  Part of the AWS IoT Core feature for onboarding IoT devices, fleet management, serial number management and security policies| - | Basically IoT core. More commercial management of devices in a production environment Large deployments

# $$$
Pricing is quite standard across all of the top 3 services we will be using. As at June 2018 AWS IoT it is included in the AWS free tier offer. You can run 1000’s devices very cheaply. AWS RTOS is free of charge to download and use. AWS Greengrass has a slightly different pricing model and is based on the connectivity of each “Core” this is quite likely the most expensive part of the setup as each core will have standard monthly cost associated with it but you won’t have an issue unless you are running 1000’s of Greengrass device connections with large data flows. Overall AWS IoT Core is very cheap unless you are connecting a huge number of devices for commercial operation.

# Low-level meets High-level

I started off my experiment with a number of devices: This included the Espressif](https://espressif.com/) ESP32, ESP16, ESP8266 and more powerful [Raspberry Pi Zero](https://www.raspberrypi.org/products/raspberry-pi-zero/). These are well known boards with great flexibility, portability and ease of development at little cost. 

These devices can run a number of operating systems and need to be securely and reliably connected to a network service for data storage and updates. Development with these devices you usually require programming low-level device sensor integration, network management, synchronisation and memory management. All in the space of a tiny device with limited compute power, memory and storage. Abstracting away the low level internals with a higher level language potentially speeds up development of POCs and test infrastructure. Hence the reason I have decided to explore a number of higher level libraries and IoT based IDEs and tools such as [MongooseOS](https://mongoose-os.com) and [MicroPython](https://micropython.org/)

[Code snippet](https://github.com/mongoose-os-apps/example-dht-js/blob/master/fs/init.js) for reading from the DHT11 humidty and temperature sensor used in our POC

Note: MongooseOS uses a subset of JavaScript called mJS
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

# Current deployment options (Software, SDKs)
First we need to understand the infrastructure deployment options. As a new user in AWS IoT deployment you might find options somewhat confusing and spread across many documents. There are many example for setting up each part of the IoT network but little information on actual implementation and integration of features.

**For general devices and clients interacting with IoT devices,infrastructure includes the following:**

- Standard AWS IoT SDKs on devices
  - This includes the Java, Python, NodeJs SDKs that can access most AWS services including the IoT services
- Greengrass SDKs with supported devices
  - Standard Greengrass SDKs (only supported on some devices)
  - Will need to use other AWS SDKs if devices are not supported

**For IoT devices at the edge interfacing with sensors and lower level hardware. These require upload and download of sensor data and config including OTA, Sync, etc.**

- [AWS FreeRTOS](https://github.com/aws/amazon-freertos) operating system
    - Access to a lower level control on our IoT devices
    - Low level requires knowledge of IoT hardware,  FreeRTOS and C
    - Manual setup
- MongooseOS (official partner and backed by AWS)
    - Integrated browser IDE environment
    - Javascript and C for flexibility of higher level abstractions with C libraries
    - Automated setup
- Other options like:
    - [Platform.IO](https://platformio.org/)
    - [MicroPython](https://micropython.org/)


# The other providers
 Provider  | Features | + | - | Support 
----- | ------- | --- | --- | ------
AWS | Iot, Greengrass | RTOS and SDK support for varying devices Custom authorisers Professional support | Little documentation on RTOS or Greengrass, fragmented documentation | AWS RTOS: 6 boards plus simulator Greengrass
GCP | Iot Core | JWT auth (Interesting) Limited docs | No CA checking by [default](https://cloud.google.com/iot/docs/how-tos/credentials/verifying-credentials) Seems to be very limited so far. | Very little information on device support. Where is it?
Azure | IoT Hub | AMQP, Data simulation docs, Standard MQTT protocol with extensions Clear less ambiguous docs compared to AWS | Limited SDK support, seems more hobbyist, No CA checking by default, C and Arduino docs. No higher level docs. | 7 devices non-commercial more hobbyist devices like adafruit MXChip IoT devkit with Visual Studio code extension integrations

# Initial impressions
- Heavy slant towards MQTT and the Raspberry Pi, no other devices are mentioned in such a way.
- Lambda is very powerful on Greengrass but seems a bit strange to implement on the Greengrass SDK device why not just write some native code? Potentially this allows the portability of Lambda code on Greengrass devices, EC2 instances and any supporting Greengrass devices. However, actual Greengrass isn’t supported at scale. Due to manual configurations settings
- Very little is talked about MongooseOS or other providers. However it is very polished, provides an easy deployment environment and automatic cloud setup and is also an AWS partner
- Greengrass OTA updates is very important one of the much needed features for IoT devices to counter the concerns of security and update regularity (eg. Android end of life support) and non recurring router updates
- Some parts are very well done, other parts aren’t so polished. I enjoyed the automatic deployment and cert setups but didn’t enjoy all the typing for RTOS setup (Surely this could be automatically set up?) and pulled down from AWS
- Only windows simulator for RTOS no Linux support yet
- Websockets, HTTPs and MQTT only,
- Lack of integration docs, most documentation talks about various aspect of IoT offerings but fails to talk about general setup and wiring of infrastructure together
- Terminology is slightly confusing around the direct splitting of groups, cores and IoT cores. Is a Greengrass core a "thing" as well?
- It’s quite a lot more complicated than originally put forward by AWS and my preconceptions about the services

# Combatting the challenges of IoT

AWS has a number of features that address certain aspects of the above IoT challenges
Challenge | Enablers 
------- | -------
Security and Privacy | TLS mutual authentication, custom authorizers
Connectivity | Greengrass for connectivity management, OTA Update
Compatibility | Generalised JSON storage structures, SQL query of data structures, Custom Lambda access to Volumes/Devices
Standardisation | MQTT, WebSockets/HTTPS
Intelligent Actions and Analysis | Actions, Jobs, Lambda flexibility, 

# Only limited to IoT devices?
Not necessarily, AWS IoT can be a generalised platform for device state management and messaging across MQTT or Websockets. There are a number of SDKs provided including Android, Java and Python. As long as the device and language has support by AWS IoT there are a number of potential use cases. Currently the costs are very low and quite likely lower compared to other services. The flexibility of Lambda with Greengrass and Actions on response to MQTT messages means there is a great deal of adaptability for other mobile devices or applicates requiring a managed state, messaging and entity management.

## Part 2:
Thanks for reading! Keep a watch for with a small proof of concept.

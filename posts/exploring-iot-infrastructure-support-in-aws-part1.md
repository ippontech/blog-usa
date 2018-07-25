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
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/07/aws_iot_esp_device.jpeg
---

According to [IEEE](https://iot.ieee.org/newsletter/march-2017/three-major-challenges-facing-iot.html) the biggest challenges and issues facing Internet of Things (IoT) are security, privacy, connectivity, compatibility, standardisation and intelligent actions/analysis. This is what is holding us back from world-wide developments in IoT. A number of recent academic research [papers](https://ieeexplore.ieee.org/abstract/document/7823334/) have discussed the security and privacy aspects of IoT deployments. 

The closer and more inter-connected a network is the more strain it puts on developers to lock down the network and maintain ethical privacy practices. AWS provides a number of IoT related services since the release of [AWS IoT Core](https://aws.amazon.com/iot-core/) at the end of 2015. These services are quite complex and try to address a number of these aforementioned challenges. Ideally developers want to spend most of their time developing the business logic of IoT application and not the supporting infrastructure.

Dmitri Zimine wrote an interesting post on [A Cloud Guru](https://read.acloud.guru/aws-greengrass-the-missing-manual-2ac8df2fbdf4) earlier in 2018 discussing AWS IoT core and [Greengrass](https://docs.aws.amazon.com/greengrass/latest/developerguide/what-is-gg.html#gg-platforms) as being a bit disorganised and quite complicated in the background. Current AWS offerings for IoT still seem to be somewhat fragmented. The AWS dashboard has 6 services but the key product offered by AWS appears to be AWS IoT Core which is for direct management of device infrastructure, device shadows and data flow within the network. It also includes the Greengrass management interface in the UI which is actually a separate product in itself.

# What will I be talking about?

In this two-part series I will look at the AWS services provided for IoT and the configuration required for setup. I aim to dissect the structure of AWS IoT Core and Greengrass with a broad focus on full end-to-end usage. I will be creating a simple proof of concept as below and an Android companion application connecting through AWS Cognito.

The ESP device will be connected to a DHT11 temperature and humidity sensor sending data to AWS over the MQTT protocol to "IoT Core". This will update the "Device Shadows" within "IoT Core". "Rules" will also be created to select information from the JSON structure of MQTT messages received and carry out "Actions". The action performed will be saving the data to S3. Once an ESP device is connected and data is being output the Android application can connect to IoT Core to access Shadow states containing device metadata and current sensor data.

In part two I will be discussing the proof of concept with further examples based on the chosen SDKs, software and hardware. I will be evaluating the development process, options for Terraform/Cloudformation and discussing what I liked and did not like about the current state of AWS IoT. Finally I will be finishing off with some useful resources I found for further experimentation and learning.

![Overview of the idealised IoT POC infrastructure](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/07/aws_iot_idealised_poc.png)

_The source code will be available in the part 2 blog post. Note: Resource usage on AWS will charge your account._

# The Experiment

I started my experiment with a few variations of boards using the [Espressif](https://espressif.com/) ESP32, ESP16, ESP8266 chips and the more powerful full board [Raspberry Pi Zero](https://www.raspberrypi.org/products/raspberry-pi-zero/). These are well-known devices with great flexibility, portability and ease of development at low costs.

To enable data storage and updates we need to be securely and reliably connected to a the network. In IoT there are often requirements to work with low-level device sensor integrations, network management, synchronisation logic and memory management. All of this in the space of a tiny device with limited power, memory and storage! Abstracting away the low-level internals with a higher level language can potentially speed up development. This is the reason I have decided to explore a number of higher level libraries and IoT based IDEs/tools such as [MongooseOS](https://mongoose-os.com) and [MicroPython](https://micropython.org/).

# IoT Configuration

AWS provides a hub like [infrastructure](https://docs.aws.amazon.com/iot/latest/developerguide/what-is-aws-iot.html) which incorporates IoT devices as "[Things](https://docs.aws.amazon.com/iot/latest/developerguide/iot-thing-management.html)" in the network. "[Device Shadows](https://docs.aws.amazon.com/iot/latest/developerguide/iot-device-shadows.html)" are the core data structure which store a devices state. This state along with additional messages are sent over MQTT network protocol. A shadow is simply a JSON document containing the expected and delta of a devices state. These devices are categorised into either control/management devices called a “Greengrass Core” or lower powered device called a “Thing” which interacts with “Cores” within a Greengrass group using the AWS SDK. The terminology is somewhat confusing with “Greengrass cores”, “Greengrass groups”, “IoT core”, “Things” etc. A Greengrass core is not needed to set up IoT devices but gives added benefits such as OTA updates and local Lambda functions. The diagram below will hopefully explain the structure of AWS IoT offerings. Things are split across both Greengrass groups and normal IoT groups. To setup a Greengrass group you must select a "Thing" to act as the core. The Greengrass core daemon then needs to be installed locally on the device.

![Core AWS IoT infrastructure](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/07/aws_iot_overview.png)

The lower powered devices acting at the edge usually have a CPU less than 1Ghz, interact with sensors and run in a limited environment. The “Greengrass Core” has a custom “Greengrass” daemon and a number of versions depending on the platform. There is support for Raspberry Pi, EC2, x86 and Arm. The lower powered “Thing” devices can also use [AWS RTOS](https://aws.amazon.com/freertos/), AWS SDKs or the REST API to interact with IoT services. 

MQTT and HTTP protocols are used to communicate between devices and back to AWS. Both Greengrass Cores, groups and IoT things are managed through the same UI and appear to be very similar. Greengrass management is actually a subsection of the IoT Core UI. Within the Greengrass UI Lambda functions, Greengrass groups and Resources can be configured.

## AWS IoT Product Overview:

The following table gives my opinions on the AWS IoT services, SDK and a summary of the intended uses.

Product | Summary | SDK/HW | Verdict
------- | ------- | ------- | ------
[IoT Core](https://aws.amazon.com/documentation/iot/) | AWS management interface, IoT device SDK, Device Gateway, Message Broker, Authorisation, Device Registry, Rule engine and Device Shadows. | Python, Java, Android, C/C++, JavaScript. | The crux of the AWS IoT offerings; management interface.
[Greengrass](https://aws.amazon.com/documentation/greengrass/) | Aan extension of IoT core and running on the devices providing local compute, message, sync and ML inference. | Raspberry Pi, EC2, Texas Instruments (x86/Arm >1GHz, 128MB+ of RAM).| A controller and SDK provided with more compute power with some cool toys like Lambda, ML inference and OTA (Very powerful!).  Ubuntu, Raspberry Pi, EC2.
[FreeRTOS](https://aws.amazon.com/documentation/freertos/) | Extension of the standard FreeRTOS. This is the local OS for nodes in our IoT network for devices with low compute power.| Espressif, Microchip, NXP, STM and Texas instruments < 128MB of RAM. | FreeRTOS with additional libs attached to carry out AWS service calls. A low-level OS.
[IoT 1-Click](https://aws.amazon.com/documentation/iot-1-click/) |  Simple service invokes Lambda functions with out of the box device support. | The Enterprise, AT&T or Soracom LTE-M [buttons](https://aws.amazon.com/iot-1-click/devices/). | Button invokes Lambda function. Not very powerful or useful compared to other offerings (Only US).
[IoT Analytics](https://aws.amazon.com/documentation/iotanalytics/) |  Managed service to collect and query data from IoT devices with Jupyter notebook and Amazon quicksight integration. | - | Have not explored yet (not available in Sydney).
[IoT Device Management](https://aws.amazon.com/documentation/iot-device-management/) |  Part of the AWS IoT Core feature for onboarding IoT devices, fleet management, serial number management and security policies. | - | Commercial management of devices used in a production environment (larger deployments).

# Current Development Options (Software, SDKs)

Before deploying we need to understand the AWS infrastructure and potential options. As a new user in of AWS IoT you might find options confusing and documentation split across many areas. There are many examples for setting up each part of the IoT offerings but little information on actual implementation and integration of features.

## General devices and clients interacting with IoT devices:

- [AWS IoT SDK](https://aws.amazon.com/iot/sdk/) on devices
  - This includes the standard Java, Python, NodeJs SDKs that can access most AWS services including IoT Core and Greengrass
- [Greengrass Software](https://docs.aws.amazon.com/greengrass/latest/developerguide/gg-config.html)
  - Standard Greengrass Software (only supported on some devices) runs the Greengrass daemon
  - Will need to use other AWS SDKs if devices are not supported

## IoT Devices at the edge (sensors and lower level hardware): 

_Carries out upload and download of sensor data and config including OTA, Network, etc._

- [AWS FreeRTOS](https://github.com/aws/amazon-freertos) operating system.
    - Access to a lower level control on our IoT devices.
    - Low level usage requires knowledge of IoT hardware, FreeRTOS and C.
    - Manual setup.
- [MongooseOS](https://mongoose-os.com) (official partner and backed by AWS).
    - Integrated browser IDE, build tools and config setup.
    - JavaScript primarily (C for flexibility of additional libraries if needed).
    - Automated setup.
- Other options:
    - [Platform.IO](https://platformio.org/)
    - [MicroPython](https://micropython.org/)

## Device Shadows and State Management

A Device Shadow can also include the additional `desired` and `metadata` attributes which gives us more flexibility in managing state. A `delta` will be calculated based on the `desired` and `reported` state. `metadata` also allows us to keep track of the timestamp of previous changes to values contained in the `desired` or `reported` states.

During our experiment `sendData` and `ledOn` will be used to managed individual device settings. If `sendData` is false the sensor data will not be sent to AWS.

```JavaScript
// MQTT Message sent to AWS IoT Core from the sensor device
{
    "message": {
        "device": "123456abcde",
        "temp": "20",
        "humidity": "10"
    }
}

// Simplified Device Shadow State maintained in IoT core
{
    "state" : {
        "reported" : {
          "sendData" : "true",
          "ledOn" : "false"
        }
    }
}
// ... Additional Device Shadow state
```

## MongooseOS Device Code:

A small [code snippet](https://github.com/mongoose-os-apps/example-dht-js/blob/master/fs/init.js) for reading the DHT11 humidity and temperature sensor. You can see the flexibility and power provided by MongooseOS utilising the specialised `load` and `Timer` functions specific to `mjs`.

_Note: MongooseOS uses an embedded C/C++ powered subset of JavaScript created by MongooseOS called [mjs](https://github.com/cesanta/mjs)_

The `Timer.set` function call runs every 2 seconds getting the current temperature from the DHT11 sensor and packaging up a JSON message to publish to the topic `devices/data`. If the AWS configuration is set up correctly in the MongooseOS IDE you will be able to login to the AWS console and subscribe to the topic to receive messages as in the above code block. For example subscribing to `devices/data` will give the output each time data is sent over MQTT from the device.

```javascript
// Load Mongoose OS API
load('api_timer.js');
load('api_dht.js');
load('api_mqtt.js');

// GPIO pin which has a DHT sensor data wire connected
let pin = 16;

// Initialize DHT library
let dht = DHT.create(pin, DHT.DHT11);

// Sensor data topic for MQTT
let sensorTopic = 'devices/data';

// This function reads data from the DHT sensor every 2 seconds
Timer.set(2000, Timer.REPEAT, function() {
  let t = dht.getTemp();
  let h = dht.getHumidity();
  
  let message = JSON.stringify({
    device: '123456abcde',
    temp: t,
    humidity: h
  });

  // Publishes the message to a MQTT topic on AWS IoT Core  
  MQTT.pub(sensorTopic, message, 1, false);

}, null);
```  
# Initial Impressions

- Lack of integration documentation
    -  Main focus is on different IoT offerings but documentation fails to discuss general setup and wiring of components together.
- Heavily slanted towards MQTT and the Raspberry Pi:
    -  No other devices or messaging protocols are mentioned in such a way.
- Lambda is very powerful on Greengrass devices:
    - Lambda code can be ported to Greengrass devices, EC2 instances and any other supporting Greengrass devices.
    - Reduces development time.
    - Additional features such as Binary input to Lambdas, `/dev/` access and ML inference
- Little or no mention of MongooseOS and other dev environments: 
    - MongooseOS however, is very polished and provides an easy deployment environment with automatic setup.
- Greengrass OTA updates: 
    - This all comes down to device support and requires a complicated setup
    - Hopefully will reduce problems like that of updates to Android and household Routers
- Terminology
    - Slightly confusing terms used for management of Groups, Cores and IoT cores. 
    - Is a Greengrass core a "Thing" as well as a Greengrass Core?
- It’s quite a lot more complicated than originally put forward by AWS and my preconceptions about the services.
    - I have yet to explore Cloudformation and Terraform deeply but I could imagine there might be some inefficiencies in stack deployment.

![Solving the challenges of IoT with AWS](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/07/solving_aws_challenges.png)

# How do other Cloud providers compare?

 Provider  | Features | + | - | Support 
----- | ------- | --- | --- | ------
AWS | IoT, Greengrass | RTOS and SDK support for varying devices Custom authorisers Professional support. | Little documentation on RTOS or Greengrass, fragmented documentation. | AWS RTOS: 6 boards plus simulator Greengrass.
GCP | IoT Core | JWT auth (Interesting) Limited documentation. | No CA checking by [default](https://cloud.google.com/iot/docs/how-tos/credentials/verifying-credentials). Seems to be very limited so far. | Large selection of starter kits supported: 13 kits with individual docs .
Azure | IoT Hub | AMQP, Data simulation docs, Standard MQTT protocol with extensions Clear less ambiguous docs compared to AWS. | Limited SDK support, seems more hobbyist, No CA checking by default, C and Arduino docs. No higher level docs. | 7 non-commercial devices: Hobbyist devices like the adafruit MXChip IoT devkit and VScode extension integrations.

# $$$

Pricing is quite standard across the top 3 services we will be using. As at June 2018, AWS IoT is included in the AWS free tier offer. You can run 1000’s devices very cheaply. AWS RTOS is free of charge for both download and use. AWS Greengrass has a slightly different pricing model based on connected cores. This is quite likely the most expensive part of the setup as each core will have standard monthly cost associated with it. However, you probably will not have an issue unless you are running 1000’s of Greengrass devices with large data flows. Overall AWS IoT Core is very affordable for experimentation and small deployments.

# Only limited to IoT devices?

AWS IoT offerings are not necessarily just for IoT but can be a generalised platform for device state management and messaging across MQTT or WebSockets. There are a number of SDKs provided including Android, Java and Python. As long as the device and language is supported by AWS IoT there are a number of potential use cases. Currently the costs are very low and quite likely lower compared to other services. Greengrass provides great flexibility with local Lambdas and "Actions" on response to MQTT messages. This gives a great deal of adaptability for other mobile devices or applications requiring a managed state, messaging and entity management. These devices have the potential to modify device state, set actions and store or send data to other AWS services if required.

### Thanks for reading! Check out the part two here

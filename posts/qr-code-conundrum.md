---
authors:
- Brad Halkett
tags:
- QR
- React
date: 2021-07-21T12:21:50.000Z
title: "QR Code Conundrum"
image: 
---
In these interesting times we live in, QR has made quite the comeback (or is this where it took off?). Everywhere we go now in Australia is splattered with QR codes from check-in, dining and information scanning. It is the way to go now to avoid extra human contact; an introvert's dream. 

It is through this lens we ran into a small problem - an up-coming event was to require some form of QR code to allow the event participants to scan event codes to exchange information. The challenge here, we thought, was how to create an app to allow this to happen. We wanted to avoid having to create a mobile app (and thus avoid having to submit it to the gods at Apple and Google to approve - are we pitiful mortals worthy?) and also allow the users to use their own phone (and thus avoid having to loan / share devices in these covid days). 

Ideally creating a small web app would be perfect - easy to implement, deploy and host. But could you create a QR scanner in web form? Surely not? Turns out it is far easier than you probably expect. Thanks to some helpful open source libraries it is rather achievable.

The below is going to demonstrate a small working example of a QR scanner in web form. To follow along you can use the steps. Please keep in mind that the below is a BASIC example and obviously if you want to use it in a full application it would require some more thought. Our fully working application links the QR data back via AWS Amplify as an example.

## QR Scanning Demo

For the purposes of this blog I will assume you have npm installed. The below steps will walk through the setting up the example and then how it works.

Firstly clone the code from the git:

```bash
git clone https://github.com/bradooles/qrscan-demo
```

Now install all the packages required:

```bash
npm install
```

You should ready to run the app locally:

```bash
npm start
```

Once compiled the app should open up in a browser and if not should be available on [http://localhost:3000](http://localhost:3000)

![QR Scanner Open](https://github.com/bradooles/blog-usa/blob/master/images/2021/07/qr-code-conundrum-1.png)
>Hopefully see yourself and not me
<br/>

You may be prompted to allow camera access on your device. Once accepted you should see a handsome individual staring back at you (assuming you do this on your laptop). Now grab your phone / tablet and browse to this blog and find the QR code below. If you are feeling particularly dangerous you can also use Dr. Google to find one to test with as well - the wikipedia one works fairly well.

![Dr evil I presume](https://github.com/bradooles/blog-usa/blob/master/images/2021/07/qr-code-conundrum-2.gif)
>Me using random QR codes to test this app with
<br/>

![QR code](https://github.com/bradooles/blog-usa/blob/master/images/2021/07/qr-code-conundrum-3.png)
>A fancy QR code
<br/>

Now you have the above QR code or one you have found on the internet, hold it up to your laptop camera whilst you have the app up and running - Like magic it should scan and display the information in a popup on screen. You now have a working example of a QR scanner in web form!


## How does it work?

The demo is based off the open source [ZXing library](https://github.com/zxing-js/library) provided by Google. The libraries themselves have been setup for multiple different scanning options, allowing for video scanning, image scanning, decode scanning,  etc. We are using the decode video option in our case. When the app starts it grabs the device of an available camera and starts scanning actively. When you provide it a QR code, it then registers that it has found a result and provides the QR data.

For the demo we have setup the app to work in React. React is a decent choice to use as it lets you react to all the scanning results (GET IT … REACT … I’ll leave now). The react states are super useful to control what is happening on screen. When a QR code is found by the decode mechanism we have setup a simple popup (react-skylight - a simple popup tool for react apps) to display the result back to the user.

We have left in the option to change camera in the demo - whilst it is not really a core feature of what we wanted to demonstrate it will hopefully give some idea on how to manipulate cameras on devices with more than one (our full application is mainly used on phones and was important to be able to switch between front and back camera). You will notice in the code that we have added a simple check on the device labels to see if it is label ‘back’ so we can attempt to default the camera to the back one upon initial load. There is some functionality in the libraries to use the default camera on the device but you need to logically set this up with your code (and as you will read below it does come with some fun problems from your favourite fruit related manufacturer).

One thing of note to pass on is that whilst the libraries do work on the major OSs, iOS is a bit more prickly about using its hardware - it will force users to accept the use of the camera in the app - despite it being used in the browser. It doesn’t break anything HOWEVER you will need to account for the fact that some users (let’s be real here, by some users I mean most users) will need to agree to the camera being used - and until they do so the app will not recognise any available cameras on the device. From the app's point of view there will be no registered cameras until the user agrees. You will need some functionality in your app to deal with this. We found react was helpful here to allow the page to “reload” upon state changing.

![Sadness](https://github.com/bradooles/blog-usa/blob/master/images/2021/07/qr-code-conundrum-4.gif)
>Me after realising that iOS was going to be annoying to deal with
<br/>

## Get your QR on!
As you can see it’s fairly easy to start using a QR scanner with your device. The ZXing libraries are quite robust and shockingy responsive (maybe it is just me but I fully expected some crazy delay). From our own experience we have found using the above libraries to work really well in an app context, so much so that I would absolutely use them again to solve a QR scanning problem. In particular it made the implementation of a web application where QR scanning was a main function quite easy to develop.

Below are the various links to references in this blog.

Until next time, happy QR scanning and good luck!

## References

* [ZXing Project](https://github.com/zxing/zxing)
* [ZXing JS library](https://github.com/zxing-js/library)
* [React Skylight](http://marcio.github.io/react-skylight)

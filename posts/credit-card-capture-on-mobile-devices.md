---
authors:
- Ryan Hill
categories:
- 
date: 
title: "Credit Card capture on mobile devices"
image: 
---

This page is to explain the basics of adding the proper HTML5 tags and attributes to your purchase path to allow the screen capture function on iOS (safari) and Android.

What you need.

Input tags should be wrapped in an HTML <form> element  
 The proper input field type and attributes

- <input type=”text” id=”ccName” name=”name” autocomplete=”cc-name” x-autocompletetype=”cc-full-name”>
- <input type=”text” inputmode=”numeric” autocomplete=”cc-number” x-autocompletetype=”cc-number” name=”cardnumber” id=”cardNumber”>
- <input type=”text” inputmode=”numeric” autocomplete=”cc-exp-month” x-autocompletetype=”cc-exp-month” maxlength=”2″ name=”ccmonth” id=”cardExpirationMonth”>
- <input type=”text” inputmode=”numeric” autocomplete=”cc-exp-year” x-autocompletetype=”cc-exp-year” maxlength=”4″ name=”ccyear” id=”cardExpirationYear”>
- <input type=”text” inputmode=”numeric” maxlength=”4″ autocomplete=”cc-csc” x-autocompletetype=”cc-csc” name=”cvc” id=”cvc”>

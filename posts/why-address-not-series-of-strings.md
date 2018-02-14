---
authors:
- Justin Risch
categories:
- Data
- Meaningfulness
date: 2017-04-11T14:42:35.000Z
title: "Why is an Address NOT a (series of) Strings?"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/04/Why-is-an-address-not-a--series-of--strings--Blog.png
---

In my article [Why an Address is NOT a String](http://blog.ippon.tech/why-address-not-string/), we discussed the complexity added when you store your address as a single string. Since then, I've discovered another, slightly worse way of storing addresses: As a ***series*** of strings. 

When you store an address as a single field, you can assume some information about it -- for example, that it's a correctly formatted mailing address. You can usually send it, as is, to Google API to get a response and look up more information about it (such as the increasingly useful geolocation of the address). 

And so enters the other way of doing it -- several strings. In our "Location" table, we have the following string fields: Address, Address2, Address3, City, Zip, and StreetNumber. Let's begin down this rabbit hole by discussing what each field represents.

City and Zip are properly named -- they represent the nearest city and the postal code of the address. StreetNumber is essentially the number portion of a street address (the '123' in '123 Baker Street'). It's a little misleading, since it's not necessarily a "Number" (it could be 2A), but at least we conceptually know what that data represents. 

Address, Address2, and Address3 are a beast of their own. They represent the first, second, and third lines that one would write in the event they were sending a letter to this address. In many cases, Line 1 would be the route or street name (without the number, stored separately). The second line would be Apartment number or such. The third is extra. However, in other cases, Address represents the name of the building or person to deliver it to, forcing Address2 to become the route/street, and Address3 to become the apartment. That's just for America, though. Don't worry -- it gets worse. 

In Japan, they write their addresses with the Country and Zip in the first line, going to the more specific as they get to the bottom line. 
```
〒100-8994
Tōkyō-to Chūō-ku Yaesu 1-Chōme 5-ban 3-gō
Tōkyō Chūō Yūbin-kyoku
```
But that's only when writing in Japanese. If using Roman letters, they switch to use western conventions, meaning that in a single country, there are cases where the language used changes the validity of the output. The Address below is the same as the one above, just written in Roman letters.

```
Tokyo Central Post Office
5-3, Yaesu 1-Chome
Chuo-ku, Tokyo 100-8994
```

And so, we began integrating with Google Maps API to try to standardize the fields as much as we could. Rather than the vaguely defined "Address, Address2, Address3," we have put into place new fields: Route, Administrative Level (1 through 5), Locality, Sublocality (1 through 5), and so on. 

The end result? Roughly 20 fields from the JSON that Google Maps API returns, the majority of which are never used except in extremely rare cases. This means that the UI displaying this table is large, and some of the fields are not very user friendly (Administrative Level 1 doesn't mean much to the average user, when it simply means "state" in the US or "district" in many other countries -- the first level of government under "country"). 

How could this have possibly been avoided?

1. **Define your Strings well**. If you're dead-set on breaking an address into its parts, then at the very least follow the convention that a field's label should always describe what is going in it. "Address2" did not describe the data within that column but rather how it was used (that it was the second line you would write on a letter). "Administrative Level 1" is far superior, as rather than describe its function in a particular use case, it describes what kind of data is in that column -- a "state." Also, avoid colloquiums and localized descriptions. While "county" might make more sense to you than Administrative Level 1, Administrative Level 2 *does not mean county in all use cases.* Even the API itself says that "Not all nations exhibit these administrative levels." That means that, by being more descriptive, you've actually introduced cases in which you are simply incorrect. The brilliant minds at Google have created a system of breaking an address down into meaningful strings and, if you're going to do it, I suggest you [follow their lead.](https://developers.google.com/maps/documentation/geocoding/intro)

2. **Store it as a String, but also with coordinates**. It's much better to have 3 columns -- a properly formatted "mailing address" adhering to the conventions of the country that it is in, and two double values representing the latitude and longitude of the address. The formatted mailing address is really only to be used in the event you need to physically mail something there or to display it on screen. For anything else -- be it comparing two addresses, displaying it on a map, or really anything that a computer will do for you, you should be referring to the longitude and latitude for speed and precision. After all, it only needs to be human readable if it is, in fact, being read by a human. The resulting table will be smaller, more readable at a glance, and not require extensive amounts of logic to take a dozen or more disparate fields and generate a valid mailing address. 

3. **Don't do it at all**. You could also simply store the longitude and latitude, then look up the resulting address in real-time through an API. This is not my preferred solution (use 2), but it would work in the majority of use cases. This is not the best simply because you would be making far more API calls; it's much better to make the API call once on creation, then store the resulting "formattedAddress" provided by the Google Maps API (as in solution 2). Solution 3 does, however, mean your formatted address is always up-to-date, in the event a street name, building name, or other field changes. 

If something in this article piqued your interest and you would like more information on the services we offer at Ippon USA, we’d love to hear from you! Please contact us at contact@ippon.fr with your questions and inquiries.

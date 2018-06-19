---
authors:
- Ahmed MSOUBER
categories:
- 
date: 2016-03-08T16:31:00.000Z
title: "Data Meaningfulness and Usability: Does your data mean what it says?"
image: 
---

There are two metrics you can use to measure the quality of your database; how meaningful is your data, and how much use can you derive from it? Data’s most basic use is storage. The second, more interesting use is analysis. The goal of this article is to understand how to store data to maximize the analysis on the data for future uses.

Take a data type of a String that represents a full name. While this is a fine way to store the name, there is a more dynamic way to store a name. By splitting a full name into two Strings, comparisons can be more dynamic. If names are duplicated due to spelling errors (Jon Doe versus John Doe) then a check can be made to determine the chance that the names belong to the same person.  Then the data can be manually corrected.

Age is useful but only in one format – a date of birth. Storing age as an integer or String makes the data less usable,and less realistic as your age changes over time. Calculating age from a date is rather simple; in comparison age stored as String or integers must be updated yearly.

Another example is an address, stored as a String. Which is fine for a human, as they can recognize what the address means. However a computer views the String as an array of characters, and nothing more. A more meaningful way to store the address is latitude and longitude as two doubles, which would allow the program or software to use them in more ways. A further explanation of this is given in “[Why is an address not a String?](https://blog.ippon.tech/blog/address-not-string-applying-concepts-data-meaningfulness-usefulness/)” by Justin Risch.

Essentially, this makes the data more comparable. 2700 E Cary St, Richmond VA 23220 and 50 Pear St Richmond VA 23220 are in fact the same place; if the address is only stored as a string, you could have two data points for the same physical location. Many houses on the borders of two towns run into the same issues (North Chesterfield can also count as Richmond).Comparing strings only allows for seeing if two locations are called the same. By using coordinates, the distances between those two locations can be calculated. Using coordinates adds another layer of usability, using reverse geolocation to determine the String address of the coordinates. In the event of a street name changing, instead of a String becoming outdated, the coordinates stay constant.

As well, storing as two doubles may actually save you space! In Java, a </span>[<span style="font-weight: 400;">String is at least 40 bytes</span>](http://www.javaworld.com/article/2077496/testing-debugging/java-tip-130--do-you-know-your-data-size-.html), while </span>[<span style="font-weight: 400;">two doubles is only 16 bytes together.](http://www.javacamp.org/javaI/primitiveTypes.html) That may seem trivial, but that is per data point. If you have one million instances of this in your database, you’d be saving 24MB in this one optimization. We’ll be going over more such optimization in a later discussion on reducing redundancy.

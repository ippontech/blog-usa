---
authors:
- Laurent Mathieu
categories:
- Java 8
date: 2014-04-22T09:00:44.000Z
title: "Java 8 - Time Management"
id: 5a267e57dd54250018d6b5c8
image: 
---

By Pierre Templier – [@ptemplier](http://www.twitter.com/ptemplier)

Java 8 includes a brand new time management API named **java.time** that was specified by the [JSR 310](https://jcp.org/en/jsr/detail?id=310) and implemented by the [threeten project](http://www.threeten.org/).

There are three time management APIs in Java 8:

- [java.util.Date](http://download.java.net/jdk8/docs/api/java/util/Date.html) (since jdk 1.0)
- [java.util.Calendar](http://download.java.net/jdk8/docs/api/java/util/Calendar.html) (since jdk 1.1)
- [java.time](http://download.java.net/jdk8/docs/api/java/time/package-summary.html) (jdk 1.8)

# Before Java 8

This new API intends to resolve issues that plagued the two first ones:

- Years are stored in two digits starting in 1900, which leads to cumbersome +1900/-1900 operations to use these values
- Month starts at 0, which forces developers to add +1 to convert them to human-readable numbers
- The Date and Calendar objects are mutable. To avoid unwanted modifications, one has to make defensive copies via clone()
- Unintuitive naming: Date is a date and time, not a date
- Many deprecated methods and constructors because of bugs
- APIs are hard to use and not intuitive
- Not thread-safe: cause of concurrency bugs
- Incomplete concepts: no duration, period, interval hour (hours-minutes-seconds), date (year-month-day), etc.

java.time was inspired by [Joda-Time](http://www.joda.org/joda-time/) and was mostly written by its author, [Stephen Colebourne](http://blog.joda.org/).
 The new API was built from scratch within Java 8 to avoid carrying over [Joda-time issues](http://blog.joda.org/2009/11/why-jsr-310-isn-joda-time_4941.html):

- Poor internal presentation of dates as ‘instant’. A date&time can correspond to multiple instants during a daylight saving time change or it may not exist (when going from 3am to 2am or 2 to 3), which leads to complicated calculations
- Too flexible. Each class can accept a calendar different from the gregorian calendar. java.time uses an global ISO calendar system (gregorian) by default.
- Accepts null without throwing an error for most methods, which makes code error-prone

java.time aims to have a simpler and more robust design than Joda-Time. This API is also available in Java 7 through a [backport](https://github.com/ThreeTen/threetenbp) available at the [central maven](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.threeten%22%20AND%20a%3A%22threetenbp%22). The fluid API allows the manipulation of immutable and thread-safe objects in order to avoid errors. The time management classes are organized according to two views of time: machine time and human time. To make the transition less painful, it is easy to convert java.util.Date and java.util.GregorianCalendar objects to and from the new API.

# Machine Time

## Instant

A precise point in time.
 The Instant class represents the number of nanoseconds since Epoch (January 1st 1970). It is very similar to java.util.Date.

```language-java
Instant.ofEpochSecond(1395100800);
Instant.parse("2014-03-18T00:00:00.000Z");
```

## Duration

Duration between two instants or duration in days/hours/minutes/seconds/milliseconds/nanoseconds.

```language-java
Duration.ofDays(5);
Duration.of(5, ChronoUnit.DAYS);
Duration.between(Instant.parse("2011-07-28T00:00:00.000Z"),
Instant.parse("2014-03-18T00:00:00.000Z")).getSeconds();
```

# Human Time

## Timezone

Use Timezone to create ZonedDateTime and OffsetDateTime from an instant through the ZoneId and ZoneOffset classes.
 ZonedDateTime and OffsetDateTime are the equivalent of GregorianCalendar.

```language-java
ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("Europe/Paris"));
OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("GMT+1"));
OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.of("+01:00"));
```

## LocalDateTime

Date and time (without the timezone concept).

```language-java
LocalDateTime date = LocalDateTime.of(LocalDate.of(2014, Month.MARCH, 18), LocalTime.MIDNIGHT);
// formatage
date.format(DateTimeFormatter.ISO_DATE);
date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
//parsing
LocalDateTime.parse("18/03/2014 00:00", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
// manipulation
date.withHour(16).plusWeeks(3); // Set time at 16:00 and we add 3 weeks
date.with(TemporalAdjusters.firstDayOfNextMonth()); // the first day of the next month
date.with(Temporals.nextWorkingDay()); // TemporalAdjuster in threeten-extra (not included in the jdk)
```

## Partial dates and times

An incomplete LocalDateTime: 03/18 (March 18th), 12:00 (noon).

```language-java
LocalDate.of(2014, Month.MARCH, 18);
LocalTime.of(12, 0); // same as LocalTime.NOON
DayOfWeek.of(2);// DayOfWeek.TUESDAY
Month.of(3); // same as Month.MARCH
MonthDay.of(Month.MARCH, 18);
YearMonth.of(2014, Month.MARCH);
Year.of(2014);
```

## Period

“Human” representation of a duration, for instance 1 month (whether it has 28 or 31 days).

```language-java
Period.ofMonths(3);
// period between Java 7 and Java 8
Period.between(LocalDate.of(2011, Month.JULY, 28), LocalDate.of(2014, Month.MARCH, 18)); // 2 years, 7 months and 18 days
```

## java.time, Date, GregorianCalendar interoperability

It is easy to convert an object to and from the new API.

```language-java
Date date = new Date();
date.toInstant();
Date.from(Instant.now());
GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
gregorianCalendar.toZonedDateTime();
gregorianCalendar.toInstant();
GregorianCalendar.from(ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("Europe/Paris")));

```

# Conclusion

As the code samples demonstrate, this new API corrects issues with previous APIs. It is easy to use thanks to its fluid syntax and intuitive concepts.
 Some of the Joda-time features that have not made it into java.time are now in the [threeten-extra project](http://www.threeten.org/threeten-extra/) (available at [maven central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22threeten-extra%22)), and can be included in your projects.
 One can hope this new API will be quickly integrated in the java frameworks developers use every day.

---
authors:
- Pasha Paterson
tags:
- Java
- data modelling
- coding standards
- design
date: 2019-02-26T18:16:00.000Z
title: "Fundamental Data Model Design"
image:
---

THIS IS A ZEROTH DRAFT AND IS VERY LIKELY CRAP. 

The only thing that most discussions of “standards” agree on is that no one agrees on standards. The world of coding standards, in particular, is perhaps one of the most hotly-contested battlegrounds of computer science. Given the patterns and trends emerging in the usage of Java today, there are certain design and implementation practices that, while they don’t provide an end to anyone’s holy wars, do tend to make the most pragmatic sense. This discussion will focus on design principles in one of the most fundamental of these arenas — how to design and implement a data model that can be used flexibly across multiple contexts in a large application architecture.

A lot of the material in this post is pretty basic, so if you're impatient and want to skip to the new stuff, look for this guy: :running:

# Basic Access and Mutation #

There are basically two different schemes for designing a class that contains a data object: the **bean** pattern, and the **builder** pattern. Both design patterns conceal direct access to member objects behind at least `protected` (and usually `private`) scope, but provide **accessors** and **mutators** to read and write the values of those fields. While beans and builders have become relatively standard over the years, they're both surprisingly clumsy in practice.

## Beans ##

The usual pattern for a bean is to declare all member fields `private` and provide "getters" and "setters" to access those fields. However the usual standard dictates that all such mutators must return `void`.

<details><summary>Example 1: A Bean</summary>

```java
public class Thing
{
    private String m_sID ;
    private String m_sName ;
    private long m_tsCreated ;
    private long m_tsUpdated ;
    
    /** Empty constructors are required by a lot of different frameworks. */
    public Thing() {}
    
    /** Sometimes you want the convenience of a "full" constructor. */
    public Thing( String sID, String sName, long tsCreated, long tsUpdated )
    {
        m_sID = sID ; m_sName = sName ;
        m_tsCreated = tsCreated ; m_tsUpdated = tsUpdated ;
    }
    
    // and then you have accessors/mutators
    
    public String getID()                   { return m_sID ; }
    public void setID( String s )           { m_sID = s ; }
    public String getName()                 { return m_sName ; }
    public void setName( String s )         { m_sName = s ; }
    public long getCreatedTime()            { return m_tsCreated ; }
    public void setCreatedTime( long ts )   { m_tsCreated = ts ; }
    public long getUpdatedTime()            { return m_tsUpdated ; }
    public void setUpdatedTime( long ts )   { m_tsUpdated = ts ; }
}
```

</details>

The practical problem with this is that **you end up with a lot of repetition in your code**. This needless repetition can come in two forms. First, let's look at that constructor. If you don't provide a "full" constructor, then you have to use the empty constructor, and then call a bunch of `set` methods. If you _do_ provide a "full" constructor, then unless you're _always_ initializing _every_ member field _every_ time, you're going to end up with a bunch of `null`s in your constructor calls.

<details><summary>Example 2: Problems with Beans</summary>

```java
// To initialize Thing 1 with just a name and creation time, we leave two fields null.
Thing thing1 = new Thing( null, "Thing 1", (new Date()).getTime(), null ) ;

// To initialize Thing 2 without using the silly constructor, we have to repeat references to it.
Thing thing2 = new Thing() ;
thing2.setName( "Thing 2" ) ;
thing2.setCreatedTime( (new Date()).getTime() ) ;
// If this were a real class with lots of other fields, we'd keep repeating "thing2.blahblahblah"
```

</details>

This way of thinking also tends to spread to other methods provided by the bean. Anything that doesn't explicitly return a value tends to return `void`, so you can't have to keep referring to the same object over and over again.

Then we come to the unit testing of the class. In order to provide full coverage, you have to exercise both constructors, but also do the same sort of repetition.

<details><summary>Example 3: 100% Bean Coverage Tedium</summary>

```java
@RunWith( JUnit4.class )
public class ThingTest
{
    @Test
    public void testBasicDataAccess()
    {
        Thing thing = new Thing() ;
        thing.setID("foo") ;
        assertEquals( "foo", thing.getID() ) ;
        thing.setName("Foo") ;
        assertEquals( "Foo", thing.getName() ) ;
        thing.setCreatedTime( 42L ) ;
        assertEquals( 42L, thing.getCreatedTime() ) ;
        thing.setUpdatedTime( 50L ) ;
        assertEquals( 50L, thing.getUpdatedTime() ) ;
        thing = new Thing( "bar", "Bar", 86L, 100L ) ;
        assertEquals( "bar", thing.getID() ) ;
        assertEquals( "Bar", thing.getName() ) ;
        assertEquals( 86L, thing.getCreatedTime() ) ;
        assertEquals( 100L, thing.getUpdatedTime() ) ;
    }
}
```

</details>

There's nothing _wrong_ with this approach, but it's clumsy. You're working with the same object all the time, but have to keep referring to it over and over again.

### :running: The TL;DR on Beans ###

| + | - |
| --- | --- |
| Standard design pattern for most data-marshalling frameworks. | Clumsy and stuttery in practice, with lots of repetitive code. |

## Builders ##

Partly in reaction to the bean pattern, the concept of a "builder" started to appear in Java usage. **A builder is a discrete class whose sole purpose is to initialize another class.** It uses the pattern of "fluid" method invocation to let you intuitively set exactly the values you want to set, then "build" an instance of the object. For example, we can extend the `Thing` above with a `ThingBuilder`.

<details><summary>Example 4: A Builder</summary>

```java
/**
 * There are a lot of ways to implement builders.
 * This is a pretty simplistic example.
 */
public class ThingBuilder
{
    private Thing m_thing = null ;
    
    public ThingBuilder()
    { m_thing = new Thing() ; }
    
    public ThingBuilder id( String s )
    { m_thing.setID(s) ; return this ; }
    
    public ThingBuilder name( String s )
    { m_thing.setName(s) ; return this ; }
    
    public ThingBuilder createdTime( long ts )
    { m_thing.setCreatedTime(ts) ; return this ; }
    
    public ThingBuilder updatedTime( long ts )
    { m_thing.setUpdatedTime(ts) ; return this ; }
    
    public Thing build()
    { return m_thing ; } // boy this feels kinda trivial now doesn't it
}
```

</details>

With a builder, we can intuitively build up instances of objects with arbitrary members initialized or not.

<details><summary>Example 5: A Builder in Use</summary>

```java
Thing thing = (new ThingBuilder())
        .name( "Good Thing" )
        .createdTime( (new Date()).getTime() )
        .build()
        ;
```

</details>

While this gives us the nice "fluid" and free-form initialization style that we wanted (note we didn't have to do `id(null)` or `updatedTime(null)` in there), we now need _a second class_ just to push and pull data in and out of our data container. And then, of course, you have to unit test _that_, in _addition_ to the tests we did with beans.

<details><summary>Example 6: 100% Builder Coverage Tedium</summary>

```java
/**
 * Note: We still need the bean test class.
 */
@RunWith( JUnit4.class )
public class ThingBuilderTest
{
    @Test
    public void testBuilder()
    {
        Thing thing = (new ThingBuilder())
                .id("foo")
                .name("Foo!")
                .createdTime(42L)
                .updatedTime(50L)
                .build()
                ;
        assertEquals( "foo", thing.getID() ) ;
        assertEquals( "Foo!", thing.getName() ) ;
        assertEquals( 42L, thing.getCreatedTime() ) ;
        assertEquals( 50L, thing.getUpdatedTime() ) ;
    }
}
```

</details>

### :running: The TL;DR on Builders ###

| + | - |
| --- | --- |
| Nice, fluid invocation style for partially-initializing fields in an object. | You have to implement and test an entire second class _in addition_ to the bean you already had to deal with. |

## :running: The (Obvious?) Alternative: Self-Brewing Beans ##

To get all the access protections (and compatibility with data frameworks), and all the "fluid" invocation style of builders, the simple solution is to design "self-building" data objects, where **any method that doesn't return a value returns the instance,** including the basic mutator methods.

### Example 7: Best of Both Patterns ###

```java
/** Revised. */
public class Thing
{
    private String m_sID ;
    private String m_sName ;
    private long m_tsCreated ;
    private long m_tsUpdated ;
    
    /** The empty constructor is the only one we'll ever need. */
    public Thing() {}
    
    // and then you have accessors/mutators, which return the object
    
    public String getID()                    { return m_sID ; }
    public Thing setID( String s )           { m_sID = s ; return this ; }
    public String getName()                  { return m_sName ; }
    public Thing setName( String s )         { m_sName = s ; return this ; }
    public long getCreatedTime()             { return m_tsCreated ; }
    public Thing setCreatedTime( long ts )   { m_tsCreated = ts ; return this ; }
    public long getUpdatedTime()             { return m_tsUpdated ; }
    public Thing setUpdatedTime( long ts )   { m_tsUpdated = ts ; return this ; }
}
```

We've simply changed all the methods that were returning `void` to return the object instance instead. Below we revisit the earlier examples using the new pattern.

### Example 8: Initializing Things ###

```java
Thing thing1 = (new Thing())
                .setName( "foo" )
                .setCreatedTime( (new Date()).getTime() )
                ;
Thing thing2 = (new Thing())
                .setName( "bar" )
                .setCreatedTime( (new Date()).getTime() )
                ;
```

### Example 9: Testing Things ###

```java
/** Revised. **
@RunWith( JUnit4.class )
public class ThingTest
{
    @Test
    public void testBasicDataAccess()
    {
        Thing thing = new Thing() ;
        assertEquals( "foo", thing.setID("foo").getID() ) ;
        assertEquals( "Foo!", thing.setName("Foo!").getName() ) ;
        assertEquals( 42L, thing.setCreatedTime(42L).getCreatedTime() ) ;
        assertEquals( 50L, thing.setUpdatedTime(50L).getUpdatedTime() ) ;
        // ...and bam we have 100% coverage
    }
}
```

## So... So what? ##

![So... who cares?](https://i.imgur.com/umKXQLx.png)

What's the big deal with being fluid _and_ still being a bean? Why did I just waste your time with this giant wall of text?

This design pattern allows us to do a lot of interesting things that capture the goodness of both beans and builders:

* Since most data marshalling frameworks use introspection based on _method names_, not return types, this pattern remains compatible with most data marshalling frameworks that work on beans.
    * It can still be marshalled to/from JSON with the usual annotation libraries (like Jackson and Gson).
    * It can still be provided as a `@Bean` to `@Autowired` fields in Java Spring.
* You can use the empty constructor and then call a chain of individual mutators, like a builder.
* You don't have to implement, and test, and instantiate a second class just to build your data class.
* This pattern, extended across other methods of the class, helps the usability of the whole class.


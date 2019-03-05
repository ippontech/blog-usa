---
authors:
- Pasha Paterson
tags:
- Java
- data modelling
- coding standards
- design
date: 2019-02-26T18:16:00.000Z
title: "Flexible Data Model Design"
image:
---

The only thing that most discussions of “standards” agree on is that no one agrees on standards. The world of coding standards, in particular, is perhaps one of the most hotly-contested battlegrounds of computer science. Given the patterns and trends emerging in the usage of Java today, there are certain design and implementation practices that, while not providing an end to anyone’s holy wars, do tend to make the most pragmatic sense. This discussion will focus on design principles in one of the most fundamental of these arenas — how to design and implement a data model that can be used flexibly across multiple contexts in a large application architecture.

A lot of the material in this post is pretty basic, so if you're impatient and want to skip to the new stuff, look for this guy: :running:

# Basic Access and Mutation #

There are basically two different schemes for designing a class that contains a data object: the **bean** pattern, and the **builder** pattern. Both design patterns conceal direct access to member objects behind at least `protected` (and usually `private`) scope, but provide **accessors** and **mutators** to read and write the values of those fields. While beans and builders have become relatively standard over the years, they're both surprisingly clumsy in practice.

## Beans ##

The usual pattern for a bean is to declare all member fields `private` and provide "getters" and "setters" to access those fields. However the usual standard dictates that all such mutators must return `void`. The practical problem with this is that **you end up with a lot of tedious repetition in your code**.

First, there's the issue of constructors. Most data marshalling frameworks require the use of a "default" constructor with no arguments, in order to initialize instances. However, to work with the bean, the code then needs to call a series of `.setX()` methods, referencing the instance each time. If the class has certain fields that are "always" (or, at least, commonly) known upfront, then it might also provide a constructor that directly initializes those fields, but when using that constructor, if any of those fields has no initial value to set at runtime, you end up with a bunch of potentially-confusing `null`s in your invocation. This problem is particularly evident in Android's SQLite query API, which is commonly shot through with `null` arguments.

Whichever constructor pattern you choose, you still have to initialize whatever values _aren't_ covered by a construtor argument. In the bean pattern, all mutator methods return `void`. This means that, for every member that you want to initialize, you have to call a `.setX()` method, referencing the same class instance over and over again. This tends to look silly in practice, and further, any form of repetition is error-prone, even with IDEs. This way of thinking also tends to spread to other methods provided by the bean: Anything that doesn't explicitly return a value tends to return `void`, so you have to keep referring to the same object over and over again.

Finally, consider writing unit tests for the class. If you provide default and "full" constructors, then you have to test both. And in order to fully cover the class, you have to tediously repeat references to the class as you "set" and then "get" each member.

There's nothing _wrong_ with this approach, but it's clumsy, repetitive, and inconvenient.

<details><summary>Example 1.1: A Bean</summary>

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

<details><summary>Example 1.2: Bean Usage Annoyances</summary>

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

<details><summary>Example 1.3: 100% Bean Coverage Tedium</summary>

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

### :running: The TL;DR on Beans ###

| + | - |
| --- | --- |
| Standard design pattern for most data-marshalling frameworks. | Clumsy and stuttery in practice, with lots of repetitive code, and possibly multiple constructors. |

## Builders ##

Partly in reaction to the bean pattern, the concept of a "builder" started to appear in Java usage. **A builder is a discrete class whose sole purpose is to initialize another class.** It uses the pattern of "fluid" method invocation to let you intuitively set exactly the values you want to set, then "build" an instance of the object. For example, we can extend the `Thing` above with a `ThingBuilder`.

With a builder, we can intuitively build up instances of objects with arbitrary members initialized or not.

While this gives us the nice "fluid" and free-form initialization style that we wanted (note we didn't have to do `id(null)` or `updatedTime(null)` in there), we now need _a second class_ just to push and pull data in and out of our data container. And then, of course, you have to unit test _that_, in _addition_ to the tests we did with beans.

<details><summary>Example 2.1: A Builder</summary>

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

<details><summary>Example 2.2: A Builder in Use</summary>

```java
Thing thing = (new ThingBuilder())
        .name( "Good Thing" )
        .createdTime( (new Date()).getTime() )
        .build()
        ;
```

</details>

<details><summary>Example 2.3: 100% Builder Coverage Tedium</summary>

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

### Example 3.1: Best of Both Patterns ###

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

### Example 3.2: Initializing Things ###

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

### Example 3.3: Testing Things ###

```java
/** Revised. */
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

## :running: Why bother changing design patterns? ##

What's the big deal with being fluid _and_ still being a bean? Why did I just waste your time with this giant wall of text?

This design pattern allows us to do a lot of interesting things that capture the goodness of both beans and builders:

* Since most data marshalling frameworks use introspection based on _method names_, not return types, this pattern remains compatible with most data marshalling frameworks that work on beans.
    * It can still be marshalled to/from JSON with the usual annotation libraries (like Jackson and Gson).
    * It can still be provided as a `@Bean` to `@Autowired` fields in Java Spring.
* You can use the empty constructor and then call a chain of individual mutators, like a builder.
* You don't have to implement, and test, and instantiate a second class just to build your data class.

and...

### Because a pattern of "fluidity" extends across your entire stack. ###

This pattern, extended across other methods of the class, helps the usability of the whole class. Further, it instills a mindset and pattern of "fluidity" across your entire codebase.

Your class can provide mutators that make sense but don't fit the usual `get`/`set` formula. In particular, any object that contains its unique ID can have an `identify()` which regenerates the ID _only if it doesn't already have one_, and returns the object for further modification, or direct storage.
    
```java
thingRepository.insertOrUpdate( o.identify() ) ;
```

Another useful verb you can add to a vital data object is `validate()`. Before saving the object to the data store, you can execute a series of one or more validations on the object's integrity. In the interest of maintainability, this method can further break down into atomic steps, each of which is also fluidly invoked.

<details><summary>Example 4.1: A Fluid Validator</summary>

```java
/**
 * Instead of being Boolean, each method either succeeds
 * or fails with a specific exception, which is more informative
 * to the consumer than "true" or "false".
 */
public Thing validate() throws RuntimeException
{
    return this.isIdentifiedProperly()
        .hasBeenNamed()
        .doesNotDivideByZero()
        .doesNotBreakTheFabricOfSpaceTime()
        ; // If we add more validation criteria, they just add to the chain.
}
    
/** Obviously we could just use identify() but just roll with the example, OK? */
protected Thing isIdentifiedProperly() throws UnidentifiedFlyingObjectException
{
    if( this.id == null || this.id.isEmpty() )
        throw new UnidentifiedFlyingObjectException( "Identify this thing!" ) ;
    return this ; // success
}

protected Thing hasBeenNamed() throws TheThingWithNoNameException
{
    if( this.name == null || this.name.isEmpty() )
        throw new TheThingWithNoNameException( "Name this thing!" ) ;
    return this ;
}

// etc.
```

</details>


When writing your database integration layer (if it isn't already provided for you), you can extend this downward to the class that marshals the objects to/from the database. The `select(UUID)` (or `select(String)`) method returns a thing named by the ID; the `insert(Thing)` and `update(Thing)` methods return the inserted/updated thing; the `delete(Thing)` and/or `delete(String)` methods can return `null`, or a `boolean` success/failure flag, but could also pop the deleted thing in case there's anything else we want to do to it.

The pattern also extends upward. If you're engineering a web service, then the RESTful API that allows a client to access these data objects will also naturally fit this design pattern.

* `GET /api/thing/{ID}` gets a specific thing
* `POST /api/thing` inserts a thing _and returns the same thing_ so that you don't have to immediately `GET` it again (to discover its new UUID, for example)
* `PUT /api/thing/{ID}` updates a thing _and returns the updated thing_ so that you don't have to `GET` it again (in case there's an "updated by"/"updated at time" audit trail).
* While functions of the form `DELETE /api/thing/{ID}` typically return `HTTP 204 No Content`, you could also choose to have it return the deleted thing, _like the `Map#remove()` method does in Java._

This continues to extend into the frontend, where the client/user interacts with the service. Alice wants to create a thing, so she enters the data, taps "Submit", and a screen showing the saved thing appears - maybe even in the same layout as the form. There's an "edit" button that makes it all editable again; no context-switching needed. You don't get shunted out of the context of dealing with that thing until you take an explicit action to move away from that thing.

All of this is to reinforce the thought-pattern that **I always get back the object that I acted on, and can act on it again**. This simplicity of interaction keeps a lid on the complexity of every part of the stack, from the DB schema, to the model, to the API, to the UI.


---
authors:
- Grégory ELHAIMER
tags:
- Java
- Design pattern
date: 2019-05-28T12:21:50.000Z
title: "Java enums? With extra Visitor please!"
image: 
---

Enums can be seen as a group of strongly typed constants. It is very convenient to make code more readable or ensure that data received through our API is compliant to our contract. But it can become tricky when conditional statements are based upon enum values. 

As the code evolves, the number of enum’s values can increase and everywhere it has been used to switch between some kind of behavior must be checked. If a lot of conditions has been based upon an enum value then it can become a nightmare to maintain and have some unexpected side effects on the system as a whole.

But what if the compiler told us where to look as we add or update enums’ values ?

# The switch case approach

Let’s say we have an `AssetClass` enum which references the different types of tradable commodities:

```java
public enum AssetClass {
    METAL,
    ENERGY,
    AGRICULTURAL,
}
```

This enum will probably be used at some point to choose the appropriate strategy, mapper or anything else depending on the asset class. This would generally look like the following code:

```java
public AssetClassBehavior getAssetClassBehavior(AssetClass assetClass) throws Exception {
    switch (assetClass) {
        case METAL: return new MetalBehavior();
        case ENERGY: return new EnergyBehavior();
        case AGRICULTURAL: return new AgriculturalBehavior();
        default: throw new Exception("Unexpected asset class");
    
}
```

Using switch-case statement is probably the most straightforward way to do it. However, this has several flaws. 

As the method is returning SomeInstrumentBehavior, using the default statement is mandatory, even if all the `AssetClass` values have been cased. This implies either returning a default behavior, null or throwing an exception.

Adding a new value to `AssetClass` will require to check every switch-case statement based on its values. And there is no guarantee that we won’t miss one.

The switch case has to be aware of the enum values which creates a strong coupling. This can have a huge impact in the code as a whole and breaks the open-close principle. 
For instance: Metal assets could be split into 2 different sub asset classes: precious and base metal. If at some point we need to do it, every part of the code relying on the metal asset class will actually have to be reworked even if the expected behavior on already implemented features remains the same.

# Visitor pattern to the rescue

Let's start with a brief reminder of what is the visitor pattern. Among the design patterns, the visitor is considered as a behavioral one. It helps to split the business logic from the objects it operates on. 

First, let’s create a visitor interface for our enum. This interface will create a contract between the enum and code wanting to interact with it.

```java
public interface AssetClassVisitor<T> {
    T visitMetal();
    T visitEnergy();
    T visitAgricultural();
}
```

Let’s update `AssetClass` enum to expose an accept function which will open the door to its visitor:

```java
public enum AssetClass {
    METAL {
        @Override
        public <E> E accept(AssetClassVisitor<E> visitor) {
            return visitor.visitMetal();
        
    },
    ENERGY {
        @Override
        public <E> E accept(AssetClassVisitor<E> visitor) {
            return visitor.visitEnergy();
        
    },
    AGRICULTURAL {
        @Override
        public <E> E accept(AssetClassVisitor<E> visitor) {
            return visitor.visitAgricultural();
        
    };

    public abstract <E> E accept(AssetClassVisitor<E> visitor);
}
```

Let's use it !

```java
assetClass.accept(new AssetClassVisitor<AssetClassBehavior>() {
    @Override
    public AssetClassBehavior visitMetal() {
        return new MetalBehavior();
    

    @Override
    public AssetClassBehavior visitEnergy() {
        return new EnergyBehavior();
    

    @Override
    public AssetClassBehavior visitAgricultural() {
        return new AgriculturalBehavior();
    
});
```

As we see in this implementation, each `AssetClass` value is responsible for calling the appropriate visitor’s method. This means that from now on, there is no need to know about `AssetClass` values as responding to the visitor contract will assure that the behavior will remain the same even if the values changes. For instance, renaming one of the values won’t affect any part of the code which responds to the contract.
Moreover, handling defaulting is no more required. We have scoped the behaviors to the interface.


# Adding a new asset class

We now need to extend our activity to livestock and meat commodities. It’s simple as adding the value in the enum and the associated visitor method.

```java
public enum AssetClass {
    METAL {
        @Override
        public <E> E accept(AssetClassVisitor<E> visitor) {
            return visitor.visitMetal();
        
    },
    ENERGY {
        @Override
        public <E> E accept(AssetClassVisitor<E> visitor) {
            return visitor.visitEnergy();
        
    },
    AGRICULTURAL {
        @Override
        public <E> E accept(AssetClassVisitor<E> visitor) {
            return visitor.visitAgricultural();
        
    },
    // Our new enum value
    LIVESTOCK_AND_MEAT {
        @Override
        public <E> E accept(AssetClassVisitor<E> visitor) {
            return visitor.visitLiveStockAndMeat();
        
    };

    public abstract <E> E accept(AssetClassVisitor<E> visitor);
}
```

```java
public interface AssetClassVisitor<T> {
    T visitMetal();
    T visitEnergy();
    T visitAgricultural();
    // and our new visit method
    T visitLiveStockAndMeat(); 
}
```

After this, the code will look like a Christmas tree: it doesn’t compile anymore. And the compiler should be thanked for his job as shown errors are highlighting every part of the code where handling the new value is required.
So let’s fix the issue. The previously implemented feature is not supported yet for our new asset. For the sake of the example, we will simply throw an exception.

```java
assetClass.accept(new AssetClassVisitor<AssetClassBehavior>() {
    @Override
    public AssetClassBehavior visitMetal() {
        return new MetalBehavior();
    }

    @Override
    public AssetClassBehavior visitEnergy() {
        return new EnergyBehavior();
    }

    @Override
    public AssetClassBehavior visitAgricultural() {
        return new AgriculturalBehavior();
    }

    @Override
    public AssetClassBehavior visitLiveStockAndMeat() {
        throw new NotImplementedException("My awesome new feature is not  enabled yet for livestock and meat.")
    }
});
```

# In a nutshell
One of my missions was rich of treatments which were different according to enum values. I discovered this practice as it was the standard way the team chose to deal with enum.
It revealed us many unseen edge cases while coding and then became an automatism.

There is no need to go through this pattern when enums are purely descriptive or if only one place uses it as a behavior discriminant. In such cases, sticking to a simple switch-case statement will probably be enough.

However, bringing the heavy artillery when meeting multiple switch-case statements on an enum is definitely worth the extra cost. It reduces the coupling between enum and business logic and can save time by highlighting unseen cases.

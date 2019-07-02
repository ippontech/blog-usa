---
authors:
- Grégory ELHAIMER
tags:
- Java
- Software Development
- Software Craftmanship
date: 2019-05-28T12:21:50.000Z
title: "Java enums? With extra Visitor please!"
image: 
---

Enums can be seen as a group of strongly typed constants. Their several use cases include: attributing a strong semantic to values, limiting and validating available values for data, enhancing code readability, etc.
However, using enums can be tricky when it comes to making decisions according to its values. As the enum evolves, each part of the code where it has been used as a condition must be checked. If many decisions have been based on an enum's values, maintaining the code base can be a nightmare; one forgotten verification can lead to corruption of the whole system.

Is there then a way to reduce the impact of enums' evolutions on the code base?

# The switch-case approach

Consider the `AssetClass` enum which represents the different group of tradable commodities:

```java
public enum AssetClass {
    METAL,
    ENERGY,
    AGRICULTURAL,
}
```

This enum can be used to decide which strategy, mapper or any other behavior according to the value of `AssetClass`. This would generally look like the following code:

```java
public AssetClassBehavior getAssetClassBehavior(AssetClass assetClass) throws Exception {
    switch (assetClass) {
        case METAL:return new MetalBehavior();
        case ENERGY: return new EnergyBehavior();
        case AGRICULTURAL: return new AgriculturalBehavior();
        default: throw new Exception("Unexpected asset class");
    }
}
```

The swith-case statement is probably the most straightforward way to do. It has, however, several flaws.

The method `getAssetClassBehavior()` returns a behavior according to the value of `AssetClass`. Defining a default behavior then becomes mandatory, even if, in this example, all the values of `AssetClass` enum are handled. To do so, we can either return a default implementation of `AssetClassBehavior` or null or throw an exception.

Using this defaulting mechanism silences any adding of new value inside the enum. It requires to check any piece of code using `AssetClass` as a conditioner without any guarantee that an oversight has been avoided.

The last problem is probably the least obvious. Using the switch-case statement generates a strong coupling between the business logic and the enum's values, breaking the open/close principle.
Yet, the switch-case statement has no interest in knowing if the asset class is an enum or an object or anything else. Only the semantic matters.
For instance, metals could be split into two sub assets: base metals and precious metals. Any already existing code based on `AssetClass.METAL` will have to be reworked to take this change into account. No business value has been added where the rework was necessary while it exposed a working implementation to the risk of regressions.


# Visitor pattern to the rescue

How can we break this coupling while offering the ability to contextualize the decision making to the enum's values ? The answer is in the title: let's use the Visitor pattern.

In the first place, we need to create an interface which will be used as a contract between the enum and the code relying on its values.

```java
public interface AssetClassVisitor<T> {
    T visitMetal();
    T visitEnergy();
    T visitAgricultural();
}
```

The interface is generified in order to allow implementations whose purpose differs according to the context.

It is now necessary to update the enum to make it accept any implementation of the `AssetClassVisitor`.

```java
public enum AssetClass {
    METAL {
        @Override
        public <E> E accept(AssetClassVisitor<E> visitor) {
            return visitor.visitMetal();
        }
    },
    ENERGY {
        @Override
        public <E> E accept(AssetClassVisitor<E> visitor) {
            return visitor.visitEnergy();
        }
    },
    AGRICULTURAL {
        @Override
        public <E> E accept(AssetClassVisitor<E> visitor) {
            return visitor.visitAgricultural();
        }
    };

    public abstract <E> E accept(AssetClassVisitor<E> visitor);
}
```

The code is now ready to forget the switch-case and use an implementation of the `AssetClassVisitor`:

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
});
```

It can be observed that each value of `AssetClass` is responsible for call the appropriate method of `AssetClassVisitor`. Values of `AssetClass` can now be ignored as the semantic is brought by the visitor. `AssetClass.AGRICULTURAL` could be renamed `AssetClass.AGRI` without rework needed where the business logic occurs.
Furthermore, handling default behavior is not required anymore. Possibilities are now scoped to the one provided by the interface.

# Add a new asset class

The business is growing and activities are extending to livestock and meat. 
This is simple as adding `AssetClass.LIVESTOCK_AND_MEAT` value and updating the visitor interface.

```java
public enum AssetClass {
    METAL {
        @Override
        public <E> E accept(AssetClassVisitor<E> visitor) {
            return visitor.visitMetal();
        }
    },
    ENERGY {
        @Override
        public <E> E accept(AssetClassVisitor<E> visitor) {
            return visitor.visitEnergy();
        }
    },
    AGRICULTURAL {
        @Override
        public <E> E accept(AssetClassVisitor<E> visitor) {
            return visitor.visitAgricultural();
        }
    },
    // The new value
    LIVESTOCK_AND_MEAT {
        @Override
        public <E> E accept(AssetClassVisitor<E> visitor) {
            return visitor.visitLiveStockAndMeat();
        }
    };

    public abstract <E> E accept(AssetClassVisitor<E> visitor);
}
```

```java
public interface AssetClassVisitor<T> {
    T visitMetal();
    T visitEnergy();
    T visitAgricultural();
    // The new method
    T visitLiveStockAndMeat();
}
```

After this, the code will light up like a Christmas Tree: it does not compile anymore. Et compiler should be thanked for doing such a great job! All those highlighted errors show that some part of the code are not designed to handle the new value yet. Let's correct this using an exception: the existing features are not available yet for livestock and meat.


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
        throw new NotImplementedException("This feature is not enabled yet for livestock and meat.")
    }
});
```
 
# In a nutshell

During one of my mission, the team was facing a huge number of enums and many business logic was based upon their values. The visitor pattern was our shield against unpredicted edge cases. It became the standard way to deal with enum in the whole code base.

Using this pattern is not necessary if your enums are purely descriptive. However, bringing the heavy artillery is definitively worth the extra cost. Breaking the coupling between enum's values and the business logic offers flexibility while the compiler provides an instantaneous feedback about potential oversight and edge cases.

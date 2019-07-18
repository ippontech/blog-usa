---
authors:
- Thomas Boutin
tags:
- Android
- Kotlin
- Mobile
- Dependency Injection
- Architecture
date: 2019-07-18T04:15:23.000Z
title: "Android DI: From Dagger to Koin"
image: https://github.com/Thomas-Boutin/blog-usa/blob/master/images/2019/07/di-android.png
---

Dependency injection is a pattern whereby you let a third party (ie, a framework) provide implementations. This provides great advantages:
- It reduces coupling in your apps
- It makes testing easier
- It forces a better code structure and organisation, by eliminating some boilerplate and creating a DI layer.

There isn’t any official Android way to use this pattern. However, Google tends to push a framework of their own: Dagger. Despite being extremely versatile and powerful, the learning curve of this tool is pretty high. Moreover, it requires the creation of many files, even for a small project.

The purpose of this article is to introduce you a smaller, simpler and yet powerful DI framework: Koin. We’ll go further by refactoring an Android project developed with:
- Some Android Architecture Components
- Dagger 2

# The project
Coinsentinel is an Android application hosted on GitHub [here](https://github.com/Thomas-Boutin/coinsentinel). It displays the current worth and rank of the main cryptocurrencies. To get up-to-date information, it fetches data from the coinmarket free-to-use API. It also provides offline capabilities to ensure a seamless user experience, whether if the user has a network access or not. The UI is simple: a list presenting sorted crypto currencies. A top-down swipe triggers a data refresh attempt.

It looks like that:

![alternate text](https://github.com/Thomas-Boutin/blog-usa/blob/master/images/2019/07/coinsentinel.png)

# Dagger
We use Dagger to inject miscellaneous stuff:
- A Gson instance, to deserialize http responses
- A business deserializer, which maps JSON to a POKO (Plain Old Kotlin Object)
- The representation of the app’s SQLite database
- The unique DAO used in this codebase

Before going deeper in the details, here’s how dagger builds its dependency tree:

![alternate text](https://github.com/Thomas-Boutin/blog-usa/blob/master/images/2019/07/dagger-injection.png)

[source](https://blog.mindorks.com/the-new-dagger-2-android-injector-cbe7d55afa6a)

As you may have already guessed, the tree is built by declaring components and modules. Basically, a component is made of several modules. As the documentation says: “Dagger uses no reflection or runtime bytecode generation, does all its analysis at compile-time, and generates plain Java source code.”. That means that you’ll have many files generated during the compilation in order to generate the dependency graph. This is supposed to allow the build to scale well when the app becomes bigger BUT generates a certain overhead even for small apps.

Although being fairly simple, Coinsentinel has 3 files which are only dedicated to the DI:
- AppComponent.kt
- AppModule.kt
- MainActivityModule.kt

I think that Dagger’s syntax doesn’t feel right in a kotlin native app. That’s because it’s written in Java. To sum up, this framework is very versatile, but forces us to develop a lot even if your project is small. This definitely feels over-engineered.

# Koin
![alternate text](https://github.com/Thomas-Boutin/blog-usa/blob/master/images/2019/07/koin-2.0.jpg)

[source](https://github.com/InsertKoinIO/koin)

Here comes Koin. It is “a pragmatic lightweight dependency injection framework for Kotlin [https://insert-koin.io/](https://insert-koin.io/)”. Bonus: it’s 100% “Written in pure Kotlin, using functional resolution only: no proxy, no code generation, no reflection.”.
Actually, this is “just” a DSL which provides a concise and beautiful syntax. This obviously helps develop the Kotlin way.
It also interacts well with Android Architecture components and AndroidX libraries.

# Diving into the refactoring
## Step 0: Update the androidX appcompat version

```gradle
implementation 'androidx.appcompat:appcompat:1.1.0-rc01'
```

This new version makes AppCompatActivity implement LifecycleOwner, which is mandatory when dealing with architecture components.

## Step 1: Koin gradle dependencies

We’ll use Koin 2. It’s brand new, full of nice features and actively maintained. In your app/build.gradle, add the following lines to import Koin:
```gradle
// Koin
def koin_version = '2.0.1'
// Koin for Kotlin
implementation "org.koin:koin-core:$koin_version"
// Koin for Unit tests
testImplementation "org.koin:koin-test:$koin_version"
// AndroidX (based on koin-android)
// Koin AndroidX Scope feature
implementation "org.koin:koin-androidx-scope:$koin_version"
// Koin AndroidX ViewModel feature
implementation "org.koin:koin-androidx-viewmodel:$koin_version"
```

Then, remove the ones related to dagger:
```gradle
// Dagger
def dagger_version = "2.16"
implementation "com.google.dagger:dagger:$dagger_version"
implementation "com.google.dagger:dagger-android:$dagger_version"
implementation "com.google.dagger:dagger-android-support:$dagger_version"
kapt "com.google.dagger:dagger-compiler:$dagger_version"
kapt "com.google.dagger:dagger-android-processor:$dagger_version"
```

Alright, now we have everything needed to play with Koin.

## Step 2: Refactoring of the DI package
Firstly, delete AppComponent and MainActivityModule as we’ll factorise the code in **AppModule**. Then, replace the code in **AppModule** by:
```kotlin

package fr.ippon.androidaacsample.coinsentinel.di

import androidx.room.Room
import com.google.gson.GsonBuilder
import fr.ippon.androidaacsample.coinsentinel.api.CoinResultDeserializer
import fr.ippon.androidaacsample.coinsentinel.api.CoinTypeAdapter
import fr.ippon.androidaacsample.coinsentinel.db.AppDatabase
import fr.ippon.androidaacsample.coinsentinel.db.Coin
import org.koin.dsl.module
import java.lang.reflect.Modifier

private const val DATABASE_NAME = "COIN_DB"

val appModule = module {
   single {
       GsonBuilder()
           .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
           .serializeNulls()
           .registerTypeAdapter(Coin::class.java, CoinTypeAdapter())
           .excludeFieldsWithoutExposeAnnotation()
           .create()
   }
   single { CoinResultDeserializer(get()) }
   single {
       Room.databaseBuilder(get(), AppDatabase::class.java, DATABASE_NAME)
           .build()
   }
   single {
       get<AppDatabase>().coinDao()
   }
   single { CoinRepository(get(), get()) }
   viewModel { CoinViewModel(get()) }
}
```

Here:
- We declare a unique module in the **appModule** read only property
- We’ve replaced the **@Singleton** annotated methods by equivalents **single** Kotlin DSL declaration.
- The **get()** statement resolves a component dependency. We can also use it to get a dependency and call a method on it, as we did for **get<AppDatabase>().coinDao()**
- We’ve added **CoinRepository** to the module definition, as it was previously injected
- Koin is shipped with a special **viewModel** statement to define our ViewModel. Actually, this library simplifies the use of this AAC Component. We’ll see why in a next section.

Koin also allows to define factory beans, submodules, bindings, etc. Further documentation can be found (there)[https://insert-koin.io/docs/2.0/quick-references/modules-definitions/].

## Step 3: Refactoring the custom Application class

Here again, that’s very straightforward: we remove the boilerplate and replace it with simple Kotlin DSL statements. In **CoinSentinelApp**, remove the dispatching android injector variable:

```kotlin
@Inject
lateinit var dispatchingAndroidActivityInjector: DispatchingAndroidInjector<Activity>
```

Then, remove implementation of the **HasActivityInjector** interface:

```kotlin
class CoinSentinelApp : Application(), HasActivityInjector {
```

Remove also its associated overriden function:

```kotlin
override fun activityInjector(): DispatchingAndroidInjector<Activity> {
   return dispatchingAndroidActivityInjector
}
```

Finally, in onCreate, refactor the DI initialization by replacing:

```kotlin
DaggerAppComponent.builder()
       .application(this)
       .build()
       .inject(this)
```
by

```kotlin
startKoin {
   androidContext(this@CoinSentinelApp)
   modules(appModule)
}
```

It is also possible to configure loggers, android contexts, properties etc
Further documentation [here](https://insert-koin.io/docs/2.0/quick-references/starting-koin/#koinapplication-dsl-recap).

# Step 4: Refactoring the ViewModel and the Repository

In this fast step, you just have to remove the **@Singleton** and **@Inject** annotations in the **CoinViewModel** and **CoinRepository** files.

## Step 5: Refactoring the MainActivity

Final step of the refactoring. Replace

```kotlin
@Inject
lateinit var coinViewModel: CoinViewModel
```

by

```kotlin
private val coinViewModel: CoinViewModel by viewModel()
```

You must also remove the reference to **AndroidInjection** **in onCreate()**:

```kotlin
AndroidInjection.inject(this)
```

And…that’s all. You can see an interesting notation: **by viewModel()**. That’s a special DSL keyword to inject an instance (declared in our module). If this viewModel has be shared among several components, just use **by sharedViewModel()** instead. Dead simple.

In a similar way, if we had wanted to inject the serializer or the repository, we would have used by inject(). To get further explanation, I invite you to take a look at the [documentation](https://insert-koin.io/docs/2.0/quick-references/koin-for-android/).

There is also another great feature about using Koin: we get rid of the lateinit var declaration. It becomes instead private val. This means that our injected variables :
- are readonly (var to val)
- have with a better visibility, more tied to its context (private)
- won’t cause lateinit exceptions. The lateinit modifier indicates that the variable will be null at the initialization, but won’t be null anymore when accessing it. Otherwise, when trying to access it, you’ll obtain a runtime exception of this kind:

```bash
kotlin.UninitializedPropertyAccessException: lateinit property X has not been initialized
```

That’s actually really painful to debug in an Android context. With Dagger, we experienced some issues during “monkey testing”, i.e., tapping everywhere on the app. Internally, this led to intensive creation and destruction of fragments. Sometimes, in an unpredictable way, the variables weren’t injected early enough, leading the app to crash.

*[Not Related to dagger neither koin]*
This is also why, if you look closely to the refactoring in the coinsentinel’s repository, you’ll notice that another variable has been refactored. I moved the initialization of coinAdapter away from the **init()** function. Instead, the variable is initialized **by lazy**:


From

```kotlin
private lateinit var coinAdapter: CoinAdapter
```

to

```kotlin
private val coinAdapter: CoinAdapter by lazy {
  CoinAdapter(coins, this)
}
```

The initialization **by lazy** indicates that the object will be created when **coinAdapter** will be accessed for the first time. Thanks to that :
- we can use **val** instead of **var**
- it is more efficient since no memory will be consumed as long as we don’t use **coinAdapter**.

# Conclusion

Koin has been a great library to use. It simplifies a lot the dependencies management and offers many great features. Some are dedicated to Android and the architecture components, to help you even more into your development. I haven’t talked about all of its concepts such as scopes or its use with the JetBrain’s official web framework [Ktor](https://ktor.io/). Thus, I advise you to take a look at the official github repository and website (below in the sources).

# Sources
- [Dagger github repository](https://github.com/google/dagger)
- [Dagger website](https://dagger.dev/)
- [Koin github repository](https://github.com/InsertKoinIO/koin)
- [Koin website](https://insert-koin.io/)
- [Coinsentinel github repository](https://github.com/Thomas-Boutin/coinsentinel)




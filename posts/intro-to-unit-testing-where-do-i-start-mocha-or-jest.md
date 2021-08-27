---
authors:
- Cory Walker
tags:
- Unit Testing
- Jest
- Mocha
- testing
date: 
title: "Intro to Unit Testing, Where Do I Start, Mocha Or Jest?"
image: 
---

# Intro To Unit Testing, Where Do I Start, Mocha Or Jest?

Working as a front end developer for about 5 years, testing code has always been the job of someone else. All the previous tech companies I have worked for in the past; there was developer that wrote the code and a QA person who reviewed the code. However, I have done visual or manual testing, but automated testing was usually handled by the QA engineer. In my current role, the developer and the QA engineer is the same person. I am now responsible for writing the code and testing my code beyond the typical manual, visual testing.

In this blog post, I will compare two popular testing frameworks, [Mocha](https://mochajs.org/) and [Jest](https://jestjs.io/), and help you decide which one to get started with for unit testing in Node. Assuming you have previous Node JS knowlegde; I am going to share briefly what I have learned about each testing framework and hopefully provide some insight on which one you would like to learn first. My goal is to simply the fundamentals of each framework so that someone with no prior testing knowledge at all can understand and create unit test. Let’s take a look at my definition of unit test briefly, and then we will get the boat sailing with looking at both frameworks.


## What Are Unit Tests?

Unit tests, essentially are functions that test the functionality of your code. You could also say that unit test are functions that test other functions. The word “unit”, in my explanation, means that we are testing one unit of code at a time. Unit testing is the easiest type of testing in my option because you only have to focus on a single functionality at a time.

Here is a quick example of what I mean. If the function in your code supposed to return a string; we can write a function to check if that function returns a string or not. If our test function concludes that the function returned a string, then the test pass. If our test function concludes that the function did NOT returned a string, then the test fails. Another example could be; if a function supposed to return the number 10. We can write a function to check if that function returned the number 10. Same as above, If our test function concludes that the function returned the number 10, then the test pass. If our test function concludes that the function returned the number 6, then the test fails.


## The Installation Process


### Installing Mocha

```javascript
$ npm install mocha

// Install with npm globally
$ npm install --global mocha

// Install as a dependency
$ npm install --save-dev mocha
```

### Installing Jest

```javascript
// Install with npm globally
$ npm install --global jest

// Install as a dependency
$ npm install --save-dev jest
```

As you may have noticed above, when comparing installation of the two testing frameworks, there is not much difference when using `‘npm’` to install. As we move on to the package.json file to verify our installation, we perform the same task of modifying our test value in the script to the name of the framework. See below.

```javascript       
"scripts": {
    "test": "mocha"
},
```
```javascript       
"scripts": {
    "test": "jest"
},
```

Similarly, when we run the test in our command line or terminal, we can use “npm test” for both testing frameworks.

```javascript       
$ npm test
```

Moreover, when we move on to our project to write our first test, we do NOT have to import Mocha or Jest. We can start using either framework right away in our code without delay.

So far, everything seems the same, but next we head towards a folk in the road with Mocha and Jest, when we look at how we actual test in the two different frameworks. Now, that we are all installed, let’s look at the functions we test with, starting with Mocha.


### Mocha Testing Function

Mocha mainly use 2 functions to test our code:

1. `“Describe”` Function ( parameter one, parameter two ) 

2. `“IT”` function ( parameter one, parameter two )

Each function has at least 2 parameters. The first parameter takes a string. The second parameter takes function.


### The Describe Function

The first parameter of the `"Describe"` function is a string, which is the description of the function.

The second parameter of the `"Describe"` function is a function.

```javascript
describe('This is a string that describes the function', () => {

}); 
```


### The “IT” Function

The first parameter of the `“IT”` Function is a string that describes what is being tested.

The second parameter of the `“IT”` Function is a function.
The `“IT”` Function lives inside the `“Describe”` function.

```javascript
describe('This is a string that describes the function', () => {
    it('This is a string that describes whats being tested', () => {

    });
});
```

Are you with me so far? If so, lets begin our first test. Ready? ... Ready. Let’s go!

### Lets Begin Our Test!

In Mocha, we run our test inside the `“IT”` function using Assertions. Node has built-in assertions, or you can use popular assertion libraries such as Should.js, Expect.js, or Chai. So, what is Assertions? Well, in ‘layman terms’, I would say it’s simply a specialized function to test one object against another, one string against another string, one function against another function, one variable against another variable, or our actual results against our expected results; and so forth. The functions in our code that return something is usually called our actual result; in which we store in a variable called `‘results’`. What we believe the results of our function should return is usually stored in a variable called `‘expected’`.


### Chai Assertion Library

I decided to use Chai assertion library since it is one of the most popular assertion libraries, and because I have some experience with using it. However, I will not be going into great detail with Chai to maintain the scope of comparing Mocha and Jest.

We install Chai in the command or terminal with `“npm install chai”`. We also need to import Chai into our project to use it. We will be able to run multiple functions on our variable we named `“assert”`, that is storing our Chai features.

```javascript       
// install
$ npm install chai

// bring in chai
const assert = require('chai').assert;
```

Our first function I am going to run on the assert variable is `“assert.equals”`, which takes 2 parameters.

One parameter with the actual result. 

One parameter with the expected result.

```javascript       
// install
// bring in chai
assert.equal("result", "expected");
```

The `“assert.equal”` function compares the first parameter with the second parameter. If they are the same, the test pass. If they are different, the test fails. In the above example, two strings are being tested, `“result”` and `“expected”`. This test would fail because `“result”` and `“expected”` are two different strings. Let’s look at this example in more detail using variables for the two parameters.

```javascript       
describe('string that describes the function', () => {
    it('string that describes the test', () => {
        // Storing our expected results
        // and actual results into variables
        let result = "example string";
        let expected = "example string";

        // This assert function test if
        // variables are equal
        assert.equal(result, expected);
    });
});
```

Here we created variables to hold the expected result, and the actual result.
We name one variable expected and name another variable result.
We use the `“assert.equal”` function that takes two parameters or two variables and test if they are equal. Can you guess if this test will pass or fail?
This test will pass because both strings are equal. This is one of many assertion functions that Chai offer that you can use to test with. Let’s move on to Jest and see what it is all about.


### Jest Testing Function

Are you still with me? Have I lost you yet? Do you remember Jest? Are you ready to begin comparing? Keep in mind the process we needed to do to test with Mocha.

With Jest, we only use one function. The `“Test”` function takes 2 parameters.

The first parameter of the `“Test”` function is a string that describes the test.

The second parameter of the `“Test”` function is a function.

```javascript       
test('string that describes the test', () => {

});
```


### Let’s Write Our First Test With Jest!

We run our test inside the `“Test”` function. No need to install an assertion library; Jest comes equipped with its own testing tools. Jest allows us to use the `“expect”` function, where we place our actual return results from the functions in our code. Then, we can use a function called `(.toBe)`, among many others, where we place our expected results. The `(.toBe)` function is similar to the `(assert.equal)` function in Mocha. The code below test if both variables are equal.

```javascript       
test('string that describes the test', () => {
    expect("result").toBe("expected");
});
```

Below we create variables to hold the expected result and the actual result. We name one variable `‘expected’` and name another variable `‘result’`. Here we use the `(expect)` and the `(.toBe)` function that takes the two variables and test if they are equal.

```javascript       
test('string that describes the test', () => {

    // Storing our expected results
    // and actual results into variables
    let result = "example string";
    let expected = "example string";

    expect(result).toBe(expected);
});
```

This test should pass because both strings are equal.


## Conclusion

Let’s recap, by comparing 'apples' to 'apples' with what we learned in this blog. We learned that the process of getting started with both Mocha and Jest is very similar. Then, when we began the actual testing process, things started to differ slightly.

### The Key Differences Between Mocha and Jest

* **Mocha uses 2 functions to run test.**

  1. The “Describe” function

  2. The “IT” function

* **Jest uses one function to run test.** 

  1. The “Test” function

* **Mocha mainly uses third party libraries for testing functionality.**

* **Jest has built-in testing functionality.**

I must admit, we barely scratched the surface with unit testing in Mocha & Jest. But hopefully you got a decent foundation or at least the concepts of unit testing with both frameworks. Which one would you choose to get started unit testing with?
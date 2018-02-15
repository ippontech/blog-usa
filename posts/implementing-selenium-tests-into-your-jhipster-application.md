---
authors:
- Derek Zuk
categories:
- JHipster
date: 2017-09-22T14:51:17.000Z
title: "Implementing Selenium Tests Into Your JHipster Application"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/09/selenium.png
---

Implementing automated web browser tests can sound intimidating at first, but the ease with which Selenium WebDriver integration tests can be implemented and the overall benefit you will be receiving from having real web browser integration tests makes overcoming the initial setup worth the hassle. This post will serve the purpose of iterating how to set up Selenium tests in a project that was created using JHipster. For the sake of explanation, I am working with the JHipster sample project found [here](https://github.com/jhipster/jhipster-sample-app). If you are working with another project that was created using JHipster, or most Spring Boot projects, you should find that there aren't significant structural differences between your project and the project I describe here.

Our first step is to add a Maven dependencies to our `pom.xml` file. In order to stay in line with best practices, you'll want to modify the version number so that it is parameter driven instead of hard coded in our `pom.xml` file as I am showing here:

```xml
<dependency>
  <groupId>org.seleniumhq.selenium</groupId>
  <artifactId>selenium-java</artifactId>
  <version>2.48.2</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>xml-apis</groupId>
  <artifactId>xml-apis</artifactId>
  <version>1.4.01</version>
</dependency>
```

The Selenium dependency retrieves the libraries that Selenium needs to run. The xml-apis dependency contains the Xerces API library (a widely used XML parses in the Java world), which Selenium also needs to run properly. After adding this dependency it is wise to build your project to ensure that the dependencies are successfully updated.

Once the dependencies are added, we are ready to start setting up our tests to run in the Chrome browser. Before we do this, we must install the ChromeDriver tool locally. As is explained on their website, _"ChromeDriver is a standalone server which implements [WebDriver's wire protocol](https://github.com/SeleniumHQ/selenium/wiki/JsonWireProtocol) for Chromium."_ This tool is required for Selenium tests to run in the Chrome browser. Down the road, when you're ready to set up tests using other browsers you must install the driver for those browsers as well.

You can retrieve ChromeDriver [here](https://sites.google.com/a/chromium.org/chromedriver/).

From here, we implement a basic set of Selenium tests that will run at the same time as our other integration tests. In our JHipster application, integration tests are triggered when our test files contain the text "IntTest" in their filenames. Our first Selenium test will run tests against our home page and our login page. These tests are build using the [page object design pattern](http://docs.seleniumhq.org/docs/06_test_design_considerations.jsp#page-object-design-pattern), which essentially means that instead of providing hard coded information about the web pages we are testing in our test classes, we are separating that information into page object classes for each web page.

My project directory structure for my Selenium tests is as follows:

```text
  src
   └── test
       └── java
           └── com
                └── myapp
                    └── selenium
                        ├── base
                        │   └── TestBaseSetup.java
                        ├── pageObjects
                        │   ├── HomePageObject.java
                        │   └── LoginPageObject.java
                        └── tests
                            └── HomeIntTest.java
```

Let's examine each file in order starting with the TestBaseSetup class. This class provides the basic steps that our Selenium tests need to run such selecting the browser and driver that we will use, opening and closing the browser, and setting the base URL that our tests will run against. I have added comments in my classes to provide further explanation of how the code is working.

```java
package com.myapp.selenium.base;

import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.concurrent.TimeUnit;

public class TestBaseSetup {

    static String LOCALHOST_PREFIX = "http://localhost:9000";
    private static WebDriver driver;

    // Before our tests start we open a new browser
    @BeforeClass
    public static void openBrowser(){
        // The browser can be passed in as a parameter. By default it is set to Chrome.
        // Note how you can run the test with different properties: mvn test -Dselenium.browser=firefox
        initializeTestBaseSetup(System.getProperty("selenium.browser", "chrome"));
    }

    // After our Tests are completed we close our browser
    @AfterClass
    public static void closeBrowser(){
        getDriver().quit();
    }

    // This method takes in the browser type and sets the correct driver
    public static void initializeTestBaseSetup(String browserType) {
        try {
            setDriver(browserType);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    public static WebDriver getDriver() {
        return driver;
    }

    // Depending on the browserType passed in, we initialize the corresponding driver. Default is Chrome.
    private static void setDriver(String browserType) {
        switch (browserType) {
            case "chrome":
                driver = initChromeDriver();
                break;
            case "firefox":
                driver = initFirefoxDriver();
                break;
            default:
                System.out.println("browser : " + browserType
                    + " is invalid, Launching Chrome by default");
                driver = initChromeDriver();
        }
    }

    private static void setWait() {
        // An implicit wait means the driver will wait this much time while it is waiting for an element to load:
        driver.manage().timeouts().implicitlyWait(10,  TimeUnit.SECONDS);
    }

    private static WebDriver initChromeDriver() {
        driver = new ChromeDriver();
        setWait();
        return driver;
    }

    private static WebDriver initFirefoxDriver() {
        driver = new FirefoxDriver();
        setWait();
        return driver;
    }

    // This method opens the full url based on the partial URL string that is passed in:
    public void openUrl(WebDriver driver, String partialurl) {
        driver.get(getPrefix() + partialurl);
    }

    private String getPrefix() {
        // This checks for a base URL if one is provided. If one is not provided we use the default base URL "http://localhost:9000":
        // We then replace the base URL with the canonical hostname. This allows us to run the tests on environments other than our localhost:
        // Note that you can run the test with different properties: mvn test -Dselenium.base.url=http://www.google.com
        return StringUtils.replace(System.getProperty("selenium.base.url", LOCALHOST_PREFIX), "@localhost@", getCanonicalHostName());
    }
    private String getCanonicalHostName() {
        try {
            return java.net.InetAddress.getLocalHost().getCanonicalHostName();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }
}
```

Next, we have our page object classes. The benefit of creating a page object class is that if there is a change to the views we are running our tests against, we can make corresponding changes to our page object classes and not have to manually fix each individual selenium test. Because one of our tests will involve navigating to the login page, I opted to created a page object class for both the home page as well as the login page.

```java
package com.myapp.selenium.pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HomePageObject {

    private WebDriver driver;
    private static WebElement element;

    // We list how we will retrieve our page elements here
    private static By mainHeader = By.id("mainHeader");
    private static By loginId = By.id("loginId");

    public HomePageObject(WebDriver driver) {
        this.driver = driver;
    }

    // We return each page element individually as part of our Page Object class
    public WebElement mainHeader(WebDriver driver){
        element = driver.findElement(mainHeader);
        return element;
    }

    public WebElement homeLoginId(WebDriver driver){
        element = driver.findElement(loginId);
        return element;
    }

    // We return our verification outside of our Test class:
    public boolean verifyMainHeader() {
        String pageTitle = "Welcome, Java Hipster!";
        return mainHeader(driver).getText().contains(pageTitle);
    }

}
```

```java
package com.myapp.selenium.pageObjects;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPageObject {

    private WebDriver driver;
    private static WebElement element = null;

    private static By loginTitle = By.id("loginTitle");

    public LoginPageObject(WebDriver driver) {
        this.driver = driver;
    }

    public WebElement loginTitle(WebDriver driver){
        element = driver.findElement(loginTitle);
        return element;
    }

    public boolean verifyLoginTitle() {
        String loginTitleText = "Sign in";
        return loginTitle(driver).getText().contains(loginTitleText);
    }
}
```

Finally, the `HomeIntTest` class runs our automated tests against our home page. Because we use a page object design pattern you can see that the actual test class is more clean and readable than it would be if we had to hard code each step of the test.

```java
package com.myapp.selenium.tests;

import com.mycompany.myapp.Application;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mycompany.myapp.selenium.pageObjects.HomePageObject;
import com.mycompany.myapp.selenium.pageObjects.LoginPageObject;
import org.junit.rules.TestName;
import com.mycompany.myapp.selenium.base.TestBaseSetup;
import org.springframework.test.util.AssertionErrors;

// We use annotations to run our application on port 9000.
// This port will close after integration tests are completed.
// Note that we run our tests in order by name ascending.
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest(value = "server.port=9000")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HomeIntTest extends TestBaseSetup {

    private final Logger log = LoggerFactory.getLogger(HomeIntTest.class);

    // This allows us to print the test name before and after it runs
    @Rule
    public TestName name = new TestName();

    HomePageObject homePageObject = new HomePageObject(getDriver());
    LoginPageObject loginPageObject = new LoginPageObject(getDriver());

    // Code included in the @Before and @After annotations is executed before and after each test
    @Before
    public void individualTestSetup() {
        // This writes every test name before it runs
        log.info("Running test " + name.getMethodName());
    }

    @After
    // This writes every test name after it completes
    public void logAfterTestsComplete() {
        log.info("Completed test " + name.getMethodName());
    }

    // This test confirms our header text is "Welcome, Java Hipster!"
    @Test
    public void verifiesHomePageHasHeader() throws Exception {
        // Our browser is directed to our homepage before each test:
        openUrl(getDriver(), "/");

        // We use our pageObjects class to retrieve the WebElement with id="mainHeader"
        // We assert that the text of the element with id="mainHeader" is "Welcome, Java Hipster!"
        Assert.assertTrue("Homepage header does not match", homePageObject.verifyMainHeader());
    }

    // This test confirms that we can navigate to the login page through a link on our homepage:
    @Test
    public void clickSignIn() {
        // Our browser is directed to our homepage before each test:
        openUrl(getDriver(), "/");

        // We click on the element with id="loginId":
        homePageObject.homeLoginId(getDriver()).click();

        // We use our pageObjects class to retrieve the WebElement with id="loginTitle" using our LoginPageObject:
        // We assert that the element with id="loginTitle" has the text "Sign in":
        Assert.assertTrue("Login title does not match", loginPageObject.verifyLoginTitle());
    }
}
```

You will notice that we are finding our elements by "id" (`element = driver.findElement(By.id("loginTitle"))`). By default, our JHipster homepage does not have an element with an id of `loginTitle`. Another popular way to find elements on a web page that does not require us to modify any HTML code is by XPath, but this can be inconvenient because you cannot tell what element is being retrieved based on its XPath. It is especially inconvenient to use XPaths if you are regularly making updates to your HTML pages. So, to make this code work we must modify elements in our `main.html`, `login.html`, and `global.json` files.

In `main.html`, we modify line 7 to read as follows:

<h1 translate="main.title" id="mainHeader">Welcome, Java Hipster!</h1>

In login.html, we modify line 14 to read as follows:

```html
<input type="text" class="form-control" id="username" placeholder="{{'global.form.username.placeholder' | translate}}" ng-model="username">
```

In `global.json`, we modify line 51 to read as follows:

```html
"authenticated": "If you want to <a class=\"alert-link\" href=\"#/login\" id=\"loginId\">sign in</a>, you can try the default accounts:<br/>- Administrator (login=\"admin\" and password=\"admin\") <br/>- User (login=\"user\" and password=\"user\").",
```

We can now ensure that everything works by having Maven run our tests. I see that Chrome starts up automatically, navigates to _localhost:9000/_, then navigates to our login page, then closes. Hopefully we find that all of our tests have passed. We now have a basic Selenium test file that we can use as a template to extend our Selenium tests to other pages of our application.

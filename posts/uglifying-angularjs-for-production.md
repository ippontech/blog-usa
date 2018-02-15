---
authors:
- Kenneth Hegeland
categories:
- 
date: 2015-09-15T10:57:21.000Z
title: "Uglifying AngularJS for Production"
image: 
---

A new Version of Tatami was [released](http://www.ipponusa.com/vcu-ippon-angularjs-tatami/) recently, since this initial deployment issues began to surface. Several revisions have been made to address these issues, most notably a release designed to speed load times, using uglification for Tatami which is described [in this post by Justin Risch](http://www.ipponusa.com/increasing-the-performance-of-your-web-application/). The uglification greatly improved performance – from our Richmond, VA office load times went from up to five minutes to about a second. Now that performance issues have been fixed, we must focus on maintaining it, unfortunately uglification in our current form has a downfall, that is, the code is always uglified during build time. Using uglified code in maintenance is a nightmare, the entire JavaScript code base is reduced to around 63 lines of code, which are not easily read by a human. Thus when something goes wrong (and it will), we are tasked with deciphering an error message which previous to uglification wasn’t always helpful, but now we also have no reference to where the issue came from. Using grunt it is possible to specify when to uglify angularjs code, and when not to uglify angularjs code – ideally, we want a development environment which does not uglify code.

#### **Creating an Uglification Task in Grunt**

Using grunt and [grunt-template](https://www.npmjs.com/package/grunt-template), we can generate an index file which includes different files based on the task being run (we would like a development task, and a build task). We will create an index template file which will be used to create the specific index file required, for Tatami, our template file looks like this

```language-html

    <meta charset="utf-8">
    <title>Tatami</title>
    <meta name="author" content="Ippon Technologies">
    <link rel="shortcut icon" type="image/x-icon" href="/assets/img/company-logo.ico">
    <%= cssTags %>
    <link href="/assets/css/tatami.css" rel="stylesheet" type="text/css">

    <body ng-app="TatamiApp" tour>
        <toast></toast>
        <div ui-view="topMenu"></div>
        <div ui-view></div>
        <div ui-view="footer"></div>
        <%= jsTags %>

```

index.html.tpl

In the file we have two important tags to note: `<%= cssTags %>` and `<%= jsTags %>`.  These reference the variables cssTags, and jsTags in the subtask definitions in our gruntfile. The task definitions are given by

```language-javascript
grunt.initConfig({
        ...
    template: {
        prepareMinIndex: {
            options: {
                data: {
                    jsTags: '<script src="TATAMI.CONCAT.js"></script>',
                    cssTags: '			<link href="/css/CSSMIN.css" rel="stylesheet" type="text/css">'
                }
            },
            files: {
                'src/main/webapp/index.html': ['src/main/webapp/index.html.tpl']
            }
        },
        prepareDevIndex: {
            options: {
                data: {
                    jsTags: prepareTags(jsFiles, '<script src="', '"></script>'),
                    cssTags: prepareTags(cssFiles, '			<link href="', '" rel="stylesheet" type="text/css">')
                }
            },
            files: {
                'src/main/webapp/index.html': ['src/main/webapp/index.html.tpl']
            }
        }
    },
    concurrent: {
        minifyTarget: ['template:prepareMinIndex', 'cssmin', 'htmlmin', 'uglify'],
        devTarget: ['template:prepareDevIndex', 'htmlmin']
    }
})
```

Note that in our grunt file, template has two subtasks, one for each build version, and in each they have cssTags, and jsTags in the data object. In both, the value will be inserted into the index file, for the development version, a method is used to build the string to be inserted, since the file list is referenced more than once. We also register concurrent tasks to generate the template along with a few other tasks. Now we simply register a build, and dev grunt task,

```language-javascript
grunt.initConfig({
	...
    grunt.loadNpmTasks('grunt-template');
    ...
    grunt.registerTask('minify', ['clean','concurrent:minifyTarget']);
    grunt.registerTask('dev', ['clean', 'concurrent:devTarget']);
})
```

running grunt minify will generate our index file with the uglified sources being used, and grunt dev will generate the index file with each file included as is (not uglified).

#### **Adding a Maven Profile for Uglification**

Our default maven build is set to not minify anything, thus we can type mvn jetty:run, and we will start Tatami in a development environment. We would like to be able to test that our uglified build works, so rather than adding the command grunt minify to the build profile, we create a new profile, named uglified. The uglified profile is created by adding the following lines to the pom file:

```language-xml
<profile>
    <id>uglified</id>
    <build>
        <plugins>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>1.2.1</version>
                <executions>
                    <execution>
                        <phase>compile</phase>
                        <goals>
                            <goal>exec</goal>
                        </goals>
                        <configuration>
                            <executable>${project.npm.bin}/grunt</executable>
                            <arguments>
                                <argument>minify</argument>
                            </arguments>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</profile>
```
The maven plugin will run grunt minify during compile time, we can start up Tatami with this profile by running mvn -Puglified jetty:run. Our default profile already ran grunt for minification, the only change is to add the dev command line argument to have the default profile become our development profile.

Finally, during production, we can use mvn -Puglified,build … in order to generate an uglified build ready for release.

This change has been merged with master, can be viewed [on GitHub](https://github.com/ippontech/tatami/commit/ba4ea16accb03f4b13adb31428bb467c256b274b#diff-595bf3fa2348192244b0319be33066b8) and has been [deployed](http://app.tatamisoft.com/#/login). With this change, those working on Tatami will be able to more easily address any issues and provide the necessary fixes.

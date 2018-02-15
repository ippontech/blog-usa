---
authors:
- Matt Ritter
categories:
- Front-End
date: 2016-12-16T21:17:04.000Z
title: "Angular 2, system.js and webpack"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/angular.jpg
---

Angular2’s tutorials and quick start use SystemJS to load your app.  I have often used webpack for my front end distributions and decided to take a look at the differences.

I will be using gulp, and have two branches [in a repo](https://bitbucket.org/IpponMattRitter/angular2-color-palette) for a simple app to compare how SystemJS and webpack serve up the same app.

I am using Chrome’s developer tools to look at load time. Perhaps not the most accurate, but for comparisons it suits this fine.

Let's look at load time for the two approaches.
First using SystemJS, out of the branch named systemjs in the above Bitbucket repo.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/angular2SystemjsPerformance.png)

Using SystemJS we have 51 requests, 2.1mb transferred, finished in 4.44s, Dom Content loaded in 1 s, with final load in 1.11s.  The app is not usable until the finished time of 4.44s.  Compiler.umd.js, being 782k, is the biggest hold up.

Now let's see how webpack does.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/Angular-2-color-palette-webpack.png)

We have 11 requests, 850kb transferred, finish in 1.73s, DomContentLoaded: in 2.17s and load in 2.17s.  The app is usable at 2.17 seconds.

As mentioned earlier I’m using gulp for webpack, I also used gulp for the SystemJS, but as that is pretty similar to what you get out of Angular2’s quick start there isn’t much point in discussing it.

```language-javascript
const gulp = require('gulp'),
    tslint = require('gulp-tslint'),
    webpack = require('gulp-webpack'),
    uglify = require('gulp-uglify'),
    sass = require('gulp-sass'),
    del = require('del'),
    // variables for source and public directories
    sourceDir = "./app/source",
    publicDir = "./app/public";
```

The packages I’m using in addition to gulp are:

- **gulp-tslint** is used with watch.
- **gulp-sass** is used both with watch and as part of the build process.
- **gulp-del** is part of the clean process.

You may also notice I’m using two variables “sourceDir” and “publicDir.” They are, as their name suggests, the location of the source files and public, or distribution, files. The gulp file in the repo has comments to explain what and why.

Gulp-webpack is what handles the bundling.

```language-javascript
gulp.task('webpack', ['clean', 'tslint'], function () {
	return gulp.src(sourceDir + '/')
	.pipe(webpack({
		entry: {
			app: sourceDir + '/main.ts',
		},
		output: {
			filename: '[name].js'
		},
		resolve: {
			extensions: ['','webpack.js', 'web.js', '.ts', '.js']
		},
		module: {
			loaders: [
				{ test: /\.ts$/, loader: 'ts-loader'}
			]
		}
	}))
	.pipe(uglify())
	.pipe(gulp.dest(publicDir + '/js/'))
});
```

Modules are needed so webpack will know what to do with TypeScript. Uglify is then minimizing the final Javascript file.

It is not surprising that webpack would load quicker, but I did not expect it to make as much of a difference as it did. SystemJS is great for a development environment, and debugging, as you can easily see what files are creating issues. Just keep in mind that if you are having page performance and loading issues, it might be worth checking that first.

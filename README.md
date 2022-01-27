# Ippon USA Blog

This repository is used to manage all blog posts published by Ippon USA. All drafting, editing, and peer reviewing is done through this repository following standard Git best practices.

## Basic workflow

To submit a post to be published on the blog, you need to make a Pull Request on this repository:
- Clone this repository
- Make changes in your fork
- Submit a Pull Request
- ASK one or multiple people to review your blog. Do not just post a link into slack or workplace.
- Once the review process is complete, the PR will be merged and your post will be published on [Ippon's blog](https://blog.ippon.tech/)

## How to write a post

- Create a file in the `posts/` directory
- The name of the file should be a _lowercase-dashed_ version of the title, with a `.md` extension ("Example title" -> `example-title.md`)
- Add images in the `images/<YYYY>/<MM>/` directory
- Start your post with metadata (see below)
- Write your post in [Markdown](https://guides.github.com/features/mastering-markdown/)

## Metadata

Each post should start with metadata. Here is an example:

```
---
authors:
- Julien Dubois
tags:
- Cassandra
- JHipster
date: 2015-04-23T12:21:50.000Z
title: "10 Tips and tricks for Cassandra"
image: 
---
```

A few notes:

- Authors: you can list one or multiple authors. Authors must exist in Ghost before a post can be sent to Ghost (when a PR is merged).
- Tags: you can list zero, one or more tags. If tags don't exist in Ghost, they are ignored.
- Date: date can be ignored. The real publication date will be overriden from Ghost.
- Title: this should "match" the _slug_ of the post, i.e. the name of the file after converting to lowercase and replacing spaces with dashes.
- Image: an absolute URL to an image file (optional).

## Images

To include an image in your post, first add it in the `images/<YYYY>/<MM>/` directory.

You can then reference the image by using the following markdown snipet:

```
![alternate text](https://github.com/<your account>/blog-usa/blob/master/images/<YYYY>/<MM>/<image>)
```

## Code

To add a code snippet, use triple backquotes and add the name of the language for syntax highlighting:

    ```java
    public class Test {
    }
    ```

For the list of supported languages, see [languages-list](https://prismjs.com/#languages-list).

Use `text` if you can't find the language you are looking for, but don't just omit the language name (the rendering would be completely different).

## A few rules to follow when writing a post

- Start numbering your titles at level 1 (not level 2 like we used to do).
- Don't start a post with a level 1 title, the title indicated in metadata will already be there.
- A paragraph should be a single line of text (no manual wrapping).
- Paragraphs need to be separated by a blank line.
- Avoid abbreviations (e.g. "I've" -> "I have").
- Put your links _on_ the text itself, not after the text it refers to.

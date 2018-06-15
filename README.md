# Ippon USA Blog

This repository will be used to manage all blog posts published by Ippon USA. All drafting, editing, and peer reviewing will be done through this repository following standard Git best practices.

This `README.md` will provide instructions for both a technical audience and a non-technical audience. If you have limited or no `git` experience it is recommended that you follow the "non-technical" section.

*******************************************************************************

## Basic rules when writing a post

- Use GitHub's standard Markdown.
- Start numbering your titles at level 1 (not level 2 like we used to do).
- A paragraph should be a single line of text (no manual wrapping).
- Paragraphs need to be separated by a blank line.
- Avoid abbreviations (e.g. "I've" -> "I have").
- Put your links _on_ the text itself, not after the text it refers to.

*******************************************************************************

## Getting Started (TECHNICAL)

1. Create a fork of the [Ippon USA Blog Repo](https://github.com/ippontech/blog-usa)
2. Clone the repo onto your local machine (unless you wish to work directly in the Github UI)

## Writing a new post

### Drafting a Post
  
1. Create a new file with the `.md` extension in the `blog-usa/posts` directory
2. Write the blog post. Be sure to commit often

#### Adding Graphics

1. After choosing the graphic that you would like to use in your post, add it to the `blog-usa/images/<year>/<month>` directory
2. Create link to your image using the format below:
```
{{< responsive-figure src="link_to_your_graphic" >}}
```
*** This image may not appear on the "Preview" in Github

### Submit Post for Peer Review

Once your post is ready for peer review, be sure to commit any recent updates. 

1. Push your changes to your forked remote repository
2. Submit a pull request from your forked remote repo to the main `blog-usa` repo. Be sure to provide a descriptive title, perferably one similar to your log post title
3. You are now ready to solicit peer review request for your blog. Provide a link to your pull request to anyone you would like to peer review your blog

### Peer Review A Post

Reviewing a blog post will all be done via commenting in the pull request. After receiving a pull request to someones blog post, submit any edits, suggestions, or additions you may have. Be sure to review exisiting comments to prevent duplicates.

### Finalizing Your Post

1. Review suggested changes in the comment section of your pull request
2. Make any changes to your blog post either locally (be sure to commit and push) or directly in Github
3. Notify the blog administrator that the post is done and ready for publishing

*******************************************************************************


## Getting Started (Non-Technical)

These instruction are targeted towards someone with limited to no experience with Git. This workflow will rely heavily on the Github UI to prevent from having to run `git` commands.

### Prerequisites

Create a [Github account](https://github.com/join) if you don't have one already.

### Forking the `blog-usa` repository

1. Login to your Github account and go to the [Ippon USA Blog](https://github.com/ippontech/blog-usa) Github repository page
2. Click on the ["Fork" icon](https://github.com/ippontech/blog-usa/raw/master/images/2018/02/fork_image.png) in the upper right hand corner. This will create a forked copy of the `blog-usa` repository in your Github remote account
3. Be sure the title of the repository your currently in is your forked repo NOT the main Ippon USA Blog repo (e.g. `<username>/blog-usa`)

## Writing a new post

### Drafting
  
1. While in your forked repo, select the `post` directory  
2. Select the "Create new file" button to create a new file with the `.md` extension (e.g. `your_blog.md`)
3. Write the blog post. Use the "Preview" option to get an idea of what your blog will look like when published
4. Choose the "Commit directly to `master` branch." option and select the "Commit new file" button
5. To make any further changes to your post, locate the the file in the `blog-usa/posts` directory. After clicking on your blog post there is a pencil icon on the right hand side. Select this icon to edit your blog. When you have completed your changes and you would like to save (aka commit) scroll to the bottom and "commit new file" directly to the `master` branch

### Adding Graphics

1. After choosing the graphic that you would like to use in your post, add it to the `blog-usa/images/<year>/<month>` directory
2. Create link to your image using the format below:
```
{{< responsive-figure src="link_to_your_graphic" >}}
```
*** Note this image will not appear in you the Github preview

### Submit Post for Peer Review

1. Navigate to the main page of your forked repository
2. Select the "New Pull Request" button
3. Be sure you are merging to the correct repo and branch. You should be merging your `master` branch in your forked repository to the `master` branch in the main Ippon `blog-usa` repo
4. Select the "Create pull request" button and provide a descriptive title and any neccessay comments. Then select the "Create pull request" button again
5. You are now ready to solicit peer reviews. Provide a link to you pull request to anyone you would like to peer review your blog post

### Peer Review A Post

Reviewing a blog post will all be done via commenting in the pull request. After receiving a pull request to someones blog post, submit any edits, suggestions, or additions you may have. Be sure to review exisiting comments to prevent duplicates.

### Finalizing Your Post

1. Review suggested changes in the comment section of your pull request
2. Make any changes to your blog post directly in Github (be sure to save aka commit)
3. Notify the blog administrator that the post is done and ready for publishing

*******************************************************************************

## Resources

* [Markdown in Sublime](http://cheng.logdown.com/posts/2015/06/30/sublime-text-3-markdown)
* [Markdown Live Preview](http://markdownlivepreview.com)
* [Forking Workflow](https://www.atlassian.com/git/tutorials/comparing-workflows/forking-workflow)
* [Git Cheatsheet](https://www.atlassian.com/git/tutorials/atlassian-git-cheatsheet)

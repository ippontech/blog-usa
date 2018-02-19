# Ippon USA Blog

This repository will be used to manage all blog post published by Ippon USA. All drafting, editing, and peer reviewing will be done through this repository following standard Git best practices. 

## Getting Started

In order to contribute in any way to the Ippon USA blog whether you are drafting a post or reviewing you must follow these steps to clone the repository

### Prerequisites

Before you can interact with the `blog-usa` Git repository you must first install and configure Git

1. Open a terminal and check to see if Git is already installed: `git --version`. If git is installed you should receive a similar response: `git version 2.9.2`

2. Install Git using 1 of the following techniques
	2. [Homebrew](https://gist.github.com/derhuerst/1b15ff4652a867391f03#file-mac-md)
	2. [Git for Mac Installer](https://www.atlassian.com/git/tutorials/install-git)
3. Configure you Git setting if you haven't already done so:
```
git config user.name "First Last"
```
```
git config user.email "your_email@ipponusa.com"
```
4. Create a Github account if you don't have one already

### Setting up your local `blog-usa` repository

1. Login to your Github account and go to the [Ippon USA Blog](https://github.com/ippontech/blog-usa) Github repository page
2. 
2. Open a terminal on your computer (e.g. Terminal, iTerm)
3. Change into the directory you would like to store the `blog-usa` repository: `cd ~/Development/Ippon`
4. Clone the `blog-usa` repository: `git clone https://github.com/ippontech/blog-usa.git`

## Writing a new post

### Drafting

1. Create a new branch with the title of your new blog: `git checkout -b sample-blog`  
2. Create a new file with the `.md` extension in the `blog-usa/posts` directory
3. Commit this change
```
git add sample-post.md
git commit
```
4. Write the blog post. Be sure to commit often. Committing == Saving

### Adding Graphics

1. After choosing the graphic that you would like to use in your post, add it to the `blog-usa/images/<year>/<month>` directory
2. Create link to your image using the format below:
```
{{< responsive-figure src="link_to_your_graphic" >}}
```

### Submit Post for Peer Review

Once your post is ready for peer review, be sure to commit any recent updates. Then push your changes to the remote repository: `git push`

### Peer Review A Post

1. Fetch all the remote branches: `git fetch`
2. Checkout the branch pertaining to the blog post you plan to peer review: `git checkout sample-blog`
3. Make changes to the the blog you are peer reviewing
4. Commit your changes leaving detailed notes
5. Push your changes to the remote repo: `git push`

## Resources

### Previewing the Post

* [Markdown in Sublime](http://cheng.logdown.com/posts/2015/06/30/sublime-text-3-markdown)
* [Markdown Live Preview](http://markdownlivepreview.com)
* [Forking Workflow](https://www.atlassian.com/git/tutorials/comparing-workflows/forking-workflow)


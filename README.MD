# Ippon USA Blog

This repository will be used to manage all blog post published by Ippon USA. All drafting, editing, and peer reviewing will be done through this repository following standard Git best practices.

This `README.md` will provide instructions for both a technical audience and a non-technical audience. If you have limited or no `git` experience it is recommended that you follow the "non-technical" section.

## Getting Started (TECHNICAL)

1. Create a fork of the [Ippon USA Blog Repo](https://github.com/ippontech/blog-usa)
2. Clone the repo onto your local machine unless you wish to work directly in the Github UI.

## Writing a new post (TECHNICAL)

### Drafting a Post
  
1. Create a new file with the `.md` extension in the `blog-usa/posts` directory
2. Write the blog post. Be sure to commit often
3. Submit a pull request from your forked remote repo to the main `blog-usa` repo. Be sure to provide a descriptive title, perferably one similar to your log post title

### Adding Graphics

1. After choosing the graphic that you would like to use in your post, add it to the `blog-usa/images/<year>/<month>` directory
2. Create link to your image using the format below:
```
{{< responsive-figure src="link_to_your_graphic" >}}
```

### Submit Post for Peer Review

Once your post is ready for peer review, be sure to commit any recent updates. Then push your changes to your forked remote repository. You are now ready to solicit peer review request for your blog. Provide a link to your pull request to anyone you would like to peer review your blog.

### Peer Review A Post

Reviewing a blog post will all be done via commenting in the pull request. After receiving a pull request to someones blog post, submit any edits, suggestions, or additions you may have. Be sure to review exisiting comments to prevent duplicates.

### Finalizing Your Post

After the peer reviews have been completed, you can review the suggested changes in the comments of your pull request. Make any chages to your blog post directly in Github or locally (be sure to commit and push).












## Getting Started (Non-Technical)

These instruction are targeted towards someone with limited to no experience with Git. This workflow will rely heavily on the Github UI to prevent from having to run `git` commands.

### Prerequisites

Create a [Github account](https://github.com/join) if you don't have one already.

### Forking the `blog-usa` repository

1. Login to your Github account and go to the [Ippon USA Blog](https://github.com/ippontech/blog-usa) Github repository page
2. Click on the ["Fork" icon](https://github.com/jhennin/blog-usa/blob/master/images/2018/02/fork_image.png) in the upper right hand corner. This will create a forked copy of the `blog-usa` repository in your Github remote account
3. Be sure the title of the repository your currently in is your forked repo NOT the main Ippon USA Blog repo (e.g. `<username>/blog-usa`)

## Writing a new post

### Drafting
  
1. Create a new file with the `.md` extension in the `blog-usa/posts` directory
2. Commit this change
```
git add sample-post.md
git commit
```
3. Write the blog post. Be sure to commit often. Committing == Saving
4. Submit a pull request from your forked remote repo to the main `blog-usa` repo. Be sure to provide a descriptive title, perferably one similar to your log post title

### Adding Graphics

1. After choosing the graphic that you would like to use in your post, add it to the `blog-usa/images/<year>/<month>` directory
2. Create link to your image using the format below:
```
{{< responsive-figure src="link_to_your_graphic" >}}
```

### Submit Post for Peer Review

Once your post is ready for peer review, be sure to commit any recent updates. Then push your changes to your forked remote repository. You are now ready to solicit peer review request for your blog. Provide a link to your pull request to anyone you would like to peer review your blog.

### Peer Review A Post

Reviewing a blog post will all be done via commenting in the pull request. After receiving a pull request to someones blog post, submit any edits, suggestions, or additions you may have. Be sure to review exisiting comments to prevent duplicates.

### Finalizing Your Post

After the peer reviews have been completed, you can review the suggested changes in the comments of your pull request. Make any chages to your blog post directly in Github or locally (be sure to commit and push).

## Resources

* [Markdown in Sublime](http://cheng.logdown.com/posts/2015/06/30/sublime-text-3-markdown)
* [Markdown Live Preview](http://markdownlivepreview.com)
* [Forking Workflow](https://www.atlassian.com/git/tutorials/comparing-workflows/forking-workflow)
* [Git Cheatsheet](https://www.atlassian.com/git/tutorials/atlassian-git-cheatsheet)

```
---
authors:
- Rodrigo Moran
tags:
- Golang
- CLI
date: 	2021-12-01T13:50:040Z
title: "A No Framework Approach to Building A CLI"
image:
---
```

# A No Framework Approach to Building A CLI with Go

---

1) I want to understand how to build useful intuitive APIs by looking at existing examples. Understanding what some of the key feature/aspects are. (Good developers build they're own tools). You can build CLIs in any language some these aspects should stay the same.
2) I want to understand and replicate a well known frameowork by stripping it down and building a version back up.
3) A well used version is Hashicorp CEO's personal command line library used in all their CLI products.

**Why use them**
**What makes it useful**
**Why build your own**

---

How many CLIs do you use on a daily basis? I'm guessing more than a hands worth not including os specific tools. Which ones came to mind?
- Git
- Docker
- Kubectl
- AWS
- JHipster
- Terraform
- PSQL/MSSQL
- etc

Ever wonder how Enterprise grade CLIs are made? How the Docker, Kubectl, and Terraforms of the world are built? ~~What if I told you you too could a build powerful, multifunctional, crossplatform CLI with just a few lines of Go~~.

That's what I'll be covering in this article and along the way we'll build our own CLI **Gupi**:

![[help_screen.png]]
 In this blog post, I'll touch on what those 3 Enterprise CLIs have in common, what they do well, how frameworks that power them functions, and walk through how to build our own using only the Go Std Library.

## A brief look at popular CLIs
- CLIs are a daily part of an engineers daily life -> Integral part
- Docker, Kubectl, Terraform -> Every stack has its essentials tools
- Readable CLIs with good documentation always win -> Most used
- Insert your essential custom project-specific CLI -> Hashicorp diy framework

Command Line Interfaces (CLIs) are an integral part of our daily routines. Most of us have macros, aliases, and cheats sheets filled with commands we use daily and a few one-offs we'd rather not lose. In the mix are CLIs that are a pleasure to use along with plenty of project specific scripts with little to no documentation. Sad thing is that those unfriendly scripts are often critical to the project.
~~*^ More of a a transition paragraph ^* ~~

Every language or stack has its own set of essential CLIs, of coarse there's a few that seem to be included in every project like ***git***. And if your project has anything to do with infrastruce you've probably run into the likes of Docker, Terraform, and/or Kubectl. Not to mention any cloud specific CLIs such as AWS CLI, Azure CLI, or GCP CLI.

Kubectl and Docker are crowd favorites for a reason, they both walk you through how to use them. And when you make a mistake they don't give you a cryptic message you need to decipher instead they usually have suggestions of what likely went wrong and how to fix them. On top of that the commands when read sound like a normal commands, not only human readable but could be commands to another person.

Just like any other popular Open Source Tool, projects with more documentation often most loved and used. Popular CLIs ad here to the same rules and on top of usually having external documentation they more often than not provide all the info you need right in the inline *help* documention.

I point out Kubectl and Docker in particular since they are both written using Go and the CLI Framework Cobra. We won't be using Cobra for our CLI but we'll be using it as reference. Also want to point out Terraform and the other CLIs from Hashicorp since they're good example using a custom CLI framework after understanding your needs and building what fits your use case. Essentially want we will be doing for our CLI.
~~I'd like to walk through a few examples of CLIs that are a pleasure to use.~~


Go is a fantastic language especially for CLI development for a reasons mentioned [here]()

Terraform, I checked before writting has moved away the framework in favor of whats looks to be minimal built by the founder.

Most popular CLI framework for Go thats used by most major Go command line tools.

![[kubectl_help 1.png]]
What Cobra is and how it works is one of the main topics I will cover but more on that later.

For now lets take a peek at the look and feel of some the CLIs I mentioned.

Its so good I want to understand how it works

## What makes them so useful?
- Bundles up functionallity into a single commands
- Using flags allows for simplified customization
- Allows for easier automation
- Readable CLIs allows anyone to understand the logic including new engineers
- Args + subcommands + flags

If you're reading this you probably don't need to be convinced CLIs are great but for anyone on the fence I'll outline a few key features.

When it comes to CLIs, first and foremost is functionallity, any good CLI will save time. That could be as little as skipping a webpage to get the info you need to bundling up serveral API calls into one command and beyond. A quick example is Kubectl, the interface for Kubenetes, strictly speaking its not nessesary since K8s comes with an awesome API. You could curl or use your favorite API Client to sends requests all day but whats often the case is you're dealing with large payloads or many calls and ti quicly become unreasonable.

~~the key feature is that they save time bundling here's an example using Kubernetes and Docker. Neither service neccessarily needs a dedicated CLI since they each have powerful APIs (these are what powers many modern CLIs). Many of you may have experience sending requests to the Kubernetes API-Server for example:~~

```shell
curl -X POST /api/namespaces/default/pods/...
```

This is a simple example but in reality anything useful could take several API calls, where as single command could bundle up multiple calls.

If CLIs save you time then the time saved gets multiplied if you doing automation
Well written CLIs allow for simplified automation.

Readable CLIs allow new users to get started quickly.

Configurable CLIs allow for easy customization using flags and feature experementation with feature flags.


~~The power of a CLI comes how it can be configured, extended, functional from what it allows by bundeling functionallity, how can be extened or automated. the functionallity it allows and how it could be extended or automaFunctionallity First and foremost is that they help accomplish tasks that otherwise would take more effort. In my opinion what makes a CLI great is how intuitive it is to use. Does the api makes sense? How friendly is it when I envitably make a mistake? How helpful are the error messages? Are the commands and flags easy to understand and use? Those are just a few that come to mind first.~~

~~Above all it should work as intended/expected no more / no less. What I've come to expect after using Git, Docker, Kubectl, or AWS is the options for subcommands. Most classic even most new linux commands don't support... better yet they don't need subcommands (ls, grep, find, etc...). Newer CLIs support various functions so subcommands make sense but it becomes more important that they make sense. This one feature I want to support in our CLI. ~~



~~Sure CLIs are often used for automations but the main users are still developers, people might be thte ones struggling to find an issue or use the correct options. If there's too much confusion or poor documentation dev might run to next available option (head for the hills), thats why being friendly is so important. Another requirement is to support friendly prompts, messaging, and instructions. ~~

## Why build and understand your tools
Well its good to understand your tool set. You know what to add how to extend, what to remove. A great is example of building simplified toolset is the CLI library from the founder of Hashicorp, [CLI](https://github.com/mitchellh/cli)

## 30 second explainer how Cobra works
There is two sides to [Cobra](https://cobra.dev/), a CLI framework and a CLI generator. The generator helps to rapidly create a CLI by instantiating the skaffolding using code generation. The framework is the core of Cobra, this is how you define commands, subcommands, function logic and flags. The generator side could be its own full blown article diving into code generationone but that will have to wait. The rest of this post will be focusing on the actual framework side.

![[cobra_logo.png]]
The framework is built around around the concept of building commands that read like sentances. Lets take a look at a Kubectl example:

```shell
$ kubectl get pods --namespace=cobra
```

Whether or not you're familair with Kubectl you can read the command and reason about what it is supposed to do.  We are telling Kubectl to *get* **pods** in the `namespace` cobra, where pods refers to containers running in Kubernetes.

The pattern you will begin noticing from any human readable CLI, is as follows:

```shell
$ app verb noun --adjective
```

or in command line terms:

```shell
$ app command arg --flag
```

This behavior is built into the core [Command](https://github.com/spf13/cobra/blob/master/command.go) struct in the framework, structs are roughly the Class equivalent in Go. How you define your Commands and how you chain them together is how readable commands come to be.

This highly truncated version of the Command struct is where we will be focusing when building our CLI.

```go
type Command struct {
	...
	// Short is the short description...
	Short string
	// Run: Typically the actual work function...
	Run func(cmd *Command, args []string)
	// args is actual args parsed from flags.
	args []string
	// flags is full set of flags.
	flags *flag.FlagSet
	// usageFunc is usage func defined by user.
	usageFunc func(*Command) error
	...
}
```

## How to build your own CLI
You can find the complete application in this [repository]()

### *Agenda*
- [[#What makes them so useful|Defining the functionallity]]
- [[#Initialize a new project]]
- [[#Create a Command Struct]]
- [[#Add our Version Command]]
- [[#Add our Add Command]]
- [[#Add our List Command]]
- [[#Add Edit Command]]
- [[#Add Delete Command]]
- [[#Add Create Command]]
- [[#Bonus Makefile with LDFlags]]

### What are we building

What we are building does not matter as much as *how* we are building it. It could easily be adapted to do whatever you need.

The CLI we are building can store, edit, list, delete and instantiate templates. We will be focusing on the Go Standard library and keep 3rd party dependencies to a minimum. The commands or features will be defined as follows:

1. *Add*: Adds an existing template to the collection via a local file

```shell
$ gupi add TEMPLATE_NAME -f /path/to/template.md
Template 'TEMPLATE_NAME' was added
```

2. *Edit*: Uses the default text editor to modify a stored template

```shell
$ gupi edit TEMPLATE_NAME
Template 'TEMPLATE_NAME' was edited
```

3. *List*: Lists all stored templates

```shell
$ gupi list
NAME               CREATED       SIZE
todo-list	       2 weeks ago   482
design-doc         3 days ago    300
```

4. *Create*: Generates an instance of a template in the current directory

```shell
$ gupi create TEMPLATE_NAME
Created 'TEMPLATE_NAME' in '/path/to/TEMPLATE_NAME'
```

5. *Delete*: Removes a stored template

```shell
$ gupi delete TEMPLATE_NAME
Template 'TEMPLATE_NAME' was deleted
```

6. *Version*: Prints version info to console

```shell
$ gupi version
gupi version: v0.1, build: 893b04957563cd7120f817dd654ba745075cfe6b
```

If yo do not already have Go installed, here is the [official installation doc](https://go.dev/doc/install).

### Initialize a new project

First thing we need to do is create the project directory and initialize it as a Git repository. I'll call this project `gupi`:

```shell
$ mkdir gupi
$ cd gupi
$ git init
```

Go has its own way to manage dependencies by using a `go.mod` file, you can easily enable this on a new project by running:

```shell
$ go mod init github.com/USERNAME/gupi
```

The full path `github.com/USERNAME/gupi` is what others would use to download if you packaged and distributed this module. Also helpful for importing local files from subfolders.

### Create a Main

Next lets create our main entry point called: `main.go`

```shell
$ touch main.go
```

Add a basic print statement make sure the environment is working:

```go
package main

import "fmt"

func main() {
	fmt.Println("Working CLI!")
}
```

We can test this app by running:

```shell
$ go run main.go
Working CLI!
```

We just confirmed we have a working Go project but no time to celebrate just yet since it this doesn't feel like a CLI tool just yet.

One thing that quickly stands from CLIs like Docker and Kubectl is the informative and friendly user manuals that are displayed by calling the base command.

Essentialy what I want is:

```shell
$ gupi
Our awesome CLI with instructions on how to quickly get started using it.
Options:
Commands:
	* Wicked Feature 1 - One wicked feature
	* Cool Feature 2 - Coolest feature by far
	* Practical Feature 3 - The daily driver of features
```

New users should be able to understand what the tool does and how to actually use. We already clearly defined the main features of our tool so putting together this usage text will be easy.

So based on the features defined above I came up with this, feel free to modify as you see fit:

```go
var usage = `Usage: gupi command [options]

A simple tool to generate and manage custom templates

Options:

Commands:
  add		Adds a template to the collection from a local file
  edit		Uses the default text editor to modify a stored template
  list		Lists all stored templates
  create	Generates an instance of a template in the current directory
  delete	Removes a stored template
  version	Prints version info to console
`
```

Now that we have a base usage message we can update our main function print this to get a feel for the CLI.

```
func main() {
	fmt.Println(usage)
}
```

If we test this app again it will slowly start feeling like a real CLI.

```shell
$ go run main.go
```

We need to add one more thing before moving one. One important aspect of any CLI tool is exit/status codes. Any CLI needs to return **0** when successfull and anything greater when an error occured.

We'll create this `usageAndExit` method prints the usage message and an optional status message.

```go
func usageAndExit(msg string) {
	if msg != "" {
		fmt.Fprint(os.Stderr, msg)
		fmt.Fprintf(os.Stderr, "\n")
	}

	flag.Usage()
	os.Exit(0)
}
```

If we update our main method as follows, then test:

```go
func main() {
	usageAndExit("")
}
```

You should get this cryptic message after running:

```shell
$ go run main.go
Usage of /var/folders/47/jlb0vvyx0px72mykl96x41xm0000gp/T/go-build235122164/b001/exe/main:
```

Not to worry this just means we are trying to access `flag.Usage()` before setting a usage message. If we update the main method again:

```go
func main() {
	flag.Usage = func() {
		fmt.Fprint(os.Stderr, fmt.Sprint(usage))
	}

	usageAndExit("")
}
```

Now it will behave as expected returning the usage message and the correct exit code.

You can confirm by running:

```shell
$ echo $?
0
```

Regardless of the shell you use the exit code will be the same.

Now that we defined our commands we work on actually creating them.å

### Create a Command Struct

First thing we want to define is the base Command Struct.  Each command needs to be able to have its own set flags and a function defining what to do with those flags. Create a new file `commands.go`

```go
type Command struct {
	flags   *flag.FlagSet
	Execute func(cmd *Command, args []string)
}
```

The `flag.Flagset` is just what is sounds like its a collection of flags, these will be defined be each individual subcommand. And `Execute` is an anonymous function which accepts a `Command` and a array of arguemnts. Note, Go treats functions as first class citizens so we can pass functions as parameters or when defining an instance of a struct. Next we need a few functions on the Command struct, it needs to:

* Parse and assign flags to its flagset
* Should be able to tell if flags have been parsed
* Should be able to execute a given function

What those turn into is:

1. Init()
2. Called()
3. Run()

So the full will look like this:

```go
// command.go
package command

import (
	"flag"
)

type Command struct {
	flags   *flag.FlagSet
	Execute func(cmd *Command, args []string)
}

func (c *Command) Init(args []string) error {
	return c.flags.Parse(args)
}

func (c *Command) Called() bool {
	return c.flags.Parsed()
}

func (c *Command) Run() {
	c.Execute(c, c.flags.Args())
}
```

So now we have enough to define our first command **Version**.

### Add our Version Command

What we need to do first is define a usage message for version.

NOTE: I didn't mention this before but the Version command will accept a single flag `--short`, if added the short version of the build info will be printed.

```go
var versionUsage = `Print the app version and build info for the current context.

Usage: gupi version [options]

Options:
  --short  If true, print just the version number. Default false.
`
```

The next part is cool but not relevant to this post. The TL;DR is we will be using LD-FLAGS to inject the app version and build commit hash into the CLI when building the binary. Here you can find more info on using LD FLAGS.

 I'll define placeholder variables now that will get overwritten during the build.

```go
var (
	build   = "???"
	version = "???"
	short   = false
)
```

As for the meat of the Version command, we'll be using the Command Struct we defined above and we'll go ahead and include the logic to print the build info:

```go
func(cmd *Command, args []string) {
			if short {
				fmt.Printf("brief version: v%s", version)
			} else {
				fmt.Printf("brief version: v%s, build: %s", version, build)
			}
			os.Exit(0)
		}
```


```go
func NewVersionCommand() *Command {
	cmd := &Command{
		flags: flag.NewFlagSet("version", flag.ExitOnError),
		Execute: version_func,
	}

	cmd.flags.BoolVar(&short, "short", false, "")

	cmd.flags.Usage = func() {
		fmt.Fprintln(os.Stderr, versionUsage)
	}

	return cmd
}
```

This function can be broken down into 3 separate actions:

1. Define a flagset for the command `version` which will exit if any error occurs
2. Use the specified print logic based on the flags passed
3. Attach the short flag to the command
4. Attach the specific version usage message

As it stands we actually use just yet, we have to make some modifications to our main function. To actaully be able to use the version subcommand we need to parse all the avaialbe flags, if nothing is passed we'll return the base usage message and exit, otherwise we need to instantiate on of the commands, then init and run it.

```go
func main() {
	flag.Usage = func() {
		fmt.Fprint(os.Stderr, fmt.Sprint(usage))
	}

  flag.Parse()
	if flag.NArg() < 1 {
		usageAndExit("")
	}
}
```

Instantiate command after the switch statement then run the command.

```go
func main() {
	flag.Usage = func() {
		fmt.Fprint(os.Stderr, fmt.Sprint(usage))
	}

	flag.Parse()
	if flag.NArg() < 1 {
		usageAndExit("")
	}

	var cmd *command.Command

	switch os.Args[1] {
	case "version":
		cmd = command.NewVersionCommand()
	default:
		usageAndExit(fmt.Sprintf("gupi: '%s' is not a gupi command.\n", os.Args[1]))
	}

	cmd.Init(os.Args[2:])
	cmd.Run()

	briefCmd.Init(os.Args[2:])
	briefCmd.Run()
}
```

Now we accept subcommands and start testing with and without the `--short` flag:

```shell
$ go run main.go version --short
brief version: v???

$ go run main.go version
brief version: v???, build: ???
```

For now the `???` is fine we'll replace those during the build process later on. At this point we've touched on all the core components of creating a CLI

* Friendly messaging and instructions
* Adding new subcommands
* Adding subcommand specific flags
* Wiring up a few components together

The next few steps will move faster since we're just repeating the same steps to add more functionallity to our CLI.

### Add our Add Command

Create `add.go`:

```go
package command

import (
	"flag"
	"fmt"
	"io/ioutil"
	"os"
)

var filePath string

var addUsage = `Add a template from a file path or URL.

Usage: brief add [OPTIONS] TEMPLATE

Options:
	--file	path to an existing template file
`

func NewAddCommand() *Command {
	cmd := &Command{
		flags: flag.NewFlagSet("add", flag.PanicOnError),
		Execute: func(cmd *Command, args []string) {
			if len(filePath) == 0 {
				fmt.Println("File path required")
				os.Exit(1)
			}

			if _, err := os.Stat(filePath); err != nil {
				fmt.Println("File does not exist")
				os.Exit(1)
			}

			f, err := ioutil.ReadFile(filePath)
			if err != nil {
				fmt.Printf("failed to read from %s\n", filePath)
				os.Exit(1)
			}

			file_name := fmt.Sprint("copy-", filePath)
			if len(args) != 0 {
				file_name = args[0]
			}
			out, err := os.Create("/Users/rodrigomoran/Workspace/brief/template/" + file_name)
			if err != nil {
				os.Exit(1)
			}
			defer out.Close()

			out.WriteString(string(f))
		},
	}

	cmd.flags.StringVar(&filePath, "file", "", "")

	cmd.flags.Usage = func() {
		fmt.Fprintln(os.Stderr, addUsage)
	}

	return cmd
}
```

Then update the switch case in our main file:

```go
	switch os.Args[1] {
	case "add":
		cmd = command.NewAddCommand()
	case "version":
		cmd = command.NewVersionCommand()
	default:
		usageAndExit(fmt.Sprintf("gupi: '%s' is not a gupi command.\n", os.Args[1]))
	}
```

Now you'll need to create a simple template file to test this subcommand, I'll create `test.md`:

```markdown
this is a test template
```

```shell
$ go run main.go add --file test.md test
```

### Add our List Command

```go
package command

import (
	"flag"
	"fmt"
	"os"
)

var listUsage = `List all currently avaible templates.

Usage: brief list
Options:
`

func NewListCommand() *Command {
	cmd := &Command{
		flags: flag.NewFlagSet("list", flag.ExitOnError),
		Execute: func(cmd *Command, args []string) {
			file, err := os.Open("/Users/rodrigomoran/Workspace/gupi/template")
			if err != nil {
				errAndExit("File not found")
			}
			defer file.Close()

			filelist, _ := file.Readdir(0)

			fmt.Printf("NAME\t\tSIZE\t\tMODIFIED")
			for _, files := range filelist {
				fmt.Printf("\n%-15s %-15v %v", files.Name(), files.Size(), files.ModTime().Format("2006-01-02 15:04:05"))
			}
		},
	}

	cmd.flags.Usage = func() {
		fmt.Fprintln(os.Stderr, listUsage)
	}

	return cmd
}
```

```shell
$ go run main.go list
```

### Add Edit Command

```go
var editUsage = `Edits an existing template.

Usage: brief edit TEMPLATE

Options:
`

func NewEditCommand() *Command {
	cmd := &Command{
		flags: flag.NewFlagSet("edit", flag.ExitOnError),
		Execute: func(cmd *Command, args []string) {
			...
			if _, err := os.Stat(file_path); err == nil {
				command := exec.Command("vim", file_path)
				command.Stdout = os.Stdout
				command.Stdin = os.Stdin
				command.Stderr = os.Stderr
				err := command.Run()
				if err != nil {
					os.Exit(1)
				}
			}
			fmt.Printf("gupi: Template '%s' was edited", file_name)
		},
	}
	...
	return cmd
}

```

```shell
$ go run main.go edit test
brief: Template 'test' was edited⏎
```

### Add Delete Command

```go
func NewDeleteCommand() *Command {
	cmd := &Command{
		flags: flag.NewFlagSet("delete", flag.ExitOnError),
		Execute: func(cmd *Command, args []string) {
			if len(args) == 0 {
				os.Exit(1)
			}
			file_name := args[0]

			path := filepath.Join("/Users/rodrigomoran/Workspace/gupi/template", file_name)
			if _, err := os.Stat(path); err == nil {
				os.Remove(path)
				fmt.Printf("brief: Template '%s' was deleted", file_name)
			}
		},
	}
	...
	return cmd
}

```



### Add Create Command

```go
func NewCreateCommand() *Command {
	cmd := &Command{
		flags: flag.NewFlagSet("create", flag.PanicOnError),
		Execute: func(cmd *Command, args []string) {
			file_name := args[0]

			path := filepath.Join("/Users/rodrigomoran/Workspace/gupi/template/", file_name)
			if _, err := os.Stat(path); err == nil {
				fmt.Print("file exists")
				date := time.Now()
				data := getDates(date)

				t, err := template.ParseFiles(path)
				if err != nil {
					fmt.Print("Failed to parse")
					os.Exit(1)
				}

				fileName := fmt.Sprintf("Week-%d.md", data.Week)
				f, err := os.Create(fileName)
				if err != nil {
					fmt.Print("Failed to create")
					os.Exit(1)
				}

				err = t.Execute(f, data)
				if err != nil {
					fmt.Print("Failed to execute")
					os.Exit(1)
				}
			}
			fmt.Print("What happened?")
		},
	}
	...
	return cmd
}
```

### Bonus: Makefile with LDFlags

```makefile
.PHONY: all clean

binary   = brief
version  = 0.1.1
build	   = $(shell git rev-parse HEAD)
ldflags  = -ldflags "-X 'github.com/phantompunk/brief/command.version=$(version)'
ldflags += -X 'github.com/phantompunk/brief/command.build=$(build)'"

all:
	go build -o $(binary) $(ldflags)

test:
	go test ./... -cover -coverprofile c.out
	go tool cover -html=c.out -o coverage.html

clean:
	rm -rf $(binary) c.out coverage.html
```





### Put it All Together

## Next Steps
- Extra our custom framework into seperate package that can be imported
- Enhance the custom framework to do some simple code generation or boiler plating for new commands
- Add a simple config management with a struct or include [Viper]()
- Enhance how storage mechanism for templates
- Add flags and options for importing templates from various sources (url,  git, etc)
## Conclusion

That's it, hope you found this useful hopefully at least interesting. You can find the full code in this [repository]().
## CTA

## Resources & extra reading
CLI Development Guild -> [CLIG:Guidelines](https://clig.dev/)
Go Modules -> https://go.dev/blog/using-go-modules
Go By Example ->
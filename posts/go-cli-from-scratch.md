```
---
authors:
- Rodrigo Moran
tags:
- Golang
- CLI
date: 	2021-12-01T13:50:040Z
title: "10 Tips and tricks for Cassandra"
image: 
---
```

# CLI From Scratch (No Framework Needed)

## Intro

As Engineers Command Line Tools are all around us with several as a part of our daily habits. There's OS specific CLI's, crossplatform ones, Enterprise CLI's etc. Do you ever wonder those are built? A few I've used on a daily basis in the past couple years have been:

* KubeCTL
* Docker
* Terraform
* JHipster
* AWS CLI
* Git

Out of the 6 I mentioned 3 happen to be written in Go. Go is great for building CLIs since it can easily be cross compiled into single binaries. The other thing I want to mention is most "Build from scratch" blogs include a popular 3rd party dependency or framework. We're building literally from scratch albeit this inspired by a CLI Framework. The one I have in mind is Cobra which is probably the most popular CLI Framework for Go. Most major Go CLIs use Cobra in some capacity and for good reason its well written, well tested, has a great community, and provides a simple abstraction. 

Its so good I want to understand how it works

## What makes the others good?

In my opinion what makes a CLI great is how intuitive it is to use. Does the api makes sense? How friendly is it when I envitably make a mistake? How helpful are the error messages? Are the commands and flags easy to understand and use? Those are just a few that come to mind first.

Above all it should work as intended/expected no more / no less. What I've come to expect after using Git, Docker, Kubectl, or AWS is the options for subcommands. Most classic even most new linux commands don't support... better yet they don't need subcommands (ls, grep, find, etc...). Newer CLIs support various functions so subcommands make sense but it becomes more important that they make sense. This one feature I want to support in our CLI. 



Sure CLIs are often used for automations but the main users are still developers, people might be thte ones struggling to find an issue or use the correct options. If there's too much confusion or poor documentation dev might run to next available option (head for the hills), thats why being friendly is so important. Another requirement is to support friendly prompts, messaging, and instructions. 

**Docker**

```shell
$ docker wait
"docker wait" requires at least 1 argument.
See 'docker wait --help'.

Usage:  docker wait CONTAINER [CONTAINER...]

Block until one or more containers stop, then print their exit codes
```



**Kubectl**

```shell
$ kubectl
kubectl controls the Kubernetes cluster manager.

 Find more information at: https://kubernetes.io/docs/reference/kubectl/overview/
Basic Commands (Beginner):
  create        Create a resource from a file or from stdin
  expose        Take a replication controller, service, deployment or pod and expose it as a new Kubernetes service
  run           Run a particular image on the cluster
  set           Set specific features on objects
```



## What are we building

What this CLI is doing doesn't matter as much as how were building it but for this purpose we'll be building a CLI that can store templates, edit them, list them, delete and create them. We will be focusing on using Go Std library and mainly focusing on the `flag` library as the core of our CLI toolset. 

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
todo-list			     2 weeks ago   482
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



### Prerequiesites

1. Make sure Go is installed

### Initialize a new project

First thing we need to do is create the project directory and initialize it as a Git repository. I'll call this project `gupi`:

```shell
mkdir gupi
cd gupi
git init
```

Go has its own way to manage dependencies by using a `go.mod` file, you can easily enable this on a new project by running:

```shell
go mod init github.com/USERNAME/gupi
```

The Github USERNAME portion is required if ever intend to package and distribute this module, it also helps with importing local subfolders. 

### Create a Main

Next lets create our entry point the `main.go`:

```
touch main.go
```

```go
package main

import "fmt"

func main() {
	fmt.Println("Working CLI!")
}
```

We test this app by running:

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

New users should be understand what the tool does and how to actually use. We already clearly defined the main features of our tool so putting together this usage text will be easy.

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

Now that we have a base usage message we update our main function print this to get a feel for the CLI.

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
# if using fish shell
$ echo $status
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
func NewVersionCommand() *Command {
	cmd := &Command{
		flags: flag.NewFlagSet("version", flag.ExitOnError),
		Execute: func(cmd *Command, args []string) {
			if short {
				fmt.Printf("brief version: v%s", version)
			} else {
				fmt.Printf("brief version: v%s, build: %s", version, build)
			}
			os.Exit(0)
		},
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
package command

import (
	"flag"
	"fmt"
	"os"
	"os/exec"
	"path/filepath"
)

var editUsage = `Edits an existing template.

Usage: brief edit TEMPLATE

Options:
`

func NewEditCommand() *Command {
	cmd := &Command{
		flags: flag.NewFlagSet("edit", flag.ExitOnError),
		Execute: func(cmd *Command, args []string) {
			if len(args) == 0 {
				errAndExit("Template name required")
			}
			file_name := args[0]
			file_path := filepath.Join("/Users/rodrigomoran/Workspace/gupi/template", file_name)

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

	cmd.flags.Usage = func() {
		fmt.Fprint(os.Stderr, editUsage)
	}

	return cmd
}

```

```shell
$ go run main.go edit test
brief: Template 'test' was edited⏎
```

### Add Delete Command

```go
package command

import (
	"flag"
	"fmt"
	"os"
	"path/filepath"
)

var deleteUsage = `Removes a specific templates from the saved directory.

Usage: brief delete TEMPLATE

Options:
`

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

	cmd.flags.Usage = func() {
		fmt.Fprintln(os.Stderr, deleteUsage)
	}

	return cmd
}

```



### Add Create Command

```go
package command

import (
	"flag"
	"fmt"
	"os"
	"path/filepath"
	"text/template"
	"time"
)

var createUsage = `Usage: brief create [options...]
Examples:
  # Generate a report for the week containing Feb 2, 2021
	brief create --date 02/17/2021

Options:
  --template	Path to custom template file for weekly report.
  --date	Date used to generate weekly report. Default is current date.
  --output 	Output directory for newly created report. Default is current directory.
`

var dest string

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

	cmd.flags.StringVar(&dest, "dest", ".", "destination")

	cmd.flags.Usage = func() {
		fmt.Fprintln(os.Stderr, createUsage)
	}

	return cmd
}

type weekYear struct {
	Week int
	Year int
	Mon  string
	Tue  string
	Wed  string
	Thu  string
	Fri  string
}

var days = map[int]int{
	0: -1,
	1: 0,
	2: 1,
	3: 2,
	4: 3,
	5: 4,
	6: -2,
}

func getDates(start time.Time) *weekYear {
	year, week := start.ISOWeek()

	firstDayOfWeek := start.AddDate(0, 0, -days[int(start.Weekday())])
	_, m, d := firstDayOfWeek.Date()
	monday := fmt.Sprintf("%d.%d", m, d)

	_, m, d = firstDayOfWeek.AddDate(0, 0, 1).Date()
	tuesday := fmt.Sprintf("%d.%d", m, d)

	_, m, d = firstDayOfWeek.AddDate(0, 0, 2).Date()
	wednesday := fmt.Sprintf("%d.%d", m, d)

	_, m, d = firstDayOfWeek.AddDate(0, 0, 3).Date()
	thursday := fmt.Sprintf("%d.%d", m, d)

	_, m, d = firstDayOfWeek.AddDate(0, 0, 4).Date()
	friday := fmt.Sprintf("%d.%d", m, d)

	return &weekYear{week, year, monday, tuesday, wednesday, thursday, friday}
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

## Conclusion

## CTA

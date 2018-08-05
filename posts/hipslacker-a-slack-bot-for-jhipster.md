---
authors:
- Theo Lebrun
tags:
- Slack
- JHipster
- Bot
- Python
date: 2018-08-05T00:00:00.000Z
title: "HipSlacker - A Slack bot for JHipster"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/08/hipslacker-logo.png
---

Slack is almost used everywhere nowadays, especially because of their bot integration called Slack App. During a hackathon day, my coworkers and I decided to start building a bot using Python that will generate a JHipster app. The workflow is pretty simple, you send a message to the bot with the JHipster's configuration you want to use. Then the bot uses [JHipster Online](https://start.jhipster.tech/) to generate your application and replies with the GitHub repository's link.

Here an example of a command: `@hipslacker generate a microservice with mongodb named my-awesome-app`, as you can see, the message is very simple and looks like a real sentence. The reason is because we wanted to make the usage of the bot very simple and avoid having to specify all the JHipster's configuration.

All the Python code from this blog post is on the [GitHub repository of HipSlacker](https://github.com/hipslacker/hipslacker). You can easily setup your own bot by following the instructions in the README.md file.

# Main method and loop

The main method is very simple, the bot connects to your workspace and parse messages with a given interval. When a message is mentioning the bot, the method `handle_command` is called and a HipSlacker object is created to process the command.

```python
def run():
    """
        Main loop, messages are read with a given interval
    """
    if slack_client.rtm_connect(auto_reconnect=True):
        logger.info("Bot connected")
        while True:
            parse_slack_output(slack_client.rtm_read())
            time.sleep(constants.READ_WEBSOCKET_DELAY)
    else:
        logger.error("Connection failed")

def parse_slack_output(slack_rtm_output):
    """
        Each messages are parsed
        The method 'handle_command' is called if a message is directed to the Bot
    """
    if slack_rtm_output and len(slack_rtm_output) > 0:
        for output in slack_rtm_output:
            if output and 'text' in output and constants.AT_BOT in output['text']:
                handle_command(output['text'], output['channel'], output['user'])

def handle_command(command, channel, user):
    """
        Handle the command sent to the bot and process it
    """
    logger.info(f"Processing command: {command}")
    hipslacker = HipSlacker(slack_client, command, channel, user)
    hipslacker.process_command()
```

# Command analysis and configuration generation

The constructor of the class `HipSlacker` splits the command into multiple ones and the configuration (payload) is initialized with a default one.

```python
def __init__(self, slack_client, command, channel, user):
    self.slack_client = slack_client
    # take the command after bot's name
    self.command = command.split(constants.AT_BOT)[1].strip().lower()
    self.channel = channel
    self.user = user

    # get logger
    self.logger = logging.getLogger("hipslacker")

    # split command to commands using spaces as delimiter
    self.commands = re.split("\s+", command)

    # init payload with default values
    self.payload = {
        "generator-jhipster": {
            "applicationType": "monolith",
            "baseName": "my-awesome-app",
            ...
            "clientFramework": "react",
            "jhiPrefix": "jhi"
        },
        "git-provider": "GitHub",
        "git-company": constants.JHIPSTER_ONLINE_USER,
        "repository-name": "my-awesome-app"
    }
    self.payload_generator = self.payload["generator-jhipster"]
```

Then the bot parses the command's content and updates the configuration. The command can contain the application type, the name, the database type and the port. If you're familiar with JHipster, those parameters have no secret for you!

For the application and database type, the bot simply looks for keywords and updates the configuration with correct values. For the port and the name, the bot uses the command after the keyword (ex: `named my-awesome-app` or `port 8080`).

```python
def generate_payload(self):
    for command in self.commands:
        # application type
        if(command in ["monolith", "microservice", "gateway", "uaa"]):
            self.set_application_type(command)

        # base name
        if(command == "named"):
            self.set_app_name()

        # sql database
        if(command in ["mysql", "mariadb", "postgresql", "oracle", "mssql"]):
            self.set_database("sql", "h2Disk", command)

        # nosql database
        if(command in ["mongodb", "cassandra"]):
            self.set_database(command, command, command)

        # port
        if(command == "port"):
            self.set_port()

    # repository name
    self.payload["repository-name"] = self.payload_generator["baseName"]

    self.logger.info("Payload: %s", json.dumps(self.payload, indent=4))

def set_application_type(self, value):
    self.payload_generator["applicationType"] = value

def set_app_name(self):
    index = self.commands.index("named") + 1
    if index < len(self.commands):
        self.payload_generator["baseName"] = self.commands[index]

def set_database(self, db_type, dev_type, prod_type):
    self.payload_generator["databaseType"] = db_type
    self.payload_generator["devDatabaseType"] = dev_type
    self.payload_generator["prodDatabaseType"] = prod_type

def set_port(self):
    index = self.commands.index("port") + 1
    if index < len(self.commands):
        self.payload_generator["serverPort"] = int(self.commands[index])
```

# Application generation with JHipster Online

To generate your JHipster application with JHipster Online, the bot needs to retrieve a JSON Web Token (JWT) and then make a POST request to `https://start.jhipster.tech/api/generate-application` with a payload (which is the JHipster configuration). The library `requests` is used to make HTTP calls and the implementation is pretty simple.

```python
def get_token(self):
    """
        Get a JWT using credentials
    """
    data = {"password": constants.JHIPSTER_ONLINE_PWD, "username": constants.JHIPSTER_ONLINE_USER}
    r = requests.post("https://start.jhipster.tech/api/authenticate", json=data)
    if r.status_code != 200:
        self.log_http("Error while getting the token", r)
        return None
    else:
        return r.json()["id_token"]

def generate_application(self):
    self.generate_payload()

    # get token
    token = self.get_token()
    if token:
        # start generation
        headers = {"Authorization": f"Bearer {token}"}
        r = requests.post("https://start.jhipster.tech/api/generate-application", data=json.dumps(self.payload), headers=headers)

        # post error if generation failed
        if r.status_code != 201:
            self.log_http("Generation error", r)
            self.post_fail_msg()
            return

        self.post_generation_status(r.text, token)
    else:
        self.post_fail_msg()
```

The response from the `generate-application` endpoint is an id and we can use this id to get the status of the generation. That way the bot will post the status of the generation on your Slack channel to keep you updated. Then the bot post the link of the GitHub repository if the generation succeeded or an error if the generation failed.

```python
def post_generation_status(self, app_id, token):
    """
        Get status of generation every 500ms during 1min
    """
    timeout = time.time() + 60
    while True:
        # get status of generation
        headers = {"Authorization": f"Bearer {token}"}
        r = requests.get(f"https://start.jhipster.tech/api/generate-application/{app_id}", headers=headers)

        # post error if getting status failed
        if r.status_code != 200:
            self.log_http("Unable to get generation's status", r)
            self.post_with_username("error while getting generation's status :boom:")
            return

        # post status
        self.post_msg(r.text)

        # post repository's link
        if "Generation finished" in r.text:
            self.logger.info("Generation finished")
            self.post_with_username("here the link of your application: https://github.com/hipslacker/" + self.payload["repository-name"])
            return

        # post error message
        if "Generation failed" in r.text:
            self.logger.info("Generation failed")
            self.post_fail_msg()
            return

        # break the loop after a specific timeout
        if time.time() > timeout:
            self.post_with_username("the generation timed out :boom:")
            return

        time.sleep(0.5)
```

# Improvements

Since [JHipster Online](https://github.com/jhipster/jhipster-online) is on GitHub, you can host your own version. That is pretty useful if you want to use a specific version of JHipster or use a private version control service instead of GitHub.

The main idea of the bot was to be user-friendly and not having to specify all the JHipster's configuration in one command. However multiple improvements can be done to the bot to extend its functionalities, here a small list:
- Add support for GitLab
- Extend the command parser to enable more JHipster's configuration (ex: translation, cache, authentication, etc)
- Make the bot more interactive with a question/answer pattern

# Conclusion

Creating a Slack bot using Python is pretty easy and by dedicating the application's generation to JHipster Online you will avoid a lot of extra work. The main challenge is how the bot parses commands and generates a JHipster's configuration from them. That all depends on how the bot generates the configuration and on how complex a command can be. A flow with questions/answers can be used to give the user more control on the JHipster's configuration. But in this case, why not simply use JHipster Online ?

Here the [GitHub repository for HipSlacker](https://github.com/hipslacker/hipslacker), feel free to directly contribute to it or maintain your own fork!
---
authors:
- Pooja Krishnan
tags:
- Data
- Streamlit
- Snowflake
- ChatGPT
date: 2023-07-13T13:33:00.000Z
title: "Using Streamlit and ChatGPT with Snowflake to Easily bring Data Insights to End Users"
image: 
---

# Using Streamlit and ChatGPT with Snowflake to Easily bring Data Insights to End Users

We're all seeing the boom in Generative AI over the past few weeks and months as the entire industry re-evaluates what potential use-cases for Generative AI are. So if you're a data engineer (like me), you might be wondering ***how AI can change how stakeholders and business decision makers interact with the data you curate and provide for them***. In this blog, I'm going to layout one potential approach for integrating Generative AI with your dashboards and democratizing data for less technical stakeholders.

The core of the application that we're building is on [Streamlit](https://docs.streamlit.io/). Streamlit is an open-source Python library which make it easy to share web applications in minutes.

## Prerequisites
I started by installing Streamlit locally. As per the [Installation instructions](https://docs.streamlit.io/library/get-started/installation#install-streamlit-on-macoslinux) for MacOS/Linux, I created a python virtual environment, pip installed streamlit and tested that the installation worked by running `streamlit hello`.

![Streamlit Hello Application](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2023/07/streamlit-gpt-blog--streamlit-hello-app.png)

A few other things you'll need for this demo are a Snowflake environment and ChatGPT credentials.


## Creating the Skeleton of Our Streamlit App
As we can see, it's relatively easy to create our first streamlit application. But I wanted to explore how to display and interact with data in an application. So I created a python file and imported pandas and streamlit and ran my application using `streamlit run my_app.py`. Below is the code I ran:

```python
import streamlit as st
import pandas as pd
df = pd.DataFrame({
  'first column': [1, 2, 3, 4],
  'second column': [10, 20, 30, 40]
})

df
```

Lo and behold, I saw the tabular output of my dataframe on my screen. This is because anytime Streamlit sees a variable or literal on a line by itself, it automatically writes the value to the app using the streamlit function `st.write()`.

Now that we've done a bit of exploration into Streamlit, we can start to formulate the solution to the scenario we're trying to solve.

**My Scenario:** If I'm a non-technical business user intending to make data driven decisions, how can I interact with an application using natural language and get the insights I'm looking for?

### Step One: Getting and capturing user inputs
Streamlit has a `text_input` widget which you can use to capture the values that a user inputs into your application.

```python
import streamlit as st

title = st.text_input('Movie title', 'Life of Brian')
st.write('The current movie title is', title)
```

This did exactly what I needed to. Now a user can put in whatever natural language text they want and my application can record and use my user input. This leads to the next part of our Scenario. 

### Step Two: The Data

We're building a data driven application and have no data to interact with - which seems like an oversight, I know. This is where Snowflake comes in. Snowflake is a single-platform, fully managed solution for integrating and analyzing your data. We're going to use Snowflake to hold the datasets we are going to use for analysis.

Out dataset of choice is Snowflake's Tasty Bytes data. Tasty Bytes is a Snowflake curated sample set of data depicting a global food truck network with localized menu options in 15 countries, 30 major cities and over 15 core brands. I setup Tasty Bytes in my Snowflake Account by following [this tutorial](https://quickstarts.snowflake.com/guide/tasty_bytes_introduction/index.html#1).

Now that we've got our data ready to go, we can work on programmatically connecting to Snowflake and pulling the data into our Streamlit Application. To do so, we can import the Snowflake Python Connector and configure our connection parameters. 

```python
import snowflake.connector

con = snowflake.connector.connect(
  user=os.getenv("SNOWFLAKE_USER"),
  password=os.getenv("SNOWFLAKE_PASSWORD"),
  account=os.getenv("SNOWFLAKE_ACCOUNT"),
  role="SYSADMIN",
  warehouse="PK_DEV_WH",
  session_parameters={
      "QUERY_TAG": "pk_dev_streamlit_test",
  },
)

cursor = con.cursor()
cursor.execute("SELECT * FROM PK_STREAMLIT_DB.RAW_CUSTOMER.CUSTOMER_LOYALTY LIMIT 10")
df = cursor.fetch_pandas_all()

df
```

I have all of my connection parameters for Snowflake in the environment variables. You can then use the connection to create the cursor and use the cursor to execute a query. To start, I began by grabbing 10 rows from the CUSTOMER_LOYALTY table. As you can see in my sample query, I provide the fully qualified path for the Snowflake table using the format [database].[schema].[table_name].

At the end of this, when you refresh your application you should see the first 10 rows from the customer_loyalty table.

> I would suggest enabling Streamlit's **Always Rerun** option, as it allows your application to refresh whenever you save it!

### Step Three: Sending User Input to ChatGPT
As we saw in Step One, we can quite easily capture user input using Streamlit's built-in functions. But what if we want to submit our user input? We can do so using a form in streamlit.

```python
with st.form(key="my_form"):
    text_input = st.text_input(label="Enter Input")
    submit_button = st.form_submit_button(label="Submit")

if submit_button:
    st.write("Text Input: ")
    st.write(text_input)
```

As you can see here, we can put in input and easily capture the state of a button click as well.

Putting the entirety of Steps 1-3 together, we thus have:

```python
import streamlit as st
import snowflake.connector

with st.form(key="my_form"):
    text_input = st.text_input(label="Enter Question")
    submit_button = st.form_submit_button(label="Submit")

if submit_button:
    st.write("Text Input: ")
    st.write(text_input)

    con = snowflake.connector.connect(
        user=os.getenv("SNOWFLAKE_USER"),
        password=os.getenv("SNOWFLAKE_PASSWORD"),
        account=os.getenv("SNOWFLAKE_ACCOUNT"),
        role="SYSADMIN",
        warehouse="PK_DEV_WH",
        session_parameters={
            "QUERY_TAG": "pk_dev_streamlit_test",
        },
    )
```

How do we submit our captured input to ChatGPT? I started by signing up for an account with ChatGPT. For this entire exercise, you can use the $5 in credits ChatGPT gives you when you first sign up. I also added an API key to my user and added that key to my environment variables. This API Key is what we'll use to access the ChatGPT API. Finally, I pip installed and imported openai into my streamlit application.

> #### An Aside on the ChatGPT Chat Completion Endpoint
> The main mechanism we'll be using to interact with ChatGPT is the Chat Completion API Endpoint. As you can probably guess, the Chat Completion Endpoint returns a Model Response for the given conversation. Here is a sample call to the Chat Completion API in Python which I'll use to describe the key parameters we'll be using from here on out.
> 
> ```python
> # Example OpenAI Python library request
> MODEL = "gpt-3.5-turbo"
> response = openai.ChatCompletion.create(
>     model=MODEL,
>     messages=[
>         {"role": "system", "content": "You are a helpful assistant."},
>         {"role": "user", "content": "Knock knock."},
>         {"role": "assistant", "content": "Who's there?"},
>         {"role": "user", "content": "Orange."},
>     ],
>     temperature=0,
> )
> ```
> - Model: ID of the model to use. The standard models to use are `gpt-4` and `gpt-3.5-turbo`. As you see during this blog, I use `gpt-3.5-turbo` as the `gpt-4` model isn't available for free tier users quite yet.
> 
> - Messages: A list of messages comprising the conversation so far. Messages have a couple of required elements, namely the Role and the Content. The *role* captures the role of the message author and is `system`, `user`, `assistant`, or `function`. The *content* captures the contents of the message. This is required for all messages and may be null for assistant messages with function calls.
> 
> - Temperature: What sampling temperature to use, between 0 and 2. Higher values like 0.8 will make the output more random, while lower values like 0.2 will make it more focused and deterministic.
> 
> For additional parameters in the Chat Completion API check out the OpenAI docs [here](https://platform.openai.com/docs/api-reference/chat/create).

Ideally, we want to be able to generate an SQL query to answer the question a business user asks about the data. We're going to use ChatGPT to do so. For the question `How many customers signed up per day?` let's see what the default query chatGPT comes up with. I ran this in the [ChatGPT playground](https://platform.openai.com/playground).

![ChatGPT Initial Query Result](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2023/07/streamlit-gpt--chatgpt-initial-contextless-query-return.png)

As we can see from the query returned, ChatGPT has extrapolated column names and the table we'll be running the query against. It also includes an explanation as well as the question we asked it. So here are our first challenges with ChatGPT:

1. How do we provide context to ChatGPT with our user input so it has the columns and other information for the tables we have to query against in Snowflake?

2. How do we ask ChatGPT to only give us the query without any of the additional text?

Challenge Number 2 is pretty easy. When we ask ChatGPT our question, all we need to add at the end is: `Do not include the question in your response. Do not provide explanations.`

Challenge Number 1 is a little bit more difficult. The reason being, is how do we provide context to ChatGPT? Ideally, ChatGPT could use in addition to our database, schema, and table names, the table's columns by name, and their descriptions. If we're building this application to be able to look at all of the databases we use for reporting, it could lead to a large amount of information being provided to ChatGPT for context alone. Since this is a small scale demonstration, I chose to create a YAML file for my tables.

```yaml
table:
- name: customer_loyalty
  schema: raw_customer
  database: pkrishnan_streamlit_db
  description: Basic information about the customer
  columns:
    - name: customer_id
      description: Unique customer identifier.

    - name: first_name
      description: Customer's first name

    - name: last_name
      description: Customer's last name

    - name: city
      description: Customer's city of residence

    - name: country
      description: Customer's country of residence

    - name: postal_code
      description: Customer's residential postal code

    - name: preferred_language
      description: Customer's preferred language

    - name: gender
      description: Customer's gender

    - name: favourite_brand
      description: Customer's favourite brand

    - name: marital_status
      description: Customer's marital status

    - name: children_count
      description: Customer's number of children (if disclosed)

    - name: sign_up_date
      description: Customer's sign up date

    - name: birthday_date
      description: Customer's birthday

    - name: e_mail
      description: Customer's email

    - name: phone_number
      description: Customer's phone number
```

I've included only a single table here and put this file, called `table_config.yaml` in a directory called `gpt_sql/config`. Now to provide this context to ChatGPT, all you need to do is read the YAML file in Python and change your message to include the yaml file. We can go ahead and do this in Python after where we setup our Snowflake connection.

```python
with open("gpt_sql/config/table_config.yml", "r") as file:
    db_config = yaml.safe_load(file)

with st.form(key="my_form"):
    text_input = st.text_input(label="Enter Question")
    summarize_check = st.checkbox("Summarize results?")
    graph_check = st.checkbox("Graph results?")
    submit_button = st.form_submit_button(label="Submit")

if submit_button:
    st.write("Text Input: ")
    st.write(text_input)

    con = snowflake.connector.connect(
        user=os.getenv("SNOWFLAKE_USER"),
        password=os.getenv("SNOWFLAKE_PASSWORD"),
        account=os.getenv("SNOWFLAKE_ACCOUNT"),
        role="SYSADMIN",
        warehouse="PK_DEV_WH",
        session_parameters={
            "QUERY_TAG": "pk_dev_streamlit_test",
        },
    )
    ### How many customers signed up per day?
    openai.api_key = os.getenv("OPEN_API_KEY")
    generate_query_message = [
        {
            "role": "user",
            "content": f"Given this database config:{db_config}, give me the SQL query for this question: {text_input} with the table_name in database.schema.table_name format. Do not include the question in your response. Do not provide explanations.",
        }
    ]
    query_completion = openai.ChatCompletion.create(
        model="gpt-3.5-turbo", messages=generate_query_message, temperature=0.2
    )

    query = query_completion.choices[0].message.content
    st.write("Generated Query: ")
    st.code(body=query, language="SQL")
```

Note the additional change I've made to the message in this code snippet. Since we're using Snowflake and I like having the fully qualified name for Snowflake resources, I'm also asking ChatGPT to format the table_name in database.schema.table_name format. The function `st.code()` is a Streamlit function which formats outputs into Code Blocks for which you can specify a language.

> I also have included a temperature of 0.2 when I submit a chat completion to ChatGPT. This is from some experimentation. I noticed that I was getting different queries every time and even sometimes queries which did not return meaninful results. Reducing the temperature helped resolve that. I high encourage you to remove the temperature or experiment with it at this stage to see how the temperature value affects what you get back from ChatGPT.

We can now submit the query ChatGPT returns to Snowflake using `cursor.execute(query)` just as we did when we ran our query earlier.

```python
cursor = con.cursor()
cursor.execute(query)
df = cursor.fetch_pandas_all()

st.write("Tabular Data Output: ")
st.dataframe(df)
```

Now that we've examined the logic for returning an SQL query from ChatGPT and subsequently retrieving the data from Snowflake, we're about 75% of the way done with this exercise.

### Step Four: Summarizing Results
It'd be nice if a tabular view of our data was all our stakeholders ever wanted from their reports. Sadly, a tabular view is rarely enough. So let's get ChatGPT to do the hard work for us and summarize our data. To do so, I added a checkbox to our Streamlit Form.

```python
with st.form(key="my_form"):
  text_input = st.text_input(label="Enter Question")
  summarize_check = st.checkbox("Summarize results?")
  submit_button = st.form_submit_button(label="Submit")
```

I then added an if-statement after we displayed our tabular data.
```python
if summarize_check:
  time.sleep(30)

  summarize_message = [
      {
          "role": "user",
          "content": f"Given this data:{df}, give me a summary of the data. Do not include the question in your response.",
      }
  ]

  summary_completion = openai.ChatCompletion.create(
      model="gpt-3.5-turbo", messages=summarize_message, temperature=0.5
  )

  st.write(f"Result Summary: ")
  st.write(summary_completion.choices[0].message.content)
```

As context, I provided our returned dataframe from Snowflake, and asked ChatGPT to give me a summary of the data. Like we did to return the query, I also asked ChatGPT to not include the question in its response. We can test this out, now.

![Input Form with Summarize Check Box](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2023/07/streamlit-gpt--input-form-with-summarize.png)
![Outputted Results with Query and Summary](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2023/07/streamlit-gpt--outputted-results-with-summary.png)


### Step Five: Graphing our Results
The final thing a stakeholder could want from our application is a graph of the results. This was the most difficult part for me because all of the fancy plugins ChatGPT has to graph results are only available on the Paid Tier and I believe are not available via the API. So I had to get a little hack-y and creative to graph our results.

The first thing I did was have ChatGPT tell me which chart would be the best to graph our data.

```python
graph_message = [
    {
        "role": "user",
        "content": f"Given this data:{df}, give me one chart type to display the data. Do not include the question in your response. Give me only the chart type",
    }
]

graph_completion = openai.ChatCompletion.create(
    model="gpt-3.5-turbo", messages=graph_message, temperature=0.2
)

chart_type = graph_completion.choices[0].message.content
st.write(f"Graphing Data Result as a {chart_type}")
```

For our question, `How many customers signed up per day?` I noticed that ChatGPT returned either Line Chart or Bar Chart. So I chose to implement these two paths as if-blocks in our Python code. Additionally, since I'm using Streamlit's line_chart and bar_chart functions, it was important especially for the line_chart to additionally identify which columns belong on the X-Axis and which belong on the Y-Axis. This is what I came up with:

```python
if "line chart" in chart_type.lower():
  ### get x and y columns for the chart from ChatGPT
  xlm = [
      {
          "role": "user",
          "content": f"Given this data:{df}, give me the X Columns for a line chart. Do not include the question in your response. Give me only the X Axis Column Names.",
      }
  ]

  xlm_completion = openai.ChatCompletion.create(
      model="gpt-3.5-turbo", messages=xlm
  )

  result = xlm_completion.choices[0].message.content.strip()

  x_axis_col_list = []
  for col in list(df):
      if col in result:
          x_axis_col_list.append(col)

  x_axis = x_axis_col_list[0]

  ylm = [
      {
          "role": "user",
          "content": f"Given this data:{df}, give me the Y Columns for a line chart. Do not include the question in your response. Give me only the Y Axis Column Names.",
      }
  ]

  ylm_completion = openai.ChatCompletion.create(
      model="gpt-3.5-turbo", messages=ylm
  )

  result = ylm_completion.choices[0].message.content.strip()

  y_axis_col_list = []
  for col in list(df):
      if col in result:
          y_axis_col_list.append(col)

  st.line_chart(data=df, x=x_axis, y=y_axis_col_list)
```
and
```python
if "bar chart" in chart_type.lower():
  xlm = [
      {
          "role": "user",
          "content": f"Given this data:{df}, give me the X Columns for a bar chart. Do not include the question in your response. Give me only the X Axis Column Names.",
      }
  ]

  xlm_completion = openai.ChatCompletion.create(
      model="gpt-3.5-turbo", messages=xlm
  )

  result = xlm_completion.choices[0].message.content.strip()

  x_axis_col_list = []
  for col in list(df):
      if col in result:
          x_axis_col_list.append(col)

  x_axis = x_axis_col_list[0]

  ylm = [
      {
          "role": "user",
          "content": f"Given this data:{df}, give me the Y Columns for a bar chart. Do not include the question in your response. Give me only the Y Axis Column Names.",
      }
  ]

  ylm_completion = openai.ChatCompletion.create(
      model="gpt-3.5-turbo", messages=ylm
  )

  result = ylm_completion.choices[0].message.content.strip()

  y_axis_col_list = []
  for col in list(df):
      if col in result:
          y_axis_col_list.append(col)

  st.bar_chart(
      data=df,
      x=x_axis,
      y=y_axis_col_list,
  )
```
This is a really quick-and-dirty way to leverage ChatGPT to graph your data. I'm sure there are better ways, but this approach showcases the power of ChatGPT even for rudimentary data analysis.


## Our Final Code
Here is the final code file for the streamlit application that we built today. 
```python
import streamlit as st
import snowflake.connector
import openai
import yaml
import time
import os

with open("gpt_sql/config/table_config.yml", "r") as file:
    db_config = yaml.safe_load(file)

with st.form(key="my_form"):
    text_input = st.text_input(label="Enter Question")
    summarize_check = st.checkbox("Summarize results?")
    graph_check = st.checkbox("Graph results?")
    submit_button = st.form_submit_button(label="Submit")

if submit_button:
    st.write("Text Input: ")
    st.write(text_input)

    con = snowflake.connector.connect(
        user=os.getenv("SNOWFLAKE_USER"),
        password=os.getenv("SNOWFLAKE_PASSWORD"),
        account=os.getenv("SNOWFLAKE_ACCOUNT"),
        role=os.getenv("SNOWFLAKE_ROLE"),
        warehouse=os.getenv("SNOWFLAKE_WAREHOUSE"),
        session_parameters={
            "QUERY_TAG": f"{os.getenv("SNOWFLAKE_QUERY_TAG")}",
        },
    )
    ### How many customers signed up per day?
    openai.api_key = os.getenv("OPEN_API_KEY")
    generate_query_message = [
        {
            "role": "user",
            "content": f"Given this database config:{db_config}, give me the SQL query for this question: {text_input} with the table_name in database.schema.table_name format. Do not include the question in your response. Do not provide explanations.",
        }
    ]
    query_completion = openai.ChatCompletion.create(
        model="gpt-3.5-turbo", messages=generate_query_message, temperature=0.2
    )

    query = query_completion.choices[0].message.content
    st.write("Generated Query: ")
    st.code(body=query, language="SQL")

    cursor = con.cursor()
    cursor.execute(query)
    df = cursor.fetch_pandas_all()

    st.write("Tabular Data Output: ")
    st.dataframe(df)

    if summarize_check:
        time.sleep(30)

        summarize_message = [
            {
                "role": "user",
                "content": f"Given this data:{df}, give me a summary of the data. Do not include the question in your response.",
            }
        ]

        summary_completion = openai.ChatCompletion.create(
            model="gpt-3.5-turbo", messages=summarize_message, temperature=0.5
        )

        st.write(f"Result Summary: ")
        st.write(summary_completion.choices[0].message.content)

    if graph_check:
        time.sleep(30)

        graph_message = [
            {
                "role": "user",
                "content": f"Given this data:{df}, give me one chart type to display the data. Do not include the question in your response. Give me only the chart type",
            }
        ]

        graph_completion = openai.ChatCompletion.create(
            model="gpt-3.5-turbo", messages=graph_message, temperature=0.2
        )

        chart_type = graph_completion.choices[0].message.content
        st.write(f"Graphing Data Result as a {chart_type}")

        if "line chart" in chart_type.lower():
            ### get x and y columns for the chart from CahtGPT
            time.sleep(5)
            xlm = [
                {
                    "role": "user",
                    "content": f"Given this data:{df}, give me the X Columns for a line chart. Do not include the question in your response. Give me only the X Axis Column Names.",
                }
            ]

            xlm_completion = openai.ChatCompletion.create(
                model="gpt-3.5-turbo", messages=xlm
            )

            result = xlm_completion.choices[0].message.content.strip()

            x_axis_col_list = []
            for col in list(df):
                if col in result:
                    x_axis_col_list.append(col)

            x_axis = x_axis_col_list[0]

            time.sleep(5)
            ylm = [
                {
                    "role": "user",
                    "content": f"Given this data:{df}, give me the Y Columns for a line chart. Do not include the question in your response. Give me only the Y Axis Column Names.",
                }
            ]

            ylm_completion = openai.ChatCompletion.create(
                model="gpt-3.5-turbo", messages=ylm
            )

            result = ylm_completion.choices[0].message.content.strip()

            y_axis_col_list = []
            for col in list(df):
                if col in result:
                    y_axis_col_list.append(col)

            st.line_chart(data=df, x=x_axis, y=y_axis_col_list)

        if "bar chart" in chart_type.lower():
            time.sleep(5)
            xlm = [
                {
                    "role": "user",
                    "content": f"Given this data:{df}, give me the X Columns for a bar chart. Do not include the question in your response. Give me only the X Axis Column Names.",
                }
            ]

            xlm_completion = openai.ChatCompletion.create(
                model="gpt-3.5-turbo", messages=xlm
            )

            result = xlm_completion.choices[0].message.content.strip()

            x_axis_col_list = []
            for col in list(df):
                if col in result:
                    x_axis_col_list.append(col)

            x_axis = x_axis_col_list[0]

            time.sleep(5)
            ylm = [
                {
                    "role": "user",
                    "content": f"Given this data:{df}, give me the Y Columns for a bar chart. Do not include the question in your response. Give me only the Y Axis Column Names.",
                }
            ]

            ylm_completion = openai.ChatCompletion.create(
                model="gpt-3.5-turbo", messages=ylm
            )

            result = ylm_completion.choices[0].message.content.strip()

            y_axis_col_list = []
            for col in list(df):
                if col in result:
                    y_axis_col_list.append(col)

            st.bar_chart(
                data=df,
                x=x_axis,
                y=y_axis_col_list,
            )

```

## Lessons Learned with ChatGPT
Finally, I wanted to share with you all some of the lessons I learned while working with ChatGPT to create this small application. 

### Phrasing Matters
The real estate phrase "Location, Location, Location" can be quite easily coverted to ChatGPT to be "Phrasing, Phrasing, Phrasing". Because of the model, ChatGPT often interprets the same phrase or question, in our case, to different results. I've seen two variations of a query returned as a result: 

```
SELECT COUNT(DISTINCT customer_id) AS num_customers, DATE(sign_up_date) AS signup_day
FROM pkrishnan_streamlit_db.raw_customer.customer_loyalty
GROUP BY signup_day;
``` 

```
SELECT COUNT(DISTINCT customer_id) AS num_customers FROM pkrishnan_streamlit_db.raw_customer.customer_loyalty
GROUP BY signup_day;
```

Obviously only one of these queries is ideal if we are to gather meaningful insights about the data displayed. 

### Model Result Temperature
This is where I had to experiment with setting different values for the temperature. ChatGPT actually provides a secondary method for narrowing the sampling range called `top_p`. With top_p, the model considers the results of the tokens with top_p probability mass. So 0.1 means only the tokens comprising the top 10% probability mass are considered. ChatGPT suggests using only top_p or temperature when working with models. It may be worth experimenting between the two as you're foraying into using the ChatGPT API more. 

### Code Generation vs. Graph Generation
Earlier, I alluded to having problems when generating the graph for our application. That is in part due to the lack of plugins and integrations with the ChatGPT API. I tried to use the ChatGPT API to generate the HTML Code for the graph itself. But no matter what phrasing I tried, I wasn't getting clean HTML code back. Additionally, even if you provide ChatGPT the entire dataset like I did, it still only returns back a sample HTML file complete with comments asking you to manually add in all of the X-points and Y-points. I'd be curious to see how the graph generation changes if you are on a paid ChatGPT subscription and using one of the plugins.

### Timeouts
As you all are sure to have noticed, I have periodic `time.sleep()` function calls interspersed throughout the final Streamlit application. This is because I'm using ChatGPT's free tier which is limited to 3 API calls per minute.

### Cost of ChatGPT
While building and running this application, I used about 20 cents of the \$5.00 credit I was given. ChatGPT prices per token. For the model I'm using, ChatGPT charged me \$0.0015 per 1K tokens for Inputs and \$0.002 per 1K tokens for Outputs. This cost is fairly cheap for a small scale application like the one we built. But it would be worth keeping an eye on for a large scale application with multiple calls to ChatGPT with a large context.

# Conclusion
Thanks so much for taking the time to follow along in this quick exercise. As I mentioned in the beginning, this is one potential approach for integrating Generative AI with dashboards to democratize your data for less technical stakeholders. As we've seen, ChatGPT is a powerful technology which can be used in a variety of ways. This application is just one of many ways it can be used in the data and analytics space. But we've also seen some of it's stumbling blocks and lessons learned while working with it. I hope you've learned something and had some fun experimenting with a technology that is brimming with potential.

----
For more information on how Ippon Technologies can help your organization utilize Snowflake or Streamlit for all of your data needs, contact sales@ipponusa.com.
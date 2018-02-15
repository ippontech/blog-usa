---
authors:
- John Zaccone
categories:
- api
date: 2017-01-03T20:51:00.000Z
title: "GraphQL for the Win"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2016/12/graphql-1.png
---

[GraphQL](http://graphql.org) is a powerful query language for APIs that gives the clients the power to ask for exactly what they need and nothing more. GraphQL makes it easier to evolve APIs without the need for introducing new versions. 

In GraphQL, every request is a POST request to a single endpoint: `/graphql`. The request consists of a mirror of the data object that contains the fields you want to see in the response.

For example, this request:
```language-json
{
  hero {
    name
  }
}
```
returns the data:
```language-json
{
  "data": {
    "hero": {
      "name": "R2-D2"
    }
  }
}
```

Allowing the client to specify which fields to see in the response makes it easier to solve for problems such as resource granularity.

### Resource Granularity

Suppose we had an insurance policy entity that contained a lot of nested data. A policy contains basic information such as an ID, policy holder, and a date range, but it also includes much more data such as a list of reasons for valid claims.

```language-json
{
  id: 12345,
  policyHolder: "John Zaccone",
  startDate: "2016-12-20"
  endDate: "2017-12-20",
  claimReasons: [
    {
       title: "reason 1",
       description: "the description for reason 1"
    },
    ...
  ]
}
```
If you are only building a policy summary page, you don't need or want all of the claim reasons in the response. 

One approach might be to build two endpoints:
```
/policies/{id}
/policies/{id}/details
```
Deciding appropriate **resource granularity** is a common problem when building RESTful APIs. I have personally stressed over this problem for many hours trying to build the best, most user-friendly APIs. But stress no more! GraphQL solves this problem simply by allowing the user to query for exactly the data they are looking for.

Simply build the request and list the fields that you want back in the response:

```language-json
{
  policy(id: "1") {
    id
    policyHolder
    startDate
    endDate
    # We can leave out claimReasons if we want.
    claimReasons {
       title
       description
    }
  }
}
```
In this request, the client can choose whether or not they want claimReasons to be in the response.

### Versioning
Versioning APIs is hard. It is so hard, that the best approach may be to **simply avoid it**. That's one advantage of using GraphQL. 

When there is limited control on the data being returned from an API endpoint, any change can break existing clients. With GraphQL, logic about what data to retrieve lives in the client, so it is much easier to evolve APIs without introducing a new version.

### Arguments and Other Features
GraphQL is littered with a bunch of really helpful features that makes the query language even more powerful. Learn about all of them in the [docs](http://graphql.org/learn/).

In REST, arguments can be sent through as query string parameters or as URL segments. Such as:
```
policies/5
policies?policyHolder="Zaccone"
```
 This is limited in the number of arguments that can be passed in. In GraphQL, every field in the data model can be given an argument. You can also use arguments to tell the server to perform data manipulation such as converting a price to the correct currency.

```language-json
{
  policy {
    id
    price(currency: "USD")
    policyHolder(name: "John Zaccone")
    startDate
    endDate
    claimReasons {
       title(title: "reason 1")
       description
    }
  }
}
```

These arguments are hardcoded into the query request, but you can use **variables** to create reusable queries with dynamic inputs.

For example:
```language-json
query PolicyQuery($policyHolder: String) {
  policy {
    id
    price(unit: "USD")
    policyHolder(name: $policyHolder)
    startDate
    endDate
    claimReasons {
       title(title: "reason 1")
       description
    }
  } 
}

{
  policyHolder: "Zaccone"
}
```
This allows us to pass in the policy holder name dynamically instead of performing string interpolation as we build each query.

Another cool feature is **directives**. We can use the `@include(if: Boolean)` directive to dynamically include or exclude the claimReasons in the response.

```language-json
query PolicyQuery($policyHolder: String, $withClaimReasons: Boolean!) {
  policy {
    id
    price(unit: "USD")
    policyHolder(name: $policyHolder)
    startDate
    endDate
    claimReasons @include(if: $withClaimReasons){
       title(title: "reason 1")
       description
    }
  }
}
```
Again, this feature allows us to avoid doing manual string interpolation to build dynamic queries ourselves.

### Mutating Data
To mutate data, use **mutation** methods. 
```language-json
mutation CreateInsurancePolicy($policy: Policy!) {
  createPolicy(policy: $policy) {
    id
    policyHolder
  }
}
```
The `id` and `policyHolder` will return for the newly created policy. It is best practice to only manipulate data explicitly using a mutation method.

 
### Summary
In summary, GraphQL is great when building APIs because it helps:

- Eliminate multiple URL requests
- Save time on API design by avoiding decisions such as resource granularity and data format
- Evolve APIs without managing multiple versions
- Keep clients clean by allowing them to retrieve the data they want

There are a number of GraphQL libraries for both client and server including  java, javascript, swift, node, python, scala and more.

This is just the tip of the iceberg for the capabilities of GraphQL, but hopefully it piqued your interest enough to look further into their [documentation](http://graphql.org/learn/). Personally, I am convinced because this tool solves a lot of the headaches I have faced in the past when designing APIs.

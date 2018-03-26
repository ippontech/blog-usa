---
authors:
- David Martin
categories:
- hateoas
- REST
- web api
date: 2014-05-19T10:48:48.000Z
title: "Hypermedia API"
image: 
---

Developing an API is something pretty usual today. There are many reasons behind this: the need to expose data for an internal use, the wish to build an ecosystem around the company’s data, the will to create new business opportunities using valuable data… In all the cases, the API is built to be used by many clients, especially if the company decides to monetize it.

Adding a modification can become a tricky operation if many clients are already using the API, especially if the design choices made for the API don’t integrate this aspect. We will have a look at an approach that simplifies API modifications without breaking the clients and the existing ecosystem.

Hypermedia, relations… these words are often used here and there. But their benefits for an API are not obvious. That’s why we are going to introduce the advantages of using these concepts.

**Reminder**

It’s rather easy to find a lot of resources on the internet describing the way to build a simple (dumb) API. But too often, these examples are clearly over-simplified: in most cases, examples provide a read/write API for a set of persisted resources through a web interface using the classical GET/PUT/POST/DELETE HTTP verbs (creating a simple CRUD interface). The entities are often (if not always) simply (de)serialized in (raw) JSON and sent/received over HTTP.

Let’s consider one of these simple examples: an order management API (for a fast food company for instance). An “order” will be a resource described by a set of properties such as a timestamp, a menu, a total price, a status, … This resource can be persisted somewhere, probably in a database.

In most of the tutorials, the proposed solution – in Java – to solve this particular case will be to add a few Jackson annotations to the JPA entity to bring JSON serialization capabilities. Then, a Spring @Controller or @RestController [1] can be used to create the API endpoints.

Using this, it’s rather simple to handle HTTP requests to manage the entities. These requests could look like:

- GET http://api.mydomain.org/orders
- GET http://api.mydomain.org/orders/1
- POST http://api.mydomain.org/orders
- …

The responses could be a single “order” resource representation or a collection of “order” objects.
 A collection could look like this:

```language-json
[
{
"uid": 1,
"price": 6.4,
"name": "Menu A",
"quantity": 1,
"created": "2014-03-26T11:55:06.142+0000"
},
{
"uid": 2,
"price": 14.6,
"name": "Menu B",
"quantity": 2,
"created": "2014-04-01T13:27:07.628+0000"
}
]

```

whereas a single order resource can be represented this way:

```language-json
 {
 "uid": 2,
 "price": 14.6,
 "name": "Menu B",
 "quantity": 2,
 "created": "2014-04-01T13:27:07.628+0000"
 }
```

NB: please note that the purpose of this article is NOT to provide a perfect model for an order 😉

**Now, in real life**

In real life situations, many problems can occur and will make the developer’s life harder.
 Maybe the number of orders will become so huge pagination will be necessary…
 An order will be created of course, but also modified, cancelled, paid, prepared, delivered… Its state will change, and the API should reflect/enable these possibilities.
 And an order is linked to a client, a payment. It describes a set of food items, each being a resource, …
 In order to support all these real life features, the first idea is to bring a detailed documentation to explain how the API handles them, and how the developers should consume the API. Such a documentation could be something like:

- <span style="text-decoration: underline">Orders list:</span>
*GET http://api.mydomain.org/orders*
 Orders are paginated, 20 per page, sorted by creation date. To navigate among the pages, use the following URL, with a positive integer value for the ‘page’ attribute:
*GET http://api.mydomain.org/orders{?page}*

- <span style="text-decoration: underline">Get a specific order detail:</span>
*GET http://api.mydomain.org/orders/{uid}*
 where ‘uid’ is the order’s unique ID, which is also named ‘uid’ in the order JSON representation.

- <span style="text-decoration: underline">Retrieve the client associated to an order:</span>
*GET http://api.mydomain.org/orders/{uid}/client*
 where ‘uid’ is the order’s unique ID

and so on.

A client developed to consume this API must be able to build all the URLs described in the documentation. If the developer is lucky enough, the URL may follow a standard pattern such as Uri Template [2], like in the example. To develop such a client, the developer will use the documentation of the API and for each feature, and will take into account how and when it can/should be used. For instance, canceling an order is only possible in a particular set of states, and forbidden for the others. The client must know these rules to enable or not the feature. In other words, the client duplicates the API logic.

It’s easy to understand how tightly coupled such a client is with the API. The client implements the same logic as the API service, use hardcoded URL fragments, is responsible for the full URL creation (which can be error prone)…

So the question that arises is: Is there a possibility to build a client which will not be so tightly coupled and will ease changes in the API without breaking all the ecosystem? Of course, there is…

… and it relies on **a well known notion: relations**. Resources representations can use relations to describe the links they have with others. These relations are forged by the API, and not by the client. The client just consumes them, if they are present.

Let’s imagine a new version of our example API. This version now returns this response to a request for a specific order:

```language-json
 GET http://api.mondomaine.org/orders/1
 {
 "data": {
 "uid": "2",
 "price": 14.6,
 "name": "Menu B",
 "quantity": 2,
 "created": "2014-04-01T13:27:07.628+0000"
 },
 "metadata": {
 "links": [
 {
 "href": "http://api.mydomain.org/orders/2",
 "rel": "self",
 "desc": "The current order representation"
 },
 {
 "href": "http://api.mydomain.org/orders/2/client",
 "rel": "client",
 "desc": "This order's client representation"
 },
 {
 "href": "http://api.mydomain.org/orders/2/payment",
 "rel": "payment",
 "desc": "The payment information, if any"
 }
 ]
 }
 }
```

This representation has more information. Let’s have a look at the data under ‘metadata.links’. They are links to related resources, each qualified with the ‘rel’ attribute, which brings an information for the client to understand what’s the purpose of the link. Using a set of relations in conjunction with the HTTP methods, it’s rather easy to determine the actions that can be done. And if an action is not allowed (for instance, using DELETE on an order that is not in the appropriate state), the API will respond accordingly with an error message.

The pros of such an API design are:

- The client and the API are less tightly coupled
- The client no longer needs to forge the URL following the fragile rule in the documentation
- The API can modify the way the resources are exposed through URL without breaking anything (except potential bookmarks, but even in this case, it’s always possible to return a ‘301 Moved Permanently’ response)
- If the API describes a flow in the representation, this flow can be modified if needed with no impact at all on the client side

and the cons:

- Representations are more verbose (but, once zipped, is this still a problem?)
- Develop such an API is trickier than a simple serialized POJO API
- There are only a few frameworks supporting the hypermedia constraint, and the support is not always complete

Let’s dive into two aspects related to this approach.

First, the format used to build the representation. Often, the first (wrong) idea is to build a custom format, such as the one in the example we just saw, and to describe it in the documentation. Sure, it works, but that’s not the smartest way to go. In fact, some people have already thought about this problem, and the best answer is to adopt an existing format, which supports the hypermedia relations. Several formats exist, among them: Hypertext Application Language (HAL) [3], Siren [4], Collection+JSON [5]… and also Atom [6], HTML…

The last one can look odd here, because people think about HTML as the way to build ‘visual’ representations, and not ‘technical’ representations (where JSON and XML reign as kings). Anyway, HTML has all the tools needed to fulfill the requirements mentioned above:

- Bring elements to structure the data
- Can build a collection of items
- Support one or more options to describe relations (think of the `<a>` element for instance)

Whatever is the choice among these formats, picking one of them will allow:

- The developer of the API - To bring to its clients a rather mature and uniform format
- To develop its API using an already existing library to generate the format. Most, if not all, of these formats have libraries in different languages.
- To share the same format across the API, or even across multiple APIs
- The developer of the client - To be able to develop a client which isn’t specific to the API or the current version of the API. He may also be able to use a library, to easily parse (and generate) the format.

Always remember this: if you make it easy for your clients, you are increasing your API adoption chances.

The other aspect is the name the relations will have (under a ‘*rel*’ attribute often). These names will be the keys for the client to identify what is the relation. Most of them can come from predefined lists, like the IANA (Internet Assigned Numbers Authority) relations names [7], microformats relations names [8], … The rule of thumb here is if the meaning of the relation is already present in such an official list, just don’t use something else.
 Sometimes, the meaning is just specific and doesn’t exist in any standard lists. In such cases, the name can come from your imagination, if it is described in the documentation. A good practice is to use an URL as the relation name, which points to a description of its meaning. For instance:

`<link rel="http://api.mondomaine.org/rels/client" href="http://api.mondomaine.org/orders/2/client" />`

The link *http://api.mydomain.org/rels/client* used in the ‘*rel*‘ attribute can be used to get the documentation related to this relation (what the relation means, what is the representation used, …)

**Let’s wrap it up!**

I hope this article helped you understand how an API can be designed more efficiently providing a representation of the relations between resources. The loose coupling between the client and the API is the main benefit. Such an API will be able to evolve without breaking the compatibility with the existing clients. Beside this, by using the relations provided by the API, a client is no longer responsible for knowing the possible workflows or actions available: it will discover them.

Finally, this way to build API, based on the hypermedia possibilities to navigate and modify the state of the resources is also known as the unpronounceable acronym HATEOAS… which can be simply called the Hypermedia constraint.

Stay tuned for the next episode on… Ippon

[1] [http://docs.spring.io/spring-framework/docs/4.0.x/javadoc-api/org/springframework/web/bind/annotation/RestController.html](http://docs.spring.io/spring-framework/docs/4.0.x/javadoc-api/org/springframework/web/bind/annotation/RestController.html "Spring RestController")
 [2] [http://tools.ietf.org/html/rfc6570](http://tools.ietf.org/html/rfc6570 "RFC 6570")
 [3] [http://stateless.co/hal_specification.html](http://stateless.co/hal_specification.html "HAL Specification")
 [4] [https://github.com/kevinswiber/siren](https://github.com/kevinswiber/siren "SIREN Specification")
 [5] [http://amundsen.com/media-types/collection/format/](http://amundsen.com/media-types/collection/format/ "Collection+JSON Specification")
 [6] [http://tools.ietf.org/html/rfc5023](http://tools.ietf.org/html/rfc5023 "RFC 5023")
 [7] [https://www.iana.org/assignments/link-relations/link-relations.xhtml](https://www.iana.org/assignments/link-relations/link-relations.xhtml "IANA Link Relations")
 [8] [http://microformats.org/wiki/existing-rel-values](http://microformats.org/wiki/existing-rel-values "Microformat Relations")

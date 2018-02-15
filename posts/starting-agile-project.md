---
authors:
- Benjamin Scott
categories:
- Agile
date: 2015-09-22T11:46:30.000Z
title: "Starting an Agile project"
image: 
---

When implementing agile there is a lot of focus on reorganizing the teams into feature teams, implementing practices such as Continuous Integration and Continuous Delivery, incorporating automated unit tests and regression tests…

There isn’t much focus on the Product Owner, we assume that he will present User Stories that the team can execute upon, but where do these stories come from? When do they get created? What is the process that combines these User Stories into a cohesive product?

## The Product Vision

Every project has a purpose that needs to be shared with team, the stakeholders, and even the clients. The Product Vision needs to succinctly convey the target customers, the utility and what makes it unique. A beginner template for a vision has the form:

FOR <target customers>
 WHO <statement of need>
 THE <product name>
 IS A <product category>
 THAT <key benefit, reason to buy>
 UNLIKE <competition, alternative>
 OUR PRODUCT <differentiating statement>
 (From [Geoffrey Moore’s Crossing the Chasm](http://www.amazon.com/Crossing-Chasm-3rd-Edition-Disruptive/dp/0062292986))

Here’s an example:

FOR individual hackathon participants
 WHO need a team in order to compete
 THE Hatch
 IS A skill matching app
 THAT will spontaneously hatch teams based on goals and skills
 UNLIKE no alternatives
 OUR PRODUCT will empower individuals to compete at hackathons

Once you have created your template you can work at making it more fluid. A good vision statement should be tweetable:

> Hatch will allow individual hackathon participants to form a diverse team unified by a common goal and with the needed skillset so that they can compete effectively.

## Agile Roadmap

The next step in the quest for good User Stories is your Roadmap. The Roadmap is a high level set of features that will accomplish your Product Vision. In a new product, the goal is to identify the minimum set of features that creates a viable product,  known as MVP, here’s an example roadmap for the Hatch app.

<div class="wp-caption aligncenter" id="attachment_13406" style="width: 610px">[![An Agile Roadmap detailing the feature set for Hatch ](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/09/AgileRoadmap.jpg)](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2015/09/AgileRoadmap.jpg)An Agile Roadmap detailing the feature set for Hatch

</div>This roadmap identifies a key set of features required to be able to launch the product in the first quarter. Once released, we have access to user feedback from which we can adjust our roadmap.

To create this Roadmap you would have to meet with stakeholders and gather requirements, that you can then group into feature themes and finally identify the minimum set of features that will produce a MVP.

## Creating User Stories

Now that the Product Owner has a Vision and a Roadmap, he can create the Product backlog by focusing on Stories that can be mapped back to the Roadmap, prioritizing them based on which quarter they are being targeted for. The User Stories for the first quarter should be well groomed, while the User Stories for the later quarters can still remain in the form of an Epic.

The project is now ready for a development team to begin Sprint Planning.

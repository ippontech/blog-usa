---
authors:
  - Ryan Walker
tags:
  - Entando
  - Keycloak
date: 2023-01-10T12:21:50.000Z
title: "Understanding Entando Identity Management using Keycloak"
---

# What is Identity Management, and why do we need it?

Identity and access management is a framework of policies and technologies to ensure authorized users have the appropriate access to technology resources to perform their job functions. It includes resources and policies encompassing an organization-wide process to correctly identify, authenticate, and authorize entities through user access rights and restrictions. Those entities can be users, groups of users, or software applications. Hardware and software resources are securely protected from unauthorized access thanks to identity management solutions.

# Understanding Entando Identity Management

The Keycloak service powers all authentication and authorization for Entando. Entando applications rely on a Keycloak instance that is either externally installed or specific to an application.

## Authentication: Abstraction through Keycloak

Entando implements Keycloak as a central authentication point to provide a unified view of identity. Keycloak acts as an abstraction layer to the underlying Identity Provider (IDP), allowing Entando to integrate into other IDPs without modifying the source. This approach increases portability, which is beneficial for configuring all authentication for an Entando application in one place.

## Authorization: Entando Microservices and Role-based Assignments

Keycloak achieves microservice authorization through clients and roles stored in a JSON Web Token (JWT). The JWT is available upon service invocation. It's important to note that a complete microservice installation in Entando creates a corresponding client and set of roles per service. You can grant a user one or many roles for a specific client to control permissions when configuring a microservice.

![Keycloak Architecture](https://developer.entando.com/assets/img/keycloak-arch-high-level.47cccaab.png)

See [here](https://developer.entando.com/v7.1/docs/consume/identity-management.html#authorization) for Role Assignment steps.

# The Entando Core and Keycloak Plugin

Entando Core is the central repository for Entando based on a docker image. Authenticated users access the core via the Entando Keycloak Plugin, which enables SSO capabilities and has User Management for an Entando instance using Keycloak.

The Entando Core has a distinct model of roles and groups. Roles set users up with application permissions and access controls. Groups then apply these roles and attributes to certain collections of users. Entando Core's current model of roles and groups has yet to be compatible with Keycloak. Even with the same users across multiple Entando Instances, each set of roles and groups must be configured separately on each instance. More information can be found [here](https://github.com/entando/entando-keycloak-plugin) for Entando Keycloak Plugin, and [here](https://github.com/entando/entando-core) for Entando core.

# Where can we go now?

Let's get started with setting up Keycloak with Entando! Setting up an external Keycloak instance is the smoothest way to get started. To connect to an external Keycloak instance, first create a Keycloak instance! Click [here](https://www.keycloak.org/getting-started/getting-started-zip) to learn how to instantiate a Keycloak instance.

Next, follow these [steps](https://developer.entando.com/v7.1/tutorials/devops/external-id-management.html#purpose) to connect to your external Keycloak instance. Following these steps should result in a working Entando instance connected to an external Keycloak server!

# Summary

Understanding identity management can be tricky, but Keycloak simplifies the process of establishing efficient user management for organizations. Entando, being the leading open source application composition platform, has done well to use Keycloak for their identity management. The Entando platform has even went the extra mile to create a Keycloak plugin! For more information on how Ippon Technologies can help your organization utilize Entando for all of your application development needs, contact [sales@ipponusa.com](mailto:sales@ipponusa.com).

# Docs

### Entando Keycloak Plugin

https://github.com/entando/entando-keycloak-plugin

### Enable Keycloak on an Entando Instance

https://github.com/entando/entando-keycloak-plugin/wiki

### Entando Identity Management System

https://developer.entando.com/v7.1/docs/consume/identity-management.html#logging-into-your-keycloak-instance

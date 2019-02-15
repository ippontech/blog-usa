---
authors:
- Theo Lebrun
tags:
- Vue.js
- Spring Boot
- JHipster
date: 2019-02-14T12:12:12.000Z
title: "Improving the access-control of a JHipster application"
image: 
---

[Vue.js](https://vuejs.org/) is the new trending framework in the Javascript front-end world and its simplicity let you build web application pretty fast. The structure of a Vue.js application is very similar to Angular as it uses components and templates to build pages. [This video](https://www.vuemastery.com/courses/intro-to-vue-js/vue-instance) is a good introduction to start playing with Vue.js especially if you never used Angular or React before.

Here is a size comparison (minified) of the current popular frameworks ([more details here](https://gist.github.com/Restuta/cda69e50a853aa64912d)):

Name  | Size
------------- | -------------
Angular 2| 566K
Ember 2.2.0 | 435K
React 0.14.5 + React DOM | 133K
React 16.2.0 + React DOM | 97.5K
**Vue 2.4.2** | **58.8K**
Inferno 1.2.2 | 48K
Preact 7.2.0 | 16K

As I said before, the simplicity of Vue.js makes it very lightweight and easy to integrate with other libraries or existing projects. On the other hand, Vue.js is totally capable of building robust Single-Page Applications with the right tools and [JHipster](https://www.jhipster.tech/) is the perfect match for that. If you're not familiar with JHipster, I recommend watching [this video](https://www.youtube.com/watch?v=-VQ_SVkaXbs) that will give you a good introduction on what you can do with JHipster.

# Vue.js using a JHipster blueprint

By default, JHipster asks you to choose between Angular or React for the front-end framework. The concept of blueprint was introduced in the version 5 and the goal is to extend the functionality of JHipster by letting you use your own sub-generator. More information on how to create and use a blueprint can be found on [the documentation](https://www.jhipster.tech/modules/creating-a-blueprint/).

## Installation

The official Vue.js blueprint will be used to generate the front-end, you can follow the installation instruction on [the blueprint repository](https://github.com/jhipster/jhipster-vuejs). Since the blueprint was still in development when I wrote this blog post, you may encounter some issues. Feel free to report them and submit a Pull Request if you think that a part was done the wrong way.

## Application generation

Let's start by creating a fresh folder for the application:

```bash
mkdir vuejs-app
cd vuejs-app
npm link generator-jhipster-vuejs
jhipster -d --blueprint vuejs
```

The default answers can be selected for each questions and if the blueprint is correctly installed you should see this message:

![](https://raw.githubusercontent.com/Falydoor/blog-usa/vuejs-jhipster/images/2019/02/vuejs-jhipster.png)

Here is my [.yo-rc.json](https://raw.githubusercontent.com/Falydoor/blogpost-vuejs/master/.yo-rc.json) in case you want to generate the exact same application as me. The file can be put in the application directory and then running `jhipster -d --blueprint vuejs` will generate the application without asking any questions.

## Entities generation

Before starting the application, let's generate few entities using a simple jdl that contains three entities. Simply create a file named `entities.jdl` in the root folder of the application with [this content](https://raw.githubusercontent.com/Falydoor/blogpost-vuejs/master/entities.jdl). Then the command `jhipster import-jdl entities.jdl` will generate the entities using the Vue.js blueprint.

You can make sure that everything is working by running the command `./mvnw` to start the application and then visiting `http://localhost:8080/#/`. After login in, the entities menu should have the three entities like below:

![](https://raw.githubusercontent.com/Falydoor/blog-usa/vuejs-jhipster/images/2019/02/vuejs-jhipster-entities.png)

# Deep dive in the Vue.js structure

The folder containing the whole Vue.js application is `src/main/webapp/app` and the structure is very similar to Angular. I recommend having the command `npm start` running in your terminal to enjoy the benefits of live compiling/reloading when you do any code changes. Like Angular and React, webpack is used to compile TypeScript code into JavaScript and package the application.

## Configuration

### Core

The **Vue instance** is created in the file `src/main/webapp/app/main.ts` and I recommend looking at `src/main/webapp/app/shared/config/config.ts` as well.

Both files configure multiple things:
- i18n
- Router
- Vuex (used to store your application state)
- Service (custom services must be added here so they can be injected)

### i18n

The Vue.js application is already configured to work with any different languages and the folder containing the translations can be found at `src/main/webapp/i18n`. The method `$t()` can then be used to display text based on the user's language.

In a template:

```html
<h1 class="display-4" v-text="$t('home.title')">Welcome, Java Hipster!</h1>
```

In a component (name parameters can be used):

```javascript
// "A Operation is updated with identifier {{ param }}"
const message = this.$t('blogpostApp.operation.updated', { param: param.id });
```

### Routes

Like React, all the routes are centralized in one file: `src/main/webapp/app/router/index.ts` and JHipster will automatically generate the routes for your entities.
 
 Custom routes should be added after the comment `// jhipster-needle-add-entity-to-router - JHipster will add entities to the router here` so it does not break things when other entities are gonna be genreated.

The field `meta` is used for checking user's authorities (can be used to pass any variables as well):

```javascript
// This route will only allow users with the "ROLE_ADMIN" authority
{
  path: '/admin/user-management',
  name: 'JhiUser',
  component: JhiUserManagementComponent,
  meta: { authorities: ['ROLE_ADMIN'] }
}
```

## Entities pages

### Structure

The folder `src/main/webapp/app/shared/model` contains the models and like in Angular, there is a component associated with a `.vue` template for each "page" of the application.

![](https://raw.githubusercontent.com/Falydoor/blog-usa/vuejs-jhipster/images/2019/02/vuejs-jhipster-operation.png)

I recommend creating a custom service/component/template when creating a new page so you don't have to resolve conflicts when re-generating an entity.

### Validation

The validation is done using the [vuelidate library](https://github.com/monterail/vuelidate) and the usage is pretty straight forward. The entity **Operation** is a good example to understand how the custom validation is done with **vuelidate**. The `OperationUpdate` component contains the validations options, see below:

```javascript
const validations: any = {
  operation: {
    date: {
      required
    },
    description: {},
    amount: {
      required,
      numeric
    }
  }
};

@Component({
  validations
})
export default class OperationUpdate extends Vue {
    ...

}
```

Custom validation messages can be shown/hidden using the validation rules, here is how it is done in the template `operation-update.vue`:

```html
<input type="number" class="form-control" name="amount" id="operation-amount"
    :class="{'valid': !$v.operation.amount.$invalid, 'invalid': $v.operation.amount.$invalid }" v-model="$v.operation.amount.$model"  required/>
<div v-if="$v.operation.amount.$anyDirty && $v.operation.amount.$invalid">
    <small class="form-text text-danger" v-if="!$v.operation.amount.required" v-text="$t('entity.validation.required')">
        This field is required.
    </small>
    <small class="form-text text-danger" v-if="!$v.operation.amount.number" v-text="$t('entity.validation.number')">
        This field should be a number.
    </small>
</div>
```

The important object is **$v** and it can be used to get the status of any validated field.

# Conclusion

Vue.js has a smooth learning curve and the time required to be productive is very short. It is a very good alternative to Angular/React and having experience in those frameworks will definitely help. [The comparison with other frameworks](https://vuejs.org/v2/guide/comparison.html) page will help you decide if Vue.js is a good fit for your application.

In addition, JHipster will provide everything you need to start a complete modern Web app and take it to production in a very short amount of time. And on top of that, the back-end is built as a high-performance and robust Java stack with Spring Boot.
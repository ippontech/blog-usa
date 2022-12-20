---
authors:
- Deejay Easter
tags:
- Frontend
- Vue
- Styling
date: 2022-05-09T10:23:55.000Z
title: "Css Modules and Vue"
---

At first glance one might think that modules work similarly to scoped styles. However, there are a number of benefits when it comes to leveraging modules, especially when managing a larger component library. Frontend development has always had the problem of conflicting CSS class names, poorly structured cascades, and confusing CSS file structures. Vue components that utilize css modules are more modular and can enable you to compose CSS that, when compiled, will generate unique class names for the specific component that it was composed for, even if you have the exact same class names across components. They will be uniquely identified using a randomly generated string, and the component name which will be appended and prepended to the class name respectively.

# What Are Scoped Styles? 
Most Vue developers are probably familiar with scoped styles. In short, they allow your css to be scoped locally to the component file you are working within. It does this by adding a data attribute to the html element once compiled and appending that data attribute to the compiled css class.

* Before Compile:
```vue
<template>
    <div>
        <button class="btn btnPrimary" data-v-220f7e6a>Click Here</button>
    </div>
</template>
```
```vue
<style scoped>
    .btn {
        /* btn styles */
        color: black;
        padding: 0.75rem 1rem;
        border-radius: 15px;
        margin-bottom: 1rem;
    }

    .btnPrimary {
        /* btnPrimary styles */
        background: blue;
        font-weight: bold;
        color: white;
    }
</style>
```
* After Compile: 
```
<template>
    <div>
        <button class="btn btnPrimary" data-v-220f7e6a>Click Here</button>
    </div>
</template>
```
```
<style scoped>
    .btn[data-v-220f7e6a] {
        /* btn styles */
        color: black;
        padding: 0.75rem 1rem;
        border-radius: 15px;
        margin-bottom: 1rem;
    }

    .btnPrimary[data-v-220f7e6a] {
        /* btnPrimary styles */
        background: blue;
        font-weight: bold;
        color: white;
    }
</style>


One issue here is that some classes, such as btn, will be particularly common and the scoped styles can still be overwritten by classes that hold greater specificity. In other words, the scoped classes approach provides a shallow layer of protection when it comes to being overwritten by other components or global styles.

# A Closer Look at CSS Modules

In order to use css modules, we first have to add the module attribute to the style block inside your Vue component like this. 
```
<style module>
    .btn {
        /* btn styles */
    }

    .btnPrimary {
        /* btnPrimary styles */
    }
</style>
```
Once we have done that, we are going to set our classes up to be called by javascript.
To do this, we have to v-bind our classes and pass them in as an array of desired class names.
```
<template>
    <div>
        <button :class="[$style.btn, $style.btnPrimary]">Click Here</button>
    </div>
</template>
```
```
<style module>
    .btn {
        /* btn styles */
        color: black;
        padding: 0.75rem 1rem;
        border-radius: 15px;
        margin-bottom: 1rem;
    }

    .btnPrimary {
        /* btnPrimary styles */
        background: blue;
        font-weight: bold;
        color: white;
    }
</style>
```
The style object encapsulate all of the classes we have defined within our component. 
When Vue compiles a css module, we see a few key changes. Vue prepends the class with the component name that the style is related to. Additionally, it generates and appends the class with a unique identifier hash. 

After compiling it should look something like this assuming the name of the component is ExampleComponent.vue:

```
<template>
    <div>
        <button class="ExampleComponent_btn_46mPR ExampleComponent_btnPrimary_22mHQ">
            Click Here
        </button>
    </div>
</template>
```
```
<style module>
    .ExampleComponent_btn_46mPR {
        /* btn styles */
        color: black;
        padding: 0.75rem 1rem;
        border-radius: 15px;
        margin-bottom: 1rem;
    }

    .ExampleComponent_btnPrimary_22mHQ {
        /* btnPrimary styles */
        background: blue;
        font-weight: bold;
        color: white;
    }
</style>
```

What we end up with when inspecting the DOM is admittedly long classnames for elements. [Fig. 5] However, there is no way another style can overwrite this component’s styling. Additionally, should an issue arise, tracking down the issue within any component becomes easy as the name of the component file resides in the element’s class names. 

# A Refactor Example

Below is an example starting point: 
```
<template>
  <li class="tab-nav-item" :class="isActive ? 'is-active' : ''">
    <button class="tab-nav-item-button" @click="emitTabValue">
      {{ listName }} ({{ listLength }})
    </button>
  </li>
</template>
```
```
<script>
export default {
  props: {
    isActive: {
      type: Boolean,
      default: false
    },
    listName: {
      type: String,
      required: true
    },
    listLength: {
      type: Number,
      required: true
    }
  },
  setup(props, ctx) {
    const emitTabValue = () => {
      ctx.emit('emit-tab-value', {
        value: props.listName
      })
    }
    return {
      emitTabValue
    }
  }
}
</script>
```
```
<style scoped>
.tab-nav-item {
  padding-bottom: 2px;
}
.tab-nav-item:hover .tab-button {
  color: red;
}
.tab-nav-item.is-active {
  border-bottom: 3px solid black;
}
.tab-nav-item.is-active .tab-button {
  color: #2d2d2d;
}
.tab-nav-item-button {
  color: #6b6b6b;
  font-weight: bold;
  padding: 0;
  font-size: 1rem;
}
.tab-nav-item-button:hover {
  cursor: pointer;
}
</style>
```

In this example, we have a navigation bar that has active states for each of the nav-links. The issue here is that we can’t have duplicate instances of a v-bind attribute on a single element. There are a few ways we could handle this but I think refactoring the isActive prop to trigger the classes as a computed property is the cleanest.

First we need to update the css block to use module rather than scoped. We will also need to update our template syntax as in the previous example.

```
<template>
  <li :class="$style['tab-nav-item']" :class="isActive ? 'is-active' : ''">
    <button :class="$style['tab-nav-item-button']" @click="emitTabValue">
      {{ listName }} ({{ listLength }})
    </button>
  </li>
</template>
```
```
<style module>
    //component styles
</style>
```

The issue here is that we can’t have two different class bindings on a single element. There are multiple ways to handle this, but I am going to leverage the computed property to handle the class states. 

Assuming we are using Vue 3, we will need to import the computed and the useCssModule properties within our script tag.

```
<script>
import { computed, useCssModule} from 'vue';
    // Component Logic
</script>
```

The useCssModule property will allow us to create a variable and assign our $style object to the new variable so that our $style class names can be accessed from within our script logic. We can add this logic to the begining of out setup function.

```
<script>
//imports
//export default 

  setup(props, ctx) {
  
    // Create style variable and assign to useCssModule property
    const style = useCssModule()
  
    const emitTabValue = () => {
      ctx.emit('emit-tab-value', {
        value: props.listName
      })
    }
    return {
      emitTabValue
    }
  }

</script>
```

Now we can set up our computed property. If the isActive prop is true, we return the navLink and isActive classes in array syntax. If isActive is false, we only return the navLink class.

```
<script>
//imports
//export default 

  setup(props, ctx) {
  
    // Create style variable and assign to useCssModule property
    const style = useCssModule()
  
    const emitTabValue = () => {
      ctx.emit('emit-tab-value', {
        value: props.listName
      })
    }
    
    //Add computed logic
    const tabNavItemClassNames = computed(() => {
        if(props.isActive) {
            return [style.tabNavItem, style.isActive]
        }
        else {
            return style.tabNavItem
        }
    });
    
    return {
      emitTabValue
    }
  }

</script>
```

# Conclusion

The above was a simple example but being able to apply classes in such a way is a powerful asset in large applications. Just imagine that you have a platform that must support a multitude of brands, or perhaps you want multiple variations of the same component with toggle-able features. Using the CSS Module approach means classes can be assigned dynamically based on props more easily on top of being far more insulated from poorly architected styles.


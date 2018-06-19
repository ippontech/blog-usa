---
authors:
- John Strickler
categories:
- AngularJS
date: 2017-07-12T15:28:30.000Z
title: "Reactive Data Using Angular and RxJS"
id: 5a267e58dd54250018d6b650
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/07/Reactive-Data-Using-Angular---RxJS-Blog--2-.png
---

The web has become reactive.  Web applications are being packed with more API calls and are consuming more third-party data than ever before.  Frameworks and patterns are evolving to satisfy the increased complexity.  Data needs are shifting, and managing data in the global state just won’t cut it anymore.

Okay, so you’ve moved on long ago from storing data on the browser’s global window object.  That’s a good start, but we can do better.  Do your web components query for data changes, or do they ***react*** to data changes?  The difference is a pull vs ***push-based*** approach, traditional vs ***modern***, imperative vs ***reactive***… you get the picture.  The solution to controlling and scaling your front-end data is in utilizing reactive data stores.

### Problem meet Solution

In Angular, components consume data and services produce it.  Components should be decoupled.  They shouldn’t depend on each other, but they often depend on the same data.  For instance, if `Component A` and `Component B` need data from `Service C`, then each component must query the service.  There are two downsides to this approach.  First, if the service is providing remote data, then it will make duplicate API calls, one for each invocation.  Second, querying a service for data is a one-time request and components are unaware of future changes.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/07/service-c.png)

Reactive stores fill this void by acting as local push-based caches between the components and the services. In this scenario, `Component A` and `Component B` subscribe to `Store D` for data.  `Store D` then uses `Service C` to load data and the components are left blissfully unaware to how the data is actually fulfilled.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/07/store-d.png)

### Building a Store

Reactive stores are just a pattern, and Angular + RxJS are the tools.  A store is a plain class that is decorated as an Angular injectable and that wraps a RxJS Subject.  The subject itself is an implementation detail, which is exposed as an observable stream. For example, below is a self-loading store that exposes a data stream of customer information:

```js
@Injectable()
export class CustomerStore {

  // for lazy-loading
  initialized = false;

  // prefer a ReplaySubject with a buffer of 1
  private subject = new ReplaySubject(1);

  constructor(private customerStore: CustomerStore) {

  }

  // expose as an observable stream
  getCustomer(): Observable<Customer> {

    // asynchronously loads the data on the first subscription to the store
    if (!this.initialized) {
      this.initialized = true;
      this.customerService.retrieveCustomer().subscribe((customer) => {
        this.subject.next(customer);
      });
    }

    return this.subject.asObservable();
  }

}
```

The implementation of the above store has these benefits:

- **Push-based data stream.**  Components are always provided the latest data.
- **Lazy-loading.**  Data is loaded on first use.
- **No duplicate HTTP requests.**  The store takes control of making HTTP requests.

#### Managing Store State

Stores act as local data caches.  Their data contents have to be managed properly or risk becoming stale.  There are three [good] ways to manage the state of a store:

- Load *(ex. lazy-loading or manual)*
- Action *(ex. updates)*
- Scope

Stores wrap the service that components would otherwise be using to directly load data.  In the previous example, the store was loaded on its first invocation.  Since stores are just a pattern, you can implement your own way to load a store, either manually (explicitly call a method from your component) or automatically (lazily on first subscription).

Actions can (and should) manipulate the store state.  In fact, any action, such as an edit or deletion, should be handled through the store.  This way, the store handles the propagation of change back to its subscribers.

Stores can be scoped using Angular's [hierarchical dependency injection](https://angular.io/guide/hierarchical-dependency-injection).  Scope is important, and it varies by use case, but the idea is that a store is only relevant to a specific group of components for a specific period of time.  Stores don't necessarily need to persist globally throughout an entire application.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2017/07/scoped-store.png)

For example, a `CustomerStore` can be scoped to the `CustomerPageComponent` by marking it as a provider to that component.  A store scoped in this way also makes the `CustomerStore` available to all child components of `CustomerPageComponent`.

```
@Component({providers:[CustomerStore]})
class CustomerPageComponent {}
```

This ensures that when the `CustomerPageComponent` is destroyed then so is the `CustomerStore` along with its data.

#### Subscription Cleanup

The lifespan of a store is usually longer than the components that use them.  Components are constantly destroyed and created  during application use.  The subscriptions that these components create will continue to live on and cause a memory leak unless they are cleaned up.

Luckily, there is a simple convention for handling subscription cleanup.  We can make use of the component's `ngOnDestroy` lifecycle hook and RxJs's `takeWhile` operator.

```
@Component
export class CustomerPageComponent implements OnInit, OnDestroy {

  alive = true;
  customer: Customer;

  constructor(private customerStore: CustomerStore) {

  }

  ngOnInit() {
    // the observable will automatically remove this
    // subscription when `alive` is false
    this.customerStore.getCustomer()
      .takeWhile(this.alive)
      .subscribe((customer) => {
        this.customer = customer;
      });
  }

  ngOnDestroy() {
    this.alive = false;
  }

}
```

#### Wrapping up

Stores are just another tool in the toolbox.  They shine in large applications, but also have practical use cases in smaller ones too.  Hopefully you'll find a lot of benefit in using them in your next or current web application.  If you have any questions, please feel free to comment or drop me a line at [jstrickler@ipponusa.com](mailto:jstrickler@ipponusa.com).

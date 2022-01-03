---
authors:
- Cody Frenzel
tags:
- Angular
- Javascript
- NgRx
- State Management
- Software Craft
- Facade Pattern
date: 2022-01-03T00:00:00.000Z
title: Facade Pattern with NgRx
---

Over the last few years, I have seen many developers struggle with state management and understanding the [Redux Pattern](https://redux.js.org/understanding/thinking-in-redux/three-principles). By implementing the Facade Pattern, you can add a layer of abstraction over your state management and decouple the knowledge of stores from your component. In this blog, we will discuss the Facade Pattern with NgRx and the benefits of implementing them together.

## What is NgRx?
NgRx is a reactive state management framework for Angular applications that is based on redux. NgRx provides libraries for managing state, isolation of side effects, entity collection management, Angular router integration, and developer tooling.

### How NgRx Works
Visit the [NgRx](https://ngrx.io/) website for more resources about the framework. The state management lifecycle below shows how we select and update our read-only state from our store.
![NgRx Lifecycle](/images/2021/12/ngrx-state-management-lifecycle.png)

### Key Concepts
* `Actions` describe events that are emitted (dispatched) to change state.
* State changes are handled by `reducers`, which are pure functions that describe how our state is to be transformed.
* `Selectors` are pure functions that derive and compose a read-only piece of state.
* We select a piece of state from the `store` using `selectors`.

## Facade Pattern
Dive into what the Facade Pattern is and learn how to implement it with NgRx.

### What is a Facade?
In software, a facade provides a public interface to mask the usage of something more complex. In this instance, it will neatly wrap our NgRx interactions and allow one point of contact for our component and state management without our component having any knowledge of NgRx.

### Implementation
Before jumping in to the facade example, here is a component using NgRx without a facade.
``` Typescript

export class AccountListComponent implements OnInit {
  accountList: Observable<Account[]>;

  constructor(private store: Store<AccountListState>) {}

  ngOnInit() {
    this.accountList = this.store.select(getAccountList);

    this.loadAccountList();
  }

  loadAccountList() {
    this.store.dispatch(new LoadAccountList());
  }

  addAccount(account: string) {
    this.store.dispatch(new AddAccount(account));
  }

  editAccount(id: string, account: string) {
    this.store.dispatch(new EditAccount({ id, account }));
  }

  deleteAccount(id: string) {
    this.store.dispatch(new DeleteAccount(id));
  }
}

```

This implementation is very `store` dependent and makes the component more complex. By adding a facade for the component to interact with, we can simplify the access to our store and decouple the knowledge of NgRx in our component. By performing that separation of duties, if we choose to remove NgRx in the future we do not have to refactor our component.

Now let's create an `AccountService` that will act as our facade for NgRx.
``` Typescript

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  accountList = this.store.select(getAccountList);

  constructor(private store: Store<AccountListState>) {}

  loadAccountList() {
    this.store.dispatch(new LoadAccountList());
  }

  addAccount(account: string) {
    this.store.dispatch(new AddAccount(account));
  }

  editAccount(id: string, account: string) {
    this.store.dispatch(new EditAccount({ id, account }));
  }

  deleteAccount(id: string) {
    this.store.dispatch(new DeleteAccount(id));
  }
}

```

After moving everything related to the store into our service, we can then inject that service into our component.

``` Typescript

export class AccountListComponent implements OnInit {
  accountList: Observable<Account[]>;

  constructor(private accountService: AccountService) {}

  ngOnInit() {
    this.accountList = this.accountService.accountList;

    this.accountService.loadAccountList();
  }

  addAccount(account: string) {
    this.accountService.addAccount(account);
  }

  editAccount(id: string, account: string) {
    this.accountService.editAccount( id, account);
  }

  deleteAccount(id: string) {
    this.accountService.deleteAccount(id);
  }
}

```
After implementing the facade pattern what did we actually gain?

### Pros
- Better developer experience through abstraction.
    - You refactor your service or component without touching both.
    - Your component doesn't care what library you use for managing state.
- This service can be reused by other components that need to access our `accountList` instead of duplicating code.
- Your components are more simplistic.
    - Devs have a clearer focus on component functionality instead of NgRx stores, actions, and reducers.


### Cons
- Loss of indirection violates a redux pattern.
    -  Indirection decouples "what happened" from the logic that determines "how the state should be updated". This allows redux to track how the state changed every time an action gets dispatched. 
- At the cost of being easier to interact with, the facade pattern obfuscates how NgRx works. This may hinder the understanding of the state management lifecycle for other developers on the team.
- Actions could be reused instead of treating them as unique events.
   - If actions are reused, then changing one can impact multiple components across our application.
   - The concept of reusing actions goes against [good action hygiene](https://indepth.dev/posts/1407/force-good-action-hygiene-and-write-less-actions-in-ngrx-with-the-prepared-events-pattern#:~:text=Good%20Action%20Hygiene,-Link%20to%20this&text=Takeaway%20%2D%20Treat%20Actions%20as%20unique,by%20going%20through%20the%20reducers.)
   - [NgRx DevTools](https://blog.angular-university.io/angular-ngrx-devtools/) has an action log that may be less useful for debugging with generic actions.

### Conclusion
While this may violate some of the standards and recommendations of the redux pattern, I don't think the gains your team can have in development speed are outweighed. If you need to move fast there is a clear upside to using facades, but I would make sure the developers on the team also gain a solid understanding of NgRx. By understanding NgRx and the potential issues that using a facade may bring, you can write better code as a team and avoid those problems.



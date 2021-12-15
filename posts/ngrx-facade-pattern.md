# Facade Pattern with NgRx
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
Now we will dive into what is the Facade Pattern and how to implement it with NgRx.

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
- Better developer experience.
    - Devs only interact with a service instead of NgRx stores, actions, and reducers.
    - You can refactor your component without touching the store logic. 
    - You can change your state management implementation without touching your component.
- This service can be reused by other components that need to access our `accountList` instead of duplicating code.
- Scalability.

### Cons
- Loss of indirection violates a redux pattern.
    -  The indirection of separating descriptions of "what happened" from the logic that determines "how the state should be updated" allows redux to track how the state changed every time an action is dispatched. 
- At the cost of being easier to interact with, the facade pattern obfuscates how NgRx works. This may hinder the understanding of the state management lifecycle for other developers on the team.
- Actions could be reused instead of treating them as unique events.

### Conclusion
While this may violate some of the standards and recommendations of the redux pattern, I don't think the gains your team can have in development speed are outweighed. If you need to move fast there is an upside to using facades, but I would make sure the developers on the team have a solid understanding of NgRx. However, if you only want to use a facade to mask the complexity of NgRx; I would consider using something else for managing state instead of taking the upfront cost of implementing redux.



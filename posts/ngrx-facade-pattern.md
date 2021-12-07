# Facade Pattern with NgRx
Over the past few years I have seen many developers struggle to understand and implement the Redux Pattern. By implementing the Facade Pattern, you can add a layer of abstraction over your state management and decouple the knowledge of stores from your component. In this blog we will discuss the Facade Pattern in relation to NgRx and the benefits of implementing them together.

## What is NgRx?
NgRx is a reactive state management framework for Angular applications that is based on redux. NgRx provides libraries for managing state, isolation of side effects, entity collection management, Angular router integration, and developer tooling.

### How NgRx Works
Visit the [NgRx](https://ngrx.io/) website for more resources surrounding the framework.
![NgRx Lifecycle](/images/2021/12/ngrx-state-management-lifecycle.png)

### Key Concepts
* `Actions` describe unique events that are dispatched from components and services.
* State changes are handled by `reducers`, which are pure functions that compute a new state from the latest action and current state.
* `Selectors` are pure functions that derive and compose a piece of state
* We select a piece of state from the `store` using `selectors`.

## Facade Pattern
Now we will get more technical as we discuss what the is the Facade Pattern and how to actually implement it with NgRx.

### What is a Facade?
A facade is generally the front part of a building. In software terms that means a facade provides a public interface to mask the usage of something more complex. In this instance, it will neatly wrap our NgRx interactions and allow one point of contact for our component and state management.

### Implementation
Before jumping in to the facade example, here is NgRx implemented without a facade.
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

This implementation is very `Store` dependent and makes the component more complex. By adding a facade for the component to interact with we can simplify the access to our store. In addition to that, if we choose to remove NgRx in the future we do not have to refactor our component. This separation of duties adheres to the principles of a Hexagonal Architecture that many backend developers would be familiar with.

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

  constructor(private accountListFacade: AccountListFacade) {}

  ngOnInit() {
    this.accountList = this.accountListFacade.accountList;

    this.accountListFacade.loadAccountList();
  }

  addAccount(account: string) {
    this.accountListFacade.addAccount(account);
  }

  editAccount(id: string, account: string) {
    this.accountListFacade.editAccount({ id, account });
  }

  deleteAccount(id: string) {
    this.accountListFacade.deleteAccount(id);
  }
}

```
After implementing the facade pattern, our component no longer depends on NgRx, it is simpler to interact with, and you can easily change your state management implementation in the future.

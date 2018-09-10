---
authors:
- Randeep Walia
tags:
- Scala
- Functional programming
date: 2018-09-10T18:36:58.000Z
title: "Scala For the Arrogant: Putting It Into Practice"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2018/09/scala-arrogant-diehard.jpg
---

# Examining a code refactoring
In my first post on *[Scala for the Arrogant](https://blog.ippon.tech/scala-for-the-arrogant/)* (*SFA*), I talked about the complexities that an experienced object-oriented developer would face in learning some of the functional programming paradigms that come with Scala. The post was a syllabus of sorts that an experienced developer could take to start learning the principles, syntax, and mindset for being a great Scala dev. Admittedly, laying out a list of resources and concepts without any code made for a rather underwhelming post, so I will follow up on that concept by looking at an algorithm developed in a manner consistent with traditional Java development principles. We'll then apply some of the concepts outlined in *SFA* to see what refactoring this algorithm into something more functional looks like.

# The Water Pouring Problem... With Bombs
*"On the fountain there should be two jugs. A 5 gallon and a 3 gallon. Fill one of the jugs with exactly 4 gallons of water and place it on the scale and the timer will stop. You must be precise. One ounce more or less will result in **detonation**. If you're still alive in 5 minutes we'll speak..."* - Simon Gruber

When John McClane faced off against his anonymous nemesis in the 3rd and final film in the Die Hard Trilogy (that's right, there were sadly no more movies made afterwards), he had to come up with an accurate answer to the challenge or face dire consequences. As the clock ticked down, he was probably thinking to himself, "I wonder if I can come up with a good efficient algorithm to solve this problem?" Known as the [Water Pouring Puzzle](https://en.wikipedia.org/wiki/Water_pouring_puzzle) we will try to solve this using older versions of Java. Essentially, what does an implementation look like incorporating classical Java patterns and without the use of lambdas or streams?

# Devising A Solution
First we consider the elements of our solution (before starting, you might want to try and devise this yourself as it's a fun problem to tackle and you most likely do not have an armed explosive sitting nearby if you make a mistake. [The starting commit is here](https://github.com/randeepbydesign/diehardJugSolver/commit/8fe691efaaa7f4135f81bf0b3b0765f279439148)). The assumptions are:

- A virtually unlimited supply of water
- Can use two or more jugs of known size
- The goal amount must be less than the capacity of the largest jug (a solution would be impossible otherwise)
- No equipment for measuring until the end, meaning we have to fill a jug up all the way or not at all (no filling up a jug halfway)

With these assumptions laid out, let's begin!

## Define our problem...
We start by thinking about the landscape of our problem and what associated objects we can create to implement our solution. The first thing that comes to mind are the **jugs**. We could model an object with a capacity and an amount:

```java
public class Jug {
    final Integer capacity;
    Integer currentAmount;
    //Define getters and setters
}
```
However, the capacity is fixed and the amount fluctuates. A better approach will be to define our Jugs as a list of Integers, and a separate list of Integers will specify how full they are at a given moment. We'll refer to that as the state (so if we start out with two empty jugs, we'd have a list [0, 0]). We then adjust the current amount based on the actions we take. Speaking of **Actions**, we should consider what movements we can take to solve the puzzle.

In this case, our game is simple enough that we can only take three actions:

1. Fill up a jug to its full capacity
2. Empty a jug so that its capacity is 0
3. Pour the contents of one jug into another

A simple enumeration defines these movements:

```java
public enum MoveType {
    EMPTY,
    FILL,
    POUR
}
```

The **move** itself is modeled as an `Interface` with a single method `doMove` that takes some state, a pointer to the jug index or indexes that are relevant to the action, and returns the result of taking the action on the jug(s). Because `EMPTY` and `FILL` are single-jug operations they only receive one index for processing while `POUR` requires two: the jug we are pouring from and the jug we are pouring to. We pass our JUG capacities to this method as well so that we know the appropriate specifics for our FILL and POUR operations:

`Integer[] doMove(final Integer[] JUGS, Integer[] state, Integer[] jugIndex);`

For example, the implementation of the `FILL` action:
```java
Integer[] doMove(final Integer[] JUGS, Integer[] state, Integer[] jugIndex) {
    Integer[] ret = Arrays.copyOf(state, state.length);
    ret[jugIndex[0]] = JUGS[jugIndex[0]];
    return ret;
}
```
That is, clone the current state and modify the resulting array at the jugIndex spot to represent the full jug capacity. We can attach the logic of the move to the enumerated object itself for convenience. We then need to pair up a move with a jug state so that we know the precise steps we can take to solve the puzzle:

```java
public class Action {
    private Integer[] jugIndex; //Jug(s) we operate on (1 or 2 length)
    private MoveType moveType;
    private Integer[] state; //State of game pre-move
}
```

Finally, we are ready to tie this all together into our solution, which we implement in our `Solver` class. This involves taking a game state and calculating every possible action:
```java
public List<Action> createActions(Integer[] state) {
    List<Action> pourStates = new ArrayList<>();
    for(int i=0; i<JUGS.length; i++) {
        pourStates.add(new Action(state, MoveType.EMPTY, i));
        pourStates.add(new Action(state, MoveType.FILL, i));
        for(int j=0; j<JUGS.length; j++) {
            pourStates.add(new Action(state, MoveType.POUR, i, j));
        }
    }
    return pourStates;
}
```
As you can see, computing the pours constitutes O(n<sup>2</sup>) as we define pouring from one jar into every other available jar.

## Execute a solution...
Now that we have setup the framework for our application we can work on the solution via a `Solver` class. The heart of our  engine will be in the form of a `solve` method that takes an `Action` and returns the list of actions constituting the Empty, Pour, and Fill actions that can be conducted next:

```java
List<Action> solve(Action action) {
    //TODO:
}
```

We could then call this method iteratively until we find a solution. That also means that the list of possible combinations will explode exponentially as we calculate 6 moves for each step, which would in turn generate 6 new moves for each of those 6 steps. 6 x 6 x 6 x .... it could get out of hand. However, most of these results will be redundant. For example, we are starting with two empty jugs. For that reason, the `Empty` actions and `Pour` actions will result in the same state and could be ignored. We can enhance this method then, by tracking the history of states and avoiding circular paths.

```java
List<Action> solve(Action action, Set<List<Integer>> stateHistory) {
    //Get the resulting state from applying the input action
    Integer[] state = renderAction(action);

    //Create actions for the empty, pour and fill moves
    List<Action> retState = createActions(state);

    //If new action results in a state not in the history, record it
    List<Action> retActions = new ArrayList<>();
    for (Action nextAction : retState) {
        Integer[] nextState = renderAction(nextAction);
        if (stateHistory.contains(Arrays.asList(nextState))) continue;
        stateHistory.add(Arrays.asList(nextState));
        retActions.add(nextAction);
    }
    return retActions;
}
```

We then write a wrapper method `findSolution` that calls this `solve` method inside of a while loop to solve the puzzle.
```java
public List<Action> findSolution() {
    //Initialize to the beginning state with empty jugs
    Set<List<Integer>> history = ...;
    List<List<Action>> trails = ...;

    while(!solutionExists(trails) && trails.size() > 0) {
        ListIterator<List<Action>> it = trails.listIterator();
        while(it.hasNext()) {
            List<Action> trail = it.next();
            Action endState= trail.get(trail.size()-1);
            List<Action> next = solve(endState, history);
            it.remove();

            if(next.size() > 0) {
                List<List<Action>> combined = new ArrayList<>();
                //Merge each action to the root trail
                combined.addAll(combineTrails(trail, next));
                for(List<Action> combinedTrail : combined) {
                    it.add(combinedTrail);
                }
            }
        }
    }

    for(List<Action> trail : trails) {
        if(solutionExistsInTrail(trail)) return trail;
    }
    return null;
}
```
With this we have a relatively efficent [breadth-first search algorithm](https://www.thecrazyprogrammer.com/2017/06/difference-between-bfs-and-dfs.html). We're using object-oriented principles and procedural design to accomplish this task. When we calculate the next states, we merge them into the actions of the current trail using the `combineTrails` method that we implement to create a new instance of an ArrayList.

## Evaluating Our Design
When we run this example [against a test](https://github.com/randeepbydesign/diehardJugSolver/blob/f8de9aabc43f65a3d5a7d6f55e8678d1334e4415/src/main/java/org/waterpouring/Solver.java#L158-L163) we get a correct answer. At this point we can take a critical look at the design of our application. We have a few ADTs that are both sum and products.

Our [MoveType](https://github.com/randeepbydesign/diehardJugSolver/blob/f8de9aabc43f65a3d5a7d6f55e8678d1334e4415/src/main/java/org/waterpouring/MoveType.java) is a classic sum Algebraic data type, combining the three actions of Emptying, Filling and Pouring into one enumeration. The **[Action](https://github.com/randeepbydesign/diehardJugSolver/blob/f8de9aabc43f65a3d5a7d6f55e8678d1334e4415/src/main/java/org/waterpouring/Action.java)** fits the definition of a Product type, as the number of instances *multiplies* with the permutations of its JugIndex, MoveType, and resulting state. We could make the definition more finite by representing the start state instead of the resulting state- something to consider in a more Functional design.

We have nested for loops, nested while loops etc. This is not considered great practice and makes the code pretty hard to read, evaluate, and maintain. We could refactor this a bit so that each nested loop is its own method, but that is really a bandage that does not address the root of the problem.

# Refactoring Our Solution To Make It Functional
Our next approach is to refactor this code to make it more functional. To do this we can consider how we structure our program, and also utilize some new language features made available to us with Java 8. I will assume you are already familiar with these features but, if not, [I highly recommend reading up on it](https://www.oracle.com/technetwork/articles/java/ma14-java-se-8-streams-2177646.html). Let's start by analyzing the `createActions(...)` from before:

```java
public List<Action> createActions(Integer[] state) {
    ArrayList<Action> pourStates = new ArrayList<>();
    for(int i=0; i<JUGS.length; i++) {
        pourStates.add(Action.of(JUGS, state, MoveType.EMPTY, i));
        pourStates.add(Action.of(JUGS, state, MoveType.FILL, i));
        for(int j=0; j<JUGS.length; j++) {
            pourStates.add(Action.of(JUGS, state, MoveType.POUR, i, j));
        }
    }
    return pourStates;
}
```
The way this is structured has nested for loops. We categorize EMPTY and FILL move types as operations that affect a single jug, while pour requires two jugs. For that reason the first two can be processed once for each jug, while pouring requires a nested for loop.

Instead we can utilize a stream to do this processing. We could stream across all available jugs, but since we need to have knowledge of the jug's index we use an IntStream to iterate across the list.

```java
public List<Action> createActions(List<Action> actionTrail) {
    return IntStream.range(0, JUGS.length)
            .mapToObj(actionTrailToAvailableActions(actionTrail))
            .flatMap(Collection::stream).collect(Collectors.toList());
}
```
So we define a stream of Integers that we use as indices to our Jugs list. This implementation is much cleaner and more readable, however there's a new method, `actionTrailToAvailableActions`, that we will need to implement. `mapToObj(...)` takes a parameter of type `IntFunction`. For our IntFunction to work we need to also supply it with the relevant Action Trail. We will therefore use a pattern that is fairly common in the Java 8 Streams world, creating a method that returns the IntFunction and that can receive the Action Trail as a method parameter:

```java
public IntFunction<List<Action>> actionTrailToAvailableActions(final List<Action> actionTrail) {
    return (jugIndex) -> Arrays.stream(MoveType.values())
            .map(moveTypeToAction(jugIndex, last(actionTrail).map(Action::getEndState)))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
}

/**
 *
 * @param list
 * @param <T>
 * @return the last element in a list if it is not empty
 */
private <T> Optional<T> last(List<T> list) {
    return list.isEmpty() ?
            Optional.empty() : Optional.of(list.get(list.size()-1));
}

private Function<MoveType, List<Action>> moveTypeToAction(final Integer jugIndex, final Optional<ArrayList<Integer>> state) {
    return moveType -> !state.isPresent() ?
    Collections.emptyList() : moveType.requiresTwoJugs ?
            //Create n-1 actions representing the from jug defined at jugIndex to the other JUGS
            IntStream.range(0, JUGS.length)
                    //Only consider jugs that don't match the "from" jugIndex
                    .filter(toJugIndex -> toJugIndex != jugIndex)
                    //Transform the index to the appropriate action
                    .mapToObj(toJugIndex -> Action.of(JUGS, state.get(), moveType, jugIndex, toJugIndex))
                    .collect(Collectors.toList()) :
            //For single jug actions, just create the Action at the defined jugIndex as a singleton list
            Collections.singletonList(Action.of(JUGS, state.get(), moveType, jugIndex));
}
```
Let's see what we gain from this approach.

1. If we are only concerned with the `createActions` method we can get a pretty good idea of its functionality here. Create a stream of integers for each Jug we have defined, transform that jug index to a list of available **Actions** using the `actionTrailToAvailableActions` method. Because there are N jugs and this method returns a list for each jug, the result would be N different lists. The Empty and Fill operations would return a singleton list, while the Pour operations would return a list of N-1 elements. We don't want to operate on a list of lists so we use the `flatMap` function to consolidate these into a flat stream. Say we had a list of lists for different actions. We would go from:
```
[Pour(0, 1), Pour(0, 2)]
[Pour(1, 0), Pour(1, 2)]
[Empty(0)]
[Fill(1)]
```
to
```
[Empty(0), Fill(1), Pour(0, 1), Pour(0, 2), Pour(1, 0), Pour(1, 2)]
```
with the flatMap operation.

2. If we need more details about what is going on with the `actionTrailToAvailableActions` method we can go to that directly. We see that it makes use of `Optional<T>`. This is our first introduction to a **monad** in Java! In part 1 I mentioned how Monad's were critical to the art of Algebraic Data Typing in which we can maintain functional purity by considering the possibility of a value T rather than assuming it is there. In this case we're returning an Optional of a list which, if null or empty, could otherwise generate an `IndexOutOfBounds` or `NullPointerException`. The last method generates this monad. If the input list has data we return a populated monad with the last element, otherwise an empty one.

3. You can see that we then call the `map(...)` method of this monad to map it to its end state if it exists. A `map` is a special category of Function with Java 8 monads and streams. In the former case, when called from an optional, it will execute the input Function and wrap the resulting value in another Optional. If the input parameter Optional is empty, another empty optional is returned. In this way we can delay evaluation of the Optional and associated "isNull" type checks until the value is truly needed.

It is not until we arrive at the `moveTypeToAction` that we attempt to utilize this value, and at that time we make the explicit decision of what to do if the value is not present. In this case, we can just return an empty list.

4. One other thing worth mentioning is that this method returns a type of `Function` which is explicitly used by the Streams API's `map(...)` method to take some Object A and turn it into type B. In this case we take a type of move **MoveType** and transform it into the **Actions** that would be associated with it. By increasing the scope of this Function via the method's parameters, we can supply a jugIndex and the current state.

5. We have also taken the logic for checking whether the correct number of inputs is supplied for the type of action out of the enumeration logic. The `assert` statement is gone and, instead, we set a flag on the enumeration to define it as a twoJug operation or not. Now our `moveTypeToAction` method can transform a jug into a List of Actions in one place according to the specification of the move.

The `new` keyword does not appear anywhere and yet, we have generated a lot of new data via filters and transformation. Our functions are pure in the sense that we are not mutating data in them. Passing the same inputs into them will always yield the same output and, as a result, they are highly testable with simple unit tests. These functions compose well to create an integrated solution with fewer surprises.

Now let's look at the final form of `findSolution` which returns an Optional now, as no solution may exist:
```java
public Optional<List<Action>> findSolution() {
    Set<ArrayList<Integer>> history = Collections.singleton(startState);
    List<List<Action>> trails = Collections.singletonList(startActionTrail);

    while(!solutionExists(trails) && !trails.isEmpty()) {
        trails = trails.stream()
                //Map each trail to list of possible paths; flat map organizes them into a single stream
                .map(actionTrailToNextActionTrails(history)).flatMap(Collection::stream)
                .collect(Collectors.toList());
        history = Stream.concat(history.stream(), trails.stream()
                .map(this::last).filter(Optional::isPresent)
                .map(Optional::get)
                .map(Action::getEndState))
                .collect(Collectors.toSet());
    }
    return trails.stream().filter(this::solutionExistsInTrail).findAny();
}
```
While we don't modify any lists or sets as we did before, we do reassign the trails and history collections which violates pure functional principles. We've also gone from a while-loop-with-a-while-loop-with-a-for-loop to one while loop. We could even eliminate that with an additional recursive method so let's try that now:
```java
public Optional<List<Action>> findSolution(List<List<Action>> trails, Set<ArrayList<Integer>> history) {
    return (!solutionExists(trails) && !trails.isEmpty()) ?
        //Haven't found the solution yet, make recursive call with next states
        findSolution(trails.stream()
                .map(actionTrailToNextActionTrails(history)).flatMap(Collection::stream)
                .collect(Collectors.toList()),
            Stream.concat(history.stream(), trails.stream()
                .map(this::last).filter(Optional::isPresent)
                .map(Optional::get)
                .map(Action::getEndState))
                .collect(Collectors.toSet())) :
        //Found a solution or no solution available
        trails.stream().filter(SolutionExistsInTrail).findAny();
}

/**
    * For the given parameters, find a solution where the Jug capacities can produce
    * the goal amount.
    * @return A list of actions that yield the goal amount, or null if no solution exists
    */
public Optional<List<Action>> findSolution() {
    return findSolution(Collections.singletonList(startActionTrail), Collections.singleton(startState));
}

```
We use a stream transformation [Function](https://docs.oracle.com/javase/8/docs/api/java/util/function/Function.html) to take an Action Trail (defined as a list of actions) and turn it into the list of possible next actions. We then flatMap into a single stream for the same reasons described above. We use some similar architecture to modify our history. Rather than adding elements to the set we take our newly found trails, transform them into the end state of the last element, and add that state to the history if it does not yet exist.

Finally, the `solutionExistsInTrail` filter defines a [Predicate](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html) that gives a concise and clear way to identify a solution.

## Evaluating Our Design
[Review the commit](https://github.com/randeepbydesign/diehardJugSolver/commit/63d79218996856317a371700b8b757c89d4f1fd4) for a Java **Functional** solution and you will see:

- There is no modifying collections, and therefore no modification of state. If we need to create a Set with an additional element, for example, we create a new set with the original elements and the new element to be added.

- There are no for/while loops in this solution. Rather we have replaced them with Functional logic to filter, transform, and collect our streams of data. The final form of this solution is much easier to write Unit tests for as well as we have decomposed the logic into functions that take concise and discrete inputs and return specific consistent values.

- Our semicolon key gets a rest as the majority of our functions are one-liners. Our method names are descriptive enough that composing them together adds readability to our solution. Admittedly, it can be more difficult to debug, but I would argue that structuring your programs in this manner creates a final result that contains far fewer defects than traditional methods.

One critique you may have is that there is still quite a bit of verbosity to the code. For example, having a method that returns a Predicate can look unwieldy- especially if you are not familiar with that type of syntax. We also manage a parameterized List of Lists of Actions that we define as an Action Trail. We also manage our history as a Set of Lists of Integers.

# Refactoring Our Solution To Make It Scala
Finally, we can rewrite this solution to use Scala. We will still retain the structure that we devised earlier, but let's see if the Scala syntax helps us create something more elegant. The inspiration for this solution comes from Martin Odersky's *[Functional Program Design In Scala](https://www.coursera.org/learn/progfun2)*, with some modifications made for clarity and to illustrate some different concepts.

## One File
Scala has a lot of nice features for development like worksheets: an interactive mode where you can see the results of your coding in real-time. We can also define a lot of our landscape in a single file. We will take that approach here. It may not be best practice but it will allow us to keep it simple and hold our solution in one space.

## Define our own "Primitives"
In our Java solution we continuously referred to our state by a list of integers (with each integer representing the volume of water currently in a jug) and our jugs as a list of integers (with each integer representing the capacity of the jug). Passing around two lists of ints with different meanings could be confusing and cause error. One alternative is to define new types that shadow the actual data underneath:
```scala
object WaterPouringTypes {
  type Jug = Int
  type State = List[Int]
  type ActionTrail = List[List[Action]]
}
```
Now we can pass a `Jug` as a parameter, have functions explicitly request a jug instead of an int, still have our logic treat it as an Int. For example, we'll take the Move Interface from our Java solution and model it as a trait, using the `Jug` and `State` types we just defined:

```scala
trait Move {
    def doMove(state: State): State
}
case class Empty(jugIndex: Jug) extends Move {
    override def doMove(state: State): State = state.updated(jugIndex, 0)
}
case class Fill(jugIndex: Jug) extends Move {
    override def doMove(state: State): State = state.updated(jugIndex, JUGS(jugIndex))
}
case class Pour(fromJugIndex: Jug, toJugIndex: Jug) extends Move {
    override def doMove(state: State): State = {
        val pourAmt = math.min(state(fromJugIndex), JUGS(toJugIndex) - state(toJugIndex))
        state.updated(fromJugIndex, state(fromJugIndex) - pourAmt).updated (toJugIndex, state(toJugIndex) + pourAmt)
    }
}
```
You can see how concisely we are able to declare the trait and its implementations in the form of a case class. Using the `updated` method we can create a modified clone of state without modifying the input state. Scala's collections and datatypes are immutable by default (you can utilize their mutable counterparts in the `scala.collection.mutable` package) so this is handled for us elegantly.

It's easy to define classes as well. Let's create a class that encapsulates the list of Moves we have taken with a resulting endState:
```scala
class Path(val moves: List[Move], val endState: State)
```
This one-liner gives us the definition of our class, its parameters and anything we need to access its members. Because a **Path** will ultimately encapsulate our solution we should add a toString method to output this in a readable way:
```scala
class Path(val moves: List[Move], val endState: State) {
  override def toString: String = moves + " yielding " + endState
}
```

The last bit of setup to examine is how we would implement our `createActions` method. We can eliminate the ActionTrail concept as we have a more concise `Path` class and setup our new `createPaths` method:
```scala
val JUG_INDEX_RANGE = 0 to JUGS.length - 1 toList

val EMPTY_MOVES = for (jug <- JUG_INDEX_RANGE) yield Empty(jug)
val FILL_MOVES = for(jug <- JUG_INDEX_RANGE) yield Fill(jug)
val POUR_MOVES = for {
    fromJug <- JUG_INDEX_RANGE
    toJug <- JUG_INDEX_RANGE
    if fromJug != toJug
} yield Pour(fromJug, toJug)
val MOVES = EMPTY_MOVES ++ FILL_MOVES ++ POUR_MOVES

def createPaths(path: Path): Stream[(Move, State)] =
MOVES.toStream.map(m => (m, m.doMove(path.endState)))
```
This code makes use of one of scala's powerful features: **[for comprehensions](https://docs.scala-lang.org/tour/for-comprehensions.html)**, which provide an alternative way to process streams and handle flatMap, map, and filter operations, as well as collating the results into a collection (or simply preserving the stream for further processing). In the first line we define an Integer Iterator with the `to` keyword creating our **JUG_INDEX_RANGE**. We then use that in the for comprehension to say, in the example of the **Empty** action, "for each integer index over our jugs array, create an Empty move for the specific index."

Because pouring requires two jugs, it is a bit more complicated and we see our first **multi-line for comprehension** using curly braces: "For each integer index over our jugs array defining the jug we are *pouring from*, and for each integer index over our jugs array defining the jugs we are *pouring to*, ignore the cases where the fromJug matches the toJug (since we cannot pour a jug into itself), and yield the remaining Pour moves. What you can see here is that the `<-` operator defines a `map` or iterative operation, while the `if` statement inside of the for comprehension signals a `filter` operation. `yield` most directly corresponds to the collect operation we use in Java.

Finally, we take these three categories of moves and concatenate them into a single list using `++`. Scala provides a variety of operators for concatenating lists, elements to lists, and other operations that return a new immutable list. We don't need to write any helper methods or use any java libraries to do these types of operations as we had to do prior.

We also use parenthesis to return a [tuple](https://www.tutorialspoint.com/scala/scala_tuples.htm), which allows us to combine a Move and State together as our return type. With Java, we would have to couple those parameters together in a class or List.

One important thing to mention: we are returning a [Stream](https://www.scala-lang.org/api/2.12.3/scala/collection/immutable/Stream.html) instead of a Collection. Streams are lazily loaded, meaning we can delay execution of the doMove method and simply return a data structure that can do that evaluation at a later time or, barring some other circumstance, not at all. We'll see how to advantage of that later on.

At this point we have essentially defined the landscape of our water pouring problem in just a few lines of code. It's vastly simpler than our Java definition and, if it does not seem easy to understand now, with more familiarity regarding Scala's syntax it will be. All that is left is to compose our functions and data types to solve our problem:

```scala
1. def solve(paths: Stream[Path], history: Set[State]): Stream[Path] = paths match {
2.     case Stream.Empty => Stream()
3.     case head #:: tail => {
4.         val next = createPaths(head)
5.             .filter(t => !(history contains(t._2)))
6.             .map(t => new Path(head.moves :+ t._1, t._2))
7.         head #:: solve(tail #::: next, history | next.map(t=>t.endState).toSet)
8.     }
9. }
```
It's just 9 lines of code, but there is a lot to unpack here. Let's go through it line-by-line.

1. We create our method using `def` and have it take a `Stream` of **Path**s and a `Set` of **State**s. Our return type is explicitly defined here as a Stream of paths, but we could omit this as well and the compiler would infer based on the logic in the method. With no curly braces after our method definition we are commiting to solving this with one line or, in this case, a single `match` expression. The match expression is a Scala construct that mostly closely mirrors the Java `switch` statement but has many features.
2. `match` uses `case` statements as well. However, while Java case statements match by value, Scala allows you to specify descriptive matches. In this line, we look to see if our input Stream is empty. If it is, no further processing is needed and we return the empty stream. Because our `solve(..)` method is going to be called recursively we can consider this to be our terminating condition.
3. The next `case` statement looks to see if we have a stream with a value `head`. The `#::` operator that follows indicates that the head belongs to a stream (indicated by the '#' sign), and is concatenated by the rest of the stream (the '::' operator). We store the remainder of the stream in the `tail` value.
4. We call the `createPaths` method we setup before with the head of our stream. Intially this will be the beginning state where all of our jugs are empty but, as we recurse through the solution, it will also represent our subsequent states.
5. We filter our results from the `createPath` method that already exist in our history so that we don't follow any circular paths.
6. We map the result to a new Path. We want to take the move we just made and concatenate it to the end of the current moves array so we use the `:+` operator to do so. The _1 and _2 operators provide a shorthand for accessing the elements of the tuple and the constructor of the Path is implictly defined based on its class definition.
7. The final line of logic is complex. It uses stream concatenation to define the way we move across our problem landscape while adding new states to our history. We start by taking the Path **head** at the beginning of our stream and concatenating to the result of calling solve recursively. Upon calling `solve`, we pass in the tail which would cause the next element in the stream to be processed, followed by the stream of `next` moves. We concatenate the Stream of tail with the Stream of next using the `#:::` which takes two streams and joins them. Finally, we send an updated form of history by taking `next`, mapping the Path to its `endState` and creating a union of the current history with the new states via the `|` operator.

By setting up the call this way we are doing a **breadth-first search** as each element of the Stream is evaluated step-by-step as we continue to add new states to the end of the list. A **depth-first search** would probably be more efficient, but this version of the code illustrates a cool concept: wouldn't we keep recursing needlessly as we're not checking for a state that matches our goal? No!

As alluded to earlier, Streams are lazily evaluated. By building our stream recursively we are creating an infinite stream that can be processed as needed. So upon calling solve:
```scala
solve(...).find(p => p.endState.contains(GOAL))
```
we get back our resulting stream and *then* specify our end condition: `endState` of a particular Path matches the `GOAL`. Now our stream, which we constructed lazily can evaluate itself until the find predicate matches.

## Evaluating Our Design
The design here is much more compact and does a lot with a little. We use many of the concepts discussed in SFA with Algebraic Data Typing and Monads, resulting in Functional Purity for our methods. By using for comprehensions we can avoid the use of flat maps when we navigate our way through collections of collections or a stream of lists.

# Wrapping Up
In *Scala For the Arrogant* I presented you with a few key concepts a Java developer needs to learn to transition to Scala. In this post we examined a problem and 3 solutions that illustrate some of those concepts. Hopefully you can get a better sense of the power of Functional Programming and the flexibility that Scala offers in designing a solution.

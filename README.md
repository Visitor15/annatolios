# annatolios

A convenience library.

## Quickstart

### Data structures

#### Monad Transformer

Implementing the ```MonadT<A>``` interface adds ```map```, ```mapTo```, and ```flatMap``` default functionality.

```java
public interface MonadT<A> {

    A ref();

    default <B extends MonadT<A>> B flatMap(Function<A, B> block) {
        return block.apply(ref());
    }

    default <B, T extends MonadT<B>> T map(Function<A, T> block) {
        return block.apply(ref());
    }

    default <B> B mapTo(Function<A, B> block) {
        return block.apply(ref());
    }
}
```

Example

```java
public class SimpleStringMonad implements MonadT<String> {

    private final String ref;

    public SimpleStringMonad(final String ref) {
        this.ref = ref;
    }

    @Override
    public String ref() {
        return this.ref;
    }
}
```

A ```SimpleStringMonad``` allows us to wrap a ```String``` and map a function to its value.

```java
SimpleStringMonad simpleMonad   = new SimpleStringMonad("Test string");
Integer result                  = simpleMonad.mapTo(string -> 666);
Container<String>               = simpleMonad.map(string -> Container.apply(string));
```

#### Container

A ```Container<A>``` wraps any type ```A``` in a monad transformer. A static method ```public static final <A> Container<A> apply(A a)``` is available to construct a ```Container```.

Example

```java
Container<Integer> integerContainer = Container.apply(500);

```

#### MultiContainer  

A ```MultiContainer<A>``` is backed by a ```Container<List<A>>```. Using a ```MultiContainer<A>``` gives the ability to ```reduce```, ```fold```, and ```mapMulti```.

```mapMulti``` allows you to map a function to each element in the MultiContainer instead of the list of elements as a whole.

#### IOContainer

An ```IOContainer<A>``` implements a ```MonadT<Optional<A>>``` and requires an instance of a ```DataProvider<A>``` and ```AbstractContext``` to construct.

Once constructed, an ```IOContainer``` can resolve its reference to data by using its ```DataProvider```. Implementing a ```MonadT<Optional<A>>``` also gives us the ability to map on interesting data - for example, remote data requiring a http request, or persisted data requiring a DB query.

Example

```java
IOContainer<SimpleUser> c0 = IOContainer.apply(new SimpleDataProvider(), new AbstractContext(UUID.randomUUID().toString()));
Optional<String> optString = c0.mapTo(user -> user.map(u -> u.getId()));    // Extracting the user ID

assert(optString.isPresent())                           // Should be true
assert(optString.get().equals(c0.ref().get().getId())); // Should be true
```

```java
public final class SimpleUser {
    private final String id;

    public SimpleUser(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}


public final class SimpleDataProvider implements DataProvider<SimpleUser> {

    @Override
    public SimpleUser provide(AbstractContext c) {
        return retrieve(c).ref();
    }

    @Override
    public Exception buildErrorEntity(String errorMessage) {
        return new RuntimeException(errorMessage);
    }

    @Override
    public <A extends Exception> Exception buildErrorEntity(A exception) {
        return exception;
    }

    private Container<SimpleUser> retrieve(AbstractContext c) {
        return Container.apply(SimpleUserFixture.newInstance(c.getId()));
    }
}
```

#### Tuple

A ```Tuple<A, B>``` is analogous to a pair of type A and type B.

Example

```java
Tuple<Integer, String> tuple = Tuple.from(0, "abc");

Integer argA = tuple.getA();
String  argB = tuple.getB();
```

```Tuple``` is backed by ```MonadT<Tuple<A, B>>``` allowing you to ```map```, ```mapTo```, and ```flatMap``` on a tuple object.

#### Either

An ```Either<A, B>```, backed by a ```MonadT<Either<A, B>>```, is of either type ```A``` _or_ type ```B```; never both. In
addition to being a ```MonadT```, an ```Either``` can optionally map on either its left or right side returning an ```Optional<T>``` in both cases.

Example

```java
Either<String, Integer> either = Either.asLeft("abc");

Either.State state  = either.state();   // Should equal Either.State.LEFT
boolean isLeft      = either.isLeft();  // Should equal true
boolean isRight     = either.isRight(); // Should equal false

Optional<SimpleStringWrapper>   leftResult  = either.mapLeft(s -> new SimpleStringWrapper(s));  // Should be defined
Optional<String>                rightResult = either.mapRight(i -> i.toString());               // Should be empty

// Test class
public final class SimpleStringWrapper {
    private final String ref;
    
    public SimpleStringWrapper(final String s) {
        this.ref = s;
    }
    
    public String getRef() {
        return this.ref;
    }
}
```
### Behavior

#### Invocable

```Invocable<T extends AbstractContext, E extends Exception>``` is an interface allowing classes to invoke a function within a Try/Catch block with recovery hooks to handle exceptions. 

A class implementing Invocable must implement:

```java
public E buildErrorEntity(final String errorMessage);

public <A extends Exception> E buildErrorEntity(final A exception);
```

Provided methods:

```java
public <A, B> B invokeWithTryCatch(T c, A a, Function<E, B> errorFunc, BiFunction<T, A, B> func)

public <A> A invokeWithTryCatch(T c, Function<E, A> errorFunc, Function<T, A> func)

public <A> A invokeWithTryCatch(Function<E, A> errorFunc, Supplier<A> func)
```

Example: ```httpClient.read(...)``` can throw a ```ResourceAccessException```; we want to be able to handle this exception and return an empty list as a default value in the case an exception is thrown.

```java
List<Data> data     = new ArrayList<>();
List<Error> errors  = new ArrayList<>();
Tuple<List<Error>, List<Data>> result = MultiContainer.apply(SERVICE_1, SERVICE_2, SERVICE_3).fold(Tuple.from(errors, facts), ((acc, s) -> {
    List<Data> serviceResponse = invokeWithTryCatch(c, s, (e) -> {
        // Adding service error to tuple and returning an empty list
        acc.getA().add(handleResourceAccessException(e));
        return new ArrayList<Data>();
    }, (context, service) -> assembler.assemble(httpClient.read(context, service)));
    // Adding data from http response to tuple
    acc.getB().addAll(serviceResponse);
    return acc;
}));
```
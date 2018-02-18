# annatolios

A functionally convenient library.

## Quickstart

### Data structures

#### [Monad Transformer](https://github.com/Visitor15/annatolios/blob/master/src/main/java/com/voodootech/annatolios/structures/MonadT.java)


Implementing the ```MonadT<A>``` interface adds ```mapTo```, and ```flatMap``` default functionality. A ```mapInternal``` is available; letting concrete classes implement their own ```map``` function and return type.

```java
public interface MonadT<A> {

    A ref();

    default <B extends MonadT<A>> B flatMap(Function<A, B> block) {
        return block.apply(ref());
    }

    default <T extends MonadT> T mapInternal(Function<A, T> block) {
        return block.apply(ref());
    }

    default <B> B mapTo(Function<A, B> block) {
        return block.apply(ref());
    }
}
```

###### Example

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
Container<String>               = simpleMonad.mapInternal(string -> Container.apply(string));
```

#### [Container](https://github.com/Visitor15/annatolios/blob/master/src/main/java/com/voodootech/annatolios/structures/Container.java)

A ```Container<A>``` wraps any type ```A``` in a monad transformer. A static method ```public static final <A> Container<A> apply(A a)``` is available to construct a ```Container```.

###### Example

```java
Container<Integer> integerContainer = Container.apply(500);
Container<String> stringContainer   = integerContainer.map(i -> i.toString());
```

#### [MultiContainer](https://github.com/Visitor15/annatolios/blob/master/src/main/java/com/voodootech/annatolios/structures/MultiContainer.java)

A ```MultiContainer<A>``` is backed by a ```Container<List<A>>```. Using a ```MultiContainer<A>``` gives the ability to ```reduce```, ```fold```, and ```mapMulti```.

```mapMulti``` allows you to map a function to each element in the MultiContainer instead of the list of elements as a whole.

```java
MultiContainer<String> m0   = MultiContainer.apply("1", "2", "3", "4", "5");
MultiContainer<Integer> m1  = m0.mapMulti(s -> Integer.valueOf(s));

// Reducing multiple Strings to a single String
String strSum0 = m0.reduce("", ((acc, s) -> String.format("%s%s", acc, s)));    // strSum0 = "12345"

// Folding multiple Integers into a single String
String strSum1 = m1.fold("", ((acc, i) -> String.format("%s%d", acc, i)));      // strSum1 = "12345"
```

#### [IOContainer](https://github.com/Visitor15/annatolios/blob/master/src/main/java/com/voodootech/annatolios/structures/IOContainer.java)

An ```IOContainer<A>``` uses an instance of a ```DataProvider<A>``` and ```AbstractContext``` to resolve a reference to some entity ```A```. 

Once constructed, an ```IOContainer``` can resolve its reference to data by using its ```DataProvider```; giving us the ability to operate on interesting data - for example, remote data requiring a http request, or persisted data requiring a DB query.

###### Example

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

#### [Tuple](https://github.com/Visitor15/annatolios/blob/master/src/main/java/com/voodootech/annatolios/structures/Tuple.java)

A ```Tuple<A, B>``` is analogous to a pair of type A and type B.

###### Example

```java
Tuple<Integer, String> tuple = Tuple.from(0, "abc");

Integer argA = tuple.getA();
String  argB = tuple.getB();
```

```Tuple``` is backed by ```MonadT<Tuple<A, B>>``` allowing you to ```mapTo```, and ```flatMap``` on a tuple object. A specialized ```map2``` function is also available.

###### Example

```java
Tuple<Integer, Integer> tuple0  = Tuple.from(2, 2);
Tuple<Integer, String> tuple1   = tuple0.map2((a, b) -> Tuple.from(a + b, String.format("%d%d", a, b)));

Integer int0    = tuple0.getA();    // 2
Integer int1    = tuple0.getB();    // 2
Integer int2    = tuple1.getA();    // 4
String str0     = tuple1.getB();    // "22"
```

#### [Either](https://github.com/Visitor15/annatolios/blob/master/src/main/java/com/voodootech/annatolios/structures/Either.java)

An ```Either<A, B>```, backed by a ```MonadT<Either<A, B>>```, is of either type ```A``` _or_ type ```B```; never both. In
addition to being a ```MonadT```, an ```Either``` can optionally map on either its left or right side returning an ```Optional<T>``` in both cases. An Either is also right-biased with its ```map``` function.

###### Example

```java
Either<String, Integer> either0 = Either.asLeft("abc");
Either<String, String> either1  = either.map(i -> i.toString());

Either.State state0 = either0.state();      // Should equal Either.State.LEFT
Either.State.state1 = either1.state();      // Should equal Either.State.LEFT
boolean isLeft0     = either0.isLeft();     // Should equal true
boolean isLeft1     = either1.isLeft();     // Should equal true
boolean isRight0    = either0.isRight();    // Should equal false
boolean isRight1    = either1.isRight();    // Should equal false

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

#### [Invocable](https://github.com/Visitor15/annatolios/blob/master/src/main/java/com/voodootech/annatolios/invocation/Invocable.java)

```Invocable<CONTEXT extends AbstractContext, ERROR extends Exception>``` is an interface allowing classes to invoke a function within a Try/Catch block with recovery hooks to handle exceptions. 

A class implementing Invocable must implement:

```java
public ERROR buildErrorEntity(final String errorMessage);

public <A extends Exception> ERROR buildErrorEntity(final A exception);
```

Provided methods:

```java
public <A, B> B invokeWithTryCatch(CONTEXT c, A a, Function<ERROR, B> errorFunc, BiFunction<CONTEXT, A, B> func)

public <A> A invokeWithTryCatch(CONTEXT c, Function<ERROR, A> errorFunc, Function<CONTEXT, A> func)

public <A> A invokeWithTryCatch(Function<ERROR, A> errorFunc, Supplier<A> func)

public <A> Either<Exception, A> invoke(CONTEXT c, Function<CONTEXT, A> func)
```

###### Example

```httpClient.read(...)``` can throw a ```ResourceAccessException```; we want to be able to handle this exception and return an empty list as a default value in the case an exception is thrown.

```java
List<Data> data     = new ArrayList<>();
List<Error> errors  = new ArrayList<>();
Tuple<List<Error>, List<Data>> result = MultiContainer.apply(SERVICE_1, SERVICE_2, SERVICE_3).fold(Tuple.from(errors, facts), ((acc, service) -> {
    List<Data> serviceResponse = invokeWithTryCatch(context, service, (exception) -> {
        // Adding service error to tuple and returning an empty list
        acc.getA().add(handleResourceAccessException(exception));
        return new ArrayList<Data>();
    }, (context, service) -> assembler.assemble(httpClient.read(context, service)));
    // Adding data from http response to tuple
    acc.getB().addAll(serviceResponse);
    return acc;
}));
```
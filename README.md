# annatolios

A convenience library.

## Quickstart

#### Monad Transformer

Extending the ```MonadT<A>``` abstract class adds ```map``` and ```flatMap``` functionality.

```java
public abstract class MonadT<A> {

    public abstract A ref();

    public <B extends MonadT<A>> B flatMap(Function<A, B> block) {
        return block.apply(ref());
    }

    public <B> B map(Function<A, B> block) {
        return block.apply(ref());
    }
}
```

Example

```java
public class SimpleStringMonad extends MonadT<String> {

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

A ```SimpleStringMonad``` allows us to wrap a ```String``` in a monad and map a function to its value returning any type.

```java
SimpleStringMonad simpleMonad   = new SimpleStringMonad("Test string");
Integer result                  = simpleMonad.map((string) -> 666);
```

#### Container

A ```Container<A>``` is a built-in data structure that allows us to wrap any type ```A``` in a monad transformer. A static convenience method ```public static final <A> Container<A> apply(A a)``` is available to construct a ```Container```.

Example

```java
Container<Integer> integerContainer = Container.apply(500);

```  


package com.voodootech.annatolios.structures;

public class Container<A> implements MonadT<A> {

    private final A ref;

    public Container(final A ref) {
        this.ref = ref;
    }

    @Override
    public A ref() {
        return this.ref;
    }

    public static final <A> Container<A> apply(A a) {
        return new Container(a);
    }
}

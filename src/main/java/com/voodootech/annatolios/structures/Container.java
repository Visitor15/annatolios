package com.voodootech.annatolios.structures;

import com.voodootech.annatolios.common.Monad;

import java.util.function.Function;

public class Container<TYPE> implements Monad<TYPE> {

    private final TYPE ref;

    public Container(final TYPE ref) {
        this.ref = ref;
    }

    @Override
    public TYPE ref() {
        return this.ref;
    }

    public static final <A> Container<A> apply(A a) {
        return new Container(a);
    }

    public <T> Container<T> map(Function<TYPE, T> block) {
        return Monad.super.<Container<T>>mapInternal(a -> Container.apply(block.apply(ref)));
    }
}

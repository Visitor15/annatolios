package com.voodootech.annatolios.common;

import java.util.function.Function;

public interface Monad<A> extends Functor<A> {

    default <T, B extends Monad<T>> B flatMap(Function<A, B> block) {
        return block.apply(ref());
    }
}
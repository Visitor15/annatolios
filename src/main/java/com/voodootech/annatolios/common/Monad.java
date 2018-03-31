package com.voodootech.annatolios.common;

import java.util.Optional;
import java.util.function.Function;

public interface Monad<A> {

    A ref();

    default <T, B extends Monad<T>> B flatMap(Function<A, B> block) {
        return block.apply(ref());
    }

    default <T extends Monad> T mapInternal(Function<A, T> block) {
        return block.apply(ref());
    }

    default <B> B mapTo(Function<A, B> block) {
        return block.apply(ref());
    }

    default boolean isEmpty() {
        A ref = ref();
        return (ref == null || (ref instanceof Optional && !((Optional) ref).isPresent()) || (ref instanceof Monad && ((Monad) ref).isEmpty()));
    }
}
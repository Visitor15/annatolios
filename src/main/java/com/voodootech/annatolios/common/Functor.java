package com.voodootech.annatolios.common;

import java.util.Optional;
import java.util.function.Function;

public interface Functor<A> {

    A ref();

    default <T extends Functor> T mapInternal(Function<A, T> block) {
        return block.apply(ref());
    }

    default <B> B mapTo(Function<A, B> block) {
        return block.apply(ref());
    }

    default boolean isEmpty() {
        A ref = ref();
        return (ref == null || (ref instanceof Optional && !((Optional) ref).isPresent()) || (ref instanceof Functor && ((Functor) ref).isEmpty()));
    }
}

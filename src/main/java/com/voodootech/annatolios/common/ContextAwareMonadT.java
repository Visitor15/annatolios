package com.voodootech.annatolios.common;

import java.util.function.BiFunction;

public interface ContextAwareMonadT<CONTEXT extends AbstractContext, A> {

    A ref();

    CONTEXT context();

    default <B extends Monad<A>> B flatMap(BiFunction<CONTEXT, A, B> block) {
        return block.apply(context(), ref());
    }

    default <T> T mapInternal(BiFunction<CONTEXT, A, T> block) {
        return block.apply(context(), ref());
    }

    default <B> B mapTo(BiFunction<CONTEXT, A, B> block) {
        return block.apply(context(), ref());
    }
}
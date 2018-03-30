package com.voodootech.annatolios.common;

import java.util.function.Function;

public interface Monad<A> {

    A ref();

    default <B extends Monad<A>> B flatMap(Function<A, B> block) {
        return block.apply(ref());
    }

    default <T extends Monad> T mapInternal(Function<A, T> block) {
        return block.apply(ref());
    }

    default <B> B mapTo(Function<A, B> block) {
        return block.apply(ref());
    }
}
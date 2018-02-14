package com.voodootech.annatolios.structures;

import java.util.function.Function;

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
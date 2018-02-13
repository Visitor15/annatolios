package com.voodootech.annatolios.structures;

import java.util.function.Function;

public abstract class MonadT<A> {

    public abstract A ref();

    public <B extends MonadT<A>> B flatMap(Function<A, B> block) {
        return block.apply(ref());
    }

    public <B, T extends MonadT<B>> T map(Function<A, T> block) {
        return block.apply(ref());
    }

    public <B> B mapTo(Function<A, B> block) {
        return block.apply(ref());
    }
}
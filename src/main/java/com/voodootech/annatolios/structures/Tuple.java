package com.voodootech.annatolios.structures;

import com.voodootech.annatolios.common.Monad;

import java.util.function.BiFunction;

public class Tuple<A, B> implements Monad<Tuple<A, B>> {

    private final A a;
    private final B b;

    public Tuple(final A a, final B b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public Tuple<A, B> ref() {
        return this;
    }

    public A getA() {
        return this.a;
    }

    public B getB() {
        return this.b;
    }

    public <A1, B1, C extends Tuple<A1, B1>> C map2(BiFunction<A, B, C> block) {
        return block.apply(getA(), getB());
    }

    public static final <A, B> Tuple<A, B> from(A a, B b) {
        return new Tuple<>(a, b);
    }

    public static final <A, B> Tuple<A, B> empty() {
        return new Tuple<>(null, null);
    }
}
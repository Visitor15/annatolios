package com.voodootech.annatolios.structures;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class MultiContainer<A> extends Container<List<A>> {

    public MultiContainer(A... a) {
        super(Arrays.asList(a));
    }

    public A reduce(A identity, BinaryOperator<A> accumulator) {
        return ref().stream().reduce(identity, accumulator);
    }

    public <B> B fold(B identity, BiFunction<B, ? super A, B> accumulator) {
        return ref().stream().reduce(identity, accumulator, ((i, c) -> identity));
    }

    public <B> List<B> mapMulti(Function<A, B> block) {
        return ref().stream().map(block).collect(toList());
    }

    public static final <A> MultiContainer<A> apply(A... a) {
        return new MultiContainer(a);
    }
}

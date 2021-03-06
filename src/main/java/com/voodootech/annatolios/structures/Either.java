package com.voodootech.annatolios.structures;

import com.voodootech.annatolios.common.Monad;

import java.util.Optional;
import java.util.function.Function;

public class Either<A, B> implements Monad<Either<A, B>> {

    public enum STATE {
        LEFT,
        RIGHT
    }

    private final STATE state;

    private final Tuple<Left<A>, Right<B>> tuple;

    public Either(Either.Left<A> left) {
        this.tuple = Tuple.from(left, null);
        this.state = STATE.LEFT;
    }

    public Either(Either.Right<B> right) {
        this.tuple = Tuple.from(null, right);
        this.state = STATE.RIGHT;
    }

    @Override
    public Either<A, B> ref() {
        return this;
    }

    public <C> Optional<C> mapRight(Function<B, C> block) {
        return mapOnState(Optional.empty(), Optional.ofNullable(block));
    }

    public <C> Optional<C> mapLeft(Function<A, C> block) {
        return mapOnState(Optional.ofNullable(block), Optional.empty());
    }

    public <C> Either<A, C> map(Function<B, C> block) {
        return Monad.super.<Either<A, C>>mapInternal(a -> (Either<A, C>) mapRight(element -> Either.asRight(block.apply(element))).orElseGet(() -> Either.asLeft(getLeft())));
    }

    public STATE state() {
        return this.state;
    }

    public boolean isLeft() {
        return state.equals(STATE.LEFT);
    }

    public boolean isRight() {
        return state.equals(STATE.RIGHT);
    }

    public A getLeft() {
        return (this.tuple.getA().value);
    }

    public B getRight() {
        return (this.tuple.getB().value);
    }

    private <C> Optional<C> mapOnState(Optional<Function<A, C>> blockLeft, Optional<Function<B, C>> blockRight) {
        switch(state) {
            case LEFT:
                return Optional.ofNullable(blockLeft.orElse(a -> null).apply(getLeft()));
            case RIGHT:
                return Optional.ofNullable(blockRight.orElse(b -> null).apply(getRight()));
            default:
                return Optional.empty();
        }
    }

    public static <A, B> Either<A, B> asLeft(A a) {
        return new Either<>(new Either.Left<>(a));
    }

    public static <A, B> Either<A, B> asRight(B b) {
        return new Either<>(new Either.Right<>(b));
    }

    public static class Left<A> {
        final A value;
        public Left(A a) {
            this.value = a;
        }
    }

    public static class Right<B> {
        final B value;
        public Right(B a) {
            this.value = a;
        }
    }

}
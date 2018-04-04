package com.voodootech.annatolios.common;

import com.voodootech.annatolios.structures.Either;

import java.util.concurrent.Future;
import java.util.function.Function;


public class EitherF<L, R> {

    private final Future<Either<L, R>> future;

    public EitherF(Future<Either<L, R>> future) {
        this.future = future;
    }

    public Either<L, R> getOrElse(Function<Exception, L> errorFunc) {
        try {
            return future.get();
        } catch (Exception error) {
            return Either.asLeft(errorFunc.apply(error));
        }
    }

    public static final <L, R> EitherF<L, R> apply(Future<Either<L, R>> future) {
        return new EitherF(future);
    }
}

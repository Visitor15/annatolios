package com.voodootech.annatolios.providers;

import com.voodootech.annatolios.common.AbstractContext;
import com.voodootech.annatolios.invocation.Invocable;
import com.voodootech.annatolios.structures.Either;

import java.util.function.Function;

public abstract class DataProvider<A> implements Invocable<AbstractContext, Exception> {

    public abstract Either<Exception, A> provide(final AbstractContext c);

    protected Either<Exception, A> resolveReference(final AbstractContext c, final Function<AbstractContext, A> resolutionFunc) {
        return invoke(c, resolutionFunc);
    }
}

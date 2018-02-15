package com.voodootech.annatolios.providers;

import com.voodootech.annatolios.common.AbstractContext;
import com.voodootech.annatolios.invocation.Invocable;
import com.voodootech.annatolios.structures.Either;

import java.util.function.Function;

public abstract class DataProvider<CONTEXT extends AbstractContext, A> implements Invocable<CONTEXT, Exception> {

    public abstract Either<Exception, A> provide(final CONTEXT c);

    protected Either<Exception, A> resolveReference(final CONTEXT c, final Function<CONTEXT, A> resolutionFunc) {
        return invoke(c, resolutionFunc);
    }
}

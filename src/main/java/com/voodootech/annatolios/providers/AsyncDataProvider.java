package com.voodootech.annatolios.providers;

import com.voodootech.annatolios.common.AbstractContext;
import com.voodootech.annatolios.common.EitherF;
import com.voodootech.annatolios.structures.Either;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AsyncDataProvider<CONTEXT extends AbstractContext, A> extends DataProvider<CONTEXT, A> {

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public EitherF<Exception, A> provideAsync(final CONTEXT c) {
        return EitherF.apply(executorService.submit(() -> {
            try {
                return provide(c);
            } catch (Exception e) {
                return Either.asLeft(e);
            }
        }));
    }
}

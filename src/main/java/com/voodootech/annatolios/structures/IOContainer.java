package com.voodootech.annatolios.structures;

import com.voodootech.annatolios.common.AbstractContext;
import com.voodootech.annatolios.providers.DataProvider;

public class IOContainer<CONTEXT extends AbstractContext, A> implements MonadT<Either<Exception, A>> {

    private final DataProvider<CONTEXT, A> dataProvider;
    private CONTEXT context;    //

    public IOContainer(final DataProvider<CONTEXT, A> dataProvider, final CONTEXT context) {
        this.dataProvider   = dataProvider;
        this.context        = context;
    }

    public IOContainer(final DataProvider<CONTEXT, A> dataProvider) {
        this.dataProvider   = dataProvider;
        this.context        = null;
    }

    @Override
    public Either<Exception, A> ref() {
        if(context == null) {
            return Either.asLeft(new RuntimeException("No context defined"));
        }
        return dataProvider.provide(context);
    }

    public Either<Exception, A> ref(final CONTEXT context) {
        return dataProvider.provide(context);
    }

    public static final <CONTEXT extends AbstractContext, A> IOContainer<CONTEXT, A> apply(DataProvider<CONTEXT, A> dataProvider, CONTEXT context) {
        return new IOContainer<>(dataProvider, context);
    }

    public static final <CONTEXT extends AbstractContext, A> IOContainer<CONTEXT, A> apply(DataProvider<CONTEXT, A> dataProvider) {
        return new IOContainer<>(dataProvider);
    }
}

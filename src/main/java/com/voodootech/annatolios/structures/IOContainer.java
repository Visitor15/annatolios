package com.voodootech.annatolios.structures;

import com.voodootech.annatolios.common.AbstractContext;
import com.voodootech.annatolios.providers.DataProvider;

import java.util.Optional;
import java.util.function.Function;

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
        checkContext();
        return dataProvider.provide(context);
    }

    @Override
    public <B extends MonadT<Either<Exception, A>>> B flatMap(Function<Either<Exception, A>, B> block) {
        checkContext();
        return MonadT.super.flatMap(block);
    }

    @Override
    public <B, T extends MonadT<B>> T map(Function<Either<Exception, A>, T> block) {
        checkContext();
        return MonadT.super.map(block);
    }

    @Override
    public <B> B mapTo(Function<Either<Exception, A>, B> block) {
        checkContext();
        return MonadT.super.mapTo(block);
    }

    public Either<Exception, A> ref(final CONTEXT context) {
        return dataProvider.provide(context);
    }

    private Optional<Exception> checkContext() {
        Exception exception = null;
        if(context == null) {
            exception = new IllegalArgumentException("No context defined");
        }
        return Optional.ofNullable(exception);
    }

    public static final <CONTEXT extends AbstractContext, A> IOContainer<CONTEXT, A> apply(DataProvider<CONTEXT, A> dataProvider, CONTEXT context) {
        return new IOContainer<>(dataProvider, context);
    }

    public static final <CONTEXT extends AbstractContext, A> IOContainer<CONTEXT, A> apply(DataProvider<CONTEXT, A> dataProvider) {
        return new IOContainer<>(dataProvider);
    }
}

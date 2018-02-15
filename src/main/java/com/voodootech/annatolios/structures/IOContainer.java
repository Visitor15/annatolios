package com.voodootech.annatolios.structures;

import com.voodootech.annatolios.common.AbstractContext;
import com.voodootech.annatolios.providers.DataProvider;

import java.util.Optional;
import java.util.function.Function;

public class IOContainer<CONTEXT extends AbstractContext, A> {

    private Either<Exception, A> ref;

    private final DataProvider<CONTEXT, A> dataProvider;
    private CONTEXT context;

    public IOContainer(final DataProvider<CONTEXT, A> dataProvider, final CONTEXT context, final A entity) {
        ref = (entity != null ? Either.asRight(entity) : Either.asLeft(new IllegalArgumentException("Entity is null")));
        this.dataProvider   = dataProvider;
        this.context        = context;
    }

    public IOContainer(final DataProvider<CONTEXT, A> dataProvider, final CONTEXT context) {
        ref = null;
        this.dataProvider   = dataProvider;
        this.context        = context;
    }

    public IOContainer(final DataProvider<CONTEXT, A> dataProvider) {
        ref = null;
        this.dataProvider   = dataProvider;
        this.context        = null;
    }

    public Either<Exception, A> ref() {
        Optional<Either<Exception, A>> contextCheck = checkContext().map(e -> Either.<Exception, A>asLeft(e));
        if(ref == null && contextCheck.isPresent()) {
            return contextCheck.get();
        }
        else if (ref == null) {
            ref = dataProvider.provide(context);
        }
        return ref;
    }

    public <B> B mapTo(Function<Either<Exception, A>, B> block) {
        return checkContext().map(e -> block.apply(Either.asLeft(e))).orElse(block.apply(ref()));
    }

    public IOContainer<CONTEXT, A> flatMap(CONTEXT c, Function<Either<Exception, A>, A> block) {
        this.ref        = null;
        this.context    = c;
        return new IOContainer(this.dataProvider, this.context, block.apply(ref()));
    }

    public <B> B mapTo(CONTEXT c, Function<Either<Exception, A>, B> block) {
        this.context = c;
        return block.apply(ref());
    }

    public Either<Exception, A> ref(final CONTEXT context) {
        this.ref = null;
        return dataProvider.provide(context);
    }

    private Optional<Exception> checkContext() {
        Exception exception = null;
        if(context == null) {
            exception = new IllegalArgumentException("No context defined");
        }
        return Optional.ofNullable(exception);
    }

    public static final <CONTEXT extends AbstractContext, A> IOContainer<CONTEXT, A> apply(DataProvider<CONTEXT, A> dataProvider, CONTEXT context, A entity) {
        return new IOContainer<>(dataProvider, context, entity);
    }

    public static final <CONTEXT extends AbstractContext, A> IOContainer<CONTEXT, A> apply(DataProvider<CONTEXT, A> dataProvider, CONTEXT context) {
        return new IOContainer<>(dataProvider, context);
    }

    public static final <CONTEXT extends AbstractContext, A> IOContainer<CONTEXT, A> apply(DataProvider<CONTEXT, A> dataProvider) {
        return new IOContainer<>(dataProvider);
    }
}

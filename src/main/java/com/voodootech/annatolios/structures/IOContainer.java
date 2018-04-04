package com.voodootech.annatolios.structures;

import com.voodootech.annatolios.common.AbstractContext;
import com.voodootech.annatolios.common.EitherF;
import com.voodootech.annatolios.providers.AsyncDataProvider;

import java.util.Optional;
import java.util.function.Function;

public class IOContainer<CONTEXT extends AbstractContext, A> {

    private final AsyncDataProvider<CONTEXT, A> dataProvider;

    private Either<Exception, A>    ref;
    private CONTEXT                 context;

    public IOContainer(final AsyncDataProvider<CONTEXT, A> dataProvider, final CONTEXT context, final A entity) {
        ref = (entity != null ? Either.asRight(entity) : Either.asLeft(new IllegalArgumentException("Entity is null")));
        this.dataProvider   = dataProvider;
        this.context        = context;
    }

    public IOContainer(final AsyncDataProvider<CONTEXT, A> dataProvider, final CONTEXT context) {
        ref                 = null;
        this.dataProvider   = dataProvider;
        this.context        = context;
    }

    public IOContainer(final AsyncDataProvider<CONTEXT, A> dataProvider) {
        ref                 = null;
        this.dataProvider   = dataProvider;
        this.context        = null;
    }

    public Either<Exception, A> resolveReference() {
        return getRefInternal();
    }

    public <B> B mapTo(Function<Either<Exception, A>, B> block) {
        return checkContext().map(e -> block.apply(Either.asLeft(e))).orElse(block.apply(resolveReference()));
    }

    public IOContainer<CONTEXT, A> flatMap(CONTEXT c, Function<Either<Exception, A>, A> block) {
        this.ref        = null;
        this.context    = c;
        return new IOContainer(this.dataProvider, this.context, block.apply(resolveReference()));
    }

    public <B> B mapTo(CONTEXT c, Function<Either<Exception, A>, B> block) {
        this.context = c;
        return block.apply(resolveReference());
    }

    public Either<Exception, A> resolveReference(final CONTEXT context) {
        this.ref = null;
        return dataProvider.provide(context);
    }

    public EitherF<Exception, A> resolveReferenceAsync(final CONTEXT context) {
        this.ref = null;
        return dataProvider.provideAsync(context);
    }

    private Either<Exception, A> getRefInternal() {
        Optional<Either<Exception, A>> contextCheck = checkContext().map(e -> Either.<Exception, A>asLeft(e));
        ref = ((ref == null && contextCheck.isPresent())) ? contextCheck.get() : ((ref == null) ? dataProvider.provide(context) : ref);
        return ref;
    }

    private Optional<Exception> checkContext() {
        Exception exception = null;
        if(context == null) {
            exception = new IllegalArgumentException("No context defined");
        }
        return Optional.ofNullable(exception);
    }

    public static final <CONTEXT extends AbstractContext, A> IOContainer<CONTEXT, A> apply(AsyncDataProvider<CONTEXT, A> dataProvider, CONTEXT context, A entity) {
        return new IOContainer<>(dataProvider, context, entity);
    }

    public static final <CONTEXT extends AbstractContext, A> IOContainer<CONTEXT, A> apply(AsyncDataProvider<CONTEXT, A> dataProvider, CONTEXT context) {
        return new IOContainer<>(dataProvider, context);
    }

    public static final <CONTEXT extends AbstractContext, A> IOContainer<CONTEXT, A> apply(AsyncDataProvider<CONTEXT, A> dataProvider) {
        return new IOContainer<>(dataProvider);
    }
}

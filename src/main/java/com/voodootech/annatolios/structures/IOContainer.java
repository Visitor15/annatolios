package com.voodootech.annatolios.structures;

import com.voodootech.annatolios.common.AbstractContext;
import com.voodootech.annatolios.providers.DataProvider;

import java.util.Optional;
import java.util.function.Function;

public class IOContainer<CONTEXT extends AbstractContext, A> extends Container<Either<Exception, A>> {

    private final DataProvider<CONTEXT, A> dataProvider;
    private CONTEXT context;

    public IOContainer(final DataProvider<CONTEXT, A> dataProvider, final CONTEXT context, final A entity) {
        super(Either.asRight(entity));
        this.dataProvider   = dataProvider;
        this.context        = context;
    }

    public IOContainer(final DataProvider<CONTEXT, A> dataProvider, final CONTEXT context) {
        super(Either.asLeft(new RuntimeException("No entity")));
        this.dataProvider   = dataProvider;
        this.context        = context;
    }

    public IOContainer(final DataProvider<CONTEXT, A> dataProvider) {
        super(Either.asLeft(new RuntimeException("No entity")));
        this.dataProvider   = dataProvider;
        this.context        = null;
    }

    @Override
    public Either<Exception, A> ref() {
        return checkContext().map(e ->Either.<Exception, A>asLeft(e)).orElse(dataProvider.provide(context));
    }

    @Override
    public <B extends MonadT<Either<Exception, A>>> B flatMap(Function<Either<Exception, A>, B> block) {
        return checkContext().map(e -> block.apply(Either.asLeft(e))).orElse(super.flatMap(block));
    }

    @Override
    public <B, T extends MonadT<B>> T map(Function<Either<Exception, A>, T> block) {
        return checkContext().map(e -> block.apply(Either.asLeft(e))).orElse(super.map(block));
    }

    @Override
    public <B> B mapTo(Function<Either<Exception, A>, B> block) {
        return checkContext().map(e -> block.apply(Either.asLeft(e))).orElse(super.mapTo(block));
    }

    public <B extends MonadT<Either<Exception, A>>> B flatMap(CONTEXT c, Function<Either<Exception, A>, B> block) {
        this.context = c;
        return super.flatMap(block);
    }

    public IOContainer<CONTEXT, A> map(CONTEXT c, Function<Either<Exception, A>, A> block) {
        this.context = c;
        return new IOContainer(this.dataProvider, this.context, super.mapTo(block));
    }

    public <B> B mapTo(CONTEXT c, Function<Either<Exception, A>, B> block) {
        this.context = c;
        return super.mapTo(block);
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

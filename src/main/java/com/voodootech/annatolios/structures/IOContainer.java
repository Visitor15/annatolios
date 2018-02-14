package com.voodootech.annatolios.structures;

import com.voodootech.annatolios.common.AbstractContext;
import com.voodootech.annatolios.providers.DataProvider;

import java.util.Optional;

public class IOContainer<A> implements MonadT<Optional<A>> {

    private final DataProvider<A> dataProvider;
    private final AbstractContext context;

    public IOContainer(final DataProvider<A> dataProvider, final AbstractContext context) {
        this.dataProvider   = dataProvider;
        this.context        = context;
    }

    @Override
    public Optional<A> ref() {
        return Optional.ofNullable(dataProvider.provide(this.context));
    }

    public static final <A> IOContainer<A> apply(DataProvider<A> dataProvider, AbstractContext context) {
        return new IOContainer<>(dataProvider, context);
    }
}

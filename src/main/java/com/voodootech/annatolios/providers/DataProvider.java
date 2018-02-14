package com.voodootech.annatolios.providers;

import com.voodootech.annatolios.common.AbstractContext;
import com.voodootech.annatolios.invocation.Invocable;

public interface DataProvider<A> extends Invocable<AbstractContext, Exception> {

    A provide(final AbstractContext c);
}

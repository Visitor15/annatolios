package com.voodootech.annatolios.fixtures;

import com.voodootech.annatolios.common.AbstractContext;
import com.voodootech.annatolios.providers.DataProvider;
import com.voodootech.annatolios.structures.Container;

public class SimpleDataProviderFixture {

    public static final SimpleDataProvider newInstance() {
        return new SimpleDataProvider();
    }

    public static final class SimpleDataProvider implements DataProvider<SimpleUserFixture.SimpleUser> {

        @Override
        public SimpleUserFixture.SimpleUser provide(AbstractContext c) {
            return retrieve(c).ref();
        }

        @Override
        public Exception buildErrorEntity(String errorMessage) {
            return new RuntimeException(errorMessage);
        }

        @Override
        public <A extends Exception> Exception buildErrorEntity(A exception) {
            return exception;
        }

        private Container<SimpleUserFixture.SimpleUser> retrieve(AbstractContext c) {
            System.out.println(String.format("Providing data with id %s.", c.getId()));
            return Container.apply(SimpleUserFixture.newInstance(c.getId()));
        }
    }
}

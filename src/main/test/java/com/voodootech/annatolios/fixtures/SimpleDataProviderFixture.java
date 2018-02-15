package com.voodootech.annatolios.fixtures;

import com.voodootech.annatolios.common.AbstractContext;
import com.voodootech.annatolios.providers.DataProvider;
import com.voodootech.annatolios.structures.Container;
import com.voodootech.annatolios.structures.Either;

public class SimpleDataProviderFixture {

    public static final SimpleDataProvider newInstance() {
        return new SimpleDataProvider();
    }

    public static final ExplodingDataProvider newExplodingInstance() {
        return new ExplodingDataProvider();
    }

    public static final class SimpleDataProvider extends DataProvider<SimpleUserFixture.SimpleUser> {

        @Override
        public Exception buildErrorEntity(String errorMessage) {
            return new RuntimeException(errorMessage);
        }

        @Override
        public <A extends Exception> Exception buildErrorEntity(A exception) {
            return exception;
        }

        @Override
        public Either<Exception, SimpleUserFixture.SimpleUser> provide(AbstractContext c) {
            return Either.asRight(retrieve(c).ref());
        }

        private Container<SimpleUserFixture.SimpleUser> retrieve(AbstractContext c) {
            System.out.println(String.format("Providing data with id %s.", c.getId()));
            return Container.apply(SimpleUserFixture.newInstance(c.getId()));
        }
    }

    public static final class ExplodingDataProvider extends DataProvider<SimpleUserFixture.SimpleUser> {

        @Override
        public Exception buildErrorEntity(String errorMessage) {
            return new RuntimeException(errorMessage);
        }

        @Override
        public <A extends Exception> Exception buildErrorEntity(A exception) {
            return exception;
        }

        @Override
        public Either<Exception, SimpleUserFixture.SimpleUser> provide(AbstractContext c) {
            return resolveReference(c, (context) -> retrieve(context));
        }

        private SimpleUserFixture.SimpleUser retrieve(AbstractContext c) {
            throw new NullPointerException(String.format("NULL user for id %s", c.getId()));
        }
    }
}

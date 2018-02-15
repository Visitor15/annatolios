package com.voodootech.annatolios.fixtures;

import com.voodootech.annatolios.common.AbstractContext;
import com.voodootech.annatolios.providers.DataProvider;
import com.voodootech.annatolios.structures.Container;
import com.voodootech.annatolios.structures.Either;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SimpleDataProviderFixture {

    public static final SimpleDataProvider newInstance() {
        return new SimpleDataProvider();
    }

    public static final ExplodingDataProvider newExplodingInstance() {
        return new ExplodingDataProvider();
    }

    public static final MockedDataProvider newMockedDataProvider() {
        return new MockedDataProvider();
    }

    public static final class SimpleDataProvider extends DataProvider<AbstractContext, SimpleUserFixture.SimpleUser> {

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

    public static final class ExplodingDataProvider extends DataProvider<AbstractContext, SimpleUserFixture.SimpleUser> {

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

    public static final class MockedDataProvider extends DataProvider<SimpleContext, SimpleUserFixture.SimpleUser> {

        private static final List<SimpleUserFixture.SimpleUser> users = new ArrayList<SimpleUserFixture.SimpleUser>() {{
            add(new SimpleUserFixture.SimpleUser("123", "testuser1@email.com"));
            add(new SimpleUserFixture.SimpleUser("456", "testuser2@email.com"));
            add(new SimpleUserFixture.SimpleUser("789", "testuser3@email.com"));
        }};

        @Override
        public Either<Exception, SimpleUserFixture.SimpleUser> provide(SimpleContext c) {
            return resolveReference(c, (context) -> retrieve(context));
        }

        @Override
        public Exception buildErrorEntity(String errorMessage) {
            return new RuntimeException(errorMessage);
        }

        @Override
        public <A extends Exception> Exception buildErrorEntity(A exception) {
            return exception;
        }

        private SimpleUserFixture.SimpleUser retrieve(SimpleContext c) {
            Optional<SimpleUserFixture.SimpleUser> optUser = users.stream().filter(u -> (u.getId().equals(c.getId()) && u.getEmail().equals(c.getEmail()))).findFirst();
            if(optUser.isPresent()) { return optUser.get(); } else throw new RuntimeException(String.format("User %s not found", c.getId()));
        }
    }

    public static final class SimpleContext extends AbstractContext {

        private final String firstName;
        private final String lastName;
        private final String email;

        public SimpleContext(String id, String firstName, String lastName, String email) {
            super(id);
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }
    }
}

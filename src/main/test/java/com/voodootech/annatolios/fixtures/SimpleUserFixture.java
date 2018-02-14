package com.voodootech.annatolios.fixtures;

public class SimpleUserFixture {

    public static final SimpleUser newInstance(final String id) {
        return new SimpleUser(id);
    }

    public static final class SimpleUser {
        private final String id;

        public SimpleUser(final String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }
    }
}

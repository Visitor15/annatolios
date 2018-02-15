package com.voodootech.annatolios.fixtures;

public class SimpleUserFixture {

    public static final SimpleUser newInstance(final String id) {
        return new SimpleUser(id);
    }

    public static final class SimpleUser {
        private final String id;
        private final String email;

        public SimpleUser(final String id) {
            this.id     = id;
            this.email  = null;
        }

        public SimpleUser(final String id, final String email) {
            this.id     = id;
            this.email  = email;
        }

        public String getId() {
            return this.id;
        }

        public String getEmail() {
            return this.email;
        }
    }
}

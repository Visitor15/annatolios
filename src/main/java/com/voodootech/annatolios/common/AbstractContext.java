package com.voodootech.annatolios.common;

public class AbstractContext {

    private final String id;

    public AbstractContext(final String id) {
        this.id = id;
    }

    public AbstractContext() {
        this.id = "";
    }

    public String getId() {
        return this.id;
    }

    public static final AbstractContext empty() {
        return new AbstractContext();
    }
}

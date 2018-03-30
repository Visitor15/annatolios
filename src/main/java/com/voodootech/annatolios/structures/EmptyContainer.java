package com.voodootech.annatolios.structures;

public class EmptyContainer<T> extends Container<T> {

    public EmptyContainer() {
        super(null);
    }

    public static final <A> EmptyContainer<A> apply() {
        return new EmptyContainer<>();
    }
}

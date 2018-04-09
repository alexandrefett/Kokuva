package com.kokuva.model;

public abstract class AbstractRoom {

    public abstract String getName();
    public abstract String getId();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

}
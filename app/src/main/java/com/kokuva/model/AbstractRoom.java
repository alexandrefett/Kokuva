package com.kokuva.model;

import java.util.Map;

public abstract class AbstractRoom {

    public abstract String getName();
    public abstract String getId();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

}
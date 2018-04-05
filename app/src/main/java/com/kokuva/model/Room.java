package com.kokuva.model;

public class Room extends AbstractRoom {
    private String name;
    private String uid;
    private boolean reserved;

    public Room() {
    }
    public Room(String name, boolean reserved, String uid) {
        this.name = name;
        this.uid = uid;
        this.reserved = reserved;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }
}

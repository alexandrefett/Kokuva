package com.kokuva.model;

import java.util.HashMap;

/**
 * Created by Alexandre on 21/09/2016.
 */

public class UserPref {

    private String email;
    private boolean man;
    private boolean woman;
    private long distance;

    public static final String  EMAIL = "email";
    public static final String  MAN = "man";
    public static final String  WOMAN = "woman";
    public static final String  DISTANCE = "distance";

    public UserPref(){
    }

    public UserPref(Object O){
        HashMap<String, Object> o = (HashMap<String, Object>)O;
        this.email  = (String)o.get(EMAIL);
        this.man = (boolean)o.get(MAN);
        this.woman = (boolean)o.get(WOMAN);
        this.distance = (long)o.get(DISTANCE);
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> o = new HashMap<>();
        o.put(EMAIL, this.email);
        o.put(MAN, this.man);
        o.put(WOMAN, this.woman);
        o.put(DISTANCE, this.distance);
        return o;
    }
}

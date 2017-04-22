package com.kokuva.model;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Alexandre on 21/09/2016.
 */

public class KokuvaUser {

    private int color;
    private String nick;
    private String uid;
    private String url;
    private double lat;
    private double log;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLog() {
        return log;
    }

    public void setLog(double log) {
        this.log = log;
    }

    public KokuvaUser(FirebaseUser u, double lat, double log, int color){
        this.lat = lat;
        this.log = log;
        this.nick = u.getDisplayName();
        this.uid = u.getUid();
        this.url = u.getPhotoUrl().toString();
        this.color = color;
    }

    public KokuvaUser(){}
}

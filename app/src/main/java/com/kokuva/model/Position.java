package com.kokuva.model;

/**
 * Created by Alexandre on 21/09/2016.
 */

public class Position {

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    private String nick_name;
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String uid;

    private double lat;

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

    private double log;

    public Position(String uid, double lat, double log){
        this.lat = lat;
        this.log = log;
        this.uid = uid;
    }
}

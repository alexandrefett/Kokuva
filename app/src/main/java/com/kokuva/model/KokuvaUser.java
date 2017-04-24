package com.kokuva.model;

import com.google.firebase.auth.FirebaseUser;
import com.kokuva.R;

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
    private boolean photo = false;
    private String email;
    private String mac;
    private int dist;

    public int getDist() {
        return dist;
    }

    public void setDist(int dist) {
        this.dist = dist;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isPhoto() {
        return photo;
    }

    public void setPhoto(boolean photo) {
        this.photo = photo;
    }

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

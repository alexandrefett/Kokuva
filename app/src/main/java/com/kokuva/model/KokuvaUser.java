package com.kokuva.model;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Alexandre on 21/09/2016.
 */

public class KokuvaUser {

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    private String displayName;
    private String uid;
    private String photoUrl;
    private double lat;
    private double log;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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


    public KokuvaUser(FirebaseUser u, double lat, double log){
        this.lat = lat;
        this.log = log;
        this.displayName = u.getDisplayName();
        this.uid = u.getUid();
        this.photoUrl = u.getPhotoUrl().toString();
    }

    public KokuvaUser(){}
}

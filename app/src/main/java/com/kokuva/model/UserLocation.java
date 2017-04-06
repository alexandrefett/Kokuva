package com.kokuva.model;

import java.util.HashMap;

/**
 * Created by Alexandre on 21/09/2016.
 */

public class UserLocation {
    private double latitude;
    private double longitude;

    public static final String  LATITUDE = "latitude";
    public static final String  LONGITUDE = "longitude";

    public UserLocation(){
    }

    public UserLocation(double lat, double log){
        this.latitude = lat;
        this.longitude = log;
    }


    public UserLocation(Object O){
        HashMap<String, Object> o = (HashMap<String, Object>)O;
        this.latitude = (double) o.get(LATITUDE);
        this.longitude = (double) o.get(LONGITUDE);
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> o = new HashMap<>();
        o.put(LATITUDE, this.latitude);
        o.put(LONGITUDE, this.longitude);
        return o;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

}

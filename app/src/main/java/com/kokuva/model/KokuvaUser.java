package com.kokuva.model;

/**
 * Created by Alexandre on 21/09/2016.
 */

public class KokuvaUser {

    String nick;
    String uid;
    String email;
    String mac;

    public KokuvaUser(){}

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}

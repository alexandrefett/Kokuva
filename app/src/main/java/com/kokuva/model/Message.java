package com.kokuva.model;

/**
 * Created by Alexandre on 21/09/2016.
 */

public class Message {

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String sender;
    private String message;

    public Message(){
    }


}

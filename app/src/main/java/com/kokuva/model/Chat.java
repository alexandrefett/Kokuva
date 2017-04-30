package com.kokuva.model;

import java.util.ArrayList;

/**
 * Created by Alexandre on 21/09/2016.
 */

public class Chat {

    private String chatId;
    private KokuvaUser userTo;

    public KokuvaUser getUserTo() {
        return userTo;
    }

    public void setUserTo(KokuvaUser userTo) {
        this.userTo = userTo;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Chat(){
    }

    public Chat(String chatId, KokuvaUser user){
        this.userTo = user;
        this.chatId = chatId;
    }


}

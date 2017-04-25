package com.kokuva.model;

import java.util.ArrayList;

/**
 * Created by Alexandre on 21/09/2016.
 */

public class Chat {

    private String chatId;
    private KokuvaUser user;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public KokuvaUser getUser() {
        return user;
    }

    public void setUser(KokuvaUser user) {
        this.user = user;
    }

    public Chat(){
    }

    public Chat(String cid, KokuvaUser k){
        chatId = cid;
        user = k;
    }
}

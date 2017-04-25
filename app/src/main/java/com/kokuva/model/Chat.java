package com.kokuva.model;

import java.util.ArrayList;

/**
 * Created by Alexandre on 21/09/2016.
 */

public class Chat {

    private String chatId;
    private ArrayList<KokuvaUser> users = new ArrayList<KokuvaUser>();

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public ArrayList<KokuvaUser> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<KokuvaUser> users) {
        this.users = users;
    }

    public Chat(){
    }


}

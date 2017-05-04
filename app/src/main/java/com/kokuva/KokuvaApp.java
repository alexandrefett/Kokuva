package com.kokuva;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kokuva.model.Chat;
import com.kokuva.model.KokuvaUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexandre on 19/09/2016.
 */
public class KokuvaApp extends Application {
    private static KokuvaApp ourInstance = new KokuvaApp();

    private Context context;
    private KokuvaUser user;
    private ArrayList<Chat> chats = new ArrayList<Chat>();

    public Chat getChat(int i){
        return chats.get(i);
    }

    public boolean chatExist(String userid){
        for(Chat c:chats){
            if(c.getUserTo().getUid().equals(userid)){
                return true;
            }
        }
        return false;
    }

    public void addChat(Chat chat){
        chats.add(chat);
    }

    public void removeChat(Chat chat){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("chats").child(user.getUid()).child(chat.getChatId()).removeValue();
        myRef.child("chats").child(chat.getUserTo().getUid()).child(chat.getChatId()).removeValue();
        chats.remove(chat);
    }

    public KokuvaUser getUser() {
        return user;
    }


    public void setUser(KokuvaUser user) {
        this.user = user;
    }
    public static KokuvaApp getInstance() {
        return ourInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

}
package com.kokuva;

import android.app.Application;
import android.content.Context;

import com.google.firebase.auth.FirebaseUser;
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

    public String getUserChat(String user){
        for(Chat c:chats){
            if(c.getUser().getUid().equals(user)){
                return c.getChatId();
            }
        }
        return null;
    }

    public ArrayList<Chat> getChats() {
        return chats;
    }

    public void setChats(ArrayList<Chat> chats) {
        this.chats = chats;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static boolean isActivityVisible() {
        return chatActivity;
    }

    public static void activityResumed() {
        chatActivity = true;
    }

    public static void activityPaused() {
        chatActivity = false;
    }

    private static boolean chatActivity;

}
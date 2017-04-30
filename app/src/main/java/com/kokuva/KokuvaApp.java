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


    public void addChatItem(Chat chat){
        chats.add(chat);
    }

    public void removeChatItem(Chat chat){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("chats").child(user.getUid()).child(chat.getChatId()).removeValue();
        myRef.child("chats").child(chat.getUserTo().getUid()).child(chat.getChatId()).removeValue();
        chats.remove(chat);
    }

    public KokuvaUser getUser() {
        return user;
    }

    public void getChats(){

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

    public void addChat(KokuvaUser userTo){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        String chatId = myRef.child("chats").push().getKey();
        Chat chat = new Chat(chatId, userTo);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("chats/"+user.getUid()+"/"+chatId, userTo);
        data.put("chats/"+userTo.getUid()+"/"+chatId, user);
        //data.put("users/"+user.getUid()+"/chats/",chatId);
        //data.put("users/"+"/chats/"+user.getUid(), chatId);

        myRef.updateChildren(data);

    }


}
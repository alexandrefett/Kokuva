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
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    public void addFragment(Fragment f){
            fragments.add(f);
    }

    public Fragment getFragment(int i){
        return fragments.get(i);
    }

    public boolean fragmentExist(Chat c){
        for(Fragment f:fragments){
            if(c.getChatId().equals(f.getArguments().getString("chatid"))){
                return true;
            }
        }
        return false;
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

    public void addChat(KokuvaUser userTo){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        String chatId = myRef.child("chats").push().getKey();
        Chat chat = new Chat(chatId, userTo);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("chats/"+user.getUid()+"/"+chatId, userTo);
        data.put("chats/"+userTo.getUid()+"/"+chatId, user);

        myRef.updateChildren(data);
    }

    public interface OnNewChat{
        public void onNewChat();
    }

    private OnNewChat newChatListener;

    private void listenMyChats(OnNewChat newChat){
        newChatListener = newChat;
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("chats").child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat c  = dataSnapshot.getValue(Chat.class);
                Bundle b = new Bundle();
                b.putString("chatid", c.getChatId());
                b.putString("userid", c.getUserTo().getUid());
                b.putString("nick", c.getUserTo().getNick());
                FragmentChat fragment = new FragmentChat();
                fragment.setArguments(b);
                KokuvaApp.getInstance().addFragment(fragment);
                KokuvaApp.getInstance().addChat(c);
                //swapFragment(fragment);
//                adapter.addFragment(fragment,c.getChatId());
//                pager.setCurrentItem(pager.getChildCount()-1, true);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {         }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {         }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {        }

            @Override
            public void onCancelled(DatabaseError databaseError) {       }
        });
    }

}
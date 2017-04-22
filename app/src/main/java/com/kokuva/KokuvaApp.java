package com.kokuva;

import android.app.Application;
import android.content.Context;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kokuva.model.KokuvaUser;

/**
 * Created by Alexandre on 19/09/2016.
 */
public class KokuvaApp extends Application {
    private static KokuvaApp ourInstance = new KokuvaApp();

    private Context context;
    private KokuvaUser user;

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

    public void addChat(String uidTo){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("users").child(uidTo).setValue("chat/"+user.getUid());
    }


}
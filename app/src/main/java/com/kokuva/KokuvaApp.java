package com.kokuva;

import android.app.Application;
import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;
import com.kokuva.model.Profile;

/**
 * Created by Alexandre on 19/09/2016.
 */
public class KokuvaApp extends Application {
    public static int LOGIN_FACEBOOK = 100;
    public static int LOGIN_GOOGLE = 200;
    private static KokuvaApp ourInstance = new KokuvaApp();
    private Profile profile;
    private Context context;

    public static KokuvaApp getInstance() {
        return ourInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(profile==null)
            profile = new Profile();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

}
package com.kokuva;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

public class App {
    // Create the instance
    private static App instance;
    private static final String TAG = "App----->";
    public static App getInstance() {
        if (instance == null) {
            synchronized (App.class) {
                if (instance == null)
                    instance = new App();
            }
        }
        // Return the instance
        return instance;
    }

    private App() {
        // Constructor hidden because this is a singleton
    }


}
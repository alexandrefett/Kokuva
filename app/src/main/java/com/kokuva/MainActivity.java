package com.kokuva;

import android.Manifest.permission;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kokuva.model.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.regex.Pattern;

public class MainActivity extends BaseActivity {

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        KokuvaApp.getInstance().setContext(this);
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_main);

        showDialog();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(TAG, "onAuthStateChanged");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    getUserFromFirebase(mAuth.getCurrentUser().getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    createUser();
//                    createUser();
                }
            }
        };


/*        if (mAuth.getCurrentUser() == null) {
            loginUser();
        } else {
            getUserFromFirebase(mAuth.getCurrentUser().getUid());
        }
        */
    }

    private void createUser(){
        Log.d(TAG, "loginUser");
        final User user = new User();
        TelephonyManager t = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        user.setPhone(t.getLine1Number());

        Pattern email = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (email.matcher(account.name).matches()) {
                user.setEmail(account.name);
            }
        }

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        user.setMacaddress(wInfo.getMacAddress());

        Log.d(TAG, "loginUser: " + user.getEmail());
        Log.d(TAG, "loginUser: " + user.getMacaddress());

        createAccount(user);
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void createAccount(final User u) {
        Log.d(TAG, "createAccount");
        mAuth.createUserWithEmailAndPassword(u.getEmail(), u.getMacaddress())
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "OnComplete:");
                }
            })
            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    u.setUid(authResult.getUser().getUid());
                    saveUser(u);
                    Log.d(TAG, "OnSuccessListener:" + authResult.getUser().getUid());
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure:" + e.getMessage());
                    mAuth.signInWithEmailAndPassword(u.getEmail(), u.getMacaddress())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Log.d(TAG, "onSuccess Login Email:" + authResult.getUser().getUid());
                            hideDialog();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure Login:" + e.getMessage());
                            hideDialog();
                        }
                    });
                }
            });
    }

    private void saveUser(final User u){
        Log.d(TAG, "saveUser");

        myRef.child("users/"+u.getUid()).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "userSaved");
            }
        });
    }

    private void getUserFromFirebase(String uid){
        Log.d(TAG, "getUserFromFirebase");
        myRef.child("users/"+uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "getUserFromFirebase:" + dataSnapshot.toString());
                if(dataSnapshot.getValue()!=null) {
                    Log.d(TAG, "getUserFromFirebase:" + dataSnapshot.toString());
                    KokuvaApp.getInstance().setUser(dataSnapshot.getValue(User.class));
                }
                hideDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void browseUsers(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment newFragment = new FragmentBrowseUsers();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

}
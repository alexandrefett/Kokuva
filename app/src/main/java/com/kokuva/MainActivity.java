package com.kokuva;

import android.Manifest.permission;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kokuva.model.User;

import java.util.regex.Pattern;

public class MainActivity extends BaseActivity {

    private DatabaseReference myRef;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        KokuvaApp.getInstance().setContext(this);
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_main);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        if (mAuth.getCurrentUser() == null) {
            createUser();
        } else {
            getUserFromFirebase(mAuth.getCurrentUser().getUid());
        }
    }

    private void createUser(){
        User user = new User();
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

        user.setEmail("alexandrefett@gmail.com");
        user.setMacaddress("00:00:00:00:00:00");
        user.setPhone("21983297979");
        createAccount(user);

    }

    private void getLocation() {

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                Log.d(TAG, "onLocationChanged: " + location.getLatitude());
                KokuvaApp.getInstance().getUser().setLocation(location.getLatitude(), location.getLongitude());
                updateLocation();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d(TAG, "onStatusChanged: " + s);
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.d(TAG, "onProviderEnabled: " + s);
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d(TAG, "onProviderDisabled: " + s);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, mLocationListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mLocationManager.removeUpdates(mLocationListener);
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                return;
            }
            if (requestCode == 0) {
                //
                //
                //
            }
        }
    }

    private void createAccount(final User u) {
        mAuth.createUserWithEmailAndPassword(u.getEmail(), u.getMacaddress())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {

                            Log.d(TAG, "createUserWithEmail: " + task.getException().getMessage());
                        } else {
                            u.setUid(task.getResult().getUser().getUid());
                            saveUser(u);
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.getResult().getUser().getUid());
                        }
                    }
                });
    }

    private void saveUser(final User u){
        myRef.child("users/"+u.getUid()).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //
            }
        });
    }

    private void updateLocation() {
        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        mLocationManager.removeUpdates(mLocationListener);
        myRef.child("users/"+KokuvaApp.getInstance().getUser().getUid()).setValue(KokuvaApp.getInstance().getUser());
    }

    private void getUserFromFirebase(String uid){
        showProgressDialog();
        myRef.child("users/"+uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    KokuvaApp.getInstance().setUser(dataSnapshot.getValue(User.class));
                    hideProgressDialog();
                }
                else {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
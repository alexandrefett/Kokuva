package com.kokuva;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kokuva.model.Position;

import static android.content.Context.LOCATION_SERVICE;

public class FragmentBrowseUsers extends BaseFragment {

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private DatabaseReference myRef;
    private FirebaseUser user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args!=null) {
            //category = args.getString("category", "");
        }
        myRef = FirebaseDatabase.getInstance().getReference();
        user = KokuvaApp.getInstance().getUser();
        getLocation();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_browseusers, container, false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop(){
        super.onStop();
        deactiveUser();
    }

    private void getLocation() {
        showDialog();
        mLocationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                Log.d(TAG, "onLocationChanged: " + location.getLatitude());
                //KokuvaApp.getInstance().getUser().setLocation(location.getLatitude(), location.getLongitude());
                Position p = new Position(user.getUid(), location.getLatitude(), location.getLongitude());
                updateLocation(p);
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

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, mLocationListener);
    }

    private void updateLocation(Position l) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        mLocationManager.removeUpdates(mLocationListener);
        myRef.child("users").child(user.getUid()).setValue(l).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
                hideDialog();
            }
        });
    }

    private void deactiveUser(){
        myRef.child("users").child(user.getUid()).removeValue();
    }

}
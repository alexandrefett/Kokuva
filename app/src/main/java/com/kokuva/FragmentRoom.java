package com.kokuva;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kokuva.adapter.FirebaseUsersAdapter;
import com.kokuva.model.KokuvaUser;
import com.kokuva.views.UserTabView;

import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;

public class FragmentRoom extends BaseFragment {
    private DatabaseReference myRef;
    private FirebaseUser user;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private RecyclerView list_users;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args!=null) {
            //userToId = args.getString("userToId", "");
        }

        myRef = FirebaseDatabase.getInstance().getReference();
        user = KokuvaApp.getInstance().getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_room, container, false);

        list_users = (RecyclerView)view.findViewById(R.id.users_list);

        getLocation();

        return view;
    }
    private void getLocation() {
        showDialog();
        mLocationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                Log.d(TAG, "onLocationChanged: " + location.getLatitude());
                updateLocation(location);
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

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, mLocationListener);
    }

    private void updateLocation(final Location l) {
        mLocationManager.removeUpdates(mLocationListener);
        KokuvaUser kuser = new KokuvaUser(user, l.getLatitude(), l.getLongitude());

        myRef.child("users").child(user.getUid()).setValue(kuser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideDialog();
                        getUsers();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                        hideDialog();
                    }
                });
    }
    private void getUsers(){

        ArrayList<KokuvaUser> users = new ArrayList<KokuvaUser>();
        ArrayList<String> usersKeys = new ArrayList<String>();
        Query recentPostsQuery = myRef.child("users");

        recentPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> list = dataSnapshot.getChildren();

//                List<KokuvaUser> userList = new ArrayList<>();
//                for (DataSnapshot dataSnapshot1 : list) {
//                    if (!dataSnapshot1.getKey().equals(user.getUid())) {
//                        userList.add(dataSnapshot1.getValue(KokuvaUser.class));
//                    }
//                }
                Log.d(TAG, "getUsers : "+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseUsersAdapter userAdapter = new FirebaseUsersAdapter(recentPostsQuery, KokuvaUser.class, users, usersKeys);
        userAdapter.setContext(getContext(), getActivity());
        userAdapter.addOnClickItemListener(new FirebaseUsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(KokuvaUser item) {
                //cria chat
                //convida usuario
                //myRef.child("users").child("chats").child(String.valueOf(System.currentTimeMillis())).setValue()
            }
        });
        list_users.setLayoutManager(new LinearLayoutManager(getContext()));
        list_users.setAdapter(userAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void exitRoom(){
        myRef.child("users").child(user.getUid()).removeValue();
    }

}
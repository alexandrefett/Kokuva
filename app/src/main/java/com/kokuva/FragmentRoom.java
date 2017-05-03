package com.kokuva;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kokuva.adapter.FirebaseUsersAdapter;
import com.kokuva.model.Chat;
import com.kokuva.model.KokuvaUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;

public class FragmentRoom extends BaseFragment {
    private DatabaseReference myRef;
    private KokuvaUser user;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private RecyclerView list_users;
    private static FragmentRoom ourInstance;

    public static FragmentRoom getInstance(String value) {
        if (ourInstance == null) {
            ourInstance = new FragmentRoom();
            if (value != null) {
                Bundle args = new Bundle();
                args.putString("value", value);
                ourInstance.setArguments(args);
            }
        }
        return ourInstance;
    }

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

        list_users = (RecyclerView)view.findViewById(R.id.room_users_list);

        //getLocation();
        hideDialog();
        getUsers();

        return view;
    }
    private void getLocation() {
        showDialog();
        mLocationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                updateLocation(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {      }

            @Override
            public void onProviderEnabled(String s) {     }

            @Override
            public void onProviderDisabled(String s) {         }
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
        user.setLat(l.getLatitude());
        user.setLog(l.getLongitude());

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("users/"+user.getUid()+"/lat", user.getLat());
        data.put("users/"+user.getUid()+"/log", user.getLog());

        myRef.updateChildren(data).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        Query query = myRef.child("users");

        FirebaseUsersAdapter userAdapter = new FirebaseUsersAdapter(query, KokuvaUser.class, users, usersKeys);
        userAdapter.setContext(getContext());
        userAdapter.addOnClickItemListener(new FirebaseUsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(KokuvaUser item) {

                if(!KokuvaApp.getInstance().chatExist(item.getUid())) {
                    String chatId = myRef.child("chats").push().getKey();
                    Chat c1 = new Chat(chatId, user);
                    final Chat c2 = new Chat(chatId, item);

                    Map<String, Object> data = new HashMap<String, Object>();
                    data.put("chats/" + user.getUid() + "/" + item.getUid(), c2);
                    data.put("chats/" + item.getUid() + "/" + user.getUid(), c1);

                    myRef.updateChildren(data);
                }
                else {

                }

            }
        });
        RecyclerView.LayoutManager lm = new GridLayoutManager(getContext(),2);
        list_users.setAdapter(userAdapter);
        list_users.setLayoutManager(lm);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
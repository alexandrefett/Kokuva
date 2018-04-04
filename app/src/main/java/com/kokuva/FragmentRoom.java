package com.kokuva;

import android.app.Activity;
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

    public interface OnClickUserListener{
        public void onClickUser(KokuvaUser u);
    }

    private DatabaseReference myRef;
    private KokuvaUser user;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private RecyclerView list_users;
    private static FragmentRoom ourInstance;
    Activity activity;

    public static FragmentRoom getInstance() {
        if (ourInstance == null) {
            ourInstance = new FragmentRoom();
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
        activity = getActivity();
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


    private void getUsers(){
        ArrayList<KokuvaUser> users = new ArrayList<KokuvaUser>();
        ArrayList<String> usersKeys = new ArrayList<String>();
        Query query = myRef.child("users");

        FirebaseUsersAdapter userAdapter = new FirebaseUsersAdapter(query, KokuvaUser.class, users, usersKeys);
        userAdapter.setContext(getContext());
        userAdapter.addOnClickItemListener(new FirebaseUsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(KokuvaUser item) {
                try{
                    ((OnClickUserListener) activity).onClickUser(item);
                }catch (ClassCastException cce){
                    Log.d(TAG,"----FragmentRoom: "+cce.getMessage());
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
        Log.d(TAG,"----FragmentRoom: OnStart");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG,"----FragmentRoom: OnResume");

    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG,"----FragmentRoom: OnPause");

    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG,"----FragmentRoom: OnStop");

    }

}
package com.kokuva;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kokuva.adapter.FirebaseUsersAdapter;
import com.kokuva.adapter.ViewPagerAdapter;
import com.kokuva.model.KokuvaUser;

import java.util.ArrayList;
import java.util.List;

public class RoomActivity extends BaseActivity {

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private RecyclerView list_users;
    private LinearLayout scroll_users;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);


        myRef = FirebaseDatabase.getInstance().getReference();
        user = KokuvaApp.getInstance().getUser();

        pager = (ViewPager)findViewById(R.id.viewpager);
        list_users = (RecyclerView)findViewById(R.id.users_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        list_users = (RecyclerView)findViewById(R.id.list_users);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.open, R.string.close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle("Kokuva");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle("Kokuva");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getLocation();

    }

    private void setPageViewer(){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentRoom(),"");
        pager.setAdapter(adapter);
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
        userAdapter.setContext(this, this);
        list_users.setLayoutManager(new LinearLayoutManager(this));
//        list_users.setItemAnimator(new DefaultItemAnimator());
//        StaggeredGridLayoutManager stag = new StaggeredGridLayoutManager(2,1);
//        list_users.setLayoutManager(stag);
        list_users.setAdapter(userAdapter);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.action_set_range: {
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop(){
        super.onStop();
        //exitRoom();
    }

    private void getLocation() {
        showDialog();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exitRoom();
        finish();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        exitRoom();
    }

    private void exitRoom(){
        myRef.child("users").child(user.getUid()).removeValue();
    }
}

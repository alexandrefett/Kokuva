package com.kokuva;

import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kokuva.adapter.ViewPagerAdapter;
import com.kokuva.model.Profile;
import com.kokuva.model.UserLocation;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 100;
    private static final int RC_PROFILE = 200;
    private DatabaseReference myRef;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Profile profile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        KokuvaApp.getInstance().setContext(this);
        myRef = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, RC_SIGN_IN);
        } else {
            getUserFromFirebase(auth.getCurrentUser().getUid());
            getLocation(auth.getCurrentUser().getUid());
        }
    }

    private void setupViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentMain(), null);
        adapter.addFragment(new FragmentProfile(), null);
        adapter.addFragment(new FragmentMessage(), null);
        adapter.addFragment(new FragmentPreferences(), null);
        viewPager.setAdapter(adapter);
        int array[] = {R.drawable.ic_action_home,
                R.drawable.ic_action_user,
                R.drawable.ic_action_dialog,
                R.drawable.ic_action_gear};
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(array[i]);
            tabLayout.getTabAt(i).setCustomView(imageView);
        }
        //tabLayout.getTabAt(0).setIcon(R.drawable.ic_action_home);
        //tabLayout.getTabAt(1).setIcon(R.drawable.ic_action_user);
        //tabLayout.getTabAt(2).setIcon(R.drawable.ic_action_dialog);
        //tabLayout.getTabAt(3).setIcon(R.drawable.ic_action_gear);
    }


    private void getLocation(final String uid) {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                Log.d(TAG, "onLocationChanged: " + location.getLatitude());
                UserLocation l = new UserLocation(location.getLatitude(), location.getLongitude());
                updateLocation(uid, l);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_SIGN_IN) {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivityForResult(intent, RC_PROFILE);
                return;
            }
            if (requestCode == RC_PROFILE) {
                //
                //
                //
            }
        }
    }

    @Override
    public void onClick(View view) {

    }

    private void updateLocation(String uid, UserLocation l) {
        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        mLocationManager.removeUpdates(mLocationListener);
        myRef.child("location/"+uid).setValue(l);
    }

    private void getUserFromFirebase(String uid){
        showProgressDialog();
        myRef.child("users/"+uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    KokuvaApp.getInstance().setProfile(dataSnapshot.getValue(Profile.class));
                    profile = KokuvaApp.getInstance().getProfile();
                    setupViewPager();
                    hideProgressDialog();
                    if (profile.getPhotos().size() == 0) {

                        Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
                        startActivityForResult(intent, RC_PROFILE);
                    }
                }
                else {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.profile:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivityForResult(intent, RC_PROFILE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
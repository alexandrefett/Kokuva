package com.kokuva;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
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

public class ChatActivity extends BaseActivity {

    private DatabaseReference myRef;
    private FirebaseUser user;
    private TabHost tab;
    private ArrayList<KokuvaUser> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        myRef = FirebaseDatabase.getInstance().getReference();
        user = KokuvaApp.getInstance().getUser();
        tab = (TabHost)findViewById(R.id.tabHost);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }
    private void addChat(){

    }
    
    private void setupViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOnTouchListener(new View.OnTouchListener()
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
        adapter.addFragment(new FragmentChat(), null);
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


    public void addUser(KokuvaUser k){
        users.add(k);
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

    @Override
    public void onDestroy(){
        super.onDestroy();
    }


}

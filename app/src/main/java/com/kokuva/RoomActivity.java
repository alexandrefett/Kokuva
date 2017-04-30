package com.kokuva;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.widgets.BubbleThumbSeekbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kokuva.adapter.ChatAdapter;
import com.kokuva.adapter.ImageAdapter;
import com.kokuva.dialogs.AvatarDialog;
import com.kokuva.dialogs.DistanceDialog;
import com.kokuva.model.KokuvaUser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RoomActivity extends BaseActivity implements DistanceDialog.DistanceDialogListener {

    private DatabaseReference myRef;
    private KokuvaUser user;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myRef = FirebaseDatabase.getInstance().getReference();
        user = KokuvaApp.getInstance().getUser();

        swapFragment(FragmentRoom.getInstance(null));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drawer_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_room:
                swapFragment(FragmentRoom.getInstance(null));
                break;
            case R.id.nav_distance:
                setDistance();
                break;
            case R.id.nav_chats:
                swapFragment(FragmentUsers.getInstance(null));
                break;
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void swapFragment(Class t){
    private void swapFragment(Fragment t){

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, t).commit();
    }

    @Override
    public void onStop(){
        super.onStop();
        myRef.child("users").child(user.getUid()).child("lat").removeValue();
        myRef.child("users").child(user.getUid()).child("log").removeValue();
    }

    private void setDistance(){
        Bundle b = new Bundle();
        b.putInt("distance", user.getDist());
        DialogFragment newFragment = new DistanceDialog();
        newFragment.setArguments(b);

        newFragment.show(getSupportFragmentManager(), "avatar");
    }
    @Override
    public void onDistanceChange(int distance) {
        user.setDist(distance);
    }
}



package com.kokuva;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.kokuva.adapter.FirebaseChatsAdapter;
import com.kokuva.adapter.ViewPagerAdapter;
import com.kokuva.dialogs.DistanceDialog;
import com.kokuva.model.Chat;
import com.kokuva.model.KokuvaUser;

import java.util.ArrayList;

public class RoomActivity extends BaseActivity implements DistanceDialog.DistanceDialogListener {

    private DatabaseReference myRef;
    private KokuvaUser user;
    private Toolbar toolbar;
    private ViewPager pager;
    private ViewPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pager = (ViewPager)findViewById(R.id.pager);

        myRef = FirebaseDatabase.getInstance().getReference();
        user = KokuvaApp.getInstance().getUser();

        setupViewPager();
    }

    private void setupViewPager(){
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(FragmentRoom.getInstance(null),"room");
        adapter.addFragment(FragmentUsers.getInstance(null),"users");
        pager.setAdapter(adapter);
        listenMyChats();
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
                pager.setCurrentItem(0);
                break;
            case R.id.nav_distance:
                setDistance();
                break;
            case R.id.nav_chats:
                pager.setCurrentItem(1);
                break;
            case android.R.id.home:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void listenMyChats(){
        myRef.child("chats").child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat c  = dataSnapshot.getValue(Chat.class);
                Bundle b = new Bundle();
                b.putString("chatid", c.getChatId());
                b.putString("userid", c.getUserTo().getUid());
                b.putString("nick", c.getUserTo().getNick());
                FragmentChat fragment = new FragmentChat();
                fragment.setArguments(b);
                adapter.addFragment(fragment,c.getChatId());
                pager.setCurrentItem(pager.getChildCount()-1, true);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void endChatDialog(final Chat c){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(c.getUserTo().getNick()+" saiu da conversa.")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // end chat
                    }
                });
        builder.setTitle("Mensagem");
        builder.show();
    }

    @Override
    public void onStop(){
        super.onStop();
        myRef.child("users").child(user.getUid()).child("lat").removeValue();
        myRef.child("users").child(user.getUid()).child("log").removeValue();
        myRef.child("chats").child(user.getUid()).removeValue();

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



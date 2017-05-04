package com.kokuva;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kokuva.adapter.ChatAdapter;
import com.kokuva.dialogs.ChatsDialog;
import com.kokuva.dialogs.DistanceDialog;
import com.kokuva.model.Chat;
import com.kokuva.model.KokuvaUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RoomActivity extends BaseActivity implements DistanceDialog.DistanceDialogListener,
        ChatsDialog.ChatDialogListener, FragmentRoom.OnClickUserListener {

    private DatabaseReference myRef;
    private KokuvaUser user;
    private Toolbar toolbar;
    private ChildEventListener listenMyChats;
    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myRef = FirebaseDatabase.getInstance().getReference();
        user = KokuvaApp.getInstance().getUser();
        adapter = new ChatAdapter(this, new ArrayList<Chat>());
    }

    private void setupListen(){
        listenMyChats = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat c  = dataSnapshot.getValue(Chat.class);
                createFragmentChat(c);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {         }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {         }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {        }

            @Override
            public void onCancelled(DatabaseError databaseError) {       }
        };
        myRef.child("chats").child(user.getUid()).addChildEventListener(listenMyChats);
    }

    public void creatChat(KokuvaUser item){
        String chatId = myRef.child("chats").push().getKey();
        Chat c1 = new Chat(chatId, user);
        final Chat c2 = new Chat(chatId, item);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("chats/" + user.getUid() + "/" + item.getUid(), c2);
        data.put("chats/" + item.getUid() + "/" + user.getUid(), c1);

        myRef.updateChildren(data);
    }

    public void createFragmentChat(Chat c){
        FragmentChat fragment = new FragmentChat();
        fragment.setChat(c);
        KokuvaApp.getInstance().addChat(c);
        addAndShow(fragment, c.getUserTo().getUid());
    }

    public void addAndShow(Fragment f, String tag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment current = fragmentManager.findFragmentById(R.id.fcontent);
        fragmentManager.beginTransaction()
                .add(R.id.fcontent, f, tag)
                .show(f)
                .hide(current)
                .commit();
        //swapFragment(f);
    }

    public void swapFragment(Fragment f){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment current = fragmentManager.findFragmentById(R.id.fcontent);
        fragmentManager.beginTransaction()
                .hide(current)
                .show(f)
                .commit();
    }

    private void setupFragment(){
        FragmentRoom r = FragmentRoom.getInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fcontent, r)
                .commit();
        setupListen();
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
                swapFragment(getSupportFragmentManager().findFragmentByTag("room"));
                break;
            case R.id.nav_distance:
                setDistance();
                break;
            case R.id.nav_chats:
                chooseChat();
                break;
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void chooseChat() {
        DialogFragment newFragment = new ChatsDialog();
        newFragment.show(getSupportFragmentManager(), "chats");
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
        Log.d(TAG,"----RoomActivity: OnResume");
        myRef.child("users").child(user.getUid()).child("lat").removeValue();
        myRef.child("users").child(user.getUid()).child("log").removeValue();
        myRef.child("chats").child(user.getUid()).removeValue();
        myRef.removeEventListener(listenMyChats);
        for(Fragment f:getSupportFragmentManager().getFragments()){
            if(!f.getTag().equals("room")){
                Fragment frag = getSupportFragmentManager().findFragmentByTag(f.getTag());
                getSupportFragmentManager().beginTransaction().remove(frag).commit();
            }
        }
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

    @Override
    public void onItemClick(int position) {
        KokuvaUser k = KokuvaApp.getInstance().getChat(position).getUserTo();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(k.getUid());
        swapFragment(fragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"----RoomActivity: OnStart");
        //addAndShow(FragmentRoom.getInstance(),"room");
        setupListen();
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG,"----RoomActivity: OnResume");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG,"----RoomActivity: OnPause");
    }

    @Override
    public void onClickUser(KokuvaUser u) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(u.getUid());
        if(fragment==null){
            creatChat(u);
        }
        else{
            swapFragment(fragment);
        }
    }
}



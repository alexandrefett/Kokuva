package com.kokuva;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
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
import com.kokuva.adapter.MyAdapter;
import com.kokuva.dialogs.ChatsDialog;
import com.kokuva.dialogs.DistanceDialog;
import com.kokuva.model.Chat;
import com.kokuva.model.KokuvaUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RoomActivity extends BaseActivity implements ChatsDialog.ChatDialogListener, FragmentRoom.OnClickUserListener {

    private DatabaseReference myRef;
    private KokuvaUser user;
    private Toolbar toolbar;
    private ChildEventListener listenMyChats;
    private MyAdapter adapter;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myRef = FirebaseDatabase.getInstance().getReference();
        user = KokuvaApp.getInstance().getUser();
        adapter = new MyAdapter(getSupportFragmentManager());
        pager = (ViewPager)findViewById(R.id.pager);
    }

    private void setupListen(){
        listenMyChats = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat c  = dataSnapshot.getValue(Chat.class);
                alertNewChat(c);
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
        Log.d(TAG,"----RoomActivity: createChat");
        String chatId = myRef.child("chats").push().getKey();
        Chat c1 = new Chat(chatId, user);
        final Chat c2 = new Chat(chatId, item);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("chats/" + user.getUid() + "/" + item.getUid(), c2);
        data.put("chats/" + item.getUid() + "/" + user.getUid(), c1);

        myRef.updateChildren(data);
    }

    public void createFragmentChat(Chat c){
        Log.d(TAG,"----RoomActivity: createFragmentChat");

        Bundle b = new Bundle();
        b.putString("chatId", c.getChatId());
        FragmentChat fragment = new FragmentChat();
        fragment.setArguments(b);

        KokuvaApp.getInstance().addChat(c);
        addAndShow(fragment, c.getChatId());
    }

    public void addAndShow(Fragment f, String tag){
        adapter.addFragment(f);
        pager.setCurrentItem(adapter.getCount()-1, false);
    }

    public void swapFragment(Chat c){
        Log.d(TAG,"----RoomActivity: SwapFragment: "+pager.getCurrentItem());
        int i = KokuvaApp.getInstance().getChat(c);
        pager.setCurrentItem(i, false);

    }
    private void setupFragment(){
        pager.setAdapter(adapter);
        adapter.addFragment(FragmentRoom.getInstance());
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
//                swapFragment(getSupportFragmentManager().findFragmentByTag("room"));
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

    private void setDistance(){
        DialogFragment newFragment = new DistanceDialog();
        newFragment.show(getSupportFragmentManager(), "avatar");
    }


    @Override
    public void onItemClick(int position) {
        Chat c = KokuvaApp.getInstance().getChat(position);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(c.getChatId());
 //       swapFragment(fragment);
    }

    @Override
    public void onBackPressed(){
        Log.d(TAG,"----RoomActivity: backpressed: "+pager.getCurrentItem());
        if(pager.getCurrentItem()==0){
            myRef.child("users").child(user.getUid()).child("lat").removeValue();
            myRef.child("users").child(user.getUid()).child("log").removeValue();
            myRef.child("chats").child(user.getUid()).removeValue();
            myRef.removeEventListener(listenMyChats);
            super.onBackPressed();
            this.finish();
        }
        else{
            pager.setCurrentItem(0, false);
        }
        //Fragment frg = getSupportFragmentManager().findFragmentById(R.id.frg_content);
        //Log.d(TAG,"----RoomActivity: backpressed "+frg.getTag());
/*        if(getSupportFragmentManager().findFragmentByTag("room").isVisible()){
            myRef.child("users").child(user.getUid()).child("lat").removeValue();
            myRef.child("users").child(user.getUid()).child("log").removeValue();
            myRef.child("chats").child(user.getUid()).removeValue();
            myRef.removeEventListener(listenMyChats);
            for (Fragment f : getSupportFragmentManager().getFragments()) {
                Log.d(TAG,"----RoomActivity: removing "+f.getTag());
                if (!f.getTag().equals("room")) {
                    Fragment frag = getSupportFragmentManager().findFragmentByTag(f.getTag());
                    getSupportFragmentManager().beginTransaction().remove(frag).commit();
                }
            }
            super.onBackPressed();
        }
        else{
            //Fragment room = getSupportFragmentManager().findFragmentByTag("room");
            //swapFragment(room);
            pager.setCurrentItem(0);
        }
        */
    }

     @Override
    public void onPostResume(){
        super.onPostResume();
        setupFragment();
    }

    @Override
    public void onClickUser(KokuvaUser u) {
        Chat c = KokuvaApp.getInstance().getUserChat(u.getUid());
        if(c==null){
            creatChat(u);
        }
        else{
            //Fragment fragment = getSupportFragmentManager().findFragmentByTag(c.getChatId());
            swapFragment(c);
        }
    }

    private void alertNewChat(final Chat c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(c.getUserTo().getNick() + " deseja conversar com você. Você aceita?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        createFragmentChat(c);
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.create();
    }
}



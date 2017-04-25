package com.kokuva;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kokuva.adapter.ChatAdapter;
import com.kokuva.model.Chat;
import com.kokuva.model.KokuvaUser;

import java.util.ArrayList;

public class FragmentUsers extends BaseFragment {
    private DatabaseReference myRef;
    private KokuvaUser user;
    private ChatAdapter chatAdapter;

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

        RecyclerView list_users = (RecyclerView)view.findViewById(R.id.users_list);

        list_users.setLayoutManager(new LinearLayoutManager(getContext()));
        chatAdapter = new ChatAdapter(getContext(), new ArrayList<Chat>());
        list_users.setAdapter(chatAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseUserOnline();
    }

    private void firebaseUserOnline(){
        Log.d(TAG,"start query listener");
        myRef.child("chats").child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG,"chats");
                Log.d(TAG,"String: "+s);
                Log.d(TAG,"dataSnap: "+dataSnapshot.toString());
                chatAdapter.addItem(dataSnapshot.getValue(Chat.class));
                KokuvaApp.getInstance().getChats().add(dataSnapshot.getValue(Chat.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG,"onChildChanged: ");
                Log.d(TAG,"String: "+s);
                Log.d(TAG,"dataSnap: "+dataSnapshot.toString());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG,"onChildRemoved: ");
                Log.d(TAG,"dataSnap: "+dataSnapshot.toString());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG,"onChildMoved: ");
                Log.d(TAG,"String: "+s);
                Log.d(TAG,"dataSnap: "+dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {  }
        });
    }
}
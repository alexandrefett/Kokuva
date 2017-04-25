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
        chatAdapter = new ChatAdapter(getContext(), new ArrayList<KokuvaUser>());
        list_users.setAdapter(chatAdapter);

        firebaseUserOnline();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void firebaseUserOnline(){
        Query query = myRef.child("users").child(user.getUid()).child("chats");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG,"chats");
                Log.d(TAG,"String: "+s);
                Log.d(TAG,"dataSnap: "+dataSnapshot.toString());
                String uid = dataSnapshot.getValue(String.class);
                myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        chatAdapter.addItem(dataSnapshot.getValue(KokuvaUser.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {   }
                });
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
package com.kokuva;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
import com.kokuva.adapter.FirebaseChatsAdapter;
import com.kokuva.adapter.FirebaseUsersAdapter;
import com.kokuva.model.Chat;
import com.kokuva.model.KokuvaUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FragmentUsers extends BaseFragment {
    private DatabaseReference myRef;
    private KokuvaUser user;
    private static FragmentUsers ourInstance;
    private RecyclerView chats_list;

    public static FragmentUsers getInstance(String value) {
        if (ourInstance == null) {
            ourInstance = new FragmentUsers();
            if (value != null) {
                Bundle args = new Bundle();
                args.putString("value", value);
                ourInstance.setArguments(args);
            }
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

        myRef = FirebaseDatabase.getInstance().getReference();
        user = KokuvaApp.getInstance().getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_users, container, false);
        chats_list = (RecyclerView)view.findViewById(R.id.chats_list);
        listenMyChats();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void listenMyChats(){
        ArrayList<Chat> users = new ArrayList<Chat>();
        ArrayList<String> usersKeys = new ArrayList<String>();

        Query query = myRef.child("chats").child(user.getUid());

        FirebaseChatsAdapter userAdapter = new FirebaseChatsAdapter(query, Chat.class, users, usersKeys);
        userAdapter.setContext(getContext());
        userAdapter.addOnClickItemListener(new FirebaseChatsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Chat item) {

            }
        });
        RecyclerView.LayoutManager lm = new GridLayoutManager(getContext(),2);
        chats_list.setAdapter(userAdapter);
        chats_list.setLayoutManager(lm);

    }
}
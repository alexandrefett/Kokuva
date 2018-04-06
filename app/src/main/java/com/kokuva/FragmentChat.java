package com.kokuva;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FragmentChat extends BaseFragment {

    private String roomId;
    private static int REQUEST_PERMISSIONS = 3;

    private static FragmentChat ourInstance;

    public static FragmentChat getInstance() {
        if (ourInstance == null) {
            ourInstance = new FragmentChat();
        }
        return ourInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args!=null) {
            roomId = args.getString("roomId");
            final CollectionReference sRoomsCollection =
                    FirebaseFirestore.getInstance().collection("rooms/");
            final Query sRoomQuery = sRoomsCollection.whereEqualTo("id", roomId);

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

}
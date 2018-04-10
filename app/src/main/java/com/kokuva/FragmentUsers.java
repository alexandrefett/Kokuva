package com.kokuva;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FragmentUsers extends BaseFragment {
    private static FragmentUsers ourInstance;
    private String id;
    private RecyclerView users;
    private CollectionReference sRoomsCollection;
    private Query sRoomQuery;

    public static FragmentUsers getInstance(String value) {
        if (ourInstance == null) {
            ourInstance = new FragmentUsers();
            if (value != null) {
                Bundle args = new Bundle();
                args.putString("id", value);
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
            id = args.getString("id", "");
            sRoomsCollection = FirebaseFirestore.getInstance().collection("rooms");
            sRoomQuery = sRoomsCollection.whereEqualTo("id", id);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_users, container, false);

        return view;
    }

}
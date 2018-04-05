package com.kokuva;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentUsers extends BaseFragment {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_users, container, false);
        return view;
    }

}
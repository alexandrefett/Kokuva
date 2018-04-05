package com.kokuva;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentRoom extends BaseFragment {

    private RecyclerView list_users;
    private static FragmentRoom ourInstance;

    public static FragmentRoom getInstance() {
        if (ourInstance == null) {
            ourInstance = new FragmentRoom();
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
        View view =  inflater.inflate(R.layout.fragment_room, container, false);

        list_users = (RecyclerView)view.findViewById(R.id.room_users_list);

        return view;
    }



    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"----FragmentRoom: OnStart");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG,"----FragmentRoom: OnResume");

    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG,"----FragmentRoom: OnPause");

    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG,"----FragmentRoom: OnStop");

    }
}
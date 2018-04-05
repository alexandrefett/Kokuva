package com.kokuva;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentChat extends BaseFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args!=null) {
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_message, container, false);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"----FragmentChat: OnStart");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG,"----FragmentChat: OnResume");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG,"----FragmentChat: OnPause");

    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG,"----FragmentChat: OnStop");
    }

}
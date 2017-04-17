package com.kokuva;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kokuva.model.KokuvaUser;
import com.kokuva.views.UserTabView;

public class FragmentRoom extends BaseFragment {
    private DatabaseReference myRef;
    private FirebaseUser user;
    private TabHost tabHost;

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
        View view =  inflater.inflate(R.layout.fragment_message, container, false);
        tabHost = (TabHost)view.findViewById(R.id.tabUsers);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
package com.kokuva;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.kokuva.model.Message;

public class FragmentChat extends BaseFragment {

    private DatabaseReference myRef;
    private FirebaseUser user;
    private String chatUid;
    private String toUid;
    private EditText text_msg;
    private ImageButton button_send;
    private LinearLayout scroll_messages;

    public static FragmentChat newInstance(String value) {
        FragmentChat myFragment = new FragmentChat();

        if(value!=null) {
            Bundle args = new Bundle();
            args.putString("value", value);
            myFragment.setArguments(args);
        }
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args!=null) {
            chatUid = args.getString("chatUid", "");
            toUid = args.getString("toUid", "");
        }

        myRef = FirebaseDatabase.getInstance().getReference();
        user = KokuvaApp.getInstance().getUser();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_message, container, false);
        text_msg = (EditText)view.findViewById(R.id.text_message);
        button_send = (ImageButton)view.findViewById(R.id.button_send);
        scroll_messages = (LinearLayout)view.findViewById(R.id.scroll_messages);

        myRef.child("chat").child(chatUid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                TextView msg = new TextView(getContext());
                msg.setText(message.getMessage());
                if(message.getSender().equals(user.getUid())){
                    msg.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                }
                else {
                    msg.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                }
                scroll_messages.addView(msg);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
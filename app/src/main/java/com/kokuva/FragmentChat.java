package com.kokuva;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.kokuva.model.KokuvaUser;
import com.kokuva.model.Message;
import com.kokuva.views.MessageView;
import com.kokuva.views.ReceiverMsgView;
import com.kokuva.views.SenderMsgView;

public class FragmentChat extends BaseFragment {

    private DatabaseReference myRef;
    private KokuvaUser user;
    private String chatid;
    private String userid;
    private String nick;
    private EditText text_msg;
    private ImageButton button_send;
    private LinearLayout scroll_messages;

    private void viewMessage(Message m){
        MessageView t;
        if(m.getSender().equals(user.getUid())){
            t = new MessageView(getContext(), R.drawable.sender_msg_layout, Gravity.RIGHT);
        }
        else {
            t = new MessageView(getContext(), R.drawable.receiver_msg_layout, Gravity.LEFT);
        }
        t.setText(m.getMessage());
        scroll_messages.addView(t);
        t.requestFocus();
        text_msg.requestFocus();
    }

    private void listenMessages(){
        myRef.child("msgs").child(chatid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message msg = dataSnapshot.getValue(Message.class);
                viewMessage(msg);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }

    private void listenActive(){
        myRef.child("chats").child(userid).child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Toast.makeText(getContext(),nick+" não está mais na conversa.",Toast.LENGTH_LONG).show();
                text_msg.setEnabled(false);
                button_send.setEnabled(false);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {           }

            @Override
            public void onCancelled(DatabaseError databaseError) {         }
        });

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args!=null) {
            chatid = args.getString("chatid", "");
            userid = args.getString("userid", "");
            nick = args.getString("nick", "");
        }

        myRef = FirebaseDatabase.getInstance().getReference();
        user = KokuvaApp.getInstance().getUser();
    }

    private void sendMsg(String msg){
        Message m = new Message(msg, user.getUid());
        myRef.child("msgs").child(chatid).push().setValue(m);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_message, container, false);
        text_msg = (EditText)view.findViewById(R.id.text_message);
        button_send = (ImageButton)view.findViewById(R.id.button_send);
        scroll_messages = (LinearLayout)view.findViewById(R.id.scroll_messages);
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String t = text_msg.getText().toString().trim();
                if(t.length()>0){
                    sendMsg(t);
                    text_msg.setText("");
                }
            }
        });
        listenMessages();
        listenActive();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
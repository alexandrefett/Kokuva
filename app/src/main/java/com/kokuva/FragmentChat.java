package com.kokuva;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import com.kokuva.model.Chat;
import com.kokuva.model.KokuvaUser;
import com.kokuva.model.Message;
import com.kokuva.views.MessageView;
import com.kokuva.views.ReceiverMsgView;
import com.kokuva.views.SenderMsgView;

public class FragmentChat extends BaseFragment {

    private DatabaseReference myRef;
    private KokuvaUser user;
    private EditText text_msg;
    private ImageButton button_send;
    private LinearLayout scroll_messages;
    private ChildEventListener listenMessages;
    private ChildEventListener listenActive;
    private Chat chat;

    private void viewMessage(Message m){

        TextView t;
        if(m.getSender().equals(user.getUid())){
            t = (TextView)getActivity().getLayoutInflater().inflate(R.layout.text_sender, scroll_messages, false);
        }
        else {
            t = (TextView)getActivity().getLayoutInflater().inflate(R.layout.text_receiver, scroll_messages, false);
        }
        t.setText(m.getMessage());
        scroll_messages.addView(t);
        t.requestFocus();
        text_msg.requestFocus();
    }

    public void setChat(Chat c){
        this.chat = c;
    }

    private void listenMessages(){
        listenMessages = new ChildEventListener() {
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
        };

    }

    private void listenActive(){
        listenActive = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                endChatDialog(chat.getUserTo().getNick());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {           }

            @Override
            public void onCancelled(DatabaseError databaseError) {         }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args!=null) {
        }

        myRef = FirebaseDatabase.getInstance().getReference();
        user = KokuvaApp.getInstance().getUser();
        listenMessages();
        listenActive();

    }

    private void sendMsg(String msg){
        Message m = new Message(msg, user.getUid());
        myRef.child("msgs").child(chat.getChatId()).push().setValue(m);
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
        return view;
    }

    private void endChatDialog(final String n){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(n+" saiu da conversa.")
            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    text_msg.setEnabled(false);
                    button_send.setEnabled(false);
                }
            });
        builder.setTitle("Mensagem");
        builder.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"----FragmentChat: OnStart");
        myRef.child("msgs").child(chat.getChatId()).addChildEventListener(listenMessages);
        myRef.child("chats").child(chat.getUserTo().getUid()).child(user.getUid()).addChildEventListener(listenActive);
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

    private void exitListen(){
        myRef.removeEventListener(listenActive);
        myRef.removeEventListener(listenMessages);
    }
}
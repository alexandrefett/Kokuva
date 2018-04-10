package com.kokuva;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;
import com.kokuva.dialogs.ExitChatDialog;
import com.kokuva.model.AbstractRoom;
import java.util.Hashtable;
import java.util.Map;


public class ChatActivity extends BaseActivity implements ExitChatDialog.NoticeDialogListener {

    private DrawerLayout mDrawerLayout;
    private static final String TAG = "---------ChatActivity";

    private String id="";
    private String nickname;

    private CollectionReference sUserCollection;
    private Query sUserQuery;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            id = extras.getString("id","");
            nickname = extras.getString("nickname");

            Log.d(TAG, "id:"+id);
            Log.d(TAG, "nickname: "+nickname);

            sUserCollection = FirebaseFirestore.getInstance().collection("users");
            sUserQuery = sUserCollection.orderBy("name");

            enterChat(id);
        }
    }

    public void enterChat(String roomid){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference sfDocRef = db.collection("rooms").document(roomid);

        db.runTransaction(new Transaction.Function<Double>() {
            @Override
            public Double apply(Transaction transaction) throws FirebaseFirestoreException {

                DocumentSnapshot snapshot = transaction.get(sfDocRef);
                double participants = snapshot.getDouble("participants") + 1;
                if (participants < 50) {
                    transaction.update(sfDocRef, "participants", participants);
                    onAddUser(nickname, getUid(), id);
                    return participants;
                } else {
                    throw new FirebaseFirestoreException("Population too high",
                            FirebaseFirestoreException.Code.ABORTED);
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<Double>() {
            @Override
            public void onSuccess(Double result) {
                Log.d(TAG, "Transaction success: " + result);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
            }
        });
    }

    public void exitChat(String roomid){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference sfDocRef = db.document("rooms/"+roomid);

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef);
                double participants = snapshot.getDouble("participants") - 1;
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"----Main Activity: OnStart");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG,"----Main Activity: OnResume");

    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG,"----Main Activity: OnPause");

    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG,"----Main Activity: OnStop");

    }

    @Override
    public void onDialogPositiveClick(AbstractRoom room) {
        Toast.makeText(this, room.getName(),Toast.LENGTH_LONG).show();
    }

    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    protected void onAddUser(String nickname, String uid, String id) {
        CollectionReference sUserCollection = FirebaseFirestore.getInstance()
                .collection("users");

        Map<String,String> map = new Hashtable<String,String>();
        map.put(nickname, uid);
        sUserCollection.document(id).set(map).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to write message", e);
            }
        });
    }

    protected void onExitUser(String nickname) {
        sUserCollection.document(nickname).delete().addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to write message", e);
            }
        });
    }
}
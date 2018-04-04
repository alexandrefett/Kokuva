package com.kokuva;

import android.Manifest;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends BaseActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private static int REQUEST_IMAGE_CAPTURE = 1;
    private static int REQUEST_IMAGE_GALLERY = 2;
    private static int REQUEST_PERMISSIONS = 3;
    private  FirebaseFirestore db;
    private LinearLayout rooms;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        KokuvaApp.getInstance().setContext(this);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_main);
        rooms = (LinearLayout)findViewById(R.id.rooms_scroll);

        createRoomsButtons();
    }


    private void addUserMac(String mac, String uuid){
        Map<String, Object> user = new HashMap<>();
        user.put("uuid", uuid);
        user.put("mac", mac);
        user.put("date", Calendar.getInstance().getTimeInMillis());

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void createRoomsButtons(){
        db.collection("rooms")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

//        Button enter = (Button)findViewById(R.id.button_enter);
//        enter.setOnClickListener(this);
    }


    private Button addRoomButton(DocumentSnapshot doc){
        Button button = new Button(this);
        button.setText((String)doc.get("name"));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return button;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length >= 1){
                for(int p:grantResults){
                    Log.d(TAG, "Permission: "+p);
                }
            }
        }
    }

    private void getPermission(){
        Log.d(TAG, "GET_PERMISSIONS");
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
            {Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_WIFI_STATE},REQUEST_PERMISSIONS);
    }

    private String getMac(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        return wInfo.getMacAddress();
    }

    @Override
    public void onStart() {
        super.onStart();
        showDialog();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
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


    private void signInAnonymously() {
        showDialog();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser fuser = mAuth.getCurrentUser();
                            addUserMac(fuser.getUid(), getMac());
                            updateUI(fuser);
                        } else {
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(final FirebaseUser u){
        boolean isSignedIn = (u != null);
        if (isSignedIn) {
            createRoomsButtons();
        } else {
            signInAnonymously();
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
    }
}
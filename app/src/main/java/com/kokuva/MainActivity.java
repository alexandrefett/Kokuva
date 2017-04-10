package com.kokuva;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity {

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CircleImageView userphoto;
    private EditText nick;
    private Button enter;
    private String userUrl;
    private FirebaseUser user;
    private boolean updatePhoto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        KokuvaApp.getInstance().setContext(this);
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        setContentView(R.layout.activity_main);

        showDialog();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(TAG, "onAuthStateChanged");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getEmail());
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getDisplayName());
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getPhotoUrl());
                    KokuvaApp.getInstance().setUser(user);
                    hideDialog();
                    fillFragment();
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    loginUser();
                }
            }
        };

        nick = (EditText)findViewById(R.id.textnick);
        enter = (Button)findViewById(R.id.buttonenter);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(nick.getText().toString().trim(), userUrl);
                //enter browse users
            }
        });

        userphoto = (CircleImageView)findViewById(R.id.userphoto);
        userphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu();
            }
        });

/*        if (mAuth.getCurrentUser() == null) {
            loginUser();
        } else {
            getUserFromFirebase(mAuth.getCurrentUser().getUid());
        }
        */
    }
    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void loginUser(){
        Log.d(TAG, "loginUser");
        TelephonyManager t = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String user_phone = t.getLine1Number();

        Pattern email = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(this).getAccounts();
        String user_email = null;
        for (Account account : accounts) {
            if (email.matcher(account.name).matches()) {
                user_email = account.name;
            }
        }

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String user_mac = wInfo.getMacAddress();

        Log.d(TAG, "loginUser: " + user_email);
        Log.d(TAG, "loginUser: " + user_mac);

        userLogin(user_email, user_mac);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void userLogin(final String email, final String pswd){
        mAuth.signInWithEmailAndPassword(email, pswd)
            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Log.d(TAG, "onSuccess Login Email:" + authResult.getUser().getUid());
                    hideDialog();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof FirebaseAuthException) {
                        FirebaseAuthException ex = (FirebaseAuthException) e;
                        Log.d(TAG, "onFailure Login:" + ex.getErrorCode());
                        createAccount(email, pswd);

                    }
                    Log.d(TAG, "onFailure Login:" + e.getMessage());
                }
            });
    }

    private void createAccount(final String email, final String pswd) {
        Log.d(TAG, "createAccount");
        mAuth.createUserWithEmailAndPassword(email, pswd)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "OnComplete:");
                }
            })
            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Log.d(TAG, "OnSuccessListener:" + authResult.getUser().getUid());
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof FirebaseAuthException) {
                        FirebaseAuthException ex = (FirebaseAuthException) e;
                        Log.d(TAG, "onFailure CreateAccount:" + ex.getErrorCode());
                        if(ex.getErrorCode().equals("ERROR_EMAIL_ALREADY_IN_USE")){
                            // alert email
                        }
                    }
                    Log.d(TAG, "onFailure:" + e.getMessage());
                }
            });
    }

    private void fillFragment(){
        user = KokuvaApp.getInstance().getUser();
        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(userphoto);
        nick.setText(user.getDisplayName());
    }

    private void showMenu(){
        AlertDialog menu;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.photo_options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0:
                        break;
                    case 1:
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, 1);
                        break;
                }
                // The 'which' argument contains the index position
                // of the selected item
            }
        });
        menu = builder.create();
        menu.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    String path = getRealPathFromURI(this, selectedImageUri);
                    File imgFile = new File(path);
                    if (imgFile.exists()) {
                        userUrl = imgFile.getAbsolutePath();
                        updatePhoto = true;
                        Log.d(TAG,"userUrl:"+userUrl);
                    }
                }
            }
        }
    }

    private void updateProfile(final String n, final String p){
        showDialog();

        if(p!=null) {
            Bitmap bmp = BitmapFactory.decodeFile(p);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();

            StorageReference imagesRef = storageRef.child("users/" + user.getUid() + ".jpg");

            UploadTask uploadTask = imagesRef.putBytes(b);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    hideDialog();
                    //alert error
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") String url = taskSnapshot.getDownloadUrl().toString();
                    updateUser(n, url);
                }
            });
        }
        else{
            updateUser(n, p);
        }
    }

    private void updateUser(final String n, final String p){

        FirebaseUser user = KokuvaApp.getInstance().getUser();

        UserProfileChangeRequest profileUpdates;
        if(!updatePhoto){
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(n)
                    .build();
        }
        else {
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(n)
                    .setPhotoUri(Uri.parse(p))
                    .build();
        }

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            // goto browser user online
                        }
                        hideDialog();
                    }
                });

    }
}
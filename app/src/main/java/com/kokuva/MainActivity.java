package com.kokuva;

import android.*;
import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kokuva.model.KokuvaUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CircleImageView userphoto;
    private EditText nick;
    private Button enter;
    private FirebaseUser fbuser;
    private KokuvaUser user;
    private TextView grey;
    private TextView green;
    private TextView black;
    private TextView blue;
    private TextView red;
    private TextView lightred;
    private TextView purple;
    private TextView orange;
    private static int REQUEST_IMAGE_CAPTURE = 1;
    private static int REQUEST_IMAGE_GALLERY = 2;
    private static int REQUEST_PERMISSIONS = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        KokuvaApp.getInstance().setContext(this);
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        setContentView(R.layout.activity_main);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(TAG, "onAuthStateChanged");
                fbuser = firebaseAuth.getCurrentUser();
                if (fbuser != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + fbuser.getUid());
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + fbuser.getEmail());
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + fbuser.getDisplayName());
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + fbuser.getPhotoUrl());
                    getKokuvaUser(fbuser.getUid());
                } else {
                    showDialog();
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    loginUser();
                }
            }
        };

        nick = (EditText)findViewById(R.id.textnick);
        enter = (Button)findViewById(R.id.button_enter);
        enter.setOnClickListener(this);

        userphoto = (CircleImageView)findViewById(R.id.userphoto);
        userphoto.setOnClickListener(this);
        grey = (TextView)findViewById(R.id.grey);
        green = (TextView)findViewById(R.id.green);
        blue = (TextView)findViewById(R.id.blue);
        black = (TextView)findViewById(R.id.black);
        purple = (TextView)findViewById(R.id.purple);
        red = (TextView)findViewById(R.id.red);
        lightred = (TextView)findViewById(R.id.lightred);
        orange = (TextView)findViewById(R.id.orange);
    }

    private void getKokuvaUser(String uid){
        myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                KokuvaApp.getInstance().setUser(dataSnapshot.getValue(KokuvaUser.class));
                user = KokuvaApp.getInstance().getUser();
                fillViews();
                hideDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {

            // Received permission result for camera permission.est.");
            // Check if the only required permission has been granted
            if (grantResults.length >= 1){
                for(int p:grantResults){
                    Log.d(TAG, "Permission: "+p);
                }
                loginUser();
            }
        }
    }

    private void getPermission(){
        Log.d(TAG, "GET_PERMISSIONS");
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
            {Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.GET_ACCOUNTS,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_WIFI_STATE},REQUEST_PERMISSIONS);
    }

    private void loginUser(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED  ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            getPermission();
            Log.d(TAG, "READ_PHONE_STATE");
        }
        else {

            Log.d(TAG, "loginUser");
            TelephonyManager t = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//        String user_phone = t.getLine1Number();

            Pattern email = Patterns.EMAIL_ADDRESS;
            Account[] accounts = AccountManager.get(getApplicationContext()).getAccounts();
            String user_email = null;
            for (Account account : accounts) {
                Log.d(TAG, "account: " + account.name);
                Log.d(TAG, "account: " + account.type);
                if (email.matcher(account.name).matches()) {
                    user_email = account.name;
                }
            }

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wInfo = wifiManager.getConnectionInfo();
            String user_mac = wInfo.getMacAddress();

            Log.d(TAG, "loginUser: " + user_email);
            Log.d(TAG, "loginUser: " + user_mac);
            //user_email = "alexandrefett@everst.com.br";
            //user_mac = "00:00:00:00:00:00:00:00";
            userLogin(user_email, user_mac);
        }
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

    private void fillViews(){
        if(user.getUrl()!=null && !user.getUrl().equals("")) {
            Glide.with(this)
                    .load(fbuser.getPhotoUrl())
                    .into(userphoto);
        }
        if(fbuser.getDisplayName()!=null && !fbuser.getDisplayName().equals("")) {
            nick.setText(fbuser.getDisplayName());
            nick.setTextColor(user.getColor());
        }
    }

    private void getImageDialog(){
        AlertDialog menu;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.photo_options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0:
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        }
                        break;
                    case 1:
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);
                        break;
                }

            }
        });
        menu = builder.create();
        menu.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    String path = Utils.getRealPathFromURI(this, selectedImageUri);
                    File imgFile = new File(path);
                    if (imgFile.exists()) {
                        uploadPhoto(Utils.pathToBitmap(imgFile.getAbsolutePath()));
                    }
                }
            }
            if(requestCode==REQUEST_IMAGE_CAPTURE){
                showDialog();
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                uploadPhoto(imageBitmap);
            }
        }
    }

    private void updateNick(final String n){
        myRef.child("users").child(user.getUid()).child("nick").setValue(n).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "User profile updated.");
                Intent intent = new Intent(getBaseContext(), RoomActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void uploadPhoto(final Bitmap bmp){
        showDialog();
        byte[] b = Utils.bitmapToByteArray(bmp);

        StorageReference imagesRef = storageRef.child("users/" + user.getUid() + ".jpg");

        UploadTask uploadTask = imagesRef.putBytes(b);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                hideDialog();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") String url = taskSnapshot.getDownloadUrl().toString();
                user.setUrl(url);
                updateUserUrl(url);
            }
        });
    }

    private void updateUserUrl(String url){
        myRef.child("users").child(user.getUid()).child("url").setValue(url).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "User profile updated.");
                Glide.with(getBaseContext())
                        .load(user.getUrl())
                        .into(userphoto);
                hideDialog();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id){
            case R.id.grey:
                setColorText(view);
                break;
            case R.id.black:
                setColorText(view);
                break;
            case R.id.blue:
                setColorText(view);
                break;
            case R.id.red:
                setColorText(view);
                break;
            case R.id.lightred:
                setColorText(view);
                break;
            case R.id.green:
                setColorText(view);
                break;
            case R.id.purple:
                setColorText(view);
                break;
            case R.id.button_enter:
                updateNick(nick.getText().toString().trim());
                break;
            case R.id.user_photo:
                getImageDialog();
                break;
        }
    }

    private void setColorText(View v){
        Paint p =((PaintDrawable) v.getBackground()).getPaint();
        nick.setTextColor(p.getColor());
    }
}
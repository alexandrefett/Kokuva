package com.kokuva;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kokuva.adapter.ColorAdapter;
import com.kokuva.dialogs.AvatarDialog;
import com.kokuva.model.KokuvaUser;
import java.io.File;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements View.OnClickListener, AvatarDialog.AvatarDialogListener{

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private CircleImageView userphoto;
    private EditText nick;
    private Button enter;
    private KokuvaUser user;
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

        nick = (EditText)findViewById(R.id.textnick);
        enter = (Button)findViewById(R.id.button_enter);
        enter.setOnClickListener(this);

        userphoto = (CircleImageView)findViewById(R.id.user_photo);
        userphoto.setOnClickListener(this);

        GridView g = (GridView)findViewById(R.id.grid_color);
        g.setAdapter(new ColorAdapter(this));
        g.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ColorAdapter colorAdapter = (ColorAdapter)adapterView.getAdapter();
                int color = colorAdapter.getItem(i);
                setColorText(color);
            }
        });
    }
    private void chooseAvatar() {
        DialogFragment newFragment = new AvatarDialog();
        newFragment.show(getSupportFragmentManager(), "avatar");
    }

    private void getCurrentUser(final String uid){
        myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = new KokuvaUser();

                if(dataSnapshot.getValue()!=null) {
                    Log.d(TAG,"dataSanpshot: "+dataSnapshot.toString());
                    user = dataSnapshot.getValue(KokuvaUser.class);
                }
                else {
                    user = new KokuvaUser();
                    user.setUid(uid);
                    user.setPhoto(false);
                    user.setColor(Color.BLACK);
                    user.setUrl("user_14");
                    user.setEmail(getEmail());
                    user.setMac(getMac());
                    user.setDist(1);
                }
                KokuvaApp.getInstance().setUser(user);
                fillViews();
                hideDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideDialog();
                Toast.makeText(MainActivity.this, "Error: "+databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
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
                    Manifest.permission.GET_ACCOUNTS,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_WIFI_STATE},REQUEST_PERMISSIONS);
    }

    private String getEmail(){
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
        return user_email;
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
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser fuser = mAuth.getCurrentUser();
                            updateUI(fuser);
                        } else {
                            // If sign in fails, display a message to the user.
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
            getCurrentUser(u.getUid());
        } else {
            signInAnonymously();
        }
    }

    private void fillViews(){
        if(user.isPhoto()) {
            Glide.with(this)
                    .load(user.getUrl())
                    .into(userphoto);
        }
        else {
            userphoto.setImageResource(getResources().getIdentifier(user.getUrl(), "drawable", getPackageName()));
        }
        if(user.getNick()!=null && !user.getNick().equals("")) {
            nick.setText(user.getNick());
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

    private void updateUser(){
        String nickName = nick.getText().toString();

        if(TextUtils.isEmpty(nickName)) {
            nick.setError("Preencha um apelido.");
            return;
        } else {
            user.setNick(nickName);
            myRef.child("users").child(user.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "User profile updated.");
                    Intent intent = new Intent(getBaseContext(), RoomActivity.class);
                    startActivityForResult(intent, 1);
                }
            });
        }
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
                user.setPhoto(true);
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id){
            case R.id.button_enter:
                updateUser();
                break;
            case R.id.user_photo:
                chooseAvatar();
                break;
        }
    }

    private void setColorText(int color){
        nick.setTextColor(color);
        user.setColor(color);
    }

    @Override
    public void onAvatarClick(String icon) {
        if(icon.equals("add")){
            getImageDialog();
        }
        else {
            user.setUrl(icon);
            user.setPhoto(false);
            userphoto.setImageResource(getResources().getIdentifier(icon, "drawable", getPackageName()));
        }
    }
}
package com.kokuva;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kokuva.model.User;
import java.io.ByteArrayOutputStream;
import java.io.File;
import de.hdodenhof.circleimageview.CircleImageView;
import static android.content.Context.LOCATION_SERVICE;

public class FragmentMain extends BaseFragment {

    private DatabaseReference myRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private CircleImageView userphoto;
    private User user;
    private EditText nick;
    private Button enter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args!=null) {
            //category = args.getString("category", "");
        }

        myRef = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        user = KokuvaApp.getInstance().getUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_login, container, false);

        nick = (EditText)view.findViewById(R.id.textnick);
        enter = (Button)view.findViewById(R.id.buttonenter);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNick(nick.getText().toString().trim());
                //enter browse users
            }
        });

        userphoto = (CircleImageView)view.findViewById(R.id.userphoto);
/*        userphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(KokuvaApp.getInstance().getUser().getImagefile()==null){
                    getImagePicker();
                }
                else {
                    showMenu();
                }
            }
        });
        */
        return view;
    }

    private void browseUsers(){
        if(nick.getText().toString().trim().equals("")){
            nick.setError("Digite um apelido.");
        }
        else{
            ((MainActivity)getActivity()).browseUsers();
            // busca usuario que ja existe
        }
    }
    private void showMenu(){

    }

    private void saveNick(String n){
        if(n.equals("")){
            nick.setError("Digite um apelido.");
        }
        else{
github            KokuvaApp.getInstance().getUser().setNick(n);
            myRef.child("users").child(KokuvaApp.getInstance().getUser().getUid()).child("nick").setValue(n).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    ((MainActivity)getActivity()).browseUsers();
                }
            });
        }
    }

    private void getImagePicker(){
        Intent photoPickerIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    private void saveUser(){
        myRef.child("users/"+user.getUid()+"/imagefile").setValue(user.getImagefile()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                browseUsers();
            }
        });
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

    private void saveImage(String path, final int type){ //type 1=flyers 3=banner
        showDialog();

        Bitmap bmp = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        StorageReference imagesRef = storageRef.child("users/"+System.currentTimeMillis()+".jpg");

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
                KokuvaApp.getInstance().getUser().setImagefile(url);
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //p.getFlyers().add(downloadUrl.toString());
                saveUser();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    String path = getRealPathFromURI(getContext(), selectedImageUri);
                    File imgFile = new File(path);
                    if (imgFile.exists()) {
                        saveImage(imgFile.getAbsolutePath(), 1);
                    }
                }
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
    }
}
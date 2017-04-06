package com.kokuva;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;
import com.kokuva.adapter.PhotoPagerAdapter;
import com.kokuva.interfaces.OnBitmapListener;
import com.kokuva.model.Profile;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ProfileActivity extends BaseActivity implements OnClickListener{

    private static int ADD_PHOTO=100;
    private static int FACEBOOK=10;
    private static int GALLERY=20;
    private DatabaseReference myRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Profile profile;
    private PhotoPagerAdapter adapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private EditText about;
    private EasyFacebook mEasyFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        myRef = FirebaseDatabase.getInstance().getReference();
        mEasyFacebook = EasyFacebook.getInstance(this);

        profile = KokuvaApp.getInstance().getProfile();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        TextView name = (TextView)findViewById(R.id.username);
        name.setText(profile.getName()+", "+ profile.getAge());

        about = (EditText)findViewById(R.id.aboutme);
        about.setText(profile.getAboutme());

        setupPager();
    }


    @Override
    public void onStop() {
        super.onStop();
        updatephotos();
        updateAbout();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
            case R.id.add_photo:
                captureImage();
                return true;
            case R.id.del_photo:
                deletePhoto();
                return true;
            case R.id.tools:
                //
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deletePhoto(){
        new AlertDialog.Builder(this)
                .setMessage("Tem certeza que vai excluir esta foto?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        profile.deletePhoto(viewPager.getCurrentItem());
                        adapter.updateItems(profile.getArrayPhotos());
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_PHOTO) {
                String path = data.getStringExtra("path");
                if (GALLERY == data.getIntExtra("provider",0)) {
                    File imgFile = new File(path);
                    if (imgFile.exists()) {
                        showProgressDialog();
                        saveImage(imgFile.getAbsolutePath());
                    }
                }
                if (FACEBOOK == data.getIntExtra("provider",0)) {
                    String url = data.getStringExtra("path");
                    mEasyFacebook.getPhoto(url, new OnBitmapListener() {
                        @Override
                        public void onComplete(Bitmap bitmap) {
                            saveImage(bitmap);
                        }
                    });
                }
            }
        }
    }

    public Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }

    private void captureImage(){
        Intent intent = new Intent(this, PhotoActivity.class);
        startActivityForResult(intent, ADD_PHOTO);
    }

    public void saveImage(Bitmap bmp){
        Log.d(TAG, "salvando imagem ");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 85, baos);
        byte[] b = baos.toByteArray();

        StorageReference imagesRef = storageRef.child("images/users/"+ profile.getUserid()+"/"+System.currentTimeMillis()+".jpg");

        UploadTask uploadTask = imagesRef.putBytes(b);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "Uploadtask failed");
                hideProgressDialog();
            }
        }).addOnSuccessListener(new OnSuccessListener<TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                profile.addPhoto(downloadUrl.toString());
                adapter.updateItems(profile.getArrayPhotos());
                viewPager.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        viewPager.setCurrentItem(profile.getArrayPhotos().size(), true);
                    }
                }, 100);

                Log.d(TAG, "downloadUrl: "+downloadUrl.toString());
                hideProgressDialog();
            }
        });

    }

    public void saveImage(String path) {
        Bitmap bmp = BitmapFactory.decodeFile(path);
        bmp = scaleCenterCrop(bmp, 1080, 1080);
        saveImage(bmp);
    }

    private void setupPager(){
        adapter = new PhotoPagerAdapter(this, profile.getArrayPhotos());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }




    private void updatephotos(){
        profile.setAboutme(about.getText().toString());
        myRef.child("users/"+ profile.getUserid()+"/photos").setValue(profile.getPhotos()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "saveData: " + task.isSuccessful());
            }
        });
    }

    private void updateAbout(){
        myRef.child("users/"+ profile.getUserid()+"/aboutme").setValue(profile.getAboutme()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "saveAboutMe: " + task.isSuccessful());
            }
        });
    }


}
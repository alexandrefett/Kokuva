package com.kokuva;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static android.app.Activity.RESULT_OK;

public class FragmentProfile extends BaseFragment {
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args!=null) {
            //category = args.getString("category", "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        myRef = FirebaseDatabase.getInstance().getReference();
        mEasyFacebook = EasyFacebook.getInstance(getContext());

        profile = KokuvaApp.getInstance().getProfile();

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);

        TextView name = (TextView) view.findViewById(R.id.username);
        name.setText(profile.getName()+", "+ profile.getAge());

        about = (EditText) view.findViewById(R.id.aboutme);
        about.setText(profile.getAboutme());
        view.findViewById(R.id.imageView2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });
        setupPager();

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void getPhoto(String url, OnBitmapListener onBitmapListener){
        GetBitmap mTask = new GetBitmap();
        mTask.setBitmapListener(onBitmapListener);
        mTask.execute(url);
    }

    private class GetBitmap extends AsyncTask<String, Void, Bitmap> {
        OnBitmapListener onBitmapListener;
        public void setBitmapListener(OnBitmapListener onBitmapListener){
            this.onBitmapListener = onBitmapListener;
        }
        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL imageUrl = new URL(urls[0]);
                Bitmap bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                return bitmap;
            }catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            //Update UI
            onBitmapListener.onComplete(result);
        }
    }
    private void deletePhoto(){
        new AlertDialog.Builder(getContext())
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

    private void captureImage(){
        Intent intent = new Intent(getContext(), PhotoActivity.class);
        startActivityForResult(intent, ADD_PHOTO);
    }

    private void setupPager(){
        adapter = new PhotoPagerAdapter(getContext(), profile.getArrayPhotos());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
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
                        showDialog();
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

    public void saveImage(String path) {
        Bitmap bmp = BitmapFactory.decodeFile(path);
        bmp = scaleCenterCrop(bmp, 1080, 1080);
        saveImage(bmp);
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
                hideDialog();
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
                hideDialog();
            }
        });

    }
}
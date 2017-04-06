package com.kokuva;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequest.Callback;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.kokuva.interfaces.OnAlbumsListener;
import com.kokuva.interfaces.OnBitmapListener;
import com.kokuva.interfaces.OnLoginListener;
import com.kokuva.interfaces.OnPhotosListener;
import com.kokuva.model.AlbumFB;
import com.kokuva.model.PhotoFB;
import com.kokuva.model.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static com.facebook.GraphRequest.newGraphPathRequest;

/**
 * Created by Alexandre on 09/10/2016.
 */

public class EasyFacebook {


    private static EasyFacebook mInstance=null;
    private static int LARGE = 1;
    private static int SMALL = 2;

        private CallbackManager mCallbackManager;
    private Context context;

    public static EasyFacebook getInstance(Context context){
        if(mInstance==null){
            FacebookSdk.sdkInitialize(context);
            mInstance = new EasyFacebook(context);
        }
        return mInstance;
    }

    private EasyFacebook(Context context){
        this.context = context;
    }

    public void getAlbumPhotos(String id, OnPhotosListener onPhotosListener){
        final OnPhotosListener listener = onPhotosListener;
        GraphRequest request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                "/"+id+"/photos", new Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        Log.d("album photos", "response "+response.toString());
                        JSONArray o = null;
                        ArrayList<PhotoFB> array = new ArrayList<PhotoFB>();
                        try {
                            o = (JSONArray)response.getJSONObject().get("data");
                            int i = o.length();
                            for (int x = 0; x<i;x++){
                                JSONObject p = (JSONObject)o.get(x);
                                array.add(new PhotoFB(p.getString("id"), p.getString("source").replace("\\","")));
                                Log.d("photo", "id "+p.getString("id")+"  picture "+p.getString("source").replace("\\",""));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        listener.onComplete(array);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, picture.type(large), source");
        parameters.putBoolean("redirect", false);
        request.setParameters(parameters);
        request.executeAsync();

    }

    public void getMyAlbuns(OnAlbumsListener onAlbunsListener){
        final OnAlbumsListener listener = onAlbunsListener;
        final ArrayList<AlbumFB> array = new ArrayList<AlbumFB>();

        GraphRequest request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                "/me/albums", new Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        Log.d("Albums", "response "+response.toString());
                        JSONArray o = null;
                        try {

                            o = (JSONArray)response.getJSONObject().get("data");
                            int i = o.length();
                            for (int x = 0; x<i;x++){
                                JSONObject p = o.getJSONObject(x);
                                array.add(new AlbumFB(p.getString("id"),
                                        p.getJSONObject("picture").getJSONObject("data").getString("url").replace("\\","")));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        listener.onComplete(array);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, picture.type(album)");
        parameters.putBoolean("redirect", false);
        request.setParameters(parameters);
        request.executeAsync();

    }


    public void getMyUploadedPhotos(OnPhotosListener onPhotosListener){
        final OnPhotosListener listener = onPhotosListener;
        final ArrayList<PhotoFB> array = new ArrayList<PhotoFB>();

        GraphRequest request = newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                "me/photos", new Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        JSONArray o = null;
                        try {
                            o = (JSONArray)response.getJSONObject().get("data");
                            int i = o.length();
                            for (int x = 0; x<i;x++){
                                JSONObject p = (JSONObject)o.get(x);

                                array.add(new PhotoFB(p.getString("id"), p.getString("source").replace("\\","")));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        listener.onComplete(array);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,source");
        parameters.putString("type", "uploaded");
        parameters.putBoolean("redirect", false);
        request.setParameters(parameters);
        request.executeAsync();

    }

    public void loginFacebook(Activity activity, OnLoginListener onloginlistener){
        final OnLoginListener loginlistener = onloginlistener;

        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        getProfile(loginResult, loginlistener);
                    }

                    @Override
                    public void onCancel() {
                        loginlistener.onCancel();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        loginlistener.onError(exception);
                    }
                });
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile, user_birthday","user_friends,email"));


    }

    private void getProfile(final LoginResult result, OnLoginListener onloginlistener ){
        final OnLoginListener onLoginListener = onloginlistener;
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("Profile","data: "+object.toString());
                        try {
                            Profile profile = new Profile();
                            String url = object.getJSONObject("picture").getJSONObject("data").getString("url").replace("\\","");
                            String birthday = object.getString("birthday").replace("\\",""); // 01/31/1980 format
                            profile.setGender(object.getString("gender"));
                            SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy");
                            Date d = null;
                            d = f.parse(birthday);
                            long milliseconds = d.getTime();
                            profile.setAge(milliseconds);
                            profile.setUsername(object.getString("name"));
                            profile.setEmail(object.getString("email"));

                            onLoginListener.onComplete(profile, url, result.getAccessToken());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday,picture.type(large)");
        parameters.putString("type", "uploaded");
        request.setParameters(parameters);
        request.executeAsync();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
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
}



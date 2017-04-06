package com.kokuva;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;
import com.kokuva.interfaces.OnBitmapListener;
import com.kokuva.interfaces.OnLoginListener;
import com.kokuva.model.Profile;

import java.io.ByteArrayOutputStream;

public class LoginActivity extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private GoogleApiClient mGoogleApiClient;
    private EasyFacebook mEasyFacebook;
    private static final int RC_SIGN_IN = 100;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        mEasyFacebook = EasyFacebook.getInstance(this);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        myRef = FirebaseDatabase.getInstance().getReference();

        profile = KokuvaApp.getInstance().getProfile();

        findViewById(R.id.google).setOnClickListener(this);
        findViewById(R.id.facebook).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void saveImage(Bitmap bmp){
        Log.d(TAG, "salvando imagem ");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] b = baos.toByteArray();

        StorageReference imagesRef = storageRef.child("images/users/"+profile.getUserid()+"/"+System.currentTimeMillis()+".jpg");

        UploadTask uploadTask = imagesRef.putBytes(b);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "Uploadtask failed");
            }
        }).addOnSuccessListener(new OnSuccessListener<TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                profile.addPhoto(downloadUrl.toString());
                Log.d(TAG, "downloadUrl: "+downloadUrl.toString());
                createUserPhoto();
            }
        });
    }

    private void createUser(final String url){
        myRef.child("users/"+profile.getUserid()).setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(url!=null)
                    mEasyFacebook.getPhoto(url, new OnBitmapListener() {
                        @Override
                        public void onComplete(Bitmap bitmap) {
                            saveImage(bitmap);
                        }
                });
            }
        });
    }

    private void createUserPhoto(){
        myRef.child("users/"+profile.getUserid()+"/photos").setValue(profile.getPhotos()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressDialog();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        } else {
            mEasyFacebook.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.google) {
            signIn();
        }
        if (i==R.id.facebook){
            mEasyFacebook.loginFacebook(this, new OnLoginListener() {
                @Override
                public void onComplete(Profile profile, String url, AccessToken accesstoken) {
                    handleFacebookAccessToken(accesstoken.getToken(), url);
                }

                @Override
                public void onError(FacebookException exception) {                }

                @Override
                public void onCancel() {                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        hideProgressDialog();
        Log.d(TAG, "Connection failed");
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

    private void handleFacebookAccessToken(final String token, final String url) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        showProgressDialog();

        final AuthCredential credential = FacebookAuthProvider.getCredential(token);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        getUserFromFirebase(task.getResult().getUser().getUid(), url);
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                    }
                });
    }

    private void getUserFromFirebase(String uid, final String url){
        myRef.child("users/"+uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    KokuvaApp.getInstance().setProfile(dataSnapshot.getValue(Profile.class));
                    profile = KokuvaApp.getInstance().getProfile();
                    setResult(RESULT_OK);
                    finish();
                }
                else {
                    createUser(url);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {         }
        });
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog();
        profile.setUsername(acct.getDisplayName());
        profile.setEmail(acct.getEmail());
        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        createUser(acct.getPhotoUrl().toString());
                    }
                });
    }

}

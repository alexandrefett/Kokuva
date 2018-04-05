package com.kokuva;

import android.Manifest;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kokuva.dialogs.EnterChatDialog;
import com.kokuva.firestore.FirestoreRecyclerAdapter;
import com.kokuva.firestore.FirestoreRecyclerOptions;
import com.kokuva.model.Chat;
import com.kokuva.model.Room;
import com.kokuva.model.RoomHolder;


public class MainActivity extends BaseActivity implements View.OnClickListener, EnterChatDialog.NoticeDialogListener, FirebaseAuth.AuthStateListener {

    private FirebaseAuth mAuth;
    private static int REQUEST_IMAGE_CAPTURE = 1;
    private static int REQUEST_IMAGE_GALLERY = 2;
    private static int REQUEST_PERMISSIONS = 3;
    private static final CollectionReference sRoomsCollection =
            FirebaseFirestore.getInstance().collection("rooms");
    private static final Query sRoomQuery = sRoomsCollection.whereEqualTo("reserved", false);

    static {
        FirebaseFirestore.setLoggingEnabled(true);
    }

    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView)findViewById(R.id.rooms);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    private void addUserMac(String mac, String uuid){
    }



    private Button addRoomButton(final DocumentSnapshot doc){
        Button button = new Button(this);
        button.setText((String)doc.get("name"));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnterChatDialog dialog = new EnterChatDialog();
                dialog.setIdRoom(doc.getId());
                dialog.show(getSupportFragmentManager(), "EnterChatDialog");
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
        if (isSignedIn()) { attachRecyclerViewAdapter(); }
        FirebaseAuth.getInstance().addAuthStateListener(this);
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
        FirebaseAuth.getInstance().removeAuthStateListener(this);
        Log.d(TAG,"----Main Activity: OnStop");

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth auth) {

        if (isSignedIn()) {
            attachRecyclerViewAdapter();
        } else {
            Toast.makeText(this, R.string.signing_in, Toast.LENGTH_SHORT).show();
            auth.signInAnonymously().addOnCompleteListener(new SignInResultNotifier(this));
        }
    }

    private boolean isSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private void attachRecyclerViewAdapter() {
        final RecyclerView.Adapter adapter = newAdapter();

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mRecyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });

        mRecyclerView.setAdapter(adapter);
    }

    protected RecyclerView.Adapter newAdapter() {
        FirestoreRecyclerOptions<Room> options =
                new FirestoreRecyclerOptions.Builder<Room>()
                        .setQuery(sRoomQuery, Room.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirestoreRecyclerAdapter<Room, RoomHolder>(options) {
            @Override
            public RoomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RoomHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_room, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull RoomHolder holder, int position, @NonNull Room model) {
                holder.bind(model);
            }

            @Override
            public void onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
                //mEmptyListMessage.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        };
    }
}
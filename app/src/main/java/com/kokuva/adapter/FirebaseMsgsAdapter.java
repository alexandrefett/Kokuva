package com.kokuva.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.Query;
import com.kokuva.R;
import com.kokuva.model.KokuvaUser;

import java.util.ArrayList;

/**
 * Created by Alexandre on 22/07/2016.
 */
public class FirebaseMsgsAdapter extends FirebaseRecyclerAdapter<FirebaseMsgsAdapter.UserViewHolder, KokuvaUser>{

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        public ImageView image;
        public TextView nick;

        public UserViewHolder (View itemView){
            super(itemView);
            nick = (TextView) itemView.findViewById(R.id.text_nick);
            image = (ImageView) itemView.findViewById(R.id.item_picture);
        }
    }

    private Context context;
    private Activity activity;

    public FirebaseMsgsAdapter(Query query, Class<KokuvaUser> itemClass, @Nullable ArrayList<KokuvaUser> items,
                               @Nullable ArrayList<String> keys) {
        super(query, itemClass, items, keys);
    }

    public void setContext(Context c, Activity a ){
        this.context = c;
        this.activity =  a;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        UserViewHolder viewHolder = new UserViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UserViewHolder viewHolder, final int position){
        final KokuvaUser user = getItem(position);

        viewHolder.image.setOnClickListener(null);
        viewHolder.nick.setText(user.getDisplayName());
        viewHolder.image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(context, PostViewActivity.class);
//                MyApplication.getInstance().setPost(post);
//                int result = 4;
//                activity.startActivityForResult(i, result);
            }
        });
        Glide.with(context)
                .load(user.getPhotoUrl())
                .into(viewHolder.image);
    }


    @Override
    protected void itemAdded(KokuvaUser item, String key, int position) {

    }

    @Override
    protected void itemChanged(KokuvaUser oldItem, KokuvaUser newItem, String key, int position) {

    }

    @Override
    protected void itemRemoved(KokuvaUser item, String key, int position) {

    }

    @Override
    protected void itemMoved(KokuvaUser item, String key, int oldPosition, int newPosition) {

    }
}

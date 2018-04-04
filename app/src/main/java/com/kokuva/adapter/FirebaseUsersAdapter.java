package com.kokuva.adapter;

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
public class FirebaseUsersAdapter extends FirebaseRecyclerAdapter<FirebaseUsersAdapter.UserViewHolder, KokuvaUser>{

    public interface OnItemClickListener {
        void onItemClick(KokuvaUser item);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        public ImageView image;
        public TextView nick;
        public View v;

        public UserViewHolder (View itemView){
            super(itemView);
            nick = (TextView) itemView.findViewById(R.id.room_item_nick);
            image = (ImageView) itemView.findViewById(R.id.room_item_image);
            v = itemView;
        }
    }

    private Context context;
    private OnItemClickListener listener;

    public FirebaseUsersAdapter(Query query, Class<KokuvaUser> itemClass, @Nullable ArrayList<KokuvaUser> items,
                                @Nullable ArrayList<String> keys) {
        super(query, itemClass, items, keys);
    }

    public void addOnClickItemListener(OnItemClickListener listener){
        this.listener = listener;
    }
    public void setContext(Context c){
        this.context = c;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.room_item_user, parent, false);
        UserViewHolder viewHolder = new UserViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UserViewHolder viewHolder, final int position){
        final KokuvaUser user = getItem(position);

        viewHolder.v.setOnClickListener(null);
        viewHolder.nick.setText(user.getNick());
        viewHolder.v.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(user);
            }
        });

    }


    @Override
    protected void itemAdded(KokuvaUser item, String key, int position) {    }

    @Override
    protected void itemChanged(KokuvaUser oldItem, KokuvaUser newItem, String key, int position) {    }

    @Override
    protected void itemRemoved(KokuvaUser item, String key, int position) {    }

    @Override
    protected void itemMoved(KokuvaUser item, String key, int oldPosition, int newPosition) {    }

}

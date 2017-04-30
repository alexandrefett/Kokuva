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
import com.kokuva.model.Chat;
import java.util.ArrayList;

/**
 * Created by Alexandre on 22/07/2016.
 */
public class FirebaseChatsAdapter extends FirebaseRecyclerAdapter<FirebaseChatsAdapter.UserViewHolder, Chat>{

    public interface OnItemClickListener {
        void onItemClick(Chat item);
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

    public FirebaseChatsAdapter(Query query, Class<Chat> itemClass, @Nullable ArrayList<Chat> items,
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
        final Chat chat = getItem(position);

        viewHolder.v.setOnClickListener(null);
        viewHolder.nick.setText(chat.getUserTo().getNick());
        viewHolder.nick.setTextColor(chat.getUserTo().getColor());
        viewHolder.v.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(chat);
            }
        });
        if(chat.getUserTo().isPhoto())
            Glide.with(context)
                    .load(chat.getUserTo().getUrl())
                    .into(viewHolder.image);
        else
            viewHolder.image.setImageResource(context.getResources().getIdentifier(chat.getUserTo().getUrl(), "drawable", context.getPackageName()));

    }


    @Override
    protected void itemAdded(Chat item, String key, int position) {    }

    @Override
    protected void itemChanged(Chat oldItem, Chat newItem, String key, int position) {    }

    @Override
    protected void itemRemoved(Chat item, String key, int position) {    }

    @Override
    protected void itemMoved(Chat item, String key, int oldPosition, int newPosition) {    }

}

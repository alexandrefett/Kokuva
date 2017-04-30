package com.kokuva.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.kokuva.R;
import com.kokuva.model.Chat;

import java.util.ArrayList;

/**
 * Created by Alexandre on 22/07/2016.
 */
public class ChatAdapter extends RecyclerView.Adapter<UserViewHolder>{
    
    private Context context;
    private ArrayList<Chat> array;
    private int position;

    public ChatAdapter(Context c, ArrayList<Chat> p){
        this.context = c;
        this.array = p;
    }

    public void addItem(Chat k){
        array.add(k);
        notifyDataSetChanged();
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item_user, parent, false);
        UserViewHolder viewHolder = new UserViewHolder(context, view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UserViewHolder viewHolder, final int position){
        final Chat u = array.get(position);
        if(u.getUser().isPhoto())
            Glide.with(context)
                    .load(u.getUser().getUrl())
                    .into(viewHolder.picture);
        else
            viewHolder.picture.setImageResource(context.getResources().getIdentifier(u.getUser().getUrl(), "drawable", context.getPackageName()));

        viewHolder.name.setText(u.getUser().getNick());
        viewHolder.name.setTextColor(u.getUser().getColor());
        this.position = position;
    }

    public int getPosition(){
        return position;
    }

    @Override
    public int getItemCount(){
        return array.size();
    }

    public ArrayList<Chat> getData(){
        return array;
    }
}

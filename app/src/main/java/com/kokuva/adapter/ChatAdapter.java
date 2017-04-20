package com.kokuva.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.kokuva.R;
import com.kokuva.model.KokuvaUser;
import java.util.ArrayList;

/**
 * Created by Alexandre on 22/07/2016.
 */
public class ChatAdapter extends RecyclerView.Adapter<UserViewHolder>{
    
    private Context context;
    private ArrayList<KokuvaUser> array;
    private Activity a;
    private int position;

    public ChatAdapter(Activity a, Context c, ArrayList<KokuvaUser> p){
        this.a = a;
        this.context = c;
        this.array = p;
    }

    public void addItem(KokuvaUser k){
        array.add(k);
    }
    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_user, parent, false);
        UserViewHolder viewHolder = new UserViewHolder(context, view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UserViewHolder viewHolder, final int position){
        final KokuvaUser u = array.get(position);

        Glide.with(context)
            .load(u.getPhotoUrl())
            .crossFade()
            .into(viewHolder.picture);
        viewHolder.name.setText(u.getDisplayName());
        this.position = position;
    }

    public int getPosition(){
        return position;
    }

    @Override
    public int getItemCount(){
        return array.size();
    }

    public ArrayList<KokuvaUser> getData(){
        return array;
    }
}

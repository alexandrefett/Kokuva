package com.kokuva.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
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
    private int position;

    public ChatAdapter(Context c, ArrayList<KokuvaUser> p){
        this.context = c;
        this.array = p;
    }

    public void addItem(KokuvaUser k){
        array.add(k);
        notifyDataSetChanged();
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.tab_user, parent, false);
        UserViewHolder viewHolder = new UserViewHolder(context, view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UserViewHolder viewHolder, final int position){
        final KokuvaUser u = array.get(position);
        if(u.isPhoto())
            Glide.with(context)
                    .load(u.getUrl())
                    .into(viewHolder.picture);
        else
            viewHolder.picture.setImageResource(context.getResources().getIdentifier(u.getUrl(), "drawable", context.getPackageName()));

        viewHolder.name.setText(u.getNick());
        viewHolder.name.setTextColor(u.getColor());
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

package com.kokuva.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kokuva.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Alexandre on 22/07/2016.
 */
public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context context=null;
    public TextView name;
    public CircleImageView picture;

    public UserViewHolder(Context context, View itemView){
        super(itemView);
        itemView.setOnClickListener(this);
        this.context = context;
        name = (TextView)itemView.findViewById(R.id.item_list_displayname);
        picture = (CircleImageView) itemView.findViewById(R.id.chat_item_image);
    }

    @Override
    public void onClick(View v){
          // Log.d("--->>>","getId: "+v.getId());
    }
}

package com.kokuva.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kokuva.R;
import com.kokuva.model.PhotoFB;

import java.util.ArrayList;

/**
 * Created by Alexandre on 22/07/2016.
 */
public class ItemFbAdapter extends RecyclerView.Adapter<ItemFbAdapter.ItemGalleryViewHolder>{

    private Context context;
    private ArrayList<PhotoFB> array;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String path);
    }

    public ItemFbAdapter(Context c, ArrayList<PhotoFB> array, OnItemClickListener l){
        this.listener = l;
        this.context = c;
        this.array = array;
    }

    @Override
    public ItemFbAdapter.ItemGalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
        return new ItemFbAdapter.ItemGalleryViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ItemGalleryViewHolder viewHolder, final int position){
        final String url = array.get(position).getUrl();
        viewHolder.image.setOnClickListener(null);
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.ic_action_user)
                .dontAnimate()
                .into(viewHolder.image);
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(array.get(position).getUrl());

            }
        });
    }

    @Override
    public int getItemCount(){
        return array.size();
    }

    static class ItemGalleryViewHolder extends RecyclerView.ViewHolder{
        public ImageView image;

        public ItemGalleryViewHolder(View itemView){
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.item_picture);
        }
    }

}

package com.kokuva.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kokuva.R;

import java.util.ArrayList;

/**
 * Created by Alexandre on 22/07/2016.
 */
public class ItemGalleryAdapter extends RecyclerView.Adapter<ItemGalleryAdapter.ItemGalleryViewHolder>{

    private Context context;
    private ArrayList<Integer> array;
    private ArrayList<String> paths;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ImageView item, String path);
    }

    public ItemGalleryAdapter(Context c, OnItemClickListener l){
        this.listener = l;
        this.context = c;
        this.paths = new ArrayList<String>();
        this.array = new ArrayList<Integer>();
        getIds();
        notifyDataSetChanged();
    }

    @Override
    public ItemGalleryAdapter.ItemGalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
        return new ItemGalleryAdapter.ItemGalleryViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ItemGalleryViewHolder viewHolder, final int position){
        final int path = array.get(position);
        viewHolder.image.setOnClickListener(null);

        Bitmap bitmap = Thumbnails.getThumbnail(context.getContentResolver(), array.get(position), Thumbnails.MINI_KIND, null);
        viewHolder.image.setImageBitmap(bitmap);
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bmp = Thumbnails.getThumbnail(context.getContentResolver(), array.get(position), Thumbnails.MINI_KIND, null);
                listener.onItemClick(viewHolder.image, paths.get(position));
            }
        });
    }

    @Override
    public int getItemCount(){
        return paths.size();
    }

    static class ItemGalleryViewHolder extends RecyclerView.ViewHolder{
        public ImageView image;

        public ItemGalleryViewHolder(View itemView){
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.item_picture);
        }
    }

    private void getIds() {
        String[] projection = new String[]{
                Media._ID
        };
        Uri images = Media.EXTERNAL_CONTENT_URI;
        Cursor cur = context.getContentResolver().query(images,
                null, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                null        // Ordering
        );

        if (cur.moveToFirst()) {
            int idColumn = cur.getColumnIndex(Media._ID);
            int pathColumn = cur.getColumnIndex(Media.DATA);
            while (!cur.isAfterLast()) {
                array.add(cur.getInt(idColumn));
                paths.add(cur.getString(pathColumn));
                cur.moveToNext();
            }
        }
    }
}

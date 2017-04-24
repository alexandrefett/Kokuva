package com.kokuva.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kokuva.R;

/**
 * Created by Alexandre on 22/04/2017.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {

        return mThumbIds[position];
    }

    public long getItemId(int position) {
        return mContext.getResources().getIdentifier(mThumbIds[position], "drawable", mContext.getPackageName());
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(5, 5, 5, 5);
        } else {
            imageView = (ImageView) convertView;
        }
        //imageView.setImageResource(mContext.getResources().getIdentifier(mThumbIds[position], "drawable", mContext.getPackageName()));
        imageView.setImageResource(mTIds[position]);
        return imageView;
    }

    // references to our images
    private String[] mThumbIds = {"add","man", "user","user_1","user_2","user_3","user_4","user_5",
            "user_6","user_7","user_8","user_9","user_10","user_11","user_12","user_13","user_14",
            "user_15","user_16","user_17","user_18","user_19","user_20","user_21","user_22"};
    private Integer[] mTIds = {R.drawable.add, R.drawable.man, R.drawable.user, R.drawable.user_1
            , R.drawable.user_2, R.drawable.user_3, R.drawable.user_4, R.drawable.user_5, R.drawable.user_6, R.drawable.user_7
            , R.drawable.user_8, R.drawable.user_9, R.drawable.user_10, R.drawable.user_11, R.drawable.user_12, R.drawable.user_13
            , R.drawable.user_14, R.drawable.user_15, R.drawable.user_16, R.drawable.user_17, R.drawable.user_18, R.drawable.user_19
            , R.drawable.user_20, R.drawable.user_21, R.drawable.user_22};
}

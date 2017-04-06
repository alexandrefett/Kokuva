package com.kokuva.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.kokuva.R;

import java.util.ArrayList;

/**
 * Created by Alexandre on 19/10/2016.
 */

public class PhotoPagerAdapter extends PagerAdapter {
    int p=0;
    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<String> items;

    public PhotoPagerAdapter(Context context, ArrayList<String> array) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        items = array;
    }

    public void updateItems(ArrayList<String> array){
        this.items = array;
        notifyDataSetChanged();
    }

    public String getItem(int position){
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.layout_image, container, false);
        this.p = position;
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        Glide.with(mContext)
                .load(items.get(position))
                .dontAnimate()
                .into(imageView);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
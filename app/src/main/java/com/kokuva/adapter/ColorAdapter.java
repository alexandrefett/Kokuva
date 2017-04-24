package com.kokuva.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kokuva.R;

/**
 * Created by Alexandre on 22/04/2017.
 */

public class ColorAdapter extends BaseAdapter {
    private Context mContext;

    public ColorAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mTIds.length;
    }

    public Integer getItem(int position) {

        return mTIds[position];
    }

    public long getItemId(int position) {
        return mTIds[position];
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view;
        if (convertView == null) {
            view = new TextView(mContext);
            view.setLayoutParams(new GridView.LayoutParams(70, 70));
        } else {
            view = (TextView) convertView;
        }
        view.setBackgroundColor(mTIds[position]);
        return view;
    }

    private Integer[] mTIds = {Color.BLACK, Color.BLUE, Color.GREEN, Color.MAGENTA,
            Color.CYAN, Color.DKGRAY, Color.RED, Color.YELLOW};
}

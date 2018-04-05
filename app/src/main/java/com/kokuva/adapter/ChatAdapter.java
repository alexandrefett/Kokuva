package com.kokuva.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kokuva.R;
import com.kokuva.model.Chat;

import java.util.ArrayList;

/**
 * Created by Alexandre on 22/04/2017.
 */

public class ChatAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Chat> array;

    public ChatAdapter(Context c, ArrayList<Chat> a) {
        array = a;
        mContext = c;
    }

    public int getCount() {
        return array.size();
    }

    public Chat getItem(int position) {
        return array.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView text;
        if (convertView == null) {
            text = new TextView(mContext);
            text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            text.setPadding(5, 5, 5, 5);
        } else {
            text = (TextView) convertView;
        }
        //imageView.setImageResource(mContext.getResources().getIdentifier(mThumbIds[position], "drawable", mContext.getPackageName()));
        text.setText(array.get(position).getName());
        return text;
    }

    // references to our images
}

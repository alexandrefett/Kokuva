package com.kokuva.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by Alexandre on 01/05/2017.
 */

public class MessageView extends AppCompatTextView {
    private static final int MARGIN=5;
    private static final int PADDING=5;

    public MessageView(Context context) {
        super(context);
    }

    public MessageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MessageView(Context context, int resBackground, int align){
        super(context);
        setBackgroundResource(resBackground);
        setPadding(PADDING,PADDING,PADDING,PADDING);
        setTextSize(20f);
        setFocusable(true);
        setFocusableInTouchMode(true);
        LinearLayoutCompat.LayoutParams lp = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(MARGIN,MARGIN,MARGIN,MARGIN);
        setLayoutParams(lp);
    }


}

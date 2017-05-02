package com.kokuva.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;

import com.kokuva.R;

/**
 * Created by Alexandre on 01/05/2017.
 */

public class SenderMsgView extends MessageView {
    public SenderMsgView(Context context) {
        super(context);
        setGravity(Gravity.RIGHT);
        setBackgroundResource(R.drawable.sender_msg_layout);
    }

    public SenderMsgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SenderMsgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}

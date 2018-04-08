package com.kokuva.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;

import com.kokuva.R;

/**
 * Created by Alexandre on 01/05/2017.
 */

public class ReceiverMsgView extends ChatView {
    public ReceiverMsgView(Context context) {
        super(context);
        setGravity(Gravity.LEFT);
        setBackgroundResource(R.drawable.receiver_msg_layout);
    }

    public ReceiverMsgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ReceiverMsgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}

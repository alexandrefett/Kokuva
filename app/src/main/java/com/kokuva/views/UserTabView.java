package com.kokuva.views;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kokuva.KokuvaApp;
import com.kokuva.R;
import com.kokuva.model.KokuvaUser;

/**
 * Created by Alexandre on 14/04/2017.
 */

public class UserTabView extends LinearLayout {
    private Context context;
    private ImageView userImage;
    private TextView displayName;
    private View rootView;
    private KokuvaUser user;

    public UserTabView(Context context, KokuvaUser user) {
        super(context);
        this.context = context;
        this.user = user;
        init();
    }

    private void init(){
        rootView = inflate(context, R.layout.tab_user, this);
        userImage = (ImageView) rootView.findViewById(R.id.image_user);
        displayName = (TextView) rootView.findViewById(R.id.name_display);
        Glide.with(context)
                .load(user.getUrl())
                .into(userImage);
    }
}

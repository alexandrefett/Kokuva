package com.kokuva.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kokuva.R;

public class UserHolder extends RecyclerView.ViewHolder {

    private final TextView nick;

    public UserHolder(View itemView) {
        super(itemView);
        nick = itemView.findViewById(R.id.nickname);
    }

    public void bind(AbstractUser user) {
        setName(user.getName());
    }

    private void setName(String name) {
        nick.setText(name);
    }
}

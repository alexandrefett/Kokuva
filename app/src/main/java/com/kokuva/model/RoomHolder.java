package com.kokuva.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kokuva.R;

public class RoomHolder extends RecyclerView.ViewHolder {

    private final TextView roomName;

    public RoomHolder(View itemView) {
        super(itemView);
        roomName = itemView.findViewById(R.id.room_name);
    }

    public void bind(AbstractRoom room) {
        setName(room.getName());
    }

    private void setName(String name) {
        roomName.setText(name);
    }
}

package com.kokuva.model;

import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.kokuva.R;
import com.kokuva.dialogs.EnterChatDialog;

public class RoomHolder extends RecyclerView.ViewHolder {

    private final TextView roomName;
    public interface OnClickListener{
        public void onClickListener(AbstractRoom room);
    }

    public RoomHolder(View itemView) {
        super(itemView);
        roomName = itemView.findViewById(R.id.room_name);
    }

    public void bind(final AbstractRoom room, final OnClickListener listener) {
        setName(room.getName());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickListener(room);
            }
        });
    }
    private void setName(String name) {
        roomName.setText(name);
    }
}

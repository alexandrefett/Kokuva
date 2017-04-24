package com.kokuva.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.kokuva.R;
import com.kokuva.adapter.ImageAdapter;

/**
 * Created by Alexandre on 23/04/2017.
 */

public class AvatarDialog extends DialogFragment {

    public interface AvatarDialogListener {
        public void onAvatarClick(String icon);
    }

    AvatarDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.avatar_layout, null);
        builder.setView(dialogView);

        final GridView grid = (GridView)dialogView.findViewById(R.id.grid_avatar);
        grid.setAdapter(new ImageAdapter(getContext()));
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ImageAdapter adapter = (ImageAdapter) grid.getAdapter();
            listener.onAvatarClick((String)adapter.getItem(i));
                dismiss();
            }
        });
        builder.setMessage("Escolha de avatar");

        return builder.create();
    }

    @Override
    public void onAttach(Context c) {
        super.onAttach(c);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (AvatarDialogListener)c;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(c.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
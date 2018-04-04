package com.kokuva.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.kokuva.R;

/**
 * Created by Alexandre on 01/05/2017.
 */

public class AcceptDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        String nick = savedInstanceState.getString("nick");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(nick+" deseja conversar com você. Você aceita?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton("não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }
}
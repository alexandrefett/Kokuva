package com.kokuva.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.kokuva.R;
import com.kokuva.model.AbstractRoom;

/**
 * Created by Alexandre on 23/04/2018
 */

public class ExitChatDialog extends DialogFragment {

    public interface NoticeDialogListener {
        void onDialogPositiveClick(AbstractRoom room);
    }
    private Activity a;
    NoticeDialogListener mListener;
    private AbstractRoom room;

    public void setListener(NoticeDialogListener listener, AbstractRoom room){
        this.mListener = listener;
        this.room = room;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.enterchat_layout, null);
        Log.d("---------->","view dialog");
        builder.setView(dialogView)
            .setPositiveButton("Entrar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d("---------->","onclick");
                    mListener.onDialogPositiveClick(room);
                }
            });
        builder.setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            a=(Activity) context;
        }
        try {
            mListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }
 }
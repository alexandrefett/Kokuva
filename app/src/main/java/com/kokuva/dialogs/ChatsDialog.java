package com.kokuva.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.kokuva.R;
import com.kokuva.adapter.ImageAdapter;

/**
 * Created by Alexandre on 23/04/2017.
 */

public class ChatsDialog extends DialogFragment {

    public interface ChatDialogListener {
        public void onItemClick(int position);
    }

    ChatDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.chats_layout, null);
        builder.setView(dialogView);

        final ListView list = (ListView)dialogView.findViewById(R.id.list_chats);
        list.setAdapter(new ImageAdapter(getContext()));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ImageAdapter adapter = (ImageAdapter) list.getAdapter();
            listener.onItemClick(i);
            }
        });
        builder.setMessage("Suas conversas");
        builder.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context c) {
        super.onAttach(c);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (ChatDialogListener)c;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(c.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
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
import android.widget.SeekBar;

import com.kokuva.R;
import com.kokuva.adapter.ImageAdapter;

/**
 * Created by Alexandre on 23/04/2017.
 */

public class DistanceDialog extends DialogFragment {

    private int distance;

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public interface DistanceDialogListener {
        public void onDistanceChange(int distance);
    }

    DistanceDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        int d = savedInstanceState.getInt("distance");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.distance_layout, null);
        builder.setView(dialogView);

        final SeekBar bar = (SeekBar) dialogView.findViewById(R.id.seek_bar);
        bar.setProgress(d-1);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setDistance(i+1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                listener.onDistanceChange(getDistance());
                dismiss();
            }
        });
        builder.setMessage("Distância de busca");

        return builder.create();
    }

    @Override
    public void onAttach(Context c) {
        super.onAttach(c);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (DistanceDialogListener)c;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(c.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
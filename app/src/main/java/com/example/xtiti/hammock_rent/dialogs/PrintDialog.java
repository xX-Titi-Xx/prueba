package com.example.xtiti.hammock_rent.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.example.xtiti.hammock_rent.R;
import com.example.xtiti.hammock_rent.activities.MainActivity;

/**
 * Created by xtiti on 30/07/15.
 */
public class PrintDialog extends DialogFragment{

    private MainActivity mainActivity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mainActivity = (MainActivity)getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.print_dialog, null);
        builder.setView(view);

        builder.setPositiveButton(getResources().getString(R.string.si), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });


        return builder.create();
    }
}

package com.example.xtiti.hammock_rent.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.example.xtiti.hammock_rent.R;
import com.example.xtiti.hammock_rent.activities.MainActivity;
import com.example.xtiti.hammock_rent.utils.AlquileresAdapter;

/**
 * Created by xtiti on 2/08/15.
 */
public class HistorialAlquileresDialog extends DialogFragment {

    private MainActivity mainActivity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mainActivity = (MainActivity)getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.historial_alquileres, null);
        ListView lvAlquileres = (ListView)view.findViewById(R.id.lv_historial_alquileres);
        lvAlquileres.setAdapter(new AlquileresAdapter(mainActivity, mainActivity.getListAlquiler()));
        builder.setView(view);

        builder.setPositiveButton(getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        AlertDialog dialogo = builder.create();
        dialogo.setCanceledOnTouchOutside(false);

        return dialogo;
    }
}

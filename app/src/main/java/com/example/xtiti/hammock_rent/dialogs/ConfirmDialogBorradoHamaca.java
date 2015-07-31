package com.example.xtiti.hammock_rent.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.xtiti.hammock_rent.R;
import com.example.xtiti.hammock_rent.activities.MainActivity;
import com.example.xtiti.hammock_rent.models.Hamaca;
import com.example.xtiti.hammock_rent.utils.Globales;
import com.example.xtiti.hammock_rent.utils.VolleyUtil;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xtiti on 9/07/15.
 */
public class ConfirmDialogBorradoHamaca extends DialogFragment {

    private ArrayList<Marker> listMarker;
    private List<Hamaca> listHamaca;
    private Marker marker;
    private Hamaca hamacaBorrar;
    private VolleyUtil volleyUtil;
    private RequestQueue requestQueue;
    private DialogInterface dialogo;
    private MainActivity mainActivity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mainActivity = ((MainActivity)getActivity());
        listMarker = mainActivity.getlistMarker();
        volleyUtil = VolleyUtil.getInstance(getActivity().getApplicationContext());
        requestQueue = volleyUtil.getRequestQueue();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.confirm_dialog_borrado_hamaca, null);

        builder.setView(view);
        builder.setPositiveButton(getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                listMarker.remove(marker);
                hamacaBorrar = new Hamaca();
                hamacaBorrar.setLongitud(marker.getPosition().longitude);
                hamacaBorrar.setLatitud(marker.getPosition().latitude);
                hamacaBorrar = listHamaca.get(listHamaca.indexOf(hamacaBorrar));

                Gson gson = new Gson();
                String hamacaBorrarString = gson.toJson(hamacaBorrar);

                JSONObject jsonObject = null;
                JsonObjectRequest jsonObjectRequest = null;

                try {
                    jsonObject = new JSONObject(hamacaBorrarString);

                    jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Globales.URL_BAJAHAMACA, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                if(response.getBoolean("borrado")){
                                    listHamaca.remove(hamacaBorrar);
                                    marker.remove();
                                    mainActivity.estableceContadoresHamacas();

                                    dialogo.dismiss();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getActivity().getApplicationContext(), "Error en la comunicación.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dialogo = dialog;
                requestQueue.add(jsonObjectRequest);

                //dialog.dismiss();
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public void setListHamaca(List<Hamaca> listHamaca) {
        this.listHamaca = listHamaca;
    }
}

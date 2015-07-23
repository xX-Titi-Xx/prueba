package com.example.xtiti.hammock_rent.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
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
import com.example.xtiti.hammock_rent.utils.Constantes;
import com.example.xtiti.hammock_rent.utils.VolleyUtil;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by xtiti on 23/07/15.
 */
public class ConfirmDialogNuevaFilaHamaca extends DialogFragment {

    private MainActivity mainActivity;
    private List<Hamaca> listNuevaFilaHamaca;
    private List<Marker> listMarkerNuevaFilaHamaca;
    private VolleyUtil volleyUtil;
    private RequestQueue requestQueue;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mainActivity = (MainActivity)getActivity();
        volleyUtil = VolleyUtil.getInstance(mainActivity);
        requestQueue = volleyUtil.getRequestQueue();

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.confirm_dialog_nueva_fila_hamaca, null);
        builder.setView(view);
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                for (Hamaca hamaca : listNuevaFilaHamaca) {

                    Gson gson = new Gson();
                    String hamacaJsonString = gson.toJson(hamaca);
                    JsonObjectRequest jsonObjectRequest = null;

                    try {
                        JSONObject jsonObject = new JSONObject(hamacaJsonString);
                        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                                Constantes.URL_SAVEHAMACA, jsonObject,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        JsonParser jsonParser = new JsonParser();
                                        Gson gson2 = new Gson();
                                        JsonElement jsonElement = jsonParser.parse(response.toString());
                                        Hamaca hamacaSaved = gson2.fromJson(jsonElement, Hamaca.class);

                                        if (hamacaSaved.getId() != Constantes.ID_HAMACA_NOT_SAVED) {

                                            mainActivity.getListHamaca().add(hamacaSaved);
                                            mainActivity.estableceContadoresHamacas();
                                        } else {
                                            Toast.makeText(mainActivity.getApplicationContext(), "No se ha podido realizar la operación.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(mainActivity.getApplicationContext(), "Se ha producido un error en la comunicación.", Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                            }
                        });

                        requestQueue.add(jsonObjectRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dismiss();
            }
        });

        builder.setNegativeButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (Marker marker : listMarkerNuevaFilaHamaca) {
                    marker.remove();
                }
                dismiss();
            }
        });

        AlertDialog dialogo = builder.create();
        dialogo.getWindow().setGravity(Gravity.TOP);

        dialogo.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;

        return dialogo;
    }

    public void setListNuevaFilaHamaca(List<Hamaca> nuevaFilaHamaca) {
        this.listNuevaFilaHamaca = nuevaFilaHamaca;
    }

    public void setListMarkerNuevaFilaHamaca(List<Marker> listMarkerNuevaFilaHamaca) {
        this.listMarkerNuevaFilaHamaca = listMarkerNuevaFilaHamaca;
    }
}

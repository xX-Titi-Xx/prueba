package com.example.xtiti.hammock_rent.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xtiti on 9/07/15.
 */
public class MarkerDialog extends DialogFragment {

    private Marker marker;
    private String[] opcionesDialog;
    private LatLng coordenadasMarker;
    private GoogleMap googleMap;
    private ArrayList<Marker> listMarker;
    private List<Hamaca> listHamaca;
    private VolleyUtil volleyUtil;
    private RequestQueue requestQueue;
    private MainActivity mainActivity;
    private Hamaca hamacaNueva;
    private String estadoAnteriorHamaca;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mainActivity = (MainActivity)getActivity();
        volleyUtil = VolleyUtil.getInstance(getActivity().getApplicationContext());
        requestQueue = volleyUtil.getRequestQueue();
        listMarker = ((MainActivity)getActivity()).getlistMarker();
        listHamaca = mainActivity.getListHamaca();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.markers_dialog, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title_dialog);
        ListView listMarkersDialog = (ListView) view.findViewById(R.id.list_marker_dialog);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, opcionesDialog);
        listMarkersDialog.setAdapter(adapter);

        listMarkersDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Menú sólo para añadir hamacas
                if(position == 0 && opcionesDialog.length == getResources().getStringArray(R.array.marker_add).length){
                    addHamaca();
                }
                //Recolocar hamacas
                else if(position == 0){
                    recolocarHamaca();
                }

                if(position == 1 && opcionesDialog.length == getResources().getStringArray(R.array.marker_add).length){
                    addRowHamaca();
                }
                //Marcar como disponible
                else if(position == 1){
                    markAsAvailable();
                }
                //Marcar como pendiente de pago (amarillo)
                else if(position == 2){
                    markAsPending();
                }
                //Marcar como alquilada (rojo)
                else if(position == 3){
                    markAsBusy();
                }
                //Borrado de hamaca
                else if(position == 4){
                    removeHamaca();
                }
            }
        });

        tvTitle.setText(getResources().getString(R.string.title_markers_dialog));
        builder.setView(view).setNegativeButton(getResources().getString(R.string.cerrar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    private void addRowHamaca(){
        //PENDIENTE
        Toast.makeText(getActivity().getApplicationContext(), "Pulse el punto de comienzo de la fila.", Toast.LENGTH_SHORT).show();
        mainActivity.setMarcandoFila(true);

        dismiss();
    }

    private void removeHamaca() {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setMarker(marker);
        confirmDialog.setListHamaca(listHamaca);
        confirmDialog.show(getFragmentManager(), "tagConfirmBorradoHamaca");

        dismiss();
    }

    private void markAsBusy() {
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_action_beach_umbrella_and_hammock_24);
        marker.setIcon(bitmapDescriptor);

        Hamaca hamaca = new Hamaca();
        hamaca.setLatitud(marker.getPosition().latitude);
        hamaca.setLongitud(marker.getPosition().longitude);

        listHamaca.get(listHamaca.indexOf(hamaca)).setEstado("OCUPADA");

        dismiss();
        Toast.makeText(getActivity(), "Hamaca pagada.", Toast.LENGTH_SHORT).show();
    }

    private void markAsPending() {
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_action_beach_umbrella_and_hammock_24_yellow);
        marker.setIcon(bitmapDescriptor);

        Hamaca hamaca = new Hamaca();
        hamaca.setLatitud(marker.getPosition().latitude);
        hamaca.setLongitud(marker.getPosition().longitude);

        listHamaca.get(listHamaca.indexOf(hamaca)).setEstado("PENDIENTE");

        dismiss();
        Toast.makeText(getActivity(), "Hamaca Pendiente de Pago.", Toast.LENGTH_SHORT).show();
    }

    private void markAsAvailable() {

        hamacaNueva = new Hamaca();
        hamacaNueva.setLongitud(marker.getPosition().longitude);
        hamacaNueva.setLatitud(marker.getPosition().latitude);
        hamacaNueva = listHamaca.get(listHamaca.indexOf(hamacaNueva));

        if(!hamacaNueva.getEstado().equalsIgnoreCase("LIBRE")) {
            estadoAnteriorHamaca = hamacaNueva.getEstado();
            hamacaNueva.setEstado("LIBRE");

            //CAMBIAR

            Gson gson = new Gson();
            String hamacaJsonString = gson.toJson(hamacaNueva);
            JsonObjectRequest jsonObjectRequest = null;

            try {
                JSONObject jsonObject = new JSONObject(hamacaJsonString);
                jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        Constantes.URL_SAVEHAMACA, jsonObject,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_action_beach_umbrella_and_hammock_24_green);
                                JsonParser jsonParser = new JsonParser();
                                Gson gson2 = new Gson();
                                JsonElement jsonElement = jsonParser.parse(response.toString());
                                Hamaca hamacaSaved = gson2.fromJson(jsonElement, Hamaca.class);

                                if (hamacaSaved.getEstado().equalsIgnoreCase("LIBRE")) {
                                    marker.setIcon(customMarker);
                                    Toast.makeText(getActivity(), "Hamaca LIBRE.", Toast.LENGTH_SHORT).show();
                                } else {
                                    hamacaNueva.setEstado(estadoAnteriorHamaca);
                                    Toast.makeText(getActivity(), "No se ha podido realizar la operación.", Toast.LENGTH_SHORT).show();
                                }

                                dismiss();
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Se ha producido un error en la comunicación.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                        dismiss();
                    }
                });

                requestQueue.add(jsonObjectRequest);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(getActivity().getApplicationContext(), "La hamaca seleccionada ya es LIBRE", Toast.LENGTH_SHORT).show();
        }
    }

    private void recolocarHamaca() {
        dismiss();
        mainActivity.setlastMarkerPosition(marker.getPosition());
        Hamaca hamaca = new Hamaca();
        hamaca.setLatitud(marker.getPosition().latitude);
        hamaca.setLongitud(marker.getPosition().longitude);

        mainActivity.setPosHamacaCogida(listHamaca.indexOf(hamaca));
        marker.setDraggable(true);
        Toast.makeText(getActivity(), "Haga una pulsación larga sobre la hamaca y arrástela a su nueva posición.", Toast.LENGTH_SHORT).show();
    }

    private void addHamaca() {
        BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_action_beach_umbrella_and_hammock_24_green);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(customMarker);
        markerOptions.position(coordenadasMarker);
        hamacaNueva = new Hamaca();
        hamacaNueva.setId_empresa(Constantes.ID_EMPRESA);
        hamacaNueva.setId(-1);
        hamacaNueva.setLongitud(coordenadasMarker.longitude);
        hamacaNueva.setLatitud(coordenadasMarker.latitude);
        hamacaNueva.setEstado("LIBRE");

        //CAMBIAR

        listMarker.add(googleMap.addMarker(markerOptions));

        Gson gson = new Gson();
        String hamacaJsonString = gson.toJson(hamacaNueva);
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

                            if(hamacaSaved.getId() != hamacaNueva.getId()){

                                listHamaca.add(hamacaSaved);
                                Toast.makeText(getActivity(), "Nueva Hamaca añadida.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getActivity(), "No se ha podido realizar la operación.", Toast.LENGTH_SHORT).show();
                            }

                            dismiss();

                        }
                    }, new Response.ErrorListener(){

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getActivity(), "Se ha producido un error en la comunicación.", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                            dismiss();
                        }
                    });

            requestQueue.add(jsonObjectRequest);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setMarker(Marker marker){
        this.marker = marker;
    }

    public void setOpcionesDialog(String[] opcionesDialog) {
        this.opcionesDialog = opcionesDialog;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public void setCoordenadasMarker(LatLng coordenadasMarker) {
        this.coordenadasMarker = coordenadasMarker;
    }
}
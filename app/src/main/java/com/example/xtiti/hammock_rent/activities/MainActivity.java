package com.example.xtiti.hammock_rent.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.xtiti.hammock_rent.dialogs.ConfirmDialogNuevaFilaHamaca;
import com.example.xtiti.hammock_rent.dialogs.HistorialAlquileresDialog;
import com.example.xtiti.hammock_rent.dialogs.MarkerDialog;
import com.example.xtiti.hammock_rent.models.Alquiler;
import com.example.xtiti.hammock_rent.models.Hamaca;
import com.example.xtiti.hammock_rent.utils.Globales;
import com.example.xtiti.hammock_rent.utils.VolleyUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.maps.android.SphericalUtil;
import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private LatLng coordenadasMap;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence mTitle;
    private DrawerLayout drawerLayout;
    private Hamaca hamacaNueva = null;
    private Hamaca hamacaArrastrada;
    private List<Hamaca> listHamaca;
    private List<Hamaca> listNuevaFilaHamaca;
    private ArrayList<Alquiler> listAlquiler;
    private List<Polyline> listPolyline;
    private ArrayList<Marker> listMarker;
    private ArrayList<Marker> listMarkerNuevaFilaHamaca;
    private LatLng lastMarkerPosition;
    private VolleyUtil volleyUtil;
    private RequestQueue requestQueue;
    private SimpleDateFormat sdf;
    private String qrCode;
    private ProgressDialog progressDialog;
    private int posHamacaCogida;
    private boolean marcandoFila;
    private Location puntoInicioFila;
    private Location puntoFinFila;
    private TextView tvContHamacasLibres;
    private TextView tvContHamacasPendientes;
    private TextView tvContHamacasOcupadas;
    private TextView tvInfoBottom;
    private int contHamacasLibres;
    private int contHamacasPendientes;
    private int contHamacasOcupadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        inicializaObjetos();

        inicializaNavigationDrawer();

        inicializaMap();

        estableceContadoresHamacas();
    }

    public void estableceContadoresHamacas() {

        contHamacasLibres = 0;
        contHamacasPendientes = 0;
        contHamacasOcupadas = 0;

        for(Hamaca hamaca : listHamaca){
            if(hamaca.getEstado().equalsIgnoreCase("LIBRE")){
                contHamacasLibres++;
            }
            else if(hamaca.getEstado().equalsIgnoreCase("OCUPADA")){
                contHamacasOcupadas++;
            }
            else{
                contHamacasPendientes++;
            }
        }

        tvContHamacasLibres.setText(String.valueOf(contHamacasLibres) + "/" + String.valueOf(listHamaca.size()));
        tvContHamacasPendientes.setText(String.valueOf(contHamacasPendientes) + "/" + String.valueOf(listHamaca.size()));
        tvContHamacasOcupadas.setText(String.valueOf(contHamacasOcupadas) + "/" + String.valueOf(listHamaca.size()));
    }

    private void inicializaMap(){

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_frag);
        mapFragment.getMapAsync(this);
    }

    private void inicializaNavigationDrawer() {

        ListView listNavigationDrawer = (ListView)findViewById(R.id.navigation_drawer);
        ArrayAdapter<CharSequence> adapterListNavigationDrawer = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.list_navigation_drawer));

        listNavigationDrawer.setAdapter(adapterListNavigationDrawer);
        listNavigationDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {

                    IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                    integrator.setPrompt("Enfoque el código QR...");
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);

                    integrator.initiateScan();

                } else if (position == 1) {

                    HistorialAlquileresDialog historialAlquileresDialog = new HistorialAlquileresDialog();
                    historialAlquileresDialog.show(getFragmentManager(), "tagHistorialAlquileresDialog");

                } else if (position == 2) {

                    Toast.makeText(getApplicationContext(), "Imprimiendo ticket...", Toast.LENGTH_SHORT).show();
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Globales.URL_HORA, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Date date = new Date(response.getLong("horaservidor"));
                                String fecha = sdf.format(date);
                                Toast.makeText(getApplicationContext(), fecha, Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "No se ha podido realizar la petición.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    requestQueue.add(jsonObjectRequest);
                } else if (position == 3) {
                    finish();
                }
            }
        });

        configuraToggle();
    }

    private void configuraToggle(){
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.ic_action_beach_umbrella_and_hammock_24, R.string.abierto,
                R.string.cerrado) {

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // creates call to onPrepareOptionsMenu()
                supportInvalidateOptionsMenu();
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle("Selecciona opción");
                // creates call to onPrepareOptionsMenu()
                supportInvalidateOptionsMenu();
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerToggle.syncState();
    }

    private void inicializaObjetos() {

        double latitudEmpresa = getIntent().getExtras().getDouble("latitudEmpresa");
        double longitudEmpresa = getIntent().getExtras().getDouble("longitudEmpresa");

        coordenadasMap = new LatLng(latitudEmpresa, longitudEmpresa);

        listHamaca = getIntent().getExtras().getParcelableArrayList("listHamaca");
        listAlquiler = getIntent().getExtras().getParcelableArrayList("listAlquiler");
        listPolyline = new ArrayList<Polyline>();

        tvContHamacasLibres = (TextView)findViewById(R.id.cont_hamacas_disponibles);
        tvContHamacasPendientes = (TextView)findViewById(R.id.cont_hamacas_pend_pago);
        tvContHamacasOcupadas = (TextView)findViewById(R.id.cont_hamacas_ocupadas);
        tvInfoBottom = (TextView)findViewById(R.id.tv_info_bottom);

        marcandoFila = false;
        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        mTitle = this.getTitle();
        volleyUtil = VolleyUtil.getInstance(this);
        requestQueue = volleyUtil.getRequestQueue();
        listMarker = new ArrayList<Marker>();
        progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            qrCode = data.getExtras().getCharSequence("SCAN_RESULT").toString();
        }
        else{
            qrCode = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.salir_menu) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        else if(id == R.id.eliminar_lineas_menu){
            eliminarLineas();
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Se bloquea el uso del botón back en la aplicación
    @Override
    public void onBackPressed() {

    }

    //GOOGLE MAP
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        CircleOptions options = new CircleOptions();

        options.center(coordenadasMap);
        options.radius(Globales.MAX_DISTANCE);

        //Ancho del borde del círculo
        options.strokeWidth(1);

        //Color del borde del círculo
        options.strokeColor(getResources().getColor(R.color.trans_white));

        //Color del interior del círculo
        options.fillColor(getResources().getColor(R.color.sand));

        CameraPosition.Builder cameraPositionBuilder = new CameraPosition.Builder();
        cameraPositionBuilder.target(coordenadasMap);
        cameraPositionBuilder.tilt(Globales.ANGULO_CAMARA);
        cameraPositionBuilder.zoom(Globales.NORMAL_ZOOM);
        CameraPosition cameraPosition = cameraPositionBuilder.build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition)); //newLatLngZoom(coordenadasMap, Globales.NORMAL_ZOOM));
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.addCircle(options);

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                if (cameraPosition.zoom < Globales.MIN_ZOOM) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenadasMap, Globales.MIN_ZOOM));
                }
                else if(SphericalUtil.computeDistanceBetween(coordenadasMap, cameraPosition.target) > Globales.MAX_DISTANCE){
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(coordenadasMap));
                }
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {

                if (!marcandoFila) {
                    MarkerDialog markerDialog = new MarkerDialog();
                    markerDialog.setMarker(marker);
                    markerDialog.setOpcionesDialog(getResources().getStringArray(R.array.marker_options));
                    markerDialog.show(getFragmentManager(), "tagSeleccionOpcionHamaca");
                }

                return false;
            }
        });

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                tvInfoBottom.setText("Recolocando Hamaca...");
                Toast.makeText(getApplicationContext(), "Coloque la hamaca en el lugar deseado.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                tvInfoBottom.setText("");

                Gson gson = new Gson();

                Location centerLocation = new Location("Centro");
                centerLocation.setLatitude(coordenadasMap.latitude);
                centerLocation.setLongitude(coordenadasMap.longitude);

                Location clickLocation = new Location("Click");
                clickLocation.setLatitude(marker.getPosition().latitude);
                clickLocation.setLongitude(marker.getPosition().longitude);

                if (centerLocation.distanceTo(clickLocation) <= Globales.MAX_DISTANCE) {
                    hamacaArrastrada = listHamaca.get(posHamacaCogida);
                    hamacaArrastrada.setLatitud(marker.getPosition().latitude);
                    hamacaArrastrada.setLongitud(marker.getPosition().longitude);

                    String hamacaArrastradaString = gson.toJson(hamacaArrastrada);
                    JSONObject jsonObject = null;
                    JsonObjectRequest jsonObjectRequest = null;

                    try {
                        jsonObject = new JSONObject(hamacaArrastradaString);

                        //Preparamos la petición de actualización de datos de la hamaca.
                        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Globales.URL_SAVEHAMACA, jsonObject, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                progressDialog.dismiss();
                                Gson gson = new Gson();
                                JsonParser jsonParser = new JsonParser();
                                JsonElement jsonElement = jsonParser.parse(response.toString());
                                Hamaca hamaca = gson.fromJson(jsonElement, Hamaca.class);

                                if (hamacaArrastrada.equals(hamaca)) {
                                    Toast.makeText(getApplicationContext(), "Hamaca rehubicada", Toast.LENGTH_SHORT).show();
                                } else {
                                    hamacaArrastrada.setLatitud(lastMarkerPosition.latitude);
                                    hamacaArrastrada.setLongitud(lastMarkerPosition.longitude);
                                    Toast.makeText(getApplicationContext(), "No se ha podido realizar la operación.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Error en la comunicación.", Toast.LENGTH_SHORT).show();
                            }
                        });

                        //Lanzamos la petición de actualización de los datos de la hamaca.
                        requestQueue.add(jsonObjectRequest);

                        progressDialog.setMessage("Guardando el cambio de posición...");
                        progressDialog.show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    hamacaArrastrada.setLatitud(lastMarkerPosition.latitude);
                    hamacaArrastrada.setLongitud(lastMarkerPosition.longitude);
                    marker.setPosition(lastMarkerPosition);
                    Toast.makeText(getApplicationContext(), "Acción cancelada. No puede ubicar la hamaca fuera del espacio reservado.", Toast.LENGTH_SHORT).show();
                }

                marker.setDraggable(false);
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (marcandoFila) {
                    if (puntoInicioFila == null) {
                        puntoInicioFila = new Location("InicioFila");
                        puntoInicioFila.setLatitude(latLng.latitude);
                        puntoInicioFila.setLongitude(latLng.longitude);

                        Toast.makeText(getApplicationContext(), "Pulse el punto final de la fila.", Toast.LENGTH_SHORT).show();
                    } else {
                        puntoFinFila = new Location("FinFila");
                        puntoFinFila.setLatitude(latLng.latitude);
                        puntoFinFila.setLongitude(latLng.longitude);

                        colocaFila(googleMap);

                        marcandoFila = false;
                        puntoInicioFila = null;
                        tvInfoBottom.setText("");
                    }
                }
            }
        });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (!marcandoFila) {
                    Location centerLocation = new Location("Centro");
                    centerLocation.setLatitude(coordenadasMap.latitude);
                    centerLocation.setLongitude(coordenadasMap.longitude);

                    Location clickLocation = new Location("Click");
                    clickLocation.setLatitude(latLng.latitude);
                    clickLocation.setLongitude(latLng.longitude);

                    if (centerLocation.distanceTo(clickLocation) <= Globales.MAX_DISTANCE) {
                        MarkerDialog markerDialog = new MarkerDialog();
                        markerDialog.setOpcionesDialog(getResources().getStringArray(R.array.marker_add));
                        markerDialog.setCoordenadasMarker(latLng);
                        markerDialog.setGoogleMap(googleMap);
                        markerDialog.show(getFragmentManager(), "tagSeleccionOpcionHamaca");
                    }
                }
            }
        });

        //Cargamos las hamacas como markers en el mapa
        for(Hamaca hamaca : listHamaca) {

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(hamaca.getLatitud(), hamaca.getLongitud()));

            if (hamaca.getEstado().equalsIgnoreCase("LIBRE")) {
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_action_beach_umbrella_and_hammock_24_green);
                markerOptions.icon(bitmapDescriptor);
            } else if (hamaca.getEstado().equalsIgnoreCase("PENDIENTE")) {
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_action_beach_umbrella_and_hammock_24_yellow);
                markerOptions.icon(bitmapDescriptor);
            } else if (hamaca.getEstado().equalsIgnoreCase("OCUPADA")) {
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_action_beach_umbrella_and_hammock_24);
                markerOptions.icon(bitmapDescriptor);
            }

            listMarker.add(googleMap.addMarker(markerOptions));
        }
    }

    public void colocaFila(GoogleMap googleMap){
        listNuevaFilaHamaca = new ArrayList<Hamaca>();
        listMarkerNuevaFilaHamaca = new ArrayList<Marker>();
        LatLng puntoIntermedio = null;
        PolylineOptions polylineOptions = new PolylineOptions();
        LatLng inicioFila = new LatLng(puntoInicioFila.getLatitude(), puntoInicioFila.getLongitude());
        LatLng finFila = new LatLng(puntoFinFila.getLatitude(), puntoFinFila.getLongitude());
        polylineOptions.add(inicioFila);
        polylineOptions.add(finFila);
        polylineOptions.color(getResources().getColor(R.color.fila));
        polylineOptions.width(Globales.ANCHO_FILA);

        listPolyline.add(googleMap.addPolyline(polylineOptions));

        MarkerOptions markerOptions = new MarkerOptions();
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_action_beach_umbrella_and_hammock_24_green);
        markerOptions.icon(bitmapDescriptor);

        for(double pos = 0; pos <= 1; pos += 0.05){
            puntoIntermedio = SphericalUtil.interpolate(inicioFila, finFila, pos);
            markerOptions.position(puntoIntermedio);

            hamacaNueva = new Hamaca();
            hamacaNueva.setId(-1);
            hamacaNueva.setId_empresa(Globales.ID_EMPRESA);
            hamacaNueva.setEstado("LIBRE");
            hamacaNueva.setLatitud(puntoIntermedio.latitude);
            hamacaNueva.setLongitud(puntoIntermedio.longitude);

            listNuevaFilaHamaca.add(hamacaNueva);

            listMarkerNuevaFilaHamaca.add(googleMap.addMarker(markerOptions));
        }

        ConfirmDialogNuevaFilaHamaca confirmDialogNuevaFilaHamaca = new ConfirmDialogNuevaFilaHamaca();
        confirmDialogNuevaFilaHamaca.setListMarkerNuevaFilaHamaca(listMarkerNuevaFilaHamaca);
        confirmDialogNuevaFilaHamaca.setListNuevaFilaHamaca(listNuevaFilaHamaca);
        confirmDialogNuevaFilaHamaca.show(getFragmentManager(), "tagDialogConfirmNuevaFilaHamaca");
    }

    private void eliminarLineas(){
        for(Polyline polyline : listPolyline){
            polyline.remove();
        }
    }

    public ArrayList<Marker> getlistMarker(){
        return listMarker;
    }

    public void setlastMarkerPosition(LatLng lastMarkerPosition){
        this.lastMarkerPosition = lastMarkerPosition;
    }

    public ArrayList<Alquiler> getListAlquiler() {
        return listAlquiler;
    }

    public List<Hamaca> getListHamaca() {
        return listHamaca;
    }

    public void setPosHamacaCogida(int posHamacaCogida){
        this.posHamacaCogida = posHamacaCogida;
    }

    public void setMarcandoFila(boolean marcandoFila) {
        this.marcandoFila = marcandoFila;
        tvInfoBottom.setText("Creando nueva Fila...");
    }

    public ArrayList<Marker> getListMarkerNuevaFilaHamaca() {
        return listMarkerNuevaFilaHamaca;
    }

    public List<Hamaca> getListNuevaFilaHamaca() {
        return listNuevaFilaHamaca;
    }

}

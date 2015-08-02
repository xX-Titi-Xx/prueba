package com.example.xtiti.hammock_rent.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.xtiti.hammock_rent.R;
import com.example.xtiti.hammock_rent.models.Alquiler;
import com.example.xtiti.hammock_rent.models.Hamaca;
import com.example.xtiti.hammock_rent.utils.Globales;
import com.example.xtiti.hammock_rent.utils.VolleyUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by xtiti on 9/07/15.
 */
public class Login extends AppCompatActivity {

    private RequestQueue requestQueue;
    private VolleyUtil volleyUtil;
    private ProgressDialog progressDialog;
    private ArrayList<Hamaca> listHamaca;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        volleyUtil = VolleyUtil.getInstance(this);
        requestQueue = volleyUtil.getRequestQueue();
        progressDialog = new ProgressDialog(this);
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

        return super.onOptionsItemSelected(item);
    }

    public void login(View view){

        JsonObjectRequest jsonObjectRequest = null;
        JSONObject jsonObject = null;
        final EditText etNombre = (EditText)findViewById(R.id.etUser);
        final EditText etPass= (EditText)findViewById(R.id.etPassword);

        try {
            jsonObject = new JSONObject();
            jsonObject.put("nombre", etNombre.getText().toString());
            jsonObject.put("pass", etPass.getText().toString());

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    Globales.URL_LOGIN, jsonObject,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();

                            try {
                                if(response.getBoolean("comprobacion")){

                                    Toast.makeText(getApplicationContext(), "Acceso concedido.", Toast.LENGTH_SHORT).show();

                                    //Empezamos a empacar los datos del intent MainActivity
                                    intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("latitudEmpresa", response.getDouble("latitud"));
                                    intent.putExtra("longitudEmpresa", response.getDouble("longitud"));

                                    JSONArray jsonArray = response.getJSONArray("listAlquiler");
                                    // Creates the json object which will manage the information received
                                    GsonBuilder builder = new GsonBuilder();

                                    // Register an adapter to manage the date types as long values
                                    builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                                        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                                            return new Date(json.getAsJsonPrimitive().getAsLong());
                                        }
                                    });

                                    Gson gson = builder.create();

                                    Alquiler[] arrAlquiler = gson.fromJson(jsonArray.toString(), Alquiler[].class);
                                    ArrayList<Alquiler> listAlquiler = new ArrayList<>(Arrays.asList(arrAlquiler));
                                    intent.putParcelableArrayListExtra("listAlquiler", listAlquiler);
                                    jsonArray = response.getJSONArray("listHamaca");

                                    gson = new Gson();
                                    Hamaca[] arrHamaca = gson.fromJson(jsonArray.toString(), Hamaca[].class);
                                    listHamaca = new ArrayList<Hamaca>(Arrays.asList(arrHamaca));
                                    intent.putParcelableArrayListExtra("listHamaca", listHamaca);

                                    Globales.ID_USUARIO = response.getInt("id_usuario");
                                    Globales.ID_EMPRESA = response.getInt("id_empresa");
                                    Globales.NOMBRE_EMPRESA = response.getString("nombre_empresa");
                                    Globales.MAX_DISTANCE = response.getDouble("distancia");
                                    Globales.USER = etNombre.getText().toString();
                                    Globales.PASSWORD = etPass.getText().toString();

                                    etPass.setText("");
                                    etNombre.setText("");
                                    etNombre.requestFocus();

                                    //Impide que la activity se reinicie al cerrarla.
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                    startActivity(intent);
                                }
                                else{
                                    etPass.setText("");
                                    Toast.makeText(getApplicationContext(), "Datos no válidos. Acceso denegado.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener(){

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Error en la comunicación...", Toast.LENGTH_SHORT).show();
                        }
                    });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Globales.TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            progressDialog.setMessage("Comprobando credenciales...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();

            requestQueue.add(jsonObjectRequest);
        }
        catch (JSONException e) {

            e.printStackTrace();
        }
    }
}

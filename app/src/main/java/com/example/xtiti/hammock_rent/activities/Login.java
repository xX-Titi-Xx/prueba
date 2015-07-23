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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.xtiti.hammock_rent.R;
import com.example.xtiti.hammock_rent.models.Hamaca;
import com.example.xtiti.hammock_rent.utils.Constantes;
import com.example.xtiti.hammock_rent.utils.VolleyUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
        final EditText etNombre = (EditText)findViewById(R.id.etUser);
        final EditText etPass= (EditText)findViewById(R.id.etPassword);

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nombre", etNombre.getText().toString());
            jsonObject.put("pass", etPass.getText().toString());

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    Constantes.URL_LOGIN, jsonObject,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();

                            try {
                                if(response.getBoolean("comprobacion")){
                                    etPass.setText("");
                                    etNombre.setText("");
                                    etNombre.requestFocus();

                                    Toast.makeText(getApplicationContext(), "Acceso concedido.", Toast.LENGTH_SHORT).show();

                                    //Empezamos a empacar los datos del intent MainActivity
                                    intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("latitudEmpresa", response.getDouble("latitudEmpresa"));
                                    intent.putExtra("longitudEmpresa", response.getDouble("longitudEmpresa"));

                                    Constantes.ID_EMPRESA = response.getInt("idEmpresa");
                                    Constantes.MAX_DISTANCE = response.getDouble("distancia");

                                    //Impide que la activity se reinicie al cerrarla.
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                    //====================================================================================//

                                    //Guardamos las credenciales para hacerlas accesibles desde el resto de activities.
                                    Constantes.USER = etNombre.getText().toString();
                                    Constantes.PASSWORD = etPass.getText().toString();

                                    JsonArrayRequest request = new JsonArrayRequest(Constantes.URL_LISTAHAMACAS, new Response.Listener<JSONArray>() {

                                        @Override
                                        public void onResponse(JSONArray response) {

                                            Gson gson = new Gson();
                                            JSONObject jsonObject = null;
                                            JsonParser jsonParser = null;
                                            JsonElement jsonElement = null;
                                            Hamaca hamaca = null;
                                            listHamaca = new ArrayList<Hamaca>();

                                            for (int cont = 0; cont < response.length(); cont++) {

                                                try {
                                                    jsonObject = response.getJSONObject(cont);
                                                    jsonParser = new JsonParser();
                                                    jsonElement = jsonParser.parse(jsonObject.toString());

                                                    hamaca = gson.fromJson(jsonElement, Hamaca.class);
                                                    listHamaca.add(hamaca);

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            intent.putParcelableArrayListExtra("listHamaca", listHamaca);
                                            //Quitamos el dialogo.
                                            progressDialog.dismiss();

                                            startActivity(intent);

                                        }
                                    }, new Response.ErrorListener() {

                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(getApplicationContext(), "No se ha podido realizar la operación.", Toast.LENGTH_SHORT);
                                            //Quitamos el dialogo.
                                            progressDialog.dismiss();
                                        }
                                    });

                                    //Configuramos el diálogo de proceso
                                    progressDialog.setIndeterminate(true);
                                    progressDialog.setMessage("Recuperando hamacas...");

                                    //Mostramos el diálogo de proceso de recuperación de hamacas
                                    progressDialog.show();

                                    //Lanzamos la petición de la lista de hamacas.
                                    requestQueue.add(request);
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

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Constantes.TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

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

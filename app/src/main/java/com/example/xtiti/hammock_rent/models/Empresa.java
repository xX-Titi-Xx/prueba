package com.example.xtiti.hammock_rent.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by xtiti on 9/07/15.
 */
public class Empresa {

    private String nombre;
    private int numHamacas;
    private LatLng coordenadas;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getNumHamacas() {
        return numHamacas;
    }

    public void setNumHamacas(int numHamacas) {
        this.numHamacas = numHamacas;
    }

    public LatLng getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(LatLng coordenadas) {
        this.coordenadas = coordenadas;
    }
}

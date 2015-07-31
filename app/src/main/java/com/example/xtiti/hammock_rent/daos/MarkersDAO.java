package com.example.xtiti.hammock_rent.daos;

import com.example.xtiti.hammock_rent.models.Hamaca;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xtiti on 9/07/15.
 */

public class MarkersDAO {

    private LatLng coordenadasMap = new LatLng(36.739714, -4.090825);

    public MarkersDAO(){}

    public List<Hamaca> getListHamacas(){

        List<Hamaca> listHamacas = new ArrayList<Hamaca>();
        //listHamacas.add(new Hamaca(new LatLng(1212, 656), Globales.ESTADO_HAMACA.LIBRE));

        return listHamacas;
    }
}

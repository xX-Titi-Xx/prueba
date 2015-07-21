package com.example.xtiti.hammock_rent.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xtiti on 9/07/15.
 */

public class Hamaca implements Parcelable{

    private String estado;
    private double latitud;
    private double longitud;
    private int id;
    private int id_empresa;

    public Hamaca(){}

    public Hamaca(Parcel in){

        id = in.readInt();
        id_empresa = in.readInt();
        estado = in.readString();
        latitud = in.readDouble();
        longitud = in.readDouble();

    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(int id_empresa) {
        this.id_empresa = id_empresa;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(id_empresa);
        dest.writeString(estado);
        dest.writeDouble(latitud);
        dest.writeDouble(longitud);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Hamaca createFromParcel(Parcel in) {
            return new Hamaca(in);
        }

        public Hamaca[] newArray(int size) {
            return new Hamaca[size];
        }
    };

    @Override
    public boolean equals(Object o) {

        if(o == null){
            return false;
        }
        else if(!(o instanceof Hamaca)){
            return false;
        }
        else{
            Hamaca hamaca = (Hamaca)o;

            if(hamaca.getLatitud() != this.latitud || hamaca.getLongitud() != this.longitud){
                return false;
            }
        }

        return true;
    }
}

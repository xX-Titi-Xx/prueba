package com.example.xtiti.hammock_rent.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xtiti on 16/07/15.
 */
public class Alquiler implements Parcelable{

    private int id;
    private int id_usuario;
    private int id_empresa;
    private int id_hamaca;
    private Date hora_comienzo;
    private Date hora_fin;

    public Alquiler(){}


    public Alquiler(Parcel in){

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

        id = in.readInt();
        id_empresa = in.readInt();
        id_hamaca = in.readInt();
        id_usuario = in.readInt();

        try {

            hora_comienzo = sdf.parse(in.readString());

        } catch (ParseException e) {

            hora_comienzo = null;

        }

        try {

            hora_fin = sdf.parse(in.readString());

        } catch (ParseException e) {

            hora_fin = null;

        }
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public Date getHora_comienzo() {
        return hora_comienzo;
    }

    public void setHora_comienzo(Date hora_comienzo) {
        this.hora_comienzo = hora_comienzo;
    }

    public Date getHora_fin() {
        return hora_fin;
    }

    public void setHora_fin(Date hora_fin) {
        this.hora_fin = hora_fin;
    }

    public int getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(int id_empresa) {
        this.id_empresa = id_empresa;
    }

    public int getId_hamaca() {
        return id_hamaca;
    }

    public void setId_hamaca(int id_hamaca) {
        this.id_hamaca = id_hamaca;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String fechaConFormato = null;

        dest.writeInt(id);
        dest.writeInt(id_empresa);
        dest.writeInt(id_hamaca);
        dest.writeInt(id_usuario);

        if(hora_comienzo != null){
            dest.writeString(sdf.format(hora_comienzo));
        }
        else{
            dest.writeString("");
        }

        if(hora_fin != null){
            dest.writeString(sdf.format(hora_fin));
        }
        else{
            dest.writeString("");
        }
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Alquiler createFromParcel(Parcel in) {
            return new Alquiler(in);
        }

        public Alquiler[] newArray(int size) {
            return new Alquiler[size];
        }
    };
}

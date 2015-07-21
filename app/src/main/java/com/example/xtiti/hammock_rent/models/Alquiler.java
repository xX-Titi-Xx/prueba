package com.example.xtiti.hammock_rent.models;

import java.util.Date;

/**
 * Created by xtiti on 16/07/15.
 */
public class Alquiler {
    private int id;
    private int id_usuario;
    private Date hora_comienzo;
    private Date hora_fin;

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
}

package com.example.xtiti.hammock_rent.utils;

/**
 * Created by xtiti on 8/07/15.
 */
public class Constantes {

    //Usuario y contraseña
    public static String USER;
    public static String PASSWORD;

    public static int ID_EMPRESA;

    public static final int ID_HAMACA_NOT_SAVED = -1;

    //CÁMARA ZOOM
    public static final float MAX_ZOOM = 20f;
    public static final float NORMAL_ZOOM = 19.5f;
    public static final float MIN_ZOOM = 18.0f;
    public static final float ANGULO_CAMARA = 45f;

    //DISTANCIA
    public static double MAX_DISTANCE = 30;

    public static float ANCHO_FILA= 0.5f;

    //RECURSOS
    public static final String PORT = "8080";
    //public static final String HOST = "http://192.168.2.3:" + PORT;
    public static final String HOST = "http://hammockrent.ddns.net:" + PORT;
    public static final String URL_SAVEHAMACA = HOST + "/Hammock_Rent/rest/savehamaca";
    public static final String URL_BAJAHAMACA = HOST + "/Hammock_Rent/rest/bajahamaca";
    public static final String URL_LISTAHAMACAS = HOST + "/Hammock_Rent/rest/listahamacas";
    public static final String URL_LOGIN = HOST + "/Hammock_Rent/rest/login";
    public static final String URL_HORA = HOST + "/Hammock_Rent/rest/horaserver";

    //CONEXIONES
    public static final int TIMEOUT = 20000;
}

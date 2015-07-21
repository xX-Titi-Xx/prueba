package com.example.xtiti.hammock_rent.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by xtiti on 14/07/15.
 */
public class VolleyUtil {

    private static VolleyUtil volleyUtil = null;
    //Este objeto es la cola que usará la aplicación
    private RequestQueue mRequestQueue;

    private VolleyUtil(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public static VolleyUtil getInstance(Context context) {
        if (volleyUtil == null) {
            volleyUtil = new VolleyUtil(context);
        }
        return volleyUtil;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

}

package com.example.xtiti.hammock_rent.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.xtiti.hammock_rent.R;
import com.example.xtiti.hammock_rent.activities.MainActivity;
import com.example.xtiti.hammock_rent.models.Alquiler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by xtiti on 2/08/15.
 */
public class AlquileresAdapter extends BaseAdapter {

    private ArrayList<Alquiler> listAlquiler;
    private MainActivity mainActivity;

    public AlquileresAdapter(MainActivity mainActivity, ArrayList<Alquiler> listAlquiler){
        this.listAlquiler = listAlquiler;
        this.mainActivity = mainActivity;
    }

    @Override
    public int getCount() {
        return listAlquiler.size();
    }

    @Override
    public Object getItem(int position) {
        return listAlquiler.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm:ss");

        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.alquiler, null);
        TextView tv = (TextView)view.findViewById(R.id.tv_id_hamaca);
        tv.setText(String.valueOf(listAlquiler.get(position).getId_hamaca()));
        tv = (TextView)view.findViewById(R.id.tv_usuario);
        tv.setText(Globales.USER);
        tv = (TextView)view.findViewById(R.id.tv_inicio_alquiler);

        try{
            tv.setText(sdf.format(listAlquiler.get(position).getHora_inicio()));
        }
        catch(Exception ex){
            tv.setText("");
        }

        tv = (TextView)view.findViewById(R.id.tv_fin_alquiler);

        try {
            tv.setText(sdf.format(listAlquiler.get(position).getHora_fin()));
        }
        catch(Exception ex){
            tv.setText("");
        }

        return view;
    }
}

package com.wangsun.android.livetracking.nav.locations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wangsun.android.livetracking.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by WANGSUN on 04-Feb-17.
 */

public class LocationAdapter extends ArrayAdapter<LocationClass> {

    public LocationAdapter(Context context, ArrayList<LocationClass> arrayList) {
        super(context, 0, arrayList);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        View viewItem = convertView;

        if(viewItem==null)
        {
            viewItem= LayoutInflater.from(getContext()).inflate(R.layout.location_items,parent,false);
        }

        LocationClass currentinfo=getItem(position);


        Date new_date=new Date(currentinfo.getTime());

        SimpleDateFormat sdf=new SimpleDateFormat("hh:mm aa dd-MM-yyyy");
        String temp_time=sdf.format(new_date);

        TextView time = (TextView) viewItem.findViewById(R.id.id_time);
        time.setText(String.valueOf(temp_time));

        TextView longi = (TextView) viewItem.findViewById(R.id.id_longi);
        longi.setText("Longitude: "+String.valueOf(currentinfo.getLongitude()));

        TextView lati = (TextView) viewItem.findViewById(R.id.id_lati);
        lati.setText("Latitude: "+String.valueOf(currentinfo.getLatitude()));

        return viewItem;
    }
}
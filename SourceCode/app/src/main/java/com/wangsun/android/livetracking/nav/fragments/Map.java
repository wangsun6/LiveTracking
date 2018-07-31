package com.wangsun.android.livetracking.nav.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wangsun.android.livetracking.R;
import com.wangsun.android.livetracking.nav.locations.DbHelper_gps;
import com.wangsun.android.livetracking.nav.locations.LocationAdapter;
import com.wangsun.android.livetracking.nav.locations.LocationClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Map extends Fragment implements OnMapReadyCallback{
    private GoogleMap mMap;

    SharedPreferences sp;

    private LocationManager locationManager;
    private LocationListener listener,listener2;


    public Map() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_map, container, false);

        sp= getContext().getSharedPreferences("myData", Context.MODE_PRIVATE);

        return  rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney;

        if(sp.getString("map_from","").equals("history")){

            Double temp_longi = Double.longBitsToDouble(sp.getLong("longi", Double.doubleToLongBits(0)));
            Double temp_lati = Double.longBitsToDouble(sp.getLong("lati", 0));

            sydney = new LatLng(temp_lati,temp_longi);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Your History"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
        else if(sp.getString("map_from","").equals("direct")){
            get_current_location();
        }
        else if(sp.getString("map_from","").equals("home")){
            get_last_location();
        }

    }

    public void get_last_location(){
        //it is recent location

        DbHelper_gps helperDb=new DbHelper_gps(getContext());
        SQLiteDatabase db =helperDb.getReadableDatabase();
        Cursor cursor=helperDb.getAllInfo(db);

        ArrayList<LocationClass> arrayList=new ArrayList();

        if(cursor.moveToFirst()){
            do{
                int id;
                double longi,lati;
                long time;

                id=cursor.getInt(0);
                longi=cursor.getDouble(1);
                lati=cursor.getDouble(2);
                time=cursor.getLong(3);

                arrayList.add(new LocationClass(id,longi,lati,time));
            }while(cursor.moveToNext());

            Collections.sort(arrayList, new Comparator<LocationClass>() {
                public int compare(LocationClass m1, LocationClass m2) {
                    return Integer.compare(m1.getId(), m2.getId());
                }
            });

            Collections.reverse(arrayList);

            LatLng sydney = new LatLng(arrayList.get(0).getLatitude(),arrayList.get(0).getLongitude());
            mMap.addMarker(new MarkerOptions().position(sydney).title("Your last location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        }
        else {
            LatLng sydney = new LatLng(16.17409569,75.65876573);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Developer Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            Toast.makeText(getContext(),"Turn on service to save new location.",Toast.LENGTH_LONG).show();
        }

        db.close();

    }

    public void get_current_location(){
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

        listener2 = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng sydney = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(sydney).title("Your current location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }
            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                //if GPS setting is off. It will redirect to the setting
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getContext().startActivity(i);
            }
        };


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng sydney = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(sydney).title("Your current location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                locationManager.removeUpdates(listener);

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 0, listener2);
                }

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }
            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                //if GPS setting is off. It will redirect to the setting
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getContext().startActivity(i);
            }
        };



        update_new_location();
    }


    void update_new_location() {
        Toast.makeText(getContext(),"Getting current location..",Toast.LENGTH_LONG).show();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, listener);
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(listener!=null)
            locationManager.removeUpdates(listener);
        if(listener2!=null)
            locationManager.removeUpdates(listener2);
    }
}

package com.wangsun.android.livetracking.nav.fragments;



import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.wangsun.android.livetracking.nav.Main;
import com.wangsun.android.livetracking.nav.locations.LocationAdapter;
import com.wangsun.android.livetracking.nav.locations.LocationClass;
import com.wangsun.android.livetracking.R;
import com.wangsun.android.livetracking.nav.locations.DbHelper_gps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 */
public class History extends Fragment implements AdapterView.OnItemClickListener {
    ArrayList<LocationClass> arrayList;
    LocationAdapter locationAdapter;
    ListView listView;

    SQLiteDatabase db;
    DbHelper_gps helperDb;
    Cursor cursor;


    public History() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        listView = (ListView) rootView.findViewById(R.id.listView_id);
        helperDb=new DbHelper_gps(getContext());
        listView.setOnItemClickListener(this);
        initialize();

        return rootView;
    }

    public void initialize(){
        arrayList = new ArrayList<>();

        db=helperDb.getReadableDatabase();
        helperDb.create_table(db);
        cursor=helperDb.getAllInfo(db);

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
        }

        db.close();

        Collections.sort(arrayList, new Comparator<LocationClass>() {
            public int compare(LocationClass m1, LocationClass m2) {
                return Long.compare(m1.getTime(), m2.getTime());
            }
        });

        Collections.reverse(arrayList);


        locationAdapter=new LocationAdapter(getContext(),arrayList);
        listView.setAdapter(locationAdapter);

    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Double temp_longi=arrayList.get(position).getLongitude();
        Double temp_lati=arrayList.get(position).getLatitude();

        ((Main)getActivity()).call_by_history(temp_longi,temp_lati);

    }
}

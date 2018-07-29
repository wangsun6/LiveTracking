package com.wangsun.android.livetracking.nav.fragments;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wangsun.android.livetracking.R;
import com.wangsun.android.livetracking.nav.Main;
import com.wangsun.android.livetracking.services.Alarm_broadcast;

import static android.content.Context.ALARM_SERVICE;
import static android.graphics.Color.parseColor;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment implements View.OnClickListener{
    Button btn_service;

    SharedPreferences sp;
    SharedPreferences.Editor editor;


    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        btn_service=(Button) rootView.findViewById(R.id.id_service);
        btn_service.setOnClickListener(this);

        sp=getContext().getSharedPreferences("myData", Context.MODE_PRIVATE);
        if(sp.getString("service","").equals("on")){
            btn_service.setBackgroundColor(parseColor("#00c853"));
        }
        else {
            btn_service.setBackgroundColor(parseColor("#cfd8dc"));
        }

        return  rootView;
    }

    @Override
    public void onClick(View v) {
        if(v==btn_service){

            // INITIALIZE INTENT
            Intent intent=new Intent(getContext(),Alarm_broadcast.class);
            //PASS CONTEXT,YOUR PRIVATE REQUEST CODE,INTENT OBJECT AND FLAG
            PendingIntent pi= PendingIntent.getBroadcast(getContext(),0,intent,0);
            AlarmManager alarmManager= (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
            //will trigger every5 minnutes

            if(sp.getString("service","").equals("on")){
                //if on then turn off
                btn_service.setBackgroundColor(parseColor("#cfd8dc"));

                alarmManager.cancel(pi);

                editor=sp.edit();
                editor.putString("service","off");
                editor.apply();

                Toast.makeText(getContext(), "Service stopped", Toast.LENGTH_SHORT).show();

                ((Main)getActivity()).call_by_home();

            }
            else {
                //if off then turn on
                btn_service.setBackgroundColor(parseColor("#00c853"));

                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+2000,2*60000,pi);

                editor=sp.edit();
                editor.putString("service","on");
                editor.apply();

                Toast.makeText(getContext(), "Service started", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

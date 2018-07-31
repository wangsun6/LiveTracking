package com.wangsun.android.livetracking.nav;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.wangsun.android.livetracking.R;
import com.wangsun.android.livetracking.login.Login;
import com.wangsun.android.livetracking.nav.fragments.History;
import com.wangsun.android.livetracking.nav.fragments.Home;
import com.wangsun.android.livetracking.nav.fragments.Map;
import com.wangsun.android.livetracking.nav.locations.DbHelper_gps;
import com.wangsun.android.livetracking.services.Alarm_broadcast;

public class Main extends AppCompatActivity {
    FragmentManager fragmentManager = getSupportFragmentManager();
    //FragmentTransaction fragmentTransaction;

    String fragment_selector;

    BottomNavigationView navigation;

    Menu menu_delete;

    SharedPreferences sp;
    SharedPreferences.Editor editor;


    SQLiteDatabase db;
    DbHelper_gps helperDb;
    Cursor cursor;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            editor=sp.edit();
            editor.putString("map_from","direct");
            editor.apply();

            //fragmentTransaction = fragmentManager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.nav_home:
                    if(!fragment_selector.equals("home")){
                        fragment_selector="home";
                        fragmentManager.beginTransaction().replace(R.id.content,new Home()).commit();
                        menu_delete.getItem(0).setVisible(false);
                    }
                    return true;
                case R.id.nav_map:
                    if(!fragment_selector.equals("map")){
                        fragment_selector="map";

                        editor=sp.edit();
                        editor.putString("map_from","direct");
                        editor.apply();

                        fragmentManager.beginTransaction().replace(R.id.content,new Map()).commit();
                        menu_delete.getItem(0).setVisible(false);
                    }

                    if(!navigation.getMenu().getItem(1).isChecked()){
                        fragment_selector="map";

                        editor=sp.edit();
                        editor.putString("map_from","direct");
                        editor.apply();
                        //fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentManager.beginTransaction().replace(R.id.content,new Map()).commit();
                        menu_delete.getItem(0).setVisible(false);
                    }
                    return true;

                case R.id.nav_history:
                    if(!fragment_selector.equals("history")){
                        fragment_selector="history";
                        fragmentManager.beginTransaction().replace(R.id.content,new History()).commit();
                        menu_delete.getItem(0).setVisible(true);
                    }

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //fragmentTransaction = fragmentManager.beginTransaction();
        fragment_selector="home";
        fragmentManager.beginTransaction().replace(R.id.content,new Home()).commit();
        navigation.getMenu().getItem(0).setChecked(true);

        helperDb=new DbHelper_gps(Main.this);

        sp = getSharedPreferences("myData",MODE_PRIVATE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        menu_delete = menu;
        menu_delete.getItem(0).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout) {

            AlertDialog.Builder welcomeBuilder = new AlertDialog.Builder(this);
            welcomeBuilder.setTitle("Log Out?")
                    .setMessage("Next time when U open app, u have to Login again.")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            logOut();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            welcomeBuilder.show();
        }
        else if (id == R.id.nav_delete) {

            AlertDialog.Builder welcomeBuilder = new AlertDialog.Builder(this);
            welcomeBuilder.setTitle("Delete?")
                    .setMessage("Locations will be deleted permanently.")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            delete_locations();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            welcomeBuilder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void delete_locations(){

        db=helperDb.getReadableDatabase();
        helperDb.create_table(db);
        cursor=helperDb.getAllInfo(db);

        if(cursor.moveToFirst()){
            //table is not empty
            helperDb.deleteAll(db);
            Toast.makeText(Main.this,"Locations deleted.", Toast.LENGTH_LONG).show();

            //fragmentTransaction = fragmentManager.beginTransaction();
            fragmentManager.beginTransaction().replace(R.id.content,new History()).commit();

        }
        else {
            Toast.makeText(Main.this,"No data available.",Toast.LENGTH_LONG).show();
        }

        db.close();
    }

    public void logOut(){
        editor = sp.edit();
        editor.putBoolean("login",false);
        editor.putString("service","off");
        editor.apply();

        //disable the service if it is running
        Intent intent=new Intent(Main.this,Alarm_broadcast.class);
        PendingIntent pi= PendingIntent.getBroadcast(Main.this,0,intent,0);
        AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pi);

        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        AlertDialog.Builder welcomeBuilder = new AlertDialog.Builder(this);
        welcomeBuilder.setTitle("Exit")
                .setMessage("Close App?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        welcomeBuilder.show();
    }

    public void call_by_history(double longi,double lati){
        editor=sp.edit();
        editor.putString("map_from","history");
        editor.putLong("longi",Double.doubleToRawLongBits(longi));
        editor.putLong("lati",Double.doubleToRawLongBits(lati));
        editor.apply();

        //fragmentTransaction = fragmentManager.beginTransaction();
        fragmentManager.beginTransaction().replace(R.id.content,new Map()).commit();
        fragment_selector="map";
        menu_delete.getItem(0).setVisible(false);

    }

    public void call_by_home(){
        editor=sp.edit();
        editor.putString("map_from","home");
        editor.apply();

        //fragmentTransaction = fragmentManager.beginTransaction();
        fragmentManager.beginTransaction().replace(R.id.content,new Map()).commit();
        fragment_selector="map";
        menu_delete.getItem(0).setVisible(false);

    }

}

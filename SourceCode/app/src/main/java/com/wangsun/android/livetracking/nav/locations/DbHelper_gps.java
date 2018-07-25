package com.wangsun.android.livetracking.nav.locations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.wangsun.android.livetracking.nav.Main;

/**
 * Created by WANGSUN on 24-July-18.
 */

public class DbHelper_gps extends SQLiteOpenHelper {

    private static final String DataBaseName="LiveTracking.DB";
    private static final int DataBase_version=1;
    private static final String Create_query=
            "CREATE TABLE IF NOT EXISTS "+ Contractor_gps.userInfo.TABLE+"("+

                    Contractor_gps.userInfo.ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    Contractor_gps.userInfo.LONGITUDE+" REAL,"+
                    Contractor_gps.userInfo.LATITUDE+" REAL,"+
                    Contractor_gps.userInfo.TIME+" INTEGER);";

    public DbHelper_gps(Context context) {
        super(context, DataBaseName, null, DataBase_version);
        //Log.e("DB oparation","Database created....");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Create_query);
        //Log.e("DB oparation","table created...");
    }

    public void create_table(SQLiteDatabase db){
        db.execSQL(Create_query);
    }

    public void addvalues(double longitude,double  latitude,long time, SQLiteDatabase db){
        ContentValues contentValues=new ContentValues();

        contentValues.put(Contractor_gps.userInfo.LONGITUDE,longitude);
        contentValues.put(Contractor_gps.userInfo.LATITUDE,latitude);
        contentValues.put(Contractor_gps.userInfo.TIME,time);

        db.insert(Contractor_gps.userInfo.TABLE,null,contentValues);
    }

    public Cursor getAllInfo(SQLiteDatabase db){
        Cursor cursor;
        String[] projection={
                Contractor_gps.userInfo.ID,
                Contractor_gps.userInfo.LONGITUDE,
                Contractor_gps.userInfo.LATITUDE,
                Contractor_gps.userInfo.TIME};

        cursor=db.query(Contractor_gps.userInfo.TABLE,projection,null,null,null,null,null);
        return cursor;
    }

    public void deleteAll(SQLiteDatabase db){
        db.execSQL("delete from "+ Contractor_gps.userInfo.TABLE);
    }

    public void deleteRow(String str_id, SQLiteDatabase db) {
        String selection = Contractor_gps.userInfo.ID+ " LIKE?";
        String[] temp_id = {str_id};
        db.delete(Contractor_gps.userInfo.TABLE, selection, temp_id);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

package com.wangsun.android.livetracking.login;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by WANGSUN on 24-July-18.
 */

public class DbHelper_info extends SQLiteOpenHelper {

    private static final String DataBaseName="LiveTracking.DB";
    private static final int DataBase_version=1;
    private static final String Create_query=
            "CREATE TABLE IF NOT EXISTS "+ Contractor_info.userInfo.TABLE+"("+

                    Contractor_info.userInfo.USERNAME+" TEXT,"+
                    Contractor_info.userInfo.MOBILE+" TEXT,"+
                    Contractor_info.userInfo.PASSWORD+" TEXT);";

    public DbHelper_info(Context context) {
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


    public void addvalues(String username,String  mobile,String password, SQLiteDatabase db){
        ContentValues contentValues=new ContentValues();


        contentValues.put(Contractor_info.userInfo.USERNAME,username);
        contentValues.put(Contractor_info.userInfo.MOBILE,mobile);
        contentValues.put(Contractor_info.userInfo.PASSWORD,password);

        db.insert(Contractor_info.userInfo.TABLE,null,contentValues);
    }

    public Cursor getAllInfo(SQLiteDatabase db){
        Cursor cursor;
        String[] projection={

                Contractor_info.userInfo.USERNAME,
                Contractor_info.userInfo.MOBILE,
                Contractor_info.userInfo.PASSWORD};

        cursor=db.query(Contractor_info.userInfo.TABLE,projection,null,null,null,null,null);
        return cursor;
    }


    public Cursor getPassword(String type, SQLiteDatabase db) {
        Cursor cursor;
        String[] projection =
                {
                        Contractor_info.userInfo.PASSWORD};

        String selection = Contractor_info.userInfo.USERNAME + " LIKE?";
        String[] temp_type = {type};
        cursor = db.query(Contractor_info.userInfo.TABLE, projection, selection, temp_type, null, null, null);
        return cursor;
    }

    public Cursor getUsername(SQLiteDatabase db) {
        Cursor cursor;
        String[] projection = {Contractor_info.userInfo.USERNAME};

        cursor=db.query(Contractor_info.userInfo.TABLE,projection,null,null,null,null,null);
        return cursor;
    }

    public void deleteRow(String str_id, SQLiteDatabase db) {
        String selection = Contractor_info.userInfo.USERNAME+ " LIKE?";
        String[] temp_id = {str_id};
        db.delete(Contractor_info.userInfo.TABLE, selection, temp_id);
    }

    public int updateValues(String id_, String seen, SQLiteDatabase db){
        ContentValues contentValues=new ContentValues();
        //contentValues.put(Contractor_info.userInfo.SEEN,seen);
        //contentValues.put(Contractor_info.userInfo.PASSWORD,password);
        String selection = Contractor_info.userInfo.USERNAME + " LIKE?";
        String[] id = {id_};
        int count=db.update(Contractor_info.userInfo.TABLE,contentValues,selection,id);
        return count;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

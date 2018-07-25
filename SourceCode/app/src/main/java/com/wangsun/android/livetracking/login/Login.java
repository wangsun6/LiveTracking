package com.wangsun.android.livetracking.login;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wangsun.android.livetracking.R;
import com.wangsun.android.livetracking.nav.Main;


public class Login extends AppCompatActivity implements View.OnClickListener {
    LinearLayout layout_signup,layout_login;
    EditText edt_new_username,edt_new_password,edt_new_mobile, edt_username,edt_password;
    Button btn_signUp,btn_login;
    TextView txt_change_signup,txt_change_login;

    String str_username,str_password,str_mobile;

    ColorStateList original,purple;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    SQLiteDatabase db;
    DbHelper_info helperDb;
    Cursor cursor;

    final static int MY_REQUEST_ACCESS_FINE_LOCATION = 70;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize all views
        txt_change_signup=(TextView) findViewById(R.id.id_change_signup);
        txt_change_login=(TextView) findViewById(R.id.id_chnage_login);

        layout_signup=(LinearLayout) findViewById(R.id.id_layout_signup);
        layout_login=(LinearLayout) findViewById(R.id.id_layout_login);

        //SignUp
        edt_new_username=(EditText) findViewById(R.id.id_new_username);
        edt_new_password=(EditText) findViewById(R.id.id_new_password);
        edt_new_mobile=(EditText) findViewById(R.id.id_new_mobile);
        btn_signUp=(Button) findViewById(R.id.id_new_signup);

        //Login
        edt_username=(EditText) findViewById(R.id.id_username);
        edt_password=(EditText) findViewById(R.id.id_password);
        btn_login=(Button) findViewById(R.id.id_signup);

        //setting onclick listener
        btn_signUp.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        txt_change_login.setOnClickListener(this);
        txt_change_signup.setOnClickListener(this);


        sp = getSharedPreferences("myData", Context.MODE_PRIVATE);

        //toggle colors of sign up/ login button when change between them
        purple = txt_change_signup.getTextColors();
        original =  txt_change_login.getTextColors();

        helperDb=new DbHelper_info(Login.this);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }
        
        Runtime_permission();

    }

    @Override
    public void onClick(View v) {
        View view =this.getCurrentFocus();

        if(view!=null){
            InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }

        if(v==txt_change_signup){
            txt_change_signup.setTextColor(purple);
            txt_change_login.setTextColor(original);
            layout_signup.setVisibility(View.VISIBLE);
            layout_login.setVisibility(View.GONE);
        }
        else if(v==txt_change_login){
            txt_change_signup.setTextColor(original);
            txt_change_login.setTextColor(purple);
            layout_signup.setVisibility(View.GONE);
            layout_login.setVisibility(View.VISIBLE);
        }
        else if(v==btn_signUp){

            if(check_permission()){
                str_username=edt_new_username.getText().toString();
                str_password=edt_new_password.getText().toString();
                str_mobile=edt_new_mobile.getText().toString();
                check_error(0);
            }
            else
                Toast.makeText(Login.this,"You need to allow permission to start the app.",Toast.LENGTH_LONG).show();


        }
        else if(v==btn_login){

            if(check_permission()){
                str_username=edt_username.getText().toString();
                str_password=edt_password.getText().toString();
                check_error(1);
            }
            else
                Toast.makeText(Login.this,"You need to allow permission to start the app.",Toast.LENGTH_LONG).show();

        }
    }

    public void check_error(int num){
        str_username=str_username.trim();
        str_password=str_password.trim();

        if(!str_username.contains(" ") && !str_password.contains(" ")){
            if(str_username.length() > 3){
                if(str_password.length() > 3){
                    if(num==0){
                        str_mobile=str_mobile.trim();
                        if(str_mobile.length()!=10 || str_mobile.contains(" ")){
                            Toast.makeText(Login.this,"Invalid phone number.",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        signUp();
                    }
                    else
                        login();
                }
                else
                    Toast.makeText(Login.this,"Password length > 3.",Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(Login.this,"Username length > 3.",Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(Login.this,"Space not allowed.",Toast.LENGTH_SHORT).show();
    }

    public void signUp(){
        AlertDialog.Builder builder_confirm = new AlertDialog.Builder(Login.this);
        builder_confirm.setTitle("Please confirm.");
        builder_confirm.setMessage("Username: "+str_username+"\nPassword: "+str_password);
        builder_confirm.setCancelable(false);
        builder_confirm.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                check_username();
            }
        });
        builder_confirm.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder_confirm.show();
    }
    
    public void check_username(){
        db=helperDb.getReadableDatabase();
        helperDb.create_table(db); //create table only if it is not created

        //we will use getPassword method..but it dosn't matter, only goal is to check whether it return any value
        //if return value then username is already exist
        cursor=helperDb.getPassword(str_username,db);
        if(cursor.moveToFirst()){
            do{
                Toast.makeText(Login.this,"Username already used.",Toast.LENGTH_SHORT).show();
                return;
            }while(cursor.moveToNext());
        }
        else {
            db = helperDb.getWritableDatabase();
            helperDb.addvalues(str_username,str_mobile,str_password,db);
            Toast.makeText(Login.this,"Account created!",Toast.LENGTH_SHORT).show();

            editor=sp.edit();
            editor.putBoolean("login",true);
            editor.putString("username",str_username);
            editor.apply();
            startActivity(new Intent(Login.this,Main.class));
        }
        db.close();

    }

    public void login(){
        db=helperDb.getReadableDatabase();
        helperDb.create_table(db); //create table only if it is not created

        cursor=helperDb.getPassword(str_username,db);
        String temp_password;

        if(cursor.moveToFirst()){
            do{
                temp_password=cursor.getString(0);
            }while(cursor.moveToNext());

            if(str_password.equals(temp_password)){
                editor=sp.edit();
                editor.putBoolean("login",true);
                editor.putString("username",str_username);
                editor.apply();
                startActivity(new Intent(Login.this,Main.class));
            }
            else
                Toast.makeText(Login.this,"Wrong Password.",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(Login.this,"Username not exist.",Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    public void Runtime_permission(){
        if(check_permission()){
            //if already login go to next activity
            if(sp.getBoolean("login",false)){
                String temp_user=sp.getString("username","");
                Toast.makeText(Login.this,"Welcome "+temp_user,Toast.LENGTH_LONG).show();
                startActivity(new Intent(Login.this,Main.class));
            }
        }
    }

    //runtime permission
    public boolean check_permission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else
            requestFineLocation();

        return false; //when above code do not execute
    }

    private void requestFineLocation(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
        }

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_REQUEST_ACCESS_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Runtime_permission();
                }
                else
                    requestFineLocation();
                break;
        }
    }
}

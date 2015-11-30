package com.electric_water_heaters_controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.content.SharedPreferences;

public class setting extends AppCompatActivity {
    private final String DEBUG_TAG = "Activity01";
    private EditText mEditText_ip = null;
    private EditText mEditText_port = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        mEditText_ip = (EditText) findViewById(R.id.editText_ip);
        mEditText_port = (EditText) findViewById(R.id.editText_port);
        mEditText_ip.setInputType(InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_NUMBER_FLAG_DECIMAL
                | InputType.TYPE_NUMBER_FLAG_SIGNED
                | InputType.TYPE_CLASS_TEXT);
    }
    protected void onStart () {
        //取得SharedPreference設定("Preference"為設定檔的名稱)
        SharedPreferences settings = getSharedPreferences("Preference", 0);
        //取出name屬性的字串
        String IP = settings.getString("IP", "");
        if(IP != "") mEditText_ip.setText(IP);
        String Port = settings.getString("Port", "");
        if(Port != "") mEditText_port.setText(Port);
        Log.e(DEBUG_TAG, "Settings onStart ");
        super.onStart();
    }
    protected void onPause() {
        //取得SharedPreference設定("Preference"為設定檔的名稱)
        SharedPreferences settings = getSharedPreferences("Preference", 0);
        //置入name屬性的字串
        settings.edit().putString("IP", mEditText_ip.getText().toString()).commit();
        settings.edit().putString("Port", mEditText_port.getText().toString()).commit();
        //Log.e(DEBUG_TAG, "Settings onPause");
        super.onPause();
    }
    protected void onStop() {
        //Log.e(DEBUG_TAG, "Settings onStop");
        super.onStop();
    }
    protected void onDestroy() {
        //Log.e(DEBUG_TAG, "Settings onDestroy");
        super.onDestroy();
    }
}

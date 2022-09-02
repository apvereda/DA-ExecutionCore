package com.apvereda.digitalavatars.virtualbeacons;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import bsh.EvalError;
import bsh.Interpreter;

import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.apvereda.db.Contact;
import com.apvereda.db.Entity;
import com.apvereda.db.Value;
import com.apvereda.digitalavatars.R;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;


public class MainActivity extends AppCompatActivity {


    public static Context cnt;
    private SharedPreferences settings;
    //private VirtualProfile myVirtualProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_beacons);
        cnt = this;
        Button start = (Button) findViewById(R.id.startBtn);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReceiverActivity.class);
                startActivity(intent);
            }
        });
        Button stop = (Button) findViewById(R.id.stopBtn);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SenderActivity.class);
                startActivity(intent);
            }
        });

        //request location permissions

        // No explanation needed, we can request the permission.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    99);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_restart_server){
            //restartServer();
        }
        return super.onOptionsItemSelected(item);
    }

    private void restartServer(){
        new CallAPI().execute(CallAPI.URL);
    }

}



package com.apvereda.digitalavatars.virtualbeacons;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.apvereda.digitalavatars.R;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bsh.EvalError;
import bsh.Interpreter;

public class ReceiverActivity extends AppCompatActivity {

    AdapterForListView adapter;
    private BeaconManager beaconManager;
    ListView beaconsListView;
    // Progress Dialog
    public ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    private List<Beacon> beaconsList;
    private MyBeaconManager myBeaconManager;
    private Alarm myAlarm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);

        Bundle bundle = getIntent().getExtras();
        beaconsList = new ArrayList<>();
        beaconsListView = (ListView) ReceiverActivity.this.findViewById(R.id.list);
        beaconsListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Beacon beacon = (Beacon) adapter.getItem(position);
                String file_url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
                //async task que llamará a executeScript en el onPostExecute()
                myBeaconManager.getDownloadFileFromURLInstance().execute(file_url);
            }
        });

        myBeaconManager = MyBeaconManager.getInstance();
        myBeaconManager.setParentReceiverActivity(this);

        adapter = new AdapterForListView(this);
        adapter.setData(beaconsList);
        beaconsListView.setAdapter(adapter);

        myBeaconManager.startBeaconScan();
        myAlarm = Alarm.getInstance();
        myAlarm.setAutomaticBeaconScanning(getApplicationContext(),1*20*1000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_receiver_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_restart_bluetooth_scan) {
            myBeaconManager.rescan();
            Toast.makeText(this, "Refreshing", Toast.LENGTH_SHORT).show();
            return true;
        }else if(item.getItemId() == R.id.action_restart_server){
            //restartServer();
        }
        return super.onOptionsItemSelected(item);
    }


    public void executeScript(String url) throws EvalError {
        //leer fichero
        int ch;
        StringBuffer fileContent = new StringBuffer("");
        FileInputStream fis;
        try {
            fis = openFileInput("beacon_script.java");
            try {
                while( (ch = fis.read()) != -1)
                    fileContent.append((char)ch);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final String data = new String(fileContent);
        int index = data.indexOf("header:interval=") + ("header:interval=".length());
        String interval = data.substring(index, index+2);

        myBeaconManager = MyBeaconManager.getInstance();
        myBeaconManager.startScriptInterval(url, interval);


        final Interpreter i = new Interpreter();
        //Set Variables
        i.set("dac", new DigitalAvatarController(this));
        //Interpret and execute code
        Thread tempThread = new Thread(){
            public void run(){
                try {
                    i.eval(data);
                } catch (EvalError evalError) {
                    evalError.printStackTrace();
                }
            }
        };
        tempThread.start();

    };

    private void restartServer(){
        new CallAPI().execute(CallAPI.URL);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);

                if(!this.isFinishing()) {
                    //don't show dialog if the activity is not running
                    pDialog.show();
                }
                return pDialog;
            default:
                return null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        myBeaconManager.unbind();
    }

    public void updateList(final List<Beacon> beacons) {
        this.beaconsList = beacons;
        runOnUiThread(new Runnable() {
            public void run() {
                adapter.setData(beaconsList);
                beaconsListView.setAdapter(adapter);
            }
        });
    }

}

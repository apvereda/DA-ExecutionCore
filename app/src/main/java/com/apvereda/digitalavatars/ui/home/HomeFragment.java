package com.apvereda.digitalavatars.ui.home;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.apvereda.digitalavatars.R;
import com.apvereda.utils.SiddhiService;
import com.apvereda.digitalavatars.virtualbeacons.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.wso2.siddhi.android.platform.SiddhiAppService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private String appName="";
    View root;
    List<String> apps;
    List<String> appnames;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        setRetainInstance(true);
        if(savedInstanceState !=null) {
            //textView.setText(savedInstanceState.getString("text"));
            if(savedInstanceState.getInt("btn_play")==View.VISIBLE) {
                root.findViewById(R.id.fabplay).setVisibility(View.VISIBLE);
                root.findViewById(R.id.fabstop).setVisibility(View.GONE);
            } else{
                root.findViewById(R.id.fabplay).setVisibility(View.GONE);
                root.findViewById(R.id.fabstop).setVisibility(View.VISIBLE);
            }
        }
        apps = new ArrayList<String>();
        appnames = new ArrayList<String>();
        //SiddhiService.getServiceConnection(getActivity().getApplicationContext());
        Button btntripshare = root.findViewById(R.id.tripsharebtn);
        btntripshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }
        });
        FloatingActionButton fabplay = root.findViewById(R.id.fabplay);
        fabplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startApp();
                    root.findViewById(R.id.fabplay).setVisibility(View.GONE);
                    root.findViewById(R.id.fabstop).setVisibility(View.VISIBLE);
                    Snackbar.make(view, "Siddhi app running", Snackbar.LENGTH_LONG).show();
                    //textView.setText("Siddhi app running");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        FloatingActionButton fabstop = root.findViewById(R.id.fabstop);
        fabstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    stopApp();
                    root.findViewById(R.id.fabplay).setVisibility(View.VISIBLE);
                    root.findViewById(R.id.fabstop).setVisibility(View.GONE);
                    Snackbar.make(view, "Siddhi app stopped", Snackbar.LENGTH_LONG).show();
                    //textView.setText("Siddhi app stopped");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        root.findViewById(R.id.fabplay).setVisibility(View.VISIBLE);
        root.findViewById(R.id.fabstop).setVisibility(View.GONE);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState !=null) {
            //TextView textView = root.findViewById(R.id.text_home);
            //textView.setText(savedInstanceState.getString("text"));
            if(savedInstanceState.getInt("btn_play")==View.VISIBLE) {
                root.findViewById(R.id.fabplay).setVisibility(View.VISIBLE);
                root.findViewById(R.id.fabstop).setVisibility(View.GONE);
            } else{
                root.findViewById(R.id.fabplay).setVisibility(View.GONE);
                root.findViewById(R.id.fabstop).setVisibility(View.VISIBLE);
            }
        }

    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    public void onSaveInstanceState(Bundle outState) {
        //TextView textView = root.findViewById(R.id.text_home);
        outState.putInt("btn_play", root.findViewById(R.id.fabplay).getVisibility());
        //outState.putString("text", textView.getText().toString());

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    private void startApp() throws RemoteException {
        String app1 = "";
        apps.add(app1);
        for(String app: apps) {
            appnames.add(SiddhiService.getServiceConnection(getActivity().getApplicationContext()).startSiddhiApp(app));
        }
    }

    private void stopApp() throws RemoteException{
        for( String app : appnames) {
            SiddhiService.getServiceConnection(getContext()).stopSiddhiApp(app);
        }
        appnames = new ArrayList<String>();
    }

    public void removeApps() {
        apps = new ArrayList<String>();
    }

    public void readFile(){
        apps = new ArrayList<String>();
        File fileDirectory = new File(getContext().getExternalFilesDir(null)+"/SiddhiApps");
        if(!fileDirectory.exists()){
            fileDirectory.mkdir();
        }
        File[] dirFiles = fileDirectory.listFiles();
        Snackbar.make(this.root, "Leyendo Siddhi Apps", Snackbar.LENGTH_LONG).show();
        String app = "";
        String line;
        for (File f : dirFiles) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));
                while ((line = br.readLine()) != null){
                    app += line;
                }
                apps.add(app);
                System.out.println(app);
                app = "";
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

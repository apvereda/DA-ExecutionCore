package com.apvereda.digitalavatars.ui.trustlist;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.apvereda.db.Avatar;
import com.apvereda.db.TrustOpinion;
import com.apvereda.digitalavatars.R;

import com.apvereda.utils.DigitalAvatar;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class TrustListFragment extends AppCompatActivity {
    DigitalAvatar da;
    TrustItemAdapter adapter;
    ListView list;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_trust_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        da = DigitalAvatar.getDA();
        List<TrustOpinion> trips = TrustOpinion.getOpinionbyTruster(Avatar.getAvatar().getUID());
        for(TrustOpinion t : trips) t.setTruster("me");
        trips.addAll(TrustOpinion.getReferralOpinions());
        adapter = new TrustItemAdapter(this, trips);
        list = (ListView) findViewById(R.id.listTrust);
        list.setAdapter(adapter);
    }

    public void updateTrips(){
        List<TrustOpinion> trips = TrustOpinion.getOpinionbyTruster(Avatar.getAvatar().getUID());
        for(TrustOpinion t : trips) t.setTruster("me");
        trips.addAll(TrustOpinion.getReferralOpinions());
        Log.i("Digital Avatar", "Estos son los trust que pongo en la lista:"+trips);
        adapter.setData(trips);
        list.setAdapter(adapter);
    }
}


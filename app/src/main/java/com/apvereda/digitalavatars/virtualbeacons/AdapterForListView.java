package com.apvereda.digitalavatars.virtualbeacons;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.apvereda.digitalavatars.R;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.util.List;

public class AdapterForListView extends BaseAdapter {
	Activity context;
	List<Beacon> data;

	public AdapterForListView(Activity context) {
		super();
		this.context = context;
	}

	public void setData(List<Beacon> data) {
		this.data = data;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.listitem, null);

		TextView lblAddress = (TextView) convertView.findViewById(R.id.lbladdress);
		byte[] decodedurl = data.get(position).getId1().toByteArray();
		if(decodedurl.length >0) {
			lblAddress.setText(UrlBeaconUrlCompressor.uncompress(decodedurl));
		}else{
			lblAddress.setText(data.get(position).getId1().toString());
		}
		lblAddress.setText(data.get(position).getBluetoothName());


		return (convertView);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}


}

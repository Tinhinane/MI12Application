package com.example.tinhinane.mi12application.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tinhinane.mi12application.Models.Beacon;
import com.example.tinhinane.mi12application.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tinhinane on 09/12/17.
 */

public class CustomList extends BaseAdapter {

    private List<Beacon> beacons;
    Context mContext;
    private static LayoutInflater inflater=null;

    public CustomList(List<Beacon> data, Context context) {

        this.beacons = data;
        this.mContext = context;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class Holder
    {
        TextView name;
        TextView rssi;
        TextView distance;
        ImageView img;
    }

    @Override
    public int getCount() {
        return beacons.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.customized_list, null);
        holder.name=(TextView) rowView.findViewById(R.id.beacon_name);
        holder.rssi=(TextView) rowView.findViewById(R.id.rssi);
        holder.distance=(TextView) rowView.findViewById(R.id.distance);
        holder.name.setText(beacons.get(position).getmDeviceCode());
        holder.rssi.setText(beacons.get(position).getmRssi()+" dBm");
        holder.distance.setText(Math.round(beacons.get(position).getDistance()*100d)/100d+" m");

        return rowView;
    }

}

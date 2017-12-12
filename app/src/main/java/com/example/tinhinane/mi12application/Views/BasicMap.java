package com.example.tinhinane.mi12application.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.tinhinane.mi12application.Helpers.RoomDrawing;
import com.example.tinhinane.mi12application.Helpers.ScanUtils;
import com.example.tinhinane.mi12application.Models.Beacon;

import java.util.ArrayList;

public class BasicMap extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("onCreate Basic map", "Ok");
        ArrayList<Beacon> beacons = (ArrayList<Beacon>) ScanUtils.getListBeacons();
        Log.i("Tag list beacons", beacons.toString());
        setContentView(new RoomDrawing(this, beacons));
    }

}

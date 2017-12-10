package com.example.tinhinane.mi12application.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.tinhinane.mi12application.Helpers.RoomDrawing;
import com.example.tinhinane.mi12application.Helpers.MapHelper;
import com.example.tinhinane.mi12application.Helpers.ScanUtils;
import com.example.tinhinane.mi12application.Helpers.Vector;
import com.example.tinhinane.mi12application.Models.Beacon;
import com.example.tinhinane.mi12application.R;

import java.util.ArrayList;

public class BasicMap extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<Beacon> beacons = (ArrayList<Beacon>) ScanUtils.getListBeacons();
        Log.i("Tag check", beacons.toString());
        Vector userPos = new Vector(0, MapHelper.scaleConvert(beacons.get(0).getDistance()),0);
        Log.i("Tag Device", beacons.get(0).getmDeviceCode()+" "+ MapHelper.scaleConvert(beacons.get(0).getDistance()) );
                //MapHelper.findPosition(beacons.get(0).getPos(), beacons.get(1).getPos(), new Vector(700,700,0), beacons.get(0).getDistance(), beacons.get(1).getDistance(), 2);
//        Vector userPos = MapHelper.findPosition(
//                beacons.get(0).getPos(),
//                beacons.get(1).getPos(),
//                new Vector(700,700,0),
//                6.18,
//                8.48,
//                6.08);

        Log.i("Tag user pos", userPos.toString());
        setContentView(new RoomDrawing(this, beacons, userPos));
    }

}

package com.example.tinhinane.mi12application.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.tinhinane.mi12application.Helpers.ProximityDrawing;
import com.example.tinhinane.mi12application.Helpers.ScanUtils;
import com.example.tinhinane.mi12application.Models.Beacon;
import com.example.tinhinane.mi12application.R;

import java.util.ArrayList;

public class ProximityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximity);
        ArrayList<Beacon> beacons = (ArrayList<Beacon>) ScanUtils.getListBeacons();
        setContentView(new ProximityDrawing(this, beacons));
    }
}

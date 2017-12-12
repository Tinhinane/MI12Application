package com.example.tinhinane.mi12application.Views;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tinhinane.mi12application.Helpers.ScanUtils;
import com.example.tinhinane.mi12application.Models.Beacon;
import com.example.tinhinane.mi12application.R;

import java.util.ArrayList;

/**
 * List BLE devices.
 */
public class SecondActivity extends AppCompatActivity {

    private ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Log.i("Tag SECOND", "Second Activity");
        final Button btnRefresh = findViewById(R.id.btnRefresh);
        final Button btnMap = findViewById(R.id.btnMap);
        btnMap.setEnabled(false);
        final Button btnProximity = findViewById(R.id.btnProximity);

        final Toast toast = Toast.makeText(getApplicationContext(), "Please wait while scanning beacons in the area...",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER, 0, 0);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ScanUtils.scanLeDevice(getApplicationContext());
                    lv = findViewById(R.id.foundDevicesList);
                new CountDownTimer(ScanUtils.SCAN_PERIOD+10, 1000) {

                    public void onTick(long millisUntilFinished) {
                        btnRefresh.setEnabled(false);
                        toast.show();
                    }

                    public void onFinish() {
                        CustomList list = new CustomList(ScanUtils.getListBeacons(), getApplicationContext());
                        lv.setAdapter(list);
                        if(list.getCount()>0){
                            btnMap.setEnabled(true);
                        }
                        btnRefresh.setEnabled(true);
                    }
                }.start();

            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //Go to basic map
                Intent basicMapActivity = new Intent(SecondActivity.this,BasicMap.class);
                startActivity(basicMapActivity);
            }
        });

        btnProximity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to Proximity view
                Intent proximityActivity = new Intent(SecondActivity.this, ProximityActivity.class);
                startActivity(proximityActivity);
            }
        });



    }
}

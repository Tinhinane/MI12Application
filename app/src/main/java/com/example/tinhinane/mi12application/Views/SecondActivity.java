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
        final ImageButton btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setBackgroundColor(Color.BLUE);
        final Button btnMap = findViewById(R.id.btnMap);
        final Button btnProximity = findViewById(R.id.btnProximity);

        final Toast toast = Toast.makeText(getApplicationContext(), "Please wait while scanning beacons in the area...",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER, 0, 0);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ScanUtils.scanLeDevice(getApplicationContext());
                    lv = findViewById(R.id.foundDevicesList);
                    /*if(ScanUtils.mScanning==false){
                        ArrayList<String> array_list = (ArrayList<String>) ScanUtils.saveBeacons();
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_list_item_1,
                            array_list);
                        lv.setAdapter(arrayAdapter);
                    }*/

                new CountDownTimer(5500, 1000) {

                    public void onTick(long millisUntilFinished) {
                        Log.i("seconds remaining: ", millisUntilFinished / 1000 +"");
                        btnRefresh.setEnabled(false);
                        btnRefresh.setBackgroundColor(Color.GRAY);
                        toast.show();
                    }

                    public void onFinish() {
                        ArrayList<String> array_list = (ArrayList<String>) ScanUtils.saveBeacons();
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                android.R.layout.simple_list_item_1,
                                array_list);
                        lv.setAdapter(arrayAdapter);
                        btnRefresh.setEnabled(true);
                        btnRefresh.setBackgroundColor(Color.BLUE);
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
                //Go to Porximity view
                Intent proximityActivity = new Intent(SecondActivity.this, ProximityActivity.class);
                startActivity(proximityActivity);
            }
        });



    }
}

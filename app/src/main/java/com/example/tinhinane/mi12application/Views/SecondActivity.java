package com.example.tinhinane.mi12application.Views;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tinhinane.mi12application.Helpers.ScanUtils;
import com.example.tinhinane.mi12application.R;

/**
 * List BLE devices.
 */
public class SecondActivity extends AppCompatActivity {

    private ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        final Button btnRefresh = findViewById(R.id.btnRefresh);
        final Button btnMap = findViewById(R.id.btnMap);
        btnMap.setEnabled(false);
        final Button btnProximity = findViewById(R.id.btnProximity);

        final Toast toastScan = Toast.makeText(getApplicationContext(), "Please wait while scanning beacons in the area...",
                Toast.LENGTH_LONG);
        toastScan.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER, 0, 0);

        final Toast toastLocalisation = Toast.makeText(getApplicationContext(), "Please wait until the app detects at least 3 beacons in the area...",
                Toast.LENGTH_SHORT);
        toastLocalisation.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER, 0, 0);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ScanUtils.scanLeDevice(getApplicationContext());
                    lv = findViewById(R.id.foundDevicesList);
                new CountDownTimer(ScanUtils.SCAN_PERIOD+10, 1000) {

                    public void onTick(long millisUntilFinished) {
                        btnRefresh.setEnabled(false);
                        toastScan.show();
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
                if(ScanUtils.getListBeacons().size()>=3){
                    Intent basicMapActivity = new Intent(SecondActivity.this,BasicMap.class);
                    startActivity(basicMapActivity);
                }
                else{
                    toastLocalisation.show();
                }

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

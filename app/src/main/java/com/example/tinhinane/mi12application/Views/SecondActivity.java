package com.example.tinhinane.mi12application.Views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.tinhinane.mi12application.Helpers.ScanUtils;
import com.example.tinhinane.mi12application.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        lv = (ListView) findViewById(R.id.foundDevicesList);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ScanUtils.scanLeDevice(getApplicationContext());
                    ArrayList<String> array_list = (ArrayList<String>) ScanUtils.saveBeacons();
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_list_item_1,
                            array_list);
                    lv.setAdapter(arrayAdapter);
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



    }
}

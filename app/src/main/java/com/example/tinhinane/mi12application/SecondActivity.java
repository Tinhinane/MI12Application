package com.example.tinhinane.mi12application;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SecondActivity extends AppCompatActivity {

    private ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Log.i("Second Tag", "Second Activity");
        lv = (ListView) findViewById(R.id.foundDevicesList);
        ArrayList<String> array_list = getIntent().getStringArrayListExtra("key");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                array_list);
        lv.setAdapter(arrayAdapter);
    }

    
    /*
        public void printMap(HashMap<String, BleDevice> mapDevices){
            // Get a set of the entries
            Set set = hm.entrySet();
            // Get an iterator
            Iterator i = set.iterator();

            // Display elements
            while(i.hasNext()) {
                Map.Entry me = (Map.Entry)i.next();
                System.out.print(me.getKey() + ": ");
                System.out.println(me.getValue());
            }
            System.out.println();
        }*/
    /*public void handleClick(MainActivity mainActivity, HashMap<String, BleDevice> mapDevices){
        Log.i("TAG Second", "kwakwakwa!");
        listBleDevices = new ArrayList<String>();
        // Get a set of the entries
        Set set = mapDevices.entrySet();
        // Get an iterator
        Iterator i = set.iterator();


        // Display elements
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            BleDevice bleDevice = (BleDevice) me.getValue();
            listBleDevices.add(bleDevice.getmDeviceCode());
            Log.i("device received", me.getValue().toString());

        }
        if(listBleDevices.size()>0){
            printListToActivity(listBleDevices);
        }
    }*/

}

package com.example.tinhinane.mi12application.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

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
        lv = (ListView) findViewById(R.id.foundDevicesList);
        ArrayList<String> array_list = getIntent().getStringArrayListExtra("key");
        Log.i("tag list rec", array_list.toString());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                array_list);
        lv.setAdapter(arrayAdapter);
    }
}

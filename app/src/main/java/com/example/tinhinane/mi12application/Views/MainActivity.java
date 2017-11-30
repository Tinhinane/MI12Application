package com.example.tinhinane.mi12application.Views;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;


import com.example.tinhinane.mi12application.Helpers.BluetoothHelper;
import com.example.tinhinane.mi12application.Helpers.LocationHelper;
import com.example.tinhinane.mi12application.Models.Beacon;
import com.example.tinhinane.mi12application.R;
import com.example.tinhinane.mi12application.Helpers.ScanUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("TAG onCreate", "MI12 app is starting up");

        final Switch switchBluetooth = findViewById(R.id.switchBluetooth);
        final Switch switchLocation = findViewById(R.id.switchLocation);

        final Button button = findViewById(R.id.btnDevices);
        final Button btnMap = findViewById(R.id.btnMaps);

        button.setEnabled(false);
        btnMap.setEnabled(false);

        // Activate Location when the user toggles the switch button
        switchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchLocation.isChecked()){
                    grantPermission();
                    activateLocation();
                }
                else {
                    //Todo :disable Location if permission not granted
                    // Todo:toggle to unchecked using also onresume
                    // activateLocation();
                }
            }
        });

        // Activate Bluetooth when the user toggles the switch button
        switchBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchBluetooth.isChecked()) {
                    activateBluetooth();
                } else {
                    activateBluetooth();
                }
            }
        });

        // The buttons of the map and the devices are activated as the bluetooth and the location are enabled
        switchBluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                button.setEnabled(isChecked && switchLocation.isChecked());
                btnMap.setEnabled(isChecked && switchLocation.isChecked());
            }
        });

        switchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                button.setEnabled(isChecked && switchBluetooth.isChecked());
                btnMap.setEnabled(isChecked && switchBluetooth.isChecked());
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ScanUtils.scanLeDevice(getApplicationContext());
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    intent.putStringArrayListExtra("key", (ArrayList<String>) ScanUtils.saveBeacons());
                    startActivity(intent);
                } catch (Exception e) {
                    //Dialog to say there's a problem
                }
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Maps = new Intent(MainActivity.this,MapsActivity.class);
                if(!ScanUtils.scannedBeacons.isEmpty()){
                    Maps.putExtra("listDistance", Beacon.populateDistanceList(ScanUtils.scannedBeacons));
                    startActivity(Maps);
                }
                else{
                    Log.i("On Click Map", "Scan devices first");
                }
            }
        });

    }


    private void grantPermission(){
        if (!LocationHelper.isLocationPermissionGranted(this)){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect beacons.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }
    }

    private void activateLocation() {
        if (!LocationHelper.isLocationEnabled(this)) {
            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Services Not Active");
            builder.setMessage("Please enable Location Services and GPS");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
        //Todo Else
    }

    private void activateBluetooth() {
        if (!BluetoothHelper.isBluetoothEnabled(this)) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        //Todo Else
    }

}

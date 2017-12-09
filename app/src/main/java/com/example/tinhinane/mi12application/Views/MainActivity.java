package com.example.tinhinane.mi12application.Views;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
    private static Button bleScannerButton;
    private static Switch switchBluetooth;
    private static Switch switchLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bleScannerButton = findViewById(R.id.btnDevices);
        switchBluetooth = findViewById(R.id.switchBluetooth);
        switchLocation = findViewById(R.id.switchLocation);
        Log.i("TAG onCreate", "MI12 app is starting up");
        bleScannerButton.setEnabled(false);

        // Activate Location when the user toggles the switch button
        switchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchLocation.isChecked()){
                    grantPermission();
                    activateLocation();
                }
                else {
                    desactivateLocation();
                }
            }
        });
        //Activate Location switch when the gps is already on
        if(LocationHelper.isLocationEnabled(this) == true){
            switchLocation.setChecked(true);
        }

        // Activate Bluetooth when the user toggles the switch button
        switchBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchBluetooth.isChecked()) {
                    activateBluetooth();
                } else {
                    desactivateBluetooth();
                }
            }
        });
        //Activate Bluetooth switch when the bluetooth is already on
        if(BluetoothHelper.getBluetoothAdapter(this).isEnabled()){
            switchBluetooth.setChecked(true);
        }

        //The BLE Scanner button is activated when both the bluetooth and the location are enabled
        switchBluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bleScannerButton.setEnabled(isChecked && switchLocation.isChecked());
            }
        });
        switchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bleScannerButton.setEnabled(isChecked && switchBluetooth.isChecked());
            }
        });
        //If both bluetooth and gps are on then enable BLE Scanner button
        if(LocationHelper.isLocationEnabled(this) && BluetoothHelper.getBluetoothAdapter(this).isEnabled()){
            bleScannerButton.setEnabled(true);
        }

        //Go to BLE Scanner
        bleScannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent secondActivity = new Intent(MainActivity.this,SecondActivity.class);
                startActivity(secondActivity);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Activate Bluetooth switch when the bluetooth is already on
        switchBluetooth.setChecked(BluetoothHelper.getBluetoothAdapter(this).isEnabled());
        switchLocation.setChecked(LocationHelper.isLocationEnabled(this));
        bleScannerButton.setEnabled(switchBluetooth.isChecked() && switchLocation.isChecked());
        // Register for broadcasts on BluetoothAdapter state change
        //IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        //registerReceiver(BluetoothHelper.mBluetoothReceiver, filter);
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

    }

    private void desactivateLocation() {
        if (LocationHelper.isLocationEnabled(this)) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    private void activateBluetooth() {
        if (!BluetoothHelper.isBluetoothEnabled(this)) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }
    private void desactivateBluetooth() {
        if (BluetoothHelper.isBluetoothEnabled(this)) {
                BluetoothHelper.desactivate();
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        switchBluetooth.setChecked(BluetoothHelper.getBluetoothAdapter(this).isEnabled());
        switchLocation.setChecked(LocationHelper.isLocationEnabled(this));
        bleScannerButton.setEnabled(switchBluetooth.isChecked() && switchLocation.isChecked());

    }

}

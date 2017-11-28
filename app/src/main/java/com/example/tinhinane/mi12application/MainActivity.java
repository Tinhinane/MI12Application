package com.example.tinhinane.mi12application;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final long SCAN_PERIOD = 10000;// Stops scanning after 10 seconds
    private Handler mHandler;
    private LocationManager lm;
    private BluetoothLeScanner mLEScanner;
    private BluetoothAdapter mBluetoothAdapter;
    private HashMap<String, Beacon> scannedBeacons = new HashMap<String, Beacon>();
    private List<String> listStringBeacons = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("TAG onCreate", "MI12 app is starting up");
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        //Get user permission grant
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
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

        activateLocation();//Enable location
        getBluetoothAdapter();//Get mBluetoothAdapter

        //OnClick listener to button "scan"
        final Button button = findViewById(R.id.btnDevices);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> testList = saveBeacons();
                // Code here executes on main thread after user presses button
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putStringArrayListExtra("key", (ArrayList<String>) testList);
                startActivity(intent);
            }
        });

        //OnClick listener to button "map"
        final Button btnMap = findViewById(R.id.btnMaps);
        btnMap.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View v){
              Intent Maps = new Intent(MainActivity.this,MapsActivity.class);
              if(!scannedBeacons.isEmpty()){
                  Maps.putExtra("listDistance", Beacon.populateDistanceList(scannedBeacons));
                  startActivity(Maps);
              }
              else{
                  Log.i("On Click Map", "Scan devices first");
              }
          }
        });

    }

    @Override
    protected void onResume(){
        Log.i("Tag onResume", "Scan");
        mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if(isBluetoothEnabled(mBluetoothAdapter) && lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //If bluetooth and location are both enabled, start scanning
            scanLeDevice(true);
        }
        super.onResume();
    }

    //Ask the user to turn on location
    private void activateLocation(){
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
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

    //Get the phone's own Bluetooth adapter, which is required for any Bluetooth activity.
    private BluetoothAdapter getBluetoothAdapter() {
        if (mBluetoothAdapter == null) {
            // Initializes Bluetooth adapter.
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) this.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }
        return mBluetoothAdapter;
    }

    //Check if bluetooth is on
    public boolean isBluetoothEnabled(BluetoothAdapter bluetoothAdapter) {
        if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()){
            //Ask user to turn on bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return false;
        }
        else{
            //Bluetooth is on
            return true;
        }
    }

    //Scan BLE devices when bluetooth is enabled
    private void scanLeDevice(boolean enable) {

        mHandler = new Handler();
        if (enable) {
            //Stop scanning after a pre-defined period
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                        mLEScanner.stopScan(mScanCallback);
                }
            }, SCAN_PERIOD);

            mLEScanner.startScan(mScanCallback);
        }
        else{
            mLEScanner.stopScan(mScanCallback);
        }
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            String deviceName = result.getDevice().toString();

            //From scan records, we see that TxPower = -50 (last byte in the packet).
            //But after some calibration, we decided to set it to 69.5
            BleDevice device = new BleDevice(result.getRssi(), -69.5, deviceName);
            if (!scannedBeacons.containsKey(deviceName)) {
                //New device found
                if(Beacon.isBeacon(device)){
                    Beacon beacon = new Beacon(device.getmRssi(), device.getmTxPower(), device.getmDeviceCode());
                    scannedBeacons.put(deviceName, beacon);
                }

            }else{
                //Update device already found
                (scannedBeacons.get(deviceName)).setmRssi(result.getRssi());
                 double distance = Beacon.distanceMathematical(device.getmTxPower(), device.getmRssi());
                (scannedBeacons.get(deviceName)).setDistance(distance);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    //Save only ibeacons
    public List<String> saveBeacons(){

        listStringBeacons.clear();
        // Get a set of the entries
        Set set = scannedBeacons.entrySet();
        // Get an iterator
        Iterator i = set.iterator();

        // loop and save only beacons
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            Beacon beacon = (Beacon) me.getValue();
            listStringBeacons.add(beacon.toString());

        }
        return listStringBeacons;
    }

}

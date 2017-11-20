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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothLeScanner mLEScanner;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private HashMap<String, BleDevice> foundDevices = new HashMap<String, BleDevice>();
    private List<String> listBleDevices = new ArrayList<String>();
    private static final long SCAN_PERIOD = 10000;// Stops scanning after 10 seconds.
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private LocationManager lm;
    private double distance_found = 0;
    private ArrayList<Double> listDistance = new ArrayList<Double>();//save found distances (3 most recent distances)

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

        final Button button = findViewById(R.id.btnDevices);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> testList = showListDevices();
                // Code here executes on main thread after user presses button
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putStringArrayListExtra("key", (ArrayList<String>) testList);
                startActivity(intent);
            }
        });

        final Button btnMap = findViewById(R.id.btnMaps);
        btnMap.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View v){
              Intent Maps = new Intent(MainActivity.this,MapsActivity.class);
              if(distance_found !=0){
                  listDistance.add(1.5);
                  listDistance.add(2.5);
                  Maps.putExtra("listDistance", listDistance);
                  Maps.putExtra("distance_found", distance_found);
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

    //Check phone's bluetooth
    public boolean isBluetoothEnabled(BluetoothAdapter bluetoothAdapter) {
        if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()){
            //Bluetooth is off
            //Ask user to turn on bluetooth if off
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return false;
        }
        else{
            //Bluetooth is on
            return true;
        }
    }

    //Scan if bluetooth is enabled
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
            //Log.i("Tag records" , result.getScanRecord().toString());
            //From scan records, we see that TxPower = -50 (last byte in the packet).
            //The value was checked on another application, it was 0xCE => -50 dBm
            BleDevice device = new BleDevice(result.getRssi(), -69.5, deviceName);
            if (!foundDevices.containsKey(deviceName)) {
                //new device found
                foundDevices.put(deviceName,device);
            }else{
                //update device already found
                (foundDevices.get(deviceName)).setmRssi(result.getRssi());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    //Fill in the list of ble devices found
    public List<String> showListDevices(){
        //Beacons used: CA:29:A7:B8:6E:02,F0:F9:90:D8:07:02
        listBleDevices.clear();
        // Get a set of the entries
        Set set = foundDevices.entrySet();
        // Get an iterator
        Iterator i = set.iterator();

        // Display elements
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            BleDevice bleDevice = (BleDevice) me.getValue();

            switch(bleDevice.getmDeviceCode()) {
                case "F0:F9:90:D8:07:02":
                    Log.i("Tag device scanned", bleDevice.getmDeviceCode());
                    listBleDevices.add(bleDevice.toString());//Add to list of ble scanned devices
                    distance_found = distanceExperimental(bleDevice.getmTxPower(), bleDevice.getmRssi());
                    break;
                case "CA:29:A7:B8:6E:02":
                    Log.i("Tag device scanned", bleDevice.getmDeviceCode());
                    listBleDevices.add(bleDevice.toString());//Add to list of ble scanned devices
                    distance_found = distanceExperimental(bleDevice.getmTxPower(), bleDevice.getmRssi());
                    break;
                default :
                    Log.i("Tag device scanned", "Not an iBeacon");
            }



        }
        return listBleDevices;
    }

    public double distanceMathematical(double txPower, int rssi){
        /*
        * n (environmental factor) = 2 (in free space)
        *
        * d = 10 ^ ((TxPower - RSSI) / (10 * n))
        */
        double distance = Math.pow(10d, ((double) txPower - rssi) / (10 * 2));
        Log.i("Tag distance (math)", distance + "");
        return distance;
    }

    public double distanceExperimental(double txPower, int rssi){

        if (rssi == 0) {
            return -1.0; // if we cannot determine distance, return -1.
        }
        //A regression equation is a polynomial regression equation if the power of independent variable is more than 1.
        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            Log.i("Tag distance (exp)", Math.pow(ratio,10) +"");
            return Math.pow(ratio,10);
        }

        else {
            double d =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            Log.i("Distance (exp formula)", d+"");
            return d;
        }
    }


}

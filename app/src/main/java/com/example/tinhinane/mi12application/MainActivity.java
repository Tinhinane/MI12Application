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

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private LocationManager lm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("TAG", "MI12 app is starting up");
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        //Get user permission to get location access
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                Log.i("TAG", "needs permission grant");
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
                Log.i("TAG", "needs permission grant");
            }
        }

        activateLocation();//enable gps
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

    }

    @Override
    protected void onResume(){
        mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if(isBluetoothEnabled(mBluetoothAdapter) && lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Log.i("On Resume location: ", lm.isProviderEnabled(LocationManager.GPS_PROVIDER)+" test");
            Log.i("OnResume", "True, I'm going to scan");
            scanLeDevice(true);
        }
        super.onResume();
    }

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

    //The BluetoothAdapter is required for any Bluetooth activity.
    //The BluetoothAdapter represents the phone's own Bluetooth adapter.
    private BluetoothAdapter getBluetoothAdapter() {
        if (mBluetoothAdapter == null) {
            // Initializes Bluetooth adapter.
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) this.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }
        return mBluetoothAdapter;
    }

    //Is the phone's bluetooth on, off?
    public boolean isBluetoothEnabled(BluetoothAdapter bluetoothAdapter) {
        if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()){
            Log.i("UtilsTag", "Bluetooth Off");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Log.i("Check bluetooth: ", bluetoothAdapter.isEnabled() + "test");
            return false;
        }
        else{
            Log.i("UtilsTag", "Bluetooth On");
            return true;
        }
    }

    private void scanLeDevice(boolean enable) {

        mHandler = new Handler();
        if (enable) {
            //Stop scanning after a pre_defined period: 10000
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
            //BluetoothDevice btDevice = result.getDevice();
            BleDevice device = new BleDevice(result.getRssi(), result.getScanRecord().getTxPowerLevel(), deviceName);
            if (!foundDevices.containsKey(deviceName)) {
                foundDevices.put(deviceName,device);
                Log.i("Tag Add", "New Device Found");
            }else{
                (foundDevices.get(deviceName)).setmRssi(result.getRssi());
                (foundDevices.get(deviceName)).setmTxPower(result.getScanRecord().getTxPowerLevel());
                Log.i("Tag Update", "Update device already found");
            }
            //connectToDevice(btDevice);
        }

        /*@Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }*/

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    //Go to second activity
    public List<String> showListDevices(){

        // Get a set of the entries
        Set set = foundDevices.entrySet();
        // Get an iterator
        Iterator i = set.iterator();

        // Display elements
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            BleDevice bleDevice = (BleDevice) me.getValue();
            listBleDevices.add(bleDevice.toString());
            Log.i("device received", me.getValue().toString());
        }
        return listBleDevices;
    }

    public HashMap<String, BleDevice> getMap(){
            return foundDevices;
    }



}

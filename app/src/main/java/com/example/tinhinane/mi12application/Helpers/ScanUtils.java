package com.example.tinhinane.mi12application.Helpers;

import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.tinhinane.mi12application.Helpers.BluetoothHelper;
import com.example.tinhinane.mi12application.Helpers.LocationHelper;
import com.example.tinhinane.mi12application.Models.Beacon;
import com.example.tinhinane.mi12application.Models.BleDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * Created by tinhinane on 30/11/17.
 */

public class ScanUtils {
    private static final long SCAN_PERIOD = 10000;// Stops scanning after 10 seconds
    private static Handler mHandler;
    private static BluetoothLeScanner mLEScanner;
    public static HashMap<String, Beacon> scannedBeacons = new HashMap<String, Beacon>();
    public static List<String> listStringBeacons = new ArrayList<String>();
    public static List<Beacon> listBeacons = new ArrayList<Beacon>();

    //Scan BLE devices when bluetooth is enabled
    public static void scanLeDevice(Context context) {
        if (LocationHelper.isLocationEnabled(context)&& BluetoothHelper.isBluetoothEnabled(context)){
            mLEScanner = BluetoothHelper.mBluetoothAdapter.getBluetoothLeScanner();
            mHandler = new Handler();

            //Stop scanning after a pre-defined period
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i("Tag Scan End","ok");
                    mLEScanner.stopScan(mScanCallback);
                }
            }, SCAN_PERIOD);
            Log.i("Tag Scan start","ok");
                mLEScanner.startScan(mScanCallback);

        }

        else{

            Log.i("Tag Scan End","ok2");
            mLEScanner.stopScan(mScanCallback);
        }
    }

    private static ScanCallback mScanCallback = new ScanCallback() {
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
                    double distance = Beacon.distanceMathematical(device.getmTxPower(), device.getmRssi());
                    beacon.setDistance(distance);
                    scannedBeacons.put(deviceName, beacon);
                }

            }else{
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
    public static List<String> saveBeacons(){
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

    public static List<Beacon> getListBeacons(){
        listBeacons.clear();
        // Get a set of the entries
        Set set = scannedBeacons.entrySet();
        // Get an iterator
        Iterator i = set.iterator();

        // loop and save only beacons
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            Beacon beacon = (Beacon) me.getValue();
            listBeacons.add(beacon);

        }
        return listBeacons;
    }

}

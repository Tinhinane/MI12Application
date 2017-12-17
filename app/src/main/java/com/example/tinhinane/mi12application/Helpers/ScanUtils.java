package com.example.tinhinane.mi12application.Helpers;

import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.tinhinane.mi12application.Models.Beacon;
import com.example.tinhinane.mi12application.Models.BleDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by tinhinane on 30/11/17.
 */

public class ScanUtils {
    public static final long SCAN_PERIOD = 30000;//[ms]
    private static final double TxPower = -55.5;//[dBm]
    private static Handler mHandler;
    private static BluetoothLeScanner mLEScanner;
    public static HashMap<String, Beacon> scannedBeacons = new HashMap<String, Beacon>();
    public static List<Beacon> listBeacons = new ArrayList<Beacon>();

    //Scan BLE devices when bluetooth is enabled
    public static void scanLeDevice(Context context) {

        if (LocationHelper.isLocationEnabled(context)&& BluetoothHelper.isBluetoothEnabled(context)){
            BluetoothHelper.mBluetoothAdapter.enable();//initialise
            mLEScanner = BluetoothHelper.mBluetoothAdapter.getBluetoothLeScanner();
            mHandler = new Handler();

            //Stop scanning after a pre-defined period
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i("Scan","Scan stop");
                    //(DEBUG) Remove
                    if(getListBeacons().size() <3){
                        Beacon beacon = new Beacon(-70, -55.5, "CA:29:A7:B8:6E:02");
                        scannedBeacons.put("CA:29:A7:B8:6E:02", beacon);
                    }
                    //Calculate avg of distances found for every beacon
                    for(Beacon b: getListBeacons()){
                        double avgDistance = averageDistance(b);
                        double avgRSSI = averageRSSI(b);
                        Log.i("Average RSSI", avgRSSI+"");
                        b.setDistance(avgDistance);
                        b.setmRssi((int)avgRSSI);
                    }
                    mLEScanner.stopScan(mScanCallback);
                }
            }, SCAN_PERIOD);
            Log.i("Scan","Scan (currently)");
            mLEScanner.startScan(mScanCallback);
        }

        else{
            Log.i("Scan","End");
            mLEScanner.stopScan(mScanCallback);
        }
    }

    private static ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            String deviceName = result.getDevice().toString();
            BleDevice device = new BleDevice(result.getRssi(), TxPower, deviceName);
            //New beacon found
            if (!scannedBeacons.containsKey(deviceName)) {
                if(Beacon.isBeacon(device)){
                    Log.i("1 Tag scan result", "RSSI: " + result.getRssi() + "Device " + deviceName);
                    Beacon beacon = new Beacon(device.getmRssi(), device.getmTxPower(), device.getmDeviceCode());
                    beacon.setRSSIs(beacon.getmRssi());
                    beacon.setDistances(beacon.getDistance());
                    scannedBeacons.put(deviceName, beacon);
                }

            }else{
                Log.i("2 Tag scan result", "RSSI: " + result.getRssi() + "Device " + deviceName);
                Beacon b = scannedBeacons.get(deviceName);
                b.setmRssi(result.getRssi());
                double distance = b.calculateDistance(b.getmTxPower(), b.getmRssi());
                b.setRSSIs(result.getRssi());
                b.setDistances(distance);
            }
        }
        @Override
        public void onBatchScanResults(List<ScanResult> results){
            for (ScanResult sr : results) {
                Log.i("On Batch Scan Result", sr.toString());
            }
        }
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e("On Scan Failed", "Error Code: " + errorCode);
        }
    };

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

    public static double averageDistance(Beacon b){
        double sum = 0;
        Log.i("Distance list:", b.getDistanceList().toString());
        for(double distance: b.getDistanceList()){
            sum = sum + distance;
        }
        return (sum/b.getDistanceList().size());
    }

    public static double averageRSSI(Beacon b){
        double sum = 0;
        Log.i("RSSI list:", b.toString());
        for(double rssi: b.getRSSIList()){
            sum = sum + rssi;
        }
        return (sum/b.getRSSIList().size());
    }

}

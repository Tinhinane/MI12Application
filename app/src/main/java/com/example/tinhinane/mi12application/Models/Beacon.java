package com.example.tinhinane.mi12application.Models;

import android.util.Log;

import com.example.tinhinane.mi12application.Helpers.Vector;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by tinhinane on 23/11/17.
 */

public class Beacon extends BleDevice {

    private double distance;
    private Vector v;


    public Beacon(int rssi, double txpower, String deviceCode) {
        super(rssi, txpower, deviceCode);
        this.distance = distanceMathematical(txpower, rssi);
        this.v = initPos(deviceCode);
    }

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public  static double distanceMathematical(double txPower, int rssi){
        /*
        * n (environmental factor) = 2 (in free space)
        *
        * d = 10 ^ ((TxPower - RSSI) / (10 * n))
        */
        double distance = Math.pow(10d, ((double) txPower - rssi) / (10 * 2));
        Log.i("Tag distance (math)", distance + "");
        return distance;
    }

    public static double distanceExperimental(double txPower, int rssi){

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

    public Vector getPos() {
        return v;
    }

    //Check if BLEDevice is a beacon
    public static boolean isBeacon(BleDevice device){

        boolean test;
        switch(device.getmDeviceCode()) {
            case "F0:F9:90:D8:07:02":
                test = true;
                break;
            case "CA:29:A7:B8:6E:02":
                test = true;
                break;
            default :
                test = false;
                Log.i("Tag device scanned", "Not an iBeacon");
        }

        return test;
    }

    public Vector initPos(String name){
        Vector vector;
        switch(name) {
            case "F0:F9:90:D8:07:02":
                vector = new Vector(10,10,0);
                break;
            case "CA:29:A7:B8:6E:02":
                vector = new Vector(350,350, 0);
                break;
            default :
                vector = new Vector(0,0, 0);
                Log.i("Tag device scanned", "Not an iBeacon");
        }
        return vector;
    }

    public static ArrayList<Double> populateDistanceList(HashMap<String, Beacon> hmBeacons){

        ArrayList<Double> listDistance = new ArrayList<Double>();
        listDistance.clear();
        // Get a set of the entries
        Set set = hmBeacons.entrySet();
        // Get an iterator
        Iterator i = set.iterator();

        // loop and save only beacons
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            Beacon beacon = (Beacon) me.getValue();
            listDistance.add(beacon.getDistance());
        }

        return listDistance;
    }

    @Override
    public String toString() {

        return  "Device = " + getmDeviceCode() + "\n" +
                "RSSI = " + getmRssi() + "\n" +
                "Tx Power = " + getmTxPower() + "\n" +
                "Distance = " + distance + "\n" +
                "Position = " + v
                ;
    }
}

package com.example.tinhinane.mi12application.Models;

import android.util.Log;

import com.example.tinhinane.mi12application.Helpers.KalmanFilter;
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
    private Vector position;
    private double filteredRssi;
    static final String[] beaconIDs = {"F0:F9:90:D8:07:02", "CA:29:A7:B8:6E:02"};

    public Beacon(double rssi, double txpower, String deviceCode) {
        super(rssi, txpower, deviceCode);
        this.filteredRssi = setFilteredRssi(rssi);
        this.distance = distanceMathematical(txpower, getFilteredRssi());
        this.position = initPos(deviceCode);

    }

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    //Filtered RSSI
    public double getFilteredRssi(){
        return this.filteredRssi;
    }

    public double setFilteredRssi(double rssi){
        KalmanFilter kf = new KalmanFilter();
        return kf.applyFilter(rssi);
    }

    public static double distanceMathematical(double txPower, double rssi){
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
        return position;
    }

    //Check if BLEDevice is a beacon
    public static boolean isBeacon(BleDevice device){
        //Note: Do not use switch expression
        boolean test;
        if (device.getmDeviceCode().equals(beaconIDs[0])) {
            test = true;

        } else if (device.getmDeviceCode().equals(beaconIDs[1])) {
            test = true;

        } else {
            test = false;
            Log.i("Tag device scanned", "Not an iBeacon");
        }

        return test;
    }

    private Vector initPos(String name){
        Vector vector;
        if (name.equals(beaconIDs[0])) {
            vector = new Vector(10, 10, 0);

        } else if (name.equals(beaconIDs[1])) {
            vector = new Vector(350, 350, 0);

        } else {
            vector = new Vector(0, 0, 0);
            Log.i("Tag device scanned", "Not an iBeacon");
        }
        return vector;
    }

    public static int proximityZone(Beacon b){

        if(b.getDistance() < 1){
            return 0; //Immediate zone
        }
        else if(b.getDistance()>1 && b.getDistance() <3){
            return 1; //Near zone
        }
        else{
            return 2; //Far zone
        }
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
                "Position = " + position
                ;
    }
}

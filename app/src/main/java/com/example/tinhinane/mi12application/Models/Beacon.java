package com.example.tinhinane.mi12application.Models;

import com.example.tinhinane.mi12application.Helpers.Vector;

import java.util.ArrayList;

/**
 * Created by tinhinane on 23/11/17.
 */

public class Beacon extends BleDevice {

    private double distance;
    private Vector position;
    private ArrayList<Double> distances = new ArrayList<Double>();
    private ArrayList<Double> RSSIs = new ArrayList<Double>();
    static final String[] beaconIDs = {"F0:F9:90:D8:07:02", "CA:29:A7:B8:6E:02", "F4:17:02:A7:4B:02"};

    public Beacon(double rssi, double txpower, String deviceCode) {
        super(rssi, txpower, deviceCode);
        this.distance = calculateDistance(txpower, rssi);
    }

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Vector getPosition() {
        return position;
    }

    public void setPosition(Vector v){
        this.position = v;
    }

    public double calculateDistance(double txPower, double rssi){
        /*
        * n (environmental factor) = 2 (in free space)
        *
        * d = 10 ^ ((TxPower - RSSI) / (10 * n))
        */
        double distance;
        if (rssi == 0) {
            distance=-1.0; // if we cannot determine distance, return -1.
        }
        //Distance [0-3m]
        else if(-70<rssi && rssi <0){
            distance = Math.pow(10d, ((double) txPower - rssi) / (10 * 2));
        }
        else if(-74<rssi && rssi<=-70){
            distance = 3.5;
        }
        else if(-77<rssi && rssi<=-74){
            distance = 4.5;
        }
        else if(-81<rssi && rssi<=-77){
            distance = 6;
        }
        else if(-86<rssi && rssi<=-81){
            distance = 8;
        }
        else if(-90<rssi && rssi<=-86){
            distance = 10;
        }
        else if(-95<rssi && rssi<=-90){
            distance = 11;
        }
        else{
            distance = -1.0; //Beacon out of range
        }

        return distance;
    }

    public static boolean isBeacon(BleDevice device){

        boolean test;
        if (device.getmDeviceCode().equals(beaconIDs[0])) {
            test = true;

        } else if (device.getmDeviceCode().equals(beaconIDs[1])) {
            test = true;

        } else if (device.getmDeviceCode().equals(beaconIDs[2])) {
            test = true;

        } else {
            test = false;
        }

        return test;
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
    public void clearDistances(){
        this.distances.clear();
    }
    public void setDistances(double distance){
        this.distances.add(distance);
    }

    public void setRSSIs(double rssi){ this.RSSIs.add(rssi);}

    public ArrayList<Double> getDistanceList(){
        return this.distances;
    }
    public ArrayList<Double> getRSSIList(){
        return this.RSSIs;
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

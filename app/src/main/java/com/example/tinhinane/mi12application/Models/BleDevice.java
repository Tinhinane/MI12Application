package com.example.tinhinane.mi12application.Models;

/**
 * Created by tinhinane on 03/11/17.
 */

public class BleDevice {

    private double mRssi;
    private double mTxPower;
    private String mDeviceCode;

    public BleDevice(double rssi, double txpower, String deviceCode){
        this.mTxPower = txpower;
        this.mRssi = rssi;
        this.mDeviceCode = deviceCode;
    }

    public double getmRssi() {
        return mRssi;
    }

    public void setmRssi(int mRssi) {
        this.mRssi = mRssi;
    }

    public double getmTxPower() {
        return mTxPower;
    }

    public void setmTxPower(double mTxPower) {
        this.mTxPower = mTxPower;
    }

    public String getmDeviceCode() {
        return mDeviceCode;
    }

    public void setmDeviceCode(String mDeviceCode) {
        this.mDeviceCode = mDeviceCode;
    }

    @Override
    public String toString() {
        return "BleDevice{" +
                "mRssi=" + mRssi +
                ", mTxPower=" + mTxPower +
                ", mDeviceCode='" + mDeviceCode + '\'' +
                '}';
    }
}

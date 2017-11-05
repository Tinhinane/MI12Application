package com.example.tinhinane.mi12application;

/**
 * Created by tinhinane on 03/11/17.
 */

public class BleDevice {

    private int mRssi;
    private int mTxPower;
    private String mDeviceCode;

    public BleDevice(int rssi, int txpower, String deviceCode){
        this.mTxPower = txpower;
        this.mRssi = rssi;
        this.mDeviceCode = deviceCode;
    }

    public int getmRssi() {
        return mRssi;
    }

    public void setmRssi(int mRssi) {
        this.mRssi = mRssi;
    }

    public int getmTxPower() {
        return mTxPower;
    }

    public void setmTxPower(int mTxPower) {
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

package com.example.tinhinane.mi12application;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("TAG", "MI12 app is starting up");
        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        checkBluetooth(bluetoothAdapter);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    //The BluetoothAdapter is required for any Bluetooth activity.
    // The BluetoothAdapter represents the phone's own Bluetooth adapter.
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
    public static boolean checkBluetooth(BluetoothAdapter bluetoothAdapter) {
        if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()){
            Log.i("UtilsTag", "Bluetooth Off");
            return false;
        }
        else{
            Log.i("UtilsTag", "Bluetooth On");
            return true;
        }
    }
}

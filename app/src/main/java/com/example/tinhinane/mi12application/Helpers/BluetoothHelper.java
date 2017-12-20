package com.example.tinhinane.mi12application.Helpers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by tinhinane on 30/11/17.
 * Class BluetoothHelper enables and controls the bluetooth
 */

public class BluetoothHelper {
    public static BluetoothAdapter mBluetoothAdapter;

    public static BluetoothAdapter getBluetoothAdapter(Context context) {

        if (mBluetoothAdapter == null) {
            // Initializes Bluetooth adapter.
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) context.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }
        return mBluetoothAdapter;
    }

    public static boolean isBluetoothEnabled(Context context) {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            getBluetoothAdapter(context);
            return false;
        }
        return true;
    }
    public static boolean desactivate(){
        return mBluetoothAdapter.disable();
    }

}


package com.example.tinhinane.mi12application.Helpers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

/**
 * Created by tinhinane on 30/11/17.
 */

public class BluetoothHelper {
    public static BluetoothAdapter mBluetoothAdapter;

    private static BluetoothAdapter getBluetoothAdapter(Context context) {

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
}


package com.example.tinhinane.mi12application.Helpers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by tinhinane on 30/11/17.
 * Class LocationHelper enables location access
 */

public class LocationHelper {
    public static boolean isLocationEnabled(Context context){
        LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            return false;
        }
        return true;
    }
    public static boolean isLocationPermissionGranted(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                return false;
            }
        }
        return true;
    }
}

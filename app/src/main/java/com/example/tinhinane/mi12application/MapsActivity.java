package com.example.tinhinane.mi12application;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Show user position
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.i("tag maps activity", "Maps Activity created");

        //Retrieve array double
        ArrayList<Double> listDouble = (ArrayList<Double>) getIntent().getSerializableExtra("listDistance");
        Log.i("Tag distance (test)", listDouble.toString());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // test hardcoded values to find my position
        Vector v1 = GeographicToCartesian(49.415502, 2.819023);
        Vector v2 = GeographicToCartesian(49.422074, 2.823758);
        Vector v3 = GeographicToCartesian(49.420919, 2.814284);

        Vector mypos = findPosition(v1, v2, v3, 438.37, 641.66,285.17);
        // Todo : test once the right lat value is found
        LatLng test = CartesianToGeographic(mypos);
        Log.i("tag user (cart)", mypos.toString());
        Log.i("tag user (geo)", test.toString());
        mMap.addMarker(new MarkerOptions().position(test).title("Marker in user position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(test));
    }

    double Deg2Rad(double deg) { return deg*Math.PI/180; }
    double Rad2Deg(double rad) { return rad*(180/Math.PI); }

    public Vector GeographicToCartesian(double lat,double lng){

        double GRS_a = 6378137;
        double GRS_f = 1 / 298.257222101;
        double GRS_b = GRS_a * (1 - GRS_f);
        double GRS_e = Math.sqrt((Math.pow(GRS_a, 2) - Math.pow(GRS_b, 2)) / Math.pow(GRS_a, 2));

        // Convert degree angles to radian
        double lon_ = Deg2Rad(lng);
        double lat_ = Deg2Rad(lat);
        //Geo to ECEF
        double N = GRS_a / Math.sqrt(1.0 - Math.pow(GRS_e, 2) * Math.pow(Math.sin(lat_), 2));
        double x = N * Math.cos(lat_) * Math.cos(lon_);
        double y = N * Math.cos(lat_) * Math.sin(lon_);
        double z = N * (1-Math.pow(GRS_e, 2)) * Math.sin(lat_);

        return new Vector(x,y, z);
    }

    public LatLng CartesianToGeographic(Vector p){

        double earth_r = 6371;

        double lat = Rad2Deg(Math.asin(p.z/(earth_r*1000)));
        double lon = Rad2Deg(Math.atan2(p.y, p.x));//this is correct

        return new LatLng(lat, lon);
    }

    //Trilateration algorithm, maths source: https://en.wikipedia.org/wiki/Trilateration
    public Vector findPosition(Vector v1, Vector v2, Vector v3, double d1, double d2, double d3){

        Vector ex = (Vector.substract(v2, v1)).normalise();
        double i = Vector.dot(ex, Vector.substract(v3, v1));
        Vector iex = ex.multiply(i);
        Vector temp = Vector.substract(Vector.substract(v3, v1), iex);
        Vector ey = temp.normalise();
        Vector ez = Vector.cross(ex, ey);
        double d = Vector.substract(v2, v1).norm();
        double j = Vector.dot(ey, Vector.substract(v3, v1));

        double x = (Math.pow(d1, 2) - Math.pow(d2, 2) + Math.pow(d, 2)) / (2*d);
        double y = ((Math.pow(d1, 2) - Math.pow(d3, 2) + Math.pow(i, 2) + Math.pow(j, 2))/(2*j)) - ((i/j)*x);

        double z = Math.sqrt(Math.abs(Math.pow(d1,2)-Math.pow(x,2)-Math.pow(y,2)));

        Vector tmp_x = ex.multiply(x);
        tmp_x = Vector.sum(tmp_x, v1);
        Vector tmp_y = ey.multiply(y);
        Vector tmp_z = ez.multiply(z);

        Vector pos = Vector.sum(tmp_x, tmp_y);
        pos = Vector.sum(pos, tmp_z);

        return pos;
    }

}

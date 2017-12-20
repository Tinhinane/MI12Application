package com.example.tinhinane.mi12application.Helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import com.example.tinhinane.mi12application.Models.Beacon;
import com.google.android.gms.maps.model.Circle;

import java.util.ArrayList;

/**
 * Created by tinhinane on 02/12/17.
 * RoomDrawing class is used to draw the canvas where the
 * beacons and user are position.
 * Canvas represents the lab room.
 * Physical width and length represents the real lab dimensions
 * Origin coordinates (0,0): (TOP, LEFT) of the Canvas
 */

public class RoomDrawing extends View {

    private static final double roomWidth = 5.20;//unit [m]
    private static final double roomHeight = 11.50;//unit [m]
    private static double scale = 0;
    public static int maxHeight;//Canvas unit
    public static int maxWidth;//Canvas unit
    int LEFT=0;
    int TOP=0;
    Rect r;
    Paint paint = new Paint();
    ArrayList<Vector> positions = new ArrayList<Vector>();
    ArrayList<Double> distances = new ArrayList<Double>();
    ArrayList<Beacon> beacons = new ArrayList<Beacon>();
    Vector userPos;
    public RoomDrawing(Context context, ArrayList<Beacon> beacons) {
        super(context);
        this.beacons = beacons;
        for (Beacon beacon : beacons){
            distances.add(beacon.getDistance());
        }

    }
    //Set beacon positions in the Canvas
    private void beaconPosition(Beacon b){
        Log.i("Beacon position", b.getmDeviceCode());
        if (b.getmDeviceCode().equals("F0:F9:90:D8:07:02")) {
            b.setPosition(new Vector(r.top, r.left, 0));
            this.positions.add(new Vector(r.top, r.left, 0));

        } else if (b.getmDeviceCode().equals("CA:29:A7:B8:6E:02")) {
            b.setPosition(new Vector(r.top, r.right, 0));
            this.positions.add(new Vector(r.right, 0, 0));
        } else if (b.getmDeviceCode().equals("F4:17:02:A7:4B:02")) {
            b.setPosition(new Vector(r.centerX(), r.centerY(), 0));
            this.positions.add(new Vector(r.centerX(), r.centerY(), 0));
        }
    }
    //Physical to canvas conversion
    public static final double scaleConvert(double physicalDistance){
        return physicalDistance*scale;
    }
    //Get conversion scale
    public static final void setScale(){
        scale = Math.round(maxHeight/roomHeight);
    }

    /**findUserPosition calculates the user position following the trilateration algorithm
     * @param v1,v2,v3 beacons positions in the frame of the Canvas
     * @param d1,d2,d3 distances between mobile and beacon converted to the scale of the Canvas
     * @return Vector that represents the position of the user in the room
     ***/
    public static Vector findUserPosition(Vector v1, Vector v2, Vector v3, double d1, double d2, double d3){
        d1 = scaleConvert(d1);
        d2 = scaleConvert(d2);
        d3 = scaleConvert(d3);
        Vector ex = (Vector.substract(v2, v1)).normalise();
        double i = Vector.dot(ex, Vector.substract(v3, v1));
        Vector iex = ex.multiply(i);
        Vector temp = Vector.substract(Vector.substract(v3, v1), iex);
        Vector ey = temp.normalise();
        double d = Vector.substract(v2, v1).norm();
        double j = Vector.dot(ey, Vector.substract(v3, v1));

        double x = (Math.pow(d1, 2) - Math.pow(d2, 2) + Math.pow(d, 2)) / (2*d);
        double y = ((Math.pow(d1, 2) - Math.pow(d3, 2) + Math.pow(i, 2) + Math.pow(j, 2))/(2*j)) - ((i/j)*x);

        Vector tmp_x = ex.multiply(x);
        tmp_x = Vector.sum(tmp_x, v1);
        Vector tmp_y = ey.multiply(y);

        Vector pos = Vector.sum(tmp_x, tmp_y);
        pos = Vector.sum(pos, new Vector(0, 0, 0));

        return pos;
    }

    @Override
    public void onDraw(Canvas canvas) {
        //Draw canvas
        maxHeight = canvas.getHeight();
        maxWidth = canvas.getWidth();
        setScale();
        canvas.translate((maxWidth/2)-(float)(scaleConvert(roomWidth)/2),0);
        super.onDraw(canvas);

        //Fill in Rectangle
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(107, 128, 154));
        r = new Rect(TOP, LEFT, (int)scaleConvert(roomWidth), (int)scaleConvert(roomHeight));
        canvas.drawRect(r, paint);
        //Set rectangle border
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setColor(Color.rgb(6, 34, 68));
        canvas.drawRect(r, paint);

        //Get beacon positions
        for (Beacon beacon : beacons){
            beaconPosition(beacon);
        }
        //Draw beacon positions
        for (Vector v : positions){
            drawBeacon(canvas, v);
        }
        //Find user position
        this.userPos = findUserPosition(positions.get(0), positions.get(1), positions.get(2), distances.get(0), distances.get(1), distances.get(2));
        drawUserPos(canvas, this.userPos);
    }

    void drawBeacon(Canvas canvas, Vector v){
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(135,206,250));
        canvas.drawCircle((float)v.x,(float)v.y,15,paint);
    }

    void drawUserPos(Canvas canvas, Vector v){
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        canvas.drawCircle((float)v.x,(float)v.y,10,paint);
        canvas.drawText("You are here!",(float)v.x-30,(float)v.y+25, paint);
    }

}

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
 */

public class RoomDrawing extends View {
    //Origin coordinates: O(LEFT,TOP)
    private static final double roomWidth = 5.20;//unit [m]
    private static final double roomHeight = 11.50;//unit [m]
    private static double scale = 0;
    public static int maxHeight;//Canvas unit
    public static int maxWidth;//Canvas unit
    int LEFT=0;
    int TOP=0;
    Paint paint = new Paint();
    ArrayList<Vector> positions = new ArrayList<Vector>();
    ArrayList<Double> distances = new ArrayList<Double>();
    Vector userPos;
    public RoomDrawing(Context context, ArrayList<Beacon> beacons) {
        super(context);
        for (Beacon beacon : beacons){
            positions.add(beacon.getPos());
            distances.add(beacon.getDistance());
        }

    }

    public static final double scaleConvert(double physicalDistance){
        return physicalDistance*scale;
    }

    public static final void setScale(){
        Log.i("Scale is:", Math.round(maxHeight/roomHeight)+"");
        scale = Math.round(maxHeight/roomHeight);
    }

    //Trilateration algorithm, maths source: https://en.wikipedia.org/wiki/Trilateration
    //v1, v2, v3 in FrameCanvas, d1, d2, d3 are also in the scale of FrameCanvas
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
        Rect r = new Rect(TOP, LEFT, (int)scaleConvert(roomWidth), (int)scaleConvert(roomHeight));
        canvas.drawRect(r, paint);

        //Set rectangle border
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setColor(Color.rgb(6, 34, 68));
        canvas.drawRect(r, paint);

        for (Vector v : positions){
            drawBeacon(canvas, v);
        }
        //Find user position
        this.userPos = findUserPosition(positions.get(0), positions.get(1), positions.get(2), distances.get(0), distances.get(1), distances.get(2));
        drawUserPos(canvas, this.userPos);
    }

    void drawBeacon(Canvas canvas, Vector v){
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        canvas.drawCircle((float)v.x,(float)v.y,10,paint);
    }

    void drawUserPos(Canvas canvas, Vector v){
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        canvas.drawCircle((float)v.x,(float)v.y,10,paint);
    }

}

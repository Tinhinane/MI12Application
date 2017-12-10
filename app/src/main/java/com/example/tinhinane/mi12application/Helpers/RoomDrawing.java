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
    Vector userPos;
    public RoomDrawing(Context context, ArrayList<Beacon> beacons, Vector userPos) {
        super(context);
        for (Beacon beacon : beacons){
            positions.add(beacon.getPos());
        }
        this.userPos = userPos;
    }

    public static final double scaleConvert(double physicalDistance){
        return physicalDistance*scale;
    }

    public static final void setScale(){
        Log.i("Scale is:", Math.round(maxHeight/roomHeight)+"");
        scale = Math.round(maxHeight/roomHeight);
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

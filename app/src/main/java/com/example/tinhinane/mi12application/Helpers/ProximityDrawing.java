package com.example.tinhinane.mi12application.Helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import com.example.tinhinane.mi12application.Models.Beacon;

import java.util.ArrayList;

/**
 * Created by tinhinane on 05/12/17.
 */

public class ProximityDrawing extends View {
    public int maxHeight;
    public int maxWidth;
    Paint paintC1 = new Paint();
    Paint paintC2 = new Paint();
    Paint paintC3 = new Paint();
    ArrayList<Beacon> beacons = new ArrayList<Beacon>();
    Vector origin = new Vector();//2D z=0

    public ProximityDrawing(Context context, ArrayList<Beacon> beacons) {
        super(context);
        this.beacons = beacons;
    }

    @Override
    public void onDraw(Canvas canvas) {
        //Prepare Canvas
        maxHeight = canvas.getHeight();
        maxWidth = canvas.getWidth();
        origin.x = maxWidth/2;
        origin.y = maxHeight/2;
        origin.z = 0;
        super.onDraw(canvas);
        //Draw circles
        drawCircle(canvas, paintC1, Color.GREEN, 100, "Immediate");//Less than 1m
        drawCircle(canvas, paintC2, Color.CYAN, 300, "Near");//Near ( 1<=d<3)
        drawCircle(canvas, paintC3, Color.RED, (float)(maxHeight-origin.y), "Far");//Far >3m
        for(Beacon beacon: beacons){
            Log.i("Check distance", beacon.getDistance() +" m");
            drawBeacon(canvas, beacon);
        }
    }

    private  void drawCircle(Canvas canvas, Paint paint, int color, float radius, String text){
        //Circle
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setColor(color);
        canvas.drawCircle((float)origin.x,(float)origin.y, radius, paint);
        //Text
        paint.setStrokeWidth(2);
        paint.setTextSize(15);
        canvas.drawText(text, (float)origin.x-50, (float)(maxHeight-origin.y)+(radius-100), paint);
    }

    private void drawBeacon(Canvas canvas, Beacon b){
        int zone = Beacon.proximityZone(b);

        //Distance is multiplied by 100, because every 1m represents 100units in the canvas
        if(zone == 0){
            paintC1.setStyle(Paint.Style.FILL);
            paintC1.setColor(Color.BLUE);
            paintC1.setTextSize(15);
            float d = (float)b.getDistance()*100;
            canvas.drawText(b.getmDeviceCode(),(float)origin.x+15,(float)origin.y+d, paintC1);
            canvas.drawCircle((float)origin.x,(float)origin.y+d,15,paintC1);
        }
        else if(zone == 1){
            paintC2.setStyle(Paint.Style.FILL);
            paintC2.setColor(Color.BLUE);
            paintC2.setTextSize(15);
            float d = (float)b.getDistance()*100;
            canvas.drawText(b.getmDeviceCode(),(float)origin.x+15,(float)origin.y+d, paintC2);
            canvas.drawCircle((float)origin.x,(float)origin.y+d,15,paintC2);
        }
        else{
            paintC3.setStyle(Paint.Style.FILL);
            paintC3.setColor(Color.BLUE);
            paintC3.setTextSize(20);
            float d = (float)b.getDistance()*100;
            if(d>(maxHeight-origin.y)){
                canvas.drawText(b.getmDeviceCode(),(float)origin.x+15,(float)origin.y+650, paintC3);
                canvas.drawCircle((float)origin.x,(float)maxHeight-10,15,paintC3);
            }
            else{
                canvas.drawText(b.getmDeviceCode(),(float)origin.x+15,(float)origin.y+d, paintC3);
                canvas.drawCircle((float)origin.x,(float)origin.y+d,15,paintC3);
            }

        }
    }
}

package com.example.tinhinane.mi12application.Helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorSpace;
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
        drawCircle(canvas, paintC3, Color.rgb(162,239,0), (float)(maxHeight-origin.y), "Far");
        drawCircle(canvas, paintC2, Color.rgb(3,137,156), 300, "Near");
        drawCircle(canvas, paintC1, Color.rgb(255,7,0), 100, "Immediate");//Less than 1m
        //Near ( 1<=d<3)
        //Far >3m

        for(int i=0; i<beacons.size();i++){
            if(i < beacons.size()-1){
                if(beacons.get(i).getDistance() <= beacons.get(i+1).getDistance()+0.5 && beacons.get(i).getDistance()>=beacons.get(i+1).getDistance()-0.5){
                    drawBeacon(canvas, beacons.get(i), true);
                }
                else{
                    drawBeacon(canvas, beacons.get(i), false);
                }
            }
            else{
                drawBeacon(canvas, beacons.get(i), false);
            }
        }
    }

    private  void drawCircle(Canvas canvas, Paint paint, int color, float radius, String text){
        //Circle
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(10);
        paint.setColor(color);
        canvas.drawCircle((float)origin.x,(float)origin.y, radius, paint);
        //Text
        paint.setStrokeWidth(2);
        paint.setTextSize(15);
        paint.setColor(Color.BLACK);
        canvas.drawText(text, (float)origin.x-50, (float)(maxHeight-origin.y)+(radius-100), paint);
    }

    private void drawFilledCircle(Canvas canvas, Paint paint, double distance, String text, boolean closeness){
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
        paint.setTextSize(15);
        float d = (float)distance*100;
        if(d>(maxHeight-origin.y)){
            if(!closeness){
                canvas.drawText(text,(float)origin.x,(float)maxHeight-45, paintC3);
                canvas.drawCircle((float)origin.x,(float)maxHeight-50,15,paintC3);
            }else{
                canvas.drawText(text,(float)origin.x-40,(float)maxHeight-55, paintC3);
                canvas.drawCircle((float)origin.x-40,(float)maxHeight-40,15,paintC3);
            }
        }
        else{
            if(!closeness){
                canvas.drawText(text,(float)origin.x,(float)origin.y+d+30, paintC3);
                canvas.drawCircle((float)origin.x,(float)origin.y+d,15,paintC3);
            }else{
                canvas.drawText(text,(float)origin.x-40,(float)origin.y+d+30, paintC3);
                canvas.drawCircle((float)origin.x-40,(float)origin.y+d,15,paintC3);
            }
        }
    }

    private void drawBeacon(Canvas canvas, Beacon b, boolean closeness){
        int zone = Beacon.proximityZone(b);

        //Distance is multiplied by 100, because every 1m represents 100units in the canvas
        if(zone == 0){
           drawFilledCircle(canvas, paintC1, b.getDistance(), b.getmDeviceCode(), closeness);
        }
        else if(zone == 1){
            drawFilledCircle(canvas, paintC2, b.getDistance(), b.getmDeviceCode(), closeness);
        }
        else{
            drawFilledCircle(canvas, paintC3, b.getDistance(), b.getmDeviceCode(), closeness);
        }
    }
}

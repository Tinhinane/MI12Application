package com.example.tinhinane.mi12application.Helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.example.tinhinane.mi12application.Models.Beacon;

import java.util.ArrayList;

/**
 * Created by tinhinane on 05/12/17.
 */

public class ProximityDrawing extends View {
    Paint paintC1 = new Paint();
    Paint paintC2 = new Paint();
    Paint paintC3 = new Paint();
    ArrayList<Beacon> beacons = new ArrayList<Beacon>();
    Vector origin = new Vector(350, 350, 0);//2D z=0

    public ProximityDrawing(Context context, ArrayList<Beacon> beacons) {
        super(context);
        this.beacons = beacons;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paintC1.setStyle(Paint.Style.STROKE);
        paintC1.setColor(Color.GREEN);
        paintC1.setTextSize(20);
        paintC2.setStyle(Paint.Style.STROKE);
        paintC2.setColor(Color.CYAN);
        paintC2.setTextSize(20);
        paintC3.setStyle(Paint.Style.STROKE);
        paintC3.setColor(Color.RED);
        paintC3.setTextSize(20);
        canvas.drawCircle((float)origin.x,(float)origin.y, 100,paintC1);//Immediate (less than 1m)
        canvas.drawText("Immediate", 300, 300, paintC1);
        canvas.drawCircle((float)origin.x,(float)origin.y, 300, paintC2);//Near ( 1<=d<3)
        canvas.drawText("Near", 550, 450, paintC2);
        canvas.drawCircle((float)origin.x,(float)origin.y, 600, paintC3);
        canvas.drawText("Far", 650,650, paintC3);
        for(Beacon beacon: beacons){
            drawBeacon(canvas, beacon);
        }
    }

    private void drawBeacon(Canvas canvas, Beacon b){
        int zone = Beacon.proximityZone(b);
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
            if(d>650){
                canvas.drawText(b.getmDeviceCode(),(float)origin.x+15,(float)origin.y+650, paintC3);
                canvas.drawCircle((float)origin.x,(float)origin.y+650,15,paintC3);
            }
            else{
                canvas.drawText(b.getmDeviceCode(),(float)origin.x+15,(float)origin.y+d, paintC3);
                canvas.drawCircle((float)origin.x,(float)origin.y+d,15,paintC3);
            }

        }
    }
}

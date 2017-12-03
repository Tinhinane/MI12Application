package com.example.tinhinane.mi12application.Helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.example.tinhinane.mi12application.Models.Beacon;
import com.google.android.gms.maps.model.Circle;

import java.util.ArrayList;

/**
 * Created by tinhinane on 02/12/17.
 */

public class Drawing extends View {
    //Origin coordinates: O(LEFT,TOP)

    int LEFT  =0;
    int TOP  =0;
    int RIGHT  =700;
    int BOTTOM  =700;
    Paint paint = new Paint();
    Rect r = new Rect(LEFT, TOP, RIGHT, BOTTOM);
    public Canvas canvas_;
    ArrayList<Vector> positions = new ArrayList<Vector>();
    Vector userPos;
    public Drawing(Context context, ArrayList<Beacon> beacons, Vector userPos) {
        super(context);
        for (Beacon beacon : beacons){
            positions.add(beacon.getPos());
        }
        this.userPos = userPos;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // fill
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);
        canvas.drawRect(r, paint);

        // border
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
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

package com.example.tinhinane.mi12application.Helpers;

/**
 * Created by tinhinane on 03/12/17.
 */

public class MapHelper {
    private static final double scale = 700/3;

    public static double scaleConvert(double d){
        return d*scale;
    }

    //Trilateration algorithm, maths source: https://en.wikipedia.org/wiki/Trilateration
    //v1, v2, v3 in FrameCanvas, d1, d2, d3 are also in the scale of FrameCanvas
    public static Vector findPosition(Vector v1, Vector v2, Vector v3, double d1, double d2, double d3){
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

}

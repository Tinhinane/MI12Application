package com.example.tinhinane.mi12application;

/**
 * Created by tinhinane on 20/11/17.
 */

public class Vector {

    public double x;
    public double y;
    public double z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(){
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public static Vector substract(Vector v1, Vector v2){
        return new Vector(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
    }

    public static Vector sum(Vector v1, Vector v2){
        return new Vector(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
    }

    //dot product
    public static double dot(Vector v1, Vector v2){
        return ((v1.x*v2.x) + (v1.y*v2.y) + (v1.z*v2.z));
    }

    //cross product
    public static Vector cross(Vector v1, Vector v2){
        return new Vector ((v1.y*v2.z) - (v1.z*v2.y),
                (v1.z*v2.x) - (v1.x*v2.z),
                (v1.x*v2.y) - (v1.y*v2.x));
    }

    //divide a vector by a constant
    public Vector divide(double cte){
        return new Vector(this.x/cte, this.y/cte, this.z/cte);
    }

    //multiply a vector by a constant
    public Vector multiply(double cte){
        return new Vector(this.x*cte, this.y*cte, this.z*cte);
    }

    public double norm() {
        return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2));
    }

    public Vector normalise() {
        double d = this.norm();
        return new Vector(this.x/d, this.y/x, this.z/d);
    }

    @Override
    public String toString() {
        return "Vector{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

}

package com.google.ar.core.examples.java.common.helpers;


public class Point {
    public float x;
    public float y;
    public float z;
    public float replacedX;

    public Point(float x,float y,float z) {
        this.x=x;
        this.y=y;
        this.z=z;
    }
    public void setReplacedX(float n){
        replacedX=n;
    }
    public float getY() {
        return y*-800f;
    }
}

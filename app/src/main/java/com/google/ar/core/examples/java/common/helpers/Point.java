package com.google.ar.core.examples.java.common.helpers;

//自定義了一個點-可以儲存各種資料
public class Point {
    public float x;
    public float y;
    public float z;
    public float replacedX;
    public int color;
    public Point(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public Point(float x,float y,float z,int color) {
        this.x=x;
        this.y=y;
        this.z=z;
        this.color=color;
    }
    public void setReplacedX(float n){
        replacedX=n;
    }
    public float getY() {
        return y*-800f;
    }
}

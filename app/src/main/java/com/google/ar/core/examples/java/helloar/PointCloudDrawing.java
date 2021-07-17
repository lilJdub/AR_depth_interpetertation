package com.google.ar.core.examples.java.helloar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.google.ar.core.examples.java.common.helpers.Point;

import java.util.ArrayList;

public class PointCloudDrawing extends Activity {
    public ArrayList<Point>pp;
    public PointCloudView customCanvas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pp=PointCloudSaving.pointC;
        customCanvas=new PointCloudView(this,null);
        customCanvas.setBackgroundColor(Color.GRAY);
        setContentView(customCanvas);
    }
}

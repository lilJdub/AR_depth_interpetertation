package com.google.ar.core.examples.java.helloar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

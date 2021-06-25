package com.google.ar.core.examples.java.helloar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.ar.core.examples.java.common.helpers.Point;

import java.util.ArrayList;
public class PointCloudView extends View {
    private static final String TAG = "pointCloudView";
    private static final String TAG2 = "ReplaceX";
    private Paint pointPaint;
    private Paint startPaint;
    private final int paintColor=Color.RED;
    public ArrayList<Point>pp;
    public PointCloudView(Context context, AttributeSet attrs){
        super(context,attrs);
        setFocusable(true);
        setup();
    }
    private void setup(){
        pointPaint=new Paint();
        pointPaint.setColor(paintColor);
        pointPaint.setAntiAlias(true);
        pointPaint.setStrokeWidth(20);
        startPaint=new Paint();
        startPaint.setColor(Color.CYAN);
        startPaint.setAntiAlias(true);
        startPaint.setStrokeWidth(20);
    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        pp=PointCloudSaving.pointC;
        float save = 0;
        /*for(Point p:pp) {
            if(p.getX()<0){
                save=p.getX()*-1;
                canvas.drawPoint(save, p.getY(), pointPaint);
                Log.d(TAG, "onDraw:x=" + save + " y:" + p.getY());
            }
            else{
                canvas.drawPoint(p.getX(),p.getY(),pointPaint);
                Log.d(TAG, "onDraw:x=" + p.getX() + " y:" + p.getY());
            }
        }*/

        //finding out starting point

        rePlaceX(pp);
        for(Point p:pp){
            canvas.drawPoint(p.replacedX*300f+100f, p.getY(), pointPaint);
            Log.d(TAG, "onDraw:x=" + save + " y:" + p.getY());
        }
    }
    public void rePlaceX(ArrayList<Point> poi){
        float min=999999;
        //find min
        for(Point p:poi){
            if (p.x<min){
                min=p.x;
            }
        }
        //正規最小頂點
        for(Point q:poi){
            q.setReplacedX(q.x+(-1*min));
            Log.d(TAG2, "rePlace: newX"+q.replacedX);
        }
    }
}
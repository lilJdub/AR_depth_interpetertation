package com.google.ar.core.examples.java.helloar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.ar.core.examples.java.common.helpers.Point;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

//成果出現ㄉ頁面-但其實只是作一個ACTIVITY給人家放，畫什麼東西出來要看Pointcloudview那裏
public class PointCloudDrawing extends Activity {
    private static final String TAG ="starting save...";
    public ArrayList<Point>pp;
    public PointCloudView customCanvas;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private static final String OkTAG ="Success";
    private static final String NoTAG ="Success";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pp=PointCloudSaving.pointC;
        customCanvas=new PointCloudView(this,null);
        customCanvas.setBackgroundColor(Color.GRAY);
        setContentView(customCanvas);
        Log.d(TAG, "onCreate: starting save...");

        //這邊:)
        //我想在這邊把pp存進我的firebase帳號裡面 但基本上下面兩行logd都沒有出來...
        mDatabase= FirebaseDatabase.getInstance("https://deepnightdeepvibes-default-rtdb.firebaseio.com/");
        mDatabaseReference= mDatabase.getReference();
        DatabaseReference pointRef=mDatabaseReference.child("pointCloud");
        pointRef.setValue(pp)
                .addOnSuccessListener(unused -> Log.d(OkTAG, "onSuccess: Object saved successfully"))
                .addOnFailureListener(e -> Log.d(NoTAG, "onFailure: Object saving failed"));
        //https://deepnightdeepvibes-default-rtdb.firebaseio.com/
    }
}

package com.google.ar.core.examples.java.helloar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.ar.core.examples.java.common.helpers.Point;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PointCloudDrawing extends Activity {
    public ArrayList<Point>pp;
    public PointCloudView customCanvas;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mDatabase.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pp=PointCloudSaving.pointC;
        customCanvas=new PointCloudView(this,null);
        customCanvas.setBackgroundColor(Color.GRAY);
        setContentView(customCanvas);

        //instatiate button
        Button saveBtn=findViewById(R.id.savePointCloud);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInfo();
            }
        });
    }

    //method for saving
    private void saveInfo(){
        mDatabaseReference = mDatabase.getReference().child("PointCloud");
        mDatabaseReference.setValue(pp);
    }
}

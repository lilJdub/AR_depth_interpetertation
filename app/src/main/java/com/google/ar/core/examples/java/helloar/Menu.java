package com.google.ar.core.examples.java.helloar;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Menu extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button scanningBtn=findViewById(R.id.ScanningBtn);
        scanningBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScan();
            }
        });
        Button viewMapBtn=findViewById(R.id.viewMapBtn);
        viewMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openViewMap();
            }
        });
    }
    public void openScan(){
        Intent intent=new Intent(this,ScanActivity.class);
        startActivity(intent);
    }
    public void openViewMap(){
        Intent intent=new Intent(this,ViewMaps.class);
        startActivity(intent);
    }
}
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

    //firebase
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private DatabaseReference myNextChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pp=PointCloudSaving.pointC;
        customCanvas=new PointCloudView(this,null);
        customCanvas.setBackgroundColor(Color.GRAY);
        setContentView(customCanvas);
        Log.d(TAG, "onCreate: starting save...");

        //把pp存進我的firebase帳號裡面.
        //Authentication
        mAuth= FirebaseAuth.getInstance();
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        myRef=mFirebaseDatabase.getReference();
        //看看用戶是否仍在登入狀態
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=mAuth.getCurrentUser();
                if(user!=null){
                    Log.d(TAG, "onAuthStateChanged:signed_in");
                }
                else{
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        savePointCloudtoFirebase();
    }

    //主要儲存的function
    public void savePointCloudtoFirebase(){
        mDatabase= FirebaseDatabase.getInstance().getReference();
        pp=PointCloudSaving.pointC;
        for(Point p:pp){
            //用push()製造一個全新的子點以供辨識
            myNextChild = mDatabase.push();
           //在子點內儲存值
            myNextChild.setValue(p);
            Log.d(TAG, "savePointCloudtoFirebase : Saving Point....");
        }
    }
}

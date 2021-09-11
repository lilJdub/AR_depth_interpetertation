package com.google.ar.core.examples.java.helloar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.ar.core.examples.java.common.helpers.Point;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DrawingPointFromDB extends Activity {
    private DatabaseReference rootRef;
    private DatabaseReference childRef;

    private ValueEventListener valueEventListener;
    private static final String TAG ="reading save...";
    private TextView tv;
    //firebase
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //暫存
    private ArrayList<Point> points;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_point_from_db);
        rootRef= FirebaseDatabase.getInstance().getReference();
        childRef=rootRef.child("arcore-f3cb4-default-rtdb");
        tv=findViewById(R.id.textViewSeeDB);
        points=new ArrayList<Point>();
        //Authentication
        mAuth= FirebaseAuth.getInstance();
        mFirebaseDatabase=FirebaseDatabase.getInstance();
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
        //重點:用Valuelistener監聽資料庫狀態，再用datasnapshot(資料快照)抓下資料
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    //XYZ照下面這樣拿就好，但我不知道為啥float變成了double了...可能要轉個檔...之類的...?
                    Double X=ds.child("x").getValue(Double.class);
                    tv.append("X"+X+"\n");
                    Double Y=ds.child("y").getValue(Double.class);
                    tv.append("Y"+Y+"\n");
                    Double Z=ds.child("z").getValue(Double.class);
                    tv.append("Z"+Z+"\n");
                    Integer A=ds.child("a").getValue(Integer.class);
                    tv.append("A"+A+"\n");
                    Integer R=ds.child("r").getValue(Integer.class);
                    tv.append("R"+R+"\n");
                    Integer G=ds.child("g").getValue(Integer.class);
                    tv.append("G"+G+"\n");
                    Integer B=ds.child("b").getValue(Integer.class);
                    tv.append("B"+B+"\n");
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.d(TAG, "onCancelled: error.");
            }
        };
        rootRef.addListenerForSingleValueEvent(valueEventListener);
    }
}
package com.google.ar.core.examples.java.helloar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DrawingPointFromDB extends Activity {
    private DatabaseReference rootRef;
    private DatabaseReference childRef;

    private ValueEventListener valueEventListener;
    private static final String TAG ="reading save...";
    private TextView tv;

    public int colors[];
    public float vertex_list[];

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

        Button passIntent=findViewById(R.id.passIntent);
        passIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass_intent_to_OpenGL();
            }
        });
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
                //init arraylist
                ArrayList<Integer> color=new ArrayList<>();
                ArrayList<Float> vertex=new ArrayList<>();
                for(DataSnapshot ds:snapshot.getChildren()){
                    //XYZ照下面這樣拿就好，但我不知道為啥float變成了double了...可能要轉個檔...之類的...?

                    Double X=ds.child("x").getValue(Double.class);
                    if(X!=null){
                        float X1=Float.valueOf(String.valueOf(X));
                        vertex.add(X1);
                    }
                    else{
                        Log.d(TAG, "onDataChange:null happened" );
                    }
                    Double Y=ds.child("y").getValue(Double.class);
                    if(Y!=null){
                        float Y1=Float.valueOf(String.valueOf(Y));
                        vertex.add(Y1);
                    }
                    else{
                        Log.d(TAG, "onDataChange: null expected");
                    }
                    Double Z=ds.child("z").getValue(Double.class);
                    if(Z!=null){
                        float Z1=Float.valueOf(String.valueOf(Z));
                        vertex.add(Z1);
                    }
                    else {
                        Log.d(TAG, "onDataChange: null happened");
                    }
                    Integer R=ds.child("r").getValue(Integer.class);
                    if(R!=null){
                        color.add(R);

                    }
                    Integer G=ds.child("g").getValue(Integer.class);
                    if(G!=null){
                        color.add(G);
                    }
                    Integer B=ds.child("b").getValue(Integer.class);
                    if(B!=null){
                        color.add(B);
                    }
                    Integer A=ds.child("a").getValue(Integer.class);
                    if(A!=null){
                        color.add(A);
                    }
                }

                colors=new int[color.size()];
                vertex_list=new float[vertex.size()];

                for(int i=0;i<color.size()-1;i++){
                    colors[i]=color.get(i);

                }
                for(int i=0;i<vertex.size()-1;i++){
                    vertex_list[i]=vertex.get(i);
                }

            }


            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.d(TAG, "onCancelled: error.");
            }
        };
        rootRef.addListenerForSingleValueEvent(valueEventListener);

    }
    public void pass_intent_to_OpenGL(){
        Intent intent=new Intent(this,OpenGLdemo.class);
        intent.putExtra("ColorArray",colors);
        intent.putExtra("Vertex_Array",vertex_list);
        startActivity(intent);
    };
}
package com.google.ar.core.examples.java.helloar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import android.widget.Spinner;

import java.util.ArrayList;


public class ViewMaps extends Activity {

    private static final String TAG = "tester";
    //firebase
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private DatabaseReference rootRef;
    private DatabaseReference myNextChild;
    public ValueEventListener valueEventListener;

    public ArrayList<String> buildings=new ArrayList<String>();
    public String buildingList[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_maps);

        //mAuth
        mAuth= FirebaseAuth.getInstance();
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        myRef=mFirebaseDatabase.getReference();
        //看看用戶是否仍在登入狀態
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            private static final String TAG ="authentication";
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
        valueEventListener = new ValueEventListener() {
            private static final String TAG = "TEST";

            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot child:snapshot.getChildren()){
                    String BuildingName=child.getKey();
                    Log.d(TAG, "onDataChange: "+BuildingName);
                    buildings.add(BuildingName);
                }
                Log.d("size of buildings",""+buildings.size());
                buildingList= new String[buildings.size()];

                //generate all buildings list
                for(int i = 0; i <= buildings.size() - 1; i++){
                    buildingList[i]=buildings.get(i);
                }

                Spinner spinner=new Spinner(ViewMaps.this);
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };

        rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(valueEventListener);


    }
}
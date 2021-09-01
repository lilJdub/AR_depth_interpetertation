package com.google.ar.core.examples.java.helloar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//主畫面-第一頁:有點無關緊要，那時候我試著給會員做登入的時候用到的 放了一個skip button跳過冗長的登入
public class FirstPage extends Activity {
    private FirebaseAuth mAuth;
    public Button subBtn;
    public Button logBtn;
    private static final String TAG = "create_acc";
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstpage);
        //HERE
        mAuth = FirebaseAuth.getInstance();


        Button insta=(Button)findViewById(R.id.loginbtn);
        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instantiate();
            }
        });
        Button skip=(Button)findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip();
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }
    public void instantiate(){
        RelativeLayout layout1=(RelativeLayout)findViewById(R.id.MainLayout);
        RelativeLayout layout2=(RelativeLayout) findViewById(R.id.LoginLayout);
        layout1.setVisibility(View.GONE);
        layout2.setVisibility(View.VISIBLE);
        subBtn =(Button)findViewById(R.id.sub);
        logBtn=(Button)findViewById(R.id.log);
        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView psw=(TextView)findViewById(R.id.passwrdinput);
                TextView em=(TextView)findViewById(R.id.emailinput);
                String password=psw.getText().toString();
                String email=em.getText().toString();
                createAccount(email,password);
            }
        });
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView psw=(TextView)findViewById(R.id.passwrdinput);
                TextView em=(TextView)findViewById(R.id.emailinput);
                String password=psw.getText().toString();
                String email=em.getText().toString();
                signIn(email,password);
            }
        });
    }
    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {


                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //inflateName(user);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(FirstPage.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
        // [END create_user_with_email]

    }
    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(FirstPage.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END sign_in_with_email]
        saveData();
    }
    public void updateUI(FirebaseUser user){
        if(user==null){
            Toast.makeText(FirstPage.this, "This is'nt in!",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            //String userName = user.getDisplayName();
            //這等HAAAAAAAARVEY再修
            String userid=user.getUid();
            Toast.makeText(FirstPage.this, "Hello"+userid,
                    Toast.LENGTH_SHORT).show();

            Intent intent=new Intent(this,ScanActivity.class);
            intent.putExtra("bruh",user);
            startActivity(intent);
        }
    }
    public void skip(){
        Intent intentS=new Intent(this,ScanActivity.class);
        startActivity(intentS);
    }
    //tester method for saving data:
    public void saveData(){
        //mDatabase = FirebaseDatabase.getInstance().getReference();
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
    }
}

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
import com.google.firebase.database.FirebaseDatabase;

//主畫面-第一頁:有點無關緊要，那時候我試著給會員做登入的時候用到的 放了一個skip button跳過冗長的登入
public class FirstPage extends Activity {
    private FirebaseAuth mAuth;
    public Button subBtn;
    public Button logBtn;
    private static final String TAG = "create_acc_type_vibe";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstpage);
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
                            //給他們輸入名字
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
    /*private void inflateName(FirebaseUser user){

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        View view = findViewById(R.id.LoginLayout);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        Button namebtn=(Button)findViewById(R.id.UserBtn);
        namebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName("Jane Q. User")
                        .build();
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User profile updated.");
                                }
                            }
                        });
            }
        });
        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }*/
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
    }
    public void updateUI(FirebaseUser user){
        if(user==null){
            Toast.makeText(FirstPage.this, "This is'nt in!",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            //String userName = user.getDisplayName();
            //這等之後再修
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
}

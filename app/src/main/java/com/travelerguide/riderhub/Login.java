package com.travelerguide.riderhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {


    EditText email,password;
    Button signin;
    TextView forgotpw,register;
    LinearLayout loadingbar;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            if(!currentUser.getEmail().equals("admin@gmail.com")){
                Intent intent=new Intent(Login.this,Home.class);
                startActivity(intent);
                finish();
            }
            else {
                Intent intent=new Intent(Login.this,adminsHome.class);
                startActivity(intent);
                finish();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email=findViewById(R.id.txt_email);
        password=findViewById(R.id.txt_password);
        signin=findViewById(R.id.btn_reset);
        forgotpw=findViewById(R.id.btn_forgotpw);
        register=findViewById(R.id.btn_regnow);
        loadingbar=findViewById(R.id.loadingbar);
        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc= GoogleSignIn.getClient(this,gso);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupintent=new Intent(Login.this,signUp.class);
                startActivity(signupintent);
                finish();
            }
        });

        forgotpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotpwintent=new Intent(Login.this,forgotPW.class);
                startActivity(forgotpwintent);
                finish();
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String semail,spassword;
                semail=email.getText().toString();
                spassword=password.getText().toString();

                if(TextUtils.isEmpty(semail)){
                    email.setText("");
                    email.setError("Enter a valid email!");
                    return;
                }
                if(TextUtils.isEmpty(spassword)){
                    password.setText("");
                    password.setError("Enter a valid password!");
                    return;
                }
                signin(semail,spassword,signin);
            }
        });


    }

    void signin(String email,String password,Button login){
        loadingbar.setVisibility(View.VISIBLE);
        login.setEnabled(false);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(!email.equals("admin@gmail.com")){
                                loadingbar.setVisibility(View.INVISIBLE);
                                Log.d("signincheck", "signInWithEmail:success");
                                Intent intent=new Intent(Login.this,Home.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                loadingbar.setVisibility(View.INVISIBLE);
                                Log.d("signincheck", "signInWithEmail:success");
                                Intent intent=new Intent(Login.this, adminsHome.class);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            loadingbar.setVisibility(View.INVISIBLE);
                            Log.w("signincheck", "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "login failed", Toast.LENGTH_SHORT).show();
                            login.setEnabled(true);
                        }

                    }
                });
    }
}
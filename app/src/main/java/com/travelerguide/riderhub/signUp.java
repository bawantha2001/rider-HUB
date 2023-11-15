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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signUp extends AppCompatActivity {

    EditText email,password,confirmPassword,name,phoneNo;
    Button btnregister;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    LinearLayout loadingbar;
    FirebaseFirestore fstore=FirebaseFirestore.getInstance();
    String userID;


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            if(!currentUser.getEmail().equals("admin@gmail.com")){
                Intent intent=new Intent(signUp.this,Home.class);
                startActivity(intent);
                finish();
            }
            else {
                Intent intent=new Intent(signUp.this,adminsHome.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email=findViewById(R.id.txt_email);
        password=findViewById(R.id.txt_password);
        confirmPassword=findViewById(R.id.txt_confirmpassword);
        btnregister=findViewById(R.id.btn_reg);
        loadingbar=findViewById(R.id.loadingbar);
        name=findViewById(R.id.txt_name);
        phoneNo=findViewById(R.id.txt_phoneNo);

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String semail,spassword,sconfirmPassword,sname,sphoneNo;


                semail=email.getText().toString();
                spassword=password.getText().toString();
                sconfirmPassword=confirmPassword.getText().toString();
                sname=name.getText().toString();
                sphoneNo=phoneNo.getText().toString();

                if(TextUtils.isEmpty(semail)){
                    email.setText("");
                    email.setError("Enter a valid email!");
                    return;
                }
                if(TextUtils.isEmpty(spassword)&&spassword.length()>=6){
                    password.setText("");
                    password.setError("Enter a valid password!(More than 6 characters)!");
                    return;
                }

                if(!spassword.equals(sconfirmPassword)){
                    confirmPassword.setText("");
                    password.setText("");
                    Toast.makeText(signUp.this, "Both passwords must be same!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(sname)){
                    name.setText("");
                    name.setError("Enter a valid Name!");
                    return;
                }
                if(TextUtils.isEmpty(sphoneNo)){
                    phoneNo.setText("");
                    phoneNo.setError("Enter a valid email!");
                    return;
                }

                registeruser(semail,spassword,sname,sphoneNo,btnregister);
            }
        });
    }

    void registeruser(String email,String password,String name,String phoneNo,Button signup){
        signup.setEnabled(false);
        loadingbar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Signupcheck", "createUserWithEmail:success");
                            Toast.makeText(signUp.this, "successful", Toast.LENGTH_SHORT).show();
                            userID=auth.getCurrentUser().getUid();
                            DocumentReference documentReference=fstore.collection("users").document(userID);
                            Map<String,Object> user=new HashMap<>();
                            user.put("Name",name);
                            user.put("email",email);
                            user.put("phoneNo",phoneNo);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.w("Signupcheck","Data Saved");
                                }
                            });
                            loadingbar.setVisibility(View.INVISIBLE);
                            Intent intent=new Intent(signUp.this,Login.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Signupcheck", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(signUp.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            loadingbar.setVisibility(View.INVISIBLE);
                            signup.setEnabled(true);
                        }
                    }
                });
        }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(signUp.this, Login.class);
        startActivity(intent);
        finish();
    }
}
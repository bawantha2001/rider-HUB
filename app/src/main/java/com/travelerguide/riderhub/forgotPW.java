package com.travelerguide.riderhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class forgotPW extends AppCompatActivity {

    EditText email;
    Button reset;
    LinearLayout loadingbar;
    FirebaseAuth auth=FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pw);
        email=findViewById(R.id.txt_email);
        reset=findViewById(R.id.btn_reset);
        loadingbar=findViewById(R.id.loadingbar);
        reset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String semail;
                semail=email.getText().toString();
                if(TextUtils.isEmpty(semail)){
                    email.setError("Enter a valid email!");
                    return;
                }
                reset(semail,reset);
                clear(email);
            }
        });
    }

    private void reset(String email,Button reset) {
        loadingbar.setVisibility(View.VISIBLE);
        reset.setEnabled(false);
        auth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(forgotPW.this, "Reset email was successfully sent to your email", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(forgotPW.this,Login.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(forgotPW.this, "Reset failed", Toast.LENGTH_SHORT).show();
                loadingbar.setVisibility(View.VISIBLE);
                reset.setEnabled(false);
            }
        });

    }

    private void clear(EditText email){
        email.setText("");
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(forgotPW.this, Login.class);
        startActivity(intent);
        finish();
    }
}
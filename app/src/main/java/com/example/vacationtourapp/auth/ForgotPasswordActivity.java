package com.example.vacationtourapp.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vacationtourapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText emailForgot;
    Button requestLink;
    ProgressDialog loadingBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        emailForgot = findViewById(R.id.emailForgot);
        requestLink = findViewById(R.id.requestLink);

        requestLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailForgot.getText().toString();
                if (email.isEmpty()){
                    Toast.makeText(ForgotPasswordActivity.this, "Field is empty!", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setMessage("please wait...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ForgotPasswordActivity.this, "Check your mail...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                finish();
                            } else {
                                String msg = task.getException().getMessage();
                                Toast.makeText(ForgotPasswordActivity.this, msg, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }
}
package com.example.vacationtourapp.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vacationtourapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AdminLoginActivity extends AppCompatActivity {

    EditText emailAdminLogin, passwordAdminLogin;
    Button loginAdminBtn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        mAuth = FirebaseAuth.getInstance();

        emailAdminLogin = findViewById(R.id.emailAdminLogin);
        passwordAdminLogin = findViewById(R.id.passwordAdminLogin);
        loginAdminBtn = findViewById(R.id.loginAdminBtn);
        loginAdminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailAdminLogin.getText().toString();
                String password = passwordAdminLogin.getText().toString();

                /*Intent intent = new Intent(getApplicationContext(), AdminMainActivity.class);
                startActivity(intent);*/

                if (email.isEmpty() && password.isEmpty()){
                    Toast.makeText(AdminLoginActivity.this, "Field's are empty", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminLoginActivity.this, "please wait...", Toast.LENGTH_SHORT).show();
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Intent intent = new Intent(getApplicationContext(), AdminMainActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(AdminLoginActivity.this, "logged in as admin", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
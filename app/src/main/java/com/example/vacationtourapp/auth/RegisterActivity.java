package com.example.vacationtourapp.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vacationtourapp.MainActivity;
import com.example.vacationtourapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText nameRegister, phoneRegister, emailRegister, passwordRegister;
    Button registerBtn;
    FirebaseAuth mAuth;
    DatabaseReference usersRef;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loadingBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        nameRegister = findViewById(R.id.nameRegister);
        phoneRegister = findViewById(R.id.phoneRegister);
        emailRegister = findViewById(R.id.emailRegister);
        passwordRegister = findViewById(R.id.passwordRegister);
        registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameRegister.getText().toString();
                String phone = phoneRegister.getText().toString();
                String email = emailRegister.getText().toString();
                String password = passwordRegister.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Field's are empty!", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setMessage("please wait...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String userId = mAuth.getCurrentUser().getUid();
                                HashMap userMap = new HashMap();
                                userMap.put("Name", name);
                                userMap.put("Email", email);
                                userMap.put("Phone", phone);
                                userMap.put("Password", password);
                                userMap.put("userId", userId);
                                userMap.put("image", "default");
                                userMap.put("Address", "NA");
                                usersRef.child(userId).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            String msg = task.getException().getMessage();
                                            Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                                        }
                                        loadingBar.dismiss();
                                    }
                                });
                            }
                            ;
                        }
                    });
                }
            }
        });

    }

    public void goToLoginPage(View view) {
        finish();
    }
}
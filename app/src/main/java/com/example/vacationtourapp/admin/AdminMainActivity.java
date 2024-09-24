package com.example.vacationtourapp.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.vacationtourapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class AdminMainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        mAuth = FirebaseAuth.getInstance();

    }

    public void goToAddPlacesActivity(View view) {
        Intent intent = new Intent(this, AddPlacesActivity.class);
        startActivity(intent);
    }

    public void goToManagePlacesActivity(View view) {
        Intent intent = new Intent(this, ManagePlacesActivity.class);
        startActivity(intent);
    }

    public void goToViewUsersActivity(View view) {
        Intent intent = new Intent(this, ViewUsersActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mAuth.signOut();
        //finish();
    }

    public void goToViewRequestActivity(View view) {
        Intent intent = new Intent(this, ViewVisitRequestActivity.class);
        startActivity(intent);
    }
}
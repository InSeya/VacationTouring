package com.example.vacationtourapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DetailsActivity extends AppCompatActivity {

    ImageView placeImageView;
    TextView placeNameText, placeAddressText, placePriceText, placeAboutText, interestedBtn;
    String PlaceID, currUserId;
    FirebaseAuth mAuth;
    ProgressDialog loadingBar;
    DatabaseReference placeRef, interestRef, interestUserRef;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mAuth = FirebaseAuth.getInstance();
        PlaceID = getIntent().getStringExtra("PlaceID");

        builder = new AlertDialog.Builder(this);
        loadingBar = new ProgressDialog(this);

        currUserId = mAuth.getCurrentUser().getUid();
        placeRef = FirebaseDatabase.getInstance().getReference().child("Places");
        interestRef = FirebaseDatabase.getInstance().getReference().child("Interested");
        interestUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currUserId).child("Interested");

        placeImageView = findViewById(R.id.placeImageView);
        placeNameText = findViewById(R.id.placeNameText);
        placeAddressText = findViewById(R.id.placeAddressText);
        placeAboutText = findViewById(R.id.placeAboutText);
        placePriceText = findViewById(R.id.placePriceText);
        interestedBtn = findViewById(R.id.interestedBtn);

        placeRef.child(PlaceID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("PlaceName").getValue().toString();
                    String address = snapshot.child("PlaceAddress").getValue().toString();
                    String price = snapshot.child("PlacePrice").getValue().toString();
                    String image = snapshot.child("PlaceImage").getValue().toString();
                    String about = snapshot.child("PlaceAbout").getValue().toString();
                    placeNameText.setText(name);
                    placeAddressText.setText(address);
                    placePriceText.setText(price);
                    placeAboutText.setText(about);

                    if (!image.equals("default")) {
                        Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.profile).into(placeImageView);
                        Picasso.with(getApplicationContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into(placeImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.profile).into(placeImageView);
                            }
                        });
                    }

                    interestedBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            builder.setTitle("Confirmation!")
                                    .setMessage("Click yes to confirm your interest to visit " + name)
                                    .setCancelable(true)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            loadingBar.setTitle("please wait...");
                                            loadingBar.setCanceledOnTouchOutside(false);
                                            loadingBar.show();

                                            Calendar calendar = Calendar.getInstance();
                                            SimpleDateFormat time = new SimpleDateFormat("HH-MM-ss");//HOUR-MINUTE-SECOND
                                            SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");//DAY-MONTH-YEAR
                                            String currentTime = time.format(calendar.getTime());
                                            String currentDate = date.format(calendar.getTime());

                                            String visitID = currentTime + currentDate;

                                            HashMap interestMap = new HashMap();
                                            interestMap.put("PlaceName", name);
                                            interestMap.put("PlaceAbout", about);
                                            interestMap.put("PlaceAddress", address);
                                            interestMap.put("PlacePrice", price);
                                            interestMap.put("PlaceID", PlaceID);
                                            interestMap.put("UserID", currUserId);
                                            interestMap.put("PlaceImage", image);

                                            interestUserRef.child(visitID).updateChildren(interestMap);
                                            interestRef.child(visitID).updateChildren(interestMap).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    Toast.makeText(DetailsActivity.this, "Request initiated. You will get a call to validate your visit. Happy journey", Toast.LENGTH_LONG).show();
                                                    dialogInterface.dismiss();
                                                    loadingBar.dismiss();
                                                    finish();
                                                }
                                            });
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
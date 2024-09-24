package com.example.vacationtourapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddressActivity extends AppCompatActivity {

    EditText addressEditText, cityEditText, districtEditText, stateEditText, pincodeEditText;
    Button addAddressBtn;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String currUserId;
    DatabaseReference userRef;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        currUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currUserId);

        dialog = new ProgressDialog(this);

        addressEditText = findViewById(R.id.addressEditText);
        cityEditText = findViewById(R.id.cityEditText);
        districtEditText = findViewById(R.id.districtEditText);
        stateEditText = findViewById(R.id.stateEditText);
        pincodeEditText = findViewById(R.id.pincodeEditText);
        addAddressBtn = findViewById(R.id.addAddressBtn);
        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = addressEditText.getText().toString();
                String city = cityEditText.getText().toString();
                String district = districtEditText.getText().toString();
                String state = stateEditText.getText().toString();
                String pincode = pincodeEditText.getText().toString();

                if (address.isEmpty() && city.isEmpty() && district.isEmpty() && state.isEmpty() && pincode.isEmpty()){
                    Toast.makeText(AddressActivity.this, "Field is empty!", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.setMessage("please wait...");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    HashMap addressMap = new HashMap();
                    addressMap.put("Address", address);
                    addressMap.put("Pincode", pincode);
                    addressMap.put("City", city);
                    addressMap.put("District", district);
                    addressMap.put("State", state);
                    userRef.updateChildren(addressMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            dialog.dismiss();
                            Toast.makeText(AddressActivity.this, "Address updated", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }

            }
        });
    }
}
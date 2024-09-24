package com.example.vacationtourapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vacationtourapp.EditProfileActivity;
import com.example.vacationtourapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    ImageView profilePic, edit;
    TextView profilePhone, profileEmail, profileName, profileAddress,
            profilePincode, profileCity, profileDistrict, profileState;
    DatabaseReference userRef;
    FirebaseAuth mAuth;
    String currentUserId;
    ProgressDialog loadingBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        edit = view.findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        profilePic = view.findViewById(R.id.profilePic);
        profileName = view.findViewById(R.id.profileName);
        profilePhone = view.findViewById(R.id.profilePhone);
        profileEmail = view.findViewById(R.id.profileEmail);

        profileAddress = view.findViewById(R.id.profileAddress);
        profilePincode = view.findViewById(R.id.profilePincode);
        profileCity = view.findViewById(R.id.profileCity);
        profileDistrict = view.findViewById(R.id.profileDistrict);
        profileState = view.findViewById(R.id.profileState);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name = snapshot.child("Name").getValue().toString();
                    String email = snapshot.child("Email").getValue().toString();
                    String address = snapshot.child("Address").getValue().toString();
                    String pincode = snapshot.child("Pincode").getValue().toString();
                    String city = snapshot.child("City").getValue().toString();
                    String district = snapshot.child("District").getValue().toString();
                    String state = snapshot.child("State").getValue().toString();
                    String phone = snapshot.child("Phone").getValue().toString();
                    String image = snapshot.child("image").getValue().toString();

                    profileName.setText("Name: "+name);
                    profileEmail.setText("E-mail: "+email);
                    profilePhone.setText("Phone: "+phone);
                    profileAddress.setText("Address: "+address);
                    profilePincode.setText("Pincode: "+pincode);
                    profileCity.setText("City: "+city);
                    profileDistrict.setText("District: "+district);
                    profileState.setText("State: "+state);

                    if (!image.equals("default"))
                    {
                        Picasso.with(getActivity()).load(image).placeholder(R.drawable.profile).into( profilePic);
                        Picasso.with(getActivity()).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into( profilePic, new Callback() {
                            @Override
                            public void onSuccess()
                            { }

                            @Override
                            public void onError()
                            {
                                Picasso.with(getActivity()).load(image).placeholder(R.drawable.profile).into( profilePic);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}
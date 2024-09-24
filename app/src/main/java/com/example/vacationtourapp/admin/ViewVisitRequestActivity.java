package com.example.vacationtourapp.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vacationtourapp.R;
import com.example.vacationtourapp.model.Interested;
import com.example.vacationtourapp.model.Places;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ViewVisitRequestActivity extends AppCompatActivity {

    RecyclerView viewRequestRecyclerView;
    ProgressDialog dialog;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_visit_request);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        viewRequestRecyclerView = findViewById(R.id.viewRequestRecyclerView);

        LinearLayoutManager visitManager = new LinearLayoutManager(getApplicationContext());
        visitManager.setOrientation(RecyclerView.VERTICAL);
        viewRequestRecyclerView.setLayoutManager(visitManager);

        dialog = new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startListen();
    }

    private void startListen() {
        Query query = FirebaseDatabase.getInstance().getReference().child("Interested").limitToLast(50);
        FirebaseRecyclerOptions<Interested> options = new FirebaseRecyclerOptions.Builder<Interested>().setQuery(query, Interested.class).build();

        FirebaseRecyclerAdapter<Interested, ViewRequestViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Interested, ViewRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewRequestViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull Interested model) {
                holder.setPlaceName(model.getPlaceName());
                holder.setPlaceAddress(model.getPlaceAddress());
                holder.setPlacePrice(model.getPlacePrice());

                userRef.child(model.getUserID()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String name = snapshot.child("Name").getValue().toString();
                            String email = snapshot.child("Email").getValue().toString();
                            String phone = snapshot.child("Phone").getValue().toString();
                            holder.setUserName(name);
                            holder.setUserEmail(email);
                            holder.setUserPhone(phone);

                            holder.callUser.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                    String temp = "tel:" + phone;
                                    intent.setData(Uri.parse(temp));
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                dialog.dismiss();
            }

            @NonNull
            @Override
            public ViewRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.visit_request_layout, parent, false);
                return new ViewRequestViewHolder(view);
            }
        };
        viewRequestRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class ViewRequestViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView callUser;
        TextView userName, userPhone, userEmail, placeName, placeAddress, placePrice;

        public ViewRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            callUser = mView.findViewById(R.id.callUser);
            userName = mView.findViewById(R.id.userName);
            userPhone = mView.findViewById(R.id.userPhone);
            userEmail = mView.findViewById(R.id.userEmail);
            placeName = mView.findViewById(R.id.placeName);
            placeAddress = mView.findViewById(R.id.placeAddress);
            placePrice = mView.findViewById(R.id.placePrice);
        }

        public void setUserName(String name) {
            TextView userName = (TextView) mView.findViewById(R.id.userName);
            userName.setText(name);
        }

        public void setUserEmail(String email) {
            TextView userEmail = (TextView) mView.findViewById(R.id.userEmail);
            userEmail.setText(email);
        }

        public void setUserPhone(String phone) {
            TextView userPhone = (TextView) mView.findViewById(R.id.userPhone);
            userPhone.setText(phone);
        }

        public void setPlaceName(String pName) {
            TextView placeName = (TextView) mView.findViewById(R.id.placeName);
            placeName.setText(pName);
        }

        public void setPlaceAddress(String pAddress) {
            TextView placeAddress = (TextView) mView.findViewById(R.id.placeAddress);
            placeAddress.setText(pAddress);
        }

        public void setPlacePrice(String pPrice) {
            TextView placePrice = (TextView) mView.findViewById(R.id.placePrice);
            placePrice.setText(pPrice);
        }
    }
}
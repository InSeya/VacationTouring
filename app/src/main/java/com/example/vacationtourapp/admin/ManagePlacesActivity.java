package com.example.vacationtourapp.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacationtourapp.R;
import com.example.vacationtourapp.model.Places;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ManagePlacesActivity extends AppCompatActivity {

    RecyclerView managePlacesRecyclerView;
    ProgressDialog dialog;
    DatabaseReference placeRef, topPlaceRef;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_places);

        dialog = new ProgressDialog(this);
        builder = new AlertDialog.Builder(this);

        placeRef = FirebaseDatabase.getInstance().getReference().child("Places");
        topPlaceRef = FirebaseDatabase.getInstance().getReference().child("TopPlaces");

        managePlacesRecyclerView = findViewById(R.id.managePlacesRecyclerView);
        managePlacesRecyclerView.setHasFixedSize(true);
        managePlacesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    public void onStart() {
        super.onStart();
        dialog.setMessage("please wait");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        startListen();
    }

    private void startListen() {
        Query query = FirebaseDatabase.getInstance().getReference().child("Places").limitToLast(50);
        FirebaseRecyclerOptions<Places> options = new FirebaseRecyclerOptions.Builder<Places>().setQuery(query, Places.class).build();

        FirebaseRecyclerAdapter<Places, RecentPlaceViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Places, RecentPlaceViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RecentPlaceViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull Places model) {
                //final String PostKey = getRef(position).getKey();


                holder.setNamee(model.getPlaceName());
                holder.setAbout(model.getPlaceAbout());
                holder.setImagee(getApplicationContext(), model.getPlaceImage());
                holder.deletePlaces.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.setTitle("Delete " + model.getPlaceName())
                                .setMessage("Please confirm to delete " + model.getPlaceName())
                                .setCancelable(true)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (model.getTopPlace().equals("YES")){
                                            topPlaceRef.child(model.getPlaceID()).removeValue();
                                        }
                                        placeRef.child(model.getPlaceID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ManagePlacesActivity.this, model.getPlaceName() + " deleted", Toast.LENGTH_SHORT).show();
                                                }
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

                holder.editPlaces.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String placeID = model.getPlaceID();
                        Intent intent = new Intent(getApplicationContext(), EditPlaceActivity.class);
                        intent.putExtra("placeID", placeID);
                        startActivity(intent);
                    }
                });

                if (model.getTopPlace().equals("YES")) {
                    holder.topPlaceLabel.setVisibility(View.VISIBLE);
                } else if (model.getTopPlace().equals("NO")){
                    holder.topPlaceLabel.setVisibility(View.GONE);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (model.getTopPlace().equals("NO")) {
                            builder.setTitle("Alert!!")
                                    .setMessage("Do you want to add " + model.getPlaceName() + " in Top Places")
                                    .setCancelable(true)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            HashMap topPlaceMap = new HashMap();
                                            topPlaceMap.put("PlaceName", model.getPlaceName());
                                            topPlaceMap.put("PlaceAbout", model.getPlaceAbout());
                                            topPlaceMap.put("CurrentTime", model.getCurrentTime());
                                            topPlaceMap.put("CurrentDate", model.getCurrentDate());
                                            topPlaceMap.put("PlaceID", model.getPlaceID());
                                            topPlaceMap.put("PlaceImage", model.getPlaceImage());
                                            topPlaceRef.child(model.getPlaceID()).updateChildren(topPlaceMap).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    HashMap tPlaceMap = new HashMap();
                                                    tPlaceMap.put("TopPlace","YES");
                                                    placeRef.child(model.getPlaceID()).updateChildren(tPlaceMap);
                                                    Toast.makeText(ManagePlacesActivity.this, "Added to top places", Toast.LENGTH_SHORT).show();
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).show();
                        } else {
                            builder.setTitle("Alert!!")
                                    .setMessage("Do you want to remove " + model.getPlaceName() + " from Top Places")
                                    .setCancelable(true)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            /*HashMap topPlaceMap = new HashMap();
                                            topPlaceMap.put("PlaceName", model.getPlaceName());
                                            topPlaceMap.put("PlaceAbout", model.getPlaceAbout());
                                            topPlaceMap.put("CurrentTime", model.getCurrentTime());
                                            topPlaceMap.put("CurrentDate", model.getCurrentDate());
                                            topPlaceMap.put("PlaceID", model.getPlaceID());
                                            topPlaceMap.put("PlaceImage", "default");*/
                                            topPlaceRef.child(model.getPlaceID()).removeValue().addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    HashMap placeMap = new HashMap();
                                                    placeMap.put("TopPlace","NO");
                                                    placeRef.child(model.getPlaceID()).updateChildren(placeMap);
                                                    Toast.makeText(ManagePlacesActivity.this, "Removed from top places", Toast.LENGTH_SHORT).show();
                                                    dialogInterface.dismiss();
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
                    }
                });

                dialog.dismiss();
            }

            @NonNull
            @Override
            public RecentPlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_places_item, parent, false);
                return new RecentPlaceViewHolder(view);
            }
        };
        managePlacesRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class RecentPlaceViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView editPlaces, deletePlaces;
        CardView topPlaceLabel;

        public RecentPlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            editPlaces = mView.findViewById(R.id.editPlaces);
            deletePlaces = mView.findViewById(R.id.deletePlaces);
            topPlaceLabel = mView.findViewById(R.id.topPlaceLabel);
        }

        public void setNamee(String name) {
            TextView placeName = (TextView) mView.findViewById(R.id.place_name);
            placeName.setText(name);
        }

        public void setAbout(String name) {
            TextView placeAbout = (TextView) mView.findViewById(R.id.place_about);
            placeAbout.setText(name);
        }

        public void setImagee(Context ctx, String image) {
            ImageView placeimage = mView.findViewById(R.id.place_image);
            Picasso.with(ctx).load(image).into(placeimage);
        }
    }
}
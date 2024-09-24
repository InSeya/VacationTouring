package com.example.vacationtourapp.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacationtourapp.DetailsActivity;
import com.example.vacationtourapp.R;
import com.example.vacationtourapp.model.Places;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    RecyclerView recent_recycler, top_places_recycler;
    DatabaseReference placesRef, topPlacesRef;
    ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        LinearLayoutManager recentManager = new LinearLayoutManager(getContext());
        LinearLayoutManager topManager = new LinearLayoutManager(getContext());

        recent_recycler = view.findViewById(R.id.recent_recycler);
        top_places_recycler = view.findViewById(R.id.top_places_recycler);
        recentManager.setOrientation(RecyclerView.HORIZONTAL);
        topManager.setOrientation(RecyclerView.HORIZONTAL);
        recent_recycler.setLayoutManager(recentManager);
        top_places_recycler.setLayoutManager(topManager);

        placesRef = FirebaseDatabase.getInstance().getReference().child("Places");
        topPlacesRef = FirebaseDatabase.getInstance().getReference().child("TopPlaces");

        dialog = new ProgressDialog(getContext());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        dialog.setMessage("please wait");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        startListenForRecent();
        startListenForTop();
    }

    private void startListenForTop() {
        Query query = FirebaseDatabase.getInstance().getReference().child("TopPlaces").limitToLast(50);
        FirebaseRecyclerOptions<Places> options = new FirebaseRecyclerOptions.Builder<Places>().setQuery(query, Places.class).build();
        FirebaseRecyclerAdapter<Places, RecentPlaceViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Places, RecentPlaceViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RecentPlaceViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull Places model) {
                //final String PostKey = getRef(position).getKey();

                holder.setNamee(model.getPlaceName());
                holder.setImagee(getContext(), model.getPlaceImage());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*String visit_user_id = getRef(position).getKey();
                        Intent teacherProfileIntent = new Intent(PlacesActivity.this, PlacessProfile.class);
                        teacherProfileIntent.putExtra("visit_user_id", visit_user_id);
                        startActivity(teacherProfileIntent);*/
                        Intent intent = new Intent(getContext(), DetailsActivity.class);
                        intent.putExtra("PlaceID", model.getPlaceID());
                        startActivity(intent);
                    }
                });

                dialog.dismiss();
            }

            @NonNull
            @Override
            public RecentPlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recents_row_item, parent, false);
                return new RecentPlaceViewHolder(view);
            }
        };
        top_places_recycler.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private void startListenForRecent() {
        Query query = FirebaseDatabase.getInstance().getReference().child("Places").limitToLast(50);
        FirebaseRecyclerOptions<Places> options = new FirebaseRecyclerOptions.Builder<Places>().setQuery(query, Places.class).build();
        FirebaseRecyclerAdapter<Places, RecentPlaceViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Places, RecentPlaceViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RecentPlaceViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull Places model) {
                //final String PostKey = getRef(position).getKey();

                holder.setNamee(model.getPlaceName());
                holder.setImagee(getContext(), model.getPlaceImage());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*String visit_user_id = getRef(position).getKey();
                        Intent teacherProfileIntent = new Intent(PlacesActivity.this, PlacessProfile.class);
                        teacherProfileIntent.putExtra("visit_user_id", visit_user_id);
                        startActivity(teacherProfileIntent);*/
                        Intent intent = new Intent(getContext(), DetailsActivity.class);
                        intent.putExtra("PlaceID", model.getPlaceID());
                        startActivity(intent);
                    }
                });

                dialog.dismiss();
            }

            @NonNull
            @Override
            public RecentPlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recents_row_item, parent, false);
                return new RecentPlaceViewHolder(view);
            }
        };
        recent_recycler.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class RecentPlaceViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public RecentPlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setNamee(String name)
        {
            TextView username = (TextView) mView.findViewById(R.id.place_name);
            username.setText(name);
        }

        public void setImagee(Context ctx, String image)
        {
            ImageView donorimage = mView.findViewById(R.id.place_image);
            Picasso.with(ctx).load(image).into(donorimage);
        }
    }
}
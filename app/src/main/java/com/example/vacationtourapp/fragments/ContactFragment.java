package com.example.vacationtourapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vacationtourapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ContactFragment extends Fragment {

    EditText nameContact, emailContact, messageContact;
    Button submitFeedback;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String currUserId;
    DatabaseReference feedbackRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        currUserId = mAuth.getCurrentUser().getUid();

        feedbackRef = FirebaseDatabase.getInstance().getReference().child("Feedback");

        nameContact = view.findViewById(R.id.nameContact);
        emailContact = view.findViewById(R.id.emailContact);
        messageContact = view.findViewById(R.id.messageContact);
        submitFeedback = view.findViewById(R.id.submitFeedback);

        submitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameContact.getText().toString();
                String email = emailContact.getText().toString();
                String suggestion = messageContact.getText().toString();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat time = new SimpleDateFormat("HH-MM-ss");//HOUR-MINUTE-SECOND
                SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");//DAY-MONTH-YEAR
                String currentTime = time.format(calendar.getTime());
                String currentDate = date.format(calendar.getTime());

                String feedbackID = currentTime + currentDate;

                if (name.isEmpty() && email.isEmpty() && suggestion.isEmpty()){
                    Toast.makeText(getContext(), "Field is empty!", Toast.LENGTH_SHORT).show();
                } else {
                    HashMap feedback = new HashMap();
                    feedback.put("Name",name);
                    feedback.put("Email",email);
                    feedback.put("Message",suggestion);
                    feedback.put("CurrentTime",currentTime);
                    feedback.put("CurrentDate",currentDate);
                    feedback.put("FeedID",feedbackID);
                    feedback.put("UID",currUserId);
                    feedbackRef.child(feedbackID).updateChildren(feedback).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Feedback submitted!", Toast.LENGTH_SHORT).show();
                                nameContact.getText().clear();
                                emailContact.getText().clear();
                                messageContact.getText().clear();
                            } else {
                                String msg = task.getException().getMessage();
                                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        return view;
    }
}
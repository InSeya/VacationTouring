package com.example.vacationtourapp.admin;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.vacationtourapp.R;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class EditPlaceActivity extends AppCompatActivity {

    String placeID;
    TextView imageStatusText;
    EditText placeNameEditText, placeAboutEditText;
    ImageView editPlaceImageView;
    Button editPlaceButton;
    DatabaseReference placeRef;
    ProgressDialog loadingBar;

    Uri uri;
    String myUrl = "";
    StorageReference storagePicRef;
    StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_place);

        placeID = getIntent().getStringExtra("placeID");

        placeRef = FirebaseDatabase.getInstance().getReference().child("Places");
        storagePicRef = FirebaseStorage.getInstance().getReference().child("PlacePictures");
        loadingBar = new ProgressDialog(this);

        imageStatusText = findViewById(R.id.imageStatusText);
        placeNameEditText = findViewById(R.id.placeNameEditText);
        placeAboutEditText = findViewById(R.id.placeAboutEditText);
        editPlaceImageView = findViewById(R.id.editPlaceImageView);
        editPlaceButton = findViewById(R.id.editPlaceButton);

        placeRef.child(placeID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("PlaceName").getValue().toString();
                    String about = snapshot.child("PlaceAbout").getValue().toString();
                    String image = snapshot.child("PlaceImage").getValue().toString();

                    placeNameEditText.setText(name);
                    placeAboutEditText.setText(about);

                    if (!image.equals("default")) {
                        Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.profile).into(editPlaceImageView);
                        Picasso.with(getApplicationContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into(editPlaceImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.profile).into(editPlaceImageView);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
            if (result.getResultCode() == RESULT_OK) {
                uri = result.getData().getData();
                // Use the uri to load the image
                editPlaceImageView.setImageURI(uri);
                imageStatusText.setText("Image changed");
            } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                // Use ImagePicker.Companion.getError(result.getData()) to show an error
            }
        });

        imageStatusText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.Companion.with(EditPlaceActivity.this)
                            .crop()
                            .cropOval()
                            .maxResultSize(512, 512, true)
                            .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                            .createIntentFromDialog((Function1) (new Function1() {
                                public Object invoke(Object var1) {
                                    this.invoke((Intent) var1);
                                    return Unit.INSTANCE;
                                }

                                public final void invoke(@NotNull Intent it) {
                                    Intrinsics.checkNotNullParameter(it, "it");
                                    launcher.launch(it);
                                }
                            }));
                } else {
                    ActivityCompat.requestPermissions(EditPlaceActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }
            }
        });

        editPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = placeNameEditText.getText().toString();
                String about = placeAboutEditText.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(EditPlaceActivity.this, "Name empty.", Toast.LENGTH_SHORT).show();
                } else if (about.isEmpty()) {
                    Toast.makeText(EditPlaceActivity.this, "About empty.", Toast.LENGTH_SHORT).show();
                } else if (uri != null){
                    loadingBar.setMessage("please wait...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    HashMap<String, Object> productMap = new HashMap<String, Object>();
                    productMap.put("PlaceName", placeNameEditText.getText().toString());
                    productMap.put("PlaceAbout", placeAboutEditText.getText().toString());
                    placeRef.child(placeID).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Place Edited!", Toast.LENGTH_SHORT).show();
                            } else {
                                String msg = task.getException().getMessage();
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    final StorageReference fileref = storagePicRef.child(placeID + ".jpg");
                    uploadTask = fileref.putFile(uri);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return fileref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUrl = task.getResult();
                                myUrl = downloadUrl.toString();
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Places").child(placeID);
                                HashMap<String, Object> userMapImg = new HashMap<String, Object>();
                                userMapImg.put("PlaceImage", myUrl);
                                ref.updateChildren(userMapImg);
                                loadingBar.dismiss();
                                finish();
                            } else {
                                String msg = task.getException().getMessage();
                                Toast.makeText(EditPlaceActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class AddPlacesActivity extends AppCompatActivity {

    ImageView addPlaceImageView;
    EditText placeNameEditText, placeAboutEditText, placeAddressEditText, placePriceEditText;
    TextView imageStatusText;
    Button addPlaceButton;
    DatabaseReference placesRef;
    ProgressDialog loadingBar;

    Uri uri;
    String myUrl = "";
    StorageReference storagePicRef;
    StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_places);

        addPlaceImageView = findViewById(R.id.addPlaceImageView);
        imageStatusText = findViewById(R.id.imageStatusText);
        placeAddressEditText = findViewById(R.id.placeAddressEditText);
        placePriceEditText = findViewById(R.id.placePriceEditText);
        placeNameEditText = findViewById(R.id.placeNameEditText);
        placeAboutEditText = findViewById(R.id.placeAboutEditText);
        addPlaceButton = findViewById(R.id.addPlaceButton);

        placesRef = FirebaseDatabase.getInstance().getReference().child("Places");
        storagePicRef = FirebaseStorage.getInstance().getReference().child("PlacePictures");

        loadingBar = new ProgressDialog(this);

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
            if (result.getResultCode() == RESULT_OK) {
                uri = result.getData().getData();
                // Use the uri to load the image
                addPlaceImageView.setImageURI(uri);
                imageStatusText.setText("Image selected successfully");
            } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                // Use ImagePicker.Companion.getError(result.getData()) to show an error
            }
        });

        addPlaceImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.Companion.with(AddPlacesActivity.this)
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
                    ActivityCompat.requestPermissions(AddPlacesActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }
            }
        });

        addPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = placeNameEditText.getText().toString();
                String about = placeAboutEditText.getText().toString();
                String price = placePriceEditText.getText().toString();
                String address = placeAddressEditText.getText().toString();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat time = new SimpleDateFormat("HH-MM-ss");//HOUR-MINUTE-SECOND
                SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");//DAY-MONTH-YEAR
                String currentTime = time.format(calendar.getTime());
                String currentDate = date.format(calendar.getTime());

                String placeID = currentTime + currentDate;

                if (name.isEmpty()) {
                    Toast.makeText(AddPlacesActivity.this, "Name empty.", Toast.LENGTH_SHORT).show();
                } else if (about.isEmpty()) {
                    Toast.makeText(AddPlacesActivity.this, "About empty.", Toast.LENGTH_SHORT).show();
                } else if (uri != null){
                    loadingBar.setMessage("please wait...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    HashMap<String, Object> productMap = new HashMap<String, Object>();
                    productMap.put("PlaceName", placeNameEditText.getText().toString());
                    productMap.put("PlaceAbout", placeAboutEditText.getText().toString());
                    productMap.put("PlaceAddress", placeAddressEditText.getText().toString());
                    productMap.put("PlacePrice", placePriceEditText.getText().toString());
                    productMap.put("CurrentTime", currentTime);
                    productMap.put("CurrentDate", currentDate);
                    productMap.put("PlaceID", placeID);
                    productMap.put("PlaceImage", "default");
                    productMap.put("TopPlace", "NO");
                    placesRef.child(placeID).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Place Added!", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(AddPlacesActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
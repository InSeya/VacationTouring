package com.example.vacationtourapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vacationtourapp.admin.EditPlaceActivity;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class EditProfileActivity extends AppCompatActivity {

    RoundedImageView editProfileImage;
    TextView imageStatusText;
    EditText editProfileName, editProfilePhone;
    Button updateBtn;

    DatabaseReference usersRef;
    ProgressDialog loadingBar;
    Uri uri;
    String myUrl = "", currentUserID;
    FirebaseAuth mAuth;
    StorageReference storagePicRef;
    StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        storagePicRef = FirebaseStorage.getInstance().getReference().child("ProfilePictures");

        currentUserID = mAuth.getCurrentUser().getUid();

        editProfileImage = findViewById(R.id.editProfileImage);
        imageStatusText = findViewById(R.id.imageStatusText);
        editProfileName = findViewById(R.id.editProfileName);
        editProfilePhone = findViewById(R.id.editProfilePhone);
        updateBtn = findViewById(R.id.updateBtn);

        usersRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("Name").getValue().toString();
                    String phone = snapshot.child("Phone").getValue().toString();
                    String image = snapshot.child("image").getValue().toString();

                    editProfileName.setText(name);
                    editProfilePhone.setText(phone);

                    if (!image.equals("default")) {
                        Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.profile).into(editProfileImage);
                        Picasso.with(getApplicationContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into(editProfileImage, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.profile).into(editProfileImage);
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
                editProfileImage.setImageURI(uri);
                imageStatusText.setText("Image changed");
            } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                // Use ImagePicker.Companion.getError(result.getData()) to show an error
            }
        });

        imageStatusText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.Companion.with(EditProfileActivity.this)
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
                    ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editProfileName.getText().toString();
                String phone = editProfilePhone.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Name empty.", Toast.LENGTH_SHORT).show();
                } else if (phone.isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Phone empty.", Toast.LENGTH_SHORT).show();
                } else if (uri != null){
                    loadingBar.setMessage("please wait...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    HashMap<String, Object> productMap = new HashMap<String, Object>();
                    productMap.put("Name", name);
                    productMap.put("Phone", phone);
                    usersRef.child(currentUserID).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                            } else {
                                String msg = task.getException().getMessage();
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    final StorageReference fileref = storagePicRef.child(currentUserID + ".jpg");
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
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
                                HashMap<String, Object> userMapImg = new HashMap<String, Object>();
                                userMapImg.put("image", myUrl);
                                ref.updateChildren(userMapImg);
                                loadingBar.dismiss();
                                finish();
                            } else {
                                String msg = task.getException().getMessage();
                                Toast.makeText(EditProfileActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void goBack(View view) {
        finish();
    }
}
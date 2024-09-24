package com.example.vacationtourapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.vacationtourapp.auth.LoginActivity;
import com.example.vacationtourapp.fragments.AboutFragment;
import com.example.vacationtourapp.fragments.ContactFragment;
import com.example.vacationtourapp.fragments.HomeFragment;
import com.example.vacationtourapp.fragments.ProfileFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ImageView logout;

    View content;

    DrawerLayout drawerLayout;
    NavigationView navView;
    Toolbar toolbar;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String currentUserID = mAuth.getCurrentUser().getUid();
    DatabaseReference usersRef;
    ImageView header_profile_image;
    TextView navHeaderName, navHeaderEmail;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        content = findViewById(R.id.container);

        // Now here we will add some dummy data in our model class

        /*List<RecentsData> recentsDataList = new ArrayList<>();
        recentsDataList.add(new RecentsData("AM Lake","India","From $200",R.drawable.recentimage1));
        recentsDataList.add(new RecentsData("Nilgiri Hills","India","From $300",R.drawable.recentimage2));
        recentsDataList.add(new RecentsData("AM Lake","India","From $200",R.drawable.recentimage1));
        recentsDataList.add(new RecentsData("Nilgiri Hills","India","From $300",R.drawable.recentimage2));
        recentsDataList.add(new RecentsData("AM Lake","India","From $200",R.drawable.recentimage1));
        recentsDataList.add(new RecentsData("Nilgiri Hills","India","From $300",R.drawable.recentimage2));

        setRecentRecycler(recentsDataList);

        List<TopPlacesData> topPlacesDataList = new ArrayList<>();
        topPlacesDataList.add(new TopPlacesData("Kasimir Hill","India","$200 - $500",R.drawable.topplaces));
        topPlacesDataList.add(new TopPlacesData("Kasimir Hill","India","$200 - $500",R.drawable.topplaces));
        topPlacesDataList.add(new TopPlacesData("Kasimir Hill","India","$200 - $500",R.drawable.topplaces));
        topPlacesDataList.add(new TopPlacesData("Kasimir Hill","India","$200 - $500",R.drawable.topplaces));
        topPlacesDataList.add(new TopPlacesData("Kasimir Hill","India","$200 - $500",R.drawable.topplaces));

        setTopPlacesRecycler(topPlacesDataList);*/

        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);

        dialog = new ProgressDialog(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        //NAVIGATION VIEW CODE --> for implementation of the navigation drawer header where we have (Image, Name and E-mail)
        navView = findViewById(R.id.navView);
        View headerView = navView.inflateHeaderView(R.layout.nav_header);
        header_profile_image = headerView.findViewById(R.id.header_profile_image);
        navHeaderName = headerView.findViewById(R.id.navHeaderName);
        navHeaderEmail = headerView.findViewById(R.id.navHeaderEmail);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("Name").getValue().toString();
                    String email = dataSnapshot.child("Email").getValue().toString();
                    navHeaderName.setText(name);
                    navHeaderEmail.setText(email);

                    final String image = dataSnapshot.child("image").getValue().toString();
                    if (!image.equals("default")) {
                        Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.profile).into(header_profile_image);
                        Picasso.with(getApplicationContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into(header_profile_image, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.profile).into(header_profile_image);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        navView.bringToFront();

        //CODE FOR THE DRAWER TOGGLING
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);

        navView.setCheckedItem(R.id.navHome);
        //setFactsMainFragment();
        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .addToBackStack("fragmentA")
                    .replace(R.id.container, new HomeFragment(), "fragmentA")
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String address = snapshot.child("Address").getValue().toString();

                    if (address.equals("NA")) {
                        Snackbar.make(content, "Address not updated", Snackbar.LENGTH_SHORT).setAction("Do it", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), AddressActivity.class);
                                startActivity(intent);
                                //Toast.makeText(MainActivity.this, "update address", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navHome:
                setHomeFragment();
                break;
            case R.id.navProfile:
                setProfileFragment();
                break;
            case R.id.navAboutUs:
                setAboutFragment();
                break;
            case R.id.navContactUs:
                setContactFragment();
                break;
            case R.id.navLogout:
                mAuth.signOut();
                sendUserToLogin();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setHomeFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
        getSupportActionBar().setTitle("Home");
        setAnimation();
    }

    private void setProfileFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
        getSupportActionBar().setTitle("Profile");
        setAnimation();
    }

    private void setAboutFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new AboutFragment()).commit();
        getSupportActionBar().setTitle("About Us");
        setAnimation();
    }

    private void setContactFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new ContactFragment()).commit();
        getSupportActionBar().setTitle("Contact Us");
        setAnimation();
    }

    private void sendUserToLogin() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout !")
                .setMessage("Are you sure you want to logout?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(loginIntent);
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private void setAnimation() {
        YoYo.with(Techniques.FadeInLeft)
                .duration(700)
                .repeat(0)
                .playOn(findViewById(R.id.container));

    }

    public void replaceFragment(Fragment fragment, String tag) {
        //Get current fragment placed in container
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        //Prevent adding same fragment on top
        if (currentFragment.getClass() == fragment.getClass()) {
            return;
        }
        //If fragment is already on stack, we can pop back stack to prevent stack infinite growth
        if (getSupportFragmentManager().findFragmentByTag(tag) != null) {
            getSupportFragmentManager().popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        //Otherwise, just replace fragment
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(tag)
                .replace(R.id.container, fragment, tag)
                .commit();
    }

    @Override
    public void onBackPressed() {
        int fragmentsInStack = getSupportFragmentManager().getBackStackEntryCount();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (fragmentsInStack > 1) {
            // If we have more than one fragment, pop back stack
            getSupportFragmentManager().popBackStack();
            getSupportActionBar().show();
            setDrawer_UnLocked();
        } else if (fragmentsInStack == 1) {
            // Finish activity, if only one fragment left, to prevent leaving empty screen
            finish();
        } else {
            super.onBackPressed();
        }
    }

    public void setDrawer_Locked() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void setDrawer_UnLocked() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }


}

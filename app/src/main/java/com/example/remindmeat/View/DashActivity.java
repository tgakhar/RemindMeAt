package com.example.remindmeat.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remindmeat.Location.LocationService;
import com.example.remindmeat.R;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author Patel Dhruv
 * @author Gakhar Tanvi
 * @author Kaur Sarbjit
 * @author Kaur Kamaljit
 * @author Varma Akshay
 * @author Dhankara Chintan
 * @author Karthik Modubowna
 * Java class for Dash activity {@link DashActivity}
 */

public class DashActivity extends AppCompatActivity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener {
    /**
     * Variables for buttons
     */
    Button btn_mapView,btn_listView;
    /**
     * Variable for floating action button
     */
    FloatingActionButton btn_addReminder;
    /**
     * Variable for drawer layout
     */
    DrawerLayout drawerLayout;
    /**
     * Variable for Navigation View
     */
    NavigationView navigationView;
    /**
     * Variable for Material toolbar
     */
    MaterialToolbar materialToolbar;
    /**
     * Object of FirebaseAuth
     */
    FirebaseAuth auth;
    /**
     * object of FirebaseUser
     */
    FirebaseUser curUser;
    /**
     * Variable of CircleImageView
     */
    CircleImageView imageView;
    /**
     * Variables of TextViews
     */
    TextView useremail, name;
    /**
     * Object of Firestore
     */
    FirebaseFirestore db;
    /**
     * onCreate Method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.navigationView);
        materialToolbar=findViewById(R.id.topAppBar);
        auth=FirebaseAuth.getInstance();
        setSupportActionBar(materialToolbar);
        navigationView.bringToFront();
        View header= navigationView.getHeaderView(0);
        imageView = header.findViewById(R.id.profile_image);
        useremail= header.findViewById(R.id.profile_email);
        name= header.findViewById(R.id.profile_name);
        /**
         * Image View OnClickListener to start profile activity
         */
        imageView.setOnClickListener(openProfile);
        /**
         * TextView OnClickListeners to start profile activity
         */
        useremail.setOnClickListener(openProfile);
        name.setOnClickListener(openProfile);
        /**
         * Toggle for Action Bar Drawer
         */
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,materialToolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        /**
         * navigation Item click Listener
         */
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);


        btn_listView=findViewById(R.id.btn_dashList);
        btn_mapView=findViewById(R.id.btn_dashMap);
        btn_addReminder=findViewById(R.id.dash_addReminderButton);
        btn_addReminder.setOnClickListener(addReminder);
        btn_listView.setOnClickListener(this);
        btn_mapView.setOnClickListener(this);
        startService();
        loadData();
    }

    /**
     * Button OnClickListener to start add reminder activity
     */
    View.OnClickListener addReminder=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(DashActivity.this,AddreminderActivity.class);
            startActivity(intent);
        }
    };
    /**
     * OnClickListener to start profile activity
     */
    View.OnClickListener openProfile  = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(DashActivity.this, ProfileActivity.class);
            startActivity(i);

        }
    };

    /**
     * Method to start Foreground Service
     */
    public void startService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //Intent intent=new Intent(this,LocationService.class);

            startForegroundService(new Intent(this, LocationService.class));
        }
        else{
            // Intent intent=new Intent(this,LocationService.class);
            startService(new Intent(this, LocationService.class));
        }

    }

    /**
     * onClick method
     * @param view
     */
    @Override
    public void onClick(View view) {
        NavController navController= Navigation.findNavController(DashActivity.this,R.id.nav_host_dash);
        switch (view.getId()){
            case R.id.btn_dashMap:
                btn_mapView.setTextColor(getResources().getColor( R.color.main));
                btn_listView.setTextColor(getResources().getColor( R.color.DarkGray));
                navController.navigate(R.id.mapFragment);

                break;
            case R.id.btn_dashList:
                btn_listView.setTextColor(getResources().getColor( R.color.main));
                btn_mapView.setTextColor(getResources().getColor( R.color.DarkGray));
                navController.navigate(R.id.listFragment);
                break;


        }
    }

    /**
     * onNavigationItemSelected method
     * @param item
     * @return
     */

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.nav_historyReminder:
                intent=new Intent(getApplicationContext(), ReminderhistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_profile:
                intent=new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_feedBack:
                intent=new Intent(getApplicationContext(), FeedbackActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                logOut();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOut() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(DashActivity.this);
        builder.setMessage("Do you want to Sign out?")
                .setCancelable(false)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        curUser=auth.getCurrentUser();
                        auth.signOut();
                        stopService(new Intent(getApplicationContext(), LocationService.class));
                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Sign Out!!!");
        alert.show();
    }

    /**
     * loadData method to load user data from the FireStore database in the profile
     */
    private void loadData() {
        curUser=auth.getCurrentUser();
        if(curUser!=null){
            if(curUser.getPhotoUrl()!= null){
                Picasso.get().load(curUser.getPhotoUrl()).into(imageView);
            }
        }
        curUser=auth.getCurrentUser();
        db.collection("Users").document(curUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                name.setText(documentSnapshot.getString("Name"));
                useremail.setText(documentSnapshot.getString("Email"));
            }
        });


    }
}
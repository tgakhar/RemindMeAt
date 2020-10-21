package com.example.remindmeat.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.remindmeat.Location.LocationService;
import com.example.remindmeat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashActivity extends AppCompatActivity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener {

    Button btn_mapView,btn_listView;
    FloatingActionButton btn_addReminder;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar materialToolbar;
    FirebaseAuth auth;
    FirebaseUser curUser;
    CircleImageView imageView;
    TextView useremail, name;
    FirebaseFirestore db;
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


        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,materialToolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        btn_listView=findViewById(R.id.btn_dashList);
        btn_mapView=findViewById(R.id.btn_dashMap);
        btn_addReminder=findViewById(R.id.dash_addReminderButton);
        btn_addReminder.setOnClickListener(addReminder);
        btn_listView.setOnClickListener(this);
        btn_mapView.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(new Intent(this, LocationService.class));
        else
            startService(new Intent(this, LocationService.class));

        loadData();
    }
    View.OnClickListener addReminder=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(DashActivity.this,AddreminderActivity.class);
            startActivity(intent);
        }
    };

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.nav_profile:
                intent=new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_feedBack:
                intent=new Intent(getApplicationContext(), FeedbackActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                auth.signOut();
                intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

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
package com.example.remindmeat.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.remindmeat.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class DashActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_mapView,btn_listView;
    FloatingActionButton btn_addReminder;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar materialToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.navigationView);
        materialToolbar=findViewById(R.id.topAppBar);

        setSupportActionBar(materialToolbar);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,materialToolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        btn_listView=findViewById(R.id.btn_dashList);
        btn_mapView=findViewById(R.id.btn_dashMap);
        btn_addReminder=findViewById(R.id.dash_addReminderButton);
        btn_addReminder.setOnClickListener(addReminder);
        btn_listView.setOnClickListener(this);
        btn_mapView.setOnClickListener(this);
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
}
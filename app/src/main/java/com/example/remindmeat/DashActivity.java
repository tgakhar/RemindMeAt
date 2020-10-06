package com.example.remindmeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_mapView,btn_listView;
    FloatingActionButton btn_addReminder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);

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
package com.example.remindmeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DashActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_mapView,btn_listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);

        btn_listView=findViewById(R.id.btn_dashList);
        btn_mapView=findViewById(R.id.btn_dashMap);

        btn_listView.setOnClickListener(this);
        btn_mapView.setOnClickListener(this);
    }

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
package com.example.remindmeat.View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.remindmeat.R;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        auth=FirebaseAuth.getInstance();

    }

}
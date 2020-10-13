package com.example.remindmeat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.remindmeat.Model.Reminder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ReminderdetailsActivity extends AppCompatActivity {

    FloatingActionButton navigate;
    Reminder reminder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminderdetails);

        reminder=getIntent().getExtras().getParcelable("Reminder");
        navigate=findViewById(R.id.details_navigate);
        navigate.setOnClickListener(Navigate);

    }


    View.OnClickListener Navigate=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr="+reminder.getReminderLat()+","+reminder.getReminderLong()));
            startActivity(intent);

     /*       Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("geo:0,0?q=37.423156,-122.084917 (" + name + ")"));
            startActivity(intent);*/
        }
    };
}
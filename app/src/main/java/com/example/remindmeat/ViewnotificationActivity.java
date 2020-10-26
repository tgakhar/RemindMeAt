package com.example.remindmeat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remindmeat.Model.Reminder;

public class ViewnotificationActivity extends AppCompatActivity {

    Reminder reminder;
    TextView txt_title,txt_location,txt_description,txt_range;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        reminder = getIntent().getExtras().getParcelable("Reminder");

        setContentView(R.layout.activity_viewnotification);

        showDetails();
    }

    private void showDetails() {
        Toast.makeText(this, "Title="+reminder.getReminderTitle(), Toast.LENGTH_SHORT).show();
        txt_title=findViewById(R.id.txt_notificationTitle);
        txt_description=findViewById(R.id.txt_notificationDescription);
        txt_range=findViewById(R.id.txt_notificationRange);
        txt_location=findViewById(R.id.txt_notificationAddress);
        txt_title.setText("This is to remind you about "+reminder.getReminderTitle()+".");
        txt_description.setText(reminder.getReminderDescription());
        txt_range.setText("You are within "+reminder.getReminderRange()+" m of");
        txt_location.setText(reminder.getReminderLocation());
    }
}
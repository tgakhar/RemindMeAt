package com.example.remindmeat.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remindmeat.Location.LocationService;
import com.example.remindmeat.Model.Reminder;
import com.example.remindmeat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.shadowfax.proswipebutton.ProSwipeButton;

public class ViewnotificationActivity extends AppCompatActivity {

    ProSwipeButton proSwipeBtn;
    Reminder reminder;
    TextView txt_title,txt_location,txt_description,txt_range;
    FirebaseAuth auth;
    FirebaseFirestore db;
    Button btn_cancel;
    FirebaseUser curUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        reminder = getIntent().getExtras().getParcelable("Reminder");

        setContentView(R.layout.activity_viewnotification);
        NotificationManagerCompat.from(getApplicationContext()).cancel(reminder.getUid());
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
        proSwipeBtn=(ProSwipeButton) findViewById(R.id.btn_complete);
        btn_cancel=findViewById(R.id.btn_cancel);
        proSwipeBtn.setOnSwipeListener(completeReminder);
        btn_cancel.setOnClickListener(cancel);
    }


    View.OnClickListener cancel=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(ViewnotificationActivity.this, DashActivity.class);
            startActivity(intent);
        }
    };

    ProSwipeButton.OnSwipeListener completeReminder=new ProSwipeButton.OnSwipeListener() {
        @Override
        public void onSwipeConfirm() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    proSwipeBtn.showResultIcon(true, false);
                }
            }, 2000);

            curUser=auth.getCurrentUser();

            db.collection("Users").document(curUser.getUid()).collection("Reminder").document(reminder.getReminderId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        //Intent intent=new Intent(this,LocationService.class);
                        stopService(new Intent(ViewnotificationActivity.this, LocationService.class));
                        startForegroundService(new Intent(ViewnotificationActivity.this, LocationService.class));
                    }
                    else{
                        // Intent intent=new Intent(this,LocationService.class);
                        stopService(new Intent(ViewnotificationActivity.this, LocationService.class));
                        startService(new Intent(ViewnotificationActivity.this, LocationService.class));
                    }
                        saveToHistory();
                }
            });
        }
    };

    private void saveToHistory() {
        Date date = new Date();
        Map<String, Object> reminderMap = new HashMap<>();

        reminderMap.put("Title",reminder.getReminderTitle());
        reminderMap.put("Description",reminder.getReminderDescription());
        reminderMap.put("Address",reminder.getReminderLocation());
        reminderMap.put("Repeat",reminder.getReminderRepeat());
        reminderMap.put("Date",reminder.getReminderDate());
        reminderMap.put("Range",reminder.getReminderRange());
        reminderMap.put("Latitude",reminder.getReminderLat());
        reminderMap.put("Longitude",reminder.getReminderLong());
        reminderMap.put("Status",reminder.getReminderStatus());
        reminderMap.put("UniqueId",reminder.getUid());

        db.collection("Users").document(curUser.getUid()).collection("Reminder History").document(String.valueOf(date)).set(reminderMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent=new Intent(ViewnotificationActivity.this, DashActivity.class);
                startActivity(intent);
            }
        });


    }
}
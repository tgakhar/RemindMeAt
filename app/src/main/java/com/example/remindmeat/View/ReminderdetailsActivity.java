package com.example.remindmeat.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remindmeat.Model.Reminder;
import com.example.remindmeat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReminderdetailsActivity extends AppCompatActivity {

    FloatingActionButton navigate;
    Reminder reminder;
    MaterialToolbar toolbar;
    TextView txt_title,txt_address,txt_description,txt_range,txt_repeat,txt_status,txt_date;
    FirebaseUser curUser;
    FirebaseFirestore db;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminderdetails);
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        reminder=getIntent().getExtras().getParcelable("Reminder");

        //
        navigate=findViewById(R.id.details_navigate);
        toolbar=findViewById(R.id.topbar_reminderDetails);
        txt_title=findViewById(R.id.txt_detailTitle);
        txt_address=findViewById(R.id.txt_detailsAddress);
        txt_description=findViewById(R.id.txt_detailsDescription);
        txt_range=findViewById(R.id.txt_detailsRange);
        txt_repeat=findViewById(R.id.txt_detailsRepeat);
        txt_status=findViewById(R.id.txt_detailsStatus);
        txt_date=findViewById(R.id.txt_detailsDate);


        toolbar.setNavigationOnClickListener(toolNav);
       toolbar.setOnMenuItemClickListener(menuClick);
        navigate.setOnClickListener(Navigate);
        setTextData();

    }

    Toolbar.OnMenuItemClickListener menuClick=new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.top_delete:
                        deleteReminder();
                    break;
                case R.id.top_edit:
                    Intent intent=new Intent(ReminderdetailsActivity.this, EditreminderActivity.class);
                    intent.putExtra("Reminder",reminder);
                    startActivity(intent);
                    break;
            }

            return true;
        }
    };

    private void deleteReminder() {
        curUser=auth.getCurrentUser();
        DocumentReference docRef=db.collection("Users").document(curUser.getUid()).collection("Reminder").document(reminder.getReminderId());

        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(ReminderdetailsActivity.this,DashActivity.class);
                startActivity(intent);
            }
        });

    }


    private void setTextData() {
        txt_title.setText(reminder.getReminderTitle());
        txt_range.setText("Within "+reminder.getReminderRange()+" m");
        txt_address.setText(reminder.getReminderLocation());
        txt_description.setText(reminder.getReminderDescription());

        if (reminder.getReminderRepeat()==0){
            txt_repeat.setText("Does not repeat");
            if (reminder.getReminderDate().length()==0||reminder.getReminderDate()==null){
                txt_date.setText("No specific date");
            }else{
                txt_date.setText("On "+reminder.getReminderDate());
            }
        }else{
            txt_repeat.setText("Repeating reminder");
        }

        if (reminder.getReminderStatus()==0){
            txt_status.setText("Inactive reminder");
        }else{
            txt_status.setText("Active reminder");
        }

    }

    View.OnClickListener toolNav=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };


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
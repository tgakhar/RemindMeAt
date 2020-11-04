package com.example.remindmeat.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

/**
 * @author Patel Dhruv
 * @author Gakhar Tanvi
 * @author Kaur Sarbjit
 * @author Kaur Kamaljit
 * @author Varma Akshay
 * @author Dankhara Chintan
 * @author Karthik Modubowna
 * ReminderdetailsActivity java class used for showing reminder details
 */
public class ReminderdetailsActivity extends AppCompatActivity {

    /**
     * variable of FloatingActionButton
     */
    FloatingActionButton navigate;

    /**
     * variable of Reminder
     */
    Reminder reminder;

    /**
     * variable of MaterialToolbar
     */
    MaterialToolbar toolbar;

    /**
     * variables of TextView
     */
    TextView txt_title,txt_address,txt_description,txt_range,txt_repeat,txt_status,txt_date;

    /**
     * variable of FirebaseUser
     */
    FirebaseUser curUser;

    /**
     * variable of FirebaseFirestore
     */
    FirebaseFirestore db;

    /**
     * variable of FirebaseAuth
     */
    FirebaseAuth auth;

    /**
     * onCreate method
     * @param savedInstanceState
     */
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

    /**
     * OnMenuItemClickListener method for checking the item clicked
     */
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

    /**
     * Alert diaolg box for deleting the reminder from the list
     */
    private void deleteReminder() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(ReminderdetailsActivity.this);
        builder.setMessage("Do you want to delete the reminder?")
                .setCancelable(false)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(),"Reminder is still available!!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Delete!!!");
        alert.show();


    }

    /**
     *Method to set the text to be displayed in the TextView
     */
    private void setTextData() {
        txt_title.setText(reminder.getReminderTitle());
        txt_range.setText("Within "+reminder.getReminderRange()+" m");
        txt_address.setText(reminder.getReminderLocation());
        txt_description.setText(reminder.getReminderDescription());

        if (reminder.getReminderRepeat()==0){
            txt_repeat.setText("Does not repeat");
            if (reminder.getReminderDate().equals("0")){
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

    /**
     * OnClickListener method
     */
    View.OnClickListener toolNav=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    /**
     * OnClickListener method
     */
    View.OnClickListener Navigate=new View.OnClickListener() {

        /**
         * OnClick event handler method for google maps
         */
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
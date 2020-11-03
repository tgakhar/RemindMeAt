package com.example.remindmeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.remindmeat.Adapter.ReminderAdapter;
import com.example.remindmeat.Adapter.ReminderHistoryAdapter;
import com.example.remindmeat.Location.LocationService;
import com.example.remindmeat.Model.Admin;
import com.example.remindmeat.Model.Reminder;
import com.example.remindmeat.View.AddreminderActivity;
import com.example.remindmeat.View.DashActivity;
import com.example.remindmeat.View.EditreminderActivity;
import com.example.remindmeat.View.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Patel Dhruv
 * @author Gakhar Tanvi
 * @author Kaur Sarbjit
 * @author Kaur Kamaljit
 * @author Varma Akshay
 * @author Dankhara Chintan
 * @author Karthik Modubowna
 *  This activity is for showing the history of all the completed Reminders {@link ReminderhistoryActivity}
 */
public class ReminderhistoryActivity extends AppCompatActivity {

    /**
     * variable for FirebaseAuth
     */
    FirebaseAuth auth;
    /**
     * Variable for FirebaseFirestore
     */
    FirebaseFirestore db;
    /**
     * array list of Reminder type for showing the list of Reminders
     */
    List<Reminder>reminderList=new ArrayList<>();
    /**
     * Variable for Firebase User
     */
    FirebaseUser curUser;
    /**
     * variable for Recycler View
     */
    RecyclerView recyclerView;
    /**
     *Variable for Reminder History Adapter
     */
    ReminderHistoryAdapter reminderHistoryAdapter;
    /**
     * variable for Material Toolbar
     */
    MaterialToolbar toolbar;

    /**
     * Object of {@link EditText for search bar}
     */
    EditText searchReminder;

    /**
     * onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminderhistory);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        recyclerView=findViewById(R.id.recycler_listReminderHistory);
        toolbar=findViewById(R.id.topbar_reminderHistory);
        searchReminder=findViewById(R.id.edt_searchList_history);
        searchReminder.addTextChangedListener(searchAdapter);

        searchReminder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (searchReminder.getRight() - searchReminder.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        searchReminder.getText().clear();
                        return true;
                    }
                }
                return false;
            }
        });


        searchReminderList();

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ReminderhistoryActivity.this, DashActivity.class);
                startActivity(intent);
            }
        });
        loadData();
    }


    /**
     * {@link TextWatcher} method for getting text value on change
     */
    TextWatcher searchAdapter=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            reminderHistoryAdapter.getFilter().filter(s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    /**
     * This method set design of search edit text
     */
    private void searchReminderList() {
        searchReminder.setBackgroundResource(R.drawable.search_input_style);
    }

    /**
     * loadData for loading the Data from Cloud FireStore
     */
    private void loadData() {
        reminderList.clear();
        curUser = auth.getCurrentUser();
        CollectionReference collectionReference = db.collection("Users").document(curUser.getUid()).collection("Reminder History");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String reminderId = (String) document.getId();
                        String reminderTitle = (String) document.getData().get("Title");
                        String reminderLocation = (String) document.getData().get("Address");
                        String reminderDescription = (String) document.getData().get("Description");
                        String reminderDate = (String) document.getData().get("Date");
                        Integer reminderRepeat = ((Long) document.getData().get("Repeat")).intValue();
                        Integer reminderRange = ((Long) document.getData().get("Range")).intValue();
                        Integer reminderStatus = ((Long) document.getData().get("Status")).intValue();
                        Double reminderLat = (Double) document.getData().get("Latitude");
                        Double reminderLong = (Double) document.getData().get("Longitude");
                        Integer reminderUid= ((Long) document.getData().get("UniqueId")).intValue();
                        addToList(reminderId, reminderTitle, reminderLocation, reminderDescription, reminderDate, reminderRepeat, reminderRange, reminderStatus, reminderLat, reminderLong,reminderUid);

                    }

                    setReminderRecycler(reminderList);
                }

            }
        });


    }

    /**
     * for adding the reminder to list
     * @param reminderId
     * @param reminderTitle
     * @param reminderLocation
     * @param reminderDescription
     * @param reminderDate
     * @param reminderRepeat
     * @param reminderRange
     * @param reminderStatus
     * @param reminderLat
     * @param reminderLong
     * @param reminderUid
     */
    private void addToList(String reminderId, String reminderTitle, String reminderLocation, String reminderDescription, String reminderDate, Integer reminderRepeat, Integer reminderRange, Integer reminderStatus, Double reminderLat, Double reminderLong, Integer reminderUid) {

        reminderList.add(new Reminder(reminderId, reminderTitle, reminderLocation, reminderDescription, reminderDate, reminderRepeat, reminderRange, reminderStatus, reminderLat, reminderLong,reminderUid));

        setReminderRecycler(reminderList);
    }

    /**
     * Setter for ReminderList
     * @param reminderList
     */
    private void setReminderRecycler(final List<Reminder> reminderList) {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext()
                , RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        reminderHistoryAdapter = new ReminderHistoryAdapter(reminderList, getApplicationContext());
        recyclerView.setAdapter(reminderHistoryAdapter);
        reminderHistoryAdapter.setOnClickListner(adapterClick);
    }

    /**
     * onClickListenr for delete Reminder and Add reminder
     */
    View.OnClickListener adapterClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.img_historyDelete:
                    deleteReminder(v);
                    break;
                case R.id.img_addReminder:
                    addReminder(v);
                    break;
            }
        }
    };

    /**
     * for reAdding the reminder to the Active Reminder list
     * @param v
     */
    private void addReminder(View v) {
        RecyclerView.ViewHolder viewHolder=(RecyclerView.ViewHolder) v.getTag();
        final int position = viewHolder.getAdapterPosition();
        final Reminder reminder=reminderHistoryAdapter.getItem(position);

        Intent intent=new Intent(getApplicationContext(), AddreminderActivity.class);
        intent.putExtra("Reminder",reminder);
        startActivity(intent);

    }

    /**
     * for deleting the reminder from history
     * @param v
     */
    private void deleteReminder(View v) {
        RecyclerView.ViewHolder viewHolder=(RecyclerView.ViewHolder) v.getTag();
        final int position = viewHolder.getAdapterPosition();
        final Reminder r=reminderHistoryAdapter.getItem(position);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(ReminderhistoryActivity.this);
        builder.setMessage("Do you want to delete the reminder?")
                .setCancelable(false)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        curUser=auth.getCurrentUser();
                        DocumentReference docRef=db.collection("Users").document(curUser.getUid()).collection("Reminder History").document(r.getReminderId());

                        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"Reminder deleted successfully!!!",
                                        Toast.LENGTH_SHORT).show();
                                loadData();
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
}
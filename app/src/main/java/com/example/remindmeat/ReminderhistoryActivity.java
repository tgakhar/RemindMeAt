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
import android.view.View;
import android.widget.Toast;

import com.example.remindmeat.Adapter.ReminderAdapter;
import com.example.remindmeat.Adapter.ReminderHistoryAdapter;
import com.example.remindmeat.Location.LocationService;
import com.example.remindmeat.Model.Reminder;
import com.example.remindmeat.View.AddreminderActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class ReminderhistoryActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore db;
    List<Reminder>reminderList=new ArrayList<>();
    FirebaseUser curUser;
    RecyclerView recyclerView;
    ReminderHistoryAdapter reminderHistoryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminderhistory);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        recyclerView=findViewById(R.id.recycler_listReminderHistory);
        loadData();
    }

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

    private void addToList(String reminderId, String reminderTitle, String reminderLocation, String reminderDescription, String reminderDate, Integer reminderRepeat, Integer reminderRange, Integer reminderStatus, Double reminderLat, Double reminderLong, Integer reminderUid) {

        reminderList.add(new Reminder(reminderId, reminderTitle, reminderLocation, reminderDescription, reminderDate, reminderRepeat, reminderRange, reminderStatus, reminderLat, reminderLong,reminderUid));

        setReminderRecycler(reminderList);
    }

    private void setReminderRecycler(final List<Reminder> reminderList) {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext()
                , RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        reminderHistoryAdapter = new ReminderHistoryAdapter(reminderList, getApplicationContext());
        recyclerView.setAdapter(reminderHistoryAdapter);
        reminderHistoryAdapter.setOnClickListner(adapterClick);
    }

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

    private void addReminder(View v) {
        RecyclerView.ViewHolder viewHolder=(RecyclerView.ViewHolder) v.getTag();
        final int position = viewHolder.getAdapterPosition();

        final Reminder reminder=reminderList.get(position);

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

        curUser=auth.getCurrentUser();
        db.collection("Users").document(curUser.getUid()).collection("Reminder").add(reminderMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    //Intent intent=new Intent(this,LocationService.class);
                    stopService(new Intent(ReminderhistoryActivity.this, LocationService.class));
                    startForegroundService(new Intent(ReminderhistoryActivity.this, LocationService.class));
                }
                else{
                    // Intent intent=new Intent(this,LocationService.class);
                    stopService(new Intent(ReminderhistoryActivity.this, LocationService.class));
                    startService(new Intent(ReminderhistoryActivity.this, LocationService.class));
                }
                DocumentReference docRef=db.collection("Users").document(curUser.getUid()).collection("Reminder History").document(reminder.getReminderId());

                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Reminder Re-added successfully!!!",
                                Toast.LENGTH_SHORT).show();
                        loadData();
                    }
                });
            }
        });

    }

    private void deleteReminder(View v) {
        RecyclerView.ViewHolder viewHolder=(RecyclerView.ViewHolder) v.getTag();
        final int position = viewHolder.getAdapterPosition();
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(ReminderhistoryActivity.this);
        builder.setMessage("Do you want to delete the reminder?")
                .setCancelable(false)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        curUser=auth.getCurrentUser();
                        DocumentReference docRef=db.collection("Users").document(curUser.getUid()).collection("Reminder History").document(reminderList.get(position).getReminderId());

                        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"Reminder deleted successfully!!!",
                                        Toast.LENGTH_SHORT).show();
                                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                    //Intent intent=new Intent(this,LocationService.class);
                                    stopService(new Intent(getApplicationContext(), LocationService.class));
                                   startForegroundService(new Intent(getApplicationContext(), LocationService.class));
                                }
                                else{
                                    // Intent intent=new Intent(this,LocationService.class);
                                    stopService(new Intent(getApplicationContext(), LocationService.class));
                                    startService(new Intent(getApplicationContext(), LocationService.class));
                                }*/
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
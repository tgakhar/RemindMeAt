package com.example.remindmeat.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Button;

import androidx.core.app.NotificationManagerCompat;

import com.example.remindmeat.Location.LocationService;
import com.example.remindmeat.Model.Reminder;
import com.example.remindmeat.View.DashActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Patel Dhruv
 * @author Gakhar Tanvi
 * @author Kaur Sarbjit
 * @author Kaur Kamaljit
 * @author Varma Akshay
 * @author Dankhara Chintan
 * @author Karthik Modubowna
 * Mynotification java class for triggering when notifications using broadcast receiver
 */
public class Mynotification extends BroadcastReceiver {

    /**
     * variable of Reminder
     */
    Reminder reminder;

    /**
     * variable of Firebase authentication
     */
    FirebaseAuth auth;

    /**
     * variable of FirebaseFirestore
     */
    FirebaseFirestore db;

    /**
     * variable of FirebaseUser
     */
    FirebaseUser curUser;

    /**
     * Method to receive the broadcast receiver components
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getStringExtra("Complete");
        reminder = intent.getExtras().getParcelable("Reminder");
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        if(action.equals("Complete")){
            Log.d("MyNotification","Notified"+action);
            if (reminder.getReminderRepeat()==0){
                deleteReminder(context);
            }
        }

        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
        NotificationManagerCompat.from(context).cancel(reminder.getUid());

    }

    /**
     * Method to delete reminder from database
     */
    private void deleteReminder(final Context context) {
        curUser=auth.getCurrentUser();

        db.collection("Users").document(curUser.getUid()).collection("Reminder").document(reminder.getReminderId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    //Intent intent=new Intent(this,LocationService.class);
                    context.stopService(new Intent(context, LocationService.class));
                    context.startForegroundService(new Intent(context, LocationService.class));
                }
                else{
                    // Intent intent=new Intent(this,LocationService.class);
                    context.stopService(new Intent(context, LocationService.class));
                    context.startService(new Intent(context, LocationService.class));
                }
                saveToHistory(context);
            }
        });
    }

    /**
     * Method called when reminder goes to the reminderHistory list when completed
     */
    private void saveToHistory(Context context) {
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
                Log.d("MyNotification","Added to history");
            }
        });

    }
}

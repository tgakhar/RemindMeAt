package com.example.remindmeat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.remindmeat.Model.Reminder;

public class Mynotification extends BroadcastReceiver {
    Reminder reminder;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getStringExtra("Complete");
        reminder = intent.getExtras().getParcelable("Reminder");

        if(action.equals("Complete")){
            Log.d("MyNotification","Notified"+action);
        }else if(action.equals("Cancel")){

        }

        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);

    }
}

package com.example.remindmeat.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.remindmeat.Location.LocationService;
import com.example.remindmeat.View.AddreminderActivity;

/**
 * @author Patel Dhruv
 * @author Gakhar Tanvi
 * @author Kaur Sarbjit
 * @author Kaur Kamaljit
 * @author Varma Akshay
 * @author Dankhara Chintan
 * @author Karthik Modubowna
 * DeviceBootReceiver class used for receiving notification after device gets re-booted and to restart location service
 */
public class DeviceBootReceiver extends BroadcastReceiver {

    /**
     * Method to receive the broadcast receiver components to start service
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.i("Broadcast Listened", "Service is started after boot");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, LocationService.class));
            } else {
                context.startService(new Intent(context, LocationService.class));
            }
        }
    }
}

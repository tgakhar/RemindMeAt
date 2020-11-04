package com.example.remindmeat.Location;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.remindmeat.BroadcastReceiver.Mynotification;
import com.example.remindmeat.BroadcastReceiver.RestartService;
import com.example.remindmeat.Model.Reminder;

import com.example.remindmeat.R;
import com.example.remindmeat.View.ViewnotificationActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.grpc.android.BuildConfig;

/**
 * @author Patel Dhruv
 * @author Gakhar Tanvi
 * @author Kaur Sarbjit
 * @author Kaur Kamaljit
 * @author Varma Akshay
 * @author Dankhara Chintan
 * @author Karthik Modubowna
 * LocationService java class is used for foreground location services
 */
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    /**
     * variable of FirebaseAuth
     */
    FirebaseAuth auth;

    /**
     * variable of FirebaseFirestore
     */
    FirebaseFirestore db;

    /**
     * variable of FirebaseUser
     */
    FirebaseUser user;

    /**
     * Arraylist of Reminder type
     */
    List<Reminder> reminderList=new ArrayList<>();
    /**
     * Tag for using in log
     */
    public static final String TAG = LocationService.class.getSimpleName();
    /**
     * Interval in milli seconds for location check
     */
    private static final long LOCATION_REQUEST_INTERVAL = 30000;
    /**
     * Location displacement value in meters for location check
     */
    private static final float LOCATION_REQUEST_DISPLACEMENT = 30;
    /**
     * Object of {@link GoogleApiClient}
     */
    private GoogleApiClient mGoogleApiClient;
    /**
     * Object of {@link FusedLocationProviderClient}
     */
    private FusedLocationProviderClient mFusedLocationProviderClient;
    /**
     * Object of {@link LocationRequest}
     */
    private LocationRequest mLocationRequest;
    /**
     * Object of {@link LocationCallback}
     */
    private LocationCallback mLocationCallback;
    /**
     * String variable to storing notification channel name
     */
    private static final String CHANNEL_ID = "Notification";
    /**
     * int variable for storing accuracy mode value
     */
    private static int accuracyMode = 100;

    /**
     * Method to provide binding for a service
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * onCreate method
     */
    @Override
    public void onCreate() {
        super.onCreate();

        buildGoogleApiClient();
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        user=auth.getCurrentUser();
        getAccuracyMode();
        loadData();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //here you get the continues location updated based on the interval defined in
                //location request
                Log.d(TAG,"New Location="+locationResult);
                Location l=locationResult.getLastLocation();
                final double lat2 = l.getLatitude();
                final double lng2 = l.getLongitude();
                showNotificationAndStartForegroundService();
                             Log.d("Location","T");


                            Log.d("ListView","List3="+reminderList);
                            //Log.d("Location","Test="+reminderList.getReminderId());
                        for (Reminder r:reminderList){
                            Log.d("Location","Test="+r.getReminderId());

                            if (distance(r.getReminderLat(), r.getReminderLong(), lat2, lng2) < r.getReminderRange()) { // if distance < 0.1 miles we take locations as equal
                                //do what you want to do...
                                Log.d("Matched,","Location matched"+r.getReminderLocation());

                                createNotification(r);

                            }
                        }

                // lat1 and lng1 are the values of a previously stored location

            }
            private double distance(double lat1, double lng1, double lat2, double lng2) {

                double earthRadius = 6378137; //3958.75; // in miles, change to 6371 for kilometer output

                double dLat = Math.toRadians(lat2-lat1);
                double dLng = Math.toRadians(lng2-lng1);

                double sindLat = Math.sin(dLat / 2);
                double sindLng = Math.sin(dLng / 2);

                double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                        * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

                double dist = earthRadius * c;
                Log.d(TAG,"New Dist="+dist);
                return dist; // output distance, in MILES
            }
        };

    }

    /**
     * Method to check accuracy mode for location
     */
    private void getAccuracyMode() {
        user=auth.getCurrentUser();
        db.collection("Users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Integer i= ((Long) documentSnapshot.getData().get("Accuracy Mode")).intValue();
                if (i==1){
                    accuracyMode=100;
                }else {
                   accuracyMode=102;
                }
            }
        });
    }


    /**
     * Method for triggering the notifications for reminders with complete intent
     */
    private void createNotification(Reminder r) {
        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID);
        createNotificationChannel();
        builder.setSmallIcon(R.drawable.logo45);
        builder.setContentTitle(r.getReminderTitle());
        builder.setContentText(r.getReminderDescription());
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent intent = new Intent(this, ViewnotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("Reminder",r);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, r.getUid(), intent, 0);
        builder.setContentIntent(pendingIntent);
        Intent intentAction = new Intent(this, Mynotification.class);

        //This is optional if you have more than one buttons and want to differentiate between two
        intentAction.putExtra("Complete","Complete");
        //intentAction.putExtra("Cancel","Cancel");
        intentAction.putExtra("Reminder",r);
        PendingIntent pIntentlogin = PendingIntent.getBroadcast(this,r.getUid(),intentAction,PendingIntent.FLAG_UPDATE_CURRENT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        builder.setAutoCancel(true);
        builder.addAction(R.drawable.ic_baseline_done_24, "Completed", pIntentlogin);
       // builder.addAction(R.drawable.logo45,"Cancel",pIntentlogin);
        builder.setCategory(NotificationCompat.CATEGORY_ALARM);
        builder.setAutoCancel(true);
        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(r.getUid(), builder.build());
    }

    /**
     * Method to group our notifications into groups—channels
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            CharSequence name="Notification";
            String Des="My personal notification";
            int importance= NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,name,importance);
            notificationChannel.setDescription(Des);

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);

        }
    }


    /**
     * loadData for loading the Data from Cloud FireStore
     */
   private void loadData() {
        user=auth.getCurrentUser();
        final String date= DateFormat.getDateInstance().format(new Date());
        int s=1;
        Log.d("LocationService","Date="+date);
        CollectionReference collectionReference=db.collection("Users").document(user.getUid()).collection("Reminder");
        Task task1 = collectionReference
                .whereIn("Date", Arrays.asList("0",date))
                .get();

        Task task2 = collectionReference
                .whereEqualTo("Status",s)
                .get();

        Tasks.whenAllSuccess(task1, task2).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> objects) {
                for (Object object : objects) {
                    QuerySnapshot queryDocumentSnapshots = (QuerySnapshot) object;
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        if (((Long) document.getData().get("Status")).intValue()==1){
                            Log.d("LocationService","Document1="+document.getData());
                            if (((String) document.getData().get("Date")).equals(date) || ((String) document.getData().get("Date")).equals("0")){
                                Log.d("LocationService","Document="+document.getData());
                                String reminderId= document.getId();
                                String reminderTitle=(String) document.getData().get("Title");
                                String reminderLocation=(String) document.getData().get("Address");
                                String reminderDescription=(String) document.getData().get("Description");
                                String reminderDate=(String) document.getData().get("Date");
                                Integer reminderRepeat= ((Long) document.getData().get("Repeat")).intValue();
                                Integer reminderRange= ((Long) document.getData().get("Range")).intValue();
                                Integer reminderStatus= ((Long) document.getData().get("Status")).intValue();
                                Double reminderLat= (Double) document.getData().get("Latitude");
                                Double reminderLong= (Double) document.getData().get("Longitude");
                                Integer reminderUid= ((Long) document.getData().get("UniqueId")).intValue();
                                //test.add(new Reminder(reminderId,reminderTitle,reminderLocation,reminderDescription,reminderDate,reminderRepeat,reminderRange,reminderStatus,reminderLat,reminderLong,reminderUid));
                                addToList(reminderId,reminderTitle,reminderLocation,reminderDescription,reminderDate,reminderRepeat,reminderRange,reminderStatus,reminderLat,reminderLong,reminderUid);
                                Log.d("ListView","ListId="+reminderId);
                            }
                        }

                    }
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
        reminderList.add(new Reminder(reminderId,reminderTitle,reminderLocation,reminderDescription,reminderDate,reminderRepeat,reminderRange,reminderStatus,reminderLat,reminderLong,reminderUid));
        Log.d("Location","ListL="+reminderId);
    }

    /**
     *  Method called every time a client starts the service using startService
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        return START_STICKY;
    }

    /**
     *  Method called when user removed a task that comes from the service's application
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, RestartService.class);
        this.sendBroadcast(broadcastIntent);
        super.onTaskRemoved(rootIntent);
    }

    /**
     * Method used for building GoogleApiClient and add connection callback
     */
    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }

    /**
     * Method used for creating location request
     * After successfully connection of the GoogleClient ,
     * This method used for to request continues location
     */
    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(accuracyMode);
        mLocationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(LOCATION_REQUEST_DISPLACEMENT);
        Log.d("LocationAccurecy","Accu="+accuracyMode);
        requestLocationUpdate();
    }

    /**
     * Method used for the request new location using Google FusedLocation Api
     */
    private void requestLocationUpdate() {
        Log.d(TAG, "createLocationRequest Called1");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.d(TAG, "createLocationRequest Called2");
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                //get the last location of the device

                Log.d(TAG, "Location="+location);

            }
        }).addOnFailureListener(new OnFailureListener() {

            /**
             * Method called when device fails to get last location
             */
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed="+e.getMessage());
            }
        });

        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                Looper.myLooper());
    }

    /**
     * Method for removing updates
     */
    private void removeLocationUpdate() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    /**
     * This Method shows notification for ForegroundService
     * Start Foreground Service and Show Notification to user for android all version
     */
    private void showNotificationAndStartForegroundService() {

        final String CHANNEL_ID = BuildConfig.APPLICATION_ID.concat("_notification_id");
        final String CHANNEL_NAME = BuildConfig.APPLICATION_ID.concat("_notification_name");
        final int NOTIFICATION_ID = 100;

        NotificationCompat.Builder builder;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            assert notificationManager != null;
            NotificationChannel mChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (mChannel == null) {
                mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
                notificationManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Location tracking is on");
            startForeground(NOTIFICATION_ID, builder.build());
        } else {
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name));
            startForeground(NOTIFICATION_ID, builder.build());
        }
    }

    /**
     * Method to get location updates when connected
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "GoogleApi Client Connected");
        createLocationRequest();
        Log.d(TAG, "createLocationRequest Called");
    }

    /**
     * Method used when connection gets suspended
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "GoogleApi Client Suspended");
    }

    /**
     * Method for failure in connection to services
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "GoogleApi Client Failed");
    }

    /**
     * Method called to destroy activity
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        removeLocationUpdate();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}